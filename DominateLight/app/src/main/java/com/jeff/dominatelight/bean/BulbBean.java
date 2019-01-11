package com.jeff.dominatelight.bean;

/**
 * Created by liucr on 2016/1/14.
 */

import java.io.Serializable;

/**
 * 灯
 */
public class BulbBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String displayName;

    private boolean firstSetShowOnHome;

    private boolean showOnHome;

    /**
     * 1:// 只可以调亮度  5//可以调亮度和色温
     */
    private long bulbType;

    private long deviceMesh;

    private String macAddress;

    private String firmwareRevision;

    private boolean hadAddToDefault;

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setMacAddres(String macAddres) {
        this.macAddress = macAddres;
    }

    public String getMacAddres() {
        return macAddress;
    }

    public void setFirstSetShowOnHome(boolean firstSetShowOnHome) {
        this.firstSetShowOnHome = firstSetShowOnHome;
    }

    public boolean isFirstSetShowOnHome() {
        return firstSetShowOnHome;
    }

    public void setBulbType(long bulbType) {
        this.bulbType = bulbType;
    }

    public long getBulbType() {
        return bulbType;
    }

    public void setDeviceMesh(long deviceAddr) {
        this.deviceMesh = deviceAddr;
    }

    public long getDeviceMesh() {
        return deviceMesh;
    }

    public void setFirmwareRevision(String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }

    public String getFirmwareRevision() {
        return firmwareRevision;
    }

    public void setShowOnHome(boolean showOnHome) {
        this.showOnHome = showOnHome;
    }

    public boolean isShowOnHome() {
        return showOnHome;
    }

    public void setHadAddToDefault(boolean hadAddToDefault) {
        this.hadAddToDefault = hadAddToDefault;
    }

    public boolean isHadAddToDefault() {
        return hadAddToDefault;
    }

    public BulbBean() {

    }

}
