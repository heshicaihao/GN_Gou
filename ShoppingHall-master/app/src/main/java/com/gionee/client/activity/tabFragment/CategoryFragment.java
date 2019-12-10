package com.gionee.client.activity.tabFragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.CategoryListAdapter;
import com.gionee.client.view.adapter.CategoryStickyGridHeadersAdapter;
import com.gionee.client.view.shoppingmall.GNSearchBar;
import com.gionee.framework.operation.net.GNImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

public class CategoryFragment extends BaseFragment {
    private static final String TAG = "CategoryFragment_Tag";
    private ListView mCategoryLeft;
    private StickyGridHeadersGridView mCategoryRight;
    private CategoryListAdapter mListAdapter;
    private JSONArray mLeftListData;
    private String mSelectType = null;
    private CategoryStickyGridHeadersAdapter<CategoryItemData> mGridAdapter;
    private ImageView mCategoryBanner;
    private RequestAction mAction;
    private String mBannerURL = null;
    private JSONObject mAdData;
    private int mPosition = 0;
    private LinearLayout mSubCategoryLayout;

    @Override
    public View getCustomToastParentView() {
        return null;
    }

    @Override
    public void onPageVisible() {
        super.onPageVisible();
        if (mSubCategoryLayout != null) {
            mSubCategoryLayout.invalidate();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GNImageLoader.getInstance().init(getActivity());
        mAction = new RequestAction();
    }

    private void requestNetWork() {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        if (checkNetworkNotVisiviblle()) {
            showErrorToast(mCategoryRight);
            return;
        }
        mAction.getCategoryTabList(this, HttpConstants.Data.CategoryTabList.LIST_INFO_JO);
        showProgressBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.category, null, false);
        mCategoryLeft = (ListView) view.findViewById(R.id.categrory_tabs_list);
        mSubCategoryLayout = (LinearLayout) view.findViewById(R.id.sub_category_layout);
        mCategoryRight = (StickyGridHeadersGridView) view.findViewById(R.id.category_grid);
        mCategoryBanner = (ImageView) view.findViewById(R.id.category_banner);
        mListAdapter = new CategoryListAdapter(getActivity());
        mCategoryLeft.setAdapter(mListAdapter);
        mGridAdapter = new CategoryStickyGridHeadersAdapter<CategoryItemData>(getActivity()
                .getApplicationContext(), R.layout.category_sticky_header, R.layout.category_gride_item);
        mCategoryRight.setAdapter(mGridAdapter);
        mCategoryLeft.setOnItemClickListener(new LeftItemOnClickListener());
        mCategoryRight.setOnItemClickListener(new RightGridItemOnClickListener());
        mCategoryBanner.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoWebPage(mBannerURL, true);
                StatService.onEvent(getActivity(), "category_r",
                        mAdData.optString(HttpConstants.Response.Category.NAME_S));
                ((GnHomeActivity) getSelfContext()).setIsClickEventOnCategoryFragment(true);
                ((GnHomeActivity) getSelfContext())
                        .addFlowStatistics(StatisticsConstants.CategoryPageConstants.ONE_LEVEL_PREFIX
                                + (mPosition + 1) + "-0");
            }
        });
        if (AndroidUtils.translateTopBar(getActivity())) {
            ((GNSearchBar) view.findViewById(R.id.category_top_menu_bar)).setTopPadding();
        }
        ((GnHomeActivity) getSelfContext())
                .addFlowStatistics(StatisticsConstants.CategoryPageConstants.ONE_LEVEL_PREFIX + 1);
        return view;
    }

    class LeftItemOnClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mPosition == position) {
                return;
            }
            mPosition = position;
            mListAdapter.setSelectItem(position);
            mGridAdapter.setData(null);
            mCategoryRight.requestFocusFromTouch();
            mCategoryRight.setSelection(0);
            getGridPositionData(position);
            ((GnHomeActivity) getSelfContext()).setIsClickEventOnCategoryFragment(true);
            ((GnHomeActivity) getSelfContext())
                    .addFlowStatistics(StatisticsConstants.CategoryPageConstants.ONE_LEVEL_PREFIX
                            + (mPosition + 1));
        }

    }

    class RightGridItemOnClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            List<CategoryItemData> mGridData = mGridAdapter.getData();
            int length = mGridData != null ? mGridData.size() : 0;
            if (position < 0 || position >= length) {
                LogUtils.loge(TAG, LogUtils.getThreadName() + "array out of bounds: position = " + position);
                return;
            }
            CategoryItemData itemData = mGridData.get(position);
            String url = itemData.mLink;
            gotoWebPageForResult(url, true);
            StatService.onEvent(getActivity(), "category_r", itemData.mName);
            ((GnHomeActivity) getSelfContext()).setIsClickEventOnCategoryFragment(true);
            ((GnHomeActivity) getSelfContext())
                    .addFlowStatistics(StatisticsConstants.CategoryPageConstants.ONE_LEVEL_PREFIX
                            + (mPosition + 1) + "-" + (position + 1));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLeftListData == null) {
            requestNetWork();
        }
    }

    private void getGridPositionData(int position) {
        mSelectType = mLeftListData.optJSONObject(position).optString(
                HttpConstants.Response.CategoryTab.LINK_S);
        mAction.getCategoryGridData(this, mSelectType);
//        mCategoryBanner.setVisibility(View.GONE);
        StatService.onEvent(getActivity(), "category_l",
                mLeftListData.optJSONObject(position).optString(HttpConstants.Response.CategoryTab.NAME_S));
    }

    private void showProgressBar() {
        if (isFirstBoot()) {
            showLoading();
        }
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        super.onSucceed(businessType, isCache, session);
        LogUtils.log(TAG, LogUtils.getThreadName() + businessType + "  iscache:" + isCache);
        if (isFirstBoot()) {
            resetFistBoot();
            hideLoading();
        }
        if (Url.CATEGORY_TAB_LIST_URL.equals(businessType)) {
            setCategoryTabs();
        } else if ((mSelectType != null) && (mSelectType.equals(businessType))) {
            setGridData(isCache);
        }
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        hideLoading();
    }

    private void setGridData(boolean iscache) {
        try {
            final JSONObject jsonObject = mSelfData.getJSONObject(mSelectType);
            JSONObject typeData = jsonObject.optJSONObject(HttpConstants.Response.Category.TYPE_DATA_JA);
            JSONArray partitionArray = typeData.optJSONArray(HttpConstants.Response.LIST_JA);
            List<CategoryItemData> itemDatas = new ArrayList<CategoryItemData>();

            for (int i = 0; i < partitionArray.length(); i++) {
                JSONObject partitionData = partitionArray.getJSONObject(i);
                String title = partitionData.optString(HttpConstants.Response.Category.NAME_S);
                JSONArray array = partitionData.optJSONArray(HttpConstants.Response.Category.ITEMS_ARRAY);
                for (int j = 0; j < array.length(); j++) {
                    JSONObject itemData = array.getJSONObject(j);
                    String name = itemData.optString(HttpConstants.Response.Category.NAME_S);
                    String image = itemData.optString(HttpConstants.Response.Category.IMG_S);
                    String link = itemData.optString(HttpConstants.Response.Category.LINK_S);
                    CategoryItemData data = new CategoryItemData(i, title, name, image, link);
                    itemDatas.add(data);
                }
            }

            mCategoryRight.setVisibility(itemDatas.size() > 0 ? View.VISIBLE : View.GONE);
            mGridAdapter.setData(itemDatas);

            updateADdata(jsonObject);
        } catch (Exception e) {
            LogUtils.loge(TAG, e.toString());
            LogUtils.loge(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateADdata(JSONObject jsonObject) {
        try {
            mAdData = jsonObject.optJSONObject(HttpConstants.Response.Category.AD_DATA_S);
            LogUtils.log(TAG, "AD  Data:" + mAdData);
            if (mAdData != null) {
                String mImageURL = mAdData.optString(HttpConstants.Response.Category.IMG_S);
                mBannerURL = mAdData.optString(HttpConstants.Response.Category.LINK_S);
                GNImageLoader.getInstance().loadBitmap(mImageURL, mCategoryBanner,
                        new ImageLoadingListener() {

                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                LogUtils.logd(TAG, LogUtils.getThreadName());
                                mCategoryBanner.setVisibility(View.VISIBLE);
                                mCategoryBanner.setImageBitmap(null);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                LogUtils.logd(TAG, LogUtils.getThreadName());
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                LogUtils.logd(TAG, LogUtils.getThreadName());
                                mCategoryBanner.requestFocus();
                                mCategoryBanner.setImageBitmap(loadedImage);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                            }
                        });

            } else {
                mBannerURL = null;
                mCategoryBanner.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            mBannerURL = null;
            mCategoryBanner.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void setCategoryTabs() {
        JSONObject jsonObject = mSelfData.getJSONObject(HttpConstants.Data.CategoryTabList.LIST_INFO_JO);
        if (jsonObject.length() < 1) {
            return;
        }
        try {
            mLeftListData = jsonObject.optJSONArray(HttpConstants.Response.CategoryTab.TYPE_JA);
            mSelectType = mLeftListData.optJSONObject(0).optString(HttpConstants.Response.CategoryTab.LINK_S);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        mListAdapter.setData(mLeftListData);
        mCategoryRight.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                getGridPositionData(mPosition);
            }
        });
    }

    @Override
    protected int setContentViewId() {
        return 0;
    }

    public static class CategoryItemData {
        public int mPartitionId;
        public String mCategoryTitle;
        public String mName;
        public String mImage;
        public String mLink;

        public CategoryItemData(int partitionId, String title, String name, String image, String link) {
            mPartitionId = partitionId;
            mCategoryTitle = title;
            mName = name;
            mImage = image;
            mLink = link;
        }
    }

}
