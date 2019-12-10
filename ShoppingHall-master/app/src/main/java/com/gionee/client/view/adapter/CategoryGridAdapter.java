package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.operation.net.GNImageLoader;

public class CategoryGridAdapter extends BaseAdapter {

    private JSONArray mArray;
    private LayoutInflater mInflater;

    public CategoryGridAdapter(Context context) {
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
            convertView = mInflater.inflate(R.layout.category_gride_item, null);
            holder = new ViewHolder();
            holder.mIcon = (ImageView) convertView.findViewById(R.id.category_icon);
            holder.mTitle = (TextView) convertView.findViewById(R.id.category_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject object = mArray.optJSONObject(position);
        holder.mTitle.setText(object.optString(HttpConstants.Response.Category.NAME_S));
        GNImageLoader.getInstance().loadBitmap(object.optString(HttpConstants.Response.Category.IMG_S),
                holder.mIcon);
        return convertView;
    }

    private static class ViewHolder {
        public TextView mTitle;
        public ImageView mIcon;
    }

}
