package cn.xlink.telinkoffical.bean.greenDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "SCENE_SORT".
 */
public class SceneSort {

    private Long id;
    private String name;
    private Integer sceneId;
    private Integer brightness;
    private Integer temperature;
    private Integer color;
    private Integer sceneType;
    private String actions;
    private Boolean isShowOnHomeScreen;
    private String Type;

    public SceneSort() {
    }

    public SceneSort(Long id) {
        this.id = id;
    }

    public SceneSort(Long id, String name, Integer sceneId, Integer brightness, Integer temperature, Integer color, Integer sceneType, String actions, Boolean isShowOnHomeScreen, String Type) {
        this.id = id;
        this.name = name;
        this.sceneId = sceneId;
        this.brightness = brightness;
        this.temperature = temperature;
        this.color = color;
        this.sceneType = sceneType;
        this.actions = actions;
        this.isShowOnHomeScreen = isShowOnHomeScreen;
        this.Type = Type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSceneId() {
        return sceneId;
    }

    public void setSceneId(Integer sceneId) {
        this.sceneId = sceneId;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Integer getSceneType() {
        return sceneType;
    }

    public void setSceneType(Integer sceneType) {
        this.sceneType = sceneType;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public Boolean getIsShowOnHomeScreen() {
        return isShowOnHomeScreen;
    }

    public void setIsShowOnHomeScreen(Boolean isShowOnHomeScreen) {
        this.isShowOnHomeScreen = isShowOnHomeScreen;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

}
