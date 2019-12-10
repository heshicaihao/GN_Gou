package com.gionee.client.business.push;

public class PushDataInfo {
    private String mPushUrl;
    private String mAction;
    private String mType;
    private String mStatisticId;
    private int mGameId;
    private String mActivity;

    public String getmPushUrl() {
        return mPushUrl;
    }

    public void setmPushUrl(String mPushUrl) {
        this.mPushUrl = mPushUrl;
    }

    public String getmAction() {
        return mAction;
    }

    public void setmAction(String mAction) {
        this.mAction = mAction;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmStatisticId() {
        return mStatisticId;
    }

    public void setmStatisticId(String mStatisticId) {
        this.mStatisticId = mStatisticId;
    }

    public int getmGameId() {
        return mGameId;
    }

    public void setmGameId(int mGameId) {
        this.mGameId = mGameId;
    }

    @Override
    public String toString() {
        return "PushUrl:" + mPushUrl + " Action:" + mAction + " Type" + mType + " StatisticId" + mStatisticId
                + " GameId" + mGameId + " mActivity" + mActivity;
    }

    public String getmActivity() {
        return mActivity;
    }

    public void setmActivity(String mActivity) {
        this.mActivity = mActivity;
    }
}
