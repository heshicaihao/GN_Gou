// Gionee <yuwei><2013-7-15> add for CR00836967 begin
package com.gionee.client.activity.webViewPage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.DownLoadUtill;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.view.widget.GNCustomDialog;

/**
 * com.gionee.client.GNSelfPageActivity
 * 
 * @author yuwei <br/>
 * @data create at 2013-7-31 上午10:46:16
 * @desciption page from gionee service
 */
public class GNSelfPageActivity extends BaseWebViewActivity {
    private static final String TAG = "GN_SelfPageActivity";
    private GNCustomDialog mDialog;
    private String mFlag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadVisible(true);
//        setFootVisible(false);
        initView();
        Intent intent = getIntent();
        mFlag = intent.getStringExtra("com.gionee.fanfan");
        mWebView.getRefreshableView().addJavascriptInterface(this, "share");
    }

    public void reload() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
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

    @Override
    protected void invalidateUrl() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mUrl = getIntent().getDataString();
        if (TextUtils.isEmpty(mUrl)) {
            mUrl = com.gionee.client.model.Constants.UN_NETWORK;
        }
    }

    @Override
    protected void onDestroy() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onDestroy();
    }

    private void initView() {

        mShareBtn.setVisibility(View.VISIBLE);
        mDialog = (GNCustomDialog) DialogFactory.createShareDialog(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.share_btn:
                LogUtils.log(TAG, LogUtils.getThreadName() + "share_btn");
                StatService.onEvent(GNSelfPageActivity.this, "share", "share_btn");
                mDialog.show();
                mDialog.setDismissBtnVisible();
                mDialog.setCanceledOnTouchOutside(true);
                mDialog.getContentView().findViewById(R.id.share_weixin).setOnClickListener(this);
                mDialog.getContentView().findViewById(R.id.share_friends).setOnClickListener(this);
                mDialog.getContentView().findViewById(R.id.share_weibo).setOnClickListener(this);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean gotoOtherPage(String url) {
        LogUtils.log("fragmentUrl", LogUtils.getFunctionName() + "url=" + url);
        setmDescription(url);
        mThumbBitmap = null;
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        sendBroadCast();
        super.onBackPressed();
    }

    private void sendBroadCast() {
        if ("gionee_fanfan".equals(mFlag)) {
            LogUtils.log(TAG, "send broadCast");
            Intent intent = new Intent("com.gionee.action.REENABLE_KEYGUARD");
            intent.putExtra("cls_name", "com.gionee.navi.fanfan");
            this.sendBroadcast(intent);
        }
    }

    @Override
    protected boolean onBackClicked() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        sendBroadCast();
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStartDownload(String url) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        DownLoadUtill.downloadFile(GNSelfPageActivity.this, url);
        mWebView.getRefreshableView().goBack();

    }

    @Override
    public void gotoHuodongProductList() {
        // TODO Auto-generated method stub
        
    }
}
//Gionee <yuwei><2013-7-16> add for CR00836967 end