/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-4-1 上午11:18:50
 */
package com.gionee.client.activity.question;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.sharesdk.framework.ShareSDK;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.story.SendStoryCommentsDialog;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.shareTool.ShareTool;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.StringUtills;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.shoppingmall.QuestionCommentsProgressBar;
import com.gionee.client.view.shoppingmall.QuestionDetailList;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;
import com.gionee.framework.operation.net.GNImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * com.gionee.client.activity.question.QuestionDetailActivity
 * 
 * @author yangxiong <br/>
 * @date create at 2015-4-1 上午11:18:50
 * @description TODO 问答详情
 */
public class QuestionDetailActivity extends BaseFragmentActivity implements OnClickListener {
    private static final String ERROR_SENSITIVE_WORD = "2";
    private static final String URL = "url";
    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private static final String IMAGE_URL = "imageUrl";
    private static final String TAG = "QuestionDetailActivity";
    private String mQuestionId;
    private QuestionDetailList mQuestionList;
    private SendStoryCommentsDialog mSendStoryCommentsDialog;
    private QuestionCommentsProgressBar mCommentsProgressBar;
    private ImageView mShareBtn;
    private boolean mIsCommentsSending;

    public QuestionDetailList getmQuestionList() {
        return mQuestionList;
    }

    public void setShareInfo(final String title, final String description, final String imageUrl,
            final String url) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        MyBean bean = MyBeanFactory.createEmptyBean();
        bean.put(TITLE, title);
        bean.put(DESCRIPTION, description);
        bean.put(IMAGE_URL, imageUrl);
        bean.put(URL, url);
        mShareBtn.setTag(bean);
    }

    @Override
    public void onClick(View v) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (isFastDoubleClick()) {
            return;
        }
        final MyBean bean = (MyBean) v.getTag();
        switch (v.getId()) {
            case R.id.comments_progress_bar:
                showCommentsDialog(mQuestionId, null, null);
                StatService.onEvent(QuestionDetailActivity.this, BaiduStatConstants.QUESTION_ANSWER_INPUT,
                        BaiduStatConstants.QUESTION_ANSWER_INPUT);
                break;
            case R.id.share_btn:
                StatService.onEvent(QuestionDetailActivity.this, BaiduStatConstants.QUESTIONG_DETAIL_SHARE,
                        BaiduStatConstants.QUESTIONG_DETAIL_SHARE);
                if (bean == null) {
                    AndroidUtils.showShortToast(this, R.string.first_refresh_then_share);
                    return;
                }
                showShareDialog(mShareBtn, this);
                break;
            case R.id.share_friends:
                shareToWeixin(true, bean.getString(TITLE), bean.getString(DESCRIPTION),
                        bean.getString(IMAGE_URL), bean.getString(URL));
                closeShareDialog();
                break;
            case R.id.share_weixin:
                shareToWeixin(false, bean.getString(TITLE), bean.getString(DESCRIPTION),
                        bean.getString(IMAGE_URL), bean.getString(URL));
                closeShareDialog();
                break;
            case R.id.share_weibo:
                showToWeibo(bean);
                closeShareDialog();
                break;
            case R.id.share_qq_friend:
                shareToQq(ShareTool.PLATFORM_QQ_FRIEND, bean.getString(TITLE), bean.getString(DESCRIPTION),
                        bean.getString(IMAGE_URL), bean.getString(URL));
                closeShareDialog();
                break;
            case R.id.share_qq_zone:
                shareToQq(ShareTool.PLATFORM_QQ_ZONE, bean.getString(TITLE), bean.getString(DESCRIPTION),
                        bean.getString(IMAGE_URL), bean.getString(URL));
                closeShareDialog();
                break;
            default:
                break;
        }
    }

    private class ShareImageListener implements ImageLoadingListener {
        private MyBean mMyBean;
        public ShareImageListener(MyBean myBean) {
            super();
            this.mMyBean = myBean;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            LogUtils.log(TAG, LogUtils.getThreadName());
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            LogUtils.log(TAG, LogUtils.getThreadName());
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            shareToWeibo(mMyBean.getString(TITLE), mMyBean.getString(DESCRIPTION), loadedImage,
                    mMyBean.getString(URL));
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            LogUtils.log(TAG, LogUtils.getThreadName());
        }
    }

    private void showToWeibo(final MyBean bean) {
        String imageUrl = bean.getString(IMAGE_URL);
        if (!TextUtils.isEmpty(imageUrl)) {
            GNImageLoader.getInstance().loadBitmap(imageUrl, new ImageView(this),
                    new ShareImageListener(bean));
        } else {
            shareToWeibo(bean.getString(TITLE), bean.getString(DESCRIPTION),
                    AndroidUtils.drawable2Bitmap(getResources().getDrawable(R.drawable.ic_launcher)),
                    bean.getString(URL));
        }
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onSucceed(businessType, isCache, session);
        if (businessType.equals(Url.ANSWER_QUESTION_URL)) {
            mIsCommentsSending = false;
            updateNickName();
            AndroidUtils.showShortToast(this, R.string.answer_sent);
            mCommentsProgressBar.setProgressBarVisible(false);
            mSendStoryCommentsDialog.setCommentContent("");
            updateCommentsContent();
            mQuestionList.refreshCurrentPageData();
            mQuestionList.pullDownToRefresh();
        }
    }

    public void updateNickName() {
        if (!TextUtils.isEmpty(mSendStoryCommentsDialog.getNickName())) {
            UserInfoManager.getInstance().setNickName(mSendStoryCommentsDialog.getNickName());
        }
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        if (businessType.equals(Url.ANSWER_QUESTION_URL)) {
            mIsCommentsSending = false;
            mCommentsProgressBar.setProgressBarVisible(false);
            // 敏感词匹配失败
            if (!TextUtils.isEmpty(errorOn) && errorOn.equals(ERROR_SENSITIVE_WORD)) {
                mSendStoryCommentsDialog.setCommentContent("");
                updateCommentsContent();
            }
            if (!StringUtills.isNotContainEnglish(errorInfo)) {
                AndroidUtils.showShortToast(this, R.string.answer_fail);
            }
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(arg0);
        setContentView(R.layout.question_detail);
        initTitleBar();
        initData();
        initview();
    }

    @Override
    protected void onResume() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onResume();
        closeProgressDialog();
    }

    @Override
    protected void onDestroy() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }

    @Override
    public void onBackPressed() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onBackPressed();
        AndroidUtils.finishActivity(this);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mQuestionId = intent.getStringExtra(HttpConstants.Data.QuestionList.ID);
        }
        ShareSDK.initSDK(this);
    }

    private void initTitleBar() {
        GNTitleBar titleBar = getTitleBar();
        titleBar.setVisibility(View.VISIBLE);
        titleBar.setTitle(R.string.question_detail);
    }

    private void initview() {
        mQuestionList = (QuestionDetailList) findViewById(R.id.question_list_view);
        mQuestionList.setQuestionId(mQuestionId);
        mCommentsProgressBar = (QuestionCommentsProgressBar) findViewById(R.id.comments_progress_bar);
        mCommentsProgressBar.setOnClickListener(this);
        mShareBtn = (ImageView) findViewById(R.id.share_btn);
        mShareBtn.setOnClickListener(this);
    }

    public void showCommentsDialog(String questionId, String replyId, String nickName) {
        if (mIsCommentsSending) {
            AndroidUtils.showToast(this, R.string.answer_sending_pls_wait, 1000);
            return;
        }
        String commentsContent = getAnswerContent();
        produceAnswerDialog(commentsContent);
        showAnswerDialog(questionId, replyId, nickName);
    }

    private void showAnswerDialog(String questionId, String replyId, String nickName) {
        if (!TextUtils.isEmpty(nickName)) {
            mSendStoryCommentsDialog.setmAnswerNickName(nickName);
        }
        if (!TextUtils.isEmpty(questionId)) {
            mSendStoryCommentsDialog.setMainId(questionId);
        }
        if (!TextUtils.isEmpty(replyId)) {
            mSendStoryCommentsDialog.setSecondId(replyId);
        }
        mSendStoryCommentsDialog.show();
    }

    private void produceAnswerDialog(String commentsContent) {
        mSendStoryCommentsDialog = new SendStoryCommentsDialog(this,
                Constants.CommentDialogType.COMMENT_QUESTION_TYPE, commentsContent);
        mSendStoryCommentsDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                updateCommentsContent();
            }
        });

        mSendStoryCommentsDialog.setmRightBtnListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                mIsCommentsSending = true;
                mCommentsProgressBar.setProgressBarVisible(true);
            }
        });
    }

    private String getAnswerContent() {
        String commentsContent = null;
        if (mSendStoryCommentsDialog != null) {
            commentsContent = mSendStoryCommentsDialog.getCommentContent();
        }
        return commentsContent;
    }

    private void updateCommentsContent() {
        if (mCommentsProgressBar != null && mSendStoryCommentsDialog != null) {
            String contentStr = mSendStoryCommentsDialog.getCommentContent();
            if (!TextUtils.isEmpty(contentStr)) {
                mCommentsProgressBar.setCommentsContent(contentStr);
                int color = getResources().getColor(R.color.comments_text_color);
                mCommentsProgressBar.setCommentsColor(color);
            } else {
                mCommentsProgressBar.setCommentsContent(getString(R.string.let_me_answer));
                int color = getResources().getColor(R.color.comments_text_nor);
                mCommentsProgressBar.setCommentsColor(color);
            }
        }
    }

}
