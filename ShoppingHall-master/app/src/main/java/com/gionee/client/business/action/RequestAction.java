// Gionee <Yangxiong> <2013-8-15> modify for CR00838312 begin
/*
 * Action.java
 * classes : com.gionee.client.business.action.RequestAction
 * @author yangxiong
 * V 1.0.0
 * Create at 2013-5-28 下午5:43:01
 */
package com.gionee.client.business.action;

import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gionee.client.R;
import com.gionee.client.business.manage.ConfigManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.statistic.header.PublicHeaderParamsManager;
import com.gionee.client.business.statistic.util.MD5Utils;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Config;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;
import com.gionee.framework.model.config.ControlKey;
import com.gionee.framework.model.config.ControlKey.request.control;
import com.gionee.framework.model.config.ControlKey.request.control.CacheType;
import com.gionee.framework.operation.business.FRequestEntity.ListRequestParams;
import com.gionee.framework.operation.business.PortBusiness;
import com.gionee.framework.operation.business.RequestEntity;
import com.gionee.framework.operation.utills.Md5Utill;

/**
 * com.gionee.client.buisiness.action.Action
 * 
 * @author Yangxiong <br/>
 * @since 2013-08-14 TODO 网络请求Action
 */
public class RequestAction extends BaseRequestAction {
	private static final String TAG = "RequestAction";

	/**
	 * 闪屏接口
	 * 
	 * void
	 * 
	 * @param handle
	 *            回调接口实现类
	 * @param dataTagetKey
	 *            数据存放的key
	 * @param width
	 *            屏幕宽度
	 * @description TODO闪屏接口
	 * 
	 */
	public void getLogoBackGround(IBusinessHandle handle, String dataTagetKey,
			int width) {

		try {
			LogUtils.log("start_page", LogUtils.getFunctionName());
			RequestEntity request = new RequestEntity(
					Config.LOGO_BACKGROUND_URL, handle, dataTagetKey);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(HttpConstants.Request.GetLoadingInfo.CHANNEL_ID_I, 1);
			bean.put(HttpConstants.Request.GetLoadingInfo.WIDTH_I, width);
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			LogUtils.log("start_page", e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param handle
	 * @author yuwei
	 * @description TODO 获取应用列表
	 */
	public void getAppStoreUpdate(IBusinessHandle handle) {
		startNoParamsGetRequest(Url.APP_STORE_URL, handle, "", false);

	}

	/**
	 * @param handle
	 * @param dataTagetKey
	 * @author yuwei
	 * @description TODO 获取banner广告列表
	 */
	public void getAdvertiseBanner(IBusinessHandle handle, String dataTagetKey) {
		startNoParamsGetRequest(Url.ADVERTISE_BANNER_URL, handle, dataTagetKey);

	}

	/**
	 * @param handle
	 * @param dataTagetKey
	 * @author yuwei
	 * @description TODO 获取我的关注列表
	 */
	public void getMyAttentionList(IBusinessHandle handle, String dataTagetKey) {
		startNoParamsGetRequest(Url.ATTENTION_CHANNEL_URL, handle, dataTagetKey);

	}

	/**
	 * @param handle
	 * @param dataTagetKey
	 * @param page
	 * @param perpage
	 * @param categoryId
	 * @author yangxiong
	 * @description TODO 天天特价接口
	 */
	public void bargainPriceList(IBusinessHandle handle, String dataTagetKey,
			int page, int perpage, int categoryId) {
		try {
			RequestEntity request = new RequestEntity(Url.BARGAIN_PRICE_URL,
					handle, dataTagetKey, new ListRequestParams(
							HttpConstants.Response.BargainPrice.LIST_JA,
							page > 1));
			MyBean bean = request.getRequestParam();
			bean.put(HttpConstants.Request.PERPAGE_S, perpage);
			bean.put(HttpConstants.Request.PAGE_S, page);
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(HttpConstants.Request.BargainPrice.CATEGORY, categoryId);
			bean.put(ControlKey.request.control.__method_s, "GET");
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getChannlList(IBusinessHandle handle, String dataTagetKey) {
		startNoParamsGetRequest(Url.ADD_CHANNEL_URL, handle, dataTagetKey);

	}

	/**
	 * @param handle
	 * @param dataTagetKey
	 * @param page
	 * @param perpage
	 * @author yuwei
	 * @description TODO 应用推荐接口
	 */
	public void getRecommendAppList(IBusinessHandle handle,
			String dataTagetKey, int page, int perpage) {
		try {
			RequestEntity request = new RequestEntity(Url.APP_RECOMMEND_URL,
					handle, dataTagetKey, new ListRequestParams(
							HttpConstants.Response.BargainPrice.LIST_JA,
							page > 1));
			MyBean bean = request.getRequestParam();
			bean.put(HttpConstants.Request.PERPAGE_S, perpage);
			bean.put(HttpConstants.Request.PAGE_S, page);
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param handle
	 * @param dataTagetKey
	 * @author yuwei
	 * @description TODO 获取便捷服务列表
	 */
	public void getSpeedyServiceList(IBusinessHandle handle, String dataTagetKey) {
		try {
			RequestEntity request = new RequestEntity(Url.SPEEDY_SERVICE_URL,
					handle, dataTagetKey);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(HttpConstants.Request.APP_VERSION_CODE_I,
					AndroidUtils.getAppVersionCode(handle.getSelfContext()));
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param handle
	 * @param dataTagetKey
	 * @param page
	 * @param perpage
	 * @param categoryId
	 * @author yangxiong
	 * @description TODO 物语列表接口
	 */
	public void commentsList(IBusinessHandle handle, String dataTagetKey,
			int page, int perpage, String uid) {
		try {
			RequestEntity request = new RequestEntity(Url.BARGAIN_PRICE_URL,
					handle, dataTagetKey, new ListRequestParams(
							HttpConstants.Response.BargainPrice.LIST_JA,
							page > 1));
			MyBean bean = request.getRequestParam();
			bean.put(HttpConstants.Request.PERPAGE_S, perpage);
			bean.put(HttpConstants.Request.PAGE_S, page);
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.UID_S, uid);
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getSpecialRegion(IBusinessHandle handle, String dataTagetKey) {
		startNoParamsGetRequest(Url.SPECIAL_REGION, handle, dataTagetKey);

	}

	public void getCategoryTabList(IBusinessHandle handle, String dataTagetKey) {
		startNoParamsGetRequest(Url.CATEGORY_TAB_LIST_URL, handle, dataTagetKey);

	}

	public void getSearchDefaultKeyword(IBusinessHandle handle,
			String dataTagetKey, int type) {
		try {
			RequestEntity request = new RequestEntity(Url.SEARCH_RAND_KEYWORDS,
					handle, dataTagetKey);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.SearchRandKeywords.TYPE, type);
			PortBusiness.getInstance().startBusiness(request, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getSearchBarDefaultKeyword(IBusinessHandle handle,
			String dataTagetKey) {
		try {
			RequestEntity request = new RequestEntity(
					Url.SEARCHBAR_DEFAULT_KEYWORD_URL, handle, dataTagetKey);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 物语点赞
	 */
	public void commentsPraise(IBusinessHandle handle, String dataTagetKey,
			int id, int type) {
		try {
			RequestEntity request = new RequestEntity(Url.COMMENT_PRAISE_URL,
					handle, dataTagetKey);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.ComentsPraise.ID, id);
			bean.put(HttpConstants.Request.ComentsPraise.TYPE, type);
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 物语收藏, 框架中已加入uid
	 */
	public void commentsFavorite(IBusinessHandle handle, String dataTagetKey,
			int id) {
		try {
			RequestEntity request = new RequestEntity(Url.COMMENT_FAVORITE_URL,
					handle, dataTagetKey);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.ComentsFavorites.ID, id);
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 物语是否收藏
	 */
	public void commentsIsFavorite(IBusinessHandle handle, String dataTagetKey,
			int id, int type) {
		try {
			RequestEntity request = new RequestEntity(
					Url.COMMENT_IS_FAVORITE_URL, handle, dataTagetKey);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.ComentsIsFavorites.ID, id);
			bean.put(HttpConstants.Request.ComentsIsFavorites.TYPE, type);
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消收藏,
	 * 
	 * @param handle
	 * @param dataTagetKey
	 * @param favoriteId
	 *            收藏的id
	 * @param id
	 *            收藏对象的id，如物语的id
	 * @param type
	 *            收藏对象的类型，如物语的type为1.
	 */
	public void cancelFavorite(IBusinessHandle handle, String dataTagetKey,
			int favoriteId, int id, int type) {
		try {
			RequestEntity request = new RequestEntity(Url.CANCEL_FAVORITE_URL,
					handle, dataTagetKey);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.ComentsFavorites.ID, favoriteId);
			bean.put(HttpConstants.Request.CancelFavorites.COMMENT_ID, id);
			bean.put(HttpConstants.Request.CancelFavorites.TYPE, type);
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getCategoryGridData(IBusinessHandle handle, String url) {
		startNoParamsGetRequest(url, handle, url);

	}

	public void getMatchRegular(IBusinessHandle handle, String dataTargetKey) {

		startNoParamsGetRequest(Url.MATCHING_REGULATION_URL, handle,
				dataTargetKey);

	}

	public void removeFavorite(IBusinessHandle handle, String dataTargetKey,
			boolean isUseCache, MyBean otherParameters) {
		startGetRequest(Url.REMOVE_FAVORITE_URL, handle, dataTargetKey,
				isUseCache, otherParameters);
	}

	public void batchRemoveFavorite(IBusinessHandle handle,
			JSONArray deletesInfo) {
		try {
			RequestEntity request = new RequestEntity(
					Url.BATCH_REMOVE_FAVORITE_URL, handle);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(HttpConstants.Request.BatchRemoveFavorites.BATCH,
					deletesInfo.toString());
			bean.put(ControlKey.request.control.__method_s, "POST");
			PortBusiness.getInstance().startBusiness(request, bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void urlFavorite(IBusinessHandle handle, String url) {
		try {
			RequestEntity request = new RequestEntity(Url.ADD_FAVORITE_URL,
					handle);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put("url", url);
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void publishStoryComments(IBusinessHandle handle, String content,
			String storyId, String nickName, String commentId) {
		try {
			RequestEntity request = new RequestEntity(Url.STORY_COMMENT_URL,
					handle, HttpConstants.Data.DiscussList.PRAISE_ID);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "POST");
			bean.put(HttpConstants.Request.SendStoryComments.CONTENT_S, content);
			bean.put(HttpConstants.Request.SendStoryComments.SIGN_S,
					PublicHeaderParamsManager.getSign(handle.getSelfContext()));
			bean.put(HttpConstants.Request.SendStoryComments.ITEM_ID_I, storyId);
			bean.put(HttpConstants.Request.SendStoryComments.PARENT_ID,
					commentId);
			if (!TextUtils.isEmpty(nickName)) {
				bean.put(HttpConstants.Request.SendStoryComments.NICKNAME_S,
						nickName);
			}
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void answerQuestion(IBusinessHandle handle, String content,
			String nickName, String mainId, String secondId) {
		try {
			RequestEntity request = new RequestEntity(Url.ANSWER_QUESTION_URL,
					handle);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "POST");
			bean.put(HttpConstants.Request.AnswerQuestion.CONTENT_S, content);
			bean.put(HttpConstants.Request.SendStoryComments.NICKNAME_S,
					nickName);
			bean.put(HttpConstants.Request.AnswerQuestion.PID_I, secondId);
			bean.put(HttpConstants.Request.AnswerQuestion.QUS_ID_I, mainId);
			bean.put(HttpConstants.Request.SendStoryComments.SIGN_S, MD5Utils
					.getMD5String(PublicHeaderParamsManager.getUid(handle
							.getSelfContext())
							+ mainId
							+ secondId
							+ PublicHeaderParamsManager.MD5_SIGN));
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取评论列表
	 */

	public void getDiscussList(IBusinessHandle handle, String itemId, int page,
			String dataTagetKey) {
		try {
			RequestEntity request = new RequestEntity(
					Url.DISCUSS_LIST_URL,
					handle,
					dataTagetKey,
					new ListRequestParams(
							HttpConstants.Request.GetDiscussList.LIST, page > 1));
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.GetDiscussList.PAGE, page);
			bean.put(HttpConstants.Request.GetDiscussList.TYPE, "story");
			bean.put(HttpConstants.Request.GetDiscussList.ITEM_ID, itemId);
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getCutList(IBusinessHandle handle, int page,
			String dataTargetKey) {
		try {
			RequestEntity requestEntity = new RequestEntity(Url.CUT_LIST_URL,
					handle, dataTargetKey, new ListRequestParams(
							HttpConstants.Request.GetCutList.LIST, page > 1));
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.GetCutList.PAGE, page);
			bean.put(HttpConstants.Request.GetCutList.PERPAGE, 6);
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cutPrice(IBusinessHandle handle, int id, String dataTargetKey) {
		try {
			RequestEntity requestEntity = new RequestEntity(Url.CUT_URL,
					handle, dataTargetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(HttpConstants.Request.GetCutList.ID_I, id);
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getCutGoodInfo(IBusinessHandle handle, int id,
			String dataTargetKey) {
		try {
			RequestEntity requestEntity = new RequestEntity(
					Url.GET_CUT_GOOD_STATUS, handle, dataTargetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(HttpConstants.Request.GetCutGoodStatus.ID_I, id);
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 名称 描述 备注 goodsId 商品id required unipid 同款pid required
	 */
	public void getSameStyleinfo(IBusinessHandle handle, String dataTargetKey,
			String goodsId, String unipid) {
		try {
			RequestEntity requestEntity = new RequestEntity(
					Url.SAME_STYLE_LIST_URL, handle, dataTargetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(HttpConstants.Request.SameStyleList.ID_I, goodsId);
			bean.put(HttpConstants.Request.SameStyleList.PID_I, unipid);
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void hasLowPrice(IBusinessHandle handle, String dataTargetKey) {
		startNoParamsGetRequest(Url.LOW_PRICE_NOTICE_URL, handle,
				dataTargetKey, false);
	}

	public void removeLowPriceNotice(IBusinessHandle handle) {
		startNoParamsGetRequest(Url.REMOVE_LOW_PRICE_NOTICE_URL, handle, "",
				false);
	}

	/**
	 * @param scoreTypeId
	 *            id 积分类型 1:每日签到; 2:每日砍价; 3:每日分享砍价; 4:每日邀请好友帮忙砍价; 5:每日分享购物大厅;
	 *            6:每日分享购物大厅链接; 7:关注购物大厅微信;
	 * @description TODO 累积分数
	 */
	public void cumulateScore(IBusinessHandle handle, String scoreTypeId) {
		cumulateScore(handle, scoreTypeId, null);
	}

	public void cumulateScore(IBusinessHandle handle, String scoreTypeId,
			String shareChannel) {
		if (!ConfigManager.isScore(handle.getSelfContext())) {
			LogUtils.logd(TAG, LogUtils.getThreadName()
					+ " cumulate score is close");
			return;
		}
		;
		MyBean otherParameters = MyBeanFactory.createEmptyBean();
		otherParameters.put(HttpConstants.Request.CumulateScore.ID_I,
				scoreTypeId);
		if (!TextUtils.isEmpty(shareChannel)) {
			otherParameters.put(
					HttpConstants.Request.CumulateScore.SHARE_CHANNEL,
					shareChannel);
		}
		String secretKey = ConfigManager.getSecretKey(handle.getSelfContext());
		String sign = MD5Utils.getMD5String(scoreTypeId + secretKey);
		otherParameters.putString(HttpConstants.Request.CumulateScore.SIGN,
				sign);
		startGetRequest(Url.CUMULATE_SCORE_URL, handle, null, false,
				otherParameters);
	}

	/**
	 * @param handle
	 * @param dataTargetKey
	 *            获取各种商品促销信息
	 */
	public void getPromotionalSaleInfo(IBusinessHandle handle,
			String dataTargetKey) {
		startNoParamsGetRequest(Url.GET_PROMOTIONAL_SALES_URL, handle,
				dataTargetKey, false);
	}

	public void getUserConfig(IBusinessHandle handle, String dataTragetKey) {
		startNoParamsGetRequest(Url.USER_CONFIG_URL, handle, dataTragetKey,
				false);
	}

	// 晒单提交
	public void submitHotOrder(IBusinessHandle handle, String dataTagetKey,
			String orderId, String hotOrderId, String title, String content,
			Context context) {
		try {
			RequestEntity request = new RequestEntity(Url.SUBMIT_HOT_ORDER,
					handle, dataTagetKey);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "POST");
			bean.put(HttpConstants.Request.SubmitHotOrder.TITLE, title);
			bean.put(HttpConstants.Request.SubmitHotOrder.CONTENT, content);
			String uid = PublicHeaderParamsManager.getUid(context);
			String secretKey = ConfigManager.getSecretKey(handle
					.getSelfContext());
			StringBuilder signBuilder = new StringBuilder();
			signBuilder.append(uid);
			if (!TextUtils.isEmpty(hotOrderId)) {
				bean.put(HttpConstants.Request.SubmitHotOrder.ID_I, hotOrderId);
				signBuilder.append(hotOrderId);
			}
			if (!TextUtils.isEmpty(orderId)) {
				bean.put(HttpConstants.Request.SubmitHotOrder.OID_I, orderId);
				signBuilder.append(orderId);
			}
			signBuilder.append(secretKey);
			String sign = MD5Utils.getMD5String(signBuilder.toString());
			bean.put(HttpConstants.Request.SubmitHotOrder.SIGN, sign);
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 晒单显示（即晒单信息获取）
	public void showHotOrder(IBusinessHandle handle, String dataTargetKey,
			String orderId, String hotOrderId) {
		MyBean bean = MyBeanFactory.createEmptyBean();
		bean.put(HttpConstants.Request.ShowHotOrder.ID_I, hotOrderId);
		bean.put(HttpConstants.Request.ShowHotOrder.OID_I, orderId);
		startGetRequest(Url.SHOW_HOT_ORDER, handle, dataTargetKey, false, bean);
	}

	public void submitDiscussPriase(IBusinessHandle handle,
			String dataTargetKey, String id) {
		RequestEntity request = new RequestEntity(Url.COMMENT_LIKE_URL, handle,
				dataTargetKey);
		MyBean bean = request.getRequestParam();
		bean.put(ControlKey.request.control.__cacheType_enum,
				CacheType.noneCache);
		bean.put(HttpConstants.Request.GetDiscussList.TYPE, "1");
		bean.put(HttpConstants.Request.GetDiscussList.ITEM_ID, id);
		PortBusiness.getInstance().startBusiness(request, 0);
	}

	public void searchQuestion(IBusinessHandle handle, String dataTargetKey,
			String keyword) {
		MyBean bean = MyBeanFactory.createEmptyBean();
		bean.put(HttpConstants.Request.SearchQuestion.SEARCH_S, keyword);
		bean.put(ControlKey.request.control.__isShowLoading_b, true);
		startGetRequest(Url.SEARCH_QUESTION_URL, handle, dataTargetKey, false,
				bean);
	}

	public void getQuestionList(IBusinessHandle handle, String dataTargetKey,
			int page) {
		LogUtils.log(TAG, LogUtils.getThreadName() + " page = " + page);
		try {
			RequestEntity requestEntity = new RequestEntity(
					Url.QUESTION_LIST_URL, handle, dataTargetKey,
					new ListRequestParams(
							HttpConstants.Data.QuestionList.LIST_INFO_JO,
							page > 1));
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.PAGE_S, page);
			bean.put(HttpConstants.Request.PERPAGE_S, 10);
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getQuestionDetailList(IBusinessHandle handle,
			String dataTargetKey, int page, String questionId) {
		LogUtils.log(TAG, LogUtils.getThreadName() + " page = " + page
				+ " questionId = " + questionId);
		try {
			RequestEntity requestEntity = new RequestEntity(
					Url.QUESTION_DETAIL_URL, handle, dataTargetKey,
					new ListRequestParams(
							HttpConstants.Data.QuestionDetailList.LIST_INFO_JO,
							page > 1));
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.ID, questionId);
			bean.put(HttpConstants.Request.PAGE_S, page);
			bean.put(HttpConstants.Request.PERPAGE_S, 10);
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getQuestionInfo(IBusinessHandle handle, String dataTargetKey) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		startNoParamsGetRequest(Url.QUESTION_INFO_URL, handle, dataTargetKey);
	}

	public void submitQuestion(IBusinessHandle handle, String dataTargetKey,
			String nickName, String avatarFilePath, String content,
			String description, List<String> imagesFileList, String verifyCode,
			Context context) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		RequestEntity request = new RequestEntity(Url.QUESTION_SUBMIT_URL,
				handle, dataTargetKey);
		MyBean bean = request.getRequestParam();
		bean.put(ControlKey.request.control.__cacheType_enum,
				CacheType.noneCache);
		bean.put(ControlKey.request.control.__method_s, "POST");
		bean.put(HttpConstants.Request.QuestionSubmit.NICK_NAME, nickName);
		bean.put(HttpConstants.Request.QuestionSubmit.TITLE, content);
		bean.put(HttpConstants.Request.QuestionSubmit.CONTENT, description);
		String uid = PublicHeaderParamsManager.getUid(context);
		StringBuilder signBuilder = new StringBuilder();
		signBuilder.append(uid).append(Constants.SECRET_KEY);
		String sign = MD5Utils.getMD5String(signBuilder.toString());
		bean.put(HttpConstants.Request.QuestionSubmit.SIGN, sign);
		if (!TextUtils.isEmpty(avatarFilePath)) {
			bean.put(HttpConstants.Request.IMG_PARAMS_PREFIX
					+ HttpConstants.Request.QuestionSubmit.AVATAR,
					avatarFilePath);
		}
		for (int i = 0; i < imagesFileList.size(); i++) {
			bean.put(HttpConstants.Request.IMG_PARAMS_PREFIX + i,
					imagesFileList.get(i));
		}
		PortBusiness.getInstance().startBusiness(request, 0);
	}

	public void answerPraise(IBusinessHandle handle, String dataTargetKey,
			String answerId) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		MyBean bean = MyBeanFactory.createEmptyBean();
		bean.put(HttpConstants.Request.ID, answerId);
		startGetRequest(Url.ANSWER_PRAISE_URL, handle, dataTargetKey, false,
				bean);
	}

	public void getMyMessageList(IBusinessHandle handle, String dataTargetKey,
			int page) {
		try {
			RequestEntity requestEntity = new RequestEntity(Url.MESSAGES_LIST,
					handle, dataTargetKey, new ListRequestParams(
							HttpConstants.Request.GetCutList.LIST, page > 1));
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.GetCutList.PAGE, page);
			bean.put(HttpConstants.Request.GetCutList.PERPAGE, 6);
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getFAQsList(IBusinessHandle handle, String url,
			String dataTargetKey, int page) {
		try {
			RequestEntity requestEntity = new RequestEntity(url, handle,
					dataTargetKey, new ListRequestParams(
							HttpConstants.Request.GetCutList.LIST, page > 1));
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.GetCutList.PAGE, page);
			bean.put(HttpConstants.Request.GetCutList.PERPAGE, 10);
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void modifyUserInfo(IBusinessHandle handle, String path,
			String nickName, String dataTargetKey) {
		try {
			RequestEntity requestEntity = new RequestEntity(
					Url.MY_PROFILE_MODIFY, handle, dataTargetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "POST");
			if (!TextUtils.isEmpty(path)) {
				bean.put(HttpConstants.Request.ModifyUserInfo.IMG_AVATAR, path);
			}
			bean.put(HttpConstants.Request.ModifyUserInfo.NICK_NAME, nickName);
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取常见问题目录
	 * 
	 * @param handle
	 * @param dataTargetKey
	 * @param context
	 */
	public void getCatagoryList(IBusinessHandle handle, String dataTargetKey,
			Context context) {
		try {
			RequestEntity requestEntity = new RequestEntity(
					Url.COMMON_CATAGORY_LIST, handle, dataTargetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得常见问题列表
	 * 
	 * @param handle
	 * @param dataTargetKey
	 * @param context
	 */
	public void getCommonQuestionList(IBusinessHandle handle,
			String dataTargetKey, int catagoryId, int position, Context context) {
		try {
			RequestEntity requestEntity = new RequestEntity(
					Url.COMMON_QUESTION_LIST, handle, dataTargetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.GetCommonQuestionList.CID,
					catagoryId);
			PortBusiness.getInstance().startBusiness(requestEntity, position);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void submitStatisticsData(IBusinessHandle handle,
			String dataTargetKey, Context context, String eventIdJson,
			String source) {
		try {
			RequestEntity requestEntity = new RequestEntity(
					Url.STATISTICS_SUBMIT, handle, dataTargetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "POST");
			bean.put(HttpConstants.Request.StatisticsSubmit.DATA, eventIdJson);
			bean.put(HttpConstants.Request.StatisticsSubmit.SOURCE, source);
			String sign = eventIdJson
					+ PublicHeaderParamsManager.getUid(context)
					+ Constants.SECRET_KEY;
			bean.put(HttpConstants.Request.SIGN, Md5Utill.makeMd5Sum(sign));
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void submitFeedback(IBusinessHandle handle, String dataTargetKey,
			String content, String session) {
		try {
			RequestEntity requestEntity = new RequestEntity(Url.SEND_FEEDBACK,
					handle, dataTargetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.CONTENT, content);
			StringBuilder signBuilder = new StringBuilder();
			String uid = PublicHeaderParamsManager.getUid(handle
					.getSelfContext());
			signBuilder.append(uid).append(Constants.SECRET_KEY);
			String sign = MD5Utils.getMD5String(signBuilder.toString());
			bean.put(HttpConstants.Request.QuestionSubmit.SIGN, sign);
			PortBusiness.getInstance().startBusiness(requestEntity, session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getFeedbackList(IBusinessHandle handle, String dataTargetKey) {
		startNoParamsGetRequest(Url.GET_CONVERSATION_LIST, handle,
				dataTargetKey);
	}

	public void cleanFeedbackNotify(IBusinessHandle handle) {
		startNoParamsGetRequest(Url.CLEAN_MSG_NOTIFY, handle, "", false);
	}

	/**
	 * 首页活动主导航接口
	 * 
	 * @param handle
	 * @param dataTargetKey
	 * @param context
	 */
	public void getActivityTabData(IBusinessHandle handle,
			String dataTargetKey, Context context) {
		try {
			RequestEntity requestEntity = new RequestEntity(Url.NAVMAIN,
					handle, dataTargetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取订单历史列表
	 * 
	 * @param handle
	 * @param dataTargetKey
	 * @param context
	 */
	public void getOerderHistoryList(IBusinessHandle handle,
			String dataTargetKey, Context context, int mCurpage, int perPage) {
		RequestEntity request = new RequestEntity(Url.ORDER_HISTORY, handle,
				dataTargetKey, new ListRequestParams(
						HttpConstants.Data.GetOrdersHistory.HISTORY_LIST_JA,
						mCurpage > 1));
		MyBean bean = request.getRequestParam();
		bean.put(HttpConstants.Request.PERPAGE_S, perPage);
		bean.put(HttpConstants.Request.PAGE_S, mCurpage);
		bean.put(ControlKey.request.control.__cacheType_enum,
				CacheType.ShowCacheAndNet);
		bean.put(ControlKey.request.control.__method_s, "GET");
		PortBusiness.getInstance().startBusiness(request, 0);
	}

	/**
	 * @description TODO 物语列表接口
	 */
	public void getCommentsList(IBusinessHandle handle, String dataTagetKey,
			int page, int perpage) {
		try {
			RequestEntity request = new RequestEntity(Url.COMMENTS_LIST_URL,
					handle, dataTagetKey, new ListRequestParams(
							HttpConstants.Response.BargainPrice.LIST_JA,
							page > 1));
			MyBean bean = request.getRequestParam();
			bean.put(HttpConstants.Request.PERPAGE_S, perpage);
			bean.put(HttpConstants.Request.PAGE_S, page);
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getNewsList(IBusinessHandle handle, String dataTagetKey,
			int page, String cat_id) {
		try {
			RequestEntity request = new RequestEntity(Url.TALE_LIST, handle,
					dataTagetKey, new ListRequestParams(
							HttpConstants.Response.BargainPrice.LIST_JA,
							page > 1));
			MyBean bean = request.getRequestParam();
			bean.put(HttpConstants.Request.PERPAGE_S, 6);
			bean.put(HttpConstants.Request.PAGE_S, page);
			bean.put(HttpConstants.Request.NewsList.CAT_ID_S, cat_id);
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addOrder(IBusinessHandle handle, String dataTagetKey,
			String channelId) {
		try {
			RequestEntity requestEntity = new RequestEntity(Url.ADD_ORDER,
					handle, dataTagetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "POST");
			bean.put(HttpConstants.Request.AddOrderSubmit.CHANNELID, channelId);
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自定义升级接口
	 * 
	 * @param handle
	 * @param dataTargetKey
	 * @param context
	 */
	public void getUpgrate(IBusinessHandle handle, String dataTargetKey,
			Context context) {
		try {
			RequestEntity requestEntity = new RequestEntity(Url.UPGRATE,
					handle, dataTargetKey);
			MyBean bean = requestEntity.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.put(HttpConstants.Request.Upgrate.CHANNEL,
					context.getString(R.string.channel));
			bean.put(HttpConstants.Request.Upgrate.PRODUCT_KEY,
					context.getPackageName());
			bean.put(HttpConstants.Request.Upgrate.VERSION_CODE,
					AndroidUtils.getAppVersionCode(handle.getSelfContext()));
			bean.put(HttpConstants.Request.Upgrate.STATUS,
					Config.getUpgrateDownloadUrl());
			PortBusiness.getInstance().startBusiness(requestEntity, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @description TODO 物语列表标题接口
	 */
	public void getCommentsListNewTitel(IBusinessHandle handle,
			String dataTagetKey) {
		try {
			RequestEntity request = new RequestEntity(Url.COMMENTS_TITEL_INFO,
					handle, dataTagetKey);
			MyBean bean = request.getRequestParam();
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param handle
	 * @param dataTagetKey
	 * @param page
	 * @param perpage
	 * @param categoryId
	 * @author yangxiong
	 * @description TODO 物语列表接口
	 */
	public void requestData(IBusinessHandle handle, String dataTagetKey,
			int page, int perpage) {
		try {
			RequestEntity request = new RequestEntity(Url.ZHIWU_FAVOR_URL,
					handle, dataTagetKey, new ListRequestParams(
							HttpConstants.Response.BargainPrice.LIST_JA,
							page > 1));
			MyBean bean = request.getRequestParam();
			bean.put(HttpConstants.Request.PERPAGE_S, perpage);
			bean.put(HttpConstants.Request.PAGE_S, page);
			bean.put(HttpConstants.Request.UID_S,
					PublicHeaderParamsManager.getUid(handle.getSelfContext()));
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.noneCache);
			bean.put(ControlKey.request.control.__method_s, "GET");
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// Gionee <Yangxiong> <2013-8-15> modify for CR00838312 end
