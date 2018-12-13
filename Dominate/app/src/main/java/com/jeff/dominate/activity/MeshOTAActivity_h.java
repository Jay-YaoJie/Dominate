package com.jeff.dominate.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.jeff.dominate.R;
import com.jeff.dominate.TelinkLightApplication;
import com.jeff.dominate.TelinkLightService;
import com.jeff.dominate.TelinkMeshErrorDealActivity;
import com.jeff.dominate.adapter.BaseRecyclerViewAdapter;
import com.jeff.dominate.adapter.TypeSelectAdapter;
import com.jeff.dominate.model.*;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.*;

import com.telink.util.Event;
import com.telink.util.EventListener;
import com.telink.util.Strings;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 演示mesh ota相关功能
 * 要求， 准备升级的设备必须是之前做过加灯操作的
 * 流程：
 * 1. 判断当前mesh在线状态， 如果是非在线状态，则不可操作
 * 2. 获取所有设备的状态信息， 并刷新设备列表 类型列表{@link MeshOTAActivity_h#rv_type}；
 * -> 由用户选择firmware版本并勾选对应升级的设备类型
 * -> 点击开始后判断是否选择设备类型和对应文件
 * -> 无误后获取设备的OTA状态{@link #sendGetDeviceOtaStateCommand()}
 * -> 获取失败， 提示不支持； 获取成功后， 设置设备的1
 */
public class MeshOTAActivity_h extends TelinkMeshErrorDealActivity implements EventListener<String>, View.OnClickListener {


    private int mode = MODE_IDLE;

    private boolean isOTAComplete = false;
    private boolean isMeshOTAComplete = false;
    private static final int MODE_IDLE = 1;
    //    private static final int MODE_OTA = 2;
    private static final int MODE_MESH_OTA = 4;
    private static final int MODE_CONTINUE_MESH_OTA = 8;
//    private static final int MODE_COMPLETE = 16;

    public static final String INTENT_KEY_CONTINUE_MESH_OTA = "com.telink.bluetooth.light.INTENT_KEY_CONTINUE_MESH_OTA";
    // 有进度状态上报 时跳转进入的
    public static final int CONTINUE_BY_REPORT = 0x21;

    // 继续之前的OTA操作，连接指定设备
//    public static final int CONTINUE_BY_PREVIOUS = 0x22;

//    private int continueType = 0;

    private static final int REQUEST_CODE_CHOOSE_FILE = 11;

    private byte[] mFirmwareData;
    private String mFileVersion;
    private Mesh mesh;
    //    private String mPath;
    private SimpleDateFormat mTimeFormat;
//    private int successCount = 0;

    //    private TextView otaProgress;
    private TextView meshOtaProgress;
    private TextView tv_log;
    private ScrollView sv_log;

    private static final int MSG_MESH_OTA_PROGRESS = 12;
    private static final int MSG_LOG = 13;
    private static final int MSG_SCROLL = 14;

    private Handler delayHandler = new Handler();

    private Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_MESH_OTA_PROGRESS:
                    meshOtaProgress.setText(getString(R.string.progress_mesh_ota, msg.obj.toString()));
                    break;

                case MSG_LOG:
                    String time = mTimeFormat.format(Calendar.getInstance().getTimeInMillis());
                    tv_log.append("\n" + time + ":" + msg.obj.toString());
                    msgHandler.obtainMessage(MSG_SCROLL).sendToTarget();
                    break;

                case MSG_SCROLL:
                    sv_log.fullScroll(View.FOCUS_DOWN);
                    break;
            }
        }
    };

    /**
     * 设备列表与类型列表
     * device list and type list
     */
    private RecyclerView rv_type;
    private TypeSelectAdapter mTypeAdapter;

    private List<MeshDeviceType> mTypeList;
    private List<Light> onlineLights;

    private Button btn_start, btn_check;

    private MeshDeviceType selectType;

    private DeviceInfo opDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesh_ota);
        mTimeFormat = new SimpleDateFormat("HH:mm:ss.S");
//        TelinkLightService.Instance().idleMode(false);
        // 获取所有 【存储于本地】&【在线】 设备
        onlineLights = Lights.getInstance().getLocalList(true);
        initView();

        log("local online lights:" + onlineLights.size());
        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_KEY_CONTINUE_MESH_OTA)) {
            this.mode = MODE_CONTINUE_MESH_OTA;
            this.mFileVersion = TelinkLightApplication.getApp().getConnectDevice().firmwareRevision;
            log("continue mesh ota");
            enableUI(false);
        }


        opDevice = TelinkLightApplication.getApp().getConnectDevice();
        if (opDevice == null || onlineLights == null || onlineLights.isEmpty()) {
            log("offline or no valid device!");
            enableUI(false);
        } else {
            initMeshInfo();
            log("direct device:" + opDevice.macAddress);
        }
    }

    private void initMeshInfo() {
        addEventListener();
        TelinkLightService.Instance().enableNotification();
        mesh = TelinkLightApplication.getApp().getMesh();

        if (TelinkLightApplication.getApp().isEmptyMesh()) {
            toast("Mesh Error!");
            log("mesh empty");
//            finish();
        }
    }


    private void initView() {
        meshOtaProgress = (TextView) findViewById(R.id.progress_mesh_ota);
        tv_log = (TextView) findViewById(R.id.tv_log);
        sv_log = (ScrollView) findViewById(R.id.sv_log);

        findViewById(R.id.back).setOnClickListener(this);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        btn_check = (Button) findViewById(R.id.btn_check);
        btn_check.setOnClickListener(this);

        mTypeList = new ArrayList<>();
        if (onlineLights != null) {
            for (Light light : onlineLights) {

                MeshDeviceType type = new MeshDeviceType();
                type.type = light.productUUID;

                if (!mTypeList.contains(type)) {
                    type.deviceList.add(light);
                    type.filePath = null;

                    mTypeList.add(type);
                } else {
                    mTypeList.get(mTypeList.indexOf(type)).deviceList.add(light);
                }
            }
        }

        mTypeAdapter = new TypeSelectAdapter(this, mTypeList);
        mTypeAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (!mTypeAdapter.isEnable()) return;
                if (mTypeAdapter.getSelectPosition() != position) {
                    mTypeAdapter.setSelectPosition(position);
                } else {
                    mTypeAdapter.setSelectPosition(-1);
                }
            }
        });

        rv_type = (RecyclerView) findViewById(R.id.rv_type);
        rv_type.setLayoutManager(new LinearLayoutManager(this));
        rv_type.setAdapter(mTypeAdapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelinkLightApplication.getApp().removeEventListener(this);
        if (this.delayHandler != null) {
            this.delayHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onLocationEnable() {
        startScan();
    }

    private void addEventListener() {
        TelinkLightApplication.getApp().addEventListener(DeviceEvent.STATUS_CHANGED, this);
        TelinkLightApplication.getApp().addEventListener(LeScanEvent.LE_SCAN, this);
        TelinkLightApplication.getApp().addEventListener(LeScanEvent.LE_SCAN_COMPLETED, this);
        TelinkLightApplication.getApp().addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this);
        TelinkLightApplication.getApp().addEventListener(NotificationEvent.GET_DEVICE_STATE, this);
    }

    private void enableUI(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_start.setEnabled(enable);
                btn_check.setEnabled(enable);
                mTypeAdapter.setEnable(enable);
                mTypeAdapter.notifyDataSetChanged();
            }
        });

    }


    /**
     * Action start: after get versions
     * hasHigh Confirm action OTA or MeshOTA
     * hasLow Confirm OTA needed
     */
    private void start() {
        // 1. 判断是否有选择
        if (mTypeAdapter.getSelectPosition() == -1) {
            showToast("select a type first!");
            return;
        }
        MeshDeviceType deviceType = mTypeList.get(mTypeAdapter.getSelectPosition());
        String path = deviceType.filePath;
        if (TextUtils.isEmpty(path)) {
            showToast("Select a firmware for type!");
            return;
        }

        parseFile(path);

        if (mFileVersion == null) {
            Toast.makeText(this, "File parse error!", Toast.LENGTH_SHORT).show();
            mFileVersion = null;
            deviceType.filePath = null;
            mTypeAdapter.notifyDataSetChanged();
            log("File parse error!");
            showToast("File parse error!");
        } else {

            log("ota mode: meshOTA");
            mode = MODE_MESH_OTA;

            selectType = deviceType;
            enableUI(false);
            if (TelinkLightApplication.getApp().getConnectDevice() == null) {
                startScan();
            } else {
                sendGetDeviceOtaStateCommand();
            }
        }
    }


    // stop
    private void sendStopMeshOTACommand() {
        byte opcode = (byte) 0xC6;
        int address = 0xFFFF;
        byte[] params = new byte[]{(byte) 0xFE, (byte) 0xFF};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                params);
    }


    private int otaStateTimeout = 0;
    private static final int OTA_STATE_TIMEOUT_MAX = 3;

    // 获取本地设备OTA状态信息
    private void sendGetDeviceOtaStateCommand() {
        otaStateTimeout = 0;
        delayHandler.post(deviceOtaStateTimeoutTask);
    }

    private Runnable deviceOtaStateTimeoutTask = new Runnable() {
        @Override
        public void run() {

            if (otaStateTimeout < OTA_STATE_TIMEOUT_MAX) {
                byte opcode = (byte) 0xC7;
                int address = 0x0000;
                byte[] params = new byte[]{0x20, 0x05};
                TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                        params);
                log("getDeviceOtaState(0xC7)");
                otaStateTimeout++;
                delayHandler.postDelayed(this, 3000);
            } else {
                log("getDeviceOtaState fail");
                delayHandler.removeCallbacks(this);
                if (mode == MODE_MESH_OTA) {
                    startUpdate();
                }
            }
        }
    };


    private int getVersionRetry = 0;

    /**
     * 获取mesh内设备版本信息
     * {@link #onNotificationEvent}
     */
    private void sendGetVersionCommand() {
        byte opcode = (byte) 0xC7;
        int address = 0xFFFF;
        byte[] params = new byte[]{0x20, 0x00};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                params);
        log("getVersion(0xC7)");
        // 转发次数 * () * interval + 500
        delayHandler.removeCallbacks(getVersionTask);
        delayHandler.postDelayed(getVersionTask, 0x20 * 2 * 40 + 500);
    }

    private Runnable getVersionTask = new Runnable() {
        @Override
        public void run() {

            boolean miss = false;
            for (Light light : onlineLights) {
                if (!light.selected) {
                    miss = true;
                    log("miss: " + light.meshAddress + " -- " + light.macAddress);
                }
            }

            if (!miss || getVersionRetry >= 2) {
                log("get version complete");
                getVersionRetry = 0;
                doComplete();
            } else {
                getVersionRetry++;
                log("get version retry: " + getVersionRetry);
                sendGetVersionCommand();
            }
        }
    };


    /**
     * action startScan
     */
    private void startScan() {
        TelinkLightService.Instance().idleMode(true);
        LeScanParameters params = Parameters.createScanParameters();
        params.setMeshName(mesh.name);
        params.setTimeoutSeconds(15);
        TelinkLightService.Instance().startScan(params);
        log("startScan ");
    }

    private void startUpdate() {
        isOTAComplete = false;
        isMeshOTAComplete = false;
//        log("startUpdate ");
        setDeviceOTAMode();
    }

    /**
     * 0 GATT OTA
     * 1 meshOTA
     */
    private void setDeviceOTAMode() {
        byte opcode = (byte) 0xC7;
        int address = 0x0000;

        int type = selectType.type;
        byte[] params = new byte[]{0x10, 0x06, (byte) 0x01, (byte) (type & 0xFF), (byte) (type >> 8 & 0xFF)};

        TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                params);
        delayHandler.postDelayed(otaModeCmdCheckTask, 500);

        log("SendCommand 0xC7 set device OTA mode");
    }

    private Runnable otaModeCmdCheckTask = new Runnable() {
        @Override
        public void run() {
            onMeshOTANotSupport();
        }
    };

    /**
     * 不支持MeshOTA
     */
    private void onMeshOTANotSupport() {
        log("MeshOTA not support !!!");
    }


    public void connectDevice(String mac) {
        log("connectDevice :" + mac);
        TelinkLightService.Instance().idleMode(true);
        TelinkLightService.Instance().connect(mac, 15);
    }

    private void login() {
        log("login");
        TelinkLightService.Instance().login(Strings.stringToBytes(mesh.name, 16), Strings.stringToBytes(mesh.password, 16));
    }

    private void onLeScan(LeScanEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        Log.e("ota progress", "LE_SCAN : " + deviceInfo.macAddress);
        log("on scan : " + deviceInfo.macAddress);
        if (opDevice != null && deviceInfo.macAddress.equals(opDevice.macAddress)) {
            connectDevice(deviceInfo.macAddress);
        }
    }

    @Override
    public void performed(Event<String> event) {
//        if (this.mode == MODE_COMPLETE) return;
        switch (event.getType()) {
            case LeScanEvent.LE_SCAN:
                onLeScan((LeScanEvent) event);
                break;
            case LeScanEvent.LE_SCAN_COMPLETED:
                // scan complete without results
                Log.e("ota progress", "LE_SCAN_COMPLETED");
                log("scan complete");
                onScanTimeout();
                break;

            case DeviceEvent.STATUS_CHANGED:
                onDeviceEvent((DeviceEvent) event);
                break;
            case NotificationEvent.GET_DEVICE_STATE:
                onNotificationEvent((NotificationEvent) event);
                break;
        }
    }

    private void onNotificationEvent(NotificationEvent event) {
        // 解析版本信息
        byte[] data = event.getArgs().params;
        if (data[0] == NotificationEvent.DATA_GET_VERSION) {
            String version = Strings.bytesToString(Arrays.copyOfRange(data, 1, 5));

            int meshAddress = event.getArgs().src;

            Lights.getInstance().getByMeshAddress(meshAddress).selected = true;

            /*if (!versionDevices.contains(meshAddress)) {
                versionDevices.add(meshAddress);
            }*/

            TelinkLog.w(" src:" + meshAddress + " get version success: " + version);
            log("getVersion:" + Integer.toHexString(meshAddress) + "  version:" + version);


            for (Light light : onlineLights) {
                if (light.meshAddress == meshAddress) {
//                        log("version: " + version + " -- light version:" + light.version + " --mode: " + this.mode);
                    /*if (this.mode == MODE_COMPLETE) {
                        if (!version.equals(light.firmwareRevision)) {
                            successCount++;
                        }
                    }*/
                    light.firmwareRevision = version;
                }
            }

        } else if (data[0] == NotificationEvent.DATA_GET_MESH_OTA_PROGRESS) {
            TelinkLog.w("mesh ota progress: " + data[1]);
            int progress = (int) data[1];
            msgHandler.obtainMessage(MSG_MESH_OTA_PROGRESS, "MeshOTA -- " + progress + "%").sendToTarget();

            if (progress == 99) {
                isMeshOTAComplete = true;
                msgHandler.obtainMessage(MSG_MESH_OTA_PROGRESS, "MeshOTA -- device rebooting...").sendToTarget();
            }
            /*if (progress == 100) {
                this.mode = MODE_COMPLETE;
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doComplete();
                    }
                }, 5 * 1000);
                Mesh mesh = TelinkLightApplication.getApp().getMesh();
                mesh.otaDevice = null;
                mesh.saveOrUpdate(this);
                sendGetVersionCommand();
            }*/
        } else if (data[0] == NotificationEvent.DATA_GET_OTA_STATE) {
            delayHandler.removeCallbacks(deviceOtaStateTimeoutTask);
            int otaState = data[1];
            log("OTA State response--" + otaState);
            if (this.mode == MODE_IDLE) return;
            if (otaState == NotificationEvent.OTA_STATE_IDLE && this.mode == MODE_MESH_OTA && !isMeshOTAComplete) {
                startUpdate();
            } else if (otaState == NotificationEvent.OTA_STATE_MASTER &&
                    (this.mode == MODE_MESH_OTA || this.mode == MODE_CONTINUE_MESH_OTA)) {
                /*if (this.mode == MODE_MESH_OTA) {
//                    sendStartMeshOTACommand();
                } else {
                    sendGetVersionCommand();
                }*/
            } else if (otaState == NotificationEvent.OTA_STATE_COMPLETE && isMeshOTAComplete) {
                log("mesh ota complete");
                msgHandler.obtainMessage(MSG_MESH_OTA_PROGRESS, "MeshOTA -- complete").sendToTarget();
                this.mode = MODE_IDLE;
                Mesh mesh = TelinkLightApplication.getApp().getMesh();
                mesh.otaDevice = null;
                mesh.saveOrUpdate(this);
                for (Light light : onlineLights) {
                    light.selected = false;
                }
                sendGetVersionCommand();
            } else {
                log("OTA State error: " + otaState);
                sendStopMeshOTACommand();
                doComplete();
            }
        } else if (data[0] == NotificationEvent.DATA_SET_OTA_MODE_NOTIFY) {
            log("set OTA mode notify:" + data[1]);
            delayHandler.removeCallbacks(otaModeCmdCheckTask);
            if (data[1] == 0x00) {
                log("OTA firmware pushing ...");
                TelinkLightService.Instance().startOta(mFirmwareData);
            } else {
                onMeshOTANotSupport();
            }
        }

    }

    private void onDeviceEvent(DeviceEvent event) {
        int status = event.getArgs().status;
        switch (status) {
            case LightAdapter.STATUS_LOGOUT:
                TelinkLog.i("OTAUpdate#STATUS_LOGOUT");
                log("logout");

                if (this.mode != MODE_IDLE) {
                    if (this.mode == MODE_MESH_OTA && !isOTAComplete) {
                        log("OTA fail, click start to retry");
                        this.mode = MODE_IDLE;
                        enableUI(true);
                    } else {
                        startScan();
                    }
                }


                break;

            case LightAdapter.STATUS_LOGIN:
                TelinkLog.i("OTAUpdate#STATUS_LOGIN");
                log("login success");
                if (this.mode == MODE_IDLE) return;
                TelinkLightService.Instance().enableNotification();
                if (this.mode == MODE_MESH_OTA || this.mode == MODE_CONTINUE_MESH_OTA) {
                    TelinkLightService.Instance().enableNotification();
                    sendGetDeviceOtaStateCommand();
                }
                break;

            case LightAdapter.STATUS_CONNECTED:
                log("connected");
                if (this.mode != MODE_IDLE)
                    login();
                break;

            case LightAdapter.STATUS_OTA_PROGRESS:
                OtaDeviceInfo deviceInfo = (OtaDeviceInfo) event.getArgs();
//                log("ota progress :" + deviceInfo.progress + "%");
                msgHandler.obtainMessage(MSG_MESH_OTA_PROGRESS, "firmware push -- " + deviceInfo.progress + "%").sendToTarget();
                break;

            case LightAdapter.STATUS_OTA_COMPLETED:
                log("OTA complete");
                log("mesh OTA processing...");
                isOTAComplete = true;

                msgHandler.obtainMessage(MSG_MESH_OTA_PROGRESS, "firmware push -- complete").sendToTarget();

                // 开始meshOTA
                mesh.otaDevice = new OtaDevice();
                DeviceInfo curDevice = TelinkLightApplication.getApp().getConnectDevice();
                mesh.otaDevice.mac = curDevice.macAddress;
                mesh.otaDevice.meshName = mesh.name;
                mesh.otaDevice.meshPwd = mesh.password;
                mesh.saveOrUpdate(this);


                break;

            case LightAdapter.STATUS_OTA_FAILURE:
                log("OTA fail");
//                startScan();
                break;
        }
    }

    private void doComplete() {
        this.mode = MODE_IDLE;
        isOTAComplete = false;
        isMeshOTAComplete = false;
        Mesh mesh = TelinkLightApplication.getApp().getMesh();
        mesh.otaDevice = null;
        mesh.saveOrUpdate(this);
        log("action finish!");
        enableUI(true);
    }


    AlertDialog.Builder mScanTimeoutDialog;

    public void onScanTimeout() {
        if ((this.mode == MODE_MESH_OTA && isOTAComplete) || this.mode == MODE_CONTINUE_MESH_OTA) {

            if (mScanTimeoutDialog == null) {
                mScanTimeoutDialog = new AlertDialog.Builder(this);
                mScanTimeoutDialog.setTitle("Warning!");
                mScanTimeoutDialog.setMessage("MeshOTA Connect Fail Quit?");
                mScanTimeoutDialog.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doComplete();
                    }
                });
                mScanTimeoutDialog.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startScan();
                        dialog.dismiss();
                    }
                });
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mScanTimeoutDialog.show();
                }
            });
        } else {
            doComplete();
        }

    }


    private void log(String log) {
        msgHandler.obtainMessage(MSG_LOG, log).sendToTarget();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        if (this.mode == MODE_MESH_OTA || this.mode == MODE_CONTINUE_MESH_OTA) {
            this.sendStopMeshOTACommand();
        }
        this.mode = MODE_IDLE;
        TelinkLightService.Instance().idleMode(false);
        TelinkLog.i("OTAUpdate#onStop#removeEventListener");
        TelinkLightApplication.getApp().removeEventListener(this);
    }


    AlertDialog.Builder mCancelBuilder;

    public void back() {
        if (this.mode == MODE_IDLE) {
            finish();
        } else {
            if (mCancelBuilder == null) {
                mCancelBuilder = new AlertDialog.Builder(this);
                mCancelBuilder.setTitle("Warning!");
                mCancelBuilder.setMessage("OTA Not Complete, Quit?");
                mCancelBuilder.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendStopMeshOTACommand();
                        Mesh mesh = TelinkLightApplication.getApp().getMesh();
                        mesh.otaDevice = null;
                        mesh.saveOrUpdate(MeshOTAActivity_h.this);
                        dialog.dismiss();
                        finish();
                    }
                });
                mCancelBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
            mCancelBuilder.show();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void parseFile(String filePath) {
        try {
            byte[] version = new byte[4];
            InputStream stream = new FileInputStream(filePath);
            int length = stream.available();
            mFirmwareData = new byte[length];
            stream.read(mFirmwareData);

            stream.close();
            System.arraycopy(mFirmwareData, 2, version, 0, 4);
            mFileVersion = new String(version);
        } catch (Exception e) {
            mFileVersion = null;
            mFirmwareData = null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCode == REQUEST_CODE_CHOOSE_FILE &&
        if (resultCode == RESULT_OK) {
            mTypeAdapter.insertFileInfo(requestCode, data.getStringExtra("path"));
            /*mPath = data.getStringExtra("path");
            tv_file.setText(getString(R.string.select_file, mPath));
            parseFile();*/
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                back();
                break;

            case R.id.btn_start:
                start();
                break;

            case R.id.btn_check:
                sendGetDeviceOtaStateCommand();
                break;
        }
    }

    private Light getLightByMeshAddress(int meshAddress) {
        if (onlineLights == null || onlineLights.size() == 0) return null;
        for (Light light : onlineLights) {
            if (light.meshAddress == meshAddress) {
                return light;
            }
        }
        return null;
    }

    private void chooseFile() {
        startActivityForResult(new Intent(this, FileSelectActivity.class), REQUEST_CODE_CHOOSE_FILE);
    }

    private boolean hasLight(int meshAddress) {
        for (Light light : onlineLights) {
            if (light.meshAddress == meshAddress) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否可升级
     * 0:最新， 1：可升级， -n：null
     */
    public int compareVersion(String lightVersion, String newVersion) {

//        return lightVersion.equals(newVersion) ? 0 : 1;
        if (lightVersion == null || newVersion == null) {
            return 0;
        }
        int compareResult = newVersion.compareTo(lightVersion);

        return compareResult == 0 ? 0 : 1;
//        return compareResult > 1 ? 1 : compareResult;
    }
}
