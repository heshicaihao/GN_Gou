package com.gionee.client.business.dbutils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.gionee.client.activity.history.BrowseHistoryInfo;
import com.gionee.client.business.util.GnDateUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.DBConstants;

public class DBOperationManager implements DBOperation<BrowseHistoryInfo> {

    private static DBOperationManager sDBOperationManager = null;
    private ContentResolver mContentResolver;
    private HandlerThread mHandlerThread;
    private DBOperationCallBack mCallBack;
    private DBOperationHandler mDBOperationHandler;
    private static final String TAG = "DBOperationManager";

    private Handler mCallbackHandler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) {

            if (null == msg) {
                return;
            }
            if (null != mCallBack) {
                mCallBack.onResultCallBack(msg.arg1, msg.obj);
            }
        };
    };

    public static DBOperationManager getInstance(Context applicationContext) {

//        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
//            throw new RuntimeException("DBOperationManager getInstance must be called in main thread!!!!");
//        }

        if (null == sDBOperationManager) {
            sDBOperationManager = new DBOperationManager(applicationContext);
        }
        return sDBOperationManager;
    }

    private DBOperationManager(Context context) {
        mContentResolver = context.getContentResolver();
        mHandlerThread = new HandlerThread("DBOperation_thread");
        mHandlerThread.start();
        mDBOperationHandler = new DBOperationHandler(mHandlerThread.getLooper());
    }

    @Override
    public Uri insert(BrowseHistoryInfo info) {

        ContentValues values = getValues(info);
        if (null == values) {
            return null;
        }

        Uri uri = null;
        int update = update(info);
        if (update <= 0) {
            uri = mContentResolver.insert(DBConstants.URI_BROWSE_HISTORY, values);
        }
        return uri;
    }

    @Override
    public int delete(BrowseHistoryInfo info) {

        if (null == info) {
            return 0;
        }

        final BrowseHistoryInfo historyInfo = info;
        String title = historyInfo.getTitle();
        String url = historyInfo.getUrl();
        String titleWhere = getTitleSelection(title);
        String urlWhere = getUrlSelection(url);
        String where = titleWhere + " AND " + urlWhere;
        return mContentResolver.delete(DBConstants.URI_BROWSE_HISTORY, where, null);
    }

    @Override
    public int update(BrowseHistoryInfo info) {

        ContentValues values = getValues(info);
        if (values == null) {
            return DBConstants.NEGATIVEONE;
        }

        String title = info.getTitle();
        String url = info.getUrl();

        String titleWhere = getTitleSelection(title);
        String urlWhere = getUrlSelection(url);
        String where = titleWhere + " AND " + urlWhere;
        return mContentResolver.update(DBConstants.URI_BROWSE_HISTORY, values, where, null);
    }

    public void registerDBOperationCallBack(DBOperationCallBack callBack) {
        mCallBack = callBack;
    }

    public void saveBrowseHistory(final Context context, final BrowseHistoryInfo info) {

        LogUtils.log(TAG, LogUtils.getThreadName() + info.toString());
        mDBOperationHandler.post(new Runnable() {
            @Override
            public void run() {
                ensureDBCapacity(context);
                long time = System.currentTimeMillis();
                info.setTime(String.valueOf(time));
                LogUtils.log(TAG, LogUtils.getThreadName() + info.toString());
                insert(info);
            }
        });
    }

    public void queryBrowseHistoryList(final Context context) {

        mDBOperationHandler.post(new Runnable() {

            @Override
            public void run() {

                List<BrowseHistoryInfo> list = new ArrayList<BrowseHistoryInfo>();

                Cursor cursor = mContentResolver.query(DBConstants.URI_BROWSE_HISTORY, null, null, null,
                        DBConstants.TableColumn.BrowseHistoryInfo.TIME_L + DBConstants.DESC);

                if (null == cursor) {
                    mDBOperationHandler.sendEmptyMessage(0);
                    LogUtils.log(TAG, "history list of all is null");
                    return;
                }
                list.clear();
                LogUtils.log(TAG,
                        LogUtils.getThreadName() + cursor.toString() + " size = " + cursor.getCount());
                while (cursor.moveToNext()) {
                    BrowseHistoryInfo info = getBrowseHistoryInfo(context, cursor);
                    list.add(info);
                }
                closeCursor(cursor);
                Message message = mCallbackHandler.obtainMessage();
                message.arg1 = 1;
                message.obj = list;
                mCallbackHandler.sendMessage(message);
            }
        });
    }

    public void queryHistoryGoodsList(final Context context) {

        mDBOperationHandler.post(new Runnable() {

            @Override
            public void run() {

                List<BrowseHistoryInfo> list = new ArrayList<BrowseHistoryInfo>();

                Cursor cursor = mContentResolver.query(DBConstants.URI_BROWSE_HISTORY, null,
                        DBConstants.TableColumn.BrowseHistoryInfo.TYPE_S + " = ?",
                        new String[] {Constants.History.HistoryType.GOODS.getValue()},
                        DBConstants.TableColumn.BrowseHistoryInfo.TIME_L + DBConstants.DESC);

                if (null == cursor) {
                    mDBOperationHandler.sendEmptyMessage(0);
                    LogUtils.log(TAG, "history list is null");
                    return;
                }
                LogUtils.log(TAG,
                        LogUtils.getFunctionName() + cursor.toString() + "size=" + cursor.getCount());
                list.clear();

                while (cursor.moveToNext()) {
                    BrowseHistoryInfo info = getBrowseHistoryInfo(context, cursor);
                    list.add(info);
                }
                closeCursor(cursor);
                Message message = mCallbackHandler.obtainMessage();
                message.arg1 = 0;
                message.obj = list;
                mCallbackHandler.sendMessage(message);
            }
        });
    }

    public void bulkDelteBrowseHistory(final List<BrowseHistoryInfo> list) {

        if (null == list) {
            return;
        }
        final List<BrowseHistoryInfo> deletList = new ArrayList<BrowseHistoryInfo>(list);

        mDBOperationHandler.post(new Runnable() {
            @Override
            public void run() {
                for (BrowseHistoryInfo info : deletList) {
                    delete(info);
                }
            }
        });
    }

    public void destory() {
        // mHandlerThread.quit();
    }

    private BrowseHistoryInfo getBrowseHistoryInfo(Context context, Cursor cursor) {

        BrowseHistoryInfo info = new BrowseHistoryInfo();
        LogUtils.log(TAG, LogUtils.getThreadName() + cursor.toString());
        int titleIndex = cursor.getColumnIndex(DBConstants.TableColumn.BrowseHistoryInfo.TITLE_S);
        if (DBConstants.NEGATIVEONE != titleIndex) {
            info.setTitle(cursor.getString(titleIndex));
        }

        int timeIndex = cursor.getColumnIndex(DBConstants.TableColumn.BrowseHistoryInfo.TIME_L);
        if (DBConstants.NEGATIVEONE != timeIndex) {

            long lastTime = cursor.getLong(timeIndex);
            info.setmTimemillis(lastTime);
            String formaterTime = GnDateUtils.formatDuring(context, System.currentTimeMillis(), lastTime);
            long days = GnDateUtils.getAbsIntervalDats(System.currentTimeMillis(), lastTime);
            info.setTime(formaterTime);
            info.setmDays(days);
        }

        int urlIndex = cursor.getColumnIndex(DBConstants.TableColumn.BrowseHistoryInfo.URL_S);
        if (DBConstants.NEGATIVEONE != urlIndex) {
            info.setUrl(cursor.getString(urlIndex));
        }
        int typeIndex = cursor.getColumnIndex(DBConstants.TableColumn.BrowseHistoryInfo.TYPE_S);
        if (DBConstants.NEGATIVEONE != typeIndex) {
            info.setmType(cursor.getString(typeIndex));
        }
        int platformIndex = cursor.getColumnIndex(DBConstants.TableColumn.BrowseHistoryInfo.PLATFORM_S);
        if (DBConstants.NEGATIVEONE != platformIndex) {
            info.setmPlatform(cursor.getString(platformIndex));
        }
        LogUtils.log(TAG, LogUtils.getThreadName() + info.toString());
        return info;
    }

    private ContentValues getValues(BrowseHistoryInfo info) {

        if (null == info) {
            return null;
        }
        ContentValues values = new ContentValues();
        String title = info.getTitle();
        String time = info.getTime();
        String url = info.getUrl();
        String type = info.getmType();
        String platform = info.getmPlatform();

        if (!TextUtils.isEmpty(title)) {
            values.put(DBConstants.TableColumn.BrowseHistoryInfo.TITLE_S, title);
        }

        if (!TextUtils.isEmpty(time)) {
            values.put(DBConstants.TableColumn.BrowseHistoryInfo.TIME_L, GnDateUtils.string2Long(time));
        }

        if (!TextUtils.isEmpty(url)) {
            values.put(DBConstants.TableColumn.BrowseHistoryInfo.URL_S, url);
        }
        if (!TextUtils.isEmpty(type)) {
            values.put(DBConstants.TableColumn.BrowseHistoryInfo.TYPE_S, type);
        }
        if (!TextUtils.isEmpty(platform)) {
            values.put(DBConstants.TableColumn.BrowseHistoryInfo.PLATFORM_S, platform);
        }
        return values;
    }

    private void closeCursor(Cursor cursor) {

        if (null != cursor) {
            cursor.close();
            cursor = null;
        }
    }

    private String getTitleSelection(String title) {
        if (TextUtils.isEmpty(title)) {
            return null;
        }
        title = sqliteEscape(title);
        String selection = DBConstants.TableColumn.BrowseHistoryInfo.TITLE_S + "='" + title + "'";
        return selection;
    }

    private String getUrlSelection(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        url = sqliteEscape(url);
        String selection = DBConstants.TableColumn.BrowseHistoryInfo.URL_S + "='" + url + "'";
        return selection;
    }

    private void ensureDBCapacity(Context context) {
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(DBConstants.URI_BROWSE_HISTORY, null, null, null,
                    DBConstants.TableColumn.BrowseHistoryInfo.TIME_L + DBConstants.DESC);
            if (null == cursor) {
                return;
            }
            final int count = cursor.getCount();
            if (count < DBConstants.DATABASE_CAPACITY) {
                closeCursor(cursor);
                return;
            }

            cursor.moveToPosition(DBConstants.DATABASE_CAPACITY - 1);
            do {
                BrowseHistoryInfo info = getBrowseHistoryInfo(context, cursor);
                delete(info);
            } while (cursor.moveToNext());
            closeCursor(cursor);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            closeCursor(cursor);
        }
    }

    private String sqliteEscape(String keyWord) {
        keyWord = keyWord.replace("'", "''");
        return keyWord;
    }

    private static class DBOperationHandler extends Handler {

        public DBOperationHandler(Looper looper) {
            super(looper);
        }
    }

}
