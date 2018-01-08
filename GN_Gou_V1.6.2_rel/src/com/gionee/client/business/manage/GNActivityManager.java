// Gionee <yuwei><2013-7-29> add for CR00821559 begin
/*
 * ActivityManager.java
 * classes : com.gionee.client.ActivityManager
 * @author yuwei
 * V 1.0.0
 * Create at 2013-7-29 上午10:24:53
 */
package com.gionee.client.business.manage;

import java.util.Iterator;
import java.util.Stack;

import com.gionee.client.activity.GnHomeActivity;

import android.app.Activity;

/**
 * com.gionee.client.ActivityManager
 * 
 * @author yuwei <br/>
 * @data create at 2013-7-29 上午10:52:40
 * @desciption manage all activity of this application,you can add,remove a activity to the single activity
 *             stack,and remove all activity when you exit this application
 */
public class GNActivityManager {
    private static Stack<Activity> sActivityStack = new Stack<Activity>();

    private GNActivityManager() {
//        sActivityStack = new Stack<Activity>();
    }

    private static class SingletonFactory {
        private static GNActivityManager sInstance = new GNActivityManager();
    }

    /**
     * @return the single instance of ActivityManager
     * @description to call this method you can get the single instance of the ActivityManager
     * @author yuwei
     */
    public static GNActivityManager getScreenManager() {
        return SingletonFactory.sInstance;
    }

    /**
     * @param activity
     * @description finish the activty in the stack top
     * @author yuwei
     */
    private void finishActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            activity.finish();
            sActivityStack.remove(activity);
            activity = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param activity
     * @description remove the activity from the activity stack,you may call it on the OnDestroy methed
     * @author yuwei
     */
    public void popActivity(Activity activity) {
        if (activity == null) {
            return;
        }    
        if (sActivityStack.contains(activity)) {
            sActivityStack.remove(activity);
        }
    }

    /**
     * @return
     * @description get current activity in the top of the stack
     * @author yuwei
     */
    public Activity currentActivity() {
        Activity activity = sActivityStack.lastElement();
        return activity;
    }

    /**
     * @param activity
     * @description add activity to the top of the stack
     * @author yuwei
     */
    public void pushActivity(Activity activity) {
        if (sActivityStack == null) {
            sActivityStack = new Stack<Activity>();
        }
        sActivityStack.add(activity);
    }

    /**
     * 
     * @description finish all activity in the stack,you can call it when you want to exit the application
     * @author yuwei
     */
    public void popAllActivity() {
        while (!sActivityStack.isEmpty()) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            finishActivity(activity);
        }
    }

    public boolean isGnHomeActivityRun() {
        boolean isRun = false;
        for (Iterator iterator = sActivityStack.iterator(); iterator.hasNext();) {
            Activity activity = (Activity) iterator.next();
            if (activity.getClass().getName().equals(GnHomeActivity.class.getName())) {
                isRun = true;
            }
        }
        return isRun;
    }
}
//Gionee <yuwei><2013-7-29> add for CR00821559 end