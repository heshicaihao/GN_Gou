//Gionee <wangyy><2014-03-12> modify for CR00956169 begin
package com.gionee.client.business.statistic.manager;

/**
 * 用于存储事件数据，即Session,activity,自定义时间,exception四类.
 */
public class EventDataInfos {

    /**
     * 存储事件数据（含多条）
     */
    private byte[] mEventDataArray;

    /**
     * 事件数据的条数, 由于协议里用2个Byte表示条数，因此取值范围为 -32768 至 32767； 目前4类事件数据，每类的条数配置为最大5000条数， 若修改配置条数，可能会造成此处溢出.
     */
    private short mEventCount;

    public EventDataInfos() {
    }

    public EventDataInfos(byte[] mEventDataArray, short mEventCount) {
        super();
        this.mEventDataArray = mEventDataArray;
        this.mEventCount = mEventCount;
    }

    public byte[] getmEventDataArray() {
        return mEventDataArray;
    }

    public void setmEventDataArray(byte[] mEventDataArray) {
        this.mEventDataArray = mEventDataArray;
    }

    public short getmEventCount() {
        return mEventCount;
    }

    public void setmEventCount(short mEventCount) {
        this.mEventCount = mEventCount;
    }
}
//Gionee <wangyy><2014-03-12> modify for CR00956169 end