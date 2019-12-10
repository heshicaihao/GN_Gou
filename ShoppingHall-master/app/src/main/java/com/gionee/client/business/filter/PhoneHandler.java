// Gionee <yuwei><2013-12-19> add for CR00821559 begin
/*
 * PhoneHandler.java
 * classes : com.gionee.client.business.filter.PhoneHandler
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-19 下午3:35:35
 */
package com.gionee.client.business.filter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

/**
 * PhoneHandler
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-19 下午3:35:35
 * @description TODO 处理电话url
 */
public class PhoneHandler extends IUrlHandler {

    @Override
    public boolean handleRequest(Activity context, String url) {
        if (url.startsWith(WebView.SCHEME_TEL)) {
            gotoTelPage(context, url);
            return true;
        }
        return mSuccessor.handleRequest(context, url);
    }

    /**
     * @param url
     */
    private void gotoTelPage(Activity context, String url) {
        String number = "";
        if (url.contains("?")) {
            number = url.substring(WebView.SCHEME_TEL.length(), url.indexOf("?") + 1);
        } else {
            number = url.substring(WebView.SCHEME_TEL.length());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebView.SCHEME_TEL + number));
        context.startActivity(intent);
    }
}
//Gionee <yuwei><2013-12-19> add for CR00821559 end