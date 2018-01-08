package com.upgrate.download;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import android.content.Context;
import android.util.Log;

import com.baidu.mobstat.StatService;
import com.upgrate.manage.Download;
import com.upgrate.manage.UpgrateDataManage;

/**
 * 文件下载器
 * 
 */
public class FileDownloader {
	private static final String TAG = "FileDownloader";
	private Context mContext;
	/* 停止下载 */
	private boolean mExit;
	/* 已下载文件长度 */
	private int mDownloadSize = 0;
	/* 原始文件长度 */
	private int mFileSize = 0;
	/* 线程数 */
	private DownloadThread[] mThreads;
	/* 本地保存文件 */
	private File mSaveFile;
	/* 缓存各线程下载的长度 */
	private Map<Integer, Integer> mData = new ConcurrentHashMap<Integer, Integer>();
	/* 每条线程下载的长度 */
	private int mBlock;
	/* 下载路径 */
	public String mDownloadUrl;

	private String mVersionName;

	public Download mDownload;

	/**
	 * 获取线程数
	 */
	public int getThreadSize() {
		return mThreads.length;
	}

	/**
	 * 退出下载
	 */
	public void exit() {
		this.mExit = true;
	}

	public boolean getExit() {
		return this.mExit;
	}

	public void setIsExit(boolean isExit) {
		this.mExit = isExit;
	}

	/**
	 * 获取文件大小
	 * 
	 * @return
	 */
	public int getFileSize() {
		return mFileSize;
	}

	/**
	 * 累计已下载大小
	 * 
	 * @param size
	 */
	protected synchronized void append(int size) {
		mDownloadSize += size;
	}

	/**
	 * 更新指定线程最后下载的位置
	 * 
	 * @param threadId
	 *            线程id
	 * @param pos
	 *            最后下载的位置
	 */
	protected synchronized void update(int threadId, int pos) {
		this.mData.put(threadId, pos);
		FileService.update(this.mDownloadUrl, threadId, pos);
	}

	/**
	 * 构建文件下载器
	 * 
	 * @param downloadUrl
	 *            下载路径
	 * @param fileSaveDir
	 *            文件保存目录
	 * @param threadNum
	 *            下载线程数
	 */
	public FileDownloader(Download download, Context context,
			String downloadUrl, File fileSaveDir, String filename,
			int threadNum, String versionName) {
		try {
			this.mContext = context;
			this.mDownloadUrl = downloadUrl;
			this.mVersionName = versionName;
			this.mDownload = download;
			URL url = new URL(this.mDownloadUrl);
			if (!fileSaveDir.exists()) {
				// 判断目录是否存在，如果不存在，创建目录
				fileSaveDir.mkdirs();
			}
			if (fileSaveDir.listFiles() == null
					|| fileSaveDir.listFiles().length == 0) {
				// 清除数据,人工手动删除apk包后数据库也要对应删除
				FileService.delete(downloadUrl);
			}
			this.mThreads = new DownloadThread[threadNum];// 实例化线程数组
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Referer", downloadUrl);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect(); // 连接
			printResponseHeader(conn);
			if (conn.getResponseCode() == 200) { // 响应成功
				if (UpgrateDataManage.getApkAllSize(mContext, mVersionName) > 0) {
					this.mFileSize = UpgrateDataManage.getApkAllSize(mContext,
							mVersionName);
				} else {
					this.mFileSize = conn.getContentLength();// 根据响应获取文件大小
				}
				if (this.mFileSize <= 0) {
					Download.getInstance(context).exit();
					throw new RuntimeException("Unkown file size ");
				}
				this.mSaveFile = new File(fileSaveDir, filename);// 构建保存文件
				File[] files = fileSaveDir.listFiles();
				// 删除非正需要下载的其他apk文件
				for (File file : files) {
					if (!filename.equals(file.getName())) {
						file.delete();
					}
				}
				Map<Integer, Integer> logdata = FileService
						.getData(downloadUrl);// 获取下载记录
				if (logdata.size() > 0) {
					// 如果存在下载记录
					for (Map.Entry<Integer, Integer> entry : logdata.entrySet()) {
						mData.put(entry.getKey(), entry.getValue());// 把各条线程已经下载的数据长度放入data中
					}
				}
				if (this.mData.size() == this.mThreads.length) {
					// 下面计算所有线程已经下载的数据总长度
					for (int i = 0; i < this.mThreads.length; i++) {
						this.mDownloadSize += this.mData.get(i + 1);
					}
					print("已经下载的长度" + this.mDownloadSize);
				}
				// 计算每条线程下载的数据长度
				this.mBlock = (this.mFileSize % this.mThreads.length) == 0 ? this.mFileSize
						/ this.mThreads.length
						: this.mFileSize / this.mThreads.length + 1;
			} else {
				Log.i("hhhhhh", "FileDownloader error1");
				Download.getInstance(context).exit();
				throw new RuntimeException("server no response ");
			}
		} catch (Exception e) {
			print(e.toString());
			Log.i("hhhhhh", "FileDownloader error2");
			Download.getInstance(context).exit();
			throw new RuntimeException("don't connection this url");
		}
	}

	/**
	 * 开始下载文件
	 * 
	 * @param listener
	 *            监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
	 * @return 已下载文件大小
	 * @throws Exception
	 */
	public int download(DownloadProgressListener listener) throws Exception {
		try {
			RandomAccessFile randOut = new RandomAccessFile(this.mSaveFile,
					"rw");
			if (this.mFileSize > 0) {
				randOut.setLength(this.mFileSize); // 预分配fileSize大小
			}
			randOut.close();
			URL url = new URL(this.mDownloadUrl);
			if (this.mData.size() != this.mThreads.length) {
				// 如果原先未曾下载或者原先的下载线程数与现在的线程数不一致
				this.mData.clear();
				for (int i = 0; i < this.mThreads.length; i++) {
					this.mData.put(i + 1, 0);// 初始化每条线程已经下载的数据长度为0
				}
				this.mDownloadSize = 0;
			}
			for (int i = 0; i < this.mThreads.length; i++) {
				// 开启线程进行下载
				int downLength = this.mData.get(i + 1);
				if (downLength < this.mBlock
						&& this.mDownloadSize < this.mFileSize) {
					// 判断线程是否已经完成下载,否则继续下载
					this.mThreads[i] = new DownloadThread(this, url,
							this.mSaveFile, this.mBlock, this.mData.get(i + 1),
							i + 1, mContext, mVersionName);
					this.mThreads[i].setPriority(7); // 设置线程优先级
					this.mThreads[i].start();
				} else {
					this.mThreads[i] = null;
				}
			}
			FileService.delete(this.mDownloadUrl);// 如果存在下载记录，删除它们，然后重新添加
			FileService.save(this.mDownloadUrl, this.mData);
			boolean notFinish = true;// 下载未完成
			while (notFinish && !getExit()) {
				// 循环判断所有线程是否完成下载
				Thread.sleep(900);
				notFinish = false;// 假定全部线程下载完成
				for (int i = 0; i < this.mThreads.length; i++) {
					if (this.mThreads[i] != null
							&& !this.mThreads[i].isFinish()) {
						// 如果发现线程未完成下载
						notFinish = true;// 设置标志为下载没有完成
						if (this.mThreads[i].getDownLength() == -1) {
							// 如果下载失败,再重新下载
							this.mThreads[i] = new DownloadThread(this, url,
									this.mSaveFile, this.mBlock,
									this.mData.get(i + 1), i + 1, mContext,
									mVersionName);
							this.mThreads[i].setPriority(7);
							this.mThreads[i].start();
						}
					}
				}
				if (listener != null) {
					listener.onDownloadSize(this.mDownloadSize);// 通知目前已经下载完成的数据长度
				}
			}
			if (mDownloadSize == this.mFileSize) {
				UpgrateDataManage
						.saveApkDownloadSize(mContext, mVersionName, 0);
				FileService.delete(this.mDownloadUrl);// 下载完成删除记录
				StatService.onEvent(mContext, "upgrade_completed",
						"upgrade_completed");
			}
		} catch (Exception e) {
			print(e.toString());
			Download.getInstance(mContext).exit();
			throw new Exception("file download error");
		}
		return this.mDownloadSize;
	}

	/**
	 * 获取Http响应头字段
	 * 
	 * @param http
	 * @return
	 */
	public static Map<String, String> getHttpResponseHeader(
			HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null) {
				break;
			}
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
	}

	/**
	 * 打印Http头字段
	 * 
	 * @param http
	 */
	public static void printResponseHeader(HttpURLConnection http) {
		Map<String, String> header = getHttpResponseHeader(http);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
			print(key + entry.getValue());
		}
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}
}
