package com.gionee.client.business.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.gionee.framework.operation.utills.BitmapUtills;

public class AsyncImageLoader {
    private ImageCallback mImageCallback;
    private static AsyncImageLoader mAsyncImageLoader;
    private static final int LOAD_FAILED = 0;
    private static final int LOAD_SUCCESS = 1;
    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case LOAD_FAILED:
                    mImageCallback.imageFailed(msg.getData().getString("imageUrl"));
                    break;
                case LOAD_SUCCESS:
                    mImageCallback.imageSuccess((Bitmap) msg.obj, msg.getData().getString("imageUrl"));
                    break;
                default:
                    break;
            }
        };

    };

    public static synchronized AsyncImageLoader getInstance() {
        if (mAsyncImageLoader == null) {
            mAsyncImageLoader = new AsyncImageLoader();
        }
        return mAsyncImageLoader;
    }

    public void loadBitmap(final String imageUrl, final ImageCallback imageCallback) {
        this.mImageCallback = imageCallback;
        synchronized (AsyncImageLoader.this) {
            new Thread() {
                @Override
                public void run() {
                    Bitmap bitmap = null;
                    bitmap = loadImageFromUrl(imageUrl);
                    if (bitmap == null) {
                        getImageFailed(imageUrl);
                    } else {
                        getImageSuccess(imageUrl, bitmap);
                    }

                }
            }.start();

        }
    }

    private void getImageFailed(final String imageUrl) {
        Message msg = new Message();
        msg.what = LOAD_FAILED;
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", imageUrl);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void getImageSuccess(final String imageUrl, Bitmap drawable) {
        Message msg = new Message();
        msg.what = LOAD_SUCCESS;
        msg.obj = drawable;
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", imageUrl);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private Bitmap loadImageFromUrl(final String imageUrl) {
        URL url = null;
        Bitmap bitmap = null;
        InputStream inputStream = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(imageUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            inputStream = conn.getInputStream();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                bitmap = BitmapFactory.decodeStream(inputStream);
//                bitmap = BitmapUtills.qualityCompress(bitmap, 20 * 1024);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return bitmap;
    }

    public interface ImageCallback {
        public void imageFailed(String imageUrl);

        public void imageSuccess(Bitmap imageDrawable, String imageUrl);
    }

}
