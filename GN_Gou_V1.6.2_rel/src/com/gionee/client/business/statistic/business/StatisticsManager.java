// Gionee <yangxiong><2014-6-10> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-6-10 下午02:07:33
 */
package com.gionee.client.business.statistic.business;

import android.content.Context;

import com.gionee.client.business.statistic.GnCountDataHelper;
import com.gionee.client.business.statistic.database.LocalDatabaseHelper;
import com.gionee.client.business.statistic.events.AppEvent;
import com.gionee.client.business.statistic.job.HandleAppEventJob;
import com.gionee.client.business.statistic.transmit.NetTransmitManager;
import com.gionee.client.business.statistic.util.NetworkUtils;
import com.gionee.client.business.statistic.util.Utils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.framework.operation.business.LocalBuisiness;

/**
 * com.gionee.client.business.statistic.business.StatisticsManager
 * 
 * @author yangxiong <br/>
 * @date create at 2014-6-10 下午02:07:33
 * @description TODO
 */
public class StatisticsManager {
    private static final String TAG = "StatisticsManager";
    private Context mContext;
    private static StatisticsManager sStatisticsManager;
    private LocalPreferenceManager mLocalPreferenceManager;
    private DataManager mDataManager;
    private NetTransmitManager mNetTransmitManager;

    public StatisticsManager(Context context) {
        super();
        this.mContext = context.getApplicationContext();
        initLocalDatabaseHelper();
        mLocalPreferenceManager = new LocalPreferenceManager(mContext);
        initAllManagers();
    }

    public static synchronized StatisticsManager getInstance(Context context) {
        if (sStatisticsManager == null) {
            sStatisticsManager = new StatisticsManager(context);
        }

        return sStatisticsManager;
    }

    public LocalPreferenceManager getLocalPreferenceManager() {
        return mLocalPreferenceManager;
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public void handlerAppEventId(final String eventId, final String eventLabel, final String paramMap,
            long occurTime) {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        if (Utils.isStringNull(eventId)) {
            return;
        }
        AppEvent event = new AppEvent(eventId, eventLabel, occurTime, paramMap);
        HandleAppEventJob handleAppEventJob = new HandleAppEventJob(mContext, event,
                mDataManager.getAppEventCount());
        LocalBuisiness.getInstance().getHandler().post(handleAppEventJob);
    }

    public boolean canUpload() {
        if (NetworkUtils.isNetworkNotAvailable(mContext)) {
            LogUtils.logd(TAG, "network isn't valid!");
            return false;
        }
        boolean isLoad = GnCountDataHelper.getStatisticSwitch();
//        if (Utils.IS_GIONEE_ROM) {
//            if (ismAssociateUserImprovementPlan()) {
//                if (isUserImprovementEnabled()) {
//
//                    return true;
//                } else {
//                    LogUtils.logi(TAG, "user improvement disabled, can not upload");
//                    return false;
//                }
//            }
//        }
        return isLoad;
    }

    private void initLocalDatabaseHelper() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        LocalDatabaseHelper.getInstance(mContext);
    }

    private void initAllManagers() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        mDataManager = new DataManager(mContext, mLocalPreferenceManager);
        mNetTransmitManager = new NetTransmitManager();
    }

    public void handleHasNoUpdateMessage() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        doAfterUploadSuccess();
    }

    public void handleUploadMessage() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        mLocalPreferenceManager.resetAllDataInfoIfNextDay();
        mNetTransmitManager.sendStatisticsInfos(mDataManager, null);
    }

    public void handleHasUpdateMessage() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        doAfterUploadSuccess();
    }

    public void doAfterUploadSuccess() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        mLocalPreferenceManager.setDataInfoIfSuccess();
        mDataManager.deleteAppEventByMaxRowId();
    }

    public void doAfterUploadFailed() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        mLocalPreferenceManager.resetDataInfoGotten();
    }

    public void doUpdateRules() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        mDataManager.getUploadCfg();
    }
}
