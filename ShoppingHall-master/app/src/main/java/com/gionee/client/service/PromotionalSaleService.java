package com.gionee.client.service;

import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.myfavorites.MyFavoritesActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.manage.GNActivityManager;
import com.gionee.client.business.push.BaiduPushUtils;
import com.gionee.client.business.statistic.IStatisticsEventManager;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.statistic.StatisticsEventManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.AsyncImageLoader;
import com.gionee.client.business.util.AsyncImageLoader.ImageCallback;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.page.PageCacheManager;
import com.gionee.framework.operation.utills.BitmapUtills;
import com.wayde.ads.EggAdsProxy;
import com.wayde.ads.model.Utils;

/**
 * @author wuhao
 * @date create at 2015-2-2 下午2:17:42
 * @description 接受服务端各种促销信息
 */
public class PromotionalSaleService extends Service {
	private static final String TAG = "com.gionee.client.service.PromotionalSaleService";
	public static final String PROMOTIONAL_SALE_SERVICE = "com.gionee.client.service.promotionalSale";
	public static final String PROMOTIONAL_SALE_RECEVICE = "com.gionee.client.recevice.promotionalSale";
	public static final String RESTART_SALE_SERVICE = "com.gionee.client.restart.promotionalSale";
	public static final String NETWORK_SALE_SERVICE = "com.gionee.client.network.promotionalSale";
	public static final String APP_EXIT_SERVICE = "com.gionee.client.app.exit.promotionalSale";
	private RequestAction mRequestAction;
	private NotificationManager mNotificationManager;
	private MyBean myBean;
	private HandlerThread mHandlerThread;
	private Handler mHandler;
	private SaleDiacountRecevicer mSaleDiacountRecevicer;
	private RequsetDataRunnable mRequsetDataRunnable;
	private ReqeustDataHandler mReqeustDataHandler;
	private Context mContext;
	private static final int MINTIME = 2 * 60 * 60;
	private static final int SEND_DATA = 1;
	private static final int CHECK_SERVICES = 2;
	private IStatisticsEventManager mStatisticsEventManager;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		mRequestAction = new RequestAction();
		myBean = PageCacheManager.LookupPageData(this.getClass().getName());
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mHandlerThread = new HandlerThread(this.getClass().getName());
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
		mRequsetDataRunnable = new RequsetDataRunnable();
		mReqeustDataHandler = new ReqeustDataHandler();
		registerServerRecevier();
		startRequestData(0);
		mainHandler.sendEmptyMessageDelayed(CHECK_SERVICES, 60 * 1000);
		mStatisticsEventManager = new StatisticsEventManager(this);
		mStatisticsEventManager.initStatisticsData();
		if (BaiduPushUtils.getIsSubmitStatistics(mContext)) {
//			Log.i("mmmmmm",
//					"PromotionalSaleService:"
//							+ BaiduPushUtils.getStatisticsSource(mContext));
			submitStatisticsData(BaiduPushUtils.getStatisticsSource(mContext));
			BaiduPushUtils.setIsSubmitStatistics(mContext, false, BaiduPushUtils.getStatisticsSource(mContext));
		}
	}

	private void startRequestData(long startTime) {
		mHandler.removeCallbacks(mRequsetDataRunnable);
		mHandler.postDelayed(mRequsetDataRunnable, startTime);
	}

	private void registerServerRecevier() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(RESTART_SALE_SERVICE);
		filter.addAction(NETWORK_SALE_SERVICE);
		filter.addAction(APP_EXIT_SERVICE);
		mSaleDiacountRecevicer = new SaleDiacountRecevicer();
		registerReceiver(mSaleDiacountRecevicer, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent(PROMOTIONAL_SALE_RECEVICE);// 防止服务被杀掉
		sendBroadcast(intent);
	};

	private void requestSalesData() {
		mRequestAction.getPromotionalSaleInfo(mReqeustDataHandler,
				HttpConstants.Data.PromotionalSaleInfo.PROMOTIONAL_SALE_INFO);
	}

	private void processReturnData(JSONObject jsonObject) {
		try {
			JSONObject discountObj = jsonObject
					.getJSONObject(HttpConstants.Response.PromotionalSales.DISCOUNT_JO);
			long next = discountObj
					.optLong(HttpConstants.Response.PromotionalSales.NEXT_I);
			JSONObject dataObj = discountObj
					.getJSONObject(HttpConstants.Response.DATA);
			LogUtils.log("PromotionalSaleService",
					"dataObj-->" + dataObj.toString());
			if (!dataObj.has(HttpConstants.Response.PromotionalSales.MSG_S)) {
				LogUtils.log("PromotionalSaleService", "dataObj--->"
						+ "no data");
				if (next < MINTIME) {
					startRequestData(MINTIME * 1000);
				} else {
					startRequestData(next * 1000);
				}
				return;
			}
			LogUtils.log("PromotionalSaleService", "dataObj---" + "has data");
			String img = dataObj
					.optString(HttpConstants.Response.PromotionalSales.IMG_S);
			String msg = dataObj
					.optString(HttpConstants.Response.PromotionalSales.MSG_S);
			String title = dataObj
					.optString(HttpConstants.Response.PromotionalSales.TITLE_S);
			displayNotification(img, msg, title, next);
		} catch (JSONException e) {
			e.printStackTrace();
			LogUtils.log("PromotionalSaleService", "JSONException---");
			startRequestData(MINTIME * 1000);// 程序异常处理
		}
	}

	private void displayNotification(String imageUrl, final String msg,
			final String title, final long next) {
		LogUtils.log("PromotionalSaleService", "displayNotification");
		if (TextUtils.isEmpty(imageUrl)
				|| (imageUrl != null && imageUrl.equals("null"))) {
			PromotionalSaleService.this.notify(next, msg, title, null);
			return;
		}

		AsyncImageLoader.getInstance().loadBitmap(imageUrl,
				new ImageCallback() {

					@Override
					public void imageSuccess(Bitmap imageDrawable,
							String imageUrl) {
						LogUtils.log("PromotionalSaleService", "imageSuccess");
						PromotionalSaleService.this.notify(next, msg, title,
								imageDrawable);
					}

					@Override
					public void imageFailed(String imageUrl) {
						LogUtils.log("PromotionalSaleService", "imageFailed");
						PromotionalSaleService.this.notify(next, msg, title,
								null);
					}
				});

	}

	private void notify(final long next, final String msg, final String title,
			final Bitmap loadedImage) {
		notifyNotification(msg, title, loadedImage);
		if (next < MINTIME) {
			LogUtils.log("PromotionalSaleService", "next < MINTIME");
			startRequestData(MINTIME * 1000);
		} else {
			LogUtils.log("PromotionalSaleService", "next > MINTIME");
			startRequestData(next * 1000);
		}
	}

	@SuppressLint("NewApi")
	private void notifyNotification(final String msg, final String title,
			Bitmap loadedImage) {
		if (AndroidUtils.isRunningForeground(getApplicationContext())) {
			LogUtils.log("PromotionalSaleService", "isRunningForeground");
			return;
		}
		if (GNActivityManager.getScreenManager().isGnHomeActivityRun()) {
			LogUtils.log("PromotionalSaleService", "isGnHomeActivityRun");
			return;
		}
		if (!BaiduPushUtils.getPushSwich(PromotionalSaleService.this)) {
			LogUtils.log("PromotionalSaleService", "getPushSwich");
			return;
		}
		try {
			Notification notification = newNotification(msg, title);
			if (loadedImage == null) {
				notification.icon = R.drawable.ic_launcher;
				mNotificationManager.notify(R.string.app_name, notification);
				removeLowPriceNotice();// 避免重复显示减价商品信息
				LogUtils.log("PromotionalSaleService", "loadedImage == null");
				StatService.onEvent(mContext,
						BaiduStatConstants.LOCAL_PUSH_SHOW,
						BaiduStatConstants.LOCAL_PUSH_SHOW);
				return;
			}
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager manager = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(metrics);
			Bitmap bitmap = null;
			int width = 0;
			if (metrics.widthPixels < 800) {
				width = 80;
				bitmap = BitmapUtills.resizeImage(loadedImage,
						AndroidUtils.dip2px(mContext, width),
						AndroidUtils.dip2px(mContext, width));
				if (notification.contentView != null) {
					notification.contentView.setImageViewBitmap(getIconId(),
							bitmap);
				}
			} else {
				if (notification.contentView != null) {
					notification.contentView.setImageViewBitmap(getIconId(),
							loadedImage);
				}
			}
			mNotificationManager.notify(R.string.app_name, notification);
			removeLowPriceNotice();// 避免重复显示减价商品信息
			StatService.onEvent(mContext, BaiduStatConstants.LOCAL_PUSH_SHOW,
					BaiduStatConstants.LOCAL_PUSH_SHOW);
		} catch (Exception e) {
			LogUtils.log("PromotionalSaleService", "e.printStackTrace()");
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private Notification newNotification(final String msg, final String title) {
		Notification notification = new Notification(R.drawable.ic_launcher,
				title, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// 设置notification发生时同时发出默认声音
		notification.defaults = Notification.DEFAULT_SOUND;
		Intent intent1 = new Intent(mContext, GnHomeActivity.class);
		Intent intent2 = new Intent(mContext, MyFavoritesActivity.class);
		intent1.putExtra(Constants.CLEAR_LOW_PRICE, true);
		intent2.putExtra(Constants.HAS_LOW_PRICE, true);
		intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			if (AndroidUtils.getAndroidSDKVersion() >= 11) {
				PendingIntent contentIntent = PendingIntent.getActivities(
						mContext, 0, new Intent[] { intent1, intent2 },
						PendingIntent.FLAG_UPDATE_CURRENT);
				notification.setLatestEventInfo(mContext, title, msg,
						contentIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notification;
	}

	private int getIconId() {
		int iconId = 0;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$id");
			Field field = clazz.getField("icon");
			field.setAccessible(true);
			iconId = field.getInt(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iconId;
	}

	private final class ReqeustDataHandler implements IBusinessHandle {
		@Override
		public void onSucceed(String businessType, boolean isCache,
				Object session) {
			if (businessType.equals(Url.GET_PROMOTIONAL_SALES_URL)) {
				JSONObject jsonObject = myBean
						.getJSONObject(HttpConstants.Data.PromotionalSaleInfo.PROMOTIONAL_SALE_INFO);
				processReturnData(jsonObject);
			}
			if (businessType.equals(Url.STATISTICS_SUBMIT)) {
				mStatisticsEventManager.removeStatisticsData();
			}
		}

		@Override
		public void onErrorResult(String businessType, String errorOn,
				String errorInfo, Object session) {
			LogUtils.log(TAG, LogUtils.getThreadName() + errorInfo);
			if (businessType.equals(Url.STATISTICS_SUBMIT)) {
				mStatisticsEventManager.saveStatisticsData();
			}
		}

		@Override
		public void onCancel(String businessType, Object session) {
			LogUtils.log(TAG, LogUtils.getThreadName());
		}

		@Override
		public Context getSelfContext() {
			return mContext;
		}
	}

	class SaleDiacountRecevicer extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(RESTART_SALE_SERVICE)) {
				boolean check = intent.getBooleanExtra("check", true);
				if (check) {
					startRequestData(0);
				} else {
					mHandler.removeCallbacks(mRequsetDataRunnable);
				}
			}
			if (intent.getAction().equals(NETWORK_SALE_SERVICE)) {
				if (AndroidUtils.isRunningForeground(getApplicationContext())) {
					LogUtils.log("PromotionalSaleService",
							"isRunningForeground");
					return;
				}
				if (GNActivityManager.getScreenManager().isGnHomeActivityRun()) {
					LogUtils.log("PromotionalSaleService",
							"isGnHomeActivityRun");
					return;
				}
				if (!BaiduPushUtils.getPushSwich(PromotionalSaleService.this)) {
					LogUtils.log("PromotionalSaleService", "getPushSwich");
					return;
				}
				try {
					startRequestData(0);
				} catch (Exception e) {
					e.printStackTrace();
					sendBroadcast(new Intent(PROMOTIONAL_SALE_RECEVICE));
				}
			}
			if (intent.getAction().equals(APP_EXIT_SERVICE)) {
				String mIntentSource = intent.getStringExtra("mIntentSource");
				BaiduPushUtils.setIsSubmitStatistics(mContext, true,
						mIntentSource);
			}
		}

	}

	private void submitStatisticsData(String intentSource) {
		mRequestAction.submitStatisticsData(mReqeustDataHandler, null, this,
				mStatisticsEventManager.buildStatisticsData(), intentSource);
	}

	class RequsetDataRunnable implements Runnable {
		@Override
		public void run() {
			mainHandler.sendEmptyMessage(SEND_DATA);
		}
	}

	private Handler mainHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case SEND_DATA:
				requestSalesData();
				break;
			case CHECK_SERVICES:
				if (!Utils.isProcessRunning(PromotionalSaleService.this,
						com.wayde.ads.model.Constants.ADVERTISE_PKG_NAME)) {
					LogUtils.logd(TAG, LogUtils.getThreadName()
							+ " ads services process died, restart it");
					EggAdsProxy.startEggAds(PromotionalSaleService.this);
				} else {
					LogUtils.logd(TAG, LogUtils.getThreadName()
							+ "ads services process runing");
				}
				mainHandler.sendEmptyMessageDelayed(CHECK_SERVICES, 60 * 1000);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 移除低价商品
	 */
	private void removeLowPriceNotice() {
		RequestAction action = new RequestAction();
		action.removeLowPriceNotice(mReqeustDataHandler);
	}

}
