package com.gionee.client.activity.question;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.widget.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class GNFAQsFragment extends BaseFragment {
    private static final String TAG = "GNFAQS_PAGE";
    private PullToRefreshListView mListView;
    private RequestAction mAction;
    private String mUrl;
    private String mDataTag;
    private int mCurpage = 1;
    private FAQsAdapter mAdapter;
    private boolean mIsHasNextPage;
    private TextView mNote;
    private RelativeLayout mNoDataLayout;
    private ProgressBar mProgressBar;
    private String mFirstBootKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = new RequestAction();
        mFirstBootKey = getClass().getName() + mDataTag;
    }

    public void setFragmentParm(String url, String tag) {
        mUrl = url;
        mDataTag = tag;
        LogUtils.log(TAG, "url:" + mUrl + "  tag:" + tag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        View view = inflater.inflate(R.layout.faqs_fragment_page, null);
        initView(view);
        requestNetWork(mCurpage);
        return view;
    }

    private boolean isAnswerMode() {
        return HttpConstants.Data.FAQsList.ANSWERS_INFO_JO.equals(mDataTag);
    }

    private void initView(View view) {
        mListView = (PullToRefreshListView) view.findViewById(R.id.faqs_list);
        setListView();
        initNoDataView(view);
    }

    private void initNoDataView(View view) {
        mNote = (TextView) view.findViewById(R.id.no_faqs_data_message);
        mNoDataLayout = (RelativeLayout) view.findViewById(R.id.no_faqs_data_layout);
        mNoDataLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QuestionListActivity.class);
                getActivity().startActivity(intent);
                addBaiduStat();
            }
        });
        if (isFirstBoot(mFirstBootKey)) {
            showLoading();
        }
    }

    private void addBaiduStat() {
        String type = isAnswerMode() ? BaiduStatConstants.NON_ANSWERS_CLICK
                : BaiduStatConstants.NON_QUSTIONS_CLICK;
        StatService.onEvent(getActivity(), type, "");
    }

    private void setListView() {
        mAdapter = new FAQsAdapter(getActivity(), isAnswerMode());
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

    protected void pullDownToRefresh() {
        mNoDataLayout.setVisibility(View.GONE);
        mCurpage = 1;
        requestNetWork(mCurpage);
    }

    protected void pullUpToRefresh() {
        if (mIsHasNextPage) {
            mCurpage++;
            requestNetWork(mCurpage);
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

    private void requestNetWork(int page) {
        try {
            if (Constants.NET_UNABLE == AndroidUtils.getNetworkType(getActivity())) {
                showErrorToast(mListView);
                hideLoading();
                isShowNoDataLayout();
                loadComplete();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAction.getFAQsList(this, mUrl, mDataTag, page);
    }

    @Override
    public View getCustomToastParentView() {
        return null;
    }

    @Override
    protected int setContentViewId() {
        return 0;
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        super.onSucceed(businessType, isCache, session);
        if (businessType.equals(mUrl)) {
            loadComplete();
            updateView();
            updateListViewTimeLable();
        }
    }

    private void updateView() {
        JSONObject jsonObject = mSelfData.getJSONObject(mDataTag);
        LogUtils.log(TAG, "data: " + jsonObject);
        mIsHasNextPage = jsonObject.optBoolean(HttpConstants.Data.FAQsList.HAS_NEXT_B);
        mCurpage = jsonObject.optInt(HttpConstants.Data.MessageList.CURPAGE_I);
        JSONArray array = jsonObject.optJSONArray(HttpConstants.Data.FAQsList.LIST_S);
        mAdapter.updateData(array);
        isShowNoDataLayout();
        if (isFirstBoot(mFirstBootKey)) {
            resetFistBoot(mFirstBootKey);
            hideLoading();
        }
    }

    private void isShowNoDataLayout() {
        if (mAdapter.getCount() == 0) {
            mNoDataLayout.setVisibility(View.VISIBLE);
            return;
        }
        mNoDataLayout.setVisibility(View.GONE);

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
        LogUtils.log(TAG, LogUtils.getFunctionName() + errorInfo + "  " + errorOn);
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        loadComplete();
        isShowNoDataLayout();
        hideLoading();
    }

}
