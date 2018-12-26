package bases


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import bases.DominateApplication.Companion.dominate
import com.jeff.dominate.MeshOTAService
import com.jeff.dominate.R
import com.jeff.dominate.TelinkLightApplication
import com.jeff.dominate.TelinkLightService
import com.jeff.dominate.model.Light
import com.jeff.dominate.model.Lights
import com.jeff.dominate.util.MeshCommandUtil
import com.telink.bluetooth.TelinkLog
import com.telink.bluetooth.event.*
import com.telink.bluetooth.light.ConnectionStatus
import com.telink.bluetooth.light.LightAdapter
import com.telink.bluetooth.light.OnlineStatusNotificationParser
import com.telink.bluetooth.light.Parameters
import com.telink.util.Event
import com.telink.util.EventListener
import jeff.bases.MainActivity
import jeff.utils.LogUtils
import jeff.utils.ToastUtil


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：bases.MainActivity
 */
class MainActivity : MainActivity(), EventListener<String> {
    /**
     * 事件处理方法
     *
     * @param event
     */
    override fun performed(event: Event<String>) {
        //  T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when (event.getType()) {
            NotificationEvent.ONLINE_STATUS -> {
                // this.onOnlineStatusNotify(event as NotificationEvent)
                TelinkLog.i("MainActivity#onOnlineStatusNotify#Thread ID : " + Thread.currentThread().id)
                val notificationInfoList: List<OnlineStatusNotificationParser.DeviceNotificationInfo>?
                //noinspection unchecked
                notificationInfoList = (event as NotificationEvent).parse() as List<OnlineStatusNotificationParser.DeviceNotificationInfo>?

                if (notificationInfoList == null || notificationInfoList.size <= 0)
                    return
                for (notificationInfo in notificationInfoList) {

                    val meshAddress = notificationInfo.meshAddress
                    val brightness = notificationInfo.brightness

                    var light: Light? = this.deviceFragment.getDevice(meshAddress)

                    if (light == null) {
                        light = Light()
                        this.deviceFragment.addDevice(light)
                    }

                    light.meshAddress = meshAddress
                    light.brightness = brightness
                    light.connectionStatus = notificationInfo.connectionStatus

                    if (light.meshAddress == this.connectMeshAddress) {
                        light.textColor = R.color.theme_positive_color
                    } else {
                        light.textColor = R.color.black
                    }
                }

                eventBus.post("DeviceFragment")//刷新页面
            }

            NotificationEvent.GET_ALARM -> {
            }
            DeviceEvent.STATUS_CHANGED -> this.onDeviceStatusChanged(event as DeviceEvent)
            MeshEvent.OFFLINE -> {
                TelinkLog.w("auto connect offline")
                val lights = Lights.getInstance().get()
                for (light in lights) {
                    light.connectionStatus = ConnectionStatus.OFFLINE
                }
                eventBus.post("DeviceFragment")//刷新页面

                if (TelinkLightApplication.getApp().mesh.isOtaProcessing) {
                    TelinkLightService.Instance().idleMode(true)
                    var mTimeoutBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                    mTimeoutBuilder.setTitle("AutoConnect Fail")
                    mTimeoutBuilder.setMessage("Connect device:" + TelinkLightApplication.getApp().mesh.otaDevice.mac + " Fail, Quit? \nYES: quit MeshOTA process, NO: retry")
                    mTimeoutBuilder.setNeutralButton("Quit", DialogInterface.OnClickListener { dialog, which ->
                        val mesh = TelinkLightApplication.getApp().mesh
                        mesh.otaDevice = null
                        mesh.saveOrUpdate(this@MainActivity)
                        autoConnect()
                        dialog.dismiss()
                    })
                    mTimeoutBuilder.setNegativeButton("Retry", DialogInterface.OnClickListener { dialog, which ->
                        autoConnect()
                        dialog.dismiss()
                    })
                    mTimeoutBuilder.setCancelable(false)

                    mTimeoutBuilder.show()
                }
            }
            ServiceEvent.SERVICE_CONNECTED -> {
                //this.onServiceConnected(event as ServiceEvent)
                autoConnect()
            }

            ServiceEvent.SERVICE_DISCONNECTED -> {
                LogUtils.d(tag, "现在什么也不做。。")
            }
            NotificationEvent.GET_DEVICE_STATE -> {
                // 解析版本信息

                val data = (event as NotificationEvent).getArgs().params

                if (data[0] == NotificationEvent.DATA_GET_MESH_OTA_PROGRESS) {
                    /*if (!MeshOTAService.isRunning) {
                Intent serviceIntent = new Intent(this, MeshOTAService.class);
                serviceIntent.putExtra(MeshOTAService.INTENT_KEY_INIT_MODE, MeshOTAService.MODE_CONTINUE_MESH_OTA);
                startService(serviceIntent);
            }*/
                } else if (data[0] == NotificationEvent.DATA_GET_OTA_STATE) {
                    if (TelinkLightApplication.getApp().mesh.isOtaProcessing && !MeshOTAService.isRunning) {
                        if (data[1] == NotificationEvent.OTA_STATE_MASTER) {
                            val serviceIntent = Intent(this, MeshOTAService::class.java)
                            serviceIntent.putExtra(MeshOTAService.INTENT_KEY_INIT_MODE, MeshOTAService.MODE_CONTINUE_MESH_OTA)
                            startService(serviceIntent)
                        } else if (data[1] == NotificationEvent.OTA_STATE_COMPLETE) {
                            val mesh = TelinkLightApplication.getApp().mesh
                            mesh.otaDevice = null
                            mesh.saveOrUpdate(this)

                            val serviceIntent = Intent(this, MeshOTAService::class.java)
                            serviceIntent.putExtra(MeshOTAService.INTENT_KEY_INIT_MODE, MeshOTAService.MODE_COMPLETE)
                            startService(serviceIntent)
                        } else if (data[1] == NotificationEvent.OTA_STATE_IDLE) {
                            MeshCommandUtil.sendStopMeshOTACommand()
                        }
                    }
                }

            }

            ErrorReportEvent.ERROR_REPORT -> {
                val info = (event as ErrorReportEvent).args
                TelinkLog.d("MainActivity#performed#ERROR_REPORT: " + " stateCode-" + info.stateCode
                        + " errorCode-" + info.errorCode
                        + " deviceId-" + info.deviceId)
            }
        }//                this.onAlarmGet((NotificationEvent) event);
    }

    override fun initViews() {
        super.initViews()
        //添加蓝牙事件
        dominate.doInit()
        //打印日志创建文件
        TelinkLog.d("-------------------------------------------")
        TelinkLog.d(Build.MANUFACTURER)
        TelinkLog.d(Build.TYPE)
        TelinkLog.d(Build.BOOTLOADER)
        TelinkLog.d(Build.DEVICE)
        TelinkLog.d(Build.HARDWARE)
        TelinkLog.d(Build.SERIAL)
        TelinkLog.d(Build.BRAND)
        TelinkLog.d(Build.DISPLAY)
        TelinkLog.d(Build.FINGERPRINT)
        TelinkLog.d(Build.PRODUCT + ":" + Build.VERSION.SDK_INT + ":" + Build.VERSION.RELEASE + ":" + Build.VERSION.CODENAME + ":" + Build.ID)
    }

    override fun onStart() {
        super.onStart()
        LogUtils.d(tag, "监听各种事件")
        dominate.addEventListener(DeviceEvent.STATUS_CHANGED, this)
        dominate.addEventListener(NotificationEvent.ONLINE_STATUS, this)
        dominate.addEventListener(NotificationEvent.GET_ALARM, this)
        dominate.addEventListener(NotificationEvent.GET_DEVICE_STATE, this)
        dominate.addEventListener(ServiceEvent.SERVICE_CONNECTED, this)
        dominate.addEventListener(MeshEvent.OFFLINE, this)

        dominate.addEventListener(ErrorReportEvent.ERROR_REPORT, this)
        //连接
        autoConnect()
    }

    var connectMeshAddress: Int = 0
    override fun onResume() {
        super.onResume()
        val deviceInfo = dominate.getConnectDevice()
        if (deviceInfo != null) {
            this.connectMeshAddress = dominate.getConnectDevice().meshAddress and 0xFF
        }
    }

    override fun onStop() {
        super.onStop()
        TelinkLightService.Instance().disableAutoRefreshNotify()
    }

    override fun onDestroy() {
        super.onDestroy()
        dominate.doDestroy()
        //移除事件
        dominate.removeEventListener(this)
        Lights.getInstance().clear()
    }

    /**
     * 自动重连
     */
    private fun autoConnect() {
        if (TelinkLightService.Instance() != null) {

            if (TelinkLightService.Instance().mode != LightAdapter.MODE_AUTO_CONNECT_MESH) {

                if (dominate.isEmptyMesh())
                    return
                // Lights.getInstance().clear();
                dominate.refreshLights()


                // this.deviceFragment.notifyDataSetChanged()
                eventBus.post("DeviceFragment")//刷新页面

                val mesh = dominate.getMesh()

                if (TextUtils.isEmpty(mesh.name) || TextUtils.isEmpty(mesh.password)) {
                    TelinkLightService.Instance().idleMode(true)
                    return
                }

                //自动重连参数
                val connectParams = Parameters.createAutoConnectParameters()
                connectParams.setMeshName(mesh.name)
                connectParams.setPassword(mesh.password)
                connectParams.autoEnableNotification(true)

                // 之前是否有在做MeshOTA操作，是则继续
                if (mesh.isOtaProcessing()) {
                    connectParams.setConnectMac(mesh.otaDevice.mac)
                    //                    saveLog("Action: AutoConnect:" + mesh.otaDevice.mac);
                } else {
                    //                    saveLog("Action: AutoConnect:NULL");
                }
                //自动重连
                TelinkLightService.Instance().autoConnect(connectParams)
            }

            //刷新Notify参数
            val refreshNotifyParams = Parameters.createRefreshNotifyParameters()
            refreshNotifyParams.setRefreshRepeatCount(2)
            refreshNotifyParams.setRefreshInterval(2000)
            //开启自动刷新Notify
            TelinkLightService.Instance().autoRefreshNotify(refreshNotifyParams)
        }
    }

    private val mHandler = Handler()
    private fun onDeviceStatusChanged(event: DeviceEvent) {
        val deviceInfo = event.args
        when (deviceInfo.status) {
            LightAdapter.STATUS_LOGIN -> {
                this.connectMeshAddress = dominate.getConnectDevice().meshAddress
                //                this.showToast("login success");
                if (TelinkLightService.Instance().mode == LightAdapter.MODE_AUTO_CONNECT_MESH) {
                    mHandler.postDelayed({
                        TelinkLightService.Instance().sendCommandNoResponse(0xE4.toByte(), 0xFFFF, byteArrayOf())
                        eventBus.post("DeviceFragment")//刷新页面
                    }, (3 * 1000).toLong())
                }

                if (TelinkLightApplication.getApp().mesh.isOtaProcessing && !MeshOTAService.isRunning) {
                    // 获取本地设备OTA状态信息
                    MeshCommandUtil.getDeviceOTAState()
                }
            }
            LightAdapter.STATUS_CONNECTING -> {
            }
            LightAdapter.STATUS_LOGOUT -> {
                val lights = Lights.getInstance().get()
                for (light in lights) {
                    light.connectionStatus = ConnectionStatus.OFFLINE
                }
                runOnUiThread {
                    eventBus.post("DeviceFragment")//刷新页面
                }
            }
            LightAdapter.STATUS_ERROR_N -> {
                ToastUtil.show("连接重试多次失败")
                TelinkLightService.Instance().idleMode(true)
                TelinkLog.d("DeviceScanningActivity#onNError")
            }
            else -> {
                // this.showToast("login");
            }
        }
    }

}


