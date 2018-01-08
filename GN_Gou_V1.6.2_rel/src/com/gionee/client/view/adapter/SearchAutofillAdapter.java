package com.gionee.client.view.adapter;

import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.gionee.client.R;
import com.gionee.client.activity.GNSearchActivity;
import com.gionee.client.business.util.AndroidUtils;

public class SearchAutofillAdapter extends BaseAdapter {

	private Context mContext;
	public JSONObject mData;
	public JSONArray mFillArray = new JSONArray();
	private JSONArray mMagicArray = new JSONArray();
	private LayoutInflater mInflater;
	private int mScreenWidth;
	public static final int MAGICID = 0;

	public SearchAutofillAdapter(Context mContext, JSONObject mData) {
		super();
		mInflater = LayoutInflater.from(mContext);
		this.mContext = mContext;
		this.mData = mData;
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
	}

	public void updateData(JSONObject mData) {
		mFillArray = mData.optJSONArray("result");
		mMagicArray = mData.optJSONArray("magic");
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFillArray == null ? 0 : mFillArray.length();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mFillArray == null ? null : mFillArray.optJSONArray(arg0)
				.optString(0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (arg1 == null) {
			arg1 = mInflater.inflate(R.layout.item_search_auto_fill, null);
			holder = new ViewHolder();
			holder.mTextView = (TextView) arg1.findViewById(R.id.tv_name);
			holder.mLlMagic = (LinearLayout) arg1.findViewById(R.id.ll_magic);
			holder.mButtomLine = arg1.findViewById(R.id.buttom_line);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		try {
			holder.mTextView
					.setText(mFillArray.optJSONArray(arg0).optString(0));
			// Define the string.
			// Measure the width of the text string.
			JSONObject currentMagicItem = null;
			holder.mLlMagic.removeAllViews();
			for (int i = 0; i < mMagicArray.length(); i++) {
				JSONObject magicDataItem = mMagicArray.optJSONObject(i);
				int magicPosition = magicDataItem.optInt("index");
				if (magicPosition == arg0 + 1) {
					currentMagicItem = magicDataItem;
				}
			}
			if (currentMagicItem != null) {
				JSONArray magics = currentMagicItem.optJSONArray("data");
				int magicPosition = currentMagicItem.optInt("index");
				if (magicPosition == arg0 + 1) {
					setMagicView(holder, magics);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (arg0 == getCount() - 1) {
			holder.mButtomLine.setVisibility(View.VISIBLE);
		} else {
			holder.mButtomLine.setVisibility(View.GONE);
		}
		return arg1;
	}

	private void setMagicView(ViewHolder holder, JSONArray magics) {
		// TODO Auto-generated method stub
		if (magics == null || magics.length() == 0) {
			return;
		}
		holder.mTextView.measure(0, 0);
		float textViewWidth = holder.mTextView.getMeasuredWidth();
		if (textViewWidth == mScreenWidth) {
			return;
		}
		float magicLayWidth = 0;
		int magicCount = magics.length() >= 3 ? 3 : magics.length();
		for (int i = 0; i < magicCount; i++) {
			holder.mLlMagic.measure(0, 0);
			magicLayWidth = holder.mLlMagic.getMeasuredWidth();
			if (magicLayWidth + textViewWidth < mScreenWidth) {
				TextView view = new TextView(mContext);
				LayoutParams layoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				if (i != 0) {
					layoutParams.leftMargin = AndroidUtils.px2px(mContext, 12);
				} else {
					layoutParams.leftMargin = 0;
				}
				view.setLayoutParams(layoutParams);
				view.setText(magics.optString(i));
				view.setTextColor(Color.parseColor("#888888"));
				view.setTextSize(13);
				view.setGravity(Gravity.CENTER);
				view.setBackgroundResource(R.drawable.magic_bg);
				view.setTag(holder.mTextView.getText());
				view.setId(MAGICID);
				view.setOnClickListener((GNSearchActivity) mContext);
				holder.mLlMagic.addView(view);
			}
		}
		holder.mLlMagic.measure(0, 0);
		magicLayWidth = holder.mLlMagic.getMeasuredWidth();
		if (magicLayWidth + textViewWidth >= mScreenWidth) {
			holder.mLlMagic.removeViewAt(holder.mLlMagic.getChildCount() - 1);
		}
	}

	private static class ViewHolder {
		public TextView mTextView;
		public LinearLayout mLlMagic;
		public View mButtomLine;
	}
}
