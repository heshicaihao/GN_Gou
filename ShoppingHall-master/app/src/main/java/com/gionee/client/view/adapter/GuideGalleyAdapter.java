package com.gionee.client.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.gionee.client.R;

public class GuideGalleyAdapter extends BaseAdapter {
    private Context mContext;
    private int[] mImageList;

    public GuideGalleyAdapter(Context mContext, int[] imageList) {
        super();
        this.mContext = mContext;
        this.mImageList = imageList;
    }

    @Override
    public int getCount() {
        return mImageList == null ? 0 : mImageList.length;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(mContext);
            convertView.setBackgroundResource(R.drawable.advertise_defaut_bg);
            convertView.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT,
                    Gallery.LayoutParams.MATCH_PARENT));
            ((ImageView) convertView).setScaleType(ScaleType.CENTER_CROP);
        }
        ((ImageView) convertView).setImageResource(mImageList[position]);
        return convertView;
    }

}
