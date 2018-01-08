// Gionee <yuwei><2013-12-27> add for CR00821559 begin
/*
 * MyAttentionAdapter.java
 * classes : com.gionee.client.view.adapter.MyAttentionAdapter
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-27 上午10:35:10
 */
package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.attention.AddAttentionActivity;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.tabFragment.HomeFragment;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.manage.CacheDataManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.CommonUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.operation.net.GNImageLoader;

/**
 * MyAttentionAdapter
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-27 上午10:35:10
 * @description TODO
 */
public class MyAttentionAdapter extends BaseAdapter {
    /**
     * 
     */
    private static final String SHARE_KEY_PREFIX = "recommond";
    private static final String BAIDU_STATISTIC_PLATFORM = "platform";
    private static final String TAG = "MyAttentionAdapter";
    private Activity mContext;
    private JSONArray mAttentionList;
    private GridView mGridView;
    private Fragment mFragment;
    private boolean mIsDeleteEnabled;
    private static final String IS_FIRST_CLICK_ADD = "is_first_click_add";

    public MyAttentionAdapter(Activity mContext, GridView gridView, Fragment fragment, boolean deleteEnabled) {
        super();
        this.mContext = mContext;
        this.mGridView = gridView;
        this.mFragment = fragment;
        this.mIsDeleteEnabled = deleteEnabled;
    }

    public void updateData(JSONArray mIconList) {
        try {
            this.mAttentionList = mIconList;
            notifyDataSetChanged();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        if (mIsDeleteEnabled) {
            return mAttentionList == null ? 1 : mAttentionList.length() + 1;
        } else {
            return mAttentionList == null ? 0 : mAttentionList.length();
        }

    }

    @Override
    public Object getItem(int position) {
        if (mAttentionList == null) {
            return null;
        }
        return mAttentionList.opt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.attention_grid_item, null);
            viewHolder = new ViewHolder();
            initViewHolder(convertView, viewHolder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        hidenDeletedBtn(viewHolder);
        bindData(position, viewHolder);
        return convertView;
    }

    /**
     * @param position
     * @param viewHolder
     * @author yuwei
     * @description TODO
     */
    private void bindData(int position, ViewHolder viewHolder) {
//        LogUtils.log(TAG, LogUtils.getCurrentLocation(this.getClass().getName(), new Exception()));
        if (mIsDeleteEnabled && (mAttentionList == null || position == mAttentionList.length())) {
//          LogUtils.log(TAG, LogUtils.getCurrentLocation(this.getClass().getName(), new Exception()));
            setAddBtn(viewHolder);
            return;
        }
        updateAttentionChannel(position, viewHolder);

    }

    /**
     * @param position
     * @param viewHolder
     * @author yuwei
     * @description TODO
     */
    private void updateAttentionChannel(int position, ViewHolder viewHolder) {
        final JSONObject iconJson = (JSONObject) mAttentionList.opt(position);
        if (iconJson != null) {
//            LogUtils.log(TAG, LogUtils.getCurrentLocation(this.getClass().getName(), new Exception()));
            if (mIsDeleteEnabled) {
                LogUtils.log(
                        TAG,
                        LogUtils.getThreadName()
                                + iconJson.optString(HttpConstants.Response.MyAttentionChannel.IMG_S)
                                + "==position==" + position);
            }
            GNImageLoader.getInstance().loadBitmap(
                    iconJson.optString(HttpConstants.Response.MyAttentionChannel.IMG_S),
                    viewHolder.mIconImage);
            setRecommondVisible(viewHolder, iconJson, false);
            setTextColor(viewHolder, iconJson);
            viewHolder.mShopName
                    .setText(iconJson.optString(HttpConstants.Response.MyAttentionChannel.DESC_S));
            setOnItemClickListener(viewHolder, iconJson, position);
        }
    }

    /**
     * @param viewHolder
     * @param iconJson
     * @author yuwei
     * @description TODO
     */
    private void setRecommondVisible(ViewHolder viewHolder, final JSONObject iconJson, boolean isClicked) {
        setClicked(iconJson, isClicked);
        boolean isFristClick = ShareDataManager.getBoolean(mContext, generateShareKey(iconJson), true);
        if (iconJson.optInt(HttpConstants.Response.MyAttentionChannel.IS_RECOMMEND_S) == 1 && isFristClick) {
            viewHolder.mRecommondSign.setVisibility(View.VISIBLE);

        } else {
            viewHolder.mRecommondSign.setVisibility(View.GONE);
        }
    }

    /**
     * @param iconJson
     * @param isClicked
     * @author yuwei
     * @description TODO
     */
    private void setClicked(final JSONObject iconJson, boolean isClicked) {
        if (isClicked) {
            ShareDataManager.putBoolean(mContext, generateShareKey(iconJson), false);
        }
    }

    /**
     * @param iconJson
     * @return
     * @author yuwei
     * @description TODO
     */
    private String generateShareKey(final JSONObject iconJson) {
        return SHARE_KEY_PREFIX + iconJson.optInt(HttpConstants.Response.MyAttentionChannel.ID_I);
    }

    /**
     * @param viewHolder
     * @param iconJson
     * @author yuwei
     * @description TODO
     */
    private void setTextColor(ViewHolder viewHolder, final JSONObject iconJson) {
        try {
            String textColor = iconJson.optString(HttpConstants.Response.MyAttentionChannel.COLOR_S);
            if (TextUtils.isEmpty(textColor)) {
                viewHolder.mShopName.setTextColor(mContext.getResources().getColor(
                        R.color.attention_text_color));
            } else {
                viewHolder.mShopName.setTextColor(Color.parseColor(textColor));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param viewHolder
     * @author yuwei
     * @description TODO
     */
    private void setAddBtn(ViewHolder viewHolder) {
        if (ShareDataManager.getBoolean(mContext, IS_FIRST_CLICK_ADD, true)) {
            viewHolder.mItemLayout.setBackgroundResource(R.drawable.add_attention_default);
        } else {
            setAddView(viewHolder);
        }
        setOnAddAttentionListener(viewHolder);
        viewHolder.mItemLayout.setOnLongClickListener(null);
    }

    public void setAddView(ViewHolder viewHolder) {
        viewHolder.mItemLayout.setBackgroundResource(R.drawable.attention_item_bg);
        viewHolder.mIconImage.setImageResource(R.drawable.plus_sign);
        viewHolder.mIconImage.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.mShopName.setText(R.string.add);
        viewHolder.mIconImage.invalidate();
    }

    private void hidenDeletedBtn(final ViewHolder viewHolder) {
        viewHolder.mDeleteImage.setVisibility(View.GONE);
        viewHolder.mItemLayout.setSelected(false);
        viewHolder.mIconImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.white));
    }

    private void setOnAddAttentionListener(final ViewHolder viewHolder) {
        viewHolder.mItemLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((BaseFragmentActivity) mContext).isFastDoubleClick()) {
                    LogUtils.log(TAG, LogUtils.getThreadName() + " fast double click");
                    return;
                }
                if (ShareDataManager.getBoolean(mContext, IS_FIRST_CLICK_ADD, true)) {
                    ShareDataManager.putBoolean(mContext, IS_FIRST_CLICK_ADD, false);
                    setAddView(viewHolder);
                }
                notifyDataSetChanged();
                Intent intent = new Intent();
                intent.setClass(mContext, AddAttentionActivity.class);
                mFragment.startActivityForResult(intent, Constants.Home.HOME_RESULT_CODE);
                StatService.onEvent(mContext, "Add", "Add");
                AndroidUtils.enterActvityAnim(mContext);
            }
        });
    }

    private void setOnItemClickListener(final ViewHolder viewHolder, final JSONObject iconJson,
            final int position) {
        viewHolder.mItemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecommondVisible(viewHolder, iconJson, true);
                ((HomeFragment) mFragment).mMyAttentionAdapter.notifyDataSetChanged();
                gotoWebViewPage(iconJson);
                if (mIsDeleteEnabled) {
                    StatService.onEvent(mContext, BAIDU_STATISTIC_PLATFORM,
                            iconJson.optString(HttpConstants.Response.MyAttentionChannel.NAME_S));
                    ((GnHomeActivity) mContext)
                            .addFlowStatistics(StatisticsConstants.HomePageConstants.ATTENTION_PREFIX
                                    + (position + 1));
                    ((GnHomeActivity) mContext).setExitStatisticsFlag(true);
                } else {
                    StatService.onEvent(mContext, BAIDU_STATISTIC_PLATFORM, String.valueOf(position));
                    ((GnHomeActivity) mContext)
                            .addFlowStatistics(StatisticsConstants.HomePageConstants.PLATFORM_PREFIX
                                    + (position + 1));
                    ((GnHomeActivity) mContext).setExitStatisticsFlag(true);
                }
            }
        });

        viewHolder.mItemLayout.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (!mIsDeleteEnabled) {
                    return false;
                }
                if (iconJson.has(HttpConstants.Response.AddChannel.DESCRIPTION)) {
                    showDeleteBtn(viewHolder);
                    viewHolder.mDeleteImage.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            CacheDataManager.deleteJsonFromArray(iconJson, mAttentionList);
                            CacheDataManager.deleteFormAttentionArray(mContext, iconJson);
                            notifyDataSetChanged();
                            AndroidUtils.setListViewHeightBasedOnChildren(mGridView, MyAttentionAdapter.this,
                                    true);
                            StatService.onEvent(mContext, "delete_platform",
                                    iconJson.optString(HttpConstants.Response.AddChannel.NAME_S));
                        }

                    });
                    return true;
                }
                return false;
            }

        });
    }

    private void showDeleteBtn(final ViewHolder viewHolder) {
        viewHolder.mDeleteImage.setVisibility(View.VISIBLE);
        viewHolder.mItemLayout.setSelected(true);
    }

    private void gotoWebViewPage(final JSONObject iconJson) {
        ((BaseFragmentActivity) mContext).gotoWebPageForResult(
                iconJson.optString(HttpConstants.Response.MyAttentionChannel.LINK_S), true);
    }

    private static class ViewHolder {

        public ImageView mIconImage;
        public TextView mShopName;
        public RelativeLayout mItemLayout;
        public ImageView mDeleteImage;
        public ImageView mRecommondSign;

    }

    private void initViewHolder(View view, ViewHolder holder) {

        holder.mIconImage = (ImageView) view.findViewById(R.id.attention_icon);
        holder.mShopName = (TextView) view.findViewById(R.id.attention_name);
        holder.mItemLayout = (RelativeLayout) view.findViewById(R.id.attention_item);
        holder.mDeleteImage = (ImageView) view.findViewById(R.id.attention_delete);
        holder.mRecommondSign = (ImageView) view.findViewById(R.id.recommond_sign);

    }
}
