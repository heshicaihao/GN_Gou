//Gionee <wangyy><2013-12-09> modify for CR00956169 begin
package com.gionee.client.business.statistic.util;

import java.io.Closeable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.gionee.appupgrade.common.utils.GNDecodeUtils;
import com.gionee.client.business.statistic.business.Constants;
import com.gionee.client.business.util.LogUtils;

public class Utils {

//    private static final String TAG = "Utils";
    private static String sImei = Constants.DefaultSDKConfig.DEFAULT_IMEI;
    private static final long ONE_DAY_MILLISECONDS = 1000 * 60 * 60 * 24;

    public static boolean isDateToday(long time) {
        long now = System.currentTimeMillis();
        long t1 = time / ONE_DAY_MILLISECONDS;
        long t2 = now / ONE_DAY_MILLISECONDS;
        return t1 == t2;
    }

    /**
     * 判断对象是否非空
     * 
     * @param object
     * @return true:空 false:非空
     */
    public static boolean isNull(Object object) {
        if (object == null) {
            return true;
        }

        return false;
    }

    /**
     * 判断对象是否非空
     * 
     * @param object
     * @return true:非空 false:空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static boolean isStringNull(CharSequence str) {
        return TextUtils.isEmpty(str);
    }

    public static boolean isStringNotNull(CharSequence str) {
        return !TextUtils.isEmpty(str);
    }

    /**
     * 获取错误的信息
     * 
     * @param arg1
     * @return
     */
    public static String getErrorInfo(Throwable arg1) {
        PrintWriter pw = null;
        Writer writer = null;
        try {
            writer = new StringWriter();
            pw = new PrintWriter(writer);
            arg1.printStackTrace(pw);
            pw.flush();
            String error = writer.toString();
            return error;
        } finally {
            closeIOStream(pw);
            closeIOStream(writer);
        }
    }

    /**
     * 获取手机的版本信息
     * 
     * @return
     */
    public static String getVersionInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return "App version: " + info.versionName + " ";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unkown version: ";
        }
    }

    /**
     * 关闭io流
     * 
     * @param closeable
     */
    public static void closeIOStream(Closeable... closeable) {
        if (isNull(closeable)) {
            return;
        }
        for (Closeable ca : closeable) {
            try {
                if (Utils.isNull(ca)) {
                    continue;
                }
                ca.close();
                ca = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭游标
     * 
     * @param cursor
     */
    public static void closeCursor(Cursor... cursor) {
        if (Utils.isNull(cursor)) {
            return;
        }
        for (Cursor cr : cursor) {
            try {
                if (Utils.isNull(cr)) {
                    continue;
                }
                cr.close();
                cr = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * save imei
     * 
     * @return
     * @hide
     */
    public static void setImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return;
        }

        String imei = telephonyManager.getDeviceId();

        if (Utils.isStringNull(imei)) {
            return;
        }

        sImei = imei;
    }

    public static String getImei() {
        if (Utils.isStringNotNull(sImei)) {
            return sImei;
        }
        return Constants.DefaultSDKConfig.DEFAULT_IMEI;
    }

    public static String getUaString(String imei) {
        try {
            @SuppressWarnings("rawtypes")
            Class productConfigurationClass = Class.forName("com.amigo.utils.ProductConfiguration");
            @SuppressWarnings("unchecked")
            Method method = productConfigurationClass.getMethod("getUAString", String.class);
            return (String) method.invoke(productConfigurationClass, imei);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String brand = SystemProperties.get("ro.product.brand", "GiONEE");
        String model = SystemProperties.get("ro.product.model", "Phone");
        String extModel = SystemProperties.get("ro.gn.extmodel", "Phone");
        String ver = getGioneeRomVersion();
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry().toLowerCase(Locale.CHINESE);
        String decodeImei = GNDecodeUtils.get(imei);
        String uaString = "Mozilla/5.0 (Linux; U; Android " + Build.VERSION.RELEASE + "; " + language + "-"
                + country + ";" + brand + "-" + model + "/" + extModel
                + " Build/IMM76D) AppleWebKit534.30(KHTML,like Gecko)Version/4.0 Mobile Safari/534.30 Id/"
                + decodeImei + " RV/" + ver;
        LogUtils.logd("uaString", "uaString=" + uaString);
        return uaString;
    }

    public static int getFirstNumIndex(String string) {
        String regex = "\\d";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        int index = 0;
        if (matcher.find() && !"".equals(matcher.group())) {
            index = matcher.start();
        }
        return index;
    }

    public static String getGioneeRomVersion() {
        String gioneeRomPropString = SystemProperties.get("ro.gn.gnromvernumber", "");
        int index = getFirstNumIndex(gioneeRomPropString);
        return gioneeRomPropString.substring(index);
    }

    public static String subStringIfNeeded(final String string, int needLength) {
        if (Utils.isStringNull(string)) {
            return "";
        }

        String result = "";
        if (string.length() > needLength) {
            result = string.substring(0, needLength);
        } else {
            result = string;
        }
        return result;
    }

    public static String getProperStringFromMap(final Map<String, Object> map) {
        Map<String, String> resultMap = new HashMap<String, String>();
        int needStringLength = Constants.DefaultSDKConfig.CFG_DEFAULT_MAX_STRING_LENGTH;
        Set<String> keySet = map.keySet();
        int i = 0;
        for (String key : keySet) {

            if (i == Constants.DefaultSDKConfig.CFG_DEFAULT_MAX_MAP_SIZE) {
                break;
            }

            String rightKey = Utils.subStringIfNeeded(key, needStringLength);
//            String rightValue = Utils.subStringIfNeeded(map.get(key).toString(), needStringLength);
            String rightValue = map.get(key).toString();
            resultMap.put(rightKey, rightValue);
            i++;
        }

        return new JSONObject(resultMap).toString();
    }

    public static long changeMillisecondToSecond(long millisecond) {
        return millisecond / 1000;
    }
}
//Gionee <wangyy><2013-12-09> modify for CR00956169 end
