//Gionee <yangxiong><2014-03-12> modify for CR00850885 begin
package com.gionee.client.business.statistic.events;

import android.content.ContentValues;

import com.gionee.client.business.statistic.database.DBFields;
import com.gionee.framework.operation.utills.Utils;

public class AppEvent extends BaseEvent {

    private String mEventId;
    private String mEventLabel = "";
    private long mOccurtime;
    private String mParamap;

    public AppEvent(String eventId, String eventLabel, long occurtime, String paramap) {
        super();
        this.mEventId = eventId;
        if (Utils.isNull(eventLabel)) {
            eventLabel = "";
        }
        this.mEventLabel = eventLabel;
        this.mOccurtime = occurtime;
        if (Utils.isNull(paramap)) {
            paramap = "";
        }
        this.mParamap = paramap;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(DBFields.EVENT_ID, mEventId);
        values.put(DBFields.EVENT_LABEL, mEventLabel);
        values.put(DBFields.OCCUR_TIME, mOccurtime);
        values.put(DBFields.PARA_MAP, mParamap);
        return values;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" ");
        sb.append("eventId:" + mEventId);
        sb.append(",eventLabel:" + mEventLabel);
        sb.append(",occurtime:" + mOccurtime);
        sb.append(",paramap:" + mParamap);
        sb.append(" ");
        return sb.toString();
    }

    public long getOccurTime() {
        return mOccurtime;
    }

    public String getEventId() {
        return mEventId;
    }
}
//Gionee <yangxiong><2014-03-12> modify for CR00850885 end