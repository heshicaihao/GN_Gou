// Gionee <yuwei><2013-8-14> add for CR00821559 begin
/*
 * K.java
 * classes : com.gionee.client.model.K
 * @author yuwei
 * V 1.0.0
 * Create at 2013-8-14 下午3:53:53
 */
package com.gionee.client.model;

import com.gionee.framework.model.index.BaseIndex;

/**
 * com.gionee.client.model.K
 * 
 * @author yuwei <br/>
 * @date create at 2013-8-14 下午3:53:53
 * @description TODO
 */
public class HttpConstants extends BaseIndex {
    // Gionee <yuwei><2013-5-27> add for CR00821559 begin
    public final class Request extends RequstIndex {
        public final class SearchQuestion {
            public static final String SEARCH_S = "s";
        }

        public final class AnswerQuestion {
            /**
             * 回答内容
             */
            public static final String CONTENT_S = "content";
            /**
             * 问题ID
             */
            public static final String QUS_ID_I = "qus_id";
            /**
             * 回答ID 如果为空, 则是对问题的回答, 否则是对帖子的回答
             */
            public static final String PID_I = "pid";
            public static final String VERIFY_CODE = "verify_code";

        }

        public final class GetLoadingInfo {
            public static final String CHANNEL_ID_I = "channel_id";
            public static final String WIDTH_I = "width";
        }

        public final class BargainPrice {
            public static final String VERSION = "version";
            public static final String CATEGORY = "category";
        }

        public final class MyFavorites {
            public static final String TYPE = "type";
        }

        public final class ComentsPraise {
            public static final String TYPE = "type";
            public static final String ID = "id";
        }

        public final class ComentsFavorites {
            public static final String ID = "id";
        }
        
        public final class ComentsIsFavorites {
            public static final String ID = "id";
            public static final String TYPE = "type";
        }
        
        public final class CancelFavorites {
            public static final String ID = "id";
            public static final String COMMENT_ID = "item_id";
            public static final String TYPE = "type";
        }

        public class RemoveFavorites {
            public static final String ID = "id";
            public static final String ITEM_ID = "item_id";
            public static final String TYPE = "type";
        }

        public final class BatchRemoveFavorites extends RemoveFavorites {
            public static final String BATCH = "batch";
        }

        public final class SendStoryComments {
            /**
             * 评论内容 不为空
             */
            public static final String CONTENT_S = "content";
            /**
             * 类型 'STORY' 不为空
             */
            public static final String TYPE_I = "type";
            /**
             * 签名 string md5(uid + NTQzY2JmMzJhYTg2N2RvY3Mva2V5)
             */
            public static final String SIGN_S = "sign";
            /**
             * item_id 默认为空
             */
            public static final String ITEM_ID_I = "item_id";
            /**
             * 用户昵称 默认为空
             */
            public static final String NICKNAME_S = "nickname";
            /**
             * 评论ID
             */
            public static final String PARENT_ID = "pid";

        }

        public final class GetDiscussList {
            public static final String PAGE = "page";
            public static final String ITEM_ID = "item_id";
            public static final String TYPE = "type";
            public static final String LIST = "list";
            public static final String ID = "id";
        }

        public final class GetCutList {
            public static final String PAGE = "page";
            public static final String LIST = "list";
            public static final String PERPAGE = "perpage";
            public static final String ID_I = "id";
        }

        public final class GetCutGoodStatus {
            public static final String ID_I = "id";
        }

        public final class SameStyleList {
            public static final String ID_I = "id";
            public static final String PID_I = "unipid";
            public static final String GOODS_ID_I = "goods_id";
        }

        public final class CumulateScore {
            public static final String ID_I = "id";
            public static final String SHARE_CHANNEL = "share_channel";
            public static final String SIGN = "sign";
        }

        public final class SubmitHotOrder {
            public static final String ID_I = "id"; // 晒单单号：实际为知物ID号
            public static final String OID_I = "oid"; // 订单id
            public static final String AUTHOR = "author";
            public static final String TITLE = "title";
            public static final String CONTENT = "content";
            public static final String SIGN = "sign";
        }

        public final class ImageHotOrder {
            public static final String ID_I = "id"; // 晒单单号：实际为知物ID号
            public static final String OID_I = "oid"; // 订单id
            public static final String IMAGE = "img"; // 图片
            public static final String DELETE_IMAGE = "del"; // 需要删除的图片的id的数组序列化
            public static final String SORT = "sort"; // 图片排序
            public static final String TOTAL = "total"; // 添加图片的总数
            public static final String SIGN = "sign"; // 签名
        }

        public final class ShowHotOrder {
            public static final String ID_I = "id"; // 晒单单号：实际为知物ID号
            public static final String OID_I = "oid"; // 订单id
        }

        public final class QuestionSubmit {
            public static final String NICK_NAME = "nickname"; // 问贴作者昵称
            public static final String AVATAR = "avatar"; // 问贴作者头像
            public static final String IS_EDIT = "is_edit"; // 是否需要编辑
            public static final String TITLE = "title"; // 问题标题
            public static final String CONTENT = "content"; // 问题描述
            public static final String IMG = "img_"; // 问题图片 stream, img_0, img_1, img_2
            public static final String SIGN = "sign"; // 签名 string md5(uid+key)
            public static final String VERIFY_CODE = "verify_code"; // 验证码

        }

        public final class ModifyUserInfo {
            public static final String NICK_NAME = "nickname";
            public static final String IMG_AVATAR = "img_avatar";
        }

        public final class OrderHistoryInfo {
            /**
             * 是否有下一页 string
             */
            public static final String HASNEXT_S = "hasnext";
            /**
             * 当前第几页 1
             */
            public static final String CURPAGE_I = "curpage";

            /**
             * list 列表
             */
            public static final String LIST_JA = "list";
        }

        /**
         * 获取常见问题列表
         * 
         * @author wuhao
         * 
         */
        public final class GetCommonQuestionList {
            public static final String CID = "cid";

        }

        public final class StatisticsSubmit {
            public static final String DATA = "eventIds";
            public static final String SOURCE = "source"; /*source   来源标识(0 原始, 1 push_baidu百度push启动 2 push_local 本地push启动 )*/
        }

        public final class AddOrderSubmit {
            public static final String CHANNELID = "channel_id";
        }

        public final class SearchRandKeywords {
            public static final String TYPE = "type";
        }

        public final class Upgrate {
            public static final String PRODUCT_KEY = "product_key";
            public static final String CHANNEL = "channel";
            public static final String VERSION_CODE = "version_code";
            public static final String STATUS = "status";
        }

        public final class NewsList {
            public static final String CAT_ID_S = "cat_id";
        }
    }

    public final class Response extends ResponseIndex {
        public final class ConversationList {
            /**
             * 是否追加默认提示 boolean
             */
            public static final String TIP_B = "tip";
            /**
             * 客服 json
             */
            public static final String KF_JO = "kf";

        }

        public final class SearchQuestionList {
            public static final String TITLE = "title";
            public static final String ANS_TOTAL = "ans_total";

        }

        public final class UserInfo {
            /**
             * 用户ID
             */
            public static final String ID_I = "id";
            /**
             * 用户唯一识别码
             */
            public static final String UID_I = "uid";
            /**
             * 用户积分id
             */
            public static final String SCOREID_I = "scoreid";
            /**
             * 用户昵称
             */
            public static final String NICKNAME_S = "nickname";
            /**
             * 手机号码
             */
            public static final String MOBILE_S = "mobile";
            /**
             * 百度用户ID
             */
            public static final String BAIDU_UID_S = "baidu_uid";
            public static final String BAIDU_CID_S = "baidu_cid";
            public static final String FEEDBACK_TIP_B = "feedback_tip";

            /**
             * 用户头像
             */
            public static final String AVATAR_S = "avatar";
            public static final String TYPE_S = "type";
            /**
             * 消息提醒字段,返回0|1
             */
            public static final String HAS_MSG_I = "has_msg";
            public static final String IS_EDIT_B = "is_edit";
            public static final String FEEDBACK_AVATAR_S = "feedback_avatar";
            public static final String feedback_time_i = "feedback_time";

        }

        public final class LowPriceNotice {
            public static final String REDUCE_B = "reduce";
            public static final String MSG_S = "msg";
        }

        public final class CommentsTitel {

            /**
             * 知物导航标题json数组
             */
            public static final String TITEL_LIST = "list";

            /**
             * 标题id
             */
            public static final String TITEL_ID = "id";

            /**
             * 标题名字
             */
            public static final String TITEL_NAME = "title";

        }

        public final class MatchRegular {

            public static final String HELP_ID = "help_id";
            /**
             * 帮助url
             */
            public static final String URL_S = "url";
            public static final String SRC_S = "src";
            public static final String STATUS_I = "status";
            /**
             * 帮助匹配列表
             */
            public static final String HELP_JA = "help";
            /**
             * 历史记录匹配列表
             */
            public static final String HISTORY_JA = "history";
            /**
             * 正则
             */
            public static final String PREG_S = "preg";
            /**
             * 类型,shop或goods
             */
            public static final String TYPE_S = "type";
            /**
             * 用户信息
             */
            public static final String USER_JO = "user";
            /**
             * 用户ID
             */
            public static final String ID_I = "id";
            /**
             * 用户UID
             */
            public static final String UID_S = "uid";
            /**
             * 用户昵称
             */
            public static final String NICKNAME_S = "nickname";

            /**
             * 微博提示语
             */
            public static final String WEIBO_NOTICE_S = "weibo_notice";
            /**
             * 是否开启服务端javascript
             */
            public static final String JAVA_SCRIPT_SWITCH_I = "plugin";
            /**
             * 是否过期
             */
            public static final String IS_OUT_OF_DATA_B = "is_out_of_data";
            /**
             * 砍价配置
             */
            public static final String CUT_JO = "cut";
            /**
             * 砍价订单url
             */
            public static final String ORDER_LIST_URL_S = "order_list";
            /**
             * 砍价规则url
             */
            public static final String CUT_RULE_URL_S = "cut_rule";
            /**
             * 添加对比匹配规则
             */
            public static final String CMP_GOODS_URL_REGEX_JA = "cmp_goods_url_regex";
            /**
             * 活动url地址
             */
            public static final String SCORE_URL_S = "score_url";
            /**
             * 积分配置
             */
            public static final String SCORE_JO = "score";
            /**
             * 是否积分
             */
            public static final String IS_SCORE_S = "is_score";
            /**
             * 我的背景图
             */
            public static final String IMG_URL_S = "img_url";

            /**
             * 密钥
             */
            public static final String SECRET_KEY = "secret_key";

            /**
             * 问答入口是否开启
             */
            public static final String IS_QUESION_IMPORT_OPEN = "is_qa_available";
            public static final String activate_b = "is_nav_main_available";
            public static final String version_s = "nav_main_version";
            public static final String ORDER_MATCH_JA = "order";
            public static final String channel_id_s = "channel_id";
            public static final String OPT_BAR = "optbar";
            public static final String PAGE_SKIP = "pageskip";
            public static final String TAOBAO_SEARCH_AUTOFILL_URL = "taobao_search_autofill_url";

        }

        public final class StatisticConfig {
            /**
             * 上传开关
             */
            public static final String ISUPLOAD_B = "isupload";
            /**
             * WiFi每次最小上传量
             */
            public static final String WIFI_MIN_I = "wifi_min";
            /**
             * WiFi每次最大上传量
             */
            public static final String WIFI_MAX_I = "wifi_max";
            /**
             * gprs每天上传最小量
             */
            public static final String GPRS_MIN_I = "gprs_min";
            /**
             * gprs每天最大上传量
             */
            public static final String GPRS_MAX_I = "gprs_max";
            /**
             * 本地最大存储量
             */
            public static final String SD_SIZE_I = "sd_size";
            /**
             * 本地最大存储数
             */
            public static final String LOCAL_ST_SIZE_I = "local_st_size";
            /**
             * SDK监听触发数
             */
            public static final String SDK_LISTEN_NUM_I = "sdk_listen_num";

        }

        public final class RecommondAppList {
            /**
             * 版本名称
             */
            public static final String VERSION_NAME_S = "version_name";
            /**
             * 名称 string
             */
            public static final String NAME_S = "name";
            /**
             * 短描述 string
             */
            public static final String DESCRIPTION_S = "description";
            /**
             * 详细描述 string
             */
            public static final String RESUME_S = "resume";
            /**
             * 大小 int 单位bytes
             */
            public static final String SIZE_I = "size";
            /**
             * 应用标示 string
             */
            public static final String COMPANY_S = "company";
            /**
             * 包名 string
             */
            public static final String PACKAGE_S = "package";
            /**
             * 版本号 string
             */
            public static final String VERSION_S = "version";
            /**
             * 地址 string
             */
            public static final String LINK_S = "link";
            /**
             * 图标 string
             */
            public static final String ICON_S = "icon";

            /**
             * 应用截图信息 string
             */
            public static final String IMAGES_S = "imgs";

            /**
             * 截图Url string
             */
            public static final String URL_S = "url";

        }

        /**
         * com.gionee.client.model.getLoadingInfo
         * 
         * @author yuwei <br/>
         * @date create at 2013-8-15 上午11:23:57
         * @description TODO 首页闪屏接口
         */
        public final class GetLoadingInfo {
            /**
             * 图片地址
             */
            public static final String IMAGE_URL_S = "img";
            /**
             * 开始时间
             */
            public static final String START_TIME_I = "start_time";
            /**
             * 结束时间
             */
            public static final String END_TIME_I = "end_time";
            /**
             * tab列表，首页头部tab由服务器动态提供，并绑定相应的url
             */
            public static final String TAB_A = "tab";
            /**
             * tab名称
             */
            public static final String TABNAME_S = "tabName";
            /**
             * tab对应的url
             */
            public static final String URL_S = "url";

        }

        public final class AppStore {
            public static final String APP_LIST_JA = "list";
            /**
             * 包名
             */
            public static final String PACKAGE_S = "package";
            /**
             * 版本号
             */
            public static final String VERSION_S = "version";
            /**
             * 下载地址
             */
            public static final String DOWNLOAD_URL_S = "download_url";

            /**
             * apk文件名
             */
            public static final String APK_S = "apk";
            /**
             * apk MD5值
             */
            public static final String APK_MD5_S = "apk_md5";
        }

        public final class AdvertiseBanner {
            /**
             * 轮播广告 json数组
             */
            public static final String ADS_JA = "ads";
            /**
             * 便捷服务
             */
            public static final String AD_JA = "ad";
            /**
             * 特色专区 json数组
             */
            public static final String SPECIAL_JA_LEFT = "ad1";
            /**
             * 特色专区 json数组
             */
            public static final String SPECIAL_JA_RIGHT = "ad2";

            /**
             * 数据ID 自增长 int
             */
            public static final String ID_I = "id";
            /**
             * 广告名称 string
             */
            public static final String TITLE_S = "title";
            /**
             * 广告链接 string
             */
            public static final String LINK_S = "link";
            /**
             * 广告图片url string
             */
            public static final String IMG_S = "img";
            /**
             * 跳转action
             */
            public static final String ACTION_S = "action";
            public static final String TYPE_S = "type";

        }

        public final class MyAttentionChannel {
            /**
             * 渠道列表
             */
            public static final String CHANNEL_JA = "channel";
            /**
             * 数据ID 自增长 int
             */
            public static final String ID_I = "id";
            /**
             * 渠道名称 string
             */
            public static final String NAME_S = "name";
            /**
             * 渠道链接 string
             */
            public static final String LINK_S = "link";
            /**
             * 渠道icon string
             */
            public static final String IMG_S = "img";
            /**
             * 短描述short_description
             */
            public static final String DESC_S = "short_description";
            /**
             * 颜色
             */
            public static final String COLOR_S = "color";
            /**
             * 是否推荐
             */
            public static final String IS_RECOMMEND_S = "is_recommend";

        }

        /**
         * @author yangxiong <br/>
         * @description TODO 天天特价 接口
         */
        public final class BargainPrice {
            /**
             * 是否有下一页 string
             */
            public static final String HASNEXT_S = "hasnext";
            /**
             * 当前第几页 1
             */
            public static final String CURPAGE_I = "curpage";

            /**
             * list 列表
             */
            public static final String LIST_JA = "list";
        }

        /**
         * 历史订单
         * 
         * @author wuhao
         * 
         */
        public final class OrderHistory {
            /**
             * 是否有下一页 string
             */
            public static final String HASNEXT_S = "hasnext";
            /**
             * 当前第几页 1
             */
            public static final String CURPAGE_I = "curpage";

            /**
             * list 列表
             */
            public static final String LIST_JA = "list";
        }

        /**
         * add channel
         */
        public final class AddChannel {
            /**
             * 渠道列表
             */
            public static final String CHANNEL_JA = "channel";
            /**
             * 渠道类型列表
             */
            public static final String CHANNEL_TYPE_JA = "type";
            /**
             * 渠道链接 string
             */
            public static final String LINK_S = "link";
            /**
             * 数据ID 自增长 int
             */
            public static final String ID_I = "id";
            /**
             * 渠道icon string
             */
            public static final String IMG_S = "img";
            /**
             * 渠道名称 string
             */
            public static final String NAME_S = "name";
            /**
             * 渠道名称 string
             */
            public static final String DESCRIPTION = "description";

        }

        public final class SearchKeywords {
            public static final String KEYWORDS_JA = "keywords";
            public static final String KEYWORD_S = "keyword";
            public static final String TAOBAO_SEARCH_URL_S = "taobao_search_url";

        }

        public final class CategoryTab {
            public static final String TYPE_JA = "type";
            public static final String ICON_S = "icon";
            public static final String LINK_S = "link";
            public static final String NAME_S = "name";
        }

        public final class Category {
            public static final String TYPE_DATA_JA = "type_data";
            public static final String NAME_S = "name";
            public static final String IMG_S = "img";
            public static final String LINK_S = "link";
            public static final String AD_DATA_S = "ad_data";
            public static final String ITEMS_ARRAY = "items";
        }

        /**
         * @description 接受各种服务端各种促销信息
         */
        public final class PromotionalSales {
            public static final String NEXT_I = "next";// 下次请求的时间
            public static final String IMG_S = "img";
            public static final String MSG_S = "msg";
            public static final String TITLE_S = "title";
            public static final String DISCOUNT_JO = "discount";
        }

        public final class BaseListData {
            /**
             * 是否有下一页 string
             */
            public static final String HASNEXT_S = "hasnext";
            /**
             * 当前第几页 1
             */
            public static final String CURPAGE_I = "curpage";

            /**
             * list 列表
             */
            public static final String LIST_JA = "list";
        }

        public final class ISHasGift {
            public static final String HAS_B = "has";
            public static final String MSG_S = "msg";
        }

    }

    public final class Data extends DataIndex {
        /**
         * 闪屏接口返回数据
         */
        public static final String LOADING_INFO_JO = "loading_info";
        /**
         * 统计配置信息
         */
        public static final String STATISTIC_CONFIG_JO = "statistic_config";

        public final class BargainPrice {

            /**
             * 接口数据的key
             */
            public static final String BARGAIN_PRICE_LIST = "bargain_price_list";

            /**
             * ID
             */
            public static final String ID = "id";

            /**
             * 折扣率
             */
            public static final String DISCOUNT = "discount";

            /**
             * 标题
             */
            public static final String TITLE = "title";

            /**
             * 商品来源
             */
            public static final String FROM = "from";

            /**
             * 市场价格
             */
            public static final String MARKET_PRICE = "market_price";

            /**
             * 销售价格
             */
            public static final String SALE_PRICE = "sale_price";

            /**
             * 渠道icon
             */
            public static final String IMG = "img";

            /**
             * 链接
             */
            public static final String LINK = "link";

            /**
             * 时间
             */
            public static final String CREAT_TIME = "create_time";

            /**
             * 是否有下一页
             */
            public static final String HAS_NEXT = "hasnext";

            /**
             * 当前第几页
             */
            public static final String CURPAGE = "curpage";

            /**
             * 数据版本号
             */
            public static final String VERSION = "version";

            /**
             * 分类列表
             */
            public static final String CATEGORYS = "category";
        }

        public final class RecommendHome {
            public static final String ADVERTISE_BANNER_JO = "advertise_banner";
            public static final String ATTENTION_LIST_JO = "attention_list";
            public static final String SPEED_SERVICE_JO = "speed_service";
            public static final String SPECIAL_REGION_JO = "special_region";
            public static final String SEARCH_DEFAULT_KEY_JO = "search_default_keyword";
            public static final String MATCH_REGULAR_JO = "match_regular";
            public static final String LOW_PRICE_NOTICE_JO = "low_price_notice";
            public static final String USER_INFO_JO = "user_info";
            public static final String HAS_GIFT_JO = "has_gift_notice";
        }

        public final class AddAttention {
            public static final String CHANNEL_JO = "channel_list";
        }

        public final class AppRecommond {
            /**
             * 页面数据存放key
             */
            public static final String APP_LIST_INFO_JO = "app_json_list_info";
            public static final String APP_LIST_JA = "app_list";
            public static final String HAS_NEXT_B = "has_next";
            public static final String CURRENT_PAGE_I = "current_page";
            /**
             * 应用列表 MyBean list
             */
            public static final String APP_INFO_LIST_AL = "app_info_array";
            /**
             * 应用json信息
             */
            public static final String APP_INFO_JO = "app_json_info";
            /**
             * 应用状态
             */
            public static final String APP_STATUS_EM = "app_status";
            /**
             * 下载百分比
             */
            public static final String APP_DOWNLOAD_PERCENT_I = "app_download_percent";
            /**
             * 应用下载ID，由downloadManager返回
             */
            public static final String APP_DOWNLOAD_ID_L = "app_download_id";
            /**
             * 应用在列表中的位置
             */
            public static final String APP_LIST_POSITION_I = "app_list_position";
            /**
             * 应用名称
             */
            public static final String APP_FILE_NAME = "app_file_name";

        }

        public final class CommentsList {

            /**
             * 知物导航标题数据存放key
             */
            public static final String COMMENTS_TITLE = "comments_json_list_title_info";

            /**
             * 页面数据存放key
             */
            public static final String COMMENTS_LIST_INFO_JO = "comments_json_list_info";
            public static final String ID = "id";
            public static final String IS_NEWEST = "is_newest";
            public static final String TITLE = "title";
            /**
             * 作者名称
             */
            public static final String AUTHOR = "author";
            public static final String IMGS = "images";
            public static final String SUMMARY = "summary";
            public static final String CREATE_TIME = "start_time";
            /**
             * 赞数量
             */
            public static final String PRAISE = "praise";
            /**
             * 新的赞数量，点赞+收藏量的和
             */
            public static final String LIKE = "like";

            public static final String URL = "url";
            /**
             * 是否推荐
             */
            public static final String RECOMMEND = "recommend";

            /**
             * 收藏量
             */
            public static final String FAVORITE = "favorite";
            /**
             * 评论数
             */
            public static final String COMMENT_COUNT = "comment";
            /**
             * 是否已收藏;0:未收藏，1：已收藏
             */
            public static final String IS_FAVORITE = "is_favorite";
            /**
             * 若已收藏，返回收藏的id
             */
            public static final String FAVORITE_ID = "fav_id";
            public static final String WORDS = "words";

            /**
             * 作者头像
             */
            public static final String AVATAR = "avatar";

            public static final String QUESTION_SUMMARY = "intro"; // 问答入口简介
            public static final String QUESTION_COUNT = "qus_total"; // 问题数量
            public static final String ANSWER_COUNT = "ans_total"; // 答案数量
            public static final String QUESTION_BACKGROUD = "image"; // 示意图
            public static final String SOURCE = "source"; // 来源
            public static final String IMAGE = "image"; // 图片
            public static final String HITS = "hits"; // 查看数量

        }

        public final class ZhiwuFavorList {
            /**
             * 页面数据存放key
             */
            public static final String ZHIWUFAVOR_LIST_INFO_JO = "zhiwu_favor_json_list_info";
            public static final String ID = "id";
            public static final String TITLE = "title";
            public static final String HITS = "hits";
            public static final String COMMENT = "comment";
            public static final String URL = "url";
            public static final String IMAGE = "image";
            public static final String CAT = "cat";
            public static final String BGCOLOR = "bgcolor";
        }

        public final class CategoryTabList {
            public static final String LIST_INFO_JO = "category_tab_list";
        }

        public final class CommentsPraise {
            public static final String LIST_INFO_JO = "comments_praise";
            public static final String TYPE = "type";
        }

        public final class CommentsFavorite {
            public static final String LIST_INFO_JO = "comments_favorite";
            public static final String FAVORITE_ID = "fav_id";
        }

        public final class CommentsIsFavorite {
            public static final String IS_FAVOR_INFO_JO = "comments_is_favorite";
            public static final String IS_FAV = "is_fav";
            public static final String FAV_ID_I = "fav_id";
        }
        
        public final class CancelFavorite {
            public static final String LIST_INFO_JO = "cancel_favorite";
        }

        public final class RemoveFavorite {
            public static final String LIST_INFO_JO = "remove_favorite";
        }

        public final class SearchWords {
            public static final String SEARCH_INFO_JO = "search_info";
        }

        public final class SearchAutofill {
            public static final String SEARCH_AUTOFILL_JO = "search_auto_fill";
        }

        public class BaseMyFavoriteList {
            /**
             * id 收藏ID integer
             */
            public static final String ID = "id";

            /**
             * 收藏项在表中ID
             */
            public static final String ITEM_ID = "item_id";
            /**
             * uid 用户uid integer
             */
            public static final String UID = "uid";
            /**
             * type 分类（3） integer
             */
            public static final String TYPE = "type";
            /**
             * title 标题 string
             */
            public static final String TITLE = "title";
            /**
             * image 店铺logo string
             */
            public static final String IMAGE = "image";
            /**
             * src 电商平台 string
             */
            public static final String SRC = "src";

            /**
             * url 详情链接地址 string
             */
            public static final String URL = "url";

            /**
             * create_time 收藏时间 integer
             */
            public static final String CREATE_TIME = "create_time";
            public static final String FAV_ID_I = "fav_id";
        }

        public final class GoodsList extends BaseMyFavoriteList {
            /**
             * 页面数据存放key
             */
            public static final String GOODS_LIST_INFO_JO = "goods_json_list_info";
            /**
             * price 价格 integer
             */
            public static final String PRICE = "price";
        }

        public final class ShopList extends BaseMyFavoriteList {
            /**
             * 页面数据存放key
             */
            public static final String SHOP_LIST_INFO_JO = "shop_json_list_info";
            /**
             * level 店铺等级 string
             */
            public static final String LEVEL = "level";
        }

        public final class WebPageList extends BaseMyFavoriteList {
            /**
             * 页面数据存放key
             */
            public static final String WEBPAGE_LIST_INFO_JO = "webpage_json_list_info";
        }

        public final class DiscussList {
            public static final String LIST_INFO_JO = "discuss_list";
            public static final String HAS_NEXT = "hasnext";
            public static final String CURPAGE = "curpage";
            public static final String LIST = "list";
            public static final String CONTENT = "content";
            public static final String TIME = "create_time";
            public static final String NIKE_NAME = "nickname";
            public static final String UID = "uid";
            public static final String REGION = "region";
            public static final String ITME_ID = "item_id";
            public static final String ID = "id";
            public static final String PRAISE = "praise";
            public static final String PRAISE_ID = "praise_id";
        }

        public final class CutList {
            public static final String LIST_INFO_JO = "cut_list";
            public static final String HAS_NEXT_B = "hasnext";
            public static final String CURPAGE_I = "curpage";
            public static final String ID_I = "id";
            public static final String SHOP_TIILE_S = "shop_title";
            public static final String SHOP_URL_S = "shop_url";
            public static final String GOODS_TILTE_S = "goods_title";
            public static final String SHARE_TILTE_S = "share_title";
            public static final String GOODS_IMG_S = "goods_img";
            public static final String PRICE_S = "price";
            public static final String CURRENT_PRICE_S = "current_price";
            public static final String CAN_CUT_B = "can_cut";
            public static final String CUT_CODE_I = "cut_code";
            public static final String CUT_MSG_S = "cut_msg";
            public static final String CUT_INFO_S = "cut_info";
            public static final String CUT_URL_S = "cut_url";
            public static final String DETAIL_URL_S = "detail_url";
            public static final String SHARE_URL_S = "share_url";
            public static final String SHOP_LOGO = "shop_logo";
            public static final String LIST_S = "list";
            public static final String TIPS_S = "tips";

        }

        public final class CutData {
            public static final String DATA_S = "data";
            public static final String CUT_INFO_S = "cut_info";
            public static final String CUT_DATA_S = "cut_data";
            public static final String ID_I = "id";
            public static final String CURRENT_PRICE_S = "current_price";
            public static final String STATUS_S = "status";
            public static final String CUT_MSG_S = "cut_msg";
            public static final String CAN_CUT_B = "can_cut";
            public static final String CUT_CODE_I = "cut_code";
            public static final String IS_CUT_B = "is_cut";
            public static final String RANGE_S = "range";
            public static final String IS_SCORE = "is_score";
        }

        public final class GoodStatus {
            public static final String DATA_INFO_S = "data_info";
            public static final String DATA_S = "data";
            public static final String ID_I = "id";
            public static final String CURRENT_PRICE_S = "current_price";
            public static final String CUT_MSG_S = "cut_msg";
            public static final String CAN_CUT_B = "can_cut";
            public static final String CUT_CODE_I = "cut_code";
            public static final String IS_CUT_B = "is_cut";
            public static final String IS_BUY_B = "is_buy";
        }

        public class SameStyleInfo {
            public static final String SAME_STYLE_LIST = "same_style_list";
            public static final String LIST_INFO_JO = "list";
            public static final String IMAGE = "img"; // 图url
            public static final String TITLE = "title"; // 标题
            public static final String PRICE = "price"; // 价格
            public static final String EXPRESS_METHOD = "express"; // 快递方式
            public static final String PRICE_LEVEL = "is_min_price"; // 价格等级
            public static final String SALES_VOLUME = "pay_num"; // 月售
            public static final String SALES_LEVEL = "is_max_pay"; // 销量等级
            public static final String SCORE = "score"; // 评分
            public static final String AREA = "area"; // 地区
            public static final String CHANNEL = "channel"; // 渠道； 淘宝/天猫/京东
            public static final String URL = "url"; // 详情页地址
            public static final String SHOP_NAME = "shop_title"; // 店铺
            public static final String STAR_LEVEL = "shop_level"; // 皇冠等级
            public static final String STAR_IMAGE = "level_icon"; // 皇冠icon url
            public static final String DESCRIPTION = "description"; // 描述相符
            public static final String SERVICE = "service"; // 服务等级
            public static final String LOGISTICS = "logistics"; // 物流等级
            public static final String IS_CURRENT = "is_current"; // 是否当前款
            public static final String IS_HIGH_LIGHT = "is_high_light"; // 评分是否高亮显示
        }

        public final class ShoppingList extends BaseMyFavoriteList {
            /**
             * 页面数据存放key
             */
            public static final String SHOPPING_LIST_INFO_JO = "shopping_json_list_info";
            /**
             * price 价格 integer
             */
            public static final String PRICE = "price";

            /**
             * reduce 降价...
             */
            public static final String REDUCE = "reduce";

            /**
             * 收藏分类名称：商品/店铺/网页
             */
            public static final String TYPE_NAME = "type_name";
        }

        /**
         * @description 商品促销信息
         */
        public final class PromotionalSaleInfo {
            public static final String PROMOTIONAL_SALE_INFO = "promotional_sale_info";
        }

        /**
         * 积分类型id
         */
        public final class CumulateScore {
            public static final String TYPE_ID = "type_id";
        }

        public final class SubmitHotOrder {
            public static final String SUBMIT_HOT_ORDER_INFO_JO = "submit_json_info"; // 页面数据存放key
            public static final String ID = "id"; // 晒单单号
        }

        public final class ShowHotOrder {
            public static final String SHOW_HOT_ORDER_INFO_JO = "show_json_info"; // 页面数据存放key
            public static final String ID = "id"; // 晒单单号
            public static final String OID = "oid"; // 订单单号
            public static final String TITLE = "title"; // 标题
            public static final String CONTENT = "content"; // 内容
            public static final String CREATE_TIME = "create_time"; // 创建时间
            public static final String STATUS = "status_label"; // 晒单状态
            public static final String IMAGES = "images_thumb"; // 缩略图图片列表
            public static final String IMAGE = "img"; // 缩略图url
            public static final String IMAGE_TAG = "tag"; // 缩略图url的md5
            public static final String IS_EDIT = "is_edit"; // 是否可以编辑
            public static final String REASON = "reason"; // 审核不通过的原因
            public static final String AUTHOR = "author"; // 作者昵称
        }

        public final class SubmitDiscussPraise {
            public static final String DISCUSS_PRIASE_INFO = "praise_info";
            public static final String ID = "id";
        }

        public final class QuestionList {
            public static final String ID = "id";
            public static final String QUESTION_LIST_INFO = "question_list_info";
            public static final String LIST_INFO_JO = "list";
            public static final String TITLE = "title"; // 标题
            public static final String AVATAR = "q_author_avatar"; // 头像
            public static final String NICK_NAME = "q_author_nickname"; // 昵称
            public static final String ANSWER_COUNT = "ans_total"; // 回答数量
            public static final String ANSWER_LIST = "ans_list"; // 回答列表
            public static final String ANSWER_NICK_NAME = "a_anthor_nickname"; // 回答者昵称
            public static final String ANSWER_CONTENT = "ans_content"; // 回复的内容
        }

        public final class QuestionDetailList {
            public static final String QUESTION_DETAIL_LIST_INFO = "question_detail_list_info";
            public static final String LIST_INFO_JO = "list";
            public static final String QID = "id"; // 问贴qid
            public static final String TITLE = "title"; // 问贴标题
            public static final String CONTENT = "content"; // 问贴描述
            public static final String IMAGES = "images"; // 问贴图片列表
            public static final String QUESTION_AVATAR = "q_author_avatar"; // 问贴作者头像
            public static final String QUESTION_NICK_NAME = "q_author_nickname"; // 问贴作者昵称
            public static final String ANSWER_COUNT = "ans_total"; // 回答数量
            public static final String AID = "id"; // 回帖aid
            public static final String ANSWER_AVATAR = "a_author_avatar"; // 问贴作者头像
            public static final String ANSWER_NICK_NAME = "a_author_nickname"; // 问贴作者昵称
            public static final String ANSWER_PRAISE_COUNT = "ans_praise"; // 赞数
            public static final String ANSWER_CONTENT = "ans_content"; // 回复的内容
            public static final String ANSWER_TIME = "ans_time"; // 赞数
            public static final String RID = "jid"; // 跳转问贴rid
            public static final String JUMP_TITLE = "j_title"; // 跳转问贴标题
            public static final String VERIFY_CODE = "verify_code"; // 验证码
            public static final String FROM = "from"; // 回复者
            public static final String URL = "url"; // 问贴的url 用于分享
        }

        public final class QuestionInfo {
            public static final String QUESTION_INFO = "question_info";
            public static final String QUESTION_AVATAR = "author_avatar"; // 问贴作者头像
            public static final String QUESTION_NICK_NAME = "author_nickname"; // 问贴作者昵称
            public static final String IS_EDIT = "is_edit"; // 是否需要编辑
            public static final String VERIFY_CODE = "verify_code"; // 验证码
        }

        public final class MessageList {
            public static final String MESSAGE_LIST_INFO_JO = "messages_list";
            public static final String LIST_S = "list";
            public static final String HAS_NEXT_B = "hasnext";
            public static final String CURPAGE_I = "curpage";
            public static final String URL_S = "url";
            public static final String TIME_S = "time";
            public static final String LABEL_S = "label";
            public static final String FROM_S = "from";
            public static final String CATE_I = "cate";
            public static final String IS_SYSTEM_B = "is_sys";
            public static final String MESSAGE_S = "msg";
            public static final String ID_S = "id";
            public static final String DESCRIPTION_S = "desc";
            public static final String IS_FAVORITE_B = "is_favorite";
            public static final String FAV_ID_I = "fav_id";
            public static final String LIKE_S = "like";
            public static final String COMMENT_I = "comment";
            public static final String TRUE_ID = "true_id";
        }

        public final class FAQsList {
            public static final String ANSWERS_INFO_JO = "answers_list";
            public static final String QUESTIONS_INFO_JO = "questions_list";
            public static final String LIST_S = "list";
            public static final String HAS_NEXT_B = "hasnext";
            public static final String CURPAGE_I = "curpage";
            public static final String ID_S = "id";
            public static final String TITLE_S = "title";
            public static final String ANS_TOTAL_S = "ans_total";
            public static final String STATUS_LABEL_S = "status_label";
            public static final String AUTHOR_AVATAR_S = "author_avatar";
            public static final String AUTHOR_NICKNAME_S = "author_nickname";
            public static final String ANSWER_CONTENT = "ans_content";
            public static final String ANSWER_CONTENT_LIST_S = "ans_list";
            public static final String STATUS_I = "status";
            public static final String REASON_S = "reason";
            public static final String FROM_S = "from";
            public static final String QUSTION_ID_I = "qus_id";
        }

        public final class AnswerPraise {
            public static final String ID = "id";
        }

        public final class ModifyUserInfo {
            public static final String MODIFY_INFO_S = "modify_info";
            public static final String AVATAR = "avatar";
        }

        public final class GameSpushNoOne {
            public static final String GAME_SPUSHNOONE_JO = "game_SpushNoOne_jo";
        }

        public final class QuestionCatagoryList {
            public static final String COMMONQUESTION_CATAGORY_JO = "CommonQuestion_Catagory_jo";
            public static final String COMMONQUESTION_CATAGORY_LIST_JA = "list";
            public static final String CATAGORYID_I = "id";
            public static final String QUESTION_TITLE_S = "title";
            public static final String QUESTION_NAME_S = "name";
            public static final String QUESTION_ICON_S = "icon";

        }

        public final class CommonQuestionList {
            public static final String COMMONQUESTION_JO = "CommonQuestion_jo";
            public static final String COMMONQUESTION_LIST_JA = "list";
            public static final String QUESTION_ID_S = "id";
            public static final String QUESTION_CID_S = "cid";
            public static final String QUESTION_URL_S = "url";
            public static final String QUESTION_QUESTION_S = "question";
        }

        public final class Navmain {
            public static final String DATA_JO = "data";
            public static final String ICON_1_1_S = "icon_1_1";
            public static final String ICON_1_2_S = "icon_1_2";
            public static final String ICON_2_1_S = "icon_2_1";
            public static final String ICON_2_2_S = "icon_2_2";
            public static final String ICON_3_1_S = "icon_3_1";
            public static final String ICON_3_2_S = "icon_3_2";
            public static final String ICON_4_1_S = "icon_4_1";
            public static final String ICON_4_2_S = "icon_4_2";
            public static final String ICON_5_1_S = "icon_5_1";
            public static final String ICON_5_2_S = "icon_5_2";
            public static final String ICON_5_LINK_S = "icon_3_link";
            public static final String TXT_COLOR_1_S = "txt_color_1";
            public static final String TXT_COLOR_2_S = "txt_color_2";
            public static final String TAB_BG_S = "tab_bg";
            public static final String VERSION_S = "version";
        }

        public final class GetOrdersHistory {
            public static final String ORDERHISTORY_JO = "order_history_jo";
            public static final String HISTORY_LIST_JA = "list";
            public static final String URL_S = "url";
            public static final String DATE_S = "date";
            public static final String CONTENT_S = "content";
            public static final String HASNEXT_B = "hasnext";
            public static final String CURPAGE_I = "curpage";
        }

        public final class AddOrder {
            public static final String ADD_ORDER_JO = "add_order_jo";
        }

        public final class Upgrate {
            public static final String UPGRATE_JO = "upgrate_jo";
            public static final String ID_S = "id";
            public static final String BG_IMG_S = "bg_img";
            public static final String SAY_S = "say";
            public static final String DESCRIPTION_S = "description";
            public static final String VERSION_NAME_S = "version_name";
            public static final String VERSION_CODE_S = "version_code";
            public static final String VERSION_SIZE_S = "size";
            public static final String CREATE_TIME_S = "create_time";
            public static final String UPGRATE_URL_S = "url";
            public static final String STAFF_JO = "staff";
            public static final String DESIGNER_NAME_S = "nickname";
            public static final String JOB_ID_S = "job_id";
            public static final String DESIGNER_ICON_S = "avatar";
            public static final String EGG_S = "egg";
        }

        public final class Zhiwu {
            public static final String ORDERHISTORY_JO = "order_history_jo";
            public static final String HISTORY_LIST_JA = "list";
            public static final String URL_S = "url";
            public static final String DATE_S = "date";
            public static final String CONTENT_S = "content";
            public static final String HASNEXT_B = "hasnext";
            public static final String CURPAGE_I = "curpage";
        }

        public final class NewsList {
            public static final String TALE_LIST_JO = "tale_list_jo";
            public static final String LIST_JA = "list";
            public static final String ID_S = "id";
            public static final String TITLE_S = "title";
            public static final String HITS_I = "hits";
            public static final String COMMENT_S = "comment";
            public static final String URL_S = "url";
            public static final String IMAGE_S = "image";
            public static final String HASNEXT_B = "hasnext";
            public static final String CURPAGE_I = "curpage";
            public static final String RECOMMAND_B = "recommend";
            public static final String FAVORITE_B = "is_favorite";
            public static final String BGCOLOR = "bgcolor";
            public static final String FAV_ID_I = "fav_id";
        }
    }
}
//Gionee <yuwei><2013-8-15> add for CR00821559 end