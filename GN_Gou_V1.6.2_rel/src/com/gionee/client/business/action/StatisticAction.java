// Gionee <yuwei><2014-1-10> add for CR00821559 begin
/*
 * StatisticAction.java
 * classes : com.gionee.client.business.action.StatisticAction
 * @author yuwei
 * V 1.0.0
 * Create at 2014-1-10 下午4:51:46
 */
package com.gionee.client.business.action;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.gionee.client.business.statistic.GnCountDataHelper;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.Url;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.config.ControlKey;
import com.gionee.framework.model.config.ControlKey.request.control.CacheType;
import com.gionee.framework.operation.business.LocalBuisiness;
import com.gionee.framework.operation.business.PortBusiness;
import com.gionee.framework.operation.business.RequestEntity;

/**
 * StatisticAction
 * 
 * @author yuwei <br/>
 * @date create at 2014-1-10 下午4:51:46
 * @description TODO
 */
public class StatisticAction {
    private static final String TAG = "StatisticAction";
    private GnCountDataHelper mCountDataHelper;

    /**
     * @param context
     * @param name
     * @param key
     * @author yuwei
     * @description TODO 发送统计数据
     */
    public void sendStatisticData(final Context context, final String name, final String key) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        LocalBuisiness.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                mCountDataHelper = new GnCountDataHelper(context);
                final Map<String, String> map = new HashMap<String, String>();
                map.put(name, key);
                map.put(Constants.Statistic.TYPE_NET, AndroidUtils.getSubNetwork(context));
                mCountDataHelper.sendCountData(map);
            }
        });

    }

    /**
     * @param handle
     * @param dataTagetKey
     * @author yuwei
     * @description TODO 获取统计配置信息
     */
    public void getStatisticConfig(IBusinessHandle handle, String dataTagetKey) {
        try {
            RequestEntity request = new RequestEntity(Url.STATISTIC_CONFIG_URL, handle, dataTagetKey);
            MyBean bean = request.getRequestParam();
            bean.put(ControlKey.request.control.__cacheType_enum, CacheType.ShowCacheAndNet);
            PortBusiness.getInstance().startBusiness(request, 0);
        } catch (Exception e) {
            LogUtils.log("start_page", e.getMessage());
            e.printStackTrace();
        }
    }
}
