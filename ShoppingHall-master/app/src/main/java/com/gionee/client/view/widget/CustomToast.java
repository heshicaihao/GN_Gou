// Gionee <yuwei><2013-8-2> add for CR00821559 begin
/*
 * CustomToast.java
 * classes : com.gionee.client.widget.CustomToast
 * @author yuwei
 * V 1.0.0
 * Create at 2013-8-2 上午11:16:53
 */
package com.gionee.client.view.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;

/**
 * com.gionee.client.widget.CustomToast
 * 
 * @author yuwei <br/>
 * @data create at 2013-8-2 上午11:16:53
 * @desciption custom toast
 */
public class CustomToast {
    private static final String TAG = "CustomToast";
    private static PopupWindow sPopupWindow;
    private TextView mText;
    private ViewFlipper mViewFlipper;

    /**
     * @param context
     */
    public CustomToast(Context context) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (context == null) {
            return;
        }
        mViewFlipper = new ViewFlipper(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        mViewFlipper.addView(layout);
        mViewFlipper.setFlipInterval(1000000);
        mText = (TextView) layout.findViewById(R.id.toast_text);
        sPopupWindow = new PopupWindow(mViewFlipper, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        sPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        sPopupWindow.setFocusable(true);
        sPopupWindow.update();
    }

    /**
     * 
     * @description
     * @author yuwei
     */
    public void setToastText(int textId) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mText.setText(textId);
    }

    public void setToastText(String text) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mText.setText(text);
    }

    /**
     * @param view
     * @description show the toast
     * @author yuwei
     */
    public void showToast(View view, int topMargin) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (sPopupWindow == null) {
            return;
        }
        if (!view.isShown()) {
            return;
        }
        if (sPopupWindow.isShowing()) {
            sPopupWindow.dismiss();
        } else {
            sPopupWindow.getContentView().setEnabled(true);
            sPopupWindow.showAtLocation(view, Gravity.TOP, 0, topMargin);
            mViewFlipper.startFlipping();
            mViewFlipper.postDelayed(new Runnable() {

                @Override
                public void run() {
                    LogUtils.log(TAG, LogUtils.getThreadName() + "dismiss popup window");
                    try {
                        sPopupWindow.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.loge(TAG, LogUtils.getFunctionName() + ":" + e);
                    }
                }
            }, 5000);
        }
    }

    /**
     * 
     * @description dismiss the toast
     * @author yuwei
     */
    public void menuDismiss() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (sPopupWindow != null && sPopupWindow.isShowing()) {
            sPopupWindow.dismiss();
        }
    }

}
//Gionee <yuwei><2013-8-2> add for CR00821559 end