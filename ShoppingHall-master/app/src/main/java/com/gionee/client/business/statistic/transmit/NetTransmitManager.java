//Gionee <wangyy><2013-11-22> modify for CR00956169 begin
package com.gionee.client.business.statistic.transmit;

import android.content.Context;

import com.gionee.client.business.statistic.business.DataManager;
import com.gionee.client.business.statistic.job.SendDataJob;
import com.gionee.framework.operation.business.StatisticsBuisiness;

public class NetTransmitManager {
    public static final String TAG = "NetTransmitManager";

    public void sendStatisticsInfos(DataManager manager, Context context) {
        SendDataJob sendDataJob = new SendDataJob(manager, context);
        StatisticsBuisiness.getInstance().getHandler().post(sendDataJob);
    }

}
//Gionee <wangyy><2013-11-22> modify for CR00956169 end