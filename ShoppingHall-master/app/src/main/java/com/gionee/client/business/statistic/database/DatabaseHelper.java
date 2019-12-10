// Gionee <yangxiong><2012-6-5> modify for CR00850885 begin
package com.gionee.client.business.statistic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * CREATE TABLE app_event (_id INTEGER PRIMARY KEY autoincrement,event_id TEXT,event_label
         * TEXT,session_id TEXT,occurtime LONG,paramap TEXT NOT NULL);
         */
        String sqlString = "CREATE TABLE IF NOT EXISTS " + DBFields.TB_NAME_APP_EVENT + " (" + DBFields.ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBFields.EVENT_ID + " TEXT NOT NULL,"
                + DBFields.EVENT_LABEL + " TEXT NOT NULL," + DBFields.OCCUR_TIME + " LONG,"
                + DBFields.PARA_MAP + " TEXT NOT NULL);";
        db.execSQL(sqlString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBFields.TB_NAME_APP_EVENT);
        onCreate(db);
    }
}
// Gionee <yangxiong><2014-6-9> modify for CR00850885 end