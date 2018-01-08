package com.gionee.client.business.statistic.cfg;

//Gionee <wangyy><2014-03-12> modify for CR00956169 begin
public class CfgObject {
    private int mVersionNum;
    private int mMinWifiUploadSize;
    private int mMaxWifiUploadSize;
    private int mMinGprsUploadSize;
    private int mMaxGprsUploadSize;
    private int mMbMaxStore;
    private int mMaxTableNumber;
    private int mAppEventCountWhenCheckUpload;
    private int mEnableStatisticsActivity;

    public int getVersionNum() {
        return mVersionNum;
    }

    protected void setVersionNum(int versionNum) {
        mVersionNum = versionNum;
    }

    public int getMinWifiUploadSize() {
        return mMinWifiUploadSize;
    }

    protected void setMinWifiUploadSize(int minWifiUploadSize) {
        mMinWifiUploadSize = minWifiUploadSize;
    }

    public int getMaxWifiUploadSize() {
        return mMaxWifiUploadSize;
    }

    protected void setMaxWifiUploadSize(int maxWifiUploadSize) {
        mMaxWifiUploadSize = maxWifiUploadSize;
    }

    public int getMinGprsUploadSize() {
        return mMinGprsUploadSize;
    }

    protected void setMinGprsUploadSize(int minGprsUploadSize) {
        mMinGprsUploadSize = minGprsUploadSize;
    }

    public int getMaxGprsUploadSize() {
        return mMaxGprsUploadSize;
    }

    protected void setMaxGprsUploadSize(int maxGprsUploadSize) {
        mMaxGprsUploadSize = maxGprsUploadSize;
    }

    public int getMbMaxStore() {
        return mMbMaxStore;
    }

    protected void setMbMaxStore(int mbMaxStore) {
        mMbMaxStore = mbMaxStore;
    }

    public int getMaxTableNumber() {
        return mMaxTableNumber;
    }

    protected void setMaxTableNumber(int maxTableNumber) {
        mMaxTableNumber = maxTableNumber;
    }

    public int getAppEventCountWhenCheckUpload() {
        return mAppEventCountWhenCheckUpload;
    }

    protected void setAppEventCountWhenCheckUpload(int appEventCountWhenCheckUpload) {
        mAppEventCountWhenCheckUpload = appEventCountWhenCheckUpload;
    }

    public int getEnableStatisticsActivity() {
        return mEnableStatisticsActivity;
    }

    protected void setEnableStatisticsActivity(int enableStatisticsActivity) {
        mEnableStatisticsActivity = enableStatisticsActivity;
    }

    public String toString() {
        return " versionNum = " + mVersionNum + " minWifiUploadSize = " + mMinWifiUploadSize
                + " maxWifiUploadSize = " + mMaxWifiUploadSize + " minGprsUploadSize = " + mMinGprsUploadSize
                + " maxGprsUploadSize = " + mMaxGprsUploadSize + " mbMaxStore = " + mMbMaxStore
                + " maxTableNumber = " + mMaxTableNumber + " appEventCountWhenCheckUpload = "
                + mAppEventCountWhenCheckUpload + " mEnableStatisticsActivity = " + mEnableStatisticsActivity;
    }
    
    
}
//Gionee <wangyy><2014-03-12> modify for CR00956169 end
