// Gionee <yuwei><2013-12-27> add for CR00821559 begin
/*
 * AdvertiseGalleryAdapter.java
 * classes : com.gionee.client.view.adapter.AdvertiseGalleryAdapter
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-27 上午10:34:12
 */
package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.gionee.client.R;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.operation.net.GNImageLoader;

/**
 * AdvertiseGalleryAdapter
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-27 上午10:34:12
 * @description TODO
 */
public class AdvertiseGalleryAdapter extends BaseAdapter {
    private JSONArray mAdvertiseList;
    private Context mContext;

    public AdvertiseGalleryAdapter(JSONArray mAdvertiseList, Context mContext) {
        super();
        this.mAdvertiseList = mAdvertiseList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if (mAdvertiseList == null || mAdvertiseList.length() == 0) {
            return 0;
        }
        if (mAdvertiseList.length() == 1) {
            return 1;
        }

        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        int index = 0;
        try {
            index = position % mAdvertiseList.length();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mAdvertiseList.optJSONObject(index);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject jsonData;
        int index = 0;
        try {
            index = position % mAdvertiseList.length();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        jsonData = mAdvertiseList.optJSONObject(index);
        if (convertView == null) {
            convertView = new ImageView(mContext);
            convertView.setBackgroundResource(R.drawable.advertise_defaut_bg);
            convertView.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT,
                    Gallery.LayoutParams.MATCH_PARENT));
            ((ImageView) convertView).setScaleType(ScaleType.CENTER_CROP);
        }
        setImage(convertView, jsonData);
        return convertView;
    }

    /**
     * @param convertView
     * @param jsonData
     * @author yuwei
     * @description TODO
     */
    private void setImage(View convertView, JSONObject jsonData) {
        if (jsonData != null) {
            GNImageLoader.getInstance()
                    .loadBitmap(jsonData.optString(HttpConstants.Response.AdvertiseBanner.IMG_S),
                            (ImageView) convertView);
        }
    }
}
