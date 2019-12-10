package com.gionee.client.business.push;

import java.util.ArrayList;
import java.util.List;

import com.gionee.client.model.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class BaiduPushUtils {
	public static final String TAG = "BaiduPushUtils";
	private static final String BIND_FLAG = "bind_flag";
	private static final String PUSH_SWITCH_FLAG = "push_switch_flag";
	private static final String WIFI_SWITCH_FLAG = "wifi_switch_flag";
	private static final String IS_SUBMIT_STATISTICS = "is_submit_Statistics";
	private static final String STATISTICS_SOURCE = "tatistics_source";

	// 获取ApiKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}

	// 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
	public static boolean hasBind(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String flag = sp.getString(BIND_FLAG, "");
		if ("ok".equalsIgnoreCase(flag)) {
			return true;
		}
		return false;
	}

	public static void setBind(Context context, boolean flag) {
		String flagStr = "not";
		if (flag) {
			flagStr = "ok";
		}
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString(BIND_FLAG, flagStr);
		editor.commit();
	}

	public static List<String> getTagsList(String originalText) {
		if (originalText == null || originalText.equals("")) {
			return null;
		}
		List<String> tags = new ArrayList<String>();
		int indexOfComma = originalText.indexOf(',');
		String tag;
		while (indexOfComma != -1) {
			tag = originalText.substring(0, indexOfComma);
			tags.add(tag);

			originalText = originalText.substring(indexOfComma + 1);
			indexOfComma = originalText.indexOf(',');
		}

		tags.add(originalText);
		return tags;
	}

	public static void setPushSwich(Context context, boolean flag) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putBoolean(PUSH_SWITCH_FLAG, flag);
		editor.commit();
	}

	public static boolean getPushSwich(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(PUSH_SWITCH_FLAG, true);
	}

	public static void setWifiSwich(Context context, boolean flag) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putBoolean(WIFI_SWITCH_FLAG, flag);
		editor.commit();
	}

	public static boolean getWifiSwich(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(WIFI_SWITCH_FLAG, true);
	}

	public static void setIsSubmitStatistics(Context context, boolean flag,
			String source) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putBoolean(IS_SUBMIT_STATISTICS, flag);
		editor.putString(STATISTICS_SOURCE, source);
		editor.commit();
	}

	public static boolean getIsSubmitStatistics(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(IS_SUBMIT_STATISTICS, false);
	}
	
	public static String getStatisticsSource(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(STATISTICS_SOURCE, Constants.BootSource.NORMAL);
	}
}
