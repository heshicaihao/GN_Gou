package com.gionee.client.view.adapter;

import org.json.JSONArray;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gionee.client.R;

public class SearchAdapter extends BaseAdapter {
    private static final int NUM_TOP_HOT = 3;
    private Context mContext;
    private JSONArray mJsonArray;

    public SearchAdapter(Context context) {
        mContext = context;
    }

    public void setData(JSONArray jsonArray) {
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mJsonArray != null ? mJsonArray.length() : 0);
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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_list_item, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mMarkTextView = (Button) convertView.findViewById(R.id.tv_mark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position < NUM_TOP_HOT) {
            holder.mMarkTextView.setBackgroundResource(R.drawable.mark_light);
        } else {
            holder.mMarkTextView.setBackgroundResource(R.drawable.mark_dark);
        }

        holder.mMarkTextView.setText(String.valueOf(position + 1));
        holder.mTextView.setText(mJsonArray.optString(position));
        return convertView;
    }

    private static class ViewHolder {
        public TextView mTextView;
        public Button mMarkTextView;
    }
}
