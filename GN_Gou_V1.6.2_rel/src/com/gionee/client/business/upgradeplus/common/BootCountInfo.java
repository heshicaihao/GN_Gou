package com.gionee.client.business.upgradeplus.common;

public class BootCountInfo {

    private String mVersionName;
    private long mBootTimes;
    private int mVersionCode;

    public String getVersionName() {
        return mVersionName;
    }

    public void setVersionName(String versionName) {
        this.mVersionName = versionName;
    }

    public long getBootTimes() {
        return mBootTimes;
    }

    public void setBootTimes(long bootTimes) {
        this.mBootTimes = bootTimes;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public void setVersionCode(int versionCode) {
        this.mVersionCode = versionCode;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("My versionName:" + this.mVersionName);
        sb.append(",versionCode:" + this.mVersionCode);
        sb.append(",bootTimes:" + this.mBootTimes);
        return sb.toString();
    }

}
