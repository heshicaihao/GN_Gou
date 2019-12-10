package com.gionee.client.activity.sina;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.sina.FriendshipsAPI;
import com.gionee.client.business.sina.WeiboAuthListener;
import com.gionee.client.business.sina.WeiboUitls;
import com.gionee.client.business.statistic.UrlCollector;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Config;
import com.gionee.client.model.Constants;
import com.gionee.client.view.widget.CustomToast;
import com.gionee.client.view.widget.GNWebView;
import com.gionee.client.view.widget.MyWebView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

public class WeiboAuthActivity extends BaseFragmentActivity implements WeiboAuthListener,
        OnRefreshListener<GNWebView>, OnClickListener {
    private static final long UID = 3624167944L;
    private static final String SCREEN_NAME = "购物大厅";
    private static final String TAG = "WeiBo_Auth";
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    private MyWebView mWebView;
    private RelativeLayout mWebViewHead;
    private WeiboAuthListener mListener;
    private boolean mIsPullToRefresh = false;
    private TextView mSuccess;
    private TextView mErr;
    private Oauth2AccessToken mAccessToken;
    private boolean mIsFollowComplete = false;
    private Button mRefocus;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.sina_auth);
        mListener = this;
        initView();
        intMyWebView();
        mWebView.getWebView().loadUrl(Config.WEIBO_AUTH_URL);
        AndroidUtils.setMiuiTopMargain(this, findViewById(R.id.auth_titlebar));
    }

    private void intMyWebView() {
        mWebView.init(false);
        mWebView.getWebView().getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setOnRefreshListener(this);
        mWebView.getRefreshableView().addJavascriptInterface(this, "share");
        mWebView.setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.DISABLED);
        mWebView.getWebView().setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.log(TAG, LogUtils.getFunctionName() + " url: " + url);
                if (url.startsWith(REDIRECT_URL)) {
                    handleRedirectUrl(view, url);
                }
                UrlCollector.getInstance().collect(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.log(TAG, LogUtils.getFunctionName() + "  url:" + url);
                if (mRlLoading != null && !mWebView.isRefreshing()) {
                    showPageLoading();
//                    mWebView.setPullToRefreshEnabled(false);
                }
                if (url.startsWith(REDIRECT_URL)) {
                    handleRedirectUrl(view, url);
//                    view.stopLoading();
                    view.loadUrl("");
                    return;
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.getWebView().getSettings().setBlockNetworkImage(false);
                hidePageLoading();
//                mWebView.setPullToRefreshEnabled(true);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mWebView.getWebView().loadUrl(com.gionee.client.model.Constants.UN_NETWORK);
                showErrorToast(R.string.refresh_error);
            }
        });
    }

    private void handleRedirectUrl(WebView view, String url) {
        Bundle values = WeiboUitls.parseUrl(url);
        String error = values.getString("error");
        String errorCode = values.getString("error_code");
        String token = values.getString("access_token");
        // success
        if (error == null && errorCode == null && !TextUtils.isEmpty(token)) {
            mListener.onComplete(values);
            // Failed
        } else if (!TextUtils.isEmpty(error) && error.equals("access_denied")) {
            mListener.onCancel();
        } else {
            // err
            mListener.onWeiboException(new WeiboException());
        }
    }

    private void initView() {
        mWebView = (MyWebView) findViewById(R.id.auth_webview);
        mWebViewHead = (RelativeLayout) findViewById(R.id.auth_titlebar);
        mSuccess = (TextView) findViewById(R.id.auth_success);
        mErr = (TextView) findViewById(R.id.auth_err);
        mRefocus = (Button) findViewById(R.id.refocus);
    }

    @Override
    public void onCancel() {
        LogUtils.log(TAG, LogUtils.getFunctionName());
//        Toast.makeText(this, R.string.cancel, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onComplete(Bundle values) {
        LogUtils.log(TAG, "WeiboAuthListener: " + LogUtils.getFunctionName() + values);
//        mAccessToken = Oauth2AccessToken.parseAccessToken(values);
//        followAppOfficialMicroBlog();
        mIsFollowComplete = true;
        mSuccess.setVisibility(View.VISIBLE);
        mErr.setVisibility(View.GONE);
        mRefocus.setVisibility(View.GONE);
    }

    private void followAppOfficialMicroBlog() {
        if (mAccessToken == null) {
            LogUtils.log(TAG, "token is null !");
            return;
        }
        FriendshipsAPI friend = new FriendshipsAPI(mAccessToken);
        friend.create(UID, SCREEN_NAME, new MyRequestListener());
    }

    class MyRequestListener implements RequestListener {

        @Override
        public void onComplete(String values) {
            LogUtils.log(TAG, "mRequestListener:" + LogUtils.getFunctionName() + values);
            mIsFollowComplete = true;
            mSuccess.setVisibility(View.VISIBLE);
            mErr.setVisibility(View.GONE);
            mRefocus.setVisibility(View.GONE);
        }

        @Override
        public void onWeiboException(WeiboException arg0) {
            LogUtils.log(TAG, "E:" + arg0);
            mIsFollowComplete = true;
            Toast.makeText(WeiboAuthActivity.this, R.string.follow_err, Toast.LENGTH_SHORT).show();
            mSuccess.setVisibility(View.GONE);
            mErr.setVisibility(View.VISIBLE);
            mRefocus.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onWeiboException(Exception e) {
        LogUtils.log(TAG, LogUtils.getFunctionName() + "  " + e);
    }

    @Override
    public void onRefresh(PullToRefreshBase<GNWebView> refreshView) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        mIsPullToRefresh = true;
        refresh();
        mSuccess.setVisibility(View.GONE);
        mErr.setVisibility(View.GONE);
        mRefocus.setVisibility(View.GONE);
    }

    public void reload() {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        StatService.onEvent(this, "refresh", "refresh");
        if (checkNetwork() == false) {
            mWebView.onRefreshComplete();
            return;
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                showPageLoading();
                mWebView.getRefreshableView().goBack();
                mWebView.getRefreshableView().goBack();
                mRlLoading.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hidePageLoading();
                    }
                }, 15000);
            }
        });

    }

    private void showErrorToast(int textId) {
        if (!mIsPullToRefresh) {
            return;
        }
        CustomToast toast = new CustomToast(this);
        toast.setToastText(textId);
        int topMagin = mWebViewHead.getBottom() + AndroidUtils.dip2px(this, 25);
        toast.showToast(mWebView.getWebView(), topMagin);
    }

    private void refresh() {
        if (isNetworkUnavalible()) {
            return;
        }
        try {
            if (mWebView.getWebView().getUrl().equals(Constants.UN_NETWORK)) {

                mWebView.getWebView().loadUrl(Config.WEIBO_AUTH_URL);
            } else {
                mWebView.getWebView().reload();
            }
        } catch (Exception e) {
            mWebView.getWebView().loadUrl(Config.WEIBO_AUTH_URL);
        } finally {
            checkNetTimeOut();
        }
    }

    private boolean isNetworkUnavalible() {
        if (AndroidUtils.getNetworkType(this) == Constants.NET_UNABLE) {
            mWebView.onRefreshComplete();
            showNetErrorToast();
            return true;
        }
        return false;
    }

    private boolean checkNetwork() {
        try {
            if (AndroidUtils.getNetworkType(this) == Constants.NET_UNABLE) {
                showNetErrorToast();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void checkNetTimeOut() {
        mWebView.postDelayed(new Runnable() {

            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (mWebView.isRefreshing()) {
                    mWebView.onRefreshComplete();
                    showErrorToast(R.string.refresh_timeout);
                }
            }
        }, 10000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auth_finish:
                finish();
                AndroidUtils.exitActvityAnim(this);
                break;
            case R.id.auth_webview_back:
                onBackPressed();
                break;
            case R.id.refocus:
                LogUtils.log(TAG, "refocus");
                followAppOfficialMicroBlog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        goBack();
        if (mSuccess.isShown()) {
            mSuccess.setVisibility(View.GONE);
        }
    }

    public void goBack() {
        if (mIsFollowComplete || !mWebView.getWebView().canGoBack() || isNetworkUnavalible()) {
            finish();
            AndroidUtils.exitActvityAnim(WeiboAuthActivity.this);
        } else {
            mWebView.getWebView().goBack();
        }
    }

    protected boolean isUnNetworkPage() {
        try {
            if (mWebView.getWebView().getUrl().equals(Constants.UN_NETWORK)) {
                return true;
            }
            String url = getLastUrl();
            if (TextUtils.isEmpty(url) || url.equals(Constants.UN_NETWORK)) {
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private String getLastUrl() {
        String url = "";
        try {
            int index = mWebView.getWebView().copyBackForwardList().getCurrentIndex();
            if (mWebView.getWebView().canGoBack()) {
                WebHistoryItem item = mWebView.getWebView().copyBackForwardList().getItemAtIndex(index - 1);
                url = item.getUrl();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    protected void onDestroy() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onDestroy();
        UrlCollector.getInstance().endCollect();
    }

}
