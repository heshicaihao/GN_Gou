// Gionee <yuwei><2013-12-23> add for CR00821559 begin
/*
 * StreamUtills.java
 * classes : com.gionee.client.business.util.StreamUtills
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-23 下午2:35:06
 */
package com.gionee.client.business.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * InputStreamUtils
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-23 下午2:35:33
 * @description TODO
 */
public class InputStreamUtils {

    private final static int BUFFER_SIZE = 4096;

    /**
     * 将InputStream转换成String
     * 
     * @param in
     *            InputStream
     * @return String
     * @throws Exception
     * 
     */
    public static String inputStreamToString(InputStream in) throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
            outStream.write(data, 0, count);
        }

        data = null;
        return new String(outStream.toByteArray(), "UTF-8");
    }

    /**
     * 将InputStream转换成某种字符编码的String
     * 
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String inputStreamToString(InputStream in, String encoding) throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(), "ISO-8859-1");
    }

    /**
     * 将String转换成InputStream
     * 
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream stringToInputStream(String in) throws Exception {

        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("ISO-8859-1"));
        return is;
    }

    /**
     * 将InputStream转换成byte数组
     * 
     * @param in
     *            InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] inputStreamToByte(InputStream in) throws IOException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
            outStream.write(data, 0, count);
        }

        data = null;
        return outStream.toByteArray();
    }

    /**
     * 将byte数组转换成InputStream
     * 
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream byteToInputStream(byte[] in) throws Exception {

        ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;
    }

    /**
     * 将byte数组转换成String
     * 
     * @param in
     * @return
     * @throws Exception
     */
    public static String byteTOString(byte[] in) throws Exception {

        InputStream is = byteToInputStream(in);
        return inputStreamToString(is);
    }

}