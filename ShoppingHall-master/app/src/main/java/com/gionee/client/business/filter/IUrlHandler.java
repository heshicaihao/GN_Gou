// Gionee <yuwei><2013-12-19> add for CR00821559 begin
/*
 * IUrlHandler.java
 * classes : com.gionee.client.business.filter.IUrlHandler
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-19 下午3:10:44
 */
package com.gionee.client.business.filter;

import android.app.Activity;

import com.gionee.client.business.util.LogUtils;

/**
 * IUrlHandler
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-19 下午3:10:44
 * @description TODO
 */
public abstract class IUrlHandler {
    /**
     * 持有后继的责任对象
     */
    protected IUrlHandler mSuccessor;

    /**
     * 对url进行处理
     */
    public abstract boolean handleRequest(Activity context, String url);

    public IUrlHandler getSuccessor() {
        return mSuccessor;
    }

    public void setSuccessor(IUrlHandler successor) {
        LogUtils.log("WebFilterChainManager", successor.getClass().getName());
        this.mSuccessor = successor;
    }
}
//Gionee <yuwei><2013-12-19> add for CR00821559 end