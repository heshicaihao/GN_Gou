/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-3-6 下午05:08:59
 */
package com.gionee.client.activity.hotorder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.manage.ConfigManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.statistic.header.PublicHeaderParamsManager;
import com.gionee.client.business.statistic.util.MD5Utils;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.UAUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;
import com.gionee.framework.model.config.ControlKey;
import com.gionee.framework.operation.net.CustomMultiPartEntity;
import com.gionee.framework.operation.net.CustomMultiPartEntity.ProgressListener;
import com.gionee.framework.operation.net.NetUtil;
import com.gionee.framework.operation.utills.FileUtil;

/**
 * @author yangxiong <br/>
 * @description TODO 晒单结果页面
 */
public class HotOrderResultActivity extends BaseFragmentActivity implements OnClickListener {
    private static final String TAG = "HotOrderResultActivity";
    private ImageView mSubmitStatusIcon;
    private ProgressBar mProgressHorizontal;
    private String mOrderId;
    private String mHotOrderId;
    private ArrayList<String> mDeletedPicList = new ArrayList<String>();
    private ArrayList<String> mPicList = new ArrayList<String>();
    private String mNickName;
    private String mTitle;
    private String mContent;

    @Override
    public void onClick(View v) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        switch (v.getId()) {
            case R.id.goto_cutprice_order:
                AndroidUtils.finishActivity(this);
                break;
            default:
                break;
        }
    }

    // 晒单提交(含图片)
    private void submitHotOrderImage() {
        try {
            JSONArray array = new JSONArray();
            for (int i = 0; i < mDeletedPicList.size(); i++) {
                array.put(mDeletedPicList.get(i));
            }

            MyBean bean = makeParametersBean(array.toString(), mPicList.size());
            UploadUtilsAsync uploadTask = new UploadUtilsAsync(this, Url.SUBMIT_HOT_ORDER, bean, mPicList);
            uploadTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MyBean makeParametersBean(String deleteImages, int total) {
        MyBean bean = MyBeanFactory.createEmptyBean();
        bean.put(HttpConstants.Request.ImageHotOrder.DELETE_IMAGE, deleteImages);
        bean.put(HttpConstants.Request.SubmitHotOrder.TITLE, mTitle);
        bean.put(HttpConstants.Request.SubmitHotOrder.CONTENT, mContent);
        bean.put(HttpConstants.Request.SubmitHotOrder.AUTHOR, mNickName);
        String uid = PublicHeaderParamsManager.getUid(this);
        String secretKey = ConfigManager.getSecretKey(this);
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(uid);
        if (!TextUtils.isEmpty(mOrderId)) {
            bean.put(HttpConstants.Request.ImageHotOrder.OID_I, mOrderId);
            signBuilder.append(mOrderId);
        }
        signBuilder.append(total);
        bean.put(HttpConstants.Request.ImageHotOrder.TOTAL, total);
        if (!TextUtils.isEmpty(deleteImages)) {
            bean.put(HttpConstants.Request.ImageHotOrder.DELETE_IMAGE, deleteImages);
            signBuilder.append(deleteImages);
        }
        signBuilder.append(secretKey);
        String sign = MD5Utils.getMD5String(signBuilder.toString());
        bean.put(HttpConstants.Request.ImageHotOrder.SIGN, sign);
        return bean;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(arg0);
        setContentView(R.layout.hotorder_result);
        initTitleBar();
        initData();
        initViews();
        submitHotOrderImage();
    }

    /**
     * 
     * @author yangxiong
     * @description TODO
     */
    private void initData() {
        Intent intent = getIntent();
        mOrderId = intent.getStringExtra(Constants.ORDER_ID);
        mHotOrderId = intent.getStringExtra(Constants.HOT_ORDER_ID);
        mNickName = intent.getStringExtra(HttpConstants.Request.SubmitHotOrder.AUTHOR);
        mTitle = intent.getStringExtra(HttpConstants.Request.SubmitHotOrder.TITLE);
        mContent = intent.getStringExtra(HttpConstants.Request.SubmitHotOrder.CONTENT);
        mDeletedPicList = intent.getStringArrayListExtra(HttpConstants.Request.ImageHotOrder.DELETE_IMAGE);
        mPicList = intent.getStringArrayListExtra(HttpConstants.Request.ImageHotOrder.IMAGE);
    }

    /**
     * 
     * @author yangxiong
     * @description TODO
     */
    private void initViews() {
        mSubmitStatusIcon = (ImageView) findViewById(R.id.submitting_icon);
        mProgressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
    }

    /**
     * 
     * @author yangxiong
     * @description TODO
     */
    private void initTitleBar() {
        GNTitleBar titleBar = getTitleBar();
        titleBar.setVisibility(View.VISIBLE);
        titleBar.setTitle(R.string.hotorder);
    }

    /**
     * 异步AsyncTask+HttpClient上传文件,支持多文件上传,并显示上传进度
     */
    private class UploadUtilsAsync extends AsyncTask<String, Integer, String> {
        /** 服务器路径 **/
        private String mUrl;
        /** 上传的参数 **/
        private Map<String, Object> mParamMap;
        /** 要上传的文件 **/
        private ArrayList<String> mFiles;
        private long mTotalSize;
        private Context mContext;
        private static final String TAG = "UploadUtilsAsync";

        public UploadUtilsAsync(Context context, String url, Map<String, Object> paramMap,
                ArrayList<String> files) {
            this.mContext = context;
            this.mUrl = url;
            this.mParamMap = paramMap;
            this.mFiles = files;
        }

        @Override
        protected void onPreExecute() {
            // 执行前的初始化
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // 执行任务
            CustomMultiPartEntity multipartContent = getMultipartEntity();
            addParameters(multipartContent);
            addImagesFiles(multipartContent);
            mTotalSize = multipartContent.getContentLength();
            LogUtils.log(TAG, LogUtils.getThreadName() + " total size = " + mTotalSize);
            // Send it
            return uploadFile(mUrl, multipartContent);
        }

        private void addImagesFiles(CustomMultiPartEntity multipartContent) {
            // We use FileBody to transfer an image; remove ".img" 后缀
            int count = 0;
            for (String file : mFiles) {
                File oldFile = new File(file);
                String newFilePath = file.substring(0, file.indexOf(".img"));
                File newFile = new File(newFilePath);
                boolean res = oldFile.renameTo(newFile); // 在.ipg文件名再增加后缀.img
                if (res) {
                    FileBody fileBody = new FileBody(newFile);// 把文件转换成流对象FileBody
                    multipartContent.addPart("img_" + count, fileBody);
                    LogUtils.log(TAG, "post param : " + "img_" + count + "=" + newFilePath);
                    count++;
                }
            }
        }

        private void addParameters(CustomMultiPartEntity multipartContent) {
            Set<Entry<String, Object>> entrySet = mParamMap.entrySet();
            for (Entry<String, Object> para : entrySet) {
                if (!para.getKey().startsWith(ControlKey.request.control.__) && para.getValue() != null) {
                    if (para.getKey().startsWith(HttpConstants.Request.IMG_PARAMS_PREFIX)) {
                        LogUtils.log(TAG,
                                "post param : " + para.getKey() + " image file path: " + para.getValue());
                        FileBody fb = new FileBody(new File((String) para.getValue()));
                        multipartContent.addPart(para.getKey(), fb);
                    } else {
                        LogUtils.log(TAG, "post param : " + para.getKey() + "=" + para.getValue());
                        try {
                            multipartContent.addPart(para.getKey(), new StringBody(
                                    para.getValue().toString(), Charset.forName(HTTP.UTF_8)));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private CustomMultiPartEntity getMultipartEntity() {
            CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(new ProgressListener() {
                @Override
                public void transferred(long num) {
                    publishProgress((int) ((num / (float) mTotalSize) * 100));
                }
            });
            return multipartContent;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 执行进度
            LogUtils.logd(TAG, LogUtils.getThreadName() + "values: " + values[0]);
            mProgressHorizontal.setProgress((int) values[0]);// 更新进度条
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            // 执行结果
            LogUtils.logd(TAG, LogUtils.getThreadName() + result);
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
            addSuffixToFileName();
            if (result.equals(getString(R.string.posts_publish_success))) {
                mSubmitStatusIcon.setImageResource(R.drawable.submited);
                mProgressHorizontal.setVisibility(View.GONE);
                for (String file : mFiles) {
                    FileUtil.deleteFile(new File(file));
                }
            } else {
                if (TextUtils.isEmpty(mHotOrderId)) { // 仅保存新建晒单的提交
                    saveData();
                }
            }

            super.onPostExecute(result);
        }

        private void addSuffixToFileName() {
            for (String file : mFiles) {
                File oldFile = new File(file);
                String newFilePath = file.substring(0, file.indexOf(".img"));
                File newFile = new File(newFilePath);
                boolean res = newFile.renameTo(oldFile); // 去掉.ipg文件增加后缀.img
                if (!res) {
                    LogUtils.logd(TAG, LogUtils.getThreadName() + " rename fail. file = " + file);
                }
            }
        }

        /**
         * 向服务器上传文件
         * 
         * @param url
         * @param entity
         * @return
         */
        public String uploadFile(String url, CustomMultiPartEntity entity) {
            LogUtils.logd(TAG, LogUtils.getThreadName());
            HttpParams httpParams = getHttpParams();
            HttpClient httpClient = new DefaultHttpClient(httpParams);// 开启一个客户端 HTTP 请求
            HttpPost httpPost = new HttpPost(url);// 创建 HTTP POST 请求
            httpPost.setEntity(entity);
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String strResult = EntityUtils.toString(httpResponse.getEntity(), NetUtil.charSet);
                    LogUtils.logd(TAG, LogUtils.getThreadName() + " strResult = " + strResult);
                    return getRusultString(strResult);
                }
            } catch (Exception e) {
                LogUtils.log(TAG, LogUtils.getThreadName() + e);
                e.printStackTrace();
            } finally {
                if (httpClient != null && httpClient.getConnectionManager() != null) {
                    httpClient.getConnectionManager().shutdown();
                }
            }
            return getString(R.string.posts_publish_fail);
        }

        private String getRusultString(String strResult) throws JSONException {
            JSONObject json = new JSONObject(strResult);
            boolean isSuccess = json.optBoolean(HttpConstants.Response.SUCCESS);
            if (isSuccess) {
                clearHotOrderCache();
                return getString(R.string.posts_publish_success);
            } else {
                String errorMsg = json.optString(HttpConstants.Response.ERR_MSG);
                return errorMsg;
            }
        }

        private HttpParams getHttpParams() {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, NetUtil.CONNECT_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, NetUtil.CONNECT_TIMEOUT);
            ConnManagerParams.setTimeout(httpParams, NetUtil.CONNECT_TIMEOUT);
            HttpProtocolParams.setUserAgent(httpParams, UAUtils.getUserAgent((mContext)));
            return httpParams;
        }
    }

    private void clearHotOrderCache() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        ShareDataManager.removeReferece(this, "hotorder_title_" + mOrderId);
        ShareDataManager.removeReferece(this, "hotorder_content_" + mOrderId);
        ShareDataManager.removeReferece(this, "hotorder_nickname_" + mOrderId);
        ShareDataManager.removeReferece(this, "hotorder_img_count_" + mOrderId);
        if (mPicList.size() > 0) {
            for (int i = 0; i < mPicList.size(); i++) {
                ShareDataManager.removeReferece(this, "hotorder_img_" + mOrderId + "_i");
            }
        }
    }

    public void saveData() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        saveTitle();
        saveContent();
        saveNickName();
        savePics();
    }

    private void savePics() {
        if (mPicList.size() > 0) {
            ShareDataManager.saveDataAsInt(this, "hotorder_img_count_" + mOrderId, mPicList.size());
            for (int i = 0; i < mPicList.size(); i++) {
                ShareDataManager.putString(this, "hotorder_img_" + mOrderId + "_" + i, mPicList.get(i));
            }
        } else {
            ShareDataManager.removeReferece(this, "hotorder_img_count_" + mOrderId);
            for (int i = 0; i < mPicList.size(); i++) {
                ShareDataManager.removeReferece(this, "hotorder_img_" + mOrderId + "_" + i);
            }
        }
    }

    private void saveNickName() {
        String nickname = mNickName;
        if (TextUtils.isEmpty(nickname)) {
            ShareDataManager.removeReferece(this, "hotorder_nickname_" + mOrderId);
        } else {
            ShareDataManager.putString(this, "hotorder_nickname_" + mOrderId, nickname);
        }
    }

    private void saveContent() {
        String content = mContent;
        if (TextUtils.isEmpty(content)) {
            ShareDataManager.removeReferece(this, "hotorder_content_" + mOrderId);
        } else {
            ShareDataManager.putString(this, "hotorder_content_" + mOrderId, content);
        }
    }

    private void saveTitle() {
        String title = mTitle;
        if (TextUtils.isEmpty(title)) {
            ShareDataManager.removeReferece(this, "hotorder_title_" + mOrderId);
        } else {
            ShareDataManager.putString(this, "hotorder_title_" + mOrderId, title);
        }
    }
}
