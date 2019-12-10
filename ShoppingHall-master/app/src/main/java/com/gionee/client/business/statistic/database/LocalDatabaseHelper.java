//Gionee <yangxiong><2014-06-09> modify for CR00850885 begin
package com.gionee.client.business.statistic.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.gionee.client.model.Constants;
import com.gionee.framework.operation.utills.Utils;

public class LocalDatabaseHelper {
    private static Uri sContentUriAppEvent = null;
    private static String sAuthority;

//    private static final String TAG = "MyContentProvider";
    private static final String STRING_AND = " AND ";
    private static final String STRING_EQUAL = "=";
    private static final String STRING_URI_BEGIN = "content://";
//    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.youju.statistics";
//    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.youju.statistics";
    private static UriMatcher sUriMatcher;
    private DatabaseHelper mDbHelper;

    private static LocalDatabaseHelper sInstance;
    private Context mContext;

    private LocalDatabaseHelper(Context context) {
        mContext = context.getApplicationContext();
        mDbHelper = new DatabaseHelper(mContext, DBFields.DB_NAME, null, DBFields.VERSION);
        initUriMatcher();
        initUri();
    }

    private void initUri() {
        sContentUriAppEvent = Uri.parse(STRING_URI_BEGIN + LocalDatabaseHelper.sAuthority + "/app_event");
    }

    public static synchronized LocalDatabaseHelper getInstance(Context context) {
        if (Utils.isNull(sInstance)) {
            sAuthority = context.getPackageName();
            sInstance = new LocalDatabaseHelper(context);
        }
        return sInstance;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        checkUri(uri);
        String tableStr = getTableNameFromUri(uri);
        String selectionWeNeed = getSelectionFromUri(uri, selection);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int num = db.delete(tableStr, selectionWeNeed, selectionArgs);
//        if (db != null) {
//            db.close();
//        }
        return num;
    }

    public Uri insert(Uri uri, ContentValues values) {
        checkUri(uri);
        String tableStr = getTableNameFromUri(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(tableStr, null, values);
//
//        if (db != null) {
//            db.close();
//        }

        if (rowId > 0) {
            Uri rowUri = ContentUris.withAppendedId(uri, rowId);
            return rowUri;
        }

        return null;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        checkUri(uri);
        String tableStr = getTableNameFromUri(uri);
        String selectionWeNeed = getSelectionFromUri(uri, selection);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableStr, projection, selectionWeNeed, selectionArgs, null, null, sortOrder);
//        if (db != null) {
//            db.close();
//        }
        return cursor;
    }

    private String getSelectionFromUri(Uri uri, String selection) {
        String selectionWeNeed = selection;
        if (isTableIdUri(uri)) {
            selectionWeNeed = createSelectionStringByUri(uri, selection);
        }
        return selectionWeNeed;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        checkUri(uri);
        String tableStr = getTableNameFromUri(uri);
        String selectionWeNeed = getSelectionFromUri(uri, selection);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int num = db.update(tableStr, values, selectionWeNeed, selectionArgs);
//        if (db != null) {
//            db.close();
//        }
        return num;
    }

    private String createSelectionStringByUri(Uri uri, String selection) {
        long rowId = ContentUris.parseId(uri);
        String where = DBFields.ID + STRING_EQUAL + rowId;
        if (Utils.isNotNull(selection)) {
            where = where + STRING_AND + selection;
        }
        return where;
    }

    private boolean isTableUri(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case Constants.URI.APP_EVENT:
                return true;
            default:
                return false;
        }
    }

    private boolean isTableIdUri(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case Constants.URI.APP_EVENT_ID:

                return true;
            default:
                return false;
        }
    }

    private void checkUri(Uri uri) {
        if (isTableUri(uri) || isTableIdUri(uri)) {
            return;
        }
        throw new IllegalArgumentException("未知Uri:" + uri);
    }

    private String getTableNameFromUri(Uri uri) {
        String tableStr = null;
        switch (sUriMatcher.match(uri)) {
            case Constants.URI.APP_EVENT:
            case Constants.URI.APP_EVENT_ID:
                tableStr = DBFields.TB_NAME_APP_EVENT;
                break;
            default:
                throw new IllegalArgumentException("未知Uri:" + uri);
        }
        return tableStr;
    }

    private static void initUriMatcher() {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(sAuthority, "app_event", Constants.URI.APP_EVENT);
        sUriMatcher.addURI(sAuthority, "app_event/#", Constants.URI.APP_EVENT_ID);
    }

    /**
     * A test package can call this to get a handle to the database underlying MyContentProvider, so it can
     * insert test data into the database. The test case class is responsible for instantiating the provider
     * in a test context; {@link android.test.ProviderTestCase2} does this during the call to setUp()
     * 
     * @return a handle to the database helper object for the provider's data.
     */
    public DatabaseHelper getOpenHelperForTest() {
        return mDbHelper;
    }

    public static Uri getEventUri() {
        return sContentUriAppEvent;
    }

}
//Gionee <yangxiong><2014-06-09> modify for CR00850885 end
