package bases

import android.content.Context
import android.support.multidex.MultiDex
import com.jeff.dominate.TelinkLightApplication
import jeff.bases.DominateApplication.Companion.instance
import jeff.utils.ActivitiesManager


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：DominateApplication
 */
class DominateApplication : TelinkLightApplication() {
    companion object {
        lateinit var dominate: DominateApplication;
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
        //初始化蓝牙
       dominate.doInit()
    }

}