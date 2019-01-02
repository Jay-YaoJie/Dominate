package jeff.beans

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-27 22:10
 * description ：FragmentAdapterBeans  保存为fragment里的adapter数据对象
 */
class FragmentAdapterBeans {
    class DeviceBean {
        var meshAddress: Int = 0 //灯的名称 数据列表12345
        var status: Int = 0
        var brightness: Int = 0 ////当前状态为0关 或着 100  开
        var reserve: Int = 0
        //  connectionStatus OFF(0), ON(1), OFFLINE(2);  关，开，离线
        //   OFF(0), ON(1), OFFLINE(2); var connectionStatus: ConnectionStatus? = null
        var connectionStatus: Int = 0

        //主页里选择适配使用的对象
        var imgAny: Any? = null//图片
        var textStr: String? = null//文字
        var groupId: Int = -1//当前id

        var deviceName: String? = null//设备里的名称
        var index: Int = -1 //当前所在的下标


        override fun toString(): String {
            return "deviceBean(imgAny=$imgAny," +
                    " textStr=$textStr," +
                    " groupId=$groupId, " +
                    "deviceName=$deviceName, " +
                    "index=$index)"
        }
    }

    class DeviceI {//出厂设备信息

        var macAddress: String? = null // Mac地址
        var deviceName: String? = null//设备名称
        var meshName: String? = null//网络名称
        var meshAddress: Int = 0// 网络地址
        var meshUUID: Int = 0
        var productUUID: Int = 0 //设备的产品标识符
        var status: Int = 0
        var longTermKey = ByteArray(16)
        var firmwareRevision: String? = null // 设备的firmware版本
    }

}