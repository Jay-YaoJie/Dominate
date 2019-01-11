package jeff.utils

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-02 21:57
 * description ：IsEnabled   是否打开（蓝牙，定位，wifi）   工具
 */
object IsEnabled {
    //判断用户是否打开系统定位服务
    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE)
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
            return !locationProviders.isNullOrEmpty() ////TextUtils.isEmpty(locationProviders)  //locationProviders.isNullOrEmpty() //
        }
    }

    //判断蓝牙是否打开
    val isBluetoothEnabled: Boolean = (BluetoothAdapter.getDefaultAdapter().isEnabled)

    // 网络是否打开
    fun isWifiEnabled(context: Context): Boolean = (context.getSystemService(Context.WIFI_SERVICE) as WifiManager).isWifiEnabled

}