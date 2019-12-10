// Gionee <yuwei><2013-12-9> add for CR00821559 begin
/*
 * InstallManager.java
 * classes : com.gionee.client.business.appInstall.InstallManager
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-9 上午10:16:43
 */
package com.gionee.client.business.appDownload;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.exception.MyErrorException;

/**
 * InstallManager
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-9 上午10:16:43
 * @description TODO
 */
@SuppressLint("NewApi")
public class InstallManager {
    private static final String TAG = "InstallManager";
    private static InstallManager sInstance;
    private JSONObject mAppJson;
    private Context mContext;

    private InstallManager(Context context) {
        mContext = context.getApplicationContext();

    }

    public static InstallManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new InstallManager(context);
        }
        return sInstance;

    }

    public void startInstallById(long downloadId) throws Exception {
        GNDownloadUtills utils = new GNDownloadUtills(
                (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE));
        String fileName = utils.getFileName(downloadId);
        LogUtils.log("gn_file_name", fileName);
        if (TextUtils.isEmpty(fileName)) {
            InstallUtills.removeDownloadHistory(mContext, downloadId, null);
            throw new MyErrorException(mContext.getString(R.string.INSTALL_ERROR));
        }
        File file = new File(fileName);
        if (!file.exists()) {
            InstallUtills.removeDownloadHistory(mContext, downloadId, file);
            throw new MyErrorException(mContext.getString(R.string.INSTALL_ERROR));
        }
        InstallUtills.alertInstall(file, mContext);

    }

    public void startInstallByName(String fileName) throws Exception {

        String fileNameStr = InstallUtills.generateFilePath(fileName);
        if (TextUtils.isEmpty(fileNameStr)) {
            throw new MyErrorException(mContext.getString(R.string.INSTALL_ERROR));
        }
        File file = new File(fileNameStr);
        if (file == null || !file.exists()) {
            throw new MyErrorException(mContext.getString(R.string.INSTALL_ERROR));
        }
        InstallUtills.alertInstall(file, mContext);

    }

    public void start(final Object appInfoJson) {
        try {
            InstallManager.getInstance(mContext).mAppJson = (JSONObject) appInfoJson;
            JSONArray appListArray = mAppJson.optJSONArray(HttpConstants.Response.AppStore.APP_LIST_JA);
            final int arrayLenth = appListArray.length();
            for (int i = 0; i < arrayLenth; i++) {
                JSONObject appInfo = appListArray.optJSONObject(i);
                String appPackageName = appInfo.optString(HttpConstants.Response.AppStore.PACKAGE_S);
                int versionCode = appInfo.optInt(HttpConstants.Response.AppStore.VERSION_S);
                PackageInfo packageInfo = InstallUtills.getPackgeInfo(mContext, appPackageName);
                if (packageInfo != null && versionCode > packageInfo.versionCode) {
                    startInstallBuisiness(mContext, appInfo);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static class AlertInstallPackate {
        public File mFile;
        public Context mContext;

    }

    public static final class Status {
        public static final int GET_PACKAGE_INFO_SUCCESS = 1;
        public static final int ALEART_INSTALL = 2;
    }

    /**
     * @param context
     * @param appInfo
     * @author yuwei
     * @description TODO 使用DownloadManger下载app
     */
    private void startInstallBuisiness(Context context, JSONObject appInfo) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "appInfo=" + appInfo.toString());
        String apkName = appInfo.optString(HttpConstants.Response.AppStore.APK_S);
        int appVersion = appInfo.optInt(HttpConstants.Response.AppStore.VERSION_S);
        String fileName = InstallUtills.generateFileName(apkName, appVersion);
        String filePath = InstallUtills.generateFilePath(fileName);
        File file = new File(filePath);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (InstallUtills.invalidataApk(context, appInfo, file)) {
            AppInstallQuenee.getInstance().startAppInstall(context, file);
        } else if (!prefs.contains(filePath) || !file.exists()) {
            InstallUtills.clearCacheData(prefs.getLong(filePath, 0), context, file);
            InstallUtills.downloadFile(context, fileName, file, appInfo);
        } else {
            ListDownloadManager.getInstance(mContext).queryDownloadStatus(prefs.getLong(filePath, 0));
        }

    }

}
//Gionee <yuwei><2013-12-20> add for CR00821559 end