package com.gionee.client.activity.question;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.shoppingmall.GNTitleBar;

public class GNFAQsActivity extends BaseFragmentActivity implements OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {
    private RadioGroup mTabBarRadio;
    private TabsAdapter mTabsAdapter;
    private ViewPager mViewPager;
    private Class<?>[] mFragmentClassesArray = new Class[] {GNFAQsFragment.class, GNFAQsFragment.class};
    private int mAttachFrangmentTag = -1;
    private String[] mUrls = {Url.MY_QUSTIONS_LIST, Url.MY_ANSWERS_LIST};
    private String[] mDataTags = {HttpConstants.Data.FAQsList.QUESTIONS_INFO_JO,
            HttpConstants.Data.FAQsList.ANSWERS_INFO_JO};

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_faqs_page);
        initView();
    }

    private void initTitleBar() {
        showTitleBar(true);
        GNTitleBar titleBar = getTitleBar();
        if (null != titleBar) {
            titleBar.setTitle(R.string.my_qustions_answers);
        }
        showShadow(false);
    }

    private void initView() {
        initTitleBar();
        mTabBarRadio = (RadioGroup) findViewById(R.id.faqs_tab_radio);
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
        mViewPager = (ViewPager) findViewById(R.id.faqs_view_pager);
        mViewPager.setAdapter(mTabsAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(0);
    }
    
    private static final class TabInfo {
        private final Class<?> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        TabInfo(Class<?> classes, Bundle args) {
            mClass = classes;
            mArgs = args;
        }
    }

    private class TabsAdapter extends FragmentPagerAdapter {
        private WeakReference<Activity> mWeakReference;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = (RadioButton) findViewById(checkedId);
        Object tag = radioButton.getTag();
        int size = mTabsAdapter.getCount();
        for (int i = 0; i < size; i++) {
            if (mTabsAdapter.getTabInfo(i) == tag) {
                mViewPager.setCurrentItem(i);
            }
        }
    }

    private void setBaiduStatService(int i) {
        if (0 == i) {
            StatService.onEvent(this, BaiduStatConstants.QA_QUESTION, "");
        } else {
            StatService.onEvent(this, BaiduStatConstants.QA_ANSWER, "");
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }
    
    private float allScroll = 0;

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        allScroll += (arg0 + arg1 + arg2);
        if (allScroll == 0) {
            StatService.onEvent(this, "gesture_back", "gesture_back");
            onBackPressed();
            AndroidUtils.exitActvityAnim(this);
        } else if (arg0 + arg1 + arg2 == 0) {
            allScroll = 0;
        }
    }

    @Override
    public void onPageSelected(int position) {
        int childCount = mTabBarRadio.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mTabBarRadio.getChildAt(i);
            if (view instanceof RadioButton) {
                Object tag = view.getTag();
                if (mTabsAdapter.getTabInfo(position) == tag) {
                    ((RadioButton) view).setChecked(true);
                    setBaiduStatService(i);
                }
            }
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        ++mAttachFrangmentTag;
        if (fragment instanceof GNFAQsFragment) {
            ((GNFAQsFragment) fragment).setFragmentParm(mUrls[mAttachFrangmentTag],
                    mDataTags[mAttachFrangmentTag]);
        }
        super.onAttachFragment(fragment);
    }
}
