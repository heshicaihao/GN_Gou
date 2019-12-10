// Gionee <yangxiong><2014-8-8> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-8-8 下午04:18:12
 */
package com.gionee.client.view.shoppingmall;

import android.content.Context;
import android.util.AttributeSet;

import com.gionee.client.R;
import com.gionee.client.business.manage.ConfigManager;
import com.gionee.client.business.statistic.header.PublicHeaderParamsManager;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.AbstractListBaseAdapter;
import com.gionee.client.view.adapter.CommentsAdapter;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;

/**
 * @author yangxiong <br/>
 * @description TODO 物语列表视图组件
 */
public class CommentList extends AbstractBaseList {

    public CommentList(Context context) {
        super(context);
        init();
    }

    public CommentList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mCommentListView.setOnScrollListener(null);
    }

    @Override
    protected MyBean getOtherParametersBean() {
        MyBean parameterBean = MyBeanFactory.createDataBean();
        return parameterBean;
    }

    @Override
    protected String getUrl() {
        boolean isQuestionImportOpen = ConfigManager.isQuestionImportOpen(getSelfContext());
        return isQuestionImportOpen ? Url.COMMENTS_LIST_INCLUDE_QUESTION_URL : Url.COMMENTS_LIST_URL;
    }

    @Override
    protected AbstractListBaseAdapter getAdapter() {
        return new CommentsAdapter(this, getContext(), getUrl());
    }

    @Override
    protected void showBootGuide() {
    }

    @Override
    protected int getNoDataMessage() {
        return R.string.no_comment;
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
        return HttpConstants.Data.CommentsList.COMMENTS_LIST_INFO_JO;
    }

    @Override
    protected void onClickWhenNoDataLayout() {

    }
}
