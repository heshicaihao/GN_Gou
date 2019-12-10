package com.gionee.client.business.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import com.gionee.client.R;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.view.widget.CustomGuideDialog;

public class CommonUtils {
    private static final String TAG = "CommonUtils";
    private static final String PREFERENCES_NAME = "boot_count_data";
    private static final String COURSE_SWITCH = "course_swith";

    public static void showHelpDialog(Activity activity) {
        if (null == activity || activity.isFinishing()) {
            return;
        }
        // Create a custom dialog and set style
        CustomGuideDialog selectDialog = new CustomGuideDialog(activity, R.style.custom_guide_dialog);
        // Sets whether this dialog is canceled when touched outside the window's bound
        selectDialog.setCanceledOnTouchOutside(true);
        selectDialog.show();
    }

    public static boolean isNeedShowBootGuide(Context context, String key) {
        boolean necessary = false;
        int bootCount = readBootCountBykey(context, key);
        LogUtils.log(TAG, LogUtils.getThreadName() + "key:" + key + ",bootCount:" + bootCount);
        if (bootCount == 0) {
            necessary = true;
            saveBootCountByKey(context, key, ++bootCount);
        } else {
            necessary = false;
        }
        return necessary;
    }

    private static void saveBootCountByKey(Context context, String key, int count) {
        try {

            LogUtils.log(TAG, LogUtils.getThreadName());
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, count);
            editor.commit();
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
    }

    private static int readBootCountBykey(Context context, String key) {
        int count = 1;
        try {
            LogUtils.log(TAG, LogUtils.getThreadName());
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
                    Context.MODE_PRIVATE);
            count = preferences.getInt(key, 0);
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
        return count;
    }

    public static void showCloseDialog(final Context context) {
        showMyDialog(context, R.string.friendly_notify, context.getText(R.string.is_exit),
                context.getText(R.string.ok), context.getText(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
    }

    private static AlertDialog showMyDialog(Context context, int title, CharSequence message,
            CharSequence leftButton, CharSequence rightButton, OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(leftButton, listener);
        builder.setNegativeButton(rightButton, new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static void printDeviceInfomation(Context context) {
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
            int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
            float xdpi = dm.xdpi;
            float ydpi = dm.ydpi;
            LogUtils.log(TAG, LogUtils.getThreadName() + "xdpi=" + xdpi + "; ydpi=" + ydpi);
            LogUtils.log(TAG, LogUtils.getThreadName() + "density=" + density + "; densityDPI=" + densityDPI);

//            Toast.makeText(context, "densityDPI=" + densityDPI, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void gotoWebViewActvity(Context mContext, String url, boolean isShowBottomBar) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "Link url: " + url);
        Intent intent = new Intent();
        intent.putExtra(StatisticsConstants.KEY_INTENT_URL, url);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.IS_SHOW_WEB_FOOTBAR, isShowBottomBar);
        intent.putExtra(Constants.Home.IS_SHOW_REFRESH, true);
        intent.setClass(mContext, ThridPartyWebActivity.class);
        mContext.startActivity(intent);
        AndroidUtils.webActivityEnterAnim((Activity) mContext);
    }
    
    public static void gotoWebViewActvityForResult(Activity mContext, String url, boolean isShowBottomBar) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "Link url: " + url);
        Intent intent = new Intent();
        intent.putExtra(StatisticsConstants.KEY_INTENT_URL, url);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.IS_SHOW_WEB_FOOTBAR, isShowBottomBar);
        intent.putExtra(Constants.Home.IS_SHOW_REFRESH, true);
        intent.setClass(mContext, ThridPartyWebActivity.class);
        mContext.startActivityForResult(intent, Constants.ActivityRequestCode.REQUEST_CODE_WEB_PAGE);
        AndroidUtils.webActivityEnterAnim((Activity) mContext);
    }

    private static final long ONE_DAY_MILLISECONDS = 1000 * 60 * 60 * 24;

    public static boolean isDateToday(long time) {
        long now = System.currentTimeMillis();
        long t1 = time / ONE_DAY_MILLISECONDS;
        long t2 = now / ONE_DAY_MILLISECONDS;
        return t1 == t2;
    }

    public static void setCourseSwitch(Context context, boolean flag) {
        ShareDataManager.putBoolean(context, COURSE_SWITCH, flag);
    }

    public static boolean getCourseSwitch(Context context) {
        return ShareDataManager.getBoolean(context, COURSE_SWITCH, true);
    }
}
