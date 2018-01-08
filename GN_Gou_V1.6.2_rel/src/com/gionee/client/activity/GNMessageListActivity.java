package com.gionee.client.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.WebViewUtills;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.MessageListAdapter;
import com.gionee.client.view.widget.PullToRefreshListView;
import com.gionee.framework.operation.business.LocalBuisiness;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class GNMessageListActivity extends BaseFragmentActivity {
    private static final String TAG = "Message_list";
    private RequestAction mAction;
    private PullToRefreshListView mListView;
    private MessageListAdapter mAdapter;
    private boolean mIsHasNextPage;
    private int mCurpage = 1;
    private View mNoListDataView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_message_list);
        initView();
        initData();
    }

    
   
    private void initData() {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        mAction = new RequestAction();
        if (!checkNetwork()) {
            return;
        }
        showPageLoading();
        requestData(mCurpage);
    }

    private boolean checkNetwork() {
        try {
            if (AndroidUtils.getNetworkType(this) == Constants.NET_UNABLE) {

                mListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        showNetErrorToast();
                    }
                }, 300);
                showNodataLayout();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void initView() {
        initTitle();
        initNoDataView();
        hideNoDataView();
        mListView = (PullToRefreshListView) findViewById(R.id.message_list);
        mAdapter = new MessageListAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mAdapter);
        mListView.setMode(Mode.BOTH);
        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDownToRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullUpToRefresh();
            }

        });
    }

    private void initNoDataView() {
        ViewStub viewStub = (ViewStub) findViewById(R.id.no_message_data);
        mNoListDataView = viewStub.inflate();
        RelativeLayout noDataLayout = (RelativeLayout) mNoListDataView.findViewById(R.id.above_layout);
        TextView mMessageTv = (TextView) noDataLayout.findViewById(R.id.message);
        mMessageTv.setText(getString(R.string.no_message_note));
        noDataLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(Constants.ActivityResultCode.RESULT_CODE_STORY);
                StatService.onEvent(GNMessageListActivity.this, BaiduStatConstants.NON_INFO_CLICK, "");
                AndroidUtils.finishActivity(GNMessageListActivity.this);
            }
        });
        mNoListDataView.setVisibility(View.VISIBLE);
    }

    private void initTitle() {
        showTitleBar(true);
        getTitleBar().setTitle(R.string.message_list);
        showShadow(false);
    }

    private void pullDownToRefresh() {
        mCurpage = 1;
        requestData(mCurpage);
    }

    private void requestData(int curpage) {
        mAction.getMyMessageList(this, HttpConstants.Data.MessageList.MESSAGE_LIST_INFO_JO, curpage);
    }

    private void pullUpToRefresh() {
        if (mIsHasNextPage) {
            mCurpage++;
            requestData(mCurpage);
        } else {
            resetPullRefreshUi();
            if (!mRlLoading.isShown()) {
//                Toast.makeText(this, R.string.no_more_msg, Toast.LENGTH_SHORT).show();
                mListView.showNoMoreText();
            }
        }
    }

    private void resetPullRefreshUi() {
        mListView.postDelayed(new Runnable() {

            @Override
            public void run() {
                mListView.onRefreshComplete();
//                mListView.setMode(Mode.PULL_FROM_START);
            }
        }, 1000);
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        super.onSucceed(businessType, isCache, session);
        LogUtils.log(TAG, LogUtils.getFunctionName());
        if (businessType.equals(Url.MESSAGES_LIST)) {
            loadComplete();
            updateListviewData();
            updateListViewTimeLable();
            showNodataLayout();
        }
    }

    private void showNodataLayout() {
        if (!isShowNoDataView()) {
            hideNoDataView();
            return;
        }
        if (mNoListDataView == null) {
            initNoDataView();
        }
        mNoListDataView.setVisibility(View.VISIBLE);
    }

    private void hideNoDataView() {
        if (null == mNoListDataView) {
            return;
        }
        mNoListDataView.setVisibility(View.GONE);
    }

    private void updateListviewData() {
        JSONObject jsonObject = mSelfData.getJSONObject(HttpConstants.Data.MessageList.MESSAGE_LIST_INFO_JO);
        LogUtils.log(TAG, "jsonObject:" + jsonObject);
        mIsHasNextPage = jsonObject.optBoolean(HttpConstants.Data.MessageList.HAS_NEXT_B);
        mCurpage = jsonObject.optInt(HttpConstants.Data.MessageList.CURPAGE_I);
        JSONArray array = jsonObject.optJSONArray(HttpConstants.Data.MessageList.LIST_S);
        mAdapter.updateData(array);
        mAdapter.notifyDataSetChanged();
        resetFistBoot();
        hidePageLoading();
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
//                    mListView.setMode(Mode.PULL_FROM_START);
                }
            }
        }, 1000);
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getFunctionName() + errorInfo + "\n  businessType:" + businessType);
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        hidePageLoading();
        loadComplete();
        showNodataLayout();
    }
}
