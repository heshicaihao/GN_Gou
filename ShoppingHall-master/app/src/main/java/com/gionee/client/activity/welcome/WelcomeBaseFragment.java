package com.gionee.client.activity.welcome;

import android.content.res.Resources.NotFoundException;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;

public abstract class WelcomeBaseFragment extends BaseFragment {

	@Override
	public View getCustomToastParentView() {
		return null;
	}

	@Override
	protected int setContentViewId() {
		return 0;
	}

	public void playInAnim() {

	}

	public void playOutAnim() {
	};

	public void reset() {
	};

	protected void delayedPlayAnim(final View view, int delayMillis) {
		view.postDelayed(new Runnable() {

			@Override
			public void run() {
				viewPlayAnim(view);
			}
		}, delayMillis);

	}

	protected void viewPlayAnim(View view) {
		try {
			Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.welcome_anim);
			view.setAnimation(anim);
			anim.start();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		view.setVisibility(View.VISIBLE);
	}

}
