package com.gionee.client.activity.imageScan;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.ImageBean;
import com.gionee.client.view.widget.MyImageView;
import com.gionee.client.view.widget.MyImageView.OnMeasureListener;
import com.gionee.framework.operation.net.GNImageLoader;

public class ImageScanAdapter extends BaseAdapter {
    private Point mPoint = new Point(0, 0);

    private HashMap<Integer, Boolean> mSelectMap;
    private List<ImageBean> mImageList;
    protected LayoutInflater mInflater;
    private ISelectImageListener mSelectImageListener;
    private List<String> mSelectImageList;

    public ImageScanAdapter(Context context, List<String> list) {
        mSelectImageList = new ArrayList<String>();
        mInflater = LayoutInflater.from(context);
        mSelectMap = new HashMap<Integer, Boolean>();
        sortImageList(list);
    }

    public void sortImageList(List<String> list) {
        List<ImageBean> mSortImageList = new ArrayList<ImageBean>();
        for (String path : list) {
            File file = new File(path);
            ImageBean imageBean = new ImageBean();
            imageBean.setFolderName(file.getParent());
            imageBean.setImageCounts(1);
            imageBean.setmLastModifiyTime(file.lastModified());
            imageBean.setTopImagePath(path);
            mSortImageList.add(imageBean);
        }
        try {
            Collections.sort(mSortImageList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mImageList = mSortImageList;
    }

    @Override
    public int getCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageList == null ? "" : mImageList.get(position).getTopImagePath();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        String path = mImageList.get(position).getTopImagePath();
        LogUtils.log("image_path", path);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.scan_image_gird_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
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
        viewHolder.mImageView.setTag(path);
        setCheckChangeListener(position, viewHolder);
        setImageViewListener(viewHolder);
        viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
        GNImageLoader.getInstance().loadLocalBitmap(path, viewHolder.mImageView);
        return convertView;
    }

    private void setImageViewListener(final ViewHolder viewHolder) {
        viewHolder.mImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (viewHolder.mCheckBox.isChecked()) {
                    viewHolder.mCheckBox.setChecked(false);
                } else {
                    viewHolder.mCheckBox.setChecked(true);
                }
            }
        });
    }

    private void setCheckChangeListener(final int position, final ViewHolder viewHolder) {
        viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    mSelectMap.put(position, isChecked);
                    String path = mImageList.get(position).getTopImagePath();
                    if (isChecked) {
                        if (!mSelectImageList.contains(path)) {
                            mSelectImageList.add(path);
                        }
                    } else if (mSelectImageList.contains(path)) {
                        mSelectImageList.remove(path);
                    }
                    mSelectImageListener.onSelectImageChange(mSelectImageList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public List<String> getSelectItems() {
        return mSelectImageList;
    }

    public static class ViewHolder {
        public MyImageView mImageView;
        public CheckBox mCheckBox;
    }

    public interface ISelectImageListener {
        void onSelectImageChange(List<String> mImageList);

    }

    public void setmSelectImageListener(ISelectImageListener mSelectImageListener) {
        this.mSelectImageListener = mSelectImageListener;
    }
}
