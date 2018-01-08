package com.gionee.client.business.sina;

import java.net.URLDecoder;

import android.os.Bundle;

/***
 * 
 * 
 */
public class WeiboUitls {

    @SuppressWarnings("deprecation")
    public static Bundle parseUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String[] array = s.split("&");
            for (String parameter : array) {
                String[] v = parameter.split("=");
                if (v[0].contains("?")) {
                    v[0] = v[0].substring(v[0].indexOf("?") + 1);
                }
                params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
            }
        }
        return params;
    }
}
