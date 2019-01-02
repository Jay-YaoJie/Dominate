package com.telink.bluetooth.light;

import android.text.TextUtils;
import android.widget.Toast;

import com.telink.TelinkApplication;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.light.activity.TempTestActivity;
import com.telink.bluetooth.light.model.*;
import com.telink.bluetooth.light.util.FileSystem;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public final class TelinkLightApplication extends TelinkApplication {

    private Mesh mesh;
    private StringBuilder logInfo;
    private static TelinkLightApplication thiz;
//    private List<String> macFilters = new ArrayList<>();

    private Toast toast;
    private int onlineCount = 0;

    private TempTestActivity.TestInput testInput;

    public TempTestActivity.TestInput getTestInput() {
        return testInput;
    }

    public void setTestInput(TempTestActivity.TestInput testInput) {
        this.testInput = testInput;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //this.doInit();
        logInfo = new StringBuilder("log:");
        thiz = this;
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//        AdvanceStrategy.setDefault(new MySampleAdvanceStrategy());


    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    public static TelinkLightApplication getApp() {
        return thiz;
    }

    @Override
    public void doInit() {

        String fileName = "telink-";
        fileName += System.currentTimeMillis();
        fileName += ".log";
        TelinkLog.LOG2FILE_ENABLE = false;
        TelinkLog.onCreate(fileName);
        super.doInit();
        //AES.Security = true;

        String name = SharedPreferencesHelper.getMeshName(this);
        String pwd = SharedPreferencesHelper.getMeshPassword(this);

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
            if (FileSystem.exists(this, name + "." + pwd)) {
                Mesh mesh = (Mesh) FileSystem.readAsObject(this, name + "." + pwd);
                setupMesh(mesh);
            }
        } else {
            Mesh mesh = getMesh();
            setupMesh(mesh);
        }


/*

        if (FileSystem.exists("telink.meshs")) {
            this.mesh = (Mesh) FileSystem.readAsObject("telink.meshs");
        }
*/


        //启动LightService
        this.startLightService(TelinkLightService.class);
    }

    @Override
    public void doDestroy() {
        TelinkLog.onDestroy();
        super.doDestroy();
    }

    public Mesh getMesh() {
        if (this.mesh == null) {
            this.mesh = new Mesh();
            this.mesh.name = "abcdf";
            this.mesh.password = "abcds";

            this.mesh.factoryName = "telink_mesh1";
            this.mesh.factoryPassword = "123";
        }
        return this.mesh;
    }

    public void setupMesh(Mesh mesh) {
        this.mesh = mesh;
        refreshLights();
    }

    public void refreshLights() {
        if (mesh != null && mesh.devices != null) {
            Lights.getInstance().clear();
            Lights.getInstance().add(mesh.devices);
            for (Light light : Lights.getInstance().get()) {
                light.connectionStatus = ConnectionStatus.OFFLINE;
                light.textColor = R.color.black;
            }
            /*Light light;
            for (com.telink.bluetooth.light.model.DeviceInfo deviceInfo : mesh.devices) {
                light = new Light();
                light.macAddress = deviceInfo.macAddress;
                light.meshAddress = deviceInfo.meshAddress;
                light.brightness = 0;
                light.connectionStatus = ConnectionStatus.OFFLINE;
                light.textColor = this.getResources().getColorStateList(
                        R.color.black);
                light.updateIcon();

                Lights.getInstance().add(light);
            }*/
        }
    }


    public boolean isEmptyMesh() {

        return this.mesh == null || TextUtils.isEmpty(mesh.name) || TextUtils.isEmpty(mesh.password);
    }

    /**********************************************
     * Log api
     **********************************************/

//    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.S");

    @Override
    public void saveLog(String action) {

//        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
//        Date date = sdf.parse(dateInString);
        ;
        String time = format.format(Calendar.getInstance().getTimeInMillis());
        logInfo.append("\n\t").append(time).append(":\t").append(action);
        /*if (Looper.myLooper() == Looper.getMainLooper()) {
            showToast(action);
        }*/

        TelinkLog.w("SaveLog: " + action);
    }


   /* @Override
    public void saveScanMac(String deviceAddress, String deviceName) {
        if (macFilters.size() == 0 || macFilters.contains(deviceAddress)) {
            String time = format.format(Calendar.getInstance().getTimeInMillis());
            logInfo.append("\n\t").append(time).append(":\t").append("Scan : ").append(deviceName).append("-- ").append(deviceAddress);
            showToast("Scan : " + deviceAddress);
            TelinkLog.w("Scan : " + deviceAddress);
        }
    }*/

    public void saveLogInFile(String fileName, String logInfo) {
        if (FileSystem.writeAsString(fileName + ".txt", logInfo)) {
            showToast("save success --" + fileName);
        }
    }


    public void showToast(CharSequence s) {

        if (this.toast != null) {
            this.toast.setView(this.toast.getView());
            this.toast.setDuration(Toast.LENGTH_SHORT);
            this.toast.setText(s);
            this.toast.show();
        }
    }

    public String getLogInfo() {
        return logInfo.toString();
    }

    public void clearLogInfo() {
//        logInfo.delete(0, logInfo.length() - 1);
        logInfo = new StringBuilder("log:");
    }


    /**
     * super method
     *
     * @param intent
     */
   /* @Override
    protected void onLeScan(Intent intent) {
        super.onLeScan(intent);
        DeviceInfo deviceInfo = intent.getParcelableExtra(LightService.EXTRA_DEVICE);
        saveLog("scan: " + deviceInfo.macAddress);
    }

    @Override
    protected void onStatusChanged(Intent intent) {
        super.onStatusChanged(intent);
        DeviceInfo deviceInfo = intent.getParcelableExtra(LightService.EXTRA_DEVICE);
        saveLog("device " + deviceInfo.macAddress + " " + getDeviceState(deviceInfo.connectionStatus));
    }

    private String getDeviceState(int connectionStatus) {
        switch (connectionStatus) {
            case LightAdapter.STATUS_CONNECTING:
                return "STATUS_CONNECTING";
            case LightAdapter.STATUS_CONNECTED:
                return "STATUS_CONNECTED";
            case LightAdapter.STATUS_LOGINING:
                return "STATUS_LOGINING";
            case LightAdapter.STATUS_LOGIN:
                return "STATUS_LOGIN_SUCCESS";
            case LightAdapter.STATUS_LOGOUT:
                return "LOGIN_FAILURE | CONNECT_FAILURE";
            case LightAdapter.STATUS_UPDATE_MESH_COMPLETED:
            case LightAdapter.STATUS_UPDATING_MESH:
            case LightAdapter.STATUS_UPDATE_MESH_FAILURE:
            case LightAdapter.STATUS_UPDATE_ALL_MESH_COMPLETED:
            case LightAdapter.STATUS_GET_LTK_COMPLETED:
            case LightAdapter.STATUS_GET_LTK_FAILURE:
            case LightAdapter.STATUS_MESH_OFFLINE:
            case LightAdapter.STATUS_MESH_SCAN_COMPLETED:
            case LightAdapter.STATUS_MESH_SCAN_TIMEOUT:
            case LightAdapter.STATUS_OTA_COMPLETED:
            case LightAdapter.STATUS_OTA_FAILURE:
            case LightAdapter.STATUS_OTA_PROGRESS:
            case LightAdapter.STATUS_GET_FIRMWARE_COMPLETED:
            case LightAdapter.STATUS_GET_FIRMWARE_FAILURE:
            case LightAdapter.STATUS_DELETE_COMPLETED:
            case LightAdapter.STATUS_DELETE_FAILURE:
            default:
                return "OTHER";
        }
    }*/
}
