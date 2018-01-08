package com.gionee.client.view.adapter;

import java.util.List;

import org.json.JSONObject;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.business.manage.CacheDataManager;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.operation.net.GNImageLoader;

public class AddChannelAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private final Context mContext;
    private List<String> mGroupData;
    private List<List<JSONObject>> mChildrenData;
    private LayoutInflater mInflater;
    private static final String TAG = "addChannelPosition";

    public AddChannelAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

    }

    public void updataData(List<String> group, List<List<JSONObject>> items) {
        LogUtils.getFunctionName();
        mGroupData = group;
        mChildrenData = items;
        this.notifyDataSetChanged();
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
        LogUtils.log(TAG, LogUtils.getThreadName() + "count=" + count);
        return count;
    }

    @Override
    public Object getItem(int position) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "position=" + position);
        return mChildrenData.get(getGroupIdByPosition(position)).get(getChildIdByPosition(position));
    }

    @Override
    public long getItemId(int position) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "position=" + position);
        return getChildIdByPosition(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "position=" + position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.add_attention_list_item, null);
            viewHolder.mChannelName = (TextView) convertView.findViewById(R.id.channel_name);
            viewHolder.mDiscription = (TextView) convertView.findViewById(R.id.channel_description);
            viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.channel_icon);
            viewHolder.mAdd = (Button) convertView.findViewById(R.id.add_channel);
            viewHolder.mAdded = (Button) convertView.findViewById(R.id.cancel_add);
            viewHolder.mSuccessfullyAdded = (TextView) convertView.findViewById(R.id.successfully_added);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mAdded.setOnClickListener(new CancelButtonListener(getGroupIdByPosition(position),
                getChildIdByPosition(position), viewHolder));
        viewHolder.mAdd.setOnClickListener(new AddButtonListener(getGroupIdByPosition(position),
                getChildIdByPosition(position), viewHolder));

        updataView(position, viewHolder);

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        GroupViewHolder groupHolder = null;
        int groupId = getGroupIdByPosition(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.add_attention_list_group, null);
            groupHolder = new GroupViewHolder();
            groupHolder.mCategory = (TextView) convertView.findViewById(R.id.group_name);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupViewHolder) convertView.getTag();
        }
        try {
            groupHolder.mCategory.setText(mGroupData.get(groupId));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "position=" + position);
        return getGroupIdByPosition(position);
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
        public Button mAdd;
        public TextView mChannelName;
        public TextView mDiscription;
        public ImageView mIcon;
        public Button mAdded;
        public TextView mSuccessfullyAdded;
    }

    static class GroupViewHolder {
        TextView mCategory;
    }

    private void updataView(int position, ViewHolder viewHolder) {
        try {
            int groupPosition = getGroupIdByPosition(position);
            int childPosition = getChildIdByPosition(position);
            LogUtils.log(TAG, LogUtils.getThreadName() + "groupPosition=" + groupPosition + "childPosition"
                    + childPosition);
            JSONObject js = mChildrenData.get(groupPosition).get(childPosition);
            viewHolder.mChannelName.setText(js.optString(HttpConstants.Response.AddChannel.NAME_S));
            viewHolder.mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.white));
            GNImageLoader.getInstance().loadBitmap(js.optString(HttpConstants.Response.AddChannel.IMG_S),
                    viewHolder.mIcon);
            viewHolder.mDiscription.setText(js.optString(HttpConstants.Response.AddChannel.DESCRIPTION));
            viewHolder.mAdded.setVisibility(View.GONE);
            viewHolder.mAdd.setVisibility(View.VISIBLE);

            if (isContains(groupPosition, childPosition)) {
                viewHolder.mAdded.setVisibility(View.VISIBLE);
                viewHolder.mAdd.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean isContains(int groupPosition, int childPosition) {
        JSONObject js = mChildrenData.get(groupPosition).get(childPosition);
        return CacheDataManager.isAddedAttention(mContext, js);

    }

    class AddButtonListener implements OnClickListener {
        private int mParent;
        private int mChild;
        private ViewHolder mHolder;

        AddButtonListener(int parent, int child, ViewHolder viewHolder) {
            mParent = parent;
            mChild = child;
            mHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {

            CacheDataManager.addDataToAttentionArray(mContext, mChildrenData.get(mParent).get(mChild));
            StatService.onEvent(mContext, "add_platform",
                    mChildrenData.get(mParent).get(mChild)
                            .optString(HttpConstants.Response.AddChannel.NAME_S));
            showAddSuccess();
//            startAimation();
        }

        public void showAddSuccess() {
            Toast.makeText(mContext, R.string.add_success, Toast.LENGTH_LONG).show();
            mHolder.mAdded.setVisibility(View.VISIBLE);
            mHolder.mAdd.setVisibility(View.GONE);
        }

//        private void startAimation() {
//            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.add_attention_btn);
//            animation.setAnimationListener(new AnimationListener() {
//
//                @Override
//                public void onAnimationStart(Animation animation) {
//                    showSuccessAdd();
//                    mHolder.mAdded.setVisibility(View.VISIBLE);
//                    mHolder.mAdd.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    hideSuccessAdd();
//                }
//            });
//            mHolder.mSuccessfullyAdded.startAnimation(animation);
//        }
//
//        private void showSuccessAdd() {
//            mHolder.mSuccessfullyAdded.setVisibility(View.VISIBLE);
//        }
//
//        private void hideSuccessAdd() {
//            mHolder.mSuccessfullyAdded.setVisibility(View.GONE);
//        }
    }

    class CancelButtonListener implements OnClickListener {
        private int mParent;
        private int mChild;
        private ViewHolder mHolder;

        CancelButtonListener(int parent, int child, ViewHolder viewHolder) {
            mParent = parent;
            mChild = child;
            mHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            mHolder.mAdd.setVisibility(View.VISIBLE);
            mHolder.mAdded.setVisibility(View.GONE);
            CacheDataManager.deleteFormAttentionArray(mContext, mChildrenData.get(mParent).get(mChild));
            Toast.makeText(mContext, R.string.delete_success, Toast.LENGTH_LONG).show();
            StatService.onEvent(mContext, "delete_platform", mChildrenData.get(mParent).get(mChild)
                    .optString(HttpConstants.Response.AddChannel.NAME_S));
        }
    }
}
