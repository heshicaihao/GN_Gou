package com.gionee.client.business.sina;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

public class WeiBoEntryActivity extends Activity implements IWeiboHandler.Response {
    private static final String TAG = "WeiBoEntryActivity";
    private IWeiboShareAPI mWeiboShareAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
        mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
    }

    @Override
    public void onResponse(BaseResponse baseResp) {
        LogUtils.log(TAG, LogUtils.getThreadName() + ", errCode = " + baseResp.errCode);
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                // 新浪微博
                StatService.onEvent(this, BaiduStatConstants.SHARE_SUCCESS, BaiduStatConstants.WEIBO);
                Toast.makeText(this, R.string.share_success, Toast.LENGTH_SHORT).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, R.string.share_cancel, Toast.LENGTH_SHORT).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, R.string.share_fail, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }
}
