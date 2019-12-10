/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-3-30 下午03:53:06
 */
package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.question.QuestionDetailActivity;
import com.gionee.client.activity.question.SearchQuestion;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.widget.CircleImageView;
import com.gionee.framework.operation.net.GNImageLoader;

/**
 * com.gionee.client.view.adapter.QuestionListAdapter
 * 
 * @author yangxiong <br/>
 * @date create at 2015-3-30 下午03:53:06
 * @description TODO 问答列表页适配
 */
public class QuestionListAdapter extends AbstractListBaseAdapter {
    private static final String TAG = "QuestionListAdapter";

    public QuestionListAdapter(Context mContext, int itemLayoutId) {
        super(mContext, itemLayoutId);
    }

    @Override
    protected Object initViewHolder(View convertView) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        final ViewHolder holder;
        holder = new ViewHolder();
        holder.mNickName = (TextView) convertView.findViewById(R.id.nick_name);
        holder.mQuestion = (TextView) convertView.findViewById(R.id.question);
        holder.mAnswerCount = (TextView) convertView.findViewById(R.id.answer_count);
        holder.mReplyNickName = (TextView) convertView.findViewById(R.id.replier_nickname);
        holder.mReplyContent = (TextView) convertView.findViewById(R.id.reply_content);
        holder.mMaskImage = (ImageView) convertView.findViewById(R.id.item_click_image);
        holder.mItemLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
        holder.mPortraitIcon = (CircleImageView) convertView.findViewById(R.id.portrait);
        holder.mSearchArea = (RelativeLayout) convertView.findViewById(R.id.search);
        holder.mSearchLayout = (RelativeLayout) convertView.findViewById(R.id.search_layout);
        return holder;
    }

    @Override
    protected void updateView(View convertView, Object viewHolder, JSONObject itemData, int position) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " position = " + position + ", itemData = " + itemData);
        if (itemData == null) {
            return;
        }
        ViewHolder holder = (ViewHolder) viewHolder;
        updateTextview(holder.mNickName, itemData, HttpConstants.Data.QuestionList.NICK_NAME, 0);
        updateTextview(holder.mQuestion, itemData, HttpConstants.Data.QuestionList.TITLE, 0);
        updateTextview(holder.mAnswerCount, itemData, HttpConstants.Data.QuestionList.ANSWER_COUNT, 0);
        updateFirstReplay(itemData, holder);
        udpatePortraitIcon(viewHolder, itemData);
        updateMaskImage(holder.mMaskImage, holder.mItemLayout, itemData, position);
        updateSearchQuestionLayout(holder, position);
    }

    private void updateFirstReplay(JSONObject itemData, ViewHolder holder) {
        JSONArray replyArray = itemData.optJSONArray(HttpConstants.Data.QuestionList.ANSWER_LIST);
        holder.mReplyNickName.setVisibility(View.GONE);
        if (replyArray == null) {
            holder.mReplyContent.setText(R.string.grab_sofa);
            return;
        }
        JSONObject replyData = replyArray.optJSONObject(0);
        if (replyData == null) {
            holder.mReplyContent.setText(R.string.grab_sofa);
            return;
        }
        String nickname = replyData.optString(HttpConstants.Data.QuestionList.ANSWER_NICK_NAME);
        if (!TextUtils.isEmpty(nickname)) {
            holder.mReplyNickName.setVisibility(View.VISIBLE);
            updateTextview(holder.mReplyNickName, replyData,
                    HttpConstants.Data.QuestionList.ANSWER_NICK_NAME, R.string.nick_name_format);
        }
        String replyContent = replyData.optString(HttpConstants.Data.QuestionList.ANSWER_CONTENT);
        if (!TextUtils.isEmpty(replyContent)) {
            updateTextview(holder.mReplyContent, replyData, HttpConstants.Data.QuestionList.ANSWER_CONTENT, 0);
        } else {
            holder.mReplyContent.setText(R.string.grab_sofa);
        }
    }

    private void updateMaskImage(final ImageView maskImage, final RelativeLayout itemlayout,
            final JSONObject itemData, final int position) {
        final String id = getQuestionId(itemData);
        updateMaskImageSize(maskImage, itemlayout);
        maskImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    return;
                }
                gotoQuestionDetailActvity(mContext, id);
                StatService.onEvent(mContext, BaiduStatConstants.QUESTION_LIST_CLICK, "id_" + id);
                StatService.onEvent(mContext, BaiduStatConstants.QUESTION_LIST_CLICK,
                        Integer.toString(position));
            }
        });
    }

    private String getQuestionId(final JSONObject itemData) {
        final String id = itemData.optString(HttpConstants.Data.QuestionList.ID);
        return id;
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

    private OnClickListener mSearchEreaClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            gotoSearchQuestionActvity(mContext);
            StatService.onEvent(mContext, BaiduStatConstants.QUESTIONG_SEARCH,
                    BaiduStatConstants.QUESTIONG_SEARCH);
        }
    };

    private void updateSearchQuestionLayout(final ViewHolder viewHolder, int position) {
        if (position == 0) {
            viewHolder.mSearchLayout.setVisibility(View.VISIBLE);
            viewHolder.mSearchArea.setOnClickListener(mSearchEreaClickListener);
        } else {
            viewHolder.mSearchLayout.setVisibility(View.GONE);
        }
    }

    private static void gotoQuestionDetailActvity(Context context, String id) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " id: " + id);
        Intent intent = new Intent();
        intent.putExtra(HttpConstants.Data.QuestionList.ID, id);
        intent.setClass(context, QuestionDetailActivity.class);
        ((Activity) context).startActivityForResult(intent,
                Constants.ActivityRequestCode.REQUEST_CODE_QUESTION_DETAIL);
        AndroidUtils.enterActvityAnim((Activity) context);
    }

    private void udpatePortraitIcon(final Object viewHolder, final JSONObject itemData) {
        ((ViewHolder) viewHolder).mPortraitIcon.setImageResource(R.drawable.head_default);
        final String urlStr = itemData.optString(HttpConstants.Data.QuestionList.AVATAR);
        if (!TextUtils.isEmpty(urlStr)) {
            GNImageLoader.getInstance().loadBitmap(urlStr, ((ViewHolder) viewHolder).mPortraitIcon);
        }
    }

    private static void gotoSearchQuestionActvity(Context context) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        Intent intent = new Intent();
        intent.setClass(context, SearchQuestion.class);
        context.startActivity(intent);
        AndroidUtils.enterActvityAnim((Activity) context);
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
         * 昵称
         */
        public TextView mNickName;

        /**
         * 问题
         */
        public TextView mQuestion;

        /**
         * 回答数
         */
        public TextView mAnswerCount;

        /**
         * 回答者昵称
         */
        public TextView mReplyNickName;

        /**
         * 第一条回答
         */
        public TextView mReplyContent;

        /**
         * 头像
         */
        public CircleImageView mPortraitIcon;

        /**
         * 搜索问题区
         */
        public RelativeLayout mSearchArea;

        public RelativeLayout mSearchLayout;
    }
}
