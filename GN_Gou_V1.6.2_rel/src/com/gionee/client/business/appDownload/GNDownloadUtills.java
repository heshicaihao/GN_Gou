// Gionee <yuwei><2014-4-8> add for CR00821559 begin
/*
 * GNDownloadUtills.java
 * classes : com.gionee.client.business.appDownload.GNDownloadUtills
 * @author yuwei
 * V 1.0.0
 * Create at 2014-4-8 下午5:06:21
 */
package com.gionee.client.business.appDownload;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.GNDowanload;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;

/**
 * GNDownloadUtills
 * 
 * @author yuwei <br/>
 * @date create at 2014-4-8 下午5:06:21
 * @description TODO
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class GNDownloadUtills {
    private static final String TAG = "GNDownloadUtills";

    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    /** represents downloaded file above api 11 **/
    public static final String COLUMN_LOCAL_FILENAME = "local_filename";
    /** represents downloaded file below api 11 **/
    public static final String COLUMN_LOCAL_URI = "local_uri";

    public static final String METHOD_NAME_PAUSE_DOWNLOAD = "pauseDownload";
    public static final String METHOD_NAME_RESUME_DOWNLOAD = "resumeDownload";

    private static boolean sIsInitPauseDownload = false;
    private static boolean sIsInitResumeDownload = false;

    private static Method sPauseDownload = null;
    private static Method sResumeDownload = null;

    private DownloadManager mDownloadManager;

    public GNDownloadUtills(DownloadManager downloadManager) {
        this.mDownloadManager = downloadManager;
    }

    /**
     * get download status
     * 
     * @param downloadId
     * @return
     */
    public int getStatusById(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_STATUS);
    }

    /**
     * get downloaded byte, total byte
     * 
     * @param downloadId
     * @return a int array with two elements
     *         <ul>
     *         <li>result[0] represents downloaded bytes, This will initially be -1.</li>
     *         <li>result[1] represents total bytes, This will initially be -1.</li>
     *         </ul>
     */
    public int[] getDownloadBytes(long downloadId) {
        int[] bytesAndStatus = getBytesAndStatus(downloadId);
        return new int[] {bytesAndStatus[0], bytesAndStatus[1]};
    }

    /**
     * get downloaded byte, total byte and download status
     * 
     * @param downloadId
     * @return a int array with three elements
     *         <ul>
     *         <li>result[0] represents downloaded bytes, This will initially be -1.</li>
     *         <li>result[1] represents total bytes, This will initially be -1.</li>
     *         <li>result[2] represents download status, This will initially be 0.</li>
     *         </ul>
     */
    public int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[] {-1, -1, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = mDownloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c
                        .getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c
                        .getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }

    public ArrayList<MyBean> queryByStatus(int status) {
        ArrayList<MyBean> mDownloadList = new ArrayList<MyBean>();
        int[] bytesAndStatus = new int[] {-1, -1, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterByStatus(status);
        Cursor c = null;
        try {
            c = mDownloadManager.query(query);
            if (c == null) {
                return mDownloadList;
            }

            while (c.moveToNext()) {
                MyBean bean = MyBeanFactory.createDataBean();
                long downloadId = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID));
                String fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));

                bytesAndStatus[0] = c.getInt(c
                        .getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c
                        .getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                int percent = (int) ((float) bytesAndStatus[0] / (float) bytesAndStatus[1] * 100);
                bean.put(HttpConstants.Data.AppRecommond.APP_STATUS_EM, transformStatus(bytesAndStatus[2]));
                bean.put(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_PERCENT_I, percent);
                bean.put(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L, downloadId);
                bean.put(HttpConstants.Data.AppRecommond.APP_FILE_NAME, fileName);
                mDownloadList.add(bean);

            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return mDownloadList;
    }

    /**
     * pause download
     * 
     * @param ids
     *            the IDs of the downloads to be paused
     * @return the number of downloads actually paused, -1 if exception or method not exist
     */
    public int pauseDownload(long... ids) {
        initPauseMethod();
        if (sPauseDownload == null) {
            return -1;
        }

        try {
            return ((Integer) sPauseDownload.invoke(mDownloadManager, ids)).intValue();
        } catch (Exception e) {
            /**
             * accept all exception, include ClassNotFoundException, NoSuchMethodException,
             * InvocationTargetException, NullPointException
             */
            e.printStackTrace();
        }
        return -1;
    }

    public static int transformStatus(int status) {
        switch (status) {
            case DownloadManager.STATUS_SUCCESSFUL:
                LogUtils.log(TAG, "STATUS_SUCCESSFUL");
                return GNDowanload.DownloadStatus.STATUS_COMPLETE;
            case DownloadManager.STATUS_FAILED:
                LogUtils.log(TAG, "STATUS_FAILED");
                return GNDowanload.DownloadStatus.STATUS_INSTALL;
            case DownloadManager.STATUS_RUNNING:
                return GNDowanload.DownloadStatus.STATUS_DOWNLOADING;
            case DownloadManager.STATUS_PENDING:
                return GNDowanload.DownloadStatus.STATUS_WAITTING;
            case DownloadManager.STATUS_PAUSED:
                return GNDowanload.DownloadStatus.STATUS_WAITTING;
            default:
                return GNDowanload.DownloadStatus.STATUS_INSTALL;
        }

    }

    /**
     * resume download
     * 
     * @param ids
     *            the IDs of the downloads to be resumed
     * @return the number of downloads actually resumed, -1 if exception or method not exist
     */
    public int resumeDownload(long... ids) {
        initResumeMethod();
        if (sResumeDownload == null) {
            return -1;
        }

        try {
            return ((Integer) sResumeDownload.invoke(mDownloadManager, ids)).intValue();
        } catch (Exception e) {
            /**
             * accept all exception, include ClassNotFoundException, NoSuchMethodException,
             * InvocationTargetException, NullPointException
             */
            e.printStackTrace();
        }
        return -1;
    }

//    private static Cursor queryAll(Context context) {
//        String[] selectionArgs = new String[] {context.getPackageName()};
//        return context.getContentResolver().query(Downloads.Impl.CONTENT_URI, null,
//                Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE + "=?", selectionArgs, null);
//    }

    /**
     * whether exist pauseDownload and resumeDownload method in {@link DownloadManager}
     * 
     * @return
     */
    public static boolean isExistPauseAndResumeMethod() {
        initPauseMethod();
        initResumeMethod();
        return sPauseDownload != null && sResumeDownload != null;
    }

    private static void initPauseMethod() {
        if (sIsInitPauseDownload) {
            return;
        }

        sIsInitPauseDownload = true;
        try {
            sPauseDownload = DownloadManager.class.getMethod(METHOD_NAME_PAUSE_DOWNLOAD, long[].class);
        } catch (Exception e) {
            // accept all exception
            e.printStackTrace();
        }
    }

    private static void initResumeMethod() {
        if (sIsInitResumeDownload) {
            return;
        }

        sIsInitResumeDownload = true;
        try {
            sResumeDownload = DownloadManager.class.getMethod(METHOD_NAME_RESUME_DOWNLOAD, long[].class);
        } catch (Exception e) {
            // accept all exception
            e.printStackTrace();
        }
    }

    /**
     * get download file name
     * 
     * @param downloadId
     * @return
     */
    public String getFileName(long downloadId) {
        return getString(downloadId, (Build.VERSION.SDK_INT < 11 ? COLUMN_LOCAL_URI : COLUMN_LOCAL_FILENAME));
    }

    /**
     * get download uri
     * 
     * @param downloadId
     * @return
     */
    public String getUri(long downloadId) {
        return getString(downloadId, DownloadManager.COLUMN_URI);
    }

    /**
     * get failed code or paused reason
     * 
     * @param downloadId
     * @return <ul>
     *         <li>if status of downloadId is {@link DownloadManager#STATUS_PAUSED}, return
     *         {@link #getPausedReason(long)}</li>
     *         <li>if status of downloadId is {@link DownloadManager#STATUS_FAILED}, return
     *         {@link #getErrorCode(long)}</li>
     *         <li>if status of downloadId is neither {@link DownloadManager#STATUS_PAUSED} nor
     *         {@link DownloadManager#STATUS_FAILED}, return 0</li>
     *         </ul>
     */
    public int getReason(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_REASON);
    }

    /**
     * get paused reason
     * 
     * @param downloadId
     * @return <ul>
     *         <li>if status of downloadId is {@link DownloadManager#STATUS_PAUSED}, return one of
     *         {@link DownloadManager#PAUSED_WAITING_TO_RETRY}<br/>
     *         {@link DownloadManager#PAUSED_WAITING_FOR_NETWORK}<br/>
     *         {@link DownloadManager#PAUSED_QUEUED_FOR_WIFI}<br/>
     *         {@link DownloadManager#PAUSED_UNKNOWN}</li>
     *         <li>else return {@link DownloadManager#PAUSED_UNKNOWN}</li>
     *         </ul>
     */
    public int getPausedReason(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_REASON);
    }

    /**
     * get failed error code
     * 
     * @param downloadId
     * @return one of {@link DownloadManager#ERROR_*}
     */
    public int getErrorCode(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_REASON);
    }

    public static class RequestPro extends DownloadManager.Request {

        public static final String METHOD_NAME_SET_NOTI_CLASS = "setNotiClass";
        public static final String METHOD_NAME_SET_NOTI_EXTRAS = "setNotiExtras";

        private static boolean sIsInitNotiClass = false;
        private static boolean sIsInitNotiExtras = false;

        private static Method sSetNotiClass = null;
        private static Method sSetNotiExtras = null;

        /**
         * @param uri
         *            the HTTP URI to download.
         */
        public RequestPro(Uri uri) {
            super(uri);
        }

        /**
         * set noti class, only init once
         * 
         * @param className
         *            full class name
         */
        public void setNotiClass(String className) {
            synchronized (this) {

                if (!sIsInitNotiClass) {
                    sIsInitNotiClass = true;
                    try {
                        sSetNotiClass = Request.class.getMethod(METHOD_NAME_SET_NOTI_CLASS,
                                CharSequence.class);
                    } catch (Exception e) {
                        // accept all exception
                        e.printStackTrace();
                    }
                }
            }

            if (sSetNotiClass != null) {
                try {
                    sSetNotiClass.invoke(this, className);
                } catch (Exception e) {
                    /**
                     * accept all exception, include ClassNotFoundException, NoSuchMethodException,
                     * InvocationTargetException, NullPointException
                     */
                    e.printStackTrace();
                }
            }
        }

        /**
         * set noti extras, only init once
         * 
         * @param extras
         */
        public void setNotiExtras(String extras) {
            synchronized (this) {

                if (!sIsInitNotiExtras) {
                    sIsInitNotiExtras = true;
                    try {
                        sSetNotiExtras = Request.class.getMethod(METHOD_NAME_SET_NOTI_EXTRAS,
                                CharSequence.class);
                    } catch (Exception e) {
                        // accept all exception
                        e.printStackTrace();
                    }
                }
            }

            if (sSetNotiExtras != null) {
                try {
                    sSetNotiExtras.invoke(this, extras);
                } catch (Exception e) {
                    /**
                     * accept all exception, include ClassNotFoundException, NoSuchMethodException,
                     * InvocationTargetException, NullPointException
                     */
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * get string column
     * 
     * @param downloadId
     * @param columnName
     * @return
     */
    private String getString(long downloadId, String columnName) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        String result = null;
        Cursor c = null;
        try {
            c = mDownloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result = c.getString(c.getColumnIndex(columnName));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    /**
     * get int column
     * 
     * @param downloadId
     * @param columnName
     * @return
     */
    private int getInt(long downloadId, String columnName) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        int result = -1;
        Cursor c = null;
        try {
            c = mDownloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result = c.getInt(c.getColumnIndex(columnName));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }
}
