package com.gionee.client.activity.compareprice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.tabFragment.HomeFragment;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.view.widget.TabViewPager;

public class ComparePriceActivity extends BaseFragmentActivity implements OnCheckedChangeListener,
		OnClickListener {

	private static final String TAG = "ComparePriceActivity";
	public static final String ComparePrice_Fragment = "ComparePriceFragment";
	private static final String HistoryPrice_Fragment = "HistoryPriceFragment";
	public static final String HALL_FRAGMENT = "hallFragment";

	private TextView mTitle;
	private RadioGroup mComparePriceRadio;
	private RadioButton mComparePriceRb, mHistoryPriceRb;
	private TabViewPager mViewpager;

	private static final int[] FRAGMENT_IDS = {R.id.compare_price_rb, R.id.history_price_rb};
	private static final String[] FRAGMENT_TAGS = { ComparePrice_Fragment,
		HistoryPrice_Fragment, }; 
	private String mCurrentPage;
	private int mCurrentActivityId = 0;
	private int mCurrentRadiobarCheckId = FRAGMENT_IDS[0];
	private static FragmentManager sFragmentManager;
	private ComparePriceFragment mComparePriceFragment;
	private HistoryPriceFragment mHistoryPriceFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		LogUtils.log(TAG, LogUtils.getThreadName());
		super.onCreate(arg0);
		setContentView(R.layout.compare_price_activity);
		initView();
		init();
	}

	private void initView() {
		mTitle = (TextView) findViewById(R.id.title_text);
		mComparePriceRadio = (RadioGroup) findViewById(R.id.compare_price_radio);
		mComparePriceRb = (RadioButton) findViewById(R.id.compare_price_rb);
		mHistoryPriceRb = (RadioButton) findViewById(R.id.history_price_rb);
		mViewpager = (TabViewPager) findViewById(R.id.compare_price_viewpager);

	}

	private void init() {
		sFragmentManager = getSupportFragmentManager();

		mComparePriceRadio.setOnCheckedChangeListener(this);

		mComparePriceRb.setOnClickListener(this);
		mHistoryPriceRb.setOnClickListener(this);

	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch (arg1) {
			case R.id.compare_price_rb:
				checkComparePrice();
				break;
			case R.id.history_price_rb:
				checkComparePrice();
				break;
			default:
				break;
		}
		showFragmentByIndex(mCurrentPage);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.compare_price_rb:
				
				break;
			case R.id.history_price_rb:
				
				break;
			default:
				break;
		}

	}
	
	private void showFragmentByIndex(String tag) {
		int lenth = FRAGMENT_TAGS.length;
		for (int i = 0; i < lenth; i++) {
			if (!FRAGMENT_TAGS[i].equals(tag)) {
				hideFragmentByTag(FRAGMENT_TAGS[i]);
			}
		}
		showFragmentByTag(tag);

	}
	
	private void showFragmentByTag(String tag) {
		FragmentTransaction fragmentTrans = sFragmentManager.beginTransaction();
		Fragment fragment = sFragmentManager.findFragmentByTag(tag);
		if (fragment != null) {
			fragmentTrans.show(fragment);
			fragment.setUserVisibleHint(true);

		}
		fragmentTrans.commitAllowingStateLoss();
	}

	/**
	 * 
	 * @author yuwei
	 * @description TODO
	 */
	private void hideFragmentByTag(String tag) {
		try {
			FragmentTransaction fragmentTrans = sFragmentManager
					.beginTransaction();
			Fragment fragment = sFragmentManager.findFragmentByTag(tag);
			if (fragment != null) {
				fragmentTrans.hide(fragment);
				fragment.setUserVisibleHint(false);
			}
			fragmentTrans.commitAllowingStateLoss();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkComparePrice() {
		if (!ComparePrice_Fragment.equals(mCurrentPage)) {
			mCurrentPage = ComparePrice_Fragment;
			mCurrentRadiobarCheckId = FRAGMENT_IDS[0];
			createComparePriceFragment();
		}
	}

	private void checkHistoryPrice() {
		if (!ComparePrice_Fragment.equals(mCurrentPage)) {
			mCurrentPage = ComparePrice_Fragment;
			mCurrentRadiobarCheckId = FRAGMENT_IDS[1];
			createHistoryPriceFragment();
		}
	}
	
	/**
	 * 
	 * @author heshicaihao
	 * @description TODO
	 */
	private void createHistoryPriceFragment() {
		if (mHistoryPriceFragment == null) {
			mHistoryPriceFragment = new HistoryPriceFragment();
			addFragmentToContainner(mHistoryPriceFragment, ComparePrice_Fragment);
			mCurrentPage = ComparePrice_Fragment;
		}
	}

	/**
	 * 
	 * @author heshicaihao
	 * @description TODO
	 */
	private void createComparePriceFragment() {
		if (mComparePriceFragment == null) {
			mComparePriceFragment = new ComparePriceFragment();
			addFragmentToContainner(mComparePriceFragment, ComparePrice_Fragment);
			mCurrentPage = HistoryPrice_Fragment;
		}
	}

	/**
	 * 
	 * @author heshicaihao
	 * @description TODO
	 */
	private void addFragmentToContainner(Fragment fragment, String tag) {
		try {
			FragmentTransaction fragmentTransaction = sFragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.content_containner, fragment, tag);
			fragmentTransaction.addToBackStack(tag);
			fragmentTransaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
