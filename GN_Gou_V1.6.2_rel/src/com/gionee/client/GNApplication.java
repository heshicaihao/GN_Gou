package com.gionee.client;

import android.app.Application;

import com.baidu.frontia.FrontiaApplication;
import com.gionee.client.model.exception.MyCrashHandler;
import com.upgrate.download.FileService;

public class GNApplication extends Application {
    private MyCrashHandler mCrashHandler;

    public GNApplication() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        mCrashHandler = MyCrashHandler.getInstance();
        mCrashHandler.init(this);
        FileService.initDB(getApplicationContext());
        FrontiaApplication.initFrontiaApplication(getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(mCrashHandler);
//        ConfigKey.setUseAnologData(true);
    }

}