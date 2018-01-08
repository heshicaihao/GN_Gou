/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-4-1 下午05:09:33
 */
package com.gionee.client.view.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.question.QuestionDetailActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.widget.CircleImageView;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.operation.net.GNImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * com.gionee.client.view.adapter.QuestionDetailListAdapter
 * 
 * @author yangxiong <br/>
 * @date create at 2015-4-1 下午05:09:33
 * @description TODO 问题详情适配
 */
public class QuestionDetailListAdapter extends AbstractListBaseAdapter implements IBusinessHandle {
    private static final String HTTP_GOU_GIONEE_COM = "http://gou.gionee.com/";
    private static final String TAG = "QuestionDetailListAdapter";
    private static final int SHOW_NUM_MAX = 999;
    private RequestAction mRequestAction;
    private String mVerifyCode;
    private String mQuestionId;
    private List<String> mPraisedList = new ArrayList<String>();

    public QuestionDetailListAdapter(Context mContext, int itemLayoutId) {
        super(mContext, itemLayoutId);
    }

    public String getVerifyCode() {
        return mVerifyCode;
    }

    public void clearPraisedData() {
        if (null == mPraisedList) {
            return;
        }
        mPraisedList.clear();
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (businessType.equals(Url.ANSWER_PRAISE_URL)) {
            JSONObject data = (JSONObject) session;
            String id = data.optString(HttpConstants.Data.AnswerPraise.ID);
            mPraisedList.add(id);
            notifyDataSetChanged();
//            ((QuestionDetailActivity) mContext).getmQuestionList().refreshCurrentPageData();
        }
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        AndroidUtils.showErrorInfo(getSelfContext(), errorInfo);
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
    protected Object initViewHolder(View convertView) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        final ViewHolder holder;
        holder = new ViewHolder();
        holder.mItemLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
        holder.mQuestionLayout = (RelativeLayout) convertView.findViewById(R.id.question_layout);
        holder.mMaskImage = (ImageView) convertView.findViewById(R.id.item_click_image);
        holder.mQuestionAvatar = (CircleImageView) convertView.findViewById(R.id.portrait);
        holder.mQuestionNickName = (TextView) convertView.findViewById(R.id.nick_name);
        holder.mQuestion = (TextView) convertView.findViewById(R.id.question);
        holder.mContent = (TextView) convertView.findViewById(R.id.description);
        holder.mAnswerCount = (TextView) convertView.findViewById(R.id.answer_count);
        holder.mPhotoUp = (ImageView) convertView.findViewById(R.id.photo_up);
        holder.mPhotoCenter = (ImageView) convertView.findViewById(R.id.photo_center);
        holder.mPhotoDown = (ImageView) convertView.findViewById(R.id.photo_down);
        holder.mAnswerNickName = (TextView) convertView.findViewById(R.id.answer_nick_name);
        holder.mReplyAvatar = (CircleImageView) convertView.findViewById(R.id.answer_avatar);
        holder.mReplyTime = (TextView) convertView.findViewById(R.id.answer_time);
        holder.mReplyContent = (TextView) convertView.findViewById(R.id.answer);
        holder.mJumpQuestionTitle = (TextView) convertView.findViewById(R.id.jump_title);
        holder.mJumpQuestionTitleShow = (TextView) convertView.findViewById(R.id.jump_title_show);
        holder.mPraiseCount = (TextView) convertView.findViewById(R.id.praise_count);
        holder.mPraiseCountAnim = (TextView) convertView.findViewById(R.id.praise_count_anim);
        return holder;
    }

    @Override
    protected void updateView(View convertView, Object viewHolder, JSONObject itemData, int position) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " position = " + position + ", itemData = " + itemData);
        if (itemData == null) {
            return;
        }
        final ViewHolder holder = (ViewHolder) viewHolder;
        if (position == 0) {
            updateQuestionLayout(convertView, holder, itemData);
        } else {
            updateAnswerLayout(holder, itemData);
        }
        updateMaskImage(holder, itemData, position);
    }

    private void updateMaskImage(final ViewHolder holder, final JSONObject itemData, final int position) {
        if (position == 0) {
            updateMaskImageSize(holder.mMaskImage, holder.mQuestionLayout);
            holder.mMaskImage.setVisibility(View.GONE);
        } else {
            updateMaskImageSize(holder.mMaskImage, holder.mItemLayout);
            holder.mMaskImage.setVisibility(View.VISIBLE);
        }
        holder.mMaskImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    return;
                }
                if (position == 0) {
                    ((QuestionDetailActivity) mContext).showCommentsDialog(mQuestionId, null, "");
                } else {
                    ((QuestionDetailActivity) mContext).showCommentsDialog(mQuestionId,
                            itemData.optString(HttpConstants.Data.QuestionDetailList.AID),
                            itemData.optString(HttpConstants.Data.QuestionDetailList.ANSWER_NICK_NAME));
                    StatService.onEvent(mContext, BaiduStatConstants.QUESTION_ANSWER_CLICK,
                            BaiduStatConstants.QUESTION_ANSWER_CLICK);
                }
            }
        });
    }

    private void setShareinfo(final JSONObject itemData) {
        String title = mContext.getResources().getString(R.string.question)
                + itemData.optString(HttpConstants.Data.QuestionDetailList.TITLE);
        String content = itemData.optString(HttpConstants.Data.QuestionDetailList.CONTENT);
        String shareUrl = itemData.optString(HttpConstants.Data.QuestionDetailList.URL);
        JSONArray imageArray = itemData.optJSONArray(HttpConstants.Data.QuestionDetailList.IMAGES);
        int length = imageArray != null ? imageArray.length() : 0;
        String imageUrl = null;
        if (length > 0) {
            imageUrl = imageArray.optString(0);
        }
        if (TextUtils.isEmpty(content)) {
            content = title;
        }
        if (TextUtils.isEmpty(shareUrl)) {
            shareUrl = HTTP_GOU_GIONEE_COM;
        }
        ((QuestionDetailActivity) mContext).setShareInfo(title, content, imageUrl, shareUrl);
    }

    private void updateMaskImageSize(final ImageView maskImage, final RelativeLayout itemlayout) {
        maskImage.post(new Runnable() {
            @Override
            public void run() {
                int w = itemlayout.getMeasuredWidth();
                int h = itemlayout.getMeasuredHeight();
                maskImage.setLayoutParams(new RelativeLayout.LayoutParams(w, h));
            }
        });
    }

    private void updateAnswerLayout(final ViewHolder holder, final JSONObject itemData) {
        holder.mQuestionLayout.setVisibility(View.GONE);
        holder.mItemLayout.setVisibility(View.VISIBLE);
        holder.mPraiseCount.setVisibility(View.VISIBLE);
        updateTextview(holder.mAnswerNickName, itemData,
                HttpConstants.Data.QuestionDetailList.ANSWER_NICK_NAME, 0);
        updateAnswerAvatar(holder, itemData);
        updateTextview(holder.mReplyTime, itemData, HttpConstants.Data.QuestionDetailList.ANSWER_TIME, 0);
        updateReplyContent(holder, itemData);
        updateTextview(holder.mPraiseCount, itemData,
                HttpConstants.Data.QuestionDetailList.ANSWER_PRAISE_COUNT, 0);
        updatePraiseCountIcon(holder, itemData);
        updateJumpQuestionTitle(holder, itemData);
    }

    private void updateQuestionLayout(final View convertView, final ViewHolder holder,
            final JSONObject itemData) {
        updateQuestionAvatar(holder, itemData);
        updateTextview(holder.mQuestionNickName, itemData,
                HttpConstants.Data.QuestionDetailList.QUESTION_NICK_NAME, 0);
        updateTextview(holder.mQuestion, itemData, HttpConstants.Data.QuestionDetailList.TITLE, 0);
        updateDescription(holder, itemData);
        updateTextview(holder.mAnswerCount, itemData, HttpConstants.Data.QuestionDetailList.ANSWER_COUNT, 0);
        holder.mQuestionLayout.setVisibility(View.VISIBLE);
        holder.mItemLayout.setVisibility(View.GONE);
        holder.mPraiseCount.setVisibility(View.GONE);
        holder.mJumpQuestionTitleShow.setVisibility(View.GONE);
//            questionLayout.setOnTouchListener(mSearchEreaTouchListener);
        updateQuestionImages(convertView, holder, itemData);
        mVerifyCode = itemData.optString(HttpConstants.Data.QuestionDetailList.VERIFY_CODE);
        mQuestionId = itemData.optString(HttpConstants.Data.QuestionDetailList.QID);
        setShareinfo(itemData);
    }

    private void updateReplyContent(final ViewHolder holder, final JSONObject itemData) {
        String from = itemData.optString(HttpConstants.Data.QuestionDetailList.FROM);
        if (!TextUtils.isEmpty(from)) {
            String replyContent = itemData.optString(HttpConstants.Data.QuestionDetailList.ANSWER_CONTENT);
            StringBuilder builder = new StringBuilder().append(from).append(replyContent);
            holder.mReplyContent.setText(builder.toString());
        } else {
            updateTextview(holder.mReplyContent, itemData,
                    HttpConstants.Data.QuestionDetailList.ANSWER_CONTENT, 0);
        }
    }

    private void updateJumpQuestionTitle(final ViewHolder holder, final JSONObject itemData) {
        String jumpTitle = itemData.optString(HttpConstants.Data.QuestionDetailList.JUMP_TITLE);
        if (!TextUtils.isEmpty(jumpTitle)) {
            holder.mJumpQuestionTitle.setVisibility(View.VISIBLE);
            holder.mJumpQuestionTitleShow.setVisibility(View.VISIBLE);
            holder.mJumpQuestionTitleShow.setText(jumpTitle);
            holder.mJumpQuestionTitleShow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.log(TAG, LogUtils.getThreadName());
                    String quesionId = itemData.optString(HttpConstants.Data.QuestionDetailList.RID);
                    gotoQuestionDetailActvity(mContext, quesionId);
                }
            });
        } else {
            holder.mJumpQuestionTitle.setVisibility(View.GONE);
            holder.mJumpQuestionTitleShow.setVisibility(View.GONE);
        }
    }

    private static void gotoQuestionDetailActvity(Context context, String id) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " id: " + id);
        Intent intent = new Intent();
        intent.putExtra(HttpConstants.Data.QuestionList.ID, id);
        intent.setClass(context, QuestionDetailActivity.class);
        context.startActivity(intent);
        AndroidUtils.enterActvityAnim((Activity) context);
    }

    private void updateDescription(final ViewHolder holder, final JSONObject itemData) {
        String content = itemData.optString(HttpConstants.Data.QuestionDetailList.CONTENT);
        if (!TextUtils.isEmpty(content)) {
            holder.mContent.setVisibility(View.VISIBLE);
            updateTextview(holder.mContent, itemData, HttpConstants.Data.QuestionDetailList.CONTENT, 0);
        } else {
            holder.mContent.setVisibility(View.GONE);
        }
    }

    private class QuestionImagesListener implements ImageLoadingListener {
        private ViewHolder mViewHolder;

        public QuestionImagesListener(ViewHolder viewHolder) {
            super();
            this.mViewHolder = viewHolder;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            LogUtils.log(TAG, LogUtils.getThreadName());
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            LogUtils.log(TAG, LogUtils.getThreadName());
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            view.setVisibility(View.VISIBLE);
            updateMaskImageSize(mViewHolder.mMaskImage, mViewHolder.mQuestionLayout);
            LogUtils.log(
                    TAG,
                    LogUtils.getThreadName() + " width = " + view.getWidth() + " height = "
                            + view.getHeight());
            int viewWidth = AndroidUtils.getDisplayWidth(mContext) - AndroidUtils.dip2px(mContext, 30);
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

    private void updateQuestionImages(final View convertView, final ViewHolder holder,
            final JSONObject itemData) {
        holder.mPhotoUp.setVisibility(View.GONE);
        holder.mPhotoCenter.setVisibility(View.GONE);
        holder.mPhotoDown.setVisibility(View.GONE);
        JSONArray imagesArray = itemData.optJSONArray(HttpConstants.Data.QuestionDetailList.IMAGES);
        int length = imagesArray != null ? imagesArray.length() : 0;
        for (int i = 0; i < length; i++) {
            if (i == 3) {
                break;
            }
            String imageUrl = imagesArray.optString(i);
            ImageView image = holder.mPhotoUp;
            switch (i) {
                case 0:
                    image = holder.mPhotoUp;
                    break;
                case 1:
                    image = holder.mPhotoCenter;
                    break;
                case 2:
                    image = holder.mPhotoDown;
                    break;
                default:
                    break;
            }
            image.setImageResource(R.drawable.comment_img_default);
            GNImageLoader.getInstance().loadBitmap(imageUrl, image, new QuestionImagesListener(holder));
        }
    }

    private String getPraiseNum(String praiseNum, int pram) {
        int num = 0;
        try {
            num = Integer.parseInt(praiseNum);
        } catch (NumberFormatException e) {
            return praiseNum;
        }
        num = num + pram;
        if (0 == num) {
            return "";
        }
        if (num > SHOW_NUM_MAX) {
            return "999+";
        }
        return String.valueOf(num);
    }

    private void updatePraiseCountIcon(final ViewHolder holder, final JSONObject itemData) {
        holder.mPraiseCount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                try {
                    if (mRequestAction == null) {
                        mRequestAction = new RequestAction();
                    }
                    holder.mPraiseCount.setClickable(false);
                    mRequestAction.answerPraise(QuestionDetailListAdapter.this, null,
                            itemData.optString(HttpConstants.Data.QuestionDetailList.AID));
                    showPraiseAnim(holder);
                } catch (Exception e) {
                    LogUtils.loge(TAG, LogUtils.getThreadName() + e);
                    e.printStackTrace();
                }
            }
        });
        holder.mPraiseCount.setText(getPraiseNum(
                itemData.optString(HttpConstants.Data.QuestionDetailList.ANSWER_PRAISE_COUNT), 0));
        holder.mPraiseCount.setSelected(false);
        String id = itemData.optString(HttpConstants.Data.QuestionDetailList.AID);
        if (mPraisedList.contains(id)) {
            LogUtils.log(TAG, LogUtils.getThreadName() + " contains id: " + id);
            holder.mPraiseCount.setText(getPraiseNum(
                    itemData.optString(HttpConstants.Data.QuestionDetailList.ANSWER_PRAISE_COUNT), 1));
            holder.mPraiseCount.setClickable(false);
            holder.mPraiseCount.setSelected(true);
        }
    }

    private void showPraiseAnim(final ViewHolder holder) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.discuss_praised_add);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                holder.mPraiseCountAnim.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                holder.mPraiseCountAnim.setVisibility(View.GONE);
            }
        });
        holder.mPraiseCountAnim.startAnimation(animation);
    }

    private void updateQuestionAvatar(final Object viewHolder, final JSONObject itemData) {
        ((ViewHolder) viewHolder).mQuestionAvatar.setImageResource(R.drawable.head_default);
        final String urlStr = itemData.optString(HttpConstants.Data.QuestionDetailList.QUESTION_AVATAR);
        if (!TextUtils.isEmpty(urlStr)) {
            GNImageLoader.getInstance().loadBitmap(urlStr, ((ViewHolder) viewHolder).mQuestionAvatar);
        }
    }

    private void updateAnswerAvatar(final Object viewHolder, final JSONObject itemData) {
        ((ViewHolder) viewHolder).mReplyAvatar.setImageResource(R.drawable.head_default);
        final String urlStr = itemData.optString(HttpConstants.Data.QuestionDetailList.ANSWER_AVATAR);
        if (!TextUtils.isEmpty(urlStr)) {
            GNImageLoader.getInstance().loadBitmap(urlStr, ((ViewHolder) viewHolder).mReplyAvatar);
        }
    }

    private static class ViewHolder {
        /**
         * item 布局
         */
        public RelativeLayout mItemLayout;

        /**
         * 问题布局区域
         */
        public RelativeLayout mQuestionLayout;

        /**
         * 蒙板
         */
        public ImageView mMaskImage;

        /**
         * 头像
         */
        public CircleImageView mQuestionAvatar;

        /**
         * 昵称
         */
        public TextView mQuestionNickName;

        /**
         * 问题标题
         */
        public TextView mQuestion;

        /**
         * 问题描述
         */
        public TextView mContent;

        /**
         * 回答数
         */
        public TextView mAnswerCount;

        public ImageView mPhotoUp;

        public ImageView mPhotoCenter;

        public ImageView mPhotoDown;

        /**
         * 回答者昵称
         */
        public TextView mAnswerNickName;

        /**
         * 头像
         */
        public CircleImageView mReplyAvatar;

        /**
         * 回贴时间
         */
        public TextView mReplyTime;

        /**
         * 回贴内容
         */
        public TextView mReplyContent;

        /**
         * 跳转问题id(空文本，用于占位)
         */
        public TextView mJumpQuestionTitle;

        /**
         * 跳转问题id（真实的显示文本）
         */
        public TextView mJumpQuestionTitleShow;

        /**
         * 赞数
         */
        public TextView mPraiseCount;

        /**
         * 赞数
         */
        public TextView mPraiseCountAnim;

    }
}
