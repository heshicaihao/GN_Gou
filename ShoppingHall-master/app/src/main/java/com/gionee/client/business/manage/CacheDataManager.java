// Gionee <yuwei><2013-8-15> add for CR00821559 begin
/*
 * CacheDataManager.java
 * classes : com.gionee.client.business.manage.CacheDataManager
 * @author yuwei
 * V 1.0.0
 * Create at 2013-8-15 上午9:48:04
 */
package com.gionee.client.business.manage;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.InputStreamUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;
import com.gionee.framework.model.config.ControlKey;
import com.gionee.framework.operation.cache.BitmapFileCache;
import com.gionee.framework.operation.cache.FNetDiscCacheManage;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.utills.JSONArrayHelper;
import com.gionee.framework.operation.utills.Md5Utill;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * com.gionee.client.business.manage.CacheDataManager
 * 
 * @author yuwei <br/>
 * @date create at 2013-8-15 上午9:48:04
 * @description TODO
 */
public class CacheDataManager {
    /**
     * 
     */
    private static final String LOGO_LINK_URL = "logo_link_url";
    // private final String TAG = "CacheDataManager";
    private static final String TAG = "CacheDataManager_Loading";
    private static final String CACHE_CONFIT_PREFERENCE = "cache_config";
    private static final String LAST_LOADING_RUL = "last_loading_url";
    private static final String LAST_START_TIME = "last_start_time";
    private static final String LAST_END_TIME = "last_end_time";
    private static final String LAST_UPDATE_TIME = "last_update_time";
    private static final String LAST_TAB_ARRAY = "last_tab_array";
    private static final String IS_SHOW_REMIDER = "is_show_remider";
    private static final String KEY_ATTENTION_CACHE = "IS_ADDED_TO_ATTENTION_LIST";

    /**
     * 
     * @param context
     * @return the last loading image url
     * @author yuwei
     * @description TODO get the last loading image url,if current time is range between the actvity's start
     *              time and the end time, return the last url, else return ""
     */
    public static String getLastLoadingUrl(Context context) {
        if (context == null) {
            return "";
        }
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        if (currentTime > getStartTime(context) && currentTime < getEndTime(context)) {
            SharedPreferences preference = context.getSharedPreferences(CACHE_CONFIT_PREFERENCE, 0);
            return preference.getString(LAST_LOADING_RUL, "");
        }
        return "";

    }

    public static String getLinkUrl(Context context) {
        SharedPreferences preference = context.getSharedPreferences(CACHE_CONFIT_PREFERENCE, 0);
        Log.d("loadingInfo", "url=" + preference.getString(LOGO_LINK_URL, ""));
        return preference.getString(LOGO_LINK_URL, "");
    }

    /**
     * @param context
     * @return the last time you update the loading info
     * @author yuwei
     * @description TODO get the last time you update the loading info,include the image url ,start time
     *              ,Anatomy.
     */
    public static long getLastUpdateTime(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences preference = context.getSharedPreferences(CACHE_CONFIT_PREFERENCE, 0);
        return preference.getLong(LAST_UPDATE_TIME, 0);

    }

    /**
     * @return the isShowRemider
     */
    public static boolean getIsShownRemider(Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences preference = context.getSharedPreferences(CACHE_CONFIT_PREFERENCE, 0);
        return preference.getBoolean(IS_SHOW_REMIDER, false);
    }

    public static void setIsShowRemider(Context context) {
        if (context == null) {
            return;
        }
        SharedPreferences preference = context.getSharedPreferences(CACHE_CONFIT_PREFERENCE, 0);
        preference.edit().putBoolean(IS_SHOW_REMIDER, true).commit();
    }

    /**
     * @param context
     * @return the activity's start time
     * @author yuwei
     * @description TODO get the activity' start time,the actvity is the last activity in native
     */
    public static int getStartTime(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences preference = context.getSharedPreferences(CACHE_CONFIT_PREFERENCE, 0);
        return preference.getInt(LAST_START_TIME, 0);
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    public static JSONArray getTabArray(Context context) {
        if (context == null) {
            return null;
        }
        SharedPreferences preference = context.getSharedPreferences(CACHE_CONFIT_PREFERENCE, 0);
        String tabArrayStr = preference.getString(LAST_TAB_ARRAY, "");
        if (TextUtils.isEmpty(tabArrayStr)) {
            return null;
        }
        try {
            return new JSONArray(tabArrayStr);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @param context
     * @return the activity's end time
     * @author yuwei
     * @description TODO get the activity's end time, the actvity is the last activity in native
     */
    public static int getEndTime(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences preference = context.getSharedPreferences(CACHE_CONFIT_PREFERENCE, 0);
        return preference.getInt(LAST_END_TIME, 0);
    }

    /**
     * @param context
     * @param loadingInfo
     *            the json date of the activity info from network
     * @author yuwei
     * @description TODO save the last activity information to xml file
     */
    public static void saveLoadingInfo(Context context, final JSONObject loadingInfo) {
        if (loadingInfo == null || context == null) {
            return;
        }
        LogUtils.log(TAG, LogUtils.getFunctionName() + "loadingInfo=" + loadingInfo);
        final String imageUrl = loadingInfo.optString(HttpConstants.Response.GetLoadingInfo.IMAGE_URL_S);
        LogUtils.log(TAG, LogUtils.getFunctionName() + imageUrl);
        JSONArray tabArray = loadingInfo.optJSONArray(HttpConstants.Response.GetLoadingInfo.TAB_A);
        final SharedPreferences preference = context.getSharedPreferences(CACHE_CONFIT_PREFERENCE, 0);
        if (tabArray != null && tabArray.length() > 0) {
            preference.edit().putString(LAST_TAB_ARRAY, tabArray.toString()).commit();
        }
        if (!TextUtils.isEmpty(imageUrl)) {
            GNImageLoader
                    .getInstance()
                    .getImageLoader()
                    .loadImage(imageUrl, GNImageLoader.getInstance().getDefaultOptions(),
                            new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String arg0, View arg1) {
                                    LogUtils.log(TAG, LogUtils.getFunctionName() + imageUrl);
                                }

                                @Override
                                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                                    LogUtils.log(TAG, LogUtils.getFunctionName() + imageUrl);
                                }

                                @Override
                                public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                                    LogUtils.log(TAG, LogUtils.getFunctionName() + imageUrl);
                                    deleteLastImage(imageUrl, preference);
                                    updateLoadingInfo(loadingInfo, imageUrl, preference);
                                }

                                @Override
                                public void onLoadingCancelled(String arg0, View arg1) {
                                    LogUtils.log(TAG, LogUtils.getFunctionName() + imageUrl);
                                }
                            });

        }

    }

    /**
     * @param loadingInfo
     * @param imageUrl
     * @param preference
     * @author yuwei
     * @description TODO update loading activity info in native
     */
    private static void updateLoadingInfo(final JSONObject loadingInfo, final String imageUrl,
            final SharedPreferences preference) {
        try {
            Editor editor = preference.edit();
            editor.putString(LAST_LOADING_RUL, imageUrl);
            editor.putInt(LAST_START_TIME,
                    loadingInfo.optInt(HttpConstants.Response.GetLoadingInfo.START_TIME_I));
            editor.putInt(LAST_END_TIME, loadingInfo.optInt(HttpConstants.Response.GetLoadingInfo.END_TIME_I));
            editor.putLong(LAST_UPDATE_TIME, System.currentTimeMillis());
            editor.putString(LOGO_LINK_URL, loadingInfo.optString(HttpConstants.Response.URL_S));
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param imageUrl
     * @param preference
     * @author yuwei
     * @description TODO delete last Image url
     */
    private static void deleteLastImage(final String imageUrl, final SharedPreferences preference) {
        String lastUrl = preference.getString(CACHE_CONFIT_PREFERENCE, CACHE_CONFIT_PREFERENCE);
        if (!TextUtils.isEmpty(lastUrl)) {
            final String fileName = Md5Utill.makeMd5Sum(imageUrl);
            BitmapFileCache.deleteBitmap(fileName);
        }
    }

    public static boolean resetMyAttionCache(Context context, JSONArray jsonData) {
        try {

            JSONObject json = getAdvertiseJson(context);
            LogUtils.log(TAG, "cache json=" + json.toString());
            JSONObject advertiseData = json.optJSONObject(HttpConstants.Response.DATA);
            advertiseData.put(HttpConstants.Response.MyAttentionChannel.CHANNEL_JA, jsonData);
            json.put(HttpConstants.Response.DATA, advertiseData);
            saveNewAttentionData(context, json);
            return true;

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private static void saveNewAttentionData(Context context, JSONObject json) {
        FNetDiscCacheManage cacheManager = new FNetDiscCacheManage();
        MyBean bean = getAttentionBean(context);
        cacheManager.save(context, bean, json.toString());
    }

    public static JSONArray getAddedAttentionArray(Context context) {
        JSONArray array = new JSONArray();
        try {
            final String key = "ADDED_ATTENTION_ARRAY";
            String addedData = PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
            array = new JSONArray(addedData);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("mAttention_data", e.getMessage());
        }
        return array;
    }

    public static JSONArray getMyAttentionArray(Context context) {
        JSONArray array = new JSONArray();
        try {
            JSONArray localArray = getLocalAttention(context);
            JSONArrayHelper helper = new JSONArrayHelper(array);
            if (localArray != null && localArray.length() > 0) {
                helper.addAll(localArray, false);
            }
            JSONArray addedAttention = getAddedAttentionArray(context);
            deleteFromHistoryCache(context, array, addedAttention);
            helper.addAll(addedAttention, false);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return array;
    }

    /**
     * @param context
     * @param array
     * @param addedAttention
     * @throws JSONException
     * @author yuwei
     * @description TODO
     */
    private static void deleteFromHistoryCache(Context context, JSONArray array, JSONArray addedAttention)
            throws JSONException {
        for (int i = 0; i < addedAttention.length(); i++) {
            for (int j = 0; j < array.length(); j++) {
                if (addedAttention.getJSONObject(i).optInt(HttpConstants.Response.AddChannel.ID_I) == array
                        .getJSONObject(j).optInt(HttpConstants.Response.AddChannel.ID_I)) {
                    JSONArrayHelper helper = new JSONArrayHelper(addedAttention);
                    helper.remove(i);
                    i--;
                    break;
                }
            }

        }
    }

    private static JSONArray getLocalAttention(Context context) throws Exception {
        InputStream in;
        JSONArray array = null;
        in = context.getAssets().open("attention.json");
        String dataStr = InputStreamUtils.inputStreamToString(in);
        array = new JSONArray(dataStr);
        addToAttentionCache(context, array);
        removeDeletedAttention(context, array);

        return array;
    }

    /**
     * @param context
     * @param array
     * @throws JSONException
     * @author yuwei
     * @description TODO
     */
    private static void removeDeletedAttention(Context context, JSONArray array) throws JSONException {
        deleteAttention(context, array);
    }

    /**
     * @param context
     * @param array
     * @throws JSONException
     * @author yuwei
     * @description TODO
     */
    private static void deleteAttention(Context context, JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            int itemId = array.getJSONObject(i).optInt(HttpConstants.Response.AddChannel.ID_I);
            if (!PreferenceManager.getDefaultSharedPreferences(context).contains(createkeyById(itemId))) {
                deleteJsonFromArray(array.getJSONObject(i), array);
                i--;
            }
        }
    }

    /**
     * @param context
     * @param array
     * @throws JSONException
     * @author yuwei
     * @description TODO
     */
    private static void addToAttentionCache(Context context, JSONArray array) throws JSONException {

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_ATTENTION_CACHE, false)) {
            return;
        }
        for (int i = 0; i < array.length(); i++) {
            int itemId = array.getJSONObject(i).optInt(HttpConstants.Response.AddChannel.ID_I);
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(createkeyById(itemId), true).commit();
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_ATTENTION_CACHE, true)
                .commit();
    }

    public static boolean saveAddedAttentionArray(Context context, JSONArray array) {

        final String key = "ADDED_ATTENTION_ARRAY";
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, array.toString())
                .commit();

    }

    public static boolean addDataToAttentionArray(Context context, JSONObject object) {
        if (object == null) {
            return false;
        }
        int itemId = object.optInt(HttpConstants.Response.AddChannel.ID_I);
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(createkeyById(itemId), false)) {
            return false;
        }
        JSONArray oldArray = getAddedAttentionArray(context);
        new JSONArrayHelper(oldArray).addToLast(object);
        saveAddedAttentionArray(context, oldArray);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(createkeyById(itemId), true)
                .commit();
        return true;

    }

    public static boolean deleteFormAttentionArray(Context context, JSONObject object) {
        if (object == null) {
            return false;
        }
        try {
            int itemId = object.optInt(HttpConstants.Response.AddChannel.ID_I);
            JSONArray oldArray = getAddedAttentionArray(context);
            for (int i = 0; i < oldArray.length(); i++) {
                JSONObject jsonData = (JSONObject) oldArray.get(i);
                if (jsonData.optInt(HttpConstants.Response.AddChannel.ID_I) == itemId) {
                    deleteAttention(context, itemId, oldArray, jsonData);
                    saveAddedAttentionArray(context, oldArray);
                    return true;
                }
            }

            JSONArray localArray = getLocalAttention(context);
            for (int i = 0; i < localArray.length(); i++) {
                JSONObject jsonData = (JSONObject) localArray.get(i);
                if (jsonData.optInt(HttpConstants.Response.AddChannel.ID_I) == itemId) {
                    deleteAttention(context, itemId, localArray, jsonData);
                    return true;
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param context
     * @param itemId
     * @param oldArray
     * @param jsonData
     * @author yuwei
     * @description TODO
     */
    private static void deleteAttention(Context context, int itemId, JSONArray oldArray, JSONObject jsonData) {
        new JSONArrayHelper(oldArray).remove(jsonData);
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(createkeyById(itemId)).commit();
    }

    private static String createkeyById(int itemId) {
        final String prefix = "ATTENTION_ITEM_";
        return prefix + itemId;
    }

    public static boolean isAddedAttention(Context context, JSONObject jsonData) {
        int itemId = jsonData.optInt(HttpConstants.Response.AddChannel.ID_I);
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(createkeyById(itemId), false);
    }

    private static JSONObject getAdvertiseJson(Context context) throws JSONException {
        MyBean bean = getAttentionBean(context);
        FNetDiscCacheManage cacheManager = new FNetDiscCacheManage();
        String advertiseJson = cacheManager.load(context, bean);
        JSONObject json = new JSONObject(advertiseJson);
        return json;
    }

    public static JSONArray getAttentionArray(Context context) throws JSONException {
        JSONObject json = getAdvertiseJson(context);
        JSONObject advertiseData = json.optJSONObject(HttpConstants.Response.DATA);
        JSONArray attentionArray = advertiseData
                .optJSONArray(HttpConstants.Response.MyAttentionChannel.CHANNEL_JA);
        return attentionArray;
    }

    private static MyBean getAttentionBean(Context context) {
        MyBean bean = MyBeanFactory.createRequestBean(context);
        bean.put(ControlKey.request.control.__url_s, Url.ATTENTION_CHANNEL_URL);
        bean.put(HttpConstants.Request.VERSION_L,
                ShareDataManager.getInterfaceDataVersionNumber(context, Url.ATTENTION_CHANNEL_URL));
        return bean;
    }

    public static void deleteJsonFromArray(JSONObject jsonData, JSONArray oldArray) {
        JSONArrayHelper jsonHelper = new JSONArrayHelper(oldArray);
        jsonHelper.remove(jsonData);
    }

}
//Gionee <yuwei><2013-1-3> add for CR00821559 end