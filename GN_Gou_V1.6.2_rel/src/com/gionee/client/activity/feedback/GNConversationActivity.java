// Gionee <yuwei><2013-8-29> add for CR00821559 begin
/*
 * ConversationActivity.java
 * classes : com.gionee.client.activity.ConversationActivity
 * @author yuwei
 * V 1.0.0
 * Create at 2013-8-29 上午11:16:44
 */
package com.gionee.client.activity.feedback;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.ReplyListAdapter;
import com.gionee.client.view.shoppingmall.GNTitleBar.OnRightBtnListener;

/**
 * com.gionee.client.activity.ConversationActivity
 * 
 * @author yuwei <br/>
 * @date create at 2013-8-29 上午11:16:44
 * @description TODO 意见反馈会话页面
 */
public class GNConversationActivity extends BaseFragmentActivity implements OnClickListener {
    private static final String TAG = "GNConversationActivity";
    private ReplyListAdapter mReplyListAdapter;
    private ListView mReplyListView;
    private EditText mUserReplyEdit;
    private RequestAction mRequestAction;
    private int mSession = 0;
    private Map<Integer, Integer> mSessionMap;
    private boolean mIsAutoRefresh;
    private int mLastLenth;
    private int mFeedbackTime;
    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            postDelayRequest();
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umeng_fb_activity_conversation);
        mSessionMap = new HashMap<Integer, Integer>();
        mFeedbackTime = UserInfoManager.getInstance().getFeedbackTime(this);
        initView();

    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void requestConversationHistory() {
        mRequestAction = new RequestAction();
        mRequestAction.getFeedbackList(this, HttpConstants.Data.DEFAULT_LIST_KEY);
        if (checkNetwork()) {
            showLoadingProgress();
        }
    }

    @Override
    protected void onResume() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onResume();
        mIsAutoRefresh = true;
        requestConversationHistory();
        mHandler.sendEmptyMessageDelayed(0, mFeedbackTime * 1000);
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void initView() {

        mUserReplyEdit = (EditText) findViewById(R.id.umeng_fb_reply_content);
        mReplyListView = (ListView) findViewById(R.id.umeng_fb_reply_list);
//        View view = LayoutInflater.from(this).inflate(R.layout.umeng_fb_list_header, null);
//        mReplyListView.addHeaderView(view);
        mReplyListAdapter = new ReplyListAdapter(this, new JSONArray());
        mReplyListView.setAdapter(mReplyListAdapter);
//        mReplyListAdapter.addConversationItem(getString(R.string.feedback_notify), true);
        showTitleBar(true);
        getTitleBar().setTitle(R.string.service_online);
        getTitleBar().getRightBtn().setVisibility(View.VISIBLE);
        getTitleBar().setRightBtnText(R.string.common_question);
        getTitleBar().setRightListener(new OnRightBtnListener() {

            @Override
            public void onClick() {
                gotoCommonQestion();
            }
        });

    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        mIsAutoRefresh = false;
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        super.onSucceed(businessType, isCache, session);
        if (businessType.equals(Url.GET_CONVERSATION_LIST)) {
            setFeedbackHasLook();
            hideLoadingProgress();
            resetFistBoot();
            JSONObject mConversationData = mSelfData.getJSONObject(HttpConstants.Data.DEFAULT_LIST_KEY);
            if (mConversationData != null) {
//                Log.i("hhhhhhhh", mConversationData.toString());
                JSONArray mConversationArray = mConversationData.optJSONArray(HttpConstants.Response.LIST_JA);
                if (mConversationArray != null && mConversationArray.length() > 0) {
                    scrollToBottom(mConversationArray, isCache);
                }
            }
        }
        if (businessType.equals(Url.SEND_FEEDBACK)) {
            String sessionStr = (String) session;
            if (sessionStr.startsWith("reSend")) {
                // 表示重新发送
                int position = Integer.parseInt(sessionStr.substring(6));
                mReplyListAdapter.updateProgress(position, Constants.STATUS_COMPLETE);
            } else {
                int position = Integer.parseInt(sessionStr);
                mLastLenth++;
                resetFeedBackTips(position);
            }
            StatService.onEvent(this, BaiduStatConstants.MESSAGE_SUBMIT, "message_submit_success");
        }
    }

    private void setFeedbackHasLook() {
        ShareDataManager.putBoolean(this, HttpConstants.Response.UserInfo.FEEDBACK_TIP_B, false);
        RequestAction action = new RequestAction();
        action.cleanFeedbackNotify(this);
    }

    public void scrollToBottom(JSONArray mConversationArray, boolean isCache) {
        try {
            if (mLastLenth <= mConversationArray.length()) {
                mReplyListAdapter = new ReplyListAdapter(this, mConversationArray);
                mReplyListView.setAdapter(mReplyListAdapter);
                mReplyListAdapter.mConversationList = mConversationArray;
                mReplyListAdapter.notifyDataSetChanged();
                mReplyListView.setSelection(mConversationArray.length());
                mLastLenth = mConversationArray.length();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postDelayRequest() {
        if (mIsAutoRefresh && mReplyListAdapter.isNotLoading() && checkNetwork()) {
            mRequestAction.getFeedbackList(this, HttpConstants.Data.DEFAULT_LIST_KEY);
        }
        mHandler.sendEmptyMessageDelayed(0, mFeedbackTime * 1000);
    }

    private boolean checkNetwork() {
        try {
            if (AndroidUtils.getNetworkType(this) == Constants.NET_UNABLE) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void resetFeedBackTips(int session) {
        try {
            int position = mSessionMap.get(session);
            LogUtils.log(TAG, LogUtils.getFunctionName() + "session=" + session + "position=" + position);
            View listItem = mReplyListView.getChildAt(position - mReplyListView.getFirstVisiblePosition());
            hideProgress(position, listItem);
            mReplyListAdapter.updateProgress(position, Constants.STATUS_COMPLETE);
            JSONObject mSuccessData = mSelfData.getJSONObject(HttpConstants.Data.DEFAULT_SUCCESS_KEY);
            if (mSuccessData != null) {
                JSONObject tipObj = mSuccessData.optJSONObject(HttpConstants.Response.ConversationList.TIP_B);
                if (tipObj != null) {
                    String notify = tipObj.optString(HttpConstants.Response.MSG_S);
                    if (!TextUtils.isEmpty(notify)) {
                        mReplyListAdapter.addReplayConversationItem(notify, false);
                    }
                }
            }
            // TODO Auto-generated method stub
            mReplyListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgress(int position, View listItem) {
        try {
            ProgressBar progress = (ProgressBar) listItem.findViewById(R.id.loading_bar);
            progress.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResult(final String businessType, String errorOn, String errorInfo,
            final Object session) {
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        mReplyListView.postDelayed(new Runnable() {

            @Override
            public void run() {
                hidePageLoading();
                resetFistBoot();
                showMsgError(businessType, session);
            }

            public void showMsgError(final String businessType, final Object session) {
                if (businessType.equals(Url.SEND_FEEDBACK)) {
                    StatService.onEvent(GNConversationActivity.this, BaiduStatConstants.MESSAGE_SUBMIT,
                            "message_submit_error");
                    try {
                        int sessionInt;
                        String sessionStr = (String) session;
                        if (sessionStr.startsWith("reSend")) {
                            // 表示重新发送
                            sessionInt = Integer.parseInt(sessionStr.substring(6));
                        } else {
                            sessionInt = (Integer) session;
                        }
                        int position = mSessionMap.get(sessionInt);
                        LogUtils.log(TAG, LogUtils.getFunctionName() + "session=" + sessionInt + "position="
                                + position);
                        View listItem = mReplyListView.getChildAt(position
                                - mReplyListView.getFirstVisiblePosition());
                        hideProgress(position, listItem);
                        showError(listItem);
                        mReplyListAdapter.updateProgress(position, Constants.STATUS_ERROR);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 200);

    }

    public void showError(View listItem) {
        ImageView errorImg = (ImageView) listItem.findViewById(R.id.send_error);
        errorImg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.umeng_fb_send:
                sendMessage();
                break;
            case R.id.send_error:
                int position = (Integer) v.getTag();
                JSONObject item = (JSONObject) mReplyListAdapter.getItem(position);
                Log.i("hhhhhhhh", item.toString());
                mReplyListAdapter.updateProgress(position, Constants.STATUS_LOADING);
                mRequestAction.submitFeedback(this, HttpConstants.Data.DEFAULT_SUCCESS_KEY,
                        item.optString("content"), "reSend" + position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndroidUtils.exitActvityAnim(GNConversationActivity.this);
        mHandler.removeMessages(0);
    }

    private void sendMessage() {
        if (!checkNetwork()) {
//            Toast.makeText(this, R.string.upgrade_error_network_exception, Toast.LENGTH_SHORT).show();
            showNetErrorToast();
            return;
        }
        String content = mUserReplyEdit.getEditableText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            mRequestAction.submitFeedback(this, HttpConstants.Data.DEFAULT_SUCCESS_KEY, content, mSession
                    + "");
            LogUtils.log(TAG, LogUtils.getFunctionName() + "session=" + mSession);
            if (mReplyListAdapter == null) {
                mReplyListAdapter = new ReplyListAdapter(this, new JSONArray());
            }
            int position = mReplyListAdapter.addFeedbackConversationItem(content, false);
            mSessionMap.put(mSession, position);
            mSession++;
            mUserReplyEdit.setText("");
            AndroidUtils.hideInputSoftware(this);
        } else {
            Toast.makeText(this, R.string.empty_content, Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoCommonQestion() {
        Intent intent = new Intent();
        intent.setClass(GNConversationActivity.this, CommonQuestionActivity.class);
        startActivity(intent);
        AndroidUtils.enterActvityAnim(this);
    }
}
//Gionee <yuwei><2013-8-29> add for CR00821559 end