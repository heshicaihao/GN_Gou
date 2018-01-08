package com.gionee.client.view.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.view.widget.FlowLayout.FlowAdapter;

public class SearchFlowLayoutAdapter implements FlowAdapter {

    private static final String TAG = "SearchFlowLayoutAdapter";
    private LayoutInflater mInflater;
    private List<String> mList;

    public SearchFlowLayoutAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<String> list) {
        mList = list;
    }

    @Override
    public View getView(ViewGroup parent, int index) {

        View view = mInflater.inflate(R.layout.search_flow_item, parent, false);
        TextView bt = (TextView) view.findViewById(R.id.flow_item_tv);
        bt.setText(mList.get(index));
        return view;
    }

    @Override
    public int getCount() {
        return null == mList ? 0 : mList.size();
    }

}
