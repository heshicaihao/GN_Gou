/*
 * SearchQuestionHistoryAdapter.java
 * classes : com.gionee.client.activity.question.SearchQuestionHistoryAdapter
 * @author yuwei
 * 
 * Create at 2015-3-31 上午11:35:06
 */
package com.gionee.client.activity.question;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.client.R;

/**
 * com.gionee.client.activity.question.SearchQuestionHistoryAdapter
 * 
 * @author yuwei <br/>
 *         create at 2015-3-31 上午11:35:06
 * @description
 */
public class SearchQuestionHistoryAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mHistoryList;
    private static final int MAX_COUNT = 10;

    @Override
    public int getCount() {
        if (mHistoryList == null) {
            return 0;
        }
        if (mHistoryList.length > MAX_COUNT) {
            return MAX_COUNT + 1;
        }
        return mHistoryList.length + 1;
    }

    public SearchQuestionHistoryAdapter(Context mContext, String[] mHistoryList) {
        super();
        this.mContext = mContext;
        this.mHistoryList = mHistoryList;
    }

    @Override
    public Object getItem(int arg0) {
        if (arg0 == mHistoryList.length) {
            return "";
        }
        return mHistoryList[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (position == MAX_COUNT || position == mHistoryList.length) {
            return LayoutInflater.from(mContext).inflate(R.layout.clear_btn_item, null);
        }
        if (convertView == null || convertView.findViewById(R.id.question_title) == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_question_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mQuestionTitle = (TextView) convertView.findViewById(R.id.question_title);
            viewHolder.mAnswerCount = (TextView) convertView.findViewById(R.id.answer_count);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mQuestionTitle.setText(mHistoryList[position]);
        viewHolder.mAnswerCount.setVisibility(View.GONE);
        return convertView;
    }

    private static final class ViewHolder {
        public TextView mQuestionTitle;
        public TextView mAnswerCount;
    }

}
