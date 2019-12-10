package com.gionee.client.view.shoppingmall;

import android.content.Context;
import android.util.AttributeSet;

import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.myfavorites.MyFavoritesActivity;

public abstract class AbstractMyFavoriteBaseList extends AbstractBaseList {

    public AbstractMyFavoriteBaseList(Context context) {
        super(context);
    }

    public AbstractMyFavoriteBaseList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AbstractMyFavoriteBaseList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void showNoDataLayout() {
        super.showNoDataLayout();
        ((MyFavoritesActivity) getContext()).switchToCommonMode();
        int index =  ((MyFavoritesActivity) getContext()).getCurrentFragmentIndex();
        if (index == 0 && this instanceof ShoppingList
                || index == 1 && this instanceof StoryList) {            
            ((BaseFragmentActivity) getContext()).getTitleBar().setRightBtnVisible(false);
        }
    }

    @Override
    public void hideNoDataLayout() {
        super.hideNoDataLayout();
        int index =  ((MyFavoritesActivity) getContext()).getCurrentFragmentIndex();
        if (index == 0 && this instanceof ShoppingList
                || index == 1 && this instanceof StoryList) {            
            ((BaseFragmentActivity) getContext()).getTitleBar().setRightBtnVisible(true);
        }
    }
}