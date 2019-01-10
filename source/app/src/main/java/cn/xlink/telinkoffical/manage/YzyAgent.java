package cn.xlink.telinkoffical.manage;

import android.content.Context;

import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.eventbus.StringEvent;
import cn.xlink.telinkoffical.utils.EventBusUtils;
import cn.xlink.telinkoffical.utils.LogUtil;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.bean.EventNotify;
import io.xlink.wifi.sdk.listener.XlinkNetListener;


public class YzyAgent implements XlinkNetListener {
    private static YzyAgent INSTANCE;

    public static YzyAgent getInstance() {
        if (INSTANCE == null)
            INSTANCE = new YzyAgent();
        return INSTANCE;
    }

    public void init(Context mContext) {
        XlinkAgent.init(mContext);
        XlinkAgent.getInstance().addXlinkListener(this);
    }

    @Override
    public void onStart(int i) {

    }

    @Override
    public void onLogin(int i) {

    }

    @Override
    public void onLocalDisconnect(int code) {
        if (code == XlinkCode.LOCAL_SERVICE_KILL) {
            // 这里是xlink服务被异常终结了（第三方清理软件，或者进入应用管理被强制停止应用/服务）
            // 永不结束的service
            // 除非调用 XlinkAgent.getInstance().stop（）;
            XlinkAgent.getInstance().start();
        }
//        YzyUtils.shortTips("本地网络已经断开");
    }

    @Override
    public void onDisconnect(int code) {
        if (code == XlinkCode.CLOUD_SERVICE_KILL) {
            // 这里是服务被异常终结了（第三方清理软件，或者进入应用管理被强制停止服务）
//            int appId = XLinkLoginHelper.getInstance().getAppId();
//            String authKey = XLinkLoginHelper.getInstance().getAuthKey();
//            if (appId != 0 && !TextUtils.isEmpty(authKey)) {
//                XlinkAgent.getInstance().login(appId, authKey);
//            }
        } else if (code == XlinkCode.CLOUD_USER_EXTRUSION) {
//            YzyUtils.mContext.sendBroadcast(new Intent(BroadConstants.ACTION_CLOUD_USER_EXTRUSION));
           MyApp.getApp().dispatchEvent(StringEvent.newInstance(null, StringEvent.USER_EXTRUSION, ""));
            LogUtil.e("XlinkAgent: " + "onDisconnect >>>" + " CLOUD_USER_EXTRUSION");
        }
    }

    /**
     * 收到 局域网设备推送的pipe数据
     */
    @Override
    public void onRecvPipeData(XDevice xDevice, byte b, byte[] data) {

    }


    /**
     * 收到设备通过云端服务器推送的pipe数据
     */
    @Override
    public void onRecvPipeSyncData(XDevice xDevice, byte b, byte[] data) {

    }

    /**
     * 设备状态改变：掉线/重连/在线
     */
    @Override
    public void onDeviceStateChanged(XDevice xDevice, int state) {

    }

    /**
     * 设备数据端点改变
     */
    @Override
    public void onDataPointUpdate(XDevice xDevice, int key, Object value, int channel, int type) {
        LogUtil.e("mac: " + xDevice.getMacAddress() + "  key: " + key + "  value: " + value);
//        YzyAgent.getInstance().updateXDevice(xDevice);
    }

    @Override
    public void onEventNotify(EventNotify eventNotify) {
        LogUtil.e(eventNotify.toString());

    }
}
