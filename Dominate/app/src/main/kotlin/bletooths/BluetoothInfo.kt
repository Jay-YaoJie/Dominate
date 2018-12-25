package bletooths

import com.telink.bluetooth.light.ConnectionStatus

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：BluetoothInfo 单个蓝牙对象
 */
class BluetoothInfo {

    //组名
    lateinit var groupName: String
    //保存当前对象是否被点击
    var checked: Boolean = false

    var icon: Int = 0
     var textColor: Int=0
    lateinit var deviceName: String
    lateinit var meshName: String
    lateinit var macAddress: String
    var meshAddress: Int = 0
    var brightness: Int = 0
    var color: Int = 0
    var temperature: Int = 0
    lateinit var connectionStatus: ConnectionStatus
    //    public DeviceInfo raw;
    var selected: Boolean = false

    lateinit var firmwareRevision: String

    var meshUUID: Int = 0
    var productUUID: Int = 0

    var status: Int = 0
    var longTermKey = ByteArray(16)

    fun getLabel(): String {
        return Integer.toString(this.meshAddress, 16) + ":" + this.brightness
    }

    fun getLabel1(): String {
        return "bulb-" + Integer.toString(this.meshAddress, 16)
    }

    fun getLabel2(): String {
        return Integer.toString(this.meshAddress, 16)
    }

    //场景模式对象 起床，影视
    class SceneInfo {
        var id: Int = 0
        //模式图片
        lateinit var imgUrl: Any
        //名字
        lateinit var name: String
        var opCode: Byte = 0
        var vendorId: Int = 0
        var address: Int = 0
        lateinit var params: ByteArray
        //保存当前对象是否被点击
        var checked: Boolean = false
    }
}
