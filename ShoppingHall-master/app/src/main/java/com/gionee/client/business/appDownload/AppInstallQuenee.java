// Gionee <yuwei><2013-12-10> add for CR00821559 begin
/*
 * AppInstallQuenee.java
 * classes : com.gionee.client.business.appInstall.AppInstallQuenee
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-10 上午10:14:46
 */
package com.gionee.client.business.appDownload;

import java.io.File;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * AppInstallQuenee
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-10 上午10:14:46
 * @description TODO 应用安装线程
 */
public class AppInstallQuenee {
//    private static final String TAG = "InstallManager";
    private static AppInstallQuenee sInstance;
    private LinkedList<InstallManager.AlertInstallPackate> mAppInstallQuenee;
    private NetThread mNetThread;
    private boolean mIsWaiting = false;
    private boolean mIsRunning = true;
    private static Object sLock = new Object();

    private AppInstallQuenee() {
        mAppInstallQuenee = new LinkedList<InstallManager.AlertInstallPackate>();
        mNetThread = new NetThread();
        mNetThread.start();
    }

    public static AppInstallQuenee getInstance() {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new AppInstallQuenee();
            }
        }
        return sInstance;
    }

    /**
     * 添加请求
     * 
     * @param page
     * @param request
     * @param listener
     */
    public void startAppInstall(Context context, File file) {
        InstallManager.AlertInstallPackate appPackage = new InstallManager.AlertInstallPackate();
        appPackage.mContext = context;
        appPackage.mFile = file;
        mAppInstallQuenee.offer(appPackage);
        if (mIsWaiting) {
            synchronized (AppInstallQuenee.this) {
                AppInstallQuenee.this.notify();
            }
        }

    }

    /**
     * 回调handler
     */
    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case InstallManager.Status.ALEART_INSTALL:
                    InstallManager.AlertInstallPackate info = (InstallManager.AlertInstallPackate) msg.obj;
                    InstallUtills.alertInstall(info.mFile, info.mContext);
                    break;

                default:
                    break;
            }

        };
    };

    @SuppressLint("HandlerLeak")
    private class NetThread extends Thread {

        @Override
        public void run() {
            while (mIsRunning) {
                synchronized (AppInstallQuenee.this) {
                    if (mAppInstallQuenee.size() > 0) {
                        InstallManager.AlertInstallPackate appPackage = mAppInstallQuenee.poll();
                        installFile(appPackage.mContext, appPackage.mFile);
                    } else {
                        try {
                            mIsWaiting = true;
                            AppInstallQuenee.this.wait();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            restartThread();
                        }

                    }
                }
            }
        }

        /**
         * 
         * @author yuwei
         * @description TODO
         */
        private void restartThread() {
            mNetThread.interrupt();
            mNetThread = null;
            mNetThread = new NetThread();
            mNetThread.start();
            synchronized (AppInstallQuenee.this) {
                mIsWaiting = false;
            }
        }
    }

    /**
     * @param context
     * @author yuwei
     * @description TODO
     */
    private static void installFile(Context context, File file) {
        final boolean isFileExist = file == null || !file.exists();
        if (isFileExist) {
            return;
        }
        if (!InstallUtills.slientInstall(file)) {
            Message msg = new Message();
            msg.what = InstallManager.Status.ALEART_INSTALL;
            InstallManager.AlertInstallPackate appPackage = new InstallManager.AlertInstallPackate();
            appPackage.mContext = context;
            appPackage.mFile = file;
            msg.obj = appPackage;
            sHandler.sendMessage(msg);

        }
    }
}
//Gionee <yuwei><2013-12-10> add for CR00821559 end