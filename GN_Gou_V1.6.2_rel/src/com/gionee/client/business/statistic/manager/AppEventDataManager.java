//Gionee <wangyy><2014-03-12> modify for CR00956169 begin
package com.gionee.client.business.statistic.manager;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.gionee.client.business.statistic.business.LocalPreferenceManager;
import com.gionee.client.business.statistic.database.DBFields;
import com.gionee.client.business.statistic.database.LocalDatabaseHelper;
import com.gionee.client.business.statistic.util.MyDatabaseUtils;
import com.gionee.client.business.statistic.util.Utils;
import com.gionee.client.business.util.LogUtils;

public class AppEventDataManager extends AbstractEventDataManager {
    private static final String TAG = "AppEventDataManager";

    /**
     * 得到app_event表中的一条数据
     */
    @Override
    protected byte[] getAPieceOfEventData(Cursor cursor) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            int time = (int) (Utils.changeMillisecondToSecond(MyDatabaseUtils.getLongColumValue(cursor,
                    DBFields.OCCUR_TIME)));
            String eventId = MyDatabaseUtils.getStringColumValue(cursor, DBFields.EVENT_ID);
            String eventLabel = MyDatabaseUtils.getStringColumValue(cursor, DBFields.EVENT_LABEL);
            String paramMap = MyDatabaseUtils.getStringColumValue(cursor, DBFields.PARA_MAP);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(time).append(",").append("A").append(",").append(eventId).append(",")
                    .append(eventLabel).append(",").append(paramMap);

            writeData(baos, stringBuilder);
            return baos.toByteArray();
        } catch (Exception e) {
            LogUtils.loge(TAG, "getAPieceOfEventData() " + e.toString());
            e.printStackTrace();
        } finally {
            Utils.closeIOStream(baos);
        }
        return null;
    }

    @Override
    protected Cursor getMaxCanUploadEventCursor(Context context) {
        LogUtils.logd(TAG, LogUtils.getThreadName() + "event");
        LocalDatabaseHelper localDatabaseHelper = LocalDatabaseHelper.getInstance(context);
        Uri uri = LocalDatabaseHelper.getEventUri();
        return localDatabaseHelper.query(uri, null, null, null, null);

    }

    @Override
    protected void saveMaxRecordId(String maxRecordId, LocalPreferenceManager localPreferenceManager) {
        localPreferenceManager.setSavedMaxAppEventId(maxRecordId);
    }
}
//Gionee <wangyy><2014-03-12> modify for CR00956169 end