/*
 * WebViewUtills.java
 * classes : com.gionee.client.business.util.WebViewUtills
 * @author yuwei
 * 
 * Create at 2015-2-5 下午2:46:51
 */
package com.gionee.client.business.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.webkit.CacheManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;

import com.gionee.client.model.Constants;
import com.gionee.framework.operation.utills.FileUtil;

/**
 * com.gionee.client.business.util.WebViewUtills
 * 
 * @author yuwei <br/>
 *         create at 2015-2-5 下午2:46:51
 * @description WebView工具
 */
public class WebViewUtills {
    /**
     * void TODO init the webView complete the basic settings
     */
    @SuppressWarnings("deprecation")
    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    public static void initWebView(WebView mWebView, boolean isUseCache) {
        WebViewReflect.setDomStorage(mWebView.getSettings());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.requestFocus();
        mWebView.setSelected(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
        mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
        mWebView.getSettings().setUserAgentString(UAUtils.getUserAgent(mWebView.getContext()));
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.setLongClickable(true);
        int sdkVersion = AndroidUtils.getAndroidSDKVersion();
        if (sdkVersion >= 11) {
            mWebView.getSettings().setTextSize(TextSize.NORMAL);
            WebViewReflect.setDisplayZoomControls(mWebView.getSettings(), false);
        }
        if (sdkVersion >= 9) {
            mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        if (isUseCache) {
            initCacheWebViewSettings(mWebView);
        }
        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        initLocationSettings(mWebView);
    }

    public static void initCacheWebViewSettings(WebView cachedWebView) {
        cachedWebView.getSettings().setAppCacheEnabled(true);
        cachedWebView.getSettings().setDatabaseEnabled(true);
        cachedWebView.getSettings().setDomStorageEnabled(true);
        cachedWebView.getSettings().setAppCacheMaxSize(Constants.CACHE_MAX_SIZE);
        cachedWebView.getSettings().setAppCachePath(
                cachedWebView.getContext().getDir("appcache", 0).getPath());
        cachedWebView.getSettings().setDatabasePath(
                cachedWebView.getContext().getDir("databases", 0).getPath());

    }

    @SuppressWarnings("deprecation")
    public static void cleanCache(Context context) {

        try {
            FileUtil.deleteFile(CacheManager.getCacheFileBaseDir());
            clearCookies(context);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static boolean isCacheExitsts(Context context) {

        try {
            return CacheManager.getCacheFileBaseDir().exists();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static void clearCookies(Context context) {

        try {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void initLocationSettings(WebView mWebView) {
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setGeolocationDatabasePath(
                mWebView.getContext().getDir("geolocation", 0).getPath());
    }

}
