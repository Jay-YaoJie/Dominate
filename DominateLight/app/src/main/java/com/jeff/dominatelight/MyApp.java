package com.jeff.dominatelight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jeff.dominatelight.activity.BaseActivity;
import com.jeff.dominatelight.activity.LoginActivity;
import com.jeff.dominatelight.bean.greenDao.PlaceSort;
import com.jeff.dominatelight.database.DaoMaster;
import com.jeff.dominatelight.database.DaoSession;
import com.jeff.dominatelight.database.utils.UpgradeHelper;
import com.jeff.dominatelight.eventbus.ConnectStateEvent;
import com.jeff.dominatelight.eventbus.StringEvent;
import com.jeff.dominatelight.manage.CmdManage;
import com.jeff.dominatelight.manage.DataToHostManage;
import com.jeff.dominatelight.manage.YzyAgent;
import com.jeff.dominatelight.model.Light;
import com.jeff.dominatelight.model.Lights;
import com.jeff.dominatelight.model.Places;
import com.jeff.dominatelight.model.Scenes;
import com.jeff.dominatelight.service.TelinkLightService;
import com.jeff.dominatelight.utils.*;
import com.jeff.dominatelight.view.dialog.TipsDialog;
import com.telink.TelinkApplication;
import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.xlink.wifi.sdk.XlinkAgent;
import jeff.bases.DominateApplication;

/**
 * Created by liucr on 2016/3/23.
 */
public class MyApp extends TelinkApplication {

    private static MyApp application;

    public static Handler mainHandler = null;

    private DaoSession daoSession;

    private Activity currentActivity;

    private int state = LightAdapter.STATUS_LOGOUT;

    private boolean isFindOrOta = false;

    private boolean isCheckTime = false;

    private boolean isCanShowConnectLoading = true;

    /**
     * 首选项设置
     */
    public static SharedPreferences sharedPreferences;

    public static void initHandler() {
        mainHandler = new Handler();
    }

    public static MyApp getApp() {
        return application;
    }

    private CopyOnWriteArrayList<BaseActivity> activities = new CopyOnWriteArrayList<>();

    public void addActivity(BaseActivity activity) {
        activities.add(activity);
    }

    public void removeActivity(BaseActivity activity) {
        for (BaseActivity ac : activities) {
            if (ac.equals(activity)) {
                activities.remove(ac);
                break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        DominateApplication.instance =this;
        DominateApplication.dialogSetting();
        sharedPreferences = getSharedPreferences("cn.xlink.telinkOffical", Context.MODE_PRIVATE);
        // bug收集
        CrashHandler.init(this);
        EventBusUtils.init();
        YzyAgent.getInstance().init(this);
        getFirmwareVersion();
        initHandler();
        initDataBase();
        doInit();
    }

    @Override
    public void doInit() {
        super.doInit();
        LeBluetooth.getInstance().isSupport(this);
        this.startLightService(TelinkLightService.class);

        autoConnect(false);
    }

    @Override
    public void doDestroy() {
        super.doDestroy();
    }

    /**
     * 修改了SDK，将 autoConnect 中 【if (this.getMode() == MODE_AUTO_CONNECT_MESH)】屏蔽了
     */
    public void autoConnect(boolean isForce) {

        this.addEventListener(DeviceEvent.STATUS_CHANGED, telinkEventListener);
        this.addEventListener(NotificationEvent.ONLINE_STATUS, telinkEventListener);
        this.addEventListener(ServiceEvent.SERVICE_CONNECTED, telinkEventListener);
        this.addEventListener(MeshEvent.OFFLINE, telinkEventListener);
        this.addEventListener(MeshEvent.UPDATE_COMPLETED, telinkEventListener);
        this.addEventListener(StringEvent.USER_EXTRUSION, telinkEventListener);

        if (TelinkLightService.Instance() != null) {
            if (TelinkLightService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {

                if (Places.getInstance().getCurPlaceSort() == null)
                    return;
                PlaceSort placeSort = Places.getInstance().getCurPlaceSort();
                final LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                connectParams.setMeshName(placeSort.getMeshAddress());
                connectParams.setPassword(placeSort.getMeshKey());
                connectParams.autoEnableNotification(false);
                TelinkLightService.Instance().autoConnect(connectParams);

                EventBusUtils.getInstance().dispatchEvent(new StringEvent(null, StringEvent.CONNECTING, "CONNECTING"));
            }

            //刷新Notify参数
            LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
            refreshNotifyParams.setRefreshRepeatCount(2);
            refreshNotifyParams.setRefreshInterval(2000);
            //开启自动刷新Notify
            TelinkLightService.Instance().autoRefreshNotify(refreshNotifyParams);
        }
    }

    private EventListener telinkEventListener = new EventListener<String>() {
        @Override
        public void performed(Event<String> event) {
            switch (event.getType()) {
                case NotificationEvent.ONLINE_STATUS:
                    onOnlineStatusNotify((NotificationEvent) event);
                    break;
                case DeviceEvent.STATUS_CHANGED:
                    onDeviceStatusChanged((DeviceEvent) event);
                    break;
                case MeshEvent.OFFLINE:
                    onMeshOffline((MeshEvent) event);
                    break;
                case MeshEvent.UPDATE_COMPLETED:
                    EventBusUtils.getInstance().dispatchEvent(new MeshEvent(this, MeshEvent.UPDATE_COMPLETED, ((MeshEvent) event).getArgs()));
                    break;
                case ServiceEvent.SERVICE_CONNECTED:
                    onServiceConnected((ServiceEvent) event);
                    break;
                case ServiceEvent.SERVICE_DISCONNECTED:
                    onServiceDisconnected((ServiceEvent) event);
                    break;
                case LeScanEvent.LE_SCAN:
                    DeviceInfo deviceInfo = (DeviceInfo) ((LeScanEvent) event).getArgs();
                    LogUtil.e("productUUID: " + deviceInfo.macAddress + "-->" + deviceInfo.productUUID);
                    LogUtil.e("firmwareRevision--> " + deviceInfo.firmwareRevision);
                    EventBusUtils.getInstance().dispatchEvent(new LeScanEvent(this, LeScanEvent.LE_SCAN, deviceInfo));
                    break;
                case StringEvent.USER_EXTRUSION:
                    extrusion();
                    break;
            }
        }
    };

    private int connectMeshAddress;

    private void onDeviceStatusChanged(DeviceEvent event) {

        DeviceInfo deviceInfo = event.getArgs();

        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                this.connectMeshAddress = this.getConnectDevice().meshAddress;
                state = LightAdapter.STATUS_LOGIN;
//                XlinkUtils.shortTips("login success");
                isCanShowConnectLoading = true;

                if (!isFindOrOta) {
                    TelinkLightService.Instance().enableNotification();
                    TelinkLightService.Instance().updateNotification();
                    CmdManage.notifyLight(2);
                }

                break;
            case LightAdapter.STATUS_LOGINING:
                state = LightAdapter.STATUS_LOGINING;
//                XlinkUtils.shortTips("login");
                break;
            case LightAdapter.STATUS_LOGOUT:
                state = LightAdapter.STATUS_LOGOUT;
                for (Light light : Lights.getInstance().get()) {
                    light.status = ConnectionStatus.OFFLINE;
                }
//                XlinkUtils.shortTips("logout");
                break;
            case LightAdapter.STATUS_CONNECTED:
                TelinkLightService.Instance().getFirmwareVersion();
                break;
            case LightAdapter.STATUS_GET_FIRMWARE_COMPLETED:
                // Log.e("Version:","Version:"+deviceInfo.meshAddress+":"+version);
                EventBusUtils.getInstance().dispatchEvent(new DeviceEvent(this, DeviceEvent.STATUS_CHANGED, event.getArgs()));
                break;
            case LightAdapter.STATUS_GET_FIRMWARE_FAILURE:
                Log.e("Version:", "GetSTATUS_GET_FIRMWARE_FAILURE");
                break;
            default:
                break;
        }

        if (!isFindOrOta && state == LightAdapter.STATUS_LOGIN) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Places.getInstance().getCurPlaceSort() == null) {
                        return;
                    }
                    //校准时间
                    if (!isCheckTime) {
                        isCheckTime = true;
                        CmdManage.timeSet(0xffff);
                    }
                }
            }, 2000);
        }
        if (!isFindOrOta) {
            EventBusUtils.getInstance().dispatchEvent(ConnectStateEvent.newInstance("MyApp", ConnectStateEvent.ConnectStateEvent, state));
        }
    }

    private void onOnlineStatusNotify(NotificationEvent event) {

        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null) return;
        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {

            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;

            final Light light = Lights.getInstance().getByMeshAddress(meshAddress);
            if (light != null) {

                if (!isFindOrOta && isCheckTime && light.status == ConnectionStatus.OFFLINE) {
                    CmdManage.timeSet(light.getLightSort().getMeshAddress());
                }

                light.getLightSort().setMeshAddress(meshAddress);
                light.brightness = brightness;
                light.status = notificationInfo.connectionStatus;
                if (TextUtils.isEmpty(light.getLightSort().getName())) {
                    light.getLightSort().setName("Bulb-" + Integer.toString(meshAddress, 16).toUpperCase());
                }

                if (!Places.getInstance().curPlaceIsShare()) {
                    if (!light.getLightSort().getIsAddToDefault() &&
                            light.status != ConnectionStatus.OFFLINE) {
                        mainHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Scenes.getInstance().addLightToDefaultScene(light);
                                light.getLightSort().setIsAddToDefault(true);
                                LightsDbUtils.getInstance().updataOrInsert(light.getLightSort());
                                DataToHostManage.updataCurToHost();
                            }
                        }, 200);
                    }
                }

            }
        }
        EventBusUtils.getInstance().dispatchEvent(new NotificationEvent(this, NotificationEvent.ONLINE_STATUS, event.getArgs()));
    }

    private void onMeshOffline(MeshEvent event) {

        List<Light> lights = Lights.getInstance().get();
        for (Light light : lights) {
            light.status = ConnectionStatus.OFFLINE;
        }
    }

    private void onServiceConnected(ServiceEvent event) {
        this.autoConnect(false);
    }

    private void onServiceDisconnected(ServiceEvent event) {

    }

    public void setIsFindOrOta(boolean isFindOrOta) {
        this.isFindOrOta = isFindOrOta;
    }

    public boolean isFindOrOta() {
        return isFindOrOta;
    }

    public void setCheckTime(boolean checkTime) {
        isCheckTime = checkTime;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void setCanShowConnectLoading(boolean canShowConnectLoading) {
        isCanShowConnectLoading = canShowConnectLoading;
    }

    public boolean isCanShowConnectLoading() {
        return isCanShowConnectLoading;
    }

    public int getState() {
        return state;
    }

    /**
     * 执行在主线程任务
     *
     * @param runnable
     */
    public static void postToMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    /**
     * GreenDao相关
     */
    public synchronized DaoSession getDaoSession() {
        if (daoSession == null) {
            initDaoSession();
        }
        return daoSession;
    }

    /**
     * 初始化数据库相关
     */
    private void initDataBase() {
        UserUtil.initUser();
        PlacesDbUtils.init();
        LightsDbUtils.init();
        GroupsDbUtils.init();
        ScenesDbUtils.init();
        SceneActionsDbUtils.init();
        SceneTimersDbUtils.init();
    }

    private void initDaoSession() {
        // 相当于得到数据库帮助对象，用于便捷获取db
        UpgradeHelper helper = new UpgradeHelper(this, "telinkoffical.db", null);
        // 得到可写的数据库操作对象
        SQLiteDatabase db = helper.getWritableDatabase();
        // 获得Master实例,相当于给database包装工具
        DaoMaster daoMaster = new DaoMaster(db);
        // 获取类似于缓存管理器,提供各表的DAO类
        daoSession = daoMaster.newSession();
    }

    private String mVersion;
    private byte[] mFirmware;

    private void getFirmwareVersion() {
        try {
            byte[] version = new byte[4];
            InputStream stream = getResources().getAssets().open("light_8267_20160527.bin");
            int length = stream.available();
            this.mFirmware = new byte[length];
            stream.read(this.mFirmware);

            stream.close();
            System.arraycopy(mFirmware, 2, version, 0, 4);
            String versionStr = toValueOf(version[3]) + toValueOf(version[2]) + toValueOf(version[1]) + toValueOf(version[0]);
            mVersion = versionStr;
        } catch (Exception e) {

        }

        Log.e("mVersion:", mVersion + "");
    }

    private String toValueOf(byte b) {
        String s = Integer.toHexString(b);
        s = s.length() == 1 ? 0 + s : s;
        return s;
    }

    public byte[] getFirmware() {
        return mFirmware;
    }

    public String getVersion() {
        return mVersion;
    }

    /*****************
     * 被挤下线
     ****************/
    protected TipsDialog tipsDialog;

    private void extrusion() {
        TelinkLightService.Instance().idleMode(true);
        for (Light light : Lights.getInstance().get()) {
            light.status = ConnectionStatus.OFFLINE;
        }
        tipsDialog = new TipsDialog(getCurrentActivity());
        tipsDialog.setCanceledOnTouchOutside(false);
        tipsDialog.setCancelable(false);
        tipsDialog.showDialogWithTips(getString(R.string.user_extrusion_tips), getString(R.string.enter),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tipsDialog.dismiss();
                        restartToLogin();
                    }
                });
    }

    public void restartToLogin() {
        getCurrentActivity().startActivity(new Intent(getCurrentActivity(), LoginActivity.class));

        String restartStr = LoginActivity.class.getSimpleName();
        for (BaseActivity activity : activities) {
            if (activity != null) {
                if (!restartStr.equals(activity.getClass().getSimpleName())) {
                    LogUtil.e(activity.getClass().getSimpleName());
                    activity.supportFinishAfterTransition();
                    activities.remove(activity);
                }
            }
        }
        XlinkAgent.getInstance().stop();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
