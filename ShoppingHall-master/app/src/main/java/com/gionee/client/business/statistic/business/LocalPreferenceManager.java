//Gionee <yangxiong><2014-03-12> modify for CR00850885 begin
package com.gionee.client.business.statistic.business;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gionee.client.business.statistic.util.Utils;

public class LocalPreferenceManager {
    public static final String TAG = "LocalPreferenceManager";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public LocalPreferenceManager(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPreferences.edit();
    }

    public void resetDataInfoGotten() {
        mEditor.putString(Constants.PreferenceKeys.KEY_MAX_ID_GOTTEN_APP_EVENT, null);
        mEditor.putInt(Constants.PreferenceKeys.KEY_DATA_BYTES_GOTTEN, 0);
        mEditor.commit();
    }
    
    public void setDataInfoIfSuccess() {
        if (!mPreferences.getBoolean(Constants.PreferenceKeys.KEY_HAS_UPLOADED_TODAY, false)) {
            mEditor.putBoolean(Constants.PreferenceKeys.KEY_HAS_UPLOADED_TODAY, true);
        }

        int uploadedBytesToday = mPreferences.getInt(Constants.PreferenceKeys.KEY_UPLOADED_BYTES_TODAY, 0);
        int dataBytesGotten = mPreferences.getInt(Constants.PreferenceKeys.KEY_DATA_BYTES_GOTTEN, 0);
        if (dataBytesGotten > 0) {
            mEditor.putInt(Constants.PreferenceKeys.KEY_UPLOADED_BYTES_TODAY, uploadedBytesToday
                    + dataBytesGotten);
        }
        mEditor.putInt(Constants.PreferenceKeys.KEY_DATA_BYTES_GOTTEN, 0);
        mEditor.commit();
    }

    public void resetAllDataInfoIfNextDay() {
        long time = mPreferences.getLong(Constants.PreferenceKeys.KEY_DATE_TODAY, 0);
        if (!Utils.isDateToday(time)) {
            mEditor.putLong(Constants.PreferenceKeys.KEY_DATE_TODAY, System.currentTimeMillis());
            mEditor.putBoolean(Constants.PreferenceKeys.KEY_HAS_UPLOADED_TODAY, false);
            mEditor.putInt(Constants.PreferenceKeys.KEY_UPLOADED_BYTES_TODAY, 0);
            mEditor.commit();
        }
    }

    public int getUploadedSizeToday() {
        return mPreferences.getInt(Constants.PreferenceKeys.KEY_UPLOADED_BYTES_TODAY, 0);
    }

    public boolean isHasUploadedToday() {
        return mPreferences.getBoolean(Constants.PreferenceKeys.KEY_HAS_UPLOADED_TODAY, false);
    }

    public void setDataBytesGotten(int datasize) {
        mEditor.putInt(Constants.PreferenceKeys.KEY_DATA_BYTES_GOTTEN, datasize);
        mEditor.commit();
    }

    public String getSavedMaxAppEventId() {
        return mPreferences.getString(Constants.PreferenceKeys.KEY_MAX_ID_GOTTEN_APP_EVENT, "");
    }

    public void setSavedMaxAppEventId(String name) {
        mEditor.putString(Constants.PreferenceKeys.KEY_MAX_ID_GOTTEN_APP_EVENT, name);
        mEditor.commit();
    }
}
//Gionee <yangxiong><2014-03-12> modify for CR00850885 end