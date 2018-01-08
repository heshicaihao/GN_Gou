// Gionee <yuwei><2014-8-14> add for CR00821559 begin
/*
 * GNInjector.java
 * classes : com.gionee.client.business.inject.GNInjector
 * @author yuwei
 * V 1.0.0
 * Create at 2014-8-14 上午10:53:09
 */
package com.gionee.client.business.inject;

import java.lang.reflect.Field;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * com.gionee.client.business.inject.GNInjector
 * 
 * @author yuwei <br/>
 * @date create at 2014-8-14 上午10:53:09
 * @description TODO
 */

public class GNInjector {
    private static GNInjector sInstance;

    private GNInjector() {

    }

    public static GNInjector getInstance() {
        if (sInstance == null) {
            sInstance = new GNInjector();
        }
        return sInstance;
    }

    public void inJectActivityView(Activity activity) {
        // TODO Auto-generated method stub
        Field[] fields = activity.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(GNInjectView.class)) {
                    injectView(activity, field);
                }
            }
        }
    }

    public void injectFragmentView(Fragment fragment) {
        Field[] fields = fragment.getClass().getDeclaredFields();

        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(GNInjectView.class)) {
                    injectView(fragment, field);
                }
            }
        }
    }

    public void injectViewHolder(Object viewHolder, View view) {
        Field[] fields = viewHolder.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(GNInjectView.class)) {
                    injectView(viewHolder, view, field);
                }
            }
        }
    }

    private void injectView(Object viewHolder, View view, Field field) {
        if (field.isAnnotationPresent(GNInjectView.class)) {
            GNInjectView viewInject = field.getAnnotation(GNInjectView.class);
            int viewId = viewInject.id();
            try {
                field.setAccessible(true);
                field.set(viewHolder, view.findViewById(viewId));
//                View childView = (View) field.get(viewHolder);
//                childView.setOnClickListener((OnClickListener) viewHolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void injectView(Fragment fragment, Field field) {
        if (field.isAnnotationPresent(GNInjectView.class)) {
            GNInjectView viewInject = field.getAnnotation(GNInjectView.class);
            int viewId = viewInject.id();
            try {
                field.setAccessible(true);
                field.set(fragment, fragment.getView().findViewById(viewId));
                View view = (View) field.get(fragment);
                view.setOnClickListener((OnClickListener) fragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void injectView(Activity activity, Field field) {
        // TODO Auto-generated method stub
        if (field.isAnnotationPresent(GNInjectView.class)) {
            GNInjectView viewInject = field.getAnnotation(GNInjectView.class);
            int viewId = viewInject.id();
            try {
                field.setAccessible(true);
                field.set(activity, activity.findViewById(viewId));
                View view = (View) field.get(activity);
                view.setOnClickListener((OnClickListener) activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
