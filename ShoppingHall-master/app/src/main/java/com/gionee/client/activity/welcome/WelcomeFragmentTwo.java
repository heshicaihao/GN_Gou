package com.gionee.client.activity.welcome;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.gionee.client.R;

public class WelcomeFragmentTwo extends WelcomeBaseFragment {

	private static final int SNOWFLAKE = 1;
	private static final int BABY = 2;

	private ImageView mImage;
	private Animation mAnimationLeft;
	private Animation mAnimationRight;

	Timer mTimer = new Timer();
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SNOWFLAKE:
					mImage.setImageResource(R.drawable.snowflake);
					break;
				case BABY:
					mImage.setImageResource(R.drawable.baby);
					break;
			}
			super.handleMessage(msg);
		}

	};

	TimerTask taskSnowflake = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = SNOWFLAKE;
			mHandler.sendMessage(message);
		}
	};
	TimerTask taskBaby = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = BABY;
			mHandler.sendMessage(message);
		}
	};

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.welcome_two, null);
		mImage = (ImageView) view.findViewById(R.id.baby);
		initAnimation();
		mTimer.schedule(taskSnowflake, 0, 1000);
		mTimer.schedule(taskBaby, 500, 1000);
		return view;
	}

	private void initAnimation() {
		mAnimationLeft = AnimationUtils
				.loadAnimation(getActivity(), R.anim.refreh_balloon_rotate_from_center);
		mAnimationLeft.setFillAfter(true);
		mAnimationRight = AnimationUtils.loadAnimation(getActivity(), R.anim.refreh_balloon_rotate_from_left);
		mAnimationRight.setFillAfter(true);
		mAnimationLeft.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				mImage.startAnimation(mAnimationRight);
			}
		});
		mImage.startAnimation(mAnimationLeft);
	}

}