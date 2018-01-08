// Gionee <yuwei><2013-10-14> add for CR00821559 begin
/*
 * Url.java
 * classes : com.gionee.client.model.Url
 * @author yuwei
 * V 1.0.0
 * Create at 2013-10-14 上午9:24:33
 */
package com.gionee.client.model;

/**
 * Url
 * 
 * @author yuwei <br/>
 * @date create at 2013-10-14 上午9:24:33
 * @description TODO
 */
public class Url {

    /**
     * 知物导航标题接口
     */
    public static final String COMMENTS_TITEL_INFO= Config.URL +"api/apk_story/cat";    
    /**
     * 填写qq送QB活动接口
     */
    public static final String SEND_OB_URL = Config.URL + "api/apk_activity/sendPhone";
    /**
     * 软件商店接口
     */
    public static final String APP_STORE_URL = Config.URL + "/api/market_soft/index";

    /**
     * 天天特价接口
     */
    public static final String BARGAIN_PRICE_URL = Config.URL + "api/apk_v015_index/tejia";

    /**
     * 轮播广告 特色专区 接口接口url： (get方法)
     */
    public static final String ADVERTISE_BANNER_URL = Config.URL + "/api/apk_v015_index/ads";
    /**
     * 我关注的购物渠道接口url：(get方法)
     */
    public static final String ATTENTION_CHANNEL_URL = Config.URL + "/api/apk_v015_index/channel";
    /**
     * 我关注的购物渠道接口url：(get方法)
     */
    public static final String ADD_CHANNEL_URL = Config.URL + "/api/apk_v015_index/channel_list";
    /**
     * 应用推荐列表接口
     */
    public static final String APP_RECOMMEND_URL = Config.URL + "/api/apk_resource/list";
    /**
     * 便捷服务位接口
     */
    public static final String SPEEDY_SERVICE_URL = Config.URL + "/api/apk_v015_index/convenient";
    /**
     * 获取统计配置信息接口
     */
    public static final String STATISTIC_CONFIG_URL = Config.STATISTICS_URL + "/stat/apk/config";

    /**
     * 统计数据上传接口
     */
    public static final String STATISTIC_UPLOAD_URL = Config.STATISTICS_URL + "/stat/apk/upload";

    /**
     * 物语列表接口
     */
    public static final String COMMENTS_LIST_URL = Config.URL + "api/apk_story/list";

    /**
     * 物语列表接口(含问答入口数据)
     */
    public static final String COMMENTS_LIST_INCLUDE_QUESTION_URL = Config.URL
            + "api/apk_story/listWithHeader";

    /**
     * 物语详情接口,即我的收藏接口
     */
    public static final String COMMENTS_MY_FAVORITE_URL = Config.URL + "api/apk_favorite/story";
    
    /**
     * 知物收藏接口
     */
    public static final String ZHIWU_FAVOR_URL = Config.URL + "api/apk_favorite/storyExt";

    /**
     * 特色专区接口
     */
    public static final String SPECIAL_REGION = Config.URL + "/api/apk_v015_index/special_new";

    /**
     * Category list
     */
    public static final String CATEGORY_TAB_LIST_URL = Config.URL + "api/apk_type/big";
    /**
     * 首页搜索默认关键字
     */
    public static final String SEARCH_DEFAULT_KEYWORD_URL = Config.URL + "api/apk_search/keywords";
    /**
     * 物语点赞
     */
    public static final String COMMENT_PRAISE_URL = Config.URL + "api/apk_story/praise";
    /**
     * 物语收藏
     */
    public static final String COMMENT_FAVORITE_URL = Config.URL + "api/apk_story/addfav";
    /**
     * 知物是否收藏
     */
    public static final String COMMENT_IS_FAVORITE_URL = Config.URL + "api/apk_favorite/isFavorite";
    /**
     * 取消收藏
     */
    public static final String CANCEL_FAVORITE_URL = Config.URL + "api/apk_user/cancelfav";
    /**
     * 首页搜索默认关键字
     */
    public static final String SEARCHBAR_DEFAULT_KEYWORD_URL = Config.URL + "api/apk_search/keyword";
    /**
     * 关于页面
     */
    public static final String ABOUT_PAGE_URL = Config.URL + "about";

    /**
     * 收藏列表（店铺）
     */
    public static final String SHOP_FAVORITE_URL = Config.URL + "api/apk_favorite/shop";

    /**
     * 收藏列表（商品）
     */
    public static final String GOODS_FAVORITE_URL = Config.URL + "api/apk_favorite/goods";

    /**
     * 收藏列表（网页）
     */
    public static final String WEB_PAGE_FAVORITE_URL = Config.URL + "api/apk_favorite/web";

    /**
     * 我的收藏购物列表（网页）
     */
    public static final String SHOPPING_LIST_URL = Config.URL + "api/apk_favorite/favorite";

    /**
     * 添加收藏（店铺/商品/网页）
     */
    public static final String ADD_FAVORITE_URL = Config.URL + "api/apk_favorite/add";

    /**
     * 取消收藏（店铺/商品/网页）
     */
    public static final String REMOVE_FAVORITE_URL = Config.URL + "api/apk_favorite/remove";
    /**
     * 批量取消收藏（店铺/商品/网页）
     */
    public static final String BATCH_REMOVE_FAVORITE_URL = Config.URL + "api/apk_favorite/batchRemove";
    /**
     * 匹配规则接口
     */
    public static final String MATCHING_REGULATION_URL = Config.URL + "api/apk_config/index";
    /**
     * 评论知物接口
     */
    public static final String STORY_COMMENT_URL = Config.URL + "api/apk_comment/add";
    /**
     * 获取评论接口
     */
    public static final String DISCUSS_LIST_URL = Config.URL + "api/apk_comment/list";

    /**
     * 我的砍价订单
     */
    public static final String BARGAIN_ORDER_URL = Config.URL + "cutorder/list";

    /**
     * 获取砍价列表接口
     */
    public static final String CUT_LIST_URL = Config.URL + "api/apk_cut/list";

    /**
     * 砍价接口
     */
    public static final String CUT_URL = Config.URL + "api/apk_cut/cut";

    /**
     * 获取单个砍价商品信息
     */
    public static final String GET_CUT_GOOD_STATUS = Config.URL + "api/apk_cut/getStatus";
    /**
     * 砍价规则
     */
    public static final String BARGAIN_RULE_URL = Config.URL + "cut/rule";
    /**
     * 同款列表获取
     */
    public static final String SAME_STYLE_LIST_URL = Config.URL + "api/apk_same/getlist";
    /**
     * 低价提示
     */
    public static final String LOW_PRICE_NOTICE_URL = Config.URL + "api/apk_favorite/isReduce";
    /**
     * 移除低价提示
     */
    public static final String REMOVE_LOW_PRICE_NOTICE_URL = Config.URL + "api/apk_favorite/removeReduce";

    /**
     * 获取各种促销商品信息
     */
    public static final String GET_PROMOTIONAL_SALES_URL = Config.URL + "api/apk_push/get";

    /**
     * 个人积分处理
     */
    public static final String CUMULATE_SCORE_URL = Config.URL + "api/apk_score/score";
    /**
     * 用户配置接口
     */
    public static final String USER_CONFIG_URL = Config.URL + "api/apk_config/user";
    /**
     * 晒单提交
     */
    public static final String SUBMIT_HOT_ORDER = Config.URL + "api/apk_story/submitHotOrder";
    /**
     * 晒单图片提交
     */
    public static final String IMAGE_HOT_ORDER = Config.URL + "api/apk_story/imgHotOrder";
    /**
     * 晒单显示(获取晒单信息)
     */
    public static final String SHOW_HOT_ORDER = Config.URL + "api/apk_story/showHotOrder";
    /**
     * 百度推送信息统计接口
     */
    public static final String GET_BAIDU_PUSH_INFO_URL = Config.URL + "/api/apk_push/getBaiduUid";
    /**
     * 评论点赞
     */
    public static final String COMMENT_LIKE_URL = Config.URL + "/api/apk_comment/like";
    /**
     * 回答问题
     */
    public static final String ANSWER_QUESTION_URL = Config.URL + "/api/apk_qa/ans";
    /**
     * 问题搜索
     */
    public static final String SEARCH_QUESTION_URL = Config.URL + "/api/apk_qa/search";

    /**
     * 问答列表
     */
    public static final String QUESTION_LIST_URL = Config.URL + "/api/apk_qa/list";
    /**
     * 问答详情
     */
    public static final String QUESTION_DETAIL_URL = Config.URL + "/api/apk_qa/detail";
    /**
     * 提出问题(提问界面获取头像、昵称等信息)
     */
    public static final String QUESTION_INFO_URL = Config.URL + "/api/apk_qa/qus";
    /**
     * 发布问题
     */
    public static final String QUESTION_SUBMIT_URL = Config.URL + "/api/apk_qa/qusSubmit";

    /**
     * 回帖点赞
     */
    public static final String ANSWER_PRAISE_URL = Config.URL + "/api/apk_qa/praise";

    /**
     * 消息列表
     */
    public static final String MESSAGES_LIST = Config.URL + "/api/apk_user/msg";
    /**
     * 我的提问列表
     */
    public static final String MY_QUSTIONS_LIST = Config.URL + "api/apk_qa/myQusList";
    /**
     * 我的回答列表
     */
    public static final String MY_ANSWERS_LIST = Config.URL + "api/apk_qa/myAnsList";

    /**
     * 个人头像和昵称修改
     */
    public static final String MY_PROFILE_MODIFY = Config.URL + "api/apk_user/modify";
    /**
     * 常见问题目录列表
     */
    public static final String COMMON_CATAGORY_LIST = Config.URL + "api/apk_faq/category";
    /**
     * 常见问题列表
     */
    public static final String COMMON_QUESTION_LIST = Config.URL + "api/apk_faq/questions";
    /**
     * 流量统计数据提交
     */
    public static final String STATISTICS_SUBMIT = Config.URL + "/stat/apk/statistic";
    /**
     * 反馈记录
     */
    public static final String GET_CONVERSATION_LIST = Config.URL + "/api/apk_feedback/record";
    /**
     * 客户反馈
     */
    public static final String SEND_FEEDBACK = Config.URL + "/api/apk_feedback/add";
    /**
     * 清除意见反馈消息提醒
     */
    public static final String CLEAN_MSG_NOTIFY = Config.URL + "/api/apk_feedback/cleanTip";

    /**
     * 热词搜索
     */
    public static final String SEARCH_RAND_KEYWORDS = Config.URL + "api/apk_search/randkeywords";
    /**
     * 首页活动主导航接口
     */
    public static final String NAVMAIN = Config.URL + "api/apk_navigation/navmain";
    /**
     * 历史订单列表
     */
    public static final String ORDER_HISTORY = Config.URL + "api/apk_user/getorders";
    /**
     * 添加订单
     */
    public static final String ADD_ORDER = Config.URL + "api/apk_user/addorder";
    /**
     * 自定义升级
     */
    public static final String UPGRATE = Config.URL + "/api/apk_update/check";
    /**
     * 新知物列表
     */
    public static final String TALE_LIST = Config.URL + "api/apk_story/listEXT";
}
