/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-12-12 上午11:26:49
 */
package com.gionee.client.activity.base;

import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gionee.client.R;
import com.gionee.client.business.util.CommonUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.adapter.AbstractListBaseAdapter;
import com.gionee.client.view.widget.PullToRefreshListView;

/**
 * com.gionee.client.activity.contrast.AbstractListViewActivity
 * 
 * @author yangxiong <br/>
 * @date create at 2014-12-12 上午11:26:49
 * @description TODO
 */
public abstract class AbstractListViewActivity extends BaseFragmentActivity implements OnClickListener {
    private static final String TAG = "AbstractListViewActivity";
    protected PullToRefreshListView mListView;
    protected AbstractListBaseAdapter mAdapter;
    protected ImageView mGoTopBtn;
    protected View mNoDataLayout;

    public AbstractListViewActivity() {
        super();
    }

    @Override
    public void onClick(View v) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        switch (v.getId()) {
            case R.id.go_top:
                mListView.getRefreshableView().setSelection(0);
                break;
            default:
                break;
        }
    }

    protected void initView() {
        mGoTopBtn = (ImageView) findViewById(R.id.go_top);
        mGoTopBtn.setOnClickListener(this);
//        requestData();
        setListView();
    }

    protected abstract void initListView();

    protected void setListView() {
        initListView();
        if (mListView == null) {
            return;
        }

        mListView.getRefreshableView().setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.logd(TAG, LogUtils.getThreadName());
                try {
                    JSONObject itemData = (JSONObject) mAdapter.getItem(position - 1);
                    CommonUtils.gotoWebViewActvity(AbstractListViewActivity.this,
                            itemData.optString(HttpConstants.Data.SameStyleInfo.URL), true);
//                    StatService.onEvent(AbstractListViewActivity.this, "b_goods", "" + mRequstID);
                    baiduState();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        showNodataInfoIfNeed();
    }

    protected abstract void baiduState();

    protected void showNodataInfoIfNeed() {
        // JSONObject rebateInfo = mSelfData.getJSONObject(mDataTargetKey);
        int productCount = 1;
        // if (rebateInfo != null) {
        // try {
        // JSONArray jsonArray = rebateInfo.getJSONArray(HttpConstants.Response.LIST_JA);
        // productCount = jsonArray.length();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }

        /**
         * 显示没有数据的提示界面
         */
        if (productCount == 0 || mAdapter.getCount() == 0) {
            showNoDataLayout();
        } else {
            hideNoDataLayout();
        }
    }

    public void showNoDataLayout() {
        if (mNoDataLayout != null) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + " mNoDataLayout  != null ");
            mNoDataLayout.setVisibility(View.VISIBLE);
            return;
        }
        initNoDataLayoutViews();
    }

    public void hideNoDataLayout() {
        LogUtils.logd(TAG, LogUtils.getThreadName());
        if (mNoDataLayout != null) {
            mNoDataLayout.setVisibility(View.GONE);
        }
    }

    protected void initNoDataLayoutViews() {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " custruct mNoDataLayout");
        ViewStub stub = (ViewStub) findViewById(R.id.no_data_layout);
        if (stub == null) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + " stub = null");
            return;
        }
        mNoDataLayout = stub.inflate();
        RelativeLayout above = (RelativeLayout) mNoDataLayout.findViewById(R.id.above_layout);

        above.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (isFastDoubleClick()) {
                    return;
                }
                // onClickWhenNoDataLayout();
            }
        });
//        TextView mMessageTv = (TextView) mNoDataLayout.findViewById(R.id.message);
        mNoDataLayout.setVisibility(View.VISIBLE);
    }

}