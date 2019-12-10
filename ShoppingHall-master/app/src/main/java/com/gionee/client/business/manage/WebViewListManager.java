/*
 * WebViewListManager.java
 * classes : com.gionee.client.business.manage.WebViewListManager
 * @author yuwei
 * 
 * Create at 2015-5-21 下午3:56:20
 */
package com.gionee.client.business.manage;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.widget.Toast;

import com.gionee.client.activity.webViewPage.BaseWebViewActivity;

/**
 * com.gionee.client.business.manage.WebViewListManager
 * 
 * @author yuwei <br/>
 *         create at 2015-5-21 下午3:56:20
 * @description webActivity队列管理
 */
public class WebViewListManager {
	private static WebViewListManager sInstance;
	private LinkedList<WeakReference<BaseWebViewActivity>> mWebViewList;
	private int mMaxActivitySize = 1;

	private WebViewListManager() {
		mWebViewList = new LinkedList<WeakReference<BaseWebViewActivity>>();
	}

	public static WebViewListManager getInstance() {
		if (sInstance == null) {
			sInstance = new WebViewListManager();
		}
		return sInstance;
	}

	public void goFoward(BaseWebViewActivity activity) {
		try {
			mWebViewList.addLast(new WeakReference<BaseWebViewActivity>(
					activity));
			ActivityManager am = (ActivityManager) activity
					.getSystemService(Context.ACTIVITY_SERVICE);
			MemoryInfo mi = new MemoryInfo();
			am.getMemoryInfo(mi);
			// Toast.makeText(activity.getSelfContext(), mi.availMem+"",
			// Toast.LENGTH_LONG).show();
			if (mi.availMem < 50000000) {
				mMaxActivitySize = 1;
			} else {
				mMaxActivitySize = 4;
			}
			if (mWebViewList.size() > mMaxActivitySize) {
				WeakReference<BaseWebViewActivity> reference = mWebViewList
						.removeFirst();
				if (reference == null) {
					return;
				}
				BaseWebViewActivity webViewActivity = reference.get();
				if (webViewActivity != null) {
					webViewActivity.finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeFirst() {
		try {
			WeakReference<BaseWebViewActivity> reference = mWebViewList
					.removeFirst();
			if (reference == null) {
				return;
			}
			BaseWebViewActivity webViewActivity = reference.get();
			if (webViewActivity != null) {
				webViewActivity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void goBack(WeakReference<BaseWebViewActivity> activity) {
		try {
			mWebViewList.remove(activity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
