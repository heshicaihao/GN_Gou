//Gionee <wangyy><2014-03-12> modify for CR00956169 begin
package com.gionee.client.business.statistic.job;

import android.content.Context;

import com.gionee.client.business.statistic.business.DataManager;
import com.gionee.client.business.statistic.business.StatisticsManager;
import com.gionee.client.business.statistic.events.AppEvent;
import com.gionee.client.business.util.LogUtils;

public class HandleAppEventJob extends Job {
    private static final String TAG = "HandleAppEventJob";
    private Context mContext;
    private AppEvent mAppEvent;
    private StatisticsManager mStatisticsManager;
    private DataManager mDataManager;
    private int mCurrentEventNum;

    public HandleAppEventJob(Context context, AppEvent appEvent, int currentEventNum) {
        mContext = context;
        mAppEvent = appEvent;
        mStatisticsManager = StatisticsManager.getInstance(mContext);
        mDataManager = mStatisticsManager.getDataManager();
        mCurrentEventNum = currentEventNum;
        LogUtils.logd(TAG, LogUtils.getThreadName() + "mCurrentEventNum = " + mCurrentEventNum);
    }

    @Override
    public void runTask() {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " save a app event");
        removeEventUploadMessage();
        saveEvent();
        if (canSendEventUploadMessage()) {
            sendStatisticsMessage();
        }
    }

    private void saveEvent() {
        LogUtils.logd(TAG, LogUtils.getThreadName() + mAppEvent.getEventId());
        mDataManager.insertOneAppEvent(mContext, mAppEvent);
        mCurrentEventNum = mDataManager.getAppEventCount();
    }

    private boolean canSendEventUploadMessage() {
        if (mCurrentEventNum < mDataManager.getAppEventCountWhenCheckUpload()) {
            return false;
        }

        if (mStatisticsManager.canUpload()) {
            return true;
        } else {
            return false;
        }
    }

    private void sendStatisticsMessage() {
//        mStatisticsManager.sendEventUploadMessage();
        mStatisticsManager.handleUploadMessage();
    }

    private void removeEventUploadMessage() {
//        mStatisticsManager.removeEventUploadMessage();
    }

    @Override
    protected void releaseResource() {
        mAppEvent = null;
        mContext = null;
        mDataManager = null;
        mStatisticsManager = null;
    }

}
//Gionee <wangyy><2014-03-12> modify for CR00956169 end
