// Gionee <yuwei><2013-8-15> add for CR00821559 begin
/*
 * BaseFragmentActivity.java
 * classes : com.gionee.client.activity.base.BaseFragmentActivity
 * @author yuwei
 * V 1.0.0
 * Create at 2013-8-15 下午2:23:19
 */
package com.gionee.client.activity.base;

import static cn.sharesdk.framework.utils.R.getStringRes;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.utils.UIHandler;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.inject.GNInjector;
import com.gionee.client.business.manage.GNActivityManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.shareTool.GNShareDialog;
import com.gionee.client.business.shareTool.ShareModel;
import com.gionee.client.business.shareTool.ShareTool;
import com.gionee.client.business.sina.WeiBoShare;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.CommonUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.widget.CustomToast;
import com.gionee.client.view.widget.GNCustomDialog;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.business.LocalBuisiness;
import com.gionee.framework.operation.page.PageCacheManager;
import com.gionee.framework.operation.utills.BitmapUtills;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.handmark.pulltorefresh.library.internal.RotateLoadingLayout;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;

/**
 * com.gionee.client.activity.base.BaseFragmentActivity
 * 
 * @author yuwei <br/>
 * @date create at 2013-8-15 下午2:23:19
 * @description TODO
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements IBusinessHandle,
        PlatformActionListener, android.os.Handler.Callback {
    private static final String TAG = "BaseFragmentActivity";
    private static final int MSG_ACTION_CCALLBACK = 1;
    protected MyBean mSelfData;
    protected GNTitleBar mTitleBar;
    protected View mShadow;
    protected RelativeLayout mGuideLayout;
    protected ImageView mGuideImage;
    private boolean mIsFirstboot;
    protected IWeiboShareAPI mWeiboShareAPI;
    protected ShareTool mShareTool;
    protected GNCustomDialog mDialog;
    protected GNShareDialog mShareDialog;
    protected ProgressBar mProgressBar;
    protected RelativeLayout mRlLoading;
    private ImageView mIvBalloon;
    private ImageView mIvLunzi;
    private long mLastClickTime;
    private Animation mRotateAnimation;
    private Animation mRefreshBalloonAnimation;
    private Animation mRefreshBalloonAnimation2;
    protected CustomToast mCustomToast;

    @Override
    protected void onCreate(Bundle arg0) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        try {
            super.onCreate(arg0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GNActivityManager.getScreenManager().pushActivity(this);
        mSelfData = PageCacheManager.LookupPageData(this.getClass().getName());
        mCustomToast = new CustomToast(this);
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    public void gotoWebPage(String url, boolean isShowFootBar) {
        CommonUtils.gotoWebViewActvity(this, url, isShowFootBar);
    }

    public void gotoWebPageForResult(String url, boolean isShowFootBar) {
        CommonUtils.gotoWebViewActvityForResult(this, url, isShowFootBar);
    }

    @Override
    public void startActivity(Intent intent) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.startActivity(intent);
        AndroidUtils.enterActvityAnim(this);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.startActivityForResult(intent, requestCode);
        AndroidUtils.enterActvityAnim(this);
    }

    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - mLastClickTime < 500) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }

    public void cumulateAppLinkScore() {
        RequestAction action = new RequestAction();
        action.cumulateScore(this, Constants.ScoreTypeId.SHARE_LINK);
    }

    public void cumulateScore(String typeId) {
        RequestAction action = new RequestAction();
        action.cumulateScore(this, typeId);
    }

    /**
     * @author yuwei
     * @description TODO
     */
    protected <T> void startActivityWithNoParams(Class<T> activity) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        startActivity(intent);

    }

    @SuppressLint("Range")
    @Override
    public void setContentView(int layoutResID) {
        // TODO Auto-generated method stub
        RelativeLayout activityView = (RelativeLayout) LayoutInflater.from(this).inflate(
                R.layout.activity_base, null);
        super.setContentView(activityView);
        initLoading();
        mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);
        FrameLayout containner = (FrameLayout) activityView.findViewById(R.id.activity_containner);
        View mContent = LayoutInflater.from(this).inflate(layoutResID, null);
        containner.addView(mContent, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTitleBar = (GNTitleBar) activityView.findViewById(R.id.title_bar);
        mShadow = activityView.findViewById(R.id.top_bar_shadow);
        mGuideLayout = (RelativeLayout) findViewById(R.id.guide_bg);
        if (AndroidUtils.getAndroidSDKVersion() >= 11) {
            mGuideLayout.setAlpha(100);
        }
        mGuideImage = (ImageView) findViewById(R.id.guide_pic);
        GNInjector.getInstance().inJectActivityView(this);
        if (AndroidUtils.translateTopBar(this)) {
            mTitleBar.setTopPadding();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        initLoading();
    }

    private void initLoading() {
        // TODO Auto-generated method stub
        mRlLoading = (RelativeLayout) findViewById(R.id.rl_loading);
        mIvBalloon = (ImageView) findViewById(R.id.iv_balloon);
        mIvLunzi = (ImageView) findViewById(R.id.iv_lunzi);
        mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(LoadingLayout.ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(RotateLoadingLayout.ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
        mRefreshBalloonAnimation = AnimationUtils.loadAnimation(this,
                R.anim.refreh_balloon_rotate_from_center);
        mRefreshBalloonAnimation.setFillAfter(true);
        mRefreshBalloonAnimation2 = AnimationUtils
                .loadAnimation(this, R.anim.refreh_balloon_rotate_from_left);
        mRefreshBalloonAnimation2.setFillAfter(true);
        mRefreshBalloonAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
                mIvBalloon.startAnimation(mRefreshBalloonAnimation2);
            }
        });
        mIvBalloon.startAnimation(mRefreshBalloonAnimation);
        mIvLunzi.startAnimation(mRotateAnimation);
    }

    public void showLoadingProgress() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoadingProgress() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 显示页面loading
     */
    public void showPageLoading() {
        if (mRlLoading != null) {
            mRlLoading.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏页面loading
     */
    public void hidePageLoading() {
        if (mRlLoading != null) {
            mRlLoading.setVisibility(View.GONE);
        }
    }

    public void showGuide(int imageId) {

        mIsFirstboot = isFirstBoot();

        if (mIsFirstboot) {
            mGuideLayout.setVisibility(View.VISIBLE);
            mGuideImage.setImageResource(imageId);
            mGuideLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mGuideLayout.setVisibility(View.GONE);
                }
            });
            resetFistBoot();
        }
    }

    public void resetFistBoot() {
        ShareDataManager.putBoolean(this, getClass().getName(), false);
    }

    public boolean isFirstBoot() {
        return ShareDataManager.getBoolean(this, getClass().getName(), true);
    }

    public void resetFistBoot(String key) {
        ShareDataManager.putBoolean(this, key, false);
    }

    public boolean isFirstBoot(String key) {
        return ShareDataManager.getBoolean(this, key, true);
    }

    public void setGuideBackgroud(int resid) {
        mGuideLayout.setBackgroundResource(resid);
    }

    public void closeShareDialog() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void closeProgressDialog() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (mShareTool != null) {
            mShareTool.dismiss();
        }
    }

    protected void showTitleBar(boolean isShowTitleBar) {
        if (isShowTitleBar) {
            mTitleBar.setVisibility(View.VISIBLE);
        } else {
            mTitleBar.setVisibility(View.GONE);
        }
    }

    protected void showShadow(boolean isShowShadow) {
        if (isShowShadow) {
            mShadow.setVisibility(View.VISIBLE);
        } else {
            mShadow.setVisibility(View.GONE);
        }
    }

    public GNTitleBar getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        RelativeLayout activityView = (RelativeLayout) LayoutInflater.from(this).inflate(
                R.layout.activity_base, null);
        super.setContentView(activityView);
        FrameLayout containner = (FrameLayout) activityView.findViewById(R.id.activity_containner);
        containner.addView(view, params);
        mTitleBar = (GNTitleBar) activityView.findViewById(R.id.title_bar);
        GNInjector.getInstance().inJectActivityView(this);
    }

    @Override
    public void setContentView(View view) {
        LinearLayout containnerView = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.activity_base, null);
        super.setContentView(containnerView);
        containnerView.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTitleBar = (GNTitleBar) containnerView.findViewById(R.id.title_bar);
        GNInjector.getInstance().inJectActivityView(this);
    }

    public void shareToWeixin(final boolean isShareToFriends, final String title, final String description,
            final String imageUrl, final String url) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " URL=" + url + ", imageUrl = " + imageUrl
                + ", title = " + title + ", description = " + description);
        initShareTool();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, R.string.loading_share_later, Toast.LENGTH_SHORT).show();
            return;
        }
        mShareTool.showProgressDialog();
        ShareModel model = new ShareModel(TextUtils.isEmpty(title) ? getString(R.string.no_title) : title,
                TextUtils.isEmpty(description) ? getString(R.string.app_name) : description, url == null ? ""
                        : url, TextUtils.isEmpty(imageUrl) ? Constants.DEFAULT_SHARE_ICON_URL : imageUrl);
        mShareTool.initShareParams(model);
        mShareTool.share(isShareToFriends ? ShareTool.PLATFORM_WECHAT_MOMENTS : ShareTool.PLATFORM_WECHAT);
    }

    public void shareToWeixin(final boolean isShareToFriends, final String title, final String description,
            final Bitmap thumb, final boolean isRecyleBitmap, final String url) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " URL=" + url + ", title = " + title
                + ", description = " + description + ", isRecyleBitmap = " + isRecyleBitmap);
        initShareTool();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, R.string.loading_share_later, Toast.LENGTH_SHORT).show();
            return;
        }
        mShareTool.showProgressDialog();
        Bitmap bitmap = getCompressedBitmap(thumb);
        ShareModel model = new ShareModel(TextUtils.isEmpty(title) ? getString(R.string.no_title) : title,
                TextUtils.isEmpty(description) ? getString(R.string.app_name) : description, url == null ? ""
                        : url, bitmap);
        mShareTool.initShareParams(model);
        mShareTool.share(isShareToFriends ? ShareTool.PLATFORM_WECHAT_MOMENTS : ShareTool.PLATFORM_WECHAT);
        if (isRecyleBitmap) {
            BitmapUtills.bitmapRecycle(thumb);
        }
    }

    /**
     * 微信好友，微信朋友圈分享
     */
    public void shareToWeixin(final boolean isShareToFriends, final String title, final String description,
            final Bitmap thumb, final String url) {
        shareToWeixin(isShareToFriends, title, description, thumb, true, url);
    }

    /**
     * 新浪微波分享
     */
    public void shareToWeibo(final String title, final String description, final Bitmap thumb,
            final String url) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        LocalBuisiness.getInstance().postRunable(new Runnable() {
            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                try {
                    if (mWeiboShareAPI == null) {
                        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(BaseFragmentActivity.this,
                                com.gionee.client.business.sina.Constants.APP_KEY);
                    }
                    if (!mWeiboShareAPI.checkEnvironment(true)) {
                        return;
                    }
                    WeiBoShare weiBoShare = new WeiBoShare(mWeiboShareAPI, BaseFragmentActivity.this);
                    mWeiboShareAPI.registerApp();
                    weiBoShare.sendShareMsg(title, description, thumb, url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        initShareTool();
//        if (TextUtils.isEmpty(title)) {
//            Toast.makeText(this, R.string.loading_share_later, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        mShareTool.showProgressDialog();
//        ShareModel model = new ShareModel(TextUtils.isEmpty(title) ? getString(R.string.no_title) : title,
//                TextUtils.isEmpty(description) ? getString(R.string.app_name) : description, url == null ? ""
//                        : url, thumb);
//        mShareTool.initShareParams(model);
//        mShareTool.share(ShareTool.PLATFORM_SINA_WEIBO);
    }

    /**
     * "QQ好友":2, "QQ空间":3
     */
    public void shareToQq(int platform, String title, String text, String imageUrl, String url) {
        initShareTool();
        ShareModel model = new ShareModel(TextUtils.isEmpty(title) ? getString(R.string.no_title) : title,
                TextUtils.isEmpty(text) ? getString(R.string.app_name) : text, url == null ? "" : url,
                TextUtils.isEmpty(imageUrl) ? Constants.DEFAULT_SHARE_ICON_URL : imageUrl);
        mShareTool.initShareParams(model);
        mShareTool.share(platform);
    }

    public boolean isWeiboValid() {
        try {
            return mWeiboShareAPI != null && mWeiboShareAPI.isWeiboAppInstalled()
                    && mWeiboShareAPI.isWeiboAppSupportAPI() && mWeiboShareAPI.checkEnvironment(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 显示分享对话框, 需要在要实现分享的activity中预置一些代码： 1) onCreate(): ShareSDK.initSDK(this); 2) onDestroy():
     * ShareSDK.stopSDK(this); 3) onResume(): closeProgressDialog(); 4) onClick(): 处理对话框中的点击事件，即调用相应分享方法.
     */
    public void showShareDialog(final View v, OnClickListener listener) {
        try {
            if (mDialog == null) {
                mDialog = (GNCustomDialog) DialogFactory.createShareDialog(this);
            }
            if (mDialog != null) {
                mDialog.show();
                mDialog.setDismissBtnVisible();
                mDialog.setCanceledOnTouchOutside(true);
                mDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        try {
                            LogUtils.log(TAG, LogUtils.getThreadName());
                            MyBean bean = (MyBean) v.getTag();
                            if (bean != null) {
                                Bitmap bitmap = (Bitmap) bean.get("thump");
                                if (bitmap != null) {
                                    BitmapUtills.bitmapRecycle(bitmap);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                MyBean bean = (MyBean) v.getTag();
                mDialog.getContentView().findViewById(R.id.share_weixin).setTag(bean);
                mDialog.getContentView().findViewById(R.id.share_weixin).setOnClickListener(listener);
                mDialog.getContentView().findViewById(R.id.share_friends).setTag(bean);
                mDialog.getContentView().findViewById(R.id.share_friends).setOnClickListener(listener);
                mDialog.getContentView().findViewById(R.id.share_weibo).setTag(bean);
                mDialog.getContentView().findViewById(R.id.share_weibo).setOnClickListener(listener);
                mDialog.getContentView().findViewById(R.id.share_qq_friend).setTag(bean);
                mDialog.getContentView().findViewById(R.id.share_qq_friend).setOnClickListener(listener);
                mDialog.getContentView().findViewById(R.id.share_qq_zone).setTag(bean);
                mDialog.getContentView().findViewById(R.id.share_qq_zone).setOnClickListener(listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onDestroy();
        GNActivityManager.getScreenManager().popActivity(this);
        // PageCacheManager.ClearPageData(this.getClass().getName());
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " errorOn = " + errorOn + ", errorInfo = " + errorInfo);
        if (getString(R.string.upgrade_error_network_exception).equals(errorInfo)) {
            mTitleBar.postDelayed(new Runnable() {

                @Override
                public void run() {
                    showNetErrorToast();
                }
            }, 500);
            return;
        }
        AndroidUtils.showErrorInfo(this, errorInfo);

    }

    public void showNetErrorToast() {
        showNetErrorToast(mTitleBar);
    }

    public void showNetErrorToast(View view) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        mCustomToast.setToastText(getString(R.string.upgrade_no_net));
        int topMagin = view.getBottom() + AndroidUtils.dip2px(BaseFragmentActivity.this, 25);
        mCustomToast.showToast(view, topMagin);
    }

    @Override
    public Context getSelfContext() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return this;
    }

    @Override
    public void onCancel(String businessType, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    protected void onStart() {
        super.onStart();
        mSelfData = PageCacheManager.LookupPageData(this.getClass().getName());
    }

    @Override
    public void onBackPressed() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onBackPressed();
        AndroidUtils.exitActvityAnim(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSelfData = PageCacheManager.LookupPageData(this.getClass().getName());
        mLastClickTime = 0;
    }

    protected final MyBean getSelfData() {
        return mSelfData;
    }

    /**
     * @author yuwei
     * @description TODO
     */
    protected <T> void gotoActivityWithOutParams(Class<T> activity) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        startActivity(intent);
        AndroidUtils.enterActvityAnim(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        int what = msg.what;
        switch (what) {
            case MSG_ACTION_CCALLBACK: {
                switch (msg.arg1) {
                    case 1:
                        Platform platform = (Platform) msg.obj;
                        // 新浪微博因回调提前不显示分享成功，客户端也会在通知栏提示分享成功
                        if (platform.getId() != 1) {
                            Toast.makeText(this, R.string.share_success, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        String expName = msg.obj.getClass().getSimpleName();
                        if ("WechatClientNotExistException".equals(expName)
                                || "WechatTimelineNotSupportedException".equals(expName)
                                || "WechatFavoriteNotSupportedException".equals(expName)) {
                            int resId = getStringRes(this, "wechat_client_inavailable");
                            if (resId > 0) {
                                Toast.makeText(this, this.getString(resId), Toast.LENGTH_SHORT).show();
                            }
                        } else if ("QQClientNotExistException".equals(expName)) {
                            int resId = getStringRes(this, "qq_client_inavailable");
                            if (resId > 0) {
                                Toast.makeText(this, this.getString(resId), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, R.string.share_fail, Toast.LENGTH_SHORT).show();
                            LogUtils.log(TAG, LogUtils.getFunctionName() + ((Throwable) msg.obj).getMessage());
                        }
                        break;
                    case 3:
                        Toast.makeText(this, R.string.share_cancel, Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }
                mShareTool.dismiss();
                break;
            default:
                break;
        }
        if (mShareTool != null) {
            mShareTool.dismiss();
        }
        return false;
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
        LogUtils.log(TAG, LogUtils.getFunctionName() + " platform: " + platform.getName() + ", id = "
                + platform.getId() + ", action = " + action + ", res = " + res);
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
        switch (platform.getId()) {
            case 1:
                // 新浪微博
                StatService.onEvent(this, BaiduStatConstants.SHARE_SUCCESS, BaiduStatConstants.WEIBO);
                break;
            case 4:
                // 微信好友
                StatService.onEvent(this, BaiduStatConstants.SHARE_SUCCESS, BaiduStatConstants.WEIXIN);
                break;
            case 5:
                // 微信朋友圈
                StatService.onEvent(this, BaiduStatConstants.SHARE_SUCCESS, BaiduStatConstants.FRIENDS);
                break;
            case 3:
                // QQ空间
                StatService.onEvent(this, BaiduStatConstants.SHARE_SUCCESS, BaiduStatConstants.ZONE);
                break;
            case 7:
                // QQ好友
                StatService.onEvent(this, BaiduStatConstants.SHARE_SUCCESS, BaiduStatConstants.QQ);
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(Platform platform, int action, Throwable t) {
        LogUtils.log(TAG, LogUtils.getFunctionName() + " Throwable: " + t.toString());
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = t;
        UIHandler.sendMessage(msg, this);
        // 分享失败的统计
//        ShareSDK.logDemoEvent(4, platform);
    }

    @Override
    public void onCancel(Platform platform, int action) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    protected void initShareTool() {
        if (mShareTool == null) {
            mShareTool = new ShareTool(this);
            mShareTool.setPlatformActionListener(this);
        }
    }

    private Bitmap getCompressedBitmap(final Bitmap thumb) {
        int option = 10;
        byte[] array;
        Bitmap bitmap = null;
        if (thumb != null && !thumb.isRecycled()) {
            do {
                BitmapUtills.bitmapRecycle(bitmap);
                int length = Constants.THUMB_SIZE * option / 10;
                bitmap = BitmapUtills.compressFromBitmap(thumb, length, length, 32 * 1024);
                array = com.gionee.client.business.util.Util.bmpToByteArray(bitmap, false);
                option = option - 2;
                if (option < 0) {
                    LogUtils.log(TAG, LogUtils.getThreadName() + " option < 0");
                    BitmapUtills.bitmapRecycle(bitmap);
                    return null;
                }
            } while (array.length > 32 * 1024);
        }
        return bitmap;
    }

    public void startCumulateAimation(final View animationView) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.everyday_check);
        animation.setFillAfter(false);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationView.setVisibility(View.GONE);
            }
        });
        animationView.setVisibility(View.VISIBLE);
        animationView.startAnimation(animation);
    }

    protected String getAppWeiXinShareURL() {
        return Constants.WEIXIN_SHARE_URL + AndroidUtils.getAppVersionName(this).trim();
    }

}
