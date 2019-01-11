package com.jeff.dominatelight.bean;

import java.io.Serializable;

/**
 * Created by liucr on 2016/4/9.
 */
public class AlarmDevice implements Serializable {

    private Integer alarmId;
    private Integer deviceMesh;

    public void setAlarmId(Integer alarmId) {
        this.alarmId = alarmId;
    }

    public Integer getAlarmId() {
        return alarmId;
    }

    public void setDeviceMesh(Integer drviceMesh) {
        this.deviceMesh = drviceMesh;
    }

    public Integer getDeviceMesh() {
        return deviceMesh;
    }
}
