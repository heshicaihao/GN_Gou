package com.gionee.client.business.push;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gionee.client.business.util.LogUtils;

public class PushAssist {

    private static final String TAG = "PushAssist";
    private static final String WRONG_RID = "-1";
    private static final String FILE_NAME = "org.gionee.gou.client";
    private Context mContext;

    public PushAssist(Context context) {
        mContext = context;
    }

    public void registerPushRid() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        // 第一步,取出自己保存的RID
        String rid = readRid(mContext);
        LogUtils.log(TAG, LogUtils.getThreadName() + "rid:" + rid);
        // 第二步,检查自己的RID是否正确
        if (rid.equals(WRONG_RID)) {
            // 2.1说明还没有注册或者没注销掉了,那么直接注册即可
            registerRid();
        } else {
            // 2.2如果有RID,没有问题,可以根据自己应用的情况做些事情,例如检查是否已经注册自己应用的APS
            ReceiverNotifier.getInstance().notifyRidGot(rid);
        }
    }

    private void registerRid() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        Intent intent = new Intent().setAction("com.gionee.cloud.intent.REGISTER").putExtra("packagename",
                mContext.getPackageName());
        intent.setPackage("com.gionee.cloud.gpe");
        mContext.startService(intent);
    }

    public static String readRid(Context mContext) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        String rid = null;
        SharedPreferences mPreference = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        rid = mPreference.getString("rid", "-1");
        return rid;
    }

    public static void writeRid(Context mContext, String key, String value) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        SharedPreferences mPreference = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Editor mEditor = mPreference.edit();
        mEditor.putString(key, value);
        mEditor.commit();

    }

    public static String readData(Context mContext, String key) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        String rid = null;
        SharedPreferences mPreference = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        rid = mPreference.getString(key, null);
        return rid;
    }

    public static boolean isRegisterAps(Context mContext, String key) {
        boolean registAps;
        SharedPreferences mPreference = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        registAps = mPreference.getBoolean(key, false);
        LogUtils.log(TAG, LogUtils.getThreadName() + "key:" + registAps);
        return registAps;
    }

    public static void writeAps(Context mContext, String key, boolean value) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "key:" + value);
        SharedPreferences mPreference = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Editor mEditor = mPreference.edit();
        mEditor.putBoolean(key, value);
        mEditor.commit();

    }
}
