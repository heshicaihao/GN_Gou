/*
 * SearchQuestion.java
 * classes : com.gionee.client.activity.question.SearchQuestion
 * @author yuwei
 * 
 * Create at 2015-3-31 上午9:42:24
 */
package com.gionee.client.activity.question;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.GnHomeActivity;
import com.gionee.client.activity.attention.AddAttentionActivity;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.history.GnBrowseHistoryActivity;
import com.gionee.client.activity.myfavorites.MyFavoritesActivity;
import com.gionee.client.activity.webViewPage.BaseWebViewActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;

/**
 * com.gionee.client.activity.question.SearchQuestion
 * 
 * @author yuwei <br/>
 *         create at 2015-3-31 上午9:42:24
 * @description
 */
public class SearchQuestion extends BaseFragmentActivity implements OnClickListener {
    private ListView mQuestionList;
    private ListView mSearchHistoryList;
    private EditText mSearchEdit;
    private static final String QUESTION_SEARCH_HISTORY = "question_search_history";
    private String mSplitStr = ",";
    private SearchQuestionHistoryAdapter mSearchQuestionHistoryAdapter;
    private SearchQuestionResultAdapter mSearchQuestionResultAdapter;
    private String[] mHistoryList;
    private RelativeLayout mNoQuestionLayout;
    private TextView mSearchBtn;
    private float mStartX;
    private float mStartY;
    private long mStartTime;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.search_question);
        initTitle();
        initView();
        setHistory();
    }

    private void setHistory() {
        try {
            String history = ShareDataManager.getString(this, QUESTION_SEARCH_HISTORY, "");
            if (!TextUtils.isEmpty(history)) {
                showHistoryList();
                getHistory(history);
                mSearchQuestionHistoryAdapter = new SearchQuestionHistoryAdapter(this, mHistoryList);
                mSearchHistoryList.setAdapter(mSearchQuestionHistoryAdapter);
            } else {
                mSearchHistoryList.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getHistory(String history) {
        mHistoryList = history.split(mSplitStr);
        revertHistoryList();
    }

    public void revertHistoryList() {
        String[] historyList = new String[mHistoryList.length];
        for (int i = 0; i < mHistoryList.length; i++) {
            historyList[i] = mHistoryList[mHistoryList.length - i - 1];
        }
        mHistoryList = historyList;
    }

    private void showHistoryList() {
        mSearchHistoryList.setVisibility(View.VISIBLE);
        mQuestionList.setVisibility(View.GONE);
        mNoQuestionLayout.setVisibility(View.GONE);
    }

    private void showSearchList() {
        mSearchHistoryList.setVisibility(View.GONE);
        mQuestionList.setVisibility(View.VISIBLE);
        mNoQuestionLayout.setVisibility(View.GONE);
    }

    private void showNoResult() {
        mSearchHistoryList.setVisibility(View.GONE);
        mQuestionList.setVisibility(View.GONE);
        mNoQuestionLayout.setVisibility(View.VISIBLE);
    }

    public void initView() {
        mQuestionList = (ListView) findViewById(R.id.list_search_result);
        mSearchHistoryList = (ListView) findViewById(R.id.list_search_history);
        mSearchEdit = (EditText) findViewById(R.id.search);
        mNoQuestionLayout = (RelativeLayout) findViewById(R.id.no_result_layout);
        mSearchBtn = (TextView) findViewById(R.id.search_btn);
        mSearchHistoryList.setOnItemClickListener(mHistoryListItemListener);
        mQuestionList.setOnItemClickListener(mQuestionListItemListener);
        mSearchQuestionResultAdapter = new SearchQuestionResultAdapter(this, null);
        mQuestionList.setAdapter(mSearchQuestionResultAdapter);
        mSearchEdit.setOnEditorActionListener(searchEditorActionListener);
        mSearchEdit.addTextChangedListener(searchTextWatcher);
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        private CharSequence mTemp;

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            mTemp = arg0;
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {

            if (mTemp.length() == 0) {
                mSearchBtn.setEnabled(false);
            } else {
                mSearchBtn.setEnabled(true);
            }
            Selection.setSelection((Spannable) mTemp, mTemp.length());
        }
    };
    private OnEditorActionListener searchEditorActionListener = new OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView arg0, int actionId, KeyEvent arg2) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                startSearch();
            }
            return true;
        }
    };
    private OnItemClickListener mHistoryListItemListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            int maxLenth = 10;
            if (arg2 == mHistoryList.length || arg2 == maxLenth) {
                clearHistory();
                return;
            }
            String searchKey = (String) mSearchQuestionHistoryAdapter.getItem(arg2);
            if (!TextUtils.isEmpty(searchKey)) {
                mSearchEdit.setText(searchKey);
                startSearch();
            }
        }

        public void clearHistory() {
            Dialog dialog = DialogFactory.createMsgDialog(SearchQuestion.this, new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    ShareDataManager.putString(SearchQuestion.this, QUESTION_SEARCH_HISTORY, "");
                    setHistory();
                }
            }, R.string.certain_clear_search_history);
            dialog.show();
        }
    };
    private OnItemClickListener mQuestionListItemListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            JSONObject obj = (JSONObject) mSearchQuestionResultAdapter.getItem(arg2);
            String id = obj.optString(HttpConstants.Response.ID_I);
            Intent intent = new Intent();
            intent.putExtra(HttpConstants.Response.ID_I, id);
            intent.setClass(SearchQuestion.this, QuestionDetailActivity.class);
            startActivity(intent);
            StatService.onEvent(SearchQuestion.this, BaiduStatConstants.QUESTION_SEARCH_CLICK, arg2 + "");
        }
    };

    private void initTitle() {
        showTitleBar(true);
        getTitleBar().setTitle(R.string.search_question);
        showShadow(false);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.search_btn:
                startSearch();

                break;
            case R.id.goto_ask_question:
                gotoAskQuestion();
                break;
            default:
                break;
        }
    }

    public void gotoAskQuestion() {

        Intent intent = new Intent();
        intent.setClass(this, AskQuestionActivity.class);
        intent.putExtra(Constants.INTENT_FLAT, mSearchEdit.getText().toString());
        startActivity(intent);
    }

    public void startSearch() {
        String keyword = mSearchEdit.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            AndroidUtils.hideInputSoftware(this);
            keyword = keyword.trim();
            requestSearch(keyword);
            addToSearchHistory(keyword);

        } else {
            Toast.makeText(this, R.string.pls_input_keyword, Toast.LENGTH_SHORT).show();
        }
    }

    public void addToSearchHistory(String keyword) {
        String lastHistory = ShareDataManager.getString(this, QUESTION_SEARCH_HISTORY, "");
        getHistory(lastHistory);
        if (TextUtils.isEmpty(lastHistory)) {
            ShareDataManager.putString(this, QUESTION_SEARCH_HISTORY, keyword);
            return;
        }
        if (lastHistory.equals(keyword)) {
            return;
        }
        lastHistory = clearRepeat(keyword, lastHistory);
        ShareDataManager.putString(this, QUESTION_SEARCH_HISTORY, lastHistory + mSplitStr + keyword);
    }

    public String clearRepeat(String keyword, String lastHistory) {
        try {
            lastHistory = lastHistory.replaceAll(mSplitStr + keyword + mSplitStr, mSplitStr);
        } catch (Exception e) {
            lastHistory = lastHistory.replace(mSplitStr + keyword + mSplitStr, mSplitStr);
        }
        if (isEqualLast(keyword, lastHistory)) {
            lastHistory = lastHistory.replace(mSplitStr + keyword, "");
        }
        if (isEqualFirst(keyword, lastHistory)) {
            lastHistory = lastHistory.replace(keyword + mSplitStr, "");
        }

        return lastHistory;
    }

    public boolean isEqualLast(String keyword, String lastHistory) {
        try {
            return lastHistory.substring(lastHistory.lastIndexOf(mSplitStr) + 1, lastHistory.length())
                    .equals(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEqualFirst(String keyword, String lastHistory) {
        try {
            return lastHistory.substring(0, lastHistory.indexOf(mSplitStr)).equals(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isContains(String keyword) {

        if (mHistoryList == null) {
            return false;
        }
        for (int i = 0; i < mHistoryList.length; i++) {
            if (keyword.equals(mHistoryList[i])) {
                LogUtils.log("searchQustion", "equal" + keyword);
                return true;
            }
        }
        return false;
    }

    public void requestSearch(String keyword) {
        RequestAction action = new RequestAction();
        action.searchQuestion(this, HttpConstants.Data.DEFAULT_TARGERT_KEY, keyword.trim());
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        super.onSucceed(businessType, isCache, session);
        if (businessType.equals(Url.SEARCH_QUESTION_URL)) {
            JSONObject mData = (JSONObject) session;
            bindSearchList(mData);
        }

        hideLoadingProgress();
    }

    public void bindSearchList(JSONObject jsonData) {
        try {
            JSONArray questionList = jsonData.optJSONArray(HttpConstants.Response.LIST_JA);
            if (questionList == null || questionList.length() == 0) {
                showNoResult();
                return;
            }
            setAdapter(questionList);
            showSearchList();
        } catch (Exception e) {
            e.printStackTrace();
            showNoResult();
        }
    }

    public void setAdapter(JSONArray questionList) {
        mSearchQuestionResultAdapter.setmQuestionArray(questionList);
        mSearchQuestionResultAdapter.notifyDataSetChanged();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                mStartTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                long endTime = System.currentTimeMillis();
                // 右滑
                if (endTime - mStartTime < 200 && event.getX() - mStartX > 100
                       ) {
                    boolean isShow = AndroidUtils.hideInputSoftware(this);
                    if (!isShow) {
                        onBackPressed();
                        AndroidUtils.exitActvityAnim(this);
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
