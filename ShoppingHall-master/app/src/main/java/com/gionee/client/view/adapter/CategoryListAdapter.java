package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
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

public class CategoryListAdapter extends BaseAdapter {

    private JSONArray mArray;
    private LayoutInflater mInflater;
    private int mSelectPosition = 0;
    private Context mContext;

    public CategoryListAdapter(Context context) {
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
            convertView = mInflater.inflate(R.layout.category_tabs_item, null);
            holder = new ViewHolder();
            holder.mIcon = (ImageView) convertView.findViewById(R.id.category_tabs_item_icon);
            holder.mTitle = (TextView) convertView.findViewById(R.id.category_tabs_item_text);
            holder.mSelectLayout = (RelativeLayout) convertView.findViewById(R.id.category_tabs_select_flag);
            holder.mFlag = (ImageView) convertView.findViewById(R.id.categrory_tabs_flag);
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
        holder.mTitle.setText(mObject.optString(HttpConstants.Response.CategoryTab.NAME_S));
        holder.mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.category_bg_nor));
        GNImageLoader.getInstance().loadBitmap(mObject.optString(HttpConstants.Response.CategoryTab.ICON_S),
                holder.mIcon);
        if (mSelectPosition == position) {
            holder.mSelectLayout.setBackgroundResource(R.drawable.category_tabs_item_select);
            holder.mTitle.setTextColor(mContext.getResources().getColor(R.color.category_tab_text_select));
            holder.mFlag.setVisibility(View.VISIBLE);
        } else {
            holder.mSelectLayout.setBackgroundResource(R.drawable.category_tabs_item_default);
            holder.mTitle.setTextColor(mContext.getResources().getColor(R.color.category_tab_text_default));
            holder.mFlag.setVisibility(View.GONE);
        }
    }

    public void setSelectItem(int position) {
        mSelectPosition = position;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView mTitle;
        public ImageView mIcon;
        public RelativeLayout mSelectLayout;
        public ImageView mFlag;
    }

}
