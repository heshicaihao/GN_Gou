package com.upgrate.manage;

import java.io.File;
import org.json.JSONObject;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GNSettingActivity;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.tabFragment.MoreFragment;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.appDownload.InstallUtills;
import com.gionee.client.business.manage.ConfigManager;
import com.gionee.client.business.push.BaiduPushUtils;
import com.gionee.client.business.upgradeplus.UpgradeDialogFactory;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.widget.GNUpgrateDialog;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.page.PageCacheManager;

public class UpgrateDownloadManage implements IBusinessHandle, OnClickListener {
	private RequestAction mRequestAction = new RequestAction();
	private GNUpgrateDialog mCheckUpgrateDialog;
	private GNUpgrateDialog mUpgrateProgressDialog;
	private Activity mActivity;
	private Boolean mIsAuto;// 是否是自动请求，不是手动点击更新
	private boolean mIsCanDownloadByUnWifi = false;// 是否可以在运行商网络下下载
	public static boolean sIsHasNewVersion = false;
	public boolean mIsCanRequest = true;// 是否可以继续请求,避免重复点击请求升级接口
	private JSONObject mUpgrateData;
	private int mLastNetType = 0;// 0表示wifi网络，1表示运营商网络,2表示无网络
	private boolean mInitIsUnWifiNet = true;// 初始是否是运营商网络
	public static final int DOWNLOAD_SIZE = 1;
	public static final int NETERROR_MSG = -1;
	public static final int FILEERROR_MSG = 2;
	public static final int NETERROR_TIME_TIP_MSG = 0;
	private int mRemainTime = 5;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_SIZE:
				int size = msg.getData().getInt("size");
				if (mUpgrateProgressDialog != null) {
					try {
						handProgressView(size);
					} catch (Exception e) {
						// TODO: handle exception
					}
				} else {
					if (size == getmLength()) {
						freshDownloadComplete();
					}
				}
				break;

			case NETERROR_MSG:
				if (mUpgrateProgressDialog != null
						&& mUpgrateProgressDialog.isShowing()) {
					if (UpgrateDataManage.getSdcardIsHasFreeBlock()) {
						if (netErrorTipIsShow()) {
							handDownloadNetError();
						}
					} else {
						Toast.makeText(mActivity,
								R.string.sdcard_no_free_block, 1).show();
						closeAllDialog();
						Download.getInstance(mActivity).setIsExit(true);
					}
				}
				break;
			case NETERROR_TIME_TIP_MSG:
				mRemainTime--;
				if (mUpgrateProgressDialog != null) {
					View view = mUpgrateProgressDialog.getContentView();
					TextView tvSecondRecord = (TextView) view
							.findViewById(R.id.tv_second_record);
					if (mRemainTime > 0) {
						tvSecondRecord.setText(mActivity.getString(
								R.string.second_record_end, mRemainTime));
						mHandler.sendEmptyMessageDelayed(NETERROR_TIME_TIP_MSG,
								1500);
					} else {
						mUpgrateProgressDialog.dismiss();
						mUpgrateProgressDialog = null;
						initHandler();
					}
				}
				break;
			case FILEERROR_MSG:
				Toast.makeText(mActivity,
						mActivity.getString(R.string.no_apk_error),
						Toast.LENGTH_LONG).show();
				closeAllDialog();
				break;
			}

		}

	};

	/**
	 * 网络异常倒计时是否正在进行
	 * 
	 * @return
	 */
	private boolean netErrorTipIsShow() {
		return mUpgrateProgressDialog != null
				&& mUpgrateProgressDialog.isShowing()
				&& mUpgrateProgressDialog.getContentView()
						.findViewById(R.id.ll_net_error_tip).getVisibility() == View.GONE;
	}

	private void initHandler() {
		mHandler.removeMessages(NETERROR_TIME_TIP_MSG);
		mRemainTime = 5;
		if (mUpgrateProgressDialog != null) {
			mUpgrateProgressDialog.getContentView()
					.findViewById(R.id.ll_net_error_tip)
					.setVisibility(View.GONE);
		}
	}

	private void handDownloadNetError() {
		// TODO Auto-generated method stub
		if (mUpgrateProgressDialog != null) {
			View view = mUpgrateProgressDialog.getContentView();
			LinearLayout llNetErrorTip = (LinearLayout) view
					.findViewById(R.id.ll_net_error_tip);
			llNetErrorTip.setVisibility(View.VISIBLE);
			LinearLayout rlButtom = (LinearLayout) view
					.findViewById(R.id.rl_buttom);
			rlButtom.setVisibility(View.GONE);
			mHandler.sendEmptyMessageDelayed(NETERROR_TIME_TIP_MSG, 1500);
		}
	}

	private void freshDownloadComplete() {
		Download.getInstance(mActivity).setIsExit(true);
		setIsDownloadComplete(true);
		Download.getInstance(mActivity).mIsHasStarDownload = false;
		if (mActivity instanceof GNSettingActivity) {
			((GNSettingActivity) mActivity).updateVersionInfo();
		}
	}

	private void handProgressView(int size) {
		View view = mUpgrateProgressDialog.getContentView();
		ProgressBar downloadbar = (ProgressBar) view
				.findViewById(R.id.progress_bar);
		downloadbar.setMax(getmLength());
		downloadbar.setProgress(size);
		float result = (float) downloadbar.getProgress()
				/ (float) downloadbar.getMax();
		int p = (int) (result * 100);
		TextView progressPercent = (TextView) view
				.findViewById(R.id.progress_percent);
		TextView progressNumber = (TextView) view
				.findViewById(R.id.progress_number);
		progressPercent.setText(p + "%");
		progressNumber
				.setText(UpgrateDataManage.kToM(downloadbar.getProgress())
						+ "/" + UpgrateDataManage.kToM(getmLength()));
		if (downloadbar.getProgress() == downloadbar.getMax()) {
			// Toast.makeText(mActivity, R.string.success,
			// 1).show();
			freshDownloadComplete();
			boolean upgrateProgressIsShow = false;
			if (mUpgrateProgressDialog != null) {
				upgrateProgressIsShow = mUpgrateProgressDialog.isShowing();
			}
			createCheckDialog(true, upgrateProgressIsShow);
		}
	}

	// 检查用户是否切换CMWAP网络
	private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Log.d("mark", "网络状态已经改变");
				ConnectivityManager connectivityManager = (ConnectivityManager) mActivity
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					String name = info.getTypeName();
					if (isChangeToUnWifiAndDownload(name)) {
						// 不在wifi环境下
						mLastNetType = 1;
						Download.getInstance(mActivity).exit();
						if (Download.getInstance(mActivity).mSize == 0) {
							createCheckDialog(false, true);
						} else {
							createProgressDialog(Download
									.getInstance(mActivity).getExit());
						}
						initHandler();
					} else if (isChangeToWifiAndDownload(name)
							&& mLastNetType != 0) {
						mLastNetType = 0;
						mInitIsUnWifiNet = false;
						// createCheckDialog(false, false);
						if (netErrorTipIsShow()) {
							mUpgrateProgressDialog.getContentView()
									.findViewById(R.id.ll_net_error_tip)
									.setVisibility(View.GONE);
							Download.getInstance(mActivity).downloadUrl(
									mUpgrateData);
						}
						initHandler();
					}
					Log.d("mark", "当前网络名称：" + name);
				} else {
					Log.d("mark", "没有可用网络");
					mLastNetType = 2;
					mInitIsUnWifiNet = false;
					Download.getInstance(mActivity).exit();
					if (mCheckUpgrateDialog != null) {
						mCheckUpgrateDialog.dismiss();
						mCheckUpgrateDialog = null;
					}
					if (netErrorTipIsShow()) {
						handDownloadNetError();
					}
				}
			}
		}

	};

	/**
	 * 切换到运营商网络
	 * 
	 * @param name
	 * @return
	 */
	private boolean isChangeToUnWifiAndDownload(String name) {
		return !name.equals("WIFI")
				&& !Download.getInstance(mActivity).isDownloadComplete()
				&& !mIsCanDownloadByUnWifi && mLastNetType != 1
				&& !mInitIsUnWifiNet;
	}

	/**
	 * 切换到无线网络
	 * 
	 * @param name
	 * @return
	 */
	private boolean isChangeToWifiAndDownload(String name) {
		if (mUpgrateData == null) {
			return false;
		}
		String versionName = mUpgrateData
				.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
		return name.equals("WIFI")
				&& !UpgrateDataManage.isFileDownloadComplete(mActivity,
						versionName);
	}

	public UpgrateDownloadManage(Activity activity, boolean isAuto) {
		super();
		this.mActivity = activity;
		this.mIsAuto = isAuto;
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		activity.registerReceiver(mNetworkReceiver, filter);
	}

	public void setHandler() {
		Download.getInstance(mActivity).setHandler(mHandler);
	}

	/**
	 * 是否正在下载
	 * 
	 * @return
	 */
	private boolean isDownloading() {
		// Log.i("kkkkkk", !Download.getInstance(mActivity).getExit() + "  "
		// + !Download.getInstance(mActivity).isDownloadComplete());
		if (!Download.getInstance(mActivity).getExit()
				&& !Download.getInstance(mActivity).isDownloadComplete()) {
			return true;
		}
		return false;
	}

	/**
	 * 文件是否已经下载并完成
	 * 
	 * @param filePath
	 * @return
	 */
	public boolean fileIsExitAndComplete(String filePath) {
		mUpgrateData = ConfigManager.getInstance().getUpgrateData(mActivity);
		if (mUpgrateData == null) {
			return false;
		}
		String versionName = mUpgrateData
				.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
		File file = new File(filePath);
		if (UpgrateDataManage.isFileDownloadComplete(mActivity, versionName)
				&& file != null && file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 创建下载对话框
	 * 
	 * @param isShowPause
	 */
	private void createProgressDialog(boolean isShowPause) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(mActivity, R.string.no_sdcard, 1).show();
			return;
		}
		if (mActivity instanceof GnHomeActivity
				&& GNSettingActivity.sIsClickSet) {
			return;
		}
		mUpgrateData = ConfigManager.getInstance().getUpgrateData(mActivity);
		if (mUpgrateData == null) {
			return;
		}
		closeAllDialog();
		mUpgrateProgressDialog = UpgradeDialogFactory
				.createUpgrateProgressDialog(mActivity, mUpgrateData, this);
		View view = mUpgrateProgressDialog.getContentView();
		LinearLayout buttomView = (LinearLayout) view
				.findViewById(R.id.rl_buttom);
		if (isShowPause) {
			buttomView.setVisibility(View.VISIBLE);
		} else {
			buttomView.setVisibility(View.GONE);
		}
		if (mActivity instanceof GnHomeActivity
				&& GNSettingActivity.sIsClickSet && !isShowPause) {
			return;
		}
		mUpgrateProgressDialog.show();
	}

	public void closeAllDialog() {
		if (mUpgrateProgressDialog != null) {
			mUpgrateProgressDialog.dismiss();
			mUpgrateProgressDialog = null;
		}
		if (mCheckUpgrateDialog != null) {
			mCheckUpgrateDialog.dismiss();
			mCheckUpgrateDialog = null;
		}
	}

	/**
	 * 创建检测更新对话框
	 * 
	 * @param fileIsExitAndComplete
	 */
	private void createCheckDialog(boolean fileIsExitAndComplete,
			boolean isShowCheckDialog) {
		mUpgrateData = ConfigManager.getInstance().getUpgrateData(mActivity);
		if (mUpgrateData == null) {
			return;
		}
		closeAllDialog();
		mCheckUpgrateDialog = UpgradeDialogFactory.createHasNewVersionDialog(
				mActivity, mUpgrateData, fileIsExitAndComplete, this);
		if (mActivity instanceof GnHomeActivity
				&& GNSettingActivity.sIsClickSet) {
			return;
		}
		if (isShowCheckDialog) {
			if (fileIsExitAndComplete) {
				StatService.onEvent(mActivity, "upgrade_install_popups",
						"upgrade_install_popups");
				String versionName = mUpgrateData
						.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
				if (mIsAuto) {
					UpgrateDataManage
							.saveNoInstallCount(mActivity, versionName);
				}
				UpgrateDataManage.saveIsFirstShowInstallTip(mActivity, versionName,false);
			} else {
				StatService.onEvent(mActivity, "upgrade_popups",
						"upgrade_popups");
			}
			mCheckUpgrateDialog.show();
		}
	}

	public String getFilePath(JSONObject upgrateData) {
		if (upgrateData == null) {
			upgrateData = ConfigManager.getInstance().getUpgrateData(mActivity);
		}
		if (upgrateData == null) {
			return "";
		}
		String mFilePath = "";
		try {
			String versionName = upgrateData
					.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
			File dir = new File(Environment.getExternalStorageDirectory()
					.getAbsoluteFile().getPath()
					+ Download.FILEFOLDER);// 文件保存目录
			String filename = Download.FILENAMEPREFIX + versionName + ".apk";
			mFilePath = dir.getAbsolutePath() + "/" + filename;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return mFilePath;
	}

	public void checkUpgrate() {
		if (isDownloading()) {
			if (mUpgrateProgressDialog != null) {
				mUpgrateProgressDialog.show();
			} else {
				createProgressDialog(false);
			}
		} else {
			if (mCheckUpgrateDialog == null) {
				requestUpgrate();
			} else {
				View checkUpgrateView = mCheckUpgrateDialog.getContentView();
				Button btRight = (Button) checkUpgrateView
						.findViewById(R.id.bt_right);
				Button btLeft = (Button) checkUpgrateView
						.findViewById(R.id.bt_left);
				if (btRight.getText().toString()
						.equals(mActivity.getString(R.string.install_at_once))) {
					if (!fileIsExitAndComplete(getFilePath(mUpgrateData))) {
						btLeft.setText(mActivity
								.getString(R.string.upgrade_donot_download));
						btRight.setText(mActivity
								.getString(R.string.upgrade_download_now));
						btRight.setBackgroundResource(R.drawable.transparent);
						btRight.setTextColor(mActivity.getResources().getColor(
								R.color.menu_text));
						Download.getInstance(mActivity).setIsExit(true);
						Download.getInstance(mActivity).setDownloadComplete(
								false);
					}
				}
				mCheckUpgrateDialog.show();
			}
		}
	}

	private void requestUpgrate() {
		mIsCanRequest = false;
		if (mActivity instanceof GNSettingActivity) {
			((GNSettingActivity) mActivity).onChecking();
		}
		// Log.i("kkkkkk", "requestUpgrate");
		mRequestAction.getUpgrate(this, HttpConstants.Data.Upgrate.UPGRATE_JO,
				mActivity);
	}

	@Override
	public void onSucceed(String businessType, boolean isCache, Object session) {
		// TODO Auto-generated method stub
		if (businessType.equals(Url.UPGRATE)) {
			mIsCanRequest = true;
			MyBean pageData = PageCacheManager.LookupPageData(mActivity
					.getClass().getName());
			if (pageData == null) {
				return;
			}
			mUpgrateData = pageData
					.getJSONObject(HttpConstants.Data.Upgrate.UPGRATE_JO);
			if (mUpgrateData == null) {
				// 表示没有更新
				if (mActivity instanceof GNSettingActivity) {
					((GNSettingActivity) mActivity).onNewestVersion();
				}else{
					StatService.onEvent(mActivity, "upgrade_needed",
							"upgrade_no_needed");
				}
				clearDir();
				return;
			}
			ConfigManager.getInstance()
					.saveUpgrateData(mActivity, mUpgrateData);
			if (mActivity instanceof GNSettingActivity) {
				((GNSettingActivity) mActivity).onCheckComplete();
			}
			sIsHasNewVersion = true;
			if (mActivity instanceof GnHomeActivity) {
				((GnHomeActivity) mActivity).hasVersionNotify();
				StatService.onEvent(mActivity, "upgrade_needed",
						"upgrade_needed");
			}
			initShowDialog(mUpgrateData);
		}
	}

	/**
	 * 清除下载目录里的下载文件
	 */
	private void clearDir() {
		try {
			File dir = new File(Environment.getExternalStorageDirectory()
					.getAbsoluteFile().getPath()
					+ Download.FILEFOLDER);
			File[] files = dir.listFiles();
			// 删除非正需要下载的其他apk文件
			for (File file : files) {
				file.delete();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void initShowDialog(JSONObject upgrateData) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (mActivity instanceof GNSettingActivity) {
				Toast.makeText(mActivity, R.string.no_sdcard, 1).show();
			}
			return;
		}
		String versionName = upgrateData
				.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
		String mFilePath = getFilePath(upgrateData);
		boolean fileIsExitAndComplete = fileIsExitAndComplete(mFilePath);
		boolean isShow = UpgrateDataManage.getDownLoadingIsTip(mActivity,
				AndroidUtils.getAppVersionName(mActivity));
		// Log.i("kkkkkk", "fileIsExitAndComplete:" + fileIsExitAndComplete);
		if (fileIsExitAndComplete) {
			isShow = UpgrateDataManage.getInstallIsTip(mActivity,
					AndroidUtils.getAppVersionName(mActivity));
		} else {
			UpgrateDataManage.saveFileDownloadComplete(mActivity, versionName,
					false);
		}
		if (mIsAuto && BaiduPushUtils.getWifiSwich(mActivity)
				&& !fileIsExitAndComplete
				&& initByWifi(upgrateData, fileIsExitAndComplete)) {
			// 无线网环境静默下载
			return;
		}
		if (!isShow && mIsAuto) {
			return;
		}
		int cancelCount = UpgrateDataManage.getNoInstallCount(mActivity,
				versionName);
		if (isShow && fileIsExitAndComplete && cancelCount > 1 && mIsAuto) {
			// 安装界面取消了3次以上
			return;
		}
		if (UpgrateDataManage.isStartDownload(mActivity, versionName)) {
			if (NetManage.isWiFiActive(mActivity)) {
				createProgressDialog(false);
				StatService.onEvent(mActivity, "upgrade_started", "wlan");
				Download.getInstance(mActivity).downloadUrl(upgrateData);
			} else {
				createProgressDialog(true);
			}
			return;
		}
		createCheckDialog(fileIsExitAndComplete, true);
	}

	private boolean initByWifi(JSONObject upgrateData,
			boolean fileIsExitAndComplete) {
		// TODO Auto-generated method stub
		if (NetManage.isWiFiActive(mActivity) && !fileIsExitAndComplete
				&& upgrateData != null) {
			// wifi下且没有下载完成
			StatService.onEvent(mActivity, "upgrade_started", "wlan");
			Download.getInstance(mActivity).downloadUrl(upgrateData);
			return true;
		}
		return false;
	}

	public int getmLength() {
		return Download.getInstance(mActivity).getmLength();
	}

	@Override
	public void onErrorResult(String businessType, String errorOn,
			String errorInfo, Object session) {
		// TODO Auto-generated method stub
		mIsCanRequest = true;
		clearDir();
		// Log.i("kkkkkk", errorInfo);
		if (mActivity instanceof GNSettingActivity) {
			((GNSettingActivity) mActivity).onNewestVersion();
		}
	}

	@Override
	public Context getSelfContext() {
		// TODO Auto-generated method stub
		return mActivity;
	}

	@Override
	public void onCancel(String businessType, Object session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_left:
			clickLeftBt(arg0);
			break;
		case R.id.bt_right:
			clickRightBt(arg0);
			break;
		case R.id.bt_cancel_download:// 取消下载
			StatService.onEvent(mActivity, "upgrade_button", "cancel");
			closeAllDialog();
			break;
		case R.id.bt_continue_download:// 继续下载
			StatService.onEvent(mActivity, "upgrade_button", "continue");
			continueDownload();
			break;
		default:
			break;
		}
	}

	/**
	 * 继续下载
	 */
	private void continueDownload() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			closeAllDialog();
			Toast.makeText(mActivity, R.string.no_sdcard, 1).show();
			return;
		}
		mIsCanDownloadByUnWifi = true;
		View view = mUpgrateProgressDialog.getContentView();
		LinearLayout buttomView = (LinearLayout) view
				.findViewById(R.id.rl_buttom);
		buttomView.setVisibility(View.GONE);
		Download.getInstance(mActivity).setIsExit(true);
		StatService.onEvent(mActivity, "upgrade_started", "operator");
		Download.getInstance(mActivity).downloadUrl(
				ConfigManager.getInstance().getUpgrateData(mActivity));
	}

	private void clickRightBt(View arg0) {
		mCheckUpgrateDialog.dismiss();
		Button bt2 = (Button) arg0;
		String text2 = bt2.getText().toString();
		if (text2.equals(mActivity.getString(R.string.upgrade_download_now))) {
			// 立即下载
			JSONObject upgrateData = ConfigManager.getInstance()
					.getUpgrateData(mActivity);
			if (upgrateData == null) {
				return;
			}
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				closeAllDialog();
				Toast.makeText(mActivity, R.string.no_sdcard, 1).show();
				return;
			}
			StatService.onEvent(mActivity, "upgrade_button", "download");
			StatService.onEvent(mActivity, "upgrade_started", "wlan");
			Download.getInstance(mActivity).downloadUrl(upgrateData);
			if (mUpgrateProgressDialog == null) {
				mUpgrateProgressDialog = UpgradeDialogFactory
						.createUpgrateProgressDialog(mActivity, upgrateData,
								this);
			}
			mUpgrateProgressDialog.show();
		} else {
			// 立即安装
			if (fileIsExitAndComplete(getFilePath(mUpgrateData))) {
				InstallUtills.alertInstall(new File(getFilePath(mUpgrateData)),
						mActivity);
				StatService.onEvent(mActivity, "upgrade_button", "install");
			} else {
				Toast.makeText(mActivity,
						mActivity.getString(R.string.no_apk_error),
						Toast.LENGTH_LONG).show();
				Download.getInstance(mActivity).setIsExit(true);
				setIsDownloadComplete(false);
				if (mActivity instanceof GNSettingActivity) {
					((GNSettingActivity) mActivity).updateVersionInfo();
				}
			}
		}
	}

	private void clickLeftBt(View arg0) {
		Button bt1 = (Button) arg0;
		String text1 = bt1.getText().toString();
		final ImageView checkbox = (ImageView) mCheckUpgrateDialog
				.getContentView().findViewById(R.id.tip_check_box);
		final Drawable box = checkbox.getDrawable();
		if (!text1.equals(mActivity.getString(R.string.upgrade_donot_download))) {
			// 暂不更新,取消三次就不再自动提示
			if (box.getLevel() == 0) {
				UpgrateDataManage.saveInstallIsTip(mActivity,
						AndroidUtils.getAppVersionName(mActivity), true);
			} else {
				UpgrateDataManage.saveInstallIsTip(mActivity,
						AndroidUtils.getAppVersionName(mActivity), false);
			}
			StatService.onEvent(mActivity, "upgrade_button", " quit_install");
		} else {
			if (box.getLevel() == 0) {
				UpgrateDataManage.saveDownLoadingIsTip(mActivity,
						AndroidUtils.getAppVersionName(mActivity), true);
			} else {
				UpgrateDataManage.saveDownLoadingIsTip(mActivity,
						AndroidUtils.getAppVersionName(mActivity), false);
			}
			StatService.onEvent(mActivity, "upgrade_button", "quit_download");
		}
		mCheckUpgrateDialog.dismiss();
	}

	private void setIsDownloadComplete(boolean isDownloadComplete) {
		JSONObject mUpgrateData = ConfigManager.getInstance().getUpgrateData(
				mActivity);
		if (mUpgrateData == null) {
			return;
		}
		String versionName = mUpgrateData
				.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
		UpgrateDataManage.saveFileDownloadComplete(mActivity, versionName,
				isDownloadComplete);
		Download.getInstance(mActivity).setDownloadComplete(isDownloadComplete);
		if (isDownloadComplete) {
			UpgrateDataManage
					.saveIsStartDownload(mActivity, versionName, false);
		}
	}

	public void finish() {
		try {
			mActivity.unregisterReceiver(mNetworkReceiver);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void isAlertInstall() {
		if (mUpgrateData == null) {
			return;
		}
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return;
		}
		String versionName = mUpgrateData
				.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
		Boolean isDownloadComplete = UpgrateDataManage.isFileDownloadComplete(
				mActivity, versionName);
		Boolean isFirstShowInstallTip = UpgrateDataManage
				.isFirstShowInstallTip(mActivity, versionName);
		if (isAlertSystemInstall(versionName, isDownloadComplete,
				isFirstShowInstallTip)) {
			InstallUtills.alertInstall(new File(getFilePath(mUpgrateData)),
					mActivity);
			UpgrateDataManage.saveIsFirstShowInstallTip(mActivity, versionName,
					false);
		}
	}

	/**
	 * 是否弹出系统安装
	 * 
	 * @param versionName
	 * @param isDownloadComplete
	 * @param isFirstShowInstallTip
	 * @return
	 */
	private boolean isAlertSystemInstall(String versionName,
			Boolean isDownloadComplete, Boolean isFirstShowInstallTip) {
		return new File(getFilePath(mUpgrateData)) != null
				&& new File(getFilePath(mUpgrateData)).exists()
				&& isDownloadComplete
				&& isFirstShowInstallTip
				&& UpgrateDataManage.getNoInstallCount(mActivity, versionName) == 0;
	}
}
