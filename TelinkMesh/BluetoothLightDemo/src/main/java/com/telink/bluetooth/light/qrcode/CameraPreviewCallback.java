package com.telink.bluetooth.light.qrcode;

import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.telink.bluetooth.TelinkLog;

import java.io.UnsupportedEncodingException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public final class CameraPreviewCallback implements Camera.PreviewCallback {

    public static final String TAG = CameraPreviewCallback.class.getSimpleName();

    public static final int MESSAGE_POST_PROGRESS = 1;
    public static final int MESSAGE_POST_RESULT = 2;
    public static final int MESSAGE_POST_DATA_ERROR = 3;
    private final Queue<TaskContext> mTasks = new ConcurrentLinkedQueue<>();
    private final DecodeThread mDecodeThread = new DecodeThread();
    private QRDecoder mDecoder;
    //    private Map<Integer, JsonPacket> packets = new HashMap<>();
    //    private int mTotalPacket;
    //    private int mTotalItem;
    private String mResult;
    private Handler mHandler;
    private Rect mCropRect;
    private long lastUpdateTime;

    public CameraPreviewCallback() {
        QRDecoder.Builder builder = new QRDecoder.Builder();
        builder.setCharset(GZIP.GZIP_ENCODE);
        this.mDecoder = builder.build();
        this.mDecodeThread.start();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if (this.mDecoder == null || this.mCropRect == null)
            return;

        long currentTime = System.currentTimeMillis();
        TelinkLog.d(TAG + "- onPreviewFrame : " + (currentTime - lastUpdateTime) / 1000);
        lastUpdateTime = currentTime;

        if (this.mTasks.size() == 0) {
            this.post(mDecoder, data, camera, mCropRect);
        }
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void setCropRect(Rect mCropRect) {
        this.mCropRect = mCropRect;
    }

    public String getResult() {
        return this.mResult;
    }

    /*public int getTotalPacket() {
        return mTotalPacket;
    }

    public int getTotalItem() {
        return mTotalItem;
    }*/

    public void start() {
        this.mDecodeThread.awake();
    }

    public void stop() {
        this.mDecodeThread.pause();
    }

    public void clear() {
        this.mDecodeThread.cancel();
        this.mHandler = null;
        this.mTasks.clear();
//        this.clearPackets();
        this.mResult = null;
    }

   /* public void clearPackets() {
        this.packets.clear();
        this.mTotalItem = 0;
        this.mTotalPacket = 0;
    }*/

    /*private boolean isCompleted() {
        Map<Integer, JsonPacket> packets = this.getPackets();
        int size = packets.size();
        return size >= this.getTotalPacket();
    }*/

    private void post(QRDecoder decoder, byte[] data, Camera camera, Rect cropRect) {
        TaskContext context = new TaskContext();
        context.decoder = decoder;
        context.camera = camera;
        context.data = data;
        context.cropRect = cropRect;
        this.mTasks.add(context);
    }

    private void process() throws FormatException, ChecksumException, NotFoundException, UnsupportedEncodingException {
        if (mDecodeThread == null || mDecodeThread.isPause()) {
            return;
        }

        TaskContext taskContext = this.mTasks.poll();

        if (taskContext == null)
            return;

        QRDecoder decoder = taskContext.decoder;
        LuminanceSource source = CameraCapture.capture(taskContext.data, taskContext.camera, taskContext.cropRect);
        Result result = decoder.decode(source);
        mResult = result.getText();

//        TelinkLog.d("RAW :" + Arrays.bytesToHexString(result.getRawBytes(), ":"));  // 转成16进制
        TelinkLog.w("Content 解压前: " + mResult + "  " + mResult.getBytes(GZIP.GZIP_ENCODE).length);
        mResult = GZIP.hexStringToBytes(mResult);
        mResult = GZIP.decompressed(mResult);
        TelinkLog.w("Content 解压后: " + mResult + "  " + mResult.getBytes(GZIP.GZIP_ENCODE).length);
//        TelinkLog.w("Content 解压后的二进制: " + mResult + "  " + GZIP.bytesToHexString(mResult.getBytes(GZIP.GZIP_ENCODE)));

        // 检测扫描到的二维码是否符合格式
        if (new QRCodeDataOperator().parseData(mResult)){
            mHandler.sendEmptyMessage(MESSAGE_POST_RESULT);
            mDecodeThread.pause();
        }else {
            mHandler.sendEmptyMessage(MESSAGE_POST_DATA_ERROR);
        }

        /*if (mResult.split("\\+").length < 15) {

        } else {

        }*/
    }

    private static class TaskContext {
        public QRDecoder decoder;
        public byte[] data;
        public Camera camera;
        public Rect cropRect;
    }

    private class DecodeThread extends Thread {

        private AtomicBoolean pause = new AtomicBoolean(false);
        private AtomicBoolean cancel = new AtomicBoolean(false);

        public boolean isPause() {
            return pause.get();
        }

        public void pause() {
            this.pause.set(true);
        }

        public void awake() {
            this.pause.set(false);
        }

        public boolean isCancel() {
            return cancel.get();
        }

        public void cancel() {
            this.cancel.set(true);
        }

        @Override
        public void run() {

            while (!isCancel()) {

                if (!this.isPause()) {
                    try {
                        process();
                    } catch (Exception e) {
                        TelinkLog.w("", e);
                    }
                }
            }
        }
    }
}
