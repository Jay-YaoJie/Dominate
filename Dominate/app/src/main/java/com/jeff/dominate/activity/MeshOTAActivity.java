package com.jeff.dominate.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeff.dominate.MeshOTAService;
import com.jeff.dominate.R;
import com.jeff.dominate.TelinkBaseActivity;
import com.jeff.dominate.TelinkLightApplication;
import com.jeff.dominate.TelinkLightService;
import com.jeff.dominate.adapter.BaseRecyclerViewAdapter;
import com.jeff.dominate.adapter.TypeSelectAdapter;
import com.jeff.dominate.model.Light;
import com.jeff.dominate.model.Lights;
import com.jeff.dominate.model.Mesh;
import com.jeff.dominate.model.MeshDeviceType;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.DeviceInfo;

import com.telink.util.Event;
import com.telink.util.EventListener;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 演示mesh ota相关功能
 * 要求， 准备升级的设备必须是之前做过加灯操作的
 * 流程：
 * 1. 判断当前mesh在线状态， 如果是非在线状态，则不可操作
 * 2. 获取所有设备的状态信息， 并刷新设备列表 类型列表{@link MeshOTAActivity#rv_type}；
 * -> 由用户选择firmware版本并勾选对应升级的设备类型
 * -> 点击开始后判断是否选择设备类型和对应文件
 * -> 无误后获取设备的OTA状态{@link #sendGetDeviceOtaStateCommand()}
 * -> 获取失败， 提示不支持； 获取成功后， 设置设备的1
 */
public class MeshOTAActivity extends TelinkBaseActivity implements EventListener<String>, View.OnClickListener {

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

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesh_ota);
        mTimeFormat = new SimpleDateFormat("HH:mm:ss.S");
//        TelinkLightService.Instance().idleMode(false);
        // 获取所有 【存储于本地】&【在线】 设备
        onlineLights = Lights.getInstance().getLocalList(true);
        initView();

        log("local online lights:" + (onlineLights == null ? 0 : onlineLights.size()));
        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_KEY_CONTINUE_MESH_OTA)) {
//            this.mode = MODE_CONTINUE_MESH_OTA;
            this.mFileVersion = TelinkLightApplication.getApp().getConnectDevice().firmwareRevision;
            log("continue mesh ota");
            enableUI(false);
        }

        opDevice = TelinkLightApplication.getApp().getConnectDevice();
        if (opDevice == null || onlineLights == null || onlineLights.isEmpty()) {
            log("offline or no valid device!");
            enableUI(false);
        } else {

            log("direct device:" + opDevice.macAddress);
            addEventListener();
            addServiceReceiver();
            mesh = TelinkLightApplication.getApp().getMesh();
            if (TelinkLightApplication.getApp().isEmptyMesh()) {
                toast("Mesh Error!");
                log("mesh empty");
                enableUI(false);
            } else {
                if (MeshOTAService.getInstance() != null && MeshOTAService.getInstance().getMode() != MeshOTAService.MODE_IDLE) {
                    log("mesh ota already running");
                    enableUI(false);
                }
            }
        }
    }


    private void addServiceReceiver() {
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) return;
                switch (intent.getAction()) {
                    case MeshOTAService.ACTION_LOG:
                        log(intent.getStringExtra(MeshOTAService.INFO_LOG));
                        break;
                    case MeshOTAService.ACTION_STATUS_CHANGE:
                        int state = intent.getIntExtra(MeshOTAService.INFO_STATE, 0);
                        String stateDesc = intent.getStringExtra(MeshOTAService.INFO_STATE_DESC);
                        log("state change: " + stateDesc);

                        switch (state) {
                            case MeshOTAService.OTA_STATE_FAIL:
                            case MeshOTAService.OTA_STATE_COMPLETE:
                                enableUI(true);
                        }
                        break;
                }
            }
        };

        final IntentFilter filter = new IntentFilter();
        filter.addAction(MeshOTAService.ACTION_LOG);
        filter.addAction(MeshOTAService.ACTION_STATUS_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
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

        if (mReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
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

            selectType = deviceType;
            enableUI(false);

            if (!MeshOTAService.isRunning) {
                Intent serviceIntent = new Intent(this, MeshOTAService.class);
                serviceIntent.putExtra(MeshOTAService.INTENT_KEY_OTA_MODE, MeshOTAService.MODE_IDLE);
                serviceIntent.putExtra(MeshOTAService.INTENT_KEY_OTA_TYPE, selectType.type);
                serviceIntent.putExtra(MeshOTAService.INTENT_KEY_OTA_DEVICE, opDevice);
                serviceIntent.putExtra(MeshOTAService.INTENT_KEY_OTA_FIRMWARE, mFirmwareData);
                startService(serviceIntent);
            } else {
                if (MeshOTAService.getInstance() != null && MeshOTAService.getInstance().getMode() == MeshOTAService.MODE_IDLE) {
                    Intent intent = new Intent();
                    intent.putExtra(MeshOTAService.INTENT_KEY_OTA_MODE, MeshOTAService.MODE_IDLE);
                    intent.putExtra(MeshOTAService.INTENT_KEY_OTA_TYPE, selectType.type);
                    intent.putExtra(MeshOTAService.INTENT_KEY_OTA_DEVICE, opDevice);
                    intent.putExtra(MeshOTAService.INTENT_KEY_OTA_FIRMWARE, mFirmwareData);
                    MeshOTAService.getInstance().restart(intent);
                }
            }

            for (Light light :
                    onlineLights) {
                log("init version : " + light.meshAddress + " -- " + light.macAddress + light.firmwareRevision);
            }
        }
    }


    // 获取本地设备OTA状态信息
    private void sendGetDeviceOtaStateCommand() {
        byte opcode = (byte) 0xC7;
        int address = 0x0000;
        byte[] params = new byte[]{0x20, 0x05};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                params);
        log("getDeviceOtaState(0xC7)");
        delayHandler.postDelayed(deviceOtaStateTimeoutTask, 1000);
    }

    private Runnable deviceOtaStateTimeoutTask = new Runnable() {
        @Override
        public void run() {
            log("get device ota state timeout!");
        }
    };

    AlertDialog.Builder mScanTimeoutDialog;

    public void onScanTimeout() {

        if (mScanTimeoutDialog == null) {
            mScanTimeoutDialog = new AlertDialog.Builder(this);
            mScanTimeoutDialog.setTitle("Warning!");
            mScanTimeoutDialog.setMessage("MeshOTA Connect Fail Quit?");
            mScanTimeoutDialog.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mScanTimeoutDialog.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                        startScan();
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


    private void log(String log) {
        msgHandler.obtainMessage(MSG_LOG, log).sendToTarget();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
//        TelinkLightService.Instance().idleMode(false);
        TelinkLog.i("OTAUpdate#onStop#removeEventListener");
        TelinkLightApplication.getApp().removeEventListener(this);
    }


    AlertDialog.Builder mCancelBuilder;

    public void back() {
        finish();
        /*{
            if (mCancelBuilder == null) {
                mCancelBuilder = new AlertDialog.Builder(this);
                mCancelBuilder.setTitle("Warning!");
                mCancelBuilder.setMessage("OTA processing, Quit?");
                mCancelBuilder.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        sendStopMeshOTACommand();
                        Mesh mesh = TelinkLightApplication.getApp().getMesh();
                        mesh.otaDevice = null;
                        mesh.saveOrUpdate(MeshOTAActivity.this);
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
        }*/
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
        if (resultCode == RESULT_OK) {
            mTypeAdapter.insertFileInfo(requestCode, data.getStringExtra("path"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                back();
                break;

            case R.id.btn_start:
//                ProgressViewManager.getInstance().createFloatWindow(this);

                start();
                break;

            case R.id.btn_check:

//                ProgressViewManager.getInstance().removeFloatWindowManager();
                sendGetDeviceOtaStateCommand();
                break;
        }
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

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(NotificationEvent.GET_DEVICE_STATE)) {
            byte[] data = ((NotificationEvent) event).getArgs().params;
            if (data[0] == NotificationEvent.DATA_GET_OTA_STATE) {
                delayHandler.removeCallbacks(deviceOtaStateTimeoutTask);
                log("OTA State response--" + data[1]);
            }
        }
    }


}
