package com.gionee.client.activity.history;

import org.json.JSONArray;
import org.json.JSONObject;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.OrderHistoryAdapter;
import com.gionee.client.view.widget.GnWebFragment.MyWebViewClient;
import com.gionee.client.view.widget.PullToRefreshListView;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.page.PageCacheManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.handmark.pulltorefresh.library.internal.RotateLoadingLayout;

public class OrderHistoryFragment extends BaseFragment implements OnClickListener, IBusinessHandle {

    private RelativeLayout mNoDataLayout;
    private RelativeLayout mHistoryRecordEmptyLayout;
    private PullToRefreshListView mListView;
    private OrderHistoryAdapter mAdapter;
    private RequestAction mAction;
    private boolean mIsFirst = true;// 是否是第一次加载页面
    protected RelativeLayout mRlLoading;
    private ImageView mIvBalloon;
    private ImageView mIvLunzi;
    private Animation mRotateAnimation;
    private Animation mRefreshBalloonAnimation;
    private Animation mRefreshBalloonAnimation2;
    private int mCurpage = 1;
    /**
     * 每页加载数据条数
     */
    private static final int COUNT_PER_PAGE = 12;
    public boolean mIsHasNextPage = false;
    private boolean mIsLoading = false;
    public JSONArray mOrders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.order_history_list, null);
        mNoDataLayout = (RelativeLayout) layout.findViewById(R.id.above_layout);
        mNoDataLayout.setOnClickListener(this);
        mHistoryRecordEmptyLayout = (RelativeLayout) layout.findViewById(R.id.history_no_comment_layout);
        mListView = (PullToRefreshListView) layout.findViewById(R.id.order_history);
        mAdapter = new OrderHistoryAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setMode(Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDownToRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullUpToRefresh();
            }

        });
        initLoading(layout);
        initData();
        return layout;
    }

    protected void pullDownToRefresh() {
        mNoDataLayout.setVisibility(View.GONE);
        mCurpage = 1;
        requestData(mCurpage, COUNT_PER_PAGE);
    }

    protected void pullUpToRefresh() {
        if (mIsHasNextPage) {
            mCurpage++;
            requestData(mCurpage, COUNT_PER_PAGE);
        } else {
            resetPullRefreshUi();
        }
    }

    private void resetPullRefreshUi() {
        mListView.postDelayed(new Runnable() {

            @Override
            public void run() {
                mListView.onRefreshComplete();
                mListView.setMode(Mode.PULL_FROM_START);
            }
        }, 1000);
    }

    private void initData() {
        mAction = new RequestAction();
        if (!checkNetwork()) {
            showNodataLayout();
            return;
        }
        requestData(mCurpage, COUNT_PER_PAGE);
        if (mIsFirst) {
            showPageLoading();
        }
    }

    private void initLoading(View layout) {
        // TODO Auto-generated method stub
        mRlLoading = (RelativeLayout) layout.findViewById(R.id.rl_loading);
        mIvBalloon = (ImageView) layout.findViewById(R.id.iv_balloon);
        mIvLunzi = (ImageView) layout.findViewById(R.id.iv_lunzi);
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
        mIvBalloon.startAnimation(mRefreshBalloonAnimation);
        mIvLunzi.startAnimation(mRotateAnimation);
        mRlLoading.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if(mRlLoading.getVisibility()==View.VISIBLE){
                    hidePageLoading();
                }
            }
        }, 60*1000);
    }

    /**
     * 显示页面loading
     */
    public void showPageLoading() {
        mRlLoading.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏页面loading
     */
    public void hidePageLoading() {
        mRlLoading.setVisibility(View.GONE);
    }

    private boolean checkNetwork() {
        try {
            if (AndroidUtils.getNetworkType(getActivity()) == Constants.NET_UNABLE) {
                showErrorToast(((GnBrowseHistoryActivity) getActivity()).mTitleBar);
                hidePageLoading();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void requestData(int mCurpage, int perPage) {
        mAction.getOerderHistoryList(this, HttpConstants.Data.GetOrdersHistory.ORDERHISTORY_JO,
                getActivity(), mCurpage, perPage);
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        if (businessType.equals(Url.ORDER_HISTORY)) {
            loadComplete();
            updateListviewData();
            updateListViewTimeLable();
            showNodataLayout();
            mIsFirst = false;
            mIsLoading = false;
        }
    }

    private void showNodataLayout() {
        if (!isShowNoDataView()) {
            hideNoDataView();
            return;
        }
        mHistoryRecordEmptyLayout.setVisibility(View.VISIBLE);
    }

    private void hideNoDataView() {
        if (null == mHistoryRecordEmptyLayout) {
            return;
        }
        mHistoryRecordEmptyLayout.setVisibility(View.GONE);
    }

    private void updateListviewData() {
        try {
            MyBean pageData = PageCacheManager.LookupPageData(getActivity().getClass().getName());
            if (pageData == null)
                return;
            JSONObject jsonObject = pageData.getJSONObject(HttpConstants.Data.GetOrdersHistory.ORDERHISTORY_JO);
            if (jsonObject == null)
                return;
            mIsHasNextPage = jsonObject.optBoolean(HttpConstants.Data.GetOrdersHistory.HASNEXT_B);
            mCurpage = jsonObject.optInt(HttpConstants.Data.GetOrdersHistory.CURPAGE_I);
            mOrders = jsonObject.optJSONArray(HttpConstants.Data.GetOrdersHistory.HISTORY_LIST_JA);
            mAdapter.updateData(jsonObject, mIsHasNextPage);
            mAdapter.notifyDataSetChanged();
            hidePageLoading();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean isShowNoDataView() {
        return mAdapter.getCount() == 0;
    }

    private void updateListViewTimeLable() {
        String label = AndroidUtils.getCurrentTimeStr(getSelfContext());
        mListView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
    }

    private void loadComplete() {
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.onRefreshComplete();
                int visibileItemCount = mListView.getRefreshableView().getLastVisiblePosition()
                        - mListView.getRefreshableView().getFirstVisiblePosition();
                int itemSum = mListView.getRefreshableView().getCount() - 2;
                if (itemSum > visibileItemCount) {
                    mListView.setMode(Mode.BOTH);
                } else {
                    mListView.setMode(Mode.PULL_FROM_START);
                }
            }
        }, 1000);
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        hidePageLoading();
        loadComplete();
        showNodataLayout();
        mIsLoading = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
//                Intent intent = new Intent();
//                intent.setClass(this, GnHomeActivity.class);
//                startActivity(intent);
//                AndroidUtils.exitActvityAnim(this);
                break;
            case R.id.above_layout:
                getActivity().setResult(Activity.RESULT_OK);
                AndroidUtils.finishActivity(getActivity());
                break;
            default:
                break;
        }
    }

    @Override
    public Context getSelfContext() {
        // TODO Auto-generated method stub
        return getActivity();
    }

    @Override
    public void onCancel(String businessType, Object session) {
        // TODO Auto-generated method stub

    }

    @Override
    public View getCustomToastParentView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected int setContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.order_history_list;
    }
}
