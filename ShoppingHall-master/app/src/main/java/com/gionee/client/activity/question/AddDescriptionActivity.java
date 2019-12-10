/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-4-7 下午04:13:26
 */
package com.gionee.client.activity.question;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.HttpConstants;
import com.gionee.framework.operation.utills.AndroidUtills;

/**
 * com.gionee.client.activity.question.AddDescriptionActivity
 * 
 * @author yangxiong <br/>
 * @date create at 2015-4-7 下午04:13:26
 * @description TODO 添加描述页面
 */
public class AddDescriptionActivity extends BaseFragmentActivity {
    private static final String TAG = "AddDescriptionActivity";
    private EditText mAddDescriptionEdit;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MIN_DESCRIPTION_LENGTH = 10;
    private TextView mWordCountRemind;
    private Button mOkButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle arg0) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(arg0);
        setContentView(R.layout.add_description);
        initView();
    }

    @Override
    public void onBackPressed() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        AndroidUtils.hideInputSoftware(this);
        super.onBackPressed();
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        private int mBeforeTextLength;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int length = s.length();
            LogUtils.log(TAG, LogUtils.getThreadName() + "length = " + length);
            if (length > MAX_DESCRIPTION_LENGTH) {
                mWordCountRemind.setVisibility(View.VISIBLE);
                mWordCountRemind.setText(getString(R.string.edittext_more_note, length
                        - MAX_DESCRIPTION_LENGTH));
            } else {
                mWordCountRemind.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mBeforeTextLength = s.length();
            LogUtils.log(TAG, LogUtils.getThreadName() + " length = " + mBeforeTextLength);
        }

        @Override
        public void afterTextChanged(Editable s) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            String content = mAddDescriptionEdit.getText().toString();
            if (!TextUtils.isEmpty(content) && content.length() > MAX_DESCRIPTION_LENGTH
                    && mBeforeTextLength <= MAX_DESCRIPTION_LENGTH) {
                AndroidUtils.showShortToast(AddDescriptionActivity.this, R.string.add_description_most_note);
            }
        }
    };

    private void initView() {
        mOkButton = (Button) findViewById(R.id.ok_btn);
        mOkButton.setOnClickListener(mOkBtnListener);
        mCancelButton = (Button) findViewById(R.id.cancel_btn);
        mCancelButton.setOnClickListener(mCancelBtnListener);
        mAddDescriptionEdit = (EditText) findViewById(R.id.add_description);
        mWordCountRemind = (TextView) findViewById(R.id.text_limit_remind);
        mAddDescriptionEdit.addTextChangedListener(mTextWatcher);
        mAddDescriptionEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (hasFocus) {
                    mAddDescriptionEdit.setSelection(mAddDescriptionEdit.getText().toString().length());
                }
            }
        });
        String description = getIntent().getStringExtra(HttpConstants.Request.QuestionSubmit.CONTENT);
        if (!TextUtils.isEmpty(description)) {
            mAddDescriptionEdit.setText(description);
        }
    }

    private OnClickListener mOkBtnListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            String description = mAddDescriptionEdit.getText().toString();
            if (TextUtils.isEmpty(description) || description.equals("\n")
                    || description.length() < MIN_DESCRIPTION_LENGTH) {
                DialogFactory.createAddDescriptionMinDialog(AddDescriptionActivity.this).show();
                return;
            }
            if (description.length() > MAX_DESCRIPTION_LENGTH) {
                DialogFactory.createAddDescriptionMaxDialog(AddDescriptionActivity.this).show();
                return;
            }
            AndroidUtills.hidenKeybord(AddDescriptionActivity.this, mAddDescriptionEdit);
            Intent intent = new Intent();
            intent.putExtra(HttpConstants.Request.QuestionSubmit.CONTENT, mAddDescriptionEdit.getText()
                    .toString());
            setResult(RESULT_OK, intent);
            AndroidUtils.finishActivity(AddDescriptionActivity.this);
        }
    };

    private OnClickListener mCancelBtnListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtils.log(TAG, LogUtils.getThreadName());
            AndroidUtills.hidenKeybord(AddDescriptionActivity.this, mAddDescriptionEdit);
            AndroidUtils.finishActivity(AddDescriptionActivity.this);
        }
    };
}
