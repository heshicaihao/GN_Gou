package com.gionee.client.activity.attention;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
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

public class AddChannelListAdapter extends BaseAdapter {

    private final Context mContext;
    private List<String> mChildrenData;
    private LayoutInflater mInflater;

    public AddChannelListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void updataData(List<String> items) {
        LogUtils.getFunctionName();
        mChildrenData = items;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mChildrenData == null ? 0 : mChildrenData.size();
    }

    @Override
    public Object getItem(int position) {
        return mChildrenData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        viewHolder.mAdded.setOnClickListener(new CancelButtonListener(position, viewHolder));
        viewHolder.mAdd.setOnClickListener(new AddButtonListener(position, viewHolder));
        updataView(position, viewHolder);

        return convertView;
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
            JSONObject js = new JSONObject(mChildrenData.get(position));
            viewHolder.mChannelName.setText(js.optString(HttpConstants.Response.AddChannel.NAME_S));
            viewHolder.mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.white));
            GNImageLoader.getInstance().loadBitmap(js.optString(HttpConstants.Response.AddChannel.IMG_S),
                    viewHolder.mIcon);
            viewHolder.mDiscription.setText(js.optString(HttpConstants.Response.AddChannel.DESCRIPTION));
            viewHolder.mAdded.setVisibility(View.GONE);
            viewHolder.mAdd.setVisibility(View.VISIBLE);
            if (isContains(position)) {
                viewHolder.mAdded.setVisibility(View.VISIBLE);
                viewHolder.mAdd.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean isContains(int position) {
        try {
            JSONObject js = new JSONObject(mChildrenData.get(position));
            return CacheDataManager.isAddedAttention(mContext, js);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private class AddButtonListener implements OnClickListener {
        private int mPostion;
        private ViewHolder mHolder;

        AddButtonListener(int position, ViewHolder viewHolder) {
            mPostion = position;
            mHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            addAttention();
        }

        private void addAttention() {
            try {
                JSONObject mItemData = new JSONObject(mChildrenData.get(mPostion));
                CacheDataManager.addDataToAttentionArray(mContext, mItemData);
                StatService.onEvent(mContext, "add_platform",
                        mItemData.optString(HttpConstants.Response.AddChannel.NAME_S));
                showAddSuccess();
                startAimation();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void showAddSuccess() {
//            Toast.makeText(mContext, R.string.add_success, Toast.LENGTH_LONG).show();
            mHolder.mAdded.setVisibility(View.VISIBLE);
            mHolder.mAdd.setVisibility(View.GONE);
        }

        private void startAimation() {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.add_attention_btn);
            animation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    showSuccessAdd();
                    mHolder.mAdded.setVisibility(View.VISIBLE);
                    mHolder.mAdd.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    hideSuccessAdd();
                }
            });
            mHolder.mSuccessfullyAdded.startAnimation(animation);
        }

        private void showSuccessAdd() {
            mHolder.mSuccessfullyAdded.setVisibility(View.VISIBLE);
        }

        private void hideSuccessAdd() {
            mHolder.mSuccessfullyAdded.setVisibility(View.GONE);
        }

    }

    private class CancelButtonListener implements OnClickListener {
        private int mPosition;
        private ViewHolder mHolder;

        CancelButtonListener(int position, ViewHolder viewHolder) {
            mPosition = position;
            mHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            try {
                cancelAddAttention();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void cancelAddAttention() throws JSONException {
            mHolder.mAdd.setVisibility(View.VISIBLE);
            mHolder.mAdded.setVisibility(View.GONE);
            JSONObject mObject = new JSONObject(mChildrenData.get(mPosition));
            CacheDataManager.deleteFormAttentionArray(mContext, mObject);
//            Toast.makeText(mContext, R.string.delete_success, Toast.LENGTH_LONG).show();
            StatService.onEvent(mContext, "delete_platform",
                    mObject.optString(HttpConstants.Response.AddChannel.NAME_S));
        }
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int arg0) {
        return 0;
    }
}
