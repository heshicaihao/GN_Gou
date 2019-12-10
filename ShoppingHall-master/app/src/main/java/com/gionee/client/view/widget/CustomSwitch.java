package com.gionee.client.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.gionee.client.R;

public class CustomSwitch extends View {

    private static final int FULL_DURATION = 150;

    private Drawable mTrackOnDrawable;
    private Drawable mTrackOffDrawable;
    private Drawable mThumbDrawable;
    private float mThumbOffsetX = 0;
    private int mWidth;
    private int mHeight;
    private int mThumbWidth;
    private int mThumbHeight;
    private boolean mCheckOn = false;
    private boolean mIsInThumb = false;
    private boolean mIsMove = false;
    private GameSwitchListener mListener;
    private GestureDetector mGestureDetetor;
    private AnimationComputer mAnimComputer;

    public CustomSwitch(Context context) {
        super(context);
        init();
    }

    public CustomSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mTrackOnDrawable = getResources().getDrawable(R.drawable.check_bg);
        mTrackOffDrawable = getResources().getDrawable(R.drawable.uncheck);
        mThumbDrawable = getResources().getDrawable(R.drawable.check_switch);
        mWidth = mTrackOnDrawable.getIntrinsicWidth();
        mHeight = mTrackOnDrawable.getIntrinsicHeight();
        mThumbWidth = mThumbDrawable.getIntrinsicWidth();
        mThumbHeight = mThumbDrawable.getIntrinsicHeight();

        mAnimComputer = new AnimationComputer(new DecelerateInterpolator());
        mGestureDetetor = new GestureDetector(getContext(), new OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                float nowX = e.getX();
                float nowY = e.getY();
                if (isInThumb(nowX, nowY)) {
                    mIsInThumb = true;
                    onThumb();
                }
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mIsInThumb) {
                    mThumbOffsetX -= distanceX;
                    keepInRange();
                    invalidate();
                }
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                moveOtherEnd();
                return false;
            }

        });
    }

    private boolean isInThumb(float x, float y) {
        if (x >= mThumbOffsetX && x <= mThumbOffsetX + mThumbWidth && y >= 0 && y <= 0 + mThumbHeight) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (!mGestureDetetor.onTouchEvent(event)) {
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                startMove();
                if (mIsInThumb) {
                    mIsInThumb = false;
                    onThumb();
                }
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mTrackOffDrawable.setBounds(0, 0, mWidth, mHeight);
        mTrackOffDrawable.draw(canvas);
        mTrackOnDrawable.setBounds(0, 0, mWidth, mHeight);
        mTrackOnDrawable.setAlpha(calculateAlpha(mThumbOffsetX, mWidth - mThumbWidth));
        mTrackOnDrawable.draw(canvas);

        mThumbDrawable.setBounds((int) mThumbOffsetX, 0, (int) mThumbOffsetX + mThumbWidth, mThumbHeight);
        mThumbDrawable.draw(canvas);
    }

    private void startMove() {
        if (!mIsMove) {
            mIsMove = true;
            keepInRange();
            if (mThumbOffsetX >= (mWidth - mThumbWidth) / (float) 2) {
                startRebound(false);
            } else {
                startRebound(true);
            }
        }
    }

    private void moveOtherEnd() {
        if (!mIsMove) {
            mIsMove = true;
            keepInRange();
            if (mCheckOn) {
                mAnimComputer.start(mWidth - mThumbWidth, 0, FULL_DURATION);
            } else {
                mAnimComputer.start(0, mWidth - mThumbWidth, FULL_DURATION);
            }
            onSwitch();
        }
    }

    private void startRebound(boolean toLeft) {
        int duration;
        float endPosition;
        if (toLeft) {
            endPosition = 0;
            duration = (int) (mThumbOffsetX / (mWidth - mThumbWidth) * FULL_DURATION);
        } else {
            endPosition = (mWidth - mThumbWidth);
            duration = (int) (((mWidth - mThumbWidth) - mThumbOffsetX) / (mWidth - mThumbWidth) * FULL_DURATION);
        }
        mAnimComputer.start(mThumbOffsetX, endPosition, duration);
        onSwitch();
    }

    private void onSwitch() {
        if (mAnimComputer.isFinish()) {
            setCheck(mThumbOffsetX >= (mWidth - mThumbWidth) / (float) 2);
            mIsMove = false;
        } else {
            int xDelta = (int) mAnimComputer.getDeltaPosition();
            mThumbOffsetX += xDelta;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                    onSwitch();
                }
            }, 100);
        }
    }

    private int calculateAlpha(float curLength, float allLength) {
        int alpha = (int) ((255 * curLength) / allLength);
        if (alpha <= 0) {
            alpha = 0;
        } else if (alpha >= 255) {
            alpha = 255;
        }
        return alpha;
    }

    private void keepInRange() {
        if (mThumbOffsetX <= 0) {
            mThumbOffsetX = 0;
        } else if (mThumbOffsetX >= (mWidth - mThumbWidth)) {
            mThumbOffsetX = mWidth - mThumbWidth;
        }
    }

    private void setCheck(boolean check) {
        if (mCheckOn != check) {
            mCheckOn = check;
            onChange();
        }
    }

    private void setOffset() {
        if (mCheckOn) {
            mThumbOffsetX = mWidth - mThumbWidth;
        } else {
            mThumbOffsetX = 0;
        }
    }

    private void onChange() {
        if (mListener != null) {
            mListener.onChange(mCheckOn);
        }
    }

    private void onThumb() {
        if (mListener != null) {
            mListener.onThumb(mIsInThumb);
        }
    }

    public void setCheckedStatus(boolean check) {
        if (mCheckOn != check) {
            setCheck(check);
            setOffset();
            invalidate();
        }
    }

    public void setGameSwitchListener(GameSwitchListener listener) {
        mListener = listener;
    }

    public interface GameSwitchListener {
        public void onChange(boolean check);

        public void onThumb(boolean isInThumb);
    }

}
