package com.gionee.client.view.widget;

import com.gionee.client.business.util.AndroidUtils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.webkit.WebView;

public class GNWebView extends WebView {

    public GNWebView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    

    public GNWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
        // TODO Auto-generated constructor stub
    }



    public GNWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }



    public GNWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }



    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        try {
                if (AndroidUtils.getAndroidSDKVersion() >= 11) {                    
                    super.onConfigurationChanged(newConfig);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
}
