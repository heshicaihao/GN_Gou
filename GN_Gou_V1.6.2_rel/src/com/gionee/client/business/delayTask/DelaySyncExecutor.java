package com.gionee.client.business.delayTask;

import android.os.Handler;

public abstract class DelaySyncExecutor {

    public static final int DELAY_MILLIS = 1000;
    private Handler mHandler;
    private boolean mIsInDelay;

    public DelaySyncExecutor() {
        mHandler = new Handler();
    }

    public void setDelayed(int delayMillis) {
        cancelTask();

        mIsInDelay = true;

        mHandler.postDelayed(mDelayedTask, delayMillis);
    }

    private Runnable mDelayedTask = new Runnable() {

        @Override
        public void run() {
            mIsInDelay = false;

            onExecute();
        }
    };

    public final void cancelTask() {
        mIsInDelay = false;

        mHandler.removeCallbacks(mDelayedTask);
    }

    public final void cancelDelay() {
        if (mIsInDelay) {
            return;
        }

        mIsInDelay = false;

        onExecute();
    }

    public boolean isInDelay() {
        return mIsInDelay;
    }

    protected abstract void onExecute();
}
