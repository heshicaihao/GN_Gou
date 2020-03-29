// Gionee <yangxiong><2014-4-3> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-4-3 下午02:18:56
 */
package com.gionee.client.activity.apprecommend;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.GNSplashActivity;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.appDownload.AppDataHelper;
import com.gionee.client.business.appDownload.GNDownloadListener;
import com.gionee.client.business.appDownload.ListDownloadManager;
import com.gionee.client.business.manage.GNActivityManager;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.adapter.AppListAdapter;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.utills.Utils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @author yangxiong <br/>
 * @date create at 2014-4-3 下午02:18:56
 * @description TODO 应用推荐详情页
 */
public class AppDetailActivity extends BaseFragmentActivity implements OnClickListener, GNDownloadListener {
    private static final String TAG = "AppDetailActivity";

    private ImageView mAppIcon;
    private Button mInstallBtn;
    private TextView mAppName;
    private TextView mAppVersion;
    private TextView mAppSize;
    private TextView mAppDescription;
    private TextView mAppIntroduceDetail;
    private TextView mAppType;
    private ProgressBar mProgressBar;
    private MyBean mAppinfo;
    private HorizontalScrollView mImagesScrollLayout;
    private LinearLayout mImagesLayout;

    private ImageView mImage1;
    private ImageView mImage2;
    private ImageView mImage3;
    private Context mContext = AppDetailActivity.this;
    private AppListAdapter mAppListAdapter = new AppListAdapter(mContext, null);
    private int mPosition = 1;
    private int mAppid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_details);
        if (!initdata(savedInstanceState)) {
            startActivity(new Intent(this, GNSplashActivity.class));
            finish();
            return;
        }
        initView();
        updateView();
        loadImages();
    }

    @Override
    protected void onStart() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onStart();
//        try {
//            mAppinfo = AppDataHelper.resetMybeanState(this, mAppinfo);
        mAppListAdapter.setAppStatus(mInstallBtn, mAppinfo);
        mAppListAdapter.setProgress(mProgressBar, mAppinfo);
//            mInstallBtn.invalidate();
//            AppDataHelper.ResetAppListData(mPosition, mAppinfo);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
        ListDownloadManager.getInstance(this).setsListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.ITENT_FLAG_APP_POSITION, mPosition);
    }

    /**
     * @description TODO 获取应用信息
     */
    private boolean initdata(Bundle savedInstanceState) {
        try {
            GNActivityManager.getScreenManager().pushActivity(this);
            Intent intent = getIntent();
            if (intent == null) {
                LogUtils.log(TAG, LogUtils.getThreadName() + "init failure: intent = null");
                return false;
            }
            GNImageLoader.getInstance().init(this);
            GNActivityManager.getScreenManager().pushActivity(this);
            mPosition = intent.getIntExtra(Constants.ITENT_FLAG_APP_POSITION, 1);
            if (savedInstanceState != null) {
                mPosition = savedInstanceState.getInt(Constants.ITENT_FLAG_APP_POSITION, 1);
            }
            MyBean bean = AppDataHelper.getAppPageData();
            @SuppressWarnings("unchecked")
            ArrayList<MyBean> list = (ArrayList<MyBean>) bean
                    .getSerializable(HttpConstants.Data.AppRecommond.APP_INFO_LIST_AL);
            if (list == null) {
                LogUtils.log(TAG, LogUtils.getThreadName() + "init failure: app info lost");
                return false;
            }
            mAppinfo = list.get(mPosition - 1);
            mAppid = getAppId(mAppinfo);
            LogUtils.log(TAG, "position: " + mPosition + "; app info: " + mAppinfo);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private int getAppId(MyBean appinfo) {
        JSONObject jsonAppInfo = appinfo.getJSONObject(HttpConstants.Data.AppRecommond.APP_INFO_JO);
        int appid = jsonAppInfo.optInt(HttpConstants.Response.ID_I);
        return appid;
    }

    @Override
    protected void onDestroy() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onDestroy();
        GNActivityManager.getScreenManager().popActivity(this);
    }

    /**
     * @description TODO 组件初始化
     */
    private void initView() {
        mAppIcon = (ImageView) findViewById(R.id.app_icon);
        mInstallBtn = (Button) findViewById(R.id.install_btn);
        mInstallBtn.setOnClickListener(mAppListAdapter.new DownloadBtnClickListener(mAppinfo, null));
        mAppName = (TextView) findViewById(R.id.app_name);
        mAppVersion = (TextView) findViewById(R.id.app_version);
        mAppSize = (TextView) findViewById(R.id.app_size);
        mAppDescription = (TextView) findViewById(R.id.description_tv);
        mAppIntroduceDetail = (TextView) findViewById(R.id.app_introduce_detail);
        mAppType = (TextView) findViewById(R.id.app_type);
        mImagesScrollLayout = (HorizontalScrollView) findViewById(R.id.images_scroll_layout);
        mImagesScrollLayout.setHorizontalFadingEdgeEnabled(false);
        mImagesLayout = (LinearLayout) findViewById(R.id.images_layout);
        mImage1 = (ImageView) findViewById(R.id.imageView1);
        mImage2 = (ImageView) findViewById(R.id.imageView2);
        mImage3 = (ImageView) findViewById(R.id.imageView3);
        mProgressBar = (ProgressBar) findViewById(R.id.app_download_progress);
        showTitleBar(true);
    }

    /**
     * @description TODO 设置ICON, 名称，版本号，大小，描述等
     */
    private void updateView() {
        if (mAppinfo == null) {
            LogUtils.log(TAG, LogUtils.getThreadName() + "ERROR: mAppinfo is null");
            return;
        }
        JSONObject jsonAppInfo = mAppinfo.getJSONObject(HttpConstants.Data.AppRecommond.APP_INFO_JO);
        GNImageLoader.getInstance().loadBitmap(
                jsonAppInfo.optString(HttpConstants.Response.RecommondAppList.ICON_S), mAppIcon);
        getTitleBar().setTitle(jsonAppInfo.optString(HttpConstants.Response.RecommondAppList.NAME_S));
        setTextview(mAppName, jsonAppInfo, HttpConstants.Response.RecommondAppList.NAME_S);
        setTextviewHideWhenEmpty(mAppType, jsonAppInfo, HttpConstants.Response.RecommondAppList.COMPANY_S);
        setTextview(mAppVersion, jsonAppInfo, HttpConstants.Response.RecommondAppList.VERSION_NAME_S);
        int size = jsonAppInfo.optInt(HttpConstants.Response.RecommondAppList.SIZE_I);
        setTextview(mAppSize, Utils.formatFileLength(size));
        setTextview(mAppDescription, jsonAppInfo, HttpConstants.Response.RecommondAppList.DESCRIPTION_S);
        mAppIntroduceDetail.setText(Html
                .fromHtml(jsonAppInfo.optString(HttpConstants.Response.RecommondAppList.RESUME_S)).toString()
                .trim());
    }

    /**
     * @author yangxiong
     * @description TODO 为指定Textview设置内容
     */
    private void setTextview(TextView textView, JSONObject jsonAppInfo, String key) {
        String text = jsonAppInfo.optString(key);
        textView.setText(text);
    }

    private void setTextview(TextView textView, String text) {
        textView.setText(text);
    }

    private void setTextviewHideWhenEmpty(TextView textView, JSONObject jsonAppInfo, String key) {
        String text = jsonAppInfo.optString(key);
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }

    }

    /**
     * @description TODO 加载截图，界面默认三张空图，多余三张时自动添加。
     */
    private void loadImages() {
        if (mAppinfo == null) {
            LogUtils.log(TAG, LogUtils.getThreadName() + "ERROR: mAppinfo is null");
            return;
        }
        JSONObject jsonAppInfo = mAppinfo.getJSONObject(HttpConstants.Data.AppRecommond.APP_INFO_JO);
        JSONArray images = jsonAppInfo.optJSONArray(HttpConstants.Response.RecommondAppList.IMAGES_S);
        if (images == null) {
            LogUtils.logd(TAG, LogUtils.getFunctionName() + "images array is null ");
            return;
        }
        for (int i = 0; i < images.length(); i++) {
            JSONObject img = images.optJSONObject(i);
            if (img == null) {
                LogUtils.logd(TAG, LogUtils.getFunctionName() + "images array [" + i + "] is null");
                continue;
            }

            String url = img.optString(HttpConstants.Response.RecommondAppList.URL_S);
            if (TextUtils.isEmpty(url)) {
                LogUtils.logd(TAG, LogUtils.getFunctionName() + "images array [" + i + "], url is null");
                continue;
            }

            loadOneImage(i, url);
        }
    }

    /**
     * @param index
     * @param url
     * @return
     * @description TODO 加载一张图片
     */
    private ImageView loadOneImage(int index, String url) {
        ImageView imageView = null;
        if (index < 3) {
            switch (index) {
                case 0:
                    imageView = mImage1;
                    break;
                case 1:
                    imageView = mImage2;
                    break;
                case 2:
                    imageView = mImage3;
                    break;

                default:
                    break;
            }
            GNImageLoader.getInstance().loadBitmap(url, imageView);
        } else {
            GNImageLoader.getInstance().getImageLoader()
                    .loadImage(url, GNImageLoader.getInstance().getDefaultOptions(), mListener);
        }
        return imageView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.install_btn:
                /**
                 * 安装！
                 */
                break;
            default:
                break;
        }
    }

    private ImageLoadingListener mListener = new ImageLoadingListener() {

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            LogUtils.log(TAG, LogUtils.getThreadName() + failReason);

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null && loadedImage.isRecycled() == false) {
                addImageToLayout(loadedImage);
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            LogUtils.log(TAG, LogUtils.getThreadName());

        }
    };

    /**
     * @param loadedImage
     * @description TODO 添加一张图片到滑动展示区
     */
    private void addImageToLayout(Bitmap loadedImage) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(loadedImage);
        ViewGroup.LayoutParams param = mImage2.getLayoutParams();
        imageView.setLayoutParams(param);
        imageView.setScaleType(ScaleType.FIT_CENTER);
        mImagesLayout.addView(imageView);
    }

    @Override
    public void onStatusChanged(MyBean bean) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        try {
            int appid = getAppId(bean);
            LogUtils.log(TAG, LogUtils.getThreadName() + " app id = " + appid);
            if (appid != mAppid) {
                return;
            }
            mAppListAdapter.setAppStatus(mInstallBtn, bean);
            mAppListAdapter.setProgress(mProgressBar, bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(MyBean bean) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        try {
            mAppListAdapter.setProgress(mProgressBar, bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//Gionee <yangxiong><2014-4-3> add for CR00850885 end
