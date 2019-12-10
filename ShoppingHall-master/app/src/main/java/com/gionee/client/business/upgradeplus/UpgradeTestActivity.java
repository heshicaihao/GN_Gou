//package com.gionee.client.business.upgradeplus;
//
//import java.io.File;
//import java.util.Random;
//
//import android.app.Dialog;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.v4.app.FragmentActivity;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.TextView;
//
//import com.gionee.client.R;
//import com.gionee.client.activity.GnHomeActivity;
//import com.gionee.client.business.manage.GNActivityManager;
//import com.gionee.client.business.upgradeplus.UpgradeManager.UserFeedback;
//import com.gionee.client.business.upgradeplus.common.UpgradeUtils;
//import com.gionee.client.business.util.DialogFactory;
//import com.gionee.client.business.util.LogUtils;
//import com.gionee.client.model.Constants;
//
//public class UpgradeTestActivity extends FragmentActivity {
//
//    private static final String TAG = "TestPageActivity";
//    private static final String TEST_PATH = "/下载";
//    private TextView mTextView;
//    private Button mCheckBtn;
//    private Button mCloseApp;
//    private Button mDownloadComplete;
//    private Button mNoNetworkBtn;
//    private Button mCleanDataBtn;
//    private Button mTestUIFeedBack;
//    private Button mGoHomeBtn;
//    private Button mCleanApkBtn;
//    private Button mFroceVersionBtn;
//    private Button mProgressBtn;
//    private Button mHasNewVersionBtn;
//    private Button mCallBtn;
//    private Button mCallBtn2;
//    private UpgradeManager mManager;
//    private CheckBox mCheckBox;
//
//    private boolean deleteDir(File dir) {
//        if (dir.isDirectory()) {
//            String[] children = dir.list();
//            for (int i = 0; i < children.length; i++) {
//                boolean success = deleteDir(new File(dir, children[i]));
//                if (!success) {
//                    return false;
//                }
//            }
//        }
//        return dir.delete();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        LogUtils.log(TAG, LogUtils.getThreadName());
//        mManager = new UpgradeManager(UpgradeTestActivity.this, UpgradeUtils.getProductKey(this));
//        setContentView(R.layout.test_page);
//        initViews();
//        GNActivityManager.getScreenManager().pushActivity(this);
//
//        mCallBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Uri telUri = Uri.parse("tel:10086");
//                Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
//                UpgradeTestActivity.this.startActivity(intent);
//            }
//        });
//        mCallBtn2.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {/*
//                                         Uri telUri = Uri.parse("tel:10086");
//                                         Intent intent = new Intent(Intent.ACTION_CALL, telUri);
//                                         UpgradeTestActivity.this.startActivity(intent);
//                                         */
//            }
//        });
//
//    }
//
//    private void initViews() {
//        mTextView = (TextView) findViewById(R.id.test_page_info);
//        mTextView.setText(this.getResources().getString(R.string.channel));
//        mCallBtn = (Button) findViewById(R.id.test_call);
//        mCallBtn2 = (Button) findViewById(R.id.test_call2);
//        mCallBtn.setText("拨打电话");
//        mCallBtn2.setText("拨打电话2");
//        mCheckBtn = (Button) findViewById(R.id.check_btn);
//        mCloseApp = (Button) findViewById(R.id.close_app_btn);
//        mGoHomeBtn = (Button) findViewById(R.id.go_home_btn);
//        mHasNewVersionBtn = (Button) findViewById(R.id.test_has_new_version);
//        mHasNewVersionBtn.setText("新版本提示");
//        mHasNewVersionBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.showUserFeedback(UserFeedback.MESSAGE_HAS_NEW_VERSION);
//            }
//        });
//        mGoHomeBtn.setText("首页");
//        mGoHomeBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                goHome();
//
//            }
//        });
//
//        mCleanApkBtn = (Button) findViewById(R.id.clean_btn);
//        mCleanApkBtn.setText("删除已经下载文件");
//        mCleanApkBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                try {
//                    deleteDir(new File(Environment.getExternalStorageDirectory().getPath() + TEST_PATH));
//                } catch (Exception e) {
//                    LogUtils.loge(TAG, LogUtils.getThreadName(), e);
//
//                }
//
//            }
//        });
//
//        mDownloadComplete = (Button) findViewById(R.id.download_complete_btn);
//        mCheckBox = (CheckBox) findViewById(R.id.always_force_mode_checkbox);
//        mNoNetworkBtn = (Button) findViewById(R.id.test_no_network_btn);
//        mCleanDataBtn = (Button) findViewById(R.id.test_clean_data);
//        mTestUIFeedBack = (Button) findViewById(R.id.test_ui_feedback);
//        mProgressBtn = (Button) findViewById(R.id.test_progress_on);
//        mFroceVersionBtn = (Button) findViewById(R.id.test_force_version);
//        mFroceVersionBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mManager.showUserFeedback(UserFeedback.MESSAGE_FORCE_MODE);
//
//            }
//        });
//        mTestUIFeedBack.setText("模拟随机事件");
//        mCleanDataBtn.setText("清理数据");
//        mCleanDataBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.cleanData();
//
//            }
//        });
//
//        mCheckBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.startCheck(false);
//            }
//        });
//
//        mCloseApp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogFactory.createAppExitDialog(UpgradeTestActivity.this).show();
//            }
//        });
//
//        mDownloadComplete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.showUserFeedback(UserFeedback.MESSAGE_DOWNLOAD_COMPLETE);
//            }
//        });
//
//        mCheckBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mCheckBox.isChecked()) {
//                    mManager.setAlwaysForceMode(true);
//                } else {
//                    mManager.setAlwaysForceMode(false);
//                }
//            }
//        });
//
//        mNoNetworkBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.showUserFeedback(UserFeedback.MESSAGE_NO_NETWORK);
//            }
//        });
//        mManager.startCheck(true);
//
//        mTestUIFeedBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Random random = new Random();
//                int id = random.nextInt(11);
//                if (id == 6 || id == 7) {
//                    return;
//                }
//                mManager.showUserFeedback(id);
//            }
//        });
//
//        final long times = 8;
//        mProgressBtn.setText("下载 Progress(" + times + "秒后关闭)");
//        mProgressBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.showUserFeedback(UserFeedback.MESSAGE_DOWNLOAD_PROGRESS_ON);
//                mManager.showUserFeedback(UserFeedback.MESSAGE_DOWNLOAD_PROGRESS_OFF, times * 1000);
//
//            }
//        });
//    }
//
//    private static final int DIALOG_EXIT_APP = 0;
//
//    @Override
//    protected Dialog onCreateDialog(int id) {
//        LogUtils.log(TAG, LogUtils.getThreadName() + "id:" + id);
//        switch (id) {
//            case DIALOG_EXIT_APP:
//                return DialogFactory.createAppExitDialog(this);
//        }
//        return null;
//    }
//
//    @SuppressWarnings("deprecation")
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                LogUtils.log(TAG, LogUtils.getThreadName());
//                this.showDialog(DIALOG_EXIT_APP);
//
//                return true;
//        }
//        return false;
//    }
//
//    @Override
//    protected void onStart() {
//        LogUtils.log(TAG, LogUtils.getThreadName());
//        super.onStart();
//    }
//
//    @Override
//    protected void onStop() {
//        LogUtils.log(TAG, LogUtils.getThreadName());
//        super.onStop();
//    }
//
//    @Override
//    protected void onRestart() {
//        LogUtils.log(TAG, LogUtils.getThreadName());
//        super.onRestart();
//    }
//
//    @Override
//    protected void onResume() {
//        LogUtils.log(TAG, LogUtils.getThreadName());
//        super.onResume();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        LogUtils.log(TAG, LogUtils.getThreadName());
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        LogUtils.log(TAG, LogUtils.getThreadName());
//        super.onRestoreInstanceState(savedInstanceState);
//    }
//
//    @Override
//    protected void onDestroy() {
//        LogUtils.log(TAG, LogUtils.getThreadName());
//        super.onDestroy();
//        mManager.onDestroy();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        LogUtils.log(TAG, LogUtils.getThreadName());
//    }
//
//    private void goHome() {
//        Intent intent = new Intent(UpgradeTestActivity.this, GnHomeActivity.class);
//        String url = getIntent().getStringExtra(Constants.Home.KEY_INTENT_URL);
//        if (!TextUtils.isEmpty(url)) {
//            intent.putExtra(Constants.Home.KEY_INTENT_URL, url);
//        }
//        startActivity(intent);
//        finish();
//    }
//
//}
