/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-12-11 下午07:07:16
 */
package com.gionee.client.view.adapter;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.operation.net.GNImageLoader;

/**
 * @author yangxiong <br/>
 * @date create at 2014-12-11 下午07:07:16
 * @description TODO 同款列表适配
 */
public class SameStyleAdapter extends AbstractListBaseAdapter {
    private static final String TAG = "SameStyleAdapter";

    public SameStyleAdapter(Context mContext, int itemLayoutId) {
        super(mContext, itemLayoutId);
    }

    @Override
    protected Object initViewHolder(View convertView) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        final ViewHolder holder;
        holder = new ViewHolder();
        holder.mThumb = (ImageView) convertView.findViewById(R.id.thumb);
        holder.mCurrentStyleIcon = (ImageView) convertView.findViewById(R.id.current_style_icon);
        holder.mTitle = (TextView) convertView.findViewById(R.id.title);
        holder.mPrice = (TextView) convertView.findViewById(R.id.price);
        holder.mExpressMethod = (TextView) convertView.findViewById(R.id.express_method);
        holder.mPriceLevel = (TextView) convertView.findViewById(R.id.price_level);
        holder.mSalesVolume = (TextView) convertView.findViewById(R.id.sales_volume);
        holder.mSalesLevel = (TextView) convertView.findViewById(R.id.sales_level);
        holder.mScoreValue = (TextView) convertView.findViewById(R.id.score_value);
        holder.mArea = (TextView) convertView.findViewById(R.id.area);
        holder.mPlatform = (TextView) convertView.findViewById(R.id.platform);
        holder.mMenu = (ImageView) convertView.findViewById(R.id.menu);

        return holder;
    }

    @Override
    protected void updateView(View convertView, Object viewHolder, JSONObject itemData, int position) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        ViewHolder holder = (ViewHolder) viewHolder;
        GNImageLoader.getInstance().loadBitmap(itemData.optString(HttpConstants.Data.SameStyleInfo.IMAGE),
                (ImageView) holder.mThumb);
        updateCurrentStyleIcon(itemData, holder);
        updateTextviewWithDefaultText(holder.mTitle, itemData, HttpConstants.Data.SameStyleInfo.TITLE, 0);
        updateTextview(holder.mPrice, itemData, HttpConstants.Data.SameStyleInfo.PRICE, R.string.sale_price);
        updateTextview(holder.mExpressMethod, itemData, HttpConstants.Data.SameStyleInfo.EXPRESS_METHOD, 0);
        updateTextviewOnlyExist(holder.mPriceLevel, itemData, HttpConstants.Data.SameStyleInfo.PRICE_LEVEL, 0);
        updateTextview(holder.mSalesVolume, itemData, HttpConstants.Data.SameStyleInfo.SALES_VOLUME, 0);
        updateTextviewOnlyExist(holder.mSalesLevel, itemData, HttpConstants.Data.SameStyleInfo.SALES_LEVEL, 0);
        updateScoreValue(holder.mScoreValue, itemData, HttpConstants.Data.SameStyleInfo.SCORE);
        updateTextview(holder.mArea, itemData, HttpConstants.Data.SameStyleInfo.AREA, 0);
        updateTextview(holder.mPlatform, itemData, HttpConstants.Data.SameStyleInfo.CHANNEL, 0);
        setSameStyleMenuListener(convertView, holder, itemData);
    }

    /**
     * @param itemData
     * @param holder
     * @author yangxiong
     * @description TODO
     */
    private void updateCurrentStyleIcon(JSONObject itemData, ViewHolder holder) {
        boolean isCurrentStyle = itemData.optBoolean(HttpConstants.Data.SameStyleInfo.IS_CURRENT);
        holder.mCurrentStyleIcon.setVisibility(isCurrentStyle ? View.VISIBLE : View.GONE);
    }

    private void updateScoreValue(TextView textview, final JSONObject itemData, String key) {
        String price = itemData.optString(key);
        if (TextUtils.isEmpty(price) || price.equals("null")) {
            price = mContext.getString(R.string.score);
        }
        textview.setText(price);
    }

    /**
     * @param itemData
     * @param holder
     * @author yangxiong
     * @description TODO
     */
    private void updateStarLevel(JSONObject itemData, ViewHolder holder) {
        holder.mStarLevelLayout = (LinearLayout) holder.mDetailLayout.findViewById(R.id.star_level_layout);
        holder.mStarLevelLayout.removeAllViews();
        int starLevel = itemData.optInt(HttpConstants.Data.SameStyleInfo.STAR_LEVEL);
        String crownUrl = itemData.optString(HttpConstants.Data.SameStyleInfo.STAR_IMAGE);
        for (int i = 0; i < starLevel; i++) {
            ImageView crown = new ImageView(mContext);
            GNImageLoader.getInstance().loadBitmap(crownUrl, crown);
            holder.mStarLevelLayout.addView(crown);
        }
    }

    private void setSameStyleMenuListener(final View convertView, final ViewHolder holder,
            final JSONObject itemData) {
        holder.mMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (holder.mDetailLayout != null) {
                    LogUtils.logd(TAG, LogUtils.getThreadName() + " mDetailLayout  != null ");
                    contrastMenuAnimationIn(holder);
                    return;
                }

                ViewStub stub = (ViewStub) convertView.findViewById(R.id.same_style_menu_layout);
                if (stub == null) {
                    LogUtils.logd(TAG, LogUtils.getThreadName() + " stub = null");
                    return;
                }
                holder.mDetailLayout = stub.inflate();
                contrastMenuAnimationIn(holder);
                holder.mShopName = (TextView) holder.mDetailLayout.findViewById(R.id.shop_name);
                holder.mDescription = (TextView) holder.mDetailLayout.findViewById(R.id.description_value);
                holder.mSevice = (TextView) holder.mDetailLayout.findViewById(R.id.service_value);
                holder.mLogistics = (TextView) holder.mDetailLayout.findViewById(R.id.logistics_value);
                holder.mClose = (ImageView) holder.mDetailLayout.findViewById(R.id.close);
                holder.mClose.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        LogUtils.log(TAG, LogUtils.getThreadName());
                        if (holder.mDetailLayout != null) {
                            contrastMenuAnimationOut(holder);
                        }
                    }
                });

                updateStarLevel(itemData, holder);
                updateTextview(holder.mShopName, itemData, HttpConstants.Data.SameStyleInfo.SHOP_NAME, 0);
                updateTextview(holder.mDescription, itemData, HttpConstants.Data.SameStyleInfo.DESCRIPTION, 0);
                updateTextview(holder.mSevice, itemData, HttpConstants.Data.SameStyleInfo.SERVICE, 0);
                updateTextview(holder.mLogistics, itemData, HttpConstants.Data.SameStyleInfo.LOGISTICS, 0);
            }

            private void contrastMenuAnimationOut(final ViewHolder holder) {
                holder.mDetailLayout.clearAnimation();
                final Animation anim = AnimationUtils.loadAnimation(mContext,
                        R.anim.slide_out_to_bottom_short_ani);
                anim.setFillAfter(false);
                holder.mDetailLayout.startAnimation(anim);
                holder.mDetailLayout.setVisibility(View.GONE);
            }

            private void contrastMenuAnimationIn(final ViewHolder holder) {
                holder.mDetailLayout.clearAnimation();
                final Animation anim = AnimationUtils.loadAnimation(mContext,
                        R.anim.slide_in_from_bottom_short_ani);
                anim.setFillAfter(false);
                holder.mDetailLayout.startAnimation(anim);
                holder.mDetailLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private static class ViewHolder {
        public ImageView mCurrentStyleIcon;
        public ImageView mThumb;
        public TextView mTitle;
        public TextView mPrice;
        public TextView mExpressMethod;
        public TextView mPriceLevel;
        public TextView mSalesVolume;
        public TextView mSalesLevel;
        public TextView mScoreValue;
        public TextView mArea;
        public TextView mPlatform;
        public ImageView mMenu;
        public View mDetailLayout;
        public TextView mShopName;
        public LinearLayout mStarLevelLayout;
        public TextView mDescription; // 描述相符度
        public TextView mSevice; // 服务等级
        public TextView mLogistics; // 物流等级
        public ImageView mClose;
    }
}
