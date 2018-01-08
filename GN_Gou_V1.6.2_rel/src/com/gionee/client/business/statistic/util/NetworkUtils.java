package com.gionee.client.business.statistic.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gionee.client.business.statistic.business.Constants;

public class NetworkUtils {

    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    return Constants.NetworkCode.NETWORK_MOBILE;
                case ConnectivityManager.TYPE_WIFI:
                    return Constants.NetworkCode.NETWORK_WIFI;
                default:
                    break;
            }
        }

        return Constants.NetworkCode.NO_NETWORK;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Utils.isNotNull(cm)) {
            NetworkInfo networkinfo = cm.getActiveNetworkInfo();
            return Utils.isNotNull(networkinfo) && networkinfo.isAvailable();
        }

        return false;
    }

    public static boolean isNetworkNotAvailable(Context context) {
        return !isNetworkAvailable(context);
    }

    public static boolean isMobileNetwork(Context context) {
        if (getNetworkType(context) == Constants.NetworkCode.NETWORK_MOBILE) {
            return true;
        }
        return false;
    }
}