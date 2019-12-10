// Gionee <yangxiong><2014-5-13> add for CR00850885 begin
package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.tabFragment.HomeFragment;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.operation.net.GNImageLoader;

/**
 * @author yangxiong <br/>
 * @date create at 2014-5-13 下午02:39:58
 * @description TODO
 */
public class SpeedServiceAdapter extends BaseAdapter {
    private static final String TAG = "SpeedServiceAdapter";

    private Context mContext;
    private JSONArray mSpeedServiceList;
    private Fragment mFragment;

    public SpeedServiceAdapter(Fragment fragment) {
        super();
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
    }

    public void updateData(JSONArray speedServiceList) {
        try {
            LogUtils.log(TAG, LogUtils.getThreadName() + speedServiceList);
            this.mSpeedServiceList = speedServiceList;
            notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
//        LogUtils.log(TAG, LogUtils.getThreadName() + "count = "
//                + (mSpeedServiceList == null ? 0 : mSpeedServiceList.length()));
        return mSpeedServiceList == null ? 0 : mSpeedServiceList.length();
    }

    @Override
    public Object getItem(int position) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (mSpeedServiceList == null) {
            return null;
        }
        return mSpeedServiceList.opt(position);
    }

    @Override
    public long getItemId(int position) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.speed_service_grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mIconImage = (ImageView) convertView.findViewById(R.id.speed_service_icon);
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
            viewHolder.mSeparator = (ImageView) convertView.findViewById(R.id.separator);
            viewHolder.mSpeedServiceItemLayout = (LinearLayout) convertView
                    .findViewById(R.id.speed_service_item_layout);
//         viewHolder.mSpeedServiceItemLayout.setLayoutParams(new LayoutParams(Gr, height)));
//         viewHolder.mSeparator.setVisibility(position % 4 == 0 ? View.GONE : View.VISIBLE);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        bindData(position, viewHolder);
        return convertView;
    }

    private void bindData(int position, ViewHolder viewHolder) {
//        LogUtils.log(TAG, LogUtils.getCurrentLocation(this.getClass().getName(), new Exception()));

        if (mSpeedServiceList == null) {
            return;
        }
        final JSONObject iconJson = (JSONObject) mSpeedServiceList.opt(position);
        if (iconJson != null) {
//          LogUtils.log(TAG, LogUtils.getCurrentLocation(this.getClass().getName(), new Exception()));
            GNImageLoader.getInstance().loadBitmap(
                    iconJson.optString(HttpConstants.Response.AdvertiseBanner.IMG_S), viewHolder.mIconImage);
            String title = iconJson.optString(HttpConstants.Response.AdvertiseBanner.TITLE_S);
            viewHolder.mTitle.setText(title.length() <= 4 ? title : title.substring(0, 3));
            setOnItemClickListener(viewHolder, iconJson, position);
        }
    }

    private void setOnItemClickListener(final ViewHolder viewHolder, final JSONObject iconJson,
            final int position) {
        viewHolder.mSpeedServiceItemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                ((HomeFragment) mFragment).mMyAttentionAdapter.notifyDataSetChanged();
                gotoWebViewPage(iconJson);
                ((GnHomeActivity) mContext)
                        .addFlowStatistics(StatisticsConstants.HomePageConstants.CONVENIENT_ENTRANCE_PREFIX
                                + (position + 1));
                ((GnHomeActivity) mContext).setExitStatisticsFlag(true);
            }
        });
    }

    private void gotoWebViewPage(final JSONObject iconJson) {
        ((BaseFragmentActivity) mContext).gotoWebPageForResult(
                iconJson.optString(HttpConstants.Response.AdvertiseBanner.LINK_S), true);
        StatService.onEvent(mContext, "convenient_service",
                iconJson.optString(HttpConstants.Response.AdvertiseBanner.TITLE_S));
    }

    private static class ViewHolder {
        public ImageView mIconImage;
        public TextView mTitle;
        public ImageView mSeparator;
        public LinearLayout mSpeedServiceItemLayout;
    }
}
//Gionee <yangxiong><2014-5-13> add for CR00850885 end