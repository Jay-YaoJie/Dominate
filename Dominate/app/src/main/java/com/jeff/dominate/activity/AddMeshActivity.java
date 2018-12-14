package com.jeff.dominate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.jeff.dominate.R;
import com.jeff.dominate.TelinkBaseActivity;
import com.jeff.dominate.TelinkLightApplication;
import com.jeff.dominate.TelinkLightService;
import com.jeff.dominate.model.Mesh;
import com.jeff.dominate.model.SharedPreferencesHelper;
import com.jeff.dominate.qrcode.QRCodeShareActivity;
import com.jeff.dominate.util.FileSystem;

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：
 */


public final class AddMeshActivity extends TelinkBaseActivity {

    private ImageView backView;
    //    private Button btn_save_filter;
    private Button btnSave;
    private Button btnShare, btnClear;

    private TelinkLightApplication mApplication;
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == backView) {
                finish();
            } else if (v == btnSave) {
                saveMesh();
            } else if (v == btnShare) {
                startActivity(new Intent(AddMeshActivity.this, QRCodeShareActivity.class));
            } else if (v == btnClear) {
                if (mApplication.getMesh().devices != null) {
                    mApplication.getMesh().devices.clear();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_add_mesh);

        this.mApplication = (TelinkLightApplication) this.getApplication();

        this.backView = (ImageView) this
                .findViewById(R.id.img_header_menu_left);
        this.backView.setOnClickListener(this.clickListener);

        this.btnSave = (Button) this.findViewById(R.id.btn_save);
        this.btnSave.setOnClickListener(this.clickListener);

        this.btnShare = (Button) findViewById(R.id.btn_share);
        this.btnShare.setOnClickListener(this.clickListener);

        this.btnClear = (Button) findViewById(R.id.btn_clear);
        this.btnClear.setOnClickListener(this.clickListener);
//        this.btn_save_filter = (Button) this.findViewById(R.id.btn_save_filter);
//        this.btn_save_filter.setOnClickListener(this.clickListener);


       /* mac_1 = (EditText) findViewById(R.id.mac_1);
        mac_2 = (EditText) findViewById(R.id.mac_2);
        mac_3 = (EditText) findViewById(R.id.mac_3);
        mac_4 = (EditText) findViewById(R.id.mac_4);*/
//        this.updateGUI();

        TelinkLightService.Instance().idleMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGUI();
    }

    private void updateGUI() {

        if (this.mApplication.isEmptyMesh())
            return;

        EditText txtMeshName = (EditText) this.findViewById(R.id.txt_mesh_name);
        EditText txtPassword = (EditText) this
                .findViewById(R.id.txt_mesh_password);

        EditText txtFactoryMeshName = (EditText) this
                .findViewById(R.id.txt_factory_name);
        EditText txtFactoryPassword = (EditText) this
                .findViewById(R.id.txt_factory_password);

        Mesh mesh = this.mApplication.getMesh();

        txtMeshName.setText(mesh.name);
        txtPassword.setText(mesh.password);
        txtFactoryMeshName.setText(mesh.factoryName);
        txtFactoryPassword.setText(mesh.factoryPassword);
    }

    @SuppressLint("ShowToast")
    private void saveMesh() {

        EditText txtMeshName = (EditText) this.findViewById(R.id.txt_mesh_name);
        EditText txtPassword = (EditText) this
                .findViewById(R.id.txt_mesh_password);

        EditText txtFactoryMeshName = (EditText) this
                .findViewById(R.id.txt_factory_name);
        EditText txtFactoryPassword = (EditText) this
                .findViewById(R.id.txt_factory_password);
        //EditText otaText = (EditText) this.findViewById(R.id.ota_device);

        String newfactoryName = txtMeshName.getText().toString().trim();
        String newfactoryPwd = txtPassword.getText().toString().trim();

        String factoryName = txtFactoryMeshName.getText().toString().trim();
        String factoryPwd = txtFactoryPassword.getText().toString().trim();

        if (newfactoryName.equals(newfactoryPwd)) {
            showToast("invalid! name should not equals with pwd");
            return;
        }

        if (factoryName.equals(newfactoryName)) {
            showToast("invalid! name should not equals with factory name");
            return;
        }

        if (newfactoryName.length() > 16 || newfactoryPwd.length() > 16 || factoryName.length() > 16 || factoryPwd.length() > 16) {
            showToast("invalid! input max length: 16");
            return;
        }
        if (newfactoryName.contains(".") || newfactoryPwd.contains(".")) {
            showToast("invalid! input should not contains '.' ");
            return;
        }


//        if (mesh == null)
//            mesh = new Mesh();


        Mesh mesh = (Mesh) FileSystem.readAsObject(this, newfactoryName + "." + newfactoryPwd);

        if (mesh == null) {
            mesh = new Mesh();
            mesh.name = newfactoryName;
            mesh.password = newfactoryPwd;
        }

        mesh.factoryName = factoryName;
        mesh.factoryPassword = factoryPwd;

        /*if (!factoryName.equals(mesh.factoryName) || !factoryPwd.equals(mesh.factoryPassword)) {
            mesh.allocDeviceAddress = null;
            mesh.devices.clear();
        }*/

        /*if (newfactoryName.equals(mesh.name) || newfactoryPwd.equals(mesh.password)) {
            mesh.allocDeviceAddress = null;
            mesh.devices.clear();
        }*/

        //mesh.otaDevice = otaText.getText().toString().trim();

        if (mesh.saveOrUpdate(this)) {
            this.mApplication.setupMesh(mesh);
            SharedPreferencesHelper.saveMeshName(this, mesh.name);
            SharedPreferencesHelper.saveMeshPassword(this, mesh.password);
            this.showToast("Save Mesh Success");
        }
    }


}
