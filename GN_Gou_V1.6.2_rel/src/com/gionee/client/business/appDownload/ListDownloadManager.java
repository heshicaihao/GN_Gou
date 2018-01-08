package com.gionee.client.business.appDownload;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.gionee.client.R;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.GNDowanload;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.exception.MyErrorException;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.net.NetUtil;

@SuppressLint("NewApi")
public class ListDownloadManager {
    private static final String TAG = "ListDownloadManager";
    private GNDownloadListener mListener;
    public ScheduledExecutorService mScheduledExecutorService;
    private Context mContext;
    private DownloadHandler mHandler;
    private ArrayList<MyBean> mDownloadList;

    private static final int MSG_STATUS_CHANGED = 1;

    private static final int MSG_PROGRESS_CHANGED = 2;
    private boolean mIsUpdateStarted;
    private static Object sLock = new Object();

    /**
     * @return the mDownloadList
     */
    public ArrayList<MyBean> getmDownloadList() {
        return mDownloadList;
    }

    /**
     * @param mDownloadList
     *            the mDownloadList to set
     */
    public void setmDownloadList(ArrayList<MyBean> mDownloadList) {
        this.mDownloadList = mDownloadList;
    }

    private DownloadManager mDownloadManager;
    private GNDownloadUtills mDownloadUtils;
    private static ListDownloadManager sInstance;

    private ListDownloadManager(Context context) {
        super();
        mContext = context;
        mHandler = new DownloadHandler();
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadUtils = new GNDownloadUtills(mDownloadManager);
    }

    public void startUpdateState() {
        if (!mIsUpdateStarted) {
            mScheduledExecutorService = Executors.newScheduledThreadPool(3);
            QueryRunable runable = new QueryRunable();
            mScheduledExecutorService.scheduleAtFixedRate(runable, 0, 1000, TimeUnit.MILLISECONDS);
            mIsUpdateStarted = true;
        }
    }

    public static ListDownloadManager getInstance(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new ListDownloadManager(context);
            }
        }
        return sInstance;
    }

    class QueryRunable implements Runnable {

        @Override
        public void run() {
            LogUtils.log(TAG, LogUtils.getThreadName() + "status or progress changed");
            if (mDownloadList != null && mDownloadList.size() > 0) {
                try {
                    for (int i = 0; i < mDownloadList.size(); i++) {
                        MyBean myBean = mDownloadList.get(i);
                        if (isDownloading(myBean)) {
                            int[] byteAndStatus = mDownloadUtils.getBytesAndStatus(myBean
                                    .getLong(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L));
                            int percent = (int) ((float) byteAndStatus[0] / (float) byteAndStatus[1] * 100);
                            int status = GNDownloadUtills.transformStatus(byteAndStatus[2]);
                            if (!checkStatusChanged(myBean, i, percent, status)) {
                                checkProgressChanged(myBean, i, percent, status);
                            }
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    LogUtils.log(TAG, LogUtils.getThreadName() + "status run error====" + e.getMessage());
                }
            }
        }
    }

    /**
     * @param myBean
     * @param percent
     * @param status
     * @author yuwei
     * @description TODO
     */
    private void checkProgressChanged(MyBean myBean, int position, int percent, int status) {
        myBean.put(HttpConstants.Data.AppRecommond.APP_STATUS_EM, status);
        myBean.put(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_PERCENT_I, percent);
        LogUtils.log(TAG, LogUtils.getThreadName() + "afterQueryt" + myBean.toString());
        Message msg = mHandler.obtainMessage();
        msg.arg1 = position;
        msg.what = MSG_PROGRESS_CHANGED;
        mHandler.sendMessage(msg);
    }

    /**
     * @param myBean
     * @param status
     * @author yuwei
     * @description TODO
     */
    private boolean checkStatusChanged(MyBean myBean, int position, int percent, int status) {
        if (status != DownloadManager.STATUS_RUNNING) {
            myBean.put(HttpConstants.Data.AppRecommond.APP_STATUS_EM, status);
            myBean.put(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_PERCENT_I, percent);
            Message msg = mHandler.obtainMessage();
            msg.what = MSG_STATUS_CHANGED;
            msg.arg1 = position;
            mHandler.sendMessage(msg);
            return true;
        }
        return false;
    }

    /**
     * @param myBean
     * @return
     * @author yuwei
     * @description TODO
     */
    private boolean isDownloading(MyBean myBean) {
        return myBean.getLong(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L) != -1
                && ((myBean.getInt(HttpConstants.Data.AppRecommond.APP_STATUS_EM) == GNDowanload.DownloadStatus.STATUS_DOWNLOADING) || (myBean
                        .getInt(HttpConstants.Data.AppRecommond.APP_STATUS_EM) == GNDowanload.DownloadStatus.STATUS_WAITTING));
    }

    /**
     * @return the sListener
     */
    public GNDownloadListener getsListener() {
        return mListener;

    }

    class DownloadHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            LogUtils.log(TAG, LogUtils.getThreadName() + "status or progress changed");
            super.handleMessage(msg);
            int position = msg.arg1;
            MyBean bean = mDownloadList.get(position);
            if(bean!=null){
            	switch (msg.what) {
            	case MSG_PROGRESS_CHANGED:
            		mListener.onProgressChanged(bean);
            		break;
            	case MSG_STATUS_CHANGED:
            		mListener.onStatusChanged(bean);
            		break;
            		
            	default:
            		break;
            	}
            }

        }
    }

    /**
     * @param sListener
     *            the sListener to set
     */
    public void setsListener(GNDownloadListener sListener) {
        this.mListener = sListener;
    }

    /**
     * 
     * @author yuwei
     * @description TODO shutDown the query ThreadPool
     */
    public void shutDownQuery() {
        if (mIsUpdateStarted) {
            mScheduledExecutorService.shutdown();
            mIsUpdateStarted = false;
        }
    }

    /**
     * @param context
     * @param bean
     *            a map contains the download url
     * @param wifiauto
     * @return
     * @author yuwei
     * @description TODO download app
     */
    public long download(Context context, MyBean bean) {
        final long mMinSize = 10;
        if (!AndroidUtils.isExistSDCard()) {
            Toast.makeText(context, R.string.sd_not_exist, Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (AndroidUtils.getSDFreeSize() < mMinSize) {
            Toast.makeText(context, R.string.sd_card_no_free, Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.upgrade_no_net, Toast.LENGTH_SHORT).show();
            return -1;
        }
        long downloadId = startDownload(context, bean);
        return downloadId;

    }

    /**
     * @param context
     * @param bean
     * @return the download id return by DownloadManager
     * @author yuwei
     * @description TODO start download the apk of the bean
     */
    private long startDownload(Context context, MyBean bean) {
        JSONObject json = bean.getJSONObject(HttpConstants.Data.AppRecommond.APP_INFO_JO);
        long downloadId = InstallUtills.startDownload(context,
                json.optString(HttpConstants.Response.RecommondAppList.LINK_S),
                json.optString(HttpConstants.Response.RecommondAppList.PACKAGE_S), true);
        bean.put(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L, downloadId);
        bean.put(HttpConstants.Data.AppRecommond.APP_STATUS_EM, GNDowanload.DownloadStatus.STATUS_DOWNLOADING);
        mListener.onStatusChanged(bean);
        return downloadId;
    }

    /**
     * @param mContext
     * @param downloadId
     * @author yuwei
     * @description TODO 查询下载状态
     */
    public int queryDownloadStatus(long downloadId) {
        Cursor cusor = null;
        JSONObject appJsonInfo = getJsonInfo(downloadId);
        MyBean myBean = null;
        try {
            myBean = getBeanById(downloadId);
            cusor = getCusor(downloadId);
            if (cusor.moveToFirst()) {
                int status = cusor.getInt(cusor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                String apkPath = InstallUtills.getFilePath(cusor);
                File apkFile = new File(apkPath);
                LogUtils.log("InstallManager", LogUtils.getThreadName() + "status=" + status);
                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        LogUtils.log(TAG, "STATUS_SUCCESSFUL");
                        if (myBean != null) {
                            installForeground(myBean);
                            return GNDowanload.DownloadStatus.STATUS_COMPLETE;
                        }
                        if (InstallUtills.invalidataApk(mContext, appJsonInfo, apkFile)) {
                            AppInstallQuenee.getInstance().startAppInstall(mContext, apkFile);
                            InstallUtills.clearCacheData(downloadId, mContext, apkFile);
                        }
                        if (appJsonInfo == null) {
                            LogUtils.log("InstallManager",
                                    LogUtils.getThreadName() + "filePath=" + apkFile.getAbsolutePath());
                            InstallUtills.alertInstall(apkFile, mContext);
                        }

                        return GNDowanload.DownloadStatus.STATUS_COMPLETE;
                    case DownloadManager.STATUS_FAILED:
                        LogUtils.log(TAG, "STATUS_FAILED");
                        notifyFaild(downloadId, myBean, apkFile);
                        return GNDowanload.DownloadStatus.STATUS_INSTALL;
                    case DownloadManager.STATUS_RUNNING:
                        notifyStatusChange(myBean, GNDowanload.DownloadStatus.STATUS_DOWNLOADING);
                        return GNDowanload.DownloadStatus.STATUS_DOWNLOADING;
                    case DownloadManager.STATUS_PENDING:
                        notifyStatusChange(myBean, GNDowanload.DownloadStatus.STATUS_WAITTING);
                        return GNDowanload.DownloadStatus.STATUS_WAITTING;
                    case DownloadManager.STATUS_PAUSED:
                        notifyStatusChange(myBean, GNDowanload.DownloadStatus.STATUS_WAITTING);
                        return GNDowanload.DownloadStatus.STATUS_WAITTING;
                    default:
                        notifyStatusChange(myBean, GNDowanload.DownloadStatus.STATUS_INSTALL);
                        return GNDowanload.DownloadStatus.STATUS_INSTALL;
                }

            }
            return GNDowanload.DownloadStatus.STATUS_INSTALL;
        } catch (MyErrorException e) {
            notifyStatusChange(myBean, GNDowanload.DownloadStatus.STATUS_INSTALL);
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return GNDowanload.DownloadStatus.STATUS_INSTALL;
        } finally {
            if (cusor != null) {
                cusor.close();
            }
        }
        return -1;

    }

    /**
     * @param downloadId
     * @param myBean
     * @param apkFile
     * @author yuwei
     * @description TODO
     */
    private void notifyFaild(long downloadId, MyBean myBean, File apkFile) {
        notifyStatusChange(myBean, GNDowanload.DownloadStatus.STATUS_INSTALL);
        JSONObject appJson = myBean.getJSONObject(HttpConstants.Data.AppRecommond.APP_INFO_JO);
        String appName = appJson.optString(HttpConstants.Response.RecommondAppList.NAME_S);
        InstallUtills.clearCacheData(downloadId, mContext, apkFile);
        Toast.makeText(mContext, appName + mContext.getString(R.string.download_failed), Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * @param downloadId
     *            the download id return by DownloadManager
     * @param myBean
     * @param apkFile
     *            the fileName of the apk file
     * @throws Exception
     * @author yuwei
     * @description TODO install the apk in type of aleart
     */
    public void installForeground(MyBean myBean) throws Exception {
        long downloadId = myBean.getLong(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L);
        String fileName = mDownloadUtils.getFileName(downloadId);
        if (TextUtils.isEmpty(fileName)) {
            InstallUtills.clearCacheData(downloadId, mContext, null);
            throw new MyErrorException(mContext.getString(R.string.download_apk_error));
        }
        File apkFile = new File(fileName);
        if (isApkUpdated(myBean)) {
            notifyStatusChange(myBean, GNDowanload.DownloadStatus.STATUS_COMPLETE);
            InstallManager.getInstance(mContext).startInstallById(downloadId);
        } else {
            InstallUtills.clearCacheData(downloadId, mContext, apkFile);
            throw new MyErrorException(mContext.getString(R.string.download_apk_error));

        }
    }

    public boolean isApkUpdated(MyBean myBean) {
        JSONObject obj = myBean.getJSONObject(HttpConstants.Data.AppRecommond.APP_INFO_JO);
        long downloadId = myBean.getLong(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L);
        String fileName = mDownloadUtils.getFileName(downloadId);
        if (TextUtils.isEmpty(fileName)) {
            InstallUtills.removeDownloadHistory(mContext, downloadId);
            return false;
        }
        File apkFile = new File(fileName);
        return isFileExist(apkFile) && InstallUtills.invalidataApk(mContext, obj, apkFile);
    }

    /**
     * @param apkFile
     * @return
     * @author yuwei
     * @description TODO
     */
    private static boolean isFileExist(File apkFile) {
        return apkFile != null && apkFile.exists();
    }

    /**
     * @param downloadId
     * @return
     * @throws JSONException
     * @author yuwei
     * @description TODO get the jsonInfo saved in sharedPreference
     */
    private JSONObject getJsonInfo(long downloadId) {
        JSONObject appJsonInfo = null;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String jsonString = prefs.getString(downloadId + "", "");
            appJsonInfo = new JSONObject(jsonString);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return appJsonInfo;
    }

    /**
     * @param downloadId
     * @return
     * @author yuwei
     * @description TODO
     */
    private Cursor getCusor(long downloadId) {
        Cursor cusor;
        DownloadManager downloadManager = (DownloadManager) mContext
                .getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        cusor = downloadManager.query(query);
        return cusor;
    }

    /**
     * @param myBean
     * @author yuwei
     * @description TODO
     */
    private void notifyStatusChange(MyBean myBean, int status) {

        if (myBean != null) {
            myBean.put(HttpConstants.Data.AppRecommond.APP_STATUS_EM, status);
            mListener.onStatusChanged(myBean);
        }
    }

    /**
     * @param downloadId
     * @author yuwei
     * @description TODO
     */
    private MyBean getBeanById(long downloadId) {
        LogUtils.log(TAG, "STATUS_FAILED" + LogUtils.getThreadName() + "downloadId = " + downloadId);
        MyBean myBean = null;
        if (mDownloadList != null) {
            for (Iterator<MyBean> iterator = mDownloadList.iterator(); iterator.hasNext();) {
                MyBean bean = (MyBean) iterator.next();
                long mDownloadId = bean.getLong(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L);
                LogUtils.log(TAG, "STATUS_FAILED" + LogUtils.getThreadName() + "mDownloadId = " + mDownloadId);
                if (mDownloadId == downloadId) {
                    myBean = bean;
                }

            }
        }
        return myBean;
    }
}
