package com.gionee.client.activity.compareprice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;

public class HistoryPriceFragment extends BaseFragment {

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.history_price_fragment, null);
		return view;
	}

	@Override
	public View getCustomToastParentView() {
		return null;
	}

	@Override
	protected int setContentViewId() {
		return 0;
	}

}
