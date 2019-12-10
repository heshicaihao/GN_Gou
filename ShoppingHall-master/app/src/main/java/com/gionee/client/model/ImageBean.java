package com.gionee.client.model;

public class ImageBean implements Comparable<ImageBean> {

    private String mTopImagePath;
    private String mFolderName;
    private int mImageCounts;
    private long mLastModifiyTime;

    public String getTopImagePath() {
        return mTopImagePath;
    }

    public void setTopImagePath(String topImagePath) {
        this.mTopImagePath = topImagePath;
    }

    public String getFolderName() {
        return mFolderName;
    }

    public void setFolderName(String folderName) {
        this.mFolderName = folderName;
    }

    public int getImageCounts() {
        return mImageCounts;
    }

    public void setImageCounts(int imageCounts) {
        this.mImageCounts = imageCounts;
    }

    public long getmLastModifiyTime() {
        return mLastModifiyTime;
    }

    public void setmLastModifiyTime(long mLastModifiyTime) {
        this.mLastModifiyTime = mLastModifiyTime;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ImageBean arg0) {
        if (this.getmLastModifiyTime() < arg0.getmLastModifiyTime()) {
            return 1;
        } else if (this.getmLastModifiyTime() == arg0.getmLastModifiyTime()) {
            return 0;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
//        LogUtils.log(TAG, LogUtils.getThreadName());
        if (o instanceof ImageBean && this.getmLastModifiyTime() == ((ImageBean) o).getmLastModifiyTime()) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
//        LogUtils.log(TAG, LogUtils.getThreadName());
        return (int) (this.getmLastModifiyTime() / 1000);
    }

}
