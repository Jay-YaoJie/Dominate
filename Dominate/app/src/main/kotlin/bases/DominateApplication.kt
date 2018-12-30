package bases

import android.content.Context
import android.support.multidex.MultiDex
import com.jeff.dominate.R
import com.telink.TelinkApplication
import com.telink.bluetooth.LeBluetooth
import jeff.bases.DominateApplication.Companion.instance
import jeff.utils.ActivitiesManager
import jeff.utils.DelegatesExt
import jeff.utils.ToastUtil


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：DominateApplication
 */
class DominateApplication : TelinkApplication() {
    companion object {
        // 自定义委托实现单例,只能修改这个值一次.
        var dominate: DominateApplication by DelegatesExt.notNullSingleValue<DominateApplication>();
        var mLightService: LightService by DelegatesExt.notNullSingleValue<LightService>();

    }


    //分包
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this;
        dominate = this;
        ////初始货Activity堆管理
        ActivitiesManager.getActivitiesManager()

        //初始化
        doInit()
    }
    override fun doInit() {
        super.doInit()
        //启动LightService
        this.startLightService(LightService::class.java)

        //检查是否支持蓝牙设备
        if (!LeBluetooth.getInstance().isSupport(this)) {
            ToastUtil(this.resources.getString(R.string.ble_not_support))
            return
        }


    }

}