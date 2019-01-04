package device

import android.os.Handler
import bases.DominateApplication.Companion.dominate
import bases.DominateApplication.Companion.mLightService
import com.jeff.dominate.R
import com.telink.bluetooth.event.DeviceEvent
import com.telink.bluetooth.event.LeScanEvent
import com.telink.bluetooth.event.MeshEvent
import com.telink.bluetooth.light.*
import com.telink.util.Event
import com.telink.util.EventListener
import jeff.beans.FragmentAdapterBeans.DeviceBean
import jeff.constants.Settings.factoryName
import jeff.constants.Settings.factoryPassword
import jeff.device.DeviceScaningActivity
import jeff.utils.LogUtils
import jeff.utils.SPUtils
import jeff.utils.ToastUtil
import kotlin_adapter.adapter_core.extension.putItems


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
                ToastUtil.show(mActivity.resources.getString(R.string.not_found_device))
                //更新列表
            }
            DeviceEvent.STATUS_CHANGED -> {
                //当设备的状态发生改变时,会分发此事件.可以根据事件参数{@link DeviceInfo#status}获取状态.
                val deviceInfo: DeviceInfo = (event as DeviceEvent).getArgs()
                LogUtils.d(tag, "修改为新的网络名返回的状态deviceInfo.status=" + deviceInfo.status)
                when (deviceInfo.status) {
                    LightAdapter.STATUS_UPDATE_MESH_COMPLETED -> {
                        LogUtils.d(tag, "加灯完成，继续扫描设备")
                        //加灯完成继续扫描,直到扫不到设备
                        LogUtils.d(tag, "扫描完成。")
                        ////扫描改变,直到扫不到设备
                        //更新列表
                        var deviceI = DeviceBean()
                        deviceI.macAddress = deviceInfo.macAddress//: String? = null // Mac地址
                        deviceI.deviceName = deviceInfo.deviceName//: String? = null//设备名称
                        deviceI.meshName = deviceInfo.meshName//: String? = null//网络名称
                        deviceI.meshAddress = deviceInfo.meshAddress//: Int = 0// 网络地址
                        deviceI.meshUUID = deviceInfo.meshUUID//: Int = 0
                        deviceI.productUUID = deviceInfo.productUUID//: Int = 0 //设备的产品标识符
                        deviceI.status = deviceInfo.status//: Int = 0
                        deviceI.longTermKey = deviceInfo.longTermKey
                        deviceI.firmwareRevision = deviceInfo.firmwareRevision//: String? = null // 设备的firmware版本
                        // singleAdapter!!.addItem(deviceI)
                        deviceList.add(deviceI)
                        deviceListSev!!.add(deviceI)//添加到要保存 持久化的对象中
                        singleAdapter!!.putItems(deviceList)
                        //刷新页面
                        singleAdapter!!.notifyDataSetChanged()

                        this.startScan(1000)
                    }
                    LightAdapter.STATUS_UPDATE_MESH_FAILURE -> {
                        //加灯失败继续扫描
                        LogUtils.d(tag, "加灯失败，继续扫描设备")
                        //  saveLog("Fail:  mac--" + deviceInfo.macAddress);
                        this.startScan(1000)
                    }

                    LightAdapter.STATUS_ERROR_N -> {
                        ToastUtil.show(mActivity.resources.getString(R.string.add_exception))
                        LogUtils.d(tag, "加灯异常")
                    }
                }
            }
            MeshEvent.ERROR -> {
                mLightService.idleMode(true)//断开连接
                ToastUtil.show(mActivity.resources.getString(R.string.add_exception))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //获取数据对象
        deviceListSev = SPUtils.getDeviceBeans(mActivity, "scanedList") as ArrayList<DeviceBean>
        //清除数据对象
        SPUtils.FragmentAdapterBeansClear(mActivity)
        if (deviceListSev == null) {
            deviceListSev = ArrayList()
        }
        //获得单个设备的数据对象
        singleListSev = SPUtils.getDeviceBeans(mActivity, "deviceSingleList") as ArrayList<DeviceBean>
        if (singleListSev == null) {
            singleListSev = ArrayList()
        }

        //扫描设备
        dominate.addEventListener(LeScanEvent.LE_SCAN, mListener)
        // dominate.addEventListener(LeScanEvent.LE_SCAN_COMPLETED, mListener)
        dominate.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, mListener)
        //修改设备名
        dominate.addEventListener(DeviceEvent.STATUS_CHANGED, mListener)
        dominate.addEventListener(MeshEvent.UPDATE_COMPLETED, mListener)
        //出错
        dominate.addEventListener(MeshEvent.ERROR, mListener)
        //开始扫描
        this.startScan(0)
    }

    var singleListSev: ArrayList<DeviceBean> = ArrayList()///保存到单个设备数据对象中
    //保存当前查询出来的数据对象
    var deviceListSev: ArrayList<DeviceBean>? = ArrayList()

    override fun onStop() {
        super.onStop()
        if (deviceListSev!!.size > 0) {
            //保存数据对象，持久保存
            SPUtils.setDeviceBeans(mActivity, "scanedList", deviceListSev!!)
            singleListSev.addAll(deviceListSev!!)// //保存到单个设备数据对象中
            SPUtils.setDeviceBeans(mActivity, "deviceSingleList", singleListSev!!)
            singleListSev.clear()
            scanedList.clear()
        }

        dominate.removeEventListener(mListener)
        this.mHandler.removeCallbacksAndMessages(null)

    }


    private var scanedList: ArrayList<DeviceInfo> = ArrayList()
    private val mHandler = Handler()
    //开始扫描
    private fun startScan(delay: Int) {
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


}