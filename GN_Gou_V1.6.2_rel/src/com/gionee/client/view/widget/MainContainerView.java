package com.gionee.client.view.widget;

import com.baidu.mobstat.StatService;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.attention.AddAttentionActivity;
import com.gionee.client.activity.history.GnBrowseHistoryActivity;
import com.gionee.client.activity.myfavorites.MyFavoritesActivity;
import com.gionee.client.activity.question.GNFAQsActivity;
import com.gionee.client.activity.webViewPage.BaseWebViewActivity;
import com.gionee.client.business.util.AndroidUtils;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class MainContainerView extends RelativeLayout {

	private Context mContext;
	private float mStartX;
	private float mStartY;
	private long mStartTime;

	public MainContainerView(Context context) {
		super(context);
		this.mContext = context;
		// TODO Auto-generated constructor stub
	}

	public MainContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		// TODO Auto-generated constructor stub
	}

	public MainContainerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mStartX = ev.getX();
			mStartY = ev.getY();
			mStartTime = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_MOVE:

			break;
		case MotionEvent.ACTION_UP:
			long endTime = System.currentTimeMillis();
			// 右滑
			if (endTime - mStartTime < 200
					&& ev.getX() - mStartX > 100
					&& Math.abs(ev.getY() - mStartY) < 40
					&& !(mContext instanceof GnHomeActivity)
					&& !(mContext instanceof MyFavoritesActivity)
					&& !(mContext instanceof BaseWebViewActivity)
					&& !(mContext instanceof GNFAQsActivity)
					&& !(mContext instanceof GnBrowseHistoryActivity)
					&& !(mContext instanceof AddAttentionActivity && ((AddAttentionActivity) mContext).mTag == 0)) {
				boolean isShow = AndroidUtils
						.hideInputSoftware((Activity) mContext);
				if (!isShow) {
					((Activity) mContext).onBackPressed();
					StatService.onEvent(mContext, "gesture_back",
							"gesture_back");
					AndroidUtils.exitActvityAnim((Activity) mContext);
					return true;
				}
			}
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
}
