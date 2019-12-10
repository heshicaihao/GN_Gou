//package com.gionee.client.business.upgradeplus;
//
//import java.lang.ref.WeakReference;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//
//import com.gionee.appupgrade.common.FactoryAppUpgrade;
//import com.gionee.appupgrade.common.IGnAppUpgrade;
//import com.gionee.appupgrade.common.IGnAppUpgrade.CallBack;
//import com.gionee.client.R;
//import com.gionee.client.activity.GNSettingActivity;
//import com.gionee.client.business.upgradeplus.common.BootCountCaretaker;
//import com.gionee.client.business.upgradeplus.common.UpgradeUtils;
//import com.gionee.client.business.util.AndroidUtils;
//import com.gionee.client.business.util.LogUtils;
//import com.gionee.client.view.widget.CustomProgressDialog;
//
///**
// * com.gionee.client.upgradeplus.UpgradeManager
// * 
// * @author hcy <br/>
// *         create at 2013-7-22 下午4:28:42
// */
//public class UpgradeManager {
//
//    private static final ExecutorService sExecutorService = Executors.newSingleThreadExecutor();
//    private static volatile Future<?> sFuture = null;
//
//    private static final String TAG = "AppUpgradeManager";
//
//    private static final String UPGRADE_PREFERENCES = "upgrade_preferences";
//    private static final String KEY_DISPLAY_THIS_VERSION = "upgrade_do_not_show_this_version";
//    private static final String KEY_THE_LATEST_VERSION = "upgrade_last_version";
//    @SuppressWarnings("unused")
//    private static final int AUTO_CHECK_MIN_THRESHOLD = 3;
//    private static final int REQUEST_CODE = 1000;
//    private static InternalHandler sHander;
//
//    private boolean mIsAlwaysForceMode = false;
//    private boolean mIsAutoCheck = true;
//
//    private Activity mActivity;
//    private IGnAppUpgrade mIGnAppUpgrade;
//    private AppNewVersionInfo mAppNewVerionInfo;
//    private SharedPreferences mPreferences;
//    private CallBack mCallBack;
//    private BootCountCaretaker mCountCaretaker;
//    private ProgressDialog mCheckPogressDialog;
//    private CustomProgressDialog mDownloadDialog;
//    private volatile Status mStatus = Status.PENDING;
//    public static boolean hasNewVersion;
//    public static String mVersionName;
//    private static IUpdateListener sUpdateStateListener;
//
//    /**
//     * @return the mUpdateStateListener
//     */
//    public IUpdateListener getmUpdateStateListener() {
//        return sUpdateStateListener;
//    }
//
//    /**
//     * @param mUpdateStateListener
//     *            the mUpdateStateListener to set
//     */
//    public static void setmUpdateStateListener(IUpdateListener mUpdateStateListener) {
//        sUpdateStateListener = mUpdateStateListener;
//    }
//
//    public Status getStatus() {
//        return mStatus;
//    }
//
//    public void setStatus(Status status) {
//        this.mStatus = status;
//    }
//
//    /**
//     * Indicates the current status of the task.
//     * 
//     */
//    public enum Status {
//        /**
//         * Indicates that the task has not been executed yet.
//         */
//        PENDING,
//        /**
//         * Indicates that the check task is running.
//         */
//        CHECKING,
//        /**
//         * Indicates that the check task is downloading.
//         */
//        DOWNLOADING,
//        /**
//         * Indicates that the check task has download_complete.
//         */
//
//        DOWNLOAD_COMPLETE,
//        /**
//         * Indicates that the check task has installing.
//         */
//        INSTALLING,
//        /**
//         * Indicates that the check task has finished.
//         */
//        FINISHED,
//    }
//
//    public static void resetDownloadTask() {
//        sFuture = null;
//        LogUtils.log(TAG, LogUtils.getThreadName() + "set future is null");
//    }
//
//    public boolean isAutoCheck() {
//        return mIsAutoCheck;
//    }
//
//    public synchronized void setAutoCheck(boolean isAutoCheck) {
//        this.mIsAutoCheck = isAutoCheck;
//    }
//
//    public AppNewVersionInfo getAppNewVerionInfo() {
//        return mAppNewVerionInfo;
//    }
//
//    public void setAppNewVerionInfo(AppNewVersionInfo appNewVerionInfo) {
//        this.mAppNewVerionInfo = appNewVerionInfo;
//    }
//
//    public UpgradeManager(Activity activity, String packageName) {
//        LogUtils.log(TAG, LogUtils.getThreadName());
//        onDestroy();
//        this.mActivity = activity;
//        this.mIGnAppUpgrade = FactoryAppUpgrade.getGnAppUpgrade();
//        this.mCallBack = new UpgradeCallback(this);
//        this.mPreferences = activity.getSharedPreferences(UPGRADE_PREFERENCES, Context.MODE_PRIVATE);
//        this.mIGnAppUpgrade.initial(mCallBack, activity, activity.getPackageName(), packageName);
//        this.mCountCaretaker = new BootCountCaretaker(activity);
//        this.mCheckPogressDialog = UpgradeDialogFactory.createProgressDialog(activity);
//        this.mDownloadDialog = UpgradeDialogFactory.createDownloadProgressDialog(activity);
//        sHander = new InternalHandler(activity, this);
//        mCountCaretaker.increaseBootCount();
//    }
//
//    public Future<?> submitCommand(Runnable runnable) {
//        if (runnable == null) {
//            LogUtils.log(TAG, LogUtils.getThreadName() + "Warning: runnable is null");
//            return null;
//        }
//        if (sExecutorService == null || sExecutorService.isShutdown()) {
//            LogUtils.log(TAG, LogUtils.getThreadName() + "Warning: mExectorService is null");
//            return null;
//        }
//        LogUtils.log(TAG, LogUtils.getThreadName() + "Runnable.hashCode:(" + runnable.hashCode() + ")");
//        return sExecutorService.submit(runnable);
//    }
//
//    public static final class UserFeedback {
//        public static final int MESSAGE_HAS_NEW_VERSION = 1;
//        public static final int MESSAGE_FORCE_MODE = 2;
//        public static final int MESSAGE_DOWNLOAD_COMPLETE = 3;
//        public static final int MESSAGE_NO_NETWORK = 4;
//        public static final int MESSAGE_NO_NEW_VERSION = 5;
//        public static final int MESSAGE_PROGRESS_ON = 6;
//        public static final int MESSAGE_PROGRESS_OFF = 7;
//        public static final int ERROR_NO_CONNECT = 8;
//        public static final int ERROR_NO_SPACE = 9;
//        public static final int ERROR_NO_SDCARD = 10;
//        public static final int ERROR_LOCAL_FILE_NOT_FOUND = 11;
//        public static final int MESSAGE_BUSY_NOW = 12;
//        public static final int MESSAGE_DOWNLOAD_PROGRESS_ON = 13;
//        public static final int MESSAGE_DOWNLOAD_PROGRESS_OFF = 14;
//    }
//
//    private static class InternalHandler extends Handler {
//
//        public WeakReference<Activity> mWeakReferenceActivity;
//        public WeakReference<UpgradeManager> mWeakReferenceManager;
//        private Dialog mNewVersionDialog;
//        private Dialog mForceVersionDialog;
//        private Dialog mDownloadCompleteDialog;
//        private Dialog mNoNetworkDialog;
//
//        public InternalHandler(Activity activity, UpgradeManager manager) {
//            mWeakReferenceActivity = new WeakReference<Activity>(activity);
//            mWeakReferenceManager = new WeakReference<UpgradeManager>(manager);
//            if (Looper.myLooper() != Looper.getMainLooper()) {
//                throw new RuntimeException("This method can only be called in main thread");
//            }
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            final Activity activity = mWeakReferenceActivity.get();
//            final UpgradeManager manager = mWeakReferenceManager.get();
//            if (msg == null || activity == null || manager == null || activity.isFinishing()) {
//                LogUtils.loge(TAG, LogUtils.getThreadName() + "Arguments error...");
//                return;
//            }
//            LogUtils.log(TAG, LogUtils.getThreadName() + "handle.hashCode():" + this.hashCode()
//                    + ",msg.what:" + msg.what);
//            switch (msg.what) {
//                case UserFeedback.MESSAGE_HAS_NEW_VERSION:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "MESSAGE_HAS_NEW_VERSION");
//                    AppNewVersionInfo appNewVerionInfo = new AppNewVersionInfo(
//                            manager.mIGnAppUpgrade.getNewVersionNum(), "",
//                            manager.mIGnAppUpgrade.getReleaseNote(),
//                            manager.mIGnAppUpgrade.getDownloadFileSize(),
//                            manager.mIGnAppUpgrade.getIsPatchFile(), manager.mIGnAppUpgrade.isForceMode());
//                    manager.setAppNewVerionInfo(appNewVerionInfo);
//                    hasNewVersion = true;
//                    mVersionName = appNewVerionInfo.getVersion();
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "Info:" + appNewVerionInfo.toString());
//                    if (mNewVersionDialog == null) {
//                        mNewVersionDialog = UpgradeDialogFactory.createHasNewVersionDialog(activity, manager);
//                    }
//                    if (!mNewVersionDialog.isShowing()) {
//                        mNewVersionDialog.show();
//                    }
//                    try {
//                        if (activity instanceof GNSettingActivity) {
//                            ((GNSettingActivity) activity).updateVersionInfo();
//                        }
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    break;
//                case UserFeedback.MESSAGE_FORCE_MODE:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "MESSAGE_FORCE_MODE");
//                    if (mForceVersionDialog == null) {
//                        mForceVersionDialog = UpgradeDialogFactory
//                                .createForceVersionDialog(activity, manager);
//                    }
//                    if (!mForceVersionDialog.isShowing()) {
//                        mForceVersionDialog.show();
//                    }
//                    break;
//                case UserFeedback.MESSAGE_DOWNLOAD_COMPLETE:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "MESSAGE_DOWNLOAD_COMPLETE");
//                    manager.setStatus(Status.DOWNLOAD_COMPLETE);
//                    if (manager.isNewVersion()) {
//                        LogUtils.log(TAG, "new version!");
//                        if (mDownloadCompleteDialog == null) {
//                            mDownloadCompleteDialog = UpgradeDialogFactory.createDownloadCompleteDialog(
//                                    activity, manager);
//                        }
//                        if (!mDownloadCompleteDialog.isShowing()) {
//                            mDownloadCompleteDialog.show();
//                        }
//                    } else {
//                        LogUtils.log(TAG, "is old version!");
//                        manager.cleanUpgradInfo();
//                        manager.setStatus(Status.PENDING);
//                    }
//                    break;
//                case UserFeedback.MESSAGE_NO_NETWORK:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "MESSAGE_NO_NETWORK");
//                    if (mNoNetworkDialog == null) {
//                        mNoNetworkDialog = UpgradeDialogFactory.createNoNetworkDialog(activity, manager);
//                    }
//                    if (!mNoNetworkDialog.isShowing()) {
//                        mNoNetworkDialog.show();
//                    }
//                    break;
//                case UserFeedback.MESSAGE_NO_NEW_VERSION:
//                    hasNewVersion = false;
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "MESSAGE_NO_NEW_VERSION");
//                    if (manager.isAutoCheck()) {
//                        LogUtils.log(TAG, LogUtils.getThreadName() + "Auto check,do nothing...");
//                    } else {
//                        if (sUpdateStateListener != null) {
//                            sUpdateStateListener.onNewestVersion();
//                        } else {
//                            UpgradeDialogFactory.createToastMessage(activity,
//                                    activity.getResources().getString(R.string.upgrade_no_need_update))
//                                    .show();
//                        }
//                    }
////                    ((GnHomeActivity) activity).getFragmentMenu().setVersionPromptIcon(false);
//                    break;
//                case UserFeedback.MESSAGE_PROGRESS_ON:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "MESSAGE_PROGRESS_ON");
//                    if (sUpdateStateListener != null) {
//                        sUpdateStateListener.onChecking();
//                    } else {
//                        UpgradeUtils.showDialog(manager.mCheckPogressDialog, activity);
//                    }
//                    break;
//                case UserFeedback.MESSAGE_PROGRESS_OFF:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "MESSAGE_PROGRESS_OFF");
//                    if (sUpdateStateListener != null) {
//                        sUpdateStateListener.onCheckComplete();
//                    } else {
//                        UpgradeUtils.dismissDialog(manager.mCheckPogressDialog, activity);
//                    }
//                    break;
//                case UserFeedback.ERROR_NO_CONNECT:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "ERROR_NO_CONNECT");
//                    if (manager.isAutoCheck()) {
//                        LogUtils.log(TAG, LogUtils.getThreadName() + "Auto check,do nothing...");
//                        return;
//                    }
//                    UpgradeDialogFactory.createToastMessage(activity,
//                            activity.getResources().getString(R.string.upgrade_error_network_exception))
//                            .show();
//                    break;
//                case UserFeedback.ERROR_NO_SPACE:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "ERROR_NO_SPACE");
////                    if (manager.isAutoCheck()) {
////                        LogUtils.log(TAG, LogUtils.getThreadName() + "Auto check,do nothing...");
////                        return;
////                    }
//                    UpgradeDialogFactory.createToastMessage(activity,
//                            activity.getResources().getString(R.string.upgrade_error_no_enough_space)).show();
//                    break;
//                case UserFeedback.ERROR_NO_SDCARD:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "ERROR_NO_SDCARD");
//                    UpgradeDialogFactory.createToastMessage(activity,
//                            activity.getResources().getString(R.string.upgrade_error_no_sdcard)).show();
//                    break;
//                case UserFeedback.ERROR_LOCAL_FILE_NOT_FOUND:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "ERROR_LOCAL_FILE_NOT_FOUND");
//                    UpgradeDialogFactory.createToastMessage(activity,
//                            activity.getResources().getString(R.string.upgrade_error_not_fond_file)).show();
//                    break;
//                case UserFeedback.MESSAGE_BUSY_NOW:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "MESSAGE_BUSY_NOW");
////                    UpgradeDialogFactory.createToastMessage(activity,
////                            activity.getResources().getString(R.string.upgrade_appupgrade_busy)).show();
//                    break;
//                case UserFeedback.MESSAGE_DOWNLOAD_PROGRESS_ON:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "MESSAGE_DOWNLOAD_PROGRESS_ON");
//                    UpgradeUtils.showDialog(manager.mDownloadDialog, activity);
//                    break;
//                case UserFeedback.MESSAGE_DOWNLOAD_PROGRESS_OFF:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "MESSAGE_DOWNLOAD_PROGRESS_OFF");
//                    UpgradeUtils.dismissDialog(manager.mDownloadDialog, activity);
//                    break;
//                default:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "No process the message with msg.what:"
//                            + msg.what);
//                    break;
//            }
//        }
//    }
//
//    public void setAlwaysForceMode(boolean alwaysForceMode) {
//        this.mIsAlwaysForceMode = alwaysForceMode;
//    }
//
//    public void cleanUpgradInfo() {
//        mActivity.getSharedPreferences("upgrade_preferences_com.gionee.client", Context.MODE_PRIVATE).edit()
//                .clear().commit();
//        mActivity
//                .getSharedPreferences("upgrade_preferences_com.gionee.client_newversion",
//                        Context.MODE_PRIVATE).edit().clear().commit();
//    }
//
//    public boolean isForceMode() {
//        if (mIsAlwaysForceMode) {
//            return true;
//        }
//        return mIGnAppUpgrade.isForceMode();
//    }
//
//    public void onDestroy() {
//        try {
//            LogUtils.log(TAG, LogUtils.getThreadName());
//            FactoryAppUpgrade.destoryGnAppUpgrade();
//        } catch (Exception e) {
//            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
//        }
//    }
//
//    public String getNewVersion() {
//        String version = "";
//        try {
//            version = mIGnAppUpgrade.getNewVersionNum();
//        } catch (Exception e) {
//        }
//        return version;
//    }
//
//    public void setDisplayFlag(Boolean isShow, String versionName) {
//        LogUtils.log(TAG, LogUtils.getThreadName() + "isShow:" + isShow + ",versionName:" + versionName);
//        mPreferences.edit().putBoolean(KEY_DISPLAY_THIS_VERSION, isShow).commit();
//        mPreferences.edit().putString(KEY_THE_LATEST_VERSION, getNewVersion()).commit();
//    }
//
//    public void cleanData() {
//        mPreferences.edit().putBoolean(KEY_DISPLAY_THIS_VERSION, true).commit();
//        mPreferences.edit().putString(KEY_THE_LATEST_VERSION, "").commit();
//    }
//
//    public boolean isDisplayThisVersion() {
//        String version = mPreferences.getString(KEY_THE_LATEST_VERSION, "");
//        boolean isShow = mPreferences.getBoolean(KEY_DISPLAY_THIS_VERSION, true);
//        String newVersion = getNewVersion();
//        LogUtils.log(TAG, LogUtils.getThreadName() + "isShow:" + isShow + ",LAST version:" + version
//                + ",NEW Version:" + newVersion);
//        if (version.equals(newVersion)) {
//            return isShow;
//        }
//        return true;
//    }
//
//    public boolean isNewVersion() {
//
//        String version = replaceV(AndroidUtils.getAppVersionName(mActivity));
//        String downloadVersion = replaceV(getNewVersion());
//
//        int dPosition = downloadVersion.lastIndexOf(".");
//        int vPosition = version.lastIndexOf(".");
//
//        if (downloadVersion.substring(0, vPosition).compareToIgnoreCase(version.substring(0, dPosition)) > 0) {
//            return true;
//        }
//
//        if (downloadVersion.substring(0, vPosition).compareToIgnoreCase(version.substring(0, dPosition)) < 0) {
//            return false;
//        }
//
//        if (downloadVersion.substring(vPosition).length() > version.substring(dPosition).length()) {
//            return true;
//        }
//
//        if (downloadVersion.substring(vPosition).length() < version.substring(dPosition).length()) {
//            return false;
//        }
//
//        if (downloadVersion.substring(vPosition).compareToIgnoreCase(version.substring(dPosition)) > 0) {
//            return true;
//        }
//
//        return false;
//    }
//
//    private String replaceV(String string) {
//        LogUtils.log(TAG, LogUtils.getFunctionName() + "  " + string);
//        string = string.replace(" ", "");
//        if (string.startsWith("v") || string.startsWith("V")) {
//            return string.substring(1);
//        }
//        return string;
//    }
//
//    class ShowProgressRunnable implements Runnable {
//        @Override
//        public void run() {
//            showUserFeedback(UserFeedback.MESSAGE_PROGRESS_ON);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    class FinishCheckRunnable implements Runnable {
//        @Override
//        public void run() {
//            mStatus = Status.FINISHED;
//            mStatus = Status.PENDING;
//        }
//    };
//
//    public synchronized void startCheck(boolean isAuto) {
//        LogUtils.log(TAG, LogUtils.getThreadName() + "isAuto:" + isAuto + ",mStatus:" + mStatus);
//        if (isDownloadRunningInBackgroud(isAuto)) {
//            if (mActivity instanceof GNSettingActivity) {
//                UpgradeDialogFactory.createToastMessage(mActivity,
//                        mActivity.getResources().getString(R.string.upgrade_appupgrade_busy)).show();
//            }
//            return;
//        }
//        if (mStatus != Status.PENDING) {
//            switch (mStatus) {
//                case DOWNLOAD_COMPLETE:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "DOWNLOAD_COMPLETE");
//                    setStatus(Status.PENDING);
//                    break;
//                case DOWNLOADING:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "DOWNLOADING");
//                    showUserFeedback(UserFeedback.MESSAGE_DOWNLOAD_PROGRESS_ON);
//                    break;
//                case CHECKING:
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "Cannot execute task:"
//                            + " the task is already running.");
//                    showUserFeedback(UserFeedback.MESSAGE_BUSY_NOW);
//                    break;
//                case FINISHED:
//                    LogUtils.log(TAG, LogUtils.getThreadName()
//                            + "Cannot execute task: the task has already been executed ");
//                    showUserFeedback(UserFeedback.MESSAGE_BUSY_NOW);
//                    break;
//                default:
//                    break;
//            }
//            return;
//        }
//
//        mStatus = Status.CHECKING;
//        mIsAutoCheck = isAuto;
//        if (mIsAutoCheck) {
//            submitCommand(mIGnAppUpgrade.checkApkVersion(mIsAutoCheck, false));
//        } else {
//            submitCommand(new ShowProgressRunnable());
//            submitCommand(mIGnAppUpgrade.checkApkVersion(mIsAutoCheck, false));
//            submitCommand(new DissmissProgressRunnable());
//        }
//        submitCommand(new FinishCheckRunnable());
//    }
//
//    protected synchronized void startDownload() {
//        LogUtils.log(TAG, LogUtils.getThreadName() + "mStatus:" + mStatus);
//        submitCommand(new ShowDownloadProgressRunnable());
//        updateProgressDialog(100, 0);
//        mStatus = Status.DOWNLOADING;
//        sFuture = submitCommand(mIGnAppUpgrade.downLoadApk());
//        submitCommand(new DissmissDownloadProgressRunnable());
//        submitCommand(new FinishCheckRunnable());
//    }
//
//    protected synchronized void startInstallApp() {
//        LogUtils.log(TAG, LogUtils.getThreadName());
//        submitCommand(mIGnAppUpgrade.installApk(mActivity, REQUEST_CODE));
//    }
//
//    private boolean isDownloadRunningInBackgroud(boolean isAuto) {
//        if (sFuture != null) {
//            if (!sFuture.isDone() && !sFuture.isCancelled()) {
//                if (!isAuto) {
//                    showUserFeedback(UserFeedback.MESSAGE_BUSY_NOW);
//                }
//                LogUtils.log(TAG, LogUtils.getThreadName() + "future.isCancelled():" + sFuture.isCancelled()
//                        + "future.isDone():" + sFuture.isDone());
//                return true;
//            }
//        } else {
//            LogUtils.log(TAG, LogUtils.getThreadName() + "Download future is null");
//        }
//        return false;
//    }
//
//    protected void showUserFeedback(int feedbackId) {
//        if (sHander != null) {
//            sHander.sendEmptyMessage(feedbackId);
//        }
//    }
//
//    protected void showUserFeedback(int feedbackId, long delayTimes) {
//        if (sHander != null) {
//            sHander.sendEmptyMessageDelayed(feedbackId, delayTimes);
//        }
//    }
//
//    public void updateProgressDialog(int max, int downsize) {
//        try {
//            if (mDownloadDialog != null && !mActivity.isFinishing()) {
//                mDownloadDialog.setMessage1(downsize, max);
//                mDownloadDialog.setMessage2(downsize, max);
//                mDownloadDialog.setProgress(downsize, max);
//            }
//        } catch (Exception e) {
//            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
//        }
//    }
//
//    class DissmissProgressRunnable implements Runnable {
//        @Override
//        public void run() {
//            showUserFeedback(UserFeedback.MESSAGE_PROGRESS_OFF);
//        }
//    };
//
//    class ShowDownloadProgressRunnable implements Runnable {
//        @Override
//        public void run() {
//            showUserFeedback(UserFeedback.MESSAGE_DOWNLOAD_PROGRESS_ON);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    class DissmissDownloadProgressRunnable implements Runnable {
//        @Override
//        public void run() {
//            showUserFeedback(UserFeedback.MESSAGE_DOWNLOAD_PROGRESS_OFF);
//            resetDownloadTask();
//        }
//    };
//
//}
//// Gionee <hcy> <2013-7-22> add for CR00838312 end
