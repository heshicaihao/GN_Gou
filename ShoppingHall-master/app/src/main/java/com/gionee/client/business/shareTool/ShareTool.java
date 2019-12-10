package com.gionee.client.business.shareTool;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;

/**
 * 分享工具 {{"微信":0, "微信朋友圈":1, "QQ好友":2, "QQ空间":3, "新浪微博":4}}
 */
public class ShareTool {
    public static final String TAG = ShareTool.class.getSimpleName();
    public static final String APP_ID = "wxb8e65b823a0f625f";
    public static final int PLATFORM_WECHAT = 0;
    public static final int PLATFORM_WECHAT_MOMENTS = 1;
    public static final int PLATFORM_QQ_FRIEND = 2;
    public static final int PLATFORM_QQ_ZONE = 3;
    public static final int PLATFORM_SINA_WEIBO = 4;
    private Context context;
    private PlatformActionListener platformActionListener;
    private ShareParams shareParams;
    private ProgressDialog pd;

    public ShareTool(Context context) {
        this.context = context;
        pd = ProgressDialog.show(context, null, "页面加载中...", true, true);
        pd.setContentView(R.layout.share_progress_dialog);
    }

    public static boolean isQQValid(Context context) {
        PackageInfo pi = null;
        try {
            String packageName = "com.tencent.mobileqq";

            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Throwable t) {
            pi = null;
            return false;
        }
        if (pi == null) {
            return false;
        }

        String[] ver = pi.versionName.split("\\.");
        int[] verCode = new int[ver.length];
        for (int i = 0; i < verCode.length; i++) {
            try {
                verCode[i] = Integer.parseInt(ver[i]);
            } catch (Throwable t) {
                verCode[i] = 0;
            }
        }
        return ((verCode.length > 0 && verCode[0] >= 5) || (verCode.length > 1 && verCode[0] >= 4 && verCode[1] >= 6));
    }

    public static boolean isWXInstalled(Context context) {
        PackageInfo pi = null;
        try {
            String packageName = "com.tencent.mm";
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Throwable t) {
            pi = null;
            return false;
        }
        if (pi == null) {
            return false;
        }
        return true;
    }

    public void showProgressDialog() {
        pd.show();
    }

    /**
     * 隐藏加载提示
     */
    public void dismiss() {
        if (pd != null) {
            pd.dismiss();
        }
    }

    public PlatformActionListener getPlatformActionListener() {
        return platformActionListener;
    }

    public void setPlatformActionListener(PlatformActionListener platformActionListener) {
        this.platformActionListener = platformActionListener;
    }

    /*    public void showShareWindow()
        {
            View view = LayoutInflater.from(context).inflate(R.layout.share_layout, null);
            GridView gridView = (GridView) view.findViewById(R.id.share_gridview);
            ShareAdapter adapter = new ShareAdapter(context);
            gridView.setAdapter(adapter);

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setView(view);
            AlertDialog adialog = dialog.show();
            gridView.setOnItemClickListener(new ShareItemClickListener(adialog));
        }*/

    private class ShareItemClickListener implements OnItemClickListener {
        private AlertDialog dialog;

        public ShareItemClickListener(AlertDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            pd.show();
            share(position);
            dialog.dismiss();
        }

    };

    /**
     * 分享
     * 
     * @param position
     */
    public void share(int position) {
        pd.show();
        LogUtils.log(
                TAG,
                LogUtils.getFunctionName() + " position = " + position + ", title = "
                        + shareParams.getTitle() + ", url = " + shareParams.getUrl() + ", text = "
                        + shareParams.getText() + ", image url = " + shareParams.getImageUrl());
        if (position == 3) {
            qzone();
        } else {
            if (position == 2 && !isQQValid(context)) {
                Toast.makeText(context, R.string.qq_client_inavailable, Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }

            if (TextUtils.isEmpty(shareParams.getUrl())) {
                Toast.makeText(context, R.string.share_faild, Toast.LENGTH_SHORT).show();
                return;
            }

            Platform plat = null;
            try {
                plat = ShareSDK.getPlatform(context, getPlatform(position));
                if (plat == null) {
                    LogUtils.logd(TAG, LogUtils.getFunctionName() + "platform == null");
                }
                if (platformActionListener != null) {
                    plat.setPlatformActionListener(platformActionListener);
                }
                plat.share(shareParams);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.log(TAG, LogUtils.getFunctionName() + " exception : " + e);
                Toast.makeText(context, R.string.share_faild, Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * 初始化分享参数
     * 
     * @param shareModel
     */
    public void initShareParams(ShareModel shareModel) {
        if (shareModel != null) {
            ShareParams sp = new ShareParams();
            sp.setShareType(Platform.SHARE_TEXT);
            sp.setShareType(Platform.SHARE_WEBPAGE);

            sp.setTitle(shareModel.getTitle());
            sp.setText(shareModel.getText());
//            sp.setText("测试标题");
            sp.setUrl(shareModel.getUrl());
            sp.setImageUrl(shareModel.getImageUrl());
            sp.setTitleUrl(shareModel.getUrl());
//            sp.setImageUrl("http://www.wyl.cc/wp-content/uploads/2014/02/10060381306b675f5c5.jpg");
            sp.setSite(context.getString(R.string.app_name));
            sp.setSiteUrl(shareModel.getUrl());
            sp.setImageData(shareModel.getBitmap());

            shareParams = sp;
        }
    }

    /**
     * 获取平台 {"微信":0, "微信朋友圈":1, "QQ好友":2, "QQ空间":3, "新浪微博":4}
     * 
     * @param position
     * @return
     */
    private String getPlatform(int position) {
        String platform = "";
        switch (position) {
            case 0:
                platform = "Wechat";
                break;
            case 1:
                platform = "WechatMoments";
                break;
            case 2:
                platform = "QQ";
                break;
            case 3:
                platform = "QZone";
                break;
            case 4:
                platform = "SinaWeibo";
                break;
            default:
                break;
        }
        return platform;
    }

    /**
     * 分享到QQ空间
     */
    private void qzone() {
        if (!isQQValid(context)) {
            Toast.makeText(context, R.string.qq_client_inavailable, Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }
        ShareParams sp = new ShareParams();
        sp.setTitle(shareParams.getTitle());
        sp.setTitleUrl(shareParams.getUrl()); // 标题的超链接
        String text = shareParams.getText();
        if (TextUtils.isEmpty(text)) {
            text = context.getString(R.string.app_name);
        }
        sp.setText(text);
        sp.setImageUrl(shareParams.getImageUrl());
//        sp.setComment("我对此分享内容的评论");
        sp.setSite(shareParams.getSite());
        sp.setSiteUrl(shareParams.getSiteUrl());
        try {
            Platform qzone = ShareSDK.getPlatform(context, "QZone");
            qzone.setPlatformActionListener(platformActionListener); // 设置分享事件回调 // 执行图文分享
            qzone.share(sp);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.logd(TAG, LogUtils.getThreadName() + " exception: " + e);
            Toast.makeText(context, R.string.share_faild, Toast.LENGTH_SHORT).show();
        }
    }
}
