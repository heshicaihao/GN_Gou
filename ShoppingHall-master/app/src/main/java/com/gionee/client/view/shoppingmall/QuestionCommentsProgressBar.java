/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-4-1 下午03:09:39
 */
package com.gionee.client.view.shoppingmall;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;

/**
 * com.gionee.client.view.shoppingmall.QuestionDetailCommentsProgressBar
 * 
 * @author yangxiong <br/>
 * @date create at 2015-4-1 下午03:09:39
 * @description TODO 问题详情底部评论入口（带进度条显示）
 */
public class QuestionCommentsProgressBar extends CommentsProgressBar {
    private static final String TAG = "QuestionDetailCommentsProgressBar";

    public QuestionCommentsProgressBar(Context context) {
        super(context);
    }

    public QuestionCommentsProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuestionCommentsProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View setContent() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return LayoutInflater.from(getContext()).inflate(R.layout.question_detail_comments_progressbar, null);
    }
}
