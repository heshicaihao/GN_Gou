package com.gionee.client.activity.comments;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.widget.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.huewu.pla.MultiColumnListView;
import com.huewu.pla.PLA_AbsListView;
import com.huewu.pla.PLA_AbsListView.OnScrollListener;

/**
 * 知物内容 Fragment 新闻首页
 * 
 * @author heshicaihao
 * 
 */
public class NewsFragment extends BaseFragment implements OnRefreshListener2<ScrollView>, OnClickListener {

    private static final String TAG = "NewsFragment";

    private String mChannelID;
    private int mCurpage = 1;
    private boolean mIsHasNextPage = false;
    private RequestAction mAction;
    private PullToRefreshScrollView mScroll;
    private MultiColumnListView mMultiColumnListview;
    private NewsListAdapter mAdapter;
    private View mView;
    private View mNoData;
    private ImageView mGoTop;
    private boolean mIsLoading = false;

    public NewsFragment() {
    }

    public NewsFragment(String channelId) {
        this.mChannelID = channelId;
        LogUtils.log(TAG, "ID :" + channelId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.news_fragment, null, false);
        initView(mView);
        initData();

        return mView;
    }

    private void initView(View view) {
        mScroll = (PullToRefreshScrollView) view.findViewById(R.id.news_scroll);
        mMultiColumnListview = (MultiColumnListView) view.findViewById(R.id.news_children_list);
        mGoTop = (ImageView) view.findViewById(R.id.go_top);

        mAdapter = new NewsListAdapter(getActivity());
        mMultiColumnListview.setAdapter(mAdapter);
        mScroll.setMode(Mode.BOTH);
        mScroll.setOnRefreshListener(this);
        mGoTop.setOnClickListener(this);
        mMultiColumnListview.setOnItemClickListener(mAdapter);
        mMultiColumnListview.setOnScrollListener(new CostomOnScrollListener());
    }

    private class CostomOnScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {
            int status = View.VISIBLE;
            if (firstVisibleItem == 0) {
                status = View.GONE;
            }
            mGoTop.setVisibility(status);
        }

    }

    private void initData() {
        mAction = new RequestAction();
        requestData(mCurpage);
        showLoadingProgress();
    }

    private void showLoadingProgress() {
        if (checkNetworkNotVisiviblle()) {
            return;
        }
        if (mAdapter.getCount() < 1) {
            showLoading();
        }
    }

    private void initNoDataLayoutViews(View view) {
        ViewStub stub = (ViewStub) view.findViewById(R.id.no_news_layout);
        if (stub == null) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + " stub = null");
            return;
        }
        mNoData = stub.inflate();
        RelativeLayout above = (RelativeLayout) mNoData.findViewById(R.id.above_layout);
        above.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkNetworkNotVisiviblle()) {
                    showErrorToast(mView);
                    return;
                }
                showLoadingProgress();
                requestData(mCurpage);
                hideNoDataView();
            }
        });
        TextView mMessageTv = (TextView) mNoData.findViewById(R.id.message);
        mMessageTv.setText(R.string.no_comment);
        mNoData.setVisibility(View.VISIBLE);
    }

    private void hideNoDataView() {
        if (mNoData != null) {
            mNoData.setVisibility(View.GONE);
        }
    }

    private void requestData(int page) {
        mIsLoading = true;
        mAction.getNewsList(this, HttpConstants.Data.NewsList.TALE_LIST_JO + mChannelID, page, mChannelID);
    }

    @Override
    public View getCustomToastParentView() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return mContentView;
    }

    @Override
    protected int setContentViewId() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return 0;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        super.onSucceed(businessType, isCache, session);
        LogUtils.log(TAG, LogUtils.getFunctionName());
        if (businessType.equals(Url.TALE_LIST)) {
            mIsLoading = false;
            if (!isCache) {
                mScroll.onRefreshComplete();
            }
            JSONObject object = mSelfData
                    .getJSONObject(HttpConstants.Data.NewsList.TALE_LIST_JO + mChannelID);
            JSONArray array = object.optJSONArray(HttpConstants.Data.NewsList.LIST_JA);
            LogUtils.log(TAG, "list :" + array);
            if (array != null) {
                mAdapter.setData(object.optJSONArray(HttpConstants.Data.NewsList.LIST_JA), mChannelID);
            }
            mIsHasNextPage = object.optBoolean(HttpConstants.Data.NewsList.HASNEXT_B);
            mCurpage = object.optInt(HttpConstants.Data.NewsList.CURPAGE_I);
            updateNodataView();
            if (mIsHasNextPage) {
                mScroll.hideNoMoreText();
            } else {
                mScroll.hideFootview();
            }
            LogUtils.log(TAG, "mChannelID :" + mChannelID + "data:" + object);
        }
        hideLoading();
    }

    private void updateNodataView() {
        if (mAdapter.getCount() < 1) {
            showNoDataView();
        } else {
            hideNoDataView();
        }
    }

    private void showNoDataView() {
        if (mNoData == null) {
            initNoDataLayoutViews(mView);
        }
        mNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        LogUtils.log(TAG, LogUtils.getFunctionName());
        mIsLoading = false;
        mScroll.onRefreshComplete();
        updateNodataView();
        hideLoading();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        pullDownToRefresh();
    }

    private void pullDownToRefresh() {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        if (mIsLoading) {
            return;
        }
        mCurpage = 1;
        hideNoDataView();
        requestData(mCurpage);
        mScroll.postDelayed(new Runnable() {

            @Override
            public void run() {
                mScroll.onRefreshComplete();
            }
        }, 1000);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
        pullUpToRefresh();
    }

    private void pullUpToRefresh() {
        LogUtils.log(TAG, LogUtils.getFunctionName() + mIsHasNextPage);
        if (mIsLoading) {
            return;
        }
        if (mIsHasNextPage) {
            requestData(mCurpage + 1);
        } else {
            resetPullRefreshUi();
            if (mCurpage == 1) {
                mScroll.hideFootview();
            } else {
                mScroll.showNoMoreText();
            }
        }
    }

    private void resetPullRefreshUi() {
        mScroll.postDelayed(new Runnable() {

            @Override
            public void run() {
                mScroll.onRefreshComplete();
            }
        }, 1000);
    }

    public void refreshData(Intent data) {
        mAdapter.refreshClickData(data);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.go_top:
//                mMultiColumnListview.setSelection(1);
                mMultiColumnListview.smoothScrollToPosition(0);
                StatService.onEvent(getActivity(), "tale_top", "tale_top");
                break;
            default:
                break;
        }
    }
}
