/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gionee.client.activity.tabFragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GNMessageListActivity;
import com.gionee.client.activity.GNProfileActivity;
import com.gionee.client.activity.GNSettingActivity;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.apprecommend.AppRecommendActivity;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.compareprice.ComparePriceActivity;
import com.gionee.client.activity.contrast.GNGoodsContrastActivity;
import com.gionee.client.activity.feedback.CommonQuestionActivity;
import com.gionee.client.activity.feedback.GNConversationActivity;
import com.gionee.client.activity.history.GnBrowseHistoryActivity;
import com.gionee.client.activity.myfavorites.MyFavoritesActivity;
import com.gionee.client.activity.sina.WeiboAuthActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.action.StatisticAction;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.shareTool.ShareTool;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.upgradeplus.common.UpgradeUtils;
import com.gionee.client.business.urlMatcher.UrlMatcher;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Config;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.widget.PullScrollView;
import com.gionee.client.view.widget.PullScrollView.TopViewListener;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.page.PageCacheManager;
import com.upgrate.manage.UpgrateDownloadManage;

//Gionee <yuwei><2012-5-30> add for CR00821559 begin
@SuppressLint("SetJavaScriptEnabled")
public class MoreFragment extends BaseFragment implements OnClickListener {
	private static final String SETTINGS = "settings";
	public static final String TAG = "MoreFragment";
	// private UpgradeManager mUpgradeManager;
	public TextView mLowPriceTv;
	private boolean mIsRemoveNotice;
	private boolean mHasLowPrice;
	private boolean mIsShowNotice;
	private boolean mIsHasMsgUnClick = true;
	private ImageView mLogoImg;
	private PullScrollView mScrollView;
	// private RelativeLayout mScrollViewHeadTop;
	private TextView mNickNameTv;
	// private TextView mNickNameTopTv;
	private ImageView mUserHeadImg;
	// private ImageView mUserHeadTopImg;
	private boolean mIsHasGift;
	// private TextView mHasGitTv;
	private boolean mIsNewReplayUnclick = true;
	private TextView mNewReplayTv;
	// private ImageView mLogoImgHead;
	private TextView newMsgNotify;
	private TextView mMenuBrowseHistory;
	private ImageView mIvOrderTip;
	private static final String IS_HAS_SHOW_ORDER_TIP = "is_has_show_order_tip";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left_menu, null);
		mNickNameTv = (TextView) view.findViewById(R.id.user_nickname);
		mUserHeadImg = (ImageView) view.findViewById(R.id.user_head_img);
		mMenuBrowseHistory = (TextView) view
				.findViewById(R.id.menu_browse_history);
		mIvOrderTip = (ImageView) view.findViewById(R.id.iv_order_tip);
		mIvOrderTip.setOnClickListener(this);
		mLogoImg = (ImageView) view.findViewById(R.id.logo_img);
		mScrollView = (PullScrollView) view.findViewById(R.id.scroll_view);
		mScrollView.setHeader(mLogoImg);
		LogUtils.log(TAG, AndroidUtils.getNavigationBarHeight(getActivity())
				+ "");
		mLowPriceTv = (TextView) view.findViewById(R.id.low_price_notice);
		// mScrollViewHeadTop = (RelativeLayout)
		// view.findViewById(R.id.scroll_view_head_top);
		mNewReplayTv = (TextView) view.findViewById(R.id.new_replay_notify);
		newMsgNotify = (TextView) view.findViewById(R.id.new_msg_notify);
		mScrollView.setTopViewListener(new TopViewListener() {

			@Override
			public void setViewIsShow(boolean isShow) {
				LogUtils.log(TAG, LogUtils.getFunctionName());
				if (isShow) {
					// mLogoImgHead.setVisibility(View.VISIBLE);
				} else {
					// mLogoImgHead.setVisibility(View.GONE);
				}
			}

			@Override
			public void setFaceSizeDelta(int scrollY) {
				LogUtils.log(TAG, LogUtils.getFunctionName());
				// int delta = scrollY / AndroidUtils.px2px(getActivity(), 10);
				// int initWidth = 80;
				// LayoutParams params = new
				// RelativeLayout.LayoutParams(AndroidUtils.dip2px(getActivity(),
				// initWidth - delta), AndroidUtils.dip2px(getActivity(),
				// initWidth - delta));
				// params.leftMargin = AndroidUtils.dip2px(getActivity(), 15);
				// params.bottomMargin = AndroidUtils.dip2px(getActivity(), 15);
				// params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				// mUserHeadImg.setLayoutParams(params);
			}
		});
		TextView appRecommond = (TextView) view
				.findViewById(R.id.menu_app_recommond);
		// mHasGitTv = (TextView) view.findViewById(R.id.bargain_notice);
		showMenuByChannel(appRecommond);
		setNewVersionSign(view);
		setTopPadding(view);
		showLowPriceNotice();
		showHasGiftNotice();
		showNewReplayNotify();
		setUserInfo(mNickNameTv, mUserHeadImg);
		// setUserInfo(mNickNameTopTv, mUserHeadTopImg);
		// setTopBannerBg();
		requestOrderHistory();
		return view;
	}

	private void requestOrderHistory() {
		// TODO Auto-generated method stub
		RequestAction mRequestAction = new RequestAction();
		mRequestAction.getOerderHistoryList(this,
				HttpConstants.Data.GetOrdersHistory.ORDERHISTORY_JO,
				getActivity(), 1, 12);
	}

	public void showNewReplayNotify() {
		if (isShowNewRelayNotify()) {
			mNewReplayTv.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @param view
	 * @author yuwei
	 * @description TODO
	 */
	protected void setTopPadding(View view) {
		try {
			if (AndroidUtils.translateTopBar(getActivity())) {
				RelativeLayout mtopBar = (RelativeLayout) view
						.findViewById(R.id.scroll_view_head);
				LayoutParams params = (LayoutParams) mtopBar.getLayoutParams();
				params.topMargin = AndroidUtils.dip2px(getActivity(), 15);
				mtopBar.setLayoutParams(params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showNewMsgNotify(View view) {
		if (UserInfoManager.getInstance().isHasNewMsg() && mIsHasMsgUnClick) {
			newMsgNotify.setVisibility(View.VISIBLE);
		} else {
			newMsgNotify.setVisibility(View.GONE);
		}
		if (!UserInfoManager.getInstance().isHasNewFeedbackReplay()) {
			mNewReplayTv.setVisibility(View.GONE);
		}
	}

	public void setUserInfo(TextView mNickNameTv, ImageView userHeadImg) {
		mNickNameTv.setText(UserInfoManager.getInstance().getNickName(
				getActivity()));
		GNImageLoader.getInstance().loadBitmap(
				UserInfoManager.getInstance().getAvatar(getActivity()),
				userHeadImg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gionee.client.activity.base.BaseFragment#reload(boolean)
	 */
	@Override
	public void reload(boolean isReload) {
		super.reload(isReload);
	}

	public void reloadProfile() {
		LogUtils.log(TAG, LogUtils.getFunctionName()
				+ UserInfoManager.getInstance().getNickName(getActivity()));
		mNickNameTv.setText(UserInfoManager.getInstance().getNickName(
				getActivity()));
		// mNickNameTopTv.setText(UserInfoManager.getInstance().getNickName(getActivity()));
		String path = getActivity().getFilesDir().getAbsolutePath() + "/"
				+ Constants.USER_HEAD_LOCAL_DEFUALT_PATH;
		LogUtils.log(TAG, "path:" + path);
		FileInputStream localStream;
		try {
			localStream = getActivity().openFileInput(
					Constants.USER_HEAD_LOCAL_DEFUALT_PATH);
			Bitmap bitmap = BitmapFactory.decodeStream(localStream);
			mUserHeadImg.setImageBitmap(bitmap);
			// mUserHeadTopImg.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// // GNImageLoader.getInstance().loadLocalBitmap(path, mUserHeadImg);
		// GNImageLoader.getInstance().loadLocalBitmap(path, userHeadTopImg);
	}

	private void showLowPriceNotice() {
		MyBean homeData = PageCacheManager.LookupPageData(GnHomeActivity.class
				.getName());
		JSONObject mLowPriceNoticeJo = homeData
				.getJSONObject(HttpConstants.Data.RecommendHome.LOW_PRICE_NOTICE_JO);
		if (mLowPriceNoticeJo == null) {
			return;
		}
		mHasLowPrice = mLowPriceNoticeJo
				.optBoolean(HttpConstants.Response.LowPriceNotice.REDUCE_B);
		String msg = mLowPriceNoticeJo
				.optString(HttpConstants.Response.LowPriceNotice.MSG_S);
		if (mHasLowPrice && !mIsRemoveNotice && !mIsShowNotice) {
			mLowPriceTv.setText(msg);
			mLowPriceTv.setVisibility(View.VISIBLE);
		}
	}

	private void showHasGiftNotice() {
		MyBean homeData = PageCacheManager.LookupPageData(GnHomeActivity.class
				.getName());
		JSONObject mHasGiftNotice = homeData
				.getJSONObject(HttpConstants.Data.RecommendHome.HAS_GIFT_JO);
		if (mHasGiftNotice == null) {
			return;
		}
		mIsHasGift = mHasGiftNotice
				.optBoolean(HttpConstants.Response.ISHasGift.HAS_B);
		String msg = mHasGiftNotice
				.optString(HttpConstants.Response.ISHasGift.MSG_S);
		if (mIsHasGift) {
			// mHasGitTv.setText(msg);
			// mHasGitTv.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onResume() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onResume();
		setNewVersionSign(getView());
		showNewMsgNotify(getView());
		setUserInfo(mNickNameTv, mUserHeadImg);
		// setUserInfo(mNickNameTopTv, mUserHeadTopImg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

	}

	@SuppressLint("NewApi")
	@Override
	public void onSucceed(String businessType, boolean isCache, Object session) {
		super.onSucceed(businessType, isCache, session);
		if (businessType.equals(Url.ORDER_HISTORY)) {
			if (null == getActivity()) {
				return;
			}
			MyBean myBean = PageCacheManager.LookupPageData(getActivity()
					.getClass().getName());
			if (null == myBean) {
				return;
			}
			JSONObject jsonObject = myBean
					.getJSONObject(HttpConstants.Data.GetOrdersHistory.ORDERHISTORY_JO);
			if (jsonObject != null) {
				JSONArray mOrders = jsonObject
						.optJSONArray(HttpConstants.Data.GetOrdersHistory.HISTORY_LIST_JA);
				boolean isHasShowOrderTip = ShareDataManager.getBoolean(
						getActivity(), IS_HAS_SHOW_ORDER_TIP, false);
				if (mOrders != null && mOrders.length() > 0
						&& !isHasShowOrderTip) {
					mMenuBrowseHistory.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							int[] location = new int[2];
							mMenuBrowseHistory.getLocationOnScreen(location);
							int x = location[0];
							int y = location[1];
							Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
							// Define the string.
							// Measure the width of the text string.
							float textWidth = mTextPaint
									.measureText(mMenuBrowseHistory.getText()
											.toString());
							mIvOrderTip.setVisibility(View.VISIBLE);
							int tipHeight = mIvOrderTip.getMeasuredHeight();
							LayoutParams layoutParams = (LayoutParams) mIvOrderTip
									.getLayoutParams();
							layoutParams.topMargin = y - tipHeight
									+ AndroidUtils.px2px(getActivity(), 10);
							layoutParams.leftMargin = (int) textWidth
									+ AndroidUtils.px2px(getActivity(), 100);
							mIvOrderTip.setLayoutParams(layoutParams);
							ShareDataManager.putBoolean(getActivity(),
									IS_HAS_SHOW_ORDER_TIP, true);
						}
					}, 200);
				} else {
					mIvOrderTip.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	/**
	 * @param view
	 * @author yuwei
	 * @description TODO
	 */
	private void setNewVersionSign(View view) {
		try {
			ImageView newVersionSign = (ImageView) view
					.findViewById(R.id.new_version_sign);
			if (UpgrateDownloadManage.sIsHasNewVersion
					|| UrlMatcher.getInstance().isHasNewWeiboNotice(
							ShareDataManager.getString(getActivity(),
									Constants.WEIBO_NOTICE, null))) {
				newVersionSign.setVisibility(View.VISIBLE);
			} else {
				newVersionSign.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param appRecommond
	 * @author yuwei
	 * @description TODO
	 */
	private void showMenuByChannel(TextView appRecommond) {
		if (getString(R.string.channel).equals(getString(R.string.anzhi))) {
			appRecommond.setVisibility(View.GONE);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// initQihuAppRecommond();
		// mUpgradeManager = new UpgradeManager(getActivity(),
		// UpgradeUtils.getProductKey(getActivity()));
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	// private void initQihuAppRecommond() {
	// // 积分墙模式
	// QihooAdAgent.setADWallMode(2);
	// }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void changeFragment(int menuId) {
		LogUtils.log(TAG, LogUtils.getThreadName());

		switch (menuId) {

		case R.id.menu_feedback:
			gotoFeedbackConversaion();
			hideNewReplayNotify();
			StatService.onEvent(getActivity(), SETTINGS, "feedback");
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.CUSTOM_SERVICE);
			break;
		case R.id.menu_check_version:
			checkUpgrade();
			break;
		case R.id.menu_weibo_auth:
			gotoWeiBo();
			break;
		case R.id.menu_like:
			gotoMarket();
			break;
		case R.id.menu_wx_friends:
			shareToWeixin(false);
			break;
		case R.id.menu_app_recommond:
			// QihooAdAgent.loadAd(getActivity());
			gotoActivityWithOutParams(AppRecommendActivity.class);
			StatService.onEvent(getActivity(), SETTINGS, "App_recommond");
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.COMPATITIVE_APP);
			break;
		case R.id.menu_favorite:
			removeLowPrice();
			gotoMyFavorites();
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.MY_FAVORITE);
			break;
		case R.id.menu_service_phone:
			dialServicePhone();
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.CALL_CUSTOM);
			break;
		case R.id.menu_common_question:
			gotoActivityWithOutParams(CommonQuestionActivity.class);
			StatService.onEvent(getActivity(), SETTINGS, "common_question");
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.COMMON_PROBLEM);
			break;
		case R.id.menu_about_shoppingmall:
			gotoWebPage(Url.ABOUT_PAGE_URL, false);
			StatService.onEvent(getActivity(), SETTINGS, "About_GOU");
			break;
		case R.id.menu_logistics_query:
			gotoWebPage(Config.LOGISTICS_QUERY, false);
			StatService.onEvent(getActivity(), SETTINGS, "query");
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.LOGISTICS_QUERY);
			break;
		case R.id.iv_order_tip:
			GnBrowseHistoryActivity.sIsFromOrderTip = true;
			gotoBrowseHistory();
			StatService.onEvent(getActivity(), SETTINGS, "browse_history");
			break;
		case R.id.menu_browse_history:
			gotoBrowseHistory();
			StatService.onEvent(getActivity(), SETTINGS, "browse_history");
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.HISTORY_BROWSE);
			break;
		case R.id.title_right_btn:
			gotoActivityWithOutParams(GNSettingActivity.class);
			StatService.onEvent(getActivity(), SETTINGS, "setting");
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.SETTING);
			break;
		// case R.id.menu_bargain_order:
		// gotoWebPage(Config.MINE_ACTIVITY_DETAIL, false);
		// mHasGitTv.setVisibility(View.GONE);
		// StatService.onEvent(getActivity(), BaiduStatConstants.GAME_RECORD,
		// "setting");
		// break;
		case R.id.menu_compare:
			startActivityWithNoParams(ComparePriceActivity.class);
//			startActivityWithNoParams(GNGoodsContrastActivity.class);
			StatService.onEvent(getActivity(), SETTINGS, "compare");
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.COMPARATIVE_PRICE);
			break;
			
			
		// case R.id.my_question_top:
		// case R.id.my_question:
		// gotoActivityWithOutParams(GNFAQsActivity.class);
		// StatService.onEvent(getActivity(), BaiduStatConstants.M_QA, "");
		// break;
		// case R.id.my_message_top:
		case R.id.my_message:
			mIsHasMsgUnClick = false;
			gotoMessageList();
			StatService.onEvent(getActivity(),
					BaiduStatConstants.M_INFOMATIONS,
					BaiduStatConstants.M_INFOMATIONS);
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.MY_MESSAGE);
			break;
		// case R.id.user_head_img_top:
		case R.id.user_head_img:
		case R.id.user_nickname:
			// case R.id.user_nickname_top:
			gotoModifyProfile();
			StatService.onEvent(getActivity(), BaiduStatConstants.M_INFO,
					BaiduStatConstants.M_INFO);
			((GnHomeActivity) getSelfContext())
					.addFlowStatistics(StatisticsConstants.MinePageConstants.PERSION_INFO);
			break;
		default:
			break;
		}
	}

	public void hideNewReplayNotify() {
		if (isShowNewRelayNotify()) {
			RequestAction action = new RequestAction();
			action.cleanFeedbackNotify(this);
			mIsNewReplayUnclick = false;
			mNewReplayTv.setVisibility(View.GONE);
		}
	}

	public boolean isShowNewRelayNotify() {
		return mIsNewReplayUnclick
				&& UserInfoManager.getInstance().isHasNewFeedbackReplay();
	}

	private void removeLowPrice() {
		StatService.onEvent(getActivity(), "my_favorite",
				String.valueOf(mHasLowPrice));
		if (mHasLowPrice) {
			mIsRemoveNotice = true;
			RequestAction action = new RequestAction();
			action.removeLowPriceNotice(this);
			mLowPriceTv.setVisibility(View.GONE);
		}
	}

	public void removeLowPriceNotice() {
		mLowPriceTv.setVisibility(View.GONE);
		RequestAction action = new RequestAction();
		action.removeLowPriceNotice(this);
		mIsShowNotice = true;
	}

	/**
	 * @author yuwei
	 * @description TODO
	 */
	private void dialServicePhone() {
		DialogFactory.createMsgDialog(this.getActivity(),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						LogUtils.log(TAG, LogUtils.getThreadName());
						Intent intent = new Intent(
								Intent.ACTION_VIEW,
								Uri.parse(WebView.SCHEME_TEL
										+ getString(R.string.service_phone_num)));
						getActivity().startActivity(intent);
						StatService.onEvent(getActivity(), SETTINGS, "CSH");
					}
				}, R.string.dial_to_service).show();
	}

	/**
	 * @author yangxiong
	 * @description TODO
	 */
	private void gotoMessageList() {
		Intent intent = new Intent(getActivity(), GNMessageListActivity.class);
		getActivity().startActivityForResult(intent,
				Constants.ActivityRequestCode.REQUEST_CODE_MY_FAVORITE);
	}

	private void gotoMyFavorites() {
		Intent intent = new Intent(getActivity(), MyFavoritesActivity.class);
		intent.putExtra("has_low_price", mHasLowPrice);
		getActivity().startActivityForResult(intent,
				Constants.ActivityRequestCode.REQUEST_CODE_MY_FAVORITE);
	}

	private void gotoModifyProfile() {
		Intent intent = new Intent(getActivity(), GNProfileActivity.class);
		getActivity().startActivityForResult(intent,
				Constants.ActivityRequestCode.REQUEST_CODE_PROFILE);
	}

	/**
	 * @author yuwei
	 * @description TODO
	 */
	private void gotoActivityWithOutParams(Class<?> activity) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), activity);
		startActivity(intent);
		AndroidUtils.enterActvityAnim(getActivity());
	}

	public void checkUpgrade() {
		// mUpgradeManager.startCheck(false);
		new StatisticAction().sendStatisticData(getActivity(),
				Constants.Statistic.TYPE_UPMODE,
				Constants.Statistic.KEY_UPMODE_MANUAL);
		StatService.onEvent(getActivity(), SETTINGS, "update");
	}

	private void shareToWeixin(boolean b) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		((BaseFragmentActivity) getActivity()).shareToWeixin(false,
				getString(R.string.app_name),
				getString(R.string.share_weixin_description), bitmap,
				getWeiXinShareURL());
		StatService.onEvent(getActivity(), SETTINGS, "share_app_weixin");
		if (ShareTool.isWXInstalled(getActivity())) {
			((BaseFragmentActivity) getActivity()).cumulateAppLinkScore();
		}
	}

	private String getWeiXinShareURL() {
		return Constants.WEIXIN_SHARE_URL
				+ AndroidUtils.getAppVersionName(getActivity()).trim();
	}

	private void gotoMarket() {
		Intent intent = new Intent();
		try {
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(Constants.MARKRT_URL));
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			gotoWebPage(Constants.MARKET_WEB_URL, false);
		}
		StatService.onEvent(getActivity(), SETTINGS, "like_app");
	}

	private void gotoWeiBo() {
		Intent intent = new Intent();
		intent.setClass(getActivity(), WeiboAuthActivity.class);
		startActivity(intent);
		AndroidUtils.enterActvityAnim(getActivity());
		StatService.onEvent(getActivity(), SETTINGS, "follow_app_sina");
	}

	private void gotoBrowseHistory() {
		mIvOrderTip.setVisibility(View.INVISIBLE);
		ShareDataManager.putBoolean(getActivity(), IS_HAS_SHOW_ORDER_TIP, true);
		Intent intent = new Intent();
		intent.setClass(getActivity(), GnBrowseHistoryActivity.class);
		getActivity().startActivityForResult(intent,
				Constants.ActivityRequestCode.REQUEST_CODE_HOME);
		AndroidUtils.enterActvityAnim(getActivity());
	}

	// private void gotoBargainPrice() {
	// Intent intent = new Intent();
	// intent.setClass(getSelfContext(), BargainPriceActivity.class);
	// startActivity(intent);
	// }

	/**
	 * @author yangxiong
	 * @description TODO 启动用户反馈界面
	 */
	private void gotoFeedbackConversaion() {
		Intent intent = new Intent();
		intent.setClass(getActivity(), GNConversationActivity.class);
		startActivity(intent);
		AndroidUtils.enterActvityAnim(getActivity());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	// Gionee <yuwei><2012-6-10> add for CR00821559 end

	@Override
	public void onClick(View v) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		changeFragment(v.getId());
		((GnHomeActivity) getSelfContext()).setIsClickedOnMineFragment(true);
	}

	@Override
	public View getCustomToastParentView() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		return null;
	}

	@Override
	protected int setContentViewId() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		return 0;
	}

}
