package device

import android.app.AlertDialog
import android.os.Handler
import android.widget.Toast
import bases.DominateApplication.Companion.dominate
import bases.DominateApplication.Companion.mLightService
import com.jeff.dominate.R
import com.jeff.dominate.model.Light
import com.telink.bluetooth.LeBluetooth
import com.telink.bluetooth.TelinkLog
import com.telink.bluetooth.event.DeviceEvent
import com.telink.bluetooth.event.LeScanEvent
import com.telink.bluetooth.event.MeshEvent
import com.telink.bluetooth.light.*
import com.telink.util.Event
import com.telink.util.EventListener
import jeff.constants.Settings
import jeff.constants.Settings.factoryName
import jeff.constants.Settings.factoryPassword
import jeff.device.DeviceScaningActivity
import jeff.utils.LogUtils
import jeff.utils.SPUtils
import jeff.utils.ToastUtil


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-28.
 * description ：DeviceScaningActivity 扫描并添加设备
 */
class DeviceScaningActivity : DeviceScaningActivity() {

    var deviceInfo: DeviceInfo? = null
    val mListener = EventListener<String> { event: Event<String> ->
        //To change body of created functions use File | Settings | File Templates.
        LogUtils.d(tag, "performed(event: Event<String>?)--event.type= " + event.type)
        when (event.type) {
            LeScanEvent.LE_SCAN -> {
                //扫描到设备
                if (deviceInfo == null) {
                    deviceInfo = (event as LeScanEvent).getArgs()
                    LogUtils.d(tag, "扫描到设备，修改网络名" + deviceInfo.toString())
                    scanedList.add(deviceInfo!!);//添加到集合中
                    mHandler.postDelayed({
                        //更新参数,
                        val params: LeUpdateParameters = Parameters.createUpdateParameters()
//                        if (!(Manufacture.getDefault().factoryName).isNullOrEmpty()) {
//                            params.setOldMeshName(Manufacture.getDefault().factoryName)//旧的网络名
//                            params.setOldPassword(Manufacture.getDefault().factoryPassword)//旧的密码
//                        } else {
//
//                        }
                        params.setOldMeshName(factoryName)//旧的网络名
                        params.setOldPassword(factoryPassword)//旧的密码
                        params.setNewMeshName(SPUtils.getLocalName(mActivity))//新的网络名
                        params.setNewPassword(SPUtils.getLocalPassword(mActivity))//新的密码
                        //获得当前已经有的设备数量
                        var deviceSingleList: Int = SPUtils.getDeviceBeanSize(mActivity, "deviceSingleList")
                        if (deviceSingleList <= 0) {
                            deviceSingleList = 1
                        }
                        deviceInfo!!.meshAddress = deviceSingleList
                        //执行更新操作
                        params.setUpdateDeviceList(deviceInfo)
                        mLightService.updateMesh(params)
                        deviceInfo = null
                    }, 200)
                }
            }
            LeScanEvent.LE_SCAN_TIMEOUT -> {
                //  扫描不到任何设备了
                ToastUtil("扫描不到任何设备了")
            }
            LeScanEvent.LE_SCAN_COMPLETED -> {
                TelinkLog.d("scan complete")
                ////扫描改变,直到扫不到设备
            }
            DeviceEvent.STATUS_CHANGED -> {
                //当设备的状态发生改变时,会分发此事件.可以根据事件参数{@link DeviceInfo#status}获取状态.
                val deviceInfo: DeviceInfo = (event as DeviceEvent).getArgs()
                LogUtils.d(tag, "修改为新的网络名返回的状态deviceInfo.status=" + deviceInfo.status)
                when (deviceInfo.status) {
                    LightAdapter.STATUS_UPDATE_MESH_COMPLETED -> {
                        LogUtils.d(tag, "加灯完成，继续扫描设备")
                        //加灯完成继续扫描,直到扫不到设备

                        // saveLog("Success:  mac--" + deviceInfo.macAddress);
                        this.startScan(1000)
                    }
                    LightAdapter.STATUS_UPDATE_MESH_FAILURE -> {
                        //加灯失败继续扫描
                        LogUtils.d(tag, "加灯失败，继续扫描设备")
                        //  saveLog("Fail:  mac--" + deviceInfo.macAddress);
                        this.startScan(1000)
                    }

                    LightAdapter.STATUS_ERROR_N -> {
                        ToastUtil(mActivity.resources.getString(R.string.add_exception))
                        LogUtils.d(tag, "加灯异常")
                    }
//                    0->{
//                        LogUtils.d(tag,"status=0")
//                    }
                }
              //  this.startScan(1000)
            }
            MeshEvent.ERROR -> {
                mLightService.idleMode(true)//断开连接
                ToastUtil.show("添加灯失败，请稍后重试！")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanedList.clear()
        dominate.removeEventListener(mListener)
        this.mHandler.removeCallbacksAndMessages(null)
        Settings.masLogin = false
    }

    private var scanedList: ArrayList<DeviceInfo> = ArrayList()
    private val mHandler = Handler()
    //开始扫描
    private fun startScan(delay: Int) {
        scanedList.clear()
        mLightService.idleMode(true)  //断开当前连接
        mHandler.postDelayed({
            //扫描参数
            val params = LeScanParameters.create()
            if (!(Manufacture.getDefault().factoryName).isNullOrEmpty()) {
                params.setMeshName(Manufacture.getDefault().factoryName)
            } else {
                params.setMeshName(factoryName)
            }
            params.setOutOfMeshName("out_of_mesh")
            params.setTimeoutSeconds(10)
            params.setScanMode(true)
            mLightService.startScan(params)
        }, delay.toLong())
    }

    override fun initViews() {
        Settings.masLogin = true
        super.initViews()
        //监听事件
        dominate.addEventListener(LeScanEvent.LE_SCAN, mListener)
        dominate.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, mListener)
        dominate.addEventListener(DeviceEvent.STATUS_CHANGED, mListener)
        dominate.addEventListener(MeshEvent.UPDATE_COMPLETED, mListener)
        dominate.addEventListener(MeshEvent.ERROR, mListener)
        //开始扫描
        this.startScan(0)
    }


}