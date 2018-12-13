package com.jeff.dominate.qrcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.jeff.dominate.R;
import com.jeff.dominate.TelinkBaseActivity;
import com.jeff.dominate.qrcode.camera.Cameras;


/**
 * 二维码扫描页面
 * 扫描 --> 解析 --> 同步
 *
 * @author kee
 */
public class QRCodeScanActivity extends TelinkBaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 0x12;
    private Cameras mCameras;
    private CameraPreviewCallback mPreviewCallback;

    private int delay = 1000;
    private Handler mDelayHandler = new Handler();
    private Handler mQRHandler = new QRScanMessageHandler(this);
    private Runnable mAutoFocus = new Runnable() {
        @Override
        public void run() {
            startAuto(delay);
        }
    };

    //    private SyncDataTask mSyncDataTask;
    private RelativeLayout scanContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setContentView(R.layout.activity_qrcode_scan);
        this.scanContainer = (RelativeLayout) this.findViewById(R.id.capture_container);
        this.scanContainer.getViewTreeObserver().addOnGlobalLayoutListener(this);
        SurfaceView surfaceView = (SurfaceView) this.findViewById(R.id.capture_preview_view);
//        PacketDecoder.setDefault(new JsonPacketDecoder());
        this.mPreviewCallback = new CameraPreviewCallback();
        this.mPreviewCallback.setHandler(this.mQRHandler);
        this.mCameras = new Cameras(surfaceView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.mPreviewCallback.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissionAndStart();
//        this.mCameras.start();
//        this.mDelayHandler.postDelayed(this.mAutoFocus, this.delay);
//        this.mPreviewCallback.start();
    }

    private void checkPermissionAndStart(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            restartCamera();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                restartCamera();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
            }
        }
    }

    private void restartCamera() {
        mCameras.start();
        mDelayHandler.postDelayed(this.mAutoFocus, this.delay);
        mPreviewCallback.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        this.mCameras.stop();
        this.mDelayHandler.removeCallbacks(this.mAutoFocus);
        this.mPreviewCallback.stop();
        //
        /*Places.getInstance().clear();
        List<PlaceSort> placeSortList = PlacesDbUtils.getInstance().getAllPlace();
        if (placeSortList != null && placeSortList.size() > 0) {
            PlaceManage.changePlace(placeSortList.get(0));
        }*/
        //PlaceManage.changeToNoUser(null);
    }

    private void startAuto(int period) {
        this.mCameras.startAutoFocus(period, new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    camera.setOneShotPreviewCallback(mPreviewCallback);
                }
            }
        });
    }

    private Rect getCropRect(Point cameraResolution, int scale) {

        int cameraWidth = cameraResolution.y;
        int cameraHeight = cameraResolution.x;

        int containerWidth = this.scanContainer.getWidth();
        int containerHeight = this.scanContainer.getHeight();

        int wh = containerWidth > containerHeight ? containerHeight : containerWidth;
        int cropWh = wh * scale / 100;
        int locationX = (containerWidth - cropWh) / 2;
        int locationY = (cameraHeight - cropWh) / 2;

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = locationX * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = locationY * cameraHeight / containerHeight;
        /** 计算最终截取的矩形的宽度 */
        int width = cropWh * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropWh * cameraHeight / containerHeight;
        /** 生成最终的截取的矩形 */
        return new Rect(x, y, width + x, height + y);
    }

    // 扫码完成后 mPreviewCallback 会获取到对应结果 这样可以开始处理结果
    private void onScanCompleted() {
        Toast.makeText(this, "scan complete", Toast.LENGTH_SHORT).show();
        this.mDelayHandler.removeCallbacks(this.mAutoFocus);
        this.mCameras.stop();
        setResult(RESULT_OK);
        finish();
//        this.startProcessData();
    }

    // 处理数据
    private void startProcessData() {

        String result = this.mPreviewCallback.getResult();


//        this.mSyncDataTask.setHandler(this.mDataHandler);
//        this.mSyncDataTask.setTotalItem(this.mPreviewCallback.getTotalItem());
        //noinspection unchecked
//        this.mSyncDataTask.execute(this.mPreviewCallback.getPackets().values());
    }

    @Override
    public void onGlobalLayout() {
        Point cameraResolution = this.mCameras.getCameraManager().getCameraResolution();
        Rect cropRect = this.getCropRect(cameraResolution, 60);
        this.mPreviewCallback.setCropRect(cropRect);
    }

    private static class QRScanMessageHandler extends Handler {

        private QRCodeScanActivity mContext;

        public QRScanMessageHandler(QRCodeScanActivity mContext) {
            super();
            this.setContext(mContext);
        }

        public void setContext(QRCodeScanActivity mContext) {
            this.mContext = mContext;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case CameraPreviewCallback.MESSAGE_POST_PROGRESS:
                    Toast.makeText(mContext, "scan complete", Toast.LENGTH_SHORT).show();
                    break;
                case CameraPreviewCallback.MESSAGE_POST_DATA_ERROR:
                    Toast.makeText(mContext, "data error", Toast.LENGTH_SHORT).show();
                    break;
                case CameraPreviewCallback.MESSAGE_POST_RESULT:
                    mContext.onScanCompleted();
                    break;
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                restartCamera();
            } else {
                // permission denied, boo! Disable the
//                Toast
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
