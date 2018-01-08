// Gionee <yuwei><2013-12-20> add for CR00821559 begin
package com.gionee.client;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.welcome.GNWelcomeActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.action.StatisticAction;
import com.gionee.client.business.appDownload.InstallManager;
import com.gionee.client.business.manage.CacheDataManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.push.BaiduPushReceiver;
import com.gionee.client.business.push.BaiduPushUtils;
import com.gionee.client.business.push.PushAssist;
import com.gionee.client.business.push.PushNotificationTask;
import com.gionee.client.business.statistic.IStatisticsEventManager;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.statistic.StatisticsEventManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Config;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.service.PromotionalSaleService;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.business.LocalBuisiness;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.page.PageCacheManager;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class GNSplashActivity extends BaseFragmentActivity {

    private static final String TAG = "GNSplashActivity";
    /** is show when frist into this Activity. **/
    private ImageView mLoadingPage;

    private boolean mIsShowLoadingFinish = false;
    private StatisticAction mStatisticAction;
    private MyBean mSelfData;
    private boolean mIsFirstStart;
    private String mPushData;
    private String mIntentSource;
    private static final String IS_FIRST_BOOT = "is_first_boot_shoppingmall";
    private static final String IS_NEW_PUSH_VERSION = "is_new_push_version";
    private boolean mIsGotoHome;
    private IStatisticsEventManager mStatisticsEventManager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);
        initView();
        initData();
        registPush();
        registerPromotioanlSaleService();
        showSplashAnimation(savedInstanceState);
    }

    private void showSplashAnimation(final Bundle savedInstanceState) {
        mLoadingPage.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (isShowFriendlyReminder(savedInstanceState)) {
                    showFriendlyReminder(savedInstanceState);
                } else {
                    showAnimation();
                }
            }
        }, 100);
    }

    @Override
    protected void onResume() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onResume();

    }

    /**
     * @param savedInstanceState
     * @return
     * @author yangxiong
     * @description TODO
     */
    private boolean isShowFriendlyReminder(Bundle savedInstanceState) {
        return !isLoadingShown(savedInstanceState) && (0 == AndroidUtils.getCurrentTwoGState(this));
    }

    private void initView() {
        mLoadingPage = (ImageView) findViewById(R.id.logo_bg);
        if (getString(R.string.anzhi).equals(getString(R.string.channel))) {
            mLoadingPage.setImageResource(R.drawable.start_page_anzhi);
//        } else if (getString(R.string.baidu).contains(getString(R.string.channel))) {
//            mLoadingPage.setImageResource(R.drawable.start_page_img_baidu);
        } else {
            mLoadingPage.setImageResource(R.drawable.start_page_img);
        }
    }

    private void initData() {
        mLoadingPage.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
//                QihooAdAgent.init(GNSplashActivity.this);
                mIsFirstStart = isNeedShowGuidePage();
                Intent intent = getIntent();
                mPushData = intent.getStringExtra(Constants.Push.DATA);
                mIntentSource = intent.getStringExtra(Constants.Push.SOURCE);
                mStatisticAction = new StatisticAction();
                GNImageLoader.getInstance().init(GNSplashActivity.this);
                mSelfData = PageCacheManager.LookupPageData(GNSplashActivity.this.getClass().getName());
                mStatisticsEventManager = new StatisticsEventManager(GNSplashActivity.this);
            }
        }, 50);
    }

    private boolean isNeedShowGuidePage() {
        if (versionIsChanged()) {
            ShareDataManager.putString(this, "opened_app_version_name", AndroidUtils.getAppVersionName(this));
            ShareDataManager.putBoolean(this, "is_show_welcome_page", false);
            return true;
        }
        return false;
    }

    private boolean versionIsChanged() {
        String oldVersion = getVersion(ShareDataManager.getString(this, "opened_app_version_name", "1.0.0.a"));
        String appVersion = getVersion(AndroidUtils.getAppVersionName(this));
        if (oldVersion.equals(appVersion)) {
            return false;
        }
        return true;
    }

    private String getVersion(String version) {
        try {
            return version.substring(0, version.lastIndexOf("."));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isLoadingShown(Bundle bundle) {
        if (bundle == null) {
            mIsShowLoadingFinish = false;
        } else {
            mIsShowLoadingFinish = bundle.getBoolean(Constants.Home.IS_SHOW_LIADING, false);
        }
        return mIsShowLoadingFinish;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsGotoHome) {
            return;
        }
        if (mStatisticsEventManager == null) {
            return;
        }
        String source = null;
        if (BaiduPushReceiver.PUSH_STYLE_BAIDU.equals(mIntentSource)) {
            mStatisticsEventManager.add(StatisticsConstants.HomePageConstants.EXIT_SPLASH_PAGE_PUSH);
            source = Constants.BootSource.PUSH_STYLE_BAIDU;
        } else if (PushNotificationTask.PUSH_STYLE_BAIDU_LOCAL.equals(mIntentSource)) {
            mStatisticsEventManager.add(StatisticsConstants.HomePageConstants.EXIT_SPLASH_PAGE_PUSH);
            source = Constants.BootSource.PUSH_STYLE_LOCAL;
        } else {
            mStatisticsEventManager.add(StatisticsConstants.HomePageConstants.EXIT_SPLASH_PAGE_NORMAL);
            source = Constants.BootSource.NORMAL;
        }
        RequestAction action = new RequestAction();
        action.submitStatisticsData(this, null, this, mStatisticsEventManager.buildStatisticsData(), source);
    }

    private void showFriendlyReminder(final Bundle savedInstanceState) {
        DialogFactory.createRemindTraffic(GNSplashActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                showAnimation();
            }
        }).show();
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void registPush() {
        LocalBuisiness.getInstance().getHandler().postDelayed(new Runnable() {

            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (AndroidUtils.isHadPermission(GNSplashActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                    try {
                        initBaiduPush();
                    } catch (Exception e) {
                        LogUtils.log(TAG, "Exception:" + e);
                    }
                }
            }
        }, 40000);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.Home.IS_SHOW_LIADING, mIsShowLoadingFinish);
        super.onSaveInstanceState(outState);
    }

    private void initBaiduPush() {
        if (!BaiduPushUtils.getPushSwich(this)) {
            return;
        }
        if (!ShareDataManager.getBoolean(this, IS_NEW_PUSH_VERSION, false)
                || !PushAssist.isRegisterAps(this, Constants.Push.BAIDU_APS)) {
            PushManager.stopWork(getApplicationContext());
            BaiduPushUtils.setBind(getApplicationContext(), false);
            ShareDataManager.putBoolean(this, IS_NEW_PUSH_VERSION, true);
        }
        if (!BaiduPushUtils.hasBind(getApplicationContext())) {
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                    BaiduPushUtils.getMetaValue(this, "api_key"));
            // PushManager.enableLbs(getApplicationContext());
            List<String> tags = new ArrayList<String>();
            tags.add(getString(R.string.channel));
            PushManager.setTags(getApplicationContext(), tags);
            setPushNotificationStyle();
        }
        if (!PushManager.isPushEnabled(this)) {
            PushManager.resumeWork(this);
        }
    }

    private void setPushNotificationStyle() {
        Resources resource = this.getResources();
        String pkgName = this.getPackageName();
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(getApplicationContext(),
                resource.getIdentifier("notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", pkgName), resource.getIdentifier(
                        "notification_title", "id", pkgName), resource.getIdentifier("notification_text",
                        "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier("ic_launcher", "drawable", pkgName));
        PushManager.setNotificationBuilder(this, 1, cBuilder);
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void setLoadingImage() {

        String imageUrl = CacheDataManager.getLastLoadingUrl(this);
        final String linkUrl = CacheDataManager.getLinkUrl(this);
        if (!TextUtils.isEmpty(imageUrl)) {
            GNImageLoader
                    .getInstance()
                    .getImageLoader()
                    .loadImage(imageUrl, GNImageLoader.getInstance().getDefaultOptions(),
                            new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String arg0, View arg1) {
                                }

                                @Override
                                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                                }

                                @Override
                                public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                                    try {
                                        mLoadingPage.setImageBitmap(arg2);
                                        mLoadingPage.setLayoutParams(new RelativeLayout.LayoutParams(
                                                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                                        mLoadingPage.setScaleType(ScaleType.CENTER);

                                        mLoadingPage.setOnClickListener(new OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                if (!TextUtils.isEmpty(linkUrl)) {
                                                    if (!mIsFirstStart) {
                                                        enterHome();
                                                        gotoWebPage(linkUrl);
                                                    }
                                                }

                                            }
                                        });
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onLoadingCancelled(String arg0, View arg1) {
                                }
                            });

        }
    }

    private void gotoWebPage(String url) {
        gotoWebPage(url, true);
        finish();
    }

    private void showAnimation() {
        mStatisticAction.sendStatisticData(GNSplashActivity.this, Constants.Statistic.TYPE_ACTION,
                Constants.Statistic.KEY_ACTION);
        setLoadingImage();
        // requestLoadingData();
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 1.0f);
        alpha.setDuration(Constants.ANIMATION_DURATION);
        mLoadingPage.startAnimation(alpha);
        mLoadingPage.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                mLoadingPage.setEnabled(false);
                mLoadingPage.setClickable(false);
            }
        }, 1500);
        alpha.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                LogUtils.log(TAG, LogUtils.getFunctionName());
                requestLoadingData();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LogUtils.log(TAG, LogUtils.getFunctionName());

                enterHome();
            }
        });
    }

    private void enterHome() {
        Intent intent = new Intent();
        if (!TextUtils.isEmpty(mPushData)) {
            intent.putExtra(Constants.Push.DATA, mPushData);
        }

        if (!TextUtils.isEmpty(mIntentSource)) {
            intent.putExtra(Constants.Push.SOURCE, mIntentSource);
        }
        
        if (mIsFirstStart) {
//            gotoGuideActivity(intent);
            gotoActivityWithOutParams(GNWelcomeActivity.class);
            ShareDataManager.putBoolean(this, IS_FIRST_BOOT, false);
            mIsGotoHome = true;
        } else {
            mIsGotoHome = true;
            gotoHomeActivity(intent);
        }

    }

    private void gotoHomeActivity(Intent intent) {
        intent.setClass(GNSplashActivity.this, GnHomeActivity.class);
        startActivity(intent);
        finish();
        AndroidUtils.logoFadeAnim(this);
    }

//    private void gotoGuideActivity(Intent intent) {
//        intent.setClass(GNSplashActivity.this, GnHomeActivity.class);
//        startActivity(intent);
//        finish();
//        AndroidUtils.ActivityFadeInAnim(this);
//    }

    private void requestLoadingData() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        RequestAction action = new RequestAction();
        action.getAppStoreUpdate(GNSplashActivity.this);
        if (!isTodayUpdated()) {
            LogUtils.log("start_page", LogUtils.getFunctionName());
            int width = AndroidUtils.getDisplayWidth(GNSplashActivity.this);
            action.getLogoBackGround(GNSplashActivity.this, HttpConstants.Data.LOADING_INFO_JO, width);
        }
    }

    /**
     * @param currentSecond
     * @return true if need update ,else return false
     * @author yuwei
     * @description TODO if the time to last update time is larger the the limit time,you will need to update
     *              it
     */
    private boolean isTodayUpdated() {
        LogUtils.log("start_page", LogUtils.getFunctionName());
        long lastTime = CacheDataManager.getLastUpdateTime(this);
        return AndroidUtils.isDateToday(lastTime);
    }

    // Gionee <hcy> <2013-7-19> modify for CR00838312 end
    @Override
    public void onSucceed(final String businessType, boolean isCache, final Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (businessType.equals(Config.LOGO_BACKGROUND_URL)) {
            JSONObject data = mSelfData.getJSONObject(HttpConstants.Data.LOADING_INFO_JO);
            CacheDataManager.saveLoadingInfo(GNSplashActivity.this, data);
        }
        if (businessType.equals(Url.APP_STORE_URL)) {
            LogUtils.log("InstallManager", ((JSONObject) session).toString());
            InstallManager.getInstance(GNSplashActivity.this.getApplicationContext()).start(session);

        }
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
//        AndroidUtils.showErrorInfo(this, errorInfo);
        super.onErrorResult(businessType, errorOn, errorInfo, session);
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

    private void registerPromotioanlSaleService() {
        Intent intent = new Intent(PromotionalSaleService.PROMOTIONAL_SALE_SERVICE);
        this.startService(intent);
    }

}
// Gionee <yuwei><2013-1-10> add for CR00821559 end