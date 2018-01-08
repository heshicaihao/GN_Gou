package com.gionee.client.activity.myfavorites;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.widget.PullToRefreshScrollView;
import com.gionee.framework.event.IBusinessHandle;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.handmark.pulltorefresh.library.internal.RotateLoadingLayout;
import com.huewu.pla.MultiColumnListView;
import com.huewu.pla.PLA_AdapterView;
import com.huewu.pla.PLA_AdapterView.OnItemClickListener;

public class ZhiwuFavorFragment extends BaseFragment implements
		OnClickListener, IBusinessHandle, OnRefreshListener2<ScrollView> {
	private RelativeLayout mParentLayout;
	private RelativeLayout mHistoryNoZhiwuLayout;
	private PullToRefreshScrollView mRefreshScrollView;
	private MultiColumnListView mWaterFallView = null;
	private ImageGridAdapter mAdapter;
	/**
	 * 每页加载数据条数
	 */
	public static final int COUNT_PER_PAGE = 12;
	private boolean mIsHasNextPage = false;
	private boolean mIsLoading = false;
	private int mCurrentPage = 1;
	private static final String DATATAGETKEY = HttpConstants.Data.ZhiwuFavorList.ZHIWUFAVOR_LIST_INFO_JO;
	protected RelativeLayout mRlLoading;
	private ImageView mIvBalloon;
	private ImageView mIvLunzi;
	private Animation mRotateAnimation;
	private Animation mRefreshBalloonAnimation;
	private Animation mRefreshBalloonAnimation2;
	private RequestAction mAction;
	private JSONArray mListData;
	private ProgressBar mLoadingBar;
	private boolean mIsFirstIn = true;
	private List<String> mClickList = new ArrayList<String>();
	private SharedPreferences mPreference;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mParentLayout = (RelativeLayout) inflater.inflate(
				R.layout.zhiwu_favor_fragment, null);
		mLoadingBar = (ProgressBar) mParentLayout
				.findViewById(R.id.loading_bar);
		mRefreshScrollView = (PullToRefreshScrollView) mParentLayout
				.findViewById(R.id.pull_to_refresh);
		mRefreshScrollView.setOnRefreshListener(this);
		mRefreshScrollView.setMode(Mode.BOTH);
		mWaterFallView = (MultiColumnListView) mParentLayout
				.findViewById(R.id.zhiwu_list);
		// imageUrls = new ArrayList<String>();
		mAdapter = new ImageGridAdapter(this, getActivity());
		mWaterFallView.setAdapter(mAdapter);
		mWaterFallView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(PLA_AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mAdapter.getmIsEditState()) {
					return;
				}
				JSONObject item = mListData.optJSONObject(position);
				try {
					item.put(
							HttpConstants.Data.ZhiwuFavorList.HITS,
							AndroidUtils.intgerToString(item
									.optString(HttpConstants.Data.ZhiwuFavorList.HITS)));
					mAdapter.setClickId(item
							.optString(HttpConstants.Data.ZhiwuFavorList.ID));
					mAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mPreference
						.edit()
						.putBoolean(
								"cl_"
										+ item.optString(HttpConstants.Data.ZhiwuFavorList.ID),
								true).commit();
				Intent intent = new Intent();
				intent.setClass(getActivity(), StoryDetailActivity.class);
				intent.putExtra(StatisticsConstants.KEY_INTENT_URL,
						item.optString(HttpConstants.Data.NewsList.URL_S));
				intent.putExtra("id",
						item.optInt(HttpConstants.Data.NewsList.ID_S));
				intent.putExtra("is_favorite",
						item.optBoolean(HttpConstants.Data.NewsList.FAVORITE_B));
				intent.putExtra("fav_id",
						item.optInt(HttpConstants.Data.NewsList.FAV_ID_I));
				intent.putExtra("comment_count",
						item.optString(HttpConstants.Data.NewsList.COMMENT_S));
				intent.putExtra("position", position);
				intent.putExtra(Constants.IS_SHOW_WEB_FOOTBAR, false);
				StatService.onEvent(getActivity(),
						BaiduStatConstants.TALE_CLICK,
						item.optInt(HttpConstants.Data.NewsList.ID_S) + "");
				getActivity().startActivity(intent);
				TextView text = (TextView) view.findViewById(R.id.news_title);
				text.setTextColor(getResources().getColor(
						R.color.zhiwu_clicked_text));
			}
		});
		initLoading(mParentLayout);
		mAction = new RequestAction();
		// queryMediaImages();
		mPreference = getActivity().getSharedPreferences("comment_view",
				Context.MODE_PRIVATE);
		return mParentLayout;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getData(mCurrentPage);
	}

	public MultiColumnListView getMultiColumnListView() {
		return mWaterFallView;
	}

	public void setEditBgIsVisible(boolean isVisible) {
		mAdapter.setmIsEditState(isVisible);
		mAdapter.notifyDataSetChanged();
	}

	private void initLoading(View layout) {
		// TODO Auto-generated method stub
		mRlLoading = (RelativeLayout) layout.findViewById(R.id.rl_loading);
		mRlLoading.setOnClickListener(this);
		mIvBalloon = (ImageView) layout.findViewById(R.id.iv_balloon);
		mIvLunzi = (ImageView) layout.findViewById(R.id.iv_lunzi);
		mRotateAnimation = new RotateAnimation(0, 720,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(LoadingLayout.ANIMATION_INTERPOLATOR);
		mRotateAnimation
				.setDuration(RotateLoadingLayout.ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);
		mRefreshBalloonAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.refreh_balloon_rotate_from_center);
		mRefreshBalloonAnimation.setFillAfter(true);
		mRefreshBalloonAnimation2 = AnimationUtils.loadAnimation(getActivity(),
				R.anim.refreh_balloon_rotate_from_left);
		mRefreshBalloonAnimation2.setFillAfter(true);
		mRefreshBalloonAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				mIvBalloon.startAnimation(mRefreshBalloonAnimation2);
			}
		});
		mIvBalloon.startAnimation(mRefreshBalloonAnimation);
		mIvLunzi.startAnimation(mRotateAnimation);
		mRlLoading.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mRlLoading.getVisibility() == View.VISIBLE) {
					hidePageLoading();
				}
			}
		}, 60 * 1000);
	}

	private void getData(int currentPage) {
		mIsLoading = true;
		mAction.requestData(this, DATATAGETKEY, currentPage, COUNT_PER_PAGE);
		if (!checkNetwork()) {
			showNoDataLayout();
			return;
		}
		if (mIsFirstIn) {
			showPageLoading();
			mIsFirstIn = false;
		}
	}

	private boolean checkNetwork() {
		try {
			if (AndroidUtils.getNetworkType(getActivity()) == Constants.NET_UNABLE) {
				showErrorToast(mParentLayout);
				hidePageLoading();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 显示页面loading
	 */
	public void showPageLoading() {
		mRlLoading.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏页面loading
	 */
	public void hidePageLoading() {
		mRlLoading.setVisibility(View.GONE);
	}

	@Override
	public View getCustomToastParentView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int setContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.zhiwu_favor_fragment;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	public void showNoDataLayout() {
		if (getActivity() != null) {
			if (mWaterFallView.getAdapter().getCount() == 0) {
				((MyFavoritesActivity) getActivity()).getTitleBar()
						.setRightBtnVisible(false);
			} else {
				((MyFavoritesActivity) getActivity()).getTitleBar()
						.setRightBtnVisible(true);
			}
		}
		mRefreshScrollView.hideFootview();
		if (mHistoryNoZhiwuLayout != null) {
			mHistoryNoZhiwuLayout.setVisibility(View.VISIBLE);
			return;
		}
		initNoDataLayoutViews();
	}

	public void hideNoDataLayout() {
		if (mHistoryNoZhiwuLayout != null) {
			mHistoryNoZhiwuLayout.setVisibility(View.GONE);
		}
	}

	private void initNoDataLayoutViews() {
		mHistoryNoZhiwuLayout = (RelativeLayout) mParentLayout
				.findViewById(R.id.history_no_zhiwu_layout);
		RelativeLayout above = (RelativeLayout) mHistoryNoZhiwuLayout
				.findViewById(R.id.above_layout);
		above.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((BaseFragmentActivity) getSelfContext())
						.isFastDoubleClick()) {
					return;
				}
				if (getActivity() != null) {
					getActivity().setResult(
							Constants.ActivityResultCode.RESULT_CODE_STORY);
					AndroidUtils.finishActivity(getActivity());
				}
			}
		});
		if (getActivity() != null) {
			TextView mMessageTv = (TextView) mHistoryNoZhiwuLayout
					.findViewById(R.id.history_message);
			mMessageTv.setText(getActivity().getString(
					R.string.comments_advertisement));
			mHistoryNoZhiwuLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onSucceed(String businessType, boolean isCache, Object session) {
		// TODO Auto-generated method stub
		super.onSucceed(businessType, isCache, session);
		// Log.i("mmmmmm", "success:" + businessType);
		hidePageLoading();
		mRefreshScrollView.onRefreshComplete();
		mIsLoading = false;
		JSONObject rebateInfo = mSelfData
				.getJSONObject(HttpConstants.Data.ZhiwuFavorList.ZHIWUFAVOR_LIST_INFO_JO);
		if (rebateInfo == null) {
			showNoDataLayout();
			return;
		}
		try {
			mListData = rebateInfo.getJSONArray(HttpConstants.Response.LIST_JA);
			mAdapter.setData(mListData);
			mAdapter.setClickList(getClickLick(mListData));
			mAdapter.notifyDataSetChanged();
			mIsHasNextPage = rebateInfo
					.getBoolean(HttpConstants.Response.HASNEXT);
			mCurrentPage = rebateInfo.getInt(HttpConstants.Response.CURPAGE_I);
			if (mListData == null || mListData.length() == 0) {
				showNoDataLayout();
			} else {
				hideNoDataLayout();
			}
			if (getActivity() != null) {
				if (mWaterFallView.getAdapter().getCount() == 0) {
					((MyFavoritesActivity) getActivity()).getTitleBar()
							.setRightBtnVisible(false);
				} else {
					((MyFavoritesActivity) getActivity()).getTitleBar()
							.setRightBtnVisible(true);
				}
			}
			if (mIsHasNextPage) {
				mRefreshScrollView.hideNoMoreText();
			} else {
				mRefreshScrollView.hideFootview();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			if (mCurrentPage == 1) {
				showNoDataLayout();
			}
		}
	}

	private List<String> getClickLick(JSONArray jsonArray) {
		mClickList.clear();
		for (int i = 0; i < jsonArray.length(); i++) {
			String id = jsonArray.optJSONObject(i).optString(
					HttpConstants.Data.NewsList.ID_S);
			if (mPreference.contains("cl_" + id)) {
				mClickList.add(id);
			}
		}
		return mClickList;
	}

	@Override
	public void onErrorResult(String businessType, String errorOn,
			String errorInfo, Object session) {
		// TODO Auto-generated method stub
		super.onErrorResult(businessType, errorOn, errorInfo, session);
		// Log.i("mmmmmm", "onErrorResult:" + businessType);
		mRefreshScrollView.onRefreshComplete();
		mIsLoading = false;
		hidePageLoading();
		if (mAdapter.getCount() == 0) {
			showNoDataLayout();
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		pullDownToRefresh();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		pullUpToRefresh();
	}

	public void pullDownToRefresh() {
		if (mIsLoading) {
			return;
		}
		mCurrentPage = 1;
		mIsLoading = true;
		hideNoDataLayout();
		getData(mCurrentPage);
	}

	private void pullUpToRefresh() {
		if (mIsLoading) {
			return;
		}
		if (mIsHasNextPage) {
			getData(mCurrentPage + 1);
			hideNoDataLayout();
		} else {
			resetPullRefreshUi();
			if (mCurrentPage == 1) {
				mRefreshScrollView.hideFootview();
			} else {
				mRefreshScrollView.showNoMoreText();
			}
		}
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void resetPullRefreshUi() {
		mRefreshScrollView.postDelayed(new Runnable() {

			@Override
			public void run() {
				mRefreshScrollView.onRefreshComplete();
			}
		}, 1000);
	}

	@Override
	public Context getSelfContext() {
		// TODO Auto-generated method stub
		return super.getSelfContext();
	}

	public void showProgress() {
		mLoadingBar.setVisibility(View.VISIBLE);
	}

	public void hideProgress() {
		mLoadingBar.setVisibility(View.GONE);
	}
}
