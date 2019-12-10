//Gionee <wangyy><2014-01-23> modify for CR01029173 begin
package com.gionee.client.business.statistic.job;

public abstract class Job implements Runnable {
    protected boolean mCanceled = false;

    public void run() {
        try {
            runTask();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseResource();
//            YouJuThreadPoolManager.getInstance().removeCompleteTask(this);
        }
    }

    protected abstract void runTask();

    protected abstract void releaseResource();

    public void cancel() {
        mCanceled = true;
        cancelTask();
    }

    protected void cancelTask() {
    }
}
//Gionee <wangyy><2014-01-23> modify for CR01029173 end
