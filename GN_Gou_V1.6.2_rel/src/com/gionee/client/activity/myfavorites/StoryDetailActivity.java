// Gionee <yangxiong><2014-8-6> add for CR00850885 begin
package com.gionee.client.activity.myfavorites;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.story.GNDiscussDetailsActivity;
import com.gionee.client.activity.story.SendStoryCommentsDialog;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.delayTask.DelaySyncExecutor;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.persistent.ShareKeys;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.CommonUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.shoppingmall.CommentsProgressBar;
import com.gionee.client.view.widget.GNCustomDialog;
import com.gionee.framework.operation.net.NetUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;

/**
 * @author yangxiong <br/>
 * @description TODO 物语详情页
 */
public class StoryDetailActivity extends ThridPartyWebActivity implements
		OnClickListener, OnLastItemVisibleListener {
	private static final String COLLECT = "collect";
	private static final String PRAISE = "praise";
	private static final String SHARE = "share";
	private static final String TAG = "Story_DetailActivity";
	/**
	 * 类型 0：取消赞，1：赞
	 */
	public static final int TYPE_COMMENT_CANCEL_PRAISE = 0;
	public static final int TYPE_COMMENT_PRAISE = 1;
	private TextView mCommentsCountTv;
	private ImageView mPraiseBtn;
	private ImageView mCollectBtn;
	private ImageView mShareBtn;
	private Button mPromptBtn;
	private SendStoryCommentsDialog mSendStoryCommentsDialog;
	private CommentsProgressBar mCommentsProgressBar;
	private RequestAction mRequestAction;
	private String mCommentSizeStr;
	private String mPraiseSizeStr;
	private int mPraiseSize;
	private int mPosition;
	private String mSendContent;
	private String mNickname;
	/**
	 * 物语的id.
	 */
	private int mCommentId;
	/**
	 * 收藏的id
	 */
	private int mFavoriteId;

	/**
	 * 当前用户点击的点赞状态
	 */
	private boolean mIsPraise;
	/**
	 * 点赞或取消点赞，正在发送中...
	 */
	private boolean mIsPraiseSending = false;
	/**
	 * 保存已经发送的点赞状态
	 */
	private boolean mIsPraiseSent;
	private DelaySyncExecutor mPraiseDelaySyncExecutor;

	/**
	 * 当前用户点击的收藏状态
	 */
	private boolean mIsFavorite;
	/**
	 * 收藏或取消收藏，正在发送中...
	 */
	private boolean mIsFavoriteSendding = false;
	/**
	 * 保存已经发送的收藏状态
	 */
	private boolean mIsFavoriteSent;
	private DelaySyncExecutor mFavoriteDelaySyncExecutor;

	// private boolean mIsCommentsSending;
	private boolean mIsShowingTost = false;
	private Toast mToast;
	private String mRelyId = "";
	private View mBack;

	public void postFavorite(boolean isFavorite) {
		mFavoriteDelaySyncExecutor.setDelayed(DelaySyncExecutor.DELAY_MILLIS);
	}

	public void postPraise(boolean isFavorite) {
		mPraiseDelaySyncExecutor.setDelayed(DelaySyncExecutor.DELAY_MILLIS);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		
		if (AndroidUtils.translateTopBar(this)) {
			//去掉标题栏
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			//			去掉信息栏
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		super.onCreate(savedInstanceState);
		initCurrentView();
		Intent intent = getIntent();
		initIntentData(intent);
		int praiseFlag = ShareDataManager.getDataAsInt(this,
				ShareKeys.KEY_COMMENT_PRAISE_PREFIX + mCommentId, 0);
		if (praiseFlag == 1) {
			mIsPraise = true;
			praiseBtnPraised();
		}

		if (mIsFavorite) {
			mFavoriteId = intent.getIntExtra("fav_id", 0);
			LogUtils.log(TAG, "is favorite" + mIsFavorite + "   favorite id"
					+ mFavoriteId);
			collectBtnFavorite();
			LogUtils.log(TAG, LogUtils.getThreadName() + " mfavoriteId = "
					+ mFavoriteId);
		}

		mIsFavoriteSent = mIsFavorite;
		mIsPraiseSent = mIsPraise;
		LogUtils.log(TAG, LogUtils.getThreadName() + " mIsFavorite = "
				+ mIsFavorite);
		mTitleTv.setText(R.string.comment_detail);
		initWebview();
		createFavorieDelayExecutor();
		createPraiseDelayExecutor();
		isFavorite();
	}

	private void isFavorite() {
		mRequestAction.commentsIsFavorite(this,
				HttpConstants.Data.CommentsIsFavorite.IS_FAVOR_INFO_JO,
				mCommentId, TYPE_COMMENT_PRAISE);
	}

	private void createPraiseDelayExecutor() {
		mPraiseDelaySyncExecutor = new DelaySyncExecutor() {
			@Override
			protected void onExecute() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				if (mIsPraiseSending) {
					LogUtils.log(TAG, LogUtils.getThreadName()
							+ "praise: sendding, return!");
					return;
				}

				if (mIsPraise == mIsPraiseSent) {
					LogUtils.log(TAG, LogUtils.getThreadName()
							+ "praise: state is same, return! "
							+ " mIsFavorite = " + mIsFavorite);
					return;
				}
				praiseSend();
			}
		};
	}

	private void createFavorieDelayExecutor() {
		mFavoriteDelaySyncExecutor = new DelaySyncExecutor() {
			@Override
			protected void onExecute() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				if (mIsFavoriteSendding) {
					LogUtils.log(TAG, LogUtils.getThreadName()
							+ "sendding, return!");
					return;
				}

				if (mIsFavorite == mIsFavoriteSent) {
					LogUtils.log(TAG, LogUtils.getThreadName()
							+ "state is same, return! " + " mIsFavorite = "
							+ mIsFavorite);
					return;
				}
				favoriteSend();
			}
		};
	}

	private void initIntentData(Intent intent) {

		if (intent != null) {

			mPosition = intent.getIntExtra("position", -1);
			mCommentId = intent.getIntExtra("id", 0);
			mIsFavorite = intent.getBooleanExtra("is_favorite", false);
			mCommentSizeStr = intent.getStringExtra("comment_count");
			if (!TextUtils.isEmpty(mCommentSizeStr)) {
				mCommentsCountTv.setText(mCommentSizeStr);
				mCommentsCountTv.setVisibility(View.VISIBLE);
				mCommentsCountTv.setOnClickListener(this);
			}
			mPraiseSizeStr = intent.getStringExtra("praise_count");
			getPraiseCount();
		}
	}

	private void initCurrentView() {
		setHeadVisible(false);
		setFootVisible(false);
		showShadow(false);
		mCommentsCountTv = (TextView) findViewById(R.id.comments_count);

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_layout);

		View view = LayoutInflater.from(this).inflate(
				R.layout.comment_detail_foot, null, false);
		RelativeLayout.LayoutParams layoutParamsall = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParamsall.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		layout.addView(view, layoutParamsall);

		mBack = (ImageView) findViewById(R.id.story_detil_back);
		mBack.setVisibility(View.VISIBLE);
		View shadow = (View) findViewById(R.id.web_shadow);
		shadow.setVisibility(View.GONE);

		mPraiseBtn = (ImageView) view.findViewById(R.id.praise_btn);
		mPraiseBtn.setOnClickListener(this);
		mCollectBtn = (ImageView) view.findViewById(R.id.collect_btn);
		mCollectBtn.setOnClickListener(this);
		mShareBtn = (ImageView) view.findViewById(R.id.share_btn);
		mShareBtn.setOnClickListener(this);
		mPromptBtn = (Button) view.findViewById(R.id.prompt_btn);
		mPromptBtn.setOnClickListener(this);
		mCommentsProgressBar = (CommentsProgressBar) view
				.findViewById(R.id.comments_progress_bar);
		mCommentsProgressBar.setOnClickListener(this);
		mRequestAction = new RequestAction();

		
	}

	/**
	 * 
	 * @author yangxiong
	 * @description TODO
	 */
	private void updateCommentsContent() {
		if (mCommentsProgressBar != null && mSendStoryCommentsDialog != null) {
			String contentStr = mSendStoryCommentsDialog.getCommentContent();
			if (!TextUtils.isEmpty(contentStr)) {
				mCommentsProgressBar
						.setCommentsContent(mSendStoryCommentsDialog
								.getCommentContent());
				int color = getResources()
						.getColor(R.color.comments_text_color);
				mCommentsProgressBar.setCommentsColor(color);
			} else {
				initCommentProgressBar();
			}
		}
	}

	public void initCommentProgressBar() {
		mCommentsProgressBar
				.setCommentsContent(getString(R.string.publish_comment));
		int color = getResources().getColor(R.color.comments_text_nor);
		mCommentsProgressBar.setCommentsColor(color);
	}

	@Override
	public boolean gotoOtherPage(String url) {
		Intent intent = new Intent();
		intent.putExtra(StatisticsConstants.KEY_INTENT_URL, url);
		intent.setClass(this, ThridPartyWebActivity.class);
		startActivity(intent);
		return true;
	}

	private void favoriteSend() {
		LogUtils.log(TAG, LogUtils.getThreadName() + " mIsFavorite = "
				+ mIsFavorite);
		mIsFavoriteSendding = true;
		if (mIsFavorite) {
			mRequestAction.commentsFavorite(this,
					HttpConstants.Data.CommentsFavorite.LIST_INFO_JO,
					mCommentId);
		} else {
			mRequestAction.cancelFavorite(this,
					HttpConstants.Data.CancelFavorite.LIST_INFO_JO,
					mFavoriteId, mCommentId, TYPE_COMMENT_PRAISE);
		}
	}

	private void praiseSend() {
		LogUtils.log(TAG, LogUtils.getThreadName() + " mIsPraise = "
				+ mIsPraise);
		mIsPraiseSending = true;
		if (mIsPraise) {
			mRequestAction.commentsPraise(this, null, mCommentId,
					TYPE_COMMENT_PRAISE);
		} else {
			mRequestAction.commentsPraise(this, null, mCommentId,
					TYPE_COMMENT_CANCEL_PRAISE);
		}
	}

	private void processBootGuide() {
		if (CommonUtils.isNeedShowBootGuide(getSelfContext(), this.getClass()
				.getName())) {
			mPromptBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.webview_back_top:
			setCallBackData();
			break;
		case R.id.comments_count:
			gotoDiscussPage();
			StatService.onEvent(StoryDetailActivity.this,
					BaiduStatConstants.REVIEW_COMMENTS,
					BaiduStatConstants.TITLE);
			break;
		case R.id.comments_progress_bar:
			if (isFastDoubleClick()) {
				return;
			}
			// if (mIsCommentsSending) {
			// AndroidUtils.showToast(this, R.string.sending_pls_wait, 1000);
			// return;
			// }
			showCommentsDialog(null, null);
			break;
		case R.id.prompt_btn:
			mPromptBtn.setVisibility(View.GONE);
			break;
		case R.id.praise_btn:
			if (isFastDoubleClick()) {
				return;
			}
			processPraiseClickEvent();
			break;
		case R.id.collect_btn:
			if (isFastDoubleClick()) {
				return;
			}
			processCollectClickEvent();
			break;
		case R.id.share_btn:
			showShareDialog();
			break;
		case R.id.share_weibo:
			setmDescription(mUrl);
			break;
		case R.id.copy_link:
			AndroidUtils.copyUriToClipboard(Uri.parse(mUrl), this);
			closeShareDialog();
			Toast.makeText(this, R.string.copy_to_clipboard, Toast.LENGTH_SHORT)
					.show();
			StatService.onEvent(StoryDetailActivity.this, SHARE, "copy_link");
			break;
		case R.id.story_detil_back:
			onBackPressed();
			break;
		default:
			break;
		}
		super.onClick(v);

	}

	/**
	 * 
	 * @author yangxiong
	 * @description TODO
	 */
	private void processPraiseClickEvent() {
		if (NetUtil.isNetworkAvailable(this)) {
			if (mIsPraise) {
				mIsPraise = false;
				praiseBtnUnpraise();
				StatService.onEvent(this, PRAISE, "cancel");
				if (mPraiseSize >= 0) {
					mPraiseSize--;
				}
			} else {
				mIsPraise = true;
				praiseBtnPraised();
				StatService.onEvent(this, PRAISE, PRAISE);
				if (mPraiseSize >= 0) {
					mPraiseSize++;
				}
			}
			postPraise(mIsPraise);
		} else {
			// AndroidUtils.showErrorInfo(this,
			// getString(R.string.upgrade_error_network_exception));
			Toast.makeText(this, getString(R.string.upgrade_no_net),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 
	 * @author yangxiong
	 * @description TODO
	 */
	private void processCollectClickEvent() {
		LogUtils.log(TAG, LogUtils.getFunctionName());
		if (NetUtil.isNetworkAvailable(this)) {
			if (mIsFavorite) {
				mIsFavorite = false;
				collectBtnUnfavorite();
				StatService.onEvent(this, COLLECT, "cancel");
				if (mPraiseSize >= 0) {
					mPraiseSize--;
				}
			} else {
				mIsFavorite = true;
				collectBtnFavorite();
				StatService.onEvent(this, COLLECT, COLLECT);
				if (mPraiseSize >= 0) {
					mPraiseSize++;
				}
			}
			postFavorite(mIsFavorite);
		} else {
			Toast.makeText(this, getString(R.string.upgrade_no_net),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 
	 * @author yangxiong
	 * @description TODO
	 */
	private void setCallBackData() {
		Intent intent = new Intent();
		intent.putExtra("position", mPosition);
		intent.putExtra("praise_count", mPraiseSize < 0 ? mPraiseSizeStr
				: String.valueOf(mPraiseSize));
		intent.putExtra("comments_count", mCommentSizeStr);
		intent.putExtra("isFavorite", mIsFavorite);
		LogUtils.log(TAG, LogUtils.getThreadName() + " mPosition = "
				+ mPosition + " mPraiseSize = " + mPraiseSize
				+ " mCommentSizeStr = " + mCommentSizeStr + " mIsFavorite = "
				+ mIsFavorite);
		setResult(Constants.ActivityRequestCode.REQUEST_CODE_COMMENT_DETAIL,
				intent);
	}

	@Override
	public void onBackPressed() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		setCallBackData();
		super.onBackPressed();
	}

	private void gotoDiscussPage() {
		Intent intent = new Intent();
		intent.setClass(StoryDetailActivity.this,
				GNDiscussDetailsActivity.class);
		intent.putExtra(Constants.TYPE_ID, String.valueOf(mCommentId));
		startActivityForResult(intent,
				Constants.ActivityRequestCode.REQUEST_CODE_DISCUSS);
	}

	/**
	 * 
	 * @author yangxiong
	 * @description TODO
	 */
	private void showCommentsDialog(String commentId, String nickName) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		if (mSendStoryCommentsDialog != null) {
			mSendContent = mSendStoryCommentsDialog.getCommentContent();
		}
		mSendStoryCommentsDialog = new SendStoryCommentsDialog(this,
				Constants.CommentDialogType.COMMENT_STORY_TYPE, mSendContent);
		mSendStoryCommentsDialog.setMainId(String.valueOf(mCommentId));
		if (!TextUtils.isEmpty(nickName)) {
			mSendStoryCommentsDialog.setmAnswerNickName(nickName);
		}
		if (!TextUtils.isEmpty(commentId)) {
			mSendStoryCommentsDialog.setSecondId(commentId);
			mRelyId = commentId;
		} else {
			mSendStoryCommentsDialog
					.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							// updateCommentsContent();
							mRelyId = "";
						}
					});
		}
		mSendStoryCommentsDialog.setmRightBtnListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtils.log(TAG, LogUtils.getThreadName());
				// mIsCommentsSending = true;
				// mCommentsProgressBar.setProgressBarVisible(true);
			}
		});
		mSendStoryCommentsDialog.show();
		StatService.onEvent(this, BaiduStatConstants.COMMENT_BOX,
				BaiduStatConstants.TALE);
	}

	/**
	 * 
	 * @author yangxiong
	 * @description TODO
	 */
	private void showShareDialog() {
		try {
			if (mDialog == null) {
				mDialog = (GNCustomDialog) DialogFactory
						.createShareDialogOnCommentDetailActivity(this);
			}
			if (mDialog != null) {
				mDialog.show();
				mDialog.setDismissBtnVisible();
				mDialog.setCanceledOnTouchOutside(true);
				mDialog.getContentView().findViewById(R.id.share_weixin)
						.setOnClickListener(this);
				mDialog.getContentView().findViewById(R.id.share_friends)
						.setOnClickListener(this);
				mDialog.getContentView().findViewById(R.id.share_weibo)
						.setOnClickListener(this);
				mDialog.getContentView().findViewById(R.id.share_qq_friend)
						.setOnClickListener(this);
				mDialog.getContentView().findViewById(R.id.share_qq_zone)
						.setOnClickListener(this);
				mDialog.getContentView().findViewById(R.id.copy_link)
						.setOnClickListener(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSucceed(String businessType, boolean isCache, Object session) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onSucceed(businessType, isCache, session);
		if (businessType.equals(Url.COMMENT_PRAISE_URL)) {
			// JSONObject info =
			// mSelfData.getJSONObject(HttpConstants.Data.CommentsPraise.LIST_INFO_JO);
			JSONObject info = (JSONObject) session;
			LogUtils.log(TAG, LogUtils.getThreadName() + "praise info = "
					+ info);
			if (info != null) {
				int type = info.optInt(HttpConstants.Data.CommentsPraise.TYPE);
				if (type == TYPE_COMMENT_PRAISE) {
					ShareDataManager.saveDataAsInt(this,
							ShareKeys.KEY_COMMENT_PRAISE_PREFIX + mCommentId,
							TYPE_COMMENT_PRAISE);
					mIsPraiseSent = true;
				} else {
					ShareDataManager.saveDataAsInt(this,
							ShareKeys.KEY_COMMENT_PRAISE_PREFIX + mCommentId,
							TYPE_COMMENT_CANCEL_PRAISE);
					mIsPraiseSent = false;
				}
				checkPraiseRequest();
			}
		} else if (businessType.equals(Url.COMMENT_FAVORITE_URL)) {
			if (mSelfData == null) {
				return;
			}
			JSONObject info = mSelfData
					.getJSONObject(HttpConstants.Data.CommentsFavorite.LIST_INFO_JO);
			LogUtils.log(TAG, LogUtils.getThreadName() + "favorite info = "
					+ info);
			if (info != null) {
				mFavoriteId = info
						.optInt(HttpConstants.Data.CommentsFavorite.FAVORITE_ID);
			}
			processBootGuide();
			mIsFavoriteSent = true;
			checkFavoriteRequest();
		} else if (businessType.equals(Url.CANCEL_FAVORITE_URL)) {
			mIsFavoriteSent = false;
			checkFavoriteRequest();
		} else if (businessType.equals(Url.STORY_COMMENT_URL)) {
			mSendStoryCommentsDialog.resetNomalStatus();
			mSendStoryCommentsDialog.showSendBtn();
			mSendStoryCommentsDialog.dismiss();
			// mIsCommentsSending = false;
			updateNickName();
			AndroidUtils.showToast(this, R.string.send_success, 1000);
			// mCommentsProgressBar.setProgressBarVisible(false);
			initCommentProgressBar();
			refreshDiscuss(mSendStoryCommentsDialog.getCommentContent());
			mSendStoryCommentsDialog.setCommentContent("");
			// updateCommentsContent();
			updateCommentsCount(1);
			StatService.onEvent(this, "send_st", BaiduStatConstants.OK);
		} else if (businessType.equals(Url.COMMENT_IS_FAVORITE_URL)) {
			JSONObject info = mSelfData
					.getJSONObject(HttpConstants.Data.CommentsIsFavorite.IS_FAVOR_INFO_JO);
			boolean isFavor = info
					.optBoolean(HttpConstants.Data.CommentsIsFavorite.IS_FAV);
			mIsFavorite=mIsFavoriteSent=isFavor;
			if (isFavor) {
			    mFavoriteId=info.optInt(HttpConstants.Data.CommentsIsFavorite.FAV_ID_I);
				collectBtnFavorite();
			} else {
				collectBtnUnfavorite();
			}
		}
	}

	public void updateNickName() {
		if (!TextUtils.isEmpty(mSendStoryCommentsDialog.getNickName())) {
			UserInfoManager.getInstance().setNickName(
					mSendStoryCommentsDialog.getNickName());
			mNickname = mSendStoryCommentsDialog.getNickName();
		}
	}

	private void refreshDiscuss(String content) {
		try {
			mWebView.getRefreshableView().loadUrl(
					"javascript:window.COMMON_INTERFACE.loadComments()");
			// sendDiscussSucuss(mSelfData.getJSONObject(HttpConstants.Data.DiscussList.PRAISE_ID));
			// window.COMMON_INTERFACE.postComment
			String js = "javascript:window.COMMON_INTERFACE.postComment('"
					+ mNickname
					+ "','"
					+ content
					+ "','"
					+ mSelfData.getJSONObject(
							HttpConstants.Data.DiscussList.PRAISE_ID)
							.optString("id") + "','" + mRelyId + "')";
			mRelyId = "";
			// String js = "javascript:window.COMMON_INTERFACE.postComment()";
			mWebView.getRefreshableView().loadUrl(js);
			mSendContent = null;
			LogUtils.log(TAG, "js:" + js);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateCommentsCount(int count) {
		if (!TextUtils.isEmpty(mCommentSizeStr)) {
			try {
				int size = Integer.valueOf(mCommentSizeStr);
				mCommentSizeStr = String.valueOf((size + count));
				mCommentsCountTv.setText(mCommentSizeStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void getPraiseCount() {
		if (!TextUtils.isEmpty(mPraiseSizeStr)) {
			try {
				mPraiseSize = Integer.valueOf(mPraiseSizeStr);
			} catch (Exception e) {
				mPraiseSize = -1;
				e.printStackTrace();
			}
		}
	}

	private void checkFavoriteRequest() {
		mIsFavoriteSendding = false;
		if (mFavoriteDelaySyncExecutor.isInDelay()) {
			LogUtils.logd(TAG, LogUtils.getThreadName() + " cancel: in delay!");
			return;
		}

		if (mIsFavorite == mIsFavoriteSent) {
			LogUtils.logd(TAG, LogUtils.getThreadName()
					+ "cancel: state is same. return! mIsFavorite = "
					+ mIsFavorite);
			return;
		}

		if (this.isFinishing()) {
			return;
		}

		favoriteSend();
	}

	private void checkPraiseRequest() {
		mIsPraiseSending = false;
		if (mPraiseDelaySyncExecutor.isInDelay()) {
			LogUtils.logd(TAG, LogUtils.getThreadName() + " cancel: in delay!");
			return;
		}

		if (mIsPraise == mIsPraiseSent) {
			LogUtils.logd(TAG, LogUtils.getThreadName()
					+ "cancel: state is same. return! mIsPraise = " + mIsPraise);
			return;
		}

		if (this.isFinishing()) {
			return;
		}

		praiseSend();
	}

	@Override
	public void onErrorResult(String businessType, String errorOn,
			String errorInfo, Object session) {

		if (businessType.equals(Url.COMMENT_PRAISE_URL)) {
			super.onErrorResult(businessType, errorOn, errorInfo, session);
			int praiseFlag = ShareDataManager.getDataAsInt(this,
					ShareKeys.KEY_COMMENT_PRAISE_PREFIX + mCommentId, 0);
			if (praiseFlag == 1) {
				praiseBtnPraised();
			} else {
				praiseBtnUnpraise();
			}
			mIsPraiseSending = false;
		} else if (businessType.equals(Url.COMMENT_FAVORITE_URL)) {
			super.onErrorResult(businessType, errorOn, errorInfo, session);
			mIsFavoriteSendding = false;
			collectBtnUnfavorite();
		} else if (businessType.equals(Url.CANCEL_FAVORITE_URL)) {
			super.onErrorResult(businessType, errorOn, errorInfo, session);
			mIsFavoriteSendding = false;
			collectBtnFavorite();
		} else if (businessType.equals(Url.STORY_COMMENT_URL)) {

			mSendStoryCommentsDialog.showSendBtn();
			if (!TextUtils.isEmpty(errorOn)
					&& (errorOn.equals("1") || errorOn.equals("2"))) {
				mSendStoryCommentsDialog
						.setStatus(Constants.CommentDialogStaus.STAUS_CONTAIN_SENSITIVE_WORDS);
				mSendStoryCommentsDialog.setSensitiveWords(errorInfo);
				mSendStoryCommentsDialog.setCommentContent("");
				// updateCommentsContent();
			} else {
				mSendStoryCommentsDialog
						.setStatus(Constants.CommentDialogStaus.STATUS_SEND_ERROR);
			}

			// AndroidUtils.showToast(this, R.string.send_failed, 1000);
			StatService.onEvent(this, "send_st", BaiduStatConstants.ERR);
		}
	}

	/**
	 * @author yangxiong
	 * @description TODO 设置点赞按钮“已赞”状态
	 */
	private void praiseBtnPraised() {
		mPraiseBtn.setImageResource(R.drawable.praise_img);
	}

	/**
	 * @author yangxiong
	 * @description TODO 设置点赞按钮“未赞”状态
	 */
	private void praiseBtnUnpraise() {
		mPraiseBtn.setImageResource(R.drawable.unpraise_img);
	}

	/**
	 * @author yangxiong
	 * @description TODO 设置收藏按钮“未收藏”状态
	 */
	private void collectBtnUnfavorite() {
		mCollectBtn.setImageResource(R.drawable.favorites_img_uninclude);
	}

	/**
	 * @author yangxiong
	 * @description TODO 设置收藏按钮“已收藏”状态
	 */
	private void collectBtnFavorite() {
		mCollectBtn.setImageResource(R.drawable.favorites_img_include);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	@Override
	public void goBack() {
		if (!mWebView.getWebView().canGoBack() || isTabaoClick()
				|| isUnNetworkPage()) {
			finish();
			AndroidUtils.exitActvityAnim(this);
		} else {
			mWebView.getWebView().goBack();
		}
	}

	@SuppressLint("ShowToast")
	private void initWebview() {
		// mToast = Toast.makeText(StoryDetailActivity.this,
		// R.string.click_see_more, 2000);
		ImageView homeBtn = (ImageView) findViewById(R.id.webview_finish);
		mWebView.getRefreshableView().addJavascriptInterface(this, "share");
		homeBtn.setVisibility(View.GONE);
		mWebView.setMode(Mode.BOTH);
		mWebView.getWebView().setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
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
				mProgress.setProgress(progress);
				if (progress == 100) {
					String label = AndroidUtils
							.getCurrentTimeStr(StoryDetailActivity.this);
					mWebView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
					view.requestFocus();
					mWebView.onRefreshComplete();
					hidenProgress();
				}
			}

			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
					Callback callback) {
				// TODO Auto-generated method stub
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

		});
		mWebView.setOnLastItemVisibleListener(this);
	}

	public void loadCommentsList() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				gotoDiscussPage();
				StatService.onEvent(StoryDetailActivity.this,
						BaiduStatConstants.REVIEW_COMMENTS,
						BaiduStatConstants.WEB);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Constants.ActivityResultCode.RESULT_CODE_DISCUSS) {
			int count = data.getExtras().getInt(Constants.SEND_DISCUSS_COUNT);
			// refreshDiscuss();
			if (count > 0) {
				updateCommentsCount(count);
			}
		}
	}

	@Override
	public void onLastItemVisible() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		try {
			if (Integer.parseInt(mCommentSizeStr) > 3) {
				if (!mIsShowingTost) {
					mToast.show();
					mIsShowingTost = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addComment(String id, String nickname) {
		LogUtils.log(TAG, "id:" + id + "   nickname:" + nickname);
		final String discussId = id;
		final String nickName = nickname;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				showCommentsDialog(discussId, nickName);
			}

		});

	}
}

// Gionee <yangxiong><2014-8-6> add for CR00850885 end
