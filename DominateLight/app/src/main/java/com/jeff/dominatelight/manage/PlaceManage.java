package com.jeff.dominatelight.manage;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import com.jeff.dominatelight.MyApp;
import com.jeff.dominatelight.bean.greenDao.*;
import com.jeff.dominatelight.eventbus.ChangePlaceEvent;
import com.jeff.dominatelight.model.*;
import com.jeff.dominatelight.service.TelinkLightService;
import com.jeff.dominatelight.utils.*;
import com.telink.bluetooth.light.Manufacture;

import java.util.List;


/**
 * Created by liucr on 2016/4/9.
 */
public class PlaceManage {

    private static Handler handler = new Handler();

    private static Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            if (TelinkLightService.Instance() != null) {
                TelinkLightService.Instance().idleMode(true);
            }
            MyApp.getApp().autoConnect(true);
        }
    };

    public static int changePlaceByMesh(String uId, String mesh) {
        PlaceSort placeSort = PlacesDbUtils.getInstance().getPlaceByMac(PlacesDbUtils.getType(uId), mesh);
        if(placeSort == null){
            return 1;
        }
        changePlace(placeSort);
        return 0;
    }

    public static void changePlace(PlaceSort placeSort) {

        if (Places.getInstance().getCurPlaceSort() != null
                && Places.getInstance().getCurPlaceSort().getMeshAddress().equals(placeSort.getMeshAddress())) {
            return;
        }

        Places.getInstance().setCurPlaceSort(placeSort);

        loadCurPlaceData();
        MyApp.getApp().setCanShowConnectLoading(true);

        handler.removeCallbacks(connectRunnable);
        handler.postDelayed(connectRunnable, 1000);

    }

    public static void loadCurPlaceData(){
        Lights.getInstance().clear();
        Lights.getInstance().get().addAll(LightsDbUtils.getInstance().getCurAccountLights());

        Groups.getInstance().clear();
        Groups.getInstance().get().addAll(GroupsDbUtils.getInstance().getCurAccountGroups());

        Scenes.getInstance().clear();
        Scenes.getInstance().get().addAll(ScenesDbUtils.getInstance().getCurAccountScenes());

        EventBusUtils.getInstance().dispatchEvent(new ChangePlaceEvent("MyApp", ChangePlaceEvent.ChangePlaceEvent, Places.getInstance().getCurPlaceSort()));
    }

    public static void changeToCurUser(){
        Places.getInstance().clear();
        for(PlaceSort localPlaceSort : PlacesDbUtils.getInstance().getAllPlace()){
            Places.getInstance().add(localPlaceSort);
        }
    }

    /**
     * 切换到没用用户状态
     *
     * @param context
     */
    public static boolean changeToNoUser(Context context) {

        boolean isNew = false;

        Lights.getInstance().clear();
        Groups.getInstance().clear();
        Scenes.getInstance().clear();
        Places.getInstance().clear();

        List<PlaceSort> placeSorts = PlacesDbUtils.getInstance().getAllPlacesByType(PlacesDbUtils.getType(UserUtil.NoMaster));
        if (placeSorts == null || placeSorts.size() == 0) {
            PlaceSort placeSort = PlaceManage.initNewPlace(UserUtil.NoMaster);
            Places.getInstance().add(placeSort);
            PlaceManage.changePlace(placeSort);
            isNew = true;
        } else {
            for (PlaceSort placeSort : placeSorts) {
                Places.getInstance().add(placeSort);
                if (Places.getInstance().getCurPlaceSort() == null) {
                    PlaceManage.changePlace(placeSort);
                }
            }
        }

        Lights.getInstance().clear();
        Lights.getInstance().get().addAll(LightsDbUtils.getInstance().getCurAccountLights());

        Groups.getInstance().clear();
        Groups.getInstance().get().addAll(GroupsDbUtils.getInstance().getCurAccountGroups());

        Scenes.getInstance().clear();
        Scenes.getInstance().get().addAll(ScenesDbUtils.getInstance().getCurAccountScenes());

        return isNew;
    }

    public static void clearPlace() {

        TelinkLightService.Instance().idleMode(true);

        Lights.getInstance().clear();
        Groups.getInstance().clear();
        Scenes.getInstance().clear();
        Places.getInstance().clear();
        Places.getInstance().clearCur();
    }

    public static void clearPlaceDb(String uId, String mesh){
        PlaceSort placeSort = PlacesDbUtils.getInstance().getPlaceByMac(PlacesDbUtils.getType(uId), mesh);
        if(placeSort != null){
            clearPlaceDb(uId, placeSort);
        }
    }

    /**
     * 清楚数据库中的Place及旗下的资源
     *
     * @param uId
     * @param placeSort
     */
    public static void clearPlaceDb(String uId, PlaceSort placeSort) {

        LightsDbUtils.getInstance().deleteLight(uId, placeSort.getMeshAddress());

        GroupsDbUtils.getInstance().deleteGroupSort(uId, placeSort.getMeshAddress());

        for (Scene scene : ScenesDbUtils.getInstance().getAllScenesByType(
                ScenesDbUtils.getType(uId, placeSort.getMeshAddress()))) {

            SceneActionsDbUtils.getInstance().deleteBySceneId(
                    SceneActionsDbUtils.getType(uId, placeSort.getMeshAddress()), scene.getSceneSort().getSceneId());
            SceneTimersDbUtils.getInstance().deleteBySceneId(
                    SceneTimersDbUtils.getType(uId, placeSort.getMeshAddress()), scene.getSceneSort().getSceneId());

            ScenesDbUtils.getInstance().deleteSceneSort(scene.getSceneSort());
        }
        PlacesDbUtils.getInstance().deletePlace(placeSort);
    }

    /**
     * 用户第一次使用，初始化默认
     */
    public static PlaceSort initNewPlace(String uId) {

        PlaceSort placeSort = new PlaceSort();

        placeSort.setName("家");
        placeSort.setCreatorId(uId);
        placeSort.setDeviceIdRecord(0);
        placeSort.setMeshAddress(XlinkUtils.getVirtualMac());
        placeSort.setMeshKey(XlinkUtils.getRandomInt(6));
        placeSort.setFactoryName(Manufacture.getDefault().getFactoryName());
        placeSort.setFactoryMeshKey(Manufacture.getDefault().getFactoryPassword());
        placeSort.setCreateDate(System.currentTimeMillis() + "");

        PlacesDbUtils.getInstance().updataOrInsert(PlacesDbUtils.getType(uId), placeSort);

        SceneSort sceneSort;
        Scene scene;

        sceneSort = new SceneSort();
        sceneSort.setSceneType(Scene.SceneType.SCENETYPE_LEAVE);
        sceneSort.setSceneId(1);
        sceneSort.setName("离家");
        sceneSort.setIsShowOnHomeScreen(true);
        scene = new Scene(sceneSort);
        Scenes.getInstance().add(scene);
        ScenesDbUtils.getInstance().updataOrInsert(
                ScenesDbUtils.getType(uId, placeSort.getMeshAddress()), scene.getSceneSort());

        sceneSort = new SceneSort();
        sceneSort.setSceneType(Scene.SceneType.SCENETYPE_GOHOME);
        sceneSort.setSceneId(2);
        sceneSort.setName("回家");
        sceneSort.setIsShowOnHomeScreen(true);
        scene = new Scene(sceneSort);
        Scenes.getInstance().add(scene);
        ScenesDbUtils.getInstance().updataOrInsert(
                ScenesDbUtils.getType(uId, placeSort.getMeshAddress()), scene.getSceneSort());

        sceneSort = new SceneSort();
        sceneSort.setSceneType(Scene.SceneType.SCENETYPE_REST);
        sceneSort.setSceneId(3);
        sceneSort.setName("休息");
        sceneSort.setIsShowOnHomeScreen(true);
        scene = new Scene(sceneSort);
        Scenes.getInstance().add(scene);
        ScenesDbUtils.getInstance().updataOrInsert(
                ScenesDbUtils.getType(uId, placeSort.getMeshAddress()), scene.getSceneSort());

        sceneSort = new SceneSort();
        sceneSort.setSceneType(Scene.SceneType.SCENETYPE_TV);
        sceneSort.setSceneId(4);
        sceneSort.setName("电视");
        sceneSort.setIsShowOnHomeScreen(true);
        scene = new Scene(sceneSort);
        Scenes.getInstance().add(scene);
        ScenesDbUtils.getInstance().updataOrInsert(
                ScenesDbUtils.getType(uId, placeSort.getMeshAddress()), scene.getSceneSort());

        GroupSort groupSort = new GroupSort();
        groupSort.setName("全部");
        groupSort.setMeshAddress(0x8001);
        groupSort.setTemperature(0);
        groupSort.setIsShowOnHomeScreen(true);
        groupSort.setColor(Color.WHITE);
        groupSort.setBrightness(0);
        Group group = new Group(groupSort);
        Groups.getInstance().add(group);
        GroupsDbUtils.getInstance().updataOrInsert(
                GroupsDbUtils.getType(uId, placeSort.getMeshAddress()), groupSort);

        return placeSort;
    }

    public static PlaceSort copyPlace(PlaceSort placeSort, String newCreatId) {

        String oldCreatorId = placeSort.getCreatorId();
        String oldMesh = placeSort.getMeshAddress();

        PlaceSort newPlaceSort = placeSort;
        newPlaceSort.setId(null);
        newPlaceSort.setCreatorId(newCreatId);

        String lightType = LightsDbUtils.getType(oldCreatorId, oldMesh);
        String newLightType = LightsDbUtils.getType(newCreatId, oldMesh);
        for (Light light : LightsDbUtils.getInstance().getAllLightsByType(lightType)) {
            light.getLightSort().setId(null);
            LightsDbUtils.getInstance().updataOrInsert(newLightType, light.getLightSort());
        }

        String groupType = GroupsDbUtils.getType(oldCreatorId, oldMesh);
        String newGroupType = GroupsDbUtils.getType(newCreatId, oldMesh);
        for (Group group : GroupsDbUtils.getInstance().getAllGroupsByType(groupType)) {
            group.getGroupSort().setId(null);
            GroupsDbUtils.getInstance().updataOrInsert(newGroupType, group.getGroupSort());
        }

        String sceneType = ScenesDbUtils.getType(oldCreatorId, oldMesh);
        String newSceneType = ScenesDbUtils.getType(newCreatId, oldMesh);
        for (Scene scene : ScenesDbUtils.getInstance().getAllScenesByType(sceneType)) {

            String actionType = SceneActionsDbUtils.getType(oldCreatorId, oldMesh);
            String newActionType = SceneActionsDbUtils.getType(newCreatId, oldMesh);
            for (SceneActionSort actionSort : SceneActionsDbUtils.getInstance().getSceneActionSortById(
                    actionType , scene.getSceneSort().getSceneId())) {
                actionSort.setId(null);
                SceneActionsDbUtils.getInstance().updataOrInsert(newActionType, actionSort);
            }

            String timerType = SceneTimersDbUtils.getType(oldCreatorId, oldMesh);
            String newTimerType = SceneTimersDbUtils.getType(newCreatId, oldMesh);
            for (SceneTimerSort timerSort : SceneTimersDbUtils.getInstance().getSceneTimerSortById(
                    timerType, scene.getSceneSort().getSceneId())) {
                timerSort.setId(null);
                SceneTimersDbUtils.getInstance().updataOrInsert(newTimerType, timerSort);
            }

            scene.getSceneSort().setId(null);
            ScenesDbUtils.getInstance().updataOrInsert(newSceneType, scene.getSceneSort());
        }
        return newPlaceSort;
    }

}
