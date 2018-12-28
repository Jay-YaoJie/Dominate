package bases

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import bases.DominateApplication.Companion.mLightService
import com.jeff.dominate.TelinkLightService
import com.telink.bluetooth.light.LightAdapter
import com.telink.bluetooth.light.LightService
import jeff.utils.LogUtils

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-28 22:55
 * description ：LightService
 */
class LightService : LightService() {
    override fun onBind(intent: Intent): IBinder? {

        if (this.mBinder == null)
            this.mBinder = LocalBinder()

        return super.onBind(intent)
    }

    override fun onCreate() {

        super.onCreate()

        mLightService = this

        if (this.mAdapter == null)
            this.mAdapter = LightAdapter()
        this.mAdapter.start(this)
    }

    inner class LocalBinder : Binder() {
        val service: bases.LightService
            get() = this@LightService
    }

}