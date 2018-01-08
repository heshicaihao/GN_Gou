// Gionee <yuwei><2013-10-17> add for CR00821559 begin
/*
 * IBaseFragment.java
 * classes : com.gionee.poorshopping.activity.base.IBaseFragment
 * @author yuwei
 * V 1.0.0
 * Create at 2013-10-17 下午3:57:16
 */
package com.gionee.client.activity.base;

/**
 * IBaseFragment
 * 
 * @author yuwei <br/>
 * @date create at 2013-10-17 下午3:57:16
 * @description TODO
 */
public interface IBaseFragment {

    /**
     * 重新加载
     * 
     * @author yuwei
     * @description TODO
     */
    public void reload(boolean isReload);

    /**
     * 关闭加载进度条
     * 
     * @author yuwei
     * @description TODO
     */
    public void dismissProgress();

    /**
     * @param url
     * @author yuwei
     * @description TODO 设置URL
     */
    public void setWebUrl(String url);

    public void onPageVisible();

    public void onPageInvisible();
}
