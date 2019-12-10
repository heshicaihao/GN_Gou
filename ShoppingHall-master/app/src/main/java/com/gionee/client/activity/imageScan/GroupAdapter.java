package com.gionee.client.activity.imageScan;

import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.model.ImageBean;
import com.gionee.client.view.widget.MyImageView;
import com.gionee.client.view.widget.MyImageView.OnMeasureListener;
import com.gionee.framework.operation.net.GNImageLoader;

public class GroupAdapter extends BaseAdapter {
    private List<ImageBean> mImageFolderlist;
    private Point mPoint = new Point(0, 0);
    protected LayoutInflater mInflater;

    @Override
    public int getCount() {
        return mImageFolderlist == null ? 0 : mImageFolderlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageFolderlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public GroupAdapter(Context context, List<ImageBean> list) {
        this.mImageFolderlist = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        ImageBean mImageBean = mImageFolderlist.get(position);
        String path = mImageBean.getTopImagePath();
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.scan_folder_grid_item, null);
            viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.group_image);
            viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_title);
            viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);
            viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {

                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }
        viewHolder.mTextViewTitle.setText(mImageBean.getFolderName());
        viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean.getImageCounts()));
        viewHolder.mImageView.setTag(path);
        GNImageLoader.getInstance().loadLocalBitmap(path, viewHolder.mImageView);
        return convertView;
    }

    public static class ViewHolder {
        public MyImageView mImageView;
        public TextView mTextViewTitle;
        public TextView mTextViewCounts;
    }

}
