//Gionee <yangxiong><2012-6-5> modify for CR00818290 begin
package com.gionee.client.business.statistic.util;

public class ByteUtil {

    /**
     * 将int转为低字节在前，高字节在后的byte数组
     */
//    public static byte[] toLowHigh(int n) {
//        byte[] b = new byte[4];
//        b[0] = (byte) (n & 0xff);
//        b[1] = (byte) (n >> 8 & 0xff);
//        b[2] = (byte) (n >> 16 & 0xff);
//        b[3] = (byte) (n >> 24 & 0xff);
//        return b;
//    }

    /**
     * 整型转换为4位字节数组
     * 
     * @param intValue
     * @return
     */
//  public static byte[] int2Byte(int intValue) {
//      byte[] b = new byte[4];
//      for (int i = 0; i < 4; i++) {
//          b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
//          // System.out.print(Integer.toBinaryString(b[i])+" ");
//          // System.out.print((b[i] & 0xFF) + " ");
//      }
//      return b;
//  }

    /**
     * 4位字节数组转换为整型
     * 
     * @param b
     * @return
     */
//  public static int byte2Int(byte[] b) {
//      int intValue = 0;
//      for (int i = 0; i < b.length; i++) {
//          intValue += (b[i] & 0xFF) << (8 * (3 - i));
//          // System.out.print(Integer.toBinaryString(intValue)+" ");
//      }
//      return intValue;
//  }

    /**
     * 整型转换为N位字节数组
     * 
     * @param intValue
     * @return
     */
    public static byte[] intToByte(int intValue, int byteLeg) {
        byte[] b = new byte[byteLeg];
        for (int i = 0; i < byteLeg; i++) {
            b[i] = (byte) (intValue >> 8 * ((byteLeg - 1) - i) & 0xFF);
        }
        return b;
    }

    /**
     * 2位字节数组转换为整型
     * 
     * @param b
     * @return
     */
    public static int bytesToInt(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (b.length - 1 - i));
            // System.out.print(Integer.toBinaryString(intValue)+" ");
        }
        return intValue;
    }

    // long类型转成byte数组

    public static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = Long.valueOf(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    // byte数组转成long

//    public static long byteToLong(byte[] b) {
//        long s = 0;
//        long s0 = b[0] & 0xff;// 最低位
//        long s1 = b[1] & 0xff;
//        long s2 = b[2] & 0xff;
//        long s3 = b[3] & 0xff;
//        long s4 = b[4] & 0xff;// 最低位
//        long s5 = b[5] & 0xff;
//        long s6 = b[6] & 0xff;
//        long s7 = b[7] & 0xff;
//
//        // s0不变
//        s1 <<= 8;
//        s2 <<= 16;
//        s3 <<= 24;
//        s4 <<= 8 * 4;
//        s5 <<= 8 * 5;
//        s6 <<= 8 * 6;
//        s7 <<= 8 * 7;
//        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
//        return s;
//    }
}
//Gionee <yangxiong><2012-6-5> modify for CR00818290 end