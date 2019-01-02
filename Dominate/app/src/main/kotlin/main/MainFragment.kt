package main

import android.content.Intent
import bases.DominateApplication.Companion.notificationInfoList
import co.metalab.asyncawait.async
import com.telink.bluetooth.light.OnlineStatusNotificationParser.DeviceNotificationInfo
import device.DeviceScaningActivity
import jeff.beans.FragmentAdapterBeans.DeviceBean
import jeff.main.MainFragment
import jeff.utils.SPUtils


/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-27 23:16
 * description ：MainFragment 主页显示
 */
class MainFragment : MainFragment() {
    override fun initViews() {
        super.initViews()
        async {
            await<Unit> {
                //加载测试数据
                infoTopList()
                infoGroupList()
                infoSingleList()
            }
            info()//加载数据列表适配器
        }
        binding.mainFragmentDeviceUngroupedTV.setOnClickListener {
            mActivity.startActivity(Intent(mActivity, DeviceScaningActivity::class.java))
        }
    }

    //获得最顶上的数据
    fun infoTopList() {
        val icListStr = SPUtils.getFragmentAdapterBeans(mActivity, "infoTopList")
        if (icListStr == null) {
            //如果没有初始化过对象，则初始化数据对象并保存
        } else {
            topicList = icListStr
        }
    }

    //获得组列表数据
    fun infoGroupList() {
        val icListStr = SPUtils.getFragmentAdapterBeans(mActivity, "infoGroupList")
        if (icListStr == null) {
            //如果没有初始化过对象，则初始化数据对象并保存
        } else {
            groupList = icListStr
        }
    }

    //获得单个设备列表数据
    fun infoSingleList() {
        if (notificationInfoList == null || notificationInfoList!!.size <= 0) {
            return
        }
        for (deviceInfo: DeviceNotificationInfo in notificationInfoList!!) {
            var deviceBean: DeviceBean = DeviceBean()

            deviceBean.meshAddress = deviceInfo.meshAddress //灯的名称 数据列表12345
            deviceBean.status = deviceInfo.status
            deviceBean.brightness = deviceInfo.brightness ////当前状态为0关 或着 100  开
            deviceBean.reserve = deviceInfo.reserve
            // OFF(0), ON(1), OFFLINE(2);  关，开，离线
            //   OFF(0), ON(1), OFFLINE(2); var connectionStatus: ConnectionStatus? = null
            deviceBean.connectionStatus = deviceInfo.connectionStatus.value

            //主页里选择适配使用的对象
            deviceBean.imgAny = null//图片
            deviceBean.textStr = deviceBean.meshAddress.toString()//文字
            deviceBean.groupId = -1//当前id

            deviceBean.deviceName = deviceBean.meshAddress.toString()//设备里的名称
            deviceBean.index = -1 //当前所在的下标

            singleList.add(deviceBean)
        }

//        val icListStr = SPUtils.getFragmentAdapterBeans(mActivity, "infoSingleList")
//        if (icListStr == null) {
//            //如果没有初始化过对象，则初始化数据对象并保存
//        } else {
//            singleList = icListStr
//        }
    }
}