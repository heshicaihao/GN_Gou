/*
 * SearchQuestionResultAdapter.java
 * classes : com.gionee.client.activity.question.SearchQuestionResultAdapter
 * @author yuwei
 * 
 * Create at 2015-3-31 上午11:34:41
 */
package com.gionee.client.activity.question;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.model.HttpConstants;

/**
 * com.gionee.client.activity.question.SearchQuestionResultAdapter
 * 
 * @author yuwei <br/>
 *         create at 2015-3-31 上午11:34:41
 * @description
 */
public class SearchQuestionResultAdapter extends BaseAdapter {

    private Context mContext;
    private JSONArray mQuestionList;

    public void setmQuestionArray(JSONArray questionList) {
        this.mQuestionList = questionList;
    }

    @Override
    public int getCount() {
        return mQuestionList == null ? 0 : mQuestionList.length();
    }

    public SearchQuestionResultAdapter(Context mContext, JSONArray mQuestionList) {
        super();
        this.mContext = mContext;
        this.mQuestionList = mQuestionList;
    }

    @Override
    public Object getItem(int arg0) {
        return mQuestionList.opt(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_question_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mQuestionTitle = (TextView) convertView.findViewById(R.id.question_title);
            viewHolder.mAnswerCount = (TextView) convertView.findViewById(R.id.answer_count);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        bindData(position, viewHolder);
        return convertView;
    }

    public void bindData(int position, ViewHolder viewHolder) {
        try {
            JSONObject mQuestionItem;
            mQuestionItem = mQuestionList.optJSONObject(position);
            viewHolder.mQuestionTitle.setText(mQuestionItem
                    .optString(HttpConstants.Response.SearchQuestionList.TITLE));
            String answerCount = String.format(mContext.getString(R.string.question_answer_count),
                    mQuestionItem.optString(HttpConstants.Response.SearchQuestionList.ANS_TOTAL));
            viewHolder.mAnswerCount.setText(answerCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final class ViewHolder {
        public TextView mQuestionTitle;
        public TextView mAnswerCount;
    }
}
