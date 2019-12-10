package com.gionee.client.activity.story;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.DiscussListAdapter;
import com.gionee.client.view.shoppingmall.CommentsProgressBar;
import com.gionee.client.view.widget.PullToRefreshListView;
import com.gionee.framework.model.bean.MyBean;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class GNDiscussDetailsActivity extends BaseFragmentActivity implements OnClickListener {

    private static final String TAG = "Discuss_Details";
    private PullToRefreshListView mListView;
    private int mCurpage = 1;
    private boolean mIsHasNextPage = false;
    private String mTypeId = "1";
    private RequestAction mAction;
    private DiscussListAdapter mAdapter;
//    private CommentsProgressBar mCommentsProgressBar;
    private SendStoryCommentsDialog mSendStoryCommentsDialog;
    private View mNoDataView;
    private int mSendDiscussCount = 0;
    private boolean mIsCommentsSending;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.discuss_details_page);
        initTypeID();
        initView();
        initData();
//        AndroidUtils.setMiuiTopMargain(this, findViewById(R.id.discuss_title));
    }

    private void initTypeID() {
        mTypeId = getIntent().getStringExtra(Constants.TYPE_ID);
    }

    private void initData() {
        mAction = new RequestAction();
        requestData(mCurpage);
    }

    private void requestData(int page) {
        mAction.getDiscussList(this, mTypeId, page, HttpConstants.Data.DiscussList.LIST_INFO_JO + mTypeId);
    }

    private void initView() {
        initTitle();
        mListView = (PullToRefreshListView) findViewById(R.id.discuss_list);
        mAdapter = new DiscussListAdapter(this, this);
        mListView.setAdapter(mAdapter);
        mListView.setMode(Mode.BOTH);

        mListView.setOnItemClickListener(commentItemListener);
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
//        mCommentsProgressBar = (CommentsProgressBar) findViewById(R.id.comments_progress_bar);
//        mCommentsProgressBar.setOnClickListener(this);
    }

    private void initTitle() {
        showTitleBar(true);
        getTitleBar().setTitle(R.string.story_discuss);
        showShadow(true);
    }

    private OnItemClickListener commentItemListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            try {
                JSONObject obj = (JSONObject) mAdapter.getItem(arg2 - 1);
                String commentId = obj.optString(HttpConstants.Response.ID);
                String nickName = obj.optString(HttpConstants.Data.DiscussList.NIKE_NAME);
                showSendStroyCommentsDialog(commentId, nickName);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private void initNoDataView() {
        ViewStub viewStub = (ViewStub) findViewById(R.id.no_discuss_data);
        mNoDataView = viewStub.inflate();
        RelativeLayout noDataLayout = (RelativeLayout) mNoDataView.findViewById(R.id.above_layout);
        TextView mMessageTv = (TextView) noDataLayout.findViewById(R.id.message);
        mMessageTv.setText(getString(R.string.no_discuss_data));
        mNoDataView.setVisibility(View.VISIBLE);
    }

    private void hideNoDataView() {
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    private void pullDownToRefresh() {
        mCurpage = 1;
        requestData(mCurpage);
    }

    private void pullUpToRefresh() {
        if (mIsHasNextPage) {
            requestData(mCurpage + 1);
        } else {
            Toast.makeText(this, R.string.no_more_msg, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        super.onSucceed(businessType, isCache, session);
        LogUtils.log(TAG, LogUtils.getFunctionName());
        if (businessType.equals(Url.STORY_COMMENT_URL)) {
            updateNickName();
            sendDiscussSucuss(mSelfData.getJSONObject(HttpConstants.Data.DiscussList.PRAISE_ID));
            StatService.onEvent(this, "send_st", BaiduStatConstants.OK);
            mSendStoryCommentsDialog.resetNomalStatus();
            mSendStoryCommentsDialog.dismiss();
            mSendStoryCommentsDialog.setCommentContent("");
        }
        if (businessType.equals(Url.DISCUSS_LIST_URL)) {
            loadComplete();
            updateView();
            updateListView();
        }
        if (businessType.equals(Url.COMMENT_LIKE_URL)) {
            updateDiscussPraiseNum(mSelfData
                    .getJSONObject(HttpConstants.Data.SubmitDiscussPraise.DISCUSS_PRIASE_INFO));
        }
    }

    public void updateNickName() {
        if (!TextUtils.isEmpty(mSendStoryCommentsDialog.getNickName())) {
            UserInfoManager.getInstance().setNickName(mSendStoryCommentsDialog.getNickName());
        }
    }

    private void updateDiscussPraiseNum(JSONObject jsonObject) {
        mAdapter.updateItemView(jsonObject.optString(HttpConstants.Data.SubmitDiscussPraise.ID));
    }

    private void updateListView() {
        String label = AndroidUtils.getCurrentTimeStr(getSelfContext());
        mListView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
    }

    private void sendDiscussSucuss(JSONObject id) {
        addItemListView(id);
        mIsCommentsSending = false;
//        mCommentsProgressBar.setProgressBarVisible(false);
//        mSendStoryCommentsDialog.setCommentContent("");
//        updateCommentsContent();
        Toast.makeText(this, R.string.send_success, Toast.LENGTH_SHORT).show();
        mSendDiscussCount++;
    }

    private void addItemListView(JSONObject id) {
        LogUtils.log("NetUtill", "refresh");
        requestData(1);
//        try {
//            JSONObject object = new JSONObject();
//            object.put(HttpConstants.Data.DiscussList.CONTENT, mSendStoryCommentsDialog.getCommentContent());
//            object.put(HttpConstants.Data.DiscussList.TIME, getResources().getString(R.string.just_a_moment));
//            object.put(HttpConstants.Data.DiscussList.ID, id.optString(HttpConstants.Data.DiscussList.ID));
//            object.put(HttpConstants.Data.DiscussList.PRAISE, "0");
//            if (TextUtils.isEmpty(mSendStoryCommentsDialog.getNickName())) {
//                object.put(HttpConstants.Data.DiscussList.NIKE_NAME, LocationUtils.getNickName(this));
//            } else {
//                object.put(HttpConstants.Data.DiscussList.NIKE_NAME, mSendStoryCommentsDialog.getNickName());
//            }
//            if (mAdapter.addItem(object)) {
//                hideNoDataView();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
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

    private void updateView() {
        hidePageLoading();
        JSONObject jsonObject = mSelfData
                .getJSONObject(HttpConstants.Data.DiscussList.LIST_INFO_JO + mTypeId);
        try {
            if (jsonObject == null || jsonObject.length() < 1) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray js = jsonObject.optJSONArray(HttpConstants.Data.DiscussList.LIST);
        mIsHasNextPage = jsonObject.optBoolean(HttpConstants.Data.DiscussList.HAS_NEXT);
        mCurpage = jsonObject.optInt(HttpConstants.Data.DiscussList.CURPAGE);
        mAdapter.setData(js);
        if (mCurpage == 1) {
            mAdapter.clearPraisedData();
        }
        if (isShowNoDataView()) {
            initNoDataView();
            return;
        }
        hideNoDataView();
    }

    private boolean isShowNoDataView() {
        return mAdapter.getCount() == 0 && mNoDataView == null;
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {

        if (businessType.equals(Url.STORY_COMMENT_URL)) {
            mSendStoryCommentsDialog.showSendBtn();
            if (!TextUtils.isEmpty(errorOn) && (errorOn.equals("1") || errorOn.equals("2"))) {
                mSendStoryCommentsDialog
                        .setStatus(Constants.CommentDialogStaus.STAUS_CONTAIN_SENSITIVE_WORDS);
                mSendStoryCommentsDialog.setSensitiveWords(errorInfo);
                mSendStoryCommentsDialog.setCommentContent("");
//                updateCommentsContent();
            } else {
                mSendStoryCommentsDialog.setStatus(Constants.CommentDialogStaus.STATUS_SEND_ERROR);
            }

        }
        if (businessType.equals(Url.DISCUSS_LIST_URL)) {
            super.onErrorResult(businessType, errorOn, errorInfo, session);
            loadComplete();
            hidePageLoading();
            if (isShowNoDataView()) {
                initNoDataView();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finishActivity();
                break;
            case R.id.comments_progress_bar:
                if (isFastDoubleClick()) {
                    return;
                }
//                if (mIsCommentsSending) {
//                    AndroidUtils.showToast(this, R.string.sending_pls_wait, 1000);
//                    return;
//                }
                showSendStroyCommentsDialog("", "");
                break;

            case R.id.send_discuss:

                sendComment();
//                sendStoryDiscuss();
                break;
            case R.id.discuss_praise:
                MyBean bean = (MyBean) v.getTag();
                sumbmitDiscussPraise(bean.getString(HttpConstants.Data.DiscussList.ID));
                StatService.onEvent(this, BaiduStatConstants.TALE_PRAISE, mTypeId);
                break;
            default:
                break;
        }
    }

    public void sendComment() {
        if (AndroidUtils.getNetworkType(GNDiscussDetailsActivity.this) == Constants.NET_UNABLE) {
            showNetErrorToast();
            return;
        }
        try {
            if (mSendStoryCommentsDialog.getCommentContent().length() < 2) {
                Toast.makeText(this, R.string.please_say_more, Toast.LENGTH_SHORT).show();
                StatService.onEvent(this, BaiduStatConstants.SEND, BaiduStatConstants.MIN);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.please_say_more, Toast.LENGTH_SHORT).show();
            StatService.onEvent(this, BaiduStatConstants.SEND, BaiduStatConstants.MIN);
            return;
        }
        mSendStoryCommentsDialog.sendComments();
        StatService.onEvent(this, BaiduStatConstants.SEND, BaiduStatConstants.NORMAL);
    }

    private void sumbmitDiscussPraise(String id) {
        LogUtils.log(TAG, LogUtils.getFunctionName() + id);
        mAction.submitDiscussPriase(this, HttpConstants.Data.SubmitDiscussPraise.DISCUSS_PRIASE_INFO, id);
    }

    private void showSendStroyCommentsDialog(String commentId, String nickName) {
        String content = null;
        if (mSendStoryCommentsDialog != null) {
            content = mSendStoryCommentsDialog.getCommentContent();
        }
        mSendStoryCommentsDialog = new SendStoryCommentsDialog(this,
                Constants.CommentDialogType.COMMENT_STORY_TYPE, content);
        mSendStoryCommentsDialog.setMainId(String.valueOf(mTypeId));
        if (!TextUtils.isEmpty(nickName)) {
            mSendStoryCommentsDialog.setmAnswerNickName(nickName);
        }
        if (!TextUtils.isEmpty(commentId)) {
            mSendStoryCommentsDialog.setSecondId(commentId);
        } else {
            mSendStoryCommentsDialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
//                    updateCommentsContent();
                }
            });
        }
        mSendStoryCommentsDialog.setmRightBtnListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCommentsProgressBar.setProgressBarVisible(true);
                mIsCommentsSending = true;
            }
        });
        mSendStoryCommentsDialog.show();
        StatService.onEvent(this, BaiduStatConstants.COMMENT_BOX, BaiduStatConstants.DISCUSS);
    }

//    private void sendStoryDiscuss() {
//        if (AndroidUtils.getNetworkType(GNDiscussDetailsActivity.this) == Constants.NET_UNABLE) {
//            Toast.makeText(this, R.string.upgrade_no_net, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        try {
//            if (mSendStoryCommentsDialog.getCommentContent().length() < 2) {
//                Toast.makeText(this, R.string.please_say_more, Toast.LENGTH_SHORT).show();
//                StatService.onEvent(this, BaiduStatConstants.SEND, BaiduStatConstants.MIN);
//                return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, R.string.please_say_more, Toast.LENGTH_SHORT).show();
//            StatService.onEvent(this, BaiduStatConstants.SEND, BaiduStatConstants.MIN);
//            return;
//        }
//        LogUtils.log(TAG, LogUtils.getFunctionName());
//        mAction.publishStoryComments(GNDiscussDetailsActivity.this,
//                mSendStoryCommentsDialog.getCommentContent(), mTypeId, mSendStoryCommentsDialog.getNickName());
//        mCommentsProgressBar.setProgressBarVisible(true);
//        mIsCommentsSending = true;
//        StatService.onEvent(this, BaiduStatConstants.SEND, BaiduStatConstants.NORMAL);
//    }

//    private void updateCommentsContent() {
//        if (mCommentsProgressBar != null && mSendStoryCommentsDialog != null) {
//            String contentStr = mSendStoryCommentsDialog.getCommentContent();
//            if (!TextUtils.isEmpty(contentStr)) {
//                mCommentsProgressBar.setCommentsContent(mSendStoryCommentsDialog.getCommentContent());
//                int color = getResources().getColor(R.color.comments_text_color);
//                mCommentsProgressBar.setCommentsColor(color);
//            } else {
//                mCommentsProgressBar.setCommentsContent(getString(R.string.publish_comment));
//                int color = getResources().getColor(R.color.comments_text_nor);
//                mCommentsProgressBar.setCommentsColor(color);
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
        finishActivity();
        super.onBackPressed();
    }

    private void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra(Constants.SEND_DISCUSS_COUNT, mSendDiscussCount);
        setResult(Constants.ActivityResultCode.RESULT_CODE_DISCUSS, intent);
        finish();
        AndroidUtils.exitActvityAnim(this);

    }
}
