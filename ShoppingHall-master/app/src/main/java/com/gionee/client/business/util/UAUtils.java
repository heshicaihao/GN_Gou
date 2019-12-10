package com.gionee.client.business.util;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.gionee.client.R;
import com.gionee.client.business.statistic.header.PublicHeaderParamsManager;

public class UAUtils {

    private static final String TAG = "UAUtils";
    private static final String AES = "AES";
    private static final String VIPARA = "0102030405060708";
    private static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";
    private static final String SEED = "GIONEE2012061900";
    private static final String HEX = "0123456789ABCDEF";
    private static final String CHARSET = "UTF-8";

    public static String getUserAgent(Context context) {
        long startTime = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        sb.append(getUserString());
        sb.append(" Id/");
        sb.append(getImei(context));
        sb.append(" RV/");
        sb.append(getRomVer());
        sb.append(";gngouua");
        sb.append(getGnGouVersion(context, "1.1"));
        sb.append(" chl/");
        sb.append(context.getString(R.string.ua_channel));
        sb.append(" uid/");
        sb.append(PublicHeaderParamsManager.getUid(context));
        LogUtils.logd(TAG, LogUtils.getThreadName() + "UA:" + sb.toString());
        LogUtils.log(TAG, LogUtils.getThreadName() + "elapsed time:"
                + (System.currentTimeMillis() - startTime) + "ms");
        return sb.toString();
    }

    public static String getStatisticsUserAgent(Context context) {
        long startTime = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        sb.append(getUserString());
        sb.append(" Id/");
        sb.append(getImei(context));
        sb.append(" RV/");
        sb.append(getRomVer());
        sb.append("; gngouua");
        sb.append(getGnGouVersion(context, "1.1"));
        sb.append(" chl/");
        sb.append(context.getString(R.string.ua_channel));
        sb.append(" uid/");
        sb.append(PublicHeaderParamsManager.getUid(context));
        LogUtils.logd(TAG, LogUtils.getThreadName() + "UA:" + sb.toString());
        LogUtils.log(TAG, LogUtils.getThreadName() + "elapsed time:"
                + (System.currentTimeMillis() - startTime) + "ms");
        return sb.toString();
    }

    public static String getUrlUserAgent(Context context) {
        StringBuffer sb = new StringBuffer();
        sb.append("m=");
        sb.append(getPhoneInfo());
        sb.append("&Id=");
        sb.append(getImei(context));
        sb.append("&cv=");
        sb.append(getGnGouVersion(context, "1.1"));
        sb.append("&source=");
        sb.append(context.getString(R.string.ua_channel));
        sb.append("&rv=");
        sb.append(getRomVer());
        sb.append(" uid/");
        sb.append(PublicHeaderParamsManager.getUid(context));
        return sb.toString();
    }

    private static String getRomVer() {
        String romVer = AndroidUtils.getSystemProperties("ro.gn.gnromvernumber", "GiONEE ROM4.0.1");
        String ver = romVer.substring(romVer.indexOf("M") == -1 ? 0 : (romVer.indexOf("M") + 1));
        return ver;
    }

    public static String getImei(Context context) {
        String imei = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
        } catch (Exception e) {
            LogUtils.log(TAG, LogUtils.getFunctionName() + ":" + e);
        }
        String decodeImei = getEncryptingCode(imei);
        return decodeImei;
    }

    private static String getGnGouVersion(Context context, String version) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return version;
        }
    }

    @SuppressLint("DefaultLocale")
    private static String getUserString() {
        Locale local = Locale.getDefault();
        String slocaleInfo = local.getLanguage() + "-" + local.getCountry().toLowerCase();
        StringBuffer sb = new StringBuffer();
        sb.append("Mozilla/5.0 (Linux; U; Android ");
        sb.append(Build.VERSION.RELEASE);
        sb.append("; ");
        sb.append(slocaleInfo);
        sb.append("; ");
        sb.append(getPhoneInfo());
        sb.append(" Build/IMM76D) AppleWebKit/534.30 (KHTML,like Gecko) Version/4.0 Mobile Safari/534.30");
        return sb.toString();
    }

    // Gionee <hcy><2013-7-1> modify for CR00825880 begin

    @SuppressWarnings("deprecation")
    public static String getPhoneInfo() {
        String brand = getPhoneBrand();
        String model = getPhoneModel();
        String extModel = getPhoneExtModel();
        StringBuffer sb = new StringBuffer();
        sb.append(URLEncoder.encode(brand));
        sb.append("-");
        sb.append(URLEncoder.encode(model));
        sb.append("/");
        sb.append(URLEncoder.encode(extModel));
        return sb.toString();
    }

    // Gionee <hcy><2013-7-1> modify for CR00825880 end

    public static String getEncryptingCode(String str) {
        if (str == null) {
            str = "";
        }
        String masterPassword = SEED;
        try {
            String encryptingCode = encrypt(masterPassword, str);
            return encryptingCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("NewApi")
    public static String encrypt(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes(Charset.forName("UTF-8")));
        byte[] result = encrypt(rawKey, cleartext.getBytes(CHARSET));
        return toHex(result);
    }

    public static byte[] getRawKey(byte[] seed) throws Exception {
        return seed;
    }

    @SuppressLint("NewApi")
    public static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes(Charset.forName("UTF-8")));
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, zeroIv);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    @SuppressLint("NewApi")
    public static String toHex(String txt) {
        return toHex(txt.getBytes(Charset.forName("UTF-8")));
    }

    public static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    // Gionee <hcy><2012-6-17> add for CR00825880 begin
    public static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    public static String getPhoneBrand() {
        return AndroidUtils.getSystemProperties("ro.product.brand", "GiONEE");
    }

    public static String getPhoneModel() {
        return AndroidUtils.getSystemProperties("ro.product.model", "Phone");
    }

    public static String getPhoneExtModel() {
        return AndroidUtils.getSystemProperties("ro.gn.op_special_vn", "Phone");
    }

    public static String getPhoneSign() {
        StringBuilder sb = new StringBuilder();
        sb.append(Build.BOARD).append(",").append(Build.BRAND).append(",").append(Build.CPU_ABI).append(",")
                .append(Build.DEVICE).append(",").append(Build.DISPLAY).append(",").append(Build.HOST)
                .append(",").append(Build.ID).append(",").append(Build.MANUFACTURER).append(",")
                .append(Build.MODEL).append(",").append(Build.PRODUCT).append(",").append(Build.TAGS)
                .append(",").append(Build.TYPE).append(",").append(Build.USER);

        return sb.toString();
    }

    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        LogUtils.log(TAG, LogUtils.getFunctionName() + info.getMacAddress());
        return info.getMacAddress();
    }

    public static String getAndroidID(Context context) {
        LogUtils.log(TAG, "android id :" + Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }
    // Gionee <hcy><2012-6-17> add for CR00825880 end
}
