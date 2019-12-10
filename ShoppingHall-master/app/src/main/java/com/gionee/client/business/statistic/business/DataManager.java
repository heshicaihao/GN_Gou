//Gionee <wangyy><2014-03-12> modify for CR00956169 begin
package com.gionee.client.business.statistic.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.gionee.client.business.statistic.GnCountDataHelper;
import com.gionee.client.business.statistic.database.DBFields;
import com.gionee.client.business.statistic.database.LocalDatabaseHelper;
import com.gionee.client.business.statistic.events.AppEvent;
import com.gionee.client.business.statistic.header.PublicHeaderParamsManager;
import com.gionee.client.business.statistic.manager.AbstractEventDataManager;
import com.gionee.client.business.statistic.manager.AppEventDataManager;
import com.gionee.client.business.statistic.manager.EventDataInfos;
import com.gionee.client.business.statistic.manager.EventDataManager;
import com.gionee.client.business.statistic.util.Base64;
import com.gionee.client.business.statistic.util.GioneeRC4;
import com.gionee.client.business.statistic.util.MyDatabaseUtils;
import com.gionee.client.business.statistic.util.NetworkUtils;
import com.gionee.client.business.statistic.util.UnGZIP;
import com.gionee.client.business.statistic.util.Utils;
import com.gionee.client.business.util.LogUtils;

public class DataManager {
    public static final String TAG = "DataManager";
    private Context mContext;

    private int mMaxGprsUploadSizePerDay;
    private int mMaxWifiUploadSizePerDay;
    private int mMinGprsUploadSizePerDay;
    private int mMinWifiUploadSizePerDay;
//    private int mMaxTableNumber;
    private int mMaxEventNumber;
    private int mAppEventCountWhenCheckUpload;

    private int mCurrentEventNum = -1;
    private Object mEventSynObject = new Object();

    private PublicHeaderParamsManager mPublicHeaderParamsManager = null;
    private LocalPreferenceManager mLocalPreferenceManager = null;

    private String mPublicInfoMD5Code = "";
    private List<EventDataManager> mEventDataManagers;

    private LocalDatabaseHelper mLocalDatabaseHelper;

    protected DataManager(Context context, LocalPreferenceManager localPreferenceManager) {
        mContext = context;
        mLocalDatabaseHelper = LocalDatabaseHelper.getInstance(context);
        initCurrentEventNum();
        mPublicHeaderParamsManager = new PublicHeaderParamsManager(context);
        initlocalPreferenceManager(localPreferenceManager);
        initDataManagers();
    }

    private void initCurrentEventNum() {
        mCurrentEventNum = getAppEventCount();
    }

    private void initDataManagers() {
        mEventDataManagers = new ArrayList<EventDataManager>(4);

        createAllEventDataManagers();
    }

    private void initlocalPreferenceManager(LocalPreferenceManager localPreferenceManager) {
        mLocalPreferenceManager = localPreferenceManager;
        mLocalPreferenceManager.resetAllDataInfoIfNextDay();
        getUploadCfg();
        checkIfDeleted();
    }

    public String getPublicInfoMD5Code() {
        return mPublicInfoMD5Code;
    }

    private void insertOneMessage(Context context, Uri baseUri, ContentValues values, int maxNumber) {
        if (needDeleteOldestRecord(baseUri, maxNumber)) {
            deleteOldestRecord(baseUri);
        }

        mLocalDatabaseHelper.insert(baseUri, values);

    }

    private void deleteOldestRecord(Uri baseUri) {
        Cursor cursor = null;
        try {
            cursor = mLocalDatabaseHelper.query(baseUri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String id = MyDatabaseUtils.getStringColumValue(cursor, DBFields.ID);
                Uri uriDeleted = Uri.withAppendedPath(baseUri, "/" + id);
                mLocalDatabaseHelper.delete(uriDeleted, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor == null) {
                return;
            }
            MyDatabaseUtils.closeCursor(cursor);
        }

    }

    private boolean needDeleteOldestRecord(Uri baseUri, int maxNum) {
        try {
            int currentNum = getCurrentRecordsNumByUri(baseUri);

            if (currentNum >= maxNum) {
                return true;
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            LogUtils.loge(TAG, LogUtils.getThreadName() + e.toString());
        }
        return false;
    }

    private int getCurrentRecordsNumByUri(Uri baseUri) throws IllegalArgumentException {
        if (baseUri == LocalDatabaseHelper.getEventUri()) {
            return getAppEventCount();
        }
        throw new IllegalArgumentException("uri is wrong");
    }

    public int getAppEventCount() {
        synchronized (mEventSynObject) {
            if (mCurrentEventNum == -1) {
                mCurrentEventNum = getCurrentEventFromDatabase(LocalDatabaseHelper.getEventUri());
            }
            return mCurrentEventNum;
        }

    }

    public void insertOneAppEvent(Context context, AppEvent event) {
        synchronized (mEventSynObject) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + "mCurrentEventNum = " + mCurrentEventNum);
            try {
                insertOneMessage(context, LocalDatabaseHelper.getEventUri(), event.toContentValues(),
                        mMaxEventNumber);
                if (mCurrentEventNum < mMaxEventNumber) {
                    mCurrentEventNum++;
                }
            } catch (Exception e) {
                LogUtils.loge(TAG, "insertOneAppEvent() " + e.toString());
                e.printStackTrace();
            }
        }

    }

    private int getCurrentEventFromDatabase(Uri uri) {
        Cursor appEventCursor = null;
        try {
            appEventCursor = mLocalDatabaseHelper.query(uri, null, null, null, null);
            if (Utils.isNull(appEventCursor)) {
                return 0;
            } else {
                return appEventCursor.getCount();
            }
        } catch (Exception e) {
            LogUtils.loge(TAG, "getCurrentEventFromDatabase() " + e.toString());
            e.printStackTrace();
        } finally {
            Utils.closeCursor(appEventCursor);
        }
        return 0;
    }

    /**
     * 判断是否需要发送公共信息。
     */
    public byte[] preparePublicInfoData() {
        return null;
        /*
        //目前暂无公共信息
        byte[] publicDataArray = mPublicHeaderParamsManager.getPublicHeaderParams();
        *//**
         * 保存公共信息的md5
         */
        /*
        mPublicInfoMD5Code = MD5Utils.getMD5String(publicDataArray);
        *//**
         * 公共信息发生变化，上传至服务器
         */
        /*
        String savedMD5Code = mLocalPreferenceManager.getPublicInfoMD5Code();
        LogUtils.logd(TAG, "public info MD5 = " + savedMD5Code);
        if (!savedMD5Code.equals(mPublicInfoMD5Code)) {
         LogUtils.logd(TAG, "public info changed, MD5 = " + mPublicInfoMD5Code);
         return publicDataArray;
        } else {
         return null;
        }
        */
    }

    /**
     * data_format_version=数据格式协议版本号, uid=手机唯一识别码, encript_imei=已加密的imei, app_version=应用软件版本号 ,
     * event_count=统计事件的条数, event_content=统计事件的内容.
     */
    public List<NameValuePair> prepareData() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        /**
         * 获取事件数据
         */
        byte[] messeageSumArray = getAllEventDataByteArray();
        if (messeageSumArray == null || messeageSumArray.length == 0) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + " messeageSumArray is empty");
            return null;
        }

        int messegeSum = messeageSumArray.length;
        LogUtils.logd(TAG, LogUtils.getThreadName() + "MessegeSum = " + messegeSum);
        /**
         * 获取事件数据头
         */
        List<NameValuePair> eventDataHeaderArray = mPublicHeaderParamsManager.getEventDataHeaderParams();
        if (messegeSum < 0) {
            return null;
        }

        // 压缩
        byte[] compressedByteArray = UnGZIP.compressToByte(messeageSumArray);
        LogUtils.logd(TAG, LogUtils.getThreadName() + " length after gzip = " + compressedByteArray.length);
        // 加密
        byte[] encryptedByteArray = GioneeRC4.code(compressedByteArray);
        LogUtils.logd(TAG, LogUtils.getThreadName() + " length after encrypt = " + encryptedByteArray.length);
        // base64编码
        String base64Str = Base64.encode(encryptedByteArray);
        LogUtils.logd(TAG, LogUtils.getThreadName() + " length after to base64 = " + base64Str.length());

        /**
         * 公共信息 + 事件数据头 + 事件数据
         */
        eventDataHeaderArray.add(new BasicNameValuePair("event_content", base64Str));

        return eventDataHeaderArray;
    }

    private int deleteUploadedRecords(Uri uri, String maxRecordId) {
        try {
            return mLocalDatabaseHelper.delete(uri, DBFields.ID + "<=" + maxRecordId, null);
        } catch (Exception e) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + "failed:" + e);
        }
        return 0;
    }

    public void deleteAppEventByMaxRowId() {
        synchronized (mEventSynObject) {
            String maxAppEventId = mLocalPreferenceManager.getSavedMaxAppEventId();
            if (Utils.isStringNull(maxAppEventId)) {
                return;
            }
            int deletedNum = deleteUploadedRecords(LocalDatabaseHelper.getEventUri(), maxAppEventId);
            mCurrentEventNum = mCurrentEventNum - deletedNum;
            LogUtils.logd(TAG, LogUtils.getThreadName() + "mCurrentEventNum = " + mCurrentEventNum);
        }

    }

    /**
     * 优化后的统计数据body部分的封装, 目前只存在自定义事件数据。
     * 
     * 上传规则配置：1. 0.5k<GPRS<10K 每天 2. 0k<wifi<100k 3. 最大保留条数 5千条 4. 最大存储量 1 M (可配置，为启用) 5.
     * 应用每保存10条记录检查一次是否符合上面上传规则（新增） 6. 每天至少上传一次(依赖于应用启动).
     */
    public byte[] getAllEventDataByteArray() {
        /**
         * 今天已上传的字节数;
         */
        int uploadedBytesToday = mLocalPreferenceManager.getUploadedSizeToday();
        int networkType = NetworkUtils.getNetworkType(mContext);

        /**
         * 2G/3G/4G 网络下达到每天最大上传量，则不上传
         */
        if (isReachMaxGprsUploadSizePerDay(networkType, uploadedBytesToday)) {
            return null;
        }

        /**
         * 组包
         */
        EventDataInfos eventDataInfos = getAllEventDataInfos(uploadedBytesToday);
        byte[] sumArray = eventDataInfos.getmEventDataArray();
        /**
         * 条数，即所有事件数据条数之和
         */
        short countSum = eventDataInfos.getmEventCount();
        /**
         * 流量，即所有事件数据的byte[]之和的大小
         */
        int flowSum = sumArray.length;
        LogUtils.logd(TAG, LogUtils.getThreadName() + "flowSum = " + flowSum + " countSum = " + countSum);

        /**
         * 如果条数小于等于0，自然没有统计数据
         */
        if (countSum <= 0) {
            return null;
        }

        /**
         * 6. 每天至少上传一次(依赖于应用启动). 今天已经传过，且数据小于最小上传量，则不上传，否则上传
         */
        boolean hasUploadedToday = mLocalPreferenceManager.isHasUploadedToday();
        if (hasUploadedToday && isFlowLessThanMinThreshold(flowSum, networkType)) {
            LogUtils.logd(TAG, LogUtils.getThreadName()
                    + "uploaded once today, data is less than min threshold (wifi: "
                    + mMinWifiUploadSizePerDay + " Bytes; Gprs : " + mMinGprsUploadSizePerDay + " Bytes)");
            return null;
        }

        /**
         * 记录本次的流量，只对GPRS
         */
        if (networkType == Constants.NetworkCode.NETWORK_MOBILE) {
            mLocalPreferenceManager.setDataBytesGotten(flowSum);
        }

        mPublicHeaderParamsManager.setStatisticsDataSum(countSum);
        return sumArray;
    }

    private void checkIfDeleted() {
        deleteAppEventByMaxRowId();
        mLocalPreferenceManager.resetDataInfoGotten();
    }

    // 从sharedpreference中取出每天的最大上传量、数据库最大存储量和过期月数
    public void getUploadCfg() {
        GnCountDataHelper helper = new GnCountDataHelper(mContext);
        mMinGprsUploadSizePerDay = helper.getGprsMinUploadSize() * Constants.DataFormat.B;
        mMinWifiUploadSizePerDay = helper.getWifiMinUploadSize() * Constants.DataFormat.B;
        mMaxGprsUploadSizePerDay = helper.getGprsMaxUploadSize() * Constants.DataFormat.B;
        mMaxWifiUploadSizePerDay = helper.getWifiMaxUploadSize() * Constants.DataFormat.B;
        mMaxEventNumber = helper.getDatabaseTableMaxEventNumber();
        mAppEventCountWhenCheckUpload = helper.getAppeventCountWhenCheckUpload();
    }

    // Gionee <yangxiong><2012-6-7> modify for CR00824102 begin
    public int getAppEventCountWhenCheckUpload() {
        return mAppEventCountWhenCheckUpload;
    }

    // Gionee <yangxiong><2012-6-7> modify for CR00824102 end

    /**
     * 不同事件数据的管理类，目前只有自定义事件数据。
     */
    private void createAllEventDataManagers() {
        mEventDataManagers.clear();
        AbstractEventDataManager manager = new AppEventDataManager();
        mEventDataManagers.add(manager);
    }

    /**
     * 是否达到每天最大运营商网络(Gprs)上传量（默认配置为1k）.
     * 
     * @param networkType
     * @param uploadedBytesToday
     * @return
     */
    private boolean isReachMaxGprsUploadSizePerDay(int networkType, int uploadedBytesToday) {
        if (networkType == Constants.NetworkCode.NETWORK_MOBILE) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + "uploadedBytesToday = " + uploadedBytesToday);
            if (uploadedBytesToday >= mMaxGprsUploadSizePerDay) { // 若已达到今天的上传总量，则不上传
                LogUtils.logd(TAG, LogUtils.getThreadName() + "have reached the sum uploaded today...");
                return true;
            }
        }
        return false;
    }

    /**
     * 流量是否小于最小阀值
     */
    private boolean isFlowLessThanMinThreshold(int flow, int networkType) {
        switch (networkType) {
            case Constants.NetworkCode.NETWORK_MOBILE:
                return flow < mMinGprsUploadSizePerDay;
            case Constants.NetworkCode.NETWORK_WIFI:
                return flow < mMinWifiUploadSizePerDay;
            default:
                break;
        }
        return true;
    }

    /**
     * 将四类数据拼装，组包.
     */
    private EventDataInfos getAllEventDataInfos(int uploadedBytesToday) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        /**
         * 计算条数，即所有事件数据条数之和
         */
        short countSum = getDataFromAllEventManager(uploadedBytesToday, baos);

        return produceFinalData(countSum, baos);
    }

    private short getDataFromAllEventManager(int uploadedBytesToday, ByteArrayOutputStream baos) {
        short countSum = 0;
        /**
         * 计算流量，即所有事件数据的byte[]之和的大小
         */
        int flowSum = 0;

        /**
         * 计算四类事件数据的总条数，总流量；组包.
         */
        for (EventDataManager eventDataManager : mEventDataManagers) {
            EventDataInfos eventInfos = new EventDataInfos();
            int maxSizeCanPut = getMaxSizeCanPut(flowSum);
            LogUtils.logd(TAG, LogUtils.getThreadName() + " maxSizeCanPut = " + maxSizeCanPut);
            eventDataManager.addEventData(mContext, eventInfos, maxSizeCanPut);

            byte[] eventArray = eventInfos.getmEventDataArray();
            short eventCount = eventInfos.getmEventCount();
            if (Utils.isNull(eventArray) || eventCount == 0) {
                LogUtils.logd(TAG, LogUtils.getThreadName() + "eventArray is null or eventCount = "
                        + eventCount);
                continue;
            }

            try {
                baos.write(eventArray);
            } catch (IOException e) {
                e.printStackTrace();
            }

            flowSum = flowSum + eventArray.length;
            countSum += eventCount;
        }
        return countSum;
    }

    private int getMaxSizeCanPut(int flowSum) {
        int networkType = NetworkUtils.getNetworkType(mContext);
        switch (networkType) {
            case Constants.NetworkCode.NETWORK_MOBILE:
                return mMaxGprsUploadSizePerDay - mLocalPreferenceManager.getUploadedSizeToday() - flowSum;
            case Constants.NetworkCode.NETWORK_WIFI:
                return mMaxWifiUploadSizePerDay - flowSum;
            default:
                break;
        }
        return 0;
    }

    private EventDataInfos produceFinalData(short countSum, ByteArrayOutputStream baos) {
        /**
         * 使用封装类返回结果
         */
        EventDataInfos eventDataInfos = new EventDataInfos();
        eventDataInfos.setmEventCount(countSum);

        try {
            byte[] sumArray = baos.toByteArray();
            baos.flush();
            eventDataInfos.setmEventDataArray(sumArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIOStream(baos);
        }
        return eventDataInfos;
    }
}
//Gionee <wangyy><2014-03-12> modify for CR00956169 end