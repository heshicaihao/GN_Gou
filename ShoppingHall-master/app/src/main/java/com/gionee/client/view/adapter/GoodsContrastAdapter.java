/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-12-8 下午07:01:25
 */
package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.contrast.GNGoodsContrastActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.persistent.ShareKeys;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.CommonUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.widget.CustomGallery;
import com.gionee.client.view.widget.PageIndicatorView;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;
import com.gionee.framework.operation.utills.JSONArrayHelper;

/**
 * @author yangxiong <br/>
 * @date create at 2014-12-8 下午07:01:25
 * @description TODO 商品对比适配
 */
public class GoodsContrastAdapter extends AbstractListBaseAdapter implements IBusinessHandle {
    private static final String TAG = "GoodsContrastAdapter";
    private RequestAction mRequestAction;
    private View mContrastMenuLayout;

    public GoodsContrastAdapter(Context mContext, int itemLayoutId) {
        super(mContext, itemLayoutId);
    }

    public void hideContrastMenuLayout() {
        if (mContrastMenuLayout == null) {
            LogUtils.log(TAG, LogUtils.getThreadName() + " mContrastMenuLayout == null");
            return;
        }

        mContrastMenuLayout.setVisibility(View.GONE);
        mContrastMenuLayout = null;
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (businessType.equals(Url.ADD_FAVORITE_URL)) {
            Toast.makeText(mContext, mContext.getString(R.string.favorite_success), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        AndroidUtils.showErrorInfo(mContext, errorInfo);
    }

    @Override
    public Context getSelfContext() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return mContext;
    }

    @Override
    public void onCancel(String businessType, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    protected Object initViewHolder(final View convertView) {
        final ViewHolder holder;
        holder = new ViewHolder();
        holder.mAdvertiseGallery = (CustomGallery) convertView.findViewById(R.id.advertise_gallery);
        holder.mAdvertiseGallery.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        holder.mAdvertiseGallery.setDrawingCacheEnabled(false);
        holder.mGalleryPageIndex = (PageIndicatorView) convertView.findViewById(R.id.grid_page_index);
        holder.mTitle = (TextView) convertView.findViewById(R.id.title);
        holder.mPrice = (TextView) convertView.findViewById(R.id.price);
        holder.mExpressMethod = (TextView) convertView.findViewById(R.id.express_method);
        holder.mSalesVolume = (TextView) convertView.findViewById(R.id.sales_volume);
        holder.mScoreValue = (TextView) convertView.findViewById(R.id.score_value);
        holder.mArea = (TextView) convertView.findViewById(R.id.area);
        holder.mPlatform = (TextView) convertView.findViewById(R.id.platform);
        holder.mMenu = (ImageView) convertView.findViewById(R.id.menu);
        return holder;
    }

    private void setContrastMenuListener(final View convertView, final ViewHolder holder,
            final JSONObject itemData, final int postion) {
        if (holder.mContrastMenuLayout != null) {
            holder.mContrastMenuLayout.clearAnimation();
            holder.mContrastMenuLayout.setVisibility(View.GONE);
        }
        holder.mMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    return;
                }

                if (mContrastMenuLayout != null && mContrastMenuLayout.isShown()) {
                    contrastMenuAnimationOut(mContrastMenuLayout);
                }

                if (holder.mContrastMenuLayout != null) {
                    LogUtils.logd(TAG, LogUtils.getThreadName() + " mContrastMenuLayout  != null ");
                    contrastMenuAnimationIn(holder.mContrastMenuLayout);
                    mContrastMenuLayout = holder.mContrastMenuLayout;
                    return;
                }

                ViewStub stub = (ViewStub) convertView.findViewById(R.id.contrast_menu_layout);
                if (stub == null) {
                    LogUtils.logd(TAG, LogUtils.getThreadName() + " stub = null");
                    return;
                }
                holder.mContrastMenuLayout = stub.inflate();
                contrastMenuAnimationIn(holder.mContrastMenuLayout);
                mContrastMenuLayout = holder.mContrastMenuLayout;
                holder.mShare = (TextView) holder.mContrastMenuLayout.findViewById(R.id.share);
                holder.mFavorite = (TextView) holder.mContrastMenuLayout.findViewById(R.id.favorite);
                holder.mDelete = (TextView) holder.mContrastMenuLayout.findViewById(R.id.delete);
                holder.mClose = (ImageView) holder.mContrastMenuLayout.findViewById(R.id.close);
                holder.mClose.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        LogUtils.log(TAG, LogUtils.getThreadName());
                        if (holder.mContrastMenuLayout != null) {
                            LogUtils.log(TAG, LogUtils.getThreadName() + "close menu.");
                            contrastMenuAnimationOut(holder.mContrastMenuLayout);
                            hideContrastMenuLayout();
                        }
                    }
                });
                setShareListener(holder, (ImageView) holder.mAdvertiseGallery.getChildAt(0), itemData);
                setFavoriteListner(holder, itemData);
                setDeleteListener(holder, itemData, postion);
                StatService.onEvent(mContext, BaiduStatConstants.CONTRAST, BaiduStatConstants.MORE);
            }

            private void contrastMenuAnimationOut(final View view) {
                view.clearAnimation();
                final Animation anim = AnimationUtils.loadAnimation(mContext,
                        R.anim.slide_out_to_bottom_short_ani);
                anim.setFillAfter(false);
                view.startAnimation(anim);
                view.setVisibility(View.GONE);
            }

            private void contrastMenuAnimationIn(final View view) {
                view.clearAnimation();
                final Animation anim = AnimationUtils.loadAnimation(mContext,
                        R.anim.slide_in_from_bottom_short_ani);
                anim.setFillAfter(false);
                view.startAnimation(anim);
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void updateView(View convertView, Object viewHolder, JSONObject itemData, int position) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        try {
            final ViewHolder holder = (ViewHolder) viewHolder;
            bindBannerList(holder, itemData);
            updateTextviewWithDefaultText(holder.mTitle, itemData, Constants.GoodsContrast.TITLE, 0);
            updateTextview(holder.mPrice, itemData, Constants.GoodsContrast.PRICE, R.string.sale_price);
            updateTextviewOnlyExist(holder.mExpressMethod, itemData, Constants.GoodsContrast.EXPRESS_METHOD,
                    0);
            updateTextview(holder.mSalesVolume, itemData, Constants.GoodsContrast.SALES_VOLUME, 0);
            updateScoreValue(holder.mScoreValue, itemData, Constants.GoodsContrast.SCORE);
            updateScoreTextColor(itemData, holder);
            updateTextview(holder.mArea, itemData, Constants.GoodsContrast.AREA, 0);
            updateTextview(holder.mPlatform, itemData, Constants.GoodsContrast.CHANNEL, 0);
            setContrastMenuListener(convertView, holder, itemData, position);
            setBannerSelectedListener(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateScoreTextColor(JSONObject itemData, ViewHolder holder) {
        boolean isHighLight = itemData.optBoolean(HttpConstants.Data.SameStyleInfo.IS_HIGH_LIGHT);
        holder.mScoreValue.setTextColor(isHighLight ? mContext.getResources().getColor(
                R.color.tab_text_color_sel) : Color.parseColor("#888888"));
    }

    private void setBannerSelectedListener(final ViewHolder holder) {
        holder.mAdvertiseGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                int currentPage = position % holder.mGalleryPageIndex.getTotalPage();
                holder.mGalleryPageIndex.setCurrentPage(currentPage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                LogUtils.log(TAG, LogUtils.getThreadName());

            }

        });
        holder.mAdvertiseGallery.setSelection(0);
    }

    private void updateScoreValue(TextView textview, final JSONObject itemData, String key) {
        try {
            String price = itemData.optString(key);
            if (TextUtils.isEmpty(price) || price.equals("null")) {
                price = mContext.getString(R.string.score);
            }
            textview.setText(price);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindBannerList(final ViewHolder viewHolder, JSONObject itemData) {
        try {
            JSONArray advertiseList = itemData.optJSONArray(Constants.GoodsContrast.BANNER_IMAGES);
            AdvertiseGalleryAdapter advertiseAdapter = new AdvertiseGalleryAdapter(advertiseList, mContext);
            viewHolder.mAdvertiseGallery.setAdapter(advertiseAdapter);
            int pageSize = advertiseList.length();
            viewHolder.mGalleryPageIndex.setVisibility(pageSize > 1 ? View.VISIBLE : View.GONE);
            viewHolder.mGalleryPageIndex.setTotalPage(pageSize);
            LogUtils.log(TAG, LogUtils.getThreadName() + " pageSize = " + pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setDeleteListener(final ViewHolder viewHolder, final JSONObject itemData, final int postion) {
        viewHolder.mDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    return;
                }
                hideContrastMenuLayout();
                try {
                    JSONArrayHelper helper = new JSONArrayHelper(mCommentArray);
                    if (postion < helper.length()) {
                        helper.remove(postion);
                    }
                    notifyDataSetChanged();
                    ShareDataManager.putJsonArray(mContext, ShareKeys.KEY_GOODS_CONTRAST_DATA, mCommentArray);
                    if (mCommentArray.length() == 0) {
                        GNGoodsContrastActivity context = (GNGoodsContrastActivity) mContext;
                        context.getTitleBar().setRightBtnVisible(false);
                        context.showNoDataLayout();
                    }
                    StatService.onEvent(mContext, BaiduStatConstants.CONTRAST, BaiduStatConstants.DELETE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void setShareListener(final ViewHolder viewHolder, final ImageView thumbImg,
            final JSONObject itemData) {
        viewHolder.mShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    return;
                }
                hideContrastMenuLayout();
                String title = itemData.optString(Constants.GoodsContrast.TITLE);
                final String url = itemData.optString(Constants.GoodsContrast.URL);
                final String src = itemData.optString(Constants.GoodsContrast.CHANNEL);
                if (TextUtils.isEmpty(title)) {
                    if (!TextUtils.isEmpty(src)) {
                        title = mContext.getString(R.string.platform_goods, src);
                    } else {
                        title = url;
                    }
                }

                MyBean bean = MyBeanFactory.createEmptyBean();
                bean.put("title", title);
                bean.put("description", "");
                bean.put("url", url);
                Bitmap bmp = null;
                if (thumbImg != null && thumbImg.getDrawable() != null) {
                    try {
                        bmp = AndroidUtils.drawable2Bitmap(thumbImg.getDrawable());
                    } catch (Exception e) {
                        bmp = BitmapFactory.decodeResource(mContext.getResources(),
                                R.drawable.weibo_share_icon);
                    }
                } else {
                    bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.weibo_share_icon);
                }
                bean.put("thump", bmp);
                v.setTag(bean);
                ((GNGoodsContrastActivity) mContext).showShareDialog(v, (GNGoodsContrastActivity) mContext);
                StatService.onEvent(mContext, BaiduStatConstants.CONTRAST, BaiduStatConstants.SHARE);
            }
        });
    }

    private void setFavoriteListner(final ViewHolder viewHolder, final JSONObject itemData) {
        viewHolder.mFavorite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    return;
                }
                hideContrastMenuLayout();
                if (mRequestAction == null) {
                    mRequestAction = new RequestAction();
                }
                mRequestAction.urlFavorite(GoodsContrastAdapter.this,
                        itemData.optString(Constants.GoodsContrast.URL));
                StatService.onEvent(mContext, BaiduStatConstants.CONTRAST, BaiduStatConstants.COLLECT);
            }
        });

    }

    private static class ViewHolder {
        public CustomGallery mAdvertiseGallery;
        public PageIndicatorView mGalleryPageIndex;
        public TextView mTitle;
        public TextView mPrice;
        public TextView mExpressMethod;
        public TextView mSalesVolume;
        public TextView mScoreValue;
        public TextView mArea;
        public TextView mPlatform;
        public ImageView mMenu;
        public View mContrastMenuLayout;
        public TextView mShare;
        public TextView mFavorite;
        public TextView mDelete;
        public ImageView mClose;
    }

}
