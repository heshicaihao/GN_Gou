// Gionee <yuwei><2013-11-21> add for CR00821559 begin
/*
 * InstallUtills.java
 * classes : com.gionee.client.business.util.InstallUtills
 * @author yuwei
 * V 1.0.0
 * Create at 2013-11-21 上午11:24:00
 */
package com.gionee.client.business.appDownload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.List;

import org.json.JSONObject;

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
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.operation.page.FrameWorkInit;

/**
 * InstallUtills
 * 
 * @author yuwei <br/>
 * @date create at 2013-11-21 上午11:24:00
 * @description TODO 应用安装工具类
 */
@SuppressLint("NewApi")
public class InstallUtills {
    private static final String TAG = "InstallManager";
    private static final String DOWNLOAD_PATH = "/GN_Gou/";

    public static void alertInstall(File file, Context context) {
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

    public static boolean isApkExist(String apkName) {
        String fileName = generateFilePath(apkName);
        File file = new File(fileName);
        return file != null && file.exists();

    }

    /**
     * @param context
     * @param mUrl
     * @param fileName
     * @param file
     * @param prefs
     * @author yuwei
     * @description TODO
     */
    public static void downloadFile(Context context, String fileName, File file, JSONObject appInfo) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "fileName=" + "file is not exits" + "absolute path="
                + file.getAbsolutePath() + "path=" + file.getPath());
        String url = appInfo.optString(HttpConstants.Response.AppStore.DOWNLOAD_URL_S);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long id = startDownload(context, url, fileName, false);
        prefs.edit().putLong(file.getAbsolutePath(), id).commit();
        prefs.edit().putString(id + "", appInfo.toString()).commit();
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

    public String[] getDownloadList() {
        String filePath = FrameWorkInit.SDCARD + DOWNLOAD_PATH;
        String[] fileList = null;
        if (isFolderExist(filePath)) {
            File folder = new File(filePath);
            fileList = folder.list();
        }
        return fileList;
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
    public static long startDownload(Context context, String url, String fileName, boolean isMobileEnable) {
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        LogUtils.log(TAG, LogUtils.getThreadName() + url);
        Uri resource = Uri.parse(url);
        LogUtils.log(TAG, LogUtils.getThreadName() + resource.toString());
        DownloadManager.Request request = new DownloadManager.Request(resource);
        if (isMobileEnable) {
            request.setAllowedNetworkTypes(Request.NETWORK_WIFI | Request.NETWORK_MOBILE);
        } else {
            request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
        }
        request.setAllowedOverRoaming(false); //
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        request.setTitle(fileName);
        request.setShowRunningNotification(false);
        request.setVisibleInDownloadsUi(false);
        isFolderExist(DOWNLOAD_PATH);
        request.setDestinationInExternalPublicDir(DOWNLOAD_PATH, fileName);
        long downloadId = downloadManager.enqueue(request);
        LogUtils.log(TAG, LogUtils.getThreadName() + "downloadId=" + downloadId);
        return downloadId;
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     * 
     * @param string
     * @return
     */
    public static String encodeGB(String string) {
        // 转换中文编码
        String[] split = string.split("/");
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
     * @param fileMd5Value
     *            the md5 value of this apk file
     * @param apkFile
     *            the apk file
     * @return
     * @author yuwei
     * @description TODO invalidate the download apk file is complete by its md5
     */
    public static boolean invalidataPackageComplete(final Context context, String fileMd5Value, File apkFile) {
        if (apkFile == null) {
            return false;
        }
        String localMd5 = InstallUtills.getFileMd5(apkFile);
        return !TextUtils.isEmpty(fileMd5Value) && fileMd5Value.equals(localMd5);
    }

    /**
     * @param appJsonInfo
     * @param apkFile
     * @author yuwei
     * @description TODO
     */
    public static boolean invalidataApk(Context context, JSONObject appJsonInfo, File apkFile) {
        if (appJsonInfo == null) {
            return false;
        }
        LogUtils.log("INVALIDATE_JSON", appJsonInfo.toString());
        String appPackageName = appJsonInfo.optString(HttpConstants.Response.AppStore.PACKAGE_S);
        int versionCode = appJsonInfo.optInt(HttpConstants.Response.AppStore.VERSION_S);
        String fileMd5 = appJsonInfo.optString(HttpConstants.Response.AppStore.APK_MD5_S);
        return invalidataAppInfo(context, apkFile, appPackageName, versionCode)
                && invalidataPackageComplete(context, fileMd5, apkFile);
    }

    /**
     * @param c
     * @return
     * @author yuwei
     * @description TODO
     */
    public static String getFilePath(Cursor c) {

        String apkPath;
        try {
            apkPath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)).replace("file://", "");
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
            clearCacheData(downloadId, context, apkFile);
            return true;
        }
        return false;
    }

    /**
     * @param downloadId
     * @param context
     * @param apkFile
     * @author yuwei
     * @description TODO
     */
    public static void clearCacheData(long downloadId, Context context, File apkFile) {
        try {
            DownloadManager downloadManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            downloadManager.remove(downloadId);
            prefs.edit().remove(downloadId + "").commit();
            prefs.edit().remove(apkFile.getAbsolutePath()).commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void removeDownloadHistory(Context context, long downloadId, File file) {
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.remove(downloadId);
        deleteFile(file);
    }

    public static void removeDownloadHistory(Context context, long downloadId) {
        try {
            DownloadManager downloadManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManager.remove(downloadId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    public static boolean deleteFile(File file) {
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
                result = true;
            } else { // 未知情况
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

    public static boolean invalidataAppInfo(Context context, File file, String packageName, int versionCode) {
        PackageInfo info = getPackageInfoByPath(context, file.getPath());
        return info != null && info.packageName.equals(packageName) && info.versionCode == versionCode;

    }

    public static String getFileMd5(File file) {
        byte[] digest = null;
        FileInputStream in = null;
        if (file == null || !file.exists()) {
            return "";
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