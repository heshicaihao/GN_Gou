package com.gionee.client.view.shoppingmall;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.gionee.client.business.util.AndroidUtils;
import com.gionee.framework.event.IBusinessHandle;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.page.PageCacheManager;

public abstract class GNBaseView extends FrameLayout implements IBusinessHandle, OnClickListener {
    protected MyBean mSelfData;
    protected View mContentView;

    public GNBaseView(Context context) {
        super(context);
        init();
    }

    public GNBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public GNBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onClick(View v) {

    }

    protected abstract View setContent();

    protected abstract void requestData();

    protected abstract void initView();

    void init() {
        mSelfData = PageCacheManager.LookupPageData(getContext().getClass().getName());
        mContentView = setContent();
        addView(mContentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        initView();
        requestData();
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
//        AndroidUtils.showErrorInfo(getContext(), errorInfo);

    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {

    }

    @Override
    public Context getSelfContext() {
        return getContext();
    }

    @Override
    public void onCancel(String businessType, Object session) {

    }

}
