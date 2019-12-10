// Gionee <yuwei><2013-10-31> add for CR00821559 begin
/*
 * StringUtills.java
 * classes : com.gionee.poorshopping.business.util.StringUtills
 * @author yuwei
 * V 1.0.0
 * Create at 2013-10-31 上午10:37:03
 */
package com.gionee.client.business.util;

import java.io.InputStream;
import java.text.DecimalFormat;

import org.json.JSONArray;

import android.content.Context;
import android.text.TextUtils;

/**
 * StringUtills
 * 
 * @author yuwei <br/>
 * @date create at 2013-10-31 上午10:37:03
 * @description TODO
 */
public class StringUtills {
    private static final String LETTER_REGEXP = "^[A-Za-z.]+$";

    public static boolean isNotContainEnglish(String source) {
        if (TextUtils.isEmpty(source)) {
            return false;
        }
        String[] sourseStr = source.split(" ");
        for (String sourceChar : sourseStr) {

            if (sourceChar.matches(LETTER_REGEXP)) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    public static String getM(double k) {
        // 该参数表示kb的值
        double m = k / 1024.0 / 1024.0;
        DecimalFormat df2 = new DecimalFormat("###.00");
        df2.format(m);
        String valueOfM = m + "M";
        // 返回kb转换之后的M值
        return valueOfM;
    }

    public static JSONArray getJsonArrayFromFile(Context context, String fileName) throws Exception {
        InputStream in = context.getAssets().open(fileName);
        String dataStr = InputStreamUtils.inputStreamToString(in);
        JSONArray array = new JSONArray(dataStr);
        return array;
    }

    public static JSONArray getJsonObjectFromFile(Context context, String fileName) throws Exception {
        InputStream in = context.getAssets().open(fileName);
        String dataStr = InputStreamUtils.inputStreamToString(in);
        JSONArray array = new JSONArray(dataStr);
        return array;
    }

}
