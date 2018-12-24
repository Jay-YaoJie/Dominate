package bletooths

import com.clj.fastble.data.BleDevice

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：BluetoothInfo 单个蓝牙对象
 */
class BluetoothInfo {
    //  private var bleDevice: BleDevice? = null//保存的蓝牙数据对象
    var bleDevice: BleDevice?
        get() {
            return bleDevice
        }
        set(value) {
            bleDevice = value
        }
}