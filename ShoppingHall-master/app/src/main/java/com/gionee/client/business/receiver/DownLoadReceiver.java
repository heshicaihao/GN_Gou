// Gionee <yuwei><2013-11-22> add for CR00821559 begin
/*
 * DownLoadReceiver.java
 * classes : com.gionee.client.business.receiver.DownLoadReceiver
 * @author yuwei
 * V 1.0.0
 * Create at 2013-11-22 下午3:26:10
 */
package com.gionee.client.business.receiver;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.gionee.client.business.appDownload.ListDownloadManager;
import com.gionee.client.business.util.LogUtils;

/**
 * DownLoadReceiver
 * 
 * @author yuwei <br/>
 * @date create at 2013-11-22 下午3:26:10
 * @description TODO
 */
@SuppressLint("InlinedApi")
public class DownLoadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        LogUtils.log("InstallManager",
                LogUtils.getThreadName() + intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ListDownloadManager.getInstance(context).queryDownloadStatus(
                        intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
            }
        }, 500);

    }
}
