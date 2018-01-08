/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;
import com.gionee.client.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;

public class RotateLoadingLayout extends LoadingLayout {

    public static final int ROTATION_ANIMATION_DURATION = 1200;

    private final Animation mRotateAnimation;
    private final Animation mRefreshBalloonAnimation;
    private final Animation mRefreshBalloonAnimation2;

    private float mRefreshBalloonWidth;
    private float mRefreshBalloonHeight;
    private float mHeadViewWidth;
    private float mHeadViewHeight;
    private float mHeadViewTopMargin;
    private float mIvTouyingWidth;
    private float mIvTouyingHeight;
    private float mIvTouyingTopMargin;

    public RotateLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);

        mHeaderImage.setScaleType(ScaleType.FIT_XY);
        mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
        mRefreshBalloonAnimation = AnimationUtils.loadAnimation(context,
                R.anim.refreh_balloon_rotate_from_center);
        mRefreshBalloonAnimation.setFillAfter(true);
        mRefreshBalloonAnimation2 = AnimationUtils.loadAnimation(context,
                R.anim.refreh_balloon_rotate_from_left);
        mRefreshBalloonAnimation2.setFillAfter(true);
        mRefreshBalloonAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
                mRefreshBalloon.startAnimation(mRefreshBalloonAnimation2);
            }
        });
    }

    public void onLoadingDrawableSet(Drawable imageDrawable) {
    }

    protected void onPullImpl(float scaleOfLayout) {
        android.widget.RelativeLayout.LayoutParams refreshBalloonLayoutParams = (android.widget.RelativeLayout.LayoutParams) mRefreshBalloon
                .getLayoutParams();
        if (mRefreshBalloonWidth == 0) {
            mRefreshBalloonWidth = refreshBalloonLayoutParams.width;
            mRefreshBalloonHeight = refreshBalloonLayoutParams.height;
        }
        android.widget.RelativeLayout.LayoutParams headerImageLayoutParams = (android.widget.RelativeLayout.LayoutParams) mHeaderImage
                .getLayoutParams();
        if (mHeadViewWidth == 0) {
            mHeadViewWidth = headerImageLayoutParams.width;
            mHeadViewHeight = headerImageLayoutParams.height;
            mHeadViewTopMargin = headerImageLayoutParams.topMargin;
        }
        android.widget.RelativeLayout.LayoutParams ivTouyingLayoutParams = (android.widget.RelativeLayout.LayoutParams) mIvTouying
                .getLayoutParams();
        if (mIvTouyingWidth == 0) {
            mIvTouyingWidth = ivTouyingLayoutParams.width;
            mIvTouyingHeight = ivTouyingLayoutParams.height;
            mIvTouyingTopMargin = ivTouyingLayoutParams.topMargin;
        }
        float scale = processScale(scaleOfLayout);
        refreshBalloonLayoutParams.width = (int) (mRefreshBalloonWidth * scale);
        refreshBalloonLayoutParams.height = (int) (mRefreshBalloonHeight * scale);
        mRefreshBalloon.setLayoutParams(refreshBalloonLayoutParams);
        headerImageLayoutParams.width = (int) (mHeadViewWidth * scale);
        headerImageLayoutParams.height = (int) (mHeadViewHeight * scale);
        float dy = mRefreshBalloonHeight - refreshBalloonLayoutParams.height;
        headerImageLayoutParams.topMargin = (int) (mHeadViewTopMargin - dy);
        mHeaderImage.setLayoutParams(headerImageLayoutParams);
        ivTouyingLayoutParams.width = (int) (mIvTouyingWidth * scale);
        ivTouyingLayoutParams.height = (int) (mIvTouyingHeight * scale);
        ivTouyingLayoutParams.topMargin = (int) (mIvTouyingTopMargin - dy);
        mIvTouying.setLayoutParams(ivTouyingLayoutParams);
    }

    private float processScale(float scaleOfLayout) {
        int scale = ((int) (scaleOfLayout * 10)) > 10 ? 10 : (int) (scaleOfLayout * 10);
        float fScale = (float) (8 + 0.2 * scale);
        return fScale / 10;
    }

    @Override
    protected void refreshingImpl() {
        mHeaderImage.startAnimation(mRotateAnimation);
        mRefreshBalloon.startAnimation(mRefreshBalloonAnimation);
    }

    @Override
    protected void resetImpl() {
        mHeaderImage.clearAnimation();
        mRefreshBalloon.clearAnimation();
    }

    @Override
    protected void pullToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected void releaseToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.lunzi;
    }

}
