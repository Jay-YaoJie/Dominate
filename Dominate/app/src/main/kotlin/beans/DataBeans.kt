package beans

import com.telink.bluetooth.light.ConnectionStatus

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：DataBeans
 * 可以使用@SerializedName("title")来做为别名
 *   //  @SerializedName("title")  var title:String
 */
//灯的数据对象 //单个设备数据对象
 class LightBean{
    var deviceName: String? = null
    var meshName: String? = null
    var macAddress: String? = null
    var meshAddress: Int = 0
    var brightness: Int = 0
    var color: Int = 0
    var temperature: Int = 0
    var connectionStatus: ConnectionStatus? = null
    //    public DeviceInfo raw;
    var selected: Boolean = false
    var textColor: Int = 0
    var firmwareRevision: String? = null

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
    var light: LightBean=LightBean();
}

//主页列表显示**************************************************************/
//最顶的场景列表数据对象
data class MainTopBean(
        var title: String,//场景名称
        var imgUrl: Any,//场景图片
        var isOf: Boolean,//是否开启
        var id: Int//场景id
)

//组数据对象
data class MainGroupBean(
        //group 数据
        var groupName: String,//组名
        var isOf: Boolean,//是否开启
        var id: Int
)
