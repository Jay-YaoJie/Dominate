package com.jeff.dominate.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.jeff.dominate.R;


public final class CameraCaptureView extends View {

    private final Paint mPaint;
    private final int mMaskColor;

    private Bitmap mBmpTopLeft;
    private Bitmap mBmpTopRight;
    private Bitmap mBmpBottomLeft;
    private Bitmap mBmpBottomRight;

    private int scale = 60;

    public CameraCaptureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Resources resources = getResources();
        mMaskColor = 0xAA525252;
        // cache images
        mBmpTopLeft = BitmapFactory.decodeResource(resources, R.mipmap.scan_corner_top_left);
        mBmpTopRight = BitmapFactory.decodeResource(resources, R.mipmap.scan_corner_top_right);
        mBmpBottomLeft = BitmapFactory.decodeResource(resources, R.mipmap.scan_corner_bottom_left);
        mBmpBottomRight = BitmapFactory.decodeResource(resources, R.mipmap.scan_corner_bottom_right);
    }

    @Override
    public void onDraw(Canvas canvas) {
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();
        final int wh = width > height ? height : width;
        final int boxLength = wh * scale / 100;
        final int left = (width - boxLength) / 2;
        final int top = (height - boxLength) / 2;
        final int right = left + boxLength;
        final int bottom = top + boxLength;
        final Rect frame = new Rect(left, top, right, bottom);
        canvas.save();
        canvas.clipRect(frame, Region.Op.XOR);
        canvas.drawColor(mMaskColor);
        canvas.restore();
        canvas.save();
        drawEdges(canvas, frame);
        canvas.restore();
    }

    private void drawEdges(Canvas canvas, Rect box) {
        mPaint.setColor(Color.WHITE);
        final float _x = box.right - mBmpTopRight.getWidth();
        final float _y = box.bottom - mBmpBottomLeft.getHeight();
        canvas.drawBitmap(mBmpTopLeft, box.left, box.top, mPaint);
        canvas.drawBitmap(mBmpTopRight, _x, box.top, mPaint);
        canvas.drawBitmap(mBmpBottomLeft, box.left, _y, mPaint);
        canvas.drawBitmap(mBmpBottomRight, _x, _y, mPaint);
    }
}
