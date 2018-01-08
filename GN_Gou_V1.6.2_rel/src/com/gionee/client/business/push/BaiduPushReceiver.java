package com.gionee.client.business.push;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.gionee.client.GNSplashActivity;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;

public class BaiduPushReceiver extends FrontiaPushMessageReceiver {

    public static final String PUSH_STYLE_BAIDU = "push_style_baidu";
    private static final String TAG = "BaiduPushReceiver";

    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId,
            String requestId) {
        LogUtils.log(TAG, LogUtils.getFunctionName() + "  errorCode ==" + errorCode + "  appid==" + appid
                + "  userId" + userId + "  channelId" + channelId + " requestId" + requestId);
        try {
            if (errorCode == 0) {
                BaiduPushUtils.setBind(context, true);
                LogUtils.log(TAG, " baidu_aps" + PushAssist.isRegisterAps(context, Constants.Push.BAIDU_APS));
                if (PushAssist.isRegisterAps(context, Constants.Push.BAIDU_APS)) {
                    return;
                }
                sendPushInfo(context, userId, channelId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPushInfo(Context context, String userId, String channelId) {
        if (userId.length() > 10 && channelId.length() > 10) {
            PushAssist.writeRid(context, Constants.Push.USER_ID, userId);
            PushAssist.writeRid(context, Constants.Push.CHANNEL_ID, channelId);
            new InfoSendThread(context, userId, channelId).start();
        }
    }

    @Override
    public void onDelTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {

    }

    @Override
    public void onListTags(Context arg0, int arg1, List<String> arg2, String arg3) {

    }

    @Override
    public void onMessage(Context context, String message, String customContentString) {
        LogUtils.log(TAG, "message : " + message + "  customContentString:" + customContentString);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (!BaiduPushUtils.getPushSwich(context)) {
            return;
        }
        if (!isNotification(message)) {
            return;
        }

        new PushNotificationTask(context, message, message).execute();
    }

    private boolean isNotification(String customContentString) {
        JSONObject customJson = null;
        try {
            customJson = new JSONObject(customContentString);
            if (!customJson.isNull(Constants.Push.NOTIFICATION)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public void onNotificationClicked(Context context, String title, String description,
            String customContentString) {

        LogUtils.log(TAG, LogUtils.getFunctionName() + " title:" + title + " description:" + description
                + " customContentString:" + customContentString);

        Intent mIntent = new Intent();

        if (AndroidUtils.isRunningForeground(context)) {
            mIntent.setClass(context, GnHomeActivity.class);
        } else {
            mIntent.setClass(context, GNSplashActivity.class);
        }
        if (!TextUtils.isEmpty(customContentString)) {
            mIntent.putExtra(Constants.Push.DATA, customContentString);
        }
        if (AndroidUtils.getAndroidSDKVersion() >= 11) {
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra(Constants.Push.SOURCE, PUSH_STYLE_BAIDU);
        context.startActivity(mIntent);

    }

    @Override
    public void onSetTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {

    }

    @Override
    public void onUnbind(Context context, int errorCode, String arg2) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        try {
            if (errorCode == 0) {
                BaiduPushUtils.setBind(context, false);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationArrived(Context context, String title, String description,
            String customContentString) {
        // TODO Auto-generated method stub
        LogUtils.log(TAG, LogUtils.getFunctionName() + " title" + title + " description" + description
                + " customContentString" + customContentString);
    }

}
