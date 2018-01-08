// Gionee <yuwei><2014-10-28> add for CR00821559 begin
/*
 * IUpdateListener.java
 * classes : com.gionee.client.business.upgradeplus.IUpdateListener
 * @author yuwei
 * V 1.0.0
 * Create at 2014-10-28 下午3:48:49
 */
package com.gionee.client.business.upgradeplus;

/**
 * com.gionee.client.business.upgradeplus.IUpdateListener
 * 
 * @author yuwei <br/>
 * @date create at 2014-10-28 下午3:48:49
 * @description TODO
 */
public interface IUpdateListener {
    void onChecking();

    void onCheckComplete();

    void onNewestVersion();
}
