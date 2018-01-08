package com.gionee.client.business.util;

// Gionee <hcy><2012-6-17> add for CR00825880 begin
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

public class LogUtils {
    private static final String GLOBAL_TAG = "GN_GOU";
    private static final String SAVELOG_FILE_NAME = "GN_GOU_log.txt";
    private static final String ENABLE_SAVELOG_FLAG_FOLDER = "GNGOU1234567890savelog";
    private static final String TAG = "LogUtils";
    private static boolean sEnableLog = false;
    private static boolean sIsSaveLog = false;

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");

    public static void loadInitConfigs() {
        Log.d(TAG, "loadInitConfigs ...");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdCardDir = getExternalStorageDirectory();
            File saveLogFlagFile = new File(sdCardDir + ENABLE_SAVELOG_FLAG_FOLDER);
            if (saveLogFlagFile.exists()) {
                Log.d(TAG, "DownloadManager savelog flag is true");
                LogUtils.sIsSaveLog = true;
            }
        }

    }

    public static void log(String tag, String msg) {
        if (sEnableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.i(GLOBAL_TAG + "." + tag, "" + msg);
            if (sIsSaveLog) {
                try {
                    saveToSDCard(formatLog(msg, tag, "i"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void logv(String tag, String msg) {
        if (sEnableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.v(GLOBAL_TAG + "." + tag, "" + msg);
            if (sIsSaveLog) {
                try {
                    saveToSDCard(formatLog(msg, tag, "V"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void logd(String tag, String msg) {
        if (sEnableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.d(GLOBAL_TAG + "." + tag, msg);
            if (sIsSaveLog) {
                try {
                    saveToSDCard(formatLog(msg, tag, "D"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loge(String tag, String msg) {
        if (sEnableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.e(GLOBAL_TAG + "." + tag + ".E", "" + msg);
            if (sIsSaveLog) {
                try {
                    saveToSDCard(formatLog(msg, tag, "E"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loge(String tag, String msg, Throwable e) {
        if (sEnableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.e(GLOBAL_TAG + "." + tag, "" + msg, e);
            if (sIsSaveLog) {
                try {
                    saveToSDCard(formatLog(msg, tag, "E"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public static void saveToSDCard(String content) throws Exception {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String sdCardDir = getExternalStorageDirectory();
                File file = new File(sdCardDir + File.separator + ENABLE_SAVELOG_FLAG_FOLDER,
                        SAVELOG_FILE_NAME);
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.seek(file.length());
                raf.write(content.getBytes(Charset.forName("UTF-8")));
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFunctionName() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(Thread.currentThread().getStackTrace()[3].getMethodName());
            sb.append("()");
            sb.append(" ");
        } catch (Exception e) {
        }
        return sb.toString();
    }

    public static String getThreadName() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(Thread.currentThread().getName());
            sb.append("-> ");
            sb.append(Thread.currentThread().getStackTrace()[3].getMethodName());
            sb.append("()");
            sb.append(" ");
        } catch (Exception e) {
            LogUtils.loge(TAG, e.getMessage());
        }
        return sb.toString();
    }

    public static String getCurrentLocation(String className, Exception exception) {
        int line = getLineNumber(exception);
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(Thread.currentThread().getName());
            sb.append("-> ");
            sb.append(className);
            sb.append("-> ");
            sb.append(Thread.currentThread().getStackTrace()[3].getMethodName());
            sb.append("()");
            sb.append("-> ");
            sb.append("line in");
            sb.append("-> ");
            sb.append(line);
        } catch (Exception e) {
            LogUtils.loge(TAG, e.getMessage());
        }
        return sb.toString();
    }

    /**
     * 得到Exception所在代码的行数 如果没有行信息,返回-1
     */
    private static int getLineNumber(Exception e) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null || trace.length == 0)
            return -1; //
        return trace[0].getLineNumber();
    }

    private static String formatLog(String log, String type, String level) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        synchronized (sFormatter) {
            builder.append(sFormatter.format(Calendar.getInstance().getTime()));
        }
        builder.append("][");
        builder.append(type);
        builder.append("][");
        builder.append(level);
        builder.append("]");
        builder.append(log);
        builder.append("\n");
        return builder.toString();
    }

    private static String getExternalStorageDirectory() {
        String rootpath = Environment.getExternalStorageDirectory().getPath();
        if (!rootpath.endsWith(File.separator)) {
            rootpath += File.separator;
        }
        Log.d(TAG, "getExternalStorageDirectory() path = " + rootpath);
        return rootpath;
    }

    public static String getClassName() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("-> ");
            sb.append(Thread.currentThread().getStackTrace()[2].getClassName());
            sb.append(".");
        } catch (Exception e) {

        }
        return sb.toString();
    }
}
// Gionee <hcy><2012-6-17> add for CR00825880 end

