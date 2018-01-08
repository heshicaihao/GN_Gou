// Gionee <yuwei><2013-11-21> add for CR00821559 begin
/*
 * InstallUtills.java
 * classes : com.gionee.client.business.util.InstallUtills
 * @author yuwei
 * V 1.0.0
 * Create at 2013-11-21 上午11:24:00
 */
package com.gionee.client.business.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.gionee.client.R;
import com.gionee.framework.operation.page.FrameWorkInit;

/**
 * InstallUtills
 * 
 * @author yuwei <br/>
 * @date create at 2013-11-21 上午11:24:00
 * @description TODO 应用安装工具类
 */
@SuppressLint("NewApi")
public class DownLoadUtill {
    private final static String TAG = "InstallManager";
    private static final String DOWNLOAD_PATH = "/GN_Gou/";

    public static void AlertInstall(File file, Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 检查手机上是否安装了指定的软件
     * 
     * @param context
     * @param packageName
     *            ：应用包名
     * @return
     */
    public static PackageInfo getPackgeInfo(Context context, String packageName) {
        List<PackageInfo> packageList = getPackageList(context);
        if (packageList != null) {
            for (int i = 0; i < packageList.size(); i++) {
                PackageInfo info = packageList.get(i);
                String appName = info.packageName;
                if (appName.equals(packageName)) {
                    return info;
                }
            }
        }
        return null;
    }

    public static List<PackageInfo> getPackageList(Context context) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        return packageInfos;
    }

    /**
     * @param fileName
     * @return
     * @author yuwei
     * @description TODO
     */
    public static String generateFilePath(String fileName) {
        String filePath = FrameWorkInit.SDCARD + DOWNLOAD_PATH + fileName;
        return filePath;
    }

    /**
     * @param context
     * @param url
     * @param fileName
     * @param mFile
     * @param prefs
     * @author yuwei
     * @description TODO
     */
    public static void downloadFile(Context context, String url) {
        try {
            LogUtils.log(TAG, LogUtils.getThreadName() + "fileName=" + "file is not exits" + "absolute path=");
            if (!AndroidUtils.isExistSDCard()) {
                Toast.makeText(context, R.string.sd_not_exist, Toast.LENGTH_SHORT).show();
                return;
            }
            String fileName = getFileName(url);
            String urlNoParams = getNoParamsUrl(url);
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            startDownload(context, urlNoParams, fileName);
            Toast.makeText(context, fileName + "已开始下载", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        prefs.edit().putLong(file.getAbsolutePath(), id).commit();
//        prefs.edit().putString(id + "", appInfo.toString()).commit();
    }

    /**
     * @param apkName
     * @param appVersion
     * @return
     * @author yuwei
     * @description TODO
     */
    public static String generateFileName(String apkName, int appVersion) {
        String preFileName = apkName.substring(0, apkName.length() - 4);
        String fileName = preFileName + "_" + appVersion + ".apk";
        return fileName;
    }

    private static boolean isFolderExist(String dir) {
        File folder = Environment.getExternalStoragePublicDirectory(dir);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    /**
     * @param context
     * @param url
     *            下载的url地址
     * @param fileName
     *            下载后保存的文件名
     * @author yuwei
     * @description TODO
     */
    @SuppressWarnings("deprecation")
    private static long startDownload(Context context, String url, String fileName) {
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        LogUtils.log(TAG, LogUtils.getThreadName() + "url=" + url + "开始下载");
        Uri resource = Uri.parse(encodeGB(url));
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setAllowedNetworkTypes(Request.NETWORK_WIFI | Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false); //
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        request.setShowRunningNotification(true);
        request.setVisibleInDownloadsUi(true);
        isFolderExist(DOWNLOAD_PATH);
        request.setDestinationInExternalPublicDir(DOWNLOAD_PATH, fileName);
        return downloadManager.enqueue(request);
    }

    public static String getFileName(String url) {

        // 从路径中获取

        String fileName = url.substring(url.lastIndexOf("/") + 1);
        fileName = getNoParamsUrl(fileName);

        return fileName;

    }

    /**
     * @param fileName
     * @return
     * @author yuwei
     * @description TODO
     */
    private static String getNoParamsUrl(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        return url;
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     * 
     * @param string
     * @return
     */
    public static String encodeGB(String string) {
        // 转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0] + "/" + split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");// 处理空格
        return split[0];
    }

    /**
     * @param context
     * @param downloadId
     * @author yuwei
     * @description TODO 查询下载状态
     */
    public static void queryDownloadStatus(final Context context, final long downloadId) {
        LogUtils.log(TAG, "下载完成");

        LogUtils.log(TAG, LogUtils.getThreadName());
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        LogUtils.log(TAG, "查询下载状态");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            String apkPath = getFilePath(c);
            File apkFile = new File(apkPath);
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    LogUtils.log(TAG, "STATUS_PAUSED");
                    break;
                case DownloadManager.STATUS_PENDING:
                    LogUtils.log(TAG, "STATUS_PENDING");
                    break;
                case DownloadManager.STATUS_RUNNING:
                    LogUtils.log(TAG, "STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    AlertInstall(apkFile, context);
                    break;
                case DownloadManager.STATUS_FAILED:
                    // 清除已下载的内容，重新下载
                    LogUtils.log(TAG, "STATUS_FAILED");
                    downloadManager.remove(downloadId);
                    prefs.edit().remove(apkFile.getAbsolutePath()).commit();
                    deleteFile(apkFile);
                    break;
                default:
                    break;

            }
            c.close();
        }

    }

    /**
     * @param c
     * @return
     * @author yuwei
     * @description TODO
     */
    private static String getFilePath(Cursor c) {

        String apkPath;
        try {
            apkPath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)).replace("file://", "");
            LogUtils.log(TAG, "查询下载路经===" + apkPath);
            return apkPath;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param downloadId
     * @param downloadManager
     * @param prefs
     * @param apkFile
     * @author yuwei
     * @description TODO
     */
    public static boolean isApkDeleted(long downloadId, Context context, File apkFile) {
        if (!apkFile.exists()) {
            DownloadManager downloadManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            downloadManager.remove(downloadId);
            prefs.edit().remove(apkFile.getAbsolutePath()).commit();
            return true;
        }
        return false;
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private static boolean deleteFile(File file) {
        return (file != null && file.exists()) ? file.delete() : false;

    }

    public static boolean slientInstall(File file) {
        String[] args = {"pm", "install", "-r", file.getAbsolutePath()};
        boolean result = false;
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
//            byte[] data = baos.toByteArray();
            int value = process.waitFor();
            // 代表成功
            if (value == 0) {
                LogUtils.log(TAG, LogUtils.getThreadName() + "安装成功");
                result = true;
            } else if (value == 1) { // 失败\
                LogUtils.log(TAG, LogUtils.getThreadName() + "安装失败");
                result = false;
            } else { // 未知情况
                LogUtils.log(TAG, LogUtils.getThreadName() + "安装失败 : value != 0 or 1");
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    public static PackageInfo getPackageInfoByPath(Context context, String path) {

        PackageManager packageManger = context.getPackageManager();
        PackageInfo p = null;
        try {
            p = packageManger.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            return p;
        }
        return p;
    }

    public static String getFileMd5(File file) {
        byte[] digest = null;
        FileInputStream in = null;
        if (file == null) {
            return null;
        }
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8192];
            in = new FileInputStream(file);
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            digest = digester.digest();
        } catch (Exception cause) {
            throw new RuntimeException("Unable to compute MD5 of \"" + file + "\"", cause);
        } finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (Exception e) {
                }

            }
        }
        return (digest == null) ? null : byteArrayToString(digest);
    }

    private static String byteArrayToString(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length << 1);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(Character.forDigit((bytes[i] >> 4) & 0xf, 16));
            ret.append(Character.forDigit(bytes[i] & 0xf, 16));
        }
        return ret.toString();
    }

}
//Gionee <yuwei><2013-12-11> add for CR00821559 end