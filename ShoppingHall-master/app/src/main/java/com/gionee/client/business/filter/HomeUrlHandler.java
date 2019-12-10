// Gionee <yuwei><2013-12-19> add for CR00821559 begin
/*
 * HomeUrlHandler.java
 * classes : com.gionee.client.business.filter.HomeUrlHandler
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-19 下午3:45:55
 */
package com.gionee.client.business.filter;

import android.app.Activity;

/**
 * HomeUrlHandler
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-19 下午3:45:55
 * @description TODO
 */
public class HomeUrlHandler extends IUrlHandler {

    @Override
    public boolean handleRequest(Activity context, String url) {
//        int homePageIndex = UrlUtills.checkHomeUrlWithParameter(url);
//        if (homePageIndex > -1) {
//            gotoHomePage(context, url, homePageIndex);
//            return true;
//        }

        return mSuccessor.handleRequest(context, url);

    }

    /**
     * @param context
     * @param url
     * @param homePageIndex
     * @author yuwei
     * @description TODO
     */
//    private void gotoHomePage(Activity context, String url, int homePageIndex) {
//        Intent intent = new Intent();
//        intent.putExtra(Constants.Home.HOME_INTENT_FLAG, url);
//        intent.putExtra(Constants.Home.TAG_PAGE_INDEX, homePageIndex);
//        context.setResult(StatisticConstants.HOME_RESULT_CODE, intent);
//        context.finish();
//    }
}
//Gionee <yuwei><2013-12-19> add for CR00821559 end