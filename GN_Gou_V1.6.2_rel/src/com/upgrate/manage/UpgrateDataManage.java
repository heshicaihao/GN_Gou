package com.upgrate.manage;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;

import com.gionee.client.business.persistent.ShareDataManager;

public class UpgrateDataManage {
	public static boolean IS3GENVIRONMENT = false;// 是否是在3G环境下
	public static final String IS_DOWNLOADING_TIP = "is_download_tip";// 升级是否提示文件名
	public static final String IS_INSTALL_TIP = "is_install_tip";// 安装是否提示文件名
	public static final String NO_INSTALL_COUNT = "no_install_count";// 暂不安装点击次数文件
	public static final String IS_FIRST_SHOW_INSTALL_TIP = "is_first_show_install_tip";// 是否是下载完后第一次显示安装提示
	public static final String FILE_IS_DOWNLOAD_COMPLETE = "file_is_download_complete";// 文件是否下载完成
	public static final String IS_APK_START_DOWNLOAD = "is_apk_start_download";// apk是否点击过开启下载
	public static final String APK_ALL_SIZE = "apk_all_size";// apk文件总大小
	public static final String APK_DOWNLOAD_SIZE = "apk_download_size";// apk已下载大小

	/**
	 * 
	 */
	public static boolean getDownLoadingIsTip(Context context,
			String versionName) {
		SharedPreferences preferences;
		try {
			preferences = context.getSharedPreferences(IS_DOWNLOADING_TIP,
					Context.MODE_MULTI_PROCESS);
			return preferences.getBoolean(versionName, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void saveDownLoadingIsTip(Context context,
			String versionName, boolean downloadingIsTip) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(
					IS_DOWNLOADING_TIP, Context.MODE_MULTI_PROCESS);
			preferences.edit().clear();
			preferences.edit().putBoolean(versionName, downloadingIsTip)
					.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public static boolean getInstallIsTip(Context context, String versionName) {
		SharedPreferences preferences;
		try {
			preferences = context.getSharedPreferences(IS_INSTALL_TIP,
					Context.MODE_MULTI_PROCESS);
			return preferences.getBoolean(versionName, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void saveInstallIsTip(Context context, String versionName,
			boolean installIsTip) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(
					IS_INSTALL_TIP, Context.MODE_MULTI_PROCESS);
			preferences.edit().clear();
			preferences.edit().putBoolean(versionName, installIsTip).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public static int getNoInstallCount(Context context, String versionName) {
		SharedPreferences preferences;
		try {
			preferences = context.getSharedPreferences(NO_INSTALL_COUNT,
					Context.MODE_MULTI_PROCESS);
			return preferences.getInt(versionName, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void saveNoInstallCount(Context context, String versionName) {
		int oldCount = 0;
		try {
			SharedPreferences preferences = context.getSharedPreferences(
					NO_INSTALL_COUNT, Context.MODE_MULTI_PROCESS);
			oldCount = preferences.getInt(versionName, 0);
			preferences.edit().clear();
			preferences.edit().putInt(versionName, ++oldCount).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isFileDownloadComplete(Context context,
			String versionName) {
		SharedPreferences preferences;
		try {
			preferences = context.getSharedPreferences(
					FILE_IS_DOWNLOAD_COMPLETE, Context.MODE_MULTI_PROCESS);
			return preferences.getBoolean(versionName, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void saveFileDownloadComplete(Context context,
			String versionName, boolean isComplete) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(
					FILE_IS_DOWNLOAD_COMPLETE, Context.MODE_MULTI_PROCESS);
			preferences.edit().clear();
			preferences.edit().putBoolean(versionName, isComplete).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isFirstShowInstallTip(Context context,
			String versionName) {
		SharedPreferences preferences;
		try {
			preferences = context.getSharedPreferences(
					IS_FIRST_SHOW_INSTALL_TIP, Context.MODE_MULTI_PROCESS);
			return preferences.getBoolean(versionName, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void saveIsFirstShowInstallTip(Context context,
			String versionName, boolean isFirstShowInstallTip) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(
					IS_FIRST_SHOW_INSTALL_TIP, Context.MODE_MULTI_PROCESS);
			preferences.edit().clear();
			preferences.edit().putBoolean(versionName, isFirstShowInstallTip)
					.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isStartDownload(Context context, String versionName) {
		SharedPreferences preferences;
		try {
			preferences = context.getSharedPreferences(IS_APK_START_DOWNLOAD,
					Context.MODE_MULTI_PROCESS);
			return preferences.getBoolean(versionName, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void saveIsStartDownload(Context context, String versionName,
			boolean isComplete) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(
					IS_APK_START_DOWNLOAD, Context.MODE_MULTI_PROCESS);
			preferences.edit().clear();
			preferences.edit().putBoolean(versionName, isComplete).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getApkAllSize(Context context, String versionName) {
		SharedPreferences preferences;
		try {
			preferences = context.getSharedPreferences(APK_ALL_SIZE,
					Context.MODE_MULTI_PROCESS);
			return preferences.getInt(versionName, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void saveApkAllSize(Context context, String versionName,
			int apkAllSize) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(
					APK_ALL_SIZE, Context.MODE_MULTI_PROCESS);
			preferences.edit().clear();
			preferences.edit().putInt(versionName, apkAllSize).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getApkDownloadSize(Context context, String versionName) {
		try {
			File dir = new File(Environment.getExternalStorageDirectory()
					.getAbsoluteFile().getPath()
					+ Download.FILEFOLDER);// 文件保存目录
			String filename = Download.FILENAMEPREFIX + versionName + ".apk";
			String mFilePath = dir.getAbsolutePath() + "/" + filename;
			if (new File(mFilePath) != null && new File(mFilePath).exists()) {
				SharedPreferences preferences;
				try {
					preferences = context.getSharedPreferences(APK_DOWNLOAD_SIZE,
							Context.MODE_MULTI_PROCESS);
					return preferences.getInt(versionName, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				saveApkDownloadSize(context, versionName, 0);
				return 0;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	public static void saveApkDownloadSize(Context context, String versionName,
			int apkDownloadSize) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(
					APK_DOWNLOAD_SIZE, Context.MODE_MULTI_PROCESS);
			preferences.edit().clear();
			preferences.edit().putInt(versionName, apkDownloadSize).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String kToM(int bit) {
		String p = "";
		DecimalFormat decimalFormat = new DecimalFormat("0.0");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		float m = ((float) bit) / 1024.0f;
		if (m < 1024) {
			p = decimalFormat.format(m) + "k";// format 返回的是字符串
		} else {
			m = m / 1024.0f;
			DecimalFormat decimalFormatM = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
			p = decimalFormatM.format(m) + "MB";// format 返回的是字符串
		}
		return p;
	}

	public static boolean getSdcardIsHasFreeBlock() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return false;
		}
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		// return freeBlocks * blockSize; //单位Byte
		// return (freeBlocks * blockSize)/1024; //单位KB
		return (freeBlocks * blockSize) / 1024 / 1024 > 1; // 单位MB
	}
}
