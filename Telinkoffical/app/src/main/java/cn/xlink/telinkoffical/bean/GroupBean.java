package cn.xlink.telinkoffical.bean;

/**
 * Created by liucr on 2016/1/14.
 */

import java.io.Serializable;
import java.util.List;

/**
 * 分组
 */
public class GroupBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String displayName;

    private boolean showOnHomeScreen;

    private long groupAddr;

    private String createDate;

    private String modifyDate;

    private List<Integer> bulbMeshArray;

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setShowOnHomeScreen(boolean showOnHomeScreen) {
        this.showOnHomeScreen = showOnHomeScreen;
    }

    public boolean isShowOnHomeScreen() {
        return showOnHomeScreen;
    }

    public void setGroupAddr(long groupAddr) {
        this.groupAddr = groupAddr;
    }

    public long getGroupAddr() {
        return groupAddr;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setBulbMeshArray(List<Integer> bulbMeshArray) {
        this.bulbMeshArray = bulbMeshArray;
    }

    public List<Integer> getBulbMeshArray() {
        return bulbMeshArray;
    }

    public GroupBean(){

    }

}
