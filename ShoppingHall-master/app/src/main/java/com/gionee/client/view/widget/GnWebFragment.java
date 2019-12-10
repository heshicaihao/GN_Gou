/*
 * GnWebFragment.java
 * classes : com.gionee.client.widget.GnWebFragment
 * @author yuwei
 * V 1.0.0
 * Create at 2013-4-8 下午5:08:18
 */
package com.gionee.client.view.widget;

//Gionee <yuwei><2013-7-15> add for CR00836967 begin
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.UrlUtills;
import com.gionee.client.model.Constants;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
/**
 * com.gionee.client.widget.GnWebFragment
 * 
 * @author yuwei <br/>
 *         create at 2013-4-8 下午5:08:18 TODO
 */
public class GnWebFragment extends BaseFragment implements OnRefreshListener<GNWebView> {

    private static final String TAG = "GnWebFragment";
    private MyWebView mWebView;
    private String mWebUrl = "";
    private boolean mIsLoaded = false;
    private ProgressBar mProgress;
    private LinearLayout mWebViewContainer;
    private boolean mIsReload;
    private boolean mIsPullToRefresh = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (savedInstanceState != null) {
            String url = savedInstanceState.getString("url");
            if (!TextUtils.isEmpty(url)) {
                mWebUrl = url;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initWebView(activity);
        LogUtils.log(TAG, LogUtils.getThreadName());
    }

    @Override
    public View getCustomToastParentView() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return mWebView.getWebView();
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void initWebView(Context context) {
        if (mWebView == null) {
            mWebView = new MyWebView(context);
            mWebView.getRefreshableView().addJavascriptInterface(this, "share");
            mWebView.setOnRefreshListener(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLoadUrlData();
        initWebView(getActivity());
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (savedInstanceState != null) {
            mWebUrl = savedInstanceState.getString("url");
        }
        if (!mIsLoaded && mWebView != null && !TextUtils.isEmpty(mWebUrl)) {
            intMyWebView();
            LogUtils.logv(TAG, LogUtils.getThreadName() + "load " + "url=" + mWebUrl);
            mWebView.getWebView().loadUrl(mWebUrl);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mWebViewContainer != null) {
            mWebViewContainer.removeView(mWebView);
        }
        LogUtils.log(TAG, LogUtils.getThreadName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.log(TAG, LogUtils.getThreadName());
    }

    private void initLoadUrlData() {
        try {
            String value = getArguments().getString(Constants.Home.KEY_INTENT_URL);
//            mPageIndex = getArguments().getInt(Constants.Home.KEY_INTENT_INDEX);
            mWebUrl = value;
        } catch (Exception e) {
        }
    }

    /**
     * void TODO reload url
     */
    public void reload(boolean isReload) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (mWebView == null) {
            return;
        }
        if (isNetWorkUnavalible()) {
            return;
        }
        try {
            if (mWebView.getWebView().getUrl().equals(Constants.UN_NETWORK)) {
                mWebView.getWebView().loadUrl(mWebUrl);
            } else if (isReload) {
                mWebView.getWebView().reload();
            }
        } catch (Exception e) {
            LogUtils.log(TAG, "refreshWebView  Exception :" + e);
        } finally {
            checkNetTimeOut();
        }

    }

    public void reload() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                mProgress.setVisibility(View.VISIBLE);
                mWebView.getRefreshableView().goBack();
                mProgress.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mProgress.setVisibility(View.GONE);

                    }
                }, 15000);
            }
        });

    }

    /**
     * 
     * @description
     * @author yuwei
     */
    private boolean isNetWorkUnavalible() {
        if (AndroidUtils.getNetworkType(getActivity()) == Constants.NET_UNABLE) {
            mWebView.onRefreshComplete();
            showErrorToast(R.string.upgrade_no_net);
            return true;
        }
        return false;
    }

    /**
     * 
     * @description
     * @author yuwei
     */
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
    public void onResume() {
        LogUtils.log(TAG, LogUtils.getFunctionName() + "webUrl:" + mWebUrl);
        super.onResume();
        if (mWebView == null) {
            return;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "webUrl:" + mWebUrl);
        View containerLayout = inflater.inflate(R.layout.fragmenta_item, null, false);
        mWebViewContainer = (LinearLayout) containerLayout.findViewById(R.id.web_view_container);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebViewContainer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mProgress = (ProgressBar) containerLayout.findViewById(R.id.fragment_loading_bar);
        mWebViewContainer.addView(mWebView, new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        if (mIsReload || mWebView.getWebView().getUrl() == null
                || mWebView.getWebView().getUrl().equals(Constants.UN_NETWORK)) {
            mWebView.getWebView().loadUrl(mWebUrl);
        }
        return containerLayout;
    }

    public GnWebFragment() {
        super();
    }

    /**
     * @return the webUrl
     */
    public String getWebUrl() {
        return mWebUrl;
    }

    /**
     * @param webUrl
     *            the webUrl to set
     */
    public void setWebUrl(String webUrl) {
        this.mWebUrl = webUrl;
    }

    private void intMyWebView() {
        mWebView.init(true);
        mWebView.getWebView().setWebViewClient(new MyWebViewClient());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (mWebViewContainer != null) {
            mWebViewContainer.removeAllViews();
        }
    }

    public void loadUrl(Context context, String url) {
        LogUtils.logv(TAG, LogUtils.getThreadName() + "url=" + url);
        mWebView = new MyWebView(context);
        intMyWebView();
        mWebUrl = url;
        mWebView.getWebView().loadUrl(mWebUrl);
        mIsLoaded = true;

    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtils.log(TAG, LogUtils.getThreadName() + "overrideUrl" + "url=" + url);
            int index = UrlUtills.checkHomeUrlWithParameter(url);
            if (url.startsWith(WebView.SCHEME_TEL)) {
                gotoTelPage(url);
                return true;
            }
            if (url.contains(Constants.CMCC_IDE_NOTIFICATION1)
                    && url.contains(Constants.CMCC_IDE_NOTIFICATION2)) {
                mWebView.getWebView().loadUrl(Constants.UN_NETWORK);
                return true;
            }
            if (index > -1) {
                mWebView.getWebView().loadUrl(url);
                return true;
            } else {
                LogUtils.log("help", LogUtils.getFunctionName() + url);
                gotoSeachResultPage(url);
                return true;
            }
        }

        /**
         * @param url
         */
        private void gotoTelPage(String url) {
            LogUtils.log(TAG, "url : " + url);
            String number = "";
            if (url.contains("?")) {
                number = url.substring(WebView.SCHEME_TEL.length(), url.indexOf("?") + 1);
            } else {
                number = url.substring(WebView.SCHEME_TEL.length());
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebView.SCHEME_TEL + number));
            getActivity().startActivity(intent);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            mWebView.getWebView().loadUrl(Constants.UN_NETWORK);
            showErrorToast(R.string.refresh_error);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onPageFinished(WebView view, String url) {
            if (mProgress != null) {
                mProgress.setVisibility(View.GONE);
                mWebView.setPullToRefreshEnabled(true);
            }
            mWebView.getWebView().getSettings().setBlockNetworkImage(false);
            LogUtils.log(TAG, LogUtils.getThreadName() + "webView.getUrl() ="
                    + mWebView.getWebView().getUrl());
            super.onPageFinished(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            LogUtils.log(TAG, LogUtils.getThreadName());
            if (mProgress != null && !mWebView.isRefreshing()) {
                mProgress.setVisibility(View.VISIBLE);
                mWebView.setPullToRefreshEnabled(false);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onSaveInstanceState(outState);
        outState.putString("url", mWebUrl);
    }

    /**
     * void
     * 
     * @param keyword
     *            TODO go to search result page
     */
    private void gotoSeachResultPage(String url) {
        LogUtils.log("fragmentUrl", LogUtils.getFunctionName() + "url=" + url);
        Intent intent = new Intent();
        intent.putExtra(StatisticsConstants.KEY_INTENT_URL, url.toString());
//        if (UrlUtills.invalidateSelfPage(url)) {
//            intent.setClass(getActivity(), GNSelfPageActivity.class);
//        } else {
        intent.setClass(getActivity(), ThridPartyWebActivity.class);
//        }
        startActivityForResult(intent, StatisticsConstants.HOME_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.log(TAG, LogUtils.getThreadName() + "fragment result" + resultCode);
        if (resultCode == StatisticsConstants.HOME_RESULT_CODE) {
            if (mWebView == null) {
                return;
            }
            int index = data.getIntExtra(Constants.Home.TAG_PAGE_INDEX, 0);
            String url = data.getStringExtra(Constants.Home.HOME_INTENT_FLAG);
            if (!TextUtils.isEmpty(url)) {
                if (!((TabChangeListener) getActivity()).onCheckTab(index, url)) {
                    checkLoadUrl(url);
                }
            } else {
                mWebView.getWebView().reload();
            }
        }
    }

    /**
     * 
     * @description
     * @author yuwei
     */
    private void showErrorToast(int textId) {
        if (!mIsPullToRefresh) {
            return;
        }
        if (getActivity() == null) {
            return;
        }
        CustomToast toast = new CustomToast(getActivity());
        toast.setToastText(textId);
        int topMargin = AndroidUtils.dip2px(getActivity(), 110);
        toast.showToast(getCustomToastParentView(), topMargin);
    }

    private void checkLoadUrl(String url) {
        if (mWebView == null) {
            return;
        }
        if (url.equals(mWebUrl)) {
            mWebView.getWebView().reload();
        } else {
            mWebUrl = url;
            mWebView.getWebView().clearView();
            mWebView.getWebView().loadUrl(mWebUrl);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mWebView.getWebView().requestFocus();
        } else {
            AndroidUtils.hidenKeybord(getActivity(), mWebView);
        }
        if (com.gionee.client.model.Config.IS_STATIC_ENABLE) {
//            statisticPageEvent(isVisibleToUser);
        }
    }

    /**
     * @param isVisibleToUser
     * @description
     * @author yuwei
     */
//    private void statisticPageEvent(boolean isVisibleToUser) {
//        LogUtils.logd("fragmenVisible", LogUtils.getFunctionName() + "currentPage=" + mPageIndex
//                + "isVisible=" + isVisibleToUser);
//        if (isVisibleToUser) {
//            switch (mPageIndex) {
//                case Constants.Home.PAGE_RECOMMOND:
//                    StatService.onEvent(getActivity(), getResources().getString(R.string.home_recommond),
//                            "pass", 1);
//                    break;
//                case Constants.Home.PAGE_PAY_ON_DELIVERY:
//                    StatService.onEvent(getActivity(), getResources()
//                            .getString(R.string.home_pay_on_delivery), "pass", 1);
//                    break;
//                case Constants.Home.PAGE_PERSONAL_CENTER:
////                    mWebView.getWebView().loadUrl(Config.PERSONAL_CENTER_URL);
//                    StatService.onEvent(getActivity(), getResources().getString(R.string.home_taobao),
//                            "pass", 1);
//                    break;
//                default:
//                    break;
//            }
//        } else {
//
//        }
//    }

    /**
     * @return the mIsReload
     */
    public boolean ismIsReload() {
        return mIsReload;
    }

    /**
     * @param mIsReload
     *            the mIsReload to set
     */
    public void setmIsReload(boolean mIsReload) {
        this.mIsReload = mIsReload;
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.log(TAG, LogUtils.getThreadName());
    }

    @Override
    public void onRefresh(PullToRefreshBase<GNWebView> refreshView) {
        mIsPullToRefresh = true;
        reload(true);
    }

    @Override
    protected int setContentViewId() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return 0;
    }

}
//Gionee <yuwei><2013-7-16> add for CR00836967 end