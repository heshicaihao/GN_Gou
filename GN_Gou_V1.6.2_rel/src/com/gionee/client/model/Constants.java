package com.gionee.client.model;

public class Constants {

	public static final String WEIXIN_PAY = "weixin://wap/pay";
	public static final String WEIXIN_PACKAGENAME = "com.tencent.mm";
	public static final String SCHEME_WTAI = "wtai://wp/";
	public static final String SCHEME_WTAI_MC = "wtai://wp/mc;";
	public static final String SCHEME_WTAI_SD = "wtai://wp/sd;";
	public static final String SCHEME_WTAI_AP = "wtai://wp/ap;";
	public static final String SCHEME_SMS = "sms:";
	public static final String HOME_PAGE_URL = "http://app.gou.gionee.com";
	public static final String CMCC_IDE_NOTIFICATION1 = "wlanacname";
	public static final String CMCC_IDE_NOTIFICATION2 = "wlanuserip";
	public static final String UN_NETWORK = "file:///android_asset/unnetwork.html";
	public static final String HELP_URL = "file:///android_asset/help.html";
	public static final int CMCC_IDE_NOTIFICATION = 100;
	public static final int LOAD_TIME_OUT = 90;
	public static final int NET_UNABLE = 0;
	public static final int WIFI_AVAILABLE = 1;
	public static final int MOBILE_AVAILABLE = 2;
	public static final int OTHERNET = 3;
	public static final int ANIMATION_DURATION = 2000;
	public static final long CACHE_MAX_SIZE = 1024 * 1024 * 80;
	public static final String ITENT_FLAG_APP_POSITION = "position";
	public static final String UN_NETWORK_GUIDE = "file:///android_asset/guide_unnetwork.html";

	public static final String FB_QESTION = "qestion";
	public static final String FB_ANSWER = "answer";

	public static final String MARKRT_URL = "market://details?id=com.gionee.client";
	public static final String MARKET_WEB_URL = "http://www.anzhuoapk.com/apps-35-1/16526/";
	public static final String WEIXIN_SHARE_URL = "http://gou.gionee.com/?source=weixin_V";
	public static final String IS_SHOW_WEB_FOOTBAR = "is_show_footbar";
	public static final String SEND_DISCUSS_COUNT = "send_connt";
	public static final String TYPE_ID = "type_id";
	public static final String NULL = "null";
	public static final String WEIBO_NOTICE = "weibo_notice";
	public static final String DEFAULT_SHARE_ICON_URL = "https://mmbiz.qlogo.cn/mmbiz/DVibZk4zYaSaibiaeYU82Rxzq92U3ANs4s36Hsh9LpUfpgn6mwCotMuSbxY9TJFWYgicH4Z5u0O6yaVk11TLfwfveg/0";
	public static final int THUMB_SIZE = 150;
	public static final String HAS_LOW_PRICE = "has_low_price";
	public static final String CLEAR_LOW_PRICE = "clear_low_price";
	public static final String SELECT_IMAGE_LIST = "select_image_list";
	public static final int THRESHOLD_IMAGE_COUNT = 9;
	public static final String ORDER_ID = "orderId";
	public static final String HOT_ORDER_ID = "hotOrderId";
	public static final String NICK_NAME = "nickName";
	public static final String MAX_TAKE_IMAGES_COUNT = "max_take_images_count";
	public static final String INTENT_FLAT = "intent_flag";
	public static final String USER_HEAD_LOCAL_DEFUALT_PATH = "user_head_img.png";
	public static final String SECRET_KEY = "NTQzY2JmMzJhYTg2N2RvY3Mva2V5";

	public static final class Statistic {
		public static final String TYPE_ACTION = "action";
		public static final String TYPE_UPMODE = "updateMode";
		public static final String TYPE_NET = "nettype";
		public static final String KEY_ACTION = "boot";
		public static final String KEY_UPMODE_AUTO = "auto";
		public static final String KEY_UPMODE_MANUAL = "manual";
		public static final int MIN_UPGRADE_START_CHECKCOUNT = 3;
	}

	public static final class URI {
		public static final int APP_EVENT = 1;
		public static final int APP_EVENT_ID = 2;
	}

	public static class Home {
		public static final String KEY_INTENT_URL = "url";
		public static final String IS_SHOW_REFRESH = "is_show_refresh";
		public static final String KEY_INTENT_INDEX = "index";
		public static final String TAG_FRAGMENT = "fragment";
		public static final int HOME_RESULT_CODE = 5;
		public static final int PAGE_RECOMMOND_INDEX = 0;
		public static final int PAGE_PAY_ON_DELIVERY_INDEX = 1;
		public static final int PAGE_PERSONAL_CENTER_INDEX = 2;
		public static final int PAGE_SECONDARY_INDEX = -1;
		public static final String HOME_INTENT_FLAG = "page_url";
		public static final String TAG_PAGE_INDEX = "page_index";
		public static final String IS_SHOW_LIADING = "is_loading_shown";
		public static final String IS_SHOW_NET = "is_show_net";
		public static final int LOGIN_PAGE_RESULT = 2;
		public static final int HELP_PAGE_RESULT = 10;
		public static final int MORE_PAGE_RESULT = 20;
		public static final String IS_GOTO_ANOTHER_APP = "is_goto_other_app";
		public static final String PID_SHARE_PREFRENCE = "pic_share_prefrence";
		public static final String PID_NUM = "pid_num";
		/** index of recommond page. **/
		public static final int PAGE_RECOMMOND = 0;
		/** index of pay on delivery page. **/
		public static final int PAGE_CATEGARY = 1;
		/** index of personal center page. **/
		public static final int PAGE_STORY = 2;
		public static final int PAGE_MORE = 3;
		public static final String ACTIVITY_DIR = "/GN_GOU/";
		public static final String ACTIVITY_TAB_DATA = "activityTabData";
	}

	public static class ActivityRequestCode {
		public static final int REQUEST_CODE_MY_FAVORITE = 1000;
		public static final int REQUEST_CODE_COMMENT_DETAIL = 1001;
		public static final int REQUEST_CODE_HOME = 1002;
		public static final int REQUEST_CODE_DISCUSS = 1003;
		public static final int REQUEST_CODE_FROM_H5_EVERYDAT_TASK = 1004;
		public static final int REQUEST_CODE_HOT_ORDER_CAMERA = 1005;
		public static final int REQUEST_CODE_HOT_ORDER_GALLERY = 1006;
		public static final int REQUEST_CODE_FROM_H5_TO_HOT_ORDER = 1007;
		public static final int REQUEST_CODE_ADD_DESCRIPTION = 1008;
		public static final int REQUEST_CODE_CAMERA_FROM_AVATAR = 1009;
		public static final int REQUEST_CODE_CAMERA_FROM_ADDIMAGE = 1010;
		public static final int REQUEST_CODE_GALLERY_FROM_AVATAR = 1011;
		public static final int REQUEST_CODE_GALLERY_FROM_ADDIMAGE = 1012;
		public static final int REQUEST_CODE_PROFILE = 1013;
		public static final int REQUEST_CODE_QUESTION_DETAIL = 1014;
		public static final int REQUEST_CODE_WEB_PAGE = 1015;
		public static final int REQUEST_CODE_CUT_PAGE = 1016;
		public static final int REQUEST_CODE_BARGAIN_PRICE = 1017;
		public static final int REQUEST_CODE_SEARCH = 1018;
		public static final int REQUEST_CODE_TWO_DIMENSION = 1019;
	}

	public static class ActivityResultCode {
		public static final int RESULT_CODE_GOODS = 2000;
		public static final int RESULT_CODE_SHOP = 2001;
		public static final int RESULT_CODE_STORY = 2002;
		public static final int RESULT_CODE_WEBPAGE = 2003;
		public static final int RESULT_CODE_DISCUSS = 2004;
		public static final int RESULT_CODE_SHOPPING = 2005;
		public static final int RESULT_CODE_IMAGE_SELECT = 2006;
		public static final int RESULT_CODE_IAMGE_CLIP = 2007;
		public static final int REQUEST_CODE_PROFILE = 2008;
	}

	public static class CutPage {
		public static final String CUT_DODE = "cut_code";
		public static final String ID = "id";
		public static final String URL = "url";
		public static final String BITMAP = "bitmap";
		public static final String IMAGE_URL = "imageurl";
		public static final String CURRENT_PRICE = "current_price";
		public static final String PRICE = "price";
		public static final String TITLE = "title";
		public static final int CUT_STATE_CUT = 0;
		public static final int CUT_STATE_REDAY = 1;
		public static final int CUT_STATE_END = 2;
		public static final int CUT_STATE_HELP = 3;
		public static final int FLOOR_PRICE = 4;
		public static final int CUT_STATE_OFF = 5;
		public static final int CUT_STATE_AFTER = 6;
	}

	public static class GoodsContrast {
		public static final String BANNER_IMAGES = "image_urls"; // banner图url
		public static final String IMAGE = "img"; // image url
		public static final String TITLE = "title"; // 标题
		public static final String PRICE = "price"; // 价格
		public static final String EXPRESS_METHOD = "express"; // 快递方式
		public static final String SALES_VOLUME = "pay_num"; // 月售
		public static final String SCORE = "score"; // 评分
		public static final String AREA = "area"; // 地区
		public static final String CHANNEL = "channel"; // 渠道/平台
		public static final String URL = "url"; // 商品详情
		public static final String ID = "id";
	}

	public enum BannerAction {

		DAY_SPECIAL_PRICE("com.gionee.client.SpecialPrice"), STORY_LIST_PAGE(
				"com.gionee.client.StoryList"), STORY_DETAIL_PAGE(
				"com.gionee.client.StoryDetail"), CUT_LIST_PAGE(
				"com.gionee.client.CutList"), THIRD_PARTY_NORMAL(
				"com.gionee.client.ThirdParty"), MESSAGES_LIST_PAGE(
				"com.gionee.client.MesaggeList"), BARGAIN_GAME_PAGE(
				"com.gionee.client.BargainGame");
		private String mValue;

		BannerAction(String value) {
			this.mValue = value;
		}

		public String getValue() {
			return this.mValue;
		}

	}

	public static class Push {
		public static final String DATA = "data";
		public static final String TYPE = "type";
		public static final String URL = "url";
		public static final String ACTIION = "action";
		public final static int Normal = 0x00;
		public final static int BIG_TEXT_STYLE = 0x02;
		public final static int BIG_PICTURE_STYLE = 0x03;
		public final static int INBOX_STYLE = 0x04;
		public final static int CUSTOM_VIEW = 0x01;
		public final static String TITLE = "title";
		public final static String MSG = "msg";
		public final static String STYLE = "style";
		public final static String BIG_MSG = "big_msg";
		public final static String BIG_PICTURE = "big_pic";
		public final static String NOTIFICATION = "notice";
		public final static String ID = "id";
		public final static String BAIDU_APS = "baidu_aps";
		public final static String USER_ID = "user_id";
		public final static String CHANNEL_ID = "channel_id";
		public final static String SUCCESS = "success";
		public final static String IS_MSG = "is_msg";
		public final static String DESCRIPTION = "description";
		public final static String GAME_ID = "game_id";
		public final static String ACTIVITY = "activity";
		public static final String SOURCE = "source";
	}

	public static class History {
		public enum HistoryType {

			GOODS("goods"), SHOP("shop"), OTHERS("others");
			private String mValue;

			HistoryType(String value) {
				this.mValue = value;
			}

			public String getValue() {
				return this.mValue;
			}
		}
	}

	/**
	 * 1:每日签到; 2:每日砍价; 3:每日分享砍价; 4:每日邀请好友帮忙砍价; 5:每日分享购物大厅; 6:每日分享购物大厅链接;
	 * 7:关注购物大厅微博;
	 */
	public static final class ScoreTypeId {
		public static final String CHECK_IN = "1";
		public static final String CUT_PRICE = "2";
		public static final String SHARE_CUT_PRICE = "3";
		public static final String FRIENDE_CUT = "4";
		public static final String SHARE_APP = "5";
		public static final String SHARE_LINK = "6";
		public static final String ATTENTION_WEIBO = "7";
	}

	/**
	 * 
	 * web is default tag;
	 * 
	 */
	public static final class PAGE_TAG {
		public static final String CUT = "cut";
		public static final String STORY = "story";
	}

	public static final class CommentDialogType {
		public static final int COMMENT_STORY_TYPE = 1;
		public static final int COMMENT_QUESTION_TYPE = 2;
	}

	public static final int STATUS_LOADING = 0;
	public static final int STATUS_COMPLETE = 1;
	public static final int STATUS_ERROR = 2;

	public static final class CommentDialogStaus {
		/**
		 * 正常发表评论状态
		 */
		public static final int STATUS_NOMAL = 0;
		/**
		 * 包含敏感词状态
		 */
		public static final int STAUS_CONTAIN_SENSITIVE_WORDS = 1;
		/**
		 * 昵称少于一个字
		 */
		public static final int STATUS_NICKNAME_LESS_ONE = 2;
		/**
		 * 评论正在发送中
		 */
		public static final int STATUS_COMMENT_SENDDING = 3;
		/**
		 * 评论发送失败
		 */
		public static final int STATUS_SEND_ERROR = 4;

		public static final int STATUS_MODIFY_HEAD = 5;
	}

	/**
	 * home activity 来源标识(0 正常启动, 1 百度push正常展示, 2百度push自定义样式展示)
	 */
	public static final class BootSource {
		public static final String NORMAL = "0";
		public static final String PUSH_STYLE_BAIDU = "1";
		public static final String PUSH_STYLE_LOCAL = "2";
	}
}