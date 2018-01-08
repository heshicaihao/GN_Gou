package com.gionee.client.business.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gionee.client.GNSplashActivity;
import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;

public class PushReceiver extends BroadcastReceiver {

    private static final String TAG = "GOUPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.log(TAG, "onReceive");
        String action = intent.getAction();

        if ("com.gionee.cloud.intent.REGISTRATION".equals(action)) {
            LogUtils.log(TAG, "onReceive rid = " + intent.getStringExtra("registration_id"));
            if ("com.gionee.cloud.intent.RECEIVE".equals(action)) {
                String message = intent.getStringExtra("message");
                ReceiverNotifier.getInstance().notifyMessage(message);
                showInNotification(context, message);
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                ConnectivityManager connManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiNetInfo.isConnected() || mobNetInfo.isConnected()) {
                    if (!PushAssist.isRegisterAps(context, Constants.Push.BAIDU_APS)) {
                        new InfoSendThread(context, PushAssist.readData(context, Constants.Push.USER_ID),
                                PushAssist.readData(context, Constants.Push.CHANNEL_ID));
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void showInNotification(Context context, String message) {
        MsgInfo msgInfo = paraseMsgInfo(message);
        String title = "";
        title = msgInfo.mTitle;
        LogUtils.log(TAG, "message:" + message);
        LogUtils.log(TAG, msgInfo.toString());
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        notification.icon = R.drawable.ic_launcher;
        notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND
                | Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.when = System.currentTimeMillis();
        notification.tickerText = title;
        Intent intent = new Intent(context, GNSplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (msgInfo.mUrl != null) {
            intent.putExtra(Constants.Home.KEY_INTENT_URL, msgInfo.mUrl);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, msgInfo.mContent, contentIntent);

        notificationManager.notify(R.string.app_name, notification);
    }

    MsgInfo paraseMsgInfo(String jsonData) {
        MsgInfo msgInfo = null;
        if (jsonData != null) {
            msgInfo = new MsgInfo();
            try {
                JSONObject jsonRoot = new JSONObject(jsonData);
                // title
                msgInfo.mTitle = jsonRoot.getString("title");
                // content
                msgInfo.mContent = jsonRoot.getString("content");
                // ids
                msgInfo.mUrl = jsonRoot.getString("url");

            } catch (JSONException e) {
                LogUtils.loge(TAG, "" + e);
            }
        }
        return msgInfo;
    }

    static class MsgInfo {
        public String mTitle;
        public String mContent;
        public String mUrl;

        @Override
        public String toString() {
            return "Title:" + mTitle + "\nContent:" + mContent + "\nURL:" + mUrl;
        }
    }
}
