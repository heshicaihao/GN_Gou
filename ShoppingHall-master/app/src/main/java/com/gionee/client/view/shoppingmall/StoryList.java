// Gionee <yangxiong><2014-8-8> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-8-8 下午05:10:54
 */
package com.gionee.client.view.shoppingmall;

import android.content.Context;
import android.util.AttributeSet;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.AbstractListBaseAdapter;
import com.gionee.client.view.adapter.CommentsAdapter;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.bean.MyBeanFactory;

/**
 * @author yangxiong <br/>
 * @description TODO 我的收藏之知物列表
 */
public class StoryList extends AbstractMyFavoriteBaseList {
    private static final String TAG = "StoryList";

    public StoryList(Context context) {
        super(context);
        init();
    }

    public StoryList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StoryList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mCommentListView.setOnScrollListener(null);
    }

    @Override
    protected MyBean getOtherParametersBean() {
        MyBean parameterBean = MyBeanFactory.createDataBean();
        parameterBean.put(HttpConstants.Request.MyFavorites.TYPE, 1);
        return parameterBean;
    }

    @Override
    protected String getUrl() {
        return Url.COMMENTS_MY_FAVORITE_URL;
    }

    @Override
    protected AbstractListBaseAdapter getAdapter() {
        return new CommentsAdapter(this, getContext(), getUrl());
    }

    @Override
    protected void showBootGuide() {
        // If the boot for the first time, Showing boot guide animation.
//        if (CommonUtils.isNeedShowBootGuide(getSelfContext(), this.getClass().getName())) {
//            CommonUtils.showMyfavoriteLongClickDialog((Activity) mContext);
//        }
    }

    @Override
    protected int getNoDataMessage() {
        return R.string.comments_advertisement;
    }

    @Override
    protected boolean isShowActionBtn() {
        return false;
    }

    @Override
    protected int getActionBtnText() {
        return R.string.have_a_look;
    }

    @Override
    protected String getDataTargetKey() {
        return HttpConstants.Data.CommentsList.COMMENTS_LIST_INFO_JO;
    }

    @Override
    protected void onClickWhenNoDataLayout() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        gotoHomeActivity(Constants.ActivityResultCode.RESULT_CODE_STORY);
    }
}
