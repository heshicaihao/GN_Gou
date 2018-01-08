package com.gionee.client.business.statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gionee.client.business.util.LogUtils;

public class UrlCollector {

    private static final String TAG = UrlCollector.class.getSimpleName();
    private static final String MAP_KEY_URL = "Url";
    private static final String MAP_KEY_START_TIME = "Startime";
    private static final String MAP_KEY_ENT_TIME = "Endtime";

    private static UrlCollector sInstance;
    private long mStarTime;
    private long mEndTime;
    private List<UrlSkipBean> mUrlSkipBeans;

    private UrlCollector() {
    }

    public static synchronized UrlCollector getInstance() {
        if (sInstance == null) {
            sInstance = new UrlCollector();
        }
        return sInstance;
    }

    public void endCollect() {
        // 屏蔽收集和结束收集两个方法，关闭url收集功能
        /* LogUtils.log(TAG,
                LogUtils.getThreadName() + " endCollect: switch = " + GnCountDataHelper.getStatisticSwitch()
                        + ", mUrlSkipBeans.size()" + (mUrlSkipBeans == null ? 0 : mUrlSkipBeans.size()));
        if (mUrlSkipBeans != null && mUrlSkipBeans.size() > 0) {
            if (GnCountDataHelper.getStatisticSwitch()) {
                mEndTime = System.currentTimeMillis();
                onRecord();
            } else {
                mUrlSkipBeans.clear();
                mUrlSkipBeans = null;
            }
        }*/
    }

    public void collect(String url) {
        // 屏蔽收集和结束收集两个方法，关闭url收集功能
        /*
         *UrlSkipBean urlSkipBean = new UrlSkipBean(url);
         *collect(urlSkipBean);
         */
    }

    public void collect(UrlSkipBean urlSkipBean) {
        LogUtils.log(TAG,
                LogUtils.getThreadName() + " collect: switch = " + GnCountDataHelper.getStatisticSwitch()
                        + " urlSkipBean.url" + urlSkipBean.getUrl());
        if (!check(urlSkipBean)) {
            return;
        }
        if (mUrlSkipBeans == null) {
            mUrlSkipBeans = new ArrayList<UrlSkipBean>();
            mStarTime = System.currentTimeMillis();
        }
        mUrlSkipBeans.add(urlSkipBean);
    }

//    private void onRecord() {
//        String eventId = "url";
//        String eventLabel = "";
////        Map<String, Object> map = getRecordMap();
////        StatisticAgent.onEvent(GNApplication.getInstance(), eventId, eventLabel, map);
//    }

    @SuppressWarnings("unused")
    private Map<String, Object> getRecordMap() {
        List<UrlSkipBean> srcData = mUrlSkipBeans;
        Map<String, Object> recordMap = new HashMap<String, Object>();
        recordMap.put(MAP_KEY_START_TIME, mStarTime);
        recordMap.put(MAP_KEY_ENT_TIME, mEndTime);
        mUrlSkipBeans = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < srcData.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(srcData.get(i).getUrl());
        }
        recordMap.put(MAP_KEY_URL, sb.toString());
        return recordMap;
    }

    private boolean check(UrlSkipBean urlSkipBean) {
        boolean isSwitch = GnCountDataHelper.getStatisticSwitch();
        return isSwitch;
    }
}
