package com.jeff.dominate.model;

import com.telink.bluetooth.light.ConnectionStatus;

import java.io.Serializable;

public final class Light implements Serializable{

    public String deviceName;
    public String meshName;
    public String macAddress;
    public int meshAddress;
    public int brightness;
    public int color;
    public int temperature;
    public ConnectionStatus connectionStatus;
//    public DeviceInfo raw;
    public boolean selected;
    public int textColor;
    public String firmwareRevision;

    public int meshUUID;
    public int productUUID;

    public int status;
    public byte[] longTermKey = new byte[16];

    public String getLabel() {
        return Integer.toString(this.meshAddress, 16) + ":" + this.brightness;
    }

    public String getLabel1() {
        return  "bulb-" + Integer.toString(this.meshAddress, 16);
    }

    public String getLabel2() {
        return Integer.toString(this.meshAddress, 16);
    }


}
