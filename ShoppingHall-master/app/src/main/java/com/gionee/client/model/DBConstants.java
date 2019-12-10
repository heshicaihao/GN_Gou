package com.gionee.client.model;

import android.net.Uri;

public class DBConstants {

    public static final int NEGATIVEONE = -1;
    public static final String AUTHORITY = "gngou.provider";
    public static final String DATABASENAME = "gngou.db";
    public static final int DATABASE_VERSION = 4;
    public static final int DATABASE_CAPACITY = 100;
    public static final String TABLENAME_BROWSE_HISTORY = "browsehistory";
    public static final int CODE_BROWSE_HISTORY = 1000;
    private static final String STR_CONTENT = "content://";
    public static final Uri URI_BROWSE_HISTORY = Uri.parse(STR_CONTENT + AUTHORITY + "/"
            + TABLENAME_BROWSE_HISTORY);
    public static final String SHAREPREFERENCE = "huodong";
    public static final String HUODONG_LOOK = "huodong_islook";//活动引导是否显示
    public static final String SHOUSHI_LOOK = "shoushi_islook";//手势引导是否显示
    
    public static final String ASC = " ASC";
    public static final String DESC = " DESC";

    public static class TableColumn {
        public static class BrowseHistoryInfo {
            public static final String ID_I = "id";
            public static final String TITLE_S = "title";
            public static final String TIME_L = "time";
            public static final String URL_S = "url";
            public static final String TYPE_S = "type";
            public static final String PLATFORM_S = "platform";
        }

    }
}
