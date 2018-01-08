// Gionee <yuwei><2014-10-14> add for CR00821559 begin
/*
 * SendStoryDialog.java
 * classes : com.gionee.client.activity.story.SendStoryDialog
 * @author yuwei
 * V 1.0.0
 * Create at 2014-10-14 下午3:21:57
 */
package com.gionee.client.activity.story;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.net.NetUtil;

/**
 * com.gionee.client.activity.story.SendStoryDialog
 * 
 * @author yuwei <br/>
 * @date create at 2014-10-14 下午3:21:57
 * @description TODO
 */
public class SendStoryCommentsDialog extends Dialog implements android.view.View.OnClickListener,
        OnFocusChangeListener {
    /**
     * 
     */
    private static final int MIN_NICK_NAME_SIZE = 1;
    private static final String TAG = "SendStoryCommentsDialog";
    private Activity mActivity;
    private Button mNickNameBtn;
    private Button mSendBtn;
    private EditText mEditNickName;
    private EditText mEditComment;
    private String mCommentContent = "";
    private String mNickName;
    private RequestAction mRequestAction;
    private ImageView mClearContent;
    private TextView mTitle;
    private RelativeLayout mNickNameLayout;
    private ProgressBar mSenddingProgressBar;

    private String mMainId = "0";
    private String mSecondId = "0";
    private android.view.View.OnClickListener mRightBtnListener;
    private int mType;
    private String mAnswerNickName;
    private ImageView mHeadImg;

    private static final int MAX_CONTENT_SIZE = 140;
    private static final int MAX_QUESTION_SIZE = 500;
    private int mStatus;
    private String mSensitiveWords;

    /**
     * @param mRightBtnListener
     *            the mRightBtnListener to set
     */
    public void setmRightBtnListener(android.view.View.OnClickListener mRightBtnListener) {
        this.mRightBtnListener = mRightBtnListener;
    }

    public SendStoryCommentsDialog(Context context, int type) {
        // TODO Auto-generated constructor stub
        super(context, R.style.send_dialog);
        mActivity = (Activity) context;
        mType = type;
    }

    public SendStoryCommentsDialog(Context context, int type, String commentContent) {
        // TODO Auto-generated constructor stub
        super(context, R.style.send_dialog);
        mActivity = (Activity) context;
        mType = type;
        mCommentContent = commentContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.comment_story_dialog);
        setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(true);
        mRequestAction = new RequestAction();
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            initNickName();
            initHeadImg();
            initEditContent();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        focusCommentEdit();
    }

    public void initHeadImg() {
        if (!TextUtils.isEmpty(UserInfoManager.getInstance().getAvatar(mActivity))) {
            GNImageLoader.getInstance().loadBitmap(UserInfoManager.getInstance().getAvatar(mActivity),
                    mHeadImg);
        }
    }

    public void initNickName() {
        if (UserInfoManager.getInstance().isEditEnable(mActivity)) {
            mNickNameBtn.postDelayed(new Runnable() {

                @Override
                public void run() {
                    showNickLayout();
                    mEditComment.requestFocusFromTouch();
                }
            }, 100);

        }
        mEditNickName.setText(UserInfoManager.getInstance().getNickName(mActivity));
    }

    public void initEditContent() {
        LogUtils.log(TAG, LogUtils.getThreadName() + " mCommentContent = " + mCommentContent
                + " mAnswerNickName = " + mAnswerNickName);
        int maxLenth = initMaxLenth();
        if (maxLenth != 0) {
            mEditComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLenth)});
        }
        if (TextUtils.isEmpty(mCommentContent)) {
            if (!TextUtils.isEmpty(mAnswerNickName)) {
                mAnswerNickName = "回复" + mAnswerNickName + ":";
                mEditComment.setHint(mAnswerNickName);
            }
        } else {
            mEditComment.setText(mCommentContent);
            highLightSensitiveWords(mSensitiveWords);
        }
//            SpannableString spanContent = new SpannableString(mAnswerNickName + mCommentContent);
//            spanContent.setSpan(
//                    new ForegroundColorSpan(mActivity.getResources().getColor(R.color.discuss_name_text)), 0,
//                    mAnswerNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void initView() {
        mEditComment = (EditText) findViewById(R.id.comment_story);
        mNickNameBtn = (Button) findViewById(R.id.title_left_btn);
        mSendBtn = (Button) findViewById(R.id.title_right_btn);
        mEditNickName = (EditText) findViewById(R.id.nickname_edit);
        mNickNameLayout = (RelativeLayout) findViewById(R.id.nickname_layout);
        mHeadImg = (ImageView) findViewById(R.id.user_head_img);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mClearContent = (ImageView) findViewById(R.id.clear_content);
        mSenddingProgressBar = (ProgressBar) findViewById(R.id.loading_bar);
        mSendBtn.setOnClickListener(this);
        mNickNameBtn.setOnClickListener(this);
        mClearContent.setOnClickListener(this);
        mEditNickName.setOnFocusChangeListener(this);
        mEditComment.addTextChangedListener(mContentTextWatcher);
        mEditNickName.addTextChangedListener(mNickNameWatcher);
        mEditNickName.setOnEditorActionListener(mNickNameEditActionListener);
        mEditComment.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mEditComment.setOnEditorActionListener(commentEditActionListener());
        mHeadImg.setOnClickListener(this);
        mEditNickName.setOnClickListener(this);
        setTitle();

    }

    public void setSensitiveWords(String sensitiveWords) {
        mSensitiveWords = sensitiveWords;
        LogUtils.log(TAG, LogUtils.getFunctionName() + sensitiveWords);
        mEditComment.postDelayed(new Runnable() {

            @Override
            public void run() {
                setStatus(Constants.CommentDialogStaus.STAUS_CONTAIN_SENSITIVE_WORDS);
            }
        }, 100);
        highLightSensitiveWords(mSensitiveWords);
        mEditComment.postDelayed(new Runnable() {

            @Override
            public void run() {
                mCommentContent = "";
            }
        }, 2200);

    }

    public void highLightSensitiveWords(String sensitiveWords) {
        String[] sensitiveWordsArray = sensitiveWords.split(",");
        String nickName = mEditNickName.getText().toString();
        String content = mEditComment.getText().toString();
        SpannableString nicknameSpan = new SpannableString(nickName);
        SpannableString contentSpan = new SpannableString(content);
        for (int i = 0; i < sensitiveWordsArray.length; i++) {
            try {
                LogUtils.log(TAG, LogUtils.getFunctionName() + "sensitiveWordsArray i=" + i);
                int nickNameLastIndex = 0;
                int contentLastIndex = 0;
                String key = sensitiveWordsArray[i];
                setColorSpan(nickName, nicknameSpan, key, nickNameLastIndex);
                setColorSpan(content, contentSpan, key, contentLastIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mEditComment.setText(contentSpan);
        mEditNickName.setText(nicknameSpan);
        mEditComment.setSelection(contentSpan.length());
        mEditNickName.setSelection(nicknameSpan.length());
    }

    public void setColorSpan(String originalString, SpannableString originalSpan, String key, int lastIndex) {
        try {
            int currentIndex = originalString.indexOf(key);
            int start = currentIndex + lastIndex;
            int end = start + key.length();
            if (currentIndex != -1) {
                ForegroundColorSpan span = new ForegroundColorSpan(getContext().getResources().getColor(
                        R.color.red_high_light));
                LogUtils.log(TAG, LogUtils.getFunctionName() + "start=" + start + "end=" + end);
                originalSpan.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                lastIndex = end;
                String subString = originalString.substring(currentIndex + key.length());
                setColorSpan(subString, originalSpan, key, lastIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.log(TAG, LogUtils.getFunctionName() + "error=" + e.getMessage());
        }
    }

    public void setStatus(int status) {
        mStatus = status;
        switch (status) {
            case Constants.CommentDialogStaus.STATUS_MODIFY_HEAD:
                setTitleNotify(R.string.goto_modify_head);
                break;
            case Constants.CommentDialogStaus.STAUS_CONTAIN_SENSITIVE_WORDS:
                setTitleNotify(R.string.do_not_input_sensitive_word);
                mCommentContent = "";
                break;
            case Constants.CommentDialogStaus.STATUS_NICKNAME_LESS_ONE:
                setTitleNotify(R.string.nickname_less_than_one);
                break;
            case Constants.CommentDialogStaus.STATUS_SEND_ERROR:
                setTitleNotify(R.string.send_error);
                break;
            case Constants.CommentDialogStaus.STATUS_COMMENT_SENDDING:
                mTitle.setText(R.string.comment_sendding);
                mTitle.setEnabled(false);
                break;

            default:
                break;
        }

    }

    public void setTitleNotify(int notifyId) {
        mTitle.setText(notifyId);
        mTitle.setEnabled(false);
        resetNomalStatusDelay();
    }

    /**
     * @return
     * @author yuwei
     * @description TODO
     */
    private OnEditorActionListener commentEditActionListener() {
        return new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComments();
                }

                return false;
            }
        };
    }

    private OnEditorActionListener mNickNameEditActionListener = new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                focusCommentEdit();
            }
            return false;
        }
    };

    public void setGravity(int gravity) {
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = gravity;
        wlp.width = AndroidUtils.getDisplayWidth(mActivity);
        wlp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        window.setAttributes(wlp);
    }

    private TextWatcher mContentTextWatcher = new TextWatcher() {
        private CharSequence mTemp;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            mTemp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            // mTextView.setText(s);//将输入的内容实时显示
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

            if ((mTemp.length() == MAX_CONTENT_SIZE && mType == Constants.CommentDialogType.COMMENT_STORY_TYPE)) {
                mTitle.setText(mActivity.getString(R.string.exceed_max_size));
                mTitle.setEnabled(false);
            }
            if (mTemp.length() == MAX_QUESTION_SIZE
                    && mType == Constants.CommentDialogType.COMMENT_QUESTION_TYPE) {
                if (!TextUtils.isEmpty(mAnswerNickName)) {
                    StatService.onEvent(getContext(), BaiduStatConstants.INPUT_SEND_REPLY, "max");
                } else {
                    StatService.onEvent(getContext(), BaiduStatConstants.INPUT_SEND_ANSWER, "max");
                }
                Toast.makeText(getContext(), R.string.answer_size_extra, Toast.LENGTH_SHORT).show();
            } else {
                setTitle();
                mTitle.setEnabled(true);
            }
        }

    };

    public void setTitle() {
        if (mType == Constants.CommentDialogType.COMMENT_STORY_TYPE) {
            mTitle.setText(R.string.publish_comment);
        } else {
            mTitle.setText(R.string.answer);
        }
    }

    private TextWatcher mNickNameWatcher = new TextWatcher() {
        private CharSequence mTemp;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            mTemp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            // mTextView.setText(s);//将输入的内容实时显示
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

            if (mTemp.length() > 0) {
                mClearContent.setVisibility(View.VISIBLE);
            } else {
                mClearContent.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_btn:
                resetLayout();
                StatService.onEvent(getContext(), BaiduStatConstants.INPUT_NICKNAME_BT,
                        BaiduStatConstants.INPUT_NICKNAME_BT);
                break;
            case R.id.title_right_btn:
                sendComments();
                break;
            case R.id.clear_content:
                mEditNickName.setText("");
                break;
            case R.id.user_head_img:
                setStatus(Constants.CommentDialogStaus.STATUS_MODIFY_HEAD);
                StatService.onEvent(getContext(), BaiduStatConstants.INPUT_HEAD,
                        BaiduStatConstants.INPUT_HEAD);
                break;
            case R.id.nickname_edit:
                StatService.onEvent(getContext(), BaiduStatConstants.INPUT_NICKNAME,
                        BaiduStatConstants.INPUT_NICKNAME);
                break;
            default:
                break;
        }

    }

    public void showSenddingProgress() {
        setStatus(Constants.CommentDialogStaus.STATUS_COMMENT_SENDDING);
        mSendBtn.setVisibility(View.GONE);
        mSenddingProgressBar.setVisibility(View.VISIBLE);
    }

    public void showSendBtn() {
        mSendBtn.setVisibility(View.VISIBLE);
        mSenddingProgressBar.setVisibility(View.GONE);
    }

    public void sendComments() {
        if (!NetUtil.isNetworkAvailable(mActivity)) {
            mEditComment.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (mActivity instanceof ThridPartyWebActivity) {
                        ((ThridPartyWebActivity) mActivity).showNetErrToast();
                    } else {
                        ((BaseFragmentActivity) mActivity).showNetErrorToast();
                    }
                }
            }, 200);
            dismiss();
            return;
        }else {
        	((BaseFragmentActivity) mActivity).showNetErrorToast();
		}
        getEditContent();
        getEditNickName();
        int minLenth = 0;
        minLenth = initMinLenth(minLenth);
        if (TextUtils.isEmpty(mNickName)) {
            setStatus(Constants.CommentDialogStaus.STATUS_NICKNAME_LESS_ONE);
            return;
        }

        if (mCommentContent.length() > minLenth) {
//            statisticQuestionDone();
//            this.dismiss();
            if (mRightBtnListener != null) {
                mRightBtnListener.onClick(mSendBtn);
            }
            sendNetRequest();
            showSenddingProgress();
        } else {
//            statisticQuestionMinSize();
            showSayMoreNotice();
        }
    }

//    public void statisticQuestionDone() {
//        if (TextUtils.isEmpty(mAnswerNickName)) {
//            if (mType == Constants.CommentDialogType.COMMENT_QUESTION_TYPE) {
//                if (!TextUtils.isEmpty(mAnswerNickName)) {
//                    StatService.onEvent(getContext(), BaiduStatConstants.INPUT_SEND_REPLY, "done");
//                } else {
//                    StatService.onEvent(getContext(), BaiduStatConstants.INPUT_SEND_ANSWER, "done");
//                }
//            }
//        }
//    }
//
//    public void statisticQuestionMinSize() {
//        if (mType == Constants.CommentDialogType.COMMENT_QUESTION_TYPE) {
//            if (!TextUtils.isEmpty(mAnswerNickName)) {
//                StatService
//                        .onEvent(getContext(), BaiduStatConstants.INPUT_SEND_REPLY, BaiduStatConstants.MIN);
//            } else {
//                StatService.onEvent(getContext(), BaiduStatConstants.INPUT_SEND_ANSWER,
//                        BaiduStatConstants.MIN);
//            }
//
//        }
//    }

    public int initMinLenth(int minLenth) {
        if (mType == Constants.CommentDialogType.COMMENT_STORY_TYPE) {
            minLenth = 1;
        } else if (mType == Constants.CommentDialogType.COMMENT_QUESTION_TYPE) {
            minLenth = 1;

        }
        return minLenth;
    }

    private int initMaxLenth() {
        int maxLenth = 0;
        if (mType == Constants.CommentDialogType.COMMENT_STORY_TYPE) {
            maxLenth = MAX_CONTENT_SIZE;
        } else if (mType == Constants.CommentDialogType.COMMENT_QUESTION_TYPE) {
            maxLenth = MAX_QUESTION_SIZE;

        }
        return maxLenth;
    }

    public void showSayMoreNotice() {
        setSayMoreNotify();
        StatService.onEvent(mActivity, "dig_send", "min");
        resetNomalStatusDelay();

    }

    public void resetNomalStatusDelay() {
        mTitle.postDelayed(new Runnable() {

            @Override
            public void run() {
                resetNomalStatus();
            }

        }, 2000);
    }

    public void resetNomalStatus() {
        setTitle();
        mTitle.setEnabled(true);
        mStatus = Constants.CommentDialogStaus.STATUS_NOMAL;
        showSendBtn();
    }

    public void setSayMoreNotify() {
        if (mType == Constants.CommentDialogType.COMMENT_STORY_TYPE) {
            mTitle.setText(R.string.please_say_more);
            mTitle.setEnabled(false);
        } else {
            Toast.makeText(getContext(), R.string.answer_more_lenth, Toast.LENGTH_SHORT).show();
        }
    }

    public void sendNetRequest() {
//        LogUtils.log("sendComments", mCommentContent);
//        LogUtils.log("sendComments", "unicode=" + convertToMsg(mEditComment.getText(), mActivity));
        if (mType == Constants.CommentDialogType.COMMENT_STORY_TYPE) {
            mRequestAction.publishStoryComments((IBusinessHandle) mActivity, mCommentContent, mMainId,
                    getNickName(), mSecondId);
            StatService.onEvent(mActivity, "dig_send", "nor");
        } else if (mType == Constants.CommentDialogType.COMMENT_QUESTION_TYPE) {
            mRequestAction.answerQuestion((IBusinessHandle) mActivity, mCommentContent, getNickName(),
                    mMainId, mSecondId);
        }

    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void resetLayout() {
        if (mNickNameLayout.isShown()) {
            hideNickLayout();

        } else {
            showNickLayout();
            StatService.onEvent(mActivity, BaiduStatConstants.COMMENT_BOX, BaiduStatConstants.NICK_NAME);
        }
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void showNickLayout() {
        try {
            mNickNameLayout.setVisibility(View.VISIBLE);
            mEditNickName.requestFocusFromTouch();
            mEditNickName.setSelection(mEditNickName.getText().toString().length());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void hideNickLayout() {
        try {
            mNickNameLayout.setVisibility(View.GONE);
            focusCommentEdit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void focusCommentEdit() {
        mEditComment.requestFocusFromTouch();
        mEditComment.setSelection(mEditComment.getText().toString().length());
        mNickNameLayout.setVisibility(View.GONE);
    }

    public void setMainId(String storyId) {
        mMainId = storyId;

    }

    public void setSecondId(String secondId) {
        mSecondId = secondId;

    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void getEditContent() {
        mCommentContent = mEditComment.getText().toString().trim();
    }

    private void getEditNickName() {
        mNickName = mEditNickName.getText().toString().trim();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        getEditContent();
    }

    public String getNickName() {
        return mNickName;

    }

    public String getCommentContent() {
        return mCommentContent;
    }

    public void setCommentContent(String content) {
        mCommentContent = content;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mEditNickName.isFocused() && !TextUtils.isEmpty(mEditNickName.getText())) {
            mClearContent.setVisibility(View.VISIBLE);
        } else {
            mClearContent.setVisibility(View.GONE);
        }

    }

    public void setmAnswerNickName(String mAnswerNickName) {
        this.mAnswerNickName = mAnswerNickName;
    }
}
