package com.gionee.client.activity.welcome;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class WelcomeFragmentAdapter extends FragmentStatePagerAdapter {
	private ArrayList<Fragment> mFragments;
	private WelcomeFragmentOne ｍOne;
	private WelcomeFragmentTwo ｍTwo;

	public WelcomeFragmentAdapter(FragmentManager fm) {
		super(fm);
		if (null == mFragments) {
			mFragments = new ArrayList<Fragment>();
			ｍOne = new WelcomeFragmentOne();
			mFragments.add(ｍOne);
			ｍTwo = new WelcomeFragmentTwo();
			mFragments.add(ｍTwo);
		}
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return ｍOne;
			case 1:
				return ｍTwo;
			default:
				break;
		}
		return ｍOne;
	}

	@Override
	public int getCount() {
		return 2;
	}

}