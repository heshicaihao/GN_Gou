// Gionee <heshicaihao><2015-11-07> add for CR01578324 begin
/*
 * WelcomePageIndicatorView.java
 * classes : com.gionee.client.view.widget.WelcomePageIndicatorView
 * @author heshicaihao
 * V 2.7.0
 * Create at 2015-11-07 上午12:20
 */
package com.gionee.client.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.gionee.client.R;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;

public class WelcomePageIndicatorView extends View {
	private int mCurrentPage = -1;
	private int mTotalPage = 0;
	private int DOT_DEFAULT_WIDTH = 7;
	private int mDotSelectedResId;
	private int mDotNormalResId;
	private int mDotWidth;

	public void setmDotWidth(int mDotWidth) {
		this.mDotWidth = mDotWidth;
	}

	public WelcomePageIndicatorView(Context context) {
		super(context);
	}

	public int getmDotSelectedResId() {
		return mDotSelectedResId;
	}

	public void setmDotSelectedResId(int mDotSelectedResId) {
		this.mDotSelectedResId = mDotSelectedResId;
	}

	public int getmDotNormalResId() {
		return mDotNormalResId;
	}

	public void setmDotNormalResId(int mDotNormalResId) {
		this.mDotNormalResId = mDotNormalResId;
	}

	public WelcomePageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setTotalPage(int nPageNum) {
		mTotalPage = nPageNum;
		if (mCurrentPage >= mTotalPage)
			mCurrentPage = mTotalPage - 1;
	}

	public int getTotalPage() {
		return mTotalPage;
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setCurrentPage(int nPageIndex) {
		if (nPageIndex < 0 || nPageIndex >= mTotalPage)
			return;

		if (mCurrentPage != nPageIndex) {
			mCurrentPage = nPageIndex;
			this.invalidate();
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		LogUtils.log("PageIndicatorView", LogUtils.getThreadName());
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		paint.setStyle(Paint.Style.FILL);
		if (mDotWidth == 0) {
			mDotWidth = DOT_DEFAULT_WIDTH;
		}
		int iconWidth = AndroidUtils.dip2px(getContext(), mDotWidth);
		int iconHeight = AndroidUtils.dip2px(getContext(), mDotWidth);
		final int DOT_SPACING = 10;
		int space = AndroidUtils.dip2px(getContext(), DOT_SPACING);

		int x = (getWidth() - (iconWidth * mTotalPage + space * (mTotalPage - 1))) / 2;
		int y = AndroidUtils.dip2px(getContext(), 15);
		if (mDotNormalResId == 0) {
			mDotNormalResId = R.drawable.dot_welcome_sel;
		}
		if (mDotSelectedResId == 0) {
			mDotSelectedResId = R.drawable.dot_welcome_nor;
		}
		for (int i = 0; i < mTotalPage; i++) {

			int resId;

			if (i == mCurrentPage) {
				resId = mDotSelectedResId;
			} else {
				resId = mDotNormalResId;
			}

			Rect r1 = new Rect();
			r1.left = x;
			r1.top = y;
			r1.right = x + iconWidth;
			r1.bottom = y + iconHeight;

			try {
				Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId);
				canvas.drawBitmap(bmp, null, r1, paint);

				x += iconWidth + space;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public void DrawImage(Canvas canvas, Bitmap mBitmap, int x, int y, int w, int h, int bx, int by) {
		Rect src = new Rect();// 图片裁剪区域
		Rect dst = new Rect();// 屏幕裁剪区域
		src.left = bx;
		src.top = by;
		src.right = bx + w;
		src.bottom = by + h;

		dst.left = x;
		dst.top = y;
		dst.right = x + w;
		dst.bottom = y + h;

		src = null;
		dst = null;
	}

}
