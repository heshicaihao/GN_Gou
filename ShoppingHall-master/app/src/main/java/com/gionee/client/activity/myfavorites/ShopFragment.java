/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-9-15 下午04:42:26
 */
package com.gionee.client.activity.myfavorites;

import com.gionee.client.R;
import com.gionee.client.business.util.LogUtils;

/**
 * com.gionee.client.activity.myfavorites.shopFragment
 * 
 * @author yangxiong <br/>
 * @date create at 2014-9-15 下午04:42:26
 * @description TODO 店铺
 */
public class ShopFragment extends MyFavoritesBaseFragment {
    private static final String TAG = "ShopFragment";

    @Override
    protected int setContentViewId() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        return R.layout.shop_list;
    }
}
