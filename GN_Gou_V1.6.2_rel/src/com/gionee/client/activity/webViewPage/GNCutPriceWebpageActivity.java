/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-2-10 下午03:33:13
 */
package com.gionee.client.activity.webViewPage;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;

/**
 * com.gionee.client.activity.webViewPage.GNCutPriceWebpageActivity
 * 
 * @author yangxiong <br/>
 * @date create at 2015-2-10 下午03:33:13
 * @description TODO 砍价详情页
 */
public class GNCutPriceWebpageActivity extends ThridPartyWebActivity {
    private final String TAG = "GNCutPriceWebpageActivity";

    @Override
    public void cumulateAppLinkScore() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        RequestAction action = new RequestAction();
        action.cumulateScore(this, Constants.ScoreTypeId.SHARE_CUT_PRICE);
    }
}
