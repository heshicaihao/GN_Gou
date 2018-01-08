//package com.gionee.client.activity;
//
//import java.util.ArrayList;
//
//import android.os.Bundle;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ImageView;
//import android.widget.ImageView.ScaleType;
//
//import com.gionee.client.R;
//import com.gionee.client.activity.base.BaseFragmentActivity;
//import com.gionee.client.view.widget.PageIndicatorView;
//
//public class GuideActivity extends BaseFragmentActivity implements OnItemSelectedListener, OnClickListener {
//    private int[] mImagesList;
//    private ViewPager mGallery;
//    private PageIndicatorView mGallryPageIndex;
//    private ArrayList<View> mViewList;
//    private int mLastPageItem;
//    private int mLastDistance;
//    private ImageView mExitGuide;
//
//    @Override
//    protected void onCreate(Bundle arg0) {
//        super.onCreate(arg0);
//        setContentView(R.layout.main_loading_page);
//
//        mGallery = (ViewPager) findViewById(R.id.guide_gallery);
//        mGallryPageIndex = (PageIndicatorView) findViewById(R.id.gallery_page_index);
//        mExitGuide = (ImageView) findViewById(R.id.exit_guide);
//        mImagesList = new int[] {R.drawable.home_guide1, R.drawable.home_guide2};
////        GuideGalleyAdapter adapter = new GuideGalleyAdapter(this, mImagesList);
////        mGallery.setAdapter(adapter);
//        mGallryPageIndex.setTotalPage(mImagesList.length);
//        mGallryPageIndex.setmDotNormalResId(R.drawable.home_dot_nor);
//        mGallryPageIndex.setmDotSelectedResId(R.drawable.home_dot_sel);
//        mGallryPageIndex.setmDotWidth(18);
//        mGallryPageIndex.setCurrentPage(0);
//        mViewList = new ArrayList<View>();
//        initImageList();
//        mGallery.setAdapter(new GuideAdapter());
//        mGallery.setOnPageChangeListener(mPageChangeListener());
//    }
//
//    private OnPageChangeListener mPageChangeListener() {
//        return new OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int arg0) {
//                mGallryPageIndex.setCurrentPage(arg0);
//                if (arg0 != 0) {
//                    mExitGuide.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//                if (isLastPageAndScrollRight(arg0, arg2)) {
//                    gotoActivityWithOutParams(GnHomeActivity.class);
//                    finish();
//                }
//                mLastPageItem = arg0;
//                mLastDistance = arg2;
//            }
//
//            private boolean isLastPageAndScrollRight(int arg0, int arg2) {
//                return arg0 == mImagesList.length - 1 && mLastPageItem == mImagesList.length - 1 && arg2 == 0
//                        && mLastDistance == 0;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//            }
//        };
//    }
//
//    private void initImageList() {
//        for (int i = 0; i < mImagesList.length; i++) {
//            ImageView imageView = new ImageView(this);
//            imageView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
//                    LayoutParams.MATCH_PARENT));
//            imageView.setImageResource(mImagesList[i]);
//            imageView.setScaleType(ScaleType.CENTER_CROP);
//            mViewList.add(imageView);
//            if (i == mImagesList.length - 1) {
//                imageView.setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        gotoActivityWithOutParams(GnHomeActivity.class);
//                    }
//                });
//            }
//
//        }
//    }
//
//    private class GuideAdapter extends PagerAdapter {
//
//        @Override
//        public int getCount() {
//            return mImagesList.length;
//        }
//
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0 == arg1;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView(mViewList.get(position));
//
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            container.addView(mViewList.get(position));
//
//            return mViewList.get(position);
//        }
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        mGallryPageIndex.setCurrentPage(position);
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> arg0) {
//    }
//
//    @Override
//    public void onClick(View arg0) {
//        switch (arg0.getId()) {
//            case R.id.exit_guide:
//                gotoActivityWithOutParams(GnHomeActivity.class);
//                finish();
//                break;
//
//            default:
//                break;
//        }
//    }
//
//}
