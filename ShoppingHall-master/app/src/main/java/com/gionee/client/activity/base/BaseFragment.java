// Gionee <yuwei><2013-10-17> add for CR00821559 begin
/*
 * BaseFragment.java
 * classes : com.gionee.poorshopping.activity.base.BaseFragment
 * @author yuwei
 * V 1.0.0
 * Create at 2013-10-17 下午3:22:17
 */
package com.gionee.client.activity.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.gionee.client.R;
import com.gionee.client.business.inject.GNInjector;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.CommonUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.StringUtills;
import com.gionee.client.model.Constants;
import com.gionee.client.view.widget.CustomToast;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.page.PageCacheManager;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.handmark.pulltorefresh.library.internal.RotateLoadingLayout;

/**
 * BaseFragment
 * 
 * @author yuwei <br/>
 * @date create at 2013-10-17 下午3:22:17
 * @description TODOBaseFragmentActivity
 */
public abstract class BaseFragment extends Fragment implements IBaseFragment, IBusinessHandle {
    private static final String TAG = "BaseFragment";
    protected MyBean mSelfData;
    protected CustomToast mCustomToast;
    protected View mContentView;
    protected View mLoadingView;
    protected RelativeLayout mRlLoading;
    private ImageView mIvBalloon;
    private ImageView mIvLunzi;
    private long mLastClickTime;
    private Animation mRotateAnimation;
    private Animation mRefreshBalloonAnimation;
    private Animation mRefreshBalloonAnimation2;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onActivityCreated(savedInstanceState);
        mSelfData = PageCacheManager.LookupPageData(this.getActivity().getClass().getName());
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        showErrorInfo(errorInfo);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(savedInstanceState);
        mSelfData = PageCacheManager.LookupPageData(this.getActivity().getClass().getName());
        mCustomToast = new CustomToast(getActivity());
//        mLoadingView = LayoutInflater.from(getActivity()).inflate(R.layout.page_loading_view, null);
    }

    @Override
    public void onAttach(Activity activity) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mSelfData = PageCacheManager.LookupPageData(this.getActivity().getClass().getName());
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSelfData = PageCacheManager.LookupPageData(this.getActivity().getClass().getName());
    }

    /**
     * @param errorInfo
     * @author yuwei
     * @description TODO
     */
    private void showErrorInfo(String errorInfo) {
        if (TextUtils.isEmpty(errorInfo)) {
            return;
        }
        if (getActivity() == null) {
            return;
        }
        try {

            if (StringUtills.isNotContainEnglish(errorInfo)) {
                mCustomToast.setToastText(errorInfo);
                int topMargin = AndroidUtils.dip2px(getActivity(), 110);
                mCustomToast.showToast(getCustomToastParentView(), topMargin);
//                Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(getActivity(), getString(R.string.net_error), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void showErrorToast(View view) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        int topMargin = AndroidUtils.dip2px(getActivity(), 74);
        showErrorToastMarginTop(view, topMargin);
    }

    protected void showErrorToastMarginTop(View view, int margin) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        mCustomToast.setToastText(getString(R.string.upgrade_no_net));
        mCustomToast.showToast(view, margin);
    }

    public abstract View getCustomToastParentView();

    @Override
    public Context getSelfContext() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return getActivity();
    }

    @Override
    public void onCancel(String businessType, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    public void reload(boolean isReload) {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    public void dismissProgress() {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    public void setWebUrl(String url) {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    /**
     * @author yuwei
     * @description TODO
     */
    protected <T> void startActivityWithNoParams(Class<T> activity) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), activity);
        startActivity(intent);
        AndroidUtils.enterActvityAnim(getActivity());
    }

    protected void gotoWebPage(String url, boolean isShowFootBar) {
        CommonUtils.gotoWebViewActvity(getActivity(), url, isShowFootBar);
    }

    protected void gotoWebPageForResult(String url, boolean isShowFootBar) {
        CommonUtils.gotoWebViewActvityForResult(getActivity(), url, isShowFootBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mContentView = setContentView(inflater);
        GNInjector.getInstance().injectFragmentView(this);
        return mContentView;
    }

    protected abstract int setContentViewId();

    protected View setContentView(LayoutInflater inflater) {
        if (setContentViewId() != 0) {
            return inflater.inflate(setContentViewId(), null);
        }
        return null;
    }

    private void initLoading() {
        // TODO Auto-generated method stub
        mRlLoading = (RelativeLayout) getView().findViewById(R.id.rl_loading);
        mIvBalloon = (ImageView) getView().findViewById(R.id.iv_balloon);
        mIvLunzi = (ImageView) getView().findViewById(R.id.iv_lunzi);
        mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(LoadingLayout.ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(RotateLoadingLayout.ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
        mRefreshBalloonAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.refreh_balloon_rotate_from_center);
        mRefreshBalloonAnimation.setFillAfter(true);
        mRefreshBalloonAnimation2 = AnimationUtils.loadAnimation(getActivity(),
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
                mIvBalloon.startAnimation(mRefreshBalloonAnimation2);
            }
        });
        try {
            mIvBalloon.startAnimation(mRefreshBalloonAnimation);
            mIvLunzi.startAnimation(mRotateAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoading() {
        try {
            if (mLoadingView == null) {
                mLoadingView = LayoutInflater.from(getActivity()).inflate(R.layout.page_loading_view, null);
            }
            mLoadingView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ((FrameLayout) getView() == null) {
                        return;
                    }
                    if (mLoadingView.getParent() == null) {
                        ((FrameLayout) getView())
                                .addView(mLoadingView, new FrameLayout.LayoutParams(
                                        FrameLayout.LayoutParams.MATCH_PARENT,
                                        FrameLayout.LayoutParams.MATCH_PARENT));
                    }
                    initLoading();
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void resetFistBoot() {
        ShareDataManager.putBoolean(getActivity(), this.getClass().getName(), false);
    }

    public boolean isFirstBoot() {
        return ShareDataManager.getBoolean(getActivity(), this.getClass().getName(), true);
    }

    public void resetFistBoot(String key) {
        ShareDataManager.putBoolean(getActivity(), key, false);
    }

    public boolean isFirstBoot(String key) {
        return ShareDataManager.getBoolean(getActivity(), key, true);
    }

    public void hideLoading() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mLoadingView == null) {
                        LogUtils.log(TAG, LogUtils.getThreadName() + " loading view == null ");
                        return;
                    }
                    mLoadingView.setVisibility(View.GONE);
                    ((FrameLayout) getView()).removeView(mLoadingView);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onPageVisible();
        } else {
            onPageInvisible();
        }
    };

    @Override
    public void onPageInvisible() {
        LogUtils.log(TAG, LogUtils.getThreadName());

    };

    @Override
    public void onPageVisible() {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    /**
     * @param view
     * @author yuwei
     * @description TODO
     */
    protected void setTopPadding(View view) {
        try {
            if (AndroidUtils.translateTopBar(getActivity())) {
                RelativeLayout mtopBar = (RelativeLayout) view.findViewById(R.id.top_title_bar);
                LayoutParams params = (LayoutParams) mtopBar.getLayoutParams();
                params.topMargin = AndroidUtils.dip2px(getActivity(), 15);
                mtopBar.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean checkNetworkNotVisiviblle() {
        return AndroidUtils.getNetworkType(getActivity()) == Constants.NET_UNABLE;
    }

}
