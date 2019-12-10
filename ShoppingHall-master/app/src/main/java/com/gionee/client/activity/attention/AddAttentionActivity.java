package com.gionee.client.activity.attention;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.adapter.AddChannelAdapter;
import com.gionee.client.view.widget.TabPageIndicator;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.page.PageCacheManager;

public class AddAttentionActivity extends BaseFragmentActivity implements
		OnClickListener {

	private static final String TAG = "Add_Attention";
	private StickyListHeadersListView mExpandableListView;
	private EditText mEditText;
	private ProgressBar mProgressBar;
	private AddChannelAdapter mAdapter;
	private List<List<JSONObject>> mChannels = new ArrayList<List<JSONObject>>();
	private List<String> mCategory = new ArrayList<String>();
	private RelativeLayout mTabLayout;
	private TabPageIndicator mTabPageIndicator;
	private ViewPager mViewPager;
	private List<AddAttentionFragment> mAttentionFragmentList;
	private ImageView mSlideRightBtn;
	private float mAllScroll = 0;
	// 0表示当前显示的关注列表，1表示显示搜索的列表
	public int mTag = 0;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.add_attention);
		initView();
		requestData();
		AndroidUtils.setMiuiTopMargain(this, findViewById(R.id.add_title));
	}

	private void initView() {
		mTag = 0;
		mAttentionFragmentList = new ArrayList<AddAttentionFragment>();
		GNImageLoader.getInstance().init(this);
		mSlideRightBtn = (ImageView) findViewById(R.id.slide_right_btn);
		mExpandableListView = (StickyListHeadersListView) findViewById(R.id.channels_list);
		mEditText = (EditText) findViewById(R.id.add_edit_text);
		mProgressBar = (ProgressBar) findViewById(R.id.add_loading_bar);
		mTabLayout = (RelativeLayout) findViewById(R.id.tab_layout);
		mAdapter = new AddChannelAdapter(this);
		mExpandableListView.setAdapter(mAdapter);
		// setExpandableListViewListener();
		setEditTextListener();
		findViewById(R.id.slide_right_btn).setOnClickListener(this);
		mExpandableListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					JSONObject itemJson = (JSONObject) mAdapter.getItem(arg2);
					gotoWebViewPage(itemJson);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void gotoWebViewPage(final JSONObject iconJson) {
		if (iconJson == null) {
			return;
		}

		gotoWebPage(
				iconJson.optString(HttpConstants.Response.MyAttentionChannel.LINK_S),
				true);
		StatService.onEvent(AddAttentionActivity.this,
				BaiduStatConstants.PLATFORM_H5,
				iconJson.optString(HttpConstants.Response.AddChannel.NAME_S));
	}

	private void setPageAdapter() {
		try {
			FragmentStatePagerAdapter adapter = new AddAttentionPageAdapter(
					getSupportFragmentManager());
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(adapter);
			mTabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
			mTabPageIndicator.setViewPager(mViewPager);
			mTabPageIndicator.setVisibility(View.VISIBLE);
			mTabLayout.setVisibility(View.VISIBLE);
			mViewPager.setOnPageChangeListener(mPageChangeListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {

			mTabPageIndicator.setCurrentItem(arg0);
			if (arg0 == mChannels.size() - 1) {
				mSlideRightBtn.setVisibility(View.GONE);
			} else {
				mSlideRightBtn.setVisibility(View.VISIBLE);
			}
			StatService.onEvent(AddAttentionActivity.this,
					BaiduStatConstants.CHOOSE_CATEGORY, mCategory.get(arg0));

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			mAllScroll += (arg0 + arg1 + arg2);
			if (mAllScroll == 0 && mRlLoading.getVisibility() == View.GONE) {
				StatService.onEvent(AddAttentionActivity.this, "gesture_back",
						"gesture_back");
				onBackPressed();
				AndroidUtils.exitActvityAnim(AddAttentionActivity.this);
			} else if (arg0 + arg1 + arg2 == 0) {
				mAllScroll = 0;
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gionee.client.activity.base.BaseFragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			PageCacheManager.ClearPageData(this.getClass().getName());
			mTabPageIndicator.removeAllViews();
			mAttentionFragmentList.clear();
			mViewPager.removeAllViews();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setEditTextListener() {
		mEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					onClickSearch();
					if (mAdapter.getCount() == 0) {
						Toast.makeText(AddAttentionActivity.this,
								R.string.no_platform, Toast.LENGTH_SHORT)
								.show();
					}
				}
				return true;
			}

		});

		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				showSearchList();
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mEditText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				StatService.onEvent(AddAttentionActivity.this,
						BaiduStatConstants.ADD_SEARCH,
						BaiduStatConstants.ADD_SEARCH);
				return false;
			}
		});
	}

	class AddAttentionPageAdapter extends FragmentStatePagerAdapter {
		public AddAttentionPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			LogUtils.log(TAG, position + "");
			return mAttentionFragmentList.get(position
					% mAttentionFragmentList.size());
		}

		@Override
		public CharSequence getPageTitle(int position) {
			LogUtils.log(TAG, mCategory.get(position));
			return mCategory.get(position % mCategory.size());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.FragmentStatePagerAdapter#destroyItem(android
		 * .view.ViewGroup, int, java.lang.Object)
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			try {
				super.destroyItem(container, position, object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public int getCount() {
			return mAttentionFragmentList.size();
		}
	}

	// private void setExpandableListViewListener() {
	// mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {
	// @Override
	// public boolean onGroupClick(ExpandableListView parent, View v, int
	// groupPosition, long id) {
	// return true;
	// }
	// });
	// }

	private void onClickSearch() {
		LogUtils.log(TAG, LogUtils.getFunctionName());
		String keyWord = mEditText.getText().toString().replaceAll(" ", "");
		if (TextUtils.isEmpty(keyWord)) {
			Toast.makeText(this, R.string.input_search_contents,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (mCategory.size() < 1) {
			Toast.makeText(this, R.string.no_platform, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		filterData(keyWord);
		mEditText.postDelayed(new Runnable() {

			@Override
			public void run() {
				AndroidUtils.hidenKeybord(AddAttentionActivity.this, mEditText);
			}
		}, 200);

	}

	private void showSearchList() {
		mTabLayout.setVisibility(View.GONE);
		mExpandableListView.setVisibility(View.VISIBLE);
		mTag = 1;
	}

	private void showTabLayout() {
		mTabLayout.setVisibility(View.VISIBLE);
		mExpandableListView.setVisibility(View.GONE);
		mTag = 0;
	}

	private void filterData(String keyWord) {
		if (TextUtils.isEmpty(keyWord)) {
			showTabLayout();
			return;
		}
		List<List<JSONObject>> item = new ArrayList<List<JSONObject>>();
		List<String> sort = new ArrayList<String>();
		if (TextUtils.isEmpty(keyWord)) {
			item = mChannels;
			sort = mCategory;
		} else {
			for (int parent = 0; parent < mCategory.size(); parent++) {
				List<JSONObject> searchItem = getChildrenItem(keyWord, parent);
				if (searchItem.size() > 0) {
					sort.add(mCategory.get(parent));
					item.add(searchItem);
				}
			}
		}
		setAdapter(item, sort);
	}

	private List<JSONObject> getChildrenItem(String keyWord, int parent) {
		List<JSONObject> searchItem = new ArrayList<JSONObject>();
		for (int child = 0; child < mChannels.get(parent).size(); child++) {
			if (isContains(keyWord, parent, child)) {
				searchItem.add(mChannels.get(parent).get(child));
			}
		}
		return searchItem;
	}

	private boolean isContains(String key, int parent, int children) {
		return mChannels.get(parent).get(children)
				.optString(HttpConstants.Response.AddChannel.NAME_S)
				.contains((key));
	}

	private void requestData() {
		RequestAction action = new RequestAction();
		mProgressBar.setVisibility(View.VISIBLE);
		action.getChannlList(this, HttpConstants.Data.AddAttention.CHANNEL_JO);
	}

	@Override
	public void onSucceed(String businessType, boolean isCache, Object session) {
		super.onSucceed(businessType, isCache, session);
		mProgressBar.setVisibility(View.GONE);
		JSONObject jsonObject = mSelfData
				.getJSONObject(HttpConstants.Data.AddAttention.CHANNEL_JO);
		if (mAttentionFragmentList != null && mAttentionFragmentList.size() > 0) {
			return;
		}
		if (jsonObject != null) {
			resolveData(jsonObject);
		}

	}

	private void resolveData(JSONObject jsonObject) {

		JSONArray jsonArray = jsonObject
				.optJSONArray(HttpConstants.Response.AddChannel.CHANNEL_JA);
		clearList();
		if (jsonArray == null) {
			return;
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject tmpJs = jsonArray.optJSONObject(i);
			String title = tmpJs
					.optString(HttpConstants.Response.AddChannel.CHANNEL_TYPE_JA);
			if (!TextUtils.isEmpty(title)) {
				mCategory.add(title);
				resolveItemData(
						tmpJs.optJSONArray(HttpConstants.Response.AddChannel.CHANNEL_JA),
						mChannels);
			}
		}
		setPageAdapter();
		setAdapter(mChannels, mCategory);
	}

	private void clearList() {
		mCategory.clear();
		mChannels.clear();
		mAttentionFragmentList.clear();
	}

	private void setAdapter(List<List<JSONObject>> children, List<String> parent) {
		mAdapter.updataData(parent, children);
		mAdapter.notifyDataSetChanged();
		// for (int i = 0; i < parent.size(); i++) {
		// mExpandableListView.collapseGroup(i);
		// mExpandableListView.expandGroup(i);
		// }
	}

	private void resolveItemData(JSONArray optJSONArray,
			List<List<JSONObject>> items) {
		try {
			List<JSONObject> item = new ArrayList<JSONObject>();
			for (int i = 0; i < optJSONArray.length(); i++) {
				item.add(optJSONArray.optJSONObject(i));
			}
			AddAttentionFragment fragment = AddAttentionFragment
					.newInstance(item);
			mAttentionFragmentList.add(fragment);
			items.add(item);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onErrorResult(String businessType, String errorOn,
			String errorInfo, Object session) {
		super.onErrorResult(businessType, errorOn, errorInfo, session);
		mProgressBar.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_search:
			onClickSearch();
			if (mAdapter.getCount() == 0) {
				Toast.makeText(this, R.string.no_platform, Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.iv_back:
			onBackPressed();
			break;
		case R.id.slide_right_btn:
			slideToRight();
			break;
		default:
			break;
		}
	}

	public void slideToRight() {
		try {
			int mNextItem = mViewPager.getCurrentItem() < mChannels.size() ? mViewPager
					.getCurrentItem() + 1 : mViewPager.getCurrentItem();
			mViewPager.setCurrentItem(mNextItem, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void finishActivity() {

		if (AndroidUtils.hideInputMethod(AddAttentionActivity.this)) {
			AndroidUtils.hidenKeybord(this, mEditText);
		}
		setResult(Constants.Home.HOME_RESULT_CODE);
		finish();
		AndroidUtils.exitActvityAnim(this);
	}

	@Override
	public void onBackPressed() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		if (mExpandableListView.isShown()) {
			showTabLayout();
			return;
		}
		setResult(Constants.Home.HOME_RESULT_CODE);
		finish();
		AndroidUtils.exitActvityAnim(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

}
