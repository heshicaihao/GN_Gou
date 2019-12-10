// Gionee <yangxiong><2014-07-31> add for CR00850885 begin
package com.gionee.client.activity.bargainprice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.BargainPriceListAdapter;
import com.gionee.client.view.shoppingmall.GNTitleBar.OnRightBtnListener;
import com.gionee.client.view.widget.BasicMyMenu;
import com.gionee.client.view.widget.CategoryMenu;
import com.gionee.client.view.widget.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

/**
 * @author yangxiong <br/>
 * @description TODO 天天特价
 */
public class BargainPriceActivity extends BaseFragmentActivity implements OnClickListener {

    private static final String BAIDU_STATISTIC_MENULIST = "menu_list";
    private static final String TAG = "BargainPriceActivity";
    private RequestAction mRequestAction = new RequestAction();
    private PullToRefreshGridView mBargainPriceGridView;
    private LinearLayout mNoBargainLayout;
    private ProgressBar mProgressBar;
    private ImageView mGoTopBtn;
    private BargainPriceListAdapter mBargainAdaper;
    private int mCurpage = 1;
    private boolean mIsHasNextPage = false;
    private int mCurCategoryId;
    private String[] mCategoryArray;
    private int[] mCategoryId;
    private CategoryMenu mMenu;
    private boolean mIsLoading = false;

    /**
     * 每页加载数据条数
     */
    private static final int COUNT_PER_PAGE = 12;

    /**
     * @param session
     *            如果缓存，数据存放在map, 如果不缓存，数据存放在session.
     * @description 将页数据添加到adapter数据中
     */
    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        loadComplete();
        mIsLoading = false;
        if (businessType.equals(Url.BARGAIN_PRICE_URL)) {
            if (isFirstBoot()) {
                hidePageLoading();
                resetFistBoot();
            }
            JSONObject rebateInfo = mSelfData
                    .getJSONObject(HttpConstants.Data.BargainPrice.BARGAIN_PRICE_LIST);
            if (rebateInfo == null) {
                return;
            }

            try {
                JSONArray jsonArray = rebateInfo.getJSONArray(HttpConstants.Response.BargainPrice.LIST_JA);
                /**
                 * 显示没有数据的提示界面
                 */
                if (jsonArray.length() == 0) {
                    mNoBargainLayout.setVisibility(View.VISIBLE);
                    return;
                } else { // 显示天天特价列表
                    mNoBargainLayout.setVisibility(View.GONE);
                    mBargainAdaper.clearRebateData();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        mBargainAdaper.addBargainPriceData(jsonObject);
                    }
                    mBargainAdaper.notifyDataSetChanged();
                    mIsHasNextPage = rebateInfo.getBoolean(HttpConstants.Data.BargainPrice.HAS_NEXT);
                    mCurpage = rebateInfo.getInt(HttpConstants.Data.BargainPrice.CURPAGE);
                    upateCategoryMenu(rebateInfo);
                    updateView();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (mMenu != null) {
            mMenu.showMenu(getTitleBar());
        }
        return false;
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void loadComplete() {
        mBargainPriceGridView.postDelayed(new Runnable() {

            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                mBargainPriceGridView.onRefreshComplete();
                mBargainPriceGridView.setMode(Mode.BOTH);
            }
        }, 1000);

    }

    private void updateView() {
        String label = AndroidUtils.getCurrentTimeStr(BargainPriceActivity.this);
        mBargainPriceGridView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * @param rebateInfo
     * @throws JSONException
     * @author yangxiong
     * @description TODO
     */
    private void upateCategoryMenu(JSONObject rebateInfo) throws JSONException {
        String categorys = rebateInfo.getString(HttpConstants.Data.BargainPrice.CATEGORYS);
        LogUtils.logd(TAG, "categorys menu: " + categorys);
        JSONArray categoryArray = new JSONArray(categorys);
        int length = categoryArray.length();

        if (length < 1) {
            return;
        }

        mCategoryArray = new String[length];
        mCategoryId = new int[length];
        for (int i = 0; i < categoryArray.length(); i++) {
            JSONObject jsonObject = categoryArray.optJSONObject(i);
            mCategoryId[i] = jsonObject.optInt(CategoryDefine.DEFINE_CATEGORY_ID);
            mCategoryArray[i] = jsonObject.optString(CategoryDefine.DEFINE_CATEGORY_NAME);
        }

        Activity contextActivity = (Activity) getSelfContext();

        if (mMenu != null) {
            mMenu.setInitData(contextActivity, mCategoryArray, mCategoryId, length);
        } else {
            mMenu = new CategoryMenu(contextActivity, mCategoryArray, mCategoryId, null, length) {
                @Override
                public void onItemClick(int viewId) {
                    if (mCurCategoryId != viewId) {
                        mBargainAdaper.clearRebateData();
                        mBargainAdaper.notifyDataSetChanged();
                        closeCategoryMenu();
                        switchCategory(viewId);
                    }

                }
            };
        }
    }

    /**
     * 
     * @author yangxiong
     * @description TODO
     */
    private void closeCategoryMenu() {
        if (mMenu != null) {
            mMenu.menuDismiss();
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(arg0);
        setContentView(R.layout.bargain_price_activity);
        initData();
        initView();
    }

    private void initView() {
        initTitleBar();

        mGoTopBtn = (ImageView) findViewById(R.id.go_top);
        mGoTopBtn.setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_bar);
        mNoBargainLayout = (LinearLayout) findViewById(R.id.no_bargain_price_layout);
        mBargainPriceGridView = (PullToRefreshGridView) findViewById(R.id.bargainPriceList);
        mBargainPriceGridView.getRefreshableView().setSelector(new ColorDrawable(Color.TRANSPARENT));
        mBargainPriceGridView.setAdapter(mBargainAdaper);
        mBargainPriceGridView.setMode(Mode.BOTH);
        mBargainPriceGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (mIsLoading) {
                    return;
                }
                mCurpage = 1;
                mIsLoading = true;
                mRequestAction.bargainPriceList(BargainPriceActivity.this,
                        HttpConstants.Data.BargainPrice.BARGAIN_PRICE_LIST, mCurpage, COUNT_PER_PAGE,
                        mCurCategoryId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (mIsLoading) {
                    return;
                }
                mBargainPriceGridView.onRefreshComplete();
                LogUtils.log(TAG, LogUtils.getThreadName() + "isHasNextPage = " + mIsHasNextPage);
                if (mIsHasNextPage) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mBargainPriceGridView.setMode(Mode.DISABLED);
                    mIsLoading = true;
                    mRequestAction.bargainPriceList(BargainPriceActivity.this,
                            HttpConstants.Data.BargainPrice.BARGAIN_PRICE_LIST, mCurpage + 1, COUNT_PER_PAGE,
                            mCurCategoryId);
                } else {
                    mBargainPriceGridView.setMode(Mode.PULL_FROM_START);
                    Toast.makeText(BargainPriceActivity.this, R.string.no_more_msg, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        mBargainPriceGridView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                int status = View.VISIBLE;
                if (firstVisibleItem == 0) {
                    status = View.GONE;
                }
                mGoTopBtn.setVisibility(status);
            }
        });

        if (isFirstBoot()) {
            showPageLoading();
        }
        mIsLoading = true;
        mRequestAction.bargainPriceList(this, HttpConstants.Data.BargainPrice.BARGAIN_PRICE_LIST, 1,
                COUNT_PER_PAGE, mCurCategoryId);
    }

    /**
     * 
     * @author yangxiong
     * @description TODO
     */
    private void initTitleBar() {
        showTitleBar(true);
        getTitleBar().setTitle(R.string.bargain_price);
        getTitleBar().setRightBtnVisible(true);
        getTitleBar().setRightBtnText(R.string.classify);
        getTitleBar().setRightListener(new OnRightBtnListener() {

            @Override
            public void onClick() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                openOptionsMenu();
                StatService.onEvent(getSelfContext(), BAIDU_STATISTIC_MENULIST, BAIDU_STATISTIC_MENULIST);
            }
        });
    }

    public void switchCategory(int categoryId) {
        if (mIsLoading) {
            return;
        }
        mCurpage = 1;
        mIsHasNextPage = false;
        mCurCategoryId = categoryId;
        mProgressBar.setVisibility(View.VISIBLE);
        mIsLoading = true;
        mRequestAction.bargainPriceList(this, HttpConstants.Data.BargainPrice.BARGAIN_PRICE_LIST, 1,
                COUNT_PER_PAGE, mCurCategoryId);
    }

    private void initData() {
        mCurCategoryId = 0;
        mBargainAdaper = new BargainPriceListAdapter(this.getSelfContext());
        mBargainAdaper.clearRebateData();
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        loadComplete();
        mIsLoading = false;
        hideLoadingProgress();
        hidePageLoading();
        closeCategoryMenu();
        showNodataInfoIfNeed();

    }

    /*    @Override
        public View getCustomToastParentView() {
            LogUtils.log(TAG, LogUtils.getThreadName());
            return mBargainPriceGridView;
        }*/

    /**
     * @author yangxiong
     * @description TODO
     */
    private void showNodataInfoIfNeed() {
        JSONObject rebateInfo = mSelfData.getJSONObject(HttpConstants.Data.BargainPrice.BARGAIN_PRICE_LIST);
        int productCount = 0;

        if (rebateInfo != null) {
            try {
                JSONArray jsonArray = rebateInfo.getJSONArray(HttpConstants.Response.BargainPrice.LIST_JA);
                productCount = jsonArray.length();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 显示没有数据的提示界面
         */
        if (productCount == 0) {
            mNoBargainLayout.setVisibility(View.VISIBLE);
        } else {
            mNoBargainLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public Context getSelfContext() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return BargainPriceActivity.this;
    }

    public static final class CategoryDefine {
        public static final String DEFINE_CATEGORY_ID = "id";
        public static final String DEFINE_CATEGORY_NAME = "name";
    }

    public BasicMyMenu getMenu() {
        return mMenu;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_top:
                mBargainPriceGridView.getRefreshableView().setSelection(0);
                break;
            default:
                break;
        }

    }
}
//Gionee <yangxiong><2013-12-23> add for CR00850885 end
