package bases


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import bases.DominateApplication.Companion.dominate
import bases.DominateApplication.Companion.mLightService
import com.jeff.dominate.MeshOTAService
import com.jeff.dominate.TelinkLightApplication
import com.jeff.dominate.TelinkLightService
import com.jeff.dominate.model.Lights
import com.jeff.dominate.util.MeshCommandUtil
import com.telink.bluetooth.TelinkLog
import com.telink.bluetooth.event.*
import com.telink.bluetooth.light.*
import com.telink.util.Event
import com.telink.util.EventListener
import device.DeviceFragment
import jeff.bases.MainActivity
import jeff.me.MeFragment
import jeff.scene.SceneFragment
import jeff.utils.LogUtils
import jeff.utils.SPUtils
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
        LogUtils.d(tag, " performed(event: Event<String>)=event.getType()=" + event.getType())

//        when (event.type) {
//            NotificationEvent.ONLINE_STATUS ->// this.onOnlineStatusNotify(event as NotificationEvent)
//
//            NotificationEvent.GET_ALARM -> {
//            }
//            DeviceEvent.STATUS_CHANGED -> //this.onDeviceStatusChanged(event as DeviceEvent)
//            MeshEvent.OFFLINE -> this.onMeshOffline(event as MeshEvent)
//            ServiceEvent.SERVICE_CONNECTED -> this.onServiceConnected(event as ServiceEvent)
//            ServiceEvent.SERVICE_DISCONNECTED -> this.onServiceDisconnected(event as ServiceEvent)
//            NotificationEvent.GET_DEVICE_STATE -> onNotificationEvent(event as NotificationEvent)
//
//            ErrorReportEvent.ERROR_REPORT -> {
//                val info = (event as ErrorReportEvent).args
//
//            }
//        }//                this.onAlarmGet((NotificationEvent) event);
    }

    //自动重新连接，不管是退出或着添加灯都会断开连接，所以就要从新连接
    private fun autoConnect() {
        if (mLightService.mode != LightAdapter.MODE_AUTO_CONNECT_MESH) {

            val name=SPUtils.getLocalName(mActivity)
            val password=SPUtils.getLocalPassword(mActivity)
            if (name.isNullOrEmpty() || password.isNullOrEmpty()) {
                mLightService.idleMode(true)//断开连接
                return
            }

            //自动重连参数
            val connectParams: LeAutoConnectParameters = Parameters.createAutoConnectParameters()
            connectParams.setMeshName(name)
            connectParams.setPassword(password)
            //连接通知
            connectParams.autoEnableNotification(true)

            // 之前是否有在做MeshOTA操作，是则继续
            val mac=SPUtils.getConnectMac(mActivity)
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

    override fun initViews() {
        mFragments.add(MainFragment())//主页
        mFragments.add(SceneFragment())//情景
        mFragments.add(DeviceFragment())//设备管理
        mFragments.add(MeFragment())//我的

    }

}


