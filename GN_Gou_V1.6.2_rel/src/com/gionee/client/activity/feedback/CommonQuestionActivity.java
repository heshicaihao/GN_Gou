package com.gionee.client.activity.feedback;

import org.json.JSONArray;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.CommonQuestionListAdapter;
import com.gionee.client.view.adapter.QuestionCategoryListAdapter;

public class CommonQuestionActivity extends BaseFragmentActivity implements OnClickListener {

    private ListView mCategoryLeft;
    private ListView mQuestionList;
    private QuestionCategoryListAdapter mQuestionCatagoryListAdapter;
    private CommonQuestionListAdapter mCommonQuestionListAdapter;
    private View mNoListDataView;
    private RequestAction mAction;
    private int mCatagoryPosition = 0;
    private JSONArray mQuestionListData;
    private JSONArray mCatagoryListData;
    private TextView mQuestionBigTitle;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_common_question);
        initView();
        initData();
    }

    private void initView() {
        // TODO Auto-generated method stub
        initNoDataView();
        mCategoryLeft = (ListView) findViewById(R.id.categrory_tabs_list);
        mQuestionList = (ListView) findViewById(R.id.commonquestionList);
        mQuestionCatagoryListAdapter = new QuestionCategoryListAdapter(this);
        mCommonQuestionListAdapter = new CommonQuestionListAdapter(this);
        mCategoryLeft.setAdapter(mQuestionCatagoryListAdapter);
        mQuestionList.setAdapter(mCommonQuestionListAdapter);
        mCategoryLeft.setOnItemClickListener(new LeftCatagoryItemOnClickListener());
        mQuestionList.setOnItemClickListener(new RightQuestionItemOnClickListener());
        mQuestionBigTitle = (TextView) findViewById(R.id.question_big_title);
    }

    private void initData() {
        // TODO Auto-generated method stub
        mAction = new RequestAction();
        requestCatagoryData();
        if (!ShareDataManager.getBoolean(this, this.getClass().getName(), false)) {
            if (!checkNetwork()) {
                showNodataLayout();
                return;
            }
            showPageLoading();
        }
    }

    private boolean checkNetwork() {
        try {
            if (AndroidUtils.getNetworkType(this) == Constants.NET_UNABLE) {
//                Toast.makeText(this, getString(R.string.upgrade_error_network_exception), Toast.LENGTH_SHORT)
//                        .show();
                showNetErrorToast();
                hidePageLoading();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void requestCatagoryData() {
        mAction.getCatagoryList(this, HttpConstants.Data.QuestionCatagoryList.COMMONQUESTION_CATAGORY_JO,
                this);
    }

    private void requestQuestionData(int catagoryId, int position) {
        mAction.getCommonQuestionList(this, HttpConstants.Data.CommonQuestionList.COMMONQUESTION_JO,
                catagoryId, position, this);
    }

    private void initNoDataView() {
        ViewStub viewStub = (ViewStub) findViewById(R.id.no_cut_data);
        mNoListDataView = viewStub.inflate();
        RelativeLayout noDataLayout = (RelativeLayout) mNoListDataView.findViewById(R.id.above_layout);
        TextView mMessageTv = (TextView) noDataLayout.findViewById(R.id.message);
        mMessageTv.setText(getString(R.string.no_question));
        mNoListDataView.setVisibility(View.GONE);
        noDataLayout.setOnClickListener(this);
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

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        // TODO Auto-generated method stub
        super.onSucceed(businessType, isCache, session);
        if (businessType.equals(Url.COMMON_CATAGORY_LIST)) {
            updateCatagoryListviewData();
            showNodataLayout();
        } else if (businessType.equals(Url.COMMON_QUESTION_LIST)) {
            int position = (Integer) session;
            if (position == mCatagoryPosition) {
                updateQuestionListviewData();
            }
            ShareDataManager.putBoolean(this, this.getClass().getName(), true);
        }
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        // TODO Auto-generated method stub
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        hidePageLoading();
    }

    private void updateCatagoryListviewData() {
        JSONObject jsonObject = mSelfData
                .getJSONObject(HttpConstants.Data.QuestionCatagoryList.COMMONQUESTION_CATAGORY_JO);
        if (jsonObject != null) {
            mCatagoryListData = jsonObject
                    .optJSONArray(HttpConstants.Data.QuestionCatagoryList.COMMONQUESTION_CATAGORY_LIST_JA);
            mQuestionCatagoryListAdapter.setData(mCatagoryListData);
            if (mCatagoryListData != null && mCatagoryListData.length() > 0) {
                requestQuestionData(
                        mCatagoryListData.optJSONObject(0).optInt(
                                HttpConstants.Data.QuestionCatagoryList.CATAGORYID_I), 0);
                String questionBigTitle = mCatagoryListData.optJSONObject(0).optString(
                        HttpConstants.Data.QuestionCatagoryList.QUESTION_TITLE_S);
                if (questionBigTitle != null && !"".equals(questionBigTitle)) {
                    mQuestionBigTitle.setVisibility(View.VISIBLE);
                    mQuestionBigTitle.setText(questionBigTitle);
                } else {
                    mQuestionBigTitle.setVisibility(View.GONE);
                }
            }
        }
        hidePageLoading();
    }

    private boolean isShowNoDataView() {
        return mQuestionCatagoryListAdapter.getCount() == 0;
    }

    private void updateQuestionListviewData() {
        JSONObject jsonObject = mSelfData
                .getJSONObject(HttpConstants.Data.CommonQuestionList.COMMONQUESTION_JO);
        if (jsonObject != null) {
            mQuestionListData = jsonObject
                    .optJSONArray(HttpConstants.Data.CommonQuestionList.COMMONQUESTION_LIST_JA);
            mCommonQuestionListAdapter.setData(mQuestionListData);
        }
        hidePageLoading();
    }

    class LeftCatagoryItemOnClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mCatagoryPosition == position) {
                return;
            }
            mQuestionListData = new JSONArray();
            mCommonQuestionListAdapter.setData(mQuestionListData);
            JSONObject catagoryItem = mCatagoryListData.optJSONObject(position);
            int catagoryId = catagoryItem.optInt(HttpConstants.Data.QuestionCatagoryList.CATAGORYID_I);
            mCatagoryPosition = position;
            requestQuestionData(catagoryId, position);
            mQuestionCatagoryListAdapter.setSelectItem(position);
            String questionBigTitle = catagoryItem
                    .optString(HttpConstants.Data.QuestionCatagoryList.QUESTION_TITLE_S);
            if (questionBigTitle != null && !"".equals(questionBigTitle)) {
                mQuestionBigTitle.setVisibility(View.VISIBLE);
                mQuestionBigTitle.setText(questionBigTitle);
            } else {
                mQuestionBigTitle.setVisibility(View.GONE);
            }
        }

    }

    class RightQuestionItemOnClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            JSONObject bean = mQuestionListData.optJSONObject(position);
            String url = bean.optString(HttpConstants.Data.CommonQuestionList.QUESTION_URL_S);
            gotoWebPage(url, true);
            StatService.onEvent(CommonQuestionActivity.this, BaiduStatConstants.QUESTION,
                    bean.optString(HttpConstants.Data.CommonQuestionList.QUESTION_ID_S));
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.iv_back:
                finish();
                AndroidUtils.exitActvityAnim(this);
                break;
            case R.id.above_layout:
                mNoListDataView.setVisibility(View.GONE);
                initData();
                break;

            default:
                break;
        }
    }
}
