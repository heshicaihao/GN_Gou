// Gionee <yuwei> <2013-12-20> modify for CR00832427 begin
/*
 * GnHomeActivity.java
 * classes : com.gionee.client.GnHomeActivity
 * @author yuwei
 * V 1.0.0
 * Create at 2013-4-8 下午5:20:16
 */
package com.gionee.client.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.tabFragment.CategoryFragment;
import com.gionee.client.activity.tabFragment.CommentsFragment;
import com.gionee.client.activity.tabFragment.HomeFragment;
import com.gionee.client.activity.tabFragment.MoreFragment;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.action.StatisticAction;
import com.gionee.client.business.manage.ConfigManager;
import com.gionee.client.business.manage.GNActivityManager;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.persistent.ShareKeys;
import com.gionee.client.business.push.BaiduPushReceiver;
import com.gionee.client.business.push.PushDataInfo;
import com.gionee.client.business.push.PushNotificationTask;
import com.gionee.client.business.statistic.IStatisticsEventManager;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.statistic.StatisticsEventManager;
import com.gionee.client.business.urlMatcher.UrlMatcher;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LocationUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.service.PromotionalSaleService;
import com.gionee.client.view.widget.BasicMyMenu;
import com.gionee.client.view.widget.TabChangeListener;
import com.gionee.framework.operation.business.LocalBuisiness;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.utills.FileUtil;
import com.gionee.framework.operation.utills.StringUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.fb.FeedbackAgent;
import com.upgrate.manage.Download;
import com.upgrate.manage.UpgrateDownloadManage;
import com.wayde.ads.EggAdsProxy;

@SuppressLint("NewApi")
public class GnHomeActivity extends BaseFragmentActivity implements
		OnCheckedChangeListener, TabChangeListener, OnClickListener {

	private static final String STORY_DETAIL = ".*Story/detail.*";
	private static final String HOME_TAB_SWITCH = "home_tab_switch";
	private static final String MORE_FRAGMENT = "moreFragment";
	public static final String SEARCH_FRAGMENT = "searchFragment";
	public static final String HALL_FRAGMENT = "hallFragment";
	private static final String STORY_FRAGMENT = "storyFragment";
	private static final String TAG = "GnHomeActivity";
	private static final String[] FRAGMENT_TAGS = { HALL_FRAGMENT,
			SEARCH_FRAGMENT, STORY_FRAGMENT, MORE_FRAGMENT };
	private static final int[] FRAGMENT_IDS = { R.id.tab_recommond,
			R.id.classify_search, R.id.story, R.id.more };

	private static final int ACTIVITY_HOME_ID = 0;
	private static final int ACTIVITY_CLASSIFY_ID = 1;
	private static final int ACTIVITY_ID = 2;
	private static final int ACTIVITY_STORY_ID = 3;
	private static final int ACTIVITY_MORE_ID = 4;
	private int mCurrentActivityId = 0;
	private int mCurrentRadiobarCheckId = FRAGMENT_IDS[0];
	private static final int[] FRAGMENT_ACTIVITY_IDS = { ACTIVITY_HOME_ID,
			ACTIVITY_CLASSIFY_ID, ACTIVITY_ID, ACTIVITY_STORY_ID,
			ACTIVITY_MORE_ID };
	private static final int[] TAG_STRINGS = { R.string.shopping_mall,
			R.string.classify_search, 0, R.string.story, R.string.me_tab };
	private String mTagTextColorDefault = "";
	private String mTagTextColorPressed = "";
	private String mActivityLink = "";
	private Map<Integer, Bitmap> mTagDrawableDefalutMap = new HashMap<Integer, Bitmap>();
	private Map<Integer, Bitmap> mTagDrawablePressedMap = new HashMap<Integer, Bitmap>();
	/** top navigation bar. **/
	private RadioGroup mTabBarRadio;
	private RadioButton mHomeRadioButton;
	// private RelativeLayout mLoadingLayout;

	private static final int DIALOG_EXIT_APP = 0;
	// private UpgradeManager mUpgradeManager;
	private UpgrateDownloadManage mUpgrateDownloadManage;
	/** index of current page. **/
	private String mCurrentPage;
	private FeedbackAgent mFeedbackAgent;
	private int mCount;
	private StatisticAction mStatisticAction;
	/** message */
	private static final int MESSAGE_CHECK_NOW = 10;
	// private static GradeHandler sHandler;
	private static FragmentManager sFragmentManager;
	private CategoryFragment mCategoryFragment;
	private HomeFragment mHomeFragment;
	private BaseFragment mStoryFragment;
	private MoreFragment mMoreFragment;
	private boolean mExitApp = false;
	private static final long EXIT_TIME = 2000;
	private static final String SAVE_PAGE_INDEX = "current_page_index";
	private static final String SAVE_PAGE_NAME = "current_page_name";
	private ImageView mLowPriceNotice;
	public boolean mIsHasMsgClicked;
	private static final String MSG_LIST_ACTION = "com.gionee.client.MesaggeList";
	private RequestAction mRequestAction = new RequestAction();
	private IStatisticsEventManager mStatisticsEventManager;
	private boolean mNoNeedStatisticsExit;
	private boolean mIsClickedOnCategoryFragment;
	private boolean mIsClickedOnMineFragment;
	private String mIntentSource;
	private boolean mIsGotoStoryDetail;
	private boolean mIsGotoStoryList;
	
	//跳到知物
	private boolean  mIsComments;
	

	private LinearLayout mTabBarRl;// 有活动时的tab
	private boolean mIsHasActivity = false;// 是否有活动
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message arg0) {
			// TODO Auto-generated method stub
			initRadioGroupActivity();
			if (!(mTagDrawableDefalutMap.size() == 5 && mTagDrawablePressedMap
					.size() == 5) && mIsHasActivity) {
				mHandler.sendEmptyMessageDelayed(0, 1000);
			}
			return false;
		}
	});

	public String getCurrentsFragmentTag() {
		return mCurrentPage;
	}

	public void setIsClickEventOnCategoryFragment(boolean flag) {
		mIsClickedOnCategoryFragment = flag;
	}

	public void setIsClickedOnMineFragment(boolean misClickedOnMineFragment) {
		this.mIsClickedOnMineFragment = misClickedOnMineFragment;
	}

	public void setExitStatisticsFlag(boolean exitStatisticsFlag) {
		mNoNeedStatisticsExit = exitStatisticsFlag;
	}

	public interface MenuOperation {
		public void setMenu(BasicMyMenu menu);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_home_page);
		// mUpgradeManager = new UpgradeManager(this,
		// UpgradeUtils.getProductKey(this.getApplicationContext()));
		mUpgrateDownloadManage = new UpgrateDownloadManage(this, true);
		
		Intent intent = getIntent();
		mIsComments = intent.getBooleanExtra("mIsComments", false);
		
		initData();
		initView();
		requestNetwork();
		initFragment(savedInstanceState);
		syncInit();
		initSdcardListener();
		if (null == savedInstanceState) {
			doPushAction();
		}
		EggAdsProxy.startEggAds(this);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void requestNetwork() {
		getUrlMatcherConfig();
	}

	private void cumulateEverydayCheckScore() {
		RequestAction action = new RequestAction();
		action.cumulateScore(this, Constants.ScoreTypeId.CHECK_IN);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void getUrlMatcherConfig() {
		RequestAction action = new RequestAction();
		action.getMatchRegular(this,
				HttpConstants.Data.RecommendHome.MATCH_REGULAR_JO);
		action.hasLowPrice(this,
				HttpConstants.Data.RecommendHome.LOW_PRICE_NOTICE_JO);
		action.getUserConfig(this,
				HttpConstants.Data.RecommendHome.USER_INFO_JO);
	}

	/**
	 * @param savedInstanceState
	 * @author yuwei
	 * @description TODO
	 */
	private void initFragment(Bundle savedInstanceState) {
		//heshicaihao
		if (mIsComments) {
			checkStory();
			mTabBarRadio.check(FRAGMENT_IDS[2]);
		}else{
			if (savedInstanceState != null) {
				popAllFragments();
				mCurrentPage = savedInstanceState.getString(SAVE_PAGE_NAME);
				int checkId = savedInstanceState.getInt(SAVE_PAGE_INDEX);
				radioGroupCheck(checkId);
				createFragmentByTag(mCurrentPage);
			} else {
				createHomeFragment();
				mTabBarRadio.check(R.id.tab_recommond);
			}
		}
		
		
	}

	private void radioGroupCheck(int checkId) {
		if (mIsHasActivity) {
			if (mTabBarRl != null) {
				clickActivityItem(checkId);
			}
		} else {
			mTabBarRadio.check(checkId);
		}
	}

	private void changeTabRadioButtonStyle(int position) {
		// TODO Auto-generated method stub
		for (int i = 0; i < mTabBarRl.getChildCount(); i++) {
			RelativeLayout rlItem = (RelativeLayout) mTabBarRl.getChildAt(i);
			final ImageView icon = (ImageView) rlItem
					.findViewById(R.id.tab_widget_icon);
			TextView content = (TextView) rlItem
					.findViewById(R.id.tab_widget_content);
			if (i == position) {
				content.setTextColor(Color.parseColor(mTagTextColorPressed));
				final Bitmap bitmap = mTagDrawablePressedMap.get(i);
				// TODO Auto-generated method stub
				setImageBitmapByDimen(icon, bitmap);
			} else {
				content.setTextColor(Color.parseColor(mTagTextColorDefault));
				Bitmap bitmap = mTagDrawableDefalutMap.get(i);
				setImageBitmapByDimen(icon, bitmap);
			}
		}

	}

	private void setImageBitmapByDimen(ImageView icon, Bitmap bitmap) {
		if (bitmap != null) {
			android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) icon
					.getLayoutParams();
			float scale = getResources().getDisplayMetrics().density;
			params.width = (int) (bitmap.getWidth() * scale / 3);
			params.height = (int) (bitmap.getHeight() * scale / 3);
			icon.setLayoutParams(params);
			icon.setImageBitmap(bitmap);
		}
	}

	@Override
	public void onSucceed(String businessType, boolean isCache, Object session) {
		LogUtils.log(TAG, LogUtils.getThreadName() + " businessType = "
				+ businessType);
		initConfigData(businessType);
		initLowPriceData(businessType);
		initUserInfo(businessType);
		initScoreConfig(businessType);
		// removeStatisticsData(businessType);
		initNavmainData(businessType);
	}

	private void initNavmainData(String businessType) {
		// TODO Auto-generated method stub
		if (businessType.equals(Url.NAVMAIN)) {
			JSONObject mConfigObject = mSelfData
					.getJSONObject(HttpConstants.Data.Navmain.DATA_JO);
			setTabData(mConfigObject);
			FileUtil.deleteFileFromSdcard(Constants.Home.ACTIVITY_DIR,
					Constants.Home.ACTIVITY_TAB_DATA);
			FileUtil.writeStringToSdcard(Constants.Home.ACTIVITY_DIR,
					Constants.Home.ACTIVITY_TAB_DATA, mConfigObject);
		}
	}

	@Override
	public void onErrorResult(String businessType, String errorOn,
			String errorInfo, Object session) {
		super.onErrorResult(businessType, errorOn, errorInfo, session);
		// saveStatisticsData(businessType);
	}

	// private void saveStatisticsData(String businessType) {
	// if (businessType.equals(Url.STATISTICS_SUBMIT)) {
	// mStatisticsEventManager.saveStatisticsData();
	// }
	// }

	// private void removeStatisticsData(String businessType) {
	// if (businessType.equals(Url.STATISTICS_SUBMIT)) {
	// mStatisticsEventManager.removeStatisticsData();
	// }
	// }

	public void initScoreConfig(String businessType) {
		if (businessType.equals(Url.CUMULATE_SCORE_URL)) {
			Fragment fragment = sFragmentManager
					.findFragmentByTag(HALL_FRAGMENT);
			if (fragment != null) {
				((HomeFragment) fragment).startEverydayCheckAimation();
			}
		}
	}

	public void initUserInfo(String businessType) {
		if (businessType.equals(Url.USER_CONFIG_URL)) {
			try {
				JSONObject mUserInfoObj = mSelfData
						.getJSONObject(HttpConstants.Data.RecommendHome.USER_INFO_JO);
				UserInfoManager.getInstance().init(this, mUserInfoObj);
				if (hasNewNotify(mUserInfoObj) && !mIsHasMsgClicked) {
					mLowPriceNotice.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void hasVersionNotify() {
		if (!mIsHasMsgClicked) {
			mLowPriceNotice.setVisibility(View.VISIBLE);
		}
	}

	public boolean hasNewNotify(JSONObject mUserInfoObj) {
		return mUserInfoObj
				.optBoolean(HttpConstants.Response.UserInfo.HAS_MSG_I)
				|| UserInfoManager.getInstance().isHasNewFeedbackReplay()
				|| UpgrateDownloadManage.sIsHasNewVersion
				|| UrlMatcher.getInstance().isHasNewWeiboNotice(
						ShareDataManager.getString(this,
								Constants.WEIBO_NOTICE, null));
	}

	/**
	 * 添加购物大厅自己的流量统计
	 */
	public void addFlowStatistics(String eventId) {
		mStatisticsEventManager.add(eventId);
	}

	public void initLowPriceData(String businessType) {
		if (businessType.equals(Url.LOW_PRICE_NOTICE_URL)) {
			setLowPriceNotice();
		}
	}

	public void initConfigData(String businessType) {
		if (businessType.equals(Url.MATCHING_REGULATION_URL)) {
			UrlMatcher.getInstance().initMatcherRegularList();
			JSONObject mConfigObject = mSelfData
					.getJSONObject(HttpConstants.Data.RecommendHome.MATCH_REGULAR_JO);
			UrlMatcher.getInstance().init(this, mConfigObject);
			ConfigManager.getInstance().init(this, mConfigObject);
			cumulateEverydayCheckScore();
			mIsHasActivity = ConfigManager.getInstance().getIsActivity(this);
			mHandler.sendEmptyMessage(0);
		}
	}

	private void setLowPriceNotice() {
		try {
			JSONObject mLowPriceNoticeJo = mSelfData
					.getJSONObject(HttpConstants.Data.RecommendHome.LOW_PRICE_NOTICE_JO);
			boolean mHasLowPrice = mLowPriceNoticeJo
					.optBoolean(HttpConstants.Response.LowPriceNotice.REDUCE_B);
			if ((mHasLowPrice || UpgrateDownloadManage.sIsHasNewVersion)
					&& !mIsHasMsgClicked) {
				mLowPriceNotice.setVisibility(View.VISIBLE);
			} else {
				mLowPriceNotice.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void syncInit() {
		LocalBuisiness.getInstance().getHandler().post(new Runnable() {
			@Override
			public void run() {
				getFeedBackInfo();
				doConnection();
				LocationUtils.startLocation(GnHomeActivity.this);
			}
		});
	}

	/**
	 * @author yuwei
	 * @description TODO
	 */
	private void initData() {
		GNActivityManager.getScreenManager().pushActivity(GnHomeActivity.this);
		GNImageLoader.getInstance().init(this);
		// sHandler = new GradeHandler(GnHomeActivity.this);
		mStatisticAction = new StatisticAction();
		ShareSDK.initSDK(this);
		mStatisticsEventManager = new StatisticsEventManager(this);
		mStatisticsEventManager.initStatisticsData();
		addFlowStatistics(StatisticsConstants.HomePageConstants.OPEN_HOME_PAGE);
		String source = getIntent().getStringExtra(Constants.Push.SOURCE);
		LogUtils.logd(TAG, LogUtils.getThreadName() + " source = " + source);
		if (BaiduPushReceiver.PUSH_STYLE_BAIDU.equals(source)) {
			mIntentSource = Constants.BootSource.PUSH_STYLE_BAIDU;
		} else if (PushNotificationTask.PUSH_STYLE_BAIDU_LOCAL.equals(source)) {
			mIntentSource = Constants.BootSource.PUSH_STYLE_LOCAL;
		} else {
			mIntentSource = Constants.BootSource.NORMAL;
		}
	}

	private void doPushAction() {
		String data = getIntent().getStringExtra(Constants.Push.DATA);
		if (TextUtils.isEmpty(data)) {
			return;
		}
		try {
			JSONObject object = new JSONObject(data);
			PushDataInfo pushInfo = new PushDataInfo();
			pushInfo.setmAction(object.optString(Constants.Push.ACTIION));
			pushInfo.setmType(object.optString(Constants.Push.TYPE));
			pushInfo.setmPushUrl(object.optString(Constants.Push.URL));
			pushInfo.setmStatisticId(object.optString(Constants.Push.ID));
			pushInfo.setmGameId(object.optInt(Constants.Push.GAME_ID));
			pushInfo.setmActivity(object.optString(Constants.Push.ACTIVITY));
			LogUtils.log(TAG, "push data : " + pushInfo.toString());
			if (!TextUtils.isEmpty(pushInfo.getmStatisticId())) {
				StatService.onEvent(this, BaiduStatConstants.PUSH_ID,
						pushInfo.getmStatisticId());
			}
			if (pushDataIsEmpty(pushInfo)) {
				return;
			}
			if (!TextUtils.isEmpty(pushInfo.getmAction())) {
				pushActionJump(pushInfo);
			} else if (!TextUtils.isEmpty(pushInfo.getmActivity())) {
				pushActivityJump(pushInfo);
			} else {
				String url = pushInfo.getmPushUrl();
				LogUtils.log(TAG, LogUtils.getThreadName() + " url = " + url);
				if (UrlMatcher.getInstance().matchedUrl(url, STORY_DETAIL)) {
					LogUtils.log(TAG, LogUtils.getThreadName()
							+ "matcher success.");
					mIsGotoStoryDetail = true;
				}
				gotoWebPage(pushInfo.getmPushUrl(), true);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			LogUtils.log(TAG, "dopush exception:" + e);
		}
	}

	private boolean pushDataIsEmpty(PushDataInfo pushInfo) {
		return TextUtils.isEmpty(pushInfo.getmPushUrl())
				&& TextUtils.isEmpty(pushInfo.getmAction())
				&& TextUtils.isEmpty(pushInfo.getmActivity());
	}

	private void pushActivityJump(PushDataInfo pushInfo) {
		try {
			String activity = pushInfo.getmActivity();
			if (!TextUtils.isEmpty(activity)
					&& activity.equals(MSG_LIST_ACTION)) {
				mIsHasMsgClicked = true;
			}

			if (activity.equals(Constants.BannerAction.BARGAIN_GAME_PAGE
					.getValue())) {
				gotoBarginPricePage(pushInfo);
				return;
			}

			Intent intent = new Intent();
			intent.setAction(activity.trim());
			if (!TextUtils.isEmpty(pushInfo.getmType())) {
				intent.putExtra(Constants.Home.KEY_INTENT_INDEX,
						pushInfo.getmType());
			}
			boolean isHttpUrl = AndroidUtils.isHttpAction(activity);
			if (isHttpUrl) {
				intent.putExtra(Constants.Home.KEY_INTENT_URL,
						pushInfo.getmPushUrl());
			}
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			gotoWebPage(pushInfo.getmPushUrl(), true);
		}
	}

	private void pushActionJump(PushDataInfo pushInfo) {
		try {
			if (pushInfo.getmAction().equals(
					Constants.BannerAction.STORY_LIST_PAGE.getValue())) {
				openStoryPage();
			} else if (pushInfo.getmAction().equals(
					Constants.BannerAction.BARGAIN_GAME_PAGE.getValue())) {
				gotoBarginPricePage(pushInfo);
			} else {
				startAction(pushInfo.getmPushUrl(), pushInfo.getmAction(),
						pushInfo.getmType());
			}
		} catch (Exception e) {
			gotoWebPage(pushInfo.getmPushUrl(), true);
			e.printStackTrace();
		}
	}

	private void gotoBarginPricePage(PushDataInfo info) {
		// LogUtils.log(TAG, LogUtils.getFunctionName() + info.getmGameId());
		// MyBean bean =
		// PageCacheManager.LookupPageData(BargainGameActivity.class.getName());
		// bean.put(HttpConstants.Data.GameDetail.ID_I, info.getmGameId());
		// Intent intent = new Intent();
		// intent.setClass(this, BargainGameActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);
		// StatService.onEvent(this, BaiduStatConstants.SURPASS_PUSH_OPEN, "");
	}

	private void openStoryPage() {
		mIsGotoStoryList = true;
		mTabBarRadio.postDelayed(new Runnable() {

			@Override
			public void run() {
				radioGroupCheck(R.id.story);
			}
		}, 50);
	}

	private void startAction(String bannerUrl, String bannerAction,
			String bannerType) {
		LogUtils.log(TAG, LogUtils.getThreadName() + "bannerUrl=" + bannerUrl
				+ "bannerAction=" + bannerAction);
		if (!TextUtils.isEmpty(bannerAction)
				&& bannerAction.equals(MSG_LIST_ACTION)) {
			mIsHasMsgClicked = true;
		}
		Intent intent = new Intent();
		intent.setAction(bannerAction.trim());
		if (!TextUtils.isEmpty(bannerType)) {
			intent.putExtra(Constants.Home.KEY_INTENT_INDEX, bannerType);
		}
		boolean isHttpUrl = AndroidUtils.isHttpAction(bannerAction);
		if (isHttpUrl) {
			intent.putExtra(Constants.Home.KEY_INTENT_URL, bannerUrl);
		}

		startActivity(intent);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void getFeedBackInfo() {
		if (AndroidUtils.isHadPermission(GnHomeActivity.this,
				Manifest.permission.READ_PHONE_STATE)) {
			mFeedbackAgent = new FeedbackAgent(this);
			mFeedbackAgent.sync();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		LogUtils.log(TAG, LogUtils.getThreadName() + "id:" + id);
		switch (id) {
		case DIALOG_EXIT_APP:
			return DialogFactory.createAppExitDialog(GnHomeActivity.this);
		}
		return null;
	}

	private void initView() {
		sFragmentManager = getSupportFragmentManager();
		mTabBarRadio = (RadioGroup) findViewById(R.id.tab_radio);
		mTabBarRadio.setOnCheckedChangeListener(this);
		mHomeRadioButton = (RadioButton) findViewById(R.id.tab_recommond);
		mHomeRadioButton.setOnClickListener(this);
		((RadioButton) findViewById(R.id.classify_search))
				.setOnClickListener(this);
		((RadioButton) findViewById(R.id.story)).setOnClickListener(this);
		((RadioButton) findViewById(R.id.more)).setOnClickListener(this);
		mLowPriceNotice = (ImageView) findViewById(R.id.low_price_notice);
		mTabBarRl = (LinearLayout) findViewById(R.id.tab_rl_activity);
	}

	/**
	 * 初始化活动时候的底部导航
	 */
	private void initRadioGroupActivity() {
		if (!mIsHasActivity) {
			if (mTabBarRl.getVisibility() == View.VISIBLE) {
				Animation mAnimation = AnimationUtils.loadAnimation(this,
						R.anim.home_activity_tab_alpha_out);
				mTabBarRl.setAnimation(mAnimation);
				mTabBarRl.setVisibility(View.GONE);
				mTabBarRadio.check(mCurrentRadiobarCheckId);
				android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mLowPriceNotice
						.getLayoutParams();
				params.rightMargin = AndroidUtils.dip2px(this, 25);
				mLowPriceNotice.setLayoutParams(params);
			}
			mTabBarRadio.setVisibility(View.VISIBLE);
			FileUtil.deleteFileFromSdcard(Constants.Home.ACTIVITY_DIR,
					Constants.Home.ACTIVITY_TAB_DATA);
			return;
		}
		String bean = FileUtil.readStringFromSdcard(
				Constants.Home.ACTIVITY_DIR, Constants.Home.ACTIVITY_TAB_DATA);
		boolean isRequestActivityTabData = false;
		JSONObject tabData = null;
		if (TextUtils.isEmpty(bean)) {
			isRequestActivityTabData = true;
		} else {
			try {
				tabData = new JSONObject(bean);
				String lastVersion = tabData
						.optString(HttpConstants.Data.Navmain.VERSION_S);
				String currentVersion = ConfigManager.getInstance()
						.getCurrentVersion(this);
				if (lastVersion.equals(currentVersion)) {
					isRequestActivityTabData = false;
				} else {
					isRequestActivityTabData = true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				isRequestActivityTabData = true;
				e.printStackTrace();
			}
		}
		if (!isRequestActivityTabData) {
			setTabData(tabData);
			return;
		}
		FileUtil.deleteFileFromSdcard(Constants.Home.ACTIVITY_DIR,
				Constants.Home.ACTIVITY_TAB_DATA);
		requestActivityTabData();
	}

	private void setTabData(JSONObject tabData) {
		mTabBarRl.removeAllViews();
		String[] urlsDefault = new String[] {
				tabData.optString(HttpConstants.Data.Navmain.ICON_1_1_S),
				tabData.optString(HttpConstants.Data.Navmain.ICON_2_1_S),
				tabData.optString(HttpConstants.Data.Navmain.ICON_3_1_S),
				tabData.optString(HttpConstants.Data.Navmain.ICON_4_1_S),
				tabData.optString(HttpConstants.Data.Navmain.ICON_5_1_S) };
		String[] urlsPressed = new String[] {
				tabData.optString(HttpConstants.Data.Navmain.ICON_1_2_S),
				tabData.optString(HttpConstants.Data.Navmain.ICON_2_2_S),
				tabData.optString(HttpConstants.Data.Navmain.ICON_3_2_S),
				tabData.optString(HttpConstants.Data.Navmain.ICON_4_2_S),
				tabData.optString(HttpConstants.Data.Navmain.ICON_5_2_S) };
		mActivityLink = tabData
				.optString(HttpConstants.Data.Navmain.ICON_5_LINK_S);
		String rbBgColor = tabData
				.optString(HttpConstants.Data.Navmain.TAB_BG_S);
		mTagTextColorDefault = tabData
				.optString(HttpConstants.Data.Navmain.TXT_COLOR_1_S);
		mTagTextColorPressed = tabData
				.optString(HttpConstants.Data.Navmain.TXT_COLOR_2_S);
		for (int i = 0; i < 5; i++) {
			String text = "";
			int height = AndroidUtils.dip2px(this, 51);
			if (i != 2) {
				text = getString(TAG_STRINGS[i]);
			} else {
				height = AndroidUtils.dip2px(this, 62);
			}
			RelativeLayout itemRl = createTabView(i, text, urlsDefault[i],
					rbBgColor, mTagTextColorDefault);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					height);
			params.weight = 1.0f;
			params.gravity = Gravity.BOTTOM;
			params.bottomMargin = 0;
			itemRl.setId(FRAGMENT_ACTIVITY_IDS[i]);
			itemRl.setLayoutParams(params);
			itemRl.setOnClickListener(this);
			mTabBarRl.addView(itemRl);
			loadImg(urlsPressed[i], i);
		}

	}

	private void loadImg(String urlsPressed, final int position) {
		GNImageLoader.getInstance().loadBitmap(urlsPressed,
				new ImageView(this), new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						// Log.i("hhhhhh", "onLoadingComplete:" + position +
						// imageUri);
						mTagDrawablePressedMap.put(position, loadedImage);
						isLoadCompleteAndShowActivityTab();
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub
						// Log.i("hhhhhh", "onLoadingCancelled:" + position +
						// imageUri);
					}

				});
	}

	private void requestActivityTabData() {
		// TODO Auto-generated method stub
		mRequestAction.getActivityTabData(this,
				HttpConstants.Data.Navmain.DATA_JO, this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			clearAddBtn();
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void clearAddBtn() {
		try {
			if (mCurrentPage.equals(HALL_FRAGMENT)) {
				((HomeFragment) sFragmentManager
						.findFragmentByTag(HALL_FRAGMENT)).mMyAttentionAdapter
						.notifyDataSetChanged();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch (arg1) {
		case R.id.tab_recommond:
			checkRecommond();
			break;

		case R.id.classify_search:
			checkClassfySearch();
			break;

		case R.id.more:
			checkMore();
			break;
		case R.id.story:
			checkStory();
			break;
		default:
			break;
		}
		showFragmentByIndex(mCurrentPage);
	}

	private void checkStory() {
		if (!STORY_FRAGMENT.equals(mCurrentPage)) {
			addFlowStatistics(StatisticsConstants.HomePageConstants.BOTTOM_TAB_3);
			addCategoryExitStatistics();
			addMineExitStatistics();
			mCurrentActivityId = ACTIVITY_STORY_ID;
			mCurrentRadiobarCheckId = FRAGMENT_IDS[2];
			mCurrentPage = STORY_FRAGMENT;
			createStroryFragment();
			StatService.onEvent(this, HOME_TAB_SWITCH, "story");
		}
	}

	private void checkMore() {
		if (!MORE_FRAGMENT.equals(mCurrentPage)) {
			addFlowStatistics(StatisticsConstants.HomePageConstants.BOTTOM_TAB_4);
			if (HALL_FRAGMENT.equals(mCurrentPage)) {
				addFlowStatistics(StatisticsConstants.MinePageConstants.HOME_TO_MINE);
			} else if (SEARCH_FRAGMENT.equals(mCurrentPage)) {
				addFlowStatistics(StatisticsConstants.MinePageConstants.CATEGORY_TO_MINE);
			} else if (STORY_FRAGMENT.equals(mCurrentPage)) {
				addFlowStatistics(StatisticsConstants.MinePageConstants.STORY_TO_MINE);
			}
			addCategoryExitStatistics();
			mIsHasMsgClicked = true;
			mCurrentActivityId = ACTIVITY_MORE_ID;
			mCurrentRadiobarCheckId = FRAGMENT_IDS[3];
			mCurrentPage = MORE_FRAGMENT;
			createMoreFragment();
			mLowPriceNotice.setVisibility(View.GONE);
			StatService.onEvent(this, HOME_TAB_SWITCH, "mine");
		}
	}

	private void addCategoryExitStatistics() {
		if (!mIsClickedOnCategoryFragment
				&& SEARCH_FRAGMENT.equals(mCurrentPage)) {
			addFlowStatistics(StatisticsConstants.CategoryPageConstants.CATEGORY_EXIT);
		}
	}

	private void addMineExitStatistics() {
		if (!mIsClickedOnMineFragment && MORE_FRAGMENT.equals(mCurrentPage)) {
			addFlowStatistics(StatisticsConstants.MinePageConstants.EXIT);
		}
	}

	private void checkClassfySearch() {
		if (!SEARCH_FRAGMENT.equals(mCurrentPage)) {
			addFlowStatistics(StatisticsConstants.HomePageConstants.BOTTOM_TAB_2);
			if (HALL_FRAGMENT.equals(mCurrentPage)) {
				addFlowStatistics(StatisticsConstants.CategoryPageConstants.HOME_TO_CATEGORY);
			} else if (STORY_FRAGMENT.equals(mCurrentPage)) {
				addFlowStatistics(StatisticsConstants.CategoryPageConstants.STORY_TO_CATEGORY);
			} else if (MORE_FRAGMENT.equals(mCurrentPage)) {
				addFlowStatistics(StatisticsConstants.CategoryPageConstants.MINE_TO_CATEGORY);
			}
			addMineExitStatistics();
			mCurrentPage = SEARCH_FRAGMENT;
			mCurrentActivityId = ACTIVITY_CLASSIFY_ID;
			mCurrentRadiobarCheckId = FRAGMENT_IDS[1];
			createSearchFragment();
			StatService.onEvent(this, HOME_TAB_SWITCH, "category");
		}
	}

	private void checkRecommond() {
		if (!HALL_FRAGMENT.equals(mCurrentPage)) {
			addCategoryExitStatistics();
			addMineExitStatistics();
			mCurrentPage = HALL_FRAGMENT;
			mCurrentActivityId = ACTIVITY_HOME_ID;
			mCurrentRadiobarCheckId = FRAGMENT_IDS[0];
			createHomeFragment();
			StatService.onEvent(this, HOME_TAB_SWITCH, "recommond");
			addFlowStatistics(StatisticsConstants.HomePageConstants.BOTTOM_TAB_1);
		}
	}

	private void createFragmentByTag(String tag) {
		if (tag.equals(HALL_FRAGMENT)) {
			createHomeFragment();
		} else if (tag.equals(SEARCH_FRAGMENT)) {
			createSearchFragment();
		} else if (tag.equals(STORY_FRAGMENT)) {
			createStroryFragment();
		} else if (tag.equals(MORE_FRAGMENT)) {
			createMoreFragment();
		}

		showFragmentByTag(mCurrentPage);

	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void createMoreFragment() {
		if (mMoreFragment == null
				|| sFragmentManager.findFragmentByTag(MORE_FRAGMENT) == null) {
			mMoreFragment = new MoreFragment();
			addFragmentToContainner(mMoreFragment, MORE_FRAGMENT);
		}
		removeLowPrice();
	}

	private void removeLowPrice() {
		try {
			boolean isRemoveLowPrice = getIntent().getBooleanExtra(
					Constants.CLEAR_LOW_PRICE, false);
			if (isRemoveLowPrice) {
				mMoreFragment.removeLowPriceNotice();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void createStroryFragment() {
		if (mStoryFragment == null
				|| sFragmentManager.findFragmentByTag(STORY_FRAGMENT) == null) {
			mStoryFragment = new CommentsFragment();
			addFragmentToContainner(mStoryFragment, STORY_FRAGMENT);
		}
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void createSearchFragment() {
		if (mCategoryFragment == null
				|| sFragmentManager.findFragmentByTag(SEARCH_FRAGMENT) == null) {
			mCategoryFragment = new CategoryFragment();
			addFragmentToContainner(mCategoryFragment, SEARCH_FRAGMENT);
		}
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void createHomeFragment() {
		if (mHomeFragment == null
				|| sFragmentManager.findFragmentByTag(HALL_FRAGMENT) == null) {
			mHomeFragment = new HomeFragment();
			addFragmentToContainner(mHomeFragment, HALL_FRAGMENT);
			mCurrentPage = HALL_FRAGMENT;
		}
	}

	private void showFragmentByIndex(String tag) {
		int lenth = FRAGMENT_TAGS.length;
		for (int i = 0; i < lenth; i++) {
			if (!FRAGMENT_TAGS[i].equals(tag)) {
				hideFragmentByTag(FRAGMENT_TAGS[i]);
			}
		}
		showFragmentByTag(tag);

	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void addFragmentToContainner(Fragment fragment, String tag) {
		try {
			FragmentTransaction fragmentTransaction = sFragmentManager
					.beginTransaction();
			fragmentTransaction.add(R.id.content_containner, fragment, tag);
			fragmentTransaction.addToBackStack(tag);
			fragmentTransaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void hideFragmentByTag(String tag) {
		try {
			FragmentTransaction fragmentTrans = sFragmentManager
					.beginTransaction();
			Fragment fragment = sFragmentManager.findFragmentByTag(tag);
			if (fragment != null) {
				fragmentTrans.hide(fragment);
				fragment.setUserVisibleHint(false);
			}
			fragmentTrans.commitAllowingStateLoss();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showFragmentByTag(String tag) {
		FragmentTransaction fragmentTrans = sFragmentManager.beginTransaction();
		Fragment fragment = sFragmentManager.findFragmentByTag(tag);
		if (fragment != null) {
			fragmentTrans.show(fragment);
			fragment.setUserVisibleHint(true);

		}
		fragmentTrans.commitAllowingStateLoss();
	}

	public static void popAllFragments() {
		for (int i = 0, count = sFragmentManager.getBackStackEntryCount(); i < count; i++) {
			sFragmentManager.popBackStack();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(SAVE_PAGE_NAME, mCurrentPage);
		// if (isHasActivity) {
		// outState.putInt(SAVE_PAGE_INDEX,
		// mTabBarRl.getCheckedRadioButtonId());
		// } else {
		outState.putInt(SAVE_PAGE_INDEX, mTabBarRadio.getCheckedRadioButtonId());
		// }
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onRestoreInstanceState(savedInstanceState);
		radioGroupCheck(savedInstanceState.getInt(SAVE_PAGE_INDEX));
		mCurrentPage = savedInstanceState.getString(SAVE_PAGE_NAME);

		if (mMoreFragment != null) {
			mMoreFragment.reload(true);
		}
	}

	@Override
	protected void onResume() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onResume();
		StatService.onResume(this);
		setLowPriceNotice();
		mUpgrateDownloadManage.setHandler();
	}

	@Override
	protected void onPause() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			LogUtils.log(TAG, LogUtils.getThreadName());
			if (mCurrentPage.equals(HALL_FRAGMENT)) {
				exitApp();
			} else {
				mCurrentRadiobarCheckId = R.id.tab_recommond;
				mCurrentActivityId = ACTIVITY_HOME_ID;
				radioGroupCheck(mCurrentRadiobarCheckId);
			}

			return true;
		}
		return false;
	}

	private void exitApp() {
		if (mExitApp) {
			mUpgrateDownloadManage.isAlertInstall();
			GNActivityManager.getScreenManager().popAllActivity();
			submitStatisticsData();
			sendExitBroadcast();
			mTabBarRadio.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// Download.getInstance(GnHomeActivity.this).exit();
					// GnHomeActivity.this.finish();
					android.os.Process.killProcess(android.os.Process.myPid());
					// System.exit(0);
				}
			}, 100);
		} else {
			Toast.makeText(this, getString(R.string.is_exit),
					Toast.LENGTH_SHORT).show();
			mExitApp = true;
			mTabBarRadio.postDelayed(new Runnable() {
				@Override
				public void run() {
					mExitApp = false;
				}
			}, EXIT_TIME);
		}
	}

	private void sendExitBroadcast() {
		Intent intent = new Intent(PromotionalSaleService.APP_EXIT_SERVICE);
		intent.putExtra("mIntentSource", mIntentSource);
		sendBroadcast(intent);
	}

	private void hideKeybord() {
		try {
			View v = getWindow().peekDecorView();
			if (v != null && v.getWindowToken() != null) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.log(TAG, LogUtils.getThreadName() + " requestCode = "
				+ requestCode + ", resultCode = " + resultCode);
		hideKeybord();

		switch (requestCode) {
		case Constants.ActivityRequestCode.REQUEST_CODE_MY_FAVORITE:
			switch (resultCode) {
			case Constants.ActivityResultCode.RESULT_CODE_STORY:
				radioGroupCheck(R.id.story);
				refreshCommentsFragment();
				break;
			case Constants.ActivityResultCode.RESULT_CODE_GOODS:
			case Constants.ActivityResultCode.RESULT_CODE_SHOP:
			case Constants.ActivityResultCode.RESULT_CODE_WEBPAGE:
			case Constants.ActivityResultCode.RESULT_CODE_SHOPPING:
				radioGroupCheck(R.id.tab_recommond);
				break;
			default:
				refreshCommentsFragment();
				break;
			}

			break;
		case Constants.ActivityRequestCode.REQUEST_CODE_COMMENT_DETAIL:
			if (data == null) {
				LogUtils.log(TAG, LogUtils.getThreadName()
						+ " data(intent) == null");
				return;
			}
			refreshClickedCommentsItem(data);
			break;

		case Constants.ActivityRequestCode.REQUEST_CODE_HOME:
			if (resultCode == RESULT_OK) {
				radioGroupCheck(R.id.tab_recommond);
			}
			break;

		case Constants.ActivityRequestCode.REQUEST_CODE_WEB_PAGE:
			if (HALL_FRAGMENT.equals(mCurrentPage)) {
				addFlowStatistics(StatisticsConstants.HomePageConstants.BACK_HOME_PAGE);
			} else if (SEARCH_FRAGMENT.equals(mCurrentPage)) {
				addFlowStatistics(StatisticsConstants.CategoryPageConstants.OTHER_TO_CATEGORY);
			}
			break;
		case Constants.ActivityRequestCode.REQUEST_CODE_CUT_PAGE:
		case Constants.ActivityRequestCode.REQUEST_CODE_BARGAIN_PRICE:
			if (HALL_FRAGMENT.equals(mCurrentPage)) {
				addFlowStatistics(StatisticsConstants.HomePageConstants.BACK_HOME_PAGE);
			}
			break;
		case Constants.ActivityRequestCode.REQUEST_CODE_SEARCH:
		case Constants.ActivityRequestCode.REQUEST_CODE_TWO_DIMENSION:
			if (SEARCH_FRAGMENT.equals(mCurrentPage)) {
				addFlowStatistics(StatisticsConstants.CategoryPageConstants.OTHER_TO_CATEGORY);
			}
			break;

		default:
			break;
		}
		switch (resultCode) {
		case Constants.Home.MORE_PAGE_RESULT:
			RequestAction action = new RequestAction();
			action.getUserConfig(this,
					HttpConstants.Data.RecommendHome.USER_INFO_JO);
			if (mMoreFragment != null) {
				mMoreFragment.reload(true);
			}
			break;
		case Constants.ActivityResultCode.REQUEST_CODE_PROFILE:
			if (mMoreFragment != null) {
				mMoreFragment.reloadProfile();
				LogUtils.log(TAG, " more page refresh!");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @author yangxiong
	 * @description TODO
	 */
	private void refreshCommentsFragment() {
		Fragment fragment = sFragmentManager.findFragmentByTag(STORY_FRAGMENT);
		if (fragment != null) {
			((CommentsFragment) fragment).refresh();
		}
	}

	private void refreshClickedCommentsItem(Intent data) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		try {
			Fragment fragment = sFragmentManager
					.findFragmentByTag(STORY_FRAGMENT);
			if (fragment == null) {
				LogUtils.log(TAG, LogUtils.getThreadName()
						+ " story fragment == null");
				return;
			}
			((CommentsFragment) fragment).refresh(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCheckTab(int index, String url) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		if (FRAGMENT_TAGS[index].equals(mCurrentPage)) {
			return false;
		} else {
			radioGroupCheck(FRAGMENT_IDS[index]);
			return true;
		}

	}

	@Override
	protected void onStop() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onStop();
		mStatisticsEventManager.saveStatisticsData();
	}

	@Override
	protected void onStart() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		LogUtils.log(TAG, LogUtils.getThreadName()
				+ "  mNoNeedStatisticsExit = " + mNoNeedStatisticsExit);
		super.onDestroy();
		LocationUtils.stopLocation();
		mUpgrateDownloadManage.finish();
		unregisterReceiver(broadcastRec);// 取消注册
		UrlMatcher.destroy();
		GNActivityManager.getScreenManager().popActivity(this);
	}

	// public static class GradeHandler extends Handler {
	// private WeakReference<GnHomeActivity> mWeakReference;
	//
	// public GradeHandler(GnHomeActivity activity) {
	// mWeakReference = new WeakReference<GnHomeActivity>(activity);
	// }
	//
	// @Override
	// public void handleMessage(Message msg) {
	// final GnHomeActivity activity = mWeakReference.get();
	// if (activity == null) {
	// return;
	// }
	// switch (msg.what) {
	// case MESSAGE_CHECK_NOW:
	// try {
	// activity.mStatisticAction.sendStatisticData(activity,
	// StatisticsConstants.TYPE_UPMODE, StatisticsConstants.KEY_UPMODE_AUTO);
	// activity.mUpgradeManager.startCheck(true);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// break;
	// default:
	// break;
	//
	// }
	// }
	// }

	private void doConnection() {
		mCount = ShareDataManager.getDataAsInt(GnHomeActivity.this,
				ShareKeys.KEY_COUNT_STATE, 0) + 1;
		ShareDataManager.saveDataAsInt(GnHomeActivity.this,
				ShareKeys.KEY_COUNT_STATE, mCount);
		// if (mCount > StatisticsConstants.MIN_UPGRADE_START_CHECKCOUNT) {
		// // sHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_NOW, 3 * 1000);
		mStatisticAction.sendStatisticData(this,
				StatisticsConstants.TYPE_UPMODE,
				StatisticsConstants.KEY_UPMODE_AUTO);
		// activity.mUpgradeManager.startCheck(true);
		mUpgrateDownloadManage.checkUpgrate();
		// }
	}

	@Override
	public void onClick(View v) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		switch (v.getId()) {
		case R.id.tab_recommond:
			addFlowStatistics(StatisticsConstants.HomePageConstants.BOTTOM_TAB_1);
			break;
		case R.id.classify_search:
			setExitStatisticsFlag(true);
			break;
		case R.id.story:
			setExitStatisticsFlag(true);
			break;
		case R.id.more:
			setExitStatisticsFlag(true);
			break;
		case ACTIVITY_HOME_ID:
			clickActivityItem(ACTIVITY_HOME_ID);
			break;
		case ACTIVITY_CLASSIFY_ID:
			setExitStatisticsFlag(true);
			clickActivityItem(ACTIVITY_CLASSIFY_ID);
			break;
		case ACTIVITY_ID:
			setExitStatisticsFlag(true);
			clickActivityItem(ACTIVITY_ID);
			break;
		case ACTIVITY_STORY_ID:
			setExitStatisticsFlag(true);
			clickActivityItem(ACTIVITY_STORY_ID);
			break;
		case ACTIVITY_MORE_ID:
			setExitStatisticsFlag(true);
			clickActivityItem(ACTIVITY_MORE_ID);
			break;
		default:
			mMoreFragment.changeFragment(v.getId());
			break;
		}

	}

	private void clickActivityItem(int activityHomeId) {
		// TODO Auto-generated method stub
		switch (activityHomeId) {
		case R.id.tab_recommond:
		case ACTIVITY_HOME_ID:
			checkRecommond();
			changeTabRadioButtonStyle(mCurrentActivityId);
			break;
		case R.id.classify_search:
		case ACTIVITY_CLASSIFY_ID:
			checkClassfySearch();
			changeTabRadioButtonStyle(mCurrentActivityId);
			break;
		case R.id.more:
		case ACTIVITY_MORE_ID:
			checkMore();
			changeTabRadioButtonStyle(mCurrentActivityId);
			break;
		case R.id.story:
		case ACTIVITY_STORY_ID:
			checkStory();
			changeTabRadioButtonStyle(mCurrentActivityId);
			break;
		case ACTIVITY_ID:
			StatService.onEvent(this, HOME_TAB_SWITCH, "activity_click");
			RelativeLayout rlItem = (RelativeLayout) mTabBarRl.getChildAt(2);
			ImageView icon = (ImageView) rlItem
					.findViewById(R.id.tab_widget_icon);
			Bitmap bitmap = mTagDrawablePressedMap.get(2);
			setImageBitmapByDimen(icon, bitmap);
			gotoWebPage(mActivityLink, true);
			addFlowStatistics(StatisticsConstants.HomePageConstants.BOTTOM_TAB_5);
			rlItem.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					changeTabRadioButtonStyle(mCurrentActivityId);
				}
			}, 200);
			break;
		default:
			break;
		}
		showFragmentByIndex(mCurrentPage);
	}

	/*
	 * 创建TabWidgetView
	 */
	private RelativeLayout createTabView(final int position, String text,
			String imageResourceUrl, String rbBgColor, String textColor) {
		RelativeLayout itemRl = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.view_home_tab, null);
		final ImageView icon = (ImageView) itemRl
				.findViewById(R.id.tab_widget_icon);
		TextView content = (TextView) itemRl
				.findViewById(R.id.tab_widget_content);
		ImageView tabBg = (ImageView) itemRl.findViewById(R.id.tab_bg);
		loadImageAndSetImage(position, imageResourceUrl, icon);
		if (StringUtils.isEmpty(text)) {
			content.setVisibility(View.GONE);
			tabBg.setVisibility(View.VISIBLE);
			tabBg.setBackgroundColor(Color.parseColor(rbBgColor));
		} else {
			itemRl.setBackgroundColor(Color.parseColor(rbBgColor));
			tabBg.setVisibility(View.GONE);
			content.setText(text);
			content.setTextColor(Color.parseColor(textColor));
		}
		return itemRl;
	}

	private void loadImageAndSetImage(final int position,
			String imageResourceUrl, final ImageView icon) {
		GNImageLoader.getInstance().loadBitmap(imageResourceUrl,
				new ImageView(this), new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						if (mCurrentActivityId == position
								&& mTagDrawablePressedMap.get(position) != null
								&& mTabBarRl.getVisibility() == View.VISIBLE) {
							setImageBitmapByDimen(icon,
									mTagDrawablePressedMap.get(position));
							view.postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									final RelativeLayout rlItem = (RelativeLayout) mTabBarRl
											.getChildAt(position);
									if (rlItem != null) {
										TextView content = (TextView) rlItem
												.findViewById(R.id.tab_widget_content);
										content.setTextColor(Color
												.parseColor(mTagTextColorPressed));
									}
								}
							}, 20);
						} else {
							setImageBitmapByDimen(icon, loadedImage);
						}
						mTagDrawableDefalutMap.put(position, loadedImage);
						isLoadCompleteAndShowActivityTab();
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub
					}
				});
	}

	private void isLoadCompleteAndShowActivityTab() {
		// Log.i("ssssss", mTagDrawableDefalutMap.size() + "    " +
		// mTagDrawablePressedMap.size());
		if (activityTabHasLoadcompleteAndShow()) {
			mHandler.removeMessages(0);
			// 显示活动导航tab
			mTabBarRl.setVisibility(View.VISIBLE);
			android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mLowPriceNotice
					.getLayoutParams();
			params.rightMargin = AndroidUtils.dip2px(this, 17);
			mLowPriceNotice.setLayoutParams(params);
			StatService.onEvent(this, HOME_TAB_SWITCH, "activity_show");
			Animation mAnimation = AnimationUtils.loadAnimation(this,
					R.anim.home_activity_tab_alpha_in);
			mTabBarRl.setAnimation(mAnimation);
			radioGroupCheck(mCurrentActivityId);
			// mTabBarRadio.setVisibility(View.GONE);
		}
	}

	private boolean activityTabHasLoadcompleteAndShow() {
		return mTagDrawableDefalutMap.size() == 5
				&& mTagDrawablePressedMap.size() == 5
				&& mTabBarRl.getVisibility() == View.GONE && mIsHasActivity;
	}

	private boolean isPushBoot() {
		if (Constants.BootSource.PUSH_STYLE_BAIDU.equals(mIntentSource)
				|| Constants.BootSource.PUSH_STYLE_LOCAL.equals(mIntentSource)) {
			return true;
		}
		return false;
	}

	private boolean isPushBootOnBaiduStyle() {
		if (Constants.BootSource.PUSH_STYLE_BAIDU.equals(mIntentSource)) {
			return true;
		}
		return false;
	}

	private void submitStatisticsData() {
		if (!mNoNeedStatisticsExit) {
			if (isPushBoot()) {
				if (isPushBootOnBaiduStyle() && mIsGotoStoryDetail) {
					addFlowStatistics(StatisticsConstants.HomePageConstants.EXIT_HOME_PAGE_PUSH_COMMENT_DETAIL);
				} else if (isPushBootOnBaiduStyle() && mIsGotoStoryList) {
					addFlowStatistics(StatisticsConstants.HomePageConstants.EXIT_HOME_PAGE_PUSH_COMMENT_LIST);
				} else {
					addFlowStatistics(StatisticsConstants.HomePageConstants.EXIT_HOME_PAGE_PUSH);
				}
			} else {
				addFlowStatistics(StatisticsConstants.HomePageConstants.EXIT_HOME_PAGE);
			}
		}
		mStatisticsEventManager.saveStatisticsData();
		// mRequestAction.submitStatisticsData(this, null, this,
		// mStatisticsEventManager.buildStatisticsData(), mIntentSource);
	}

	public String getIntentSource() {
		return mIntentSource;
	}

	private void initSdcardListener() {
		// 在IntentFilter中选择你要监听的行为
		IntentFilter filter4SdCard = new IntentFilter(Intent.ACTION_MEDIA_EJECT);
		filter4SdCard.addDataScheme("file");
		registerReceiver(broadcastRec, filter4SdCard);
		Log.i(TAG, "sd状态改变");
	}

	private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
				// SD卡已经成功挂载
				mUpgrateDownloadManage.closeAllDialog();
				Download.getInstance(GnHomeActivity.this).exit();
				Toast.makeText(GnHomeActivity.this, R.string.no_sdcard, 1)
						.show();
			} else {
			}
		}
	};

}
// Gionee <yuwei> <2013-1-10> modify for CR00832427 end
