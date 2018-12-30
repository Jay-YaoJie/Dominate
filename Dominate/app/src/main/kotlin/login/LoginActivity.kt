package login

import android.app.AlertDialog
import android.content.Intent
import bases.DominateApplication
import com.jeff.dominate.R
import com.telink.bluetooth.LeBluetooth
import com.telink.bluetooth.light.LeAutoConnectParameters
import com.telink.bluetooth.light.LeRefreshNotifyParameters
import com.telink.bluetooth.light.LightAdapter
import com.telink.bluetooth.light.Parameters
import device.DeviceScaningActivity
import jeff.login.LoginActivity
import jeff.utils.SPUtils
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
    //自动重新连接，不管是退出或着添加灯都会断开连接，所以就要从新连接
    private fun autoConnect() {
        if (DominateApplication.mLightService.mode != LightAdapter.MODE_AUTO_CONNECT_MESH) {

            val name = SPUtils.getLocalName(mActivity)
            val password = SPUtils.getLocalPassword(mActivity)
            if (name.isNullOrEmpty() || password.isNullOrEmpty()) {
                DominateApplication.mLightService.idleMode(true)//断开连接
                return
            }

            //自动重连参数
            val connectParams: LeAutoConnectParameters = Parameters.createAutoConnectParameters()
            connectParams.setMeshName(name)
            connectParams.setPassword(password)
            //连接通知
            connectParams.autoEnableNotification(true)

            // 之前是否有在做MeshOTA操作，是则继续
            val mac = SPUtils.getConnectMac(mActivity)
            if (!mac.isNullOrEmpty()) {
                connectParams.setConnectMac(mac)
            }
            //开始连接
            DominateApplication.mLightService.autoConnect(connectParams)
        }

        //刷新Notify参数
        val refreshNotifyParams: LeRefreshNotifyParameters = Parameters.createRefreshNotifyParameters()
        refreshNotifyParams.setRefreshRepeatCount(2)
        refreshNotifyParams.setRefreshInterval(2000)
        //开启自动刷新Notify
        DominateApplication.mLightService.autoRefreshNotify(refreshNotifyParams)

    }
}