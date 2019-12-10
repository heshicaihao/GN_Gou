// Gionee <yuwei><2013-12-19> add for CR00821559 begin
/*
 * HttpUrlHandler.java
 * classes : com.gionee.client.business.filter.HttpUrlHandler
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-19 下午3:20:32
 */
package com.gionee.client.business.filter;

import android.app.Activity;

/**
 * HttpUrlHandler
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-19 下午3:20:32
 * @description TODO 处理http url
 */
public class HttpUrlHandler extends IUrlHandler {

    @Override
    public boolean handleRequest(Activity context, String url) {
        for (UrlFilterConfig.HttpUrlEnum urlEnum : UrlFilterConfig.HttpUrlEnum.values()) {
            if (url.contains(urlEnum.getValue())) {
                return false;
            }
        }
        return true;
    }
}
//Gionee <yuwei><2013-12-19> add for CR00821559 end