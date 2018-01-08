package com.gionee.client.activity.question;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.widget.CircleImageView;
import com.gionee.framework.operation.net.GNImageLoader;

public class FAQsAdapter extends BaseAdapter implements OnItemClickListener {
    private JSONArray mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean mIsAnswerMode = false;
    private static final String TAG = "FAQsAdapter";

    public FAQsAdapter(Context context, boolean answerMode) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mIsAnswerMode = answerMode;
    }

    public void updateData(JSONArray array) {
        mData = array;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mData != null ? mData.length() : 0);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.answers_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mLable = (TextView) convertView.findViewById(R.id.answers_lable);
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.answers_title);
            viewHolder.mLayout = (RelativeLayout) convertView.findViewById(R.id.answers_content);
            viewHolder.mStatus = (TextView) convertView.findViewById(R.id.answers_questions_status);
            viewHolder.mDetails = (TextView) convertView.findViewById(R.id.answer_quesiton_details);
            viewHolder.mTotals = (TextView) convertView.findViewById(R.id.answers_questions_total);
            viewHolder.mIcon = (CircleImageView) convertView.findViewById(R.id.answers_avatar);
            viewHolder.mNickName = (TextView) convertView.findViewById(R.id.ansers_nickname);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mIsAnswerMode) {
            setAnswerModeView(position, viewHolder);
        } else {
            setQuestionModeView(position, viewHolder);
        }
        return convertView;
    }

    private void setQuestionModeView(int position, ViewHolder viewHolder) {
        JSONObject object = mData.optJSONObject(position);
        setQuestionItemTitle(viewHolder, object);
        int status = object.optInt(HttpConstants.Data.FAQsList.STATUS_I);
        viewHolder.mTotals.setVisibility(View.GONE);
        viewHolder.mStatus.setVisibility(View.GONE);
        switch (status) {
            case FAQsState.SAVE:
            case FAQsState.CHECK:
                setQuestionCheckStateView(viewHolder, object);
                break;
            case FAQsState.APPROVED:
                setQuetionsApprovedStateView(viewHolder, object);
                break;
            case FAQsState.VERIFY_FAILED:
                setQuestionVerifyFailedView(viewHolder, object);
                break;
            default:
                break;
        }
    }

    private void setQuestionVerifyFailedView(ViewHolder viewHolder, JSONObject object) {
        viewHolder.mStatus.setText(object.optString(HttpConstants.Data.FAQsList.STATUS_LABEL_S));
        viewHolder.mStatus.setVisibility(View.VISIBLE);
        viewHolder.mTotals.setVisibility(View.GONE);
        viewHolder.mStatus.setTextColor(mContext.getResources().getColor(R.color.verify_failed));
        viewHolder.mTitle.setTextColor(mContext.getResources().getColor(R.color.verify_failed));
        setQustiongFaqsDetailsView(viewHolder, object);
    }

    private void setQuetionsApprovedStateView(ViewHolder viewHolder, JSONObject object) {
        viewHolder.mTotals.setText(object.optString(HttpConstants.Data.FAQsList.ANS_TOTAL_S));
        viewHolder.mTotals.setVisibility(View.VISIBLE);
        viewHolder.mStatus.setVisibility(View.GONE);
        setQustiongFaqsDetailsView(viewHolder, object);
    }

    private void setQuestionCheckStateView(ViewHolder viewHolder, JSONObject object) {
        viewHolder.mStatus.setText(object.optString(HttpConstants.Data.FAQsList.STATUS_LABEL_S));
        viewHolder.mStatus.setVisibility(View.VISIBLE);
        viewHolder.mStatus.setTextColor(mContext.getResources().getColor(R.color.discuss_praise));
        viewHolder.mTotals.setVisibility(View.GONE);
        setQustiongFaqsDetailsView(viewHolder, object);
    }

    private void setQustiongFaqsDetailsView(ViewHolder viewHolder, JSONObject object) {
        JSONArray answerArray = object.optJSONArray(HttpConstants.Data.FAQsList.ANSWER_CONTENT_LIST_S);
        if (null == answerArray || answerArray.length() < 1) {
            return;
        }
        JSONObject content = answerArray.optJSONObject(0);
        String details = content.optString(HttpConstants.Data.FAQsList.ANSWER_CONTENT);
        String nickname = content.optString(HttpConstants.Data.FAQsList.AUTHOR_NICKNAME_S);
        String avatar = content.optString(HttpConstants.Data.FAQsList.AUTHOR_AVATAR_S);

        GNImageLoader.getInstance().loadBitmap(avatar, viewHolder.mIcon);
        viewHolder.mNickName.setText(nickname);
        viewHolder.mDetails.setText(details);
        setItemStateNote(viewHolder, details, nickname, avatar);
    }

    private void setAnswerModeView(int position, ViewHolder viewHolder) {
        JSONObject object = mData.optJSONObject(position);
        setAnsweItemTitle(viewHolder, object);
        int status = object.optInt(HttpConstants.Data.FAQsList.STATUS_I);
        viewHolder.mTotals.setVisibility(View.GONE);
        viewHolder.mStatus.setVisibility(View.GONE);
        switch (status) {
            case FAQsState.SAVE:
            case FAQsState.CHECK:
                setAnswerCheckStateView(viewHolder, object);
                break;
            case FAQsState.APPROVED:
                setAnswerApprovedStateView(viewHolder, object);
                break;
            case FAQsState.VERIFY_FAILED:
                setAnswerVerifyFailedView(viewHolder, object);
                break;
            default:
                break;
        }
    }

    private void setAnswerVerifyFailedView(ViewHolder viewHolder, JSONObject object) {
        viewHolder.mStatus.setText(object.optString(HttpConstants.Data.FAQsList.STATUS_LABEL_S));
        viewHolder.mStatus.setVisibility(View.VISIBLE);
        viewHolder.mTotals.setVisibility(View.GONE);
        viewHolder.mStatus.setTextColor(mContext.getResources().getColor(R.color.verify_failed));
        viewHolder.mStatus.setVisibility(View.VISIBLE);
        viewHolder.mStatus.setText(object.optString(HttpConstants.Data.FAQsList.REASON_S));
        setAnswerFaqsDetailsView(viewHolder, object);
    }

    private void setAnswerApprovedStateView(ViewHolder viewHolder, JSONObject object) {
        viewHolder.mTotals.setText(object.optString(HttpConstants.Data.FAQsList.ANS_TOTAL_S));
        viewHolder.mStatus.setVisibility(View.GONE);
        setAnswerFaqsDetailsView(viewHolder, object);
    }

    private void setAnswerCheckStateView(ViewHolder viewHolder, JSONObject object) {
        viewHolder.mStatus.setText(object.optString(HttpConstants.Data.FAQsList.STATUS_LABEL_S));
        viewHolder.mStatus.setTextColor(mContext.getResources().getColor(R.color.discuss_praise));
        viewHolder.mTotals.setVisibility(View.GONE);
        viewHolder.mTotals.setVisibility(View.GONE);
        setAnswerFaqsDetailsView(viewHolder, object);
    }

    private void setAnswerFaqsDetailsView(ViewHolder viewHolder, JSONObject object) {

        String details = object.optString(HttpConstants.Data.FAQsList.TITLE_S);
        String nickname = object.optString(HttpConstants.Data.FAQsList.AUTHOR_NICKNAME_S);
        String avatar = object.optString(HttpConstants.Data.FAQsList.AUTHOR_AVATAR_S);

        GNImageLoader.getInstance().loadBitmap(avatar, viewHolder.mIcon);
        viewHolder.mNickName.setText(nickname);
        viewHolder.mDetails.setText(details);
        setItemStateNote(viewHolder, details, nickname, avatar);
    }

    private void setItemStateNote(ViewHolder viewHolder, String details, String nickname, String avatar) {
        if (hideDetailsView(details, nickname, avatar)) {
            viewHolder.mLayout.setVisibility(View.GONE);
        } else {
            viewHolder.mLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setAnsweItemTitle(ViewHolder viewHolder, JSONObject object) {
        viewHolder.mTitle.setTextColor(mContext.getResources().getColor(R.color.comments_text_color));
        viewHolder.mLable.setText(object.optString(HttpConstants.Data.FAQsList.FROM_S));
        viewHolder.mLable.setVisibility(View.VISIBLE);
        viewHolder.mTitle.setText(object.optString(HttpConstants.Data.FAQsList.ANSWER_CONTENT));
    }

    private void setQuestionItemTitle(ViewHolder viewHolder, JSONObject object) {
        viewHolder.mTitle.setTextColor(mContext.getResources().getColor(R.color.comments_text_color));
        viewHolder.mTitle.setText(object.optString(HttpConstants.Data.FAQsList.TITLE_S));
    }

    private boolean hideDetailsView(String details, String nickname, String avatar) {
        return TextUtils.isEmpty(details) && TextUtils.isEmpty(nickname) && TextUtils.isEmpty(avatar);
    }

    private static class ViewHolder {
        TextView mLable;
        TextView mTitle;
        TextView mDetails;
        TextView mStatus;
        TextView mTotals;
        CircleImageView mIcon;
        TextView mNickName;
        RelativeLayout mLayout;
    }

    private static class FAQsState {
        public static final int CHECK = 1;
        public static final int SAVE = 0;
        public static final int APPROVED = 2;
        public static final int VERIFY_FAILED = 3;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mIsAnswerMode) {
            int status = mData.optJSONObject(position - 1).optInt(HttpConstants.Data.FAQsList.STATUS_I);
            LogUtils.log(TAG, "status:" + status);
            if (FAQsState.VERIFY_FAILED == status) {
                return;
            }
        }
        Intent intent = new Intent();
        intent.putExtra(HttpConstants.Response.ID_I,
                mData.optJSONObject(position - 1).optString(HttpConstants.Data.FAQsList.QUSTION_ID_I));
        intent.setClass(mContext, QuestionDetailActivity.class);
        mContext.startActivity(intent);
    }

}
