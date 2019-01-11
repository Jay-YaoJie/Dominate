/*
 * Copyright (C) 2015 The Telink Bluetooth Light Project
 *
 */
package com.telink.bluetooth.event;

import android.os.IBinder;

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：   LightService事件
 */
public class ServiceEvent extends DataEvent<IBinder> {

    /**
     * 服务启动
     */
    public static final String SERVICE_CONNECTED = "com.telink.bluetooth.light.EVENT_SERVICE_CONNECTED";
    /**
     * 服务关闭
     */
    public static final String SERVICE_DISCONNECTED = "com.telink.bluetooth.light.EVENT_SERVICE_DISCONNECTED";

    public ServiceEvent(Object sender, String type, IBinder args) {
        super(sender, type, args);
    }

    public static ServiceEvent newInstance(Object sender, String type, IBinder args) {
        return new ServiceEvent(sender, type, args);
    }
}