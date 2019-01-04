package main

import android.content.Intent
import co.metalab.asyncawait.async
import device.DeviceScaningActivity
import jeff.constants.DeviceBean
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
    override fun initViews() {
        super.initViews()
        binding.mainFragmentDeviceUngroupedTV.setOnClickListener {
            mActivity.startActivity(Intent(mActivity, DeviceScaningActivity::class.java))
        }
    }


    override fun lazyLoad() {
        super.lazyLoad()
        //可见时,刷新数据
        async {
            await<Unit> {
                //加载测试数据
                //  //获得最顶上的数据
                topicList = SPUtils.getDeviceBeans(mActivity, "deviceTopList")
                //   //获得组列表数据
                groupList = SPUtils.getDeviceBeans(mActivity, "deviceGroupList")
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

    //点击按钮 开 返回的事件
    override fun singleToggleToOn(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tagFragment, "点击按钮 开 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return true
    }

    //点击按钮 关 返回的事件
    override fun singleToggleToOff(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tagFragment, "点击按钮 关 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return true
    }

    //点击列表事件
    override fun singleClickListener(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tagFragment, "点击列表事件 deviceBean= ${deviceBean.toString()} ")
        return true
    }

}