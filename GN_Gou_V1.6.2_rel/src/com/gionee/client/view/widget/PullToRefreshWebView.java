// Gionee <yuwei><2013-8-1> add for CR00821559 begin
/*
 * PullToRefreshWebView.java
 * classes : com.gionee.client.widget.PullToRefreshWebView
 * @author yuwei
 * V 1.0.0
 * Create at 2013-8-1 下午2:26:21
 */
package com.gionee.client.view.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.gionee.client.R;
import com.gionee.client.business.util.AndroidUtils;
import com.handmark.pulltorefresh.library.OverscrollHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * com.gionee.client.widget.PullToRefreshWebView
 * 
 * @author yuwei <br/>
 * @data create at 2013-8-1 下午2:26:21
 * @desciption this WebView has add the fuction of refresh by pull down
 */
@SuppressLint("FloatMath")
public class PullToRefreshWebView extends PullToRefreshBase<GNWebView> {
    private OnLastItemVisibleListener mOnLastItemVisibleListener;
    private static final OnRefreshListener<GNWebView> DEFAULT_ONREFRESH_LISTENER = new OnRefreshListener<GNWebView>() {

        @Override
        public void onRefresh(PullToRefreshBase<GNWebView> refreshView) {
            refreshView.getRefreshableView().reload();
        }

    };

    public void scrollToTop() {
        smoothScrollTo(0);
    }

    private final WebChromeClient mDefaultWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Log.v("webview", "newProgress=" + newProgress);
            if (newProgress == 100) {
                // Update the LastUpdatedLabel
                String label = AndroidUtils.getCurrentTimeStr(getContext());
                getLoadingLayoutProxy().setLastUpdatedLabel(label);
                view.requestFocus();
                onRefreshComplete();
            }
        }

    };

    public PullToRefreshWebView(Context context) {
        super(context);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        setOnRefreshListener(DEFAULT_ONREFRESH_LISTENER);
        mRefreshableView.setWebChromeClient(mDefaultWebChromeClient);
//      getLoadingLayoutProxy().setLoadingDrawable(drawable);
    }

    public PullToRefreshWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        setOnRefreshListener(DEFAULT_ONREFRESH_LISTENER);
        mRefreshableView.setWebChromeClient(mDefaultWebChromeClient);
    }

    public PullToRefreshWebView(Context context, Mode mode) {
        super(context, mode);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        setOnRefreshListener(DEFAULT_ONREFRESH_LISTENER);
        mRefreshableView.setWebChromeClient(mDefaultWebChromeClient);
    }

    public PullToRefreshWebView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        setOnRefreshListener(DEFAULT_ONREFRESH_LISTENER);
        mRefreshableView.setWebChromeClient(mDefaultWebChromeClient);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @SuppressLint("NewApi")
    @Override
    protected GNWebView createRefreshableView(Context context, AttributeSet attrs) {
        GNWebView webView;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            webView = new InternalWebViewSDK9(context, attrs);
        } else {
            webView = new GNWebView(context, attrs);
        }
        webView.setId(R.id.webview);
        return webView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        float exactContentHeight = FloatMath.floor(mRefreshableView.getContentHeight()
                * mRefreshableView.getScale());
        if (mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight())
                && mOnLastItemVisibleListener != null) {
            mOnLastItemVisibleListener.onLastItemVisible();
        }
        return false;
    }

    @Override
    protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
        super.onPtrRestoreInstanceState(savedInstanceState);
        mRefreshableView.restoreState(savedInstanceState);
    }

    @Override
    protected void onPtrSaveInstanceState(Bundle saveState) {
        super.onPtrSaveInstanceState(saveState);
        mRefreshableView.saveState(saveState);
    }

    @TargetApi(9)
    final class InternalWebViewSDK9 extends GNWebView {

        // WebView doesn't always scroll back to it's edge so we add some
        // fuzziness
        static final int OVERSCROLL_FUZZY_THRESHOLD = 2;

        // WebView seems quite reluctant to overscroll so we use the scale
        // factor to scale it's value
        static final float OVERSCROLL_SCALE_FACTOR = 1.5f;

        public InternalWebViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshWebView.this, deltaX, scrollX, deltaY, scrollY,
                    getScrollRange(), OVERSCROLL_FUZZY_THRESHOLD, OVERSCROLL_SCALE_FACTOR, isTouchEvent);

            return returnValue;
        }

        /* (non-Javadoc)
         * @see android.webkit.WebView#onConfigurationChanged(android.content.res.Configuration)
         */
        @Override
        protected void onConfigurationChanged(Configuration newConfig) {
            try {
                super.onConfigurationChanged(newConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int getScrollRange() {
            return (int) Math.max(0,
                    FloatMath.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale())
                            - (getHeight() - getPaddingBottom() - getPaddingTop()));
        }
    }

    public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
        mOnLastItemVisibleListener = listener;
    }
}
//Gionee <yuwei><2013-8-1> add for CR00821559 end
