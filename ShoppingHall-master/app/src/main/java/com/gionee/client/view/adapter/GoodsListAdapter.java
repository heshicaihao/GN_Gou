/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-9-11 下午05:16:57
 */
package com.gionee.client.view.adapter;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.shoppingmall.AbstractBaseList;
import com.gionee.framework.operation.net.GNImageLoader;

/**
 * com.gionee.client.view.adapter.GoodsListAdapter
 * 
 * @author yangxiong <br/>
 * @date create at 2014-9-11 下午05:16:57
 * @description TODO 商品列表适配
 */
public class GoodsListAdapter extends AbstractMyfavoriteBaseAdapter {
    private static final String TAG = "GoodsListAdapter";

    public GoodsListAdapter(AbstractBaseList baseList, Context mContext) {
        super(baseList, mContext, R.layout.goods_item);
    }

    @Override
    protected Object initViewHolder(View convertView) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        final ViewHolder holder;
        holder = new ViewHolder();
        holder.mTitle = (TextView) convertView.findViewById(R.id.title);
        holder.mPrice = (TextView) convertView.findViewById(R.id.price);
        holder.mPlatform = (TextView) convertView.findViewById(R.id.shop_platform);
        holder.mMaskImage = (ImageView) convertView.findViewById(R.id.item_click_image);
        holder.mItemLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
        holder.mGoodsIcon = (ImageView) convertView.findViewById(R.id.goods_icon);
        holder.mDropDownImg = (ImageView) convertView.findViewById(R.id.drop_down_img);
        holder.mDropDownLayout = (RelativeLayout) convertView.findViewById(R.id.drop_down_layout);
        holder.mShareImg = (ImageView) convertView.findViewById(R.id.drop_down_share);
        holder.mDeleteImg = (ImageView) convertView.findViewById(R.id.drop_down_delete);
        return holder;
    }

    @Override
    protected void updateView(View convertView, final Object viewHolder, final JSONObject itemData,
            int position) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " position = " + position);
        ViewHolder holder = (ViewHolder) viewHolder;
        updateTextviewWithDefaultText(holder.mTitle, itemData, HttpConstants.Data.GoodsList.TITLE, 0);
        updateTextview(holder.mPrice, itemData, HttpConstants.Data.GoodsList.PRICE, R.string.sale_price);
        updateTextview(holder.mPlatform, itemData, HttpConstants.Data.GoodsList.SRC, 0);
        udpateGoodsIcon(viewHolder, itemData);
        updateMaskImage(holder.mMaskImage, holder.mItemLayout, itemData);
        udpateDropdownImage(holder.mDropDownImg, holder.mDropDownLayout, position);
        updateDropDownLayout(holder.mDropDownLayout, holder.mShareImg, holder.mDeleteImg,
                getThumbInfo(holder, itemData), itemData, 2);
    }

    private ThumbInfo getThumbInfo(final ViewHolder holder, final JSONObject itemData) {
        String imageUrl = itemData.optString(HttpConstants.Data.GoodsList.IMAGE);
        ThumbInfo info = new ThumbInfo(holder.mGoodsIcon, imageUrl);
        return info;
    }

    private void udpateGoodsIcon(final Object viewHolder, final JSONObject itemData) {
        ((ViewHolder) viewHolder).mGoodsIcon.setImageResource(R.drawable.comment_img_default);
        final String urlStr = itemData.optString(HttpConstants.Data.GoodsList.IMAGE);
        if (urlStr != null) {
            GNImageLoader.getInstance().loadBitmap(urlStr, ((ViewHolder) viewHolder).mGoodsIcon);
        }
    }

    private static class ViewHolder {
        /**
         * item 布局
         */
        public RelativeLayout mItemLayout;
        /**
         * 蒙板
         */
        public ImageView mMaskImage;

        /**
         * 下拉分享删除显示区
         */
        public RelativeLayout mDropDownLayout;

        /**
         * 商品图标
         */
        public ImageView mGoodsIcon;

        /**
         * 标题
         */
        public TextView mTitle;

        /**
         * 价格
         */
        public TextView mPrice;

        /**
         * 点商平台
         */
        public TextView mPlatform;

        /**
         * 更多下拉按钮
         */
        public ImageView mDropDownImg;

        /**
         * 分享
         */
        public ImageView mShareImg;
        /**
         * 删除
         */
        public ImageView mDeleteImg;
    }

    @Override
    protected void baiduStat(String type) {
        StatService.onEvent(mContext, BaiduStatConstants.CHECK_COLLECT, BaiduStatConstants.GOODS);
    }
}
