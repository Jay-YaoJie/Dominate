package main

import android.content.Intent
import co.metalab.asyncawait.async
import com.google.gson.Gson
import jeff.beans.FragmentAdapterBeans.deviceBean
import jeff.main.MainFragment
import jeff.utils.SPUtils
import com.google.gson.reflect.TypeToken
import com.jeff.dominate.activity.DeviceScanningActivity
import com.jeff.dominate.model.Light
import com.jeff.dominate.model.Lights
import com.telink.bluetooth.light.ConnectionStatus
import jeff.utils.LogUtils


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
       var lights=Lights.getInstance().get()//获得所有设备
        for (light in lights) {
           var device: deviceBean= deviceBean()
            //主页里选择适配使用的对象
           // device.imgAny//: Any? = null//图片
            device.textStr=light.deviceName//: String? = null//文字
           // device.groupId//: Int = 0//当前id
            device.idInt=light.meshAddress//:Int=0//单个设备时的id
            device.deviceName=light.macAddress//: String? = null//设备里的名称
            device.index=light.meshUUID//: Int = 0 //当前所在的下标
            device.checke=false//: Boolean = false//当前是否选择
            // OFF(0), ON(1), OFFLINE(2);  关，开，离线
            device.connectionStatus=light.connectionStatus.value//:Int=0
            singleList.add(device)
        }
        if (singleList.size<=0){
            mActivity.startActivity(Intent(mActivity, DeviceScanningActivity::class.java))

        }

//        val icListStr = SPUtils.getFragmentAdapterBeans(mActivity, "infoSingleList")
//        if (icListStr == null) {
//            //如果没有初始化过对象，则初始化数据对象并保存
//        } else {
//            singleList = icListStr
//        }
    }
}