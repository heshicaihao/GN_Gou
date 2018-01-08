// Gionee <yangxiong><2014-1-3> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-1-3 上午11:26:53
 */
package com.gionee.client.view.widget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.ViewFlipper;

import com.gionee.client.R;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;

/**
 * com.gionee.client.view.widget.CategoryMenu
 * 
 * @author yangxiong <br/>
 * @date create at 2014-1-3 上午11:26:53
 * @description TODO
 */
public abstract class CategoryMenu extends BasicMyMenu {
    private static final String TAG = "CategoryMenu";
    private ListView mListView;
    private LinearLayout mLinearLayout;

//    private TextView mTopMenuImg;

    public CategoryMenu(Activity activity, String[] nameArray, int[] nameId, int[] imageArr, int count) {
        super(activity, nameArray, nameId, imageArr, count, R.layout.menu_view_category);
    }

    public CategoryMenu(Activity activity, String[] nameArray, int[] nameId, int count) {
        super(activity, nameArray, nameId, null, count, R.layout.menu_view_category);
    }

    /**
     * @author yangxiong
     * @description TODO
     */
    @SuppressLint("NewApi")
    public void setInitData(Activity activity, String[] nameArray, int[] nameId, int[] imageArr, int count) {
        this.mActivity = activity;
        this.mNameArray = nameArray != null ? Arrays.copyOf(nameArray, nameArray.length) : null;
        this.mNameId = nameId != null ? Arrays.copyOf(nameId, nameId.length) : null;
        this.mImageArr = imageArr != null ? Arrays.copyOf(imageArr, imageArr.length) : null;
        this.mCount = count;
        initMenuAdapter(mListView);
    }

    @SuppressLint("NewApi")
    public void setInitData(Activity activity, String[] nameArray, int[] nameId, int count) {
        this.mActivity = activity;
        this.mNameArray = nameArray != null ? Arrays.copyOf(nameArray, nameArray.length) : null;
        this.mNameId = nameId != null ? Arrays.copyOf(nameId, nameId.length) : null;
        this.mImageArr = null;
        this.mCount = count;
        initMenuAdapter(mListView);
    }

    /**
     * @description init menu
     * @author yuwei
     */
    protected void initMenu(int menuViewLayout) {
        initView(menuViewLayout);
    }

    private void initPopupWindow(int menuViewLayout) {
        mViewFlipper = new ViewFlipper(mActivity);
        mLinearLayout = (LinearLayout) mActivity.getLayoutInflater().inflate(menuViewLayout, null);
        mViewFlipper.addView(mLinearLayout);
        mViewFlipper.setFlipInterval(1000000);
        mPopupWindow = new PopupWindow(mViewFlipper, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.update();
//        mTopMenuImg = (TextView) mActivity.findViewById(R.id.top_menu);
//        mPopupWindow.setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                mTopMenuImg.setBackgroundResource(R.drawable.select_bk);
//            }
//        });
    }

    private void initView(int menuViewLayout) {
        initPopupWindow(menuViewLayout);
        initListview(mLinearLayout);
    }

    /**
     * @param mLinearLayout
     * @author yangxiong
     * @description TODO
     */
    private void initListview(LinearLayout mLinearLayout) {
        mListView = (ListView) mLinearLayout.findViewById(R.id.menu_ListView);
        initMenuAdapter(mListView);
        mListView.setOnKeyListener(this);
        mListView.setOnItemClickListener(this);
        /**
         * 如果分类过多，菜单的高度会充满整个屏幕。
         */
        if (mCount > 11) {
            LayoutParams layoutParams = mListView.getLayoutParams();
            int screenHeight = AndroidUtils.getDisplayHeight(mActivity);
            layoutParams.height = screenHeight * 4 / 5;
            mListView.setLayoutParams(layoutParams);
        }
    }

    /**
     * @param mListView
     * @author yangxiong
     * @description TODO
     */
    private void initMenuAdapter(ListView mListView) {
        initAdapterData();
        setMenuAdapter(mListView);
    }

    @Override
    protected void setMenuAdapter(AbsListView mListView) {
        if (mListView == null) {
            return;
        }
        LogUtils.log(TAG, LogUtils.getThreadName());
        BaseAdapter adapter = new SimpleAdapter(mActivity, mData, R.layout.menu_item_category,
                new String[] {ITEM_TEXT}, new int[] {R.id.menu_item_text});
        try {
            ((ListView) mListView).setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initAdapterData() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mData.clear();
        for (int i = 0; i < mNameId.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ITEM_TEXT, mNameArray[i]);
            map.put(KEY, mNameId[i]);
            mData.add(map);
        }
    }

    @Override
    public void showMenu(View view) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        try {
            if (mPopupWindow != null) {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                } else {
                    mPopupWindow.getContentView().setEnabled(true);
                    int h = view.getHeight();
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
//                    int x = location[0];
                    int y = location[1];
//                    mTopMenuImg.setBackgroundResource(R.drawable.select_bk_press);
                    mPopupWindow.showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 0, h * 4 / 5 + y);
                    mViewFlipper.startFlipping();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
