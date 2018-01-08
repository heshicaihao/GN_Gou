// Gionee <yuwei><2013-7-23> add for CR00821559 begin
/*
 * GnHelpActivity.java
 * classes : com.gionee.client.GnHelpActivity
 * @author yuwei
 * V 1.0.0
 * Create at 2013-7-23 下午3:10:15
 */
package com.gionee.client.activity.webViewPage;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.gionee.client.R;
import com.gionee.client.activity.GNCutActivity;
import com.gionee.client.business.manage.WebViewListManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DownLoadUtill;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;

/**
 * com.gionee.client.GnHelpActivity
 * 
 * @author yuwei <br/>
 *         create at 2013-7-23 下午3:10:15
 */
public class ThridPartyWebActivity extends BaseWebViewActivity {
    private static final String KEY = "contrast_first";
    private String mBannerType;
    private View mWebGuideView;
    private boolean mIsGotoOtherPage;
    private boolean mIsNotContinue;
    private boolean mIsGoBack;
    private String mLastUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadVisible(true);
        if (getIntent() != null) {
            boolean isShowFooter = getIntent().getBooleanExtra(Constants.IS_SHOW_WEB_FOOTBAR, true);
            mBannerType = getIntent().getStringExtra(Constants.Home.KEY_INTENT_INDEX);
            setFootState(isShowFooter);

        } else {
            setFootState(true);
        }
        try {
            mWebView.getRefreshableView().addJavascriptInterface(this, "share");
        } catch (Exception e) {
            // TODO: handle exception
        }
        WebViewListManager.getInstance().goFoward(this);
    }

    private void setFootState(boolean flag) {
        setFootVisible(flag);
        if (flag) {
            initGuideView();
        }
    }

    private void initGuideView() {
        if (ShareDataManager.getDataAsInt(this, KEY, 0) == 0) {
            showGuideView();
            ShareDataManager.saveDataAsInt(this, KEY, 1);
        }
    }

    private void showGuideView() {
        mWebGuideView = LayoutInflater.from(this).inflate(R.layout.web_guide_view, null, false);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_layout);
        RelativeLayout.LayoutParams layoutParamsall = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParamsall.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.addView(mWebGuideView, layoutParamsall);
        mWebGuideView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mWebGuideView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected boolean onBackClicked() {
        if (!TextUtils.isEmpty(mBannerType) && mBannerType.equals("cut_detail")) {
            gotoActivityWithOutParams(GNCutActivity.class);
            AndroidUtils.exitActvityAnim(this);
        }
        return true;
    }

    public void onJsClick() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    setIsGotoNewPage();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
    }

    public void setIsGotoNewPage() {
        WebView.HitTestResult result = mWebView.getRefreshableView().getHitTestResult();
        LogUtils.log("jsOnclick", "mIsGotoOtherPage3=" + mIsGotoOtherPage);
        if (result != null) {
            LogUtils.log("jsOnclick", "mIsGotoOtherPage4=" + mIsGotoOtherPage);
            int type = result.getType();
            LogUtils.log("jsOnclick", "type=" + type + "jsExtra=" + result.getExtra());
            if (isUrlLink(type)) {
                mPageRedirectStep = 0;
                String url = result.getExtra();
                LogUtils.log("jsOnclick", "url=" + url);
                if (!TextUtils.isEmpty(url)
                        && (url.contains("http://") || url.contains("javascript:void(0)"))) {
                    mIsGotoOtherPage = true;
                    mLastUrl = mWebView.getRefreshableView().getUrl();
                    LogUtils.log("jsOnclick", "mIsGotoOtherPage=" + mIsGotoOtherPage);
                }

            }
        }
    }

    @SuppressWarnings("deprecation")
    private boolean isUrlLink(int type) {
        return type == WebView.HitTestResult.SRC_ANCHOR_TYPE
                || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                || type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.ANCHOR_TYPE
                || type == WebView.HitTestResult.UNKNOWN_TYPE
                || type == WebView.HitTestResult.SRC_ANCHOR_TYPE;
    }

    @Override
    public boolean gotoOtherPage(String url) {
        LogUtils.log("jsOnclick", "mIsGotoOtherPage2=" + mIsGotoOtherPage);
        if (mIsGotoOtherPage && url.startsWith("http://")) {
            gotoWebPage(url, true);
            mIsGotoOtherPage = false;
            mIsNotContinue = true;
            return true;
        }
        if (mIsNotContinue) {
            return true;
        }
        if (mIsGoBack) {
            return true;
        }

        return false;
    }

    /* (non-Javadoc)
     * @see com.gionee.client.activity.webViewPage.BaseWebViewActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        mIsGotoOtherPage = false;
//        mIsNotContinue = true;
//        mIsGoBack = true;
    }

    @Override
    protected void onStartDownload(String url) {
        DownLoadUtill.downloadFile(ThridPartyWebActivity.this, url);
        try {
            mWebView.getRefreshableView().goBack();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.log("WebViewManager", "onpause finish=" + isFinishing());
        if (isFinishing()) {
            LogUtils.log("WebViewManager", "isfinish=" + isFinishing());
            WebViewListManager.getInstance().goBack(new WeakReference<BaseWebViewActivity>(this));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.log("WebViewManager", "ondestroy finish=" + isFinishing());
    }

    @Override
    public void gotoHuodongProductList() {
        // TODO Auto-generated method stub
    }

}
//Gionee <yuwei><2013-7-23> add for CR00821559 end