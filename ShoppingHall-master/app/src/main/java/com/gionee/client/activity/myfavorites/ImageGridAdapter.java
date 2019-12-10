package com.gionee.client.activity.myfavorites;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.client.R;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.operation.utills.JSONArrayHelper;
import com.huewu.pla.ImageLoader;
import com.huewu.pla.ScaleImageView;

public class ImageGridAdapter extends BaseAdapter implements IBusinessHandle {
    private static final String TAG = "ImageGridAdapter";
    private ImageLoader mLoader;
    private LayoutInflater mLayoutInflater;
    private JSONArray mArray;
    private Boolean[] mChooseDels = new Boolean[2];
    private boolean mIsEditState;// 是否是编辑状态
    private Context mContext;
    private ZhiwuFavorFragment mZhiwuFavorFragment;
    private List<String> mClickList = new ArrayList<String>();

    public ImageGridAdapter(ZhiwuFavorFragment zhiwuFavorFragment, Context context) {
        mLoader = new ImageLoader(context);
        mLoader.setIsUseMediaStoreThumbnails(false);
        // mImageList = list;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        mLoader.setRequiredSize(width / 2);
        mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mZhiwuFavorFragment = zhiwuFavorFragment;
    }

    public int getCount() {
        return mArray == null ? 0 : mArray.length();
        // return mImageList.size();
    }

    public Object getItem(int arg0) {
        return mArray == null ? null : mArray.optJSONObject(arg0);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public void setData(JSONArray array) {
        this.mArray = array;
        if (mArray != null && mArray.length() > 0) {
            mChooseDels = new Boolean[mArray.length()];
            initChooseDelBoolean(mChooseDels);
        }
        notifyDataSetChanged();
    }

    public void setClickList(List<String> list) {
        mClickList = list;
    }

    public void setClickId(String id) {
        mClickList.add(id);
    }

    public void removeFavor(int position) {
        JSONArrayHelper helper = new JSONArrayHelper(mArray);
        helper.remove(position);
        if (mArray != null && mArray.length() > 0) {
            mChooseDels = new Boolean[mArray.length()];
            initChooseDelBoolean(mChooseDels);
        }else{
        	mChooseDels=null;
        }
        notifyDataSetChanged();
    }

    public void changeItemCommentCount(int position, int count) {
        JSONObject item = mArray.optJSONObject(position);
        if (item != null) {
            try {
                item.put(HttpConstants.Data.ZhiwuFavorList.COMMENT, count);
                notifyDataSetChanged();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void initChooseDelBoolean(Boolean[] mChooseDels) {
        // TODO Auto-generated method stub
        for (int i = 0; i < mChooseDels.length; i++) {
            mChooseDels[i] = false;
        }
    }

    public boolean getmIsEditState() {
        return mIsEditState;
    }

    public void setmIsEditState(boolean mIsEditState) {
        if (mChooseDels != null) {
            for (int i = 0; i < mChooseDels.length; i++) {
                mChooseDels[i] = false;
            }
        }
        this.mIsEditState = mIsEditState;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_zhiwu_favor, null);
            holder = new ViewHolder();
            holder.mImageView = (ScaleImageView) convertView.findViewById(R.id.news_pic);
            holder.mTvCommentCount = (TextView) convertView.findViewById(R.id.tv_comment_count);
            holder.mTvClickCount = (TextView) convertView.findViewById(R.id.tv_click_count);
            holder.mNewsTitle = (TextView) convertView.findViewById(R.id.news_title);
            holder.mNewsType = (TextView) convertView.findViewById(R.id.news_type);
            holder.mCheckBox = (ImageView) convertView.findViewById(R.id.single_selection_check_box);
            holder.mLlEditBg = (ImageView) convertView.findViewById(R.id.iv_edit_bg);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        JSONObject item = mArray.optJSONObject(position);
        holder.mImageView.setTag(item.optInt(HttpConstants.Data.ZhiwuFavorList.BGCOLOR));
        mLoader.DisplayImage(item.optString(HttpConstants.Data.ZhiwuFavorList.IMAGE), holder.mImageView,
                holder.mLlEditBg);
        holder.mTvCommentCount
                .setText(item.optString(HttpConstants.Data.ZhiwuFavorList.COMMENT) == null ? "0" : item
                        .optString(HttpConstants.Data.ZhiwuFavorList.COMMENT));
        String hitCountStr = item.optString(HttpConstants.Data.ZhiwuFavorList.HITS);
        int countHit = 0;
        if (!hitCountStr.contains(mContext.getString(R.string.comment_count_str))) {
            countHit = Integer.parseInt(hitCountStr);
        }
        if (countHit >= 10000) {
            hitCountStr = countHit / 10000 + mContext.getString(R.string.comment_count_str);
        }
        holder.mTvClickCount.setText(hitCountStr);
        holder.mNewsTitle.setText(item.optString(HttpConstants.Data.ZhiwuFavorList.TITLE) == null ? "" : item
                .optString(HttpConstants.Data.ZhiwuFavorList.TITLE));
        holder.mNewsType.setVisibility(View.VISIBLE);
        holder.mNewsType.setText(item.optString(HttpConstants.Data.ZhiwuFavorList.CAT) == null ? "" : item
                .optString(HttpConstants.Data.ZhiwuFavorList.CAT));
        if (mIsEditState) {
            if (holder.mCheckBox.getVisibility() == View.GONE) {
                singleSelectionAnimationIn(holder.mCheckBox);
            }
            holder.mLlEditBg.setVisibility(View.VISIBLE);
            holder.mCheckBox.setVisibility(View.VISIBLE);
            if (mChooseDels[position]) {
                holder.mCheckBox.setImageResource(R.drawable.single_select_btn_bg_sel);
                holder.mCheckBox.setTag(1);
            } else {
                holder.mCheckBox.setImageResource(R.drawable.single_select_btn_bg_nor);
                holder.mCheckBox.setTag(0);
            }
        } else {
            holder.mLlEditBg.setVisibility(View.GONE);
            if (holder.mCheckBox.getVisibility() == View.VISIBLE) {
                singleSelectionAnimationOut(holder.mCheckBox);
            }
            holder.mCheckBox.setVisibility(View.GONE);
        }
        holder.mCheckBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                boolean isChoose = false;
                if (((Integer) arg0.getTag()) == 0) {
                    isChoose = true;
                    arg0.setTag(1);
                    ((ImageView) arg0).setImageResource(R.drawable.single_select_btn_bg_sel);
                } else {
                    isChoose = false;
                    arg0.setTag(0);
                    ((ImageView) arg0).setImageResource(R.drawable.single_select_btn_bg_nor);
                }
                mChooseDels[position] = isChoose;
                changeDelView();
            }

        });

        if (mClickList.contains(item.optString(HttpConstants.Data.ZhiwuFavorList.ID))) {
            holder.mNewsTitle.setTextColor(mContext.getResources().getColor(R.color.zhiwu_clicked_text));
        } else {
            holder.mNewsTitle.setTextColor(mContext.getResources().getColor(R.color.zhiwu_tittle_text));
        }
        return convertView;
    }

    private void changeDelView() {
        int count = 0;
        for (int i = 0; i < mChooseDels.length; i++) {
            if (mChooseDels[i]) {
                count++;
            }
        }
        ((MyFavoritesActivity) mContext).getSingleSelectDeleteListener().onChange(count);
    }

    static class ViewHolder {
        private ScaleImageView mImageView;
        private TextView mTvCommentCount;
        private TextView mTvClickCount;
        private TextView mNewsTitle;
        private TextView mNewsType;
        private ImageView mCheckBox;
        private ImageView mLlEditBg;
    }

    /**
     * 全选
     */
    public void setAllSelected() {
        if (mChooseDels != null) {
            for (int i = 0; i < mChooseDels.length; i++) {
                mChooseDels[i] = true;
            }
        }
        changeDelView();
        notifyDataSetChanged();
    }

    /**
     * 清除全选
     */
    public void clearAllSingleSelect() {
        if (mChooseDels != null) {
            for (int i = 0; i < mChooseDels.length; i++) {
                mChooseDels[i] = false;
            }
        }
        changeDelView();
        notifyDataSetChanged();
    }

    /**
     * 接口请求删除选中的知物收藏
     */
    public void deleteChooseFavor() {
        mZhiwuFavorFragment.showProgress();
        new RequestAction().batchRemoveFavorite(this, getBatchDeleteInfos());
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        // TODO Auto-generated method stub
        if (businessType.equals(Url.BATCH_REMOVE_FAVORITE_URL)) {
            mZhiwuFavorFragment.hideProgress();
            JSONArray deleteCompleteArray = new JSONArray();
            for (int i = 0; i < mChooseDels.length; i++) {
                if (mChooseDels != null && !mChooseDels[i]) {
                    deleteCompleteArray.put(mArray.optJSONObject(i));
                }
            }
            setData(deleteCompleteArray);
            ((MyFavoritesActivity) mContext).processModeSwitch();
            if (mArray == null || mArray.length() == 0) {
                mZhiwuFavorFragment.showNoDataLayout();
            }
            Toast.makeText(mContext, mContext.getString(R.string.delete_success_toast), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        // TODO Auto-generated method stub
        Toast.makeText(mContext, mContext.getString(R.string.delete_fail), Toast.LENGTH_LONG).show();
        mZhiwuFavorFragment.hideProgress();
    }

    @Override
    public Context getSelfContext() {
        // TODO Auto-generated method stub
        return mContext;
    }

    @Override
    public void onCancel(String businessType, Object session) {
        // TODO Auto-generated method stub

    }

    protected JSONArray getBatchDeleteInfos() {
        JSONArray array = new JSONArray();
        JSONArrayHelper helper = new JSONArrayHelper(array);
        for (int i = 0; i < mChooseDels.length; i++) {
            boolean checked = mChooseDels[i];
            if (checked) {
                Object itemObject = getItem(i);
                if (itemObject != null) {
                    JSONObject object = (JSONObject) itemObject;
                    String favoriteId = object.optString(HttpConstants.Data.BaseMyFavoriteList.FAV_ID_I);
                    String itemId = object.optString(HttpConstants.Data.BaseMyFavoriteList.ITEM_ID);
                    String favoriteType = object.optString(HttpConstants.Data.BaseMyFavoriteList.TYPE);
                    LogUtils.log(TAG, LogUtils.getThreadName() + " favoriteId = " + favoriteId + " itemId = "
                            + itemId + " favoriteType = " + favoriteType);
                    JSONObject deleteItemInfo = new JSONObject();
                    try {
                        deleteItemInfo.put(HttpConstants.Request.BatchRemoveFavorites.ID, favoriteId);
                        deleteItemInfo.put(HttpConstants.Request.BatchRemoveFavorites.ITEM_ID, itemId);
                        deleteItemInfo.put(HttpConstants.Request.BatchRemoveFavorites.TYPE, favoriteType);
                        helper.addToLast(deleteItemInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return array;
    }

    protected void singleSelectionAnimationOut(final View view) {
        view.clearAnimation();
        final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.single_select_out);
        anim.setFillAfter(false);
        view.startAnimation(anim);
        view.setVisibility(View.GONE);
    }

    protected void singleSelectionAnimationIn(final View view) {
        view.clearAnimation();
        final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.single_select_in);
        anim.setFillAfter(false);
        view.startAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

}
