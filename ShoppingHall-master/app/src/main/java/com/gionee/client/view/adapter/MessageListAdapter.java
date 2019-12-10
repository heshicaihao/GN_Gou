package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.myfavorites.StoryDetailActivity;
import com.gionee.client.activity.question.QuestionDetailActivity;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Constants.PAGE_TAG;

public class MessageListAdapter extends BaseAdapter implements OnItemClickListener {
    private JSONArray mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private static final String TAG = "MessageListAdapter";

    public MessageListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void updateData(JSONArray array) {
        mData = array;
    }

    @Override
    public int getCount() {
        return (mData != null ? mData.length() : 0);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.messages_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mTpye = (TextView) convertView.findViewById(R.id.messages_lable);
            viewHolder.mMsg = (TextView) convertView.findViewById(R.id.messages_msg);
            viewHolder.mDes = (TextView) convertView.findViewById(R.id.messages_des);
            viewHolder.mTime = (TextView) convertView.findViewById(R.id.messages_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        updateItemView(position, viewHolder);
        return convertView;
    }

    private void updateItemView(int position, ViewHolder viewHolder) {
        JSONObject object = mData.optJSONObject(position);
        viewHolder.mTpye.setText(object.optString(HttpConstants.Data.MessageList.LABEL_S));
        viewHolder.mMsg.setText(object.optString(HttpConstants.Data.MessageList.MESSAGE_S));
        viewHolder.mDes.setText(object.optString(HttpConstants.Data.MessageList.DESCRIPTION_S));
        viewHolder.mTime.setText(object.optString(HttpConstants.Data.MessageList.TIME_S));
        setLabelBackground(viewHolder, object.optInt(HttpConstants.Data.MessageList.CATE_I));

    }

    private void setLabelBackground(ViewHolder viewHolder, int type) {

        switch (type) {
            case Lable.ANSWER:
                viewHolder.mTpye.setBackgroundResource(R.drawable.messages_answer);
                break;
            case Lable.TATLE:
                viewHolder.mTpye.setBackgroundResource(R.drawable.messages_tale);
                break;

            default:
                break;
        }
    }

    private static class ViewHolder {
        TextView mTpye;
        TextView mMsg;
        TextView mDes;
        TextView mTime;
    }

    static class Lable {
        public static final int ANSWER = 2;
        public static final int TATLE = 1;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject object = mData.optJSONObject(position - 1);
        int type = object.optInt(HttpConstants.Data.MessageList.CATE_I);
        Intent intent = new Intent();
        switch (type) {
            case Lable.ANSWER:
                intent.putExtra(HttpConstants.Response.ID_I,
                        object.optString(HttpConstants.Data.MessageList.TRUE_ID));
                intent.setClass(mContext, QuestionDetailActivity.class);
                break;
            case Lable.TATLE:

                intent.setClass(mContext, StoryDetailActivity.class);
                intent.putExtra(StatisticsConstants.KEY_INTENT_URL,
                        object.optString(HttpConstants.Data.MessageList.URL_S));
                intent.putExtra("id", object.optInt(HttpConstants.Data.MessageList.TRUE_ID));
                intent.putExtra("is_favorite",
                        object.optBoolean(HttpConstants.Data.MessageList.IS_FAVORITE_B));
                intent.putExtra("fav_id", object.optInt(HttpConstants.Data.MessageList.FAV_ID_I));
                intent.putExtra("comment_count", object.optString(HttpConstants.Data.MessageList.COMMENT_I));
                intent.putExtra("praise_count", object.optString(HttpConstants.Data.CommentsList.LIKE));
                intent.putExtra(StatisticsConstants.PAGE_TAG, PAGE_TAG.STORY);
                intent.putExtra(Constants.IS_SHOW_WEB_FOOTBAR, false);
                break;
            default:
                break;
        }

        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StatService.onEvent(mContext, BaiduStatConstants.IN_CLICK, BaiduStatConstants.IN_CLICK);
    }
}
