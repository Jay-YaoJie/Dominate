package bases


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import bases.DominateApplication.Companion.dominate
import com.jeff.dominate.MeshOTAService
import com.jeff.dominate.TelinkLightApplication
import com.jeff.dominate.TelinkLightService
import com.jeff.dominate.model.Lights
import com.jeff.dominate.util.MeshCommandUtil
import com.telink.bluetooth.TelinkLog
import com.telink.bluetooth.event.*
import com.telink.bluetooth.light.ConnectionStatus
import com.telink.bluetooth.light.LightAdapter
import com.telink.bluetooth.light.Parameters
import com.telink.util.Event
import com.telink.util.EventListener
import device.DeviceFragment
import jeff.bases.MainActivity
import jeff.me.MeFragment
import jeff.scene.SceneFragment
import jeff.utils.LogUtils
import jeff.utils.ToastUtil
import main.MainFragment


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：bases.MainActivity
 */
class MainActivity : MainActivity(), EventListener<String> {
    override fun onRestart() {
        super.onRestart()
    // eventReg()
    }

    /**
     * 事件处理方法
     *
     * @param event
     */
    override fun performed(event: Event<String>) {
        //  T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        LogUtils.d(tag," performed(event: Event<String>)=event.getType()="+event.getType())
        when (event.getType()) {
            DeviceEvent.STATUS_CHANGED -> {
                // 当设备的状态发生改变时,会分发此事件.可以根据事件参数{@link DeviceInfo#status}获取状态.
                val deviceInfo = (event as DeviceEvent).args
                when (deviceInfo.status) {
                    LightAdapter.STATUS_LOGIN -> {
                        this.connectMeshAddress = dominate.getConnectDevice().meshAddress
                        //                this.showToast("login success");
                        if (TelinkLightService.Instance().mode == LightAdapter.MODE_AUTO_CONNECT_MESH) {
                            Handler().postDelayed({
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
            MeshEvent.OFFLINE -> {
                //连接到不任何设备的时候分发此事件
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
                    mTimeoutBuilder.setNeutralButton("Quit", DialogInterface.OnClickListener { dialog, _ ->
                        val mesh = TelinkLightApplication.getApp().mesh
                        mesh.otaDevice = null
                        mesh.saveOrUpdate(this@MainActivity)
                        autoConnect()
                        dialog.dismiss()
                    })
                    mTimeoutBuilder.setNegativeButton("Retry", DialogInterface.OnClickListener { dialog, _ ->
                        autoConnect()
                        dialog.dismiss()
                    })
                    mTimeoutBuilder.setCancelable(false)

                    mTimeoutBuilder.show()
                }
            }
            ServiceEvent.SERVICE_CONNECTED -> { //服务启动
                //this.onServiceConnected(event as ServiceEvent)
                autoConnect()
            }

            ServiceEvent.SERVICE_DISCONNECTED -> {//服务关闭
                LogUtils.d(tag, "现在什么也不做。。")
            }
            NotificationEvent.GET_DEVICE_STATE -> {//获取设备版本号
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

            ErrorReportEvent.ERROR_REPORT -> {//错误信息
                val info = (event as ErrorReportEvent).args
                TelinkLog.d("MainActivity#performed#ERROR_REPORT: " + " stateCode-" + info.stateCode
                        + " errorCode-" + info.errorCode
                        + " deviceId-" + info.deviceId)
            }
        }//                this.onAlarmGet((NotificationEvent) event);
    }

    override fun initViews() {
        mFragments.add(MainFragment())//主页
        mFragments.add(SceneFragment())//情景
        mFragments.add(DeviceFragment())//设备管理
        mFragments.add(MeFragment())//我的

        super.initViews()
        //添加蓝牙事件
        //初始化蓝牙
       // dominate.doInit()
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

        LogUtils.d(tag, "onStart()监听各种事件")
        //当设备的状态发生改变时,会分发此事件.可以根据事件参数{@link DeviceInfo#status}获取状态.
        dominate.addEventListener(DeviceEvent.STATUS_CHANGED, this)
        //获取设备版本号
        dominate.addEventListener(NotificationEvent.GET_DEVICE_STATE, this)
        //服务启动
        dominate.addEventListener(ServiceEvent.SERVICE_CONNECTED, this)
        //连接到不任何设备的时候分发此事件
        dominate.addEventListener(MeshEvent.OFFLINE, this)
        //  //出现错误信息时
        dominate.addEventListener(ErrorReportEvent.ERROR_REPORT, this)
        dominate.addEventListener(MeshEvent.ERROR, this);
        //连接
        autoConnect()
    }

    var connectMeshAddress: Int = 0
    override fun onResume() {
        super.onResume()
        LogUtils.d(tag, "onResume()")
        val deviceInfo = dominate.getConnectDevice()
        if (deviceInfo != null) {
            this.connectMeshAddress = deviceInfo.meshAddress and 0xFF
        }
    }

    override fun onStop() {
        super.onStop()
        LogUtils.d(tag, "onStop()")
        TelinkLightService.Instance().disableAutoRefreshNotify()
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d(tag, "onDestroy()")
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
            LogUtils.d(tag,"autoConnect()")
            if (TelinkLightService.Instance().mode != LightAdapter.MODE_AUTO_CONNECT_MESH) {

                if (dominate.isEmptyMesh())
                    return
                // Lights.getInstance().clear();
                dominate.refreshLights()


                // this.deviceFragment.notifyDataSetChanged()
                eventBus.post("DeviceFragment")//刷新页面

                val mesh = dominate.getMesh()

                if (TextUtils.isEmpty(mesh.name) || TextUtils.isEmpty(mesh.password)) {
                   //  是否断开当前的连接: disconnect
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

}


