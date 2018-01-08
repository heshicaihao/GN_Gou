package com.gionee.client.business.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.gionee.client.R;
import com.gionee.client.business.manage.GNActivityManager;
import com.gionee.client.model.Constants;

@SuppressLint({"SimpleDateFormat", "NewApi"})
public class AndroidUtils {

    // Gionee <hcy> <2013-7-4> modify for CR00832427 begin

    private static boolean sIsGoBack = false;
    private static final String TAG = "Utils";

    @SuppressWarnings("deprecation")
    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
        }
        return version;
    }

    public static boolean isExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static int getRespStatus(String url) {
        int status = -1;
        try {
            HttpHead head = new HttpHead(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(head);
            status = resp.getStatusLine().getStatusCode();
        } catch (Exception e) {
        }
        return status;
    }

    public static String getCurrentProgramPkgName(Context context) {
        /** 获取系统服务 ActivityManager */
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> listOfProcesses = manager.getRunningAppProcesses();

        String packageName = listOfProcesses.get(0).processName;
        return packageName;

    }

    public static boolean isInAdvertiseWhiteList(Context context) {
        /** 获取系统服务 ActivityManager */
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listOfProcesses = manager.getRunningAppProcesses();

        for (int j = 0; j < 2; j++) {
            LogUtils.log("TestPackageName", "processName=" + listOfProcesses.get(j).processName
                    + "==position=" + j);
            if (listOfProcesses.get(j).processName.equals("com.tencent.mm")
                    || listOfProcesses.get(j).processName.equals("com.gionee.aora.market")) {
                return true;
            }
        }
        return false;

    }

    public static void inputFilterSpace(final EditText edit) {
        edit.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16), new InputFilter() {
            public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dstart, int dend) {
                if (src.length() < 1) {
                    return null;
                } else {
                    char temp[] = (src.toString()).toCharArray();
                    char result[] = new char[temp.length];
                    for (int i = 0, j = 0; i < temp.length; i++) {
                        if (temp[i] == ' ') {
                            continue;
                        } else {
                            result[j++] = temp[i];
                        }
                    }
                    return String.valueOf(result).trim();
                }

            }
        }});
    }

    public static int getNotifyBarHeight(Activity context) {
        Rect frame;
        try {
            frame = new Rect();
            context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            return frame.top;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取标题栏的高度
     * 
     * @param activity
     * @return
     */
    public static int getTitleHeight(Activity activity) {
        Rect rect = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;

        return titleBarHeight;
    }

    /**
     * 获取标题栏的高度
     * 
     * @param activity
     * @return
     */
//    public static int getNavigationBarHeight(Activity activity) {
//        try {
//            if (checkDeviceHasNavigationBar(activity)) {
//                Resources resources = activity.getResources();
//                int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
//                if (resourceId > 0) {
//                    return resources.getDimensionPixelSize(resourceId);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }

    public static void hideNavigationBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        if (AndroidUtils.getAndroidSDKVersion() >= 11) {
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }

            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
            if (id > 0 && checkDeviceHasNavigationBar(context)) {
                navigationBarHeight = rs.getDimensionPixelSize(id);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return navigationBarHeight;
    }

    public static void finishActivity(Activity activity) {

        if (!hideInputSoftware(activity)) {
            activity.finish();
            exitActvityAnim(activity);

        }
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    public static void exitApp() {
        try {
            GNActivityManager.getScreenManager().popAllActivity();
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    public static boolean hideInputSoftware(Activity context) {

        try {
            View v = context.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public static void showShortToast(Context context, int id) {
        Toast.makeText(context, context.getText(id), Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int id, int millisTime) {
        Toast.makeText(context, context.getText(id), millisTime).show();
    }

    public static int getCurrentTwoGState(Context context) {
        SharedPreferences sp = context.getSharedPreferences("twoginfo", 0);
        int currentTwoGState = sp.getInt("twoginfo", 0);
        if (sIsGoBack) {
            currentTwoGState = 0;
        }
        return currentTwoGState;
    }

    /**
     * @param context
     * @return such as Constants.wifi_available
     * @author yuwei
     * @description TODO get the net work type
     */
    public static int getNetworkType(Context context) {
        @SuppressWarnings("static-access")
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        // 获取当前的网络连接是否可用
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return Constants.NET_UNABLE;
        }
        // Wifi
        State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (State.CONNECTED == state || state == State.CONNECTING) {
            return Constants.WIFI_AVAILABLE;
        }
        // GPRS
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (State.CONNECTED == state) {
            return Constants.MOBILE_AVAILABLE;
        }
        return Constants.OTHERNET;
    }

    public interface LoadListener {
        void startLoad();
    }

    // Gionee <hcy> <2013-7-4> modify for CR00832427 end
    // Gionee <yuwei><2013-7-15> add for CR00836967 begin
    /**
     * @param context
     * @param dipValue
     * @return the px value
     * @description transform the dip value to px value
     * @author yuwei
     */
    public static int dip2px(Context context, float dipValue) {
        // scal 　密度。
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * @param context
     * @param pxValue
     * @return dip value
     * @description transform the px value to dip value
     * @author yuwei
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);

    }

    /**
     * @param context
     * @param pxValue
     * @return current screen px value
     * @description transform the hdpi px value to current screen px value
     * @author yuwei
     */
    public static int px2px(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / 1.5 * scale);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * @param context
     * @return current screen width
     * @description to call this method you can get the screen width of the display
     * @author yuwei
     */
    public static int getDisplayWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        // 获取窗口管理器,获取当前的窗口,调用getDefaultDisplay()后，其将关于屏幕的一些信息写进DM对象中,最后通过getMetrics(DM)获取
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 打印获取的宽和高
        return dm.widthPixels;

    }

    /**
     * @param context
     * @return current screen height
     * @description to call this method you can get the screen height of the display
     * @author yuwei
     */
    public static int getDisplayHeight(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        // 获取窗口管理器,获取当前的窗口,调用getDefaultDisplay()后，其将关于屏幕的一些信息写进DM对象中,最后通过getMetrics(DM)获取
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 打印获取的宽和高
        return dm.heightPixels;

    }

    /**
     * @param context
     * @return the immei number
     * @description get the immei number of current devices
     * @author yuwei
     */
    public static String getIMEI(Context context) {
        String imei = "";
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        return imei;
    }

    /**
     * @param context
     * @return version name
     * @description to call this method you can get the current version name of the app,it may defined in the
     *              manifest.xml
     * @author yuwei
     */
    public static String getAppVersionName(Context context) {
        String versionName = "unknown version";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = " " + packInfo.versionName;
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
        return versionName;
    }

    /**
     * @param context
     * @return current version code
     * @description to call this method you can get the current version code of the app,it may defined in the
     *              manifest.mxl
     * @author yuwei
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packInfo.versionCode;
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);

        }
        return versionCode;
    }

    /**
     * @param context
     * @return
     * @description to call this method you can get a string contained the immei number of the devices android
     *              the version name of the current app
     * @author yuwei
     */
    public static String getAppInfo(Context context) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("IMEI:");
        sb.append(getIMEI(context));
        sb.append(" Application versionName:");
        sb.append(getAppVersionName(context));
        sb.append("]");
        return sb.toString();
    }

    @SuppressLint("NewApi")
    public static Drawable getAppIcon(Context context) {
        Drawable appIcon = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            appIcon = packInfo.applicationInfo.loadIcon(packageManager);
            if (appIcon == null) {
                appIcon = packInfo.applicationInfo.loadLogo(packageManager);
            }
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
        return appIcon;
    }

    /**
     * @param key
     * @param defalut
     * @return system properties
     * @description get the system properties
     * @author yuwei
     */
    public static String getSystemProperties(String key, String defalut) {
        String info = defalut;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Object obj = c.newInstance();
            Method method = c.getMethod("get", String.class, String.class);
            info = (String) method.invoke(obj, key, defalut);
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
            e.printStackTrace();
            info = defalut;
        }
        return info;
    }

    /**
     * @param str
     * @return
     * @description get the format time string
     * @author yuwei
     */
    public static String getCurrentTimeStr(Context context) {
        if (context == null)
            return "";
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        String time = context.getString(R.string.last_update_time) + format.format(date);
        return time;
    }

    /**
     * @param str
     * @return
     * @description get the format time string
     * @author yuwei
     */
    public static String getDataFormatStr(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);
        return time;
    }

    /**
     * @param time
     * @return
     * @author yuwei
     * @description TODO is the day of time equal to current day
     */
    public static boolean isDateToday(long time) {
        long now = System.currentTimeMillis();
        long t1 = time / (1000 * 60 * 60 * 24);
        long t2 = now / (1000 * 60 * 60 * 24);
        return t1 == t2;
    }

    /**
     * @param context
     * @param view
     *            which the keybord bind to
     * @author yuwei
     * @description TODO make the keybord from shown to invisible
     */
    public static void hidenKeybord(Context context, View view) {
        if (context == null || view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void enterActvityAnim(Activity activity) {
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void exitActvityAnim(Activity activity) {
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static void webActivityEnterAnim(Activity activity) {
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.zoom_exit);
    }

    public static void logoFadeAnim(Activity activity) {
        activity.overridePendingTransition(R.anim.hold, R.anim.fade);
    }

    public static void ActivityFadeInAnim(Activity activity) {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.hold);
    }

    public static void webActivityExitAnim(Activity activity) {
        activity.overridePendingTransition(R.anim.zoom_enter, R.anim.push_right_out);
    }

    public static String getSubNetwork(Context context) {
        NetworkParser parser = new NetworkParser(context);
        return parser.getNetworkType();
    }

    /**
     * 计算LISTVIEW高度
     * 
     * @param listView
     *            listView
     */
    public static void setListViewHeightBasedOnChildren(GridView gridView, BaseAdapter adapter,
            boolean isFooterEnable) {
//        final int MAX_DEFUALT_COUNT = 3;
//        int count = 3;
//        if (adapter.getCount() > MAX_DEFUALT_COUNT) {
        int count = (adapter.getCount() - 1) / 3 + 1;
//        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();

        int itemHeight = gridView.getResources().getDimensionPixelSize(R.dimen.attention_item_height)
                + gridView.getResources().getDimensionPixelSize(R.dimen.attention_item_divider);
        int listHeight = 0;
        if (isFooterEnable) {
            listHeight = itemHeight * count;
        } else {
            listHeight = itemHeight * count
                    - gridView.getResources().getDimensionPixelSize(R.dimen.attention_item_divider);
        }
        params.height = listHeight;

        gridView.setLayoutParams(params);
    }

    public static boolean isHadPermission(Context context, String permission) {
        try {
            return context.checkPermission(permission, Binder.getCallingPid(), Binder.getCallingUid()) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hideInputMethod(Activity activity) {

        try {
            View v = activity.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean translateTopBar(Context context) {
        Window window = ((Activity) context).getWindow();
        String versionName = getSystemProperties("ro.miui.ui.version.name", "");
        if (TextUtils.isEmpty(versionName)) {
            return false;
        }
        if (versionName.equals("V6")) {
            Class<? extends Window> clazz = window.getClass();
            try {
                int tranceFlag = 0;
                int darkModeFlag = 0;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");

                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
                tranceFlag = field.getInt(layoutParams);

                field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);

                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                // 只需要状态栏透明
                extraFlagField.invoke(window, tranceFlag, tranceFlag);
                // 状态栏透明且黑色字体
                extraFlagField.invoke(window, tranceFlag | darkModeFlag, tranceFlag | darkModeFlag);
                // 清除黑色字体
//                extraFlagField.invoke(window, 0, darkModeFlag);
                return true;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        return false;

    }

    @SuppressWarnings("deprecation")
    public static Bitmap takeScreenShot(Activity activity) throws Exception {
        LogUtils.log(TAG, LogUtils.getThreadName());
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        // 去掉标题栏 //Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    } // 保存到sdcard

    /**
     * @param packageName
     * @author yuwei
     * @description TODO
     */
    public static void startActivityByPackageName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * 同步一下cookie
     */
    public static void synCookies(Context context, String url, String cookies) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();// 移除
        cookieManager.setCookie(url, cookies);// cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
    }

    public static void showErrorInfo(Context context, String errorInfo) {
        if (TextUtils.isEmpty(errorInfo)) {
            return;
        }
        try {

            if (StringUtills.isNotContainEnglish(errorInfo)) {
                Toast.makeText(context, errorInfo, Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, getString(R.string.net_error), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 将Uri复制到剪切板。
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void copyUriToClipboard(Uri uri, Context context) {
        // 得到剪贴板管理器
        if (AndroidUtils.getAndroidSDKVersion() >= 11) {
            ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setPrimaryClip(ClipData.newRawUri("URI", uri));
        } else {
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(uri.toString());
        }
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable
                        .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    public static void setMiuiTopMargain(Context context, View mHeadView) {
        if (translateTopBar(context)) {
            LayoutParams params = (LayoutParams) mHeadView.getLayoutParams();
            params.topMargin = dip2px(context, 15);
            mHeadView.setLayoutParams(params);
        }
    }

    public static boolean isHttpAction(String bannerAction) {
        try {
            return bannerAction.equals(Constants.BannerAction.STORY_DETAIL_PAGE.getValue())
                    || bannerAction.equals(Constants.BannerAction.THIRD_PARTY_NORMAL.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    public static boolean isServerWorked(Activity activity, String serverName) {
        ActivityManager myManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serverName)) {
                return true;
            }
        }
        return false;
    }
    public static String intgerToString(String value) {
        try {
            int count = Integer.valueOf(value) + 1;
            return String.valueOf(count);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }
}
