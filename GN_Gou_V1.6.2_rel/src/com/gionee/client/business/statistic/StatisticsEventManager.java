package com.gionee.client.business.statistic;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.LogUtils;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;

/**
 * 购物大厅自己的数据统计管理类
 * 
 * @author yangxiong
 */
public class StatisticsEventManager implements IStatisticsEventManager {
    private static final String TAG = StatisticsEventManager.class.getSimpleName();
    private MyBean mEventIdBean = MyBeanFactory.createEmptyBean();
    private Context mContext;
    private static final String STATISTICS_KEY = "statistics_event_id";

    public StatisticsEventManager(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public void add(String eventId) {
        Integer count = mEventIdBean.getInt(eventId);
        mEventIdBean.put(eventId, ++count);
    }

    @Override
    public String buildStatisticsData() {
        JSONObject eventData = new JSONObject(mEventIdBean);
        return eventData.toString();
    }

    @Override
    public void saveStatisticsData() {
        String data = buildStatisticsData();
        LogUtils.logd(TAG, LogUtils.getThreadName() + " data = " + data);
        ShareDataManager.putString(mContext, STATISTICS_KEY, data);
    }

    @Override
    public void removeStatisticsData() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        ShareDataManager.removeReferece(mContext, STATISTICS_KEY);
    }

    @Override
    public void initStatisticsData() {
        String data = ShareDataManager.getString(mContext, STATISTICS_KEY, "");
        LogUtils.logd(TAG, LogUtils.getThreadName() + " data = " + data);
        if (TextUtils.isEmpty(data)) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + " data is empty");
            return;
        }
        try {
            JSONObject eventData = new JSONObject(data);
            Iterator<?> it = eventData.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                int value = eventData.optInt(key);
                mEventIdBean.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
