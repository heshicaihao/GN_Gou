/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-9-11 下午04:33:15
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
import com.gionee.client.view.adapter.WebPageListAdapter;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;

/**
 * com.gionee.client.view.shoppingmall.WebPageList
 * 
 * @author yangxiong <br/>
 * @date create at 2014-9-11 下午04:33:15
 * @description TODO 网页列表
 */
public class WebPageList extends AbstractBaseList {
    private static final String TAG = "WebPageList";

    public WebPageList(Context context) {
        super(context);
        init();
    }

    public WebPageList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebPageList(Context context, AttributeSet attrs, int defStyle) {
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
        parameterBean.put(HttpConstants.Request.MyFavorites.TYPE, 4);
        return parameterBean;
    }

    @Override
    protected String getUrl() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return Url.WEB_PAGE_FAVORITE_URL;
    }

    @Override
    protected void showBootGuide() {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    protected AbstractListBaseAdapter getAdapter() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return new WebPageListAdapter(this, getContext());
    }

    @Override
    protected int getNoDataMessage() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return R.string.no_webpage;
    }

    @Override
    protected boolean isShowActionBtn() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return false;
    }

    @Override
    protected int getActionBtnText() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return 0;
    }

    @Override
    protected String getDataTargetKey() {
        return HttpConstants.Data.WebPageList.WEBPAGE_LIST_INFO_JO;
    }

    @Override
    protected void onClickWhenNoDataLayout() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        gotoHomeActivity(Constants.ActivityResultCode.RESULT_CODE_WEBPAGE);
    }
}
