package com.gionee.client.activity.welcome;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.gionee.client.R;

public class WelcomeFragmentOne extends WelcomeBaseFragment {

	private ImageView mPlane;
	private int mWidth;
	private int mPlaneWidth;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.welcome_one, null);
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		mWidth = metric.widthPixels;
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plane);
		mPlaneWidth = mBitmap.getWidth();
		mPlane = (ImageView) view.findViewById(R.id.plane_image);
		initAnimation(mPlane);
		return view;
	}

	private void initAnimation(View view) {
		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.3, 1);
		alphaAnimation.setDuration(1000);
		animationSet.addAnimation(alphaAnimation);
		view.setAnimation(alphaAnimation);
		TranslateAnimation animation = new TranslateAnimation(mWidth / 5, mWidth * 4 / 5 - mPlaneWidth, 0, 0);
		animation.setDuration(1000);
		animation.setRepeatCount(0);
		animationSet.addAnimation(animation);
		view.setAnimation(animation);
		animationSet.setFillAfter(true);
		view.startAnimation(animationSet);
	}

	@Override
	public void onResume() {
		super.onResume();
		initAnimation(mPlane);
	}

	@Override
	public void reset() {
		initAnimation(mPlane);
	}
}