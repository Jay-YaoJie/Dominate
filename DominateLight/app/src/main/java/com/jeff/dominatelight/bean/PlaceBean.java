package com.jeff.dominatelight.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liucr on 2016/1/13.
 */
public class PlaceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<BulbBean> bulbsArray;

    private List<SceneBean> sceneArray;

    private List<GroupBean> groupsArray;

    private AdminBean adminBean;

    private String placeName;

    private String createDate;

    private String lastUseDate;

    private long placeVersion;

    private int deviceIdRecord;

    public void setBulbsArray(List<BulbBean> bulbsArray) {
        this.bulbsArray = bulbsArray;
    }

    public List<BulbBean> getBulbsArray() {
        return this.bulbsArray;
    }

    public void setSceneArray(List<SceneBean> sceneArray) {
        this.sceneArray = sceneArray;
    }

    public List<SceneBean> getSceneArray() {
        return this.sceneArray;
    }

    public void setGroupsArray(List<GroupBean> groupsArray) {
        this.groupsArray = groupsArray;
    }

    public List<GroupBean> getGroupsArray() {
        return this.groupsArray;
    }

    public void setAdminBean(AdminBean adminBean) {
        this.adminBean = adminBean;
    }

    public AdminBean getAdminBean() {
        return adminBean;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceName() {
        return this.placeName;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateDate() {
        return this.createDate;
    }

    public void setLastUseDate(String lastUseDate) {
        this.lastUseDate = lastUseDate;
    }

    public String getLastUseDate() {
        return this.lastUseDate;
    }

    public void setPlaceVersion(long placeVersion) {
        this.placeVersion = placeVersion;
    }

    public long getPlaceVersion() {
        return placeVersion;
    }

    public void setDeviceIdRecord(int deviceIdRecord) {
        this.deviceIdRecord = deviceIdRecord;
    }

    public int getDeviceIdRecord() {
        return deviceIdRecord;
    }

    public PlaceBean(){

    }


}
