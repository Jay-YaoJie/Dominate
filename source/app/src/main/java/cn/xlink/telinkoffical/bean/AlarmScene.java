package cn.xlink.telinkoffical.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liucr on 2016/4/9.
 */
public class AlarmScene implements Serializable {

    private Integer sceneId;
    private Integer sceneTimerId;
    private Integer timerType;
    private Integer hour;
    private Integer minute;
    private Integer workDay;
    private Boolean isEnable;
    private List<AlarmDevice> alarmDevices;

    public void setSceneId(Integer sceneId) {
        this.sceneId = sceneId;
    }

    public Integer getSceneId() {
        return sceneId;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }

    public Boolean getEnable() {
        return isEnable;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getHour() {
        return hour;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setSceneTimerId(Integer sceneTimerId) {
        this.sceneTimerId = sceneTimerId;
    }

    public Integer getSceneTimerId() {
        return sceneTimerId;
    }

    public void setTimerType(Integer timerType) {
        this.timerType = timerType;
    }

    public Integer getTimerType() {
        return timerType;
    }

    public void setWorkDay(Integer workDay) {
        this.workDay = workDay;
    }

    public Integer getWorkDay() {
        return workDay;
    }

    public void setAlarmDevices(List<AlarmDevice> alarmDevices) {
        this.alarmDevices = alarmDevices;
    }

    public List<AlarmDevice> getAlarmDevices() {
        return alarmDevices;
    }
}
