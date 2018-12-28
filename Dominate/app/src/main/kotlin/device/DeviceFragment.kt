package device

import co.metalab.asyncawait.async
import com.jeff.dominate.model.Lights
import jeff.beans.FragmentAdapterBeans
import jeff.device.DeviceFragment
import jeff.utils.SPUtils

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-28.
 * description ：DeviceFragment
 */
class DeviceFragment : DeviceFragment () {
    override fun initViews() {
        super.initViews()
        async {
            await<Unit> {
                //加载测试数据
                infoGroupList()
                infoSingleList()
            }
            info()//加载数据列表适配器
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
//        var lights= Lights.getInstance().get()//获得所有设备
//        for (light in lights) {
//            var device: FragmentAdapterBeans.deviceBean = FragmentAdapterBeans.deviceBean()
//            //主页里选择适配使用的对象
//            // device.imgAny//: Any? = null//图片
//            device.textStr=light.deviceName//: String? = null//文字
//            // device.groupId//: Int = 0//当前id
//            device.idInt=light.meshAddress//:Int=0//单个设备时的id
//            device.deviceName=light.macAddress//: String? = null//设备里的名称
//            device.index=light.meshUUID//: Int = 0 //当前所在的下标
//            device.checke=false//: Boolean = false//当前是否选择
//            // OFF(0), ON(1), OFFLINE(2);  关，开，离线
//            device.connectionStatus=light.connectionStatus.value//:Int=0
//            singleList.add(device)
//        }


//        val icListStr = SPUtils.getFragmentAdapterBeans(mActivity, "infoSingleList")
//        if (icListStr == null) {
//            //如果没有初始化过对象，则初始化数据对象并保存
//        } else {
//            singleList = icListStr
//        }
    }
}