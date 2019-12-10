// Gionee <yangxiong><2014-8-6> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-8-6 上午10:14:20
 */
package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gionee.client.R;

/**
 * com.gionee.client.view.adapter.ListBaseAdapter
 * 
 * @author yangxiong <br/>
 * @date create at 2014-8-6 上午10:14:20
 * @description TODO 列表适配器基类
 */
public abstract class AbstractListBaseAdapter extends BaseAdapter {

    protected JSONArray mCommentArray;
    protected Context mContext;
    protected LayoutInflater mInflater;
    private int mItemLayoutId;

    public AbstractListBaseAdapter(Context mContext, int itemLayoutId) {
        super();
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.mItemLayoutId = itemLayoutId;
    }

    public void setmCommentArray(JSONArray mCommentArray) {
        this.mCommentArray = mCommentArray;
    }

    public JSONArray getmCommentArray() {
        return mCommentArray;
    }

    public void clearCommentArray() {
        this.mCommentArray = null;
    }

    @Override
    public int getCount() {
        int count = mCommentArray == null ? 0 : mCommentArray.length();
        return count;
    }

    @Override
    public Object getItem(int position) {
        return mCommentArray == null ? null : mCommentArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mItemLayoutId, null);
            holder = initViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = convertView.getTag();
        }
        updateView(convertView, holder, (JSONObject) getItem(position), position);
        return convertView;
    }

    /**
     * @param textview
     * @param itemData
     * @param key
     * @param stringResId
     *            用于输入格式化字符串的情形，如¥%1$s。若没有可以传0.
     * @author yangxiong
     * @description TODO 更新价格显示的文本.
     */
    protected void updateTextview(TextView textview, final JSONObject itemData, String key, int stringResId) {
        try {
            String price = itemData.optString(key);
            if (TextUtils.isEmpty(price) || price.equals("null")) {
                price = "";
            }

            String priceWithSign = "";
            if (stringResId > 0) {
                priceWithSign = mContext.getString(stringResId, price);
            } else {
                priceWithSign = price;
            }
            textview.setText(priceWithSign != null ? priceWithSign : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void updateTextviewWithDefaultText(TextView textview, final JSONObject itemData, String key,
            int stringResId) {
        try {
            String price = itemData.optString(key);
            if (TextUtils.isEmpty(price) || price.equals("null")) {
                price = mContext.getString(R.string.not_known);
            }

            String priceWithSign = "";
            if (stringResId > 0) {
                priceWithSign = mContext.getString(stringResId, price);
            } else {
                priceWithSign = price;
            }
            textview.setText(priceWithSign != null ? priceWithSign : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void updateTextviewOnlyExist(TextView textview, final JSONObject itemData, String key,
            int stringResId) {
        String price = itemData.optString(key);
        if (TextUtils.isEmpty(price) || price.equals("null")) {
            textview.setVisibility(View.GONE);
            return;
        }

        String priceWithSign = "";
        if (stringResId > 0) {
            priceWithSign = mContext.getString(stringResId, price);
        } else {
            priceWithSign = price;
        }
        textview.setText(priceWithSign != null ? priceWithSign : "");
        textview.setVisibility(View.VISIBLE);
    }

    /**
     * 在具体类中，要定义一个ViewHolder的内部类；Object 实际上是ViewHolder的类型.
     */
    protected abstract Object initViewHolder(final View convertView);

    protected abstract void updateView(View convertView, Object viewHolder, final JSONObject itemData,
            int position);

    public static class ThumbInfo {
        ImageView mThumbView;
        String mImageUrl;

        public ThumbInfo(ImageView thumbView, String url) {
            super();
            this.mThumbView = thumbView;
            this.mImageUrl = url;
        }

        public ImageView getmThumbView() {
            return mThumbView;
        }

        public void setmThumbView(ImageView mThumbView) {
            this.mThumbView = mThumbView;
        }

        public String getmUrl() {
            return mImageUrl;
        }

        public void setmUrl(String mUrl) {
            this.mImageUrl = mUrl;
        }
    }
}