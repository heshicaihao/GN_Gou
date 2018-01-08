package com.upgrate.manage;

import java.io.File;
import org.json.JSONObject;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.gionee.client.model.HttpConstants;
import com.upgrate.download.DownloadProgressListener;
import com.upgrate.download.FileDownloader;

public class Download {
	private boolean mIsDownloadComplete = false;
	private static Download sDownload;
	private Context mContext;
	public static final String FILEFOLDER = "/GN_GOU/upgrate/";
	public static final String FILENAMEPREFIX = "gngou_";
	private int mMaxLength;
	private Handler mHandler;
	public FileDownloader mLoader;
	public boolean mIsHasStarDownload = false;
	public int mSize = 0;

	private Download(Context context) {
		mContext = context;
	}

	public static Download getInstance(Context context) {
		if (sDownload == null) {
			sDownload = new Download(context);
		}
		return sDownload;
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public boolean isDownloadComplete() {
		return mIsDownloadComplete;
	}

	public void setDownloadComplete(boolean isDownloadComplete) {
		this.mIsDownloadComplete = isDownloadComplete;
	}

	public void downloadUrl(JSONObject upgrateData) {
		// TODO Auto-generated method stub
		if (mIsHasStarDownload) {
			return;
		}
		if (!getExit()) {
			return;
		}
		setIsExit(false);
		if (mIsDownloadComplete) {
			return;// 假如下载完成，则不执行
		}
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// Toast.makeText(mActivity, R.string.sdcarderror, 1).show();
			return;
		}
		String url = upgrateData
				.optString(HttpConstants.Data.Upgrate.UPGRATE_URL_S);
		String versionName = upgrateData
				.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
		File dir = new File(Environment.getExternalStorageDirectory()
				.getAbsoluteFile().getPath()
				+ FILEFOLDER);// 文件保存目录
		String filename = FILENAMEPREFIX + versionName + ".apk";
		download(url, dir, filename, versionName);
	}

	public void download(final String url, final File dir,
			final String filename, final String versionName) {
		// TODO Auto-generated method stub
		new NetRequestTimeThread().start();
		mSize = 0;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mIsDownloadComplete = false;
					mIsHasStarDownload = true;
					UpgrateDataManage.saveIsStartDownload(mContext,
							versionName, mIsHasStarDownload);
					UpgrateDataManage.saveFileDownloadComplete(mContext,
							versionName, false);
					mLoader = new FileDownloader(Download.this, mContext, url,
							dir, filename, 3, versionName);
					int length = mLoader.getFileSize();// 获取文件的长度
//					Log.i("kkkkkk", "length:" + length);
					setmLength(length, versionName);
					mLoader.download(new DownloadProgressListener() {
						@Override
						public void onDownloadSize(int size) {
							// 可以实时得到文件下载的长度
							Message msg = new Message();
							msg.what = UpgrateDownloadManage.DOWNLOAD_SIZE;
							mSize = size;
							msg.getData().putInt("size", size);
							mHandler.sendMessage(msg);
							UpgrateDataManage.saveApkDownloadSize(mContext,
									versionName, size);
						}
					});
				} catch (Exception e) {
					Message msg = new Message();// 信息提示
					msg.what = UpgrateDownloadManage.NETERROR_MSG;
					// 如果下载错误，显示提示失败！
					mHandler.sendMessage(msg);
					exit();
					Log.i("hhhhhh", "download error1");
				}
			}
		}).start();// 开始
	}

	public void setmLength(int mLength, String versionName) {
		this.mMaxLength = mLength;
		UpgrateDataManage.saveApkAllSize(mContext, versionName, mLength);
	}

	public int getmLength() {
		return mMaxLength;
	}

	public void exit() {
		mIsHasStarDownload = false;
		if (mLoader != null) {
			mLoader.exit();
		}
	}

	public boolean getExit() {
		if (mLoader != null) {
			return mLoader.getExit();
		}
		return true;
	}

	public void setIsExit(boolean isExit) {
		if (mLoader != null) {
			mLoader.setIsExit(isExit);
		}
	}

	private class NetRequestTimeThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (mSize == 0) {
				Message msg = new Message();// 信息提示
				msg.what = -1;
				// 如果下载错误，显示提示失败！
				mHandler.sendMessage(msg);
				exit();
			}
		}
	}
}
