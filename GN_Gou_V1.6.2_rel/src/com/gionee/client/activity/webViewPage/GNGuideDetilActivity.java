package com.gionee.client.activity.webViewPage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.gionee.client.R;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.view.widget.MyWebView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

public class GNGuideDetilActivity extends Activity implements OnClickListener {

    private String mUrl = null;
    private MyWebView mWebView;
    private ProgressBar mProgressBar;
    private static final String TAG = "Guide_page";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_guide_page);
        intView();
        invalidateUrl();
        initWebView();
    }

    private void intView() {
        mWebView = (MyWebView) findViewById(R.id.mywebview);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_bar);
    }

    private void initWebView() {
        mWebView.init(false);
        mWebView.setMode(Mode.DISABLED);
        mWebView.getRefreshableView().addJavascriptInterface(this, "share");
        mWebView.getWebView().loadUrl(mUrl);
        mWebView.getWebView().setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(Constants.CMCC_IDE_NOTIFICATION1)
                        && url.contains(Constants.CMCC_IDE_NOTIFICATION2)) {
                    mWebView.getWebView().loadUrl(Constants.UN_NETWORK_GUIDE);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mWebView.getWebView().loadUrl(Constants.UN_NETWORK_GUIDE);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                mWebView.getWebView().getSettings().setBlockNetworkImage(false);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mProgressBar != null && !mWebView.isRefreshing()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    protected void invalidateUrl() {
        mUrl = getIntent().getStringExtra(StatisticsConstants.KEY_INTENT_URL);
        if (TextUtils.isEmpty(mUrl)) {
            mUrl = Constants.UN_NETWORK_GUIDE;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                finish();
                AndroidUtils.logoFadeAnim(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onBackPressed();
        AndroidUtils.logoFadeAnim(this);
    }

    public void reload() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                mProgressBar.setVisibility(View.VISIBLE);
                mWebView.getRefreshableView().loadUrl(mUrl);
                mProgressBar.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);

                    }
                }, 15000);
            }
        });

    }

}
