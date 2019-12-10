// Gionee <yuwei><2013-12-27> add for CR00821559 begin
/*
 * RecommendFragment.java
 * classes : com.gionee.client.activity.attention.RecommendFragment
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-27 上午10:20:50
 */
package com.gionee.client.activity.tabFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.bargainprice.BargainPriceActivity;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.activity.base.INetWorkState;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.manage.CacheDataManager;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.AdvertiseGalleryAdapter;
import com.gionee.client.view.adapter.MyAttentionAdapter;
import com.gionee.client.view.adapter.SpeedServiceAdapter;
import com.gionee.client.view.shoppingmall.AbstractBaseList;
import com.gionee.client.view.shoppingmall.GNSearchBar;
import com.gionee.client.view.widget.CustomGallery;
import com.gionee.client.view.widget.PageIndicatorView;
import com.gionee.client.view.widget.PullToRefreshScrollView;
import com.gionee.framework.operation.net.GNImageLoader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * RecommendFragment
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-27 上午10:20:50
 * @description TODO
 */
public class HomeFragment extends BaseFragment implements OnItemClickListener, OnItemSelectedListener,
        OnClickListener, OnRefreshListener<ScrollView>, OnTouchListener, INetWorkState {
    private static final String BAIDU_STATISTIC_BANNER = "banner";
    private static final String BAIDU_STATISTIC_STORE = "store";
    private static final int MESSAGE_ADVERTISE_ANIMATION = 10000;
    private static final String TAG = "HomeFragment";
    private ViewHolder mViewHolder;
    public MyAttentionAdapter mMyAttentionAdapter;
    private MyAttentionAdapter mRecommondAdpater;
    private SpeedServiceAdapter mSpeedServiceAdpater;
    private View mContentView;
    private int mCurrentPage;
    private ImageView[] mSpecialRegionViews;
    private BroadcastReceiver mNetworkStateReceiver;
    private View mNoNetTips;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.log(TAG, LogUtils.getThreadName() + isVisibleToUser);
        if (mViewHolder == null) {
            return;
        }

    }

    private void scrollToTop() {
//        mViewHolder.mScrollView.scrollToTop();
//        ((ScrollView) mViewHolder.mScrollView.getRefreshableView()).smoothScrollTo(0, 0);
        mViewHolder.mAdvertiseGallery.requestFocusFromTouch();
    }

    @Override
    public void onResume() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onResume();
    }

    @Override
    public void onStop() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mContentView = inflater.inflate(R.layout.recommend_fragment, null, false);
        mViewHolder = new ViewHolder();
        initViewHolder(mViewHolder, mContentView);
        mNoNetTips = (View) mContentView.findViewById(R.id.no_network_tips);
        initAdapter();
        requestNetWork();
        startAdvertisePlay();
        if (AndroidUtils.translateTopBar(getActivity())) {
            ((GNSearchBar) mContentView.findViewById(R.id.top_menu_bar)).setTopPadding();
        }
        if (checkNetworkNotVisiviblle()) {
            mNoNetTips.setVisibility(View.VISIBLE);
        }
        return mContentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(savedInstanceState);
        GNImageLoader.getInstance().init(getActivity());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.Home.HOME_RESULT_CODE) {
            reloadAttentionGrid(true);
        }
    }

    @Override
    public void onDestroy() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        unregisterNetChageReceiver();
        super.onDestroy();

    }

    private void unregisterNetChageReceiver() {
        try {
            if (null == mNetworkStateReceiver) {
                return;
            }
            getActivity().unregisterReceiver(mNetworkStateReceiver);
            mNetworkStateReceiver = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void requestNetWork() {
        if (checkNetworkNotVisiviblle()) {
            resetLoadingView();
            return;
        }
        RequestAction action = new RequestAction();
        action.getMyAttentionList(this, HttpConstants.Data.RecommendHome.ATTENTION_LIST_JO);
        action.getSpeedyServiceList(this, HttpConstants.Data.RecommendHome.SPEED_SERVICE_JO);
        action.getSpecialRegion(this, HttpConstants.Data.RecommendHome.SPECIAL_REGION_JO);
        action.getAdvertiseBanner(this, HttpConstants.Data.RecommendHome.ADVERTISE_BANNER_JO);
        if (isFirstBoot()) {
            showLoading();
            mViewHolder.mScrollView.setVisibility(View.GONE);
        }

        mNetworkStateReceiver = new ConnectionChangeReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mNetworkStateReceiver, filter);
        preloadNetDataWhenWifi(action);
    }

    private void preloadNetDataWhenWifi(RequestAction action) {
        int netType = AndroidUtils.getNetworkType(getSelfContext());
        if (netType == Constants.WIFI_AVAILABLE) {            
            action.getCategoryTabList(this, null);
            action.getCommentsList(this, null, 1, AbstractBaseList.COUNT_PER_PAGE);
        }
    }

    private void startAdvertisePlay() {
        Message message = mLocalhandler.obtainMessage(MESSAGE_ADVERTISE_ANIMATION);
        mLocalhandler.sendMessageDelayed(message, 5000);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mLocalhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_ADVERTISE_ANIMATION:
                    try {
                        startAdvertiseAnimation();
                        Message message = mLocalhandler.obtainMessage(MESSAGE_ADVERTISE_ANIMATION);
                        mLocalhandler.sendMessageDelayed(message, 6000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void startAdvertiseAnimation() {
        int totalPage = mViewHolder.mAdvertiseGallery.getAdapter().getCount();
        mViewHolder.mAdvertiseGallery.setSelection((mCurrentPage + 1) % totalPage, true);
    }

    @Override
    public void onDestroyView() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onDestroyView();
        mLocalhandler.removeMessages(MESSAGE_ADVERTISE_ANIMATION);
    }

    /**
     * @param holder
     * @author yuwei
     * @description TODO
     */
    private void initViewHolder(final ViewHolder holder, View contentView) {
        holder.mAdvertiseGallery = (CustomGallery) contentView.findViewById(R.id.advertise_gallery);
        holder.mGallryPageIndex = (PageIndicatorView) contentView.findViewById(R.id.grid_page_index);
        holder.mBannerLeft = (ImageView) contentView.findViewById(R.id.banner_left);
        holder.mBannerRight = (ImageView) contentView.findViewById(R.id.banner_right);
        holder.mCenterBanner = (ImageView) contentView.findViewById(R.id.banner_center);
        holder.mBannerLeftBoard = (ImageView) contentView.findViewById(R.id.banner_left_board);
        holder.mBannerRightBoard = (ImageView) contentView.findViewById(R.id.banner_right_board);
        holder.mBannerCenterBoard = (ImageView) contentView.findViewById(R.id.banner_center_board);
        holder.mAttentionGrid = (GridView) contentView.findViewById(R.id.my_attention_grid);
        holder.mScrollView = (PullToRefreshScrollView) contentView.findViewById(R.id.pull_to_refresh);
        holder.mRecommondGrid = (GridView) contentView.findViewById(R.id.recommond_grid);
        holder.mSpeedServiceGrid = (GridView) contentView.findViewById(R.id.speed_service_grid);
        holder.mBannerBargain = (ImageView) contentView.findViewById(R.id.banner_bargain);
        holder.mBannerBargainBoard = (ImageView) contentView.findViewById(R.id.banner_bargain_board);
        mSpecialRegionViews = new ImageView[] {holder.mBannerLeft, holder.mBannerBargain,
                holder.mCenterBanner, holder.mBannerRight};
        holder.mEverydayCheck = (TextView) contentView.findViewById(R.id.every_data_check);
        initEverydataCheckNotifycation();
        setListener(holder);
//        processBootGuide(holder, contentView);
    }

//    /**
//     * @param holder
//     * @param mContentView
//     * @author yangxiong
//     * @description TODO
//     */
//    private void processBootGuide(final ViewHolder holder, View mContentView) {
//        if (CommonUtils.isNeedShowBootGuide(getSelfContext(), this.getClass().getName())) {
//            holder.mGuideImage = (ImageView) mContentView.findViewById(R.id.guide_pic);
//            holder.mGuideImageBg = (RelativeLayout) mContentView.findViewById(R.id.guide_bg);
//            holder.mGuideImageBg.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LogUtils.log(TAG, LogUtils.getThreadName());
//                    holder.mGuideImageBg.setVisibility(View.GONE);
//                }
//            });
//
//            updateBootGuideHeight(holder, mContentView);
//        }
//    }

    /**
     * @param holder
     * @param mContentView
     * @author yangxiong
     * @description TODO
     */
    private void updateBootGuideHeight(final ViewHolder holder, View mContentView) {
        final GNSearchBar bar = (GNSearchBar) mContentView.findViewById(R.id.top_menu_bar);
//        holder.mGuideImageBg.post(new Runnable() {
//            @Override
//            public void run() {
//                int barHeight = bar.getHeight();
//                int galleryHeight = holder.mAdvertiseGallery.getHeight();
//                int speedServiceGridHeight = holder.mSpeedServiceGrid.getHeight();
//                LogUtils.log(TAG, LogUtils.getThreadName() + " barHeight = " + barHeight
//                        + " galleryHeight = " + galleryHeight + " speedServiceGridHeight = "
//                        + speedServiceGridHeight);
//                int height = bar.getHeight() + holder.mAdvertiseGallery.getHeight()
//                        + holder.mSpeedServiceGrid.getHeight() + AndroidUtils.dip2px(getSelfContext(), 98);
//                LogUtils.log(TAG, LogUtils.getThreadName() + " height = " + height);
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                lp.setMargins(0, height, 0, 0);
//                holder.mGuideImage.setLayoutParams(lp);
//                holder.mGuideImageBg.setVisibility(View.VISIBLE);
//            }
//        });
    }

    private void initEverydataCheckNotifycation() {
        String msg = getResources().getString(R.string.everyday_check_cumulate_score);
        String colorText = getResources().getString(R.string.everyday_check_cumulate_score_value);
        SpannableStringBuilder style = new SpannableStringBuilder(msg);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tab_text_color_sel)),
                msg.indexOf(colorText), msg.indexOf(colorText) + colorText.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mViewHolder.mEverydayCheck.setText(style);
    }

    private void setListener(ViewHolder holder) {
        holder.mAdvertiseGallery.setOnItemSelectedListener(this);
        holder.mAdvertiseGallery.setOnItemClickListener(this);
        holder.mBannerLeftBoard.setOnClickListener(this);
        holder.mBannerRightBoard.setOnClickListener(this);
        holder.mBannerCenterBoard.setOnClickListener(this);
        holder.mBannerBargain.setOnClickListener(this);
        holder.mBannerBargainBoard.setOnClickListener(this);
        holder.mScrollView.setOnRefreshListener(this);

    }

    private void initAdapter() {
        mMyAttentionAdapter = new MyAttentionAdapter(getActivity(), mViewHolder.mAttentionGrid, this, true);
        mRecommondAdpater = new MyAttentionAdapter(getActivity(), mViewHolder.mRecommondGrid, this, false);
        mSpeedServiceAdpater = new SpeedServiceAdapter(this);
        mViewHolder.mAttentionGrid.setAdapter(mMyAttentionAdapter);
        mViewHolder.mRecommondGrid.setAdapter(mRecommondAdpater);
        mViewHolder.mSpeedServiceGrid.setAdapter(mSpeedServiceAdpater);
        mViewHolder.mAttentionGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mViewHolder.mRecommondGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
//        mViewHolder.mSpeedServiceGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        reloadAttentionGrid(false);
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void reloadAttentionGrid(boolean isScrollToEnd) {
        JSONArray mAttentionData = CacheDataManager.getMyAttentionArray(getActivity());
        Log.d("mAttention_data", mAttentionData.toString());
        mMyAttentionAdapter.updateData(mAttentionData);
        AndroidUtils.setListViewHeightBasedOnChildren(mViewHolder.mAttentionGrid, mMyAttentionAdapter, true);
        mViewHolder.mAttentionGrid.setSelected(false);
        if (isScrollToEnd) {
            mViewHolder.mAttentionGrid.requestFocus();

        }
    }

    private static class ViewHolder {
        public CustomGallery mAdvertiseGallery;
        public PageIndicatorView mGallryPageIndex;
        public ImageView mBannerLeft;
        public ImageView mBannerRight;
        public ImageView mBannerLeftBoard;
        public ImageView mBannerRightBoard;
        public ImageView mBannerCenterBoard;
        public ImageView mBannerBargain;
        public ImageView mBannerBargainBoard;
        public GridView mAttentionGrid;
        public GridView mRecommondGrid;
        public PullToRefreshScrollView mScrollView;
        public ImageView mCenterBanner;
        public GridView mSpeedServiceGrid;
        public TextView mEverydayCheck;
//        public TextView mNoNetTips;
//        private RelativeLayout mGuideImageBg;
//        private ImageView mGuideImage;
    }

    @Override
    public void onSucceed(final String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName() + "isCache" + isCache + mSelfData.toString());
        super.onSucceed(businessType, isCache, session);
        update(businessType);
    }

    public void startEverydayCheckAimation() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        Context context = getSelfContext();
        if (context == null) {
            LogUtils.log(TAG, LogUtils.getThreadName() + ", activity is destroyed when show animation");
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(getSelfContext(), R.anim.everyday_check);
        animation.setFillAfter(false);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewHolder.mEverydayCheck.setVisibility(View.GONE);
            }
        });
        mViewHolder.mEverydayCheck.setVisibility(View.VISIBLE);
        mViewHolder.mEverydayCheck.startAnimation(animation);
    }

    public class SpeedServiceImageListener implements ImageLoadingListener {

        private TextView mSpeedServiceView;

        public SpeedServiceImageListener(TextView mSpeedServiceView) {
            super();
            this.mSpeedServiceView = mSpeedServiceView;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            LogUtils.log(TAG, LogUtils.getThreadName());

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            LogUtils.log(TAG, LogUtils.getThreadName());

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            try {
//                Bitmap bitmap = BitmapUtills.compressFromBitmap(loadedImage, 24, 24, 20 * 1024);
                BitmapDrawable bd = new BitmapDrawable(getSelfContext().getResources(), loadedImage);
                bd.setBounds(0, 0, bd.getMinimumWidth(), bd.getMinimumHeight());
                mSpeedServiceView.setCompoundDrawables(bd, null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            LogUtils.log(TAG, LogUtils.getThreadName());
        }

    }

    private void update(String businessType) {
        if (businessType.equals(Url.ADVERTISE_BANNER_URL)) {
            bindAdvertiseData();
            if (isFirstBoot()) {
                hideLoading();
                mViewHolder.mScrollView.setVisibility(View.VISIBLE);
                resetFistBoot();
            }
        }

        if (businessType.equals(Url.SPEEDY_SERVICE_URL)) {
            bindSpeedServiceData();
            updateView();

        }

        if (businessType.equals(Url.ATTENTION_CHANNEL_URL)) {
            bindRecommondData();

        }
        if (businessType.equals(Url.SPECIAL_REGION)) {
            bindBannerData();
            if (isFirstBoot()) {
                hideLoading();
                mViewHolder.mScrollView.setVisibility(View.VISIBLE);
                resetFistBoot();
            }
        }

    }

    /**
     * @author yangxiong
     * @description TODO
     */
    private void bindSpeedServiceData() {
        JSONObject speedServiceJson = mSelfData
                .getJSONObject(HttpConstants.Data.RecommendHome.SPEED_SERVICE_JO);
        if (speedServiceJson != null) {
            JSONArray speedServiceList = speedServiceJson
                    .optJSONArray(HttpConstants.Response.AdvertiseBanner.AD_JA);
            int state = View.GONE;
            if (speedServiceList != null && speedServiceList.length() > 0) {
                state = View.VISIBLE;
                mViewHolder.mSpeedServiceGrid.setVisibility(View.VISIBLE);
                mSpeedServiceAdpater.updateData(speedServiceList);
//                setSpeedServiceGridviewHeight(mViewHolder.mSpeedServiceGrid, mSpeedServiceAdpater);
            }
            mViewHolder.mSpeedServiceGrid.setVisibility(state);
        }
    }

//    private void setSpeedServiceGridviewHeight(GridView gridView, BaseAdapter adapter) {
////      final int MAX_DEFUALT_COUNT = 3;
////      int count = 3;
////      if (adapter.getCount() > MAX_DEFUALT_COUNT) {
//        int count = (adapter.getCount() - 1) / 4 + 1;
////      }
//
//        ViewGroup.LayoutParams params = gridView.getLayoutParams();
//        int itemHeight = gridView.getResources().getDimensionPixelSize(R.dimen.speed_service_item_height)
//                + gridView.getResources().getDimensionPixelSize(R.dimen.speed_service_item_divider);
//        int listHeight = itemHeight * (count)
//                - gridView.getResources().getDimensionPixelSize(R.dimen.speed_service_item_divider);
//        params.height = listHeight;
//        LogUtils.logd(TAG, " listHeight = " + listHeight + " count = " + count);
//        gridView.setLayoutParams(params);
//    }

    @Override
    public void reload(boolean isReload) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.reload(isReload);
        reloadAttentionGrid(true);
    }

    private void updateView() {
        final Context context = getActivity();
        if (context == null) {
            return;
        }
        mViewHolder.mScrollView.postDelayed(new Runnable() {

            @Override
            public void run() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                String label = AndroidUtils.getCurrentTimeStr(context);
                mViewHolder.mScrollView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                resetLoadingView();
                scrollToTop();
//                if (mViewHolder.mGuideImageBg != null && mViewHolder.mGuideImageBg.isShown()) {
//                    updateBootGuideHeight(mViewHolder, mContentView);
//                }
            }
        }, 1000);

    }

    private void resetLoadingView() {
        try {
            mViewHolder.mScrollView.onRefreshComplete();
            mViewHolder.mScrollView.setMode(Mode.PULL_FROM_START);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public View getCustomToastParentView() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return mViewHolder.mAdvertiseGallery;
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        mViewHolder.mScrollView.onRefreshComplete();
        resetLoadingView();
        hideLoading();
        mViewHolder.mScrollView.setVisibility(View.VISIBLE);
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void bindRecommondData() {
        LogUtils.log(
                "RECOMMOND_DATA",
                LogUtils.getThreadName() + "myRecommond data="
                        + mSelfData.getJSONObject(HttpConstants.Data.RecommendHome.ATTENTION_LIST_JO));
        JSONObject attentionJson = mSelfData
                .getJSONObject(HttpConstants.Data.RecommendHome.ATTENTION_LIST_JO);
        if (attentionJson != null) {
            JSONArray attentionList = attentionJson
                    .optJSONArray(HttpConstants.Response.MyAttentionChannel.CHANNEL_JA);
            if (attentionList != null && attentionList.length() > 0) {
                mRecommondAdpater.updateData(attentionList);
                AndroidUtils.setListViewHeightBasedOnChildren(mViewHolder.mRecommondGrid, mRecommondAdpater,
                        false);
                mViewHolder.mRecommondGrid.setSelected(false);
            }
        }
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void bindAdvertiseData() {
        try {
            JSONObject advertiseJson = mSelfData
                    .getJSONObject(HttpConstants.Data.RecommendHome.ADVERTISE_BANNER_JO);
            if (advertiseJson != null) {
                bindAdvertiseList(advertiseJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param advertiseJson
     * @author yuwei
     * @description TODO
     */
    private void bindBannerData() {
        try {
            JSONObject specialRegionData = mSelfData
                    .getJSONObject(HttpConstants.Data.RecommendHome.SPECIAL_REGION_JO);
            JSONArray mSpecionRegionArray = specialRegionData
                    .optJSONArray(HttpConstants.Response.AdvertiseBanner.AD_JA);
            for (int i = 0; i < mSpecionRegionArray.length(); i++) {
                setBanner(mSpecionRegionArray.optJSONObject(i), mSpecialRegionViews[i]);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param bannerJson
     * @param image
     * @author yuwei
     * @description TODO
     */
    private void setBanner(JSONObject bannerJson, ImageView image) {
        try {
            if (bannerJson != null) {
                GNImageLoader.getInstance().loadBitmap(
                        bannerJson.optString(HttpConstants.Response.AdvertiseBanner.IMG_S), image);
                image.invalidate();
                image.setTag(R.string.banner_image_tag,
                        bannerJson.optString(HttpConstants.Response.AdvertiseBanner.LINK_S));

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param advertiseJson
     * @author yuwei
     * @description TODO
     */
    private void bindAdvertiseList(JSONObject advertiseJson) {
        try {
            JSONArray advertiseList = advertiseJson
                    .optJSONArray(HttpConstants.Response.AdvertiseBanner.ADS_JA);
            AdvertiseGalleryAdapter advertiseAdapter = new AdvertiseGalleryAdapter(advertiseList,
                    getActivity());
            mViewHolder.mAdvertiseGallery.setAdapter(advertiseAdapter);
            mViewHolder.mGallryPageIndex.setTotalPage(advertiseList.length());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        LogUtils.log(TAG, LogUtils.getThreadName());
        JSONObject advertiseJson = (JSONObject) parent.getAdapter().getItem(position);
        if (advertiseJson == null) {
            return;
        }
        mCurrentPage = position % mViewHolder.mGallryPageIndex.getTotalPage();
        mViewHolder.mGallryPageIndex.setCurrentPage(mCurrentPage);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mMyAttentionAdapter.notifyDataSetChanged();
        JSONObject json = (JSONObject) parent.getAdapter().getItem(position);
        LogUtils.logd("mActionItem", json.toString());
        String bannerAction = json.optString(HttpConstants.Response.AdvertiseBanner.ACTION_S);
        String bannerType = json.optString(HttpConstants.Response.AdvertiseBanner.TYPE_S);
        String bannerUrl = json.optString(HttpConstants.Response.MyAttentionChannel.LINK_S);
        if (!TextUtils.isEmpty(bannerAction)) {
            if (bannerAction.equals(Constants.BannerAction.STORY_LIST_PAGE.getValue())) {
                ((GnHomeActivity) getActivity()).onCheckTab(Constants.Home.PAGE_STORY, "");
            } else {
                try {
                    startAction(bannerUrl, bannerAction, bannerType);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } else {
            gotoWebPageForResult(bannerUrl, true);
        }
        StatService.onEvent(getActivity(), BAIDU_STATISTIC_BANNER, BAIDU_STATISTIC_BANNER);
        ((GnHomeActivity) getSelfContext())
                .addFlowStatistics(StatisticsConstants.HomePageConstants.BANNER_PREFIX + (++position));
        ((GnHomeActivity) getSelfContext()).setExitStatisticsFlag(true);
    }

    @Override
    public void onClick(View v) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mMyAttentionAdapter.notifyDataSetChanged();
        switch (v.getId()) {
            case R.id.banner_left_board:
//                gotoWebPage(mViewHolder.mBannerLeft, intent);
                rediRectPage(mViewHolder.mBannerLeft);
                StatService.onEvent(getActivity(), BAIDU_STATISTIC_STORE, "left");
                ((GnHomeActivity) getSelfContext())
                        .addFlowStatistics(StatisticsConstants.HomePageConstants.MARKETING_POSITION_1);
                ((GnHomeActivity) getSelfContext()).setExitStatisticsFlag(true);
                break;
            case R.id.banner_right_board:
                ((GnHomeActivity) getSelfContext()).setExitStatisticsFlag(true);
                rediRectPage(mViewHolder.mBannerRight);
                StatService.onEvent(getActivity(), BAIDU_STATISTIC_STORE, "right");
                ((GnHomeActivity) getSelfContext())
                        .addFlowStatistics(StatisticsConstants.HomePageConstants.MARKETING_POSITION_4);
                break;
            case R.id.banner_center_board:
                ((GnHomeActivity) getSelfContext()).setExitStatisticsFlag(true);
                rediRectPage(mViewHolder.mCenterBanner);
                StatService.onEvent(getActivity(), BAIDU_STATISTIC_STORE, "center");
                ((GnHomeActivity) getSelfContext())
                        .addFlowStatistics(StatisticsConstants.HomePageConstants.MARKETING_POSITION_3);
                break;
            case R.id.banner_bargain_board:
                ((GnHomeActivity) getSelfContext()).setExitStatisticsFlag(true);
                rediRectPage(mViewHolder.mBannerBargain);
                StatService.onEvent(getActivity(), BAIDU_STATISTIC_STORE, "middle");
                ((GnHomeActivity) getSelfContext())
                        .addFlowStatistics(StatisticsConstants.HomePageConstants.MARKETING_POSITION_2);
                break;
            default:
                break;
        }
    }

    private void rediRectPage(View v) {
        try {
            String configTag = (String) v.getTag(R.string.banner_image_tag);
            if (configTag.contains("http://")) {
                gotoWebPageForResult(configTag, true);
            }
            if (configTag.equals(BannerConfig.BARGAIN.getValue())) {
                gotoCutPage();
            }
            if (configTag.equals(BannerConfig.SPECIAL_PRICE.getValue())) {
                gotoBargainPriceActivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gotoCutPage() {
//        Intent intent = new Intent();
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setClass(getActivity(), BargainProductActivity.class);
//        getActivity().startActivityForResult(intent, Constants.ActivityRequestCode.REQUEST_CODE_CUT_PAGE);
//        AndroidUtils.enterActvityAnim(getActivity());
    }

    /**
     * @param bannerUrl
     * @param bannerAction
     * @param bannerType
     *            TODO
     */
    private void startAction(String bannerUrl, String bannerAction, String bannerType) {
        LogUtils.log("bannerAction", LogUtils.getThreadName() + "bannerUrl=" + bannerUrl + "bannerAction="
                + bannerAction + "bannerType=" + bannerType);
        Intent intent = new Intent();
        intent.setAction(bannerAction);
        intent.putExtra(Constants.Home.KEY_INTENT_INDEX, bannerType);
        boolean isHttpUrl = isHttpAction(bannerAction);
        setUrlParams(bannerUrl, intent, isHttpUrl);
        startActivity(intent);
        setEnterAnimation(isHttpUrl);
    }

    /**
     * @param bannerUrl
     * @param intent
     * @param isHttpUrl
     */
    private void setUrlParams(String bannerUrl, Intent intent, boolean isHttpUrl) {
        if (isHttpUrl) {
            intent.putExtra(Constants.Home.KEY_INTENT_URL, bannerUrl);
        }
    }

    /**
     * @param isHttpUrl
     */
    private void setEnterAnimation(boolean isHttpUrl) {
        if (isHttpUrl) {
            AndroidUtils.webActivityEnterAnim(getActivity());
        } else {
            AndroidUtils.enterActvityAnim(getActivity());
        }
    }

    /**
     * s
     * 
     * @param bannerAction
     */
    private boolean isHttpAction(String bannerAction) {
        try {
            return bannerAction.equals(Constants.BannerAction.STORY_DETAIL_PAGE.getValue())
                    || bannerAction.equals(Constants.BannerAction.THIRD_PARTY_NORMAL.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        requestNetWork();
        ((GnHomeActivity) getActivity())
                .addFlowStatistics(StatisticsConstants.HomePageConstants.FRESH_HOME_PAGE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtils.log(TAG, LogUtils.getThreadName() + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMyAttentionAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected int setContentViewId() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return 0;
    }

    private void gotoBargainPriceActivity() {
        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(getActivity(), BargainPriceActivity.class);
        getActivity()
                .startActivityForResult(intent, Constants.ActivityRequestCode.REQUEST_CODE_BARGAIN_PRICE);
        AndroidUtils.enterActvityAnim(getActivity());
    }

    private enum BannerConfig {
        BARGAIN("bargain"), SPECIAL_PRICE("special_price");
        private String mValue;

        BannerConfig(String value) {
            this.mValue = value;
        }

        public String getValue() {
            return this.mValue;
        }
    }

    @Override
    public void networkChange(boolean isVisible) {
        int visible = isVisible ? View.VISIBLE : View.GONE;
        mNoNetTips.setVisibility(visible);
    }

    private class ConnectionChangeReceiver extends BroadcastReceiver {

        private INetWorkState mState;

        public ConnectionChangeReceiver(INetWorkState state) {
            mState = state;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mState.networkChange(AndroidUtils.getNetworkType(context) == Constants.NET_UNABLE);
        }

    }

}
// Gionee <yuwei><2013-3-14> add for CR00821559 end
