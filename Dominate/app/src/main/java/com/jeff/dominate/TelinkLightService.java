package com.jeff.dominate;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.LightService;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-11-17.
 * description ：TelinkLightService
 */
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
}
