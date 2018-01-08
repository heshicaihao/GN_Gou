/**
 * 2012-9-13
 */
//Gionee <yangxiong><2012-6-5> modify for CR00818290 begin
package com.gionee.client.business.statistic.util;

import android.annotation.SuppressLint;
import java.nio.charset.Charset;

/**
 * @author zhuxing
 * @version 1.0.0
 */
@SuppressLint("NewApi")
public class GioneeRC4 {
    private static final byte[] STATE = new byte[256];
    private static final String KEY = "92fe5927095eaac53cd1aa3408da8135";
    static {
        byte[] kb = KEY.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < 256; i++) {
            STATE[i] = (byte) i;
        }
        int index1 = 0;
        int index2 = 0;
        byte tmp;
        if (kb == null || kb.length == 0) {
            throw new NullPointerException();
        }
        for (int i = 0; i < 256; i++) {
            index2 = ((kb[index1] & 0xff) + (STATE[i] & 0xff) + index2) & 0xff;
            tmp = STATE[i];
            STATE[i] = STATE[index2];
            STATE[index2] = tmp;
            index1 = (index1 + 1) % kb.length;
        }
    }

    public static byte[] code(byte[] buf) {
        int x = 0;
        int y = 0;
        byte[] mystate = new byte[256];
        System.arraycopy(STATE, 0, mystate, 0, 256);
        int xorIndex;
        byte tmp;
        if (buf == null) {
            return null;
        }
        byte[] result = new byte[buf.length];
        for (int i = 0; i < buf.length; i++) {
            x = (x + 1) & 0xff;
            y = ((mystate[x] & 0xff) + y) & 0xff;
            tmp = mystate[x];
            mystate[x] = mystate[y];
            mystate[y] = tmp;
            xorIndex = ((mystate[x] & 0xff) + (mystate[y] & 0xff)) & 0xff;
            result[i] = (byte) (buf[i] ^ mystate[xorIndex]);
        }
        return result;
    }
}
//Gionee <yangxiong><2012-6-5> modify for CR00818290 end