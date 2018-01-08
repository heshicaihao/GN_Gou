/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-9-28 下午03:54:42
 */
package com.gionee.client.activity.myfavorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragment;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.view.adapter.AbstractMyfavoriteBaseAdapter;
import com.gionee.client.view.shoppingmall.AbstractBaseList;

/**
 * com.gionee.client.activity.myfavorites.MyFavoritesBaseFragment
 * 
 * @author yangxiong <br/>
 * @date create at 2014-9-28 下午03:54:42
 * @description TODO
 */
public abstract class MyFavoritesBaseFragment extends BaseFragment {
    private static final String TAG = "MyFavoritesBaseFragment";
    protected AbstractBaseList mMyFavoritesList;

    public AbstractBaseList getMyFavoritesList() {
        return mMyFavoritesList;
    }

    @Override
    public void onPageVisible() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onPageVisible();
//        if (mMyFavoritesList != null) {
//            mMyFavoritesList.showNodataInfoIfNeed();
//        }
    }

    @Override
    public void onPageInvisible() {
        super.onPageInvisible();
        if (mMyFavoritesList != null) {
            AbstractMyfavoriteBaseAdapter adapter = (AbstractMyfavoriteBaseAdapter) mMyFavoritesList
                    .getListBaseAdapter();
            adapter.hideDropDownLayout();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (mMyFavoritesList != null) {
//            mMyFavoritesList.showNodataInfoIfNeed();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initview(view);
        return view;
    }

    @Override
    public View getCustomToastParentView() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return mMyFavoritesList;
    }

    private void initview(View layout) {
        mMyFavoritesList = (AbstractBaseList) layout.findViewById(R.id.my_favorites_list_view);
        mMyFavoritesList.setFragmentContainer(this);
    }
    
    
    public interface OnEditModeListener {
        void onEditMode();
    }
}
