package com.gionee.client.activity.tabFragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.activity.comments.NewsFragment;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.widget.TabPageIndicator;
import com.gionee.framework.operation.page.PageCacheManager;

/**
 * 知物首页 只有导航条 内容在 NewsFragment 中
 * 
 * @author heshicaihao 2015-10-26
 * 
 */
public class CommentsFragment extends BaseFragment implements OnClickListener {

	private static final String TAG = "CommentFragment";

	private View mView;
	private ViewPager mContentPager;
	private ImageView mSlideRightBtn;
	private RelativeLayout mTabLayout;
	private List<NewsFragment> mNewsFragmentList;
	private TabPageIndicator mTabPageIndicator;

	private List<String> mTitelIds = new ArrayList<String>();
	private List<String> mTitelNames = new ArrayList<String>();

	private MyPagerAdapter mAdapter;
	private View mNoDataLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtils.logd(TAG, LogUtils.getThreadName());
		mView = inflater.inflate(R.layout.comment_list, container, false);
		init();
		requesData();
		if (AndroidUtils.translateTopBar(getActivity())) {
			mView.findViewById(R.id.top_title_view).setVisibility(View.VISIBLE);
		}
		return mView;
	}

	public void refresh() {
		requesData();
	}

	public void refresh(Intent data) {
		LogUtils.log(TAG, LogUtils.getThreadName() + data);
		int index = mContentPager.getCurrentItem();
		mNewsFragmentList.get(index).refreshData(data);
	}

	private void initNoDataLayoutViews(View view) {
		LogUtils.logd(TAG, LogUtils.getThreadName()
				+ " custruct mNoBargainLayout");
		ViewStub stub = (ViewStub) view.findViewById(R.id.no_comment_layout);
		if (stub == null) {
			LogUtils.logd(TAG, LogUtils.getThreadName() + " stub = null");
			return;
		}
		mNoDataLayout = stub.inflate();
		RelativeLayout above = (RelativeLayout) mNoDataLayout
				.findViewById(R.id.above_layout);
		above.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogUtils.log(TAG, LogUtils.getThreadName());
				requesData();
			}
		});
		TextView mMessageTv = (TextView) mNoDataLayout
				.findViewById(R.id.message);
		mMessageTv.setText(R.string.no_comment);
		mNoDataLayout.setVisibility(View.VISIBLE);
	}

	private void init() {
		mNewsFragmentList = new ArrayList<NewsFragment>();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.slide_right_btn:
			slideToRight();
			break;
		default:
			break;
		}
	}

	public void slideToRight() {
		try {
			int mNextItem = mContentPager.getCurrentItem() < mTitelIds.size() ? (mContentPager
					.getCurrentItem() + 1) : mContentPager.getCurrentItem();
			mContentPager.setCurrentItem(mNextItem, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 设置滑动监听
	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			mTabPageIndicator.setCurrentItem(arg0);
			if (arg0 == mTitelIds.size() - 1) {
				mSlideRightBtn.setVisibility(View.INVISIBLE);
			} else {
				setSlideRightBtnVisible();
			}
			StatService.onEvent(getActivity(), "tabopen", mTitelIds.get(arg0));
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	// 导航条的适配器
	private class MyPagerAdapter extends FragmentStatePagerAdapter {

		private List<String> titel_ids;
		private List<String> titel_names;
		private final FragmentManager mFragmentManager;
		private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
		private FragmentTransaction mCurTransaction = null;

		public MyPagerAdapter(FragmentManager fm, List<String> titel_ids,
				List<String> titel_names) {
			super(fm);
			this.titel_ids = titel_ids;
			this.titel_names = titel_names;
			this.mFragmentManager = fm;
		}

		@Override
		public Fragment getItem(int position) {
			return mNewsFragmentList.get(position % mNewsFragmentList.size());
		}

		@Override
		public int getCount() {
			return mNewsFragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (titel_names == null) {
				return "";
			}
			return titel_names.get(position);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			if (mNewsFragmentList.size() > position) {
				Fragment f = mNewsFragmentList.get(position);
				if (f != null) {
					return f;
				}
			}
			if (mCurTransaction == null) {
				mCurTransaction = mFragmentManager.beginTransaction();
			}
			Fragment fragment = getItem(position);
			if (mSavedState.size() > position) {
				Fragment.SavedState fss = mSavedState.get(position);
				if (fss != null) {
					fragment.setInitialSavedState(fss);
				}
			}
			while (mNewsFragmentList.size() <= position) {
				mNewsFragmentList.add(null);
			}
			mNewsFragmentList.add(position, (NewsFragment) fragment);
			mCurTransaction.add(container.getId(), fragment);
			return fragment;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			Fragment fragment = (Fragment) object;

			if (mCurTransaction == null) {
				mCurTransaction = mFragmentManager.beginTransaction();
			}
			while (mSavedState.size() <= position) {
				mSavedState.add(null);
			}
			mSavedState.set(position,
					mFragmentManager.saveFragmentInstanceState(fragment));
			mNewsFragmentList.set(position, null);
			mCurTransaction.remove(fragment);
		}

		@Override
		public void finishUpdate(View container) {
			if (mCurTransaction != null) {
				mCurTransaction.commit();
				mCurTransaction = null;
				mFragmentManager.executePendingTransactions();
			}
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return ((Fragment) object).getView() == view;
		}

	}

	@Override
	public View getCustomToastParentView() {
		return mContentView;
	}

	@Override
	protected int setContentViewId() {
		return 0;
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			PageCacheManager.ClearPageData(this.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSucceed(String businessType, boolean isCache, Object session) {
		super.onSucceed(businessType, isCache, session);
		LogUtils.log(TAG, LogUtils.getFunctionName());
		update(businessType);
		hideLoading();
		updateNoDataView();
	}

	@Override
	public void onErrorResult(String businessType, String errorOn,
			String errorInfo, Object session) {
		super.onErrorResult(businessType, errorOn, errorInfo, session);
		updateNoDataView();
		hideLoading();
	}

	private void updateNoDataView() {
		LogUtils.log(TAG, LogUtils.getFunctionName());
		if (mTitelNames.size() < 1) {
			showNoDataLayout();
		} else {
			hideNoDataView();
		}
	}

	private void showNoDataLayout() {
		if (mNoDataLayout == null) {
			initNoDataLayoutViews(mView);
		}
	}

	private void hideNoDataView() {
		if (mNoDataLayout != null) {
			mNoDataLayout.setVisibility(View.GONE);
		}
	}

	private void requesData() {
		LogUtils.log(TAG, LogUtils.getFunctionName());
		RequestAction action = new RequestAction();
		action.getCommentsListNewTitel(this,
				HttpConstants.Data.CommentsList.COMMENTS_TITLE);
		if (checkNetworkNotVisiviblle()) {
			showErrorToastMarginTop(mView,
					AndroidUtils.dip2px(getActivity(), 25));
			return;
		}
		if (mTitelIds.size() < 1) {
			hideNoDataView();
			showProgressBar();
		}

	}

	private void showProgressBar() {
		showLoading();
	}

	private void update(String businessType) {
		if (businessType.equals(Url.COMMENTS_TITEL_INFO)) {
			bindTitelData();
			if (isFirstBoot()) {
				hideLoading();
				resetFistBoot();
			}
		}
		updateNoDataView();
	}

	/**
	 * 
	 * @author heshicaihao
	 * @description TODO
	 */
	private void bindTitelData() {
		try {
			JSONObject titelJson = mSelfData
					.getJSONObject(HttpConstants.Data.CommentsList.COMMENTS_TITLE);
			LogUtils.log(TAG, "advertiseJson:  " + titelJson);

			if (titelJson != null) {
				bindTitelList(titelJson);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author heshicaihao
	 * @description TODO
	 */
	private void bindTitelList(JSONObject titelJson) {
		LogUtils.log(TAG, LogUtils.getFunctionName());
		try {
			JSONArray titel_list = titelJson
					.optJSONArray(HttpConstants.Response.CommentsTitel.TITEL_LIST);
			LogUtils.log(TAG, "titel_list:  " + titel_list);
			if (mNewsFragmentList.size() > 1) {
				LogUtils.log(TAG, "get new data,but page is not empty!");
				return;
			}
			resolve(titel_list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void resolve(JSONArray titel_list) {
		for (int i = 0; i < titel_list.length(); i++) {
			String titel_list_id = titel_list.optJSONObject(i).optString(
					HttpConstants.Response.CommentsTitel.TITEL_ID);
			String titel_list_name = titel_list.optJSONObject(i).optString(
					HttpConstants.Response.CommentsTitel.TITEL_NAME);
			if (!TextUtils.isEmpty(titel_list_id)
					&& !TextUtils.isEmpty(titel_list_name)) {
				mTitelIds.add(titel_list_id);
				mTitelNames.add(titel_list_name);
				NewsFragment fragment = new NewsFragment(titel_list_id);
				mNewsFragmentList.add(fragment);
			}
		}
		if (mNewsFragmentList.size() > 0) {
			setPageAdapter();
		}
	}

	private void setPageAdapter() {
		mAdapter = new MyPagerAdapter(this.getChildFragmentManager(),
				mTitelIds, mTitelNames);
		mTabPageIndicator = (TabPageIndicator) mView.findViewById(R.id.tabs);
		mContentPager = (ViewPager) mView.findViewById(R.id.content_pager);
		mSlideRightBtn = (ImageView) mView.findViewById(R.id.slide_right_btn);
		mTabLayout = (RelativeLayout) mView.findViewById(R.id.tab_layout);
		mTabLayout.setVisibility(View.VISIBLE);
		mContentPager.setAdapter(mAdapter);
		mTabPageIndicator.setViewPager(mContentPager);
		mTabPageIndicator.setVisibility(View.VISIBLE);
		setSlideRightBtnVisible();
		mSlideRightBtn.setOnClickListener(this);
		mContentPager.setOnPageChangeListener(mPageChangeListener);
		mContentPager.setOffscreenPageLimit(3);
	}

	private void setSlideRightBtnVisible() {
//		if (mNewsFragmentList.size() > 4) {
			mSlideRightBtn.setVisibility(View.VISIBLE);
//		}
	}

}