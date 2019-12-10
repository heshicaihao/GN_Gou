//Gionee <wangyy><2014-01-23> modify for CR01029173 begin
package com.gionee.client.business.statistic.job;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;

import com.gionee.client.business.statistic.business.DataManager;
import com.gionee.client.business.statistic.business.StatisticsManager;
import com.gionee.client.business.statistic.util.HttpClientUtils;
import com.gionee.client.business.statistic.util.Utils;
import com.gionee.client.business.util.InputStreamUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Url;

public class SendDataJob extends Job {
    private static final String TAG = "SendDataJob";
    private DataManager mDataManager;
    private AndroidHttpClient mDefaultHttpClient;
    private HttpPost mHttpRequest;
//    private ByteArrayInputStream mByteArrayInputStream;
    private HttpEntity mHttpEntity;
    private StatisticsManager mStatisticsManager;

    public SendDataJob(DataManager dataManager, Context context) {
        mDataManager = dataManager;
        mStatisticsManager = StatisticsManager.getInstance(context);
    }

    @Override
    protected void runTask() {
        try {
            LogUtils.logd(TAG, LogUtils.getThreadName() + " send data to server");
            List<NameValuePair> infos = getData();
            if (infos == null || infos.size() == 0) {
                LogUtils.logd(TAG, LogUtils.getThreadName() + " infos is empty");
                return;
            }
            String urlString = getHostUrl();
            createHttpPost(urlString, infos);
            mDefaultHttpClient = HttpClientUtils.getAndroidHttpClient();
            HttpResponse httpResponse = mDefaultHttpClient.execute(mHttpRequest);
            handleHttpResponse(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleHttpResponse(HttpResponse httpResponse) throws Exception {
        int code = httpResponse.getStatusLine().getStatusCode();
        LogUtils.logd(TAG, LogUtils.getThreadName() + " status code = " + code);
        if (HttpClientUtils.isRightHttpStatusCode(code)) {
            handleHttpExcuteResult(httpResponse.getEntity().getContent());
        }
    }

    @Override
    protected void releaseResource() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        mDataManager = null;
//        Utils.closeIOStream(mByteArrayInputStream);
//        mByteArrayInputStream = null;
        try {
            mHttpEntity.consumeContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHttpEntity = null;
        HttpClientUtils.disconnectHttpMethod(mHttpRequest);
        HttpClientUtils.close(mDefaultHttpClient);
        mDefaultHttpClient = null;
        mHttpRequest = null;
    }

    private List<NameValuePair> getData() {
        List<NameValuePair> infos = mDataManager.prepareData();
        return infos;
    }

    private void createHttpPost(String urlString, List<NameValuePair> infos)
            throws UnsupportedEncodingException {
        LogUtils.logd(TAG, LogUtils.getThreadName() + "parameter: " + infos);
        mHttpRequest = new HttpPost(urlString);
        mHttpEntity = new UrlEncodedFormEntity(infos, HTTP.UTF_8);
        mHttpRequest.setEntity(mHttpEntity);
    }

    private void handleHttpExcuteResult(InputStream inputStream) throws Exception {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " parse response json stream.");
        if (inputStream == null) {
            LogUtils.loge(TAG, LogUtils.getThreadName() + "InputStream is null");
            return;
        }
        try {
            String res = InputStreamUtils.inputStreamToString(inputStream, "utf-8");
            LogUtils.logd(TAG, LogUtils.getThreadName() + " response = " + res);
            JSONObject json = new JSONObject(res);
            if (json.getString("success").equals("true")) {
                mStatisticsManager.doAfterUploadSuccess();
            } else {
                mStatisticsManager.doAfterUploadFailed();
            }
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName() + "Error: exception = " + e);
            e.printStackTrace();
        } finally {
            Utils.closeIOStream(inputStream);
        }
    }

    private String getHostUrl() {
        return Url.STATISTIC_UPLOAD_URL;
    }

}
//Gionee <wangyy><2014-01-23> modify for CR01029173 end
