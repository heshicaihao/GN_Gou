package com.gionee.client.activity.scan;

import java.io.IOException;
import java.util.Vector;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.business.zxing.camera.CameraManager;
import com.gionee.client.business.zxing.decoding.CaptureActivityHandler;
import com.gionee.client.business.zxing.decoding.InactivityTimer;
import com.gionee.client.business.zxing.view.ViewfinderView;
import com.gionee.client.view.widget.GNCustomDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

public class ScannerActivity extends BaseFragmentActivity implements Callback {
    private static final String TAG = "Scanner_Activity";
    private static final long VIBRATE_DURATION = 200L;
    private CaptureActivityHandler mCaptureActivityHandler;
    private ViewfinderView mViewfinderView;
    private boolean mHasSurface;
    private Vector<BarcodeFormat> mDecodeFormats;
    private String mCharacterSet;
    private InactivityTimer mInactivityTimer;
    private MediaPlayer mMediaPlayer;
    private boolean mPlayBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean mVibrate;
    private boolean mIsShowNoCameraDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_page);
        CameraManager.init(getApplication());
        initView();
        showTitleBar(true);
        getTitleBar().setTitle(R.string.scanner);
        mHasSurface = false;
        mInactivityTimer = new InactivityTimer(this);
    }

    private void initView() {
        mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
        if (mIsShowNoCameraDialog) {
            return;
        }
        startScanner();
    }

    @SuppressWarnings("deprecation")
    private void startScanner() {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (mHasSurface) {
            initCamera(surfaceHolder);
            LogUtils.log(TAG, "CameraManager.get().isOpen():" + CameraManager.get().isOpen());
            if (!CameraManager.get().isOpen()) {
                showNoCameraDialog();
            }
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mDecodeFormats = null;
        mCharacterSet = null;

        mPlayBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            mPlayBeep = false;
        }
        initBeepSound();
        mVibrate = true;
    }

    private void initBeepSound() {
        if (mPlayBeep && mMediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mBeepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception ioe) {
            LogUtils.log(TAG, "E:" + ioe);
            showNoCameraDialog();
            return;
        }
        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = new CaptureActivityHandler(this, mDecodeFormats, mCharacterSet);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        resetHandler();
        CameraManager.get().closeDriver();
        StatService.onPause(this);
    }

    private void resetHandler() {
        if (mCaptureActivityHandler != null) {
            mCaptureActivityHandler.quitSynchronously();
            mCaptureActivityHandler = null;
        }
    }

    @Override
    protected void onDestroy() {
        mInactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(Result result, Bitmap barcode) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        LogUtils.log(TAG, "result:" + resultString);
        if (resultString.equals("")) {
            Toast.makeText(ScannerActivity.this, R.string.scan_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        if (resultString.startsWith("http")) {
            showScannerDialog(resultString);
        } else {
            Toast.makeText(ScannerActivity.this, R.string.scan_failed, Toast.LENGTH_SHORT).show();
            restartPreviewAfterDelay(0l);
        }
    }

    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return mViewfinderView;
    }

    public Handler getHandler() {
        return mCaptureActivityHandler;
    }

    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();

    }

    private final OnCompletionListener mBeepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void showScannerDialog(final String result) {

        GNCustomDialog dialog = new GNCustomDialog(this);
        dialog.setTitle(R.string.friendly_notify);
        if (result.endsWith(".apk")) {
            dialog.setMessage(R.string.scan_download_tips);
        } else {
            dialog.setMessage(R.string.scan_tips);
        }
        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartPreviewAfterDelay(0l);
            }
        });
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                openWebView(result);
            }

        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showNoCameraDialog() {
        if (mIsShowNoCameraDialog) {
            return;
        }
        GNCustomDialog dialog = new GNCustomDialog(this);
        dialog.setTitle(R.string.friendly_notify);
        dialog.setMessage(R.string.no_camera_info);
        dialog.setCancelable(false);
        dialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
        mIsShowNoCameraDialog = true;
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (mCaptureActivityHandler != null) {
            mCaptureActivityHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
            return;
        }
        resetHandler();
        startScanner();
    }

    private void openWebView(String result) {

        Intent intent = new Intent();
        intent.setClass(this, ScanResultActivity.class);
        intent.putExtra("url", result);
        startActivity(intent);

    }
}
