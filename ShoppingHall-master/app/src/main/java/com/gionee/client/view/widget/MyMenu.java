// Gionee <yangxiong><2014-1-3> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-1-3 上午10:56:12
 */
package com.gionee.client.view.widget;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.ViewFlipper;

import com.gionee.client.R;

/**
 * com.gionee.client.view.widget.categoryMymenu
 * @author yangxiong <br/>
 * @date create at 2014-1-3 上午10:56:12
 * @description TODO
 */
public abstract class MyMenu extends BasicMyMenu {

    public MyMenu(Activity activity, String[] nameArray, int[] nameId, int[] imageArr, int count) {
        super(activity, nameArray, nameId, imageArr, count, R.layout.menu_view);
    }
    
    
    /**
     * @param mGridView
     * @description
     * @author yuwei
     */
    protected void setMenuAdapter(AbsListView mGridView) {
        BaseAdapter adapter = new SimpleAdapter(mActivity, mData, R.layout.menu_item, new String[] {
                ITEM_IAMGE, ITEM_TEXT}, new int[] {R.id.menu_item_image, R.id.menu_item_text});
        try {
            ((GridView)mGridView).setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @description init menu
     * @author yuwei
     */
    protected void initMenu(int menuViewLayout) {
        mViewFlipper = new ViewFlipper(mActivity);
        LinearLayout mLinearLayout = (LinearLayout) mActivity.getLayoutInflater().inflate(menuViewLayout,
                null);
        mViewFlipper.addView(mLinearLayout);
        mViewFlipper.setFlipInterval(1000000);
        GridView mGridView = (GridView) mLinearLayout.findViewById(R.id.menu_gridview);
        mGridView.setNumColumns(mCount);
        mPopupWindow = new PopupWindow(mViewFlipper, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.update();
        initAdapterData();
        setMenuAdapter(mGridView);
        mGridView.setOnKeyListener(this);
        mGridView.setOnItemClickListener(this);
    }
    
    /**
     * 
     * @description init the adapter data
     * @author yuwei
     */
    protected void initAdapterData() {
        for (int i = 0; i < mNameId.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ITEM_IAMGE, mImageArr[i]);
            map.put(ITEM_TEXT, mNameArray[i]);
            map.put(KEY, mNameId[i]);
            mData.add(map);
        }
    }
    
    /**
     * @param view
     * @description show the menu
     * @author yuwei
     */
    public void showMenu(View view) {

        try {
            if (mPopupWindow != null) {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                } else {
                    mPopupWindow.getContentView().setEnabled(true);
                    mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                    mViewFlipper.startFlipping();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }    
}
