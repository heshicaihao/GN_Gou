// Gionee <yuwei><2013-8-30> add for CR00821559 begin
/*
 * ReplyListAdapter.java
 * classes : com.gionee.client.view.adapter.ReplyListAdapter
 * @author yuwei
 * V 1.0.0
 * Create at 2013-8-30 下午2:34:25
 */
package com.gionee.client.view.adapter;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.feedback.GNContactActivity;
import com.gionee.client.activity.feedback.GNConversationActivity;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Config;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.widget.CircleImageView;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.utills.JSONArrayHelper;
import com.google.zxing.qrcode.encoder.Encoder;

/**
 * com.gionee.client.view.adapter.ReplyListAdapter
 * 
 * @author yuwei <br/>
 * @date create at 2013-8-30 下午2:34:25
 * @description TODO
 */

public class ReplyListAdapter extends BaseAdapter {
    private final String TAG = "ReplyListAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    public JSONArray mConversationList;
    private static final int RESPONSE_TYPE = 1;
    private static final int FEEDBACK_TYPE = 0;
    private Map<Integer, Integer> mLoadingStatusMap;
    private int paddingMin;
    private int paddingMax;

    public ReplyListAdapter(Context context, JSONArray conversation) {
        this.mContext = context;
        this.mLoadingStatusMap = new HashMap<Integer, Integer>();
        this.mConversationList = conversation;

        mInflater = LayoutInflater.from(mContext);
        paddingMin = AndroidUtils.dip2px(mContext, 10);
        paddingMax = AndroidUtils.dip2px(mContext, 20);
        addContentToList(mContext.getString(R.string.feedback_notify), true, RESPONSE_TYPE);
    }

    @Override
    public int getCount() {
        return (mConversationList == null) ? 0 : mConversationList.length();
    }

    public int addReplayConversationItem(String content, boolean isAhead) {
        return addConversationItem(content, isAhead, RESPONSE_TYPE);
    }

    public int addFeedbackConversationItem(String content, boolean isAhead) {
        return addConversationItem(content, isAhead, FEEDBACK_TYPE);
    }

    private int addConversationItem(String content, boolean isAhead, int type) {

        boolean addSuccess = addContentToList(content, isAhead, type);
        if (addSuccess) {
            notifyDataSetChanged();
            mLoadingStatusMap.put(mConversationList.length() - 1,
                    type == RESPONSE_TYPE ? Constants.STATUS_COMPLETE : Constants.STATUS_LOADING);
            return mConversationList.length();
        }
        return 0;

    }

    public boolean addContentToList(String content, boolean isAhead, int type) {
        try {

            if (mConversationList == null) {
                mConversationList = new JSONArray();
            }
            if (isAhead) {
                String conversationStr = "{content:" + URLEncoder.encode(content) + ",status:1,type: " + type
                        + "}";
                JSONObject newObject = new JSONObject(conversationStr);
                new JSONArrayHelper(mConversationList).add(0, newObject);
            } else {
                String conversationStr = "{content:" + URLEncoder.encode(content) + ",status:1,type: " + type
                        + "}";
                JSONObject newObject = new JSONObject(conversationStr);
                new JSONArrayHelper(mConversationList).addToLast(newObject);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateProgress(int position, int status) {
        mLoadingStatusMap.put(position - 1, status);
    }

    public void updateList(JSONArray conversation) {
        mConversationList = conversation;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.umeng_fb_list_item, null);
            holder = new ViewHolder();
            holder.mReplyContent = (TextView) convertView.findViewById(R.id.umeng_fb_reply_content);
            holder.mErrorImage = (ImageView) convertView.findViewById(R.id.send_error);
            holder.mServiceHeadImg = (CircleImageView) convertView.findViewById(R.id.service_head);
            holder.mUserHeadImg = (CircleImageView) convertView.findViewById(R.id.user_head);
            holder.mSendProgress = (ProgressBar) convertView.findViewById(R.id.loading_bar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        hideLoadingAndError(holder);
        JSONObject conversationData = mConversationList.optJSONObject(position);
        updateView(position, holder, conversationData);
        return convertView;
    }

    public void updateView(int position, ViewHolder holder, JSONObject conversationData) {
        try {

            String content = URLDecoder.decode(conversationData.optString(HttpConstants.Request.CONTENT));
            JSONObject linkObject = conversationData.optJSONObject(HttpConstants.Request.LINK);
            String linkStr = "";
            String linkUrl = "";
            if (linkObject != null) {
                String linkContent = linkObject.optString(HttpConstants.Request.NAME);
                linkUrl = linkObject.optString(HttpConstants.Request.URL);
                linkStr = "\n" + linkContent;
            }
            final String finalLinkUrl = linkUrl;
            SpannableString spanContent = new SpannableString(content + linkStr);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    ((GNConversationActivity) mContext).gotoWebPage(finalLinkUrl, true);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(mContext.getResources().getColor(R.color.discuss_praise));
                    ds.setUnderlineText(false);
                    ds.clearShadowLayer();
                }
            };
            spanContent.setSpan(clickableSpan, content.length(), content.length() + linkStr.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int type = conversationData.optInt(HttpConstants.Response.TYPE_I);
            holder.mReplyContent.getBackground().setLevel(type);
            if (type == FEEDBACK_TYPE) {
                setUserItemView(position, holder);
                holder.mReplyContent.setText(URLDecoder.decode(content));
            } else if (type == RESPONSE_TYPE) {
                setServiceItemView(holder);
                holder.mReplyContent.setText(spanContent);
                holder.mReplyContent.setMovementMethod(LinkMovementMethod.getInstance());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setServiceItemView(ViewHolder holder) {
        showServiceHead(holder);
        hideLoadingAndError(holder);
        holder.mReplyContent.setPadding(paddingMax, paddingMin, paddingMin, paddingMin);
        GNImageLoader.getInstance().loadBitmap(UserInfoManager.getInstance().getServiceAvater(),
                holder.mServiceHeadImg);
    }

    @SuppressWarnings("rawtypes")
    public boolean isNotLoading() {
        try {
            Iterator mapSetIterator = mLoadingStatusMap.entrySet().iterator();
            while (mapSetIterator.hasNext()) {
                Map.Entry entity = (Entry) mapSetIterator.next();
                Integer status = (Integer) entity.getValue();
                if (status.intValue() == Constants.STATUS_LOADING) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void setUserItemView(int position, ViewHolder holder) {
        showUserHead(holder);
        GNImageLoader.getInstance().loadBitmap(UserInfoManager.getInstance().getAvatar(mContext),
                holder.mUserHeadImg);
        holder.mReplyContent.setPadding(paddingMin, paddingMin, paddingMax, paddingMin);
        setSendStatus(position, holder);
    }

    public void setSendStatus(int position, ViewHolder holder) {
        try {
            int status = mLoadingStatusMap.get((Integer) position);
            switch (status) {
                case Constants.STATUS_COMPLETE:
                    hideLoadingAndError(holder);
                    break;
                case Constants.STATUS_ERROR:
                    holder.mSendProgress.setVisibility(View.GONE);
                    holder.mErrorImage.setVisibility(View.VISIBLE);
                    break;
                case Constants.STATUS_LOADING:
                    holder.mSendProgress.setVisibility(View.VISIBLE);
                    holder.mErrorImage.setVisibility(View.GONE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideLoadingAndError(ViewHolder holder) {
        holder.mSendProgress.setVisibility(View.GONE);
        holder.mErrorImage.setVisibility(View.GONE);
    }

    public void showServiceHead(ViewHolder holder) {
        holder.mServiceHeadImg.setVisibility(View.VISIBLE);
        holder.mUserHeadImg.setVisibility(View.GONE);
    }

    public void showUserHead(ViewHolder holder) {
        holder.mServiceHeadImg.setVisibility(View.GONE);
        holder.mUserHeadImg.setVisibility(View.VISIBLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return mConversationList.opt(position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        public TextView mReplyContent;
        public CircleImageView mServiceHeadImg;
        public CircleImageView mUserHeadImg;
        public ProgressBar mSendProgress;
        public ImageView mErrorImage;

    }
}
//Gionee <yuwei><2013-8-30> add for CR00821559 end