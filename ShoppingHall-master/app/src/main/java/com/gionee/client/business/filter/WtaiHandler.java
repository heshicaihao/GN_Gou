// Gionee <yuwei><2013-12-19> add for CR00821559 begin
/*
 * WtaiHandler.java
 * classes : com.gionee.client.business.filter.WtaiHandler
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-19 下午4:28:02
 */
package com.gionee.client.business.filter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import com.gionee.client.model.Constants;

/**
 * WtaiHandler
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-19 下午4:28:02
 * @description TODO
 */
public class WtaiHandler extends IUrlHandler {

    @Override
    public boolean handleRequest(Activity context, String url) {
        if (url.startsWith(Constants.SCHEME_WTAI)) {
            if (url.startsWith(Constants.SCHEME_WTAI_MC)) {
                gotoMcPage(context, url);
                return true;
            }
            if (url.startsWith(Constants.SCHEME_WTAI_SD)) {
                return false;
            }
            if (url.startsWith(Constants.SCHEME_WTAI_AP)) {
                return false;
            }
        }

        return mSuccessor.handleRequest(context, url);
    }

    /**
     * @param context
     *            TODO
     * @param url
     */
    private void gotoMcPage(Activity context, String url) {
        String number = "";
        if (url.contains("?")) {
            number = url.substring(Constants.SCHEME_WTAI_MC.length(), url.indexOf("?"));
        } else {
            number = url.substring(Constants.SCHEME_WTAI_MC.length());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebView.SCHEME_TEL + number));
        context.startActivity(intent);
    }
}
