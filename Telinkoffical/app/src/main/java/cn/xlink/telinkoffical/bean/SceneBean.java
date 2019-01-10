package cn.xlink.telinkoffical.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liucr on 2016/1/14.
 */
public class SceneBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean showOnHomeScreen;

    private long sceneId;

    private String name;

    /**
     *    WAKE UP = 1, MOVIE TIME = 4, BEDTIME = 2, GET HOME = 3 ，用户创建：5
     */
    private long sceneType;

    /**
     *    WAKE UP = 1, MOVIE TIME = 4, BEDTIME = 2, GET HOME = 3 ， 用户创建：5++
     */

    private List<ActionScene> actionArray;

    private List<AlarmScene> alarmScenes;

    public void setShowOnHomeScreen(boolean showOnHomeScreen) {
        this.showOnHomeScreen = showOnHomeScreen;
    }

    public boolean getShowOnHomeScreen() {
        return this.showOnHomeScreen;
    }

    public void setSceneType(long sceneType) {
        this.sceneType = sceneType;
    }

    public long getSceneType() {
        return this.sceneType;
    }

    public void setActionArray(List<ActionScene> actionArray) {
        this.actionArray = actionArray;
    }

    public List<ActionScene> getActionArray() {
        return this.actionArray;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSceneId(long sceneId) {
        this.sceneId = sceneId;
    }

    public long getSceneId() {
        return sceneId;
    }

    public void setAlarmScenes(List<AlarmScene> alarmScenes) {
        this.alarmScenes = alarmScenes;
    }

    public List<AlarmScene> getAlarmScenes() {
        return alarmScenes;
    }

    public SceneBean(){

    }

}
