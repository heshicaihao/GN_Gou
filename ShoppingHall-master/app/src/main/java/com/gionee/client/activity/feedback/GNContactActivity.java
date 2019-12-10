// Gionee <yangxiong><2013-8-29> add for CR00850885 begin
package com.gionee.client.activity.feedback;

/**
 * com.gionee.client.activity.GNContactActivity
 * @author yangxiong <br/>
 * @date create at 2013-8-29 上午11:11:50
 * @description 用户联系信息设置界面
 */
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.UserInfo;

/**
 * * Activity for user to fill plain messy contact information. Developers are * encouraged to implement their
 * own activities by following this example using * the API provided by SDK. * * @author yangxiong *
 */
public class GNContactActivity extends BaseFragmentActivity {
    /**
     * * The predefined key used by Umeng Feedback SDK to store non-structural * plain text messy contact
     * info. Info can be retrieved from * {@link UserInfo#getContact()} with key *
     * {@link #KEY_UMENG_CONTACT_INFO_PLAIN_TEXT}. <br />
     * * This key is reserved by Umeng. Third party developers DO NOT USE this * key.
     */
    private static final String TAG = GNContactActivity.class.getName();
    private static final String KEY_UMENG_CONTACT_INFO_PLAIN_TEXT = "plain";
    private Button mSaveBtn;
    private EditText mContactInfoEdit;
    private FeedbackAgent mAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umeng_fb_activity_contact);
        initView();
        updateView();
    }

    @SuppressLint("NewApi")
    private void back() {
        AndroidUtils.hidenKeybord(GNContactActivity.this, mContactInfoEdit);
        AndroidUtils.finishActivity(this);
        // add transition animation for exit.
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
            overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                    R.anim.umeng_fb_slide_out_from_right);
        }
    }

    /**
     * 
     * @author yangxiong
     * @description TODO
     */
    private void initView() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        mAgent = new FeedbackAgent(this);
        mSaveBtn = (Button) this.findViewById(R.id.umeng_fb_save);
        mContactInfoEdit = (EditText) this.findViewById(R.id.umeng_fb_contact_info);
        mSaveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
                back();
            }
        });
        if (AndroidUtils.translateTopBar(this)) {
            ((GNTitleBar) findViewById(R.id.title_bar)).setTopPadding();
        }
    }

    /**
     * 
     * @author yangxiong
     * @description TODO
     */
    private void updateView() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        try {
            String contactInfo = mAgent.getUserInfo().getContact().get(KEY_UMENG_CONTACT_INFO_PLAIN_TEXT);
            mContactInfoEdit.setText(contactInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author yangxiong
     * @description TODO
     */
    private void saveUserInfo() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        try {
            UserInfo info = mAgent.getUserInfo();
            if (info == null) {
                info = new UserInfo();
            }
            Map<String, String> contact = info.getContact();
            if (contact == null) {
                contact = new HashMap<String, String>();
            }
            String contactInfo = mContactInfoEdit.getEditableText().toString();
            contact.put(KEY_UMENG_CONTACT_INFO_PLAIN_TEXT, contactInfo);
            info.setContact(contact);
            // Map<String, String> remark = info.getRemark();
            // if (remark == null)
            // remark = new HashMap<String, String>();
            // remark.put("tag1", "game");
            // info.setRemark(remark);
            mAgent.setUserInfo(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndroidUtils.exitActvityAnim(this);
    }
}
//Gionee <yangxiong><2013-8-29> add for CR00850885 end