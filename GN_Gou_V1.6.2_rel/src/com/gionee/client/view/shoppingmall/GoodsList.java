// Gionee <yangxiong><2014-9-11> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-9-11 下午04:23:37
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
import com.gionee.client.view.adapter.GoodsListAdapter;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;

/**
 * com.gionee.client.view.shoppingmall.GoodsList
 * @author yangxiong <br/>
 * @date create at 2014-9-11 下午04:23:37
 * @description TODO 商品列表
 */
public class GoodsList extends AbstractBaseList {
    private static final String TAG = "GoodsList";
    
    public GoodsList(Context context) {
        super(context);
        init();
    }

    public GoodsList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GoodsList(Context context, AttributeSet attrs, int defStyle) {
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
        parameterBean.put(HttpConstants.Request.MyFavorites.TYPE, 2);
        return parameterBean;
    }

    @Override
    protected String getUrl() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return Url.GOODS_FAVORITE_URL;
    }

    @Override
    protected void showBootGuide() {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    protected AbstractListBaseAdapter getAdapter() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return new GoodsListAdapter(this, getContext());
    }

    @Override
    protected int getNoDataMessage() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return R.string.no_goods;
    }

    @Override
    protected boolean isShowActionBtn() {
        return false;
    }
    
    @Override
    protected int getActionBtnText() {
        return -1;
    }

    @Override
    protected String getDataTargetKey() {
        return HttpConstants.Data.GoodsList.GOODS_LIST_INFO_JO;
    }

    @Override
    protected void onClickWhenNoDataLayout() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        gotoHomeActivity(Constants.ActivityResultCode.RESULT_CODE_GOODS);
    }
}
