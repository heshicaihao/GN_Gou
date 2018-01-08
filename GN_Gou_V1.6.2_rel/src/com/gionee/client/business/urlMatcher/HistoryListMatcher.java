// Gionee <yuwei><2014-9-18> add for CR00821559 begin
/*
 * HistoryListMatcher.java
 * classes : com.gionee.client.business.datahelper.HistoryListMatcher
 * @author yuwei
 * V 1.0.0
 * Create at 2014-9-18 下午4:53:51
 */
package com.gionee.client.business.urlMatcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gionee.client.activity.history.BrowseHistoryInfo;

/**
 * com.gionee.client.business.datahelper.HistoryListMatcher
 * 
 * @author yuwei <br/>
 * @date create at 2014-9-18 下午4:53:51
 * @description TODO 历史列表匹配工具类，返回商品，店铺，网页列表
 */
public class HistoryListMatcher {

    private List<BrowseHistoryInfo> mShopList;
    private List<BrowseHistoryInfo> mGoodsList;
    private List<BrowseHistoryInfo> mOthersList;

    public HistoryListMatcher(List<BrowseHistoryInfo> historyList) {
        if (historyList == null || historyList.size() == 0) {
            return;
        }
        initList();
        matchList(historyList);

    }

    /**
     * @param historyList
     * @author yuwei
     * @description TODO
     */
    private void matchList(List<BrowseHistoryInfo> historyList) {
        try {
            for (Iterator<BrowseHistoryInfo> iterator = historyList.iterator(); iterator.hasNext();) {
                BrowseHistoryInfo browseHistoryInfo = (BrowseHistoryInfo) iterator.next();
                String url = browseHistoryInfo.getUrl();
                if (UrlMatcher.getInstance().isGoodsUrl(url)) {
                    mGoodsList.add(browseHistoryInfo);
                } else if (UrlMatcher.getInstance().isShopUrl(url)) {
                    mShopList.add(browseHistoryInfo);
                } else {
                    mOthersList.add(browseHistoryInfo);
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
    private void initList() {
        mShopList = new ArrayList<BrowseHistoryInfo>();
        mGoodsList = new ArrayList<BrowseHistoryInfo>();
        mOthersList = new ArrayList<BrowseHistoryInfo>();
    }

    /**
     * @return the mShopList
     */
    public List<BrowseHistoryInfo> getmShopList() {
        return mShopList;
    }

    /**
     * @return the mGoodsList
     */
    public List<BrowseHistoryInfo> getmGoodsList() {
        return mGoodsList;
    }

    /**
     * @return the mOthersList
     */
    public List<BrowseHistoryInfo> getmOthersList() {
        return mOthersList;
    }
}
