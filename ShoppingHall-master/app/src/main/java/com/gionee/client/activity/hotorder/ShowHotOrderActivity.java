/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-3-6 上午10:42:05
 */
package com.gionee.client.activity.hotorder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.shoppingmall.GNTitleBar.OnRightBtnListener;
import com.gionee.client.view.widget.ImagePanelLayout;
import com.gionee.framework.operation.net.GNImageLoader;

/**
 * com.gionee.client.activity.hotorder.ShowHotOrderActivity
 * 
 * @author yangxiong <br/>
 * @date create at 2015-3-6 上午10:42:05
 * @description TODO 晒单展示页面
 */
public class ShowHotOrderActivity extends BaseFragmentActivity {
    private static final String TAG = "ShowHotOrderActivity";
    private TextView mSubmitTime;
    private TextView mCurrentStatus;
    private TextView mCheckInfos;
    private TextView mTitle;
    private TextView mContent;
    private TextView mNickName;
    private ProgressBar mProgressBar;
    private ImagePanelLayout mAddImageLayout;
    private String mOrderId;
    private String mHotOrderId;
    private int mCellWidth;
    private int mCellHeight;

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onSucceed(businessType, isCache, session);
        if (businessType.equals(Url.SHOW_HOT_ORDER)) {
            mProgressBar.setVisibility(View.GONE);
            JSONObject response = (JSONObject) session;
            LogUtils.log(TAG, LogUtils.getThreadName() + " data: " + response);
            if (response == null) {
                return;
            }
            showTime(response);
            showStatus(response);
            showCheckInfo(response);
            String title = showTitle(response);
            String content = showContent(response);
            String author = showAuthor(response);
            JSONArray imageList = showAddedImages(response);
            updateSubmitButton(response, title, content, author, imageList);
        }
    }

    private void updateSubmitButton(JSONObject response, final String title, final String content,
            final String author, final JSONArray imageList) {
        boolean isEdit = response.optBoolean(HttpConstants.Data.ShowHotOrder.IS_EDIT);
        GNTitleBar titleBar = getTitleBar();
        if (isEdit) {
            titleBar.getRightBtn().setEnabled(true);
            titleBar.setRightListener(new OnRightBtnListener() {
                @Override
                public void onClick() {
                    gotoSubmitHotOrderActivity(title, content, author, imageList);
                }
            });
        } else {
            titleBar.setRightBtnTextColor(getResources().getColor(R.color.edit_btn_unenable));
        }
    }

    private void gotoSubmitHotOrderActivity(final String title, final String content, final String author,
            final JSONArray imageList) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        Intent intent = new Intent(ShowHotOrderActivity.this, SubmitHotOrderActivity.class);
        intent.putExtra(Constants.ORDER_ID, mOrderId);
        intent.putExtra(Constants.HOT_ORDER_ID, mHotOrderId);
        intent.putExtra(HttpConstants.Data.ShowHotOrder.AUTHOR, author);
        intent.putExtra(HttpConstants.Data.ShowHotOrder.TITLE, title);
        intent.putExtra(HttpConstants.Data.ShowHotOrder.CONTENT, content);
        if (imageList != null && imageList.length() > 0) {
            intent.putExtra(HttpConstants.Data.ShowHotOrder.IMAGES, imageList.toString());
        }
        startActivity(intent);
        StatService.onEvent(ShowHotOrderActivity.this, BaiduStatConstants.ORDER_EDIT,
                BaiduStatConstants.ORDER_EDIT);
        AndroidUtils.finishActivity(ShowHotOrderActivity.this);
    }

    private JSONArray showAddedImages(JSONObject response) {
        final JSONArray imageList = response.optJSONArray(HttpConstants.Data.ShowHotOrder.IMAGES);
        final int length = imageList != null ? imageList.length() : 0;
        LogUtils.log(TAG, LogUtils.getThreadName() + " image length = " + length);
        if (length > 0) {
            findViewById(R.id.addImageLayout).setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < length; i++) {
            JSONObject imgInfo = imageList.optJSONObject(i);
            String imgUrl = imgInfo.optString(HttpConstants.Data.ShowHotOrder.IMAGE);
            final View rLayout = LayoutInflater.from(this).inflate(R.layout.add_image, null);
            ImageView addedImg = makeAddedImage(rLayout);
            mAddImageLayout.addView(rLayout, mAddImageLayout.getChildCount());
            GNImageLoader.getInstance().loadBitmap(imgUrl, addedImg);
        }
        return imageList;
    }

    private ImageView makeAddedImage(final View rLayout) {
        ImageView addedImg = (ImageView) rLayout.findViewById(R.id.addImage);
        LayoutParams para1;
        para1 = addedImg.getLayoutParams();
        para1.height = mCellHeight;
        para1.width = mCellWidth;
        addedImg.setLayoutParams(para1);
        final ImageView deleteImg = (ImageView) rLayout.findViewById(R.id.delete);
        deleteImg.setVisibility(View.GONE);
        return addedImg;
    }

    private String showAuthor(JSONObject response) {
        final String author = response.optString(HttpConstants.Data.ShowHotOrder.AUTHOR);
        if (!TextUtils.isEmpty(author)) {
            mNickName.setText(author);
        }
        return author;
    }

    private String showContent(JSONObject response) {
        final String content = response.optString(HttpConstants.Data.ShowHotOrder.CONTENT);
        if (!TextUtils.isEmpty(content)) {
            mContent.setText(content);
        }
        return content;
    }

    private String showTitle(JSONObject response) {
        final String title = response.optString(HttpConstants.Data.ShowHotOrder.TITLE);
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
        return title;
    }

    private void showCheckInfo(JSONObject response) {
        String reason = response.optString(HttpConstants.Data.ShowHotOrder.REASON);
        if (TextUtils.isEmpty(reason)) {
            reason = getString(R.string.nothing);
        }
        mCheckInfos.setText(reason);
    }

    private void showStatus(JSONObject response) {
        String status = response.optString(HttpConstants.Data.ShowHotOrder.STATUS);
        if (!TextUtils.isEmpty(status)) {
            mCurrentStatus.setText(status);
        }
        if (getString(R.string.examine_not_pass).equals(status)) {
            findViewById(R.id.examine_info).setVisibility(View.VISIBLE);
        }
    }

    private void showTime(JSONObject response) {
        String time = response.optString(HttpConstants.Data.ShowHotOrder.CREATE_TIME);
        if (!TextUtils.isEmpty(time)) {
            mSubmitTime.setText(time);
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(arg0);
        setContentView(R.layout.show_hot_order);
        initTitleBar();
        initViews();
        showHotOrder();
    }

    private void showHotOrder() {
        RequestAction requestAction = new RequestAction();
        requestAction.showHotOrder(this, HttpConstants.Data.ShowHotOrder.SHOW_HOT_ORDER_INFO_JO, mOrderId,
                mHotOrderId);
    }

    private void initViews() {
        mProgressBar = (ProgressBar) findViewById(R.id.loading_bar);
        mSubmitTime = (TextView) findViewById(R.id.submit_time_value);
        mCurrentStatus = (TextView) findViewById(R.id.current_status_value);
        mCheckInfos = (TextView) findViewById(R.id.reason_value);
        mTitle = (TextView) findViewById(R.id.title);
        mContent = (TextView) findViewById(R.id.content);
        mNickName = (TextView) findViewById(R.id.nickname);
        mAddImageLayout = (ImagePanelLayout) findViewById(R.id.addedImageLayout);
        mCellWidth = (AndroidUtils.getDisplayWidth(this) - AndroidUtils.dip2px(this, 26)) / 4;
        mCellHeight = (AndroidUtils.getDisplayWidth(this) - AndroidUtils.dip2px(this, 26)) / 4;
        mAddImageLayout.setmCellHeight(mCellHeight);
        mAddImageLayout.setmCellWidth(mCellWidth);
        mOrderId = getIntent().getStringExtra(Constants.ORDER_ID);
        mHotOrderId = getIntent().getStringExtra(Constants.HOT_ORDER_ID);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void initTitleBar() {
        GNTitleBar titleBar = getTitleBar();
        titleBar.setTitle(R.string.hotorder);
        titleBar.setVisibility(View.VISIBLE);
        titleBar.setRightBtnText(R.string.edit);
        titleBar.setRightBtnVisible(true);
        titleBar.getRightBtn().setEnabled(false);
    }
}
