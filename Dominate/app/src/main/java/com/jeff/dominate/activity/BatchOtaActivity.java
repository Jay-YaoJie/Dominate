package com.jeff.dominate.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
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
import com.telink.util.Strings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BatchOtaActivity extends TelinkBaseActivity implements EventListener<String>, View.OnClickListener {

    private static final int FILE_SELECT_CODE = 1;

    private static final int MSG_OTA_FAILURE = 0;
    private static final int MSG_OTA_SUCCESS = 1;
    private static final int MSG_OTA_PROGRESS = 2;

    private Button mChooseFile;
    private Button mStartOta;
    private TextView mTip;
    private GridView mDeviceListView;
    private DeviceListAdapter mDeviceAdapter;

    private EditText otaDelay;
    private EditText otaSize;

    private TelinkLightApplication mApp;
    private List<DeviceHolder> mDevices;
    private String mFirmwarePath;
    private byte[] mFirmwareData;
    private int successCount;
    private int failureCount;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_OTA_PROGRESS:
                    mDeviceAdapter.notifyDataSetChanged();
                    break;
                case MSG_OTA_SUCCESS:
                    mDeviceAdapter.notifyDataSetChanged();
                    break;
                case MSG_OTA_FAILURE:
                    mDeviceAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private int maxRetry = 5;
    private boolean notFound;
    private boolean otaCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelinkLog.LOG2FILE_ENABLE = true;
        setContentView(R.layout.activity_batch_ota);
        mApp = (TelinkLightApplication) getApplication();
        //监听事件
        mApp.addEventListener(LeScanEvent.LE_SCAN, this);
        mApp.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this);
        mApp.addEventListener(LeScanEvent.LE_SCAN_COMPLETED, this);
        mApp.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        mApp.addEventListener(MeshEvent.ERROR, this);

        mDevices = getSelectedDevices();
        mChooseFile = (Button) findViewById(R.id.chooseFile);
        mChooseFile.setOnClickListener(this);
        mStartOta = (Button) findViewById(R.id.startOta);
        mStartOta.setOnClickListener(this);
        mDeviceListView = (GridView) findViewById(R.id.devices);
        mDeviceAdapter = new DeviceListAdapter();
        mDeviceListView.setAdapter(mDeviceAdapter);
        mTip = (TextView) findViewById(R.id.tip);

        this.otaDelay = (EditText) this.findViewById(R.id.otadelay);
        this.otaSize = (EditText) this.findViewById(R.id.otaSize);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelinkLog.LOG2FILE_ENABLE = false;
        mApp.removeEventListener(this);
        TelinkLightService.Instance().startScan(null);
        TelinkLightService.Instance().idleMode(true);
    }

    @Override
    public void onClick(View v) {
        if (v == mChooseFile) {
            showFileChooser();
        } else if (v == mStartOta) {
            startOta();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择Firmware文件"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;

        Uri uri = data.getData();
        mFirmwarePath = uri.getPath();
        mTip.setText(mFirmwarePath);
        readFirmware(mFirmwarePath);
        TelinkLog.d(mFirmwarePath);
    }

    private void startOta() {
        mHandler.removeCallbacksAndMessages(null);
        Manufacture.Builder builder = new Manufacture.Builder();
        builder.setOtaDelay(Integer.parseInt(this.otaDelay.getText().toString()));
        builder.setOtaSize(Integer.parseInt(this.otaSize.getText().toString()));
        Manufacture.setManufacture(builder.build());

        if (notFound || (successCount + failureCount) >= mDevices.size()) {
            otaCompleted = true;
            mTip.append("\nOta Completed");
            Toast.makeText(this, "Ota Completed", Toast.LENGTH_SHORT).show();
            mDeviceAdapter.notifyDataSetChanged();
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startScan();
                }
            }, 500);
        }
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        LeScanParameters params = Parameters.createScanParameters();
        params.setMeshName(mApp.getMesh().name);
        params.setTimeoutSeconds(20);
        /*params.set(Parameters.PARAM_MESH_NAME, this.selectedDevice.meshName);
        params.set(Parameters.PARAM_SCAN_TIMEOUT_SECONDS, 10);*/
        TelinkLightService.Instance().idleMode(true);
        TelinkLightService.Instance().startScan(params);
    }

    /**
     * 开始OTA
     */
    private void startOta(String macAddress) {

        /*Mesh currentMesh = this.mApp.getMesh();
        LeOtaParameters params = LeOtaParameters.create();
        params.setMeshName(currentMesh.name);
        params.setPassword(currentMesh.password);
        params.setLeScanTimeoutSeconds(15);

        OtaDeviceInfo deviceInfo = new OtaDeviceInfo();
        deviceInfo.macAddress = macAddress;
        deviceInfo.firmware = mFirmwareData;
        params.setDeviceInfo(deviceInfo);
        TelinkLightService.Instance().idleMode(false);
        TelinkLightService.Instance().startOta(params);*/
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //TelinkLightService.Instance().sendCommandNoResponse((byte) 0xD0, 0x0000, new byte[]{0x01, 0x00, 0x00});
                TelinkLightService.Instance().startOta(mFirmwareData);
            }
        }, 500);
    }

    private void login() {
        Mesh currentMesh = this.mApp.getMesh();
        TelinkLightService.Instance().login(Strings.stringToBytes(currentMesh.name, 16), Strings.stringToBytes(currentMesh.password, 16));
    }

    private List<DeviceHolder> getSelectedDevices() {
        List<DeviceHolder> devices = new ArrayList<>();
        for (Light deviceInfo : mApp.getMesh().devices) {
            if (deviceInfo.selected) {
                devices.add(new DeviceHolder(deviceInfo));
            }
        }
        return devices;
    }

    private DeviceHolder findDeviceHolder(String mac) {
        for (DeviceHolder holder : mDevices) {
            if (holder.deviceInfo.macAddress.equals(mac))
                return holder;
        }
        return null;
    }

    private void readFirmware(String fileName) {
        try {
            InputStream stream = new FileInputStream(fileName);
            int length = stream.available();
            mFirmwareData = new byte[length];
            stream.read(mFirmwareData);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void performed(Event<String> event) {
        if (event instanceof LeScanEvent) {
            this.onLeScanEvent((LeScanEvent) event);
        } else if (event instanceof DeviceEvent) {
            this.onDeviceEvent((DeviceEvent) event);
        }
    }

    /**
     * 处理LeScanEvent事件
     *
     * @param event
     */
    private void onLeScanEvent(LeScanEvent event) {
        String type = event.getType();

        switch (type) {
            case LeScanEvent.LE_SCAN:
                com.telink.bluetooth.light.DeviceInfo deviceInfo = event.getArgs();
                onDeviceFound(deviceInfo.macAddress);
                break;
            case LeScanEvent.LE_SCAN_COMPLETED:
                notFound = true;
                startOta();
                break;
            case LeScanEvent.LE_SCAN_TIMEOUT:
                TelinkLog.d(type + "    模式 : " + TelinkLightService.Instance().getMode());
                startOta();
                break;
        }
    }

    /**
     * 处理DeviceEvent事件
     *
     * @param event
     */
    private void onDeviceEvent(DeviceEvent event) {
        String type = event.getType();
        switch (type) {
            case DeviceEvent.STATUS_CHANGED:
                int status = event.getArgs().status;
                if (status == LightAdapter.STATUS_OTA_PROGRESS) {
                    //ota进度
                    OtaDeviceInfo deviceInfo = (OtaDeviceInfo) event.getArgs();
                    onOtaProgress(deviceInfo.macAddress, deviceInfo.progress);
                } else if (status == LightAdapter.STATUS_CONNECTED) {
                    //获取版本
                    TelinkLightService.Instance().getFirmwareVersion();
                } else if (status == LightAdapter.STATUS_LOGOUT) {
                    TelinkLog.d("-------------------------");
                    TelinkLog.d("断开连接 : " + event.getArgs().macAddress);
                    TelinkLog.d("模式 : " + TelinkLightService.Instance().getMode());
                    //com.telink.bluetooth.light.DeviceInfo deviceInfo = event.getArgs();
                    onOtaFailure(event.getArgs().macAddress);
                } else if (status == LightAdapter.STATUS_OTA_COMPLETED) {
                    //ota完成
                    com.telink.bluetooth.light.DeviceInfo deviceInfo = event.getArgs();
                    onOtaCompleted(deviceInfo.macAddress);
                } else if (status == LightAdapter.STATUS_OTA_FAILURE) {
                    com.telink.bluetooth.light.DeviceInfo deviceInfo = event.getArgs();
                    onOtaFailure(deviceInfo.macAddress);
                } else if (status == LightAdapter.STATUS_GET_FIRMWARE_COMPLETED) {
                    //版本获取成功，比较版本后,开始ota
                    com.telink.bluetooth.light.DeviceInfo deviceInfo = event.getArgs();
                    onFirmwareSuccess(deviceInfo.macAddress, deviceInfo.firmwareRevision);
                    //startOta(deviceInfo.macAddress);
                    login();
                } else if (status == LightAdapter.STATUS_GET_FIRMWARE_FAILURE) {
                    //版本获取失败
                    com.telink.bluetooth.light.DeviceInfo deviceInfo = event.getArgs();
                    TelinkLog.d(deviceInfo.macAddress + " 获取firmware失败");
                    onOtaFailure(deviceInfo.macAddress);
                } else if (status == LightAdapter.STATUS_LOGIN) {
                    com.telink.bluetooth.light.DeviceInfo deviceInfo = event.getArgs();
                    startOta(deviceInfo.macAddress);
                }

                break;
        }
    }

    synchronized private void onDeviceFound(String mac) {
        DeviceHolder holder = findDeviceHolder(mac);
        if (holder != null && !holder.completed && holder.failureCount < maxRetry) {
            notFound = false;
            TelinkLightService.Instance().idleMode(true);
            TelinkLightService.Instance().connect(mac, 10);
        }
    }

    private void onOtaFailure(String mac) {
        DeviceHolder holder = findDeviceHolder(mac);
        if (holder != null) {
            if (holder.completed || holder.failureCount == maxRetry) {
                startOta();
            } else {
                holder.failureCount++;
                TelinkLog.d(mac + "==>失败次数统计 ：" + holder.failureCount);
                //失败3次后不会在ota
                if (holder.failureCount >= maxRetry) {
                    failureCount++;
                    TelinkLog.d("总计失败次数 ：" + failureCount);

                }
                startOta();
                mHandler.obtainMessage(MSG_OTA_FAILURE).sendToTarget();
            }
        } else {
            startOta();
        }
    }


    private void onOtaCompleted(String mac) {
        DeviceHolder holder = findDeviceHolder(mac);
        if (holder != null) {
            holder.completed = true;
            successCount++;
            TelinkLog.d(mac + " ota完成");
            mHandler.obtainMessage(MSG_OTA_SUCCESS).sendToTarget();
        }
    }

    private void onOtaProgress(String mac, int progress) {
        DeviceHolder holder = findDeviceHolder(mac);
        if (holder != null) {
            holder.progress = progress;
            mHandler.obtainMessage(MSG_OTA_PROGRESS).sendToTarget();
        }
    }

    private void onFirmwareSuccess(String mac, String firmwareVersion) {
        DeviceHolder holder = findDeviceHolder(mac);
        if (holder != null) {
            TelinkLog.d(mac + " 获取firmware完成 : " + firmwareVersion);
            holder.firmwareVersion = firmwareVersion;
        }
    }

    private static class DeviceItemHolder {
        public ImageView icon;
        public TextView name;
        public TextView progress;
        public TextView retryCount;
    }

    private static class DeviceHolder {
        public Light deviceInfo;
        public int failureCount;
        public boolean completed;
        public String firmwareVersion;
        public int progress;

        public DeviceHolder(Light deviceInfo) {
            this.deviceInfo = deviceInfo;
        }
    }

    private class DeviceListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDevices == null ? 0 : mDevices.size();
        }

        @Override
        public DeviceHolder getItem(int position) {
            return mDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DeviceItemHolder holder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.ota_progress_list_item, null);
                ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                TextView name = (TextView) convertView.findViewById(R.id.name);
                TextView progress = (TextView) convertView.findViewById(R.id.progress);
                TextView retryCount = (TextView) convertView.findViewById(R.id.retryCount);
                holder = new DeviceItemHolder();
                holder.icon = icon;
                holder.name = name;
                holder.progress = progress;
                holder.retryCount = retryCount;
                convertView.setTag(holder);
            } else {
                holder = (DeviceItemHolder) convertView.getTag();
            }

            DeviceHolder deviceHolder = getItem(position);
            holder.name.setText(deviceHolder.deviceInfo.deviceName);
            holder.name.append("\n" + deviceHolder.deviceInfo.macAddress);
            if (deviceHolder.completed) {
                holder.progress.setText("ota success");
            } else {
                if (deviceHolder.progress >= 100) {
                    deviceHolder.progress = 99;
                }
                holder.progress.setText(deviceHolder.progress + "%");
            }

            if (deviceHolder.failureCount >= maxRetry) {
                holder.retryCount.setText("ota failure");
            } else {
                holder.retryCount.setText(deviceHolder.failureCount + "");
            }

            if (otaCompleted && !deviceHolder.completed) {
                if (deviceHolder.failureCount < maxRetry) {
                    holder.retryCount.setText("not found  " + deviceHolder.failureCount + "");
                }
            }

            return convertView;
        }
    }
}
