//Gionee <wangyy><2014-03-12> modify for CR00956169 begin
package com.gionee.client.business.statistic.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.gionee.client.business.statistic.business.LocalPreferenceManager;
import com.gionee.client.business.statistic.business.StatisticsManager;
import com.gionee.client.business.statistic.database.DBFields;
import com.gionee.client.business.statistic.util.ByteUtil;
import com.gionee.client.business.statistic.util.MyDatabaseUtils;
import com.gionee.client.business.statistic.util.Utils;
import com.gionee.client.business.util.LogUtils;

public abstract class AbstractEventDataManager implements EventDataManager {
    private static final String TAG = "AbstractEventDataManager";

    @Override
    public void addEventData(Context context, EventDataInfos eventDataInfos, int maxDataSizeCanPut) {
        if (Utils.isNull(eventDataInfos)) {
            LogUtils.loge(TAG, LogUtils.getThreadName() + "eventDataInfos is null");
            return;
        }

        if (maxDataSizeCanPut <= 0) {
            return;
        }

        Cursor eventCursor = null;
        try {
            eventCursor = getMaxCanUploadEventCursor(context);
            if (MyDatabaseUtils.isCursorHasNoRecord(eventCursor)) {
                MyDatabaseUtils.closeCursor(eventCursor);
                return;
            }

            String maxRecordId = putMaxSizeData(eventCursor, eventDataInfos, maxDataSizeCanPut);
            saveRecordId(context, maxRecordId);
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName() + e.toString());
            e.printStackTrace();
        } finally {
            if (eventCursor == null) {
                return;
            }
            MyDatabaseUtils.closeCursor(eventCursor);
        }
    }

    private void saveRecordId(Context context, String maxRecordId) {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        LocalPreferenceManager localPreferenceManager = StatisticsManager.getInstance(context)
                .getLocalPreferenceManager();
        saveMaxRecordId(maxRecordId, localPreferenceManager);
    }

    @SuppressLint("NewApi")
    private String putMaxSizeData(Cursor eventCursor, EventDataInfos eventDataInfos, int maxDataSizeCanPut) {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        String lastRecordIndex = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        short eventCount = 0;
        eventCursor.moveToFirst();
        int curSize = 0;
        try {
            do {
                int id = MyDatabaseUtils.getIntColumValue(eventCursor, DBFields.ID);
                byte[] dataArray = getAPieceOfEventData(eventCursor);
                curSize = curSize + dataArray.length;

                if (curSize <= maxDataSizeCanPut) {
                    baos.write(dataArray);
                    baos.write("@".getBytes(Charset.forName("UTF-8")));
                    lastRecordIndex = String.valueOf(id);
                    ++eventCount;
                } else {
                    break;
                }

            } while (eventCursor.moveToNext());
            baos.flush();
            eventDataInfos.setmEventDataArray(baos.toByteArray());
            eventDataInfos.setmEventCount(eventCount);

        } catch (Exception e) {
            LogUtils.loge(TAG, "putMaxSizeData() " + e.toString());
            e.printStackTrace();
        } finally {
            Utils.closeIOStream(baos);
        }

        return lastRecordIndex;
    }

    /**
     * 获取一条事件数据
     */
    protected abstract byte[] getAPieceOfEventData(Cursor cursor);

    protected abstract Cursor getMaxCanUploadEventCursor(Context context);

    /**
     * 保存最大某一事件数据对应的最大主键ID.
     * 
     * @param name
     * @param ruler
     * 
     */
    protected abstract void saveMaxRecordId(String maxRecordId, LocalPreferenceManager localPreferenceManager);

    protected void writeTime(ByteArrayOutputStream baos, int time) throws IOException {
        baos.write(ByteUtil.intToByte(time, 4));
    }

    protected void writeData(ByteArrayOutputStream baos, StringBuilder stringBuilder) throws IOException {
        byte[] dataArray = stringBuilder.toString().getBytes(HTTP.UTF_8);

//        baos.write(ByteUtil.intToByte(dataArray.length, 2));
        baos.write(dataArray);
        baos.flush();
    }
}
//Gionee <wangyy><2014-03-12> modify for CR00956169 end