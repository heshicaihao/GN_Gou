package com.gionee.client.business.statistic.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyDatabaseUtils {
    public static void closeCursor(Cursor cursor) {
        if (Utils.isNotNull(cursor)) {
            cursor.close();
        }
    }

    public static Cursor getSimpleCursor(String uriString, Context context) {
        Uri uri = Uri.parse(uriString);
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public static String getStringColumValue(Cursor cursor, String columnName)
            throws IllegalArgumentException {

        return cursor.getString(getColumnIndex(cursor, columnName));

    }

    public static int getIntColumValue(Cursor cursor, String columnName) throws IllegalArgumentException {

        return cursor.getInt(getColumnIndex(cursor, columnName));

    }

    public static long getLongColumValue(Cursor cursor, String columnName) throws IllegalArgumentException {

        return cursor.getLong(getColumnIndex(cursor, columnName));

    }

    private static int getColumnIndex(Cursor cursor, String columnName) throws IllegalArgumentException {
        return cursor.getColumnIndexOrThrow(columnName);
    }

    public static void closeDatabase(SQLiteDatabase database) {
        if (Utils.isNotNull(database)) {
            database.close();
        }
    }

    public static boolean isCursorHasRecords(Cursor cursor) {
        if (isCursorHasNoRecord(cursor)) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean isCursorHasNoRecord(Cursor cursor) {
        if (Utils.isNull(cursor) || cursor.getCount() == 0) {
            return true;
        }
        return false;
    }

}
