package com.gionee.client.activity.history;

public class BrowseHistoryInfo {

    private String mTitle;
    private String mTime;
    private String mUrl;
    private String mType;
    private String mPlatform;
    private long mDays;
    private long mTimemillis;

    public BrowseHistoryInfo() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmPlatform() {
        return mPlatform;
    }

    public void setmPlatform(String mPlatform) {
        this.mPlatform = mPlatform;
    }

    @Override
    public String toString() {
        return "mTitle=" + mTitle + "mTime=" + mTime + "mType" + mType + "mPlatform=" + mPlatform + "mUrl="
                + mUrl;
    }

    public long getmDays() {
        return mDays;
    }

    public void setmDays(long mDays) {
        this.mDays = mDays;
    }

    public long getmTimemillis() {
        return mTimemillis;
    }

    public void setmTimemillis(long mTimemillis) {
        this.mTimemillis = mTimemillis;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof BrowseHistoryInfo)) {
            return false;
        }
        BrowseHistoryInfo info = (BrowseHistoryInfo) obj;
        if (this.getmTimemillis() == info.getmTimemillis()) {
            return true;
        }
        return false;

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
