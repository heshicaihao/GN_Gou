// Gionee <yuwei><2013-7-29> add for CR00821559 begin
/*
* TabViewPager.java
 * classes : com.gionee.client.widget.TabViewPager
 * @author yuwei
 * V 1.0.0
 * Create at 2013-4-9 下午1:58:22
 */
package com.gionee.client.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * com.gionee.client.widget.TabViewPager
 * 
 * @author yuwei <br/>
 *         create at 2013-4-9 下午1:58:22 TODO
 */
public class TabViewPager extends ViewPager {
    private boolean mEnabled;

    /**
     * @param context
     */
    public TabViewPager(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */

    public TabViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mEnabled = true;
    }

//触摸没有反应就可以了
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.mEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public boolean getPagingEnabled() {
        return this.mEnabled;
    }

    /*    @Override
        public boolean onInterceptTouchEvent(MotionEvent arg0) {
            try {
                return super.onInterceptTouchEvent(arg0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }*/

    /*    @Override
        public boolean onTouchEvent(MotionEvent arg0) {
            try {
                return super.onTouchEvent(arg0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }*/

}
//Gionee <yuwei><2013-7-29> add for CR00821559 end