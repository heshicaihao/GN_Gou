// Gionee <yangxiong><2014-8-5> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-8-5 下午05:28:39
 */
package com.gionee.client.view.shoppingmall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.CommonUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.adapter.AbstractListBaseAdapter;
import com.gionee.client.view.widget.CustomToast;
import com.gionee.client.view.widget.PullToRefreshListView;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.config.ControlKey;
import com.gionee.framework.model.config.ControlKey.request.control.CacheType;
import com.gionee.framework.operation.business.FRequestEntity.ListRequestParams;
import com.gionee.framework.operation.business.PortBusiness;
import com.gionee.framework.operation.business.RequestEntity;
import com.gionee.framework.operation.page.PageCacheManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

/**
 * @author yangxiong <br/>
 * @description TODO 列表展示组件, 及其综合业务逻辑类
 */
public abstract class AbstractBaseList extends FrameLayout implements
		IBusinessHandle, OnClickListener {
	private final String TAG = "AbstractBaseList_" + getClass().getSimpleName();
	/**
	 * 一些接口特用的参数信息；
	 */
	private String mUrl;
	private String mDataTargetKey;
	protected PullToRefreshListView mCommentListView;
	protected View mNoBargainLayout;
	private ProgressBar mProgressBar;
	private ImageView mGoTopBtn;
	protected AbstractListBaseAdapter mCommentAdaper;
	protected int mCurpage = 1;
	private boolean mIsHasNextPage = false;
	private boolean mIsLoading = false;
	private CustomToast mCustomToast;
	/**
	 * 每页加载数据条数
	 */
	public static final int COUNT_PER_PAGE = 12;
	protected View mContentView;
	protected MyBean mSelfData;
	private BaseFragment mFragmentContainer;

	public AbstractBaseList(Context context) {
		super(context);
		init(context);
	}

	public AbstractBaseList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public AbstractBaseList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public View onCreateView(ViewGroup container, Bundle savedInstanceState) {
		LogUtils.logd(TAG, LogUtils.getThreadName());
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.comment, container, false);
		initView(view);
		return view;
	}

	public void setFragmentContainer(BaseFragment mFragmentContainer) {
		this.mFragmentContainer = mFragmentContainer;
	}

	public View getContentView() {
		return mContentView;
	}

	public AbstractListBaseAdapter getListBaseAdapter() {
		return mCommentAdaper;
	}

	public PullToRefreshListView getPullToRefreshListView() {
		return mCommentListView;
	}

	public View getCustomToastParentView() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		return mCommentListView;
	}

	public void setProgressState(int state) {
		mProgressBar.setVisibility(state);
	}

	public void updateList() {
		JSONObject rebateInfo = mSelfData.getJSONObject(mDataTargetKey);
		mProgressBar.setVisibility(View.GONE);
		if (rebateInfo == null) {
			return;
		}

		try {
			JSONArray jsonArray = rebateInfo
					.getJSONArray(HttpConstants.Response.LIST_JA);
			if (jsonArray == null || jsonArray.length() == 0) {
				showNoDataLayout();
			} else {
				LogUtils.log(TAG, LogUtils.getThreadName()
						+ "jsonArray.length = " + jsonArray.length()
						+ ", jsonArray = " + jsonArray);
				hideNoDataLayout();
				updateView();
			}
			mCommentAdaper.setmCommentArray(jsonArray);
			mCommentAdaper.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param session
	 *            如果缓存，数据存放在map, 如果不缓存，数据存放在session.
	 * @description 将页数据添加到adapter数据中
	 */
	@Override
	public void onSucceed(String businessType, boolean isCache, Object session) {
		LogUtils.log(TAG, LogUtils.getThreadName() + " isCache: " + isCache);
		loadComplete();
		mIsLoading = false;
		if (businessType.equals(mUrl)) {
			if (!isCache) {
				mProgressBar.setVisibility(View.GONE);
				hideProgressBar();
			}
			JSONObject rebateInfo = mSelfData.getJSONObject(mDataTargetKey);
			if (rebateInfo == null) {
				LogUtils.log(TAG, LogUtils.getThreadName()
						+ " rebateInfo == null ");
				return;
			}

			try {
				JSONArray jsonArray = rebateInfo
						.getJSONArray(HttpConstants.Response.LIST_JA);
				mCommentListView.setVisibility(View.VISIBLE);
				/**
				 * 显示没有数据的提示界面
				 */
				if (jsonArray == null || jsonArray.length() == 0) {
					if (!isCache) {
						showNoDataLayout();
					}
					mCommentAdaper.setmCommentArray(jsonArray);
					mCommentAdaper.notifyDataSetChanged();
					return;
				}
				/**
				 * 显示物语列表
				 */
				else {
					LogUtils.log(TAG, LogUtils.getThreadName()
							+ "jsonArray.length = " + jsonArray.length()
							+ ", jsonArray = " + jsonArray);
					if (isFirstBoot()) {
						resetFistBoot();
						hideProgressBar();
					}
					hideNoDataLayout();
					mCommentAdaper.setmCommentArray(jsonArray);
					mCommentAdaper.notifyDataSetChanged();
					mIsHasNextPage = rebateInfo
							.getBoolean(HttpConstants.Response.HASNEXT);
					mCurpage = rebateInfo
							.getInt(HttpConstants.Response.CURPAGE_I);
					updateView();
					showBootGuide();
				}
			} catch (JSONException e) {
				LogUtils.log(TAG, LogUtils.getThreadName() + " JSONException: "
						+ e);
				e.printStackTrace();
			}
		}
	}

	private void resetFistBoot() {
		if (mFragmentContainer != null) {
			if (mFragmentContainer.isFirstBoot()) {
				LogUtils.log(TAG, LogUtils.getThreadName()
						+ "mFragmentContainer != null ");
				mFragmentContainer.resetFistBoot();
			}
		} else {
			if (((BaseFragmentActivity) getContext()).isFirstBoot(getClass()
					.getName())) {
				LogUtils.log(TAG, LogUtils.getThreadName()
						+ "mFragmentContainer == null ");
				((BaseFragmentActivity) getContext()).resetFistBoot(getClass()
						.getName());
			}
		}
	}

	public boolean isFirstBoot() {
		if (mFragmentContainer != null) {
			return mFragmentContainer.isFirstBoot();
		} else {
			return ((BaseFragmentActivity) getContext()).isFirstBoot(getClass()
					.getName());
		}
	}

	@Override
	public void onErrorResult(String businessType, String errorOn,
			String errorInfo, Object session) {
		LogUtils.log(TAG, LogUtils.getThreadName() + "errorOn = " + errorOn
				+ ", errorInfo = " + errorInfo);
		onErrorResultToast(errorInfo);
		loadComplete();
		mIsLoading = false;
		mProgressBar.setVisibility(View.GONE);
		mCommentListView.setVisibility(View.VISIBLE);
		hideProgressBar();
		showNodataInfoIfNeed();
	}

	private void onErrorResultToast(String errorInfo) {
		if (getContext().getString(R.string.upgrade_error_network_exception)
				.equals(errorInfo)) {
			showNoNetErrToast();
			return;
		}
		AndroidUtils.showErrorInfo(getContext(), errorInfo);
	}

	private void showNoNetErrToast() {
		mCustomToast.setToastText(getContext().getString(
				R.string.upgrade_no_net));
		int topMagin = AndroidUtils.dip2px(getContext(), 74);
		mCustomToast.showToast(mContentView, topMagin);
	}

	private void hideProgressBar() {
		if (mFragmentContainer != null) {
			LogUtils.log(TAG, LogUtils.getThreadName()
					+ "mFragmentContainer != null ");
			mFragmentContainer.hideLoading();
		} else {
			LogUtils.log(TAG, LogUtils.getThreadName()
					+ "mFragmentContainer == null ");
			((BaseFragmentActivity) getContext()).hidePageLoading();
		}
	}

	/**
	 * 接口特有的参数信息；
	 */
	protected abstract MyBean getOtherParametersBean();

	/**
	 * 网络接口地址;
	 */
	protected abstract String getUrl();

	/**
	 * 显示开机引导页；
	 */
	protected abstract void showBootGuide();

	/**
	 * 列表适配器
	 */
	protected abstract AbstractListBaseAdapter getAdapter();

	/**
	 * 无数据提示信息
	 * 
	 * @return String 资源id
	 */
	protected abstract int getNoDataMessage();

	/**
	 * 是否显示 action Button
	 * 
	 * @return boolean
	 */
	protected abstract boolean isShowActionBtn();

	/**
	 * 无数据页面被点击
	 * 
	 * @author yangxiong
	 * @description TODO
	 */
	protected abstract void onClickWhenNoDataLayout();

	/**
	 * 无数据页面button上的文本信息
	 * 
	 * @return String 资源id
	 */
	protected abstract int getActionBtnText();

	protected abstract String getDataTargetKey();

	private void init(Context context) {
		this.mSelfData = PageCacheManager.LookupPageData(context.getClass()
				.getName());
		this.mUrl = getUrl();
		this.mDataTargetKey = getDataTargetKey();
		this.mCommentAdaper = getAdapter();
		mContentView = onCreateView(null, null);
		addView(mContentView);
		mCustomToast = new CustomToast(context);
		// mCommentListView.getRefreshableView().setHeaderDividersEnabled(false);
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
	private void requestData(IBusinessHandle handle, String dataTagetKey,
			int page, int perpage) {
		try {
			RequestEntity request = new RequestEntity(mUrl, handle,
					dataTagetKey, new ListRequestParams(
							HttpConstants.Response.BargainPrice.LIST_JA,
							page > 1));
			MyBean bean = request.getRequestParam();
			bean.put(HttpConstants.Request.PERPAGE_S, perpage);
			bean.put(HttpConstants.Request.PAGE_S, page);
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.putAll(getOtherParametersBean());
			PortBusiness.getInstance().startBusiness(request, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean clearCacheByRequest(IBusinessHandle handle,
			String dataTagetKey, int page, int perpage, String targetId,
			String keyOfId) {
		try {
			RequestEntity request = new RequestEntity(mUrl, handle,
					dataTagetKey, new ListRequestParams(
							HttpConstants.Response.BargainPrice.LIST_JA,
							page > 1));
			MyBean bean = request.getRequestParam();
			bean.put(HttpConstants.Request.PERPAGE_S, perpage);
			bean.put(HttpConstants.Request.PAGE_S, page);
			bean.put(ControlKey.request.control.__cacheType_enum,
					CacheType.ShowCacheAndNet);
			bean.put(ControlKey.request.control.__method_s, "GET");
			bean.putAll(getOtherParametersBean());
			return PortBusiness.getInstance().deleteItemOnListCacheByRequest(
					request, targetId, keyOfId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void updateView() {
		String label = AndroidUtils.getCurrentTimeStr(getSelfContext());
		mCommentListView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		mProgressBar.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void resetPullRefreshUi() {
		mCommentListView.postDelayed(new Runnable() {

			@Override
			public void run() {
				mCommentListView.onRefreshComplete();
				// mCommentListView.setMode(Mode.PULL_FROM_START);
			}
		}, 1000);
	}

	private void loadComplete() {
		mCommentListView.postDelayed(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				mCommentListView.onRefreshComplete();
				int visibileItemCount = mCommentListView.getRefreshableView()
						.getLastVisiblePosition()
						- mCommentListView.getRefreshableView()
								.getFirstVisiblePosition();
				int itemSum = mCommentListView.getRefreshableView().getCount() - 2;
				LogUtils.log(TAG, LogUtils.getThreadName()
						+ " visibileItemCount = " + visibileItemCount
						+ ", itemSum = " + itemSum);
				if (itemSum > visibileItemCount) {
					mCommentListView.setMode(Mode.BOTH);
				} else {
					mCommentListView.setMode(Mode.PULL_FROM_START);
				}
			}
		}, 1000);
	}

	public void updateTitleBarRightBtn() {
		boolean visible = false;
		JSONObject rebateInfo = mSelfData.getJSONObject(mDataTargetKey);
		int productCount = 0;
		if (rebateInfo != null) {
			try {
				JSONArray jsonArray = rebateInfo
						.getJSONArray(HttpConstants.Response.LIST_JA);
				productCount = jsonArray.length();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (productCount == 0 || mCommentAdaper.getCount() == 0) {
			visible = false;
		} else {
			visible = true;
		}
		((BaseFragmentActivity) getContext()).getTitleBar().setRightBtnVisible(
				visible);
	}

	/**
	 * @author yangxiong
	 * @description TODO
	 */
	public void showNodataInfoIfNeed() {
		JSONObject rebateInfo = mSelfData.getJSONObject(mDataTargetKey);
		int productCount = 0;
		if (rebateInfo != null) {
			try {
				JSONArray jsonArray = rebateInfo
						.getJSONArray(HttpConstants.Response.LIST_JA);
				productCount = jsonArray.length();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 显示没有数据的提示界面
		 */
		if (productCount == 0 || mCommentAdaper.getCount() == 0) {
			showNoDataLayout();
		} else {
			hideNoDataLayout();
		}
	}

	public void showNoDataLayout() {
		if (mNoBargainLayout != null) {
			LogUtils.logd(TAG, LogUtils.getThreadName()
					+ " mNoBargainLayout  != null ");
			mNoBargainLayout.setVisibility(View.VISIBLE);
			return;
		}
		initNoDataLayoutViews();
	}

	private void initNoDataLayoutViews() {
		LogUtils.logd(TAG, LogUtils.getThreadName()
				+ " custruct mNoBargainLayout");
		ViewStub stub = (ViewStub) findViewById(R.id.no_comment_layout);
		if (stub == null) {
			LogUtils.logd(TAG, LogUtils.getThreadName() + " stub = null");
			return;
		}
		mNoBargainLayout = stub.inflate();
		RelativeLayout above = (RelativeLayout) mNoBargainLayout
				.findViewById(R.id.above_layout);
		above.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogUtils.log(TAG, LogUtils.getThreadName());
				if (((BaseFragmentActivity) getSelfContext())
						.isFastDoubleClick()) {
					return;
				}
				onClickWhenNoDataLayout();
			}
		});
		TextView mMessageTv = (TextView) mNoBargainLayout
				.findViewById(R.id.message);
		mMessageTv.setText(getNoDataMessage());
		if (isShowActionBtn()) {
			Button mActionBtn = (Button) mNoBargainLayout
					.findViewById(R.id.action_btn);
			mActionBtn.setOnClickListener(this);
			mActionBtn.setText(getActionBtnText());
			mActionBtn.setVisibility(View.VISIBLE);
		}
		mNoBargainLayout.setVisibility(View.VISIBLE);
	}

	public void hideNoDataLayout() {
		LogUtils.logd(TAG, LogUtils.getThreadName());
		if (mNoBargainLayout != null) {
			mNoBargainLayout.setVisibility(View.GONE);
		}
	}

	public void pullDownToRefresh() {
		if (mIsLoading) {
			return;
		}
		mCurpage = 1;
		mIsLoading = true;
		hideNoDataLayout();
		requestData(AbstractBaseList.this, mDataTargetKey, mCurpage,
				COUNT_PER_PAGE);
	}

	public void refreshCurrentPageData() {
		if (mIsLoading) {
			return;
		}
		mProgressBar.setVisibility(View.VISIBLE);
		mIsLoading = true;
		hideNoDataLayout();
		requestData(AbstractBaseList.this, mDataTargetKey, mCurpage,
				COUNT_PER_PAGE);
	}

	public void updateCurrentPageCacheWhenDelete(String targetId,
			String keyOfTargetId) {
		for (int i = 1; i <= mCurpage; i++) {
			boolean result = clearCacheByRequest(AbstractBaseList.this,
					mDataTargetKey, i, COUNT_PER_PAGE, targetId, keyOfTargetId);
			if (result) {
				break;
			}
		}
	}

	private void initView(View view) {
		mGoTopBtn = (ImageView) view.findViewById(R.id.go_top);
		mGoTopBtn.setOnClickListener(this);
		mProgressBar = (ProgressBar) view.findViewById(R.id.loading_bar);
		initListView(view);
		mIsLoading = true;
		mCommentListView.post(new Runnable() {
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				mCommentListView.setVisibility(View.GONE);
				showProgressBar();
				requestData(AbstractBaseList.this, mDataTargetKey, 1,
						COUNT_PER_PAGE);
			}
		});
	}

	private void showProgressBar() {
		if (mFragmentContainer != null) {
			if (mFragmentContainer.isFirstBoot()) {
				LogUtils.log(TAG, LogUtils.getThreadName()
						+ "mFragmentContainer != null ");
				mFragmentContainer.showLoading();
			}
		} else {
			if (((BaseFragmentActivity) getContext()).isFirstBoot(getClass()
					.getName())) {
				LogUtils.log(TAG, LogUtils.getThreadName()
						+ "mFragmentContainer == null ");
				((BaseFragmentActivity) getContext()).showPageLoading();
			}
		}
	}

	private void initListView(View view) {
		mCommentListView = (PullToRefreshListView) view
				.findViewById(R.id.comment_list);
		mCommentListView.getRefreshableView().setSelector(
				new ColorDrawable(Color.TRANSPARENT));
		mCommentListView.setAdapter(mCommentAdaper);
		mCommentListView.setMode(Mode.BOTH);
		mCommentListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						LogUtils.log(TAG, LogUtils.getThreadName());
						pullDownToRefresh();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						LogUtils.log(TAG, LogUtils.getThreadName());
						if (mIsLoading) {
							return;
						}
						// mCommentListView.setRefreshing();
						LogUtils.log(TAG, LogUtils.getThreadName()
								+ "isHasNextPage = " + mIsHasNextPage);
						if (mIsHasNextPage) {
							mProgressBar.setVisibility(View.VISIBLE);
							// mCommentListView.setMode(Mode.DISABLED);
							mIsLoading = true;
							requestData(AbstractBaseList.this, mDataTargetKey,
									mCurpage + 1, COUNT_PER_PAGE);
						} else {
							resetPullRefreshUi();
							mCommentListView.showNoMoreText();
							// Toast.makeText(getContext(),
							// R.string.no_more_msg, Toast.LENGTH_SHORT).show();
						}
					}

				});

		mCommentListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int status = View.VISIBLE;
				if (firstVisibleItem == 0) {
					status = View.GONE;
				}
				mGoTopBtn.setVisibility(status);
			}
		});
	}

	public void onClick(View v) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		switch (v.getId()) {
		case R.id.go_top:
			mCommentListView.getRefreshableView().setSelection(0);
			break;
		case R.id.action_btn:
			gotoHomeActivityWithCommentTab();
			break;
		default:
			break;
		}
	}

	@Override
	public Context getSelfContext() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		return getContext();
	}

	@Override
	public void onCancel(String businessType, Object session) {
		LogUtils.log(TAG, LogUtils.getThreadName());

	}

	protected void gotoHomeActivity(int resultCode) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		((Activity) getContext()).setResult(resultCode);
		AndroidUtils.finishActivity((Activity) getContext());
	}

	public void gotoHomeActivityWithCommentTab() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		((Activity) getContext())
				.setResult(Constants.ActivityResultCode.RESULT_CODE_STORY);
		AndroidUtils.finishActivity((Activity) getContext());
	}
}
