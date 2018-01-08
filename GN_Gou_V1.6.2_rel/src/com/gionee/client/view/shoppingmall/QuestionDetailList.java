/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-4-1 下午03:50:36
 */
package com.gionee.client.view.shoppingmall;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.AbstractListBaseAdapter;
import com.gionee.client.view.adapter.QuestionDetailListAdapter;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;

/**
 * com.gionee.client.view.shoppingmall.QuestionDetailList
 * 
 * @author yangxiong <br/>
 * @date create at 2015-4-1 下午03:50:36
 * @description TODO 问题详情列表
 */
public class QuestionDetailList extends AbstractBaseList {
    private static final String TAG = "QuestionDetailList";
    private String mQuestionId;

    public QuestionDetailList(Context context) {
        super(context);
        init();
    }

    public QuestionDetailList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionDetailList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setQuestionId(String questionId) {
        this.mQuestionId = questionId;
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onSucceed(businessType, isCache, session);
        if (mCurpage == 1) {
            ((QuestionDetailListAdapter) mCommentAdaper).clearPraisedData();
        }
    }

    @Override
    protected MyBean getOtherParametersBean() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        MyBean parameterBean = MyBeanFactory.createDataBean();
        parameterBean.put(HttpConstants.Request.ID, mQuestionId);
        return parameterBean;
    }

    @Override
    protected String getUrl() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return Url.QUESTION_DETAIL_URL;
    }

    @Override
    protected void showBootGuide() {
        LogUtils.log(TAG, LogUtils.getThreadName());

    }

    @Override
    protected AbstractListBaseAdapter getAdapter() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return new QuestionDetailListAdapter(getSelfContext(), R.layout.question_detail_list_item);
    }

    @Override
    protected int getNoDataMessage() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return R.string.question_detail_fail_note;
    }

    @Override
    protected boolean isShowActionBtn() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return false;
    }

    @Override
    protected void onClickWhenNoDataLayout() {
        LogUtils.log(TAG, LogUtils.getThreadName());
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
        return HttpConstants.Data.QuestionDetailList.QUESTION_DETAIL_LIST_INFO;
    }

    private void init() {
        mCommentListView.setOnScrollListener(null);
        mCommentListView.getRefreshableView().setDivider(new ColorDrawable(0xffdfdfdf));
        mCommentListView.getRefreshableView().setDividerHeight(1);
    }
}
