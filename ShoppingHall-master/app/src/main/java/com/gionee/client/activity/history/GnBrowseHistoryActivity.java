package com.gionee.client.activity.history;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.dbutils.DBOperationCallBack;
import com.gionee.client.business.dbutils.DBOperationManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.GnDateUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.view.shoppingmall.GNTitleBar;

public class GnBrowseHistoryActivity extends BaseFragmentActivity implements OnCheckedChangeListener,
        ViewPager.OnPageChangeListener, OnClickListener {

    private static final String TAG = "GnBrowseHistory_Activity";
    private ViewPager mViewPager;
    public GNTitleBar mTitleBar;
    private RadioGroup mTabBarRadio;
    private TabsAdapter mTabsAdapter;
    private int mCurrentFragment = 0;
    private int mAttachFrangmentTag = -1;
    private DBOperationManager mDbOperationManager;

    private List<BrowseHistoryInfo> mGoodsInfos = new ArrayList<BrowseHistoryInfo>();
    private List<BrowseHistoryInfo> mAllInfos = new ArrayList<BrowseHistoryInfo>();

    private static final int GOODS_INFOS_FRAGMENT = 0;
    private static final int ALL_INFOS_FRAGMENT = 1;
    private float mAllScroll = 0;
    private LinearLayout mScanerLayoutChoose;
    private ImageView mIvChoose;
    private TextView mTvGoods;
    private TextView mTvAll;
    private Class<?>[] mFragmentClassesArray = new Class[] {BrowseHistoryFragment.class,
            OrderHistoryFragment.class};
    private List<String> mGoodsHistoryHeader = new ArrayList<String>();
    private List<String> mAllHistoryHeader = new ArrayList<String>();
    private List<ArrayList<BrowseHistoryInfo>> mGoodsChildList = new ArrayList<ArrayList<BrowseHistoryInfo>>();
    private List<ArrayList<BrowseHistoryInfo>> mAllChildList = new ArrayList<ArrayList<BrowseHistoryInfo>>();
    public static boolean sIsFromOrderTip = false;// 是否是从点击订单提示页进入
    private static final String IS_FIRST_IN = "is_first_in";

    private DBOperationCallBack mRequestDataCallBack = new DBOperationCallBack() {

        @Override
        public void onResultCallBack(final int queryIndex, final Object result) {
            LogUtils.log(TAG, LogUtils.getThreadName() + queryIndex);
            if (null == result) {
                return;
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        switch (queryIndex) {
                            case GOODS_INFOS_FRAGMENT:
                                mGoodsInfos = convertToBrowseList(result);
                                initGoodsList();
                                break;
                            case ALL_INFOS_FRAGMENT:
                                mAllInfos = convertToBrowseList(result);
                                initAllList();
                                break;
                            default:
                                break;
                        }
//                        notifyReshAllFragement();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }

    };

    private void initAllList() {
        TreeMap<String, ArrayList<BrowseHistoryInfo>> allHistoryMap = getCategaryListMap(mAllInfos);
        Set<String> allKeySet = allHistoryMap.keySet();
        mAllHistoryHeader.clear();
        mAllChildList.clear();
        for (Iterator<String> iterator = allKeySet.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            mAllHistoryHeader.add(key);
            mAllChildList.add(allHistoryMap.get(key));

        }
        notifyRefresh(mCurrentFragment);
    }

    private void initGoodsList() {

        TreeMap<String, ArrayList<BrowseHistoryInfo>> goodsHistoryMap = getCategaryListMap(mGoodsInfos);
        Set<String> goodsKeySet = goodsHistoryMap.keySet();
        mGoodsHistoryHeader.clear();
        mGoodsChildList.clear();
        for (Iterator<String> iterator = goodsKeySet.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            mGoodsHistoryHeader.add(key);
            mGoodsChildList.add(goodsHistoryMap.get(key));

        }
        notifyRefresh(mCurrentFragment);
    }

    private TreeMap<String, ArrayList<BrowseHistoryInfo>> getCategaryListMap(
            List<BrowseHistoryInfo> mHistoryList) {
        TreeMap<String, ArrayList<BrowseHistoryInfo>> historyMap = new TreeMap<String, ArrayList<BrowseHistoryInfo>>();
        for (Iterator<BrowseHistoryInfo> iterator = mHistoryList.iterator(); iterator.hasNext();) {
            BrowseHistoryInfo info = (BrowseHistoryInfo) iterator.next();
            String formatDays = GnDateUtils.formatDays(GnBrowseHistoryActivity.this, info.getmDays(),
                    info.getmTimemillis());
            if (historyMap.containsKey(formatDays)) {
                ArrayList<BrowseHistoryInfo> list = historyMap.get(formatDays);
                list.add(info);
            } else {
                ArrayList<BrowseHistoryInfo> list = new ArrayList<BrowseHistoryInfo>();
                historyMap.put(formatDays, list);
                list.add(info);
            }
        }
        return historyMap;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<BrowseHistoryInfo> convertToBrowseList(Object result) {
        if (result instanceof ArrayList<?>) {
            ArrayList<BrowseHistoryInfo> info = (ArrayList<BrowseHistoryInfo>) result;
            return info;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.browse_history);
        initView();
        initData();
        mDbOperationManager.queryHistoryGoodsList(getApplicationContext());
        mDbOperationManager.queryBrowseHistoryList(getApplicationContext());
        if (sIsFromOrderTip) {
            mViewPager.setCurrentItem(1);
            mScanerLayoutChoose.setVisibility(View.INVISIBLE);
            ((RadioButton) mTabBarRadio.getChildAt(0)).setTextColor(getResources().getColor(
                    R.color.tab_text_color_nor));
            sIsFromOrderTip = false;
        } else {
            if (ShareDataManager.getBoolean(this, IS_FIRST_IN, true)) {
                mScanerLayoutChoose.setVisibility(View.VISIBLE);
                ((RadioButton) mTabBarRadio.getChildAt(0)).setTextColor(getResources().getColor(
                        R.color.tab_text_color_sel));
                mScanerLayoutChoose.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mScanerLayoutChoose.setVisibility(View.INVISIBLE);
                        ((RadioButton) mTabBarRadio.getChildAt(0)).setTextColor(getResources().getColor(
                                R.color.tab_text_color_nor));
                    }
                }, 2000);
                ShareDataManager.putBoolean(this, IS_FIRST_IN, false);
            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndroidUtils.finishActivity(this);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        List<String> group = getBrowseHistoryInfoHeaderList(++mAttachFrangmentTag);
        List<ArrayList<BrowseHistoryInfo>> list = getBrowseHistoryInfoItemList(mAttachFrangmentTag);
        boolean isShowType = getShowType(mAttachFrangmentTag);
        if (fragment instanceof BrowseHistoryFragment) {
            ((BrowseHistoryFragment) fragment).setBrowseHistoryInfoList(group, list, isShowType);
        }
        notifyRefresh(mCurrentFragment);
        super.onAttachFragment(fragment);
    }

    private boolean getShowType(int fragmentFlag) {
        return !(GOODS_INFOS_FRAGMENT == fragmentFlag);
    }

    private List<String> getBrowseHistoryInfoHeaderList(int fragmentFlag) {
        return GOODS_INFOS_FRAGMENT == fragmentFlag ? mGoodsHistoryHeader : mAllHistoryHeader;
    }

    private List<ArrayList<BrowseHistoryInfo>> getBrowseHistoryInfoItemList(int fragmentFlag) {
        return GOODS_INFOS_FRAGMENT == fragmentFlag ? mGoodsChildList : mAllChildList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_right_btn:
                showDialog();
                break;
            case R.id.iv_choose:
                if (mScanerLayoutChoose.getVisibility() == View.VISIBLE) {
                    mScanerLayoutChoose.setVisibility(View.GONE);
                    mIvChoose.setImageResource(R.drawable.down);
                    ((RadioButton) mTabBarRadio.getChildAt(0)).setTextColor(getResources().getColor(
                            R.color.tab_text_color_nor));
                } else {
                    if (mTabBarRadio.getCheckedRadioButtonId() == R.id.history_tab_goods) {
                        mScanerLayoutChoose.setVisibility(View.VISIBLE);
                        ((RadioButton) mTabBarRadio.getChildAt(0)).setTextColor(getResources().getColor(
                                R.color.tab_text_color_sel));
                        mIvChoose.setImageResource(R.drawable.up);
                    }
                }
                break;
            case R.id.tv_goods:
                mCurrentFragment = 0;
                notifyRefresh(mCurrentFragment);
                mScanerLayoutChoose.setVisibility(View.GONE);
                ((RadioButton) mTabBarRadio.getChildAt(0)).setTextColor(getResources().getColor(
                        R.color.tab_text_color_nor));
                mIvChoose.setImageResource(R.drawable.down);
                mTvGoods.setTextColor(getResources().getColor(R.color.tab_text_color_sel));
                mTvAll.setTextColor(getResources().getColor(R.color.tab_text_color_nor));
                break;
            case R.id.tv_all:
                mCurrentFragment = 1;
                notifyRefresh(mCurrentFragment);
                mScanerLayoutChoose.setVisibility(View.GONE);
                ((RadioButton) mTabBarRadio.getChildAt(0)).setTextColor(getResources().getColor(
                        R.color.tab_text_color_nor));
                mIvChoose.setImageResource(R.drawable.down);
                mTvGoods.setTextColor(getResources().getColor(R.color.tab_text_color_nor));
                mTvAll.setTextColor(getResources().getColor(R.color.tab_text_color_sel));
                break;
            default:
                break;
        }

    }

    public void removeHistoryInfo(BrowseHistoryInfo info) {

        if (null == info) {
            return;
        }
        mDbOperationManager.delete(info);
        mAllInfos.remove(info);
        mGoodsInfos.remove(info);
        initGoodsList();
        initAllList();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (arg0 == 0 && mAllScroll == 0 && mRlLoading.getVisibility() == View.GONE) {
            StatService.onEvent(this, "gesture_back", "gesture_back");
            onBackPressed();
            AndroidUtils.exitActvityAnim(this);
            return;
        }
        mAllScroll = 0;
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        mAllScroll += (arg0 + arg1 + arg2);
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            mTitleBar.setRightBtnTextColor(getResources().getColor(R.color.tab_text_color_nor));
            mTitleBar.getRightBtn().setOnClickListener(this);
            notifyRefresh(mCurrentFragment);
        } else {
            mTitleBar.setRightBtnTextColor(getResources().getColor(R.color.tab_text_color_unable));
            mTitleBar.getRightBtn().setOnClickListener(null);
            OrderHistoryFragment orderHistoryFragment = (OrderHistoryFragment) mTabsAdapter.getItem(position);
            if (orderHistoryFragment.mOrders != null) {
                mTitleBar.getRightBtn().setVisibility(View.VISIBLE);
            } else {
                mTitleBar.getRightBtn().setVisibility(View.GONE);
            }
            StatService.onEvent(this, "record", "record");
        }
        int childCount = mTabBarRadio.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mTabBarRadio.getChildAt(i);
            if (view instanceof RadioButton) {
                Object tag = view.getTag();
                if (mTabsAdapter.getTabInfo(position) == tag) {
                    ((RadioButton) view).setChecked(true);
                    mScanerLayoutChoose.setVisibility(View.GONE);
                    ((RadioButton) mTabBarRadio.getChildAt(0)).setTextColor(getResources().getColor(
                            R.color.tab_text_color_nor));
                    mIvChoose.setImageResource(R.drawable.down);
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1) {
        RadioButton radioButton = (RadioButton) findViewById(arg1);
        Object tag = radioButton.getTag();
        int size = mTabsAdapter.getCount();
        for (int i = 0; i < size; i++) {
            if (mTabsAdapter.getTabInfo(i) == tag) {
                mViewPager.setCurrentItem(i);
            }
        }
    }

    private static class TabsAdapter extends FragmentPagerAdapter {
        private WeakReference<Activity> mWeakReference;

        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> mClass;
            private final Bundle mArgs;
            private Fragment mFragment;

            TabInfo(Class<?> classes, Bundle args) {
                mClass = classes;
                mArgs = args;
            }
        }

        public TabsAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            mWeakReference = new WeakReference<Activity>(activity);
        }

        public void addTab(RadioButton radioButton, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            radioButton.setTag(info);
            mTabs.add(info);
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            if (info.mFragment == null) {
                info.mFragment = Fragment
                        .instantiate(mWeakReference.get(), info.mClass.getName(), info.mArgs);
            }
            return info.mFragment;
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        public TabInfo getTabInfo(int position) {
            return mTabs.get(position);
        }
    }

    private void initView() {
        showTitleBar(true);
        mTitleBar = getTitleBar();
        if (null != mTitleBar) {
            mTitleBar.setTitle(R.string.browse_history);
            mTitleBar.setRightBtnText(R.string.clear);
            mTitleBar.setRightBtnTextColor(getResources().getColor(R.color.tab_text_color_nor));
            mTitleBar.getRightBtn().setOnClickListener(this);
        }
        showShadow(false);
        mTabBarRadio = (RadioGroup) findViewById(R.id.history_tab_radio);
        mTabBarRadio.setOnCheckedChangeListener(this);

        mTabsAdapter = new TabsAdapter(this);

        int childCount = mTabBarRadio.getChildCount();
        int j = 0;
        for (int i = 0; i < childCount; i++) {
            View view = mTabBarRadio.getChildAt(i);
            if (view instanceof RadioButton) {
                mTabsAdapter.addTab((RadioButton) view, mFragmentClassesArray[j++], null);
            }
        }
        mViewPager = (ViewPager) findViewById(R.id.history_view_pager);
        mViewPager.setAdapter(mTabsAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(0);
        mScanerLayoutChoose = (LinearLayout) findViewById(R.id.scaner_layout_choose);
        mIvChoose = (ImageView) findViewById(R.id.iv_choose);
        mTvGoods = (TextView) findViewById(R.id.tv_goods);
        mTvAll = (TextView) findViewById(R.id.tv_all);
        mTvGoods.setOnClickListener(this);
        mTvAll.setOnClickListener(this);
    }

    private void initData() {
        mDbOperationManager = DBOperationManager.getInstance(getApplicationContext());
        mDbOperationManager.registerDBOperationCallBack(mRequestDataCallBack);
    }

    private void bulkDeleteInfos() {

        List<BrowseHistoryInfo> delteList = null;
        delteList = getBrowseHistoryInfoList(mCurrentFragment);
        mDbOperationManager.bulkDelteBrowseHistory(delteList);
        mDbOperationManager.queryBrowseHistoryList(this);
        mDbOperationManager.queryHistoryGoodsList(this);
    }

    private void notifyReshAllFragement() {
        for (int i = 0, length = mFragmentClassesArray.length; i < length; i++) {
            notifyRefresh(i);
        }
    }

    private void notifyRefresh(int fragmentFlag) {
        List<ArrayList<BrowseHistoryInfo>> list = getBrowseHistoryInfoItemList(fragmentFlag);
        List<String> group = getBrowseHistoryInfoHeaderList(fragmentFlag);
        if (fragmentFlag == mCurrentFragment) {
            showTitleBarRightBtn(group);
        }
        BrowseHistoryDataChangeNotify notify = (BrowseHistoryDataChangeNotify) getCurrentFragement(0);
        if (null != notify) {
            if (notify instanceof BrowseHistoryFragment) {
                ((BrowseHistoryFragment) notify).mIsShowType = getShowType(fragmentFlag);
            }
            LogUtils.log(TAG, LogUtils.getFunctionName() + "group size:" + group.size());
            notify.onBrowseHistoryDataChange(group, list);
        }
    }

    private List<BrowseHistoryInfo> getBrowseHistoryInfoList(int fragmentFlag) {
        List<BrowseHistoryInfo> list = null;
        if (GOODS_INFOS_FRAGMENT == fragmentFlag) {
            list = mGoodsInfos;
        } else {
            list = mAllInfos;
        }
        return list;
    }

    private Fragment getCurrentFragement(int index) {

        Fragment fragement = null;
        try {
            fragement = getSupportFragmentManager().findFragmentByTag(
                    "android:switcher:" + R.id.history_view_pager + ":" + index);
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName(), e);
        }
        return fragement;
    }

    private void showDialog() {
        int msgId;
        List<BrowseHistoryInfo> list;
        if (GOODS_INFOS_FRAGMENT == mCurrentFragment) {
            msgId = R.string.clear_goods;
            list = mGoodsInfos;
        } else {
            msgId = R.string.clear_alls;
            list = mAllInfos;
        }

        if (list.size() == 0) {
            return;
        }

        String msg = getResources().getString(msgId);
        SpannableStringBuilder style = new SpannableStringBuilder(msg);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tab_text_color_sel)), 4, 6,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        Dialog dialog = DialogFactory.createMsgDialog(GnBrowseHistoryActivity.this, mClearListener, style);
        if (null != dialog) {
            dialog.show();
        }
        StatService.onEvent(this, BaiduStatConstants.CLEAR_WEB_HISTORY, BaiduStatConstants.ALL);
    }

    private void showTitleBarRightBtn(List<String> list) {

        GNTitleBar titleBar = getTitleBar();
        if (null == titleBar) {
            return;
        }

        if (null == list || list.size() == 0) {
            titleBar.setRightBtnVisible(false);
        } else {
            titleBar.setRightBtnVisible(true);
        }
    }

    private View.OnClickListener mClearListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            bulkDeleteInfos();
            notifyReshAllFragement();
        }
    };

}
