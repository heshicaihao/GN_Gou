/*
 Copyright 2013 Tonic Artos

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.gionee.client.view.adapter;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.activity.tabFragment.CategoryFragment.CategoryItemData;
import com.gionee.client.business.util.LogUtils;
import com.gionee.framework.operation.net.GNImageLoader;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

/**
 * @author yangxiong <br/>
 * @date create at 2014-10-29 上午09:50:14
 */
public class CategoryStickyGridHeadersAdapter<T> extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {
    protected static final String TAG = CategoryStickyGridHeadersAdapter.class.getSimpleName();

    private int mHeaderResId;

    private LayoutInflater mInflater;

    private int mItemResId;

    private List<T> mItems;

    public CategoryStickyGridHeadersAdapter(Context context, int headerResId, int itemResId) {
        init(context, null, headerResId, itemResId);
    }

    public CategoryStickyGridHeadersAdapter(Context context, List<T> items, int headerResId, int itemResId) {
        init(context, items, headerResId, itemResId);
    }

    public CategoryStickyGridHeadersAdapter(Context context, T[] items, int headerResId, int itemResId) {
        init(context, Arrays.asList(items), headerResId, itemResId);
    }

    public void setData(List<T> itemsData) {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        mItems = itemsData;
        notifyDataSetChanged();
    }

    /**
     * 
     * @author yangxiong
     * @description TODO
     */
    public List<T> getData() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return mItems;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getCount() {
        int count = mItems != null ? mItems.size() : 0;
//        LogUtils.log(TAG, LogUtils.getThreadName() + " count = " + count); 
        return count;
    }

    @Override
    public long getHeaderId(int position) {
        T item = getItem(position);
        CategoryItemData value = null;
        if (item instanceof CategoryItemData) {
            value = (CategoryItemData) item;
        } else {
//            value = item.toString();
        }
        if (value != null) {
            return value.mPartitionId;
        } else {
            return 0;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
//        LogUtils.log(TAG, LogUtils.getThreadName() + " position = " + position);
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mHeaderResId, parent, false);
            holder = new HeaderViewHolder();
            holder.mHeaderTitle = (TextView) convertView.findViewById(R.id.sticky_title);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        bindData(position, holder);

        return convertView;
    }

    /**
     * @param position
     * @param holder
     * @author yuwei
     * @description TODO
     */
    private void bindData(int position, HeaderViewHolder holder) {
        try {
            T item = getItem(position);
            CategoryItemData itemData = null;
            if (item instanceof CategoryItemData) {
                itemData = (CategoryItemData) item;
            } else {
//            jsonObject = item.toString();
            }
            if (itemData == null) {
                return;
            }
            // set header text as first char in string
            if (!TextUtils.isEmpty(itemData.mCategoryTitle)) {
                holder.mHeaderTitle.setText(itemData.mCategoryTitle);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
//        LogUtils.log(TAG, LogUtils.getThreadName() + " position " + position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mItemResId, parent, false);
            holder = new ViewHolder();
            holder.mCategory = (TextView) convertView.findViewById(R.id.category_text);
            holder.mIcon = (ImageView) convertView.findViewById(R.id.category_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        T item = getItem(position);
        if (item instanceof CategoryItemData) {
            CategoryItemData itemData = (CategoryItemData) item;
            holder.mCategory.setText(itemData.mName);
            holder.mIcon.setImageBitmap(null);
            GNImageLoader.getInstance().loadBitmap(itemData.mImage, holder.mIcon);
        } else {
            holder.mCategory.setText(item.toString());
        }

        return convertView;
    }

    private void init(Context context, List<T> items, int headerResId, int itemResId) {
        this.mItems = items;
        this.mHeaderResId = headerResId;
        this.mItemResId = itemResId;
        mInflater = LayoutInflater.from(context);
    }

    protected class HeaderViewHolder {
        public TextView mHeaderTitle;
    }

    protected static class ViewHolder {
        public TextView mCategory;
        public ImageView mIcon;
    }

}
