package com.jeff.dominate.qrcode.camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.telink.bluetooth.TelinkLog;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Cameras {

    private final static String TAG = Cameras.class.getSimpleName();

    private final SurfaceView mPreviewSurfaceView;
    private final CameraManager mCameraManager;
    private boolean mIsSurfaceViewReady = false;

    private FocusManager mFocusManager;
    private OneshotTask mPreviewTask = new OneshotTask() {
        @Override
        public void doThis() {
            TelinkLog.d(TAG + "- NOW open camera and start preview...");
            try {
                mCameraManager.attachPreview(mPreviewSurfaceView.getHolder());
                mCameraManager.startPreview();
                if (mFocusManager.isAutoFocusEnabled()) {
                    mFocusManager.startAutoFocus(mCameraManager.getCamera());
                }
            } catch (IOException e) {
                TelinkLog.e(TAG + "- Cannot attach to preview", e);
            }
        }
    };

    private final SurfaceViewReadyCallback mViewReadyCallback = new SurfaceViewReadyCallback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mIsSurfaceViewReady = true;
            TelinkLog.d(TAG + "- Preview SurfaceView NOW ready, open camera by CameraManager");
            mPreviewTask.run();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            super.surfaceDestroyed(holder);
            mIsSurfaceViewReady = false;
            TelinkLog.d(TAG + "- surfaceDestroy");
        }
    };

    public Cameras(SurfaceView previewSurfaceView) {
        mPreviewSurfaceView = previewSurfaceView;
        mCameraManager = new CameraManager(previewSurfaceView.getContext());
        final SurfaceHolder holder = mPreviewSurfaceView.getHolder();
        holder.addCallback(mViewReadyCallback);
        mFocusManager = new FocusManager();
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    public void start() {
        TelinkLog.d(TAG + "- Try open camera and start preview...");
        try {
            if (!mCameraManager.isOpen()) {
                mCameraManager.open();
            }
            mPreviewTask.ready();
            if (mIsSurfaceViewReady) {
                TelinkLog.d(TAG + "- openCameraDirectly");
                mPreviewTask.run();
            }
        } catch (Exception e) {
            TelinkLog.e(TAG + "- Cannot open camera", e);
        }
    }


    public void stop() {
        TelinkLog.d(TAG + "- Try stop preview and close camera...");
        TelinkLog.d(TAG + "- stopCameraDirectly");

        if (mCameraManager.getCamera() != null)
            mCameraManager.getCamera().setPreviewCallback(null);

        mFocusManager.stopAutoFocus(mCameraManager.getCamera());
        mCameraManager.stopPreview();
        try {
            mCameraManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startAutoFocus(int period, Camera.AutoFocusCallback callback) {
        mFocusManager.setAutoFocus(period, callback);
        if (mCameraManager.isOpen() && mFocusManager.isAutoFocusEnabled()) {
            mFocusManager.startAutoFocus(mCameraManager.getCamera());
        }
    }

    static abstract class OneshotTask implements Runnable {
        private AtomicBoolean ready = new AtomicBoolean();

        public void ready() {
            ready.set(true);
        }

        @Override
        public final void run() {
            if (ready.get()) {
                ready.set(false);
                doThis();
            }
        }

        protected abstract void doThis();
    }
}
