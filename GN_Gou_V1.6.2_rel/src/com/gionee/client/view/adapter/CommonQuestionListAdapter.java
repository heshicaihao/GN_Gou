package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gionee.client.R;
import com.gionee.client.model.HttpConstants;

public class CommonQuestionListAdapter extends BaseAdapter {

    private JSONArray mArray;
    private LayoutInflater mInflater;

    public CommonQuestionListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(JSONArray jsonArray) {
        mArray = jsonArray;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mArray != null ? mArray.length() : 0);
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.common_question_item, null);
            holder = new ViewHolder();
            holder.mTitle = (TextView) convertView.findViewById(R.id.common_question_item_text);
            holder.mPosition = (TextView) convertView.findViewById(R.id.common_question_item_position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject mObject = mArray.optJSONObject(position);
        holder.mTitle.setText(mObject.optString(HttpConstants.Data.CommonQuestionList.QUESTION_QUESTION_S));
        holder.mPosition.setText(position + 1 + "");
        return convertView;
    }

    private static class ViewHolder {
        public TextView mTitle;
        public TextView mPosition;
    }

}
