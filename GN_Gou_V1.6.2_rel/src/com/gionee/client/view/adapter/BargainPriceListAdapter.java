// Gionee <yangxiong><2013-12-23> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2013-12-23 下午05:46:03
 */
package com.gionee.client.view.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.operation.net.GNImageLoader;

/**
 * @author yangxiong <br/>
 * @date create at 2013-12-23 下午05:46:03
 * @description TODO 天天特价页面列表适配
 */
public class BargainPriceListAdapter extends BaseAdapter {
    private static final String TAG = "BargainPriceListAdapter";
    private Context mContext;
    private ArrayList<JSONObject> mBargainArrayList = new ArrayList<JSONObject>();
    private LayoutInflater mInflater;

    public BargainPriceListAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mBargainArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBargainArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.bargain_price_item, null);
            holder = initViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        updateView(holder, (JSONObject) getItem(position));

        return convertView;
    }

    /**
     * @param convertView
     * @return
     * @author yangxiong
     * @description TODO
     */
    private ViewHolder initViewHolder(View convertView) {
        ViewHolder holder;
        holder = new ViewHolder();
        holder.mItemImage = (ImageView) convertView.findViewById(R.id.item_click_image);
        holder.mDiscount = (TextView) convertView.findViewById(R.id.discount);
        holder.mTitle = (TextView) convertView.findViewById(R.id.title);
        holder.mCommodityIcon = (ImageView) convertView.findViewById(R.id.product_icon);
        holder.mMarketPrice = (TextView) convertView.findViewById(R.id.market_price);
        holder.mSalePrice = (TextView) convertView.findViewById(R.id.sale_monney);
        holder.mSource = (TextView) convertView.findViewById(R.id.from);
        return holder;
    }

    /**
     * @param jObject
     * @author yangxiong
     * @description TODO
     */
    public void addBargainPriceData(JSONObject jObject) {
        mBargainArrayList.add(jObject);
    }

    /**
     * 
     * @author yangxiong
     * @description TODO
     */
    public void clearRebateData() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mBargainArrayList.clear();
    }

    /**
     * @description TODO 更新商品信息
     */
    private void updateView(ViewHolder viewHolder, final JSONObject itemData) {
        updateTextview(viewHolder.mTitle, itemData, HttpConstants.Data.BargainPrice.TITLE, 0);
        updateTextview(viewHolder.mSource, itemData, HttpConstants.Data.BargainPrice.FROM, 0);
        updateTextview(viewHolder.mMarketPrice, itemData, HttpConstants.Data.BargainPrice.MARKET_PRICE,
                R.string.sale_price);
        updateTextview(viewHolder.mSalePrice, itemData, HttpConstants.Data.BargainPrice.SALE_PRICE,
                R.string.sale_price);
        updateDiscount(viewHolder, itemData);
        updateCommodityIcon(viewHolder.mCommodityIcon, itemData, HttpConstants.Data.BargainPrice.IMG);
        setItemLayoutListener(viewHolder, itemData);
        setMiddileHorizontal(viewHolder.mMarketPrice);
    }

    /**
     * @param textview
     * @author yangxiong
     * @description TODO 文本加中间横线
     */
    private void setMiddileHorizontal(TextView textview) {
        textview.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    /**
     * @param viewHolder
     * @param itemData
     * @author yangxiong
     * @description TODO
     */
    private void setItemLayoutListener(ViewHolder viewHolder, final JSONObject itemData) {
        viewHolder.mItemImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                String linkStr = itemData.optString(HttpConstants.Data.BargainPrice.LINK);
                gotoWebViewActvity(linkStr);
            }
        });
    }

    /**
     * @param viewHolder
     * @param itemData
     * @author yangxiong
     * @description TODO 更新折扣 低于3折不显示.
     */
    private void updateDiscount(ViewHolder viewHolder, final JSONObject itemData) {
        String discountString = itemData.optString(HttpConstants.Data.BargainPrice.DISCOUNT);
        if (checkDiscountExist(discountString)) {
            float discout = Float.parseFloat(discountString);
            if (discout <= 3) {
                String priceWithSign = "";
                priceWithSign = mContext.getString(R.string.discount, String.format("%.1f", discout));
                viewHolder.mDiscount.setText(priceWithSign);
                viewHolder.mDiscount.setVisibility(View.VISIBLE);
                return;
            }
        }
        viewHolder.mDiscount.setVisibility(View.GONE);
    }

    /**
     * @param discountString
     * @return
     * @author yangxiong
     * @description TODO
     */
    private boolean checkDiscountExist(String discountString) {
        return !TextUtils.isEmpty(discountString);
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
    private void updateTextview(TextView textview, final JSONObject itemData, String key, int stringResId) {
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
        textview.setText(priceWithSign);
    }

    /**
     * @description TODO 更新商品Icon
     */
    private void updateCommodityIcon(ImageView imageview, final JSONObject itemData, String key) {
        String urlStr;
        imageview.setImageResource(R.drawable.white);
        urlStr = itemData.optString(key);
        GNImageLoader.getInstance().loadBitmap(urlStr, imageview);
    }

    /**
     * @author yangxiong
     * @description TODO 启动web页
     */
    private void gotoWebViewActvity(String url) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "Link url: " + url);
        ((BaseFragmentActivity) mContext).gotoWebPage(url, true);

    }

//    private SpannableStringBuilder builtSpan(int strId, String content, ForegroundColorSpan color,
//            AbsoluteSizeSpan textSize) {
//        String infor = mContext.getString(strId, content);
//        SpannableStringBuilder style = new SpannableStringBuilder(infor);
//        style.setSpan(color, infor.indexOf(content), infor.indexOf(content) + content.length(),
//                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
////      style.setSpan(textSize, infor.indexOf(content),infor.indexOf(content)+content.length(),
////      Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
//        return style;
//    }

    private static class ViewHolder {

        /**
         * 项布局
         */
        public ImageView mItemImage;

        /**
         * 折扣
         */
        public TextView mDiscount;

        /**
         * 标题
         */
        public TextView mTitle;

        /**
         * 商品图片
         */
        public ImageView mCommodityIcon;

        /**
         * 市场价格
         */
        public TextView mMarketPrice;

        /**
         * 销售价格
         */
        public TextView mSalePrice;

        /**
         * 商品来源
         */
        public TextView mSource;

    }
}
//Gionee <yangxiong><2013-12-23> add for CR00850885 end