// Gionee <yuwei><2014-4-1> add for CR00821559 begin
/*
 * AppDataHelper.java
 * classes : com.gionee.client.business.datahelper.AppDataHelper
 * @author yuwei
 * V 1.0.0
 * Create at 2014-4-1 下午4:34:06
 */
package com.gionee.client.business.appDownload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.gionee.client.activity.apprecommend.AppRecommendActivity;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.GNDowanload;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;
import com.gionee.framework.operation.page.PageCacheManager;
import com.gionee.framework.operation.utills.FileUtil;

/**
 * AppDataHelper
 * 
 * @author yuwei <br/>
 * @date create at 2014-4-1 下午4:34:06
 * @description TODO
 */
@SuppressLint("InlinedApi")
public class AppDataHelper {
    private static final String TAG = "AppDataHelper";
    private static final String DOWNLOADING_APP_LIST = "downloading_app_list";

    public static MyBean getAppPageData() {
        return PageCacheManager.LookupPageData(AppRecommendActivity.class.getName());
    }

    public static void resetAppListData(int position, MyBean bean) {
        MyBean appListBean = PageCacheManager.LookupPageData(AppRecommendActivity.class.getName());
        @SuppressWarnings("unchecked")
        ArrayList<MyBean> appList = (ArrayList<MyBean>) appListBean
                .getSerializable(HttpConstants.Data.AppRecommond.APP_INFO_LIST_AL);
        appList.set(position, bean);
    }

    public static ArrayList<MyBean> getAppListByJson(Context context, JSONArray array) {
//        LogUtils.log(TAG, array.toString());
        HashMap<String, Integer> appMap = getAppMap(context);
        ArrayList<MyBean> appList = createBeanListByArray(context, array, appMap);
        return appList;

    }

    public static MyBean resetMybeanState(Context context, MyBean myBean) {
        MyBean mBean = myBean;
        HashMap<String, Integer> appMap = getAppMap(context);
        JSONObject jsonData = mBean.getJSONObject(HttpConstants.Data.AppRecommond.APP_INFO_JO);

        ArrayList<MyBean> historyList = getHistoryDownloadList(context);
        MyBean bean = createBean(context, appMap, jsonData, historyList);
        return bean;

    }

    /**
     * @param myBean
     * @param myBean2
     * @return
     * @author yuwei
     * @description TODO
     */
    private static boolean compareBean(JSONObject myBean, MyBean myBean2) {
        return getAppName(myBean2)
                .equals(myBean.optString(HttpConstants.Response.RecommondAppList.PACKAGE_S));
    }

    /**
     * @param myBean2
     * @return
     * @author yuwei
     * @description TODO
     */
    private static String getAppName(MyBean myBean2) {
        return myBean2.getString(HttpConstants.Data.AppRecommond.APP_FILE_NAME);
    }

    /**
     * @param array
     * @param appMap
     * @return
     * @author yuwei
     * @description TODO
     */
    private static ArrayList<MyBean> createBeanListByArray(Context context, JSONArray array,
            HashMap<String, Integer> appMap) {
        if (array == null) {
            return null;
        }
        ArrayList<MyBean> appList = new ArrayList<MyBean>();
        ArrayList<MyBean> historyList = getHistoryDownloadList(context);
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonData = ((JSONObject) array.opt(i));
            MyBean bean = createBean(context, appMap, jsonData, historyList);
            appList.add(bean);
            LogUtils.log(TAG, "myBean=" + bean.toString());
        }
        return appList;
    }

    /**
     * @param context
     * @return
     * @author yuwei
     * @description TODO
     */
    private static ArrayList<MyBean> getHistoryDownloadList(Context context) {
        ArrayList<MyBean> historyList = new ArrayList<MyBean>();
        ArrayList<MyBean> downloadingList = getDownloadingList(context);
        ArrayList<MyBean> downloadedList = getDownloadedList(context);
        ArrayList<MyBean> pendingList = getPendingList(context);
        ArrayList<MyBean> pausedList = getPausedList(context);
        historyList.addAll(downloadedList);
        historyList.addAll(downloadingList);
        historyList.addAll(pendingList);
        historyList.addAll(pausedList);
        return historyList;
    }

    /**
     * @param appMap
     * @param jsonData
     * @return
     * @author yuwei
     * @description TODO
     */
    private static MyBean createBean(Context context, HashMap<String, Integer> appMap, JSONObject jsonData,
            ArrayList<MyBean> dowloadList) {
        MyBean bean = setLastDownloadStatus(context, jsonData, dowloadList, appMap);
        putJsonData(jsonData, bean);
        return bean;
    }

    /**
     * @param context
     * @param jsonData
     * @param dowloadList
     * @param bean
     * @author yuwei
     * @description TODO
     */
    private static MyBean setLastDownloadStatus(Context context, JSONObject jsonData,
            ArrayList<MyBean> dowloadList, HashMap<String, Integer> appMap) {
        MyBean bean = MyBeanFactory.createDataBean();
        bean.put(HttpConstants.Data.AppRecommond.APP_STATUS_EM, GNDowanload.DownloadStatus.STATUS_INSTALL);
        if (dowloadList == null) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + "dowloadList is null");
            return bean;
        }
        setStatus(appMap, jsonData, bean);
        setStatusByDownloadHistory(jsonData, dowloadList, bean);
        return bean;
    }

    /**
     * @param jsonData
     * @param dowloadList
     * @param bean
     * @author yuwei
     * @description TODO
     */
    private static void setStatusByDownloadHistory(JSONObject jsonData, ArrayList<MyBean> dowloadList,
            MyBean bean) {
        LogUtils.logd(TAG, LogUtils.getThreadName() + "jsonData=" + jsonData.toString() + "downloadList="
                + dowloadList.toString());
        if (bean.getInt(HttpConstants.Data.AppRecommond.APP_STATUS_EM) == GNDowanload.DownloadStatus.STATUS_OPEN) {
            return;
        }
        for (Iterator<MyBean> iterator2 = dowloadList.iterator(); iterator2.hasNext();) {
            MyBean myBean2 = (MyBean) iterator2.next();
            if (compareBean(jsonData, myBean2)) {

                bean.put(HttpConstants.Data.AppRecommond.APP_STATUS_EM,
                        myBean2.get(HttpConstants.Data.AppRecommond.APP_STATUS_EM));
                bean.put(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_PERCENT_I,
                        myBean2.get(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_PERCENT_I));
                bean.put(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L,
                        myBean2.get(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L));

            }

        }
    }

    /**
     * @param appMap
     * @param jsonData
     * @param bean
     * @author yuwei
     * @description TODO
     */
    private static void setStatus(HashMap<String, Integer> appMap, JSONObject jsonData, MyBean bean) {
        String packageName = jsonData.optString(HttpConstants.Response.RecommondAppList.PACKAGE_S);
        int appVersion = jsonData.optInt(HttpConstants.Response.RecommondAppList.VERSION_S);
        if (appMap.containsKey(packageName)) {
            setStatusByVersion(appMap, bean, packageName, appVersion);
        }

    }

    /**
     * @param appMap
     * @param bean
     * @param packageName
     * @param appVersion
     * @author yuwei
     * @description TODO
     */
    private static void setStatusByVersion(HashMap<String, Integer> appMap, MyBean bean, String packageName,
            int appVersion) {
        if (appMap.get(packageName) < appVersion) {
            setState(bean, GNDowanload.DownloadStatus.STATUS_UPDATE);
        } else {
            setState(bean, GNDowanload.DownloadStatus.STATUS_OPEN);
        }
    }

    /**
     * @param jsonData
     * @param bean
     * @author yuwei
     * @description TODO
     */
    private static void putJsonData(JSONObject jsonData, MyBean bean) {
        bean.put(HttpConstants.Data.AppRecommond.APP_INFO_JO, jsonData);
    }

    /**
     * @param bean
     * @author yuwei
     * @description TODO
     */
    private static void setState(MyBean bean, int status) {
        bean.put(HttpConstants.Data.AppRecommond.APP_STATUS_EM, status);
    }

    /**
     * @param context
     * @return
     * @author yuwei
     * @description TODO
     */
    private static HashMap<String, Integer> getAppMap(Context context) {
        HashMap<String, Integer> appMap = new HashMap<String, Integer>();
        List<PackageInfo> installedList = InstallUtills.getPackageList(context);
        for (Iterator<PackageInfo> iterator = installedList.iterator(); iterator.hasNext();) {
            PackageInfo packageInfo = (PackageInfo) iterator.next();
            appMap.put(packageInfo.packageName, packageInfo.versionCode);
        }
        return appMap;
    }

    public static ArrayList<MyBean> getDownloadingList(Context context) {
        GNDownloadUtills utils = new GNDownloadUtills(
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE));
        return utils.queryByStatus(DownloadManager.STATUS_RUNNING);
    }

    public static ArrayList<MyBean> getPausedList(Context context) {
        GNDownloadUtills utils = new GNDownloadUtills(
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE));
        return utils.queryByStatus(DownloadManager.STATUS_PAUSED);
    }

    public static ArrayList<MyBean> getPendingList(Context context) {
        GNDownloadUtills utils = new GNDownloadUtills(
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE));
        return utils.queryByStatus(DownloadManager.STATUS_PENDING);
    }

    public static ArrayList<MyBean> getDownloadedList(Context context) {
        GNDownloadUtills utils = new GNDownloadUtills(
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE));
        ArrayList<MyBean> downloadList = utils.queryByStatus(DownloadManager.STATUS_SUCCESSFUL);
        LogUtils.log(TAG, LogUtils.getThreadName() + downloadList.toString());
        return downloadList;
    }

    public static void saveDownloadingList(Context context, ArrayList<MyBean> beanList) {
        FileUtil.writeObjectToLocation(context, DOWNLOADING_APP_LIST, beanList);

    }

    public static ArrayList<MyBean> getLastDownloadingList(Context context) {
        ArrayList<MyBean> appList = FileUtil.readObjectFromLocation(context, DOWNLOADING_APP_LIST);
        return appList;
    }
}
