package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.operation.net.GNImageLoader;

public class QuestionCategoryListAdapter extends BaseAdapter {

    private JSONArray mArray;
    private LayoutInflater mInflater;
    private int mSelectPosition = 0;
    private Context mContext;

    public QuestionCategoryListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.category_question_item, null);
            holder = new ViewHolder();
            holder.mIcon = (ImageView) convertView.findViewById(R.id.category_item_icon);
            holder.mTitle = (TextView) convertView.findViewById(R.id.category_item_text);
            holder.mCategoryTabsSelectFlag = (RelativeLayout) convertView
                    .findViewById(R.id.category_tabs_select_flag);
            holder.mCategoryItemRightLine = convertView.findViewById(R.id.category_item_right_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject mObject = mArray.optJSONObject(position);
        if (mObject == null) {
            return convertView;
        }
        updateView(position, holder, mObject);
        return convertView;
    }

    private void updateView(int position, ViewHolder holder, JSONObject mObject) {
        holder.mTitle.setText(mObject.optString(HttpConstants.Data.QuestionCatagoryList.QUESTION_NAME_S));
        holder.mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.category_bg_nor));
        GNImageLoader.getInstance().loadBitmap(
                mObject.optString(HttpConstants.Data.QuestionCatagoryList.QUESTION_ICON_S), holder.mIcon);
        if (mSelectPosition == position) {
            holder.mCategoryTabsSelectFlag.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.mCategoryItemRightLine.setVisibility(View.GONE);
        } else {
            holder.mCategoryTabsSelectFlag.setBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.mCategoryItemRightLine.setVisibility(View.VISIBLE);
        }
    }

    public void setSelectItem(int position) {
        mSelectPosition = position;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView mTitle;
        public ImageView mIcon;
        public RelativeLayout mCategoryTabsSelectFlag;
        public View mCategoryItemRightLine;
    }

}
