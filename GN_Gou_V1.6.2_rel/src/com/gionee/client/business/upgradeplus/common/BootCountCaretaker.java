package com.gionee.client.business.upgradeplus.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gionee.client.business.util.LogUtils;

public class BootCountCaretaker {

    private static final String PREFERENCES_NAME = "gn_boot_count_db";
    private static final String PREFERENCES_KEY_VERSION_CODE = "version_code";
    private static final String PREFERENCES_KEY_VERSION_NAME = "version_name";
    private static final String PREFERENCES_KEY_BOOT_COUNT = "boot_count";
    private static final String TAG = "BootCountCaretaker";

    private Context mContext;

    public BootCountCaretaker(Context context) {
        this.mContext = context;
    }

    public boolean isFirstBootThisVersion() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        boolean flag = false;
        try {
            // 1.If didn't have any data,means start the version for the first time
            if (!hasData(mContext)) {
                LogUtils.log(TAG, LogUtils.getThreadName() + "## no saved data");
                flag = true;
            }
            // 2.If the version flag changed, means start the version for the first time
            if (isVersionChanged(mContext)) {
                LogUtils.log(TAG, LogUtils.getThreadName() + "## version changed");
                flag = true;
            }
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
        return flag;
    }

    public void increaseBootCount() {
        try {
            BootCountInfo info = getData(mContext);
            long bootTimes = info.getBootTimes();
            if (bootTimes >= Long.MAX_VALUE) {
                bootTimes = 1;
            }
            info.setBootTimes(bootTimes + 1);
            info.setVersionCode(UpgradeUtils.getAppVersionCode(mContext));
            info.setVersionName(UpgradeUtils.getAppVersionName(mContext));
            saveData(info, mContext);
            LogUtils.log(TAG, LogUtils.getThreadName() + info.toString());
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
    }

    public long getBootCounts() {
        BootCountInfo bootCountinfo = getData(mContext);
        return bootCountinfo.getBootTimes();
    }

    private void saveData(BootCountInfo info, Context context) {
        if (null == info) {
            throw new IllegalArgumentException("array is null,or length less than 1");
        }
        if (null == context) {
            throw new IllegalArgumentException("context is null");
        }
        try {
            Editor sharedata = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
            sharedata.putInt(PREFERENCES_KEY_VERSION_CODE, info.getVersionCode());
            sharedata.putLong(PREFERENCES_KEY_BOOT_COUNT, info.getBootTimes());
            sharedata.putString(PREFERENCES_KEY_VERSION_NAME, info.getVersionName());
            sharedata.commit();
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }

    }

    private BootCountInfo getData(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("context is null");
        }
        BootCountInfo info = new BootCountInfo();
        try {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
                    Context.MODE_PRIVATE);
            info.setBootTimes(preferences.getLong(PREFERENCES_KEY_BOOT_COUNT, 0l));
            info.setVersionCode(preferences.getInt(PREFERENCES_KEY_VERSION_CODE, 0));
            info.setVersionName(preferences.getString(PREFERENCES_KEY_VERSION_NAME, ""));
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }

        return info;
    }

    public boolean cleanData(Context context) {
        SharedPreferences sharedata = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedata.edit().clear().commit();
    }

    private boolean hasData(Context context) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
                    Context.MODE_PRIVATE);
            String versionName = preferences.getString(PREFERENCES_KEY_VERSION_NAME, "");
            int versionCode = preferences.getInt(PREFERENCES_KEY_VERSION_CODE, 0);

            LogUtils.log(TAG, LogUtils.getThreadName() + "saved versionName:" + versionName
                    + ",saved versionCode:" + versionCode);

            if (!versionName.equals("") && (versionCode != 0)) {
                return true;
            }

        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
            return false;
        }
        return false;
    }

    public boolean isVersionChanged(Context context) {
        if (!hasData(context)) {
            return true;
        }
        BootCountInfo info = getData(context);
        String cVersionName = UpgradeUtils.getAppVersionName(context);
        int cVersionCode = UpgradeUtils.getAppVersionCode(context);
        boolean isSameCode = (info.getVersionCode() == cVersionCode);
        boolean isSameName = info.getVersionName().equals(cVersionName);
        if (!(isSameCode) || !(isSameName)) {
            // Ensure changed
            return true;
        }
        return false;
    }

}
