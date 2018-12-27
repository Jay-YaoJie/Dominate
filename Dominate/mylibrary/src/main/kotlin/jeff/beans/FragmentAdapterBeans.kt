package jeff.beans

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-27 22:10
 * description ：FragmentAdapterBeans  保存为fragment里的adapter数据对象
 */
class FragmentAdapterBeans {
    class deviceBean {
        //主页里选择适配使用的对象
        var imgAny: Any? = null//图片
        var textStr: String? = null//文字
        var groupId: Int = 0//当前id
        var deviceName: String? = null//设备里的名称
        var index: Int = 0 //当前所在的下标
        var checke: Boolean = false//当前是否选择
        override fun toString(): String {
            return "deviceBean(imgAny=$imgAny, textStr=$textStr, groupId=$groupId, deviceName=$deviceName, index=$index, checke=$checke)"
        }

    }
}