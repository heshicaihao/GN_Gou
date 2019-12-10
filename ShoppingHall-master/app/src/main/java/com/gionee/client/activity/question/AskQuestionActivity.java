/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-4-3 下午06:01:11
 */
package com.gionee.client.activity.question;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.imageScan.ImageFolderScanActivity;
import com.gionee.client.activity.imageScan.ImageScanAndSelectActivity;
import com.gionee.client.business.action.RequestAction;
import com.gionee.client.business.manage.UserInfoManager;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.util.StringUtills;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.model.Url;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.shoppingmall.GNTitleBar.OnRightBtnListener;
import com.gionee.client.view.widget.CircleImageView;
import com.gionee.client.view.widget.GNCustomDialog;
import com.gionee.client.view.widget.ImagePanelLayout;
import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.operation.cache.BitmapFileCache;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.net.NetUtil;
import com.gionee.framework.operation.page.FrameWorkInit;
import com.gionee.framework.operation.page.PageCacheManager;
import com.gionee.framework.operation.utills.AndroidUtills;
import com.gionee.framework.operation.utills.BitmapUtills;
import com.gionee.framework.operation.utills.PictureUtil;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * com.gionee.client.activity.question.AskQuestionActivity
 * 
 * @author yangxiong <br/>
 * @date create at 2015-4-3 下午06:01:11
 * @description TODO 提问页面
 */
public class AskQuestionActivity extends BaseFragmentActivity implements OnClickListener {
    private static final int MIN_NICK_NAME_SIZE = 1;
    private static final String QUESTION_SEND_MAX = "max";
    private static final String QUESTION_SEND_MIN = "min";
    private static final String QUESTION_SEND_NICK = "nick";
    private static final String QUESTION_SEND_DONE = "done";
    private static final int MAX_IMAGE_COUNT = 3;
    private static final String TAG = "AskQuestionActivity";
    private static final String QUESTION_DESCRIPTION = "question_description";
    private static final String QUESTION_CONTENT = "question_content";
    private static final String IMAGEPATH = FrameWorkInit.SDCARD + "/GN_GOU/questionimgcache/";
    private static final int MAX_CONTENT_LENGTH = 50;
    private static final int MIN_CONTENT_LENGTH = 8;
    private String mImageCacheDir;
    private static final int CHILD_COUNT_MAX = 5;
    private TextView mAskQuestionTips;
    private EditText mNickName;
    private CircleImageView mCircleAvatar;
    private EditText mContent;
    private TextView mDescription;
    private Button mAddDescription;
    private ImageView mAddPhotoImgView;
    private TextView mAddPhotoTv;
    private ImagePanelLayout mImagePanelLayout;
    private ProgressBar mProgressBar;
    private RequestAction mRequestAction;
    private int mCellWidth;
    private int mCellHeight;
    private ArrayList<String> mPicList = new ArrayList<String>();
    private String mCaptureImageFileName;
    private String mAvatarImagePath;
    private TextView mWordCountRemind;
    private GNCustomDialog mSavedDataDialog;
    private String mContentStr;
    private String mDescriptionStr;
    private String mVerifyCode;
    private String mNickNameStr;
    private boolean mIsSubmitting;
    private float mStartX;
    private float mStartY;
    private long mStartTime;

    @Override
    public void onSucceed(String businessType, boolean isCache, Object session) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onSucceed(businessType, isCache, session);
        if (!isCache) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (Url.QUESTION_SUBMIT_URL.equals(businessType)) {
            mIsSubmitting = false;
            AndroidUtils.showToast(this, R.string.question_publish_success, Toast.LENGTH_SHORT);
            AndroidUtils.finishActivity(this);
            clearContent();
            clearDescription();
            UserInfoManager.getInstance().setNickName(mNickNameStr);
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        GNImageLoader.getInstance().init(this);
    }

    @Override
    public void onErrorResult(String businessType, String errorOn, String errorInfo, Object session) {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " errorOn = " + errorOn + " errorInfo = " + errorInfo);
        super.onErrorResult(businessType, errorOn, errorInfo, session);
        mProgressBar.setVisibility(View.GONE);
        if (Url.QUESTION_SUBMIT_URL.equals(businessType)) {
            if (!StringUtills.isNotContainEnglish(errorInfo)) {
                AndroidUtils.showToast(this, R.string.question_publish_fail, Toast.LENGTH_SHORT);
            }
            mIsSubmitting = false;
        }
    }

    @Override
    public void onClick(View v) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.add_description:
                gotoAddDescriptionActivity();
                StatService.onEvent(AskQuestionActivity.this, BaiduStatConstants.QUESTION_DESC,
                        BaiduStatConstants.QUESTION_DESC);
                break;
            case R.id.portrait:
                AndroidUtils.showShortToast(this, R.string.modify_avata_note);
                StatService.onEvent(AskQuestionActivity.this, BaiduStatConstants.QUSTION_HEAD,
                        BaiduStatConstants.QUSTION_HEAD);
                break;
            case R.id.addPhoto:
                goToAddImage();
                StatService.onEvent(AskQuestionActivity.this, BaiduStatConstants.QUESTION_PIC,
                        BaiduStatConstants.QUESTION_PIC);
                break;
            case R.id.camera:
                if (!BitmapFileCache.existSDCard()) {
                    Toast.makeText(this, getString(R.string.sd_not_exist), Toast.LENGTH_SHORT).show();
                    return;
                }
                int requestCode = getRequestCodeForCamera(v);
                getImageFromCamera(requestCode);
                closeShareDialog();
                break;
            case R.id.gallery:
                if (!BitmapFileCache.existSDCard()) {
                    Toast.makeText(this, getString(R.string.sd_not_exist), Toast.LENGTH_SHORT).show();
                    return;
                }
                int reqCode = getRequestionCodeForGallery(v);
                getImageFromGallery(reqCode);
                closeShareDialog();
                break;
            case R.id.nickname:
                StatService.onEvent(AskQuestionActivity.this, BaiduStatConstants.QUESTION_NICKNAME,
                        BaiduStatConstants.QUESTION_NICKNAME);
                break;
            default:
                break;
        }
    }

    private int getRequestionCodeForGallery(View v) {
        Integer viewId = (Integer) v.getTag();
        int reqCode = 0;
        if (viewId == R.id.portrait) {
            reqCode = Constants.ActivityRequestCode.REQUEST_CODE_GALLERY_FROM_AVATAR;
        } else if (viewId == R.id.addPhoto) {
            reqCode = Constants.ActivityRequestCode.REQUEST_CODE_GALLERY_FROM_ADDIMAGE;
        }
        return reqCode;
    }

    private int getRequestCodeForCamera(View v) {
        Integer sourceViewId = (Integer) v.getTag();
        int requestCode = 0;
        if (sourceViewId == R.id.portrait) {
            requestCode = Constants.ActivityRequestCode.REQUEST_CODE_CAMERA_FROM_AVATAR;
        } else if (sourceViewId == R.id.addPhoto) {
            requestCode = Constants.ActivityRequestCode.REQUEST_CODE_CAMERA_FROM_ADDIMAGE;
        }
        return requestCode;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(arg0);
        setContentView(R.layout.ask_question);
        initTitleBar();
        initData();
        initView();
    }

    @Override
    public void onBackPressed() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (isNeedSaveData()) {
            showSaveDataDialog();
        } else {
            super.onBackPressed();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (mIsSubmitting) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isNeedSaveData() {
        LogUtils.log(TAG, LogUtils.getThreadName() + " mContentStr = " + mContentStr + ", mDescriptionStr = "
                + mDescriptionStr);
        String content = mContent.getText().toString();
        String descrption = mDescription.getText().toString();
        // 内容没变化时显示
        if (mContentStr != null && mContentStr.equals(content) && mDescriptionStr.equals(descrption)) {
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " requestCode = " + requestCode + ", resultCode = "
                + resultCode);
        switch (requestCode) {
            case Constants.ActivityRequestCode.REQUEST_CODE_GALLERY_FROM_ADDIMAGE:// 添加图片
                showThumpFromGallery();
                break;
            case Constants.ActivityRequestCode.REQUEST_CODE_CAMERA_FROM_ADDIMAGE: // 添加图片
                if (resultCode == RESULT_OK) {
                    showThumpFromCamera();
                }
                break;
            case Constants.ActivityRequestCode.REQUEST_CODE_GALLERY_FROM_AVATAR: // 修改头像
                showAvatarFromGallery();
                break;
            case Constants.ActivityRequestCode.REQUEST_CODE_CAMERA_FROM_AVATAR: // 修改头像
                if (resultCode == RESULT_OK) {
                    showAvatarFromCamera();
                }
                break;
            case Constants.ActivityRequestCode.REQUEST_CODE_ADD_DESCRIPTION: // 添加描述
                if (resultCode == RESULT_OK) {
                    String description = data.getStringExtra(HttpConstants.Request.QuestionSubmit.CONTENT);
                    if (!TextUtils.isEmpty(description)) {
                        mDescription.setText(description);
                        mAddDescription.setText(R.string.modify_decription);
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            LogUtils.log(TAG, LogUtils.getThreadName() + " back key pressed");
            StatService.onEvent(AskQuestionActivity.this, BaiduStatConstants.QUESTION_BACK,
                    BaiduStatConstants.FOOT_BACK);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addImage(Bitmap bd, String filePath) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " filePath = " + filePath);
        View rLayout = makeAddedView(bd, filePath);
        int count = mImagePanelLayout.getChildCount();
        if (count < CHILD_COUNT_MAX) {
            mImagePanelLayout.addView(rLayout, count - 2); // 从尾部添加图片
            if (mImagePanelLayout.getChildCount() == CHILD_COUNT_MAX) {
                mAddPhotoImgView.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.question_image_max_limit_note),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private View makeAddedView(Bitmap bd, String filePath) {
        View rLayout = LayoutInflater.from(this).inflate(R.layout.add_image, null);
        ImageView addedImg = (ImageView) rLayout.findViewById(R.id.addImage);
        addedImg.setImageBitmap(bd);
        final ImageView deleteImg = (ImageView) rLayout.findViewById(R.id.delete);
        ImageInfo imgInfo = new ImageInfo(rLayout, filePath);
        deleteImg.setTag(imgInfo);
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                ImageInfo imgInfo = (ImageInfo) deleteImg.getTag();
                mImagePanelLayout.removeView(imgInfo.getView());
                mPicList.remove(imgInfo.getFilePath());
                if (mImagePanelLayout.getChildCount() < CHILD_COUNT_MAX) {
                    mAddPhotoImgView.setVisibility(View.VISIBLE);
                }
            }
        });
        return rLayout;
    }

    private void showThumpFromCamera() {
        try {
            mAddPhotoTv.setVisibility(View.GONE);
            String path = IMAGEPATH + mCaptureImageFileName;
            showThumpAndSaveSmallBitmap(path);
            allScan(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAvatarFromCamera() {
        try {
            String path = IMAGEPATH + mCaptureImageFileName;
            showAvatarAndSaveSmallBitmap(path);
            allScan(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showThumpFromGallery() {
        mAddPhotoTv.setVisibility(View.GONE);
        MyBean bean = PageCacheManager.LookupPageData(ImageScanAndSelectActivity.class.getName());
        @SuppressWarnings("unchecked")
        List<String> pathList = (List<String>) bean.get(Constants.SELECT_IMAGE_LIST);
        int size = pathList != null ? pathList.size() : 0;
        for (int i = 0; i < size; i++) {
            int count = mImagePanelLayout.getChildCount();
            if (i == MAX_IMAGE_COUNT || count >= CHILD_COUNT_MAX) {
                LogUtils.log(TAG, LogUtils.getThreadName() + " imaged more than 3 pieces");
                Toast.makeText(this, getResources().getString(R.string.question_image_max_limit_note),
                        Toast.LENGTH_SHORT).show();
                break;
            }
            String path = (String) pathList.get(i);
            showThumpAndSaveSmallBitmap(path);
        }
    }

    private void showAvatarFromGallery() {
        MyBean bean = PageCacheManager.LookupPageData(ImageScanAndSelectActivity.class.getName());
        @SuppressWarnings("unchecked")
        List<String> pathList = (List<String>) bean.get(Constants.SELECT_IMAGE_LIST);
        int size = pathList != null ? pathList.size() : 0;
        if (size > 0) {
            String path = (String) pathList.get(0);
            showAvatarAndSaveSmallBitmap(path);
        }
    }

    private void showThumpAndSaveSmallBitmap(String path) {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " path = " + path);
        try {
            Bitmap bm = PictureUtil.getSmallBitmap(path, PictureUtil.IMAGE_MAX_WIDTH,
                    PictureUtil.IMAGE_MAX_HEIGHT);
            File f = new File(path);
            bm = adjustDegreeIfRotate(bm, f);
            String smallImagePath = saveSmallImage(bm, f);
            int h = mAddPhotoImgView.getHeight();
            float scaleWidth = h / (float) bm.getWidth();
            float scaleHeigh = h / (float) bm.getHeight();
            Bitmap smallBitmap = BitmapUtills.getScaleBitmap(bm, scaleWidth, scaleHeigh);
            // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
            BitmapUtills.bitmapRecycle(bm);
            mPicList.add(smallImagePath);
            addImage(smallBitmap, smallImagePath);
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName() + " make small image fail!");
            e.printStackTrace();
        }
    }

    private void showAvatarAndSaveSmallBitmap(String path) {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " path = " + path);
        try {
            Bitmap bm = PictureUtil.getSmallBitmap(path, PictureUtil.IMAGE_MAX_WIDTH,
                    PictureUtil.IMAGE_MAX_HEIGHT);
            File f = new File(path);
            bm = adjustDegreeIfRotate(bm, f);
            String smallImagePath = saveSmallImage(bm, f);
            int h = mCircleAvatar.getHeight();
            float scaleWidth = h / (float) bm.getWidth();
            float scaleHeigh = h / (float) bm.getHeight();
            Bitmap smallBitmap = BitmapUtills.getScaleBitmap(bm, scaleWidth, scaleHeigh);
            // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
            BitmapUtills.bitmapRecycle(bm);
            mAvatarImagePath = smallImagePath;
            mCircleAvatar.setImageBitmap(smallBitmap);
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName() + " make small image fail!");
            e.printStackTrace();
        }
    }

    private String saveSmallImage(Bitmap bm, File f) throws FileNotFoundException, IOException {
        String smallImagePath = mImageCacheDir + "small_" + f.getName();
        File smallImageFile = new File(smallImagePath);
        if (!smallImageFile.exists()) {
            FileOutputStream fos = new FileOutputStream(smallImageFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
            LogUtils.logd(TAG, LogUtils.getThreadName() + " file name: " + smallImageFile.getName()
                    + " length: " + smallImageFile.length());
        }
        return smallImagePath;
    }

    private Bitmap adjustDegreeIfRotate(Bitmap bm, File f) {
        /**
         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
         */
        int degree = BitmapUtills.readPictureDegree(f.getAbsolutePath());
        /**
         * 把图片旋转为正的方向
         */
        if (degree != 0) {
            Bitmap newbitmap = BitmapUtills.rotaingImageView(degree, bm);
            BitmapUtills.bitmapRecycle(bm);
            bm = newbitmap;
        }
        return bm;
    }

    private void initData() {
        mRequestAction = new RequestAction();
        mCellWidth = (AndroidUtils.getDisplayWidth(this) - AndroidUtils.dip2px(this, 26)) / 4;
        mCellHeight = mCellWidth;
        mImageCacheDir = StorageUtils.getCacheDirectory(this, false) + "/GN_GOU/questionimgcache/";
        LogUtils.logd(TAG, LogUtils.getThreadName() + " image cache dir: ");
        File file = new File(mImageCacheDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (BitmapFileCache.existSDCard()) {
            file = new File(IMAGEPATH);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        private int mBeforeTextLength;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int length = s.length();
            LogUtils.log(TAG, LogUtils.getThreadName() + "length = " + length);
            if (length > MAX_CONTENT_LENGTH) {
                mWordCountRemind.setVisibility(View.VISIBLE);
                mWordCountRemind.setText(getString(R.string.edittext_more_note, length - MAX_CONTENT_LENGTH));
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
            String content = mContent.getText().toString();
            if (!TextUtils.isEmpty(content) && content.length() > MAX_CONTENT_LENGTH
                    && mBeforeTextLength <= MAX_CONTENT_LENGTH) {
                AndroidUtils.showShortToast(AskQuestionActivity.this, R.string.question_length_note);
            }
        }
    };

    private void initView() {
        mAskQuestionTips = (TextView) findViewById(R.id.ask_question_tips);
        mNickName = (EditText) findViewById(R.id.nickname);
        mCircleAvatar = (CircleImageView) findViewById(R.id.portrait);
        mContent = (EditText) findViewById(R.id.content);
        mDescription = (TextView) findViewById(R.id.description);
        mAddDescription = (Button) findViewById(R.id.add_description);
        mImagePanelLayout = (ImagePanelLayout) findViewById(R.id.image_panel_layout);
        mAddPhotoImgView = (ImageView) findViewById(R.id.addPhoto);
        mAddPhotoTv = (TextView) findViewById(R.id.addPhotoTv);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_bar);
        mWordCountRemind = (TextView) findViewById(R.id.text_limit_remind);
        mCircleAvatar.setOnClickListener(this);
        mAddDescription.setOnClickListener(this);
        mNickName.setOnClickListener(this);
        updateAddImagePanel();
        setContent();
        setDescription();
        setNickName();
        setDescriptionButtonText();
    }

    private void setDescriptionButtonText() {
        String description = mDescription.getText().toString();
        if (!TextUtils.isEmpty(description)) {
            mAddDescription.setText(R.string.modify_decription);
        }
    }

    private void updateAddImagePanel() {
        mImagePanelLayout.setmCellHeight(mCellHeight);
        mImagePanelLayout.setmCellWidth(mCellWidth);
        LayoutParams para = mAddPhotoImgView.getLayoutParams();
        para.height = mCellHeight;
        para.width = mCellWidth;
        mAddPhotoImgView.setLayoutParams(para);
    }

    private void setNickName() {
        String avatarUrl = UserInfoManager.getInstance().getAvatar(this);
        String nickname = UserInfoManager.getInstance().getNickName(this);
        boolean isEdit = UserInfoManager.getInstance().isEditEnable(this);
        LogUtils.log(TAG, LogUtils.getThreadName() + " avatarUrl = " + avatarUrl + ", nickname = " + nickname
                + ", isEdit = " + isEdit);
        if (isEdit) {
            mAskQuestionTips.setText(R.string.ask_question_tips);
            mCircleAvatar.setVisibility(View.VISIBLE);
            mNickName.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(avatarUrl)) {
                GNImageLoader.getInstance().loadBitmap(avatarUrl, mCircleAvatar);
            }

        } else {
            mAskQuestionTips.setText(R.string.ask_question_tips2);
            mCircleAvatar.setVisibility(View.GONE);
            mNickName.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(nickname)) {
            mNickName.setText(nickname);
            mNickName.setSelection(nickname.length());
            mContent.requestFocusFromTouch();
        }
    }

    private void setDescription() {
        String content = getIntent().getStringExtra(Constants.INTENT_FLAT);
        if (TextUtils.isEmpty(content)) {
            mDescriptionStr = getDescriptionFromCache();
            if (!TextUtils.isEmpty(mDescriptionStr)) {
                mDescription.setText(mDescriptionStr);
            }
        } else {
            mDescriptionStr = "";
        }
    }

    private void setContent() {
        mContent.addTextChangedListener(mTextWatcher);
        mContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                if (hasFocus) {
                    mContent.setSelection(mContent.getText().toString().length());
                }

            }
        });

        String content = getIntent().getStringExtra(Constants.INTENT_FLAT);
        if (!TextUtils.isEmpty(content)) {
            mContentStr = content;
            mContent.setText(mContentStr);
        } else {
            mContentStr = getContentFromCache();
            if (!TextUtils.isEmpty(mContentStr)) {
                mContent.setText(mContentStr);
            }
        }
    }

    private void initTitleBar() {
        final GNTitleBar titleBar = getTitleBar();
        titleBar.setVisibility(View.VISIBLE);
        titleBar.setTitle(R.string.ask_question);
        titleBar.setRightBtnText(R.string.publish);
        titleBar.setRightBtnVisible(true);
        titleBar.setRightListener(mSubmitBtnListener);
        titleBar.getBackBtn().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                switch (v.getId()) {
                    case R.id.iv_back:
                        StatService.onEvent(AskQuestionActivity.this, BaiduStatConstants.QUESTION_BACK,
                                BaiduStatConstants.TOP_BACK);
                        break;
                    default:
                        break;
                }
                titleBar.onClick(v);
            }
        });
    }

    private OnRightBtnListener mSubmitBtnListener = new OnRightBtnListener() {
        @Override
        public void onClick() {
            LogUtils.log(TAG, LogUtils.getThreadName());
            if (isFastDoubleClick()) {
                return;
            }
            if (mIsSubmitting) {
                return;
            }
            AndroidUtills.hidenKeybord(AskQuestionActivity.this, mContent);
            submitQuestion();
        }
    };

    private void submitQuestion() {
        String nickName = getNickName();
        String content = getContent();
        String description = getDescription();
        boolean nickNameValid = !TextUtils.isEmpty(nickName);
        boolean contentValid = !TextUtils.isEmpty(content);
        boolean netValid = checkNet();
        if (nickNameValid && contentValid && netValid) {
            mProgressBar.setVisibility(View.VISIBLE);
            mNickNameStr = nickName;
            mIsSubmitting = true;
            mRequestAction.submitQuestion(this, null, nickName, mAvatarImagePath, content, description,
                    mPicList, mVerifyCode, this);
            StatService.onEvent(AskQuestionActivity.this, BaiduStatConstants.QUESTION_SEND,
                    QUESTION_SEND_DONE);
        }
    }

    private String getNickName() {
        String nickName = mNickName.getText().toString();
        LogUtils.log(TAG, LogUtils.getThreadName() + nickName);
        if (TextUtils.isEmpty(nickName) || nickName.equals("\n")) {
            Toast.makeText(this, getString(R.string.nick_isnt_empty), Toast.LENGTH_SHORT).show();
            StatService.onEvent(AskQuestionActivity.this, BaiduStatConstants.QUESTION_SEND,
                    QUESTION_SEND_NICK);
            return null;
        }
        if (nickName.length() < MIN_NICK_NAME_SIZE) {
            Toast.makeText(this, getString(R.string.nick_less_note), Toast.LENGTH_SHORT).show();
            StatService.onEvent(AskQuestionActivity.this, BaiduStatConstants.QUESTION_SEND,
                    QUESTION_SEND_NICK);
            return null;
        }
        return nickName;
    }

    private String getContent() {
        String content = mContent.getText().toString();
        LogUtils.log(TAG, LogUtils.getThreadName() + content);
        if (TextUtils.isEmpty(content) || content.equals("\n") || content.length() < MIN_CONTENT_LENGTH) {
            DialogFactory.createQustionContentMinDialog(this).show();
            StatService
                    .onEvent(AskQuestionActivity.this, BaiduStatConstants.QUESTION_SEND, QUESTION_SEND_MIN);
            return null;
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            DialogFactory.createQustionContentMaxDialog(this).show();
            StatService
                    .onEvent(AskQuestionActivity.this, BaiduStatConstants.QUESTION_SEND, QUESTION_SEND_MAX);
            return null;
        }
        return content;
    }

    private String getDescription() {
        String desciption = mDescription.getText().toString();
        LogUtils.log(TAG, LogUtils.getThreadName() + desciption);
        return desciption;
    }

    private boolean checkNet() {
        if (!NetUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.upgrade_error_network_exception),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void gotoAddDescriptionActivity() {
        Intent intent = new Intent(this, AddDescriptionActivity.class);
        intent.putExtra(HttpConstants.Request.QuestionSubmit.CONTENT, getDescription());
        startActivityForResult(intent, Constants.ActivityRequestCode.REQUEST_CODE_ADD_DESCRIPTION);
    }

    private void goToAddImage() {
        int childCount = mImagePanelLayout.getChildCount();
        if (childCount < CHILD_COUNT_MAX) {
            showSelectProgramDialog(R.id.addPhoto);
        } else {
            Toast.makeText(this, getString(R.string.question_images_max_limit), Toast.LENGTH_SHORT).show();
        }
    }

    private void showSelectProgramDialog(int sourceViewId) {
        try {
            if (mDialog == null) {
                mDialog = (GNCustomDialog) DialogFactory.createSelectProgramDialog(this);
            }
            if (mDialog != null) {
                mDialog.show();
                mDialog.setDismissBtnVisible();
                mDialog.setCanceledOnTouchOutside(true);
                View cameraView = mDialog.getContentView().findViewById(R.id.camera);
                cameraView.setOnClickListener(this);
                cameraView.setTag(sourceViewId);
                View galleryView = mDialog.getContentView().findViewById(R.id.gallery);
                galleryView.setOnClickListener(this);
                galleryView.setTag(sourceViewId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getImageFromGallery(int requestCode) {
        Intent intent = new Intent(this, ImageFolderScanActivity.class);
        intent.putExtra(Constants.MAX_TAKE_IMAGES_COUNT, CHILD_COUNT_MAX - mImagePanelLayout.getChildCount());
        startActivityForResult(intent, requestCode);
    }

    private void getImageFromCamera(int requestCode) {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCaptureImageFileName = getCaptureImageFileName();
        Uri imageUri = Uri.fromFile(new File(IMAGEPATH, mCaptureImageFileName));
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, requestCode);
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

    private void allScan(String imageFileName) {
        updateGallery(imageFileName);
    }

    // filename是我们的文件全名，包括后缀哦
    private void updateGallery(String filename) {
        MediaScannerConnection.scanFile(this, new String[] {filename}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        LogUtils.logd(TAG, LogUtils.getThreadName() + " Scanned " + path + ":");
                        LogUtils.logd(TAG, LogUtils.getThreadName() + "-> uri=" + uri);
                    }
                });
    }

    private void showSaveDataDialog() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        try {
            if (mSavedDataDialog == null) {
                mSavedDataDialog = (GNCustomDialog) DialogFactory
                        .createSaveDataDialogWhenQuestionSubmit(this);
            }
            if (mSavedDataDialog != null) {
                mSavedDataDialog.show();
                mSavedDataDialog.setDismissBtnVisible();
                mSavedDataDialog.setCanceledOnTouchOutside(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        saveContent();
        saveDescription();
    }

    private void saveContent() {
        String content = mContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ShareDataManager.removeReferece(this, QUESTION_CONTENT);
        } else if (!content.equals(mContentStr)) {
            ShareDataManager.putString(this, QUESTION_CONTENT, content);
        }
    }

    private void clearContent() {
        ShareDataManager.removeReferece(this, QUESTION_CONTENT);
    }

    private void saveDescription() {
        String description = mDescription.getText().toString();
        if (TextUtils.isEmpty(description)) {
            ShareDataManager.removeReferece(this, QUESTION_DESCRIPTION);
        } else if (!description.equals(mDescriptionStr)) {
            ShareDataManager.putString(this, QUESTION_DESCRIPTION, description);
        }
    }

    private void clearDescription() {
        ShareDataManager.removeReferece(this, QUESTION_DESCRIPTION);
    }

    private String getContentFromCache() {
        String savedContent = ShareDataManager.getString(this, QUESTION_CONTENT, "");
        return savedContent;
    }

    private String getDescriptionFromCache() {
        String savedDescription = ShareDataManager.getString(this, QUESTION_DESCRIPTION, "");
        return savedDescription;
    }

    private static class ImageInfo {
        private View mView;
        private String mFilePath;

        public ImageInfo(View view, String filePath) {
            super();
            this.mView = view;
            this.mFilePath = filePath;
        }

        public View getView() {
            return mView;
        }

        public String getFilePath() {
            return mFilePath;
        }

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
