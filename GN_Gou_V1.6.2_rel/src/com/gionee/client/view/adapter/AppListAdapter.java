package com.gionee.client.view.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.business.appDownload.GNDownloadListener;
import com.gionee.client.business.appDownload.ListDownloadManager;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.GNDowanload;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.utills.Utils;

public class AppListAdapter extends BaseAdapter {
    private static final String TAG = "AppListAdapter";
    private Context mContext;
    private ArrayList<MyBean> mAppList;
    private ListDownloadManager mListDownloadManager;

    public ArrayList<MyBean> getmAppList() {
        return mAppList;
    }

    public void setmAppList(ArrayList<MyBean> mAppList) {
        this.mAppList = mAppList;
        mListDownloadManager = ListDownloadManager.getInstance(mContext);
        mListDownloadManager.setmDownloadList(this.mAppList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAppList == null ? 0 : mAppList.size();
    }

    @Override
    public Object getItem(int position) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "position = " + position);
        return mAppList.get(position);
    }

    public AppListAdapter(Context context, ArrayList<MyBean> appList) {
        mContext = context;
        mAppList = appList;
    }

    @Override
    public long getItemId(int position) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        final MyBean bean = mAppList.get(position);
        LogUtils.log(TAG, LogUtils.getThreadName() + "position=" + position);
        bean.put(HttpConstants.Data.AppRecommond.APP_LIST_POSITION_I, position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.app_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mAppIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            viewHolder.mAppName = (TextView) convertView.findViewById(R.id.app_name);
            viewHolder.mAppDescription = (TextView) convertView.findViewById(R.id.app_description);
            viewHolder.mAppSize = (TextView) convertView.findViewById(R.id.app_size);
            viewHolder.mAppStatus = (Button) convertView.findViewById(R.id.app_install);
            viewHolder.mProgress = (ProgressBar) convertView.findViewById(R.id.app_download_progress);
            viewHolder.mAppType = (TextView) convertView.findViewById(R.id.app_type);
            viewHolder.mAppVersion = (TextView) convertView.findViewById(R.id.app_version);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        bindData(viewHolder, bean);
        viewHolder.mAppStatus.setOnClickListener(new DownloadBtnClickListener(bean, viewHolder.mProgress));
        return convertView;
    }

    public class DownloadBtnClickListener implements OnClickListener {
        private MyBean mBean;
        private ProgressBar mProgressBar;

        public DownloadBtnClickListener(MyBean mBean, ProgressBar progress) {
            super();
            this.mBean = mBean;
            this.mProgressBar = progress;
        }

        @Override
        public void onClick(View v) {
            if (mListDownloadManager == null) {
                mListDownloadManager = ListDownloadManager.getInstance(mContext);
                mListDownloadManager.setmDownloadList(mAppList);
            }
            switch (mBean.getInt(HttpConstants.Data.AppRecommond.APP_STATUS_EM)) {
                case GNDowanload.DownloadStatus.STATUS_DOWNLOADING:

                    break;
                case GNDowanload.DownloadStatus.STATUS_COMPLETE:
                    statisticsEvent(StatisticsConstants.APP_INSTALL);
                    installByDownloadId(v, GNDowanload.DownloadStatus.STATUS_INSTALL);

                    break;
                case GNDowanload.DownloadStatus.STATUS_INSTALL:
                    statisticsEvent(StatisticsConstants.APP_DOWNLOAD);
                    resetProgress(mBean, mProgressBar);
                    mListDownloadManager.setsListener((GNDownloadListener) mContext);
                    mListDownloadManager.download(mContext, mBean);
                    break;
                case GNDowanload.DownloadStatus.STATUS_OPEN:
                    statisticsEvent(StatisticsConstants.APP_OPEN);
                    String packageName = getAppPackageName();
                    try {
                        startActivityByPackageName(packageName);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        AndroidUtils.showShortToast(mContext, R.string.open_failed);
                        resetMybeanStatus(GNDowanload.DownloadStatus.STATUS_INSTALL);
                        if (v instanceof Button) {
                            setAppStatus((Button) v, mBean);
                        }
                        notifyStatusChange();
                    }
                    break;
                case GNDowanload.DownloadStatus.STATUS_UPDATE:
                    statisticsEvent(StatisticsConstants.APP_UPGRADE);
                    resetProgress(mBean, mProgressBar);
                    if (mListDownloadManager.isApkUpdated(mBean)) {
                        installByDownloadId(v, GNDowanload.DownloadStatus.STATUS_UPDATE);
                    } else {
                        mListDownloadManager.setsListener((GNDownloadListener) mContext);
                        mListDownloadManager.download(mContext, mBean);
                    }
                    break;

                default:
                    break;
            }

        }

        /**
         * @author yangxiong
         * @description TODO 统计 下载/安装/升级/打开 事件
         */
        private void statisticsEvent(String eventId) {
            JSONObject appinfo = mBean.getJSONObject(HttpConstants.Data.AppRecommond.APP_INFO_JO);
            StatService.onEvent(
                    mContext,
                    eventId,
                    appinfo.optString(HttpConstants.Response.ID_I) + "_"
                            + appinfo.optString(HttpConstants.Response.RecommondAppList.NAME_S) + "_"
                            + appinfo.optString(HttpConstants.Response.RecommondAppList.VERSION_NAME_S));
        }

        /**
         * @param v
         * @author yuwei
         * @description TODO
         */
        private void installByDownloadId(View v, int lastStatus) {
            try {
                mListDownloadManager.installForeground(mBean);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                resetMybeanStatus(lastStatus);
                setAppStatus((Button) v, mBean);
                notifyStatusChange();
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * @return
         * @author yuwei
         * @description TODO
         */
        private String getAppPackageName() {
            JSONObject jsonData = mBean.getJSONObject(HttpConstants.Data.AppRecommond.APP_INFO_JO);
            String packageName = jsonData.optString(HttpConstants.Response.RecommondAppList.PACKAGE_S);
            return packageName;
        }

        /**
         * @param packageName
         * @author yuwei
         * @description TODO
         */
        private void startActivityByPackageName(String packageName) throws Exception {
            PackageManager packageManager = mContext.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            mContext.startActivity(intent);

        }

        /**
         * 
         * @author yuwei
         * @description TODO
         * @param status
         *            TODO
         */
        private void resetMybeanStatus(int status) {
            mBean.put(HttpConstants.Data.AppRecommond.APP_STATUS_EM, status);
            mBean.remove(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L);
        }

        /**
         * 
         * @author yuwei
         * @description TODO
         */
        private void notifyStatusChange() {
            try {
                mListDownloadManager.getsListener().onStatusChanged(mBean);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void bindData(ViewHolder viewHolder, MyBean bean) {
        JSONObject jsonData = bean.getJSONObject(HttpConstants.Data.AppRecommond.APP_INFO_JO);
        LogUtils.log("APP_LIST_DATA", LogUtils.getThreadName() + bean.toString());
        viewHolder.mAppIcon.setImageResource(R.drawable.white);
        GNImageLoader.getInstance().loadBitmap(
                jsonData.optString(HttpConstants.Response.RecommondAppList.ICON_S), viewHolder.mAppIcon);
        viewHolder.mAppName.setText(jsonData.optString(HttpConstants.Response.RecommondAppList.NAME_S));
        viewHolder.mAppDescription.setText(jsonData
                .optString(HttpConstants.Response.RecommondAppList.DESCRIPTION_S));
        viewHolder.mAppSize.setText(Utils.formatFileLength(jsonData
                .optLong(HttpConstants.Response.RecommondAppList.SIZE_I)));
        String appType = jsonData.optString(HttpConstants.Response.RecommondAppList.COMPANY_S);

        if (TextUtils.isEmpty(appType)) {
            viewHolder.mAppType.setVisibility(View.GONE);
        } else {
            viewHolder.mAppType.setVisibility(View.VISIBLE);
            viewHolder.mAppType.setText(appType);
        }

        viewHolder.mAppVersion.setText(jsonData
                .optString(HttpConstants.Response.RecommondAppList.VERSION_NAME_S));
        setAppStatus(viewHolder.mAppStatus, bean);
        setProgress(viewHolder.mProgress, bean);
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void resetProgress(MyBean myBean, ProgressBar mProgressBar) {
        myBean.put(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_PERCENT_I, 0);
        if (mProgressBar != null) {
            mProgressBar.setProgress(0);
        }
    }

    /**
     * @param viewHolder
     * @author yuwei
     * @description TODO
     */
    public void setAppStatus(Button statusBtn, MyBean bean) {
        if (bean == null) {
            LogUtils.log(TAG, LogUtils.getThreadName() + "ERROR: app bean is null");
            return;
        }
        int status = bean.getInt(HttpConstants.Data.AppRecommond.APP_STATUS_EM);
        String[] statusArray = mContext.getResources().getStringArray(R.array.app_status_array);
        int[] statusColor = {R.color.white, R.color.app_download_text_color, R.drawable.open_text_color,
                R.color.white, R.color.white, R.color.app_download_text_color};
        statusBtn.setText(statusArray[status]);
        statusBtn.setTextColor(mContext.getResources().getColorStateList(statusColor[status]));
        statusBtn.getBackground().setLevel(status);

    }

    /**
     * @param viewHolder
     * @param bean
     * @param status
     * @author yuwei
     * @description TODO
     */
    public void setProgress(ProgressBar progress, MyBean bean) {
        int status = bean.getInt(HttpConstants.Data.AppRecommond.APP_STATUS_EM);
        if (status == GNDowanload.DownloadStatus.STATUS_DOWNLOADING
                || status == GNDowanload.DownloadStatus.STATUS_WAITTING) {
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(bean.getInt(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_PERCENT_I));
        } else {
            progress.setVisibility(View.GONE);
        }

    }

    private static class ViewHolder {
        public ImageView mAppIcon;
        public TextView mAppName;
        public TextView mAppType;
        public TextView mAppVersion;
        public TextView mAppSize;
        public TextView mAppDescription;
        public Button mAppStatus;
        public ProgressBar mProgress;
    }
}
