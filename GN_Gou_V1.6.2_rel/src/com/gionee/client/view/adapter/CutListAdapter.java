package com.gionee.client.view.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;
import com.gionee.framework.operation.net.GNImageLoader;

public class CutListAdapter extends BaseAdapter {

    private static final String TAG = "CutListAdapter";
    private JSONArray mArray;
    private LayoutInflater mInflater;
    private OnClickListener mListener;
    private Context mContext;
    private List<JSONObject> mCutList = new ArrayList<JSONObject>();

    public void setData(JSONArray jsonArray) {
        mArray = jsonArray;
        notifyDataSetChanged();
    }

    public CutListAdapter(Context context, View.OnClickListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
        mContext = context;
    }

    @Override
    public int getCount() {
        return (mArray != null ? mArray.length() : 0);
    }

    @Override
    public Object getItem(int position) {
        if (mArray == null) {
            return null;
        }
        return mArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.cut_list_item, null);
            viewHolder = setViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final JSONObject object = mArray.optJSONObject(position);
        setItemView(viewHolder, object);
        return convertView;
    }

    private void setItemView(ViewHolder viewHolder, final JSONObject object) {
        viewHolder.mShopName.setText(object.optString(HttpConstants.Data.CutList.SHOP_TIILE_S));

        viewHolder.mGoodIcon.setImageDrawable(mContext.getResources().getDrawable(
                R.drawable.comment_img_default));
        viewHolder.mShopIcon.setImageDrawable(mContext.getResources().getDrawable(
                R.drawable.comment_img_default));
        GNImageLoader.getInstance().loadBitmap(object.optString(HttpConstants.Data.CutList.SHOP_LOGO),
                viewHolder.mShopIcon);
        String goodsUrl = object.optString(HttpConstants.Data.CutList.GOODS_IMG_S);
        GNImageLoader.getInstance().loadBitmap(object.optString(HttpConstants.Data.CutList.GOODS_IMG_S),
                viewHolder.mGoodIcon);
        viewHolder.mGoodIcon.setTag(goodsUrl);

        viewHolder.mGoodPrice.setText(addUnit(object.optString(HttpConstants.Data.CutList.PRICE_S)));
        viewHolder.mGoodPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.mGoodPrice.getPaint().setAntiAlias(true);
        viewHolder.mGoodTitle.setText(object.optString(HttpConstants.Data.CutList.GOODS_TILTE_S));
        viewHolder.mShopView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((BaseFragmentActivity) mContext).gotoWebPage(
                        object.optString(HttpConstants.Data.CutList.SHOP_URL_S), true);
                StatService.onEvent(mContext, "b_shop",
                        object.optString(HttpConstants.Data.CutList.SHOP_TIILE_S));
            }

        });
        LogUtils.log(TAG, LogUtils.getThreadName() + "object=" + object.toString());
        setCutView(viewHolder, object);
    }

    private ViewHolder setViewHolder(View convertView) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mShopIcon = (ImageView) convertView.findViewById(R.id.cut_shop_icon);
        viewHolder.mShopName = (TextView) convertView.findViewById(R.id.cut_shop_name);
        viewHolder.mGoodIcon = (ImageView) convertView.findViewById(R.id.cut_good_icon);
        viewHolder.mGoodTitle = (TextView) convertView.findViewById(R.id.cut_good_title);
        viewHolder.mGoodPrice = (TextView) convertView.findViewById(R.id.good_price);
        viewHolder.mCurrentPrice = (TextView) convertView.findViewById(R.id.good_current_price);
        viewHolder.mCutInfo = (TextView) convertView.findViewById(R.id.cut_good_info);
        viewHolder.mCut = (Button) convertView.findViewById(R.id.cut);
        viewHolder.mShopView = (RelativeLayout) convertView.findViewById(R.id.cut_shop);
        return viewHolder;
    }

    private void setCutView(final ViewHolder viewHolder, final JSONObject object) {
        final JSONObject oJsonObject = checkId(object.optInt(HttpConstants.Data.CutList.ID_I));
        viewHolder.mCut.setEnabled(true);
        final int cut_code;
        if (oJsonObject != null) {
            viewHolder.mCurrentPrice.setText(addUnit(oJsonObject
                    .optString(HttpConstants.Data.CutData.CURRENT_PRICE_S)));
            viewHolder.mCut.setText(oJsonObject.optString(HttpConstants.Data.CutData.CUT_MSG_S));
            setCutButtonBackground(viewHolder, oJsonObject.optInt(HttpConstants.Data.CutData.CUT_CODE_I));
            viewHolder.mCutInfo.setText(oJsonObject.optString(HttpConstants.Data.CutData.CUT_INFO_S));
            cut_code = oJsonObject.optInt(HttpConstants.Data.CutList.CUT_CODE_I);

        } else {
            viewHolder.mCurrentPrice.setText(addUnit(object
                    .optString(HttpConstants.Data.CutData.CURRENT_PRICE_S)));
            viewHolder.mCutInfo.setText(object.optString(HttpConstants.Data.CutList.CUT_INFO_S));
            viewHolder.mCut.setText(object.optString(HttpConstants.Data.CutList.CUT_MSG_S));
            setCutButtonBackground(viewHolder, object.optInt(HttpConstants.Data.CutList.CUT_CODE_I));
            viewHolder.mCut.setText(object.optString(HttpConstants.Data.CutList.CUT_MSG_S));
            cut_code = object.optInt(HttpConstants.Data.CutList.CUT_CODE_I);
        }

        viewHolder.mCut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName() + object.toString());
                String shareTitle = object.optString(HttpConstants.Data.CutList.SHARE_TILTE_S);
                if (TextUtils.isEmpty(shareTitle)) {
                    shareTitle = viewHolder.mGoodTitle.getText().toString();
                }
                String url = object.optString(HttpConstants.Data.CutList.SHARE_URL_S);
                if (oJsonObject == null) {
                    v.setTag(setViewTagBean(viewHolder, object, cut_code, shareTitle, url));
                } else {
                    v.setTag(setViewTagBean(viewHolder, oJsonObject, cut_code, shareTitle, url));
                }
                mListener.onClick(v);
            }

        });
    }

    private String addUnit(String s) {
        return ("￥" + s);
    }

    private MyBean setViewTagBean(final ViewHolder viewHolder, final JSONObject object, final int cutCode,
            final String shareTitle, final String url) {
        MyBean bean = MyBeanFactory.createEmptyBean();
        bean.put(Constants.CutPage.ID, object.optInt(HttpConstants.Data.CutList.ID_I));
        bean.put(Constants.CutPage.URL, url);
        bean.put(HttpConstants.Data.CutList.TIPS_S, object.optString(HttpConstants.Data.CutList.TIPS_S));
        bean.put(Constants.CutPage.BITMAP, getGoodIconBitmap(viewHolder));
        bean.put(Constants.CutPage.CUT_DODE, cutCode);
        bean.put(Constants.CutPage.CURRENT_PRICE, viewHolder.mCurrentPrice.getText().toString());
        bean.put(Constants.CutPage.PRICE, viewHolder.mGoodPrice.getText().toString());
        bean.put(Constants.CutPage.TITLE, shareTitle);
        bean.put(Constants.CutPage.IMAGE_URL, viewHolder.mGoodIcon.getTag());
        return bean;
    }

    private Bitmap getGoodIconBitmap(ViewHolder viewHolder) {
        try {
            if (viewHolder.mGoodIcon != null && viewHolder.mGoodIcon.getDrawable() != null) {
                return AndroidUtils.drawable2Bitmap(viewHolder.mGoodIcon.getDrawable());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.weibo_share_icon);

    }

    private JSONObject checkId(int id) {
        if (mCutList == null) {
            return null;
        }
        for (int i = 0; i < mCutList.size(); i++) {
            if (id == mCutList.get(i).optInt(HttpConstants.Data.CutData.ID_I)) {
                LogUtils.log("CutListAdapter", LogUtils.getThreadName() + mCutList.get(i).toString());
                return mCutList.get(i);
            }
        }
        return null;
    }

    private void setCutButtonBackground(ViewHolder viewHolder, int state) {
        switch (state) {
            case Constants.CutPage.CUT_STATE_CUT:
                viewHolder.mCut.setBackgroundResource(R.drawable.yellow_btn_src);
                break;
            case Constants.CutPage.CUT_STATE_REDAY:
                viewHolder.mCut.setBackgroundResource(R.drawable.green_btn_src);
                break;
            case Constants.CutPage.CUT_STATE_END:
                viewHolder.mCut.setBackgroundResource(R.drawable.gray_btn_src);
                break;
            case Constants.CutPage.CUT_STATE_HELP:
                viewHolder.mCut.setBackgroundResource(R.drawable.purple_btn_src);
                break;
            case Constants.CutPage.FLOOR_PRICE:
                viewHolder.mCut.setBackgroundResource(R.drawable.gray_btn_src);
                break;
            case Constants.CutPage.CUT_STATE_OFF:
                break;
            case Constants.CutPage.CUT_STATE_AFTER:
                viewHolder.mCut.setBackgroundResource(R.drawable.gray_btn_bg_nor);
                break;
            default:
                break;
        }
    }

    private static class ViewHolder {
        public TextView mShopName;
        public ImageView mShopIcon;
        public ImageView mGoodIcon;
        public TextView mGoodTitle;
        public TextView mGoodPrice;
        public TextView mCurrentPrice;
        public TextView mCutInfo;
        public Button mCut;
        public RelativeLayout mShopView;
    }

    public void addCutData(JSONObject object) {
        try {
            if (null == object || mCutList.contains(object)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            for (int i = 0; i < mCutList.size(); i++) {
                if (mCutList.get(i).optInt(HttpConstants.Data.CutData.ID_I) == object
                        .optInt(HttpConstants.Data.CutData.ID_I)) {
                    mCutList.remove(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.log("CutListAdapter", LogUtils.getThreadName() + object.toString());
        mCutList.add(object);
        notifyDataSetChanged();
    }

    public void initCutListData() {
        if (mCutList.isEmpty()) {
            return;
        }
        mCutList.clear();
    }

}
