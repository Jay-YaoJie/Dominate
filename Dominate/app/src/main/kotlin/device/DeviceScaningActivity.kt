package device

import android.os.Handler
import bases.DominateApplication.Companion.dominate
import bases.DominateApplication.Companion.mLightService
import bases.LightService
import co.metalab.asyncawait.async
import com.jeff.dominate.TelinkLightService
import com.telink.bluetooth.event.*
import com.telink.bluetooth.light.DeviceInfo
import com.telink.bluetooth.light.LeScanParameters
import com.telink.bluetooth.light.Manufacture
import com.telink.bluetooth.light.Parameters
import com.telink.util.Event
import com.telink.util.EventListener
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
class DeviceScaningActivity : DeviceScaningActivity(), EventListener<String> {
    override fun performed(event: Event<String>) {
        //To change body of created functions use File | Settings | File Templates.
        LogUtils.d(tag, "performed(event: Event<String>?)--event.type= " + event.type)

        when (event.type) {
            LeScanEvent.LE_SCAN -> {
                //扫描到设备
                val deviceInfo: DeviceInfo = (event as LeScanEvent).getArgs()
                LogUtils.d(tag, "扫描到设备，添加到集合中" + deviceInfo.toString())
                scanedList.add(deviceInfo);
            }
            LeScanEvent.LE_SCAN_TIMEOUT -> {
                //  扫描不到任何设备了
                ToastUtil("扫描不到任何设备了")
            }

            LeScanEvent.LE_SCAN_COMPLETED -> {
                //扫描到设备
                if (scanedList.size <= 0) {
                    return
                }
                //更新参数,
                val params = Parameters.createUpdateParameters()
                if (!(Manufacture.getDefault().factoryName).isNullOrEmpty()) {
                    params.setOldMeshName(Manufacture.getDefault().factoryName)//旧的网络名
                    params.setOldPassword(Manufacture.getDefault().factoryPassword)//旧的密码
                } else {
                    params.setOldMeshName(factoryName)//旧的网络名
                    params.setOldPassword(factoryPassword)//旧的密码
                }

                params.setNewMeshName(SPUtils.getLocalName(mActivity))//新的网络名
                params.setNewPassword(SPUtils.getLocalPassword(mActivity))//新的密码
                //执行更新操作
                // DeviceInfo[] deviceInfos = scanedList.toArray(new DeviceInfo[scanedList.size()]);
                val deviceInfos: Array<DeviceInfo> = scanedList.toTypedArray()
                params.setUpdateDeviceList(*deviceInfos)
                mLightService.idleMode(true)//断开当前的连接
                mLightService.updateMesh(params)
            }

            DeviceEvent.STATUS_CHANGED -> {
                //当设备的状态发生改变时,会分发此事件.可以根据事件参数{@link DeviceInfo#status}获取状态.
            }

            MeshEvent.UPDATE_COMPLETED -> this.startScan(1000)
            MeshEvent.ERROR -> {
                mLightService.idleMode(true)//断开连接
                ToastUtil.show("添加灯失败，请稍后重试！")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanedList.clear()
        dominate.removeEventListener(this)
        this.mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 开始扫描
     */
    private var scanedList: ArrayList<DeviceInfo> = ArrayList()
    private val mHandler = Handler()
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
            params.setScanMode(false)

            mLightService.startScan(params)
        }, delay.toLong())
    }

    override fun initViews() {
        super.initViews()
        //添加监听
        dominate.addEventListener(LeScanEvent.LE_SCAN, this)
        dominate.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this)
        dominate.addEventListener(DeviceEvent.STATUS_CHANGED, this)
        dominate.addEventListener(MeshEvent.UPDATE_COMPLETED, this)
        dominate.addEventListener(MeshEvent.ERROR, this)
        dominate.addEventListener(NotificationEvent.GET_MESH_DEVICE_LIST, this)
        dominate.addEventListener(NotificationEvent.UPDATE_MESH_COMPLETE, this)
        //开始扫描
        this.startScan(0)
    }


}