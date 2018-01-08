// Gionee <yuwei><2013-12-23> add for CR00821559 begin
/*
 * WebFilterChainManager.java
 * classes : com.gionee.client.business.filter.WebFilterChainManager
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-23 下午2:26:10
 */
package com.gionee.client.business.filter;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import com.gionee.client.business.util.InputStreamUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.framework.event.SuperInjectFactory;

/**
 * WebFilterChainManager
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-23 下午7:15:34
 * @description TODO url过滤器
 */
public class WebFilterChainManager {
    private static final String TAG = "WebFilterChainManager";
    private static final String FILTER = "filter";
    private static WebFilterChainManager sInstance;
    private static IUrlHandler[] sFilterList;

    private static JSONArray getChainConfigArray(Context context) throws Exception {
        InputStream in = context.getAssets().open("filterChain.json");
        String dataStr = InputStreamUtils.inputStreamToString(in);
        LogUtils.log(TAG, dataStr);
        JSONArray array = new JSONArray(dataStr);
        return array;
    }

    private WebFilterChainManager(Context context) {
        // TODO Auto-generated constructor stub
        sFilterList = getFilterList(context);
        buildFilterChain(sFilterList);
    }

    public static WebFilterChainManager getInstance(Context context) {
        synchronized (WebFilterChainManager.class) {
            if (sInstance == null) {
                sInstance = new WebFilterChainManager(context);
            }
        }
        return sInstance;

    }

    private static IUrlHandler[] getFilterList(Context context) {
        IUrlHandler[] filterList = null;
        try {
            JSONArray array = getChainConfigArray(context);
            filterList = new IUrlHandler[array.length()];
            for (int i = 0; i < array.length(); i++) {
                IUrlHandler filter = getFilterById(array, i);
                filterList[i] = filter;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return filterList;
    }

    public boolean startFilter(Activity context, String url) {
        if (isArrayEmpty(sFilterList)) {
            return false;
        }
        return sFilterList[0].handleRequest(context, url);
    }

    private void buildFilterChain(IUrlHandler[] filterList) {
        if (isArrayEmpty(filterList)) {
            return;
        }
        for (int i = 0; i < filterList.length - 1; i++) {
            filterList[i].setSuccessor(filterList[i + 1]);
        }

    }

    /**
     * @param filterList
     * @return
     * @author yuwei
     * @description TODO
     */
    private boolean isArrayEmpty(IUrlHandler[] filterList) {
        return filterList == null || filterList.length == 0;
    }

    /**
     * @param array
     * @param id
     * @throws JSONException
     * @author yuwei
     * @description TODO
     */
    private static IUrlHandler getFilterById(JSONArray array, int id) throws JSONException {
        IUrlHandler filter;
        JSONObject object = (JSONObject) array.get(id);
        String filterName = object.optString(FILTER);
        LogUtils.log(TAG, filterName);
        filter = (IUrlHandler) SuperInjectFactory.createByClassName(filterName);
        LogUtils.log(TAG, filter.getClass().getName() + "======success");
        return filter;
    }

}
//Gionee <yuwei><2013-12-23> add for CR00821559 end