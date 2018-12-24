package bletooths

import android.os.Handler
import bases.DominateApplication.Companion.dominate
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import utils.LogUtils
import java.util.*

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：BLES Ble工具进行 扫描、连接、读、写、通知订阅与取消 基本操作
 */
object BLES {
    val tag: String = "Ble工具"
    //注册蓝牙工具，设置连接配置
    fun initBleManager() {
        BleManager.getInstance().init(dominate)//初始化配置
        BleManager.getInstance()
                // .enableLog(true)
                // .setReConnectCount(0, 1000*10)//设置重新连接
                // .setSplitWriteNum(20) //设置最大  同时写入数量
                .setConnectOverTime(1000)
                .operateTimeout = 1000//设置超时连接
    }

    //当前蓝牙操作集合对象
    var list: List<BluetoothInfo> = ArrayList();
    fun scanCallback(handler: Handler) =
    // 开始扫描
            BleManager.getInstance().scan(object : BleScanCallback() {
                override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                    LogUtils.d(tag, "扫描蓝牙结束。。" + scanResultList!!.size);
                    //T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    handler.sendEmptyMessage(1001)
                }

                override fun onScanStarted(success: Boolean) {
                    //T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    LogUtils.d(tag, "开始扫描蓝牙。。onScanStarted(success: Boolean)success=" + success)

                }

                override fun onScanning(bleDevice: BleDevice?) {
                    //T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    LogUtils.d(tag, "正在扫描蓝牙。。" + bleDevice.toString() + "---bleDevice.getName()=" + bleDevice!!.getName());
                   var boolean=true;
                    //循环比对是否已经添加过了
                    for ( isInfo:BluetoothInfo in list){
                        if (isInfo.bleDevice!!.mac.equals(bleDevice.mac)){
                            boolean=false
                        }
                    }
                    if (boolean){//如果已经添加过了就不用在添加了
                        var info: BluetoothInfo = BluetoothInfo()
                        info.bleDevice=bleDevice;
                        list +=info
                    }
                }
            })

}