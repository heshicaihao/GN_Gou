/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-3-30 上午11:40:46
 */
package com.gionee.client.view.shoppingmall;

import android.content.Context;
import android.util.AttributeSet;

import com.gionee.client.R;
import com.gionee.client.business.statistic.header.PublicHeaderParamsManager;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.AbstractListBaseAdapter;
import com.gionee.client.view.adapter.QuestionListAdapter;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;

/**
 * com.gionee.client.view.shoppingmall.QuestionList
 * 
 * @author yangxiong <br/>
 * @date create at 2015-3-30 上午11:40:46
 * @description TODO 问题列表
 */
public class QuestionList extends AbstractBaseList {
    private static final String TAG = "QuestionList";

    public QuestionList(Context context) {
        super(context);
        init();
    }

    public QuestionList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected MyBean getOtherParametersBean() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        MyBean parameterBean = MyBeanFactory.createDataBean();
        return parameterBean;
    }

    @Override
    protected String getUrl() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return Url.QUESTION_LIST_URL;
    }

    @Override
    protected void showBootGuide() {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    protected AbstractListBaseAdapter getAdapter() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return new QuestionListAdapter(getContext(), R.layout.question_list_item);
    }

    @Override
    protected int getNoDataMessage() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return R.string.no_question;
    }

    @Override
    protected boolean isShowActionBtn() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return false;
    }

    @Override
    protected void onClickWhenNoDataLayout() {
        LogUtils.log(TAG, LogUtils.getThreadName());
//        gotoHomeActivityWithCommentTab();
        pullDownToRefresh();
    }

    @Override
    protected int getActionBtnText() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return 0;
    }

    @Override
    protected String getDataTargetKey() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return HttpConstants.Data.QuestionList.QUESTION_LIST_INFO;
    }

    private void init() {
        mCommentListView.setOnScrollListener(null);
//        mCommentListView.getRefreshableView().setDivider(new ColorDrawable(0xffdfdfdf));
        mCommentListView.getRefreshableView().setDividerHeight(0);
    }
}
