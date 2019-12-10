/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-10-15 下午02:53:16
 */
package com.gionee.client.view.shoppingmall;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;

/**
 * @author yangxiong <br/>
 * @date create at 2014-10-15 下午02:53:16
 * @description TODO 显示知物的评论以及进度条
 */
public class CommentsProgressBar extends GNBaseView {
    private static final String TAG = CommentsProgressBar.class.getSimpleName();

    private TextView mCommentsContent;
    private ProgressBar mProgressBar;

    public CommentsProgressBar(Context context) {
        super(context);
    }

    public CommentsProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CommentsProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View setContent() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return LayoutInflater.from(getContext()).inflate(R.layout.comments_progress_bar, null);
    }

    @Override
    protected void requestData() {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    protected void initView() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mCommentsContent = (TextView) mContentView.findViewById(R.id.comments_content);
        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.loading_bar);
    }

    public void setCommentsContent(int resId) {
        mCommentsContent.setText(resId);
    }

    public void setCommentsContent(String title) {
        mCommentsContent.setText(title);
    }

    public void setCommentsColor(int color) {
        mCommentsContent.setTextColor(color);
    }
    

    public void setProgressBarVisible(boolean visible) {
        if (visible) {
            mProgressBar.setVisibility(VISIBLE);
        } else {
            mProgressBar.setVisibility(GONE);
        }
    }
}
