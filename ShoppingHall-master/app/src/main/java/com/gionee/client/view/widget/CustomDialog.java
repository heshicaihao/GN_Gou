// Gionee <yuwei><2013-7-15> add for CR00836967 begin
/*
 * CustomDialog.java
 * classes : com.gionee.channelshopping.util.CustomDialog
 * @author yuwei
 * V 1.0.0
 * Create at 2013-7-8 下午1:48:18
 */
package com.gionee.client.view.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.business.util.AndroidUtils;

/**
 * com.gionee.channelshopping.util.CustomDialog
 * 
 * @author yuwei <br/>
 *         create at 2013-7-8 下午1:48:18
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    /**
     * dialog confirm button
     */
    private Button mConfirmBtn;
    /**
     * dialog cancel button
     */
    private Button mCancelBtn;
    /**
     * dialog message text
     */
    private TextView mMsgText;
    /**
     * dialog title text
     */
    private TextView mTitleText;
    /**
     * the listener of confirm button
     */
    private OnPositiveListener mOnPositiveListener;
    /**
     * the listener of cancel button
     */
    private OnNegativeListener mOnNegativeListener;
    /**
     * the checkBox
     */
    private CheckBox mCheckBox;

    /**
     * activity context
     */
    private Activity mContext;
    /**
     * the msg layout
     */
    private LinearLayout mLinearLayout;

    /**
     * @return the confirmListener
     */
    public OnPositiveListener getOnPositiveListener() {
        return mOnPositiveListener;
    }

    /**
     * @param confirmListener
     *            the confirmListener to set
     */
    public void setOnPositiveListener(OnPositiveListener onPositiveListener) {
        this.mOnPositiveListener = onPositiveListener;
    }

    /**
     * @return the cancelListener
     */
    public OnNegativeListener getOnNegativeListener() {
        return mOnNegativeListener;
    }

    /**
     * @param onNegativeListener
     *            the onNegativeListener to set
     */
    public void setOnNegativeListener(OnNegativeListener onNegativeListener) {
        this.mOnNegativeListener = onNegativeListener;
    }

    /**
     * void
     * 
     * @param msg
     *            弹出信息 TODO
     */
    public void setMsg(int msgId) {
        mMsgText.setText(msgId);
    }

    public void setMsg(String msg) {
        mMsgText.setText(msg);
    }

    public void setChecked(boolean isCheckd) {
        mCheckBox.setChecked(isCheckd);
    }

    public boolean getChecked() {
        return mCheckBox.isChecked();
    }

    /**
     * @param isVisible
     *            设置checkBox可见性
     */
    public void setCheckVisible(boolean isVisible) {
        if (isVisible) {
            mCheckBox.setVisibility(View.VISIBLE);
        } else {
            mCheckBox.setVisibility(View.GONE);
        }

    }

    public void setLeftBtnText(int leftTitleId) {
        mCancelBtn.setText(leftTitleId);

    }

    public void setRightBtnText(int rightTitleId) {
        mConfirmBtn.setText(rightTitleId);
    }

    /**
     * @param titleId
     *            设置标题
     */
    @Override
    public void setTitle(int titleId) {
        mTitleText.setText(titleId);
    }

    /**
     * @param titleId
     *            设置checkbox文字
     */
    public void setCheckBoxText(CharSequence text) {
        mCheckBox.setText(text);

    }

    public void setCheckBoxListener(View.OnClickListener l) {
        mCheckBox.setOnClickListener(l);
    }

    /**
     * @param context
     */
    public CustomDialog(Activity context) {
        super(context, R.style.custom_dialog_style);
        this.mContext = context;
        setContentView(R.layout.custom_dialog);
        initView();
        setLayoutParams();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setMSGHeight() {
        android.view.ViewGroup.LayoutParams lp = mLinearLayout.getLayoutParams();
        lp.height = AndroidUtils.getDisplayHeight(mContext) / 2;
    }

    /**
     * 初始化View
     */
    private void initView() {
        mConfirmBtn = (Button) findViewById(R.id.dialog_certain_btn);
        mCancelBtn = (Button) findViewById(R.id.dialog_cancel_btn);
        mTitleText = (TextView) findViewById(R.id.title);
        mCheckBox = (CheckBox) findViewById(R.id.check_box);
        mMsgText = (TextView) findViewById(R.id.now_version);
        mLinearLayout = (LinearLayout) findViewById(R.id.dialog_msg);
        mConfirmBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

    /**
     * 设置对话框大小及位置
     */
    private void setLayoutParams() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = AndroidUtils.getDisplayWidth(mContext) - 80;
        getWindow().setAttributes(params);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_certain_btn:
                if (mOnPositiveListener != null) {
                    mOnPositiveListener.onClick(mCheckBox.isChecked());
                }
                if (mContext != null && !mContext.isFinishing()) {
                    this.dismiss();
                }

                break;
            case R.id.dialog_cancel_btn:
                if (mOnNegativeListener != null) {
                    mOnNegativeListener.onClick();
                }
                if (mContext != null && !mContext.isFinishing()) {
                    this.dismiss();
                }

                break;
            default:
                break;
        }
    }

    /**
     * com.gionee.channelshopping.util.OnPositiveListener
     * 
     * @author yuwei <br/>
     *         create at 2013-7-8 下午2:12:36 对话框确定接口
     */
    public interface OnPositiveListener {
        /**
         * @param isChecked
         *            even radio is checked
         */
        void onClick(boolean isChecked);
    }

    public interface OnNegativeListener {
        void onClick();
    }

}
//Gionee <yuwei><2013-7-16> add for CR00836967 end