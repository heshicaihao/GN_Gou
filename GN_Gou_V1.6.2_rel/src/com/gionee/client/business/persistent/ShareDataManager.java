package com.gionee.client.business.persistent;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class ShareDataManager {

    public static final String NAME = "init";
    private static final String PREFNAME = "json";

    @SuppressLint("InlinedApi")
    public static long getInterfaceDataVersionNumber(Context context, String key) {
        if (context == null) {
            return 1;
        }
        SharedPreferences preference = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        return preference.getLong(key, 1);
    }

    @SuppressLint("InlinedApi")
    public static void setInterfaceDataVersionNumber(Context context, String key, long versionCode) {
        if (context == null) {
            return;
        }
        SharedPreferences preference = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        preference.edit().putLong(key, versionCode).commit();
    }

    @SuppressLint("InlinedApi")
    public static void saveDataAsInt(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        preferences.edit().putInt(key, value).commit();
    }

    @SuppressLint("InlinedApi")
    public static int getDataAsInt(Context context, String key, int defValue) {
        int result = -1;
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        result = preferences.getInt(key, defValue);
        return result;
    }

    @SuppressLint("InlinedApi")
    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        float result = preferences.getFloat(key, defaultValue);
        return result;
    }

    @SuppressLint("InlinedApi")
    public static void putFloat(Context context, String key, float value) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        preferences.edit().putFloat(key, value).commit();
    }

    @SuppressLint("InlinedApi")
    public static void saveDataAsLong(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        preferences.edit().putLong(key, value).commit();
    }

    @SuppressLint("InlinedApi")
    public static long getDataAsLong(Context context, String key, long defValue) {
        long result = -1;
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        result = preferences.getLong(key, defValue);
        return result;
    }

    @SuppressLint("InlinedApi")
    public static void putBoolean(Context context, String key, boolean value) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
            preferences.edit().putBoolean(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("InlinedApi")
    public static boolean getBoolean(Context context, String key, boolean defValue) {

        SharedPreferences preferences;
        try {
            preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
            return preferences.getBoolean(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @SuppressLint("InlinedApi")
    public static void putString(Context context, String key, String value) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
            preferences.edit().putString(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("InlinedApi")
    public static void putInt(Context context, String key, int value) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
            preferences.edit().putInt(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("InlinedApi")
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences preferences;
        try {
            preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
            return preferences.getString(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("InlinedApi")
    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences preferences;
        try {
            preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
            return preferences.getInt(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @SuppressLint("InlinedApi")
    public static void putJsonArray(Context context, String key, JSONArray js) {
        if (js == null)
            return;
        SharedPreferences settings = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, js.toString());
        editor.commit();
    }

    @SuppressLint("InlinedApi")
    public static JSONArray getJSONArray(Context context, String key) throws JSONException {
        SharedPreferences settings = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        return new JSONArray(settings.getString(key, "[]"));
    }

    @SuppressLint("InlinedApi")
    public static void removeReferece(Context context, String key) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
            preferences.edit().remove(key).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
