// Gionee <yuwei><2013-12-20> add for CR00821559 begin
/*
 * BlackListHanlder.java
 * classes : com.gionee.client.business.filter.BlackListHanlder
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-20 上午10:55:11
 */
package com.gionee.client.business.filter;

import android.app.Activity;

/**
 * BlackListHanlder
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-20 上午10:55:11
 * @description TODO
 */
public class BlackListHanlder extends IUrlHandler {

    @Override
    public boolean handleRequest(Activity context, String url) {
        for (UrlFilterConfig.BlackListUrlEnum blackUrl : UrlFilterConfig.BlackListUrlEnum.values()) {
            if (url.contains(blackUrl.getValue())) {
                return true;
            }
        }
        return mSuccessor.handleRequest(context, url);
    }
}
