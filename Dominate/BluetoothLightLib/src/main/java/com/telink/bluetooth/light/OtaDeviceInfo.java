/*
 * Copyright (C) 2015 The Telink Bluetooth Light Project
 *
 */
package com.telink.bluetooth.light;

import android.os.Parcel;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：  OTA设备信息
 */
public class OtaDeviceInfo extends DeviceInfo {

    public static final Creator<OtaDeviceInfo> CREATOR = new Creator<OtaDeviceInfo>() {
        @Override
        public OtaDeviceInfo createFromParcel(Parcel in) {
            return new OtaDeviceInfo(in);
        }

        @Override
        public OtaDeviceInfo[] newArray(int size) {
            return new OtaDeviceInfo[size];
        }
    };

    /**
     * firmware数据
     */
    public byte[] firmware;
    /**
     * ota进度
     */
    public int progress;

    public OtaDeviceInfo() {
    }

    public OtaDeviceInfo(Parcel in) {
        super(in);
        this.progress = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.progress);
    }
}
