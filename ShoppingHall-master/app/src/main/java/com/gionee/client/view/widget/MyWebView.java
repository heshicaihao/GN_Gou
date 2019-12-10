// Gionee <yuwei><2013-7-29> add for CR00821559 begin
package com.gionee.client.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.gionee.client.business.util.WebViewUtills;

public class MyWebView extends PullToRefreshWebView {
    private GNWebView mWebView;

    /**
     * @return the webView
     */
    public WebView getWebView() {
        return mWebView;
    }

    public MyWebView(Context context) {
        super(context);
        mWebView = mRefreshableView;
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWebView = mRefreshableView;
    }

    /**
     * void TODO init the webView complete the basic settings
     */
    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    public void init(boolean isUseCache) {
        WebViewUtills.initWebView(mWebView, true);

    }

    public void setWebPullToRefreshMode(Mode mode) {
        setMode(mode);
    }

}
//Gionee <yuwei><2013-7-29> add for CR00821559 end