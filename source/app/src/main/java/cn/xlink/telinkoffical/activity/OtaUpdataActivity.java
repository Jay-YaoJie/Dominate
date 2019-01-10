package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeOtaParameters;
import com.telink.bluetooth.light.LeScanParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.OtaDeviceInfo;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.telink.util.EventListener;
import com.telink.util.Strings;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.bean.greenDao.PlaceSort;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.manage.PlaceManage;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.model.Places;
import cn.xlink.telinkoffical.service.TelinkLightService;
import cn.xlink.telinkoffical.utils.LightsDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/4/6.
 */
public class OtaUpdataActivity extends BaseActivity {

    private static int timeout = 17000;

    @Bind(R.id.act_ota_udata_title)
    TitleBar titleBar;

    @Bind(R.id.act_ota_udata_success)
    TextView successNumView;

    private List<Integer> hadUpdataMeshList = new ArrayList<>();

//    private List<Light> lights = new ArrayList<>();

    private List<DeviceHolder> deviceHolders = new ArrayList<>();

    private DeviceHolder curLight;

    private int failCount = 0;

    private int successCount = 0;

    private boolean isRemoveOTAProg = false;

    private Handler handler = new Handler();

    private Runnable delayUpdata = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e("ota progress", "ota : overtime");
            failCount++;
//            failMsg = failMsg + "\n" + curLight.name + " : ota fail";
            startScan();
        }
    };

    private Runnable scaneRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("ota progress", "scane : fail");
            failCount++;
//            failMsg = failMsg + "\n" + " : scane fail";
            startScan();
        }
    };

    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("ota progress", "connect : overtime");
            reTry();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ota_updata);
        MyApp.getApp().setIsFindOrOta(true);
        ButterKnife.bind(this);
        initData();
        initView();

        MyApp.getApp().addEventListener(DeviceEvent.STATUS_CHANGED, telinkEventListener);
        MyApp.getApp().addEventListener(LeScanEvent.LE_SCAN, telinkEventListener);
        MyApp.getApp().addEventListener(LeScanEvent.LE_SCAN_COMPLETED, telinkEventListener);
        MyApp.getApp().addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, telinkEventListener);

        startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        MyApp.getApp().removeEventListener(telinkEventListener);
        handler.removeCallbacks(runnable);
        handler.removeCallbacks(scaneRunnable);
        handler.removeCallbacks(connectRunnable);
        MyApp.getApp().setIsFindOrOta(false);
    }

    @Override
    protected void back() {
        showCancelDialog();
    }

    @Override
    protected void initData() {
        int meshAddress = getIntent().getExtras().getInt(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH);
        if (meshAddress == -1) {
            for (Light onlight : Lights.getInstance().get()) {
                if (onlight.isCanUpdata() == 1) {
                    deviceHolders.add(new DeviceHolder(onlight));
                    CmdManage.setLightStatus(onlight, ConnectionStatus.OFF);
                }
            }
        } else {
            Light light = Lights.getInstance().getByMeshAddress(meshAddress);
            deviceHolders.add(new DeviceHolder(light));
            CmdManage.setLightStatus(light, ConnectionStatus.OFF);
        }
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(getString(R.string.updataing));
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog();
            }
        });
    }

    private void startScan() {

        curLight = null;

        handler.removeCallbacks(runnable);
        handler.removeCallbacks(scaneRunnable);
        handler.removeCallbacks(connectRunnable);

        if ((failCount + successCount) == deviceHolders.size()) {
            MyApp.getApp().autoConnect(false);
            updataText();
            showFinishDialog();
            return;
        }
        updataText();

        Log.e("ota progress", "startScan: ");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LeScanParameters params = Parameters.createScanParameters();
                params.setMeshName(Places.getInstance().getCurPlaceSort().getMeshAddress());
                params.setTimeoutSeconds(15);
                TelinkLightService.Instance().startScan(params);
                handler.postDelayed(scaneRunnable, timeout);
                Log.e("ota progress", "start by startScan ");
            }
        }, 1500);

    }

    private void startOta(Light light) {
        TelinkLightService.Instance().startOta(MyApp.getApp().getFirmware());
    }

    private void onDeviceEvent(DeviceEvent event) {
        switch (event.getType()) {
            case DeviceEvent.STATUS_CHANGED:
                int status = event.getArgs().status;
                if (status == LightAdapter.STATUS_OTA_PROGRESS) {
                    if (!isRemoveOTAProg) {
                        handler.removeCallbacks(runnable);
                        isRemoveOTAProg = true;
                    }
                    OtaDeviceInfo deviceInfo = (OtaDeviceInfo) event.getArgs();
                    Log.e("ota progress", "ota progress :" + deviceInfo.progress + "%");
//                    handler.postDelayed(runnable, 5000);
                } else if (status == LightAdapter.STATUS_CONNECTED) {
                    if (curLight != null) {
                        Log.e("ota progress", "connected : get firmware");
                        TelinkLightService.Instance().getFirmwareVersion();
                    }
                } else if (status == LightAdapter.STATUS_LOGOUT) {
                    TelinkLog.d("-------------------------");
                    TelinkLog.d("断开连接 : " + event.getArgs().macAddress);
                    TelinkLog.d("模式 : " + TelinkLightService.Instance().getMode());

                    if (curLight != null && getDeviceHolder(curLight.light.getLightSort().getMeshAddress()).completed) {
                        Log.e("ota progress", "ota success");
                        hadUpdataMeshList.add(curLight.light.getLightSort().getMeshAddress());
                        saveDataToDb(curLight.light);
                        successCount++;
                        updataText();
                        startScan();
                    } else if (curLight != null) {
                        reTry();
                    }

                } else if (status == LightAdapter.STATUS_OTA_COMPLETED) {
                    Log.e("ota progress", "STATUS_OTA_COMPLETED");
                    if (curLight != null) {
                        getDeviceHolder(curLight.light.getLightSort().getMeshAddress()).completed = true;
                    }
                } else if (status == LightAdapter.STATUS_OTA_FAILURE) {
                    reTry();
                } else if (status == LightAdapter.STATUS_GET_FIRMWARE_COMPLETED) {
                    handler.removeCallbacks(connectRunnable);
                    DeviceInfo deviceInfo = event.getArgs();
                    Log.e("ota progress", "firmware :" + deviceInfo.firmwareRevision);
                    if (curLight != null) {
                        curLight.mac = deviceInfo.macAddress;
//                        startOta(curLight);
                        login();
                    }
                } else if (status == LightAdapter.STATUS_GET_FIRMWARE_FAILURE) {
                    Log.e("ota progress", "get firmware fail");
                    Log.e("ota progress", "ota fail");
                    handler.removeCallbacks(connectRunnable);
                    failCount++;
                    startScan();
                }else if (status == LightAdapter.STATUS_LOGIN) {
                    if (curLight != null) {
                        com.telink.bluetooth.light.DeviceInfo deviceInfo = event.getArgs();
                        startOta(curLight.light);
                    }
                }
                break;
        }
    }

    private void onLeScanEvent(LeScanEvent event) {
        switch (event.getType()) {
            case LeScanEvent.LE_SCAN:

                DeviceInfo deviceInfo = event.getArgs();
                Log.e("ota progress", "LE_SCAN : " + deviceInfo.macAddress);
                if (!hadUpdataMeshList.contains(deviceInfo.meshAddress) && curLight == null) {
                    for (DeviceHolder deviceHolder : deviceHolders) {
                        if (deviceInfo.meshAddress == deviceHolder.light.getLightSort().getMeshAddress().byteValue()) {
                            curLight = deviceHolder;
                            connectDevice(deviceInfo.macAddress);
                            handler.removeCallbacks(scaneRunnable);
                            break;
                        }
                    }
                }
                break;
            case LeScanEvent.LE_SCAN_COMPLETED:
                Log.e("ota progress", "LE_SCAN_COMPLETED");
                break;
        }
    }

    private EventListener telinkEventListener = new EventListener() {

        @Override
        public void performed(Event event) {
            if (event instanceof LeScanEvent) {
                onLeScanEvent((LeScanEvent) event);
            } else if (event instanceof DeviceEvent) {
                onDeviceEvent((DeviceEvent) event);
            }
        }
    };

    public void connectDevice(String mac) {
        Log.e("ota progress", "connectDevice :" + mac);
        TelinkLightService.Instance().idleMode(true);
        if (TelinkLightService.Instance().connect(mac, 15)) {
            handler.postDelayed(connectRunnable, timeout);
        } else {
            failCount++;
            startScan();
        }
    }

    private void login() {
        PlaceSort currentMesh = Places.getInstance().getCurPlaceSort();
        TelinkLightService.Instance().login(Strings.stringToBytes(currentMesh.getMeshAddress(), 16), Strings.stringToBytes(currentMesh.getMeshKey(), 16));
    }

    private void showFinishDialog() {
        showOneButtonDialog(successCount + getString(R.string.updata_lights), false, getString(R.string.enter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showCancelDialog() {
        showTipsDialog(getString(R.string.updata_cancel), getString(R.string.enter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updataText() {
        successNumView.setText(successCount + "");
    }

    private void saveDataToDb(Light light) {
        light.getLightSort().setFirmwareRevision(MyApp.getApp().getVersion() + "");

        LightsDbUtils.getInstance().updataOrInsert(light.getLightSort());
        DataToHostManage.updataCurToHost();
    }

    private void reTry() {
        if (curLight != null && getDeviceHolder(curLight.light.getLightSort().getMeshAddress()).failureCount < 2) {
            Log.e("ota progress", "ota fail times: " + getDeviceHolder(curLight.light.getLightSort().getMeshAddress()).failureCount);
            getDeviceHolder(curLight.light.getLightSort().getMeshAddress()).failureCount++;
        } else {
            failCount++;
//            failMsg = failMsg + "\n" + curLight.name + " : ota fail ";
            hadUpdataMeshList.add(curLight.light.getLightSort().getMeshAddress());
        }
        startScan();
    }

    public DeviceHolder getDeviceHolder(int mesh) {
        for (DeviceHolder deviceHolder : deviceHolders) {
            if (deviceHolder.light.getLightSort().getMeshAddress() == mesh) {
                return deviceHolder;
            }
        }
        return null;
    }

    private static class DeviceHolder {
        public Light light;
        public String mac;
        public int failureCount;
        public boolean completed;

        public DeviceHolder(Light light) {
            this.light = light;
        }
    }

}
