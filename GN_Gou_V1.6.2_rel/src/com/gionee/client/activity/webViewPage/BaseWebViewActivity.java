// Gionee <yuwei><2013-7-23> add for CR00821559 begin
/*
 * WebViewActivity.java
 * classes : com.gionee.client.WebViewActivity
 * @author yuwei
 * V 1.0.0
 * Create at 2013-7-23 下午2:48:40
 */
package com.gionee.client.activity.webViewPage;

import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GNCutActivity;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.contrast.GNGoodsContrastActivity;
import com.gionee.client.activity.history.BrowseHistoryInfo;
import com.gionee.client.activity.hotorder.ShowHotOrderActivity;
import com.gionee.client.activity.hotorder.SubmitHotOrderActivity;
import com.gionee.client.activity.samestyle.GNSameStyleActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.dbutils.DBOperationManager;
import com.gionee.client.business.filter.WebFilterChainManager;
import com.gionee.client.business.filter.WeiXinClientHandler;
import com.gionee.client.business.manage.ConfigManager;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.persistent.ShareKeys;
import com.gionee.client.business.shareTool.GNShareDialog;
import com.gionee.client.business.shareTool.ShareModel;
import com.gionee.client.business.shareTool.ShareTool;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.statistic.UrlCollector;
import com.gionee.client.business.statistic.header.PublicHeaderParamsManager;
import com.gionee.client.business.urlMatcher.UrlMatcher;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.UrlUtills;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Config;
import com.gionee.client.model.Constants;
import com.gionee.client.model.Constants.PAGE_TAG;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.widget.CustomToast;
import com.gionee.client.view.widget.GNCustomDialog;
import com.gionee.client.view.widget.GNWebView;
import com.gionee.client.view.widget.MyProgress;
import com.gionee.client.view.widget.MyWebView;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.net.NetUtil;
import com.gionee.framework.operation.utills.JSONArrayHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 * com.gionee.client.WebViewActivity
 * 
 * @author yuwei <br/>
 *         create at 2013-7-23 下午2:48:40
 */
@SuppressLint("NewApi")
public abstract class BaseWebViewActivity extends BaseFragmentActivity
		implements OnClickListener, OnRefreshListener<GNWebView>,
		IWeiboHandler.Response, IInterfaceForHtml {
	private static final String WEB_TOOLS = "web_tools";
	private static final String TAG = "BaseWebViewActivity";

	protected MyWebView mWebView;
	// private TextView mBack;
	protected TextView mTitleTv;
	protected MyProgress mProgress;
	protected String mUrl;
	private RelativeLayout mWebViewFootView;
	protected RelativeLayout mWebViewHead;
	private boolean mIsPullToRefresh = false;
	protected ImageView mShareBtn;
	protected String mShareTitle;
	protected String mShareUrl;
	protected String mDescription = "";
	protected Bitmap mThumbBitmap;
	private RequestAction mRequestAction;
	private boolean mIsCollected = false;
	private boolean mIsCollectSend = false;
	private String mGuideUrl;
	private ImageView mWebViewFinishBtn;
	private WebViewClient mDefaultWebViewClient;
	private WebChromeClient mDefaultWebChromeClient;
	private ProgressBar mCollectedProgress;
	private ImageView mCollectImg;
	private PopupWindow mPopupWindow;
	private ImageView mRefresh;
	private String mUid = "";
	private TextView mCompareSizeText;
	private ImageView mContrastAddImg;
	private TextView mContrastAddText;
	private TextView mContrastCheck;
	private TextView mWebHelp;
	private boolean mIsGetContrastData;
	private String mShareImageUrl;
	private String mBaiduStatTag;
	protected int mPageRedirectStep;
	private boolean mIsLoadingJs;
	private String mJavaScript = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_help_page);
		// AndroidUtils.translateTopBar(this);
		mUid = PublicHeaderParamsManager.getUid(this);
		mJavaScript = "javascript:var oScript= document.createElement(\"script\");"
				+ "oScript.type =\"text/javascript\"; "
				+ "oScript.src=\""
				+ Config.URL
				+ "api/super/js?uid="
				+ mUid
				+ "\"; document.getElementsByTagName(\"BODY\").item(0).appendChild(oScript);";
		initView();
		invalidateUrl();
		intMyWebView();
		initData(savedInstanceState);
		initCollectAction();
		setConfigCallback((WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE));
	}

	@Override
	public void gotoHuodongProductList() {
		// TODO Auto-generated method stub

	}

	private void initData(Bundle savedInstanceState) {
		ShareSDK.initSDK(this);
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this,
				com.gionee.client.business.sina.Constants.APP_KEY);
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
		}
		if (getContrastListSize() > 0) {
			mContrastCheck.setSelected(true);
		}

	}

	private void initCollectAction() {
		mRequestAction = new RequestAction();
	}

	private void postCollect() {
		StatService.onEvent(this, BaiduStatConstants.WEB_TOOLS,
				BaiduStatConstants.WEB_CLOLLECT);
		if (!NetUtil.isNetworkAvailable(this)) {
			LogUtils.log(TAG, "no net");
			showWebNetErrorToast();
			return;
		}
		if (mIsCollectSend) {
			return;
		}
		if (mIsCollected) {
			collectSucceed();
			return;
		}
		if (Constants.UN_NETWORK.equals(mWebView.getRefreshableView().getUrl())) {
			collectFailed();
			return;
		}
		collect();
	}

	public void showWebNetErrorToast() {
		mCustomToast.setToastText(getString(R.string.upgrade_no_net));
		int topMagin = mWebViewHead.getBottom() + AndroidUtils.dip2px(this, 25);
		mCustomToast.showToast(mWebViewHead, topMagin);
	}

	public void showNetErrToast() {
		Toast.makeText(this, getString(R.string.upgrade_no_net),
				Toast.LENGTH_SHORT).show();
	}

	private void collectFailed() {
		Toast.makeText(this, getString(R.string.favorite_err),
				Toast.LENGTH_SHORT).show();
		mCollectImg.setVisibility(View.VISIBLE);
		mCollectedProgress.setVisibility(View.GONE);
	}

	@SuppressWarnings("deprecation")
	public void swithToSelectAndCopyTextMode() {
		try {
			KeyEvent shiftPressEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
			shiftPressEvent.dispatch(mWebView.getRefreshableView());
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * check the url before init it
	 */
	protected void invalidateUrl() {
		Intent i_getvalue = getIntent();
		String action = i_getvalue.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			Uri uri = i_getvalue.getData();
		}

		mUrl = getIntent().getStringExtra(StatisticsConstants.KEY_INTENT_URL);
		LogUtils.log(TAG, mUrl);
		String pageTag = getIntent().getStringExtra(
				StatisticsConstants.PAGE_TAG);

		initTag(pageTag);
		try {
			if (TextUtils.isEmpty(mUrl)) {
				Uri uri = getIntent().getData();
				mUrl = uri.toString().replace("gngou://web/", "");
				if (TextUtils.isEmpty(uri.toString())) {
					mUrl = Constants.UN_NETWORK;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mUrl = Constants.UN_NETWORK;
		}
		// if (!TextUtils.isEmpty(mUrl)) {
		// mUrl = Html.fromHtml(mUrl).toString();
		// }
	}

	private void initTag(String tag) {

		try {
			if (tag.equals(PAGE_TAG.CUT)) {
				mBaiduStatTag = BaiduStatConstants.CUT_DETAIL_SHARE;
			} else if (tag.equals(PAGE_TAG.STORY)) {
				mBaiduStatTag = BaiduStatConstants.SHARE;
			} else {
				mBaiduStatTag = BaiduStatConstants.WEB_TOOLS;
			}
		} catch (Exception e) {
			mBaiduStatTag = BaiduStatConstants.WEB_TOOLS;
		}
	}

	/**
	 * after the setContentView has called,you can call this methed to init the
	 * view of this Activity
	 */
	@SuppressLint("NewApi")
	private void initView() {
		mProgress = (MyProgress) findViewById(R.id.loading_bar);
		mTitleTv = (TextView) findViewById(R.id.webview_title);
		mWebView = (MyWebView) findViewById(R.id.mywebview);
		// mBack = (TextView) findViewById(R.id.webview_back);
		mWebViewFootView = (RelativeLayout) findViewById(R.id.webview_foot);
		mWebViewHead = (RelativeLayout) findViewById(R.id.webview_titlebar);
		mShareBtn = (ImageView) findViewById(R.id.share_btn);
		mWebViewFinishBtn = (ImageView) findViewById(R.id.webview_finish);
		mRefresh = (ImageView) findViewById(R.id.webview_refresh);
		mProgress.setVisibility(View.GONE);
		// mBack.setEnabled(false);
		// mRefresh.setEnabled(false);
		mWebView.getRefreshableView().requestFocusFromTouch();
		mWebView.getRefreshableView().addJavascriptInterface(this, "share");
		mWebView.setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.DISABLED);
		mWebView.getRefreshableView()
				.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
		mWebView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
		mWebView.getWebView().getSettings()
				.setRenderPriority(RenderPriority.HIGH);
		mWebView.getWebView().getSettings().setBlockNetworkImage(true);
		mCollectImg = (ImageView) findViewById(R.id.collect_img);
		mCollectedProgress = (ProgressBar) findViewById(R.id.collect_loading_bar);
		mCompareSizeText = (TextView) findViewById(R.id.contrast_num);
		mContrastAddImg = (ImageView) findViewById(R.id.contrast_add_img);
		mContrastAddText = (TextView) findViewById(R.id.contrast_add);
		mContrastCheck = (TextView) findViewById(R.id.contrast_check);
		mContrastAddImg.setEnabled(false);
		mContrastAddText.setEnabled(false);
		mContrastCheck.setSelected(false);
		AndroidUtils.setMiuiTopMargain(this, mWebViewHead);
	}

	private void initPopupWindow() {
		View contentView = getLayoutInflater().inflate(R.layout.web_more_page,
				null);
		mWebHelp = (TextView) contentView.findViewById(R.id.web_help);

		if (TextUtils.isEmpty(UrlMatcher.getInstance().getHelpUrl(
				mWebView.getRefreshableView().getUrl(),
				BaseWebViewActivity.this))) {
			mWebHelp.setVisibility(View.INVISIBLE);
			mPopupWindow = new PopupWindow(contentView,
					(int) (AndroidUtils.getDensity(this) * 134),
					(int) (AndroidUtils.getDensity(this) * 100));
		} else {
			mWebHelp.setVisibility(View.VISIBLE);
			mPopupWindow = new PopupWindow(contentView,
					(int) (AndroidUtils.getDensity(this) * 134),
					(int) (AndroidUtils.getDensity(this) * 146));
		}
		mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
	}

	/**
	 * @param isVisibleisVisible
	 *            if the foot tool bar is visible TODO by this methed you can
	 *            set the visible of the toobar visible in the bottom of the
	 *            View @author yuwei
	 */
	protected void setFootVisible(boolean isVisible) {
		boolean isShowRefresh = getIntent().getBooleanExtra(
				Constants.Home.IS_SHOW_REFRESH, false);
		if (isVisible) {
			mWebViewFootView.setVisibility(View.VISIBLE);
			mWebViewFinishBtn.setVisibility(View.VISIBLE);
			mRefresh.setVisibility(View.VISIBLE);
		} else if (isShowRefresh) {
			mWebViewFootView.setVisibility(View.GONE);
			mWebViewFinishBtn.setVisibility(View.VISIBLE);
			mRefresh.setVisibility(View.VISIBLE);
		} else {
			mWebViewFootView.setVisibility(View.GONE);
			mWebViewFinishBtn.setVisibility(View.GONE);
			mRefresh.setVisibility(View.GONE);
		}

	}

	/**
	 * @param isVisible
	 *            if the head view is visible
	 * @description by this methed you can set the visible of the head title
	 *              view of the page
	 * @author yuwei
	 */
	protected void setHeadVisible(boolean isVisible) {
		if (isVisible) {
			mWebViewHead.setVisibility(View.VISIBLE);
		} else {
			mWebViewHead.setVisibility(View.GONE);
		}

	}

	/**
	 * @param url
	 *            the url which current page will goto , such as an click event
	 *            will invoke a link
	 * @return true if you want to go to other ativity ,false if you want to
	 *         stay at current activity
	 * @description if to want to go to other activty,you should override this
	 *              methed
	 * @author yuwei
	 */
	public abstract boolean gotoOtherPage(String url);

	/**
	 * @param index
	 * @param count
	 *            //
	 */
	//
	// private void setBackEnable() {
	// if (mWebView.getWebView().canGoBack()) {
	// if (mWebView.getWebView().getUrl().equals(Constants.UN_NETWORK)) {
	// mBack.setEnabled(false);
	// } else {
	// mBack.setEnabled(true);
	// }
	//
	// } else {
	// mBack.setEnabled(false);
	// }
	// }

	private void setContrastAddEnable() {
		try {
			boolean flag = UrlMatcher.getInstance().isCanAddCompare(
					mWebView.getRefreshableView().getUrl(),
					BaseWebViewActivity.this);
			mContrastAddImg.setEnabled(flag);
			mContrastAddText.setEnabled(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setContrastState() {
		if (getContrastListSize() > 0) {
			mContrastCheck.setSelected(true);
			return;
		}
		mContrastCheck.setSelected(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.webview_finish:
			if (!hideInputMethod()) {
				finish();
				onBackClicked();
				gotoActivityWithOutParams(GnHomeActivity.class);
				AndroidUtils.webActivityExitAnim(BaseWebViewActivity.this);
			}
			StatService.onEvent(this, BaiduStatConstants.WEB_CLOSE,
					BaiduStatConstants.WEB_CLOSE_BTN);
			break;
		case R.id.webview_back_top:
			StatService.onEvent(this, BaiduStatConstants.BACK,
					BaiduStatConstants.TOP);
			goBack();
			break;
		case R.id.webview_back:
			goBack();
			StatService.onEvent(this, BaiduStatConstants.BACK,
					BaiduStatConstants.FOOT);
			break;
		case R.id.webview_refresh:
			refresh();
			StatService.onEvent(this, WEB_TOOLS, BaiduStatConstants.REFRESH);
			break;
		case R.id.webview_collect:
		case R.id.collect_img:
			try {
				postCollect();
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case R.id.web_more:
			showMoreMenu(v);
			break;
		case R.id.contrast_add:
		case R.id.contrast_add_img:
			try {
				loadContrastJs();
			} catch (Exception e) {
				// TODO: handle exception
				// mWebView.getWebView().loadUrl("about:blank");
			}
			break;
		case R.id.contrast_check:
			gotoGoodsContrastPage();
			break;
		case R.id.web_share:
			showWebShareDialog();
			break;
		case R.id.share_weixin:
			shareToWeixin(false);
			closeShareDialog();
			StatService.onEvent(this, mBaiduStatTag, BaiduStatConstants.WEIXIN);
			if (ShareTool.isWXInstalled(this)) {
				cumulateAppLinkScore();
			}
			break;
		case R.id.share_friends:
			shareToWeixin(true);
			closeShareDialog();
			StatService
					.onEvent(this, mBaiduStatTag, BaiduStatConstants.FRIENDS);
			if (ShareTool.isWXInstalled(this)) {
				cumulateAppLinkScore();
			}
			break;
		case R.id.share_weibo:
			shareToWeibo();
			closeShareDialog();
			StatService.onEvent(this, mBaiduStatTag, BaiduStatConstants.WEIBO);
			if (isWeiboValid()) {
				cumulateAppLinkScore();
			}
			break;
		case R.id.share_qq_friend:
			share(ShareTool.PLATFORM_QQ_FRIEND);
			StatService.onEvent(this, mBaiduStatTag, BaiduStatConstants.QQ);
			closeShareDialog();
			if (ShareTool.isQQValid(this)) {
				cumulateAppLinkScore();
			}
			break;
		case R.id.share_qq_zone:
			share(ShareTool.PLATFORM_QQ_ZONE);
			StatService.onEvent(this, mBaiduStatTag, BaiduStatConstants.ZONE);
			closeShareDialog();
			if (ShareTool.isQQValid(this)) {
				cumulateAppLinkScore();
			}
			break;
		case R.id.web_copy:
			try {
				AndroidUtils
						.copyUriToClipboard(Uri.parse(mWebView
								.getRefreshableView().getUrl()), this);
				Toast.makeText(this, R.string.copy_to_clipboard,
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			hidePopupWindow();
			StatService.onEvent(this, BaiduStatConstants.WEB_TOOLS,
					BaiduStatConstants.WEB_COPY);
			break;
		case R.id.web_help:
			try {
				gotoGuidePage();
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		default:
			break;

		}
	}

	private void gotoGoodsContrastPage() {
		mCompareSizeText.setVisibility(View.GONE);
		Intent intent = new Intent(this, GNGoodsContrastActivity.class);
		startActivity(intent);
		StatService.onEvent(this, BaiduStatConstants.WEB_TOOLS,
				BaiduStatConstants.WEB_CHEKCK_ADD);
	}

	// h5调用，用于分享网页
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gionee.client.activity.webViewPage.IInterfaceForHtml#showWebShareDialog
	 * ()
	 */
	@Override
	public void showWebShareDialog() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				try {
					if (mDialog == null) {
						mDialog = (GNCustomDialog) DialogFactory
								.createShareDialog(BaseWebViewActivity.this);
					}
					if (mDialog != null) {
						mDialog.setTitle(R.string.share);
						mDialog.show();
						mDialog.setDismissBtnVisible();
						mDialog.setCanceledOnTouchOutside(true);
						mDialog.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
							}
						});
						mDialog.getContentView()
								.findViewById(R.id.share_weixin)
								.setOnClickListener(BaseWebViewActivity.this);
						mDialog.getContentView()
								.findViewById(R.id.share_friends)
								.setOnClickListener(BaseWebViewActivity.this);
						mDialog.getContentView().findViewById(R.id.share_weibo)
								.setOnClickListener(BaseWebViewActivity.this);
						mDialog.getContentView()
								.findViewById(R.id.share_qq_friend)
								.setVisibility(View.GONE);
						mDialog.getContentView()
								.findViewById(R.id.share_qq_zone)
								.setVisibility(View.GONE);
					}
					hidePopupWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
				StatService.onEvent(BaseWebViewActivity.this,
						BaiduStatConstants.WEB_TOOLS, BaiduStatConstants.SHARE);
			}
		});
	}

	private void loadContrastJs() {
		StatService.onEvent(this, BaiduStatConstants.WEB_TOOLS,
				BaiduStatConstants.WEB_ADD_COMPARE);
		mWebView.getRefreshableView().loadUrl(
				"javascript:window.SameStyle.addShopToList()");
		mContrastAddImg.clearAnimation();
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.contrast_add_img);
		mContrastAddImg.setAnimation(animation);
		mContrastAddText.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!mIsGetContrastData) {
					mIsGetContrastData = true;
					showNoContrastDataToast();
				}
			}
		}, 500);

	}

	private void showMoreMenu(View v) {
		try {
			if (hidePopupWindow()) {
				return;
			}
			initPopupWindow();
			int[] moreLocation = new int[2];
			int[] footLocation = new int[2];
			v.getLocationOnScreen(moreLocation);
			mWebViewFootView.getLocationOnScreen(footLocation);
			mPopupWindow
					.showAtLocation(
							v,
							Gravity.NO_GRAVITY,
							moreLocation[0],
							(moreLocation[1] - mPopupWindow.getHeight() - (int) (6 * AndroidUtils
									.getDensity(this))));
			StatService.onEvent(this, BaiduStatConstants.WEB_TOOLS,
					BaiduStatConstants.WEB_MENU);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean hidePopupWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			return true;
		}
		return false;
	}

	private void collect() {
		mIsCollectSend = true;
		mRequestAction
				.urlFavorite(this, mWebView.getRefreshableView().getUrl());
		mCollectedProgress.setVisibility(View.VISIBLE);
		mCollectImg.setVisibility(View.GONE);
	}

	private void gotoGuidePage() {
		mGuideUrl = UrlMatcher.getInstance().getHelpUrl(
				mWebView.getRefreshableView().getUrl(),
				BaseWebViewActivity.this);
		if (TextUtils.isEmpty(mGuideUrl)) {
			return;
		}
		Intent intent = new Intent();
		intent.setClass(this, GNGuideDetilActivity.class);
		intent.putExtra(StatisticsConstants.KEY_INTENT_URL, mGuideUrl);
		startActivity(intent);
		AndroidUtils.logoFadeAnim(this);
		hidePopupWindow();
		StatService.onEvent(this, BaiduStatConstants.GUIDE,
				BaiduStatConstants.GUIDE);
	}

	protected abstract boolean onBackClicked();

	private boolean hideInputMethod() {

		try {
			View v = getWindow().peekDecorView();
			if (v != null && v.getWindowToken() != null) {
				InputMethodManager imm = (InputMethodManager) this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	void showErrorPage() {
		if (mWebView != null) {
			mWebView.setVisibility(View.GONE);
		}
	}

	private void refresh() {
		if (isNetworkUnavalible()) {
			showWebNetErrorToast();
			return;
		}
		mProgress.setProgress(0);
		try {
			if (mWebView.getWebView().getUrl().equals(Constants.UN_NETWORK)) {
				mWebView.getWebView().loadUrl(mUrl);
			} else {
				mWebView.getWebView().reload();
			}
		} catch (Exception e) {
			// mWebView.getWebView().loadUrl("about:blank");
		} finally {
			if (mWebView != null) {
				checkNetTimeOut();
			}
		}
	}

	/**
	 * 
	 * @description
	 * @author yuwei
	 */
	private void checkNetTimeOut() {
		mWebView.postDelayed(new Runnable() {

			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				if (mWebView.isRefreshing()) {
					mWebView.onRefreshComplete();
					// showErrorToast(R.string.refresh_timeout);
				}
			}
		}, 10000);
	}

	/**
	 * 
	 * @description
	 * @author yuwei
	 */
	private boolean isNetworkUnavalible() {
		if (AndroidUtils.getNetworkType(BaseWebViewActivity.this) == Constants.NET_UNABLE) {
			mWebView.onRefreshComplete();
			showNetErrorToast();
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if (hidePopupWindow()) {
			return;
		}
		goBack();
	}

	public void goBack() {
		try {
			LogUtils.log(TAG, "mStep=" + mPageRedirectStep);
			boolean isBlackListUrl = isTabaoClick() || isM6GouUrl();

			if (netWorkUnavalible()
					|| !mWebView.getWebView().canGoBack()
					|| isBlackListUrl
					|| isUnNetworkPage()
					|| UrlMatcher.getInstance().isMatchPageSkipUrl(this,
							mWebView.getRefreshableView().getUrl())) {
				exitActivity();
			} else if (mWebView.getRefreshableView().getUrl()
					.contains(Config.mPriseRecordUrl)
					&& getLastUrl().contains(Config.mGetPriseUrl)) {
				LogUtils.log(TAG, "====lastUrl=" + getLastUrl() + "currentUrl="
						+ mWebView.getRefreshableView().getUrl());
				mWebView.getWebView().goBackOrForward(-3);
			} else {
				LogUtils.log(TAG, "lastUrl=" + getLastUrl() + "currentUrl="
						+ mWebView.getRefreshableView().getUrl());
				mWebView.getWebView().goBack();
			}
			// exitActivity();
			mShareImageUrl = Constants.DEFAULT_SHARE_ICON_URL;
		} catch (Exception e) {
			e.printStackTrace();
			exitActivity();
		}
	}

	/**
	 * @return
	 * @author yuwei
	 * @description TODO
	 */
	private boolean netWorkUnavalible() {
		return isUnNetworkPage()
				&& AndroidUtils.getNetworkType(BaseWebViewActivity.this) == Constants.NET_UNABLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gionee.client.activity.webViewPage.IInterfaceForHtml#exitWebView()
	 */
	@Override
	public void exitWebView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				setResult(Constants.Home.MORE_PAGE_RESULT);
				finish();
				AndroidUtils.webActivityExitAnim(BaseWebViewActivity.this);
				onBackClicked();
			}
		});
	}

	public void exitActivity() {
		finish();
		AndroidUtils.webActivityExitAnim(BaseWebViewActivity.this);
		onBackClicked();
	}

	private boolean isM6GouUrl() {
		try {
			if (mWebView.getRefreshableView().getUrl()
					.equals("http://m.m6go.com/")) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.FragmentActivity#onConfigurationChanged(android
	 * .content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean isTabaoClick() {
		String url = getLastUrl();
		LogUtils.logd(TAG, LogUtils.getThreadName() + "last url = " + url);
		return UrlUtills.invalidateTaobaoClick(url);
	}

	protected boolean isUnNetworkPage() {
		try {
			if (mWebView.getWebView().getUrl().equals(Constants.UN_NETWORK)) {
				return true;
			}
			String url = getLastUrl();
			if (TextUtils.isEmpty(url) || url.equals(Constants.UN_NETWORK)) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @return
	 */
	private String getLastUrl() {
		String url = "";
		try {
			int index = mWebView.getWebView().copyBackForwardList()
					.getCurrentIndex();
			if (mWebView.getWebView().canGoBack()) {
				WebHistoryItem item = mWebView.getWebView()
						.copyBackForwardList().getItemAtIndex(index - 1);
				url = item.getUrl();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	@Override
	protected void onResume() {
		super.onResume();
		setContrastState();
		closeProgressDialog();
		try {
			mWebView.getRefreshableView().requestFocus();
			mWebView.getRefreshableView().onResume();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			mWebView.getRefreshableView().onPause();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			setConfigCallback(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		clearWebViewMemory();
		UrlCollector.getInstance().endCollect();
		ShareSDK.stopSDK(this);
	}

	public void clearWebViewMemory() {
		try {
			if (mWebView.getWebView() != null) {
				mWebView.getWebView().loadUrl("about:blank");
				mWebView.getWebView().clearCache(false);
				mWebView.getWebView().removeAllViews();
				mWebView.getWebView().destroy();
				mWebView.removeAllViews();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		// if()
		// WebViewListManager.getInstance().removeFirst();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			LogUtils.log(TAG, LogUtils.getThreadName() + "help page load url="
					+ url);
			// Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			// startActivity(intent);
			onStartDownload(url);

		}

	}

	public void setConfigCallback(WindowManager windowManager) {
		Field field;
		try {
			field = WebView.class.getDeclaredField("mWebViewCore");
			field = field.getType().getDeclaredField("mBrowserFrame");
			field = field.getType().getDeclaredField("sConfigCallback");
			field.setAccessible(true);
			Object configCallback = field.get(null);
			if (null == configCallback) {
				return;
			}
			field = field.getType().getDeclaredField("mWindowManager");
			field.setAccessible(true);
			field.set(configCallback, windowManager);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected abstract void onStartDownload(String url);

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	private void intMyWebView() {
		mWebView.init(false);
		mWebView.getWebView().getSettings()
				.setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.setOnRefreshListener(this);
		try {
			mWebView.getWebView().loadUrl(mUrl);
			String channelId = UrlMatcher.getInstance().getOrderChannelId(
					BaseWebViewActivity.this, mUrl);
			if (!"".equals(channelId)) {
				mRequestAction.addOrder(BaseWebViewActivity.this,
						HttpConstants.Data.AddOrder.ADD_ORDER_JO, channelId);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		LogUtils.log(TAG, mUrl);
		mWebView.getWebView().setDownloadListener(
				new MyWebViewDownLoadListener());
		initWebViewClient();
		mWebView.getWebView().setWebViewClient(mDefaultWebViewClient);
		mWebView.getWebView().setWebChromeClient(mDefaultWebChromeClient);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void initWebViewClient() {
		mDefaultWebViewClient = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtils.log(TAG, "help page load url=" + url + "code ="
						+ AndroidUtils.getRespStatus(url));
				super.shouldOverrideUrlLoading(view, url);
				try {
					mIsLoadingJs = false;
					mPageRedirectStep--;
					if (url.contains(Constants.WEIXIN_PAY)) {
						WeiXinClientHandler.gotoWeiXin(
								BaseWebViewActivity.this, url);
						return true;
					}
					if (url.contains(Constants.CMCC_IDE_NOTIFICATION1)
							&& url.contains(Constants.CMCC_IDE_NOTIFICATION2)) {
						mWebView.getWebView().loadUrl(Constants.UN_NETWORK);
						return true;
					}
					LogUtils.log(TAG, "help page load url2=" + url);
					if (url.equals(Constants.UN_NETWORK)) {
						mWebView.getWebView().loadUrl(Constants.UN_NETWORK);
						return true;
					}
					LogUtils.log(TAG, "help page load url3=" + url);
					String channelId = UrlMatcher.getInstance()
							.getOrderChannelId(BaseWebViewActivity.this, url);
					if (!"".equals(channelId)) {
						mRequestAction.addOrder(BaseWebViewActivity.this,
								HttpConstants.Data.AddOrder.ADD_ORDER_JO,
								channelId);
					}
					if (gotoOtherPage(url)) {
						return true;
					}
					LogUtils.log(TAG, "help page load url4=" + url);
					setFootVisible(!UrlMatcher.getInstance().isMatchOptBarUrl(
							BaseWebViewActivity.this, url));
				} catch (Exception e) {
					// mWebView.getWebView().loadUrl("about:blank");
					// TODO: handle exception
				}
				return WebFilterChainManager.getInstance(
						BaseWebViewActivity.this).startFilter(
						BaseWebViewActivity.this, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (mProgress != null && !mWebView.isRefreshing()) {
					mProgress.setVisibility(View.VISIBLE);
				}

				mIsCollected = false;
				mIsCollectSend = false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				try {
					mWebView.getWebView().getSettings()
							.setBlockNetworkImage(false);
					if (mWebViewFootView.isShown()) {
						LogUtils.log(TAG, "foward is enabled");
						// setBackEnable();
						setContrastAddEnable();
					}
					if (NetUtil.isNetworkAvailable(BaseWebViewActivity.this)) {
						UrlCollector.getInstance().collect(url);
					}
					String mIsExcuteJs = ConfigManager.getInstance()
							.getIsExcuteJs(BaseWebViewActivity.this);
					if (!TextUtils.isEmpty(mIsExcuteJs)
							&& mIsExcuteJs.endsWith("1")) {
						LogUtils.log("mWebViewJs", LogUtils.getThreadName()
								+ mJavaScript);
						mWebView.getRefreshableView().loadUrl(mJavaScript);
					}
				} catch (Exception e) {
					// TODO: handle exception
					// mWebView.getWebView().loadUrl("about:blank");
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				LogUtils.log("WebViewRecievedError", errorCode + description);
				try {
					mWebView.getWebView().loadUrl(Constants.UN_NETWORK);
					showErrorToast(R.string.refresh_error);
					mProgress.setVisibility(View.GONE);
					mProgress.setProgress(0);
				} catch (Exception e) {
					// TODO: handle exception

				}
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
				mProgress.setVisibility(View.GONE);
				mProgress.setProgress(0);
			}

		};
		mDefaultWebChromeClient = new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				try {
					mTitleTv.setText(Html.fromHtml(title));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, JsPromptResult result) {
				LogUtils.log(TAG, LogUtils.getThreadName());
				return super.onJsPrompt(view, url, message, defaultValue,
						result);
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, JsResult result) {
				LogUtils.log(TAG, LogUtils.getThreadName() + url);
				return super.onJsConfirm(view, url, message, result);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				LogUtils.log(TAG, LogUtils.getThreadName() + url);
				return super.onJsAlert(view, url, message, result);
			}

			@Override
			public boolean onCreateWindow(WebView view, boolean isDialog,
					boolean isUserGesture, Message resultMsg) {
				LogUtils.log(TAG, LogUtils.getThreadName() + resultMsg);
				return super.onCreateWindow(view, isDialog, isUserGesture,
						resultMsg);
			}

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				LogUtils.log(TAG, LogUtils.getThreadName());
				super.onShowCustomView(view, callback);
			}

			@Override
			public void onProgressChanged(WebView view, int progress) {
				try {
					mProgress.setProgress(progress);
					if (progress > 50 && !mIsLoadingJs) {
						// LogUtils.log(TAG, LogUtils.getThreadName() +
						// "progress=" + progress);
						mWebView.getRefreshableView().loadUrl(Config.mLocalJs);
						mIsLoadingJs = true;
					}
					if (progress == 100) {
						view.requestFocus();
						notifyUpdateComplete();
						hidenProgress();
					}
				} catch (Exception e) {
					// TODO: handle exception
					// mWebView.getWebView().loadUrl("about:blank");
				}
			}

			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
					Callback callback) {
				// TODO Auto-generated method stub
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

		};
	}

	/**
	 * @param view
	 * @author yuwei
	 * @description TODO
	 */
	public void insertUrlHistory(String url, String title, String type,
			String platform) {
		if (TextUtils.isEmpty(title)) {
			title = getString(R.string.unknow);
		}
		try {
			BrowseHistoryInfo info = new BrowseHistoryInfo();
			info.setUrl(url);

			info.setTitle(title);
			info.setmType(type);
			info.setmPlatform(platform);
			DBOperationManager.getInstance(getApplicationContext())
					.saveBrowseHistory(getApplicationContext(), info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	protected void hidenProgress() {
		mProgress.postDelayed(new Runnable() {

			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				mProgress.setVisibility(View.GONE);
			}
		}, 500);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void notifyUpdateComplete() {
		String label = AndroidUtils.getCurrentTimeStr(BaseWebViewActivity.this);
		mWebView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		mWebView.onRefreshComplete();
	}

	/**
	 * 
	 * @description
	 * @author yuwei
	 */
	private void showErrorToast(int textId) {
		if (!mIsPullToRefresh) {
			return;
		}
		CustomToast toast = new CustomToast(BaseWebViewActivity.this);
		toast.setToastText(textId);
		int topMagin = mWebViewHead.getBottom()
				+ AndroidUtils.dip2px(BaseWebViewActivity.this, 25);
		toast.showToast(mWebView.getWebView(), topMagin);
	}

	@Override
	public void onRefresh(PullToRefreshBase<GNWebView> refreshView) {
		mIsPullToRefresh = true;
		refresh();
		StatService.onEvent(BaseWebViewActivity.this, "refresh", "refresh");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gionee.client.activity.webViewPage.IInterfaceForHtml#getmDescription
	 * ()
	 */
	public String getmDescription() {
		return mDescription;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gionee.client.activity.webViewPage.IInterfaceForHtml#getVersionName()
	 */
	@Override
	public String getVersionName() {
		return AndroidUtils.getAppVersionName(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gionee.client.activity.webViewPage.IInterfaceForHtml#setmDescription
	 * (java.lang.String)
	 */
	@Override
	public void setmDescription(final String description) {
		LogUtils.log(TAG, LogUtils.getThreadName() + " description = "
				+ description);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				mDescription = description.trim();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gionee.client.activity.webViewPage.IInterfaceForHtml#setShareTitle
	 * (java.lang.String)
	 */
	@Override
	public void setShareTitle(final String mTitle) {
		LogUtils.log(TAG, LogUtils.getThreadName() + " title = " + mTitle);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				mShareTitle = mTitle;
			}
		});
	}

	@Override
	public void setShareUrl(final String url) {
		LogUtils.log(TAG, LogUtils.getThreadName() + " url = " + url);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mShareUrl = url;
			}
		});
	}

	public void setContrastData(String data) {
		LogUtils.log(TAG, "setContrastData::" + data);
		mIsGetContrastData = true;
		if (TextUtils.isEmpty(data)) {
			showNoContrastDataToast();
			return;
		}
		saveConrastData(data);
	}

	private void showNoContrastDataToast() {
		Toast.makeText(this, R.string.no_contrast_data, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void showSameStyleList(String data) {
		try {
			LogUtils.log(TAG, LogUtils.getThreadName() + " same style data: "
					+ data);
			JSONObject json = new JSONObject(data);
			String imageUrl = json
					.optString(HttpConstants.Data.SameStyleInfo.IMAGE);
			String title = json
					.optString(HttpConstants.Data.SameStyleInfo.TITLE);
			String price = json
					.optString(HttpConstants.Data.SameStyleInfo.PRICE);
			String salesVolume = json
					.optString(HttpConstants.Data.SameStyleInfo.SALES_VOLUME);
			String score = json
					.optString(HttpConstants.Data.SameStyleInfo.SCORE);
			String expressMethod = json
					.optString(HttpConstants.Data.SameStyleInfo.EXPRESS_METHOD);
			String url = json.optString(HttpConstants.Data.SameStyleInfo.URL);
			String goodsId = json
					.optString(HttpConstants.Request.SameStyleList.ID_I);
			String unipid = json
					.optString(HttpConstants.Request.SameStyleList.PID_I);
			Intent intent = new Intent(this, GNSameStyleActivity.class);
			intent.putExtra(HttpConstants.Data.SameStyleInfo.IMAGE, imageUrl);
			intent.putExtra(HttpConstants.Data.SameStyleInfo.TITLE, title);
			intent.putExtra(HttpConstants.Data.SameStyleInfo.PRICE, price);
			intent.putExtra(HttpConstants.Data.SameStyleInfo.SALES_VOLUME,
					salesVolume);
			intent.putExtra(HttpConstants.Data.SameStyleInfo.SCORE, score);
			intent.putExtra(HttpConstants.Data.SameStyleInfo.EXPRESS_METHOD,
					expressMethod);
			intent.putExtra(HttpConstants.Data.SameStyleInfo.URL, url);
			intent.putExtra(HttpConstants.Request.SameStyleList.ID_I, goodsId);
			intent.putExtra(HttpConstants.Request.SameStyleList.PID_I, unipid);
			startActivity(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param data
	 */
	private void saveConrastData(String data) {
		try {
			JSONArray js = ShareDataManager
					.getJSONArray(BaseWebViewActivity.this,
							ShareKeys.KEY_GOODS_CONTRAST_DATA);

			JSONObject object = new JSONObject(data);
			JSONArrayHelper helper = new JSONArrayHelper(js);
			for (int i = 0; i < js.length(); i++) {
				if (object.optString(Constants.GoodsContrast.ID).equals(
						js.getJSONObject(i).optString(
								Constants.GoodsContrast.ID))) {
					helper.remove(i);
				}
			}
			if (js.length() >= 20) {
				showComparedFullDialog();
				return;
			}
			helper.add(0, object);
			ShareDataManager.putJsonArray(this,
					ShareKeys.KEY_GOODS_CONTRAST_DATA, js);
			startContrastNumAnimation();
			mContrastCheck.setSelected(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startContrastNumAnimation() {
		LogUtils.log(TAG, LogUtils.getFunctionName() + getContrastListSize());
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.compare_num);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mCompareSizeText.setVisibility(View.VISIBLE);
				mCompareSizeText.setText(String.valueOf(getContrastListSize()));
				mCompareSizeText.removeCallbacks(mCompareNumRunnable);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mCompareSizeText.postDelayed(mCompareNumRunnable, 5000);
			}
		});
		mCompareSizeText.setAnimation(animation);
	}

	private Runnable mCompareNumRunnable = new Runnable() {

		@Override
		public void run() {
			mCompareSizeText.setVisibility(View.GONE);
		}
	};

	private int getContrastListSize() {
		try {
			return ShareDataManager.getJSONArray(BaseWebViewActivity.this,
					ShareKeys.KEY_GOODS_CONTRAST_DATA).length();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void showComparedFullDialog() {
		GNCustomDialog dialog = (GNCustomDialog) DialogFactory
				.compareFullDialog(this, new OnClickListener() {

					@Override
					public void onClick(View v) {
						gotoGoodsContrastPage();
					}
				});
		dialog.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gionee.client.activity.webViewPage.IInterfaceForHtml#getmThumbBitmap
	 * ()
	 */
	public Bitmap getmThumbBitmap() {
		try {
			if (mThumbBitmap == null || mThumbBitmap.isRecycled()) {
				mThumbBitmap = AndroidUtils.takeScreenShot(this);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mThumbBitmap;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onCreateContextMenu(menu, v, menuInfo);
		swithToSelectAndCopyTextMode();
	}

	// h5调用接口
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gionee.client.activity.webViewPage.IInterfaceForHtml#setmThumbBitmap
	 * (java.lang.String)
	 */
	@Override
	public void setmThumbBitmap(final String fileName) {
		LogUtils.log(TAG, LogUtils.getThreadName() + " thumb path: " + fileName);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				mShareImageUrl = fileName;
				GNImageLoader.getInstance().init(BaseWebViewActivity.this);
				GNImageLoader
						.getInstance()
						.getImageLoader()
						.loadImage(
								fileName,
								GNImageLoader.getInstance().getDefaultOptions(),
								new ImageLoadingListener() {
									@Override
									public void onLoadingStarted(String arg0,
											View arg1) {
										LogUtils.log(TAG,
												LogUtils.getThreadName());
									}

									@Override
									public void onLoadingFailed(String arg0,
											View arg1, FailReason arg2) {
										LogUtils.log(TAG,
												LogUtils.getThreadName());
									}

									@Override
									public void onLoadingComplete(String arg0,
											View arg1, Bitmap arg2) {
										LogUtils.log(TAG,
												LogUtils.getThreadName());
										mThumbBitmap = arg2;
									}

									@Override
									public void onLoadingCancelled(String arg0,
											View arg1) {
										LogUtils.log(TAG,
												LogUtils.getThreadName());
									}
								});
			}
		});
	}

	// h5调用接口
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gionee.client.activity.webViewPage.IInterfaceForHtml#shareToWeixin
	 * (boolean)
	 */
	@Override
	public void shareToWeixin(final boolean isShareToFriends) {
		LogUtils.log(TAG, "URL=" + mWebView.getRefreshableView().getUrl());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				getmThumbBitmap();
				String title = mShareTitle;
				if (TextUtils.isEmpty(mShareTitle)) {
					title = mWebView.getRefreshableView().getTitle();
					if (TextUtils.isEmpty(title)) {
						title = getString(R.string.no_title);
					}
				}

				String shareUrl = mShareUrl;
				if (TextUtils.isEmpty(shareUrl)) {
					shareUrl = mWebView.getRefreshableView().getUrl();
				}
				shareToWeixin(isShareToFriends, title, getmDescription(),
						mThumbBitmap, false, shareUrl);
			}
		});
	}

	// h5调用接口
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gionee.client.activity.webViewPage.IInterfaceForHtml#shareToWeibo()
	 */
	@Override
	public void shareToWeibo() {
		LogUtils.log(TAG, LogUtils.getFunctionName());
		String title = mShareTitle;
		if (TextUtils.isEmpty(mShareTitle)) {
			title = mWebView.getRefreshableView().getTitle();
			if (TextUtils.isEmpty(title)) {
				title = getString(R.string.no_title);
			}
		}

		String shareUrl = mShareUrl;
		if (TextUtils.isEmpty(shareUrl)) {
			shareUrl = mWebView.getRefreshableView().getUrl();
		}
		shareToWeibo(title, getmDescription(), getmThumbBitmap(), shareUrl);
	}

	// h5调用接口 分享到"QQ好友":2, "QQ空间":3
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gionee.client.activity.webViewPage.IInterfaceForHtml#share(int)
	 */
	@Override
	public void share(final int platform) {
		LogUtils.log(TAG, LogUtils.getFunctionName() + " platform = "
				+ platform);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				initShareTool();
				ShareModel model = new ShareModel();
				if (TextUtils.isEmpty(mShareImageUrl)) {
					mShareImageUrl = Constants.DEFAULT_SHARE_ICON_URL;
				}
				model.setImageUrl(mShareImageUrl);
				String title = mShareTitle;
				if (TextUtils.isEmpty(title)) {
					title = mWebView.getRefreshableView().getTitle();
					if (TextUtils.isEmpty(title)) {
						title = getString(R.string.no_title);
					}
				}
				model.setTitle(title);
				String description = getmDescription();
				if (TextUtils.isEmpty(description)) {
					description = getString(R.string.app_name);
				}
				model.setText(description);
				String shareUrl = mShareUrl;
				if (TextUtils.isEmpty(shareUrl)) {
					shareUrl = mWebView.getRefreshableView().getUrl();
				}
				model.setUrl(shareUrl == null ? "" : shareUrl);
				mShareTool.initShareParams(model);
				mShareTool.share(platform);
			}
		});
	}

	// h5调用接口, 分享购物大厅app本身
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gionee.client.activity.webViewPage.IInterfaceForHtml#shareApp()
	 */
	@Override
	public void shareApp() {
		LogUtils.log(TAG, LogUtils.getFunctionName());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				if (mShareDialog == null) {
					mShareDialog = new GNShareDialog(BaseWebViewActivity.this);
				}
				String title = getString(R.string.app_name);
				String text = getString(R.string.share_weixin_description);
				String url = getAppWeiXinShareURL();
				ShareModel shareModel = new ShareModel(title, text, url,
						Constants.DEFAULT_SHARE_ICON_URL);
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_launcher);
				shareModel.setBitmap(bitmap);
				mShareDialog.setmShareModel(shareModel);
				mShareDialog.setmTypeId(Constants.ScoreTypeId.SHARE_APP);
				mShareDialog.showShareDialog();
			}
		});
	}

	// h5调用接口
	@Override
	public void gotoCutPriceInterface() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				Intent intent = new Intent();
				intent.setClass(BaseWebViewActivity.this, GNCutActivity.class);
				startActivityForResult(
						intent,
						Constants.ActivityRequestCode.REQUEST_CODE_FROM_H5_EVERYDAT_TASK);
				AndroidUtils.enterActvityAnim(BaseWebViewActivity.this);
			}
		});
	}

	public void reload() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				mProgress.setVisibility(View.VISIBLE);
				mWebView.getRefreshableView().loadUrl(mUrl);
				mProgress.postDelayed(new Runnable() {

					@Override
					public void run() {
						mProgress.setVisibility(View.GONE);

					}
				}, 15000);
			}
		});

	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, R.string.share_success, Toast.LENGTH_SHORT)
					.show();
			StatService.onEvent(this, BaiduStatConstants.SHARE_SUCCESS,
					BaiduStatConstants.WEIBO);
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.log(TAG, LogUtils.getThreadName() + " requestCode = "
				+ requestCode);
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.ActivityRequestCode.REQUEST_CODE_FROM_H5_EVERYDAT_TASK:
		case Constants.ActivityRequestCode.REQUEST_CODE_FROM_H5_TO_HOT_ORDER:
			refresh();
			break;
		default:
			break;
		}
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogUtils.log(TAG, LogUtils.getFunctionName());
		invalidateUrl();
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	@Override
	public void onSucceed(String businessType, boolean isCache, Object session) {
		super.onSucceed(businessType, isCache, session);
		LogUtils.log(TAG, LogUtils.getFunctionName() + "   " + businessType);
		if (businessType.equals(Url.ADD_FAVORITE_URL)) {
			mIsCollectSend = false;
			mIsCollected = true;
			collectSucceed();
		}
		if (businessType.equals(Url.USER_CONFIG_URL)) {

			try {
				JSONObject mUserInfoObj = (JSONObject) session;
				UserInfoManager.getInstance().init(this, mUserInfoObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void collectSucceed() {
		Toast.makeText(this, getString(R.string.favorite_success),
				Toast.LENGTH_SHORT).show();
		mCollectedProgress.setVisibility(View.INVISIBLE);
		mCollectImg.setVisibility(View.VISIBLE);
	}

	@Override
	public void onErrorResult(String businessType, String errorOn,
			String errorInfo, Object session) {
		super.onErrorResult(businessType, errorOn, errorInfo, session);
		LogUtils.log(TAG, LogUtils.getFunctionName() + "   " + businessType
				+ "  errorInfo:" + errorInfo + "  errorOn:" + errorOn);
		if (businessType.equals(Url.ADD_FAVORITE_URL)) {
			mIsCollectSend = false;
			collectFailed();
		}
	}

	// h5调用接口
	@Override
	public void gotoHotOrderInterface(final String orderId,
			final String hotOrderId, final String nickname) {
		LogUtils.log(TAG, LogUtils.getThreadName() + " orderId = " + orderId
				+ ", hotOrderId = " + hotOrderId + ", nickname = " + nickname);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				if (TextUtils.isEmpty(hotOrderId)) {
					// 新建晒单
					Intent intent = new Intent(BaseWebViewActivity.this,
							SubmitHotOrderActivity.class);
					intent.putExtra(Constants.ORDER_ID, orderId);
					intent.putExtra(Constants.NICK_NAME, nickname);
					startActivityForResult(
							intent,
							Constants.ActivityRequestCode.REQUEST_CODE_FROM_H5_TO_HOT_ORDER);
					StatService.onEvent(BaseWebViewActivity.this,
							BaiduStatConstants.POST_ORDER,
							BaiduStatConstants.NEW);
				} else {
					// 显示已提交的晒单信息
					Intent intent = new Intent(BaseWebViewActivity.this,
							ShowHotOrderActivity.class);
					intent.putExtra(Constants.ORDER_ID, orderId);
					intent.putExtra(Constants.HOT_ORDER_ID, hotOrderId);
					startActivityForResult(
							intent,
							Constants.ActivityRequestCode.REQUEST_CODE_FROM_H5_TO_HOT_ORDER);
					StatService.onEvent(BaseWebViewActivity.this,
							BaiduStatConstants.POST_ORDER,
							BaiduStatConstants.AGAIN);
				}
			}
		});
	}
}
// Gionee <yuwei><2013-7-23> add for CR00821559 end
