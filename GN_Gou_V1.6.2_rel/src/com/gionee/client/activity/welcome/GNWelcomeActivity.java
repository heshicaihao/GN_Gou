package com.gionee.client.activity.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gionee.client.R;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.view.widget.WelcomePageIndicatorView;

public class GNWelcomeActivity extends FragmentActivity implements OnClickListener {
	private ViewPager mPager;
	private WelcomeFragmentAdapter mAdapter;
	private WelcomePageIndicatorView mIndex;
	private int mCurrentPosition;
	private boolean mIsScrolled;
	private Button mJump;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.welcome_activity);
		mPager = (ViewPager) findViewById(R.id.welcome_viewpager);
		mIndex = (WelcomePageIndicatorView) findViewById(R.id.welcome_page_index);
		mIndex.setTotalPage(2);
		mIndex.setCurrentPage(0);
		mJump = (Button) findViewById(R.id.jump_over);
		mAdapter = new WelcomeFragmentAdapter(getSupportFragmentManager());
		mPager.setOffscreenPageLimit(2);
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				pageSelcect(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				switch (state) {
					case ViewPager.SCROLL_STATE_DRAGGING:
						mIsScrolled = false;
						break;
					case ViewPager.SCROLL_STATE_SETTLING:
						mIsScrolled = true;
						break;
					case ViewPager.SCROLL_STATE_IDLE:
						if (mPager.getCurrentItem() == (mPager.getAdapter().getCount() - 1) && !mIsScrolled) {
							gotoActivityWithOutParams(GnHomeActivity.class);
							finish();
						}
						break;
					default:
						break;
				}
			}
		});
		mPager.setCurrentItem(0);
		mPager.postDelayed(new Runnable() {
			@Override
			public void run() {
				pageSelcect(0);
			}
		}, 600);
	}

	private void pageSelcect(int position) {
		mIndex.setCurrentPage(position);
		reset();
		WelcomeBaseFragment fragment = (WelcomeBaseFragment) mAdapter.getItem(position);
		fragment.playInAnim();
		mCurrentPosition = position;
		mJump.setVisibility(View.VISIBLE);
		mIndex.setVisibility(position == 1 ? View.GONE : View.VISIBLE);
	}

	private void reset() {
		if (mCurrentPosition > 0) {
			WelcomeBaseFragment fragment = (WelcomeBaseFragment) mAdapter.getItem(mCurrentPosition - 1);
			fragment.reset();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.third_open_app:
				gotoActivityParams(GnHomeActivity.class, true);
				finish();
				break;
			case R.id.jump_over:
				gotoActivityWithOutParams(GnHomeActivity.class);
				finish();
				break;
			default:
				break;
		}
	}

	private <T> void gotoActivityParams(Class<T> activity, Boolean mIsComments) {
		Intent intent = new Intent();
		intent.putExtra("mIsComments", mIsComments);
		intent.setClass(this, activity);
		startActivity(intent);
		AndroidUtils.enterActvityAnim(this);
	}

	private <T> void gotoActivityWithOutParams(Class<T> activity) {
		Intent intent = new Intent();
		intent.setClass(this, activity);
		startActivity(intent);
		AndroidUtils.enterActvityAnim(this);
	}

}
