/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2014-12-10 上午10:46:39
 */
package com.gionee.client.activity.contrast;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.AbstractListViewActivity;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.persistent.ShareKeys;
import com.gionee.client.business.shareTool.ShareTool;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.view.adapter.GoodsContrastAdapter;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.shoppingmall.GNTitleBar.OnRightBtnListener;
import com.gionee.client.view.widget.PullToRefreshListView;
import com.gionee.framework.model.bean.MyBean;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * @author yangxiong <br/>
 * @date create at 2014-12-10 上午10:46:39
 * @description TODO 商品对比
 */
public class GNGoodsContrastActivity extends AbstractListViewActivity {
    private static final String TAG = "GNGoodsContrastActivity";
    private JSONArray mContrastArray;

    public GNTitleBar getTitleBar() {
        return super.getTitleBar();
    }

    @Override
    protected void initNoDataLayoutViews() {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " custruct mNoDataLayout");
        ViewStub stub = (ViewStub) findViewById(R.id.no_data_layout);
        if (stub == null) {
            LogUtils.logd(TAG, LogUtils.getThreadName() + " stub = null");
            return;
        }
        mNoDataLayout = stub.inflate();
        ImageView amigoIcon = (ImageView) mNoDataLayout.findViewById(R.id.amigo_icon);
        amigoIcon.setVisibility(View.GONE);
        mNoDataLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (isFastDoubleClick()) {
                    return;
                }
                finish();
                AndroidUtils.exitActvityAnim(GNGoodsContrastActivity.this);
            }
        });
        TextView mMessageTv = (TextView) mNoDataLayout.findViewById(R.id.message);
        mMessageTv.setText(R.string.goods_contrast_no_data_info);
        mNoDataLayout.findViewById(R.id.add_compare_guide).setVisibility(View.VISIBLE);
        mNoDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onClick(v);

        MyBean bean = (MyBean) v.getTag();
        switch (v.getId()) {
            case R.id.share_friends:
                shareToWeixin(true, bean.getString("title"), bean.getString("description"),
                        (Bitmap) bean.get("thump"), bean.getString("url"));
                closeShareDialog();
                StatService.onEvent(GNGoodsContrastActivity.this, BaiduStatConstants.CONTRAST,
                        BaiduStatConstants.FRIENDS);
                if (ShareTool.isWXInstalled(this)) {
                    cumulateAppLinkScore();
                }
                break;
            case R.id.share_weixin:
                shareToWeixin(false, bean.getString("title"), bean.getString("description"),
                        (Bitmap) bean.get("thump"), bean.getString("url"));
                closeShareDialog();
                StatService.onEvent(GNGoodsContrastActivity.this, BaiduStatConstants.CONTRAST,
                        BaiduStatConstants.WEIXIN);
                if (ShareTool.isWXInstalled(this)) {
                    cumulateAppLinkScore();
                }
                break;
            case R.id.share_weibo:
//                Toast.makeText(this, R.string.booting_pls_waiting, Toast.LENGTH_SHORT).show();
                shareToWeibo(bean.getString("title"), bean.getString("description"),
                        (Bitmap) bean.get("thump"), bean.getString("url"));
                closeShareDialog();
                StatService.onEvent(GNGoodsContrastActivity.this, BaiduStatConstants.CONTRAST,
                        BaiduStatConstants.WEIBO);
                if (isWeiboValid()) {
                    cumulateAppLinkScore();
                }
                break;
            case R.id.share_qq_friend:
                shareToQq(ShareTool.PLATFORM_QQ_FRIEND, bean.getString("title"),
                        bean.getString("description"), bean.getString("imageUrl"), bean.getString("url"));
                closeShareDialog();
                if (ShareTool.isQQValid(this)) {
                    cumulateAppLinkScore();
                }
                break;
            case R.id.share_qq_zone:
                shareToQq(ShareTool.PLATFORM_QQ_ZONE, bean.getString("title"), bean.getString("description"),
                        bean.getString("imageUrl"), bean.getString("url"));
                closeShareDialog();
                if (ShareTool.isQQValid(this)) {
                    cumulateAppLinkScore();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(arg0);
        setContentView(R.layout.goods_contrast);
        initData();
        initView();
        showFirstbootGuide();
        ShareSDK.initSDK(this);
    }

    @Override
    protected void onResume() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onResume();
        try {
            mContrastArray = ShareDataManager.getJSONArray(this, ShareKeys.KEY_GOODS_CONTRAST_DATA);
            mAdapter.setmCommentArray(mContrastArray);
            ((GoodsContrastAdapter) mAdapter).hideContrastMenuLayout();
            final GNTitleBar titleBar = getTitleBar();
            titleBar.setRightBtnVisible(mContrastArray.length() > 0 ? true : false);
            mAdapter.notifyDataSetChanged();
            showNodataInfoIfNeed();
        } catch (Exception e) {
            e.printStackTrace();
        }

        closeProgressDialog();
    }

    @Override
    protected void onDestroy() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }

    private void showFirstbootGuide() {
        if (mContrastArray != null && mContrastArray.length() > 0) {
            showGuide(R.drawable.compare_guide);
            mGuideImage.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
        }
    }

    protected void initView() {
        super.initView();
        initTitleBar();
        showNodataInfoIfNeed();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        try {
            mContrastArray = ShareDataManager.getJSONArray(this, ShareKeys.KEY_GOODS_CONTRAST_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.log(TAG, LogUtils.getThreadName() + "mContrastArray = " + mContrastArray);
    }

    private void initTitleBar() {
        showTitleBar(true);
        final GNTitleBar titleBar = getTitleBar();
        titleBar.setTitle(R.string.goods_contrast);
        titleBar.setRightBtnVisible(mContrastArray.length() > 0 ? true : false);
        titleBar.setRightBtnText(R.string.clear);
        final OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                try {
                    ShareDataManager.putJsonArray(GNGoodsContrastActivity.this,
                            ShareKeys.KEY_GOODS_CONTRAST_DATA, new JSONArray("[]"));
                    mAdapter.clearCommentArray();
                    mAdapter.notifyDataSetChanged();
                    titleBar.setRightBtnVisible(false);
                    showNoDataLayout();
                    StatService.onEvent(GNGoodsContrastActivity.this, BaiduStatConstants.CONTRAST,
                            BaiduStatConstants.CLEAR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        String msg = getResources().getString(R.string.clear_alls_goods_contrast);
        SpannableStringBuilder style = new SpannableStringBuilder(msg);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tab_text_color_sel)), 4, 6,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        final Dialog dialog = DialogFactory.createMsgDialog(GNGoodsContrastActivity.this, listener, msg);
        titleBar.setRightListener(new OnRightBtnListener() {
            @Override
            public void onClick() {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (null != dialog) {
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void initListView() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mListView = (PullToRefreshListView) findViewById(R.id.contrast_list);
        mListView.setMode(Mode.DISABLED);
        mAdapter = new GoodsContrastAdapter(this, R.layout.goods_contrast_item);
        mAdapter.setmCommentArray(mContrastArray);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void baiduState() {
        StatService.onEvent(this, BaiduStatConstants.CONTRAST, BaiduStatConstants.CLICK);
    }

}
