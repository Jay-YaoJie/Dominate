package com.telink.bluetooth.light.qrcode.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;

import com.telink.bluetooth.TelinkLog;

import java.util.concurrent.atomic.AtomicInteger;

public class FocusManager {

    private static final String TAG = FocusManager.class.getSimpleName();

    private final AtomicInteger mPeriod = new AtomicInteger(0);

    private final Handler mFocusHandler = new Handler(Looper.getMainLooper());

    private AutoFocusTask mAutoFocusTask;

    private Camera.AutoFocusCallback mAutoFocusCallback;
    private boolean mEnabledAutoFocus;

    public void requestAutoFocus(Camera camera, Camera.AutoFocusCallback cb) {
        camera.autoFocus(cb);
    }

    public void startAutoFocus(Camera camera) {
        final String mode = camera.getParameters().getFocusMode();
        if (Camera.Parameters.FOCUS_MODE_AUTO.equals(mode) || Camera.Parameters.FOCUS_MODE_MACRO.equals(mode)) {
            // Remove pre task
            if (mAutoFocusTask != null) {
                mFocusHandler.removeCallbacks(mAutoFocusTask);
            }
            mAutoFocusTask = new AutoFocusTask(camera, mAutoFocusCallback);
            mFocusHandler.post(mAutoFocusTask);
        }
    }

    public void setAutoFocus(int ms, Camera.AutoFocusCallback cb) {
        TelinkLog.d(TAG + "- setAutoFocus:" + ms + ", " + cb);
        if (ms < 100) {
            throw new IllegalArgumentException("Auto Focus period time must more than 100ms !");
        }
        mPeriod.set(ms);
        mAutoFocusCallback = cb;
        mEnabledAutoFocus = cb != null;
    }

    public void stopAutoFocus(Camera camera) {
        mEnabledAutoFocus = false;
        mFocusHandler.removeCallbacks(mAutoFocusTask);

        if (camera != null)
            camera.cancelAutoFocus();
    }

    public boolean isAutoFocusEnabled() {
        return mEnabledAutoFocus;
    }

    private class AutoFocusTask implements Runnable {

        private final Camera mCamera;
        private final Camera.AutoFocusCallback mAutoFocusCallback;

        private AutoFocusTask(Camera camera, Camera.AutoFocusCallback cb) {
            mCamera = camera;
            mAutoFocusCallback = cb;
        }

        @Override
        public void run() {
            requestAutoFocus(mCamera, mAutoFocusCallback);
            final int period = mPeriod.get();
            if (period > 0) {
                mFocusHandler.postDelayed(mAutoFocusTask, period);
            }
        }
    }
}
