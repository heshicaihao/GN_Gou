/*
 * ClipPhotoActivity.java
 * classes : com.gionee.client.activity.question.ClipPhotoActivity
 * @author yuwei
 * 
 * Create at 2015-4-2 下午8:10:21
 */
package com.gionee.client.activity.question;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.persistent.ShareDataManager;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.view.widget.ViewArea;
import com.gionee.framework.operation.business.StatisticsBuisiness;
import com.gionee.framework.operation.net.GNImageLoader;

/**
 * com.gionee.client.activity.question.ClipPhotoActivity
 * 
 * @author yuwei <br/>
 *         create at 2015-4-2 下午8:10:21
 * @description 图片裁剪页面
 */
@SuppressLint("NewApi")
public class ClipPhotoActivity extends BaseFragmentActivity implements OnClickListener {
    private LinearLayout mImageContainer;
    private ImageView mClipCircle;
    private float mClipTop;
    private float mClipLeft;
    private float mClipRight;
    private float mClipBottom;
    private static final String TAG = "clip_picture";
    private ViewArea mViewArea;
    private float mZoomOutPixel;
    private float mZoomInPixel;
    private String CLIP_LEFT = "clip_left";
    private String CLIP_TOP = "clip_top";
    private String CLIP_RIGHT = "clip_right";
    private String CLIP_BOTTOM = "clip_bottom";
    private ProgressBar mProgress;
    private TextView mClipCertainBtn;
    private int mTop;
    private int mBottom;
    private int mLeft;
    private int mRight;

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slip_head_img);
        initView();
        initClipRect();
        if (!TextUtils.isEmpty(getIntent().getStringExtra(Constants.INTENT_FLAT))) {
            addImageViewArea();
        } else {
            finish();
        }

    }

    public void initView() {
        mImageContainer = (LinearLayout) findViewById(R.id.ll_viewArea);
        mClipCircle = (ImageView) findViewById(R.id.clip_circle);
        mProgress = (ProgressBar) findViewById(R.id.loading_progress);
        mClipCertainBtn = (TextView) findViewById(R.id.clip_certain);
    }

    public void addImageViewArea() {
        mImageContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                LayoutParams parm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                mViewArea = new ViewArea(ClipPhotoActivity.this, getIntent().getStringExtra(
                        Constants.INTENT_FLAT));
                initTouchBorder();
                mImageContainer.addView(mViewArea, parm);
            }
        }, 200);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        GNImageLoader.getInstance().init(this);
    }

    public void showLoading() {
        mProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    public void initClipRect() {
        try {
            getClipAreaFromCache();
            if (mClipLeft == 0) {

                StatisticsBuisiness.getInstance().getHandler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        LogUtils.log(TAG, "mClipCircleWidth=" + mClipCircle.getWidth());
                        Bitmap bitmap = ((BitmapDrawable) (mClipCircle.getDrawable())).getBitmap();
                        LogUtils.log(TAG, "bitmapWidth=" + bitmap.getWidth());
                        mZoomOutPixel = (float) mClipCircle.getWidth() / (float) bitmap.getWidth();
                        List<Point> mPointList = new ArrayList<Point>();
                        getTransparentPointList(bitmap, mPointList);
                        getGapOfTranparentArea(mPointList);
                        bitmapToPixel();
                        saveClipArea();
                    }

                }, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getClipAreaFromCache() {
        mClipLeft = ShareDataManager.getFloat(this, CLIP_LEFT, 0f);
        mClipRight = ShareDataManager.getFloat(this, CLIP_RIGHT, 0);
        mClipBottom = ShareDataManager.getFloat(this, CLIP_BOTTOM, 0);
        mClipTop = ShareDataManager.getFloat(this, CLIP_TOP, 0);

    }

    public void initTouchBorder() {
        if (mClipLeft == 0) {
            return;
        }
        int clipWidth = (int) (mClipRight - mClipLeft);
        int clipHeight = (int) (mClipBottom - mClipTop);
        mTop = (mImageContainer.getHeight() - clipHeight) / 2 - 2;
        mBottom = (mImageContainer.getHeight() + clipHeight) / 2 + 2;
        mLeft = (mImageContainer.getWidth() - clipWidth) / 2;
        mRight = (mImageContainer.getWidth() + clipWidth) / 2;
        mViewArea.initRect(mLeft, mTop, mRight, mBottom);

    }

    public void getTransparentPointList(Bitmap bitmap, List<Point> mPointList) {
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                if (bitmap.getPixel(i, j) == 0) {
                    Point point = new Point(i, j);
                    mPointList.add(point);
                }
            }
        }
    }

    public void bitmapToPixel() {
        mClipLeft *= mZoomOutPixel;
        mClipRight *= mZoomOutPixel;
        mClipBottom *= mZoomOutPixel;
        mClipTop *= mZoomOutPixel;
    }

    public void getGapOfTranparentArea(List<Point> mPointList) {
        for (int i = 0; i < mPointList.size(); i++) {
            Point point = mPointList.get(i);
            int tempX = point.x;
            int tempY = point.y;
            if (i == 0) {
                mClipBottom = mClipTop = tempY;
                mClipLeft = mClipRight = tempX;
            }
            if (tempX > mClipRight) {
                mClipRight = tempX;
            }
            if (tempX < mClipLeft) {
                mClipLeft = tempX;
            }
            if (tempY > mClipBottom) {
                mClipBottom = tempY;
            }
            if (tempY < mClipTop) {
                mClipTop = tempY;
            }
        }

    }

    public void saveClipArea() {
        ShareDataManager.putFloat(this, CLIP_LEFT, mClipLeft);
        ShareDataManager.putFloat(this, CLIP_RIGHT, mClipRight);
        ShareDataManager.putFloat(this, CLIP_TOP, mClipTop);
        ShareDataManager.putFloat(this, CLIP_BOTTOM, mClipBottom);
        initTouchBorder();
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.clip_cancel:
                finish();
                break;

            case R.id.clip_certain:
                clipBitmap();
                break;

            default:
                break;
        }

    }

    public void clipBitmap() {
        mClipCertainBtn.setEnabled(false);
//        mCertainProgress.setVisibility(View.VISIBLE);
        StatisticsBuisiness.getInstance().postRunable(new Runnable() {

            @Override
            public void run() {
                try {
                    Bitmap originalBmp = ((BitmapDrawable) mViewArea.getTouchView().getDrawable())
                            .getBitmap();
                    mZoomInPixel = (float) originalBmp.getWidth()
                            / (float) mViewArea.getTouchView().getWidth();
                    int destinationBmpLeft = (int) ((mLeft - mViewArea.getTouchView().getLeft()) * mZoomInPixel);
                    int destinationBmpTop = (int) ((mTop - mViewArea.getTouchView().getTop()) * mZoomInPixel);
                    int destinationBmtWidth = (int) ((mClipRight - mClipLeft) * mZoomInPixel);
                    int destinationBmpHeight = (int) ((mClipBottom - mClipTop) * mZoomInPixel);
                    Bitmap newBmp = Bitmap.createBitmap(originalBmp, destinationBmpLeft, destinationBmpTop,
                            destinationBmtWidth, destinationBmpHeight);

                    saveHeadImg(newBmp);
                    mHandler.sendMessage(mHandler.obtainMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    notifyError();

                }
            }

        });
    }

    public void notifyError() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mClipCertainBtn.setEnabled(true);
                Toast.makeText(ClipPhotoActivity.this, R.string.select_suitable_pic, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            mClipCertainBtn.setEnabled(true);
            setResult(Constants.ActivityResultCode.RESULT_CODE_IAMGE_CLIP);
            finish();
        };
    };

    private int getTopAndBottomBarHeight() {
        LogUtils.log(TAG, "notifybar height=" + AndroidUtils.getNotifyBarHeight(this)
                + "navigationbar height=" + AndroidUtils.getNavigationBarHeight(this));
        if (AndroidUtils.getNavigationBarHeight(this) == 0) {
            return AndroidUtils.getNotifyBarHeight(this);
        } else {
            return AndroidUtils.getNavigationBarHeight(this);
        }
    }

    public void saveHeadImg(Bitmap newBmp) {
        try {
            String path = getFilesDir() + Constants.USER_HEAD_LOCAL_DEFUALT_PATH;
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream stream = openFileOutput(Constants.USER_HEAD_LOCAL_DEFUALT_PATH, 0);
            newBmp.compress(CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

        } catch (Exception e) {
            LogUtils.log(TAG, e.getMessage());
        }
    }

}
