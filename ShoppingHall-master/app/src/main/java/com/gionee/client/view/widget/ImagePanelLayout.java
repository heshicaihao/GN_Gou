// Gionee <yangxiong><2013-11-19> add for CR00850885 begin
/*
 * @author yangxiong
 * Create at 2013-11-19 下午02:52:00
 */
package com.gionee.client.view.widget;

/**
 * @author yangxiong <br/>
 * @date create at 2013-11-19 下午02:52:00
 * @description TODO 自定义自动换行LinearLayout 
 */

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ImagePanelLayout extends ViewGroup {
    private static final String TAG = "ImagePanelLayout";

    private int mCellWidth;
    private int mCellHeight;
    private int mTextviewWidth;

    public ImagePanelLayout(Context context) {
        super(context);
    }

    public ImagePanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImagePanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setmCellWidth(int w) {
        mCellWidth = w;
        requestLayout();
    }

    public void setmCellHeight(int h) {
        mCellHeight = h;
        requestLayout();
    }

    public void setTextviewWidth(int w) {
        mTextviewWidth = w;
    }

    /**
     * 控制子控件的换行
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cellWidth = mCellWidth;
        int cellHeight = mCellHeight;
        int columns = (r - l) / cellWidth;
        if (columns < 0) {
            columns = 1;
        }
        int x = 0;
        int y = 0;
        int i = 0;
        int count = getChildCount();
        for (int j = 0; j < count; j++) {
            final View childView = getChildAt(j);
            // 获取子控件Child的宽高
            int w = childView.getMeasuredWidth();
            int h = childView.getMeasuredHeight();
            // 计算子控件的顶点坐标
            int left = x + ((cellWidth - w) / 2);
            int top = y + ((cellHeight - h) / 2);
            // int left = x;
            // int top = y;
            // 布局子控件
            childView.layout(left, top, left + w, top + h);

            if (i >= (columns - 1)) {
                i = 0;
                x = 0;
                y += cellHeight;
            } else {
                i++;
                x += cellWidth;

            }
        }
    }

    /**
     * 计算控件及子控件所占区域
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 创建测量参数
        int cellWidthSpec = MeasureSpec.makeMeasureSpec(mCellWidth, MeasureSpec.AT_MOST);
        int cellHeightSpec = MeasureSpec.makeMeasureSpec(mCellHeight, MeasureSpec.AT_MOST);
        // 记录ViewGroup中Child的总个数
        int count = getChildCount();
        // 设置子空间Child的宽高
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            /* 
             * 090 This is called to find out how big a view should be. 091 The 
             * parent supplies constraint information in the width and height 
             * parameters. 092 The actual mesurement work of a view is performed 
             * in onMeasure(int, int), 093 called by this method. 094 Therefore, 
             * only onMeasure(int, int) can and must be overriden by subclasses. 
             * 095 
             */

            childView.measure(cellWidthSpec, cellHeightSpec);
        }

        int layoutWidth = MeasureSpec.getSize(widthMeasureSpec);

        // 容器控件需要多少行来显示自控件
        // Textview"添加照片"出现在第一行显示时，计算第一行最少显示的照片数量
        int imageCount = (layoutWidth - mTextviewWidth) / mCellWidth;
        int leftImageCount = count - imageCount;
        int linesCount = 0; // 最终的需显示的行数
        if (leftImageCount > 0) { // 当需要分多行显示时，计算行数
            int countInAline = layoutWidth / mCellWidth;
            int quotient = leftImageCount / countInAline; // 商数
            int remainder = leftImageCount % countInAline; // 余数
            linesCount = quotient + (remainder > 0 ? 1 : 0);
        }
        if (count > 0) {
            linesCount++;
        }

        // 设置容器控件所占区域大小
        // 注意setMeasuredDimension和resolveSize的用法
        setMeasuredDimension(resolveSize(mTextviewWidth - mCellWidth + mCellWidth * count, widthMeasureSpec),
                resolveSize(mCellHeight * linesCount, heightMeasureSpec));
        // setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

        // 不需要调用父类的方法
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 为控件添加边框
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
//        // 获取布局控件宽高
//        int width = getWidth();
//        int height = getHeight();
//        // 创建画笔
//        Paint mPaint = new Paint();
//        // 设置画笔的各个属性
//        mPaint.setColor(Color.BLUE);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(10);
//        mPaint.setAntiAlias(true);
//        // 创建矩形框
//        Rect mRect = new Rect(0, 0, width, height);
//        // 绘制边框
//        canvas.drawRect(mRect, mPaint);
//        // 最后必须调用父类的方法
        super.dispatchDraw(canvas);
    }

}
//Gionee <yangxiong><2013-11-19> add for CR00850885 end