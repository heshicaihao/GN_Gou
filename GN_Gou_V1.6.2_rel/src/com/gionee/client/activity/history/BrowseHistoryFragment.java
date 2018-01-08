package com.gionee.client.activity.history;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gionee.client.R;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.view.adapter.BrowserHistoryBaseAdapter;

public class BrowseHistoryFragment extends Fragment implements BrowseHistoryDataChangeNotify, OnClickListener {

    private RelativeLayout mNoDataLayout;
    private StickyListHeadersListView mListView;
    private int mViewVisible = View.GONE;
    private RelativeLayout mHistoryRecordEmptyLayout;
    private BrowserHistoryBaseAdapter mAdapter;
    private List<ArrayList<BrowseHistoryInfo>> mBrowseHistoryInfos;
    private List<String> mBrowserHitoryGroup;
    public boolean mIsShowType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.browse_history_list, null);
        mNoDataLayout = (RelativeLayout) layout.findViewById(R.id.above_layout);
        mNoDataLayout.setOnClickListener(this);
        mListView = (StickyListHeadersListView) layout.findViewById(R.id.history_list);
        mHistoryRecordEmptyLayout = (RelativeLayout) layout.findViewById(R.id.history_no_comment_layout);
        mAdapter = new BrowserHistoryBaseAdapter(getActivity(), this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mAdapter);
        onBrowseHistoryDataChange(mBrowserHitoryGroup, mBrowseHistoryInfos);
        return layout;
    }

    @Override
    public void onBrowseHistoryDataChange(final List<String> group,
            final List<ArrayList<BrowseHistoryInfo>> list) {
        mBrowserHitoryGroup = group;
        mBrowseHistoryInfos = list;
        setHistoryRecordLayoutVisible(group);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mAdapter) {
                    refreshList(group, list);
                }
            }
        });

    }

    public void refreshList(final List<String> group, final List<ArrayList<BrowseHistoryInfo>> list) {
        try {
            mAdapter.notifyDataChanged(group, list);
            mAdapter.notifyDataSetChanged();
            mAdapter.setShowType(mIsShowType);
            if (group == null || group.size() == 0) {
                mListView.setVisibility(View.GONE);
            } else {
                mListView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBrowseHistoryInfoList(List<String> group, List<ArrayList<BrowseHistoryInfo>> list,
            boolean flag) {
        mBrowserHitoryGroup = group;
        mBrowseHistoryInfos = list;
        mIsShowType = flag;
    }

    private void setHistoryRecordLayoutVisible(List<String> list) {
        if (list.isEmpty()) {
            mViewVisible = View.VISIBLE;
        } else {
            mViewVisible = View.GONE;
        }
        if (null != mHistoryRecordEmptyLayout) {
            mHistoryRecordEmptyLayout.setVisibility(mViewVisible);
        }
    }

    @Override
    public void onRemoveBrowseHistoryData(BrowseHistoryInfo deleteData) {
        ((GnBrowseHistoryActivity) getActivity()).removeHistoryInfo(deleteData);
    }

    @Override
    public void onClick(View v) {
        getActivity().setResult(Activity.RESULT_OK);
        AndroidUtils.finishActivity(getActivity());
    }

}
