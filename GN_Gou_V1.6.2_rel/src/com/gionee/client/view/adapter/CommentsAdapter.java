/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-8-1 下午02:52:04
 */
package com.gionee.client.view.adapter;

import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.myfavorites.MyFavoritesActivity;
import com.gionee.client.activity.myfavorites.StoryDetailActivity;
import com.gionee.client.activity.question.QuestionListActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.manage.ConfigManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.persistent.ShareKeys;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.CommonUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.Constants.PAGE_TAG;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.shoppingmall.AbstractBaseList;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.utills.JSONArrayHelper;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @author yangxiong <br/>
 * @description TODO 知物列表适配/我的收藏页知物列表适配
 */
public class CommentsAdapter extends AbstractMyfavoriteBaseAdapter {
    private static final String TAG = "CommentsAdapter";
    private String mUrl;
    private View mClickedItemView;
    private boolean mIsFromCommentsList;
    private boolean mIsRedDotClickedToday;
    private String mType;
    private boolean mIsQuestionImportOpen;

    public CommentsAdapter(AbstractBaseList baseList, Context mContext, String url) {
        super(baseList, mContext, R.layout.comment_item);
        this.mUrl = url;
        init(mContext);
    }

    public View getClickedItemView() {
        return mClickedItemView;
    }

    private void init(Context mContext) {
        mIsQuestionImportOpen = ConfigManager.isQuestionImportOpen(mContext);
        mIsFromCommentsList = !TextUtils.isEmpty(mUrl) && (mUrl.equals(Url.TALE_LIST));
        mType =mIsFromCommentsList == true ? BaiduStatConstants.TALE : BaiduStatConstants.TALE_CLICK;
        if (mIsFromCommentsList) {
            long lastClickTime = ShareDataManager
                    .getDataAsLong(mContext, ShareKeys.KEY_COMMENT_CLICK_TIME, 0);
            if (CommonUtils.isDateToday(lastClickTime)) {
                mIsRedDotClickedToday = true;
            }
        }
    }

    protected Object initViewHolder(final View convertView) {
        final ViewHolder holder;
        holder = new ViewHolder();
        holder.mTitle = (TextView) convertView.findViewById(R.id.title);
        holder.mSummary = (TextView) convertView.findViewById(R.id.summary);
        holder.mProductIconLayout = (LinearLayout) convertView.findViewById(R.id.product_icon_layout);
        holder.mCommodityIconLeft = (ImageView) convertView.findViewById(R.id.product_icon_left);
        holder.mCommodityIconCenter = (ImageView) convertView.findViewById(R.id.product_icon_center);
        holder.mCommodityIconRight = (ImageView) convertView.findViewById(R.id.product_icon_right);
        holder.mMaskImage = (ImageView) convertView.findViewById(R.id.item_click_image);
        holder.mItemLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
        holder.mRecommendIcon = (ImageView) convertView.findViewById(R.id.recommend_icon);
        holder.mRedDot = (ImageView) convertView.findViewById(R.id.red_dot);
        holder.mPraise = (ImageView) convertView.findViewById(R.id.praise);
        holder.mPraiseCount = (TextView) convertView.findViewById(R.id.praise_count);
        holder.mCollect = (ImageView) convertView.findViewById(R.id.collect);
        holder.mCommentsCount = (TextView) convertView.findViewById(R.id.collect_status);
        holder.mSaid = (TextView) convertView.findViewById(R.id.said);
        holder.mAvatarImg = (ImageView) convertView.findViewById(R.id.person_icon);
        holder.mPublishTime = (TextView) convertView.findViewById(R.id.publish_time);
        holder.mMoreImg = (ImageView) convertView.findViewById(R.id.drop_down_img);
        holder.mQuestionImportLayout = (RelativeLayout) convertView.findViewById(R.id.question_import_layout);
        holder.mQuestionBackgroudIcon = (ImageView) convertView.findViewById(R.id.question_icon);
        holder.mQuestionSummary = (TextView) convertView.findViewById(R.id.question_summary);
        holder.mQuestionCount = (TextView) convertView.findViewById(R.id.question_count);
        holder.mAnswerCount = (TextView) convertView.findViewById(R.id.answer_count);
        holder.mSingleSelectionCheckBox = (CheckBox) convertView
                .findViewById(R.id.single_selection_check_box);
        return holder;
    }

    /**
     * @description TODO 更新物语视图
     */
    protected void updateView(final View convertView, final Object viewHolder, final JSONObject itemData,
            int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        updateTextviewWithDefaultText(((ViewHolder) viewHolder).mTitle, itemData,
                HttpConstants.Data.CommentsList.TITLE, 0);
        updateTextview(((ViewHolder) viewHolder).mSummary, itemData, HttpConstants.Data.CommentsList.SUMMARY,
                0);
        updateTextview(((ViewHolder) viewHolder).mPraiseCount, itemData,
                HttpConstants.Data.CommentsList.LIKE, 0);
        updateTextview(((ViewHolder) viewHolder).mCommentsCount, itemData,
                HttpConstants.Data.CommentsList.COMMENT_COUNT, 0);
        updateTextview(((ViewHolder) viewHolder).mSaid, itemData, HttpConstants.Data.CommentsList.AUTHOR,
                R.string.author_said);
        updateCommodityIcon(((ViewHolder) viewHolder).mItemLayout, (ViewHolder) viewHolder, itemData,
                HttpConstants.Data.CommentsList.IMGS);
        updateAvatar(viewHolder, itemData);
        updateRecommendIcon(viewHolder, itemData);
        updateNewArticleIcon(viewHolder, itemData, position);
//        updatePraiseState(viewHolder, itemData);
//        updateFavoriteState(viewHolder, itemData);
        updateCreateTime(viewHolder, itemData);
//        udpateDropdownImage(holder.mMoreImg, holder.mDropDownLayout, position);
//        setContrastMenuListener(convertView, holder, itemData, position);
        checkDisplayDropDownOrPraise(viewHolder);
        updateQuestionImportLayout(convertView, holder, itemData, position);
        updateMaskImage(convertView, holder, itemData, position, holder.mRedDot);
        updateSingleSelectionCheckbox(holder, position);
    }

    private void updateSingleSelectionCheckbox(final ViewHolder holder, final int position) {
        if (mIsFromCommentsList) {
            return;
        }
        LogUtils.log(TAG, LogUtils.getThreadName());
        int visibiity = View.GONE;
        int originalVisibility = holder.mSingleSelectionCheckBox.getVisibility();
        if (mFavoriteMode == FavoriteMode.MULTI_SELECT_DELETE) {
            visibiity = View.VISIBLE;
            processFavoriteMOde(holder, position, visibiity, originalVisibility);
        } else {
            visibiity = View.GONE;
            holder.mMaskImage.setVisibility(View.VISIBLE);
            if (originalVisibility != visibiity) {
                singleSelectionAnimationOut(holder.mSingleSelectionCheckBox);
            }
        }
        holder.mSingleSelectionCheckBox.setVisibility(visibiity);
    }

    private void processFavoriteMOde(final ViewHolder holder, final int position, int visibiity,
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
            singleSelectionAnimationIn(holder.mSingleSelectionCheckBox);
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

    private void updateQuestionImportLayout(final View convertView, final ViewHolder viewHolder,
            final JSONObject itemData, int position) {
        if (mIsFromCommentsList && mIsQuestionImportOpen && position == 0) {
            viewHolder.mItemLayout.setVisibility(View.GONE);
            viewHolder.mQuestionImportLayout.setVisibility(View.VISIBLE);
            viewHolder.mMaskImage.setVisibility(View.GONE);
            viewHolder.mQuestionImportLayout.setOnClickListener(new QuestionImportClickListener());
            updateTextview(viewHolder.mQuestionSummary, itemData,
                    HttpConstants.Data.CommentsList.QUESTION_SUMMARY, 0);
            updateTextview(viewHolder.mQuestionCount, itemData,
                    HttpConstants.Data.CommentsList.QUESTION_COUNT, R.string.question_count);
            updateTextview(viewHolder.mAnswerCount, itemData, HttpConstants.Data.CommentsList.ANSWER_COUNT,
                    R.string.answer_count);
            updateQuestionBackground(convertView, viewHolder, itemData);
        } else {
            viewHolder.mQuestionImportLayout.setVisibility(View.GONE);
            viewHolder.mItemLayout.setVisibility(View.VISIBLE);
            viewHolder.mMaskImage.setVisibility(View.VISIBLE);
        }
    }

    private void updateQuestionBackground(final View convertView, final ViewHolder viewHolder,
            final JSONObject itemData) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        String bgUrl = itemData.optString(HttpConstants.Data.CommentsList.QUESTION_BACKGROUD);
        if (TextUtils.isEmpty(bgUrl)) {
            LogUtils.log(TAG, LogUtils.getThreadName() + "return: bgUrl = " + bgUrl);
            return;
        }
        GNImageLoader.getInstance().loadBitmap(bgUrl, ((ViewHolder) viewHolder).mQuestionBackgroudIcon,
                new QuestionBackgroudLoadListener());
    }

    private void setContrastMenuListener(final View convertView, final ViewHolder holder,
            final JSONObject itemData, final int postion) {
        if (holder.mMoreLayout != null) {
            holder.mMoreLayout.clearAnimation();
            holder.mMoreLayout.setVisibility(View.GONE);
        }
        holder.mMoreImg.setOnClickListener(new MoreImageClickListener(holder, itemData, convertView));
    }

    private void checkDisplayDropDownOrPraise(final Object viewHolder) {

        if (!TextUtils.isEmpty(mUrl) && mUrl.equals(Url.COMMENTS_MY_FAVORITE_URL)) {
            ((ViewHolder) viewHolder).mPraiseCount.setVisibility(View.GONE);
            ((ViewHolder) viewHolder).mCommentsCount.setVisibility(View.GONE);
            ((ViewHolder) viewHolder).mPraise.setVisibility(View.GONE);
            ((ViewHolder) viewHolder).mCollect.setVisibility(View.GONE);
            ((ViewHolder) viewHolder).mMoreImg.setVisibility(View.GONE);
        }
    }

//    private void updateFavoriteState(final Object viewHolder, final JSONObject itemData) {
//        boolean isFavorite = itemData.optBoolean(HttpConstants.Data.CommentsList.IS_FAVORITE);
//        ((ViewHolder) viewHolder).mCollect.setImageResource(isFavorite ? R.drawable.comment_include_me
//                : R.drawable.comment_include_me);
//    }

//    private void updatePraiseState(final Object viewHolder, final JSONObject itemData) {
//        int isPraise = 0;
//        String id = itemData.optString(HttpConstants.Data.CommentsList.ID);
//        if (!TextUtils.isEmpty(id)) {
//            isPraise = ShareDataManager.getDataAsInt(mContext, ShareKeys.KEY_COMMENT_PRAISE_PREFIX + id, 0);
//        }
//        ((ViewHolder) viewHolder).mPraise.setImageResource(isPraise == 1 ? R.drawable.praise_include_me
//                : R.drawable.praise_uninclude_me);
//    }

    private void updateRecommendIcon(final Object viewHolder, final JSONObject itemData) {
        boolean isRecommend = itemData.optBoolean(HttpConstants.Data.CommentsList.RECOMMEND);
        ((ViewHolder) viewHolder).mRecommendIcon.setVisibility(isRecommend ? View.VISIBLE : View.GONE);
    }

    private void updateNewArticleIcon(final Object viewHolder, final JSONObject itemData, int position) {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " position = " + position);
        boolean isShowRedDot = false;
        boolean isNewest = itemData.optBoolean(HttpConstants.Data.CommentsList.IS_NEWEST);
        if (isNewest && mIsFromCommentsList) {
            int maxId = ShareDataManager.getDataAsInt(mContext, ShareKeys.KEY_COMMENT_MAX_ID, 0);
            int id = itemData.optInt(HttpConstants.Data.CommentsList.ID);
            LogUtils.logd(TAG, LogUtils.getThreadName() + " position = " + position + " maxid = " + maxId
                    + " id = " + id);
            if (id > maxId) {
                ShareDataManager.saveDataAsInt(mContext, ShareKeys.KEY_COMMENT_MAX_ID, id);
                isShowRedDot = true;
            } else if (id == maxId && !mIsRedDotClickedToday) {
                isShowRedDot = true;
            }
        }

        ((ViewHolder) viewHolder).mRedDot.setVisibility(isShowRedDot ? View.VISIBLE : View.GONE);
    }

    private void updateAvatar(final Object viewHolder, final JSONObject itemData) {
        String avatarUrl = itemData.optString(HttpConstants.Data.CommentsList.AVATAR);
        if (TextUtils.isEmpty(avatarUrl)) {
            return;
        }
        GNImageLoader.getInstance().loadBitmap(avatarUrl, ((ViewHolder) viewHolder).mAvatarImg);
    }

    private void updateCreateTime(final Object viewHolder, final JSONObject itemData) {
        if (mIsFromCommentsList) {
            String time = itemData.optString(HttpConstants.Data.CommentsList.CREATE_TIME);
            if (TextUtils.isEmpty(time)) {
                time = "";
            }
            ((ViewHolder) viewHolder).mPublishTime.setText(time);
            ((ViewHolder) viewHolder).mPublishTime.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @description TODO 更新商品Icon
     */
    private void updateCommodityIcon(final View convertView, final ViewHolder viewHolder,
            final JSONObject itemData, String key) {
        JSONArray urlJsonArray = itemData.optJSONArray(key);
        int length = urlJsonArray == null ? 0 : urlJsonArray.length();
        showCommodityIcon(viewHolder, length);
        loadCommodityIcon(convertView, viewHolder, urlJsonArray, length);
    }

    private void loadCommodityIcon(final View convertView, final ViewHolder viewHolder,
            JSONArray urlJsonArray, int length) {
        String urlStr = null;
        ImageView imageview = null;
        for (int i = 0; i < length; i++) {
            switch (i) {
                case 0:
                    imageview = viewHolder.mCommodityIconLeft;
                    break;
                case 1:
                    imageview = viewHolder.mCommodityIconCenter;
                    break;
                case 2:
                    imageview = viewHolder.mCommodityIconRight;
                    break;

                default:
                    break;
            }
            if (imageview != null) {
                imageview.setImageResource(R.drawable.comment_img_default);
                urlStr = urlJsonArray.optString(i);
                updateCommodityIconItem(convertView, viewHolder, urlStr, imageview);
            }
        }
    }

    private void showCommodityIcon(final ViewHolder viewHolder, int length) {
        int visibility = View.GONE;
        if (length > 0) {
            visibility = View.VISIBLE;
        }
        viewHolder.mProductIconLayout.setVisibility(visibility);
        switch (length) {
            case 0:
                viewHolder.mCommodityIconLeft.setVisibility(View.INVISIBLE);
                viewHolder.mCommodityIconCenter.setVisibility(View.INVISIBLE);
                viewHolder.mCommodityIconRight.setVisibility(View.INVISIBLE);
                break;
            case 1:
                viewHolder.mCommodityIconLeft.setVisibility(View.VISIBLE);
                viewHolder.mCommodityIconCenter.setVisibility(View.INVISIBLE);
                viewHolder.mCommodityIconRight.setVisibility(View.INVISIBLE);
                break;
            case 2:
                viewHolder.mCommodityIconLeft.setVisibility(View.VISIBLE);
                viewHolder.mCommodityIconCenter.setVisibility(View.VISIBLE);
                viewHolder.mCommodityIconRight.setVisibility(View.INVISIBLE);
                break;
            case 3:
                viewHolder.mCommodityIconLeft.setVisibility(View.VISIBLE);
                viewHolder.mCommodityIconCenter.setVisibility(View.VISIBLE);
                viewHolder.mCommodityIconRight.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void updateCommodityIconItem(final View convertView, final ViewHolder holder, String urlStr,
            final ImageView imageview) {
        GNImageLoader.getInstance().loadBitmap(urlStr, imageview, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                holder.mMaskImage.post(new Runnable() {
                    @Override
                    public void run() {
                        int w = holder.mItemLayout.getMeasuredWidth();
                        int h = holder.mItemLayout.getMeasuredHeight();
                        holder.mMaskImage.setLayoutParams(new RelativeLayout.LayoutParams(w, h));
                    }
                });
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage == null) {
                    LogUtils.log(TAG, LogUtils.getThreadName() + "Error: loadedImage == null");
                    return;
                }
                holder.mMaskImage.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int w = holder.mItemLayout.getMeasuredWidth();
                        int h = holder.mItemLayout.getMeasuredHeight();
                        holder.mMaskImage.setLayoutParams(new RelativeLayout.LayoutParams(w, h));
                    }
                }, 20);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                LogUtils.log(TAG, LogUtils.getThreadName());

            }
        });
    }

    protected void updateMaskImage(final View convertView, final ViewHolder holder,
            final JSONObject itemData, final int position, final View redhot) {
        if (mIsFromCommentsList && mIsQuestionImportOpen && position == 0) {
//        if (position == 0) {
            updateMaskImageSize(holder.mMaskImage, holder.mQuestionImportLayout);
        } else {
            updateMaskImageSize(holder.mMaskImage, holder.mItemLayout);
        }
        holder.mMaskImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (mIsFromCommentsList) {
                    ((GnHomeActivity) mContext).setExitStatisticsFlag(true);
                }
                handleMaskImageClicked(convertView, itemData, position, redhot);
            }
        });
//        deleteCommentWhenLongClick(holder, itemData);
    }

    private void handleMaskImageLongClick(final JSONObject itemData) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        View.OnLongClickListener listener = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                deleteMyfavoriteComment(itemData);
                StatService.onEvent(mContext, BaiduStatConstants.TALE_MORE, BaiduStatConstants.DELETE_LONG);
                return false;
            }
        };
        DialogFactory.createDeleteCommentDialog((Activity) mContext, listener).show();
        StatService.onEvent(mContext, BaiduStatConstants.TALE_MORE, BaiduStatConstants.LONG_CLICK);
    }

    private void handleMaskImageClicked(final View convertView, final JSONObject itemData,
            final int position, final View redhot) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
            return;
        }

        mClickedItemView = convertView;
        gotoCommentDetailActivity(itemData, position);
        mIsRedDotClickedToday = true;
        redhot.setVisibility(View.GONE);
        ShareDataManager.saveDataAsLong(mContext, ShareKeys.KEY_COMMENT_CLICK_TIME,
                System.currentTimeMillis());
    }

    @Override
    protected void setDeleteImgListener(View deleteImg, final JSONObject itemData, int favoriteType) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        deleteImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                deleteMyfavoriteComment(itemData);
                StatService.onEvent(mContext, BaiduStatConstants.TALE_MORE, BaiduStatConstants.DELETE);
            }
        });
    }

    @Override
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
                    String favoriteId = object.optString(HttpConstants.Data.CommentsList.FAVORITE_ID);
                    String itemId = object.optString(HttpConstants.Data.CommentsList.ID);
                    String favoriteType = "1";
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

    private void deleteMyfavoriteComment(final JSONObject itemData) {
        int favoriteId = itemData.optInt(HttpConstants.Data.CommentsList.FAVORITE_ID);
        int commentId = itemData.optInt(HttpConstants.Data.CommentsList.ID);
        mBaseList.setProgressState(View.VISIBLE);
        new RequestAction().cancelFavorite(this, null, favoriteId, commentId, 1);
    }

    private void gotoCommentDetailActivity(final JSONObject itemData, int position) {
        String url = itemData.optString(HttpConstants.Data.CommentsList.URL);
        int id = itemData.optInt(HttpConstants.Data.CommentsList.ID);
        boolean isFavorite = itemData.optBoolean(HttpConstants.Data.CommentsList.IS_FAVORITE);
        LogUtils.log(TAG, LogUtils.getThreadName() + " is_favorite = " + isFavorite);
        int favoriteId = 0;
        if (isFavorite) {
            favoriteId = itemData.optInt(HttpConstants.Data.CommentsList.FAVORITE_ID);
        }
        LogUtils.log(TAG, LogUtils.getThreadName() + "Link url: " + url + ", id =" + id + ", is_favorite = "
                + isFavorite + ", fav_id = " + favoriteId);
        Intent intent = new Intent();
        intent.putExtra(StatisticsConstants.KEY_INTENT_URL, url);
        intent.setClass(mContext, StoryDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("is_favorite", isFavorite);
        intent.putExtra("fav_id", favoriteId);
        intent.putExtra("comment_count", itemData.optString(HttpConstants.Data.CommentsList.COMMENT_COUNT));
        intent.putExtra("praise_count", itemData.optString(HttpConstants.Data.CommentsList.LIKE));
        intent.putExtra("position", position);
        intent.putExtra(StatisticsConstants.PAGE_TAG, PAGE_TAG.STORY);
        intent.putExtra(Constants.IS_SHOW_WEB_FOOTBAR, false);
        ((Activity) mContext).startActivityForResult(intent,
                Constants.ActivityRequestCode.REQUEST_CODE_COMMENT_DETAIL);
        AndroidUtils.enterActvityAnim((Activity) mContext);
        StatService.onEvent(mContext, mType, String.valueOf(id));
    }

    private final class QuestionImportClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                return;
            }
            Intent intent = new Intent(mContext, QuestionListActivity.class);
            mContext.startActivity(intent);
            StatService.onEvent(mContext, BaiduStatConstants.QUESIOTN_ANSWER,
                    BaiduStatConstants.QUESIOTN_ANSWER);
        }
    }

    private final class MoreImageClickListener implements OnClickListener {
        private final ViewHolder mHolder;
        private final JSONObject mItemData;
        private final View mConvertView;

        private MoreImageClickListener(ViewHolder holder, JSONObject itemData, View convertView) {
            this.mHolder = holder;
            this.mItemData = itemData;
            this.mConvertView = convertView;
        }

        @Override
        public void onClick(View v) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            hanleMoreImageClick();
        }

        private void hanleMoreImageClick() {
            if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                return;
            }

            if (mDropDownLayoutShown != null && mDropDownLayoutShown.isShown()) {
                contrastMenuAnimationOut(mDropDownLayoutShown);
            }

            if (mHolder.mMoreLayout != null) {
                LogUtils.logd(TAG, LogUtils.getThreadName() + " mDropDownLayoutShown  != null ");
                showShareMenu();
                ThumbInfo info = getThumbInfo(mHolder, mItemData);
                updateDropDownLayout(null, mHolder.mShare, mHolder.mDelete, info, mItemData, 1);
                return;
            }

            ViewStub stub = (ViewStub) mConvertView.findViewById(R.id.comment_menu_layout);
            if (stub == null) {
                LogUtils.logd(TAG, LogUtils.getThreadName() + " stub = null");
                return;
            }
            mHolder.mMoreLayout = stub.inflate();
            showShareMenu();
            initShareMenuView();
            updateDropDownLayout(mDropDownLayoutShown, mHolder.mShare, mHolder.mDelete,
                    getThumbInfo(mHolder, mItemData), mItemData, 1);
            StatService.onEvent(mContext, BaiduStatConstants.TALE_MORE, BaiduStatConstants.MORE);
        }

        private void initShareMenuView() {
            mHolder.mShare = (TextView) mHolder.mMoreLayout.findViewById(R.id.share);
            mHolder.mDelete = (TextView) mHolder.mMoreLayout.findViewById(R.id.delete);
            mHolder.mClose = (ImageView) mHolder.mMoreLayout.findViewById(R.id.close);
            mHolder.mClose.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    LogUtils.log(TAG, LogUtils.getThreadName());
                    if (mHolder.mMoreLayout != null) {
                        contrastMenuAnimationOut(mHolder.mMoreLayout);
                        hideDropDownLayout();
                        StatService.onEvent(mContext, BaiduStatConstants.TALE_MORE,
                                BaiduStatConstants.WEB_CLOSE_BTN);
                    }
                }
            });
        }

        private void showShareMenu() {
            int w = mHolder.mItemLayout.getMeasuredWidth();
            int h = mHolder.mItemLayout.getMeasuredHeight();
            mHolder.mMoreLayout.setLayoutParams(new RelativeLayout.LayoutParams(w, h));
            shareMenuAnimationIn(mHolder.mMoreLayout);
            mDropDownLayoutShown = mHolder.mMoreLayout;
        }
    }

    private ThumbInfo getThumbInfo(final ViewHolder holder, final JSONObject itemData) {
        JSONArray urlJsonArray = itemData.optJSONArray(HttpConstants.Data.CommentsList.IMGS);
        int length = urlJsonArray == null ? 0 : urlJsonArray.length();
        String imageUrl = "";
        if (length > 0) {
            imageUrl = urlJsonArray.optString(0);
        }
        ThumbInfo info = new ThumbInfo(holder.mCommodityIconLeft, imageUrl);
        return info;
    }

    private void contrastMenuAnimationOut(final View view) {
        view.clearAnimation();
        final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_to_bottom_short_ani);
        anim.setFillAfter(false);
        view.startAnimation(anim);
        view.setVisibility(View.GONE);
    }

    private void shareMenuAnimationIn(final View view) {
        view.clearAnimation();
        final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_bottom_short_ani);
        anim.setFillAfter(false);
        view.startAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

    private final class QuestionBackgroudLoadListener implements ImageLoadingListener {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
            LogUtils.log(TAG, LogUtils.getThreadName());
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            LogUtils.log(TAG, LogUtils.getThreadName());
        }

        @Override
        public void onLoadingComplete(String imageUri, final View view, final Bitmap loadedImage) {
            int viewWidth = view.getWidth();
            viewWidth = AndroidUtils.getDisplayWidth(mContext) - AndroidUtils.dip2px(mContext, 16);
            LayoutParams para = view.getLayoutParams();
            float scaleWidth = viewWidth / (float) loadedImage.getWidth();
            para.width = viewWidth;
            para.height = (int) (loadedImage.getHeight() * scaleWidth);
            view.setLayoutParams(para);
            LogUtils.log(TAG, LogUtils.getThreadName() + " bm_W =" + loadedImage.getWidth() + " "
                    + " bm_H = " + loadedImage.getHeight() + " Scale= " + scaleWidth + " para.height = "
                    + para.height + " para.width = " + para.width);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            LogUtils.log(TAG, LogUtils.getThreadName());
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
         * 更多分享删除显示区
         */
        public View mMoreLayout;

        /**
         * 显示推荐图标
         */
        public ImageView mRecommendIcon;

        /**
         * 新知物显示红点
         */
        public ImageView mRedDot;

        /**
         * 标题
         */
        public TextView mTitle;

        /**
         * 内容摘要
         */
        public TextView mSummary;

        /**
         * 图片展示布局
         */
        public LinearLayout mProductIconLayout;

        /**
         * 商品图片(左)
         */
        public ImageView mCommodityIconLeft;

        /**
         * 商品图片(中)
         */
        public ImageView mCommodityIconCenter;

        /**
         * 商品图片（右）
         */
        public ImageView mCommodityIconRight;

        /**
         * 赞
         */
        public ImageView mPraise;

        /**
         * 点赞数量（= 原点赞 + 收藏）
         */
        public TextView mPraiseCount;

        /**
         * 收藏图标
         */
        public ImageView mCollect;

        /**
         * 评论数量
         */
        public TextView mCommentsCount;

        /**
         * 发布者寄语（小惠说：...；小豆说：...。）
         */
        public TextView mSaid;

        /**
         * 发布者头像
         */
        public ImageView mAvatarImg;

        /**
         * 发布时间
         */
        public TextView mPublishTime;

        /**
         * 更多按钮
         */
        public ImageView mMoreImg;

        /**
         * 分享
         */
        public TextView mShare;
        /**
         * 删除
         */
        public TextView mDelete;
        /**
         * 关闭
         */
        public ImageView mClose;

        /**
         * 购物问答区
         */
        public RelativeLayout mQuestionImportLayout;

        /**
         * 问答区背景图
         */
        public ImageView mQuestionBackgroudIcon;
        /**
         * 问题数
         */
        public TextView mQuestionCount;
        /**
         * 问答区简介
         */
        public TextView mQuestionSummary;
        /**
         * 回答数
         */
        public TextView mAnswerCount;
        /**
         * 单选checkbox
         */
        public CheckBox mSingleSelectionCheckBox;
    }

    @Override
    protected void baiduStat(String type) {
    }
}
