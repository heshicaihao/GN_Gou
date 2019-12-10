/*
 * ViewArea.java
 * classes : com.gionee.client.view.widget.ViewArea
 * @author yuwei
 * 
 * Create at 2015-4-2 下午7:57:14
 */
package com.gionee.client.view.widget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gionee.client.R;
import com.gionee.client.activity.question.ClipPhotoActivity;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.framework.operation.business.StatisticsBuisiness;
import com.gionee.framework.operation.utills.BitmapUtills;
import com.gionee.framework.operation.utills.PictureUtil;
import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;

/**
 * com.gionee.client.view.widget.ViewArea
 * 
 * @author yuwei <br/>
 *         create at 2015-4-2 下午7:57:14
 * @description
 */
public class ViewArea extends FrameLayout {
    private int imgDisplayW;
    private int imgDisplayH;
    private int imgW;
    private int imgH;
    private TouchView touchView;
    private DisplayMetrics dm;
    private static final int BUFFER_SIZE = 32 * 1024;
    private String mFilePath;
    private Context mContext;
    private Bitmap mHeadBitmap;
    private String mPicSuffix;

    // resId为图片资源id
    public ViewArea(Context context, final String filePath) {

        super(context);
        dm = new DisplayMetrics();
        mContext = context;
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        imgDisplayW = dm.widthPixels;
        imgDisplayH = dm.heightPixels - AndroidUtils.dip2px(context, 60);
        mFilePath = filePath;
        mPicSuffix = mFilePath.substring(mFilePath.lastIndexOf(".") + 1, mFilePath.length());
        touchView = new TouchView(context);
        ((ClipPhotoActivity) context).showLoading();
        loadBitmap(filePath);

    }

    public void loadBitmap(final String filePath) {
        StatisticsBuisiness.getInstance().getHandler().postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    Message msg = mHander.obtainMessage();
                    msg.arg1 = 0;
                    try {
                        CompressFormat format = CompressFormat.JPEG;
                        if (mPicSuffix.equals("png")) {
                            format = CompressFormat.PNG;
                        } else {
                            format = CompressFormat.JPEG;
                        }
                        LogUtils.log("clip_picture", "path=" + filePath);
                        mHeadBitmap = PictureUtil.getSmallBitmap(filePath, imgDisplayW, imgDisplayH, 2048,
                                format);
                        mHeadBitmap = adjustDegreeIfRotate(mHeadBitmap, new File(mFilePath));
                        LogUtils.log("clip_picture", "mHeadBitmap size=" + mHeadBitmap.getWidth());
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg.arg1 = 1;
                    }
                    mHander.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 200);
    }

    public void initRect(int left, int top, int right, int bottom) {
        touchView.setmBorderTop(top);
        touchView.setmBorderBottom(bottom);
        touchView.setmBorderLeft(left);
        touchView.setmBorderRight(right);

    }

    public InputStream getFileStream(final String filePath) {
        BufferedInputStream imageStream;
        try {
            imageStream = new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE);
            InputStream inputStream = new ContentLengthInputStream(imageStream,
                    (int) new File(filePath).length());
            return inputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Handler mHander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            try {
                if (msg.arg1 == 0) {

                    Bitmap loadedImage = mHeadBitmap;
                    touchView.setImageBitmap(loadedImage);
                    imgW = loadedImage.getWidth();
                    imgH = loadedImage.getHeight();
                    computeLayoutSize();
                    ViewArea.this.addView(touchView);
                    invalidate();
                    ((ClipPhotoActivity) mContext).hideProgress();
                    LogUtils.log("clip_picture", "mHeadBitmap size=" + mHeadBitmap.getWidth());
                } else {
                    showError();
                    ((ClipPhotoActivity) mContext).hideProgress();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError();
            }
        }

        public void showError() {
            Toast.makeText(mContext, R.string.pic_loading_error, Toast.LENGTH_SHORT).show();
            ((ClipPhotoActivity) mContext).hideProgress();
        }

        public void computeLayoutSize() {
            int layout_w = imgW > imgDisplayW ? imgDisplayW : imgW;
            int layout_h = imgH > imgDisplayH ? imgDisplayH : imgH;
            LogUtils.log("clip_picture", "touchViewBorderTop3=" + touchView.getmBorderTop());

            if (imgW >= imgH) {
                if (layout_w == imgDisplayW) {
                    layout_h = (int) (imgH * ((float) imgDisplayW / imgW));
                }
            } else {
                if (layout_h == imgDisplayH) {
                    layout_w = (int) (imgW * ((float) imgDisplayH / imgH));
                }
            }
            if (touchView.getmBorderTop() != 0) {

                int clipHeight = touchView.getmBorderBottom() - touchView.getmBorderTop();
                int clipWidth = touchView.getmBorderRight() - touchView.getmBorderLeft();
                layout_w = layout_w < clipWidth ? clipWidth : layout_w;
                layout_h = layout_h < clipHeight ? clipHeight : layout_h;
                if (imgW <= imgH) {
                    if (layout_w == clipWidth) {
                        layout_h = (int) (imgH * ((float) clipWidth / imgW));
                    }
                } else {
                    if (layout_h == clipHeight) {
                        layout_w = (int) (imgW * ((float) clipHeight / imgH));
                    }
                }
                LogUtils.log("clip_picture", "layout_w=" + layout_w + "clipWidht=" + clipWidth);
            }
            touchView.setLayoutParams(new FrameLayout.LayoutParams(layout_w, layout_h, Gravity.CENTER));
        };
    };

    private Bitmap adjustDegreeIfRotate(Bitmap bm, File f) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    public TouchView getTouchView() {
        return touchView;
    }
}
