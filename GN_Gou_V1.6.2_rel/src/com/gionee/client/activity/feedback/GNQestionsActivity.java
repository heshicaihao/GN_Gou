package com.gionee.client.activity.feedback;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.widget.ListView;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.view.adapter.QestionsListAdapter;
import com.gionee.client.view.shoppingmall.GNTitleBar;

public class GNQestionsActivity extends BaseFragmentActivity {

    private ListView mListView;
    private QestionsListAdapter mAdapter;
    private String[] mQestions;
    private String[] mAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umeng_fb_activity_qestions);
        initView();
        initData();
        if (AndroidUtils.translateTopBar(this)) {
            ((GNTitleBar) findViewById(R.id.title_bar)).setTopPadding();
        }
    }

    private void initData() {

        mQestions = getResources().getStringArray(R.array.umeng_fb_qestions_qestion);
        mAnswers = getResources().getStringArray(R.array.umeng_fb_qestions_answer);

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < mQestions.length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(com.gionee.client.model.Constants.FB_QESTION, mQestions[i]);
            map.put(com.gionee.client.model.Constants.FB_ANSWER, mAnswers[i]);
            list.add(map);
        }
        mAdapter.setData(list);

    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.umeng_fb_qestion_list);
        mAdapter = new QestionsListAdapter(this);

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndroidUtils.exitActvityAnim(this);
    }

}
