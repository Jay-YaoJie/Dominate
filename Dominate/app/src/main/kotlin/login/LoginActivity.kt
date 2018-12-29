package login

import android.app.AlertDialog
import android.content.Intent
import com.jeff.dominate.R
import com.telink.bluetooth.LeBluetooth
import device.DeviceScaningActivity
import jeff.login.LoginActivity
import jeff.utils.ToastUtil

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-27.
 * description ：LoginActivity 登录页面
 */
class LoginActivity : LoginActivity() {
    override fun login(): Boolean {
        if (super.login()) {
            //登录成功
            ToastUtil(R.string.login_success)
            startActivity(Intent(this, DeviceScaningActivity::class.java))
            this.finish()

            return true
        } else {
            //登录失败
            ToastUtil(R.string.logon_back)
            logonBack()//清除数据
        }

        return false
    }

    override fun onResume() {
        super.onResume()
        //检查是否支持蓝牙设备
        if (!LeBluetooth.getInstance().isSupport(applicationContext)) {
            ToastUtil("ble not support")
            return
        }

        if (!LeBluetooth.getInstance().isEnabled) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("开启蓝牙，体验智能灯!")
            builder.setNeutralButton("cancel") { dialog, which -> finish() }
            builder.setNegativeButton("enable") { dialog, which -> LeBluetooth.getInstance().enable(applicationContext) }
            builder.show()
        }


    }


}