package cn.xlink.telinkoffical.bean;

import java.io.Serializable;

import cn.xlink.telinkoffical.bean.BulbBean;

/**
 * Created by liucr on 2016/1/14.
 */
public class ActionScene implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sceneId;
    private Integer brightness;
    private Integer temperature;
    private Integer color;
    private Integer deviceMesh;

    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Integer getColor() {
        return color;
    }

    public void setDeviceMesh(Integer deviceMesh) {
        this.deviceMesh = deviceMesh;
    }

    public Integer getDeviceMesh() {
        return deviceMesh;
    }

    public void setSceneId(Integer sceneId) {
        this.sceneId = sceneId;
    }

    public Integer getSceneId() {
        return sceneId;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getTemperature() {
        return temperature;
    }
}
