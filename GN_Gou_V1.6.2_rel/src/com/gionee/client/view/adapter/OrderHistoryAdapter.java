/**
 * @author hehongwan
 * V 2.5.0
 * create at 2015-4-24 下午5:54:14
 */
package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.history.OrderHistoryFragment;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.CommonUtils;
import com.gionee.client.model.HttpConstants;

/**
 * 砍价活动商品列表适配器
 * 
 * @author hehongwan
 * @date create at 2015-4-24 下午5:54:14
 * @description
 */
public class OrderHistoryAdapter extends BaseAdapter {
    private static final String TAG = "BargainProductAdapter";
    private JSONObject mObject;
    private Context mContext;
    private LayoutInflater mInflater;
    private JSONArray mOrderHistoryArray;
    private boolean mIsHasNextPage;

    public OrderHistoryAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void updateData(JSONObject object, boolean mIsHasNextPage) {
        this.mObject = object;
        this.mIsHasNextPage = mIsHasNextPage;
    }

    @Override
    public int getCount() {
        if (mObject == null) {
            return 0;
        }
        mOrderHistoryArray = mObject.optJSONArray(HttpConstants.Data.GetOrdersHistory.HISTORY_LIST_JA);
        int huodongCount = (mOrderHistoryArray == null ? 0 : mOrderHistoryArray.length());
        return huodongCount;
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.order_history_item, null);
            viewHolder.mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.mTvOrderContent = (TextView) convertView.findViewById(R.id.tv_order_content);
            viewHolder.mIvClock = (ImageView) convertView.findViewById(R.id.iv_clock);
            viewHolder.mLlTimeLine = (LinearLayout) convertView.findViewById(R.id.ll_time_line);
            viewHolder.mRlOrderContent = (RelativeLayout) convertView.findViewById(R.id.rl_order_content);
            viewHolder.mArrow = (ImageView) convertView.findViewById(R.id.iv_order_arrow);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final JSONObject item = mOrderHistoryArray.optJSONObject(position);
        viewHolder.mTvTime.setText(item.optString(HttpConstants.Data.GetOrdersHistory.DATE_S));
        viewHolder.mTvOrderContent.setText(Html.fromHtml(item
                .optString(HttpConstants.Data.GetOrdersHistory.CONTENT_S)));
        if (position == mOrderHistoryArray.length() - 1 && !mIsHasNextPage) {
            viewHolder.mIvClock.setImageResource(R.drawable.time_end);
            viewHolder.mLlTimeLine.setVisibility(View.INVISIBLE);
            viewHolder.mRlOrderContent.setBackgroundResource(R.drawable.messges_list_divider);
            android.widget.LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) viewHolder.mRlOrderContent
                    .getLayoutParams();
            layoutParams.leftMargin = AndroidUtils.px2px(mContext, -15);
            viewHolder.mRlOrderContent.setLayoutParams(layoutParams);
            viewHolder.mRlOrderContent.setOnClickListener(null);
            viewHolder.mArrow.setVisibility(View.GONE);
            LayoutParams params = (LayoutParams) viewHolder.mTvOrderContent.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            viewHolder.mTvOrderContent.setLayoutParams(params);
        } else {
            viewHolder.mIvClock.setImageResource(R.drawable.clock);
            viewHolder.mLlTimeLine.setVisibility(View.VISIBLE);
            viewHolder.mRlOrderContent.setBackgroundResource(R.drawable.order_history_content_bg);
            viewHolder.mRlOrderContent.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    String url = item.optString(HttpConstants.Data.GetOrdersHistory.URL_S);
                    CommonUtils.gotoWebViewActvity(mContext, url, true);
                    StatService.onEvent(mContext, "rec_click", "rec_click");
                }
            });
            android.widget.LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) viewHolder.mRlOrderContent
                    .getLayoutParams();
            layoutParams.leftMargin = AndroidUtils.px2px(mContext, 5);
            viewHolder.mRlOrderContent.setLayoutParams(layoutParams);
            LayoutParams params = (LayoutParams) viewHolder.mTvOrderContent.getLayoutParams();
            params.leftMargin = AndroidUtils.px2px(mContext, 15);
            viewHolder.mTvOrderContent.setLayoutParams(params);
            viewHolder.mArrow.setVisibility(View.VISIBLE);
            android.widget.LinearLayout.LayoutParams clockParams = (android.widget.LinearLayout.LayoutParams) viewHolder.mIvClock
                    .getLayoutParams();
            android.widget.LinearLayout.LayoutParams timeParams = (android.widget.LinearLayout.LayoutParams) viewHolder.mTvTime
                    .getLayoutParams();
            if (position == 0) {
                clockParams.topMargin = AndroidUtils.px2px(mContext, 20);
                timeParams.topMargin = AndroidUtils.px2px(mContext, 20);
            } else {
                clockParams.topMargin = AndroidUtils.px2px(mContext, 0);
                timeParams.topMargin = AndroidUtils.px2px(mContext, 0);
            }
            viewHolder.mIvClock.setLayoutParams(clockParams);
            viewHolder.mTvTime.setLayoutParams(clockParams);
        }
        return convertView;
    }

    private static class ViewHolder {
        /**
         * 订单日期
         */
        public TextView mTvTime;
        /**
         * 订单内容
         */
        public TextView mTvOrderContent;

        public ImageView mIvClock;
        public LinearLayout mLlTimeLine;
        public RelativeLayout mRlOrderContent;
        public ImageView mArrow;
    }

}
