package com.gionee.client.view.widget;

import java.text.NumberFormat;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.business.upgradeplus.common.UpgradeUtils;
import com.gionee.client.business.util.LogUtils;

/**
 * com.gionee.client.widget.CustomProgressDialog
 * 
 * @author hcy <br/>
 *         create at 2013-7-29 下午4:00:25
 */
public class CustomProgressDialog extends Dialog implements OnClickListener {

    private static final String TAG = "CustomProgressDialog";
    private TextView mProgressTitle;
    private TextView mProgressPercent;
    private TextView mProgressNumber;
    private ProgressBar mProgressBar;
    private NumberFormat mProgressPercentFormat;

    /**
     * @param context
     */
    public CustomProgressDialog(Activity context) {
        super(context, R.style.Dialog);
        setContentView(R.layout.custom_progress_dialog);
        initView();
        setLayoutParams();
        initFormats();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    public void setMSGHeight() {
//        android.view.ViewGroup.LayoutParams lp = mLinearLayout.getLayoutParams();
//        lp.height = AndroidUtils.getDisplayHeight(context) / 2;
//    }

    private void initView() {
        mProgressTitle = (TextView) findViewById(R.id.progress_title);
        mProgressPercent = (TextView) findViewById(R.id.progress_percent);
        mProgressNumber = (TextView) findViewById(R.id.progress_number);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    public void setTitle(String msg) {
        mProgressTitle.setText(msg);
    }

    private void initFormats() {
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    public void setMessage1(double progress, double max) {
        try {
            if (mProgressPercentFormat != null) {
                double percent = progress / max;
                SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, tmp.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mProgressPercent.setText(tmp);
            } else {
                mProgressPercent.setText("");
            }
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
    }

    public void setMessage2(double progress, double max) {
        try {
            String currentfileSize = UpgradeUtils.bytes2kb((long) progress);
            String fileSize = UpgradeUtils.bytes2kb((long) max);
            mProgressNumber.setText(currentfileSize + "/" + fileSize);
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
    }

    public void setProgress(double progress, double max) {
        try {
            double percent = progress / max;
            int p = (int) (percent * 100);
            mProgressBar.setProgress(p);
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
    }

    private void setLayoutParams() {
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
    }

    @Override
    public void onClick(View v) {

    }

}
