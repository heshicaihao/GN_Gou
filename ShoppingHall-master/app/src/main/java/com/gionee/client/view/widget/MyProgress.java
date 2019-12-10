package com.gionee.client.view.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class MyProgress extends View {

	private int mEndX;
	private int mScreenWidth;
	private float mScale;
	private Paint mPaint;
	private String[] mColors = new String[] { "#FFD002", "#FFCE03", "#FFCB04",
			"#FFC905", "#FFC706", "#FFC507", "#FFC209", "#FFBF0A", "#FFBD0B",
			"#FFBA0C", "#FFB80D", "#FFB50E", "#FFB30F", "#FFB110", "#FFAE12",
			"#FFAB13", "#FFA914", "#FFA715", "#FFA416", "#FFA217", "#FF9F18",
			"#FF9C1A", "#FF9A1B", "#FF981C", "#FF951D", "#FF931E", "#FF901F",
			"#FF8E20", "#FF8B21", "#FF8923", "#FF8624", "#FF8425", "#FF8226",
			"#FF7F27", "#FF7D28", "#FF7A29", "#FF782A", "#FF752B", "#FF732D",
			"#FF6A31" };

	public MyProgress(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public MyProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public MyProgress(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
		// TODO Auto-generated constructor stub
	}

	private void init(Context context) {
		// TODO Auto-generated method stub
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScale = context.getResources().getDisplayMetrics().density;
		mPaint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//
		super.onDraw(canvas);
		for (int i = 0; i < mColors.length; i++) {
			mPaint.setColor(Color.parseColor(mColors[i]));
			float left = (float)(i * mEndX / mColors.length);
			float top = 0f;
			float right = (float)((i + 1) * mEndX / mColors.length);
			float buttom = mScale * 4 / 1.5f;
			canvas.drawRect(left, top, right, buttom, mPaint);
		}
		if (mEndX != 0) {
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.parseColor(mColors[39]));
			canvas.drawCircle((float)mEndX, mScale * 2f / 1.5f, mScale * 1.5f / 1.5f,
					mPaint);
		}
	}

	public void setProgress(int progress) {
		mEndX = progress * mScreenWidth / 100;
		invalidate();
	}
}
