// Gionee <yuwei><2014-9-16> add for CR00821559 begin
/*
 * UrlListMatcher.java
 * classes : com.gionee.client.business.datahelper.UrlListMatcher
 * @author yuwei
 * V 1.0.0
 * Create at 2014-9-16 上午11:24:00
 */
package com.gionee.client.business.urlMatcher;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gionee.framework.operation.utills.JSONArrayHelper;

/**
 * com.gionee.client.business.datahelper.UrlListMatcher
 * 
 * @author yuwei <br/>
 * @date create at 2014-9-16 上午11:24:00
 * @description TODO 完成jsonArray的匹配并返回店铺列表，商品列表，网页列表
 */
public class UrlListMatcher {
//    private final String TAG = "UrlListMatcher";
    private JSONArray mUrlArray;
    private String mUrlKey;
    private JSONArray mShopArray;
    private JSONArray mGoodsArray;
    private JSONArray mOthersArray;

    public UrlListMatcher(JSONArray urlArray, String urlKey) {
        this.mUrlArray = urlArray;
        this.mUrlKey = urlKey;
        matchArray();
    }

    public JSONArray getShopList() {
        return mShopArray;
    }

    public JSONArray getGoodsList() {
        return mGoodsArray;

    }

    public JSONArray getOthersList() {
        return mOthersArray;
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void matchArray() {
        initArray();
        createMatchedArray();
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void createMatchedArray() {
        try {
            JSONArrayHelper shopHelper = new JSONArrayHelper(mShopArray);
            JSONArrayHelper goodsHelper = new JSONArrayHelper(mGoodsArray);
            JSONArrayHelper otherHelper = new JSONArrayHelper(mOthersArray);
            for (int i = 0; i < mUrlArray.length(); i++) {
                JSONObject mUrlObject = mUrlArray.optJSONObject(i);
                String url = mUrlObject.optString(mUrlKey);
                if (UrlMatcher.getInstance().isGoodsUrl(url)) {
                    goodsHelper.addToLast(mUrlObject);
                } else if (UrlMatcher.getInstance().isShopUrl(url)) {
                    shopHelper.addToLast(mUrlObject);
                } else {
                    otherHelper.addToLast(mUrlObject);
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void initArray() {
        mShopArray = new JSONArray();
        mGoodsArray = new JSONArray();
        mOthersArray = new JSONArray();
    }
}
