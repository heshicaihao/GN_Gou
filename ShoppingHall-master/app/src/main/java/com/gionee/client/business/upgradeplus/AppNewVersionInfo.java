package com.gionee.client.business.upgradeplus;

public class AppNewVersionInfo {

    private String mVersion;
    private String mDisplayVersion;
    private String mReleaseNote;
    private int mSize;
    private boolean mIsPatchFile;
    private boolean mIsForceMode;

    public AppNewVersionInfo() {
        mVersion = "";
        mDisplayVersion = "";
        mReleaseNote = "";
        mSize = 0;
        mIsPatchFile = false;
        mIsForceMode = false;
    }

    public AppNewVersionInfo(String version, String displayVersion, String releaseNote, int size,
            boolean isPatchFile, boolean isForceMode) {
        this.mVersion = version;
        this.mDisplayVersion = displayVersion;
        this.mReleaseNote = releaseNote;
        this.mSize = size;
        this.mIsPatchFile = isPatchFile;
        this.mIsForceMode = isForceMode;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("version:" + mVersion);
        sb.append(",");
        sb.append("displayVersion:" + mDisplayVersion);
        sb.append(",");
        sb.append("size:" + mSize);
        sb.append(",");
        sb.append("releaseNote:" + mReleaseNote);
        sb.append(",");
        sb.append("isPatchFile:" + mIsPatchFile);
        sb.append(",");
        sb.append("isForceMode:" + mIsForceMode);
        return sb.toString();
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        this.mSize = size;
    }

    public boolean isPatchFile() {
        return mIsPatchFile;
    }

    public void setPatchFile(boolean isPatchFile) {
        this.mIsPatchFile = isPatchFile;
    }

    public boolean isForceMode() {
        return mIsForceMode;
    }

    public void setForceMode(boolean isForceMode) {
        this.mIsForceMode = isForceMode;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        this.mVersion = version;
    }

    public String getDisplayVersion() {
        return mDisplayVersion;
    }

    public void setDisplayVersion(String displayVersion) {
        this.mDisplayVersion = displayVersion;
    }

    public String getReleaseNote() {
        return mReleaseNote;
    }

    public void setReleaseNote(String releaseNote) {
        this.mReleaseNote = releaseNote;
    }

}
