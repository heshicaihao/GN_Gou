// Gionee <yuwei><2013-12-19> add for CR00821559 begin
/*
 * TaobaoClientHandler.java
 * classes : com.gionee.client.business.filter.TaobaoClientHandler
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-19 下午3:18:13
 */
package com.gionee.client.business.filter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;

import com.gionee.client.activity.webViewPage.BaseWebViewActivity;
import com.gionee.client.business.appDownload.InstallUtills;
/**
 * TaobaoClientHandler
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-19 下午3:18:13
 * @description TODO处理淘宝客户端url
 */
public class TaobaoClientHandler extends IUrlHandler {

    private static final int LAST_USABLE_TAOBAO_VERSION = 83;

    @Override
    public boolean handleRequest(Activity context, String url) {
        for (UrlFilterConfig.TaobaoClientEnum scheme : UrlFilterConfig.TaobaoClientEnum.values()) {
            if (url.contains(scheme.getValue())) {
                if (isTaobaoClientUseable(context)) {
                    gotoTaobao(context, url);
                    return true;
                }

            }
        }

        return mSuccessor.handleRequest(context, url);
    }

    private boolean isTaobaoClientUseable(Activity context) {
        final String packageName = "com.taobao.taobao";
        PackageInfo info = InstallUtills.getPackgeInfo(context, packageName);
        if (info != null && !(info.versionCode < LAST_USABLE_TAOBAO_VERSION)) {
            return true;
        }
        return false;
    }

    /**
     * @param context
     *            TODO
     * @param url
     * @author yuwei
     * @description TODO
     */
    private void gotoTaobao(Activity context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            context.startActivity(intent);
            ((BaseWebViewActivity) context).goBack();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
//Gionee <yuwei><2013-12-19> add for CR00821559 end