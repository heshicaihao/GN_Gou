/*
 * UserInfoManager.java
 * classes : com.gionee.client.business.manage.UserInfoManager
 * @author yuwei
 * 
 * Create at 2015-3-30 下午3:29:06
 */
package com.gionee.client.business.manage;

import org.json.JSONObject;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;

import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.model.HttpConstants;

/**
 * com.gionee.client.business.manage.UserInfoManager
 * 
 * @author yuwei <br/>
 *         create at 2015-3-30 下午3:29:06
 * @description
 */
public class UserInfoManager {
    private static UserInfoManager sInstance;
//  private JSONObject mJSONObject;
    private JSONObject mUserInfoObj;
    private static final String USER_AVATAR = "user_avatar_url";
    private static final String USER_NICKNAME = "user_nickname";
    private static final String FEEDBACK_REFRESH_TIME = "feedback_refresh_time";
    
    private Context mContext;

    private UserInfoManager() {
        // TODO Auto-generated constructor stub
    }

    public static UserInfoManager getInstance() {
        if (sInstance == null) {
            sInstance = new UserInfoManager();
        }
        return sInstance;
    }

    public void init(Context context, JSONObject object) {
        if (object == null) {
            return;
        }
        mContext = context.getApplicationContext();
        mUserInfoObj = object;
        ShareDataManager.putString(mContext, USER_AVATAR,
                mUserInfoObj.optString(HttpConstants.Response.UserInfo.AVATAR_S));
        ShareDataManager.putString(mContext, USER_NICKNAME,
                mUserInfoObj.optString(HttpConstants.Response.UserInfo.NICKNAME_S));
        saveBooleanToPreference(HttpConstants.Response.UserInfo.IS_EDIT_B);
        saveBooleanToPreference(HttpConstants.Response.UserInfo.FEEDBACK_TIP_B);
        saveStringToPreference(HttpConstants.Response.UserInfo.FEEDBACK_AVATAR_S);
        ShareDataManager.putInt(mContext, FEEDBACK_REFRESH_TIME,
                mUserInfoObj.optInt(HttpConstants.Response.UserInfo.feedback_time_i));
    }

    private void saveStringToPreference(String key) {
        ShareDataManager.putString(mContext, key, mUserInfoObj.optString(key));

    }

    private void saveBooleanToPreference(String key) {
        ShareDataManager.putBoolean(mContext, key, mUserInfoObj.optBoolean(key));

    }

    public String getServiceAvater() {
        return ShareDataManager.getString(mContext, HttpConstants.Response.UserInfo.FEEDBACK_AVATAR_S, "");
    }

    public boolean isHasNewFeedbackReplay() {
        return ShareDataManager.getBoolean(mContext, HttpConstants.Response.UserInfo.FEEDBACK_TIP_B, false);
    }

    public boolean isEditEnable(Context context) {
        return ShareDataManager.getBoolean(context, HttpConstants.Response.UserInfo.IS_EDIT_B, true);
    }

    public String getAvatar(Context context) {
        return ShareDataManager.getString(context, USER_AVATAR, "");
    }

    public boolean isHasNewMsg() {
        if (mUserInfoObj != null && mUserInfoObj.optBoolean(HttpConstants.Response.UserInfo.HAS_MSG_I)) {
            return true;

        }
        return false;
    }

    public String getNickName(Context context) {
        return ShareDataManager.getString(context, USER_NICKNAME, "");
    }

    public int getFeedbackTime(Context context) {
        return ShareDataManager.getInt(context, FEEDBACK_REFRESH_TIME,10);
    }

    
    public void setNickName(String nickName) {
        try {
            if (TextUtils.isEmpty(nickName)) {
                return;
            }
            if (getNickName(mContext).equals(nickName)) {
                return;
            }
            ShareDataManager.putString(mContext, USER_NICKNAME, nickName);
            ShareDataManager.putBoolean(mContext, HttpConstants.Response.UserInfo.IS_EDIT_B, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAvator(String avator) {
        if (TextUtils.isEmpty(avator)) {
            return;
        }
        ShareDataManager.putString(mContext, USER_AVATAR, avator);
    }
}
