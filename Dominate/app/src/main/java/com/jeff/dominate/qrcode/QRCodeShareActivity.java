package com.jeff.dominate.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.jeff.dominate.R;
import com.jeff.dominate.TelinkBaseActivity;


public final class QRCodeShareActivity extends TelinkBaseActivity {

    private ImageView qr_image;
    private Handler mGeneratorHandler;
    QRCodeGenerator mQrCodeGenerator;
    private final static int Request_Code_Scan = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_place_share);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        qr_image = (ImageView) this.findViewById(R.id.qr_image);
        TextView title = (TextView) this.findViewById(R.id.txt_header_title);
        title.setText("Share");
        findViewById(R.id.act_share_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(QRCodeShareActivity.this, QRCodeScanActivity.class), Request_Code_Scan);
            }
        });

        mGeneratorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == QRCodeGenerator.QRCode_Generator_Success) {
                    if (mQrCodeGenerator.getResult() != null)
                        qr_image.setImageBitmap(mQrCodeGenerator.getResult());
                } else {
                    showToast("qr code data error!");
                }
            }
        };

        mQrCodeGenerator = new QRCodeGenerator(mGeneratorHandler);
        mQrCodeGenerator.execute();
        /*DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int size = (int) metrics.density * 300;

        QREncoder.Builder builder = new QREncoder.Builder();
        builder.setBackground(0xFFFFFFFF);
        builder.setCodeColor(0xFF000000);
        builder.setCharset("UTF-8");
        builder.setWidth(size);
        builder.setHeight(size);
        builder.setPadding(2);
        builder.setLevel(ErrorCorrectionLevel.L);
        QREncoder encoder = builder.build();

        this.mGenerator = new QRCodeGeneratorTask();
        this.mGenerator.setPlaceSort(null);
        this.mGenerator.setEncoder(encoder);
        this.mGenerator.setHandler(this.mHandler);
        this.mGenerator.execute();*/

//        this.mAdapter.setDataSource(this.mGenerator.getResult());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static class QRCodeImageHolder {
        public ImageView image;
        public TextView name;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request_Code_Scan && resultCode == RESULT_OK) {
            finish();
        }
    }
}
