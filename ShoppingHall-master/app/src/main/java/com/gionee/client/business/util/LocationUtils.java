// Gionee <yuwei><2014-10-15> add for CR00821559 begin
/*
 * LocationUtils.java
 * classes : com.gionee.client.business.util.LocationUtils
 * @author yuwei
 * V 1.0.0
 * Create at 2014-10-15 下午4:22:56
 */
package com.gionee.client.business.util;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.pm.UserInfo;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.gionee.client.R;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.persistent.ShareDataManager;

/**
 * com.gionee.client.business.util.LocationUtils
 * 
 * @author yuwei <br/>
 * @date create at 2014-10-15 下午4:22:56
 * @description TODO
 */
public class LocationUtils {

    private static String cityName; // 城市名
    private static String mNickName;
    private static Geocoder geocoder; // 此对象能通过经纬度来获取相应的城市等信息
    private static LocationManager locationManager;

    /**
     * 通过地理坐标获取城市名 其中CN分别是city和name的首字母缩写
     * 
     * @param context
     */
    public static void startLocation(Context context) {

        try {
            geocoder = new Geocoder(context);
            // 用于获取Location对象，以及其他

            String serviceName = Context.LOCATION_SERVICE;
            // 实例化一个LocationManager对象
            locationManager = (LocationManager) context.getSystemService(serviceName);
            // provider的类型
            String provider = LocationManager.NETWORK_PROVIDER;
            initCriteria();
            // 通过最后一次的地理位置来获得Location对象
            Location location = locationManager.getLastKnownLocation(provider);
            String queryed_name = updateWithNewLocation(location);
            if ((queryed_name != null) && (0 != queryed_name.length())) {

                cityName = queryed_name;
            }

            locationManager.requestLocationUpdates(provider, 10000, 50, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void stopLocation() {
        // 移除监听器，在只有一个widget的时候，这个还是适用的
        try {
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private static void initCriteria() {
        try {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
            criteria.setAltitudeRequired(false); // 不要求海拔
            criteria.setBearingRequired(false); // 不要求方位
            criteria.setCostAllowed(false); // 不允许有话费
            criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getNickName(Context context) {
//        if (TextUtils.isEmpty(mNickName) || mNickName.contains("null")) {
//            if (TextUtils.isEmpty(cityName) || cityName.contains("null")) {
//                return context.getString(R.string.app_name_cn) + context.getString(R.string.other_erea)
//                        + context.getString(R.string.net_friends);
//            } else {
//                return context.getString(R.string.app_name_cn) + cityName
//                        + context.getString(R.string.net_friends);
//            }
//        }
        return UserInfoManager.getInstance().getNickName(context);

    }

    public static void SetNickName(String nickName) {
        if (TextUtils.isEmpty(nickName)) {
            return;
        }
        UserInfoManager.getInstance().setNickName(nickName);

    }

    /**
     * 方位改变时触发，进行调用
     */
    private final static LocationListener locationListener = new LocationListener() {
        String tempCityName;

        public void onLocationChanged(final Location location) {

            tempCityName = updateWithNewLocation(location);
            if ((tempCityName != null) && (tempCityName.length() != 0)) {

                cityName = tempCityName;
            }
        }

        public void onProviderDisabled(String provider) {

            tempCityName = updateWithNewLocation(null);
            if ((tempCityName != null) && (tempCityName.length() != 0)) {

                cityName = tempCityName;
            }

        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * 更新location
     * 
     * @param location
     * @return cityName
     */
    private static String updateWithNewLocation(Location location) {
        StringBuilder mcityName = new StringBuilder();
        double lat = 0;
        double lng = 0;
        List<Address> addList = null;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {

            System.out.println("无法获取地理信息");
        }

        try {

            addList = geocoder.getFromLocation(lat, lng, 1); // 解析经纬度

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address add = addList.get(i);
                mcityName.append(add.getLocality());
            }
        }
        if (mcityName.length() != 0) {

            return mcityName.substring(0, (mcityName.length() - 1));
        } else {
            return mcityName.toString();
        }
    }

//    /**
//     * 通过经纬度获取地址信息的另一种方法
//     * 
//     * @param latitude
//     * @param longitude
//     * @return 城市名
//     */
//    public static String GetAddr(String latitude, String longitude) {
//        String addr = "";
//
//        /* 
//         * 也可以是http://maps.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s，不过解析出来的是英文地址 
//         * 密钥可以随便写一个key=abc 
//         * output=csv,也可以是xml或json，不过使用csv返回的数据最简洁方便解析     
//         */
//        String url = String.format("http://ditu.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s", latitude,
//                longitude);
//        URL myURL = null;
//        URLConnection httpsConn = null;
//        try {
//
//            myURL = new URL(url);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        try {
//
//            httpsConn = (URLConnection) myURL.openConnection();
//
//            if (httpsConn != null) {
//                InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
//                BufferedReader br = new BufferedReader(insr);
//                String data = null;
//                if ((data = br.readLine()) != null) {
//                    String[] retList = data.split(",");
//                    if (retList.length > 2 && ("200".equals(retList[0]))) {
//                        addr = retList[2];
//                    } else {
//                        addr = "";
//                    }
//                }
//                insr.close();
//            }
//        } catch (IOException e) {
//
//            e.printStackTrace();
//            return null;
//        }
//        return addr;
//    }

}