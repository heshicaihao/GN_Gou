/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-3-30 上午11:29:55
 */
package com.gionee.client.activity.question;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.view.shoppingmall.AbstractBaseList;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.shoppingmall.GNTitleBar.OnRightBtnListener;

/**
 * com.gionee.client.activity.question.QuestionListActivity
 * 
 * @author yangxiong <br/>
 * @date create at 2015-3-30 上午11:29:55
 * @description TODO 问答列表
 */
public class QuestionListActivity extends BaseFragmentActivity {
    private static final String TAG = "QuestionListActivity";
    protected AbstractBaseList mQuestionList;

    @Override
    protected void onCreate(Bundle arg0) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(arg0);
        setContentView(R.layout.question_list);
        initTitleBar();
        initview();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            StatService.onEvent(QuestionListActivity.this, BaiduStatConstants.QUSTIONG_LIST_BACK,
                    BaiduStatConstants.FOOT_BACK);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " request code = " + arg0 + ", result code = " + arg1);
        super.onActivityResult(arg0, arg1, arg2);
        switch (arg0) {
            case Constants.ActivityRequestCode.REQUEST_CODE_QUESTION_DETAIL:
                mQuestionList.refreshCurrentPageData();
                break;
            default:
                break;
        }
    }

    private void initTitleBar() {
        final GNTitleBar titleBar = getTitleBar();
        titleBar.setVisibility(View.VISIBLE);
        titleBar.setTitle(R.string.question_list);
        titleBar.setRightBtnText(R.string.ask_question);
        titleBar.setRightBtnVisible(true);
        titleBar.setRightListener(mAskBtnListener);
        showShadow(false);
        titleBar.getBackBtn().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                titleBar.onClick(v);
                switch (v.getId()) {
                    case R.id.iv_back:
                        StatService.onEvent(QuestionListActivity.this, BaiduStatConstants.QUSTIONG_LIST_BACK,
                                BaiduStatConstants.TOP_BACK);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private OnRightBtnListener mAskBtnListener = new OnRightBtnListener() {
        @Override
        public void onClick() {
            LogUtils.log(TAG, LogUtils.getThreadName());
            askQuestion();
        }
    };

    private void askQuestion() {
        Intent intent = new Intent(this, AskQuestionActivity.class);
        StatService.onEvent(QuestionListActivity.this, BaiduStatConstants.QUESTION_QUSTION,
                BaiduStatConstants.QUESTION_QUSTION);
        startActivity(intent);
    }

    private void initview() {
        mQuestionList = (AbstractBaseList) findViewById(R.id.question_list_view);
    }
}
