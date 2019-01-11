package jeff.constants

import android.content.res.ColorStateList
import java.util.*
import kotlin.collections.ArrayList

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2019-01-04.
 * description ：DeviceBean   Device数据对象
 */
class DeviceBean {
    var meshAddress: Int = 0 //灯的名称 数据列表12345
    var meshUUID: Int = 0
    var status: Int = 0
    var brightness: Int = 0 ////当前状态为0关 或着 100  开
    var reserve: Int = 0
    //  connectionStatus OFF(0), ON(1), OFFLINE(2);  关，开，离线
    //   OFF(0), ON(1), OFFLINE(2); var connectionStatus: ConnectionStatus? = null
    var connectionStatus: Int = 0

    //主页里选择适配使用的对象
    var sceneImgAnyList: ArrayList<Any> = ArrayList() //图片
    var sceneNameList: ArrayList<String> = ArrayList() //文字  情景的名称
    var sceneMeshAddressList: ArrayList<Int> = ArrayList()  //当前情景的Mesh
    var sceneIndexId: Int = 0;//当前在多少个组里面

    var groupMeshAddressList: ArrayList<Int> = ArrayList()//当前组的Mesh
    var groupNameList: ArrayList<String> = ArrayList()//当前组的名称
    var groupIndexId: Int = 0;//当前在多少个组里面


    var productUUID: Int = 0 //设备的产品标识符
    var macAddress: String? = "" // Mac地址
    var meshName: String? = ""//网络名称
    var deviceName: String? = ""//设备里的名称
    var index: Int = -1 //当前所在的下标

    var firmwareRevision: String? = "" // 设备的firmware版本
    var longTermKey = ByteArray(16)
    var checkd: Boolean = false

}

//分组
class GroupBean {
    //分组的组名
    var groupName: String? = "" //当前组的名称
    var groupId: Int = -1//当前id
    //以下是 组里的控制器
    var meshAddress: Int = 0 //灯的名称 数据列表12345
    var meshUUID: Int = 0
    var status: Int = 0
    var brightness: Int = 0 ////当前状态为0关 或着 100  开
    var reserve: Int = 0
    //  connectionStatus OFF(0), ON(1), OFFLINE(2);  关，开，离线
    //   OFF(0), ON(1), OFFLINE(2); var connectionStatus: ConnectionStatus? = null
    var connectionStatus: Int = 0
    var checkd: Boolean = false
    override fun toString(): String {
        return "GroupBean(" +
                "groupName=$groupName," +
                "groupId=$groupId," +
                "meshAddress=$meshAddress," +
                "meshUUID=$meshUUID," +
                "status=$status," +
                "brightness=$brightness," +
                "reserve=$reserve," +
                "connectionStatus=$connectionStatus," +
                "checkd=$checkd" +
                ")"
    }


}


class SceneBean {
    //主页里选择适配使用的对象
    var imgAny: Any? = ""//图片
    var sceneName: String? = ""//文字  情景的名称
    //以下是 组里的控制器
    var meshAddress: Int = 0 //灯的名称 数据列表12345
    var meshUUID: Int = 0
    var status: Int = 0
    var brightness: Int = 0 ////当前状态为0关 或着 100  开
    var reserve: Int = 0
    //  connectionStatus OFF(0), ON(1), OFFLINE(2);  关，开，离线
    //   OFF(0), ON(1), OFFLINE(2); var connectionStatus: ConnectionStatus? = null
    var connectionStatus: Int = 0
    var checkd: Boolean = false
    override fun toString(): String {
        return "SceneBean(" +
                "imgAny=$imgAny," +
                "sceneName=$sceneName," +
                "meshAddress=$meshAddress," +
                "meshUUID=$meshUUID," +
                "status=$status," +
                "brightness=$brightness," +
                "reserve=$reserve," +
                "connectionStatus=$connectionStatus," +
                "checkd=$checkd" +
                ")"
    }

}