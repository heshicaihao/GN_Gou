package com.gionee.client.activity.comments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.myfavorites.StoryDetailActivity;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.huewu.pla.ImageLoader;
import com.huewu.pla.PLA_AdapterView;
import com.huewu.pla.PLA_AdapterView.OnItemClickListener;
import com.huewu.pla.ScaleImageView;

public class NewsListAdapter extends BaseAdapter implements OnItemClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private JSONArray mJsonArray;
    private ImageLoader mLoader;
    private static final String TAG = "NewsListAdapter";
    private boolean mIsShowFrom;
    private View mClickedItemView;
    private SharedPreferences mPreference;
    private List<String> mClickList = new ArrayList<String>();
    private String mChannel = "0";

    public NewsListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLoader = new ImageLoader(context);
        mLoader.setIsUseMediaStoreThumbnails(false);
        // mImageList = list;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        mLoader.setRequiredSize(width / 2);
        mPreference = mContext.getSharedPreferences("comment_view", Context.MODE_PRIVATE);
    }

    public void setData(JSONArray jsonArray, String id) {
        mJsonArray = jsonArray;
        notifyDataSetChanged();
        getClickList();
        mChannel=id;
        try {
            mIsShowFrom = id.equals("0");
        } catch (Exception e) {
            e.printStackTrace();
            mIsShowFrom = false;
        }
    }

    private void getClickList() {
        mClickList.clear();
        for (int i = 0; i < mJsonArray.length(); i++) {
            String id = mJsonArray.optJSONObject(i).optString(HttpConstants.Data.NewsList.ID_S);
            if (mPreference.contains("cl_" + id)) {
                mClickList.add(id);
            }
        }

    }

    @Override
    public int getCount() {
        return (mJsonArray != null ? mJsonArray.length() : 0);
    }

    @Override
    public Object getItem(int position) {
        return mJsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_zhiwu_favor, null);
            holder = new ViewHolder();
            holder.imageView = (ScaleImageView) convertView.findViewById(R.id.news_pic);
            holder.tv_comment_count = (TextView) convertView.findViewById(R.id.tv_comment_count);
            holder.tv_click_count = (TextView) convertView.findViewById(R.id.tv_click_count);
            holder.news_title = (TextView) convertView.findViewById(R.id.news_title);
            holder.news_type = (TextView) convertView.findViewById(R.id.news_type);
            holder.hot = (ImageView) convertView.findViewById(R.id.recomment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject item = mJsonArray.optJSONObject(position);
        holder.imageView.setTag(item.optInt(HttpConstants.Data.ZhiwuFavorList.BGCOLOR));
        if (!TextUtils.isEmpty(item.optString(HttpConstants.Data.ZhiwuFavorList.IMAGE))) {
            mLoader.DisplayImage(item.optString(HttpConstants.Data.ZhiwuFavorList.IMAGE), holder.imageView);
        }
        String commentStr = item.optString(HttpConstants.Data.ZhiwuFavorList.COMMENT);
        holder.tv_comment_count.setText(commentStr);
        String hitCountStr = item.optString(HttpConstants.Data.ZhiwuFavorList.HITS);
        int countHit = 0;
        if (!hitCountStr.contains(mContext.getString(R.string.comment_count_str))) {
            countHit = Integer.parseInt(hitCountStr);
        }
        if (countHit >= 10000) {
            hitCountStr = countHit / 10000 + mContext.getString(R.string.comment_count_str);
        }
        holder.tv_click_count.setText(hitCountStr);
        holder.news_title.setText(item.optString(HttpConstants.Data.ZhiwuFavorList.TITLE) == null ? "" : item
                .optString(HttpConstants.Data.ZhiwuFavorList.TITLE));
        if (mIsShowFrom) {
            holder.news_type.setText(item.optString(HttpConstants.Data.ZhiwuFavorList.CAT) == null ? ""
                    : item.optString(HttpConstants.Data.ZhiwuFavorList.CAT));
            holder.news_type.setVisibility(View.VISIBLE);
        }
        if (item.optBoolean(HttpConstants.Data.NewsList.RECOMMAND_B)) {
            holder.hot.setVisibility(View.VISIBLE);
        } else {
            holder.hot.setVisibility(View.GONE);
        }
        if (mClickList.contains(item.optString(HttpConstants.Data.NewsList.ID_S))) {
            holder.news_title.setTextColor(mContext.getResources().getColor(R.color.zhiwu_clicked_text));
        } else {
            holder.news_title.setTextColor(mContext.getResources().getColor(R.color.zhiwu_tittle_text));
        }

        return convertView;
    }

    private static class ViewHolder {
        ScaleImageView imageView;
        TextView tv_comment_count;
        TextView tv_click_count;
        TextView news_title;
        TextView news_type;
        ImageView hot;
    }

    @Override
    public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
        JSONObject item = mJsonArray.optJSONObject(position);
        LogUtils.log(TAG, LogUtils.getFunctionName() + "position:" + position + "  item" + item);

        Intent intent = new Intent();
        intent.setClass(mContext, StoryDetailActivity.class);
        intent.putExtra(StatisticsConstants.KEY_INTENT_URL, item.optString(HttpConstants.Data.NewsList.URL_S));
        intent.putExtra("id", item.optInt(HttpConstants.Data.NewsList.ID_S));
        intent.putExtra("is_favorite", item.optBoolean(HttpConstants.Data.NewsList.FAVORITE_B));
        intent.putExtra("fav_id", item.optInt(HttpConstants.Data.NewsList.FAV_ID_I));
        intent.putExtra("comment_count", item.optString(HttpConstants.Data.NewsList.COMMENT_S));
        // intent.putExtra("click_count",
        // item.optString(HttpConstants.Data.NewsList.HITS_I));
        intent.putExtra("position", position);
        intent.putExtra(Constants.IS_SHOW_WEB_FOOTBAR, false);

        ((Activity) mContext).startActivityForResult(intent,
                Constants.ActivityRequestCode.REQUEST_CODE_COMMENT_DETAIL);
        mClickedItemView = view;
        ViewHolder holder = (ViewHolder) view.getTag();
        mClickList.add(item.optString(HttpConstants.Data.NewsList.ID_S));
        mPreference.edit().putBoolean("cl_" + item.optString(HttpConstants.Data.NewsList.ID_S), true)
                .commit();
        holder.news_title.setTextColor(mContext.getResources().getColor(R.color.zhiwu_clicked_text));
        StatService.onEvent(mContext, "tale_new", mChannel);
    }

    public void refreshClickData(Intent data) {

        LogUtils.log(TAG, LogUtils.getThreadName());
        String commentscount = data.getStringExtra("comments_count");
        int position = data.getIntExtra("position", -1);
        boolean isFavorite = data.getBooleanExtra("isFavorite", false);
        TextView comment_count = (TextView) mClickedItemView.findViewById(R.id.tv_comment_count);
        TextView click_count = (TextView) mClickedItemView.findViewById(R.id.tv_click_count);
        comment_count.setText(commentscount);
        JSONObject jsonObject = (JSONObject) getItem(position);
        String clickCount = jsonObject.optString(HttpConstants.Data.NewsList.HITS_I);
        try {
            int count = Integer.valueOf(clickCount)+1;
            clickCount = String.valueOf(count);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        click_count.setText(clickCount);
        try {
            if (jsonObject != null) {
                jsonObject.put(HttpConstants.Data.NewsList.HITS_I, clickCount);
                jsonObject.put(HttpConstants.Data.NewsList.COMMENT_S, commentscount);
                jsonObject.put(HttpConstants.Data.NewsList.FAVORITE_B, isFavorite);
                mJsonArray.put(position, jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
