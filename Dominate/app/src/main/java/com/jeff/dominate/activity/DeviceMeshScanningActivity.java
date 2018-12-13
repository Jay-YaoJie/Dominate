package com.jeff.dominate.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.jeff.dominate.R;
import com.jeff.dominate.TelinkBaseActivity;
import com.jeff.dominate.TelinkLightApplication;
import com.jeff.dominate.TelinkLightService;
import com.jeff.dominate.adapter.BaseRecyclerViewAdapter;
import com.jeff.dominate.model.Light;
import com.jeff.dominate.model.Mesh;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.*;

import com.telink.util.Arrays;
import com.telink.util.Event;
import com.telink.util.EventListener;
import com.telink.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * FF:FF:35:35:35:01
 */

/**
 * 以mesh形式批量加灯
 * 只会添加 telink_mesh1 的设备
 * 当前处于连接状态下: 切换为默认网络 -> loop (获取device信息 -> 分配deviceId) 直到连续{TIMEOUT_CNT}次timeout -> 设置name/pwd/ltk -> 切换为正常网络
 * <p>
 * 当前处于非连接状态下: 扫描telink_mesh1设备 -> 连接登录 -> 获取device信息 -> 分配deviceId -> 设置name/pwd/ltk
 */
public final class DeviceMeshScanningActivity extends TelinkBaseActivity implements EventListener<String> {
    private static final String LOG_TAG = "DeviceMeshScan -- ";


    private static final String DEFAULT_NAME = "telink_mesh1";
    private static final String DEFAULT_PASSWORD = "123";

    private static final int TIMEOUT_CNT_MAX = 2;
    private static final int GET_LIST_TIMEOUT = 2 * 1000;

    private int retryCnt = 0;
    private static final int CMD_RELAY_CNT = 0x10;
    private static final long COMPLETE_DELAY = 60 * 1000;
    //    private AtomicBoolean isSetProcessing = new AtomicBoolean(false);
    private GetMeshDeviceNotificationParser.MeshDeviceInfo processingDevice;
    private TelinkLightApplication mApplication;
    private Mesh mesh;
    private Button btn_back;
    private List<GetMeshDeviceNotificationParser.MeshDeviceInfo> mMeshDevices;
    private Handler delayHandler;
    private MeshDeviceListAdapter mAdapter;

    private boolean updateComplete;

    /**
     * 默认网络持续时间
     */
    private static final int DEFAULT_TIMEOUT_SEC = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesh_scanning);

        //监听事件
        this.mApplication = (TelinkLightApplication) this.getApplication();

        if (mApplication.isEmptyMesh()) {
            finish();
            Toast.makeText(mApplication, "mesh info null", Toast.LENGTH_SHORT).show();
//            showToast("mesh info null!");
            return;
        }
        this.mesh = mApplication.getMesh();

        this.mApplication.addEventListener(LeScanEvent.LE_SCAN, this);
        this.mApplication.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this);
        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        this.mApplication.addEventListener(MeshEvent.UPDATE_COMPLETED, this);
        this.mApplication.addEventListener(MeshEvent.ERROR, this);
        this.mApplication.addEventListener(NotificationEvent.GET_MESH_DEVICE_LIST, this);
        this.mApplication.addEventListener(NotificationEvent.UPDATE_MESH_COMPLETE, this);
        TelinkLightService.Instance().idleMode(false);
        mMeshDevices = new ArrayList<>();
        delayHandler = new Handler();
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setEnabled(false);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RecyclerView rl = (RecyclerView) findViewById(R.id.list_devices);
        rl.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new MeshDeviceListAdapter();
        rl.setAdapter(this.mAdapter);
        if (this.mApplication.getConnectDevice() != null) {
            setDefault();
        } else {
            startScan();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mApplication.removeEventListener(this);
        if (delayHandler != null) {
            delayHandler.removeCallbacksAndMessages(null);
        }
    }

    private void startScan() {

        TelinkLightService.Instance().idleMode(true);

        if (mApplication.isEmptyMesh())
            return;

        //扫描参数
        LeScanParameters params = LeScanParameters.create();
        params.setMeshName(DEFAULT_NAME);
        params.setOutOfMeshName(DEFAULT_NAME);
        params.setTimeoutSeconds(10);
        params.setScanMode(true);
//        params.setScanTypeFilter(0x00);
        TelinkLightService.Instance().startScan(params);
    }


    private void onLeScan(LeScanEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        TelinkLightService.Instance().connect(deviceInfo.macAddress, 10);
    }

    private void onLeScanTimeout() {
        TelinkLog.d("scan timeout");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_back.setEnabled(true);
            }
        });
    }

    private void onConnected(DeviceInfo deviceInfo) {
        TelinkLightService.Instance().login(Strings.stringToBytes(DEFAULT_NAME, 16), Strings.stringToBytes(DEFAULT_PASSWORD, 16));
    }

    private void onLogin(DeviceInfo deviceInfo) {
        TelinkLightService.Instance().enableNotification();
        getDissociateList();
    }

    /**
     * 切换为默认网络
     */
    private void setDefault() {
        TelinkLog.d("切换为默认网络");
        byte opcode = (byte) (0xC9 & 0xFF);
        int addr = 0xFFFF;
        byte[] params = {0x08, (byte) (DEFAULT_TIMEOUT_SEC & 0xFF), 0x00};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);

        getDissociateList();
    }

    private void setDefaultBack() {
        TelinkLog.d("从默认网络切回到之前");
        byte opcode = (byte) (0xC9 & 0xFF);
        int addr = 0xFFFF;
        byte[] params = {0x08, 0x00, 0x00};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);
    }


    /**
     * 获取未配置过的设备列表
     * <p>
     * 16 * 80ms + 100ms 完成后delay， 获取两次
     */
    private void getDissociateList() {
//        mMeshDevices.clear();
        TelinkLog.d(LOG_TAG + "获取设备列表");
        byte opcode = (byte) (0xE0 & 0xFF);
        int addr = 0xFFFF;
        byte[] params = {(byte) 0xFF, (byte) 0xFF, 0x01, 0x10};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);

        delayHandler.removeCallbacksAndMessages(null);
        delayHandler.postDelayed(getListTimeoutTask, GET_LIST_TIMEOUT);
    }

    /**
     * 分配deviceId
     */
    private void setDeviceId(GetMeshDeviceNotificationParser.MeshDeviceInfo deviceInfo, int desId) {
        TelinkLog.d(LOG_TAG + "配置设备信息");
        byte opcode = (byte) (0xE0 & 0xFF);
        int srcId = deviceInfo.deviceId;
//        int desId = mesh.getDeviceAddress();

        byte[] params = new byte[10];

        params[0] = (byte) (desId & 0xFF);
        params[1] = (byte) (desId >> 8 & 0xFF);

        params[2] = 0x01;
        params[3] = 0x10;

        System.arraycopy(deviceInfo.macBytes, 0, params, 4, deviceInfo.macBytes.length);

//        String mac = deviceInfo.macAddress.replace(":", "");
//        byte[] macBt = Arrays.hexToBytes(mac);


        TelinkLightService.Instance().sendCommandNoResponse(opcode, srcId, params);

        deviceInfo.deviceId = desId;
    }

    private synchronized void updateDeviceList(GetMeshDeviceNotificationParser.MeshDeviceInfo deviceInfo) {

        for (GetMeshDeviceNotificationParser.MeshDeviceInfo info : mMeshDevices) {
            if (Arrays.equals(info.macBytes, deviceInfo.macBytes)) {
                info.deviceId = deviceInfo.deviceId;
                return;
            }
        }


        mMeshDevices.add(deviceInfo);

        Light localDeviceInfo = new Light();
        localDeviceInfo.meshAddress = mesh.getDeviceAddress();
        localDeviceInfo.macAddress = Arrays.bytesToHexString(Arrays.reverse(deviceInfo.macBytes), ":");
        mesh.devices.add(localDeviceInfo);
        mesh.saveOrUpdate(this);
        setDeviceId(deviceInfo, localDeviceInfo.meshAddress);
    }

    private void resetMeshInfo() {
        updateComplete = false;
        TelinkLog.d(LOG_TAG + "重置Mesh信息 " + mesh.name + " -- " + mesh.password);
        TelinkLightService.Instance().resetByMesh(mesh.name, mesh.password);
//        delayHandler.removeCallbacksAndMessages(null);
//        delayHandler.postDelayed(resetCompleteTask, 5 * 1000);
    }


    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case LeScanEvent.LE_SCAN:
                onLeScan((LeScanEvent) event);
                break;

            case LeScanEvent.LE_SCAN_TIMEOUT:
                onLeScanTimeout();
                break;

            case DeviceEvent.STATUS_CHANGED:
                DeviceInfo deviceInfo = ((DeviceEvent) event).getArgs();
                int state = deviceInfo.status;
                if (state == LightAdapter.STATUS_CONNECTED) {
                    onConnected(deviceInfo);
                } else if (state == LightAdapter.STATUS_LOGIN) {
                    onLogin(deviceInfo);
                } else if (state == LightAdapter.STATUS_LOGOUT) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_back.setEnabled(true);
                        }
                    });
                } else if (state == LightAdapter.STATUS_UPDATE_MESH_COMPLETED) {
                    TelinkLog.d(LOG_TAG + "update complete");
                    if (!updateComplete) {
                        delayHandler.removeCallbacks(resetCompleteTask);
                        delayHandler.postDelayed(resetCompleteTask, COMPLETE_DELAY);
                    }

                }
                break;
            case NotificationEvent.GET_MESH_DEVICE_LIST:

                GetMeshDeviceNotificationParser.MeshDeviceInfo meshDeviceInfo
                        = GetMeshDeviceNotificationParser.create().parse(((NotificationEvent) event).getArgs());
                TelinkLog.d(LOG_TAG + "获取到notify -- id: " + meshDeviceInfo.deviceId + " -- mac: " + Arrays.bytesToHexString(meshDeviceInfo.macBytes, ":"));

                // 是否正在配置设备deviceId， 防止出现循环状态
                if (processingDevice == null) {

                    // && 该设备未被配置过, 即不存在于设备列表内
                    for (GetMeshDeviceNotificationParser.MeshDeviceInfo device :
                            mMeshDevices) {
                        if (Arrays.equals(device.macBytes, meshDeviceInfo.macBytes)) {
                            TelinkLog.e("获取到已配置的设备");
                            return;
                        }
                    }

                    delayHandler.removeCallbacks(getListTimeoutTask);
                    retryCnt = 0;
                    processingDevice = meshDeviceInfo;
                    setDeviceId(processingDevice, mesh.getDeviceAddress());
                } else {
                    if (Arrays.equals(meshDeviceInfo.macBytes, processingDevice.macBytes)) {
                        TelinkLog.d("配置deviceId完成");
                        mMeshDevices.add(meshDeviceInfo);
                        Light localDeviceInfo = new Light();
                        localDeviceInfo.meshAddress = meshDeviceInfo.deviceId;
                        localDeviceInfo.macAddress = Arrays.bytesToHexString(Arrays.reverse(meshDeviceInfo.macBytes), ":");
                        mesh.devices.add(localDeviceInfo);
                        mesh.saveOrUpdate(this);
                        notifyListData();
                        processingDevice = null;
                        getDissociateList();
                    } else {
                        TelinkLog.d("配置过程中接收到其它设备");
                    }
                }

                break;

            case NotificationEvent.UPDATE_MESH_COMPLETE:
                TelinkLog.d("获取加灯完成通知");
                updateComplete = true;
                delayHandler.removeCallbacks(resetCompleteTask);
                delayHandler.post(resetCompleteTask);
                break;
        }
    }

    private void notifyListData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    private Runnable getListTimeoutTask = new Runnable() {
        @Override
        public void run() {
            TelinkLog.d(LOG_TAG + "获取设备列表 超时");
            if (mMeshDevices.size() != 0) {
                if (retryCnt <= TIMEOUT_CNT_MAX) {
                    getDissociateList();
                    retryCnt++;
                } else {
                    resetMeshInfo();
                }

            } else {
                btn_back.setEnabled(true);
            }
        }
    };

    private Runnable resetCompleteTask = new Runnable() {
        @Override
        public void run() {
            TelinkLog.d(LOG_TAG + "reset complete");
            setDefaultBack();
            btn_back.setEnabled(true);
//            TelinkLightService.Instance().updateNotification();
        }
    };

    public class MeshDeviceListAdapter extends BaseRecyclerViewAdapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(DeviceMeshScanningActivity.this).inflate(R.layout.device_item, null);
            ViewHolder holder = new ViewHolder(itemView);
            holder.img_icon = (ImageView) itemView.findViewById(R.id.img_icon);
            holder.txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            return holder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            final GetMeshDeviceNotificationParser.MeshDeviceInfo deviceInfo = mMeshDevices.get(position);
            holder.txt_name.setText(deviceInfo.deviceId + " -- " + Arrays.bytesToHexString(deviceInfo.macBytes, ":"));
        }


        @Override
        public int getItemCount() {
            return mMeshDevices.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_icon;
        TextView txt_name;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
