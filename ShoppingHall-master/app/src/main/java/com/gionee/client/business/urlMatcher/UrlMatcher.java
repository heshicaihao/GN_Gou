// Gionee <yuwei><2014-9-16> add for CR00821559 begin
/*
 * UrlMatcher.java
 * classes : com.gionee.client.business.datahelper.UrlMatcher
 * @author yuwei
 * V 1.0.0
 * Create at 2014-9-16 上午9:59:51
 */
package com.gionee.client.business.urlMatcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.business.LocalBuisiness;
import com.gionee.framework.operation.page.PageCacheManager;

/**
 * com.gionee.client.business.datahelper.UrlMatcher
 * 
 * @author yuwei <br/>
 * @date create at 2014-9-16 上午9:59:51
 * @description TODO url匹配工具，匹配 商品，店铺，其它三种类型
 */
public class UrlMatcher {
    private static final String TAG = "UrlMatcher";
    private JSONArray mHistoryMatchRegularArray;
    private JSONArray mHelpMatchRegularArray;
    private JSONArray mOrderMatchRegularArray;
    private JSONArray mOptBarMatchRegularArray;
    private JSONArray mPageSkipMatchRegularArray;
    private static UrlMatcher mInstance;
    private List<String> mShopRegularList;
    private List<String> mGoodsRegularList;
    private String mWeiboNotice;
    private JSONArray mCompareRegArray;
    private static final String HISTORY_MATCH_RULE = "history_match_rule";
    private static final String HELP_MATCH_RULE = "help_match_rule";
    private static final String COMPARE_MATCH_RULE = "compare_match_rule";
    private static final String ORDER_MATCH_RULE = "order_match_rule";
    private static final String OPTION_BAR_MATCH_RULE = "opt_bar_match_rule";
    private static final String PAGE_SKIP_MATCH_RULE = "page_skip_match_rule";

    private UrlMatcher() {
        // TODO Auto-generated constructor stub
        initRegularList();
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void initRegularList() {
        mShopRegularList = new ArrayList<String>();
        mGoodsRegularList = new ArrayList<String>();
    }

    public static void destroy() {
        mInstance = null;
    }

    public String getHelpUrl(String url, Context context) {

        if (null == mHelpMatchRegularArray) {
            mHelpMatchRegularArray = getHelpMatchRule(context);
        }

        try {
            for (int i = 0; i < mHelpMatchRegularArray.length(); i++) {
                JSONObject helpJson = mHelpMatchRegularArray.optJSONObject(i);
                String preg = helpJson.optString(HttpConstants.Response.MatchRegular.PREG_S);
                LogUtils.log(TAG, "preg=" + preg + "====url=" + url);
                if (matchedUrl(url, preg)) {
                    String helpUrl = helpJson.optString(HttpConstants.Response.MatchRegular.URL_S);
                    LogUtils.log(TAG, "matched success");
                    LogUtils.log(TAG, helpUrl);
                    return helpUrl;
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }

    private JSONArray getHelpMatchRule(Context context) {
        try {
            return ShareDataManager.getJSONArray(context, HELP_MATCH_RULE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean matchedUrl(String url, String preg) {
        try {
            Pattern p = Pattern.compile(preg);
            Matcher m = p.matcher(url);
            while (m.find()) {
                LogUtils.logd(TAG, LogUtils.getThreadName() + " match ok: url = " + url);
                return true;
            }
        } catch (Exception e) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + " exception: " + e);
            e.printStackTrace();
        }
        return false;

    }

    private JSONArray getOrderMatchRule(Context context) {
        try {
            return ShareDataManager.getJSONArray(context, ORDER_MATCH_RULE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getOrderChannelId(Context context, String url) {
        if (null == mHelpMatchRegularArray) {
            mOrderMatchRegularArray = getOrderMatchRule(context);
        }
        try {
            for (int i = 0; i < mOrderMatchRegularArray.length(); i++) {
                JSONObject orderJson = mOrderMatchRegularArray.optJSONObject(i);
                String preg = orderJson.optString(HttpConstants.Response.MatchRegular.PREG_S);
                LogUtils.log(TAG, "preg=" + preg + "====url=" + url);
                if (matchedUrl(url, preg)) {
                    String helpUrl = orderJson.optString(HttpConstants.Response.MatchRegular.URL_S);
                    String channelId = orderJson.optString(HttpConstants.Response.MatchRegular.channel_id_s);
                    LogUtils.log(TAG, "matched success");
                    LogUtils.log(TAG, helpUrl);
                    return channelId;
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void getMatchRegularArray() {
        try {
            MyBean homeData = PageCacheManager.LookupPageData(GnHomeActivity.class.getName());
            JSONObject mRegularObject = homeData
                    .getJSONObject(HttpConstants.Data.RecommendHome.MATCH_REGULAR_JO);
            mHistoryMatchRegularArray = mRegularObject
                    .optJSONArray(HttpConstants.Response.MatchRegular.HISTORY_JA);
            mHelpMatchRegularArray = mRegularObject.optJSONArray(HttpConstants.Response.MatchRegular.HELP_JA);
            mCompareRegArray = mRegularObject
                    .optJSONArray(HttpConstants.Response.MatchRegular.CMP_GOODS_URL_REGEX_JA);
            mWeiboNotice = mRegularObject.optString(HttpConstants.Response.MatchRegular.WEIBO_NOTICE_S);
            mOptBarMatchRegularArray = mRegularObject
                    .optJSONArray(HttpConstants.Response.MatchRegular.OPT_BAR);
            mPageSkipMatchRegularArray = mRegularObject
                    .optJSONArray(HttpConstants.Response.MatchRegular.PAGE_SKIP);
            LogUtils.logd(TAG, LogUtils.getThreadName() + " mOptBarMatchRegularArray = "
                    + mOptBarMatchRegularArray + ", mPageSkipMatchRegularArray = "
                    + mPageSkipMatchRegularArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getWeiBoNotice() {
        return mWeiboNotice;
    }

    public boolean isCanAddCompare(String url, Context context) {

        if (null == mCompareRegArray) {
            mCompareRegArray = getCompareRegArray(context);
        }
        try {
            for (int i = 0; i < mCompareRegArray.length(); i++) {
                if (matchedUrl(url, mCompareRegArray.getString(i))) {
                    return true;
                }

            }
        } catch (Exception e) {
            LogUtils.log(TAG, "E:" + e);
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private JSONArray getCompareRegArray(Context context) {
        try {
            return ShareDataManager.getJSONArray(context, COMPARE_MATCH_RULE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray getOptBarArray(Context context) {
        try {
            return ShareDataManager.getJSONArray(context, OPTION_BAR_MATCH_RULE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray getPageSkipArray(Context context) {
        try {
            return ShareDataManager.getJSONArray(context, PAGE_SKIP_MATCH_RULE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isHasNewWeiboNotice(String notice) {
        if (TextUtils.isEmpty(mWeiboNotice) || Constants.NULL.equals(mWeiboNotice)) {
            return false;
        }
        if (TextUtils.isEmpty(notice)) {
            return true;
        }
        return !mWeiboNotice.equals(notice);
    }

    public static UrlMatcher getInstance() {
        if (mInstance == null) {
            mInstance = new UrlMatcher();
        }
        return mInstance;
    }

    public void init(Context context, JSONObject object) {
        if (object == null) {
            return;
        }
        ShareDataManager.putJsonArray(context, HISTORY_MATCH_RULE,
                object.optJSONArray(HttpConstants.Response.MatchRegular.HISTORY_JA));
        ShareDataManager.putJsonArray(context, HELP_MATCH_RULE,
                object.optJSONArray(HttpConstants.Response.MatchRegular.HELP_JA));
        ShareDataManager.putJsonArray(context, COMPARE_MATCH_RULE,
                object.optJSONArray(HttpConstants.Response.MatchRegular.CMP_GOODS_URL_REGEX_JA));
        ShareDataManager.putJsonArray(context, ORDER_MATCH_RULE,
                object.optJSONArray(HttpConstants.Response.MatchRegular.ORDER_MATCH_JA));
        ShareDataManager.putJsonArray(context, OPTION_BAR_MATCH_RULE,
                object.optJSONArray(HttpConstants.Response.MatchRegular.OPT_BAR));
        ShareDataManager.putJsonArray(context, PAGE_SKIP_MATCH_RULE,
                object.optJSONArray(HttpConstants.Response.MatchRegular.PAGE_SKIP));
    }

    public void initMatcherRegularList() {

        LocalBuisiness.getInstance().postRunable(new Runnable() {

            @Override
            public void run() {
                getMatchRegularArray();
                generateRegularList();

            }

        });
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void generateRegularList() {
        if (mHistoryMatchRegularArray != null) {
            LogUtils.log(TAG, LogUtils.getThreadName() + "mHistoryMatchRegularArray!=null");
            for (int i = 0; i < mHistoryMatchRegularArray.length(); i++) {
                JSONObject matchData = mHistoryMatchRegularArray.optJSONObject(i);
                addDataToRegularList(matchData);

            }
            LogUtils.log(TAG, "myGoodsRegularList=" + mGoodsRegularList.toString() + "===="
                    + "mShopRegularList=" + mShopRegularList.toString());
        }
    }

    /**
     * @param matchData
     * @author yuwei
     * @description TODO
     */
    private void addDataToRegularList(JSONObject matchData) {

        try {
            String regular = Html.fromHtml(matchData.optString(HttpConstants.Response.MatchRegular.PREG_S))
                    .toString();
            String type = matchData.optString(HttpConstants.Response.MatchRegular.TYPE_S);
            if (type.equals(UrlType.GOODS.getValue())) {
                mGoodsRegularList.add(regular);
            } else if (type.equals(UrlType.SHOP.getValue())) {
                mShopRegularList.add(regular);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public UrlType matchUrl(String url) {
        if (isShopUrl(url)) {
            return UrlType.SHOP;
        } else if (isGoodsUrl(url)) {
            return UrlType.GOODS;
        } else {
            return UrlType.OTHER;
        }

    }

    public boolean isShopUrl(String url) {
        try {
            for (Iterator<String> iterator = mShopRegularList.iterator(); iterator.hasNext();) {
                String regular = (String) iterator.next();
                LogUtils.log(TAG, "shops regular=" + regular + "url=" + url);
                if (matchedUrl(url, regular)) {
                    LogUtils.log(TAG, "match Sucessful url=" + url + "====" + "regular=" + regular
                            + "matched shop");
                    return true;
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogUtils.log(TAG, "excepton=" + e.getMessage());
        }
        return false;
    }

    public boolean isGoodsUrl(String url) {
        try {
            for (Iterator<String> iterator = mGoodsRegularList.iterator(); iterator.hasNext();) {
                String regular = (String) iterator.next();
                LogUtils.log(TAG, "goods regular=" + regular + "====url=" + url);
                if (matchedUrl(url, regular)) {
                    LogUtils.log(TAG, "goods regular=" + regular + "====url=" + url
                            + "------matched goods successful");
                    return true;
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogUtils.log(TAG, "excepton=" + e.getMessage());
        }
        return false;

    }

    public enum UrlType {
        SHOP("shop"), GOODS("goods"), OTHER("others");
        String value;

        UrlType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public boolean isMatchOptBarUrl(Context context, String url) {
        if (null == mOptBarMatchRegularArray) {
            mOptBarMatchRegularArray = getOptBarArray(context);
        }
        try {
            String regular;
            for (int i = 0; i < mOptBarMatchRegularArray.length(); i++) {
                regular = mOptBarMatchRegularArray.getString(i);
                if (matchedUrl(url, regular)) {
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtils.log(TAG, "E:" + e);
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean isMatchPageSkipUrl(Context context, String url) {
        if (null == mPageSkipMatchRegularArray) {
            mPageSkipMatchRegularArray = getPageSkipArray(context);
        }
        try {
            for (int i = 0; i < mPageSkipMatchRegularArray.length(); i++) {
                String htmlUrl = mPageSkipMatchRegularArray.getString(i);
                String originalUrl = Html.fromHtml(htmlUrl).toString();
                LogUtils.log("UrlMatcher", "url=" + url + "matcherUrl=" + originalUrl);
                if (matchedUrl(url, originalUrl)) {
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtils.log(TAG, "E:" + e);
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
