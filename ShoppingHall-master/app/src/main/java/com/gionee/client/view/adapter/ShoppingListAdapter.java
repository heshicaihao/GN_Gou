/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-9-11 下午05:16:57
 */
package com.gionee.client.view.adapter;

import java.util.Map.Entry;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.myfavorites.MyFavoritesActivity;
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
 * @description TODO 购物列表适配
 */
public class ShoppingListAdapter extends AbstractMyfavoriteBaseAdapter {
    private static final String TAG = "ShoppingListAdapter";

    public ShoppingListAdapter(AbstractBaseList baseList, Context mContext) {
        super(baseList, mContext, R.layout.shopping_item);
    }

    @Override
    protected Object initViewHolder(View convertView) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        final ViewHolder holder;
        holder = new ViewHolder();
        holder.mCategory = (TextView) convertView.findViewById(R.id.category);
        holder.mTitle = (TextView) convertView.findViewById(R.id.title);
        holder.mPrice = (TextView) convertView.findViewById(R.id.price);
        holder.mPlatform = (TextView) convertView.findViewById(R.id.shop_platform);
        holder.mReducePrice = (TextView) convertView.findViewById(R.id.reduce_price);
        holder.mMaskImage = (ImageView) convertView.findViewById(R.id.item_click_image);
        holder.mItemLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
        holder.mGoodsIcon = (ImageView) convertView.findViewById(R.id.goods_icon);
        holder.mSingleSelectionLayout = (RelativeLayout) convertView
                .findViewById(R.id.single_selection_layout);
        holder.mSingleSelectionCheckBox = (CheckBox) convertView
                .findViewById(R.id.single_selection_check_box);
        return holder;
    }

    @Override
    protected void updateView(View convertView, final Object viewHolder, final JSONObject itemData,
            int position) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " position = " + position + ", itemData = " + itemData);
        if (itemData == null) {
            return;
        }
        ViewHolder holder = (ViewHolder) viewHolder;
        updateTextviewOnlyExist(holder.mCategory, itemData, HttpConstants.Data.ShoppingList.TYPE_NAME, 0);
        updateCategoryBackground(holder.mCategory, itemData);
        updateTextviewWithDefaultText(holder.mTitle, itemData, HttpConstants.Data.ShoppingList.TITLE, 0);
        updateTextviewOnlyExist(holder.mPrice, itemData, HttpConstants.Data.ShoppingList.PRICE,
                R.string.sale_price);
        updateTextview(holder.mPlatform, itemData, HttpConstants.Data.ShoppingList.SRC, 0);
        updateTextviewOnlyExist(holder.mReducePrice, itemData, HttpConstants.Data.ShoppingList.REDUCE, 0);
        udpateGoodsIcon(viewHolder, itemData);
        updateMaskImage(holder.mMaskImage, holder.mItemLayout, itemData);
//        udpateDropdownImage(holder.mDropDownImg, holder.mDropDownLayout, position);
//        setContrastMenuListener(convertView, holder, itemData, position);
        updateSingleSelectionCheckbox(holder, position);
    }

    private void updateSingleSelectionCheckbox(final ViewHolder holder, final int position) {
        int visibiity = View.GONE;
        int originalVisibility = holder.mSingleSelectionLayout.getVisibility();
        if (mFavoriteMode == FavoriteMode.MULTI_SELECT_DELETE) {
            visibiity = View.VISIBLE;
            processFavoriteMode(holder, position, visibiity, originalVisibility);
        } else {
            processCommonMode(holder, visibiity, originalVisibility);
        }
        holder.mSingleSelectionLayout.setVisibility(visibiity);
    }

    private void processCommonMode(final ViewHolder holder, int visibiity, int originalVisibility) {
        holder.mMaskImage.setVisibility(View.VISIBLE);
        if (originalVisibility != visibiity) {
            singleSelectionAnimationOut(holder.mSingleSelectionLayout);
        }
    }

    private void processFavoriteMode(final ViewHolder holder, final int position, int visibiity,
            int originalVisibility) {
        holder.mMaskImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    return;
                }
                boolean checked = holder.mSingleSelectionCheckBox.isChecked();
                holder.mSingleSelectionCheckBox.setChecked(!checked);
            }
        });
        if (originalVisibility != visibiity) {
            singleSelectionAnimationIn(holder.mSingleSelectionLayout);
        }
        setCheckboxChangeListener(holder, position);
        boolean checked = false;
        try {
            checked = mSingleSelectRecord.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.mSingleSelectionCheckBox.setChecked(checked);
    }

    private void setCheckboxChangeListener(final ViewHolder holder, final int position) {
        holder.mSingleSelectionCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSingleSelectRecord.put(position, isChecked);
                int count = 0;
                for (Entry<Integer, Boolean> entry : mSingleSelectRecord.entrySet()) {
                    boolean checked = entry.getValue();
                    if (checked) {
                        count++;
                    }
                }
                ((MyFavoritesActivity) mContext).getSingleSelectDeleteListener().onChange(count);
            }
        });
    }

    /**
     * type: 1 知物 / 2 商品 / 3 店铺/ 4 网页
     */
    protected void updateCategoryBackground(TextView textview, final JSONObject itemData) {
        try {
            int type = itemData.optInt(HttpConstants.Data.ShoppingList.TYPE);
            int resId = R.drawable.platform_label_bg;
            boolean isShow = false;
            switch (type) {
                case 2:
                    resId = R.drawable.goods_label_bg;
                    isShow = true;
                    break;
                case 3:
                    resId = R.drawable.shop_label_bg;
                    isShow = true;
                    break;
                case 4:
                    resId = R.drawable.platform_label_bg;
                    break;
                default:
                    break;
            }
            textview.setBackgroundResource(resId);
            textview.setVisibility(isShow ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
         * 商品图标
         */
        public ImageView mGoodsIcon;

        /**
         * 分类
         */
        public TextView mCategory;

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
         * 点商平台
         */
        public TextView mReducePrice;

        /**
         * 单选区域
         */
        public RelativeLayout mSingleSelectionLayout;

        /**
         * 单选checkbox
         */
        public CheckBox mSingleSelectionCheckBox;
    }

    @Override
    protected void baiduStat(String type) {
        StatService.onEvent(mContext, BaiduStatConstants.SHOPPING, type);
    }
}
