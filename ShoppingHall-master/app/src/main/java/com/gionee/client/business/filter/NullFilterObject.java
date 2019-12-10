// Gionee <yuwei><2013-12-23> add for CR00821559 begin
/*
 * NullFilterObject.java
 * classes : com.gionee.client.business.filter.NullFilterObject
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-23 下午3:01:54
 */
package com.gionee.client.business.filter;

import android.app.Activity;

/**
 * NullFilterObject
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-23 下午3:01:54
 * @description TODO
 */
public class NullFilterObject extends IUrlHandler {

    @Override
    public boolean handleRequest(Activity context, String url) {
        return false;
    }
}
