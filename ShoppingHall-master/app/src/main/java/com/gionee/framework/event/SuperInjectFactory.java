package com.gionee.framework.event;

import android.text.TextUtils;
import android.util.Log;

import com.gionee.framework.model.config.ConfigKey;
import com.gionee.framework.operation.business.NetConnector;
import com.gionee.framework.operation.cache.FNetDiscCacheManage;

/**
 * com.gionee.app_framework.event.inject.SuperInjectFactory
 * 
 * @author yuwei <br/>
 *         create at 2013-3-19 上午11:54:10 TODO
 */
public class SuperInjectFactory {

    /**
     * 
     * @return
     */
    public static INetDiscCacheManage lookupNetCacheManage() {
        Object tag = createByClassName(ConfigKey.NETCACHEMANAGECLASS);
        if (tag != null) {
            return (INetDiscCacheManage) tag;
        }
        return new FNetDiscCacheManage();
    }

    /**
     * 
     * @return
     */
    public static INetConnector lookupNetConnector() {
        return ConfigKey.isUseAnologData() ? new AnologDataConnector() : new NetConnector();
    }

    /**
     * 
     * @param configKeyclazz
     * @return
     */
    public static Object createByClassName(String configKeyclazz) {
        String clazz = configKeyclazz;
        if (!TextUtils.isEmpty(clazz)) {
            try {
                return Class.forName(clazz).newInstance();
            } catch (Exception e) {
            }
        }
        return null;
    }

}
