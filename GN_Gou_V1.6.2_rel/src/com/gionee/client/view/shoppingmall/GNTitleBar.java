// Gionee <yuwei><2014-8-15> add for CR00821559 begin
/*
 * GNTitleBar.java
 * classes : com.gionee.client.view.shoppingmall.GNTitleBar
 * @author yuwei
 * V 1.0.0
 * Create at 2014-8-15 下午4:51:19
 */
package com.gionee.client.view.shoppingmall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.business.util.AndroidUtils;

/**
 * com.gionee.client.view.shoppingmall.GNTitleBar resource
 * 
 * @author yuwei <br/>
 * @date create at 2014-8-15 下午4:51:19
 * @description TODO
 */
@SuppressLint("Recycle")
public class GNTitleBar extends GNBaseView {
    private ImageView mBackBtn;
    private TextView mTitle;
    private Button mRightBtn;
    private OnRightBtnListener mListener;
    private View mTopPaddingView;

    public GNTitleBar(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public GNTitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initAttributeSet(context, attrs);
    }

    public GNTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initAttributeSet(context, attrs);
    }

    @Override
    protected View setContent() {
        return LayoutInflater.from(getContext()).inflate(R.layout.title_bar, null);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);

        if (a.hasValue(R.styleable.TitleBar_title)) {
            mTitle.setText(a.getText(R.styleable.TitleBar_title));
        }

        if (a.hasValue(R.styleable.TitleBar_rigntBtnBg)) {
            mRightBtn.setBackgroundDrawable(a.getDrawable(R.styleable.TitleBar_rigntBtnBg));
        }
        if (a.hasValue(R.styleable.TitleBar_rightBtnText)) {
            mRightBtn.setText(a.getText(R.styleable.TitleBar_rightBtnText));
        }
        if (a.hasValue(R.styleable.TitleBar_rightBtnVisible)) {

            boolean isVisible = a.getBoolean(R.styleable.TitleBar_rightBtnVisible, false);

            if (isVisible) {
                mRightBtn.setVisibility(View.VISIBLE);
            } else {
                mRightBtn.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void initView() {
        mBackBtn = (ImageView) mContentView.findViewById(R.id.iv_back);
        mTitle = (TextView) mContentView.findViewById(R.id.tv_title);
        mRightBtn = (Button) mContentView.findViewById(R.id.title_right_btn);
        mTopPaddingView = findViewById(R.id.top_title_view);
        mBackBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
    }

    public void setTopPadding() {
        mTopPaddingView.setVisibility(View.VISIBLE);
    }

    public void setTitle(int id) {
        mTitle.setText(id);

    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setRightBtnVisible(boolean visible) {
        if (visible) {
            mRightBtn.setVisibility(VISIBLE);

        } else {
            mRightBtn.setVisibility(GONE);
        }

    }

    public void setRightBtnText(int textResId) {
        mRightBtn.setText(textResId);

    }

    public void setRightBtnBg(int drawableResId) {
        mRightBtn.setBackgroundResource(drawableResId);
    }

    public void setRightBtnTextColor(int colorResId) {
        mRightBtn.setTextColor(colorResId);
    }

    public ImageView getBackBtn() {
        return mBackBtn;
    }

    public Button getRightBtn() {
        return mRightBtn;
    }

    public void setRightListener(OnRightBtnListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_back:
                ((Activity) getContext()).onBackPressed();
                AndroidUtils.exitActvityAnim((Activity) getContext());
                break;
            case R.id.title_right_btn:
                if (mListener != null) {
                    mListener.onClick();
                }
                break;

            default:
                break;
        }
    }

    public interface OnRightBtnListener {
        void onClick();
    }
}
