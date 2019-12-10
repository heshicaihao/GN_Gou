// Gionee <yuwei><2014-4-1> add for CR00821559 begin
/*
 * GN_DownloadListener.java
 * classes : com.gionee.client.business.appDownload.GN_DownloadListener
 * @author yuwei
 * V 1.0.0
 * Create at 2014-4-1 上午10:14:46
 */
package com.gionee.client.business.appDownload;

import com.gionee.framework.model.bean.MyBean;

/**
 * GN_DownloadListener
 * 
 * @author yuwei <br/>
 * @date create at 2014-4-1 上午11:12:26
 * @description TODO
 */
public interface GNDownloadListener {
    /**
     * @param bean
     * @author yuwei
     * @description TODO 应用状态发生改变
     */
    void onStatusChanged(MyBean bean);

    /**
     * @param bean
     * @author yuwei
     * @description TODO 应用下载进度发生改变
     */
    void onProgressChanged(MyBean bean);

}
//Gionee <yuwei><2014-4-1> add for CR00821559 end