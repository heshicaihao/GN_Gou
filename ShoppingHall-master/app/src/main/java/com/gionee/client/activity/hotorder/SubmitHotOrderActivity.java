package com.gionee.client.activity.hotorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.imageScan.ImageFolderScanActivity;
import com.gionee.client.activity.imageScan.ImageScanAndSelectActivity;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.shoppingmall.GNTitleBar;
import com.gionee.client.view.shoppingmall.GNTitleBar.OnRightBtnListener;
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

/**
 * @author yangxiong <br/>
 * @description TODO 晒单提交页
 */
public class SubmitHotOrderActivity extends BaseFragmentActivity implements OnClickListener {
    private static final String TAG = "SubmitHotOrderActivity";
    private static final String IMAGEPATH = FrameWorkInit.SDCARD + "/GN_GOU/hotorderimgcache/";
    private static final int CHILD_COUNT_MIN = 4;
    private static final int CHILD_COUNT_MAX = 11;
    private ImageView mAddPhotoImgView;
    private TextView mAddPhotoTv;
    private EditText mContentEdit;
    private EditText mTitleEdit;
    private TextView mNickName;
    private ImagePanelLayout mAddImageLayout;
    private ArrayList<String> mPicList = new ArrayList<String>();
    private ArrayList<String> mDeletedPicList = new ArrayList<String>();
    private String mOrderId;
    private String mHotOrderId;
    private GNCustomDialog mSavedDataDialog;
    private GNCustomDialog mAbandonEditDialog;
    private String mInitNickName = "";
    private int mCellWidth;
    private int mCellHeight;
    private String mInitTitle = "";
    private String mInitcontent = "";
    private int mInitImageCount;
    private String mCaptureImageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_hotorder);
        initData();
        initView();
    }

    private void initData() {
        mCellWidth = (AndroidUtils.getDisplayWidth(this) - AndroidUtils.dip2px(this, 26)) / 4;
        mCellHeight = mCellWidth;
        mOrderId = getIntent().getStringExtra(Constants.ORDER_ID);
        mHotOrderId = getIntent().getStringExtra(Constants.HOT_ORDER_ID);
        LogUtils.log(TAG, LogUtils.getThreadName() + " mOrderId = " + mOrderId + " mHotOrderId = "
                + mHotOrderId);
        if (BitmapFileCache.existSDCard()) {
            File file = new File(IMAGEPATH);
            file.mkdirs();
        }
    }

    private void initView() {
        initTitleBar();
        mAddImageLayout = (ImagePanelLayout) findViewById(R.id.addedImageLayout);
        mAddPhotoTv = (TextView) findViewById(R.id.addPhotoTv);
        mAddPhotoImgView = (ImageView) findViewById(R.id.addPhoto);
        mAddPhotoImgView.setOnClickListener(this);
        mAddImageLayout.setmCellHeight(mCellHeight);
        mAddImageLayout.setmCellWidth(mCellWidth);
        LayoutParams para = mAddPhotoImgView.getLayoutParams();
        para.height = mCellHeight;
        para.width = mCellWidth;
        mAddPhotoImgView.setLayoutParams(para);
        mContentEdit = (EditText) findViewById(R.id.content);
        mTitleEdit = (EditText) findViewById(R.id.title_edit);
        mNickName = (TextView) findViewById(R.id.nickname);
        setViews();
    }

    private void setViews() {
        String title = null;
        String content = null;
        String nickName = getIntent().getStringExtra(Constants.NICK_NAME);
        int imageCount = 0;
        if (!TextUtils.isEmpty(mHotOrderId)) { // 编辑晒单提交
            Intent intent = getIntent();
            title = getTitle(intent);
            content = getContent(intent);
            nickName = getNickName(intent);
            imageCount = setHotOrderImages(imageCount, intent);
        } else { // 新晒单提交
            title = getTitle(title);
            content = getContent(content);
            nickName = getNickName(nickName);
            imageCount = setHotOrderImages();
        }
        mInitImageCount = imageCount;
        setTitle(title);
        setContent(content);
        setNickName(nickName);
    }

    private int setHotOrderImages() {
        int imageCount = ShareDataManager.getDataAsInt(this, "hotorder_img_count_" + mOrderId, 0);
        LogUtils.log(TAG, LogUtils.getThreadName() + " imageCount = " + imageCount);
        if (imageCount > 0) {
            mAddPhotoTv.setVisibility(View.GONE);
        }
        for (int i = 0; i < imageCount; i++) {
            String smallImagePath = ShareDataManager
                    .getString(this, "hotorder_img_" + mOrderId + "_" + i, "");
            if (!TextUtils.isEmpty(smallImagePath)) {
                showThump(smallImagePath);
            }
        }
        return imageCount;
    }

    private String getNickName(String nickName) {
        String savedNickName = ShareDataManager.getString(this, "hotorder_nickname_" + mOrderId, "");
        if (!TextUtils.isEmpty(savedNickName)) {
            nickName = savedNickName;
        }
        return nickName;
    }

    private String getContent(String content) {
        String savedContent = ShareDataManager.getString(this, "hotorder_content_" + mOrderId, "");
        if (!TextUtils.isEmpty(savedContent)) {
            content = savedContent;
        }
        return content;
    }

    private String getTitle(String title) {
        String savedTitle = ShareDataManager.getString(this, "hotorder_title_" + mOrderId, "");
        if (!TextUtils.isEmpty(savedTitle)) {
            title = savedTitle;
        }
        return title;
    }

    private int setHotOrderImages(int imageCount, Intent intent) {
        String imageList = intent.getStringExtra(HttpConstants.Data.ShowHotOrder.IMAGES);
        try {
            LogUtils.log(TAG, LogUtils.getThreadName() + " imageList = " + imageList);
            JSONArray imageArray = new JSONArray(imageList);
            imageCount = imageArray.length();
            if (imageCount > 0) {
                mAddPhotoTv.setVisibility(View.GONE);
            }
            for (int i = 0; i < imageCount; i++) {
                JSONObject imgData = imageArray.optJSONObject(i);
                String imgUrl = imgData.optString(HttpConstants.Data.ShowHotOrder.IMAGE);
                final String imgUrlTag = imgData.optString(HttpConstants.Data.ShowHotOrder.IMAGE_TAG);
                final View rLayout = LayoutInflater.from(this).inflate(R.layout.add_image, null);
                ImageView addedImg = (ImageView) rLayout.findViewById(R.id.addImage);
                LayoutParams para1 = addedImg.getLayoutParams();
                para1.height = mCellHeight;
                para1.width = mCellWidth;
                addedImg.setLayoutParams(para1);
                addImageFromServer(rLayout, imgUrlTag);
                GNImageLoader.getInstance().loadBitmap(imgUrl, addedImg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageCount;
    }

    private String getNickName(Intent intent) {
        String nickName;
        nickName = intent.getStringExtra(HttpConstants.Data.ShowHotOrder.AUTHOR);
        return nickName;
    }

    private String getContent(Intent intent) {
        String content;
        String contentSaved = ShareDataManager.getString(this, "hotorder_content_" + mOrderId, "");
        content = intent.getStringExtra(HttpConstants.Data.ShowHotOrder.CONTENT);
        if (!TextUtils.isEmpty(contentSaved)) {
            content = contentSaved;
        }
        return content;
    }

    private String getTitle(Intent intent) {
        String title = intent.getStringExtra(HttpConstants.Data.ShowHotOrder.TITLE);
        title = getTitle(title);
        return title;
    }

    private void setNickName(String nickName) {
        if (!TextUtils.isEmpty(nickName)) {
            mNickName.setText(nickName);
            mInitNickName = nickName;
        } else {
            mInitNickName = getString(R.string.nickname_default);
            mNickName.setText(mInitNickName);
        }
    }

    private void setContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            mContentEdit.setText(content);
            mInitcontent = content;
        }
    }

    private void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitleEdit.setText(title);
            mTitleEdit.setSelection(title.length());
            mInitTitle = title;
        }
    }

    private OnRightBtnListener mSubmitBtnListener = new OnRightBtnListener() {
        @Override
        public void onClick() {
            LogUtils.log(TAG, LogUtils.getThreadName());
            AndroidUtills.hidenKeybord(SubmitHotOrderActivity.this, mContentEdit);
            submitHotOrder();
            if (TextUtils.isEmpty(mHotOrderId)) {
                StatService.onEvent(SubmitHotOrderActivity.this, BaiduStatConstants.SUBMMIT,
                        BaiduStatConstants.NEW);
            } else {
                StatService.onEvent(SubmitHotOrderActivity.this, BaiduStatConstants.SUBMMIT,
                        BaiduStatConstants.AGAIN);
            }
        }
    };

    private void initTitleBar() {
        GNTitleBar titleBar = getTitleBar();
        titleBar.setTitle(R.string.hotorder);
        titleBar.setVisibility(View.VISIBLE);
        titleBar.setRightBtnText(R.string.submit);
        titleBar.setRightBtnVisible(true);
        titleBar.setRightListener(mSubmitBtnListener);
        ImageView backImageView = titleBar.getBackBtn();
        backImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        switch (v.getId()) {
            case R.id.addPhoto:
                goToAddImage();
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
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (!TextUtils.isEmpty(mHotOrderId)) { // 编辑提交
            showAbandonEditDialog();
        } else { // 新建提交
            if (isNeedSaveData()) {
                showSaveDataDialog();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " requestCode = " + requestCode + ", resultCode = "
                + resultCode);
        switch (requestCode) {
            case Constants.ActivityRequestCode.REQUEST_CODE_HOT_ORDER_GALLERY:
                showThumpFromGallery();
                break;

            case Constants.ActivityRequestCode.REQUEST_CODE_HOT_ORDER_CAMERA:
                if (resultCode == RESULT_OK) {
                    showThumpFromCamera();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private void showThumpFromGallery() {
        mAddPhotoTv.setVisibility(View.GONE);
        MyBean bean = PageCacheManager.LookupPageData(ImageScanAndSelectActivity.class.getName());
        @SuppressWarnings("unchecked")
        List<String> pathList = (List<String>) bean.get(Constants.SELECT_IMAGE_LIST);
        int size = pathList != null ? pathList.size() : 0;
        for (int i = 0; i < size; i++) {
            int count = mAddImageLayout.getChildCount();
            if (i == 9 || count >= CHILD_COUNT_MAX) {
                LogUtils.log(TAG, LogUtils.getThreadName() + " imaged more than 9 pieces");
                Toast.makeText(this, getResources().getString(R.string.images_max_limit_note),
                        Toast.LENGTH_SHORT).show();
                break;
            }
            String path = (String) pathList.get(i);
            showThumpAndSaveSmallBitmap(path);
        }
    }

    private void showThumpAndSaveSmallBitmap(String path) {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " path = " + path);
        try {
            Bitmap bm = PictureUtil.getSmallBitmap(path, 240, 320);
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

    private String saveSmallImage(Bitmap bm, File f) throws FileNotFoundException, IOException {
        String smallImagePath = IMAGEPATH + "small_" + f.getName() + ".img";
        File smallImageFile = new File(smallImagePath);
        if (!smallImageFile.exists()) {
            FileOutputStream fos = new FileOutputStream(smallImageFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
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

    private void showThump(String smallImagePath) {
        LogUtils.logd(TAG, LogUtils.getThreadName() + " smallImagePath = " + smallImagePath);
        try {
            File f = new File(smallImagePath);
            if (!f.exists()) {
                LogUtils.loge(TAG, LogUtils.getThreadName() + " file: " + smallImagePath + " don't exist");
                return;
            }
            Bitmap bm = PictureUtil.getSmallBitmap(smallImagePath, 240, 320);
            LogUtils.logd(TAG, LogUtils.getThreadName() + " small image decode successful");
            float scaleWidth = mCellWidth / (float) bm.getWidth();
            float scaleHeigh = mCellHeight / (float) bm.getHeight();
            Bitmap smallBitmap = BitmapUtills.getScaleBitmap(bm, scaleWidth, scaleHeigh);
            // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
            BitmapUtills.bitmapRecycle(bm);
            mPicList.add(smallImagePath);
            addImage(smallBitmap, smallImagePath);
        } catch (Exception e) {
            LogUtils.loge(TAG, LogUtils.getThreadName() + " make small image fail! exception :" + e);
            e.printStackTrace();
        }
    }

    private void addImage(Bitmap bd, String filePath) {
        LogUtils.log(TAG, LogUtils.getThreadName() + " filePath = " + filePath);
        View rLayout = makeAddedView(bd, filePath);
        int count = mAddImageLayout.getChildCount();
        if (count < CHILD_COUNT_MAX) {
            mAddImageLayout.addView(rLayout, count - 2); // 从尾部添加图片
        } else {
            Toast.makeText(this, getResources().getString(R.string.images_max_limit_note), Toast.LENGTH_SHORT)
                    .show();
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
                mAddImageLayout.removeView(imgInfo.getView());
                mPicList.remove(imgInfo.getFilePath());
            }
        });
        return rLayout;
    }

    private void addImageFromServer(View addImageLayout, String filePath) {
        final ImageView deleteImg = (ImageView) addImageLayout.findViewById(R.id.delete);
        ImageInfo imgInfo = new ImageInfo(addImageLayout, filePath);
        deleteImg.setTag(imgInfo);
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log(TAG, LogUtils.getThreadName());
                ImageInfo imgInfo = (ImageInfo) deleteImg.getTag();
                mAddImageLayout.removeView(imgInfo.getView());
                mDeletedPicList.add(imgInfo.getFilePath());
            }
        });

        int count = mAddImageLayout.getChildCount();
        if (count < CHILD_COUNT_MAX) {
            mAddImageLayout.addView(addImageLayout, count - 2); // 从尾部添加图片
        }
        findViewById(R.id.addImageLayout).invalidate();
    }

    private void goToAddImage() {
        int childCount = mAddImageLayout.getChildCount();
        if (childCount < CHILD_COUNT_MAX) {
            showSelectProgramDialog();
        } else {
            Toast.makeText(this, getString(R.string.images_max_limit_note), Toast.LENGTH_SHORT).show();
        }
    }

    private void submitHotOrder() {
        String title = checkTitle();
        String nickName = checkNickName();
        String content = checkContent();
        boolean titleValid = !TextUtils.isEmpty(title);
        boolean nickNameValid = !TextUtils.isEmpty(nickName);
        boolean contentValid = !TextUtils.isEmpty(content);
        boolean countValid = checkCount();
        boolean netValid = checkNet();
        if (titleValid && nickNameValid && contentValid && countValid && netValid) {
            statisticsNickNameModified(nickName);
            gotoHotOrderResultActivity(title, nickName, content);
        }
    }

    private boolean checkNet() {
        if (!NetUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.upgrade_error_network_exception),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkCount() {
        int childCount = mAddImageLayout.getChildCount();
        LogUtils.log(TAG, LogUtils.getThreadName() + " childCount = " + childCount);
        if (childCount < CHILD_COUNT_MIN) {
            Toast.makeText(this, getResources().getString(R.string.images_min_limit_note), Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (childCount > CHILD_COUNT_MAX) {
            Toast.makeText(this, getResources().getString(R.string.images_max_limit_note), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private String checkContent() {
        String content = mContentEdit.getText().toString();
        if (TextUtils.isEmpty(content) || content.equals("\n") || content.length() < 30) {
            Toast.makeText(this, getString(R.string.content_least_note), Toast.LENGTH_SHORT).show();
            return null;
        }
        return content;
    }

    private String checkNickName() {
        String nickName = mNickName.getText().toString();
        if (TextUtils.isEmpty(nickName) || nickName.equals("\n")) {
            Toast.makeText(this, getString(R.string.nick_isnt_empty), Toast.LENGTH_SHORT).show();
            return null;
        }
        return nickName;
    }

    private String checkTitle() {
        String title = mTitleEdit.getText().toString();
        if (TextUtils.isEmpty(title) || title.equals("\n")) {
            Toast.makeText(this, getString(R.string.dont_write_title), Toast.LENGTH_SHORT).show();
            return null;
        }
        if (title.length() < 2) {
            Toast.makeText(this, getString(R.string.title_least_note), Toast.LENGTH_SHORT).show();
            return null;
        }
        return title;
    }

    private void gotoHotOrderResultActivity(String title, String nickName, String content) {
        Intent intent = new Intent(this, HotOrderResultActivity.class);
        intent.putExtra(Constants.ORDER_ID, mOrderId);
        intent.putExtra(Constants.HOT_ORDER_ID, mHotOrderId);
        intent.putExtra(HttpConstants.Request.SubmitHotOrder.AUTHOR, nickName);
        intent.putExtra(HttpConstants.Request.SubmitHotOrder.TITLE, title);
        intent.putExtra(HttpConstants.Request.SubmitHotOrder.CONTENT, content);
        intent.putStringArrayListExtra(HttpConstants.Request.ImageHotOrder.DELETE_IMAGE, mDeletedPicList);
        intent.putStringArrayListExtra(HttpConstants.Request.ImageHotOrder.IMAGE, mPicList);
        startActivity(intent);
        AndroidUtils.finishActivity(this);
    }

    private void statisticsNickNameModified(String nickName) {
        if (!mInitNickName.equals(nickName)) {
            StatService.onEvent(this, BaiduStatConstants.EDIT_NICKNAME,
                    BaiduStatConstants.EDIT_NICKNAME_SUBMIT);
        }
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(this, ImageFolderScanActivity.class);
        intent.putExtra(Constants.MAX_TAKE_IMAGES_COUNT, CHILD_COUNT_MAX - mAddImageLayout.getChildCount());
        startActivityForResult(intent, Constants.ActivityRequestCode.REQUEST_CODE_HOT_ORDER_GALLERY);
    }

    private void getImageFromCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCaptureImageFileName = getCaptureImageFileName();
        Uri imageUri = Uri.fromFile(new File(IMAGEPATH, mCaptureImageFileName));
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, Constants.ActivityRequestCode.REQUEST_CODE_HOT_ORDER_CAMERA);
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

    private static final class ImageInfo {
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

    private void showSaveDataDialog() {
        try {
            if (mSavedDataDialog == null) {
                mSavedDataDialog = (GNCustomDialog) DialogFactory.createSaveDataDialog(this);
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

    private void showAbandonEditDialog() {
        try {
            if (mAbandonEditDialog == null) {
                mAbandonEditDialog = (GNCustomDialog) DialogFactory.createAbandonEditDialog(this);
            }
            if (mAbandonEditDialog != null) {
                mAbandonEditDialog.show();
                mAbandonEditDialog.setDismissBtnVisible();
                mAbandonEditDialog.setCanceledOnTouchOutside(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNeedSaveData() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        String title = mTitleEdit.getText().toString();
        String content = mContentEdit.getText().toString();
        String nickname = mNickName.getText().toString();
        // 标题、内容、昵称、图片数量都没变化时显示
        if (title.equals(mInitTitle) && content.equals(mInitcontent) && nickname.equals(mInitNickName)
                && mInitImageCount == mAddImageLayout.getChildCount() - 2) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @author yangxiong
     * @description TODO 保存页面数据
     */
    public void saveData() {
        LogUtils.log(TAG, LogUtils.getThreadName() + " mPicList.size() = " + mPicList.size());
        saveTitle();
        saveContent();
        saveNickname();
        savePics();
    }

    private void savePics() {
        if (mPicList.size() > 0) {
            ShareDataManager.saveDataAsInt(this, "hotorder_img_count_" + mOrderId, mPicList.size());
            for (int i = 0; i < mPicList.size(); i++) {
                ShareDataManager.putString(this, "hotorder_img_" + mOrderId + "_" + i, mPicList.get(i));
            }
        } else {
            ShareDataManager.removeReferece(this, "hotorder_img_count_" + mOrderId);
            for (int i = 0; i < mInitImageCount; i++) {
                ShareDataManager.removeReferece(this, "hotorder_img_" + mOrderId + "_" + i);
            }
        }
    }

    private void saveNickname() {
        String nickname = mNickName.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            ShareDataManager.removeReferece(this, "hotorder_nickname_" + mOrderId);
        } else if (!nickname.equals(mInitNickName)) {
            ShareDataManager.putString(this, "hotorder_nickname_" + mOrderId, nickname);
            StatService
                    .onEvent(this, BaiduStatConstants.EDIT_NICKNAME, BaiduStatConstants.EDIT_NICKNAME_SAVE);
        }
    }

    private void saveContent() {
        String content = mContentEdit.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ShareDataManager.removeReferece(this, "hotorder_content_" + mOrderId);
        } else if (!content.equals(mInitcontent)) {
            ShareDataManager.putString(this, "hotorder_content_" + mOrderId, content);
        }
    }

    private void saveTitle() {
        String title = mTitleEdit.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ShareDataManager.removeReferece(this, "hotorder_title_" + mOrderId);
        } else if (!title.equals(mInitTitle)) {
            ShareDataManager.putString(this, "hotorder_title_" + mOrderId, title);
        }
    }
}