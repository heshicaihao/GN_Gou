// Gionee <yuwei><2013-12-19> add for CR00821559 begin
/*
 * EmailHandler.java
 * classes : com.gionee.client.business.filter.EmailHandler
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-19 下午3:37:39
 */
package com.gionee.client.business.filter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;

/**
 * EmailHandler
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-19 下午3:37:39
 * @description TODO 处理邮件url
 */
public class EmailHandler extends IUrlHandler {

    @Override
    public boolean handleRequest(Activity context, String url) {
        if (url.startsWith("mailto:")) {
            gotoEmailPage(context, url);
            return true;
        }

        return mSuccessor.handleRequest(context, url);
    }

    /**
     * @param context
     *            TODO
     * @param url
     */
    private void gotoEmailPage(Activity context, String url) {
        MailTo mt = MailTo.parse(url);
        Intent i = newEmailIntent(context, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
        context.startActivity(i);
    }

    private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }
}
//Gionee <yuwei><2013-12-19> add for CR00821559 end