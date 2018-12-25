package bletooths

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import bases.DominateApplication.Companion.dominate
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.jeff.dominate.R
import com.telink.bluetooth.LeBluetooth
import utils.LogUtils
import utils.ToastUtil
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

    //传入handle数据
    private fun handlerSend(handler: Handler, any: Any) {
        if (handler == null) {
            return;
        }
        when (any) {
            any is Int -> {//如果是int则发送整形
                handler.sendEmptyMessage(any as Int)
                //handler.obtainMessage(any).sendToTarget()
            }
            any is String -> {
                val message = Message.obtain();
                message.obj = any
                handler.sendMessage(message)
            }
            any is Bundle -> {
                val message = Message.obtain();
                message.data = any as Bundle
                handler.sendMessage(message)
            }
            any is Message -> {//如果是Message，则直接发送
                handler.sendMessage(any as Message)
            }
        }

    }

    //    if (!LeBluetooth.getInstance().isSupport(getApplicationContext())) {
//        Toast.makeText(this, "ble not support", Toast.LENGTH_SHORT).show();
//        this.finish();
//        return;
//    }
    //检查是否支持蓝牙设备
    fun isBle(): Boolean {
        if (!LeBluetooth.getInstance().isSupport(dominate)) {
            ToastUtil.show(R.string.ble_not_support)
            return false;
        } else {
            return true
        }
    }

    //蓝牙监听 handler= （1001 蓝牙开启，1002 蓝牙关闭）
    fun bleReceiver(handler: Handler): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                //T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                val action = intent!!.action
                if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)

                    when (state) {
                        BluetoothAdapter.STATE_ON -> {
                            LogUtils.d(tag, "蓝牙开启 BluetoothAdapter.STATE_ON=" + BluetoothAdapter.STATE_ON)
//                        Log.d(TAG, "蓝牙开启")
                            handlerSend(handler, 1001)
//                        TelinkLightService.Instance().idleMode(true)
//                        autoConnect()
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            handlerSend(handler, 1002)
                            LogUtils.d(tag, "蓝牙关闭 BluetoothAdapter.STATE_OFF=" + BluetoothAdapter.STATE_OFF)
                        }
                    }
                }
            }
        }

    }

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

    //2001
    fun scanCallback(handler: Handler) =
    // 开始扫描
            BleManager.getInstance().scan(object : BleScanCallback() {
                override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                    LogUtils.d(tag, "扫描蓝牙结束。。" + scanResultList!!.size);
                    //T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    handlerSend(handler, 2001)
                }

                override fun onScanStarted(success: Boolean) {
                    //T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    LogUtils.d(tag, "开始扫描蓝牙。。onScanStarted(success: Boolean)success=" + success)

                }

                override fun onScanning(bleDevice: BleDevice?) {
                    //T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    LogUtils.d(tag, "正在扫描蓝牙。。" + bleDevice.toString() + "---bleDevice.getName()=" + bleDevice!!.getName());
                    var boolean = true;
                    //循环比对是否已经添加过了
                    for (isInfo: BluetoothInfo in list) {
//                        if (isInfo.bleDevice!!.mac.equals(bleDevice.mac)) {
//                            boolean = false
//                        }
                    }
                    if (boolean) {//如果已经添加过了就不用在添加了
                        var info: BluetoothInfo = BluetoothInfo()
//                        info.bleDevice = bleDevice;
//                        list += info
                    }
                }
            })

}