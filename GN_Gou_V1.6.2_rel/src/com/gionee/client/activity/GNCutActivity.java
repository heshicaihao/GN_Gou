package com.gionee.client.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.webViewPage.GNCutPriceWebpageActivity;
import com.gionee.client.activity.webViewPage.ThridPartyWebActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.manage.ConfigManager;
import com.gionee.client.business.shareTool.ShareTool;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.Constants.PAGE_TAG;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.CutListAdapter;
import com.gionee.client.view.widget.GNCustomDialog;
import com.gionee.client.view.widget.PullToRefreshListView;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.utills.BitmapUtills;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class GNCutActivity extends BaseFragmentActivity implements OnClickListener {
    private static final String TAG = "Cut_Page";
    private RequestAction mAction;
    private PullToRefreshListView mListView;
    private CutListAdapter mAdapter;
    private int mCurpage = 1;
    private boolean mIsHasNextPage = false;
    private JSONArray mJsonArray;
    private View mNoListDataView;
    private ProgressBar mProgressBar;
    private View mGuideView;
    private int mRequstID = -1;
    private int mLastStatus;
    private TextView mCutPriceCumulateNotifyTv;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_cut_page);
        initView();
        initData();
        setTopPadding();
    }

    @Override
    protected void onDestroy() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }

    /**
     * 
     * @author yuwei
     * @description TODO
     */
    private void setTopPadding() {
        if (AndroidUtils.translateTopBar(this)) {
            RelativeLayout topTitleBar = (RelativeLayout) findViewById(R.id.cut_title);
            LayoutParams params = (LayoutParams) topTitleBar.getLayoutParams();
            params.topMargin = AndroidUtils.dip2px(this, 15);
            topTitleBar.setLayoutParams(params);
        }
    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.cut_list);
        mProgressBar = (ProgressBar) findViewById(R.id.cut_loading_bar);
        mAdapter = new CutListAdapter(this, new CutButtonClickListener());
        mListView.setAdapter(mAdapter);
        mListView.setMode(Mode.BOTH);
        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDownToRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullUpToRefresh();
            }

        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    gotoCutDetailWebPage(
                            mJsonArray.optJSONObject(position - 1).optString(
                                    HttpConstants.Data.CutList.SHARE_URL_S), true);
                    mRequstID = mJsonArray.optJSONObject(position - 1).optInt(
                            (HttpConstants.Data.CutList.ID_I));
                    StatService.onEvent(GNCutActivity.this, "b_goods", "" + mRequstID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mDialog = (GNCustomDialog) DialogFactory.createShareDialog(GNCutActivity.this);
        mCutPriceCumulateNotifyTv = (TextView) findViewById(R.id.cut_price_cumulate);
        initCutPriceNotifycation();
    }

    private void initCutPriceNotifycation() {
        String msg = getResources().getString(R.string.cut_price_cumulate_score);
        String colorText = getResources().getString(R.string.cut_price_cumulate_score_value);
        SpannableStringBuilder style = new SpannableStringBuilder(msg);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tab_text_color_sel)),
                msg.indexOf(colorText), msg.indexOf(colorText) + colorText.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mCutPriceCumulateNotifyTv.setText(style);
    }

    private void initNoDataView() {
        ViewStub viewStub = (ViewStub) findViewById(R.id.no_cut_data);
        mNoListDataView = viewStub.inflate();
        RelativeLayout noDataLayout = (RelativeLayout) mNoListDataView.findViewById(R.id.above_layout);
        TextView mMessageTv = (TextView) noDataLayout.findViewById(R.id.message);
        mMessageTv.setText(getString(R.string.no_data));
        mNoListDataView.setVisibility(View.VISIBLE);
    }

    private void cutPrice(int id) {
        mAction.cutPrice(this, id, HttpConstants.Data.CutData.CUT_DATA_S);
    }

    private void pullUpToRefresh() {
        if (mIsHasNextPage) {
            requestData(mCurpage + 1);
        } else {
            resetPullRefreshUi();
            if (!mProgressBar.isShown()) {
                Toast.makeText(this, R.string.no_more_msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetPullRefreshUi() {
        mListView.postDelayed(new Runnable() {

            @Override
            public void run() {
                mListView.onRefreshComplete();
                mListView.setMode(Mode.PULL_FROM_START);
            }
        }, 1000);
    }

    private void pullDownToRefresh() {
        mCurpage = 1;
        requestData(mCurpage);
    }

    private void requestData(int curpage) {
        mAction.getCutList(GNCutActivity.this, curpage, HttpConstants.Data.CutList.LIST_INFO_JO);
    }

    private void initData() {
        mAction = new RequestAction();
        requestData(mCurpage);
        ShareSDK.initSDK(this);
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        super.onSucceed(businessType, isCache, session);
        if (businessType.equals(Url.CUT_LIST_URL)) {
            loadComplete();
            updateView();
            updateListView();
        }
        if (businessType.equals(Url.CUT_URL)) {
            JSONObject object = mSelfData.getJSONObject(HttpConstants.Data.CutData.CUT_DATA_S);
            LogUtils.log(TAG, LogUtils.getThreadName() + "object=" + object.toString());
            mAdapter.addCutData(object);
            showCutToast(object);
        }
        if (businessType.equals(Url.GET_CUT_GOOD_STATUS)) {
            JSONObject object = mSelfData.getJSONObject(HttpConstants.Data.GoodStatus.DATA_INFO_S);
            LogUtils.log(TAG, LogUtils.getThreadName() + "object=" + object.toString());
            LogUtils.log("CutListAdapter", object.toString());
            mAdapter.addCutData(object);
        }
    }

    /**
     * @param object
     * @author yuwei
     * @description TODO
     */
    private void showCutToast(JSONObject object) {
        int mCurrentCutCode = object.optInt(HttpConstants.Data.CutList.CUT_CODE_I);
        if ((mLastStatus == Constants.CutPage.CUT_STATE_CUT && mCurrentCutCode == Constants.CutPage.CUT_STATE_AFTER)
                || mLastStatus != Constants.CutPage.CUT_STATE_CUT) {
            Toast.makeText(this, object.optString(HttpConstants.Data.CutList.TIPS_S), Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this,
                    getString(R.string.cut_off_money, object.optString(HttpConstants.Data.CutData.RANGE_S)),
                    Toast.LENGTH_SHORT).show();
            boolean isCore = object.optBoolean(HttpConstants.Data.CutData.IS_SCORE);
            if (isCore) {
                startCumulateAimation(mCutPriceCumulateNotifyTv);
            }
        }
    }

    private void updateListView() {
        String label = AndroidUtils.getCurrentTimeStr(getSelfContext());
        mListView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
    }

    private void loadComplete() {
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.onRefreshComplete();
                int visibileItemCount = mListView.getRefreshableView().getLastVisiblePosition()
                        - mListView.getRefreshableView().getFirstVisiblePosition();
                int itemSum = mListView.getRefreshableView().getCount() - 2;
                if (itemSum > visibileItemCount) {
                    mListView.setMode(Mode.BOTH);
                } else {
                    mListView.setMode(Mode.PULL_FROM_START);
                }
            }
        }, 1000);
    }

    private void updateView() {
        mProgressBar.setVisibility(View.GONE);
        JSONObject jsonObject = mSelfData.getJSONObject(HttpConstants.Data.CutList.LIST_INFO_JO);

        try {
            if (jsonObject == null || jsonObject.length() < 1) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mJsonArray = jsonObject.optJSONArray(HttpConstants.Data.CutList.LIST_S);
        mIsHasNextPage = jsonObject.optBoolean(HttpConstants.Data.CutList.HAS_NEXT_B);
        mCurpage = jsonObject.optInt(HttpConstants.Data.CutList.CURPAGE_I);
        mAdapter.setData(mJsonArray);
        if (mCurpage == 1) {
            mAdapter.initCutListData();
        }
        if (isShowNoDataView()) {
            initNoDataView();
            return;
        }
        hideNoDataView();

    }

    private void hideNoDataView() {
        if (mNoListDataView != null) {
            mNoListDataView.setVisibility(View.GONE);
        }
    }

    private boolean isShowNoDataView() {
        return mAdapter.getCount() == 0 && mNoListDataView == null;
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        if (businessType.equals(Url.CUT_URL)) {
            if (!TextUtils.isEmpty(errorOn) && errorOn.equals("2")) {
                return;
            }
            JSONObject object = mSelfData.getJSONObject(HttpConstants.Data.CutData.CUT_DATA_S);
            mAdapter.addCutData(object);
        }
        if (businessType.equals(Url.CUT_LIST_URL)) {
            loadComplete();
            mProgressBar.setVisibility(View.GONE);
            LogUtils.log(TAG, "" + isShowNoDataView());
            if (isShowNoDataView()) {
                initNoDataView();
            }
        }
    }

    @Override
    public void onClick(View v) {
        MyBean bean = (MyBean) v.getTag();
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                AndroidUtils.exitActvityAnim(this);
                break;
            case R.id.cut_rule:
                gotoWebUrlPage(ConfigManager.getInstance().getCutRuleUrl(this), false);
                StatService.onEvent(this, "cut_rule", "cut_rule");
                break;
            case R.id.cut_orders:
                gotoWebUrlPage(ConfigManager.getInstance().getOrderListUrl(this), true);
                StatService.onEvent(this, "order", "cut");
                break;
            case R.id.share_weixin:
                shareToWeixinSquare(bean);
                break;
            case R.id.share_friends:
                shareToWeixinFriends(bean);
                break;
            case R.id.share_weibo:
                shareToWeibo(bean.getString(Constants.CutPage.TITLE), getShareContent(bean),
                        (Bitmap) bean.get(Constants.CutPage.BITMAP), bean.getString(Constants.CutPage.URL));
                StatService.onEvent(GNCutActivity.this, "b_share", "weibo");
                closeShareDialog();
                if (isWeiboValid()) {
                    cumulateShareCutPriceScore("SinaWeibo");
                }
                break;
            case R.id.share_qq_friend:
                shareToQq(ShareTool.PLATFORM_QQ_FRIEND, bean.getString(Constants.CutPage.TITLE),
                        getShareContent(bean), bean.getString(Constants.CutPage.IMAGE_URL),
                        bean.getString(Constants.CutPage.URL));
                closeShareDialog();
                if (ShareTool.isQQValid(this)) {
                    cumulateShareCutPriceScore("QQ");
                }
                StatService.onEvent(GNCutActivity.this, "b_share", BaiduStatConstants.QQ);
                break;
            case R.id.share_qq_zone:
                shareToQq(ShareTool.PLATFORM_QQ_ZONE, bean.getString(Constants.CutPage.TITLE),
                        getShareContent(bean), bean.getString(bean.getString(Constants.CutPage.IMAGE_URL)),
                        bean.getString(Constants.CutPage.URL));
                closeShareDialog();
                if (ShareTool.isQQValid(this)) {
                    cumulateShareCutPriceScore("QZone");
                }
                StatService.onEvent(GNCutActivity.this, "b_share", BaiduStatConstants.ZONE);
                break;
//            case R.id.cut_guide_check:
//            case R.id.cute_guide_rule:
//                gotoWebUrlPage(ConfigManager.getInstance().getCutRuleUrl(this), false);
//                StatService.onEvent(this, "cut_rule", "cut_rule");
//                hideGuideView();
//                break;
//            case R.id.cut_guide_know:
//                hideGuideView();
//                break;
            default:
                break;
        }
    }

    private void shareToWeixinSquare(MyBean bean) {
        try {
            shareToWeixin(false, bean.getString(Constants.CutPage.TITLE), getShareContent(bean),
                    (Bitmap) bean.get(Constants.CutPage.BITMAP), bean.getString(Constants.CutPage.URL));
            StatService.onEvent(GNCutActivity.this, "b_share", "weixin");
            closeShareDialog();
            if (ShareTool.isWXInstalled(this)) {
                cumulateShareCutPriceScore("Wechat");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareToWeixinFriends(MyBean bean) {
        try {
            shareToWeixin(true, bean.getString(Constants.CutPage.TITLE), getShareContent(bean),
                    (Bitmap) bean.get(Constants.CutPage.BITMAP), bean.getString(Constants.CutPage.URL));
            StatService.onEvent(GNCutActivity.this, "b_share", "friends");
            closeShareDialog();
            if (ShareTool.isWXInstalled(this)) {
                cumulateShareCutPriceScore("WechatMoments");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getShareContent(MyBean bean) {
        return getString(R.string.cut_share_content, bean.getString(Constants.CutPage.PRICE),
                bean.getString(Constants.CutPage.CURRENT_PRICE));
    }

    private void hideGuideView() {
        if (mGuideView != null) {
            mGuideView.setVisibility(View.GONE);
        }
    }

    private void gotoWebUrlPage(String url, boolean showFootBar) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent webIntent = new Intent(this, ThridPartyWebActivity.class);
        webIntent.putExtra(StatisticsConstants.PAGE_TAG, PAGE_TAG.CUT);
        webIntent.putExtra(StatisticsConstants.KEY_INTENT_URL, url);
//        webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        webIntent.putExtra(Constants.IS_SHOW_WEB_FOOTBAR, showFootBar);
        startActivity(webIntent);
        AndroidUtils.webActivityEnterAnim(this);
    }

    private void gotoCutDetailWebPage(String url, boolean showFootBar) {
        Intent webIntent = new Intent(this, GNCutPriceWebpageActivity.class);
        webIntent.putExtra(StatisticsConstants.PAGE_TAG, PAGE_TAG.CUT);
        webIntent.putExtra(StatisticsConstants.KEY_INTENT_URL, url);
        webIntent.putExtra(Constants.IS_SHOW_WEB_FOOTBAR, showFootBar);
        startActivity(webIntent);
        AndroidUtils.webActivityEnterAnim(this);
    }

    class CutButtonClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            MyBean bean = (MyBean) v.getTag();
            mLastStatus = bean.getInt(Constants.CutPage.CUT_DODE);
            switch (mLastStatus) {
                case Constants.CutPage.CUT_STATE_CUT:
                    cutPrice(bean.getInt(Constants.CutPage.ID));
                    StatService.onEvent(GNCutActivity.this, "bargain",
                            ("" + bean.getInt(Constants.CutPage.ID)));
                    break;
                case Constants.CutPage.CUT_STATE_REDAY:
                    showTips(bean);
                    break;
                case Constants.CutPage.CUT_STATE_END:
                    showTips(bean);
                    break;
                case Constants.CutPage.CUT_STATE_HELP:
                    showShareDialog(v);
                    StatService.onEvent(GNCutActivity.this, "b_share", "share");
                    break;
                case Constants.CutPage.FLOOR_PRICE:
                    Toast.makeText(GNCutActivity.this, R.string.floor_price, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.CutPage.CUT_STATE_OFF:
                    showTips(bean);
                    break;
                case Constants.CutPage.CUT_STATE_AFTER:
                    showTips(bean);
                    break;
                default:
                    break;
            }
        }

        /**
         * @param bean
         * @author yuwei
         * @description TODO
         */
        private void showTips(MyBean bean) {
            try {
                Toast.makeText(GNCutActivity.this, bean.getString(HttpConstants.Data.CutList.TIPS_S),
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void showShareDialog(final View v) {
        super.showShareDialog(v, this);
        if (mDialog != null) {
            mDialog.setTitle(R.string.cut_share_titile);
            mDialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    LogUtils.log(TAG, LogUtils.getThreadName());
                    MyBean bean = (MyBean) v.getTag();
                    Bitmap bitmap = (Bitmap) bean.get(Constants.CutPage.BITMAP);
                    BitmapUtills.bitmapRecycle(bitmap);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        AndroidUtils.exitActvityAnim(this);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.log(TAG, LogUtils.getFunctionName() + mRequstID);
        if (mRequstID > 0) {
            mAction.getCutGoodInfo(this, mRequstID, HttpConstants.Data.GoodStatus.DATA_INFO_S);
            mRequstID = -1;
        }
        closeProgressDialog();
    }

    private void cumulateShareCutPriceScore(String shareChannel) {
        RequestAction action = new RequestAction();
        action.cumulateScore(this, Constants.ScoreTypeId.SHARE_CUT_PRICE, shareChannel);
    }
}
