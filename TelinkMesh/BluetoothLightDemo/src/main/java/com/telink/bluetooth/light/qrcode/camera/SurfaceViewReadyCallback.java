package com.telink.bluetooth.light.qrcode.camera;

import android.view.SurfaceHolder;


public abstract class SurfaceViewReadyCallback implements SurfaceHolder.Callback {

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
