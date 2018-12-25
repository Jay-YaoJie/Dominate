package bases

import android.content.Context
import android.support.multidex.MultiDex
import com.telink.TelinkApplication

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：DominateApplication
 */
class DominateApplication : TelinkApplication() {
 companion object {
    lateinit var dominate:DominateApplication;

 }

    //分包
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        dominate=this;
    }
}