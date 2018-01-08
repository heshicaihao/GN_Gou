// Gionee <yuwei> <2014-4-4> modify for CR00832427 begin
/*
 * GnHomeActivity.java
 * classes : com.gionee.client.AppRecommendActivity
 * @author yuwei
 * V 1.0.0
 * Create at 2013-4-8 下午5:20:16
 */
package com.gionee.client.activity.apprecommend;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.appDownload.AppDataHelper;
import com.gionee.client.business.appDownload.GNDownloadListener;
import com.gionee.client.business.appDownload.ListDownloadManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.GNDowanload;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.adapter.AppListAdapter;
import com.gionee.client.view.widget.PullToRefreshListView;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.business.LocalBuisiness;
import com.gionee.framework.operation.net.GNImageLoader;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;

/**
 * AppRecommendActivity
 * 
 * @author yuwei <br/>
 * @date create at 2014-4-4 下午6:19:17
 * @description TODO
 */
public class AppRecommendActivity extends BaseFragmentActivity implements OnLastItemVisibleListener,
        OnItemClickListener, GNDownloadListener, OnItemLongClickListener {
    /**
     * 
     */
    private static final int MIN_LIST_SHOWLOADING_SIZE = 3;
    private static final String TAG = "AppRecommendActivity_TAG";
    private PullToRefreshListView mAppList;
    private int mPage;
    private int mPerpage = 10;
    private boolean mHasnext;
    private static final String TAG_DATA = "APP_DATA_TAG";

    private AppListAdapter mAppListAdapter;
    private ArrayList<MyBean> mAppInfoList = new ArrayList<MyBean>();
    private static UpdataHandler sUpdateUiHandler;
    private ListDownloadManager mListDownloadManager;

    @Override
    protected void onCreate(Bundle bundle) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(bundle);
        setContentView(R.layout.app_recommond);
        initData();
        initView();
        setAdapter();
        requestAppList();

    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void initData() {
        sUpdateUiHandler = new UpdataHandler();
        GNImageLoader.getInstance().init(AppRecommendActivity.this);

    }

    @Override
    protected void onDestroy() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onDestroy();
        mListDownloadManager.shutDownQuery();
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void setAdapter() {
        mAppListAdapter = new AppListAdapter(this, mAppInfoList);
        mAppList.setAdapter(mAppListAdapter);
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void initView() {
        mAppList = (PullToRefreshListView) findViewById(R.id.app_recommond_list);
        mAppList.setOnLastItemVisibleListener(this);
        mAppList.setOnItemClickListener(this);
        mAppList.getRefreshableView().setOnItemLongClickListener(this);
        mListDownloadManager = ListDownloadManager.getInstance(this);
        mListDownloadManager.setsListener(this);
        showTitleBar(true);
        getTitleBar().setTitle(R.string.app_recommond_title);
    }

    @Override
    protected void onStart() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onStart();
        updateState();
        mListDownloadManager.setsListener(this);
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void updateState() {
        try {
            if (AndroidUtils.isExistSDCard()) {
                JSONObject jsonData = mSelfData
                        .getJSONObject(HttpConstants.Data.AppRecommond.APP_LIST_INFO_JO);
                JSONArray array = jsonData.optJSONArray(HttpConstants.Response.LIST_JA);
                LocalBuisiness.getInstance().getHandler().post(new UpdateListRunable(array));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    private void requestAppList() {
        mAppList.setMode(Mode.DISABLED);
        if (isFirstBoot()) {
            showPageLoading();
        }
        RequestAction action = new RequestAction();
        action.getRecommendAppList(this, HttpConstants.Data.AppRecommond.APP_LIST_INFO_JO, mPage + 1,
                mPerpage);
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG_DATA, LogUtils.getThreadName() + " isCache = " + isCache);
        super.onSucceed(businessType, isCache, session);
        JSONObject jsonData = mSelfData.getJSONObject(HttpConstants.Data.AppRecommond.APP_LIST_INFO_JO);
        bindListData(jsonData);
        mListDownloadManager.startUpdateState();
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void hideLoading() {
        if (mAppInfoList != null && mAppInfoList.size() > MIN_LIST_SHOWLOADING_SIZE && mHasnext) {
//    enablePullFromEnd();
            mAppList.setMode(Mode.PULL_FROM_END);
        }
        mAppList.onRefreshComplete();
        if (isFirstBoot()) {
            hidePageLoading();
            resetFistBoot();
        }
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        hideLoading();

    }

    /**
     * @param jsonData
     * @author yuwei
     * @description TODO
     */
    private void bindListData(JSONObject jsonData) {
        if (jsonData != null) {
            final JSONArray array;
            mHasnext = jsonData.optBoolean(HttpConstants.Response.HASNEXT);
            array = jsonData.optJSONArray(HttpConstants.Response.LIST_JA);
            mPage = jsonData.optInt(HttpConstants.Response.CURPAGE_I);
            LocalBuisiness.getInstance().getHandler().post(new UpdateListRunable(array));

        } else {
            hideLoading();
        }
    }

    class UpdateListRunable implements Runnable {
        private JSONArray mAppArray;

        public UpdateListRunable(JSONArray array) {
            super();
            this.mAppArray = array;
        }

        @Override
        public void run() {
            mAppInfoList = AppDataHelper.getAppListByJson(AppRecommendActivity.this, mAppArray);
            sUpdateUiHandler.sendMessage(sUpdateUiHandler.obtainMessage());
        }

    }

    @SuppressLint("HandlerLeak")
    private class UpdataHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            super.handleMessage(msg);
            mAppListAdapter.setmAppList(mAppInfoList);
            mSelfData.put(HttpConstants.Data.AppRecommond.APP_INFO_LIST_AL, mAppInfoList);
            hideLoading();
        }

    }

    @Override
    public void onBackPressed() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onBackPressed();
        AndroidUtils.exitActvityAnim(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parentView, View view, int position, long arg3) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        Intent intent = new Intent();
        intent.putExtra(Constants.ITENT_FLAG_APP_POSITION, position);
        intent.setClass(this, AppDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStatusChanged(MyBean bean) {

        try {
            View listItem = getView(bean);
            Button statusBtn = (Button) listItem.findViewById(R.id.app_install);
            ProgressBar progress = (ProgressBar) listItem.findViewById(R.id.app_download_progress);
            mAppListAdapter.setAppStatus(statusBtn, bean);
            mAppListAdapter.setProgress(progress, bean);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param bean
     * @return
     * @author yuwei
     * @description TODO
     */
    private View getView(MyBean bean) {
        int position = bean.getInt(HttpConstants.Data.AppRecommond.APP_LIST_POSITION_I);
        View listItem = mAppList.getRefreshableView().getChildAt(
                position + 1 - mAppList.getRefreshableView().getFirstVisiblePosition());
        return listItem;
    }

    @Override
    public void onProgressChanged(MyBean bean) {
        try {
            View listItem = getView(bean);
            ProgressBar progress = (ProgressBar) listItem.findViewById(R.id.app_download_progress);
            mAppListAdapter.setProgress(progress, bean);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onLastItemVisible() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (mHasnext) {
            requestAppList();
        } else {
            mAppList.onRefreshComplete();
            mAppList.setMode(Mode.DISABLED);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        MyBean bean = mAppInfoList.get(position - 1);
        if (bean.getInt(HttpConstants.Data.AppRecommond.APP_STATUS_EM) == GNDowanload.DownloadStatus.STATUS_WAITTING) {
            Dialog dialog = DialogFactory.createReloadDialog(this, bean);
            dialog.show();
            return true;
        }
        return false;
    }
}
//Gionee <yuwei> <2014-4-4> modify for CR00832427 end