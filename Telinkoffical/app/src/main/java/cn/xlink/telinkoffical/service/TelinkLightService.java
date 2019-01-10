package cn.xlink.telinkoffical.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.LightPeripheral;
import com.telink.bluetooth.light.LightService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.xlink.telinkoffical.eventbus.GetDeviceGoupEvent;
import cn.xlink.telinkoffical.utils.EventBusUtils;
import cn.xlink.telinkoffical.utils.LogUtil;
import cn.xlink.telinkoffical.utils.XlinkUtils;


public final class TelinkLightService extends LightService {

    private static TelinkLightService mThis;

    public static TelinkLightService Instance() {
        return mThis;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (this.mBinder == null)
            this.mBinder = new LocalBinder();

        return super.onBind(intent);
    }

    @Override
    public void onCreate() {

        super.onCreate();

        mThis = this;

        if (this.mAdapter == null)
            this.mAdapter = new LightAdapter();
        this.mAdapter.start(this);
    }

    public class LocalBinder extends Binder {
        public TelinkLightService getService() {
            return TelinkLightService.this;
        }
    }

//    @Override
//    public boolean onLeScan(LightPeripheral light, int mode, byte[] scanRecord) {
//
//         super.onLeScan(light, mode, scanRecord);
//         Log.e("onLeScan ", XlinkUtils.getHexBinString(scanRecord));
//        Log.e("productUUID",light.getMacAddress()+"-->"+light.getProductUUID());
//        return  true;
//    }

    @Override
    public void onNotify(LightPeripheral light, int mode, int opcode, int src, byte[] params) {
        super.onNotify(light, mode, opcode, src, params);
        LogUtil.e("onNotify " + "  opcode: " + opcode + " src:  " + src + " params: "+ params.toString());
        Log.e("productUUID", light.getMacAddress() + "-->" + light.getProductUUID());
    }

    /**
     * 查询灯的分组
     *
     * @param src
     * @param params
     */
    private void addDataToLight(int src, byte[] params) {
        List<Integer> list = new ArrayList<>();
        if (src < 0) {
            src = 256 + src;
        }
        for (byte b : params) {
            if (b != -1) {
                int group = 0x8000 + b;
                list.add(group);
            }
        }
        EventBusUtils.getInstance().dispatchEvent(GetDeviceGoupEvent.newInstance(src, GetDeviceGoupEvent.GetDeviceGoupAction, list));
    }
}
