// Gionee <yangxiong><2014-6-10> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-6-10 上午11:18:39
 */
package com.gionee.client.business.statistic;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;

import com.gionee.client.business.statistic.business.Constants;
import com.gionee.client.business.statistic.business.StatisticsManager;
import com.gionee.client.business.statistic.util.Utils;
import com.gionee.client.business.util.LogUtils;

/**
 * com.gionee.client.business.statistic.statisticAgent
 * 
 * @author yangxiong <br/>
 * @date create at 2014-6-10 上午11:18:39
 * @description TODO
 */
public final class StatisticAgent {
    public static final String TAG = StatisticAgent.class.getSimpleName();
    private static AtomicBoolean sInited = new AtomicBoolean(true);

    private static boolean hasInited() {
        return sInited.get();
    }

//    private static void setInited() {
//        sInited.set(true);
//    }

    /**
     * 自定义事件接口 1
     */
    public static void onEvent(Context context, String eventId) {
        onEvent(context, eventId, "");
    }

    /**
     * 自定义事件接口 2
     */
    public static void onEvent(Context context, String eventId, String eventLabel) {
        onEvent(context, eventId, eventLabel, null);
    }

    /**
     * 自定义事件接口 3
     */
    public static void onEvent(final Context context, final String eventId, final String eventLabel,
            final Map<String, Object> map) {
        throwExceptionIfNotInit();
        LogUtils.logd(TAG, LogUtils.getThreadName());
        int needLength = Constants.DefaultSDKConfig.CFG_DEFAULT_MAX_STRING_LENGTH;
        StatisticsManager.getInstance(context.getApplicationContext()).handlerAppEventId(
                Utils.subStringIfNeeded(eventId, needLength),
                Utils.subStringIfNeeded(eventLabel, needLength), Utils.getProperStringFromMap(map),
                System.currentTimeMillis());
    }

    private static void throwExceptionIfNotInit() {
        if (hasInited()) {
            return;
        }
        throw new RuntimeException("statistics not initialized.");
    }
}
