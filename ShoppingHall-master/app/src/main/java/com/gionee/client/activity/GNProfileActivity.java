package com.gionee.client.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.history.GnBrowseHistoryActivity;
import com.gionee.client.activity.myfavorites.MyFavoritesActivity;
import com.gionee.client.activity.question.ClipPhotoActivity;
import com.gionee.client.activity.question.GNFAQsActivity;
import com.gionee.client.activity.webViewPage.BaseWebViewActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.StringUtills;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.widget.CircleImageView;
import com.gionee.client.view.widget.GNCustomDialog;
import com.gionee.framework.operation.cache.BitmapFileCache;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.page.FrameWorkInit;

public class GNProfileActivity extends BaseFragmentActivity implements OnClickListener {
    private static final String IMAGEPATH = FrameWorkInit.SDCARD + "/GN_GOU/profile/";
    private static final String TAG = "Profile_Page";
    private String mCaptureImageFileName;
    private static final int REQUEST_CODE_CAMERA = 3001;
    private static final int REQUEST_CODE_GALLERY = 3002;
    private static final int REQUEST_CODE_CLIP = 3003;
    private CircleImageView mIcon;
    private EditText mEditText;
    private RequestAction mAction;
    private String mNickName;
    private String mAvatorPath;
    private GNTitleBar mTitleBar;
    private float mStartX;
    private long mStartTime;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.custom_my_profile);
        initView();
        initData();

    }

    private void initData() {
        creatFile();
        LogUtils.log(TAG, "avator:" + UserInfoManager.getInstance().getAvatar(GNProfileActivity.this));
        if (!TextUtils.isEmpty(UserInfoManager.getInstance().getAvatar(GNProfileActivity.this))) {
            GNImageLoader.getInstance().loadBitmap(
                    UserInfoManager.getInstance().getAvatar(GNProfileActivity.this), mIcon);
        }
        mNickName = UserInfoManager.getInstance().getNickName(this);
        mEditText.setText(mNickName);
        mEditText.setSelection(mEditText.getText().length());
        mEditText.setOnClickListener(this);
        mAction = new RequestAction();

    }

    private void creatFile() {
        if (BitmapFileCache.existSDCard()) {
            File file = new File(IMAGEPATH);
            file.mkdirs();
        }
    }

    private void initView() {
        initTitle();
        mIcon = (CircleImageView) findViewById(R.id.my_profile_photo);
        mEditText = (EditText) findViewById(R.id.my_profile_nickname_edit);
    }

    private void initTitle() {
        showTitleBar(true);
        mTitleBar = getTitleBar();
        if (null == mTitleBar) {
            return;
        }
        mTitleBar.setTitle(R.string.edit_profile);
        mTitleBar.setRightBtnText(R.string.save);
        mTitleBar.setRightBtnTextColor(getResources().getColor(R.color.tab_text_color_sel));
        mTitleBar.setRightBtnVisible(true);
        mTitleBar.getRightBtn().setOnClickListener(this);

    }

    private void showSelectProgramDialog() {
        try {
            if (mDialog == null) {
                mDialog = (GNCustomDialog) DialogFactory.createSelectProgramDialog(this);
            }
            if (mDialog != null) {
                mDialog.show();
                mDialog.setDismissBtnVisible();
                mDialog.setCanceledOnTouchOutside(true);
                mDialog.getContentView().findViewById(R.id.camera).setOnClickListener(this);
                mDialog.getContentView().findViewById(R.id.gallery).setOnClickListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_profile_photo:
                showSelectProgramDialog();
                StatService.onEvent(this, BaiduStatConstants.M_INFO_HEAD, "");
                break;
            case R.id.camera:
                if (!BitmapFileCache.existSDCard()) {
                    Toast.makeText(this, getString(R.string.sd_not_exist), Toast.LENGTH_SHORT).show();
                    return;
                }
                getImageFromCamera();
                closeShareDialog();
                break;
            case R.id.gallery:
                if (!BitmapFileCache.existSDCard()) {
                    Toast.makeText(this, getString(R.string.sd_not_exist), Toast.LENGTH_SHORT).show();
                    return;
                }
                getImageFromGallery();
                closeShareDialog();
                break;
            case R.id.title_right_btn:
                saveProfile();
                StatService.onEvent(this, BaiduStatConstants.M_SAVE, "");
                break;
            case R.id.my_profile_nickname_edit:
                StatService.onEvent(this, BaiduStatConstants.M_INFO_NICKNAME, "");
                break;
            default:
                break;
        }
    }

    private void saveProfile() {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        if (!isConnectNetwork()) {
            return;
        }
        String name = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.nick_isnt_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        mTitleBar.getRightBtn().setClickable(false);
        mNickName = name;
        LogUtils.log(TAG, "nick name:" + mNickName + "    path" + mAvatorPath);
        mAction.modifyUserInfo(this, mAvatorPath, name, HttpConstants.Data.ModifyUserInfo.MODIFY_INFO_S);
        showLoadingProgress();
    }

    private boolean isConnectNetwork() {
        try {
            if (Constants.NET_UNABLE == AndroidUtils.getNetworkType(this)) {
                showNetErrorToast();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void getImageFromGallery() {
//        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//        getAlbum.setType(IMAGE_TYPE);
//        startActivityForResult(getAlbum, REQUEST_CODE_GALLERY);
        /**
         * 刚开始，我自己也不知道ACTION_PICK是干嘛的，后来直接看Intent源码， 可以发现里面很多东西，Intent是个很强大的东西，大家一定仔细阅读下
         */
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        /**
         * 下面这句话，与其它方式写是一样的效果，如果： intent.setData(MediaStore.Images .Media.EXTERNAL_CONTENT_URI);
         * intent.setType(""image/*");设置数据类型 如果朋友们要限制上传到服务器的图片类型时可以直接写如 ："image/jpeg 、 image/png等的类型"
         */
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        try {
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getImageFromCamera() {
        try {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mCaptureImageFileName = getCaptureImageFileName();
            Uri imageUri = Uri.fromFile(new File(IMAGEPATH, mCaptureImageFileName));
            LogUtils.log(TAG, "uri:" + imageUri);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(openCameraIntent, REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.open_camera_faild), Toast.LENGTH_SHORT).show();
        }
    }

    public String getCaptureImageFileName() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int cureentDate = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        String fileName = "IMG_" + currentYear + currentMonth + cureentDate + "_" + hour + minute + second
                + ".jpg";
        LogUtils.logd(TAG, LogUtils.getThreadName() + " image file name: " + fileName);
        return fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " requestCode = " + requestCode + ", resultCode = "
                + resultCode);
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                LogUtils.log(TAG, "gallery");
                gotoClipPhoto(getGallrayPath(data));
                break;

            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    String path = IMAGEPATH + mCaptureImageFileName;
                    LogUtils.log(TAG, "camera " + path);
                    gotoClipPhoto(path);
                }
                break;
            case REQUEST_CODE_CLIP:
                if (resultCode == Constants.ActivityResultCode.RESULT_CODE_IAMGE_CLIP) {
                    mAvatorPath = getFilesDir().getAbsolutePath() + "/"
                            + Constants.USER_HEAD_LOCAL_DEFUALT_PATH;
                    FileInputStream localStream;
                    try {
                        localStream = openFileInput(Constants.USER_HEAD_LOCAL_DEFUALT_PATH);
                        Bitmap bitmap = BitmapFactory.decodeStream(localStream);
                        mIcon.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("deprecation")
    private String getGallrayPath(Intent data) {
        try {
            Uri originalUri = data.getData(); // 获得图片的uri
            LogUtils.log(TAG, "originalUri=" + originalUri);
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(originalUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            LogUtils.log(TAG, "originalUri Url=" + path);
            return path;
        } catch (Exception e) {
            LogUtils.loge(TAG, e.toString());
        }
        return "";
    }

    private void gotoClipPhoto(String path) {
        Intent clip = new Intent(this, ClipPhotoActivity.class);
        clip.putExtra(Constants.INTENT_FLAT, path);
        startActivityForResult(clip, REQUEST_CODE_CLIP);
    }

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        super.onSucceed(businessType, isCache, session);
        LogUtils.log(TAG, LogUtils.getFunctionName());
        if (businessType.equals(Url.MY_PROFILE_MODIFY)) {
            JSONObject object = mSelfData.getJSONObject(HttpConstants.Data.ModifyUserInfo.MODIFY_INFO_S);
            String avator = object.optString(HttpConstants.Data.ModifyUserInfo.AVATAR);
            if (!TextUtils.isEmpty(avator)) {
                UserInfoManager.getInstance().setAvator(avator);
            }
            if (!TextUtils.isEmpty(mNickName)) {
                UserInfoManager.getInstance().setNickName(mNickName);
            }
            Toast.makeText(this, getString(R.string.get_qb_success), Toast.LENGTH_SHORT).show();
        }
        hideLoadingProgress();
        mTitleBar.getRightBtn().setClickable(true);
        setResult(Constants.ActivityResultCode.REQUEST_CODE_PROFILE);
        finish();
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        LogUtils.log(TAG, LogUtils.getFunctionName() + "modify failed!" + " errorInfo: " + errorInfo
                + " errorOn " + errorOn);
        showErrToast(errorInfo);
        hideLoadingProgress();
        mTitleBar.getRightBtn().setClickable(true);
    }

    private void showErrToast(String errorInfo) {
        try {
            if (!StringUtills.isNotContainEnglish(errorInfo)) {
                Toast.makeText(this, getString(R.string.upgrade_no_net), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                long endTime = System.currentTimeMillis();
                if (endTime - mStartTime < 200 && event.getX() - mStartX > 100) {// 右滑
                    onBackPressed();
                    AndroidUtils.exitActvityAnim(this);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
