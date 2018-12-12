package com.telink.bluetooth.light.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeScanParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.OtaDeviceInfo;
import com.telink.bluetooth.light.Parameters;
import com.telink.bluetooth.light.R;
import com.telink.bluetooth.light.TelinkLightApplication;
import com.telink.bluetooth.light.TelinkLightService;
import com.telink.bluetooth.light.TelinkMeshErrorDealActivity;
import com.telink.bluetooth.light.model.Light;
import com.telink.bluetooth.light.model.Lights;
import com.telink.bluetooth.light.model.Mesh;
import com.telink.bluetooth.light.model.OtaDevice;
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
 * 升级页面
 * 思路：
 * 1.由于demo中没有对设备持久化操作，只有通过online_status 来添加和更新设备，
 * 而online_status返回的数据中只有meshAddress能判断设备唯一性
 * 在OTA升级过程中会保存此时现在的所有设备信息（onlineLights），
 * 如果是从MainActivity页面跳转而来（1.自动连接的设备有上报MeshOTA进度信息；2主动连接之前本地保存的设备），需要读取一次版本信息\n
 * 并初始化onlineLights
 * 1. {@link MainActivity#onNotificationEvent(NotificationEvent)}；
 * 2. {@link MainActivity#onDeviceStatusChanged(DeviceEvent)}；
 * <p>
 * 在开始OTA或者MeshOTA之前都会获取当前设备的OTA状态信息 {@link OTAUpdateActivity_h_0419#sendGetDeviceOtaStateCommand()},
 * \n\t 并通过 {@link OTAUpdateActivity_h_0419#onNotificationEvent(NotificationEvent)}返回状态， 处理不同模式下的不同状态
 * 在continue MeshOTA和MeshOTA模式下 {@link OTAUpdateActivity_h_0419#MODE_CONTINUE_MESH_OTA},{@link OTAUpdateActivity_h_0419#MODE_MESH_OTA}
 * <p>
 * <p>
 * 校验通过后，会开始动作
 * <p>
 * <p>
 * Action Start by choose correct bin file!
 * <p>
 * Created by Administrator on 2017/4/20.
 */
public class OTAUpdateActivity_h_0419 extends TelinkMeshErrorDealActivity implements EventListener<String>, View.OnClickListener {
    private int mode = MODE_IDLE;
    private static final int MODE_IDLE = 1;
    private static final int MODE_OTA = 2;
    private static final int MODE_MESH_OTA = 4;
    private static final int MODE_CONTINUE_MESH_OTA = 8;
    private static final int MODE_COMPLETE = 16;

    public static final String INTENT_KEY_CONTINUE_MESH_OTA = "com.telink.bluetooth.light.INTENT_KEY_CONTINUE_MESH_OTA";
    // 有进度状态上报 时跳转进入的
    public static final int CONTINUE_BY_REPORT = 0x21;

    // 继续之前的OTA操作，连接指定设备
    public static final int CONTINUE_BY_PREVIOUS = 0x22;

    private int continueType = 0;

    private static final int REQUEST_CODE_CHOOSE_FILE = 11;

    //    private static final int MODE_SCAN = 8;
    private byte[] mFirmwareData;
    private List<Light> onlineLights;
    private Mesh mesh;
    private String mPath;
    private SimpleDateFormat mTimeFormat;
    private int successCount = 0;

    private TextView otaProgress;
    private TextView meshOtaProgress;
    private TextView tv_log, tv_version, tv_file;
    private View select;
    private ScrollView sv_log;

    private static final int MSG_OTA_PROGRESS = 11;
    private static final int MSG_MESH_OTA_PROGRESS = 12;
    private static final int MSG_LOG = 13;
    private Handler delayHandler = new Handler();
    private Handler visibleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ((View) msg.obj).setVisibility(msg.what);
        }
    };
    private Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_OTA_PROGRESS:
                    otaProgress.setText(getString(R.string.progress_ota, msg.obj.toString()));
                    break;

                case MSG_MESH_OTA_PROGRESS:
                    meshOtaProgress.setText(getString(R.string.progress_mesh_ota, msg.obj.toString()));
                    break;

                case MSG_LOG:
                    String time = mTimeFormat.format(Calendar.getInstance().getTimeInMillis());
                    tv_log.append("\n" + time + ":" + msg.obj.toString());

//                    int scroll_amount = tv_log.getBottom();
//                    tv_log.scrollTo(0, scroll_amount);
                    sv_log.fullScroll(View.FOCUS_DOWN);
//                    ((ScrollView) tv_log.getParent()).fullScroll(ScrollView.FOCUS_DOWN);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota_update);
//        TelinkLightService.Instance().idleMode(false);
        addEventListener();
        TelinkLightService.Instance().enableNotification();
        mesh = TelinkLightApplication.getApp().getMesh();
        if (mesh == null || TextUtils.isEmpty(mesh.name) || TextUtils.isEmpty(mesh.password)) {
            toast("Mesh Error!");
            finish();
            return;
        }
        mTimeFormat = new SimpleDateFormat("HH:mm:ss.S");
        initView();

        onlineLights = new ArrayList<>();
        for (Light light : Lights.getInstance().get()) {
            if (light.connectionStatus != ConnectionStatus.OFFLINE) {
                onlineLights.add(light);
            }
        }

        log("onlineLights:" + onlineLights.size());
        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_KEY_CONTINUE_MESH_OTA)) {
            this.mode = MODE_CONTINUE_MESH_OTA;
            this.mFileVersion = TelinkLightApplication.getApp().getConnectDevice().firmwareRevision;
            tv_version.setText(getString(R.string.target_version, mFileVersion));
            select.setEnabled(false);
            continueType = intent.getIntExtra(INTENT_KEY_CONTINUE_MESH_OTA, 0);
            if (continueType == CONTINUE_BY_PREVIOUS) {
                sendGetDeviceOtaStateCommand();
//                sendGetVersionCommand();
                log("continue mesh ota by previous OTA");
            } else {
                log("continue mesh ota by progress report");
            }
            visibleHandler.obtainMessage(View.VISIBLE, meshOtaProgress).sendToTarget();
//            meshOtaProgress.setVisibility(View.VISIBLE);
        }

    }

    private void initView() {
        otaProgress = (TextView) findViewById(R.id.progress_ota);
        meshOtaProgress = (TextView) findViewById(R.id.progress_mesh_ota);
        tv_log = (TextView) findViewById(R.id.tv_log);
        sv_log = (ScrollView) findViewById(R.id.sv_log);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_file = (TextView) findViewById(R.id.file);

        select = findViewById(R.id.select);
        findViewById(R.id.select).setOnClickListener(this);

        findViewById(R.id.back).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelinkLog.i("OTAUpdate#onStop#removeEventListener");
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


    /**
     * Action start: after get versions
     * hasHigh Confirm action OTA or MeshOTA
     * hasLow Confirm OTA needed
     */
    private void start() {

        boolean hasHigh = false;
        boolean hasLow = false;
        for (Light light : onlineLights) {
            if (light.firmwareRevision == null || light.firmwareRevision.equals("")) continue;
            int compare = compareVersion(light.firmwareRevision, mFileVersion);
            /*if (compare == 0) {
                hasHigh = true;
            } else*/
            if (compare == 1) {
                hasLow = true;
            }


        }

        if (hasLow) {
            DeviceInfo deviceInfo = TelinkLightApplication.getApp().getConnectDevice();
            if (deviceInfo != null) {
                Light light = getLightByMeshAddress(deviceInfo.meshAddress);
                if (light != null && compareVersion(light.firmwareRevision, mFileVersion) != 1) {
                    // 当前为高版本
                    startMeshOTA();
                } else {
                    // 当前为低版本
                    this.mode = MODE_OTA;
                    sendGetDeviceOtaStateCommand();
                }
            }


            /*if (hasHigh) {
                startMeshOTA();
            } else {
                this.mode = MODE_OTA;

                int curMeshAddress = TelinkLightApplication.getApp().getConnectDevice().meshAddress;
                Light light = getLightByMeshAddress(curMeshAddress);
                if (light != null && compareVersion(light.firmwareRevision, mFileVersion) == 1) {
                    sendGetDeviceOtaStateCommand();
                } else {
                    startScan();
                }


            }*/
        } else {
            log("No device need OTA! Idle");
            select.setEnabled(true);
            this.mode = MODE_IDLE;
        }
    }


    /**
     * 判断当前连接的设备是否是高版本
     * true: sendCommand
     * false: connectDevice
     */
    private void startMeshOTA() {
        this.mode = MODE_MESH_OTA;
        DeviceInfo deviceInfo = TelinkLightApplication.getApp().getConnectDevice();
        boolean action = false;
        if (deviceInfo != null) {
            for (Light light : onlineLights) {
                if (light.meshAddress == deviceInfo.meshAddress && light.firmwareRevision != null && light.firmwareRevision.equals(mFileVersion)) {
                    action = true;
                    break;
                }
            }
        }

        if (action) {
            sendGetDeviceOtaStateCommand();
        } else {
            // scan and connect high version
            startScan();
        }
    }

    // start
    private void sendStartMeshOTACommand() {
        // save mesh info
        mesh.otaDevice = new OtaDevice();
        DeviceInfo curDevice = TelinkLightApplication.getApp().getConnectDevice();
        mesh.otaDevice.mac = curDevice.macAddress;
        mesh.otaDevice.meshName = mesh.name;
        mesh.otaDevice.meshPwd = mesh.password;
        mesh.saveOrUpdate(this);

        visibleHandler.obtainMessage(View.VISIBLE, meshOtaProgress).sendToTarget();
//        meshOtaProgress.setVisibility(View.VISIBLE);
        byte opcode = (byte) 0xC6;
        int address = 0x0000;
        byte[] params = new byte[]{(byte) 0xFF, (byte) 0xFF};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                params);
        log("SendCommand 0xC6 startMeshOTA");
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
    private int OTA_STATE_TIMEOUT_MAX = 3;

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
                log("SendCommand 0xC7 getDeviceOtaState");
                otaStateTimeout++;
                delayHandler.postDelayed(this, 3000);
            } else {
                log("SendCommand 0xC7 getDeviceOtaState fail");
                delayHandler.removeCallbacks(this);
                if (mode == MODE_OTA) {
                    startOTA();
                } else if (mode == MODE_MESH_OTA) {
                    sendStartMeshOTACommand();
                } else if (mode == MODE_CONTINUE_MESH_OTA) {
                    sendGetVersionCommand();
                }
            }

        }
    };


    private List<Integer> versionDevices = new ArrayList<>();
    private int retryCount = 0;

    private void sendGetVersionCommand() {
        versionDevices.clear();
        byte opcode = (byte) 0xC7;
        int address = 0xFFFF;
        byte[] params = new byte[]{0x20, 0x00};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                params);
        log("SendCommand 0xC7 getVersion");
        // 转发次数 * () * interval + 500
        if (this.mode != MODE_COMPLETE)
            delayHandler.postDelayed(getVersionTask, 0x20 * 2 * 40 + 500);
    }

    private Runnable getVersionTask = new Runnable() {
        @Override
        public void run() {
            if (versionDevices.size() == onlineLights.size() || retryCount >= 2) {
                retryCount = 0;
                if (mode == MODE_IDLE) {
                    start();
                } else if (mode == MODE_CONTINUE_MESH_OTA || mode == MODE_MESH_OTA) {
                    if (hasLow()) {
                        sendStartMeshOTACommand();
                    } else {
                        log("No device need OTA! Stop");
                        doFinish();
                    }
                }
            } else {
                retryCount++;
                log("get version retry");
                sendGetDeviceOtaStateCommand();
            }
        }
    };

    private boolean hasLow() {
        boolean hasLow = false;
        for (Light light : onlineLights) {
            if (light.firmwareRevision == null || light.firmwareRevision.equals("")) continue;
            if (compareVersion(light.firmwareRevision, mFileVersion) == 1) {
                hasLow = true;
            }
        }

        return hasLow;
    }

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

    private void startOTA() {
        this.mode = MODE_OTA;
//        otaProgress.setVisibility(View.VISIBLE);
        visibleHandler.obtainMessage(View.VISIBLE, otaProgress).sendToTarget();

        if (TelinkLightApplication.getApp().getConnectDevice() != null) {
            TelinkLightService.Instance().startOta(mFirmwareData);
        } else {
            startScan();
        }
        log("startOTA ");
    }

    private String mFileVersion;


    public void connectDevice(String mac) {
        log("connectDevice :" + mac);
        TelinkLightService.Instance().idleMode(true);
        TelinkLightService.Instance().connect(mac, 15);
    }

    private void login() {
        log("login");
        TelinkLightService.Instance().login(Strings.stringToBytes(mesh.name, 16), Strings.stringToBytes(mesh.password, 16));
    }

    private boolean isConnectting = false;

    private void onLeScan(LeScanEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        Log.e("ota progress", "LE_SCAN : " + deviceInfo.macAddress);
        log("on scan : " + deviceInfo.macAddress);


        if (this.mode == MODE_MESH_OTA || this.mode == MODE_CONTINUE_MESH_OTA) {
            mesh = TelinkLightApplication.getApp().getMesh();
            if (mesh.isOtaProcessing() && mesh.otaDevice.mac.equals(deviceInfo.macAddress)) {
                log("Mesh OTA Target device discovered mesh Address:" + Integer.toHexString(deviceInfo.meshAddress) + " mac:" + deviceInfo.macAddress);
                if (!isConnectting) {
                    connectDevice(deviceInfo.macAddress);
                }
            } else {
                for (Light light : onlineLights) {
                    if (light.meshAddress == deviceInfo.meshAddress && mFileVersion.equals(light.firmwareRevision)) {
                        log("Mesh OTA Target device discovered mesh Address:" + Integer.toHexString(deviceInfo.meshAddress) + " mac:" + deviceInfo.macAddress);
                        connectDevice(deviceInfo.macAddress);
                        return;
                    }
                }
            }
        } else if (this.mode == MODE_OTA) {
            Light light = getLightByMeshAddress(deviceInfo.meshAddress);
            if (light != null && compareVersion(light.firmwareRevision, mFileVersion) == 1) {
                connectDevice(deviceInfo.macAddress);
            }
        } else if (this.mode == MODE_IDLE) {
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
//            meshAddress = src & 0xFF;
            if (!versionDevices.contains(meshAddress)) {
                versionDevices.add(meshAddress);
            }

            TelinkLog.w(" src:" + meshAddress + " get version success: " + version);
            log("getVersion:" + Integer.toHexString(meshAddress) + "  version:" + version);

            if (this.mode == MODE_CONTINUE_MESH_OTA) {
                if (!hasLight(meshAddress)) {
                    Light light = new Light();
                    light.meshAddress = meshAddress;
                    light.firmwareRevision = version;
                    onlineLights.add(light);
//                    log("addLight to online lights-- " + "meshAddress:" + meshAddress + " version:" + version);
                }
            } else {
                for (Light light : onlineLights) {
                    if (light.meshAddress == meshAddress) {
//                        log("version: " + version + " -- light version:" + light.firmwareRevision + " --mode: " + this.mode);
                        if (this.mode == MODE_COMPLETE) {
                            if (!version.equals(light.firmwareRevision)) {
                                successCount++;
                            }
                        }
                        light.firmwareRevision = version;
                    }
                }
            }

            if (this.mode == MODE_CONTINUE_MESH_OTA && meshAddress == TelinkLightApplication.getApp().getConnectDevice().meshAddress) {
                TelinkLightApplication.getApp().getConnectDevice().firmwareRevision = version;
                this.mFileVersion = version;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_version.setText(getString(R.string.target_version, mFileVersion));
                    }
                });
            }
        } else if (data[0] == NotificationEvent.DATA_GET_MESH_OTA_PROGRESS) {
            TelinkLog.w("mesh ota progress: " + data[1]);
            int progress = (int) data[1];
            msgHandler.obtainMessage(MSG_MESH_OTA_PROGRESS, progress + "%").sendToTarget();
            if (progress == 100) {
                this.mode = MODE_COMPLETE;
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doFinish();
                    }
                }, 5 * 1000);
                Mesh mesh = TelinkLightApplication.getApp().getMesh();
                mesh.otaDevice = null;
                mesh.saveOrUpdate(this);
                sendGetVersionCommand();
            }
        } else if (data[0] == NotificationEvent.DATA_GET_OTA_STATE) {
            delayHandler.removeCallbacks(deviceOtaStateTimeoutTask);
            int otaState = data[1];
            log("OTA State response--" + otaState);
            if (otaState == NotificationEvent.OTA_STATE_IDLE) {
                if (this.mode == MODE_OTA) {
                    startOTA();
                } else if (this.mode == MODE_MESH_OTA) {
                    sendStartMeshOTACommand();
                } else if (this.mode == MODE_CONTINUE_MESH_OTA) {
                    sendGetVersionCommand();
                }
            } else if (otaState == NotificationEvent.OTA_STATE_MASTER &&
                    (this.mode == MODE_MESH_OTA || this.mode == MODE_CONTINUE_MESH_OTA)) {
                if (this.mode == MODE_MESH_OTA) {
                    sendStartMeshOTACommand();
                } else {
                    sendGetVersionCommand();
                }
            } else {
                log("OTA State response: Busy!!! Stopped!--" + otaState);
                doFinish();
            }
        }

    }

    private void onDeviceEvent(DeviceEvent event) {
        int status = event.getArgs().status;
        switch (status) {
            case LightAdapter.STATUS_LOGOUT:
                TelinkLog.i("OTAUpdate#STATUS_LOGOUT");
                log("logout");
                if (this.mode != MODE_COMPLETE) {
                    startScan();
                }
                break;

            case LightAdapter.STATUS_LOGIN:
                TelinkLog.i("OTAUpdate#STATUS_LOGIN");
                log("login success");
                if (this.mode == MODE_COMPLETE) return;
                TelinkLightService.Instance().enableNotification();
                if (this.mode == MODE_OTA) {
                    sendGetDeviceOtaStateCommand();
                } else if (this.mode == MODE_MESH_OTA || this.mode == MODE_CONTINUE_MESH_OTA) {
                    TelinkLightService.Instance().enableNotification();
                    sendGetDeviceOtaStateCommand();
                } else if (this.mode == MODE_IDLE) {
                    if (this.mFirmwareData != null) {
                        sendGetVersionCommand();
                    }
                }
                break;

            case LightAdapter.STATUS_CONNECTED:
                log("connected");
                if (this.mode != MODE_COMPLETE)
                    login();
                break;

            case LightAdapter.STATUS_OTA_PROGRESS:
                OtaDeviceInfo deviceInfo = (OtaDeviceInfo) event.getArgs();
//                log("ota progress :" + deviceInfo.progress + "%");
                msgHandler.obtainMessage(MSG_OTA_PROGRESS, deviceInfo.progress + "%").sendToTarget();
                break;

            case LightAdapter.STATUS_OTA_COMPLETED:
                log("OTA complete");
                msgHandler.obtainMessage(MSG_OTA_PROGRESS, "OTA complete").sendToTarget();
                DeviceInfo deviceInfo_1 = event.getArgs();
                for (Light light : onlineLights) {
                    if (light.meshAddress == deviceInfo_1.meshAddress) {
                        light.firmwareRevision = mFileVersion;
                    }
                }

                successCount++;
                if (onlineLights.size() <= successCount) {
                    doFinish();
                } else {
                    this.mode = MODE_MESH_OTA;
                }
                break;

            case LightAdapter.STATUS_OTA_FAILURE:
                log("OTA fail");
                if (this.mode == MODE_COMPLETE) return;
                startScan();
                break;
        }
    }

    private void doFinish() {
        this.mode = MODE_COMPLETE;
        Mesh mesh = TelinkLightApplication.getApp().getMesh();
        mesh.otaDevice = null;
        mesh.saveOrUpdate(this);
        log("Finish: Success Count : " + successCount);
    }


    AlertDialog.Builder mScanTimeoutDialog;

    public void onScanTimeout() {
        if (this.mode == MODE_OTA || this.mode == MODE_IDLE) {
            doFinish();
        } else if (this.mode == MODE_MESH_OTA || this.mode == MODE_CONTINUE_MESH_OTA) {
            if (mScanTimeoutDialog == null) {
                mScanTimeoutDialog = new AlertDialog.Builder(this);
                mScanTimeoutDialog.setTitle("Warning!");
                mScanTimeoutDialog.setMessage("MeshOTA Connect Fail Quit?");
                mScanTimeoutDialog.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendStopMeshOTACommand();
                        Mesh mesh = TelinkLightApplication.getApp().getMesh();
                        mesh.otaDevice = null;
                        mesh.saveOrUpdate(OTAUpdateActivity_h_0419.this);
                        dialog.dismiss();
                        doFinish();
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
        this.mode = MODE_COMPLETE;
        TelinkLightService.Instance().idleMode(false);
        TelinkLog.i("OTAUpdate#onStop#removeEventListener");
        TelinkLightApplication.getApp().removeEventListener(this);
    }


    AlertDialog.Builder mCancelBuilder;

    public void back() {
        if (this.mode == MODE_COMPLETE || this.mode == MODE_IDLE) {
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
                        mesh.saveOrUpdate(OTAUpdateActivity_h_0419.this);
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

    private void parseFile() {
        try {
            byte[] version = new byte[4];
            InputStream stream = new FileInputStream(mPath);
            int length = stream.available();
            mFirmwareData = new byte[length];
            stream.read(mFirmwareData);

            stream.close();
            System.arraycopy(mFirmwareData, 2, version, 0, 4);
            mFileVersion = new String(version);
        } catch (Exception e) {
            mFileVersion = null;
            mFirmwareData = null;
            mPath = null;
        }
        //  || mFileVersion.charAt(0) != 'V'
        if (mFileVersion == null) {
            Toast.makeText(this, "File parse error!", Toast.LENGTH_SHORT).show();
            this.mPath = null;
            mFileVersion = null;
            tv_file.setText(getString(R.string.select_file, "NULL"));
            tv_version.setText("File parse error!");
        } else {
            tv_version.setText("File Version: " + mFileVersion);
            select.setEnabled(false);
            if (TelinkLightApplication.getApp().getConnectDevice() != null) {
                sendGetVersionCommand();
            } else {
                startScan();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_FILE && resultCode == RESULT_OK) {
            mPath = data.getStringExtra("path");
            tv_file.setText(getString(R.string.select_file, mPath));
            parseFile();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select:
                chooseFile();
                break;

            case R.id.back:
                back();
                break;
        }
    }

/*    private float getVersionValue(String version) {
        return Float.valueOf(version.substring(1));
    }*/

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
