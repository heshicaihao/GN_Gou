//Gionee <wangyy><2014-03-12> modify for CR00956169 begin
package com.gionee.client.business.statistic.header;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Build;

import com.gionee.client.business.statistic.business.Constants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DeviceUuidFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.UAUtils;
import com.gionee.framework.operation.utills.Md5Utill;

/**
 * Protocol: data_format_version=数据格式协议版本号, uid=手机唯一识别码, encript_imei=已加密的imei, app_version=应用软件版本号 ,
 * event_count=统计事件的条数, event_content=统计事件的内容.
 */

public class PublicHeaderParamsManager {
    public static final String TAG = "PublicHeaderParamsManager";
    public static final String MD5_SIGN = "NTQzY2JmMzJhYTg2N2RvY3Mva2V5";
    /**
     * 以下命名表示其所占的字节数
     */
    public static final int UID_LENGTH = 32;

    // 数据协议格式版本号
    private static final byte mDataFormatProtocolVerNum = Constants.CFG_DATA_FORMAT_PROTOCAL_VERSION;
    private short mLogSizeSum;
    private Context mContext = null;
    private static String sUid = null;

    public PublicHeaderParamsManager(Context context) {
        mContext = context.getApplicationContext();
//        dataInit();
    }

    public static String getUid(Context context) {
        try {
            if (sUid != null) {
                return sUid;
            }
            String phonesign = UAUtils.getPhoneSign();
            String imeiString = UAUtils.getImei(context);
            LogUtils.logd(TAG, LogUtils.getThreadName());
            String uuid = new DeviceUuidFactory(context).getDeviceUuid().toString();
            LogUtils.logd(TAG, "phonesign = " + phonesign + " imei = " + imeiString + " uuid = " + uuid);
            String uid = Md5Utill.makeMd5Sum(new StringBuilder().append(phonesign).append(imeiString)
                    .append(uuid).toString());

            int appStringLength = UID_LENGTH;
            if (uid.length() > appStringLength) {
                uid = uid.substring((uid.length() - appStringLength), uid.length());
            }
            if (uid.length() < appStringLength) {
                uid = uid + "0123456789ABCDEF0123456789ABCDEF".substring(0, appStringLength - uid.length());
            }
            sUid = uid;
            LogUtils.logd(TAG, "uid = " + uid);
            return uid;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getSign(Context context) {

        return Md5Utill.makeMd5Sum(getUid(context) + MD5_SIGN);

    }

    public String getIMEINumber() {
        String deviceId = UAUtils.getImei(mContext);
        LogUtils.logd(TAG, LogUtils.getThreadName() + " IMEI = " + deviceId);
        int length = deviceId.length();
        try {
            // if the length of imei is 14, this is cdma phone
            if (length > 32) {
                deviceId = deviceId.substring(0, 31);
            } else if (length < 32) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 32 - length; i++) {
                    builder.append('0');
                }
                deviceId = builder.append(deviceId).toString();
            }
        } catch (Exception e) {
            LogUtils.loge(TAG, "getIMEINumber() " + e.toString());
            e.printStackTrace();
        }
        return deviceId;
    }

    public String getModel() {
        String model = Build.MODEL;
        String brand = Build.BRAND;
        LogUtils.logd(TAG, LogUtils.getThreadName() + " model = " + model + " , brand = " + brand);
        return brand + "￥" + model;
    }

    public String getSystemVersion() {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " System version = " + android.os.Build.VERSION.RELEASE);
        return android.os.Build.VERSION.RELEASE;
    }

    public String getVersionNameAndLength() {
        String name = AndroidUtils.getAppVersionName(mContext);
        LogUtils.logd(TAG, LogUtils.getThreadName() + " app version name = " + name);
        return name;
    }

    public void setStatisticsDataSum(short sum) {
        LogUtils.logd(TAG, LogUtils.getThreadName() + "sum = " + sum);
        mLogSizeSum = sum;
    }

    /**
     * 事件数据头信息的封装
     */
    public List<NameValuePair> getEventDataHeaderParams() {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();

        /**
         * 数据格式协议版本号
         */
        formparams.add(new BasicNameValuePair("data_format_version", String
                .valueOf(mDataFormatProtocolVerNum)));
        /**
         * 手机唯一识别码
         */
        formparams.add(new BasicNameValuePair("uid", getUid(mContext)));
        /**
         * 加密的imei
         */
        formparams.add(new BasicNameValuePair("encript_imei", getIMEINumber()));
        /**
         * 应用软件版本号
         */
        formparams.add(new BasicNameValuePair("app_version", getVersionNameAndLength()));
        /**
         * 统计事件条数
         */
        formparams.add(new BasicNameValuePair("event_count", String.valueOf(mLogSizeSum)));

        return formparams;
    }

}
//Gionee <wangyy><2014-03-12> modify for CR00956169 end