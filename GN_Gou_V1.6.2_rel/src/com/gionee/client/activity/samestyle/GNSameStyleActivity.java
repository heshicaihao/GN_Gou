/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-12-11 上午11:08:01
 */
package com.gionee.client.activity.samestyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.AbstractListViewActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.adapter.SameStyleAdapter;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.widget.PullToRefreshListView;
import com.gionee.framework.operation.net.GNImageLoader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

/**
 * @author yangxiong <br/>
 * @date create at 2014-12-11 上午11:08:01
 * @description TODO 同款推荐
 */
public class GNSameStyleActivity extends AbstractListViewActivity {
    private static final String TAG = "GNSameStyleActivity";
    private ImageView mThumbImage;
    private TextView mTitle;
    private TextView mPrice;
    private TextView mSalesVolume;
    private TextView mScoreValue;
    private TextView mExpressMethod;
    private RelativeLayout mCurrentStyleLayout;
    @SuppressWarnings("unused")
    private String mCurrentStyleUrl;
    private TextView mNoMoreBestText;
    private String mGoodsId;
    private String mUnipid;

    @Override
    protected void initNoDataLayoutViews() {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " custruct mNoDataLayout");
        ViewStub stub = (ViewStub) findViewById(R.id.no_data_layout);
        if (stub == null) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + " stub = null");
            return;
        }
        mNoDataLayout = stub.inflate();
        ImageView amigoIcon = (ImageView) mNoDataLayout.findViewById(R.id.amigo_icon);
        amigoIcon.setVisibility(View.GONE);
        TextView mMessageTv = (TextView) mNoDataLayout.findViewById(R.id.message);
        mMessageTv.setText(R.string.no_more_best);
        mNoDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        try {
            refreshComplete();
            JSONObject object = mSelfData.getJSONObject(HttpConstants.Data.SameStyleInfo.SAME_STYLE_LIST);
            if (object == null) {
                hidePageLoading();
                return;
            }
            JSONArray array = object.optJSONArray(HttpConstants.Data.SameStyleInfo.LIST_INFO_JO);
            if (array == null || array.length() == 0) {
                showNoDataLayout();
                mNoDataLayout.setVisibility(View.VISIBLE);
                hidePageLoading();
                return;
            } else {
                LogUtils.log("SameStyleNoMoreText", "array lenth=" + array.length());
                hideNoDataLayout();
                mAdapter.setmCommentArray(array);
                mAdapter.notifyDataSetChanged();
                showNoMoreText(array);
                hidePageLoading();
            }
        } catch (Exception e) {
            hidePageLoading();
            e.printStackTrace();
        }
    }

    private void showNoMoreText(JSONArray array) throws JSONException {
        LogUtils.log(
                "SameStyleNoMoreText",
                "current goods id="
                        + getIntent().getStringExtra(HttpConstants.Request.SameStyleList.ID_I)
                        + "same style goods id="
                        + ((JSONObject) array.get(0))
                                .getString(HttpConstants.Request.SameStyleList.GOODS_ID_I));

        try {
            if (array.length() == 1
                    && ((JSONObject) array.get(0)).getString(HttpConstants.Request.SameStyleList.GOODS_ID_I)
                            .equals(getIntent().getStringExtra(HttpConstants.Request.SameStyleList.ID_I))) {
                LogUtils.log("SameStyleNoMoreText", "showNoMoreText");
                mNoMoreBestText.setVisibility(View.VISIBLE);
            } else {
                mNoMoreBestText.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onClick(v);
        switch (v.getId()) {
            case R.id.current_style_layout:
                StatService.onEvent(GNSameStyleActivity.this, BaiduStatConstants.SAME,
                        BaiduStatConstants.SAME);
                finish();
                AndroidUtils.webActivityExitAnim(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        hidePageLoading();
        showNodataInfoIfNeed();
        refreshComplete();
    }

    private void refreshComplete() {
        mListView.postDelayed(new Runnable() {

            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                mListView.onRefreshComplete();
            }
        }, 1000);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(arg0);
        setContentView(R.layout.same_style);
        initView();
        initData();
    }

    protected void initView() {
        super.initView();
        initTitleBar();
        mThumbImage = (ImageView) findViewById(R.id.thumb);
        mTitle = (TextView) findViewById(R.id.title);
        mPrice = (TextView) findViewById(R.id.price);
        mSalesVolume = (TextView) findViewById(R.id.month_sale);
        mScoreValue = (TextView) findViewById(R.id.score_value);
        mExpressMethod = (TextView) findViewById(R.id.express_method);
        mCurrentStyleLayout = (RelativeLayout) findViewById(R.id.current_style_layout);
        mCurrentStyleLayout.setOnClickListener(this);
        mNoMoreBestText = (TextView) findViewById(R.id.no_more_best);
    }

    protected void initListView() {
        mListView = (PullToRefreshListView) findViewById(R.id.recommend_list);
        mListView.setMode(Mode.PULL_FROM_START);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                requestData();
            }
        });
        mAdapter = new SameStyleAdapter(this, R.layout.same_style_item);
//        mAdapter.setmCommentArray(mCommentArray)
        mListView.setAdapter(mAdapter);
    }

    private void initTitleBar() {
        showTitleBar(true);
        GNTitleBar titleBar = getTitleBar();
        titleBar.setTitle(R.string.same_style_recommend);
    }

    private void requestData() {
        RequestAction requstAction = new RequestAction();
        requstAction.getSameStyleinfo(this, HttpConstants.Data.SameStyleInfo.SAME_STYLE_LIST, mGoodsId,
                mUnipid);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        mCurrentStyleUrl = intent.getStringExtra(HttpConstants.Data.SameStyleInfo.URL);
        String thumbUrl = intent.getStringExtra(HttpConstants.Data.SameStyleInfo.IMAGE);
        GNImageLoader.getInstance().loadBitmap(thumbUrl, mThumbImage);
        String title = intent.getStringExtra(HttpConstants.Data.SameStyleInfo.TITLE);
        mTitle.setText(title);
        String price = intent.getStringExtra(HttpConstants.Data.SameStyleInfo.PRICE);
        mPrice.setText(getString(R.string.sale_price, price));
        String salesVolume = intent.getStringExtra(HttpConstants.Data.SameStyleInfo.SALES_VOLUME);
        mSalesVolume.setText(getString(R.string.month_sale_with_count, salesVolume));
        String scoreValue = intent.getStringExtra(HttpConstants.Data.SameStyleInfo.SCORE);
        if (!TextUtils.isEmpty(scoreValue)) {
            mScoreValue.setText(scoreValue);
        }
        String expressMethod = intent.getStringExtra(HttpConstants.Data.SameStyleInfo.EXPRESS_METHOD);
        mExpressMethod.setText(expressMethod);
        mGoodsId = intent.getStringExtra(HttpConstants.Request.SameStyleList.ID_I);
        mUnipid = intent.getStringExtra(HttpConstants.Request.SameStyleList.PID_I);
        showPageLoading();
        requestData();
    }

    @Override
    protected void baiduState() {
        StatService.onEvent(this, BaiduStatConstants.SAME, BaiduStatConstants.CLICK);
    }
}
