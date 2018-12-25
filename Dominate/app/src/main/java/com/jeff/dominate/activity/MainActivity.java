package com.jeff.dominate.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.jeff.dominate.MeshOTAService;
import com.jeff.dominate.R;
import com.jeff.dominate.TelinkLightApplication;
import com.jeff.dominate.TelinkLightService;
import com.jeff.dominate.TelinkMeshErrorDealActivity;
import com.jeff.dominate.fragments.DeviceListFragment;
import com.jeff.dominate.fragments.GroupListFragment;
import com.jeff.dominate.fragments.MainFragment;
import com.jeff.dominate.fragments.MeFragment;
import com.jeff.dominate.model.Light;
import com.jeff.dominate.model.Lights;
import com.jeff.dominate.model.Mesh;
import com.jeff.dominate.util.FragmentFactory;
import com.jeff.dominate.util.MeshCommandUtil;
import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.ErrorReportEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.ErrorReportInfo;
import com.telink.bluetooth.light.GetAlarmNotificationParser;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.BuildUtils;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.List;

import utils.LogUtils;

public final class MainActivity extends TelinkMeshErrorDealActivity implements EventListener<String> {

    private final static String TAG = MainActivity.class.getSimpleName();

    private static final int UPDATE_LIST = 0;
    private FragmentManager fragmentManager;
    private DeviceListFragment deviceFragment;
    private GroupListFragment groupFragment;
    private MainFragment mainTestFragment;
    private MeFragment meFragment;

    private Fragment mContent;

    private RadioGroup tabs;

    private TelinkLightApplication mApplication;

    private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if (checkedId == R.id.tab_devices) {
                switchContent(mContent, deviceFragment);
            } else if (checkedId == R.id.tab_groups) {
                switchContent(mContent, groupFragment);
            } else if (checkedId == R.id.tab_main) {
                switchContent(mContent, mainTestFragment);
            }else if (checkedId==R.id.tab_me){
                switchContent(mContent, meFragment);

            }
        }
    };

    private int connectMeshAddress;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LIST:
                    deviceFragment.notifyDataSetChanged();
                    break;
            }
        }
    };

    private Handler mDelayHandler = new Handler();
    private int delay = 200;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "蓝牙开启");
                        TelinkLightService.Instance().idleMode(true);
                        autoConnect();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "蓝牙关闭");
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        //TelinkLog.ENABLE = false;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_main);

        this.mApplication = (TelinkLightApplication) this.getApplication();

        this.fragmentManager = this.getFragmentManager();

        this.deviceFragment = (DeviceListFragment) FragmentFactory
                .createFragment(R.id.tab_devices);
        this.groupFragment = (GroupListFragment) FragmentFactory
                .createFragment(R.id.tab_groups);
        this.mainTestFragment = (MainFragment) FragmentFactory
                .createFragment(R.id.tab_main);
        this.meFragment = (MeFragment) FragmentFactory
                .createFragment(R.id.tab_me);
        this.tabs = (RadioGroup) this.findViewById(R.id.tabs);
        this.tabs.setOnCheckedChangeListener(this.checkedChangeListener);

        if (savedInstanceState == null) {

            FragmentTransaction transaction = this.fragmentManager
                    .beginTransaction();
            transaction.add(R.id.content, this.deviceFragment).commit();

            this.mContent = this.deviceFragment;
        }

        this.mApplication.doInit();

        TelinkLog.d("-------------------------------------------");
        TelinkLog.d(Build.MANUFACTURER);
        TelinkLog.d(Build.TYPE);
        TelinkLog.d(Build.BOOTLOADER);
        TelinkLog.d(Build.DEVICE);
        TelinkLog.d(Build.HARDWARE);
        TelinkLog.d(Build.SERIAL);
        TelinkLog.d(Build.BRAND);
        TelinkLog.d(Build.DISPLAY);
        TelinkLog.d(Build.FINGERPRINT);

        TelinkLog.d(Build.PRODUCT + ":" + Build.VERSION.SDK_INT + ":" + Build.VERSION.RELEASE + ":" + Build.VERSION.CODENAME + ":" + Build.ID);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        registerReceiver(mReceiver, filter);

        checkPermission();
    }


    int PERMISSION_REQUEST_CODE = 0x10;

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                    // 显示解释权限用途的界面，然后再继续请求权限
                } else {
                    // 没有权限，直接请求权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CODE);
                }
            }
        }

    }

    @Override
    protected void onStart() {

        super.onStart();

        Log.d(TAG, "onStart");

        int result = BuildUtils.assetSdkVersion("4.4");
        Log.d(TAG, " Version : " + result);

        // 监听各种事件
        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        this.mApplication.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        this.mApplication.addEventListener(NotificationEvent.GET_ALARM, this);
        this.mApplication.addEventListener(NotificationEvent.GET_DEVICE_STATE, this);
        this.mApplication.addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        this.mApplication.addEventListener(MeshEvent.OFFLINE, this);

        this.mApplication.addEventListener(ErrorReportEvent.ERROR_REPORT, this);

        this.autoConnect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查是否支持蓝牙设备
        if (!LeBluetooth.getInstance().isSupport(getApplicationContext())) {
            Toast.makeText(this, "ble not support", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        if (!LeBluetooth.getInstance().isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("开启蓝牙，体验智能灯!");
            builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LeBluetooth.getInstance().enable(getApplicationContext());
                }
            });
            builder.show();
        }

        DeviceInfo deviceInfo = this.mApplication.getConnectDevice();

        if (deviceInfo != null) {
            this.connectMeshAddress = this.mApplication.getConnectDevice().meshAddress & 0xFF;
        }

        Log.d(TAG, "onResume");
        /*mDelayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAlarm();
            }
        }, 1000);*/
    }

    public static void getAlarm() {
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE6, 0x0000, new byte[]{0x10, (byte) 0x00});
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        TelinkLightService.Instance().disableAutoRefreshNotify();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        this.mApplication.doDestroy();
        this.mDelayHandler.removeCallbacksAndMessages(null);
        //移除事件
        this.mApplication.removeEventListener(this);
        Lights.getInstance().clear();
    }

    /**
     * 自动重连
     */
    private void autoConnect() {
        if (TelinkLightService.Instance() != null) {

            if (TelinkLightService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {


                if (this.mApplication.isEmptyMesh())
                    return;

//                Lights.getInstance().clear();
                this.mApplication.refreshLights();


                this.deviceFragment.notifyDataSetChanged();

                Mesh mesh = this.mApplication.getMesh();

                if (TextUtils.isEmpty(mesh.name) || TextUtils.isEmpty(mesh.password)) {
                    TelinkLightService.Instance().idleMode(true);
                    return;
                }

                //自动重连参数
                LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                connectParams.setMeshName(mesh.name);
                connectParams.setPassword(mesh.password);
                connectParams.autoEnableNotification(true);

                // 之前是否有在做MeshOTA操作，是则继续
                if (mesh.isOtaProcessing()) {
                    connectParams.setConnectMac(mesh.otaDevice.mac);
//                    saveLog("Action: AutoConnect:" + mesh.otaDevice.mac);
                } else {
//                    saveLog("Action: AutoConnect:NULL");
                }
                //自动重连
                TelinkLightService.Instance().autoConnect(connectParams);
            }

            //刷新Notify参数
            LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
            refreshNotifyParams.setRefreshRepeatCount(2);
            refreshNotifyParams.setRefreshInterval(2000);
            //开启自动刷新Notify
            TelinkLightService.Instance().autoRefreshNotify(refreshNotifyParams);
        }
    }

    private void switchContent(Fragment from, Fragment to) {

        if (this.mContent != to) {
            this.mContent = to;

            FragmentTransaction transaction = this.fragmentManager
                    .beginTransaction();

            if (!to.isAdded()) {
                transaction.hide(from).add(R.id.content, to);
            } else {
                transaction.hide(from).show(to);
            }

            transaction.commit();
        }
    }

    private Handler mHanlder = new Handler();

    private void onDeviceStatusChanged(DeviceEvent event) {

        DeviceInfo deviceInfo = event.getArgs();

        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                this.connectMeshAddress = this.mApplication.getConnectDevice().meshAddress;
//                this.showToast("login success");
                if (TelinkLightService.Instance().getMode() == LightAdapter.MODE_AUTO_CONNECT_MESH) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE4, 0xFFFF, new byte[]{});
                        }
                    }, 3 * 1000);
                }

                if (TelinkLightApplication.getApp().getMesh().isOtaProcessing() && !MeshOTAService.isRunning) {
                    // 获取本地设备OTA状态信息
                    MeshCommandUtil.getDeviceOTAState();
                }
                break;
            case LightAdapter.STATUS_CONNECTING:
//                this.showToast("login");
                break;
            case LightAdapter.STATUS_LOGOUT:
//                this.showToast("disconnect");
                onLogout();
                break;

            case LightAdapter.STATUS_ERROR_N:
                onNError(event);
            default:
                break;
        }

    }

    private void onNError(final DeviceEvent event) {

        TelinkLightService.Instance().idleMode(true);
        TelinkLog.d("DeviceScanningActivity#onNError");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("当前环境:Android7.0!连接重试:" + " 3次失败!");
        builder.setNegativeButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void onLogout() {
        List<Light> lights = Lights.getInstance().get();
        for (Light light : lights) {
            light.connectionStatus = ConnectionStatus.OFFLINE;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceFragment.notifyDataSetChanged();
            }
        });
    }

    private void onAlarmGet(NotificationEvent notificationEvent) {
        GetAlarmNotificationParser.AlarmInfo info = GetAlarmNotificationParser.create().parse(notificationEvent.getArgs());
        if (info != null)
            TelinkLog.d("alarm info index: " + info.index);
    }


    /**
     * 处理{@link NotificationEvent#ONLINE_STATUS}事件
     */
    private synchronized void onOnlineStatusNotify(NotificationEvent event) {
        LogUtils.INSTANCE.d(TAG,"MainActivity#onOnlineStatusNotify#Thread ID : " + Thread.currentThread().getId());
        TelinkLog.i("MainActivity#onOnlineStatusNotify#Thread ID : " + Thread.currentThread().getId());
        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList;
        //noinspection unchecked
        notificationInfoList = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;

        /*if (this.deviceFragment != null) {
            this.deviceFragment.onNotify(notificationInfoList);
        }*/

        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {

            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;
            LogUtils.INSTANCE.d(TAG,"notificationInfo.toString()"+notificationInfo.toString());
            Light light = this.deviceFragment.getDevice(meshAddress);

            if (light == null) {
                light = new Light();
                this.deviceFragment.addDevice(light);
            }

            light.meshAddress = meshAddress;
            light.brightness = brightness;
            light.connectionStatus = notificationInfo.connectionStatus;

            if (light.meshAddress == this.connectMeshAddress) {
                light.textColor = R.color.theme_positive_color;
            } else {
                light.textColor = R.color.black;
            }
        }

        mHandler.obtainMessage(UPDATE_LIST).sendToTarget();
    }

    private void onServiceConnected(ServiceEvent event) {
        this.autoConnect();
    }

    private void onServiceDisconnected(ServiceEvent event) {

    }

    AlertDialog.Builder mTimeoutBuilder;

    private void onMeshOffline(MeshEvent event) {
        TelinkLog.w("auto connect offline");
        List<Light> lights = Lights.getInstance().get();
        for (Light light : lights) {
            light.connectionStatus = ConnectionStatus.OFFLINE;
        }
        this.deviceFragment.notifyDataSetChanged();

        if (TelinkLightApplication.getApp().getMesh().isOtaProcessing()) {
            TelinkLightService.Instance().idleMode(true);
            if (mTimeoutBuilder == null) {
                mTimeoutBuilder = new AlertDialog.Builder(this);
                mTimeoutBuilder.setTitle("AutoConnect Fail");
                mTimeoutBuilder.setMessage("Connect device:" + TelinkLightApplication.getApp().getMesh().otaDevice.mac + " Fail, Quit? \nYES: quit MeshOTA process, NO: retry");
                mTimeoutBuilder.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Mesh mesh = TelinkLightApplication.getApp().getMesh();
                        mesh.otaDevice = null;
                        mesh.saveOrUpdate(MainActivity.this);
                        autoConnect();
                        dialog.dismiss();
                    }
                });
                mTimeoutBuilder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        autoConnect();
                        dialog.dismiss();
                    }
                });
                mTimeoutBuilder.setCancelable(false);
            }
            mTimeoutBuilder.show();
        }
    }


    /**
     * 事件处理方法
     *
     * @param event
     */
    @Override
    public void performed(Event<String> event) {
        LogUtils.INSTANCE.d(TAG,"performed(Event<String> event)  event.getType()="+event.getType());
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                this.onOnlineStatusNotify((NotificationEvent) event);
                break;

            case NotificationEvent.GET_ALARM:
//                this.onAlarmGet((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                this.onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.OFFLINE:
                this.onMeshOffline((MeshEvent) event);
                break;
            case ServiceEvent.SERVICE_CONNECTED:
                this.onServiceConnected((ServiceEvent) event);
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
                this.onServiceDisconnected((ServiceEvent) event);
                break;
            case NotificationEvent.GET_DEVICE_STATE:
                onNotificationEvent((NotificationEvent) event);
                break;

            case ErrorReportEvent.ERROR_REPORT:
                ErrorReportInfo info = ((ErrorReportEvent) event).getArgs();
                TelinkLog.d("MainActivity#performed#ERROR_REPORT: " + " stateCode-" + info.stateCode
                        + " errorCode-" + info.errorCode
                        + " deviceId-" + info.deviceId);
                break;
        }
    }

    @Override
    protected void onLocationEnable() {
        autoConnect();
    }


    private void onNotificationEvent(NotificationEvent event) {
        if (!foreground) return;
        // 解析版本信息

        byte[] data = event.getArgs().params;

        if (data[0] == NotificationEvent.DATA_GET_MESH_OTA_PROGRESS) {
            /*if (!MeshOTAService.isRunning) {
                Intent serviceIntent = new Intent(this, MeshOTAService.class);
                serviceIntent.putExtra(MeshOTAService.INTENT_KEY_INIT_MODE, MeshOTAService.MODE_CONTINUE_MESH_OTA);
                startService(serviceIntent);
            }*/
        } else if (data[0] == NotificationEvent.DATA_GET_OTA_STATE) {
            if (TelinkLightApplication.getApp().getMesh().isOtaProcessing() && !MeshOTAService.isRunning) {
                if (data[1] == NotificationEvent.OTA_STATE_MASTER) {
                    Intent serviceIntent = new Intent(this, MeshOTAService.class);
                    serviceIntent.putExtra(MeshOTAService.INTENT_KEY_INIT_MODE, MeshOTAService.MODE_CONTINUE_MESH_OTA);
                    startService(serviceIntent);
                } else if (data[1] == NotificationEvent.OTA_STATE_COMPLETE) {
                    Mesh mesh = TelinkLightApplication.getApp().getMesh();
                    mesh.otaDevice = null;
                    mesh.saveOrUpdate(this);

                    Intent serviceIntent = new Intent(this, MeshOTAService.class);
                    serviceIntent.putExtra(MeshOTAService.INTENT_KEY_INIT_MODE, MeshOTAService.MODE_COMPLETE);
                    startService(serviceIntent);
                } else if (data[1] == NotificationEvent.OTA_STATE_IDLE) {
                    MeshCommandUtil.sendStopMeshOTACommand();
                }
            }
        }


    }

}