/*
 * TouchView.java
 * classes : com.gionee.client.view.widget.TouchView
 * @author yuwei
 * 
 * Create at 2015-4-2 下午7:58:50
 */
package com.gionee.client.view.widget;

import android.content.Context;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;

/**
 * com.gionee.client.view.widget.TouchView
 * 
 * @author yuwei <br/>
 *         create at 2015-4-2 下午7:58:50
 * @description 可回弹缩放的ImageView
 */
public class TouchView extends ImageView

{
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int BIGGER = 3;
    private static final int SMALLER = 4;
    private int mode = NONE;

    private float mBeforeLengh;
    private float mAfterLengh;
    private float mScaleFactor = 0.01f;
    private int mAbsoluteX;
    private int mAbsoluteY;
    private int mOppositeX;
    private int mOppositeY;
    private TranslateAnimation mBackAnimation;
    private int mBorderLeft;
    private int mBorderRight;
    private int mBorderTop;
    private int mBorderBottom;

    public TouchView(Context context) {
        super(context);
        this.setPadding(0, 0, 0, 0);

    }

    /**
     * 用来计算2个触摸点的距离
     * 
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int circleWidth = mBorderRight - mBorderLeft;
        int circleHeight = mBorderBottom - mBorderTop;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                mOppositeX = (int) event.getRawX();
                mOppositeY = (int) event.getRawY();
                mAbsoluteX = mOppositeX - this.getLeft();
                mAbsoluteY = mOppositeY - this.getTop();

                if (event.getPointerCount() == 2)
                    mBeforeLengh = spacing(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > 10f) {
                    mode = ZOOM;
                    mBeforeLengh = spacing(event);
                }
                break;
            case MotionEvent.ACTION_UP:

                int disY = computeDisY();
                int disX = computeDisX();
                computeScare(circleWidth, circleHeight);
                startReturnAnim(disY, disX);
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    startMoveAction(event);

                } else if (mode == ZOOM) {
                    if (spacing(event) > 10f) {
                        mAfterLengh = spacing(event);
                        float gapLenght = mAfterLengh - mBeforeLengh;
                        if (gapLenght == 0) {
                            break;
                        }
                        startScareAction(circleWidth, gapLenght);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void startScareAction(int circleWidth, float gapLenght) {
        scareSmall(circleWidth, gapLenght);
        scareBigger(gapLenght);
    }

    public void scareBigger(float gapLenght) {
        if (getWidth() > AndroidUtils.dip2px(getContext(), 150) && getWidth() < 3000 && gapLenght > 0) {
            this.setScale(mScaleFactor, BIGGER);
            mBeforeLengh = mAfterLengh;
        }
    }

    public void scareSmall(int circleWidth, float gapLenght) {
        if (getWidth() > circleWidth && gapLenght < 0) {
            this.setScale(mScaleFactor, SMALLER);
            mBeforeLengh = mAfterLengh;
        }
    }

    public void startMoveAction(MotionEvent event) {
        int left = mOppositeX - mAbsoluteX;
        int top = mOppositeY - mAbsoluteY;
        int right = mOppositeX + this.getWidth() - mAbsoluteX;
        int bottom = mOppositeY - mAbsoluteY + this.getHeight();
        this.setPosition(mOppositeX - mAbsoluteX, mOppositeY - mAbsoluteY, mOppositeX + this.getWidth()
                - mAbsoluteX, mOppositeY - mAbsoluteY + this.getHeight());
        LogUtils.log("clip_picture", "left=" + left + "top=" + top + "right=" + right + "bottom=" + bottom);
        mOppositeX = (int) event.getRawX();
        mOppositeY = (int) event.getRawY();
    }

    public void startReturnAnim(int disY, int disX) {
        if (disX != 0 || disY != 0) {
            mBackAnimation = new TranslateAnimation(disX, 0, disY, 0);
            mBackAnimation.setDuration(500);
            this.startAnimation(mBackAnimation);
        }
    }

    public void computeScare(int circleWidth, int circleHeight) {
        while (getHeight() < circleHeight || getWidth() < circleWidth) {

            setScale(mScaleFactor, BIGGER);
        }
    }

    public int computeDisX() {
        int disX = 0;
        if (getLeft() > mBorderLeft) {
            disX = getLeft() - mBorderLeft;
            this.layout(mBorderLeft, this.getTop(), mBorderLeft + this.getWidth(), this.getBottom());
        }
        if (getRight() < mBorderRight) {
            disX = this.getRight() - mBorderRight;
            this.layout(mBorderRight - this.getWidth(), this.getTop(), mBorderRight, this.getBottom());
        }
        return disX;
    }

    public int computeDisY() {
        int disY = 0;
        if (getTop() > mBorderTop)//
        {
            disY = getTop() - mBorderTop;
            this.layout(this.getLeft(), mBorderTop, this.getRight(), mBorderTop + this.getHeight());
        }
        if (getBottom() < mBorderBottom) {
            disY = getBottom() - mBorderBottom;
            this.layout(this.getLeft(), mBorderBottom - this.getHeight(), this.getRight(), mBorderBottom);
        }
        return disY;
    }

    private void setScale(float temp, int flag) {

        if (flag == BIGGER) {
            this.setFrame(this.getLeft() - (int) (temp * this.getWidth()),
                    this.getTop() - (int) (temp * this.getHeight()),
                    this.getRight() + (int) (temp * this.getWidth()),
                    this.getBottom() + (int) (temp * this.getHeight()));
        } else if (flag == SMALLER) {
            this.setFrame(this.getLeft() + (int) (temp * this.getWidth()),
                    this.getTop() + (int) (temp * this.getHeight()),
                    this.getRight() - (int) (temp * this.getWidth()),
                    this.getBottom() - (int) (temp * this.getHeight()));
        }
    }

    private void setPosition(int left, int top, int right, int bottom) {
        this.layout(left, top, right, bottom);
    }

    public int getmBorderLeft() {
        return mBorderLeft;
    }

    public void setmBorderLeft(int mBorderLeft) {
        this.mBorderLeft = mBorderLeft;
    }

    public int getmBorderRight() {
        return mBorderRight;
    }

    public void setmBorderRight(int mBorderRight) {
        this.mBorderRight = mBorderRight;
    }

    public int getmBorderTop() {
        return mBorderTop;
    }

    public void setmBorderTop(int mBorderTop) {
        this.mBorderTop = mBorderTop;
    }

    public int getmBorderBottom() {
        return mBorderBottom;
    }

    public void setmBorderBottom(int mBorderBottom) {
        this.mBorderBottom = mBorderBottom;
    }

}
