package com.gionee.client.business.push;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.baidu.mobstat.StatService;
import com.gionee.client.GNSplashActivity;
import com.gionee.client.R;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;

public class PushNotificationTask extends AsyncTask<Void, Void, Void> {
    public static final String PUSH_STYLE_BAIDU_LOCAL = "push_style_local";
    private static final String TAG = "PushNotificationTask";
    private int mStyle = 0;
    private Context mContext;
    private String mTitle;
    private String mMsg;
    private String mPictureUrl;
    private String mData;
    private String mPushID;
    private boolean mIsMsg;

    public PushNotificationTask(Context context, String data, String msg) {
        mContext = context;
        mData = data;
        parseData(data);
    }

    private void parseData(String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        try {
            JSONObject object = new JSONObject(data);
            mTitle = object.optString(Constants.Push.TITLE);
            mStyle = object.optInt(Constants.Push.STYLE);
            mPictureUrl = object.optString(Constants.Push.BIG_PICTURE);
            mPushID = object.optString(Constants.Push.ID);
            mMsg = object.optString(Constants.Push.DESCRIPTION);
            mIsMsg = object.optBoolean(Constants.Push.IS_MSG);
            addPushStat(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addPushStat(JSONObject object) {
        if (!TextUtils.isEmpty(mPushID)) {
            StatService.onEvent(mContext, BaiduStatConstants.PUSH_GET, mPushID);
        }
        try {
            String action = object.optString(Constants.Push.ACTIION);
            if (!TextUtils.isEmpty(action)
                    && Constants.BannerAction.BARGAIN_GAME_PAGE.getValue().equals(action)) {
                StatService.onEvent(mContext, BaiduStatConstants.SURPASS_PUSH_SHOW, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        if (!isNeedShowNotification()) {
            return null;
        }
        Notification notification = new Notification();
        switch (mStyle) {
            case Constants.Push.Normal:
                notification = creatNormalNotification();
                break;
            case Constants.Push.CUSTOM_VIEW:
                notification = creatCutomViewNotification();
                break;
            case Constants.Push.BIG_TEXT_STYLE:
                notification = creatBigTextNotification();
                break;
            case Constants.Push.BIG_PICTURE_STYLE:
                notification = creatBigPictureNotification();
                break;
            default:
                break;

        }

        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int) System.currentTimeMillis(), notification);
        return null;
    }

    private boolean isNeedShowNotification() {
        if (!mIsMsg) {
            return true;
        }
        if (isRunningForeground()) {
            return false;
        }
        return true;
    }

    private boolean isRunningForeground() {
        String packageName = mContext.getPackageName();
        String topActivityClassName = getTopActivityName(mContext);
        LogUtils.log(TAG, "packageName=" + packageName + ",topActivityClassName=" + topActivityClassName);
        if (packageName != null && topActivityClassName != null
                && topActivityClassName.startsWith(packageName)) {
            LogUtils.log(TAG, "---> isRunningForeGround");
            return true;
        }
        LogUtils.log(TAG, "---> isRunningBackGround");
        return false;

    }

    public String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager) (context
                .getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }

    private Notification creatBigPictureNotification() {
        Bitmap remotePicture = null;

        try {
            if (!TextUtils.isEmpty(mPictureUrl)) {
                remotePicture = getPictureBitmap(mPictureUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (remotePicture == null) {
            return creatNormalNotification();
        }
        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.bigPicture(remotePicture);
        notiStyle.setSummaryText(mMsg);
        Intent resultIntent = initIntentData();

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

        stackBuilder.addParentStack(GNSplashActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(mStyle,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true).setContentIntent(resultPendingIntent).setContentTitle(mTitle)
                .setContentText(mMsg).setStyle(notiStyle).build();
    }

    private Bitmap getPictureBitmap(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpStatus.SC_OK) {
                return BitmapFactory.decodeStream(new FlushedInputStream(connection.getInputStream()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Notification creatBigTextNotification() {

        NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
        notiStyle.bigText(mMsg);
        Intent resultIntent = initIntentData();

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

        stackBuilder.addParentStack(GNSplashActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(mStyle,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(mContext).setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher).setContentIntent(resultPendingIntent)
                .setContentTitle(mTitle).setContentText(mMsg).setStyle(notiStyle).build();
    }

    @SuppressLint("NewApi")
    private Notification creatCutomViewNotification() {
        Intent resultIntent = initIntentData();
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, mStyle, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews contentViews = new RemoteViews(mContext.getPackageName(),
                R.layout.notification_custom_builder);
        contentViews.setImageViewResource(R.id.notification_icon, R.drawable.ic_launcher);
        contentViews.setTextViewText(R.id.notification_title, mTitle);
        contentViews.setTextViewText(R.id.notification_text, mMsg);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(
                R.drawable.ic_launcher).setContentTitle(mMsg);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContent(contentViews);
        mBuilder.setAutoCancel(true);

        Notification notification = mBuilder.build();
        notification.contentView = contentViews;
        return notification;
    }

    private Notification creatNormalNotification() {

        Intent intent = initIntentData();
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, mStyle, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true).setContentIntent(contentIntent).setContentTitle(mTitle)
                .setContentText(mMsg).build();
    }

    @SuppressLint("InlinedApi")
    private Intent initIntentData() {
        Intent intent = new Intent();
        if (isRunningForeground()) {
            intent.setClass(mContext, GnHomeActivity.class);
        } else {
            intent.setClass(mContext, GNSplashActivity.class);
        }
        if (AndroidUtils.getAndroidSDKVersion() >= 11) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!TextUtils.isEmpty(mData)) {
            intent.putExtra(Constants.Push.DATA, mData);
        }
        intent.putExtra(Constants.Push.SOURCE, PUSH_STYLE_BAIDU_LOCAL);
        return intent;
    }

}
