package com.gionee.client.business.statistic;

public class StatisticsConstants {

    public static final String TYPE_ACTION = "action";
    public static final String TYPE_UPMODE = "updateMode";

    public static final String KEY_ACTION = "boot";
    public static final String KEY_UPMODE_AUTO = "auto";
    public static final String KEY_UPMODE_MANUAL = "manual";
    public static final String ANDROID_ID = "android_id";
    public static final String MAC_ADDRESS = "mac_address";

    public static final int MIN_UPGRADE_START_CHECKCOUNT = 3;
    public static final String KEY_INTENT_URL = "url";
    public static final String TAG_FRAGMENT = "fragment";
    public static final int HOME_RESULT_CODE = 5;
    public static final String HOME_INTENT_FLAG = "page_index";
    public static final String PAGE_TAG = "page_tag";

    /**
     * 应用下载
     */
    public static final String APP_DOWNLOAD = "downloadapp";
    /**
     * 应用安装
     */
    public static final String APP_INSTALL = "installapp";
    /**
     * 应用升级
     */
    public static final String APP_UPGRADE = "upgradeapp";
    /**
     * 应用打开
     */
    public static final String APP_OPEN = "openapp";

    /**
     * 首页流量统计字段 @author yangxiong
     */
    public static final class HomePageConstants {
        public static final String OPEN_HOME_PAGE = "a1";
        public static final String BACK_HOME_PAGE = "a2";
        public static final String FRESH_HOME_PAGE = "a3";
        public static final String EXIT_HOME_PAGE = "i1";
        public static final String EXIT_SPLASH_PAGE_PUSH = "i2";
        public static final String EXIT_HOME_PAGE_PUSH = "i3";
        public static final String EXIT_HOME_PAGE_PUSH_COMMENT_DETAIL = "i4";
        public static final String EXIT_HOME_PAGE_PUSH_COMMENT_LIST = "i5";
        public static final String EXIT_SPLASH_PAGE_NORMAL = "i6";
        public static final String TOP_SEARCH = "b1";
        public static final String TWO_DIMENSION_CODE = "b2";
        public static final String BANNER_PREFIX = "c";
        public static final String CONVENIENT_ENTRANCE_PREFIX = "d";
        public static final String MARKETING_POSITION_1 = "e1";
        public static final String MARKETING_POSITION_2 = "e2";
        public static final String MARKETING_POSITION_3 = "e3";
        public static final String MARKETING_POSITION_4 = "e4";
        public static final String PLATFORM_PREFIX = "f";
        public static final String ATTENTION_PREFIX = "g";
        public static final String BOTTOM_TAB_1 = "h1";
        public static final String BOTTOM_TAB_2 = "h2";
        public static final String BOTTOM_TAB_3 = "h3";
        public static final String BOTTOM_TAB_4 = "h4";
        public static final String BOTTOM_TAB_5 = "h5";
    }

    public static final class CategoryPageConstants {
        public static final String HOME_TO_CATEGORY = "q1"; // 'q1' => '从大厅跳转',
        public static final String STORY_TO_CATEGORY = "q2"; // 'q2' => '从知物跳转',
        public static final String MINE_TO_CATEGORY = "q3"; // 'q3' => '从我的跳转',
        public static final String OTHER_TO_CATEGORY = "q4"; // 'q4' => '从其他页面返回',
        public static final String CLICK_SEARCH = "r1"; // 'r1' => '搜索点击',
        public static final String CLICK_TWO_DIMENSION = "r2"; // 'r2' => '二维码点击',
        public static final String CATEGORY_EXIT = "t1";// 't1' => '分类跳出汇总',
        /**
         * 一级分类的统计: s1,s2,s3......类推; banner的统计: s1-0; 二级分类的统计：s1-1, s1-2,s1-3......类推；
         */
        public static final String ONE_LEVEL_PREFIX = "s";
    }

    public static final class MinePageConstants {
        public static final String HOME_TO_MINE = "n1"; // 'n1' => '从大厅跳转',
        public static final String CATEGORY_TO_MINE = "n2"; // 'n2' => '从分类跳转',
        public static final String STORY_TO_MINE = "n3"; // 'n3' => '从知物跳转',
        public static final String OTHER_TO_MINE = "n4"; // 'n4' => '从其他页面返回',
        public static final String SETTING = "o1"; // 'o1' => '设置',
        public static final String PERSION_INFO = "o2"; // 'o2' => '个人信息',
        public static final String MY_MESSAGE = "o3";// 'o3' => '我的消息',
        public static final String COMPARATIVE_PRICE = "o4";// 'o4' => '我的比价商品',
        public static final String MY_FAVORITE = "o5";// 'o5' => '我的收藏',
        public static final String HISTORY_BROWSE = "o6";// 'o6' => '历史浏览记录及订单',
        public static final String LOGISTICS_QUERY = "o7";// 'o7' => '物流查询',
        public static final String COMMON_PROBLEM = "o8";// 'o8' => '常见问题',
        public static final String CUSTOM_SERVICE = "o9"; // 'o9' => '客户小慧在线',
        public static final String CALL_CUSTOM = "o10";// 'o10' => '致电客户',
        public static final String COMPATITIVE_APP = "o11";// 'o11' => '精品应用',
        public static final String EXIT = "p1";// 'p1' => '我的跳出汇总',
    }

    public static final class SearchConstants {

        public static final String OPEN_SEARCH_FROM_HOME = "j1";
        public static final String OPEN_SEARCH_FROM_CATEGORY = "j2";
        public static final String OPEN_SEARCH_FROM_OTHER = "j3";

        public static final String INITIATIVE_SEARCH_GOODS = "k1";
        public static final String SEARCH_DEFAULT_GOODS = "k2";
        public static final String SERACH_HISTORY_GOODS = "k3";
        public static final String SEARCH_HOTWORD_GOODS = "k4";
        public static final String SEARCH_MATCH_WORD_GOODS = "k5";
        public static final String REMOVE_SEARCH_GOODS = "k6";
        public static final String SEARCH_GOODS_CHANGETO_SHOPS = "k7";

        public static final String INITIATIVE_SEARCH_SHOP = "l1";
        public static final String SEARCH_DEFAULT_SHOP = "l2";
        public static final String SERACH_HISTORY_SHOP = "l3";
        public static final String SEARCH_HOTWORD_SHOP = "l4";
        public static final String SEARCH_MATCH_WORD_SHOP = "l5";
        public static final String REMOVE_SEARCH_SHOP = "l6";
        public static final String SEARCH_SHOPS_CHANGETO_GOODS = "l7";

        public static final String SEARCH_EXIT = "m1";

    }

}
