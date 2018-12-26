package com.jeff.dominate.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jeff.dominate.TelinkLightApplication;
import com.telink.bluetooth.TelinkLog;


import java.io.UnsupportedEncodingException;


/**
 * 二维码生成器
 * Created by kee on 2016/9/14.
 */
public class QRCodeGenerator extends AsyncTask<Void, Void, Bitmap> {
    private QREncoder mEncoder;
    private Bitmap mResult;
    // 任务执行成功
    public final static int QRCode_Generator_Success = 1;
    // 任务执行失败
    public final static int QRCode_Generator_Fail = 2;
    private Handler mHandler;


    public QRCodeGenerator(Handler handler) {
        super();
        mResult = null;
        initEncoder();
        this.mHandler = handler;
    }

    private void initEncoder() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) TelinkLightApplication.getApp().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int size = (int) metrics.density * 350;
        QREncoder.Builder builder = new QREncoder.Builder();
        builder.setBackground(0xFFFFFFFF);
        builder.setCodeColor(0xFF000000);
        builder.setCharset(GZIP.GZIP_ENCODE);
        builder.setWidth(size);
        builder.setHeight(size);
//        builder.setPadding(2);
        builder.setLevel(ErrorCorrectionLevel.L);
        mEncoder = builder.build();

    }

    public Bitmap getResult() {
        return mResult;
    }

    public void clear() {
        this.mResult = null;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
//        QRCodeDataOperator provider = QRCodeDataOperator.getInstance();
//        String src = provider.provide();
//        JSONObject jsonObject = new JSONObject();
        QRCodeDataOperator dataProvider = new QRCodeDataOperator();
        String src = dataProvider.provideStr();
        if (src == null) {
            mHandler.sendEmptyMessage(QRCode_Generator_Fail);
            return null;
        }
//        String src = "www.12306.com";
        TelinkLog.w("原始数据: " + src + " 共" + src.getBytes().length + "字节");
        src = GZIP.compressed(src);
        try {
            src = GZIP.bytesToHexString(src.getBytes(GZIP.GZIP_ENCODE));
            TelinkLog.w("压缩后的数据: " + src + " 共" + src.getBytes(GZIP.GZIP_ENCODE).length + "字节");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(QRCode_Generator_Fail);
            return null;
        }
        try {
            mResult = mEncoder.encode(src);
        } catch (WriterException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(QRCode_Generator_Fail);
        }
        return mResult;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        this.mResult = bitmap;
        mHandler.sendEmptyMessage(QRCode_Generator_Success);
    }
}
