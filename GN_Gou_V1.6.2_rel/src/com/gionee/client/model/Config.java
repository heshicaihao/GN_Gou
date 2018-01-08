/*
 * Config.java
 * classes : com.gionee.client.Statistic.Config
 * @author yuwei
 * V 1.0.0
 * Create at 2013-4-15 上午10:21:48
 */
package com.gionee.client.model;

import com.gionee.client.business.util.GNReadPropertyFile;
import com.gionee.client.business.util.LogUtils;

/**
 * com.gionee.client.Statistic.Config
 * 
 * @author yuwei <br/>
 *         create at 2013-4-15 上午10:21:48 TODO
 */
public class Config {
	private static final String TAG = "Config";

	public static final String TEST_URL = "http://apk.gou.3gtest.gionee.com/";
	public static final String PRODUCT_URL = "http://apk.gou.gionee.com/";
	public static final boolean IS_STATIC_ENABLE = false;
	public static final String URL = getUrl();
	public static final String RECOMMOND_URL = URL;
	public static final String PERSONAL_CENTER_URL = URL + "shop";

	public static final String PAY_ON_DELIVERY_URL = URL + "cod";
	public static final String PERSONAL_LOGIN_URL = URL + "user/login/index";
	public static final String PERSONAL_URL = URL + "user";
	public static final String SEARCH_URL = URL
			+ "front/index/search/?keyword=";
	public static final String RECOMMOND_URL_WITH_PARAMETER = RECOMMOND_URL
			+ "?";
	public static final String PAY_ON_DELIVERY_URL_WITH_PARAMETER = PAY_ON_DELIVERY_URL
			+ "?";
	public static final String PERSONAL_LOGIN_SUCCESS_URL = URL
			+ "user/login/login?code=";
	public static final String MOBILE_PHONE_PARTS_RUL = URL + "mall";
	public static final String FATION_WOMEN_CLOTHING_RUL = URL + "new";
	public static final String NEW_WOMEN_CLOTH_RUL = URL + "index";
	public static final String USER_SETTINT_URL = URL + "user/setting/index";
	public static final String HELP_URL = URL + "help";
	public static final String USER_LOGIN_OUG_JUMP_URL = "http://t-id.gionee.com/user/logout";
	public static final String USER_LOGIN_OUT_URL = URL + "/user/login/logout";
	public static final String SCORE_TO_GOODS_URL = URL + "goods";

	public static final String PERSONAL_DISIRE_URL = URL + "user/want/index";
	public static final String PERSONAL_ADDRESS_URL = URL
			+ "user/address/index";
	public static final String PERSONAL_SETTING_URL = URL
			+ "user/setting/index";
	public static final String PERSONAL_ORDER_LIST_URL = URL
			+ "user/account/order_list";
	public static final String LOGIN_URL = URL + "user/login/login_step?";
	public static final String WEIBO_AUTH_URL = URL
			+ "api/clientCommon/weiboAuth";
	public static final String SEARCH_KEY_URL = URL
			+ "api/apk_search/submit?keyword=";
	public static final String GET_SEARCH_KEY_URL = URL
			+ "api/apk_search/keywords";

	public static final String MINE_ACTIVITY_DETAIL = URL + "winprize/activity";
	public static final String GAME_RULE = URL + "winprize/rule";

	public static final String LOGIN_REDIRECT_URL = "http://t-id.gionee.com/oauth/authorize?";
	public static final String TAOBAO_ONCLICK_URL = "http://s.click.taobao.com/";
	public static final String TAOBAO_JUMP_URL = "http://ai.m.taobao.com/detail.html";

	public static final String TAOBAO_CLIENT_URL = "taobao://a.m.taobao.com";
	public static final String TMALL_CLIENT_URL = "tmall://tmallclient";
	public static final String ITMALL_MOBILE_URL = "itmall://mobile.tmall.com";
	public static final String TAOBAO_ITEM_URL = "taobao://item.taobao.com";
	public static final String TAOBAO_SHOP_URL = "taobao://shop.m.taobao.com";
	public static final String TAOBAO_H5_URL = "taobao://h5.m.taobao.com";
	public static final String TAOBAO_ERR_URL = "taobao://#";
	public static final String APPLE_APP_URL = "http://itunes.apple.com";
	public static final String TAOBAO_A_M_URL = "taobao://m.taobao.com";
	public static final String TAOBAO_WEB_URL = "taobaowebview://m.taobao.com";

	public static final String LOGISTICS_QUERY = "http://apk.gou.gionee.com/index/redirect?url_id=1951";

	// statistic data url

	public static final String COUNT_TEST_URL = "http://gou.3gtest.gionee.com/front/index/clienttj/?";
	public static final String COUNT_URL = "http://gou.gionee.com/front/index/clienttj/?";

	// Gionee <Yangxiong> <2013-8-15> modify for CR00838312 begin
	/**
	 * 金立购服务端接口 测试环境 和生产环境URL前缀
	 */
	public static final String PREFIX_SERVER_TEST_URL = "http://gou.3gtest.gionee.com/";
	public static final String PREFIX_SERVER_URL = "http://gou.gionee.com/";
	private static final String SERVER_URL = getServerUrl();

	/**
	 * 用户行为统计接口
	 */
	public static final String STATISTICS_TEST_URL = "http://s.gou.3gtest.gionee.com";
	public static final String STATISTICS_PRODUCT_URL = "http://s.gou.gionee.com";
	public static final String STATISTICS_URL = getStatisticsUrl();

	public static final int TEST_UPGRATE_FLAG = 1;// 测试环境自定义升级下载链接
	public static final int PRODUCT_UPGRATE_FLAG = 0;// 正式环境自定义升级下载链接

	/**
	 * 闪屏接口
	 */
	public static final String LOGO_BACKGROUND_URL = SERVER_URL
			+ "api/clientcommon/start";

	private static String getServerUrl() {
		if (GNReadPropertyFile.isTestEnvironment()) {
			LogUtils.log(TAG, LogUtils.getThreadName() + "url :"
					+ PREFIX_SERVER_TEST_URL);
			return PREFIX_SERVER_TEST_URL;
		}
		LogUtils.log(TAG, LogUtils.getThreadName() + "server url :"
				+ PREFIX_SERVER_URL);
		return PREFIX_SERVER_URL;

	}

	// Gionee <Yangxiong> <2013-8-15> modify for CR00838312 end

	private static String getUrl() {
		if (GNReadPropertyFile.isTestEnvironment()) {
			LogUtils.log(TAG, LogUtils.getThreadName() + "url :" + TEST_URL);
			return TEST_URL;
		}
		LogUtils.log(TAG, LogUtils.getThreadName() + "url :" + PRODUCT_URL);
		return PRODUCT_URL;
	}

	private static String getStatisticsUrl() {
		if (GNReadPropertyFile.isTestEnvironment()) {
			LogUtils.log(TAG, LogUtils.getThreadName() + "statistics url :"
					+ STATISTICS_TEST_URL);
			return STATISTICS_TEST_URL;
		}
		LogUtils.log(TAG, LogUtils.getThreadName() + "statistics url :"
				+ STATISTICS_PRODUCT_URL);
		return STATISTICS_PRODUCT_URL;
	}

	public static int getUpgrateDownloadUrl() {
		if (GNReadPropertyFile.isUpgrateTestEnvironment()) {
			LogUtils.log(TAG, LogUtils.getThreadName() + "url :" + TEST_URL);
			return TEST_UPGRATE_FLAG;
		}
		LogUtils.log(TAG, LogUtils.getThreadName() + "url :" + PRODUCT_URL);
		return PRODUCT_UPGRATE_FLAG;
	}

	public static final String mPriseRecordUrl = "winprize/awardlist";
	public static final String mGetPriseUrl = "winprize/award";

	public static final String mLocalJs = "javascript:var localjs=window.addEventListener('click', function() {"
			+ " window.share && window.share.onJsClick && window.share.onJsClick(); }, true);document.getElementsByTagName(\"HEAD\").item(0).appendChild(localjs);";
}
