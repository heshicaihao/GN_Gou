//Gionee <yangxiong><2012-6-5> modify for CR00818290 begin
package com.gionee.client.business.statistic.util;

import android.annotation.SuppressLint;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    private static final char[] sHexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
            'd', 'e', 'f'};

    private static MessageDigest sMessagedigest = null;
    static {
        try {
            sMessagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            nsaex.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public static String getMD5String(String s) {
        return getMD5String(s.getBytes(Charset.forName("UTF-8")));
    }

    public static boolean checkMD5(String newString, String md5PwdStr) {
        String s = getMD5String(newString);
        return s.equals(md5PwdStr);
    }

    public static String getFileMD5String(File file) throws IOException {
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                sMessagedigest.update(buffer, 0, numRead);
            }
            return bufferToHex(sMessagedigest.digest());
        } finally {
            Utils.closeIOStream(fis);
        }

    }

    public static String getFileMD5String(InputStream fis) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                sMessagedigest.update(buffer, 0, numRead);
            }
            fis.close();
            return bufferToHex(sMessagedigest.digest());
        } finally {
            Utils.closeIOStream(fis);
        }

    }

    public static String getMD5String(byte[] bytes) {
        sMessagedigest.update(bytes);
        return bufferToHex(sMessagedigest.digest());
    }

    private static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte[] bytes, int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = sHexDigits[(bt & 0xf0) >> 4];
        char c1 = sHexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

}
//Gionee <yangxiong><2012-6-5> modify for CR00818290 end