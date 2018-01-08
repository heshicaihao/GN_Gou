package com.gionee.client.view.shoppingmall;

import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GNSearchActivity;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.scan.ScannerActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;

public class GNSearchBar extends GNBaseView {
    private TextView mSearchText;

    public GNSearchBar(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public GNSearchBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public GNSearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected View setContent() {
        return LayoutInflater.from(getContext()).inflate(R.layout.top_search_bar, null);
    }

    @Override
    protected void requestData() {
        RequestAction action = new RequestAction();
        action.getSearchBarDefaultKeyword(this, HttpConstants.Data.RecommendHome.SEARCH_DEFAULT_KEY_JO);

    }

    @Override
    protected void initView() {
        mContentView.findViewById(R.id.two_dimensional_code).setOnClickListener(this);
        mSearchText = (TextView) findViewById(R.id.search);
        mContentView.findViewById(R.id.search).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    gotoSearchActivity();
                    addSearchStatisticsData();
                }
                return false;
            }
        });
    }

    public void setTopPadding() {
        findViewById(R.id.top_title_view).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.two_dimensional_code:
                gotoScannerActivity();
                addTwoDimensionStatisticsData();
                break;
            default:
                break;
        }
    }

    private void addSearchStatisticsData() {
        if (GnHomeActivity.HALL_FRAGMENT.equals(((GnHomeActivity) getSelfContext()).getCurrentsFragmentTag())) {
            ((GnHomeActivity) getSelfContext())
                    .addFlowStatistics(StatisticsConstants.HomePageConstants.TOP_SEARCH);
            ((GnHomeActivity) getSelfContext())
                    .addFlowStatistics(StatisticsConstants.SearchConstants.OPEN_SEARCH_FROM_HOME);
            ((GnHomeActivity) getSelfContext()).setExitStatisticsFlag(true);
        } else if (GnHomeActivity.SEARCH_FRAGMENT.equals(((GnHomeActivity) getSelfContext())
                .getCurrentsFragmentTag())) {
            ((GnHomeActivity) getSelfContext())
                    .addFlowStatistics(StatisticsConstants.CategoryPageConstants.CLICK_SEARCH);
            ((GnHomeActivity) getSelfContext())
                    .addFlowStatistics(StatisticsConstants.SearchConstants.OPEN_SEARCH_FROM_CATEGORY);
            ((GnHomeActivity) getSelfContext()).setIsClickEventOnCategoryFragment(true);
        }
    }

    private void addTwoDimensionStatisticsData() {
        if (GnHomeActivity.HALL_FRAGMENT.equals(((GnHomeActivity) getSelfContext()).getCurrentsFragmentTag())) {
            ((GnHomeActivity) getSelfContext())
                    .addFlowStatistics(StatisticsConstants.HomePageConstants.TWO_DIMENSION_CODE);
            ((GnHomeActivity) getSelfContext()).setExitStatisticsFlag(true);
        } else if (GnHomeActivity.SEARCH_FRAGMENT.equals(((GnHomeActivity) getSelfContext())
                .getCurrentsFragmentTag())) {
            ((GnHomeActivity) getSelfContext())
                    .addFlowStatistics(StatisticsConstants.CategoryPageConstants.CLICK_TWO_DIMENSION);
            ((GnHomeActivity) getSelfContext()).setIsClickEventOnCategoryFragment(true);
        }
    }

    private void gotoSearchActivity() {
        Intent intent = new Intent(getContext(), GNSearchActivity.class);
        intent.putExtra(Constants.Push.SOURCE, ((GnHomeActivity) getSelfContext()).getIntentSource());
        ((BaseFragmentActivity) getContext()).startActivityForResult(intent,
                Constants.ActivityRequestCode.REQUEST_CODE_SEARCH);
        StatService.onEvent(getContext(), "search", "search");
        AndroidUtils.logoFadeAnim((Activity) getContext());
    }

    private void gotoScannerActivity() {

        if (!AndroidUtils.isHadPermission(getContext(), Manifest.permission.CAMERA)) {
            Toast.makeText(getContext(), R.string.no_camera_permission, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), ScannerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((BaseFragmentActivity) getContext()).startActivityForResult(intent,
                Constants.ActivityRequestCode.REQUEST_CODE_TWO_DIMENSION);
        AndroidUtils.enterActvityAnim((Activity) getContext());
        StatService.onEvent(getContext(), "scan", "scan");
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        super.onSucceed(businessType, isCache, session);
        if (businessType.equals(Url.SEARCHBAR_DEFAULT_KEYWORD_URL)) {
            JSONObject mKeyData = mSelfData
                    .getJSONObject(HttpConstants.Data.RecommendHome.SEARCH_DEFAULT_KEY_JO);
            String keyword = null;
            try {
                keyword = mKeyData.optString(HttpConstants.Response.SearchKeywords.KEYWORD_S);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(keyword)) {
                mSearchText.setText(keyword);
            }
        }
    }
}
