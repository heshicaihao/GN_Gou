/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-9-22 上午11:22:14
 */
package com.gionee.client.view.adapter;

import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.myfavorites.MyFavoritesActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.CommonUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.shoppingmall.AbstractBaseList;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;
import com.gionee.framework.operation.utills.JSONArrayHelper;

/**
 * com.gionee.client.view.adapter.abstractMyfavoriteBaseAdapter
 * 
 * @author yangxiong <br/>
 * @date create at 2014-9-22 上午11:22:14
 * @description TODO 我的收藏四个tab页列表适配
 */
public abstract class AbstractMyfavoriteBaseAdapter extends AbstractListBaseAdapter implements
        IBusinessHandle {
    private static final String TAG = "abstractMyfavoriteBaseAdapter";
    protected AbstractBaseList mBaseList;
    protected View mDropDownLayoutShown;
    protected FavoriteMode mFavoriteMode;
    @SuppressLint("UseSparseArrays")
    protected HashMap<Integer, Boolean> mSingleSelectRecord = new HashMap<Integer, Boolean>();

    public AbstractMyfavoriteBaseAdapter(AbstractBaseList baseList, Context mContext, int itemLayoutId) {
        super(mContext, itemLayoutId);
        this.mBaseList = baseList;
    }

    protected JSONArray getBatchDeleteInfos() {
        JSONArray array = new JSONArray();
        JSONArrayHelper helper = new JSONArrayHelper(array);
        for (Entry<Integer, Boolean> entry : mSingleSelectRecord.entrySet()) {
            boolean checked = entry.getValue();
            if (checked) {
                int position = entry.getKey();
                Object itemObject = getItem(position);
                if (itemObject != null) {
                    JSONObject object = (JSONObject) itemObject;
                    String favoriteId = object.optString(HttpConstants.Data.BaseMyFavoriteList.ID);
                    String itemId = object.optString(HttpConstants.Data.BaseMyFavoriteList.ITEM_ID);
                    String favoriteType = object.optString(HttpConstants.Data.BaseMyFavoriteList.TYPE);
                    LogUtils.log(TAG, LogUtils.getThreadName() + " favoriteId = " + favoriteId + " itemId = "
                            + itemId + " favoriteType = " + favoriteType);
                    JSONObject deleteItemInfo = new JSONObject();
                    try {
                        deleteItemInfo.put(HttpConstants.Request.BatchRemoveFavorites.ID, favoriteId);
                        deleteItemInfo.put(HttpConstants.Request.BatchRemoveFavorites.ITEM_ID, itemId);
                        deleteItemInfo.put(HttpConstants.Request.BatchRemoveFavorites.TYPE, favoriteType);
                        helper.addToLast(deleteItemInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return array;
    }

    public void setFavoriteMode(FavoriteMode mFavoriteMode) {
        this.mFavoriteMode = mFavoriteMode;
        switch (mFavoriteMode) {
            case NORMAL:
                clearAllSingleSelect();
                break;
            default:
                break;
        }
        notifyDataSetChanged();
    }

    public void clearAllSingleSelect() {
        mSingleSelectRecord.clear();
    }

    public void setAllSelected() {
        for (int i = 0; i < getCount(); i++) {
            mSingleSelectRecord.put(i, true);
        }
        notifyDataSetChanged();
    }

    public void hideDropDownLayout() {
        if (mDropDownLayoutShown == null) {
            LogUtils.log(TAG, LogUtils.getThreadName() + " mDropDownLayoutShown == null");
            return;
        }

        mDropDownLayoutShown.setVisibility(View.GONE);
        mDropDownLayoutShown = null;
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        try {
            if (businessType.equals(Url.REMOVE_FAVORITE_URL) || businessType.equals(Url.CANCEL_FAVORITE_URL)) {
                if (session == null) {
                    LogUtils.logd(TAG, LogUtils.getThreadName() + " response = null");
                    return;
                }
                String keyOfTargetId = HttpConstants.Response.ID_I;
                if (businessType.equals(Url.CANCEL_FAVORITE_URL)) {
                    keyOfTargetId = HttpConstants.Data.CommentsList.FAVORITE_ID;
                }
                String targetId = ((JSONObject) session).optString(HttpConstants.Response.ID_I);
                mBaseList.updateCurrentPageCacheWhenDelete(targetId, keyOfTargetId);
//                mBaseList.pullDownToRefresh();
                mBaseList.updateList();
            }
            if (businessType.equals(Url.BATCH_REMOVE_FAVORITE_URL)) {
                AndroidUtils.showShortToast(mContext, R.string.delete_success_toast);
                mBaseList.pullDownToRefresh();
                clearAllSingleSelect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " errorInfo = " + errorInfo);
        if (businessType.equals(Url.BATCH_REMOVE_FAVORITE_URL)) {
            AndroidUtils.showErrorInfo(mContext, mContext.getResources().getString(R.string.delete_fail));
        } else {
            AndroidUtils.showErrorInfo(mContext, errorInfo);
        }
        mBaseList.setProgressState(View.GONE);
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

    protected void singleSelectionAnimationOut(final View view) {
        view.clearAnimation();
        final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.single_select_out);
        anim.setFillAfter(false);
        view.startAnimation(anim);
        view.setVisibility(View.GONE);
    }

    protected void singleSelectionAnimationIn(final View view) {
        view.clearAnimation();
        final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.single_select_in);
        anim.setFillAfter(false);
        view.startAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

    protected String getLinkUrl(final JSONObject itemData) {
        final String url = itemData.optString(HttpConstants.Data.BaseMyFavoriteList.URL);
        return url;
    }

    protected void udpateDropdownImage(final ImageView dropDownImage, final RelativeLayout dropDownLayout,
            final int position) {
        dropDownImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                StatService.onEvent(mContext, BaiduStatConstants.FAVORITE_TOOLS,
                        BaiduStatConstants.FAVORITE_TOOLS);
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    LogUtils.log(TAG, LogUtils.getThreadName() + " return by fast click");
                    return;
                }
                if (dropDownLayout.isShown()) {
                    LogUtils.log(TAG, LogUtils.getThreadName() + " hide dropdwon layout");
                    dropDownLayout.setVisibility(View.GONE);
                    mDropDownLayoutShown = null;
                } else {
                    if (mDropDownLayoutShown != null) {
                        mDropDownLayoutShown.setVisibility(View.GONE);
                    }
                    final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.push_down_in);
                    anim.setFillAfter(true);
                    dropDownLayout.startAnimation(anim);
                    dropDownLayout.setVisibility(View.VISIBLE);
                    mDropDownLayoutShown = dropDownLayout;
                    int lastVisiblePosition = mBaseList.getPullToRefreshListView().getRefreshableView()
                            .getLastVisiblePosition();
                    int height = AndroidUtils.dip2px(mContext, 40);
                    LogUtils.log(TAG, LogUtils.getThreadName() + " position = " + position
                            + ", lastVisiblePosition = " + lastVisiblePosition + " height = " + height);
                    /**
                     * Last visible one or two, perform smooth scroll.
                     */
                    if ((position + 1) == lastVisiblePosition || (position + 2) == lastVisiblePosition) {
                        LogUtils.log(TAG, LogUtils.getThreadName() + " perform smooth scroll.");
                        final ListView listView = mBaseList.getPullToRefreshListView().getRefreshableView();
                        listView.smoothScrollBy(height, 300);
                        listView.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                LogUtils.log(TAG, LogUtils.getThreadName());
                                listView.requestFocusFromTouch();
                            }
                        }, 500);
                    }
                }
            }
        });
    }

    protected void updateMaskImage(final ImageView maskImage, final RelativeLayout itemlayout,
            final JSONObject itemData) {
        final String url = getLinkUrl(itemData);
        updateMaskImageSize(maskImage, itemlayout);
        if (mFavoriteMode == FavoriteMode.MULTI_SELECT_DELETE) {
            maskImage.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    LogUtils.log(TAG, LogUtils.getThreadName());
                    if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                        return;
                    }
//                    if ()
                    
                }
            });
        } else {
            maskImage.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    LogUtils.log(TAG, LogUtils.getThreadName());
                    if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                        return;
                    }
                    CommonUtils.gotoWebViewActvity(mContext, url, true);
                    baiduStat(itemData.optString(HttpConstants.Data.ShoppingList.TYPE_NAME));
                }
            });
        }
    }

    protected void updateMaskImageSize(final ImageView maskImage, final RelativeLayout itemlayout) {
        maskImage.post(new Runnable() {
            @Override
            public void run() {
                int w = itemlayout.getMeasuredWidth();
                int h = itemlayout.getMeasuredHeight();
                maskImage.setLayoutParams(new RelativeLayout.LayoutParams(w, h));
            }
        });
    }

    protected void updateDropDownLayout(final View dropDownLayout, final View shareImg, final View deleteImg,
            final ThumbInfo thumbInfo, final JSONObject itemData, final int favoriteType) {
        if (dropDownLayout != null) {
            dropDownLayout.setVisibility(View.GONE);
        }
        setShareImgListener(shareImg, thumbInfo, itemData);
        setDeleteImgListener(deleteImg, itemData, favoriteType);
    }

    protected void setDeleteImgListener(final View deleteImg, final JSONObject itemData,
            final int favoriteType) {
        deleteImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName() + " itemData = " + itemData);
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    return;
                }
                hideDropDownLayout();
                int favoriteId = itemData.optInt(HttpConstants.Data.BaseMyFavoriteList.ID);
                int itemId = itemData.optInt(HttpConstants.Data.BaseMyFavoriteList.ITEM_ID);
                LogUtils.log(TAG, LogUtils.getThreadName() + " favoriteId = " + favoriteId + " itemId = "
                        + itemId);
                removeFavorite(favoriteId, itemId, favoriteType);
                String type = (favoriteId > 1) == true ? BaiduStatConstants.SHOPPING_MORE
                        : BaiduStatConstants.TALE_MORE;
                StatService.onEvent(mContext, type, BaiduStatConstants.DELETE);
            }
        });
    }

    private void setShareImgListener(final View shareImg, final ThumbInfo thumbInfo, final JSONObject itemData) {
        shareImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    return;
                }
                hideDropDownLayout();
                String title = itemData.optString(HttpConstants.Data.BaseMyFavoriteList.TITLE);
                final String url = itemData.optString(HttpConstants.Data.BaseMyFavoriteList.URL);
                final String src = itemData.optString(HttpConstants.Data.BaseMyFavoriteList.SRC);
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
                bean.put("imageUrl",
                        TextUtils.isEmpty(thumbInfo.mImageUrl) ? Constants.DEFAULT_SHARE_ICON_URL
                                : thumbInfo.mImageUrl);
                Bitmap bmp = null;
                if (thumbInfo.mThumbView != null && thumbInfo.mThumbView.getDrawable() != null) {
                    try {
                        bmp = AndroidUtils.drawable2Bitmap(thumbInfo.mThumbView.getDrawable());
                    } catch (Exception e) {
                        bmp = BitmapFactory.decodeResource(mContext.getResources(),
                                R.drawable.weibo_share_icon);
                    }
                } else {
                    bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.weibo_share_icon);
                }
                bean.put("thump", bmp);
                bean.put(HttpConstants.Data.BaseMyFavoriteList.TYPE,
                        itemData.optInt(HttpConstants.Data.BaseMyFavoriteList.TYPE));
                v.setTag(bean);
                ((MyFavoritesActivity) mContext).showShareDialog(v, (MyFavoritesActivity) mContext);
                String type = isShoppingType(itemData) == true ? BaiduStatConstants.SHOPPING_MORE
                        : BaiduStatConstants.TALE_MORE;
                StatService.onEvent(mContext, type, BaiduStatConstants.SHARE);
            }

            private boolean isShoppingType(final JSONObject itemData) {
                return itemData.optInt(HttpConstants.Data.BaseMyFavoriteList.TYPE) > 1;
            }
        });
    }

    private void removeFavorite(int favoriteId, int itemId, int type) {
        mBaseList.setProgressState(View.VISIBLE);
        MyBean bean = MyBeanFactory.createEmptyBean();
        bean.put(HttpConstants.Request.RemoveFavorites.ID, favoriteId);
        bean.put(HttpConstants.Request.RemoveFavorites.ITEM_ID, itemId);
        bean.put(HttpConstants.Request.RemoveFavorites.TYPE, type);
        new RequestAction().removeFavorite(this, HttpConstants.Data.RemoveFavorite.LIST_INFO_JO, false, bean);
    }

    public void batchRemoveFavorite() {
        mBaseList.setProgressState(View.VISIBLE);
        new RequestAction().batchRemoveFavorite(this, getBatchDeleteInfos());
    }

    protected abstract void baiduStat(String type);

    public enum FavoriteMode {
        NORMAL, MULTI_SELECT_DELETE
    }
}
