// Gionee <yuwei><2013-7-15> add for CR00836967 begin
package com.gionee.client.view.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.ViewFlipper;

/**
 * com.gionee.client.widget.MyMenu
 * 
 * @author yuwei <br/>
 * @data create at 2013-7-31 下午12:04:04
 * @desciption the custom menu
 */
public abstract class BasicMyMenu implements OnKeyListener, OnItemClickListener {
    protected static final String ITEM_IAMGE = "itemIamge";
    protected static final String ITEM_TEXT = "itemText";
    protected static final String KEY = "key";
    protected Activity mActivity;
    protected int[] mNameId;
    protected int[] mImageArr;
    protected String[] mNameArray;
    protected int mCount;
    protected PopupWindow mPopupWindow;
    protected ViewFlipper mViewFlipper;
    protected List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

    /**
     * @param activity
     * @param nameId
     *            the name of the menu, it my be a tring array of each item
     * @param imageArr
     *            the image id of the menu item
     * @param count
     */
    @SuppressLint("NewApi")
    public BasicMyMenu(Activity activity, String[] nameArray, int[] nameId, int[] imageArr, int count,
            int menuViewLayout) {
        this.mActivity = activity;
        this.mNameArray = nameArray != null ? Arrays.copyOf(nameArray, nameArray.length) : null;
        this.mNameId = nameId != null ? Arrays.copyOf(nameId, nameId.length) : null;
        this.mImageArr = imageArr != null ? Arrays.copyOf(imageArr, imageArr.length) : null;
        this.mCount = count;
        initMenu(menuViewLayout);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        int key = Integer.parseInt(mData.get(arg2).get("key").toString());
        onItemClick(key);
    }

    /**
     * @description dismiss the menu
     * @author yuwei
     */
    public void menuDismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * @description init menu
     * @author yuwei
     */
    protected abstract void initMenu(int menuViewLayout);

    /**
     * @param mGridView
     * @description
     * @author yuwei
     */
    protected abstract void setMenuAdapter(AbsListView mGridView);

    /**
     * @description init the adapter data
     * @author yuwei
     */
    protected abstract void initAdapterData();

    /**
     * @param view
     * @description show the menu
     * @author yuwei
     */
    public abstract void showMenu(View view);

    public abstract void onItemClick(int viewId);

}
//Gionee <yuwei><2013-7-15> add for CR00836967 end
