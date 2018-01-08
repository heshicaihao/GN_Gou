// Gionee <yangxiong><2014-8-6> add for CR00850885 begin
package com.gionee.client.activity.myfavorites;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.shareTool.ShareTool;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.adapter.AbstractListBaseAdapter;
import com.gionee.client.view.adapter.AbstractMyfavoriteBaseAdapter;
import com.gionee.client.view.adapter.AbstractMyfavoriteBaseAdapter.FavoriteMode;
import com.gionee.client.view.adapter.CommentsAdapter;
import com.gionee.client.view.shoppingmall.AbstractBaseList;
import com.gionee.client.view.shoppingmall.AbstractMyFavoriteBaseList;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.shoppingmall.GNTitleBar.OnRightBtnListener;
import com.gionee.client.view.widget.TabViewPager;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.net.GNImageLoader;
import com.huewu.pla.MultiColumnListView;

/**
 * @author yangxiong <br/>
 * @description TODO 我的收藏
 */
public class MyFavoritesActivity extends BaseFragmentActivity implements
		OnClickListener, OnCheckedChangeListener,
		ViewPager.OnPageChangeListener {
	private static final String TAG = "MyFavoritesActivity";
	private static final int DEFAULT_OFFSCREEN_PAGES = 1;
	private TabViewPager mViewPager;
	private RadioGroup mTabBarRadio;
	private RelativeLayout mMultiSelectLayout;
	private TabsAdapter mTabsAdapter;
	private Class<?>[] mFragmentClassesArray = new Class[] {
			ShoppingFragment.class, ZhiwuFavorFragment.class };
	protected String mDescription = "";
	private int mCurFragmentIndex;
	private float mAllScroll = 0;
	private FavoriteMode mFavoriteMode = FavoriteMode.NORMAL;
	private Button mDeleteBtn;
	private CheckBox mAllSelectCheckBox;
	private int mDeleteItemsCount;
	private OnSingleSelectCheckBoxListener mDeleteListener = new OnSingleSelectCheckBoxListener() {
		@Override
		public void onChange(int count) {
			mDeleteItemsCount = count;
			if (count > 0) {
				mDeleteBtn.setText(getString(R.string.delete_include_count,
						count));
				mDeleteBtn.setTextColor(getResources().getColor(
						R.color.tab_text_color_sel));
				mDeleteBtn.setClickable(true);
			} else {
				mDeleteBtn.setText(getString(R.string.delete));
				mDeleteBtn.setTextColor(getResources().getColor(
						R.color.delete_normal_color));
				mDeleteBtn.setClickable(false);
			}
			Fragment fragment = getCurrentFragement(mCurFragmentIndex);
			if (fragment instanceof MyFavoritesBaseFragment) {
				AbstractBaseList listViewLayout = (AbstractBaseList) ((MyFavoritesBaseFragment) fragment)
						.getCustomToastParentView();
				AbstractListBaseAdapter adapater = listViewLayout
						.getListBaseAdapter();
				if (count < adapater.getCount()) {
					mAllSelectCheckBox.setChecked(false);
				}
				if (count == adapater.getCount()) {
					mAllSelectCheckBox.setChecked(true);
				}
			} else if (fragment instanceof ZhiwuFavorFragment) {
				MultiColumnListView multiColumnListView = ((ZhiwuFavorFragment) fragment)
						.getMultiColumnListView();
				if (count < multiColumnListView.getAdapter().getCount()) {
					mAllSelectCheckBox.setChecked(false);
				} else {
					mAllSelectCheckBox.setChecked(true);
				}
			}
		}
	};

	public OnSingleSelectCheckBoxListener getSingleSelectDeleteListener() {
		return mDeleteListener;
	}

	public void showMultiSelectBottomBar(int visibility) {
		mMultiSelectLayout.setVisibility(visibility);
		switch (visibility) {
		case View.VISIBLE:
			// multiSelectionAnimationIn();
			break;
		case View.GONE:
			// multiSelectionAnimationOut();
			break;
		default:
			break;
		}
	}

	public int getCurrentFragmentIndex() {
		return mCurFragmentIndex;
	}

	public BaseFragment getCurrentFragment() {
		return (BaseFragment) getCurrentFragement(mCurFragmentIndex);
	}

	@Override
	public void onClick(View v) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		MyBean bean = (MyBean) v.getTag();
		String type = isShoppingType(bean) == true ? BaiduStatConstants.SHOPPING_MORE
				: BaiduStatConstants.TALE_MORE;
		switch (v.getId()) {
		case R.id.share_btn:
			StatService.onEvent(MyFavoritesActivity.this,
					BaiduStatConstants.FAVORITE_TOOLS,
					BaiduStatConstants.SHARE_BTN);
			showShareDialog(v, this);
			break;
		case R.id.share_friends:
			shareToWeixin(true, bean.getString("title"),
					bean.getString("description"), (Bitmap) bean.get("thump"),
					bean.getString("url"));
			closeShareDialog();
			StatService.onEvent(this, type, BaiduStatConstants.FRIENDS);
			if (ShareTool.isWXInstalled(this)) {
				cumulateAppLinkScore();
			}
			break;
		case R.id.share_weixin:
			shareToWeixin(false, bean.getString("title"),
					bean.getString("description"), (Bitmap) bean.get("thump"),
					bean.getString("url"));
			closeShareDialog();
			StatService.onEvent(this, type, BaiduStatConstants.WEIXIN);
			if (ShareTool.isWXInstalled(this)) {
				cumulateAppLinkScore();
			}
			break;
		case R.id.share_weibo:
			// Toast.makeText(this, R.string.booting_pls_waiting,
			// Toast.LENGTH_SHORT).show();
			shareToWeibo(bean.getString("title"),
					bean.getString("description"), (Bitmap) bean.get("thump"),
					bean.getString("url"));
			closeShareDialog();
			StatService.onEvent(this, type, BaiduStatConstants.WEIBO);
			if (isWeiboValid()) {
				cumulateAppLinkScore();
			}
			break;
		case R.id.share_qq_friend:
			shareToQq(ShareTool.PLATFORM_QQ_FRIEND, bean.getString("title"),
					bean.getString("description"), bean.getString("imageUrl"),
					bean.getString("url"));
			closeShareDialog();
			StatService.onEvent(this, type, BaiduStatConstants.QQ_FRIEND);
			if (ShareTool.isQQValid(this)) {
				cumulateAppLinkScore();
			}
			break;
		case R.id.share_qq_zone:
			shareToQq(ShareTool.PLATFORM_QQ_ZONE, bean.getString("title"),
					bean.getString("description"), bean.getString("imageUrl"),
					bean.getString("url"));
			closeShareDialog();
			StatService.onEvent(this, type, BaiduStatConstants.QQ_ZONE);
			if (ShareTool.isQQValid(this)) {
				cumulateAppLinkScore();
			}
			break;
		default:
			break;
		}
	}

	private boolean isShoppingType(MyBean bean) {
		return bean.getInt(HttpConstants.Data.BaseMyFavoriteList.TYPE) > 1;
	}

	@Override
	public void onBackPressed() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onBackPressed();
		AndroidUtils.finishActivity(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			LogUtils.log(TAG, LogUtils.getThreadName());
			Fragment fragment = getCurrentFragment();
			if (fragment instanceof MyFavoritesBaseFragment) {
				if (mFavoriteMode == FavoriteMode.MULTI_SELECT_DELETE) {
					processModeSwitch();
					return true;
				}
			} else if (fragment instanceof ZhiwuFavorFragment) {
				MultiColumnListView multiColumnListView = ((ZhiwuFavorFragment) fragment)
						.getMultiColumnListView();
				ImageGridAdapter adapater = (ImageGridAdapter) multiColumnListView
						.getAdapter();
				if (adapater.getmIsEditState()) {
					processModeSwitch();
					return true;
				}
			}
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		RadioButton radioButton = (RadioButton) findViewById(arg1);
		Object tag = radioButton.getTag();
		int size = mTabsAdapter.getCount();
		for (int i = 0; i < size; i++) {
			if (mTabsAdapter.getTabInfo(i) == tag) {
				mViewPager.setCurrentItem(i, false);
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// Log.i("hhhhhhhh", arg0 + "  ");
		if (arg0 == 0 && mAllScroll == 0
				&& mRlLoading.getVisibility() == View.GONE) {
			StatService.onEvent(this, "gesture_back", "gesture_back");
			onBackPressed();
			AndroidUtils.exitActvityAnim(this);
			return;
		}
		mAllScroll = 0;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		mAllScroll += (arg0 + arg1 + arg2);
	}

	@Override
	public void onPageSelected(int arg0) {
		mCurFragmentIndex = arg0;
		int childCount = mTabBarRadio.getChildCount();
		LogUtils.log(TAG, LogUtils.getThreadName() + " page: " + arg0
				+ ", childCount: " + childCount);
		for (int i = 0; i < childCount; i++) {
			View view = mTabBarRadio.getChildAt(i);
			if (view instanceof RadioButton) {
				Object tag = view.getTag();
				if (mTabsAdapter.getTabInfo(arg0) == tag) {
					((RadioButton) view).setChecked(true);
				}
			}
		}

		Fragment fragment = getCurrentFragment();
		if (fragment instanceof MyFavoritesBaseFragment) {
			AbstractMyFavoriteBaseList listViewLayout = (AbstractMyFavoriteBaseList) ((MyFavoritesBaseFragment) fragment)
					.getCustomToastParentView();
			listViewLayout.updateTitleBarRightBtn();
		} else if (fragment instanceof ZhiwuFavorFragment) {
			MultiColumnListView multiColumnListView = ((ZhiwuFavorFragment) fragment)
					.getMultiColumnListView();
			if (multiColumnListView.getAdapter().getCount() == 0) {
				getTitleBar().setRightBtnVisible(false);
			} else {
				getTitleBar().setRightBtnVisible(true);
			}
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onCreate(arg0);
		setContentView(R.layout.my_favourites);
		initview();
		processLowPriceGuide();
		ShareSDK.initSDK(this);
		GNImageLoader.getInstance().init(this);
	}

	@Override
	protected void onStop() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onStop();
		BaseFragment fragment = (BaseFragment) getCurrentFragement(mCurFragmentIndex);
		if (fragment != null) {
			if (fragment instanceof MyFavoritesBaseFragment) {
				fragment.onPageInvisible();
			}/*
			 * else if (fragment instanceof ZhiwuFavorFragment) {
			 * showMultiSelectBottomBar(View.GONE); }
			 */
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.ActivityRequestCode.REQUEST_CODE_COMMENT_DETAIL:
			BaseFragment fragment = (BaseFragment) getCurrentFragement(1);
			if (fragment != null && data != null
					&& fragment instanceof ZhiwuFavorFragment) {
				String commentscount = data.getStringExtra("comments_count");
				boolean isFavor = data.getBooleanExtra("isFavorite", true);
				int position = data.getIntExtra("position", 0);
				MultiColumnListView multiColumnListView = ((ZhiwuFavorFragment) fragment)
						.getMultiColumnListView();
				ImageGridAdapter adapater = (ImageGridAdapter) multiColumnListView
						.getAdapter();
				if (!isFavor) {
					adapater.removeFavor(position);
				}
				adapater.changeItemCommentCount(position,
						Integer.parseInt(commentscount));
			}
			break;

		default:
			break;
		}
	}

	private Fragment getCurrentFragement(int index) {
		BaseFragment fragement = null;
		try {
			fragement = (BaseFragment) getSupportFragmentManager()
					.findFragmentByTag(
							"android:switcher:" + R.id.view_pager + ":" + index);
		} catch (Exception e) {
			LogUtils.loge(TAG, LogUtils.getThreadName(), e);
		}
		return fragement;
	}

	private static class TabsAdapter extends FragmentPagerAdapter {
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		private WeakReference<Activity> mWeakReference;

		static final class TabInfo {
			private final Class<?> mClass;
			private final Bundle mArgs;

			TabInfo(Class<?> classes, Bundle args) {
				mClass = classes;
				mArgs = args;
			}
		}

		public TabsAdapter(FragmentActivity activity) {
			super(activity.getSupportFragmentManager());
			mWeakReference = new WeakReference<Activity>(activity);

		}

		public void addTab(RadioButton radioButton, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			radioButton.setTag(info);
			mTabs.add(info);
		}

		public TabInfo getTabInfo(int position) {
			return mTabs.get(position);
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mWeakReference.get(),
					info.mClass.getName(), info.mArgs);
		}
	}

	private void initview() {
		LogUtils.log(TAG, LogUtils.getThreadName() + " enter!");
		initTitleBar();
		showShadow(false);
		mViewPager = (TabViewPager) findViewById(R.id.view_pager);
		mViewPager.setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGES);
		mViewPager.postDelayed(new Runnable() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public void run() {
				LogUtils.log(TAG, LogUtils.getThreadName());
				initTabBar();
				mViewPager.setOnPageChangeListener(MyFavoritesActivity.this);
				mViewPager.setAdapter(mTabsAdapter);
				mViewPager.setCurrentItem(0);
			}
		}, 40);
		initMultiSelectDelete();
		LogUtils.log(TAG, LogUtils.getThreadName() + " leave!");
	}

	private void initTabBar() {
		mTabBarRadio = (RadioGroup) findViewById(R.id.tab_radio);
		mTabBarRadio.setOnCheckedChangeListener(MyFavoritesActivity.this);
		mTabsAdapter = new TabsAdapter(MyFavoritesActivity.this);
		int childCount = mTabBarRadio.getChildCount();
		int j = 0;
		for (int i = 0; i < childCount; i++) {
			final View view = mTabBarRadio.getChildAt(i);
			if (view instanceof RadioButton) {
				final int index = j;
				mTabsAdapter.addTab((RadioButton) view,
						mFragmentClassesArray[j++], getIntent().getExtras());
				view.setOnTouchListener(new OnTouchListener() {
					@SuppressLint("ClickableViewAccessibility")
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!mViewPager.getPagingEnabled()
								&& mCurFragmentIndex != index) {
							if (isFastDoubleClick()) {
								return false;
							}
							AndroidUtils.showShortToast(
									MyFavoritesActivity.this,
									R.string.pls_exit_edit);
							return true;
						}
						return false;
					}
				});
			}
		}
	}

	private void initTitleBar() {
		showTitleBar(true);
		final GNTitleBar titleBar = getTitleBar();
		titleBar.setTitle(R.string.my_favorites);
		titleBar.setRightBtnVisible(false);
		titleBar.setRightBtnText(R.string.edit);
		titleBar.setRightBtnTextColor(getResources().getColor(
				R.color.tab_text_color_nor));
		titleBar.setRightListener(new OnRightBtnListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick() {
				// go to edit mode.
				if (isFastDoubleClick()) {
					return;
				}
				processModeSwitch();
			}
		});
	}

	public void processModeSwitch() {
		final GNTitleBar titleBar = getTitleBar();
		Fragment fragment = getCurrentFragement(mCurFragmentIndex);
		if (fragment instanceof MyFavoritesBaseFragment) {
			AbstractBaseList listViewLayout = (AbstractBaseList) ((MyFavoritesBaseFragment) fragment)
					.getCustomToastParentView();
			AbstractListBaseAdapter adapater = listViewLayout
					.getListBaseAdapter();
			if (mFavoriteMode == FavoriteMode.NORMAL
					&& adapater.getCount() == 0) {
				AndroidUtils.showShortToast(this, R.string.cant_switch);
				return;
			}
			mFavoriteMode = mFavoriteMode == FavoriteMode.NORMAL ? FavoriteMode.MULTI_SELECT_DELETE
					: FavoriteMode.NORMAL;
			switch (mFavoriteMode) {
			case NORMAL:
				mViewPager.setPagingEnabled(true);
				titleBar.setRightBtnText(R.string.edit);
				restoreMultiSelectDeleteBar();
				showMultiSelectBottomBar(View.GONE);
				((AbstractMyfavoriteBaseAdapter) adapater)
						.setFavoriteMode(mFavoriteMode);
				break;
			case MULTI_SELECT_DELETE:
				mViewPager.setPagingEnabled(false);
				titleBar.setRightBtnText(R.string.cancel);
				showMultiSelectBottomBar(View.VISIBLE);
				((AbstractMyfavoriteBaseAdapter) adapater)
						.setFavoriteMode(mFavoriteMode);
				break;
			default:
				break;
			}
		} else if (fragment instanceof ZhiwuFavorFragment) {
			MultiColumnListView multiColumnListView = ((ZhiwuFavorFragment) fragment)
					.getMultiColumnListView();
			ImageGridAdapter adapater = (ImageGridAdapter) multiColumnListView
					.getAdapter();
			if (!adapater.getmIsEditState() && adapater.getCount() == 0) {
				AndroidUtils.showShortToast(this, R.string.cant_switch);
				return;
			}
			if (adapater.getmIsEditState()) {
				adapater.setmIsEditState(false);
			} else {
				adapater.setmIsEditState(true);
			}
			boolean isEditState = adapater.getmIsEditState();
			if (isEditState) {
				mViewPager.setPagingEnabled(false);
				titleBar.setRightBtnText(R.string.cancel);
				showMultiSelectBottomBar(View.VISIBLE);
				final Animation anim = AnimationUtils.loadAnimation(this,
						R.anim.push_down_in);
				anim.setFillAfter(true);
				mMultiSelectLayout.startAnimation(anim);
				((ZhiwuFavorFragment) fragment).setEditBgIsVisible(true);
			} else {
				mViewPager.setPagingEnabled(true);
				titleBar.setRightBtnText(R.string.edit);
				restoreMultiSelectDeleteBar();
				showMultiSelectBottomBar(View.GONE);
				((ZhiwuFavorFragment) fragment).setEditBgIsVisible(false);
			}
			((ZhiwuFavorFragment) fragment).hideProgress();
		}
	}

	public void switchToCommonMode() {
		if (mFavoriteMode == FavoriteMode.MULTI_SELECT_DELETE) {
			processModeSwitch();
		}
	}

	public boolean isMultiSelectDeleteCheckBoxChecked() {
		return mAllSelectCheckBox.isChecked();
	}

	private void restoreMultiSelectDeleteBar() {
		mAllSelectCheckBox.setChecked(false);
		mDeleteBtn.setClickable(false);
		mDeleteBtn.setText(getString(R.string.delete));
		mDeleteBtn.setTextColor(getResources().getColor(
				R.color.delete_normal_color));
	}

	private void initMultiSelectDelete() {
		mMultiSelectLayout = (RelativeLayout) findViewById(R.id.all_select_layout);
		mAllSelectCheckBox = (CheckBox) mMultiSelectLayout
				.findViewById(R.id.all_select_checkbox);
		if (AndroidUtils.getAndroidSDKVersion() <= 16) {
			LogUtils.logd(TAG, LogUtils.getThreadName() + " sdk version <= 16");
			mAllSelectCheckBox.setPadding(40, 0, 0, 0);
		}
		mAllSelectCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment = getCurrentFragement(mCurFragmentIndex);
				if (fragment instanceof MyFavoritesBaseFragment) {
					AbstractBaseList listViewLayout = (AbstractBaseList) ((MyFavoritesBaseFragment) fragment)
							.getCustomToastParentView();
					AbstractListBaseAdapter adapater = listViewLayout
							.getListBaseAdapter();
					if (mAllSelectCheckBox.isChecked()) {
						((AbstractMyfavoriteBaseAdapter) adapater)
								.setAllSelected();
					} else {
						((AbstractMyfavoriteBaseAdapter) adapater)
								.clearAllSingleSelect();
						((AbstractMyfavoriteBaseAdapter) adapater)
								.notifyDataSetChanged();
					}
				} else if (fragment instanceof ZhiwuFavorFragment) {
					MultiColumnListView multiColumnListView = ((ZhiwuFavorFragment) fragment)
							.getMultiColumnListView();
					ImageGridAdapter adapater = (ImageGridAdapter) multiColumnListView
							.getAdapter();
					if (mAllSelectCheckBox.isChecked()) {
						adapater.setAllSelected();
					} else {
						adapater.clearAllSingleSelect();
					}
				}
			}
		});
		mDeleteBtn = (Button) mMultiSelectLayout.findViewById(R.id.delete);
		mDeleteBtn.setText(getString(R.string.delete));
		mDeleteBtn.setTextColor(getResources().getColor(
				R.color.delete_normal_color));
		mDeleteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFactory.createDeletePromptDialog(
						MyFavoritesActivity.this, mDeleteItemsCount).show();
			}
		});
		mDeleteBtn.setClickable(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
		AndroidUtils.hideInputSoftware(this);
		closeProgressDialog();
	}

	private void processLowPriceGuide() {
		Intent intent = getIntent();
		if (intent == null) {
			LogUtils.logd(TAG, LogUtils.getThreadName() + " intent is null");
			return;
		}
		boolean hasLowPrice = intent.getBooleanExtra("has_low_price", false);
		if (hasLowPrice) {
			showGuide(R.drawable.reduce_price_guide);
			setGuideBackgroud(R.color.transparent);
			StatService.onEvent(this, BaiduStatConstants.LOCAL_PUSH_OPEN,
					BaiduStatConstants.LOCAL_PUSH_OPEN);
		}
	}

	public interface OnSingleSelectCheckBoxListener {
		void onChange(JSONObject count);
	}

}
// Gionee <yangxiong><2014-8-6> add for CR00850885 end