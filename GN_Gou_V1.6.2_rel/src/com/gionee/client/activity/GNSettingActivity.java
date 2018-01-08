// Gionee <yuwei><2014-10-14> add for CR00821559 begin
/*
 * GNSettingActivity.java
 * classes : com.gionee.client.activity.GNSettingActivity
 * @author yuwei
 * V 1.0.0
 * Create at 2014-10-14 上午10:33:03
 */
package com.gionee.client.activity;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.sina.WeiboAuthActivity;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.action.StatisticAction;
import com.gionee.client.business.manage.ConfigManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.push.BaiduPushUtils;
import com.gionee.client.business.shareTool.ShareTool;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.upgradeplus.IUpdateListener;
import com.gionee.client.business.upgradeplus.common.UpgradeUtils;
import com.gionee.client.business.urlMatcher.UrlMatcher;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.WebViewUtills;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.service.PromotionalSaleService;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.widget.CustomSwitch;
import com.gionee.client.view.widget.CustomSwitch.GameSwitchListener;
import com.gionee.framework.operation.business.LocalBuisiness;
import com.gionee.framework.operation.net.GNImageLoader;
import com.upgrate.manage.Download;
import com.upgrate.manage.UpgrateDownloadManage;

/**
 * com.gionee.client.activity.GNSettingActivity
 * 
 * @author yuwei <br/>
 * @date create at 2014-10-14 上午10:33:03
 * @description TODO
 */
public class GNSettingActivity extends BaseFragmentActivity implements
		OnClickListener {
	private static final String TAG = "GNSettingActivity";
	private static final String SETTINGS = "settings";
	private TextView mCleanSuccessTv;
	private ProgressBar mCleaningPro;
	private CustomSwitch mCustomSwitch;
	private CustomSwitch mWifiHelpSwitch;
	private TextView mHelpSwitchNotify;
	private TextView mCheckVersionTv;
	private ImageView mVersionTriangle;
	private ProgressBar mCheckVersionPro;
	private ImageView mNewVersionSign;
	private TextView mVersionName;
	private TextView mVersionCode;
	private TextView mNewWeiboNotice;
	private ImageView mNewSinaSign;
	private RelativeLayout mCheckVersionLayout;
	private UpgrateDownloadManage mUpgrateDownloadManage;
	public static boolean sIsClickSet = false;

	@Override
	public void onResume() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onResume();
		updateVersionInfo();
		closeProgressDialog();
		sIsClickSet = true;
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	public void updateVersionInfo() {
		try {
			if (UpgrateDownloadManage.sIsHasNewVersion) {
				JSONObject data = ConfigManager.getInstance().getUpgrateData(
						this);
				if (data == null) {
					return;
				}
				mNewVersionSign.setVisibility(View.VISIBLE);
				mVersionName.setVisibility(View.VISIBLE);
				String versionTip = getString(R.string.newest)
						+ data.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
				if (mUpgrateDownloadManage
						.fileIsExitAndComplete(mUpgrateDownloadManage
								.getFilePath(data))) {
					versionTip = getString(R.string.install_tip);
				}
				mVersionName.setText(versionTip);
				hideCheckProgress();
			} else {
				mNewVersionSign.setVisibility(View.GONE);
				mVersionName.setVisibility(View.GONE);
			}
			mCheckVersionLayout.setEnabled(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onErrorResult(String businessType, String errorOn,
			String errorInfo, Object session) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		if (businessType.equals(Url.CUMULATE_SCORE_URL)) {
			return;
		}
		super.onErrorResult(businessType, errorOn, errorInfo, session);
	}

	private void showCheckProgress() {
		mCheckVersionTv.setText(R.string.check_version_ing);
		mVersionTriangle.setVisibility(View.GONE);
		mCheckVersionPro.setVisibility(View.VISIBLE);
		mNewVersionSign.setVisibility(View.GONE);
		mVersionName.setVisibility(View.GONE);
	}

	private void showIsNewVersion() {
		mVersionName.setVisibility(View.VISIBLE);
		mVersionName.setText(R.string.is_newest_version);
		mVersionName.invalidate();
		mVersionTriangle.setVisibility(View.GONE);
		mCheckVersionLayout.setEnabled(false);
		mVersionName.postDelayed(new Runnable() {

			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				mVersionName.setVisibility(View.GONE);
				mVersionTriangle.setVisibility(View.VISIBLE);
				mCheckVersionLayout.setEnabled(true);
			}
		}, 3000);
	}

	private void hideCheckProgress() {
		mCheckVersionTv.setText(R.string.menu_check_version);
		mVersionTriangle.setVisibility(View.VISIBLE);
		mCheckVersionPro.setVisibility(View.GONE);
		mVersionName.setVisibility(View.VISIBLE);
		JSONObject data = ConfigManager.getInstance().getUpgrateData(this);
		if (data == null) {
			return;
		}
		String versionTip = getString(R.string.newest)
				+ data.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
		if (mUpgrateDownloadManage.fileIsExitAndComplete(mUpgrateDownloadManage
				.getFilePath(data))) {
			versionTip = getString(R.string.install_tip);
		}
		mVersionName.setText(versionTip);
		mVersionName.invalidate();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		GNImageLoader.getInstance().init(this);
		initView();
		// initQihuAppRecommond();
		initSettingData();
		// mUpgradeManager = new UpgradeManager(this,
		// UpgradeUtils.getProductKey(this));
		// UpgradeManager.setmUpdateStateListener(this);
		if (AndroidUtils.translateTopBar(this)) {
			((GNTitleBar) findViewById(R.id.top_title)).setTopPadding();
		}
		ShareSDK.initSDK(this);
		mUpgrateDownloadManage = new UpgrateDownloadManage(this, false);
		mUpgrateDownloadManage.setHandler();
		initSdcardListener();
	}

	private void initSettingData() {
		String weiboNotice = UrlMatcher.getInstance().getWeiBoNotice();
		if (!Constants.NULL.equals(weiboNotice)) {
			mNewWeiboNotice.setText(weiboNotice);
		}
		if (UrlMatcher.getInstance().isHasNewWeiboNotice(
				ShareDataManager.getString(this, Constants.WEIBO_NOTICE, null))) {
			mNewWeiboNotice.setVisibility(View.VISIBLE);
			mNewSinaSign.setVisibility(View.VISIBLE);
		}
		setVersionName();
	}

	private void setVersionName() {
		try {
			String versionCode = this.getResources().getString(
					R.string.version_code);
			String versionName = AndroidUtils.getAppVersionName(this);
			versionName = versionName
					.substring(0, versionName.lastIndexOf(".")).trim();
			mVersionCode.setText(String.format(versionCode, versionName));
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}

	private void showCleanProgress() {
		mCleanSuccessTv.setVisibility(View.GONE);
		mCleaningPro.setVisibility(View.VISIBLE);

	}

	private void showCleanSuccess() {
		mCleanSuccessTv.setVisibility(View.VISIBLE);
		mCleaningPro.setVisibility(View.GONE);

	}

	private void hideCleanNotify() {
		mCleanSuccessTv.setVisibility(View.GONE);
		mCleaningPro.setVisibility(View.GONE);

	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void initView() {

		mCleaningPro = (ProgressBar) findViewById(R.id.clear_chache_pro);
		mCleanSuccessTv = (TextView) findViewById(R.id.clear_cache_tv);
		mCustomSwitch = (CustomSwitch) findViewById(R.id.shopping_help_switch);
		mWifiHelpSwitch = (CustomSwitch) findViewById(R.id.wifi_help_switch);
		mHelpSwitchNotify = (TextView) findViewById(R.id.help_switch_notify);
		mCustomSwitch.setCheckedStatus(BaiduPushUtils.getPushSwich(this));
		mWifiHelpSwitch.setCheckedStatus(BaiduPushUtils.getWifiSwich(this));
		setNotifyText(BaiduPushUtils.getPushSwich(this));
		mCheckVersionTv = (TextView) findViewById(R.id.menu_check_version_tv);
		mVersionTriangle = (ImageView) findViewById(R.id.more_version_triangle);
		mCheckVersionPro = (ProgressBar) findViewById(R.id.check_version_pro);
		mNewVersionSign = (ImageView) findViewById(R.id.new_version_sign);
		mVersionName = (TextView) findViewById(R.id.version_name_tv);
		mVersionCode = (TextView) findViewById(R.id.version_code);
		mNewWeiboNotice = (TextView) findViewById(R.id.menu_weibo_notice);
		mNewSinaSign = (ImageView) findViewById(R.id.new_sina_sign);
		mCheckVersionLayout = (RelativeLayout) findViewById(R.id.menu_check_version);
		mCustomSwitch.setGameSwitchListener(new GameSwitchListener() {

			@Override
			public void onThumb(boolean isInThumb) {
				LogUtils.log(TAG, LogUtils.getThreadName());

			}

			@Override
			public void onChange(boolean check) {
				LogUtils.log(TAG, LogUtils.getThreadName());
				setPush(check);
				BaiduPushUtils.setPushSwich(GNSettingActivity.this, check);
				setNotifyText(check);
				sendBroadcast(BaiduPushUtils
						.getPushSwich(GNSettingActivity.this));
			}

		});
		mWifiHelpSwitch.setGameSwitchListener(new GameSwitchListener() {

			@Override
			public void onThumb(boolean isInThumb) {
				LogUtils.log(TAG, LogUtils.getThreadName());

			}

			@Override
			public void onChange(boolean check) {
				LogUtils.log(TAG, LogUtils.getThreadName());
				BaiduPushUtils.setWifiSwich(GNSettingActivity.this, check);
				StatService.onEvent(GNSettingActivity.this,
						"upgrade_autodownload", "upgrade_autodownload");
			}

		});

	}

	private void setPush(boolean check) {
		if (check) {
			PushManager.startWork(getApplicationContext(),
					PushConstants.LOGIN_TYPE_API_KEY,
					BaiduPushUtils.getMetaValue(this, "api_key"));
			StatService.onEvent(this, BaiduStatConstants.PUSH, "true");
		} else {
			PushManager.stopWork(getApplicationContext());
			StatService.onEvent(this, BaiduStatConstants.PUSH, "false");
		}
	}

	/**
	 * @param check
	 * @author yuwei
	 * @description TODO
	 */
	private void setNotifyText(boolean check) {
		if (check) {
			mHelpSwitchNotify.setText(R.string.push_help_off);
		} else {
			mHelpSwitchNotify.setText(R.string.push_help_open);
		}
	}

	private void sendBroadcast(boolean check) {
		Intent intent = new Intent(PromotionalSaleService.RESTART_SALE_SERVICE);
		intent.putExtra("check", check);
		sendBroadcast(intent);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	// private void initQihuAppRecommond() {
	// // 积分墙模式
	// QihooAdAgent.setADWallMode(2);
	// }

	public void changeFragment(int menuId) {
		LogUtils.log(TAG, LogUtils.getThreadName());

		switch (menuId) {

		case R.id.menu_check_version:
			checkUpgrade();
			// mCheckVersionLayout.setEnabled(false);
			// mCheckVersionLayout.postDelayed(new Runnable() {
			//
			// @Override
			// public void run() {
			// LogUtils.log(TAG, LogUtils.getThreadName());
			// mCheckVersionLayout.setEnabled(true);
			// }
			// }, 6000);
			break;
		case R.id.menu_weibo_auth:
			gotoWeiBo();
			break;
		case R.id.menu_like:
			gotoMarket();
			break;
		case R.id.menu_wx_friends:
			shareToWeixin(false);
			break;

		case R.id.menu_about_shoppingmall:

			gotoWebPage(Url.ABOUT_PAGE_URL, false);
			StatService.onEvent(this, SETTINGS, "About_GOU");
			break;

		case R.id.clear_cache_layout:
			showCleanCacheDialog();
			StatService.onEvent(this, SETTINGS, "clear");
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void showCleanCacheDialog() {
		DialogFactory.createMsgDialog(this, new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogUtils.log(TAG, LogUtils.getThreadName());
				showCleanProgress();
				syncCleanCache();
				postCleanSuccess();
				postHideNotify();
			}

		}, R.string.certain_clean_cache).show();
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	public void postHideNotify() {
		mCleaningPro.postDelayed(new Runnable() {

			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				hideCleanNotify();
			}
		}, 2000);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	public void postCleanSuccess() {
		mCleaningPro.postDelayed(new Runnable() {

			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				showCleanSuccess();
			}
		}, 1000);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	public void syncCleanCache() {
		LocalBuisiness.getInstance().postRunable(new Runnable() {

			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				WebViewUtills.cleanCache(GNSettingActivity.this);
			}
		});
	}

	public void checkUpgrade() {
		if (mUpgrateDownloadManage.mIsCanRequest) {
			StatService.onEvent(this, SETTINGS, "update");
			new StatisticAction().sendStatisticData(this,
					Constants.Statistic.TYPE_UPMODE,
					Constants.Statistic.KEY_UPMODE_MANUAL);
			if (checkNetworkNotVisiviblle()) {
				showNetErrorToast(findViewById(R.id.top_title));
				return;
			}
			// mUpgradeManager.startCheck(false);
			mUpgrateDownloadManage.checkUpgrate();
		}
	}

	private boolean checkNetworkNotVisiviblle() {
		return AndroidUtils.getNetworkType(this) == Constants.NET_UNABLE;
	}

	private void shareToWeixin(boolean b) {
		shareToWeixin(false, getString(R.string.app_name),
				getString(R.string.share_weixin_description),
				Constants.DEFAULT_SHARE_ICON_URL, getAppWeiXinShareURL());
		StatService.onEvent(this, SETTINGS, "share_app_weixin");
		if (ShareTool.isWXInstalled(this)) {
			cumulateShareAppScore();
		}
	}

	private void cumulateShareAppScore() {
		RequestAction action = new RequestAction();
		action.cumulateScore(this, Constants.ScoreTypeId.SHARE_APP);
	}

	private void cumulateAttentionWeiboScore() {
		RequestAction action = new RequestAction();
		action.cumulateScore(this, Constants.ScoreTypeId.ATTENTION_WEIBO);
	}

	private void gotoMarket() {
		Intent intent = new Intent();
		try {
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(Constants.MARKRT_URL));
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			gotoWebPage(Constants.MARKET_WEB_URL, true);
		}
		StatService.onEvent(this, SETTINGS, "like_app");
	}

	private void gotoWeiBo() {
		hideWeiboNotice();
		Intent intent = new Intent();
		intent.setClass(this, WeiboAuthActivity.class);
		startActivity(intent);
		AndroidUtils.enterActvityAnim(this);
		StatService.onEvent(this, SETTINGS, "follow_app_sina");
		cumulateAttentionWeiboScore();
	}

	private void hideWeiboNotice() {
		if (mNewSinaSign.isShown()) {
			mNewSinaSign.setVisibility(View.GONE);
			ShareDataManager.putString(this, Constants.WEIBO_NOTICE,
					mNewWeiboNotice.getText().toString());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		changeFragment(v.getId());

	}

	public void onChecking() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		showCheckProgress();
		mCheckVersionLayout.setEnabled(true);

	}

	public void onNewestVersion() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		hideCheckProgress();
		showIsNewVersion();
		mCheckVersionLayout.setEnabled(true);
	}

	public void onCheckComplete() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		hideCheckProgress();
		mCheckVersionLayout.setEnabled(true);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mUpgrateDownloadManage != null) {
			mUpgrateDownloadManage.finish();
		}
		sIsClickSet = false;
		unregisterReceiver(broadcastRec);// 取消注册
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
				Download.getInstance(GNSettingActivity.this).exit();
				mUpgrateDownloadManage.closeAllDialog();
				Toast.makeText(GNSettingActivity.this, R.string.no_sdcard, 1)
						.show();
			} else {
			}
		}
	};

}
