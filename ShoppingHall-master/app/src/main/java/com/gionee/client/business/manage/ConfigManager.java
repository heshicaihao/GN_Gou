// Gionee <yuwei><2014-11-25> add for CR00821559 begin
/*
 * ConfigManager.java
 * classes : com.gionee.client.business.manage.ConfigManager
 * @author yuwei
 * V 1.0.0
 * Create at 2014-11-25 下午7:56:31
 */
package com.gionee.client.business.manage;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.StringUtills;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.framework.operation.utills.StringUtils;

/**
 * com.gionee.client.business.manage.ConfigManager
 * 
 * @author yuwei <br/>
 * @date create at 2014-11-25 下午7:56:31
 * @description TODO
 */
public class ConfigManager {
	private static final String TAG = "ConfigManager";
	private static ConfigManager sInstance;
	// private JSONObject mJSONObject;
	private Context mContext;
	private static final String ACTIVATE = "activate";
	private static final String CURRENT_VERSION = "current_version";
	public static final String TAOBAO_SEARCH_AUTOFILL_URL = "taobao_search_autofill_url";
	public static final String UPGRATE_DATA = "upgrate_data";

	private ConfigManager() {
		// TODO Auto-generated constructor stub
	}

	public static ConfigManager getInstance() {
		if (sInstance == null) {
			sInstance = new ConfigManager();
		}
		return sInstance;
	}

	public void init(Context context, JSONObject object) {
		mContext = context.getApplicationContext();
		if (object == null) {
			LogUtils.log(TAG, LogUtils.getThreadName() + " object == null ");
			return;
		}
		try {
			boolean isOutOfData = object
					.optBoolean(HttpConstants.Response.MatchRegular.IS_OUT_OF_DATA_B);
			String mIsExcuteJs = object
					.optString(HttpConstants.Response.MatchRegular.JAVA_SCRIPT_SWITCH_I);
			LogUtils.log("ConfigManager", LogUtils.getThreadName() + "object="
					+ object.toString());
			ShareDataManager.putBoolean(mContext,
					HttpConstants.Response.MatchRegular.IS_OUT_OF_DATA_B,
					isOutOfData);
			JSONObject cutConfigObj = object
					.optJSONObject(HttpConstants.Response.MatchRegular.CUT_JO);
			JSONObject scoreObject = object
					.optJSONObject(HttpConstants.Response.MatchRegular.SCORE_JO);
			saveCutPriceConfig(cutConfigObj);
			saveScoreConfig(context, scoreObject);
			ShareDataManager.putString(mContext,
					HttpConstants.Response.MatchRegular.JAVA_SCRIPT_SWITCH_I,
					mIsExcuteJs);
			String secretKey = object
					.optString(HttpConstants.Response.MatchRegular.SECRET_KEY);
			ShareDataManager.putString(mContext,
					HttpConstants.Response.MatchRegular.SECRET_KEY, secretKey);
			// saveQuestionImportConfig(object);
			ShareDataManager
					.putBoolean(
							mContext,
							ACTIVATE,
							object.optBoolean(HttpConstants.Response.MatchRegular.activate_b));
			ShareDataManager.putString(mContext, CURRENT_VERSION, object
					.optString(HttpConstants.Response.MatchRegular.version_s));
			String searchAutoUrl = object
					.optString(HttpConstants.Response.MatchRegular.TAOBAO_SEARCH_AUTOFILL_URL);
			if (searchAutoUrl != null && !"".equals(searchAutoUrl)) {
				searchAutoUrl = searchAutoUrl.replace("&callback=jsonp1211212",
						"");
				ShareDataManager.putString(mContext,
						TAOBAO_SEARCH_AUTOFILL_URL, searchAutoUrl);
			}
		} catch (Exception e) {
			LogUtils.log(TAG, LogUtils.getThreadName() + " exception: " + e);
			e.printStackTrace();
		}
	}

	public static boolean isScore(Context context) {
		return ShareDataManager.getBoolean(context,
				HttpConstants.Response.MatchRegular.IS_SCORE_S, false);
	}

	private void saveScoreConfig(Context context, JSONObject scoreObject) {
		try {
			if (scoreObject != null) {
				String scoreUrl = scoreObject
						.optString(HttpConstants.Response.MatchRegular.SCORE_URL_S);
				boolean isScore = scoreObject
						.optBoolean(HttpConstants.Response.MatchRegular.IS_SCORE_S);
				String moreBgUrl = scoreObject
						.optString(HttpConstants.Response.MatchRegular.IMG_URL_S);
				LogUtils.log("ConfigManager", LogUtils.getThreadName()
						+ "scoreUrl=" + scoreUrl);
				ShareDataManager.putString(mContext,
						HttpConstants.Response.MatchRegular.SCORE_URL_S,
						scoreUrl);
				ShareDataManager
						.putBoolean(context,
								HttpConstants.Response.MatchRegular.IS_SCORE_S,
								isScore);
				ShareDataManager.putString(context,
						HttpConstants.Response.MatchRegular.IMG_URL_S,
						moreBgUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveUpgrateData(Context context, JSONObject object) {
		Context mContext = context.getApplicationContext();
		ShareDataManager.putString(mContext, UPGRATE_DATA, object.toString());
	}

	public JSONObject getUpgrateData(Context context) {
		Context mContext = context.getApplicationContext();
		String bean = ShareDataManager.getString(mContext, UPGRATE_DATA, "");
		if (StringUtils.isEmpty(bean)) {
			return null;
		}
		try {
			JSONObject object = new JSONObject(bean);
			return object;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getMoreBgUrl(Context context) {
		Context mContext = context.getApplicationContext();
		return ShareDataManager.getString(mContext,
				HttpConstants.Response.MatchRegular.IMG_URL_S, "");
	}

	private void saveCutPriceConfig(JSONObject cutConfigObj) {
		try {
			if (cutConfigObj != null) {
				String orderListUrl = cutConfigObj
						.optString(HttpConstants.Response.MatchRegular.ORDER_LIST_URL_S);
				String cutRuleUrl = cutConfigObj
						.optString(HttpConstants.Response.MatchRegular.CUT_RULE_URL_S);
				ShareDataManager.putString(mContext,
						HttpConstants.Response.MatchRegular.ORDER_LIST_URL_S,
						orderListUrl);
				ShareDataManager.putString(mContext,
						HttpConstants.Response.MatchRegular.CUT_RULE_URL_S,
						cutRuleUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveQuestionImportConfig(JSONObject object) {
		boolean isQuestionImportOpen = object
				.optBoolean(HttpConstants.Response.MatchRegular.IS_QUESION_IMPORT_OPEN);
		LogUtils.log(TAG, LogUtils.getThreadName() + " isQuestionImportOpen = "
				+ isQuestionImportOpen);
		ShareDataManager.putBoolean(mContext,
				HttpConstants.Response.MatchRegular.IS_QUESION_IMPORT_OPEN,
				isQuestionImportOpen);
	}

	public static boolean isQuestionImportOpen(Context context) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		// return ShareDataManager.getBoolean(contex，
		// HttpConstants.Response.MatchRegular.IS_QUESION_IMPORT_OPEN, false);
		return false;
	}

	public String getOrderListUrl(Context context) {
		Context mContext = context.getApplicationContext();
		return ShareDataManager.getString(mContext,
				HttpConstants.Response.MatchRegular.ORDER_LIST_URL_S,
				Url.BARGAIN_ORDER_URL);
	}

	public String getCutRuleUrl(Context context) {
		Context mContext = context.getApplicationContext();
		return ShareDataManager.getString(mContext,
				HttpConstants.Response.MatchRegular.CUT_RULE_URL_S,
				Url.BARGAIN_RULE_URL);
	}

	public String getIsExcuteJs(Context context) {
		Context mContext = context.getApplicationContext();
		return ShareDataManager.getString(mContext,
				HttpConstants.Response.MatchRegular.JAVA_SCRIPT_SWITCH_I, "");
	}

	public static String getSecretKey(Context context) {
		Context mContext = context.getApplicationContext();
		return ShareDataManager.getString(mContext,
				HttpConstants.Response.MatchRegular.SECRET_KEY, "");
	}

	public boolean getIsActivity(Context context) {
		return ShareDataManager.getBoolean(context, ACTIVATE, false);
	}

	public String getCurrentVersion(Context context) {
		return ShareDataManager.getString(context, CURRENT_VERSION, "");
	}
}
