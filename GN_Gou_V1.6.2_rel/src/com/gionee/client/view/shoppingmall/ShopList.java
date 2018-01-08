// Gionee <yangxiong><2014-9-11> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-9-11 下午04:18:30
 */
package com.gionee.client.view.shoppingmall;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;

import com.gionee.client.R;
import com.gionee.client.business.statistic.header.PublicHeaderParamsManager;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.AbstractListBaseAdapter;
import com.gionee.client.view.adapter.ShopListAdapter;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;

/**
 * com.gionee.client.view.shoppingmall.ShopList
 * 
 * @author yangxiong <br/>
 * @date create at 2014-9-11 下午04:18:30
 * @description TODO 店铺列表
 */
public class ShopList extends AbstractBaseList {
    private static final String TAG = "ShopList";

    public ShopList(Context context) {
        super(context);
        init();
    }

    public ShopList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShopList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mCommentListView.setOnScrollListener(null);
        mCommentListView.getRefreshableView().setDivider(new ColorDrawable(0xffdfdfdf));
        mCommentListView.getRefreshableView().setDividerHeight(1);
    }

    @Override
    protected MyBean getOtherParametersBean() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        MyBean parameterBean = MyBeanFactory.createDataBean();
        String uid = PublicHeaderParamsManager.getUid(getSelfContext());
        parameterBean.put(HttpConstants.Request.UID_S, uid);
        parameterBean.put(HttpConstants.Request.MyFavorites.TYPE, 1);
        return parameterBean;
    }

    @Override
    protected String getUrl() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return Url.SHOP_FAVORITE_URL;
    }

    @Override
    protected void showBootGuide() {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    protected AbstractListBaseAdapter getAdapter() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return new ShopListAdapter(this, getContext());
    }

    @Override
    protected int getNoDataMessage() {
        return R.string.no_shop;
    }

    @Override
    protected boolean isShowActionBtn() {
        return false;
    }

    @Override
    protected int getActionBtnText() {
        return 0;
    }

    @Override
    protected String getDataTargetKey() {
        return HttpConstants.Data.ShopList.SHOP_LIST_INFO_JO;
    }

    @Override
    protected void onClickWhenNoDataLayout() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        gotoHomeActivity(Constants.ActivityResultCode.RESULT_CODE_SHOP);
    }
}
