// Gionee <yangxiong><2014-6-9> modify for CR00850885 begin
package com.gionee.client.business.statistic.database;

public class DBFields {
    public static final String DB_NAME = "statistics.db";
    public static final String TB_NAME_APP_EVENT = "app_event";
    public static final int VERSION = 1;

    /**
     * 应用编号, byte 行为时间, int 信息长度, short int 信息内容, String
     */
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String START_TIME = "start_time";
    public static final String DURATION = "duration";
    public static final String SESSION_ID = "session_id";
    public static final String REFER = "refer";
    public static final String REALTIME = "realtime";
    public static final String IS_LAUNCH = "is_launch";
    public static final String INTERVAL = "interval";
    public static final String IS_CONNECTED = "is_connected";
    public static final String EVENT_ID = "event_id";
    public static final String EVENT_LABEL = "event_label";
    public static final String OCCUR_TIME = "occur_time";
    public static final String PARA_MAP = "para_map";
    public static final String ERROR_TIME = "error_time";
    public static final String MESSAGE = "message";
    public static final String REPEAT = "repeat";
    public static final String SHORT_HASH_CODE = "short_hash_code";
}

//Gionee <yangxiong><2014-6-9> modify for CR00850885 end