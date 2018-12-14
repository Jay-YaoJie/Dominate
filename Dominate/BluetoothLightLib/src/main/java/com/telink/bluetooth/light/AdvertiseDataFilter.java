/*
 * Copyright (C) 2015 The Telink Bluetooth Light Project
 *
 */
package com.telink.bluetooth.light;

import android.bluetooth.BluetoothDevice;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ： 广播包过滤接口
 *
 * @param <E>
 */
public interface AdvertiseDataFilter<E extends LightPeripheral> {

    /**
     * 过滤接口
     *
     * @param device     扫描到的蓝牙设备
     * @param rssi       信号强度
     * @param scanRecord 广播数据包
     * @return
     */
    E filter(BluetoothDevice device, int rssi,
             byte[] scanRecord);
}
