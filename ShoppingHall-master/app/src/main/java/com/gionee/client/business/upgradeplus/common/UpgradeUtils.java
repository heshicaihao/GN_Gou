package com.gionee.client.business.upgradeplus.common;

import java.math.BigDecimal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.gionee.client.R;
import com.gionee.client.business.manage.GNActivityManager;
import com.gionee.client.business.util.LogUtils;

public class UpgradeUtils {

    private static final String TAG = "AppUpgradeUtils";

    public static void dismissDialog(Dialog dialog, Activity activity) {
        try {
            if (dialog != null && !activity.isFinishing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
    }

    public static void showDialog(Dialog dialog, Activity activity) {
        try {
            if (dialog != null && !activity.isFinishing()) {
                dialog.show();
            }
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
    }

    public static String getVersion(Context context, String version) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "version" + version);
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return version;
        }
    }

    public static void closeApp(Activity activity) {
        try {
            GNActivityManager.getScreenManager().popAllActivity();
        } catch (Exception e) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    public static String getAppVersionName(Context context) {
        String versionName = "unknown version";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = " " + packInfo.versionName;
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
        return versionName;
    }

    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packInfo.versionCode;
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);

        }
        return versionCode;
    }

    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1) {
            return (returnValue + "  MB ");
        }
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "  KB ");
    }

    public static String getProductKey(Context context) {
        String key = context.getPackageName();
        String channel = context.getString(R.string.ua_channel);
        if (channel.equals("gionee")) {
            LogUtils.log(TAG, "ProductKey : " + key);
            return key;
        } else if (channel.equals("aora")) {
            key = key + "." + "channel";
        } else if (channel.equals("360App")) {
            key = key + "." + "360app";
        } else {
            key = key + "." + channel;
        }
        LogUtils.log(TAG, "ProductKey : " + key);
        return key;
    }
}
