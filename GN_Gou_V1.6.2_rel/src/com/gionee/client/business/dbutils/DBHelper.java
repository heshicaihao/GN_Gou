package com.gionee.client.business.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gionee.client.model.DBConstants;

public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, DBConstants.DATABASENAME, null, DBConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableBrowseHistory(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion > oldVersion) {
            removeTables(db);
            createTableBrowseHistory(db);
        }
    }

    private void removeTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLENAME_BROWSE_HISTORY);
    }

    private void createTableBrowseHistory(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DBConstants.TABLENAME_BROWSE_HISTORY + " ("
                + DBConstants.TableColumn.BrowseHistoryInfo.ID_I + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DBConstants.TableColumn.BrowseHistoryInfo.TITLE_S + " TEXT,"
                + DBConstants.TableColumn.BrowseHistoryInfo.TIME_L + " LONG,"
                + DBConstants.TableColumn.BrowseHistoryInfo.URL_S + " TEXT,"
                + DBConstants.TableColumn.BrowseHistoryInfo.PLATFORM_S + " TEXT,"
                + DBConstants.TableColumn.BrowseHistoryInfo.TYPE_S + " TEXT" + ");");
    }
}
