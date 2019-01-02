package bases


import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import bases.DominateApplication.Companion.dominate
import bases.DominateApplication.Companion.mLightService
import bases.DominateApplication.Companion.notificationInfoList
import com.jeff.dominate.MeshOTAService
import com.jeff.dominate.R
import com.jeff.dominate.TelinkLightApplication
import com.jeff.dominate.TelinkLightService
import com.jeff.dominate.model.Lights
import com.jeff.dominate.util.MeshCommandUtil
import com.telink.bluetooth.TelinkLog
import com.telink.bluetooth.event.*
import com.telink.bluetooth.light.*
import com.telink.util.BuildUtils
import com.telink.util.Event
import com.telink.util.EventListener
import device.DeviceFragment
import jeff.bases.MainActivity
import jeff.me.MeFragment
import jeff.scene.SceneFragment
import jeff.utils.LogUtils
import jeff.utils.SPUtils
import jeff.utils.ToastUtil
import login.LoginActivity
import main.MainFragment
import com.telink.bluetooth.light.OnlineStatusNotificationParser.DeviceNotificationInfo
import jeff.constants.Settings.factoryName
import jeff.constants.Settings.factoryPassword
import jeff.constants.Settings.masLogin
import org.greenrobot.eventbus.EventBus

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：bases.MainActivity
 */
class MainActivity : MainActivity(), EventListener<String> {
    val mHandler: Handler = Handler()
    /**
     * 事件处理方法
     *
     * @param event
     */
    override fun performed(event: Event<String>) {
        //  T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        LogUtils.d(tag, " performed(event: Event<String>)=event.getType()=" + event.getType())
        when (event.type) {
            NotificationEvent.ONLINE_STATUS -> {//  设备的状态变化事件
                //  获得当前在线数据,离线数据，或着不存在的设备数据
                // this.onOnlineStatusNotify(event as NotificationEvent)
                notificationInfoList = ((event as NotificationEvent).parse()) as List<DeviceNotificationInfo>?
                LogUtils.d(tag, "获得当前数据对象列表"+notificationInfoList!!.toString())
                //更新主页，单个设备列表
                EventBus.getDefault().post("MainFragment")
            }
            DeviceEvent.STATUS_CHANGED -> {
                val deviceInfo: DeviceInfo = (event as DeviceEvent).args
                //当设备的状态发生改变时
                LogUtils.d(tag, "当设备的状态发生改变时deviceInfo.toString()=" + deviceInfo.toString())
                when (deviceInfo.status) {
                    LightAdapter.STATUS_LOGIN -> {
                        LogUtils.d(tag, "STATUS_LOGIN~！")
                        //this.connectMeshAddress = dominate.getConnectDevice().meshAddress
                        //                this.showToast("login success");
                        if (mLightService.mode == LightAdapter.MODE_AUTO_CONNECT_MESH) {
                            mHandler.postDelayed({ mLightService.sendCommandNoResponse(0xE4.toByte(), 0xFFFF, byteArrayOf()) }, (3 * 1000).toLong())
                        }
//                        if (!MeshOTAService.isRunning) {
//                            // 获取本地设备OTA状态信息
//                            MeshCommandUtil.getDeviceOTAState()
//                        }
                    }
                    LightAdapter.STATUS_CONNECTING -> {
                        LogUtils.d(tag, "登录成功~！")
                    }
                    LightAdapter.STATUS_LOGOUT -> {
                        LogUtils.d(tag, "登录失败~！")
                        // this.showToast("disconnect");//第一次进来，没有设备，定会失败连接
                        //重新登录
                        autoConnect()
                    }
                    LightAdapter.STATUS_ERROR_N -> {
                        //登录异常 清除所有登录数据
                        //SPUtils.clearAll(mActivity) //第一次进来，没有设备，定会失败连接
                        //重新登录
                        autoConnect()
                    }
                }
            }
            MeshEvent.OFFLINE -> {// 连接到不任何设备的时候分发此事件
                LogUtils.d(tag, "OFFLINE")
                ToastUtil.show(mActivity.resources.getString(R.string.check_add_device))

            }
            ErrorReportEvent.ERROR_REPORT -> {
                val info = (event as ErrorReportEvent).args
                LogUtils.d(tag, info.toString())
            }
        }
    }


    //自动重新连接，不管是退出或着添加灯都会断开连接，所以就要从新连接
    private fun autoConnect() {
        if (mLightService.mode != LightAdapter.MODE_AUTO_CONNECT_MESH) {
            //自动重连参数
            val connectParams: LeAutoConnectParameters = Parameters.createAutoConnectParameters()
            if (masLogin) {
                //如果使用默认名登录
                connectParams.setMeshName(factoryName)
                connectParams.setPassword(factoryPassword)
            } else {
                //否则使用自己的名字登录
                val name = SPUtils.getLocalName(mActivity)
                val password = SPUtils.getLocalPassword(mActivity)
                if (name.isNullOrEmpty() || password.isNullOrEmpty()) {
                    mLightService.idleMode(true)//断开连接
                    /*账号异常。请重新次登录*/
                    ToastUtil.show(mActivity.resources.getString(R.string.account_exception))
                    mActivity.startActivity(Intent(mActivity, LoginActivity::class.java))
                    mActivity.finish()
                    return
                }
                connectParams.setMeshName(name)
                connectParams.setPassword(password)
            }

            //连接通知
            connectParams.autoEnableNotification(true)

            // 之前是否有在做MeshOTA操作，是则继续
            val mac = SPUtils.getConnectMac(mActivity)
            if (!mac.isNullOrEmpty()) {
                connectParams.setConnectMac(mac)
            }
            //开始连接
            mLightService.autoConnect(connectParams)
        }

        //刷新Notify参数
        val refreshNotifyParams: LeRefreshNotifyParameters = Parameters.createRefreshNotifyParameters()
        refreshNotifyParams.setRefreshRepeatCount(2)
        refreshNotifyParams.setRefreshInterval(2000)
        //开启自动刷新Notify
        mLightService.autoRefreshNotify(refreshNotifyParams)

    }

    override fun onStop() {
        super.onStop()
        LogUtils.d(tag, "onStop")
        //关闭自动连接刷新  关闭自动刷新网络通知
        mLightService.disableAutoRefreshNotify()
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d(tag, "onDestroy")
        unregisterReceiver(mReceiver)
        this.mHandler.removeCallbacksAndMessages(null)
        dominate.doDestroy()
        //移除事件
        dominate.removeEventListener(this)
        EventBus.getDefault().unregister(mActivity)

    }

    val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)

                when (state) {
                    BluetoothAdapter.STATE_ON -> {
                        LogUtils.d(tag, "蓝牙开启")
                        TelinkLightService.Instance().idleMode(true)
                        //重新连接登录
                        autoConnect()
                    }
                    BluetoothAdapter.STATE_OFF -> LogUtils.d(tag, "蓝牙关闭")
                }
            }
        }
    }

    override fun initViews() {
        mFragments.add(MainFragment())//主页
        mFragments.add(SceneFragment())//情景
        mFragments.add(DeviceFragment())//设备管理
        mFragments.add(MeFragment())//我的
        super.initViews()
        EventBus.getDefault().isRegistered(mActivity)
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY - 1
        registerReceiver(mReceiver, filter)

    }


    override fun onStart() {
        super.onStart()
        // 监听各种事件
        dominate.addEventListener(NotificationEvent.ONLINE_STATUS, this)
        dominate.addEventListener(DeviceEvent.STATUS_CHANGED, this)
        dominate.addEventListener(MeshEvent.OFFLINE, this)//连接到不任何设备的时候分发此事件
        dominate.addEventListener(ServiceEvent.SERVICE_CONNECTED, this) //服务启动
        dominate.addEventListener(ErrorReportEvent.ERROR_REPORT, this)////出现错误信息时
        this.autoConnect()
    }
}


