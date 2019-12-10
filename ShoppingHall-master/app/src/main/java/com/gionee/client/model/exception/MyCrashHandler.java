// Gionee <yuwei><2014-1-8> add for CR00821559 begin
/*
 * MyCrashHandler.java
 * classes : com.gionee.client.model.exception.MyCrashHandler
 * @author yuwei
 * V 1.0.0
 * Create at 2014-1-8 下午1:55:56
 */
package com.gionee.client.model.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.gionee.client.business.manage.GNActivityManager;
import com.gionee.client.business.util.LogUtils;

/**
 * MyCrashHandler
 * 
 * @author yuwei <br/>
 * @date create at 2014-1-8 下午1:55:56
 * @description TODO 自定义的 异常处理类 , 实现了 UncaughtExceptionHandler接口
 */

public class MyCrashHandler implements UncaughtExceptionHandler {
    // 需求是 整个应用程序 只有一个 MyCrash-Handler
    private static MyCrashHandler myCrashHandler;
    private Context context;

    // 1.私有化构造方法
    private MyCrashHandler() {

    }

    public static synchronized MyCrashHandler getInstance() {
        if (myCrashHandler != null) {
            return myCrashHandler;
        } else {
            myCrashHandler = new MyCrashHandler();
            return myCrashHandler;
        }
    }

    public void init(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        // 干掉当前的程序
        try {
            LogUtils.log("UNCAUGHT_EXCEPTION", getErrorInfo(arg1));
            GNActivityManager.getScreenManager().popAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取错误的信息
     * 
     * @param arg1
     * @return
     */
    private String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error = writer.toString();
        return error;
    }

    /**
     * 获取手机的硬件信息
     * 
     * @return
     */
    private String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        // 通过反射获取系统的硬件信息
        try {

            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                // 暴力反射 ,获取私有的信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取手机的版本信息
     * 
     * @return
     */
    private String getVersionInfo() {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

}