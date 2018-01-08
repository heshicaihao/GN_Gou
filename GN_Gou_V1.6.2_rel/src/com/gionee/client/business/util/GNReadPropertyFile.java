/*******************************************************************************
 * Filename:
 * ---------
 *  GNReadPropertyFile.java
 *
 * Project:
 * --------
 *   Browser
 *
 * Description:
 * ------------
 *  save and query properties
 *
 * Author:
 * -------
 *  2012.05.21 hanyong
 *
 ****************************************************************************/
package com.gionee.client.business.util;

import java.io.File;

import android.os.Environment;

public class GNReadPropertyFile {

    public static final String UPGRADE_WIFI = "upgrade.wifi";
    public static final String TAG = "GNReadPropertyFile";

    private static final String FILE_PATH_FLAG_TEST = "gngou1234567890test";
    private static final String FILE_PATH_FLAG_TEST_ALL = "gngou1234567890testall";
    private static final String FILE_PATH_FLAG_UPGRATE_TEST = "gngou1234567890testupgrade";

    public static String getExternalStorageDirectory() {
        String rootpath = "";
        try {
            rootpath = Environment.getExternalStorageDirectory().getPath();
            if (!rootpath.endsWith(File.separator)) {
                rootpath += File.separator;
            }
        } catch (Exception e) {
            rootpath = "";
        }
        return rootpath;
    }

    public static boolean isTestEnvironment() {
        if (isFileExit(FILE_PATH_FLAG_TEST)) {
            return true;
        }
        return false;
    }

    public static boolean isTestFullEnviroment() {
        if (isFileExit(FILE_PATH_FLAG_TEST_ALL)) {
            return true;
        }
        return false;

    }
    public static boolean isUpgrateTestEnvironment() {
        if (isFileExit(FILE_PATH_FLAG_UPGRATE_TEST)) {
            return true;
        }
        return false;
    }

    public static boolean isFileExit(String file) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdCardDir = GNReadPropertyFile.getExternalStorageDirectory();
            File f = new File(sdCardDir + file);
            if (f.exists()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
