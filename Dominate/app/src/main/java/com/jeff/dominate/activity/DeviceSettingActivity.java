package com.jeff.dominate.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeff.dominate.R;
import com.jeff.dominate.TelinkBaseActivity;
import com.jeff.dominate.fragments.DeviceSettingFragment;
import com.jeff.dominate.model.Light;
import com.jeff.dominate.model.Lights;



public final class DeviceSettingActivity extends TelinkBaseActivity {

    private ImageView backView;
    private ImageView editView;
    private DeviceSettingFragment settingFragment;

    private int meshAddress;
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == backView) {
                finish();
            } else if (v == editView) {
                Light light = Lights.getInstance().getByMeshAddress(meshAddress);
                if (light == null || TextUtils.isEmpty(light.macAddress)) {
                    showToast("error! Lack of mac");
                    return;
                }
                Intent intent = new Intent(DeviceSettingActivity.this,
                        DeviceGroupingActivity.class);
                intent.putExtra("meshAddress", meshAddress);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_device_setting);

        this.meshAddress = this.getIntent().getIntExtra("meshAddress", 0);

        Light light = Lights.getInstance().getByMeshAddress(meshAddress);

        if (light != null) {
            TextView txtTitle = (TextView) this
                    .findViewById(R.id.txt_header_title);
            txtTitle.setText(light.getLabel2());
        }

        this.backView = (ImageView) this
                .findViewById(R.id.img_header_menu_left);
        this.backView.setOnClickListener(this.clickListener);

        this.editView = (ImageView) this
                .findViewById(R.id.img_header_menu_right);
        this.editView.setOnClickListener(this.clickListener);

        this.settingFragment = (DeviceSettingFragment) this
                .getFragmentManager().findFragmentById(
                        R.id.device_setting_fragment);

        this.settingFragment.meshAddress = meshAddress;
    }
}
