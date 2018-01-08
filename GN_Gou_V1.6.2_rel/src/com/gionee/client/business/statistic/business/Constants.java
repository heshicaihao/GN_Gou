//Gionee <yangxiong><2014-06-16> modify for CR00850885 begin
package com.gionee.client.business.statistic.business;

public final class Constants {

    public static final String EVENT_ID = "eventId";
    public static final String EVENT_LABEL = "eventLabel";
    public static final String PARAM_MAP = "paramMap";

    /**
     * 此字段是配置协议版本号
     */
    public static final int CFG_PROTOCAL_VERSION_NUM = 5;

    /**
     * 此字段是数据格式协议版本号
     */
    public static final int CFG_DATA_FORMAT_PROTOCAL_VERSION = 1;

    public static final class NetworkConfig {
        public static final int GIONEE_CONNECT_TIMEOUT = 10 * 1000;
        public static final int GIONEE_SOCKET_TIMEOUT = 10 * 1000;
    }

    public static final class NetworkCode {
        public static final int NO_NETWORK = -1;
        public static final int NETWORK_MOBILE = 0;
        public static final int NETWORK_WIFI = 1;
        public static final int NETWORK_UNKNOWN = 2;
    }

    public static final class HttpStatusCode {
        public static final int CODE_OK = 200;
        public static final int CODE_BAD_REQUEST = 400;
        public static final int CODE_NOT_ALLOWED = 405;
        public static final int CODE_SERVER_ERROR = 500;
        public static final int CODE_GATE_WAY_ERROR = 502;
        /**
         * 4000–数据错误 4001 – appkey无效
         */
        public static final int CODE_DATA_ERROR = 4000;
        public static final int CODE_APP_KEY_INVALID = 4001;
    }

    public static final class PreferenceKeys {
        public static final String KEY_DATE_TODAY = "date_today";
        public static final String KEY_HAS_UPLOADED_TODAY = "has_uploaded_today";

        /**
         * 运营商网络下今天已上传的字节数
         */
        public static final String KEY_UPLOADED_BYTES_TODAY = "uploaded_bytes_today";

        /**
         * 1 byte(配置版本号)
         */
        public static final String KEY_CFG_VER_NUMBER = "cfg_ver_num";

        /**
         * WIFI上传一次的最小流量
         */
        public static final String KEY_CFG_MIN_FOLW_UPLOAD_ONCE_WIFI = "cfg_min_flow_upload_once_wifi";

        /**
         * 运营商网络上传一次的最小流量
         */
        public static final String KEY_CFG_MIN_FOLW_UPLOAD_ONCE_MOBILE = "cfg_min_flow_upload_once_mobile";

        /**
         * WiFi每次最大上传量
         */
        public static final String KEY_CFG_WIFI_MAX_UPLOAD_SIZE = "cfg_wifi_max_upload_size";

        /**
         * GPRS每天最大上传量
         */
        public static final String KEY_CFG_GPRS_MAX_UPLOAD_SIZE = "cfg_gprs_max_upload_size";

        /**
         * 本地最大Event条数
         */
        public static final String KEY_CFG_TABLE_MAX_EVENT_NUMBER = "cfg_table_max_event_number";

        /**
         * 当appEvent达到某个阀值条数时候，检查上传.
         */
        public static final String KEY_CFG_APPEVENT_COUNT_WHEN_CHECK_UPLOAD = "cfg_appevent_count_when_check_upload";

        /**
         * 查询到的可以上传的统计信息对应的最大行id
         */
        public static final String KEY_MAX_ID_GOTTEN = "max_id_gotten";

        /**
         * 查询到的可以上传的appEvent表对应的最大行id
         */
        public static final String KEY_MAX_ID_GOTTEN_APP_EVENT = "max_id_gotten_app_event";

        /**
         * 通过运营商网络上传的字节数
         */
        public static final String KEY_DATA_BYTES_GOTTEN = "data_bytes_gotten";

        /**
         * 上次上传时间
         */
        public static final String KEY_TIME_PREVIOUS_UPLOAD = "time_previous_upload";
    }

    public static final class DataFormat {
        public static final int B = 1;
        public static final int KB = 1024;
        public static final int MB = 1024 * KB;
        public static final int MONTH = 30;
    }

    public static final class URI {
        public static final int ITEM = 1;
        public static final int ITEM_ID = 2;
        public static final int APP_EVENT = 3;
        public static final int APP_EVENT_ID = 4;
    }

    public static final class DefaultConfigParameters {
        /**
         * 配置版本号：1 WiFi每次最小上传量：0B WiFi每次最大上传量：100,000 B GPRS每天最小上传量：500B GPRS每天最大上传量：3,000 B 本地最大存储量：1M
         * 本地最大存储数：5000
         */
        public static final int CFG_VERSION_NUMBER = 1;
        public static final int WIFI_MIN_UPLOAD_FLOW = 0;
        public static final int WIFI_MAX_UPLOAD_FLOW = 100000;
        public static final int GPRS_MIN_UPLOAD_FLOW = 500;
        public static final int GPRS_MAX_UPLOAD_FLOW = 10 * 1000;
//        public static final int LOCAL_MAX_DATABASES_TABLE_NUMBER = 5000;
        public static final int LOACL_MAX_ACTIVITY_INFO_NMUBER = 5000;
        public static final int LOACL_MAX_SESSION_INFO_NUMBER = 5000;
        public static final int LOACL_MAX_EVENT_NUMBER = 5000;
        public static final int LOACL_MAX_EXCEPTION_NUMBER = 500;
        public static final int LOACL_MAX_SAVE_CAPACITY = 1;
        public static final int DEFAULT_APP_EVENT_COUNT_WHEN_CHECK_UPLOAD = 0;
    }

    public static final class DefaultSDKConfig {
        public static final String DEFAULT_APPID = "0123456789ABCDEF";
        public static final String DEFAULT_IMEI = "123456789012345";
        public static final String TEST_ENVIRONMENT_FLAG_FILE = "gse";
        public static final int CFG_DEFAULT_MAX_STRING_LENGTH = 32;
        public static final int CFG_DEFAULT_MAX_MAP_SIZE = 10;
        public static final int CFG_DEFAULT_MAX_ERROR_INFO_LENGTH = 5000;
        public static final int CFG_DEFAULT_UPLOAD_WATIED_TIME = 1000;
        public static final int CFG_DEFAULT_SESSION_INTERVAL_TIME = 30 * 1000;
        public static final int CFG_DEFAULT_ENABLE_STATISTICS_ACTIVTY = 1;
        public static final int CFG_DEFAULT_DISABLE_STATISTICS_ACTIVTY = 0;
        public static final long CFG_DEFAULT_EVENT_UPLOAD_WAITED_TIME = 5 * 60 * 1000;
    }

}
//Gionee <yangxiong><2014-06-16> modify for CR00850885 end
