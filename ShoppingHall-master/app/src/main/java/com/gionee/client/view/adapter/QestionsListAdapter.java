package com.gionee.client.view.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.client.R;

public class QestionsListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<HashMap<String, String>> mListItem;
    private LayoutInflater mInflater;
    private TextView mNum;
    private TextView mQestion;
    private TextView mAnswer;

    public QestionsListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setData(ArrayList<HashMap<String, String>> list) {
        mListItem = list;
    }

    @Override
    public int getCount() {
        return (mListItem != null ? mListItem.size() : 0);
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.umeng_fb_qestion_list_item, null);
        }
        mNum = (TextView) convertView.findViewById(R.id.umeng_fb_questions_num);
        mQestion = (TextView) convertView.findViewById(R.id.umeng_fb_questions_sescription);
        mAnswer = (TextView) convertView.findViewById(R.id.umeng_fb_questions_answer);
        mNum.setText(String.valueOf(position + 1) + ".");
        HashMap<String, String> map = mListItem.get(position);
        mQestion.setText(map.get(com.gionee.client.model.Constants.FB_QESTION));
        mAnswer.setText(map.get(com.gionee.client.model.Constants.FB_ANSWER));
        if (position % 2 != 0) {
            mAnswer.setBackgroundResource(R.drawable.qestions_answer_other);
        }
        return convertView;
    }
}
