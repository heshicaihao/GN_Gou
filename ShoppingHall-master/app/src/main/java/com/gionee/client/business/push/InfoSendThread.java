package com.gionee.client.business.push;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;

import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.UAUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.Url;

public class InfoSendThread extends Thread {
    private static final int MAX_SEND_INFO_COUNT = 3;
    private static final int DELAY_TIME = 10 * 60 * 1000;
    private static final String TAG = "InfoSendThread";
    private static int sCount = 0;
    private WeakReference<Context> mContextRef;
    private Timer mTimer;
    private APSTimerTask mTimerTask;
    private int mTaskCount = 0;
    private String mUserId;
    private String mChannelId;

    public InfoSendThread(Context context, String userId, String channelId) {
        mContextRef = new WeakReference<Context>(context);
        mUserId = userId;
        mChannelId = channelId;
    }

    @Override
    public void run() {
        Context context = mContextRef.get();
        if (context == null) {
            return;
        }
        
        sendBaiduInfoToServer(context, mUserId, mChannelId);
    }

    private void sendBaiduInfoToServer(Context context, String userID, String channelID) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        LogUtils.log(TAG, "APSurl: " + Url.GET_BAIDU_PUSH_INFO_URL);
        List<NameValuePair> postParam = new ArrayList<NameValuePair>(2);
        postParam.add(new BasicNameValuePair(Constants.Push.USER_ID, userID));
        postParam.add(new BasicNameValuePair(Constants.Push.CHANNEL_ID, channelID));
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Url.GET_BAIDU_PUSH_INFO_URL);
        httpPost.setHeader("User-Agent", UAUtils.getUserAgent(context));
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(postParam, "UTF-8");
            httpPost.setEntity(httpEntity);

            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject js = new JSONObject(result);
                LogUtils.log(TAG, "result: " + result.trim() + js.optBoolean("success"));
                if (js.optBoolean(Constants.Push.SUCCESS)) {
                    PushAssist.writeAps(context, Constants.Push.BAIDU_APS, true);
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer.purge();
                    }
                    if (null != mTimerTask) {
                        mTimerTask.cancel();
                    }
                }
            } else {
                sCount++;
                if (sCount < MAX_SEND_INFO_COUNT) {
                    sendBaiduInfoToServer(context, mUserId, mChannelId);
                }
                if (mTimer == null) {
                    mTimer = new Timer(false);
                }
                mTimerTask = new APSTimerTask();
                mTimer.schedule(mTimerTask, DELAY_TIME);
                PushAssist.writeAps(context, Constants.Push.BAIDU_APS, false);
                LogUtils.log(TAG, "false");
            }
            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class APSTimerTask extends TimerTask {

        private static final int MAX_COUNT = 6;

        @Override
        public void run() {
            mTaskCount++;
            if (mTaskCount > MAX_COUNT) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                this.cancel();
            } else {
                Context context = mContextRef.get();
                if (context == null) {
                    return;
                }
                sendBaiduInfoToServer(context, mUserId, mChannelId);
            }

        }

    }

}
