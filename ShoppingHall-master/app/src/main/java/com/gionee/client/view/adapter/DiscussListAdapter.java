package com.gionee.client.view.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.activity.story.GNDiscussDetailsActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.utills.JSONArrayHelper;

public class DiscussListAdapter extends BaseAdapter {
    private static final int DIVIDEND = 10000;
    private static final int SHOW_NUM_MAX = 9999;
    private JSONArray mJsonArray;
    private Context mContext;
    private OnClickListener mListener;
    private List<String> mPraisedList = new ArrayList<String>();

    public DiscussListAdapter(Context context) {
        mContext = context;
    }

    public DiscussListAdapter(Context context, View.OnClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setData(JSONArray jsonArray) {
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mJsonArray != null ? mJsonArray.length() : 0);
    }

    @Override
    public Object getItem(int position) {
        return mJsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.discuss_item, null);
            holder = new ViewHolder();
            holder.mContent = (TextView) convertView.findViewById(R.id.discuss_content);
            holder.mName = (TextView) convertView.findViewById(R.id.discuss_name);
            holder.mTime = (TextView) convertView.findViewById(R.id.discuss_time);
            holder.mPraiseNum = (TextView) convertView.findViewById(R.id.discuss_praise_num);
            holder.mPraiseImg = (ImageView) convertView.findViewById(R.id.discuss_praise_img);
            holder.mPraiseAnim = (TextView) convertView.findViewById(R.id.discuss_praiseAnim);
            holder.mPraise = (RelativeLayout) convertView.findViewById(R.id.discuss_praise);
            holder.mHeadImg = (ImageView) convertView.findViewById(R.id.user_head);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject mObject = mJsonArray.optJSONObject(position);
        updateView(holder, mObject);
        return convertView;
    }

    private void updateView(final ViewHolder holder, final JSONObject mObject) {
        try {

            holder.mTime.setText(mObject.optString(HttpConstants.Data.DiscussList.TIME));
            holder.mName.setText(mObject.optString(HttpConstants.Data.DiscussList.NIKE_NAME));
            setPrise(holder, mObject);
            setAvatar(holder, mObject);
            setCommentContent(holder, mObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPrise(final ViewHolder holder, final JSONObject mObject) {
        holder.mPraiseNum.setText(getPraiseNum(mObject.optString(HttpConstants.Data.DiscussList.PRAISE), 0));
        holder.mPraiseImg.setSelected(false);
        setPraiseListener(holder, mObject);
        if (mPraisedList.contains(mObject.optString(HttpConstants.Data.DiscussList.ID))) {
            holder.mPraiseNum.setText(getPraiseNum(mObject.optString(HttpConstants.Data.DiscussList.PRAISE),
                    1));
            holder.mPraiseImg.setSelected(true);
            holder.mPraise.setClickable(false);
        }
    }

    public void setAvatar(final ViewHolder holder, final JSONObject mObject) {
        holder.mHeadImg.setImageResource(R.drawable.head_default);
        String avatar = mObject.optString(HttpConstants.Response.UserInfo.AVATAR_S);
        if (!TextUtils.isEmpty(avatar)) {
            GNImageLoader.getInstance().loadBitmapWithDefualt(avatar, holder.mHeadImg,
                    R.drawable.head_default);
        }
    }

    public void setCommentContent(final ViewHolder holder, final JSONObject mObject) {
        String contantString = mObject.optString(HttpConstants.Data.DiscussList.CONTENT);
        String answerUser = mObject.optString(HttpConstants.Data.MessageList.FROM_S);
//            String answerUser = mContext.getString(R.string.about_shoppingmall);
        if (TextUtils.isEmpty(answerUser)) {
            holder.mContent.setText(contantString);
        } else {
            contantString = answerUser + contantString;
            SpannableString spanContent = new SpannableString(contantString);
            spanContent.setSpan(
                    new ForegroundColorSpan(mContext.getResources().getColor(R.color.comments_text_color)),
                    0, answerUser.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.mContent.setText(spanContent);
        }
    }

    private void setPraiseListener(final ViewHolder holder, final JSONObject mObject) {
        holder.mPraise.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AndroidUtils.getNetworkType((Activity) mContext) == Constants.NET_UNABLE) {
                    ((GNDiscussDetailsActivity) mContext).showNetErrorToast();
                    return;
                }
                MyBean bean = MyBeanFactory.createEmptyBean();
                bean.put(HttpConstants.Data.DiscussList.ID,
                        mObject.optString(HttpConstants.Data.DiscussList.ID));
                v.setTag(bean);
                holder.mPraise.setClickable(false);
                mListener.onClick(v);
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.discuss_praised_add);
                animation.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        holder.mPraiseAnim.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.mPraiseAnim.setVisibility(View.GONE);
                    }
                });
                holder.mPraiseAnim.startAnimation(animation);
            }
        });
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
            return ((num / DIVIDEND) + "万+");
        }
        return String.valueOf(num);
    }

    private static class ViewHolder {
        public TextView mName;
        public TextView mTime;
        public TextView mContent;
        public TextView mPraiseNum;
        public ImageView mPraiseImg;
        public TextView mPraiseAnim;
        public RelativeLayout mPraise;
        public ImageView mHeadImg;
    }

    public boolean addItem(JSONObject object) {
        try {
            JSONArrayHelper helper = new JSONArrayHelper(mJsonArray);
            helper.add(0, object);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void updateItemView(String position) {
        mPraisedList.add(position);
        notifyDataSetChanged();
    }

    public void clearPraisedData() {
        if (null == mPraisedList) {
            return;
        }
        mPraisedList.clear();
    }
}
