//package com.gionee.client.business.upgradeplus;
//
//import com.gionee.appupgrade.common.IGnAppUpgrade.CallBack;
//import com.gionee.appupgrade.common.IGnAppUpgrade.Error;
//import com.gionee.client.business.upgradeplus.UpgradeManager.UserFeedback;
//import com.gionee.client.business.util.LogUtils;
//
////Gionee <hcy> <2013-7-22> add for CR00838312 begin
//public class UpgradeCallback implements CallBack {
//
//    private static final String TAG = "AppUpgradeCallback";
//    private UpgradeManager mManager;
//
//    public UpgradeCallback(UpgradeManager manager) {
//        this.mManager = manager;
//    }
//
//    public static final class CallbackStatus {
//        public static final int HAS_NEW_VERSION = 1; // 有新版本
//        public static final int NO_NEW_VERSION = 2;// 没有新版本
//        public static final int DOWNLOAD_COMPLETE = 3; // 下载完成
//        public static final int DOWNLOAD_PAUSE = 4; // 下载暂停
//        public static final int NO_MORE_THAN_24_HOUR = 5; // 本次检查时间和上次检查时间间隔不到24小时
//    }
//
//    @Override
//    public void onDownLoading(final int filesize, final int downsize, String packageName) {
//        if (filesize > 0 && filesize >= downsize) {
//            mManager.updateProgressDialog(filesize, downsize);
//        }
//    }
//
//    @Override
//    public void onError(int error, String arg1) {
//        LogUtils.log(TAG, LogUtils.getFunctionName() + "error:" + error + ",arg1:" + arg1);
//        switch (error) {
//            case Error.NET_CONNECT_ERROR:
//                UpgradeManager.resetDownloadTask();
//                mManager.showUserFeedback(UserFeedback.ERROR_NO_CONNECT);
//                break;
//            case Error.DICK_NOSPACE:
//                UpgradeManager.resetDownloadTask();
//                mManager.showUserFeedback(UserFeedback.ERROR_NO_SPACE);
//                break;
//            case Error.NO_SDCARD:
//                UpgradeManager.resetDownloadTask();
//                mManager.showUserFeedback(UserFeedback.ERROR_NO_SDCARD);
//                break;
//            case Error.ERROR_LOCAL_FILE_NOT_FOUND:
//                UpgradeManager.resetDownloadTask();
//                mManager.showUserFeedback(UserFeedback.ERROR_LOCAL_FILE_NOT_FOUND);
//                break;
//            default:
//                LogUtils.log(TAG, LogUtils.getFunctionName() + "No hander Error:" + error);
//                break;
//        }
//
//    }
//
//    @Override
//    public void onOperationStateChange(int state, String packageName) {
//        LogUtils.log(TAG, LogUtils.getThreadName() + "state:" + state);
//        switch (state) {
//            case CallbackStatus.HAS_NEW_VERSION:
//                LogUtils.log(TAG, LogUtils.getThreadName() + "Has new version");
//                if (!mManager.isAutoCheck()) {
//                    mManager.showUserFeedback(UserFeedback.MESSAGE_HAS_NEW_VERSION);
//                    return;
//                }
//                if (mManager.isDisplayThisVersion()) {
//                    mManager.showUserFeedback(UserFeedback.MESSAGE_HAS_NEW_VERSION);
//                } else {
//                    LogUtils.log(TAG, LogUtils.getThreadName() + "Auto check,Don't prompt");
//                }
//                break;
//            case CallbackStatus.NO_NEW_VERSION:
//                // 没有新版本,应用可以设计没有新版本时对用户的提示界面。
//                LogUtils.log(TAG, LogUtils.getThreadName() + "No new version");
//                mManager.showUserFeedback(UserFeedback.MESSAGE_NO_NEW_VERSION);
//                break;
//            case CallbackStatus.DOWNLOAD_COMPLETE:
//                LogUtils.log(TAG, LogUtils.getThreadName() + "Download completed...");
//                UpgradeManager.resetDownloadTask();
//                mManager.showUserFeedback(UserFeedback.MESSAGE_DOWNLOAD_PROGRESS_OFF);
//                mManager.showUserFeedback(UserFeedback.MESSAGE_DOWNLOAD_COMPLETE);
//                // 下载完成,应用可以设计下载完成的各种提示界面及开始安装界面，开始安装接口如下：
//                break;
//            case CallbackStatus.DOWNLOAD_PAUSE:
//                UpgradeManager.resetDownloadTask();
//                LogUtils.log(TAG, LogUtils.getThreadName() + "Download paused...");
//                // 下载暂停应用可以进行设计界面实现继续下载等功能。
//                break;
//            case CallbackStatus.NO_MORE_THAN_24_HOUR:
//                LogUtils.log(TAG, LogUtils.getThreadName() + "Check so many times in 24 hour");
//                break;
//
//            default:
//                LogUtils.log(TAG, LogUtils.getThreadName() + "state:" + state + ",packageName:" + packageName);
//                break;
//        }
//    }
//
//}
////Gionee <hcy> <2013-7-22> add for CR00838312 end
