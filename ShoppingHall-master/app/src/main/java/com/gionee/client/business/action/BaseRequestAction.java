// Gionee <yuwei><2014-9-15> add for CR00821559 begin
/*
 * BaseRequestAction.java
 * classes : com.gionee.client.business.action.BaseRequestAction
 * @author yuwei
 * V 1.0.0
 * Create at 2014-9-15 下午3:01:45
 */
package com.gionee.client.business.action;

import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.config.ControlKey;
import com.gionee.framework.model.config.ControlKey.request.control.CacheType;
import com.gionee.framework.operation.business.PortBusiness;
import com.gionee.framework.operation.business.RequestEntity;

/**
 * com.gionee.client.business.action.BaseRequestAction
 * 
 * @author yuwei <br/>
 * @date create at 2014-9-15 下午3:01:45
 * @description TODO
 */
public class BaseRequestAction {

    public BaseRequestAction() {
        super();
    }

    /**
     * @param handle
     * @param dataTargetKey
     * @author yuwei
     * @description TODO
     */
    protected void startNoParamsGetRequest(String url, IBusinessHandle handle, String dataTargetKey,
            boolean isUseCache) {
        try {
            RequestEntity request;
            MyBean bean;
            if (isUseCache) {
                request = new RequestEntity(url, handle, dataTargetKey);
                bean = request.getRequestParam();
                bean.put(ControlKey.request.control.__cacheType_enum, CacheType.ShowCacheAndNet);

            } else {
                request = new RequestEntity(url, handle, dataTargetKey);
                bean = request.getRequestParam();
                bean.put(ControlKey.request.control.__cacheType_enum, CacheType.noneCache);
            }
            bean.put(ControlKey.request.control.__method_s, "GET");
            PortBusiness.getInstance().startBusiness(request, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void startSessionGetRequest(String url, IBusinessHandle handle, String dataTargetKey,
            boolean isUseCache, int session) {
        try {
            RequestEntity request;
            MyBean bean;
            if (isUseCache) {
                request = new RequestEntity(url, handle, dataTargetKey);
                bean = request.getRequestParam();
                bean.put(ControlKey.request.control.__cacheType_enum, CacheType.ShowCacheAndNet);

            } else {
                request = new RequestEntity(url, handle, dataTargetKey);
                bean = request.getRequestParam();
                bean.put(ControlKey.request.control.__cacheType_enum, CacheType.noneCache);
            }
            bean.put(ControlKey.request.control.__method_s, "GET");
            PortBusiness.getInstance().startBusiness(request, session);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void startNoParamsGetRequest(String url, IBusinessHandle handle, String dataTargetKey) {
        startNoParamsGetRequest(url, handle, dataTargetKey, true);
    }

    protected void startGetRequest(String url, IBusinessHandle handle, String dataTargetKey,
            boolean isUseCache, MyBean otherParameters) {
        try {
            RequestEntity request;
            MyBean bean;
            if (isUseCache) {
                request = new RequestEntity(url, handle, dataTargetKey);
                bean = request.getRequestParam();
                bean.put(ControlKey.request.control.__cacheType_enum, CacheType.ShowCacheAndNet);
                bean.putAll(otherParameters);
            } else {
                request = new RequestEntity(url, handle);
                bean = request.getRequestParam();
                bean.put(ControlKey.request.control.__cacheType_enum, CacheType.noneCache);
                bean.putAll(otherParameters);
            }
            bean.put(ControlKey.request.control.__method_s, "GET");
            PortBusiness.getInstance().startBusiness(request, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}