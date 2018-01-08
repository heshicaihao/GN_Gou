package com.gionee.client.activity;

// Gionee <tianyr> <2013-6-21> add for CR00828519 begin
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.manage.ConfigManager;
import com.gionee.client.business.manage.GNActivityManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.statistic.GnCountDataHelper;
import com.gionee.client.business.statistic.IStatisticsEventManager;
import com.gionee.client.business.statistic.StatisticsConstants;
import com.gionee.client.business.statistic.StatisticsEventManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Config;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.adapter.SearchAutofillAdapter;
import com.gionee.client.view.adapter.SearchFlowLayoutAdapter;
import com.gionee.client.view.widget.FlowLayout;

public class GNSearchActivity extends BaseFragmentActivity implements
		OnClickListener, OnItemClickListener {
	private static final String SEARCH_KEY = "searchKey";
	private static final int HISTORIES_MAX_NUM = 10;
	private static final String TAG = "Search_Activity";
	private static final String HISTORY_PRODUCT = "history_product";
	private static final String HISTORY_PRODUCT_SIZE = "history_product_size";
	private static final String HISTORY_STORE = "history_store";
	private static final String HISTORY_STORE_SIZE = "history_store_size";
	private int mType = 0;// 0表示商品，1表示店铺
	public static final String KEY_WORDS = "keywords";
	private EditText mEditText;
	private String mHotKeyWord;
	private GnCountDataHelper mCountDataHelper;
	private SharedPreferences mPreferences;
	private JSONArray mJsonArray = null;
	private List<String> mHistories = null;
	private List<String> mHots = null;
	private String mUrl;
	private boolean mIsSetEditCursor = false;
	private FlowLayout mHistoryFlowLayout;
	private FlowLayout mHotsFlowLayout;
	private SearchFlowLayoutAdapter mHistoryAdapter;
	private SearchFlowLayoutAdapter mHotsAdapter;
	private View mHistory;
	private LinearLayout mLlTypeChoose;
	private TextView mTvType;
	private TextView mTvHistoryTitle;
	private TextView mTvHotWordsTitle;
	private TextView mClearHistoryRecord;
	private ListView mLvSearchAutofill;
	private SearchAutofillAdapter autofillAdapter;
	private String mIntentSource;
	public static final int GET_SEARCH_AUTOFILL_SUCCESS = 0;
	private JSONObject mAutofillObject = new JSONObject();
	private boolean mIsSearchAutoFill = true;// 是否获取搜索匹配关键字
	private SchemeRegistry mRegistry;
	private Scheme mSchemeHttp, mSchemeHttps;
	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case GET_SEARCH_AUTOFILL_SUCCESS:
				try {
					String json = (String) msg.obj;
					if (json == null || "".equals(json)) {
						return false;
					}
					JSONObject object = new JSONObject(json);
					autofillAdapter.mData = object;
					autofillAdapter.updateData(object);
					JSONArray mFillArray = object.optJSONArray("result");
					if (mFillArray == null || mFillArray.length() == 0) {
						mLvSearchAutofill.setVisibility(View.GONE);
					} else {
						mLvSearchAutofill.setVisibility(View.VISIBLE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			default:
				break;
			}
			return false;
		}
	});
	private IStatisticsEventManager mStatisticsEventManager;
	private boolean mIsDefaultKey = true;
	private boolean mIsEditSetText = false;
	private boolean mIsClick = false;
	private static final int CONNECTION_TIMEOUT = 10000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.log(TAG, LogUtils.getThreadName());
		setContentView(R.layout.main_search_page);
		GNActivityManager.getScreenManager().pushActivity(this);
		initView();
		initData();
		requesData();
	}

	private void requesData() {
		LogUtils.log(TAG, LogUtils.getFunctionName());
		RequestAction action = new RequestAction();
		action.getSearchDefaultKeyword(this,
				HttpConstants.Data.SearchWords.SEARCH_INFO_JO, mType);
	}

	private void initData() {
		mStatisticsEventManager = new StatisticsEventManager(this);
		mStatisticsEventManager.initStatisticsData();
		mCountDataHelper = new GnCountDataHelper(this);
		mPreferences = getSharedPreferences(KEY_WORDS, Context.MODE_PRIVATE);
		mHotKeyWord = mEditText.getHint().toString().trim();
		initHistory();
		mIntentSource = getIntent().getStringExtra(Constants.Push.SOURCE);
	}

	public void addFlowStatistics(String eventId) {
		mStatisticsEventManager.add(eventId);
	}

	private void initHistory() {
		mHistories = loadArray();
		mHistoryAdapter.setData(mHistories);
		if (mHistories.size() > 0) {
			mHistoryFlowLayout.onDataChange();
			mHistoryFlowLayout
					.setOnItemClickListener(new HistoryItemOnClickListenner());
			mHistory.setVisibility(View.VISIBLE);
		} else {
			mHistoryFlowLayout.removeAllViews();
			mHistory.setVisibility(View.GONE);
		}
	}

	private void initView() {
		findViewById(R.id.tv_goods).setOnClickListener(this);
		findViewById(R.id.tv_shop).setOnClickListener(this);
		findViewById(R.id.transparent_view).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						// TODO Auto-generated method stub
						mLlTypeChoose.setVisibility(View.GONE);
						AndroidUtils.hideInputSoftware(GNSearchActivity.this);
						return false;
					}
				});
		mLvSearchAutofill = (ListView) findViewById(R.id.lv_search_autofill);
		mLvSearchAutofill.setOnItemClickListener(this);
		autofillAdapter = new SearchAutofillAdapter(this, mAutofillObject);
		mLvSearchAutofill.setAdapter(autofillAdapter);
		mTvType = (TextView) findViewById(R.id.tv_type);
		mTvHistoryTitle = (TextView) findViewById(R.id.tv_history_title);
		mTvHotWordsTitle = (TextView) findViewById(R.id.tv_hot_words_title);
		mClearHistoryRecord = (TextView) findViewById(R.id.clear_history_record);
		mLlTypeChoose = (LinearLayout) findViewById(R.id.ll_type_choose);
		mEditText = (EditText) findViewById(R.id.search);
		mHistory = (View) findViewById(R.id.search_history);
		mEditText.clearFocus();
		mEditText.setOnClickListener(this);
		mHistoryFlowLayout = (FlowLayout) findViewById(R.id.search_history_flow);
		mHotsFlowLayout = (FlowLayout) findViewById(R.id.search_hot);
		if (AndroidUtils.translateTopBar(this)) {
			RelativeLayout mTopBar = (RelativeLayout) findViewById(R.id.top_menu_bar);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTopBar
					.getLayoutParams();
			params.topMargin = AndroidUtils.dip2px(this, 15);
			mTopBar.setLayoutParams(params);
		}
		mHistoryAdapter = new SearchFlowLayoutAdapter(this);
		mHotsAdapter = new SearchFlowLayoutAdapter(this);
		mHistoryFlowLayout.setAdapter(mHistoryFlowLayout, mHistoryAdapter);
		mHotsFlowLayout.setAdapter(mHotsFlowLayout, mHotsAdapter);
		setListener();

	}

	class HistoryItemOnClickListenner implements FlowLayout.OnItemClickListener {

		@Override
		public void onItemClick(View v, int index) {
			mIsDefaultKey = false;
			setEditCursor(false);
			mLlTypeChoose.setVisibility(View.GONE);
			search(mHistories.get(index));
			StatService.onEvent(GNSearchActivity.this,
					mType == 0 ? BaiduStatConstants.HISTORY_RECORD
							: BaiduStatConstants.HISTORY_STORE_RECORD,
					BaiduStatConstants.OK);
			addFlowStatistics(isGoodSMode() ? StatisticsConstants.SearchConstants.SERACH_HISTORY_GOODS
					: StatisticsConstants.SearchConstants.SERACH_HISTORY_SHOP);
		}
	}

	class HotsItemOnClickListenner implements FlowLayout.OnItemClickListener {

		@Override
		public void onItemClick(View v, int index) {
			mIsDefaultKey = false;
			mLlTypeChoose.setVisibility(View.GONE);
			AndroidUtils.hideInputSoftware(GNSearchActivity.this);
			setEditCursor(false);
			search(mHots.get(index));
			StatService.onEvent(GNSearchActivity.this,
					mType == 0 ? BaiduStatConstants.KEYWORD_CLICK
							: BaiduStatConstants.KEYWORD_STORE_CLICK,
					BaiduStatConstants.OK);
			addFlowStatistics(isGoodSMode() ? StatisticsConstants.SearchConstants.SEARCH_HOTWORD_GOODS
					: StatisticsConstants.SearchConstants.SEARCH_HOTWORD_SHOP);
		}

	}

	private void setListener() {
		mEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					onClickSearch();
				}
				return true;
			}
		});

		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mIsSearchAutoFill) {
					setSearchState();
				}
				mIsClick = true;
			}

		});
	}

	private void addSearchStatic() {
		LogUtils.log(TAG, LogUtils.getFunctionName() + mIsDefaultKey);
		if (mIsDefaultKey) {
			addFlowStatistics(isGoodSMode() ? StatisticsConstants.SearchConstants.SEARCH_DEFAULT_GOODS
					: StatisticsConstants.SearchConstants.SEARCH_DEFAULT_SHOP);
			return;
		}
		addFlowStatistics(isGoodSMode() ? StatisticsConstants.SearchConstants.INITIATIVE_SEARCH_GOODS
				: StatisticsConstants.SearchConstants.INITIATIVE_SEARCH_SHOP);
	}

	@Override
	public void onBackPressed() {
		LogUtils.log(TAG, LogUtils.getThreadName());
		if (mLvSearchAutofill.getVisibility() == View.VISIBLE) {
			mLvSearchAutofill.setVisibility(View.GONE);
			return;
		}
		mLlTypeChoose.setVisibility(View.GONE);
		addExitStaticData();
		finish();
		AndroidUtils.logoFadeAnim(this);
	}

	private void setSearchState() {
		String keyword = mEditText.getText().toString().trim();
		if (keyword.length() < 1) {
			mEditText.setHint(mHotKeyWord);
			mLvSearchAutofill.setVisibility(View.GONE);
			findViewById(R.id.iv_delete).setVisibility(View.GONE);
			mIsDefaultKey = true;
		} else {
			findViewById(R.id.iv_delete).setVisibility(View.VISIBLE);
			requestSearchAutofillByKeyword(keyword);
			return;
		}
		if (mIsEditSetText && mIsDefaultKey) {
			mIsDefaultKey = true;
			return;
		}
		mIsDefaultKey = false;
	}

	private void requestSearchAutofillByKeyword(final String keyword) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = ShareDataManager.getString(GNSearchActivity.this,
						ConfigManager.TAOBAO_SEARCH_AUTOFILL_URL, "");
				if ("".equals(url)) {
					return;
				}
				url = url.replace("%s", URLEncoder.encode(keyword));
				if (url.startsWith("https://")) {
					try {
						doHttpsGet(url, keyword);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					doHttpPost(url, keyword);
				}
			}
		}).start();
	}

	private void search(final String key) {
		mIsSearchAutoFill = false;
		mLvSearchAutofill.setVisibility(View.GONE);
		mIsClick = true;
		StatService.onEvent(GNSearchActivity.this, "s_click", "s_click");
		mIsEditSetText = true;
		mEditText.setText(key);
		mEditText.setSelection(key.length());
		gotoWebPageForResult(getUrl(key, mType), true);
		mEditText.postDelayed(new Runnable() {

			@Override
			public void run() {
				sendCountData(key);
				saveHistory(key);
				updateHistoyList();
			}
		}, 1000);
	}

	private void sendCountData(String key) {
		final Map<String, String> map = new HashMap<String, String>();
		map.put(SEARCH_KEY, key);
		mCountDataHelper.sendCountData(map);
	}

	private String getUrl(String keyword, int type) {
		if (mUrl != null) {
			return mUrl + keyword;
		}
		return Config.SEARCH_KEY_URL + keyword + "&type=" + type;
	}

	private void setEditCursor(boolean isEditFocus) {
		if (isEditFocus) {
			if (mIsSetEditCursor) {
				return;
			}
			mIsSetEditCursor = true;
			mEditText.setCursorVisible(true);
		} else {
			mIsSetEditCursor = false;
			mEditText.setCursorVisible(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search:
			setEditCursor(true);
			if (!"".equals(mEditText.getHint())) {
				mEditText.setHint("");
			}
			mLlTypeChoose.setVisibility(View.GONE);
			if (mLvSearchAutofill.getVisibility() == View.GONE) {
				setSearchState();
			}
			break;
		case R.id.bt_search:
			mLvSearchAutofill.setVisibility(View.GONE);
			mLlTypeChoose.setVisibility(View.GONE);
			AndroidUtils.hideInputSoftware(GNSearchActivity.this);
			onClickSearch();
			break;
		case R.id.iv_delete:
			mLvSearchAutofill.setVisibility(View.GONE);
			mEditText.setText("");
			mIsDefaultKey = true;
			break;
		case R.id.iv_back:
			mLlTypeChoose.setVisibility(View.GONE);
			AndroidUtils.finishActivity(this);
			AndroidUtils.logoFadeAnim(this);
			addExitStaticData();
			break;
		case R.id.clear_history_record:
			mLlTypeChoose.setVisibility(View.GONE);
			AndroidUtils.hideInputSoftware(GNSearchActivity.this);
			showClearDialog();
			mIsClick = true;
			break;
		case R.id.ll_search_type:
			if (mLlTypeChoose.getVisibility() == View.VISIBLE) {
				mLlTypeChoose.setVisibility(View.GONE);
			} else {
				mLlTypeChoose.setVisibility(View.VISIBLE);
			}
			mIsClick = true;
			break;
		case R.id.tv_goods:
			mLlTypeChoose.setVisibility(View.GONE);
			if (mType != 0) {
				setGoodsData();
				initHistory();
				requesData();
				addFlowStatistics(StatisticsConstants.SearchConstants.SEARCH_SHOPS_CHANGETO_GOODS);
			}
			mIsClick = true;
			break;
		case R.id.tv_shop:
			mLlTypeChoose.setVisibility(View.GONE);
			if (mType != 1) {
				setStoreData();
				initHistory();
				requesData();
				addFlowStatistics(StatisticsConstants.SearchConstants.SEARCH_GOODS_CHANGETO_SHOPS);
			}
			break;
		case SearchAutofillAdapter.MAGICID:
			TextView textView = (TextView) v;
			Object object = v.getTag();
			if (object == null) {
				return;
			}
			String key = object.toString() + " "
					+ textView.getText().toString();
			StatService.onEvent(this, "match_click",
					getString(R.string.match_click));
			search(key);
			mIsClick = true;
			break;

		default:
			break;
		}

	}

	private void addExitStaticData() {
		if (!mIsClick) {
			addFlowStatistics(StatisticsConstants.SearchConstants.SEARCH_EXIT);
			LogUtils.log(TAG, "not touch!");
		}
	}

	/**
	 * 设置处于店铺搜索时的数据
	 */
	private void setStoreData() {
		mType = 1;
		mTvHistoryTitle.setText(getString(R.string.search_histroy_store));
		mTvHotWordsTitle.setText(getString(R.string.search_hot_words));
		mClearHistoryRecord.setText(getString(R.string.clear_history_store));
		mEditText.setText("");
		StatService.onEvent(GNSearchActivity.this, "s_switch", "shop");
		mTvType.setText(getString(R.string.shop));
		mIsEditSetText = false;
		mIsDefaultKey = true;
	}

	/**
	 * 设置处于商品搜索时的数据
	 */
	private void setGoodsData() {
		mType = 0;
		mTvHistoryTitle.setText(getString(R.string.search_histroy));
		mTvHotWordsTitle.setText(getString(R.string.search_hot_words));
		mClearHistoryRecord.setText(getString(R.string.clear_history));
		mEditText.setText("");
		StatService.onEvent(GNSearchActivity.this, "s_switch", "goods");
		mTvType.setText(getString(R.string.goods));
		mIsEditSetText = false;
		mIsDefaultKey = true;
	}

	private void showClearDialog() {
		StatService.onEvent(GNSearchActivity.this,
				BaiduStatConstants.CLEAR_SEARCH_HISTORY,
				BaiduStatConstants.CLICK);
		DialogFactory.ClearHistory(this, new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearHistory();
			}
		}, mType).show();
	}

	private void onClickSearch() {
		mLvSearchAutofill.setVisibility(View.GONE);
		String keyWord = mEditText.getText().toString().trim();
		AndroidUtils.hideInputSoftware(GNSearchActivity.this);
		if ("".equals(keyWord)) {
			if (mHotKeyWord == null) {
				Toast.makeText(this, R.string.no_input, Toast.LENGTH_SHORT)
						.show();
				return;
			} else {
				keyWord = mHotKeyWord;
			}
		}
		setEditCursor(false);
		search(keyWord);
		if (keyWord.equals(mHotKeyWord)) {
			mEditText.setText(keyWord);
		}
		addSearchStatic();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		submitStatisticsData();
		GNActivityManager.getScreenManager().popActivity(this);
		mRegistry.unregister("http");
		mRegistry.unregister("https");
	}

	private void submitStatisticsData() {
		RequestAction action = new RequestAction();
		action.submitStatisticsData(this, null, this,
				mStatisticsEventManager.buildStatisticsData(), mIntentSource);
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mIsSearchAutoFill = true;
		if (!"".equals(mEditText.getText().toString())) {
			findViewById(R.id.iv_delete).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.iv_delete).setVisibility(View.GONE);
		}
		StatService.onResume(this);
	}

	private void saveHistory(String text) {
		mHistories.add(0, text);
		for (int i = 1; i < mHistories.size(); i++) {
			if (text.equals(mHistories.get(i))) {
				mHistories.remove(i);
			}
		}
		if (mHistories.size() > HISTORIES_MAX_NUM) {
			mHistories.remove(mHistories.size() - 1);
		}
		saveArray(mHistories);
	}

	private void updateHistoyList() {
		mHistoryFlowLayout.onDataChange();
		mHistoryFlowLayout
				.setOnItemClickListener(new HistoryItemOnClickListenner());
		if (!mHistory.isShown()) {
			mHistory.setVisibility(View.VISIBLE);
		}

	}

	private void clearHistory() {
		mHistories.clear();
		mPreferences.edit()
				.remove(mType == 0 ? HISTORY_PRODUCT_SIZE : HISTORY_STORE_SIZE)
				.commit();
		mHistoryFlowLayout.onDataChange();
		mHistory.setVisibility(View.GONE);
		StatService.onEvent(GNSearchActivity.this,
				mType == 0 ? BaiduStatConstants.CLEAR_SEARCH_HISTORY
						: BaiduStatConstants.CLEAR_SEARCH_STORE_HISTORY,
				BaiduStatConstants.OK);
		addFlowStatistics(isGoodSMode() ? StatisticsConstants.SearchConstants.REMOVE_SEARCH_GOODS
				: StatisticsConstants.SearchConstants.REMOVE_SEARCH_SHOP);
	}

	private List<String> loadArray() {
		int size = getHistoriesNum();
		String keyHead = (mType == 0 ? HISTORY_PRODUCT : HISTORY_STORE);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			list.add(mPreferences.getString(keyHead + "_" + i, null));
		}
		return list;
	}

	private int getHistoriesNum() {
		String key = (mType == 0 ? HISTORY_PRODUCT_SIZE : HISTORY_STORE_SIZE);
		return mPreferences.getInt(key, 0);
	}

	private void saveArray(List<String> list) {
		String sizeKey = (mType == 0 ? HISTORY_PRODUCT_SIZE
				: HISTORY_STORE_SIZE);
		String historyKey = (mType == 0 ? HISTORY_PRODUCT : HISTORY_STORE);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putInt(sizeKey, list.size());
		for (int i = 0; i < list.size(); i++) {
			editor.putString(historyKey + "_" + i, list.get(i));
		}
		editor.commit();
	}

	@Override
	public void onSucceed(String businessType, boolean isCache, Object session) {
		super.onSucceed(businessType, isCache, session);
		LogUtils.log(TAG, LogUtils.getFunctionName() + businessType);
		removeStatisticsData(businessType);
		if (businessType.equals(Url.SEARCH_RAND_KEYWORDS)) {
			int type = (Integer) session;
			if (type == mType) {
				try {
					JSONObject object = mSelfData
							.getJSONObject(HttpConstants.Data.SearchWords.SEARCH_INFO_JO);
					if (object == null) {
						return;
					}
					String keyword = object
							.optString(HttpConstants.Response.SearchKeywords.KEYWORD_S);
					if (!TextUtils.isEmpty(keyword)) {
						mHotKeyWord = keyword;
					}
					updateHotsView(object);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void updateHotsView(JSONObject object) {
		mEditText.setHint(mHotKeyWord);
		mJsonArray = object
				.optJSONArray(HttpConstants.Response.SearchKeywords.KEYWORDS_JA);
		mHots = new ArrayList<String>();
		for (int i = 0; i < mJsonArray.length(); i++) {
			mHots.add(mJsonArray.optString(i));
		}
		mHotsAdapter.setData(mHots);
		mHotsFlowLayout.onDataChange();
		mHotsFlowLayout.setOnItemClickListener(new HotsItemOnClickListenner());
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mStatisticsEventManager.saveStatisticsData();
	}

	private void saveStatisticsData(String businessType) {
		if (businessType.equals(Url.STATISTICS_SUBMIT)) {
			mStatisticsEventManager.saveStatisticsData();
		}
	}

	private void removeStatisticsData(String businessType) {
		if (businessType.equals(Url.STATISTICS_SUBMIT)) {
			mStatisticsEventManager.removeStatisticsData();
		}
	}

	private boolean isGoodSMode() {
		return mType == 0;
	}

	private void doHttpPost(String url, String keyword) {
		String strResult = null;
		HttpPost httpRequest = new HttpPost(url);
		// 取得HTTP response
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 20000); // 设置请求超时时间
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000); // 读取超时
		client.getParams().setParameter(HTTP.CONN_DIRECTIVE,
				HTTP.CONN_KEEP_ALIVE);
		HttpResponse httpResponse;
		try {
			httpResponse = client.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
			Message message = new Message();
			String currentKeyWord = mEditText.getText().toString().trim();
			if (currentKeyWord.equals(keyword)) {
				message.what = GET_SEARCH_AUTOFILL_SUCCESS;
				message.obj = strResult;
				handler.sendMessage(message);
			}
			client.getConnectionManager().shutdown();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			client.getConnectionManager().shutdown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			client.getConnectionManager().shutdown();
		}
	}

	public String doHttpsGet(String serverURL, String keyword) throws Exception {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpClient hc = initHttpClient(httpParameters);
		HttpGet get = new HttpGet(serverURL);
		get.addHeader("Content-Type", "text/xml");
		get.setParams(httpParameters);
		HttpResponse response = null;
		try {
			response = hc.execute(get);
		} catch (UnknownHostException e) {
			throw new Exception("Unable to access " + e.getLocalizedMessage());
		} catch (SocketException e) {
			throw new Exception(e.getLocalizedMessage());
		}
		int sCode = response.getStatusLine().getStatusCode();
		if (sCode == HttpStatus.SC_OK) {
			Message message = new Message();
			String currentKeyWord = mEditText.getText().toString().trim();
			if (currentKeyWord.equals(keyword)) {
				message.what = GET_SEARCH_AUTOFILL_SUCCESS;
				message.obj = EntityUtils.toString(response.getEntity());
				handler.sendMessage(message);
			}
			return EntityUtils.toString(response.getEntity());
		} else {
			throw new Exception("StatusCode is " + sCode);
		}
	}

	public HttpClient initHttpClient(HttpParams params) {
		KeyStore trustStore;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			if (mSchemeHttp == null) {
				mSchemeHttp = new Scheme("http",
						PlainSocketFactory.getSocketFactory(), 80);
			}
			if (mSchemeHttps == null) {
				mSchemeHttps = new Scheme("https", sf, 443);
			}
			if (mRegistry == null) {
				mRegistry = new SchemeRegistry();
				mRegistry.register(mSchemeHttp);
				mRegistry.register(mSchemeHttps);
			}
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, mRegistry);
			return new DefaultHttpClient(ccm, params);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DefaultHttpClient(params);
	}

	public class SSLSocketFactoryImp extends SSLSocketFactory {
		final SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryImp(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		StatService.onEvent(this, "match_click",
				getString(R.string.match_click));
		search((String) autofillAdapter.getItem(arg2));
		addFlowStatistics(isGoodSMode() ? StatisticsConstants.SearchConstants.SEARCH_MATCH_WORD_GOODS
				: StatisticsConstants.SearchConstants.SEARCH_MATCH_WORD_SHOP);
		mIsClick = true;
	}

	@Override
	public void onErrorResult(String businessType, String errorOn,
			String errorInfo, Object session) {
		super.onErrorResult(businessType, errorOn, errorInfo, session);
		saveStatisticsData(businessType);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (Constants.ActivityRequestCode.REQUEST_CODE_WEB_PAGE == requestCode) {
			addFlowStatistics(StatisticsConstants.SearchConstants.OPEN_SEARCH_FROM_OTHER);
			mIsEditSetText = false;
		}
	}
}
// Gionee <tianyr> <2013-6-21> add for CR00828519 end
