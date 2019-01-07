package main

import adjust.AdjustActivity
import android.content.Intent
import bases.DominateApplication.Companion.mLightService
import co.metalab.asyncawait.async
import com.jeff.dominate.TelinkLightService
import jeff.constants.DeviceBean
import jeff.constants.GroupBean
import jeff.main.MainFragment
import jeff.utils.LogUtils
import jeff.utils.SPUtils
import kotlin_adapter.adapter_core.extension.putItems


/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-27 23:16
 * description ：MainFragment 主页显示
 */
class MainFragment : MainFragment() {
    override fun lazyLoad() {
        super.lazyLoad()
        //可见时,刷新数据
        async {
            await<Unit> {
                //加载测试数据
                //  //获得最顶上的数据
                topicList = SPUtils.getSceneBeans(mActivity, "sceneList")
                //   //获得组列表数据
                groupList = SPUtils.getGroupBeans(mActivity, "grouplist")
                var groupBean: GroupBean = GroupBean()
                groupBean.groupName = "All Device";
                groupBean.groupId = 1
                groupBean.meshAddress = 0xFFFF;
                groupBean.brightness = 100;
                groupBean.connectionStatus = 1;
                groupList.add(0, groupBean)

                // //获得单个设备列表数据
                singleList = SPUtils.getDeviceBeans(mActivity, "deviceSingleList")
            }
            //最顶层的列表
            topAdapter!!.putItems(topicList)
            topAdapter!!.notifyDataSetChanged()
            //组 数据列表
            groupAdapter!!.putItems(groupList)
            groupAdapter!!.notifyDataSetChanged()
            //  //单个数据列表
            singleAdapter!!.putItems(singleList)
            singleAdapter!!.notifyDataSetChanged()
        }
    }

    val opcode = 0xD0.toByte()
    //开
    val paramsOn = byteArrayOf(0x01, 0x00, 0x00)
    //关
    val paramsOff = byteArrayOf(0x00, 0x00, 0x00)

    override fun groupClickListener(groupBean: GroupBean): Boolean {
        return true
    }

    override fun groupToggleToOn(groupBean: GroupBean): Boolean {
        mLightService.sendCommandNoResponse(opcode, groupBean.meshAddress, paramsOn)
        return true
    }

    override fun groupToggleToOff(groupBean: GroupBean): Boolean {
        mLightService.sendCommandNoResponse(opcode, groupBean.meshAddress, paramsOff)
        return true
    }

    //点击按钮 开 返回的事件
    override fun singleToggleToOn(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 开 返回的事件 deviceBean= ${deviceBean.toString()} ")
        mLightService.sendCommandNoResponse(opcode, deviceBean.meshAddress, paramsOn)
        return true
    }

    //点击按钮 关 返回的事件
    override fun singleToggleToOff(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 关 返回的事件 deviceBean= ${deviceBean.toString()} ")
        mLightService.sendCommandNoResponse(opcode, deviceBean.meshAddress, paramsOff);
        return true
    }

    //点击列表事件
    override fun singleClickListener(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceBean.toString()} ")
        mActivity.startActivity(Intent(mActivity, AdjustActivity::class.java))
        return true
    }

}