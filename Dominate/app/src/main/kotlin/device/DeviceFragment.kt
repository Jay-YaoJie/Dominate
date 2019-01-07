package device

import android.content.Intent
import android.os.Handler
import bases.DominateApplication.Companion.mLightService
import co.metalab.asyncawait.async
import com.telink.bluetooth.event.NotificationEvent
import jeff.constants.DeviceBean
import jeff.constants.GroupBean
import jeff.device.DeviceFragment
import jeff.utils.LogUtils
import jeff.utils.SPUtils
import kotlin_adapter.adapter_core.extension.putItems
import com.telink.util.Event
import com.telink.util.EventListener

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-28.
 * description ：DeviceFragment
 */
class DeviceFragment() : DeviceFragment(), EventListener<String> {


    override fun initViews() {
        super.initViews()

        //点击设备，添加设备
        binding.deviceFragmentUngroupedIv.setOnClickListener {
            mActivity.startActivity(Intent(mActivity, DeviceScaningActivity::class.java))
        }

    }

    override fun lazyLoad() {
        super.lazyLoad()
        //可见时
        async {
            await<Unit> {
                //加载测试数据
                //group  //组 数据列表
                groupList = SPUtils.getGroupBeans(mActivity, "grouplist")
                // single  ////单个数据列表
                singleList = SPUtils.getDeviceBeans(mActivity, "deviceSingleList")

            }
            groupAdapter.putItems(groupList)
            groupAdapter.notifyDataSetChanged()
            singleAdapter.putItems(singleList)
            singleAdapter.notifyDataSetChanged()
        }
    }

    var meshAddress: Int = 0
    lateinit var group: GroupBean
    lateinit var addDeviceBeanList: ArrayList<DeviceBean>
    lateinit var deviceBeanList: ArrayList<DeviceBean>

    //点击添加按钮，组里添加设备
    override fun groupAdd(groupName: GroupBean, addsingleList: ArrayList<DeviceBean>) {
        LogUtils.d(tag, "向组里添加设备")
        group = groupName
        addDeviceBeanList = addsingleList
        deviceBeanList = SPUtils.getDeviceBeans(mActivity, "deviceSingleList")
        forAddDevice()
    }
    val mHandler = Handler()
    fun forAddDevice() {
        for (i in addDeviceBeanList!!.indices) {
            mHandler.postDelayed({
                if (addDeviceBeanList[i].checkd){
                    meshAddress = addDeviceBeanList[i].meshAddress
                    addDeviceBeanList[i].checkd=false
                    getDeviceGroup()//先发送更新命令
                    allocDeviceGroup()//最后发送添加命令
                }
            }, (3 * 1000).toLong())
        }

    }

    private fun getDeviceGroup() {
        //发送更新命令
        val opcode = 0xDD.toByte()
        val dstAddress = meshAddress
        val params = byteArrayOf(0x08, 0x01)
        mLightService.sendCommandNoResponse(opcode, dstAddress, params)
        mLightService.updateNotification()
    }

    private fun allocDeviceGroup() {
        //发送添加设备
        val groupAddress = group.meshAddress
        val dstAddress = meshAddress
        val opcode = 0xD7.toByte()
        val params = byteArrayOf(0x01, (groupAddress and 0xFF).toByte(), (groupAddress shr 8 and 0xFF).toByte())
        //添加
        params[0] = 0x01
        mLightService.sendCommandNoResponse(opcode, dstAddress, params)

        //取消
//            params[0] = 0x00
//            mLightService.sendCommandNoResponse(opcode, dstAddress, params)

    }

    override fun performed(event: Event<String>) {
        if (event.getType() === NotificationEvent.GET_GROUP) {
            val e = event as NotificationEvent
            val info = e.args
            val srcAddress = info.src and 0xFF
            val params = info.params

            if (srcAddress == meshAddress) {
                //更新单个设备的数据对象
                for (i in deviceBeanList!!.indices) {
                    if (deviceBeanList[i].meshAddress == meshAddress) {
                        //更新数据对象
                        deviceBeanList[i].groupMeshAddressList.add(group.meshAddress)//当前组的Mesh
                        deviceBeanList[i].groupNameList.add(group.groupName!!)//当前组的名称
                        //当前在多少个组里面
                        deviceBeanList[i].groupIndexId = deviceBeanList[i].groupIndexId + 1
                    }
                }
            }

        }
    }
}