/**
 * 2012-9-13
 */
//Gionee <yangxiong><2012-6-5> modify for CR00818290 begin
package com.gionee.client.business.statistic.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author zhuxing
 * @version 1.0.0
 */
public class UnGZIP {
    private static final int BUFFER_SIZE = 10 * 1024;

    public static byte[] decompress(byte[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        byte[] result = null;
        ByteArrayOutputStream bout = null;
        ByteArrayInputStream bin = null;
        GZIPInputStream zipIn = null;
        try {
            bout = new ByteArrayOutputStream();
            bin = new ByteArrayInputStream(src);
            zipIn = new GZIPInputStream(bin);
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = zipIn.read(buffer)) != -1) {
                bout.write(buffer, 0, n);
            }
            result = bout.toByteArray();
            bout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIOStream(bout, zipIn, zipIn);
        }
        return result;
    }

    /**
     * 压缩
     * 
     * @author Point
     * */
    public static byte[] compressToByte(byte[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        byte[] result = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(src, 0, src.length);
            gzip.finish();
            gzip.flush();
            result = out.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIOStream(gzip, out);
        }
        return result;
    }

}
//Gionee <yangxiong><2012-6-5> modify for CR00818290 end