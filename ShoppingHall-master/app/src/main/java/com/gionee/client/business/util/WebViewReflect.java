/*
 * PhoneGap is available under *either* the terms of the modified BSD license *or* the
 * MIT License (2008). See http://opensource.org/licenses/alphabetical for full text.
 * 
 * Copyright (c) 2005-2010, Nitobi Software Inc.
 * Copyright (c) 2010, IBM Corporation
 */
package com.gionee.client.business.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.webkit.WebSettings;

public class WebViewReflect {
    private static final String TAG = "WebViewReflect";
    private static Method sWebSettingsSetDatabaseEnabled;
    private static Method sWebSettingsSetDatabasePath;
    private static Method sWebSettingsSetDomStorageEnabled;
    private static Method sWebSettingsSetGeolocationEnabled;
    private static Method sWebSettingsSetDisplayZoomControls;

    static {
        checkCompatibility();
    }

    @SuppressWarnings("unused")
    private static void setDatabaseEnabled(boolean e) throws IOException {
        try {
            sWebSettingsSetDatabaseEnabled.invoke(e);
        } catch (InvocationTargetException ite) {
            /* unpack original exception when possible */
            Throwable cause = ite.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                /* unexpected checked exception; wrap and re-throw */
                throw new RuntimeException(ite);
            }
        } catch (IllegalAccessException ie) {
            System.err.println("unexpected " + ie);
        }
    }

    public static void checkCompatibility() {
        try {
            sWebSettingsSetDatabaseEnabled = WebSettings.class.getMethod("setDatabaseEnabled",
                    new Class[] {boolean.class});
            sWebSettingsSetDatabasePath = WebSettings.class.getMethod("setDatabasePath",
                    new Class[] {String.class});
            sWebSettingsSetDomStorageEnabled = WebSettings.class.getMethod("setDomStorageEnabled",
                    new Class[] {boolean.class});
            sWebSettingsSetGeolocationEnabled = WebSettings.class.getMethod("setGeolocationEnabled",
                    new Class[] {boolean.class});
            sWebSettingsSetDisplayZoomControls = WebSettings.class.getMethod("setDisplayZoomControls",
                    new Class[] {boolean.class});
            /* success, this is a newer device */
        } catch (NoSuchMethodException nsme) {
            /* failure, must be older device */
        }
    }

    public static void setStorage(WebSettings setting, boolean enable, String path) {
        if (sWebSettingsSetDatabaseEnabled != null) {
            /* feature is supported */
            try {
                sWebSettingsSetDatabaseEnabled.invoke(setting, enable);
                sWebSettingsSetDatabasePath.invoke(setting, path);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            /* feature not supported, do something else */
        }
    }

    public static void setGeolocationEnabled(WebSettings setting, boolean enable) {
        if (sWebSettingsSetGeolocationEnabled != null) {
            /* feature is supported */
            try {
                sWebSettingsSetGeolocationEnabled.invoke(setting, enable);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            /* feature not supported, do something else */
            System.out.println("Native Geolocation not supported - we're ok");
        }
    }

    public static void setDomStorage(WebSettings setting) {
        if (sWebSettingsSetDomStorageEnabled != null) {
            /* feature is supported */
            try {
                LogUtils.log("WebViewReflect", LogUtils.getThreadName() + "");
                sWebSettingsSetDomStorageEnabled.invoke(setting, true);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            /* feature not supported, do something else */
        }

    }

    public static void setDisplayZoomControls(WebSettings setting, boolean enable) {
        if (sWebSettingsSetDisplayZoomControls != null) {
            /* feature is supported */
            try {
                sWebSettingsSetDisplayZoomControls.invoke(setting, enable);
                LogUtils.logd(TAG, LogUtils.getThreadName() + "enable:" + enable);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            /* feature not supported, do something else */
            System.out.println("Native not supported - we're ok");
        }
    }
}
