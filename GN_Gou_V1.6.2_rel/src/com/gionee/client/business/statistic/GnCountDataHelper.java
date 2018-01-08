package com.gionee.client.business.statistic;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONObject;

import android.content.Context;

import com.gionee.client.activity.apprecommend.AppRecommendActivity;
import com.gionee.client.business.statistic.business.Constants;
import com.gionee.client.business.util.GNReadPropertyFile;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.UAUtils;
import com.gionee.client.model.Config;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.business.LocalBuisiness;
import com.gionee.framework.operation.page.PageCacheManager;

public class GnCountDataHelper {
    private static final String TAG = "GnCountDataHelper";

    private Context mContext;

    public GnCountDataHelper(Context context) {
        this.mContext = context;
    }

    public void sendGetRequest(final Map<String, String> map) {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 60000);
        HttpProtocolParams.setUserAgent(httpParameters, UAUtils.getUserAgent((mContext)));
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {
            StringBuilder url = new StringBuilder();
            url.append(getPropertyUrl()).append(UAUtils.getUrlUserAgent(mContext).replaceAll(" ", "%20"));
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                url.append("&").append(key).append("=").append(value);
            }
            url.append("&").append(StatisticsConstants.ANDROID_ID).append("=")
                    .append(UAUtils.getAndroidID(mContext)).append("&")
                    .append(StatisticsConstants.MAC_ADDRESS).append("=")
                    .append(UAUtils.getMacAddress(mContext));
            url.toString().replaceAll(" ", "%20");
            LogUtils.log(TAG, LogUtils.getThreadName() + "replace URL:" + url.toString());
            HttpGet httpGet = new HttpGet(url.toString());
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            LogUtils.logd(TAG, LogUtils.getThreadName() + "StatusLine:" + response.getStatusLine());
            if (entity != null) {
                entity.consumeContent();
            }

        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
            } catch (Exception e) {
                LogUtils.loge(TAG, LogUtils.getThreadName(), e);
            }
        }
    }

    public void sendCountData(final Map<String, String> map) {
        LocalBuisiness.getInstance().getHandler().post(new Runnable() {

            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                sendGetRequest(map);
            }
        });

    }

    public static String getPropertyUrl() {
        String url = Config.COUNT_URL;

        if (GNReadPropertyFile.isTestEnvironment()) {
            url = Config.COUNT_TEST_URL;
        }
        LogUtils.log(TAG, "count url=" + url);
        return url;
    }

    /**
     * @return 配置信息json对象
     * @author yuwei
     * @description TODO 获取统计配置信息
     */
    public static JSONObject getConfig() {
        MyBean bean = PageCacheManager.LookupPageData(AppRecommendActivity.class.getName());
        JSONObject configObject = bean.getJSONObject(HttpConstants.Data.STATISTIC_CONFIG_JO);
        return configObject;
    }

    /**
     * @return
     * @author yuwei
     * @description TODO 获取统计开关
     */
    public static boolean getStatisticSwitch() {
        if (getConfig() == null) {
            return true;
        }
        return getConfig().optBoolean(HttpConstants.Response.StatisticConfig.ISUPLOAD_B, true);
    }

    public int getWifiMaxUploadSize() {
        if (getConfig() == null) {
            return Constants.DefaultConfigParameters.WIFI_MAX_UPLOAD_FLOW;
        }
        return getConfig().optInt(HttpConstants.Response.StatisticConfig.WIFI_MAX_I,
                Constants.DefaultConfigParameters.WIFI_MAX_UPLOAD_FLOW);
    }

    public int getWifiMinUploadSize() {
        if (getConfig() == null) {
            return Constants.DefaultConfigParameters.WIFI_MIN_UPLOAD_FLOW;
        }
        return getConfig().optInt(HttpConstants.Response.StatisticConfig.WIFI_MIN_I,
                Constants.DefaultConfigParameters.WIFI_MIN_UPLOAD_FLOW);
    }

    public int getGprsMaxUploadSize() {
        if (getConfig() == null) {
            return Constants.DefaultConfigParameters.GPRS_MAX_UPLOAD_FLOW;
        }
        return getConfig().optInt(HttpConstants.Response.StatisticConfig.GPRS_MAX_I,
                Constants.DefaultConfigParameters.GPRS_MAX_UPLOAD_FLOW);

    }

    public int getGprsMinUploadSize() {
        if (getConfig() == null) {
            return Constants.DefaultConfigParameters.GPRS_MIN_UPLOAD_FLOW;
        }
        return getConfig().optInt(HttpConstants.Response.StatisticConfig.GPRS_MIN_I,
                Constants.DefaultConfigParameters.GPRS_MIN_UPLOAD_FLOW);

    }

    public int getDatabaseTableMaxEventNumber() {
        if (getConfig() == null) {
            return Constants.DefaultConfigParameters.LOACL_MAX_EVENT_NUMBER;
        }
        return getConfig().optInt(HttpConstants.Response.StatisticConfig.SD_SIZE_I,
                Constants.DefaultConfigParameters.LOACL_MAX_EVENT_NUMBER);
    }

    public int getAppeventCountWhenCheckUpload() {
        if (getConfig() == null) {
            return Constants.DefaultConfigParameters.DEFAULT_APP_EVENT_COUNT_WHEN_CHECK_UPLOAD;
        }
        return getConfig().optInt(HttpConstants.Response.StatisticConfig.SDK_LISTEN_NUM_I,
                Constants.DefaultConfigParameters.DEFAULT_APP_EVENT_COUNT_WHEN_CHECK_UPLOAD);
    }

    public int getLocalMaxSize() {
        if (getConfig() == null) {
            return Constants.DefaultConfigParameters.LOACL_MAX_SAVE_CAPACITY;
        }
        return getConfig().optInt(HttpConstants.Response.StatisticConfig.LOCAL_ST_SIZE_I,
                Constants.DefaultConfigParameters.LOACL_MAX_SAVE_CAPACITY);

    }

}
