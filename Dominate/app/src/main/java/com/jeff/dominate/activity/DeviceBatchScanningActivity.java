package com.jeff.dominate.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.jeff.dominate.R;
import com.jeff.dominate.TelinkBaseActivity;
import com.jeff.dominate.TelinkLightApplication;
import com.jeff.dominate.TelinkLightService;
import com.jeff.dominate.model.Light;
import com.jeff.dominate.model.Mesh;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.light.*;

import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.List;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：
 */

public final class DeviceBatchScanningActivity extends TelinkBaseActivity implements AdapterView.OnItemClickListener, EventListener<String> {

    private ImageView backView;
    private Button btnScan;

    private LayoutInflater inflater;
    private DeviceListAdapter adapter;

    private TelinkLightApplication mApplication;
    private List<DeviceInfo> updateList;

    private List<DeviceInfo> scanedList;

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == backView) {
//                TelinkLightService.Instance().idleMode();
                finish();
            } else if (v == btnScan) {
                finish();
                //stopScanAndUpdateMesh();
            } else if (v.getId() == R.id.btn_log) {
                startActivity(new Intent(DeviceBatchScanningActivity.this, LogInfoActivity.class));
            }
        }
    };
    private Handler mHandler = new Handler();

    @Override
    public void onBackPressed() {
//        TelinkLightService.Instance().idleMode();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_device_scanning);

        //监听事件
        this.mApplication = (TelinkLightApplication) this.getApplication();
        this.mApplication.addEventListener(LeScanEvent.LE_SCAN, this);
        this.mApplication.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this);
        this.mApplication.addEventListener(LeScanEvent.LE_SCAN_COMPLETED, this);
        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        this.mApplication.addEventListener(MeshEvent.UPDATE_COMPLETED, this);
        this.mApplication.addEventListener(MeshEvent.ERROR, this);

        this.inflater = this.getLayoutInflater();
        this.adapter = new DeviceListAdapter();

        this.backView = (ImageView) this
                .findViewById(R.id.img_header_menu_left);
        this.backView.setOnClickListener(this.clickListener);
        findViewById(R.id.btn_log).setOnClickListener(this.clickListener);
        this.btnScan = (Button) this.findViewById(R.id.btn_scan);
        this.btnScan.setOnClickListener(this.clickListener);
        this.btnScan.setEnabled(false);
        this.btnScan.setBackgroundResource(R.color.gray);

        GridView deviceListView = (GridView) this
                .findViewById(R.id.list_devices);
        deviceListView.setAdapter(this.adapter);
        deviceListView.setOnItemClickListener(this);

        this.updateList = new ArrayList<>();
        scanedList = new ArrayList<>();
        this.startScan(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.updateList = null;
        this.mApplication.removeEventListener(this);
        this.mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 开始扫描
     */
    private void startScan(int delay) {
        scanedList.clear();
        TelinkLightService.Instance().idleMode(true);
        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mApplication.isEmptyMesh())
                    return;

                Mesh mesh = mApplication.getMesh();

                //扫描参数
                LeScanParameters params = LeScanParameters.create();
                params.setMeshName(mesh.factoryName);
                params.setOutOfMeshName("kick");
                params.setTimeoutSeconds(10);
                params.setScanMode(false);
                TelinkLightService.Instance().startScan(params);
            }
        }, delay);

    }

    /**
     * 处理扫描事件
     *
     * @param event
     */
    private void onLeScan(LeScanEvent event) {
        scanedList.add(event.getArgs());
    }

    private void updateList() {
        if (scanedList.size() == 0) {
            onLeScanTimeout(null);
            return;
        }
        Mesh mesh = this.mApplication.getMesh();
        int meshAddress = mesh.getDeviceAddress();

        if (meshAddress == -1) {
            this.showToast("哎呦，网络里的灯泡太多了！目前可以有256灯");
            this.finish();
            return;
        }

        //更新参数
        LeUpdateParameters params = Parameters.createUpdateParameters();
        params.setOldMeshName(mesh.factoryName);
        params.setOldPassword(mesh.factoryPassword);
        params.setNewMeshName(mesh.name);
        params.setNewPassword(mesh.password);

        /*DeviceInfo deviceInfo = event.getArgs();
        deviceInfo.meshAddress = meshAddress;*/
        DeviceInfo[] deviceInfos = scanedList.toArray(new DeviceInfo[scanedList.size()]);
        params.setUpdateDeviceList(deviceInfos);
//        params.setUpdateMeshIndex(meshAddress);
        //params.set(Parameters.PARAM_DEVICE_LIST, deviceInfo);
//        TelinkLightService.Instance().idleMode(true);
        //加灯
        TelinkLightService.Instance().updateMesh(params);
    }

    /**
     * 扫描不到任何设备了
     *
     * @param event
     */
    private void onLeScanTimeout(LeScanEvent event) {
        this.btnScan.setEnabled(true);
        this.btnScan.setBackgroundResource(R.color.theme_positive_color);
    }

    private void onDeviceStatusChanged(DeviceEvent event) {

        DeviceInfo deviceInfo = event.getArgs();

        switch (deviceInfo.status) {
            case LightAdapter.STATUS_UPDATE_MESH_COMPLETED:
                //加灯完成继续扫描,直到扫不到设备

                int meshAddress = deviceInfo.meshAddress & 0xFF;
                Light light = this.adapter.get(meshAddress);

                if (light == null) {
                    light = new Light();

                    light.deviceName = deviceInfo.deviceName;
                    light.meshName = deviceInfo.meshName;
                    light.firmwareRevision = deviceInfo.firmwareRevision;
                    light.longTermKey = deviceInfo.longTermKey;
                    light.macAddress = deviceInfo.macAddress;
                    light.meshAddress = deviceInfo.meshAddress;
                    light.meshUUID = deviceInfo.meshUUID;
                    light.productUUID = deviceInfo.productUUID;
                    light.status = deviceInfo.status;
                    light.textColor = R.color.black;
                    light.selected = false;
//                    light.raw = deviceInfo;
                    this.mApplication.getMesh().devices.add(light);
                    this.mApplication.getMesh().saveOrUpdate(this);


                    this.adapter.add(light);
                    this.adapter.notifyDataSetChanged();
                }

                break;

            case LightAdapter.STATUS_UPDATE_MESH_FAILURE:
                //加灯失败继续扫描
//                this.startScan(1000);
                TelinkLog.w("DeviceBatchScanningActivity#STATUS_UPDATE_MESH_FAILURE");
                break;

            case LightAdapter.STATUS_ERROR_N:
                this.onNError(event);
                break;
        }
    }

    private void onNError(final DeviceEvent event) {

        TelinkLightService.Instance().idleMode(true);
        TelinkLog.d("DeviceScanningActivity#onNError");

        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceBatchScanningActivity.this);
        builder.setMessage("当前环境:Android7.0!加灯时连接重试: 3次失败!");
        builder.setNegativeButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();

    }

    private void onMeshEvent(MeshEvent event) {
        new AlertDialog.Builder(this).setMessage("重启蓝牙,更好地体验智能灯!").show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Light light = this.adapter.getItem(position);
        light.selected = !light.selected;
        DeviceItemHolder holder = (DeviceItemHolder) view.getTag();
        holder.selected.setChecked(light.selected);

        /*if (light.selected) {
            this.updateList.add(light.raw);
        } else {
            this.updateList.remove(light.raw);
        }*/
    }

    /**
     * 事件处理方法
     *
     * @param event
     */
    @Override
    public void performed(Event<String> event) {

        switch (event.getType()) {
            case LeScanEvent.LE_SCAN:
                this.onLeScan((LeScanEvent) event);
                break;
            case LeScanEvent.LE_SCAN_TIMEOUT:
                this.onLeScanTimeout((LeScanEvent) event);
                break;

            case LeScanEvent.LE_SCAN_COMPLETED:
                updateList();
                break;

            case DeviceEvent.STATUS_CHANGED:
                this.onDeviceStatusChanged((DeviceEvent) event);
                break;

            case MeshEvent.UPDATE_COMPLETED:
                this.startScan(1000);
                break;
            case MeshEvent.ERROR:
                this.onMeshEvent((MeshEvent) event);
                break;
        }
    }

    private static class DeviceItemHolder {
        public ImageView icon;
        public TextView txtName;
        public CheckBox selected;
    }

    final class DeviceListAdapter extends BaseAdapter {

        private List<Light> lights;

        public DeviceListAdapter() {

        }

        @Override
        public int getCount() {
            return this.lights == null ? 0 : this.lights.size();
        }

        @Override
        public Light getItem(int position) {
            return this.lights.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            DeviceItemHolder holder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.device_item, null);
                ImageView icon = (ImageView) convertView
                        .findViewById(R.id.img_icon);
                TextView txtName = (TextView) convertView
                        .findViewById(R.id.txt_name);
                CheckBox selected = (CheckBox) convertView.findViewById(R.id.selected);

                holder = new DeviceItemHolder();

                holder.icon = icon;
                holder.txtName = txtName;
                holder.selected = selected;
                holder.selected.setVisibility(View.GONE);

                convertView.setTag(holder);
            } else {
                holder = (DeviceItemHolder) convertView.getTag();
            }

            Light light = this.getItem(position);

            holder.txtName.setText(light.deviceName);
            holder.icon.setImageResource(R.mipmap.icon_light_on);
            holder.selected.setChecked(light.selected);

            return convertView;
        }

        public void add(Light light) {

            if (this.lights == null)
                this.lights = new ArrayList<>();

            this.lights.add(light);
        }

        public Light get(int meshAddress) {

            if (this.lights == null)
                return null;

            for (Light light : this.lights) {
                if (light.meshAddress == meshAddress) {
                    return light;
                }
            }

            return null;
        }
    }
}
