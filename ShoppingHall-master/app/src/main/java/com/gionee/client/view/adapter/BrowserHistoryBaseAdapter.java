package com.gionee.client.view.adapter;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.history.BrowseHistoryDataChangeNotify;
import com.gionee.client.activity.history.BrowseHistoryInfo;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;

public class BrowserHistoryBaseAdapter extends BaseAdapter implements StickyListHeadersAdapter,
        OnItemClickListener {

    private static final String TAG = "BrowserHistoryBaseAdapter";
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private BrowseHistoryDataChangeNotify mHistoryDataChangeNotify;
    private List<String> mGroupData;
    private List<ArrayList<BrowseHistoryInfo>> mChildrenData;
    private boolean mShowType = false;

    public BrowserHistoryBaseAdapter(Context context, BrowseHistoryDataChangeNotify notify) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHistoryDataChangeNotify = notify;
    }

    public void notifyDataChanged(List<String> group, List<ArrayList<BrowseHistoryInfo>> item) {
        mGroupData = group;
        mChildrenData = item;
    }

    public void setShowType(boolean flag) {
        mShowType = flag;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mChildrenData == null) {
            return 0;
        }
        for (int i = 0; i < mChildrenData.size(); i++) {
            count = count + mChildrenData.get(i).size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return mChildrenData.get(getGroupIdByPosition(position)).get(getChildIdByPosition(position));
    }

    @Override
    public long getItemId(int position) {
        return getChildIdByPosition(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.browse_history_list_item, null);
            holder.mTitle = (TextView) convertView.findViewById(R.id.history_title);
            holder.mTime = (TextView) convertView.findViewById(R.id.history_time);
            holder.mPlatform = (TextView) convertView.findViewById(R.id.platform);
            holder.mDelete = (ImageView) convertView.findViewById(R.id.history_delete);
            holder.mType = (TextView) convertView.findViewById(R.id.history_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        updateItemView(position, holder);
        holder.mDelete.setTag(position);
        holder.mDelete.setOnClickListener(mClickListener);
        return convertView;
    }

    private void updateItemView(int position, ViewHolder holder) {

        try {
            int groupPosition = getGroupIdByPosition(position);
            int childPosition = getChildIdByPosition(position);
            BrowseHistoryInfo info = mChildrenData.get(groupPosition).get(childPosition);
            if (null != info) {
                updateTypeView(holder.mType, info);
                holder.mTitle.setText(info.getTitle());
                holder.mTime.setText(info.getTime());
                holder.mPlatform.setText(info.getmPlatform());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTypeView(TextView textView, BrowseHistoryInfo info) {
        if (!mShowType) {
            textView.setVisibility(View.GONE);
            return;
        }
        String type = info.getmType();
        int drawable_id = R.drawable.platform_label_bg;
        int string_id = R.string.goods;
        boolean isShow = false;
        if (type.equals(Constants.History.HistoryType.GOODS.getValue())) {
            string_id = R.string.goods;
            drawable_id = R.drawable.goods_label_bg;
            isShow = true;
        } else if (type.equals(Constants.History.HistoryType.SHOP.getValue())) {
            string_id = R.string.shop;
            drawable_id = R.drawable.shop_label_bg;
            isShow = true;
        } else {
            isShow = false;
        }
        textView.setBackgroundResource(drawable_id);
        textView.setText(string_id);
        textView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                int position = (Integer) v.getTag();
                switch (v.getId()) {
                    case R.id.history_delete:
                        deleteItemResponse(position);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    private void deleteItemResponse(int position) {
        try {
            int groupPosition = getGroupIdByPosition(position);
            int childPosition = getChildIdByPosition(position);
            if (null != mGroupData && mChildrenData.get(groupPosition).size() > childPosition) {
                BrowseHistoryInfo data = mChildrenData.get(groupPosition).get(childPosition);
                mChildrenData.get(groupPosition).remove(data);
                mHistoryDataChangeNotify.onRemoveBrowseHistoryData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        StatService.onEvent(mContext, BaiduStatConstants.CLEAR_WEB_HISTORY, BaiduStatConstants.ONE);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        final GroupViewHolder groupHolder;
        int groupId = getGroupIdByPosition(position);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.hitory_list_group_item, null);
            groupHolder = new GroupViewHolder();
            groupHolder.mCategory = (TextView) convertView.findViewById(R.id.group_name);
            groupHolder.mDriver = (View) convertView.findViewById(R.id.history_goup_top_driver);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupViewHolder) convertView.getTag();
        }
        try {
            groupHolder.mCategory.setText(mGroupData.get(groupId));
            if (position == 0) {
                groupHolder.mDriver.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return getGroupIdByPosition(position);
    }

    private void browseDetail(int position) {
        int groupPosition = getGroupIdByPosition(position);
        int childPosition = getChildIdByPosition(position);
        LogUtils.log(TAG, " click  groupPosition" + groupPosition + "   childPosition" + childPosition);
        if (null != mGroupData && mChildrenData.get(groupPosition).size() > childPosition) {
            BrowseHistoryInfo data = mChildrenData.get(groupPosition).get(childPosition);
            if (null == data) {
                return;
            }
            String url = data.getUrl();
            ((BaseFragmentActivity) mContext).gotoWebPage(url, true);
            StatService.onEvent(mContext, BaiduStatConstants.WEB_HISTORY_CLICK, data.getmType());
        }
    }

    private int getGroupIdByPosition(int position) {
        int index = 0;
        int groupPosition = 0;
        for (int i = 0; i < mChildrenData.size(); i++) {
            index += mChildrenData.get(i).size();
            if (index >= position + 1) {
                groupPosition = i;
                return groupPosition;

            }
        }
        LogUtils.log(TAG, LogUtils.getThreadName() + "position=" + position + "groupPosition="
                + groupPosition);
        return groupPosition;
    }

    private int getChildIdByPosition(int position) {
        int index = 0;
        int childPosition = 0;
        for (int i = 0; i < mChildrenData.size(); i++) {
            index += mChildrenData.get(i).size();
            if (index >= position + 1) {
                childPosition = position - index + mChildrenData.get(i).size();
                return childPosition;

            }
        }
        LogUtils.log(TAG, LogUtils.getThreadName() + "position=" + position + "childPosition="
                + childPosition);
        return childPosition;

    }

    private static class ViewHolder {
        public TextView mTitle;
        public TextView mTime;
        public TextView mPlatform;
        public ImageView mDelete;
        public TextView mType;
    }

    private static class GroupViewHolder {
        TextView mCategory;
        View mDriver;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        browseDetail(position);
    }
}
