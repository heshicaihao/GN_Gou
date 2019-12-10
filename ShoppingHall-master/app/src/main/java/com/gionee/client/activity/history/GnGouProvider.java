
package com.gionee.client.activity.history;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.gionee.client.business.dbutils.DBHelper;
import com.gionee.client.model.DBConstants;

public class GnGouProvider extends ContentProvider {

    private static UriMatcher sUriMatcher;
    private DBHelper mDbHelper = null;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(DBConstants.AUTHORITY, DBConstants.TABLENAME_BROWSE_HISTORY,
                DBConstants.CODE_BROWSE_HISTORY);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DBHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = sUriMatcher.match(uri);
        Uri appendUri = null;
        switch (code) {
            case DBConstants.CODE_BROWSE_HISTORY:
                appendUri = insert(uri, values, DBConstants.TABLENAME_BROWSE_HISTORY);
                break;

            default:
                break;
        }
        return appendUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {

        int code = sUriMatcher.match(uri);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (code) {
            case DBConstants.CODE_BROWSE_HISTORY:
                qb.setTables(DBConstants.TABLENAME_BROWSE_HISTORY);
                break;

            default:
                break;
        }
        cursor = qb.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int code = sUriMatcher.match(uri);
        int count = 0;
        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            count = 0;
            switch (code) {
                case DBConstants.CODE_BROWSE_HISTORY:
                    count = db.update(DBConstants.TABLENAME_BROWSE_HISTORY, values, selection,
                            selectionArgs);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        int code = sUriMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (code) {
            case DBConstants.CODE_BROWSE_HISTORY:
                count = db.delete(DBConstants.TABLENAME_BROWSE_HISTORY, selection, selectionArgs);
                break;

            default:
                break;
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private Uri insert(Uri uri, ContentValues values, String tableName) {
        Uri appendUri = null;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long insertRowId = db.insert(tableName, null, values);

        if (insertRowId > 0) {
            appendUri = ContentUris.withAppendedId(uri, insertRowId);
            getContext().getContentResolver().notifyChange(appendUri, null);
        }
        return appendUri;
    }
    

}
