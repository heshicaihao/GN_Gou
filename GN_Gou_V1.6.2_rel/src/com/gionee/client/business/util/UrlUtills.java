// Gionee <yuwei><2013-7-29> add for CR00821559 begin
/*
* UrlUtills.java
 * classes : com.gionee.client.util.UrlUtills
 * @author yuwei
 * V 1.0.0
 * Create at 2013-4-17 下午3:32:23
 */
package com.gionee.client.business.util;

import android.text.TextUtils;

import com.gionee.client.model.Config;
import com.gionee.client.model.Constants;

/**
 * com.gionee.client.util.UrlUtills
 * 
 * @author yuwei <br/>
 *         create at 2013-4-17 下午3:32:23 TODO
 */
public class UrlUtills {
    private static int checkHomeUrl(String url) {
        if (url.equals(Config.RECOMMOND_URL)) {
            return Constants.Home.PAGE_RECOMMOND_INDEX;
        }
        if (url.equals(Config.PAY_ON_DELIVERY_URL)) {
            return Constants.Home.PAGE_PAY_ON_DELIVERY_INDEX;
        }
        if (url.equals(Config.PERSONAL_CENTER_URL) || url.equals(Config.PERSONAL_LOGIN_URL)) {
            return Constants.Home.PAGE_PERSONAL_CENTER_INDEX;
        }
        return -100;
    }

    /**
     * @param url
     * @return a posit// if (!url.contains("type=CHANNEL&_url")) // return -101;ive number if is the url of
     *         the home page ,else return a negative number
     * @description invalidate the url,return a int constant according the url
     * @author yuwei
     */
    public static int checkHomeUrlWithParameter(String url) {
        if (TextUtils.isEmpty(url)) {
            return -101;
        }
        if (url.contains(Config.PAY_ON_DELIVERY_URL_WITH_PARAMETER)) {
            return Constants.Home.PAGE_PAY_ON_DELIVERY_INDEX;
        }
        return checkHomeUrl(url);
    }

    /**
     * @param url
     * @return if is the login page url
     * @description in// if (!url.contains("type=CHANNEL&_url")) // return -101;validate login page
     * @author yuwei
     */
    public static boolean isLoginPage(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (url.contains(Config.LOGIN_URL) || url.contains(Config.LOGIN_REDIRECT_URL)) {
            return true;
        }
        return false;

    }

    /**
     * @return
     */
    public static boolean invalidateSelfPage(String url) {
        boolean isSelfPage = checkHomeUrlWithParameter(url) == Constants.Home.PAGE_SECONDARY_INDEX;
        return isSelfPage;
    }

    public static boolean invalidateTaobaoClick(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.contains(Config.TAOBAO_ONCLICK_URL)||url.contains(Config.TAOBAO_JUMP_URL);
    }
}
//Gionee <yuwei><2013-7-29> add for CR00821559 end