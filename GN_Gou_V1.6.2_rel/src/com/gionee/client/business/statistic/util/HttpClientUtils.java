//Gionee <yangxiong><2013-12-09> modify for CR00850885 begin
package com.gionee.client.business.statistic.util;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.params.CoreConnectionPNames;

import android.net.http.AndroidHttpClient;

import com.gionee.client.business.statistic.business.Constants;

public class HttpClientUtils {

    public static AndroidHttpClient getAndroidHttpClient() {
        String imei = Utils.getImei();
        AndroidHttpClient androidHttpClient = AndroidHttpClient.newInstance(Utils.getUaString(imei));
        androidHttpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                Constants.NetworkConfig.GIONEE_CONNECT_TIMEOUT);
        androidHttpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
                Constants.NetworkConfig.GIONEE_SOCKET_TIMEOUT);
        return androidHttpClient;
    }

    public static void disconnectHttpMethod(HttpRequestBase httpRequest) {
        if (Utils.isNull(httpRequest)) {
            return;
        }

        if (!httpRequest.isAborted()) {
            httpRequest.abort();
        }

    }

    public static void close(AndroidHttpClient androidHttpClient) {
        if (Utils.isNull(androidHttpClient)) {
            return;
        }
        androidHttpClient.close();
    }

    public static boolean isRightHttpStatusCode(int httpStatusCode) {
        if (httpStatusCode == Constants.HttpStatusCode.CODE_OK) {
            return true;
        }
        return false;
    }
}
//Gionee <yangxiong><2013-12-09> modify for CR00850885 end