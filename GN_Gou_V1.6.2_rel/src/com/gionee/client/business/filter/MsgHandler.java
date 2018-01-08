// Gionee <yuwei><2013-12-19> add for CR00821559 begin
/*
 * MsgHandler.java
 * classes : com.gionee.client.business.filter.MsgHandler
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-19 下午3:36:13
 */
package com.gionee.client.business.filter;

import android.app.Activity;
import android.content.Intent;

import com.gionee.client.model.Constants;

/**
 * MsgHandler
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-19 下午3:36:13
 * @description TODO 处理信息url
 */
public class MsgHandler extends IUrlHandler {

    @Override
    public boolean handleRequest(Activity context, String url) {
        if (url.startsWith(Constants.SCHEME_SMS)) {
            gotoMsgPage(context, url);
            return true;
        }
        return mSuccessor.handleRequest(context, url);
    }

    /**
     * @param view
     * @param url
     */
    private void gotoMsgPage(Activity context, String url) {
        String[] strings = url.split("\\?");
        // String smsString = strings[0].substring(4);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("address", strings[0].substring(4));
        intent.putExtra("sms_body", url.toString());
        intent.setType("vnd.android-dir/mms-sms");
        context.startActivity(intent);
    }
}
//Gionee <yuwei><2013-12-19> add for CR00821559 end