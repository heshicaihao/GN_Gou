// Gionee <yangxiong><2014-9-10> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-9-10 下午04:07:16
 */
package com.gionee.client.activity.myfavorites;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.gionee.client.R;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.view.shoppingmall.AbstractBaseList;

/**
 * com.gionee.client.activity.myfavorites.StoryFragment
 * 
 * @author yangxiong <br/>
 * @date create at 2014-9-10 下午04:07:16
 * @description TODO 知物
 */
public class StoryFragment extends MyFavoritesBaseFragment implements OnClickListener {
    private static final String TAG = "StoryFragment";

    public AbstractBaseList getBaseList() {
        return mMyFavoritesList;
    }
    
    public void refresh() {
        mMyFavoritesList.pullDownToRefresh();
    }


    @Override
    public void onClick(View v) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        switch (v.getId()) {
            case R.id.action_btn:
                gotoHomeActivityWithCommentTab();
                break;
            default:
                break;
        }
    }

    protected void gotoHomeActivityWithCommentTab() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        getActivity().setResult(Activity.RESULT_OK);
        AndroidUtils.finishActivity(getActivity());
    }

    @Override
    protected int setContentViewId() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return R.layout.story_list;
    }
}
