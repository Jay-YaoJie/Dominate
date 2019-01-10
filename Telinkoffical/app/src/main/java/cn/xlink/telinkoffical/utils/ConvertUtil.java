package cn.xlink.telinkoffical.utils;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.telinkoffical.bean.ActionScene;
import cn.xlink.telinkoffical.bean.AdminBean;
import cn.xlink.telinkoffical.bean.AlarmDevice;
import cn.xlink.telinkoffical.bean.AlarmScene;
import cn.xlink.telinkoffical.bean.BulbBean;
import cn.xlink.telinkoffical.bean.GroupBean;
import cn.xlink.telinkoffical.bean.PlaceBean;
import cn.xlink.telinkoffical.bean.SceneBean;
import cn.xlink.telinkoffical.bean.greenDao.GroupSort;
import cn.xlink.telinkoffical.bean.greenDao.LightSort;
import cn.xlink.telinkoffical.bean.greenDao.PlaceSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneActionSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneTimerSort;
import cn.xlink.telinkoffical.bean.greenDao.User;
import cn.xlink.telinkoffical.model.Group;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Scene;


/**
 * Created by liucr on 2015/12/25.
 */
public class ConvertUtil {

    /****************************************
     * 写入数据库的数据转换
     ****************************************/

    public static User userToDao(long maxId, String type, User olduser) {
        User user = new User(maxId);
        user.setType(type);
        user.setAccount(encode(olduser.getAccount()));
        user.setPassword(encode(olduser.getPassword()));
        user.setUid(encode(olduser.getUid()));
        user.setName(encode(olduser.getName() == null ? "" : olduser.getName()));
        user.setAuthKey(encode(olduser.getAuthKey()));
        user.setAccessToken(encode(olduser.getAccessToken()));
        user.setRefreshToken(encode(olduser.getRefreshToken()));
        return user;
    }

    public static List<User> daoToUser(List<User> users) {
        List<User> newUsers = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User user = new User();
            user.setAccount(decode(users.get(i).getAccount()));
            user.setPassword(decode(users.get(i).getPassword()));
            user.setUid(decode(users.get(i).getUid()));
            user.setName(decode(users.get(i).getName() == null ? "" : users.get(i).getName()));
            user.setAuthKey(decode(users.get(i).getAuthKey()));
            user.setAccessToken(decode(users.get(i).getAccessToken()));
            user.setRefreshToken(decode(users.get(i).getRefreshToken()));
            newUsers.add(user);
        }
        return newUsers;
    }

    private static String encode(String str) {
        byte[] bytes = new byte[]{};
        return Base64.encodeToString(str == null ? bytes : str.getBytes(), Base64.DEFAULT);
    }

    private static String decode(String strBase64) {
        return new String(Base64.decode(strBase64.getBytes(), Base64.DEFAULT));
    }

    /**
     * @param placeSort
     * @return
     */
    public static PlaceBean placeSortToBean(PlaceSort placeSort) {

        String typeuId = UserUtil.getUser().getUid();
        String typeMesh = placeSort.getMeshAddress();

        PlaceBean placeBean = new PlaceBean();
        placeBean.setPlaceName(placeSort.getName());
        placeBean.setPlaceVersion(placeSort.getPlaceVersion());
        placeBean.setCreateDate(placeSort.getCreateDate());
        placeBean.setDeviceIdRecord(placeSort.getDeviceIdRecord());

        AdminBean adminBean = new AdminBean();
        adminBean.setEmailAddress(placeSort.getCreatorAccount());
        adminBean.setUserID(Long.parseLong(placeSort.getCreatorId()));
        adminBean.setUsername(UserUtil.getUser().getName());
        placeBean.setAdminBean(adminBean);

        List<BulbBean> bulbBeens = new ArrayList<>();
        for (Light light : LightsDbUtils.getInstance().getAllLightsByType(LightsDbUtils.getType(typeuId, typeMesh))) {
            bulbBeens.add(lightSortToBean(light.getLightSort()));
        }
        placeBean.setBulbsArray(bulbBeens);

        List<GroupBean> groupBeens = new ArrayList<>();
        for (Group group : GroupsDbUtils.getInstance().getAllGroupsByType(GroupsDbUtils.getType(typeuId, typeMesh))) {
            groupBeens.add(groupSortToBean(group.getGroupSort()));
        }
        placeBean.setGroupsArray(groupBeens);

        List<SceneBean> sceneBeens = new ArrayList<>();
        for (Scene scene : ScenesDbUtils.getInstance().getAllScenesByType(ScenesDbUtils.getType(typeuId, typeMesh))) {
            sceneBeens.add(sceneSortToBean(scene.getSceneSort(), typeuId, typeMesh));
        }
        placeBean.setSceneArray(sceneBeens);

        return placeBean;
    }

    public static BulbBean lightSortToBean(LightSort lightSort) {
        BulbBean bulbBean = new BulbBean();

        bulbBean.setDeviceMesh(lightSort.getMeshAddress());
        bulbBean.setBulbType(lightSort.getLightType());
        bulbBean.setDisplayName(lightSort.getName());
        bulbBean.setFirmwareRevision(lightSort.getFirmwareRevision());
        bulbBean.setFirstSetShowOnHome(lightSort.getIsAlone());
        bulbBean.setShowOnHome(lightSort.getIsShowOnHomeScreen());
        bulbBean.setHadAddToDefault(lightSort.getIsAddToDefault());
        if(!TextUtils.isEmpty(lightSort.getMacAddress())){
            String mac = lightSort.getMacAddress().replaceAll(":", "");
            bulbBean.setMacAddres(mac);
        }
        return bulbBean;
    }

    public static LightSort bulbBeanToSort(BulbBean bulbBean) {
        LightSort lightSort = new LightSort();

        lightSort.setMeshAddress((int) bulbBean.getDeviceMesh());
        lightSort.setMacAddress(bulbBean.getMacAddres());
        lightSort.setLightType((int) bulbBean.getBulbType());
        lightSort.setFirmwareRevision(bulbBean.getFirmwareRevision());
        lightSort.setName(bulbBean.getDisplayName());
        lightSort.setIsAlone(bulbBean.isFirstSetShowOnHome());
        lightSort.setIsShowOnHomeScreen(bulbBean.isShowOnHome());
        lightSort.setIsAddToDefault(bulbBean.isHadAddToDefault());
        if(!TextUtils.isEmpty(bulbBean.getMacAddres())
                && bulbBean.getMacAddres().length() == 12){
            String mac = bulbBean.getMacAddres().substring(0, 2) + ":" + bulbBean.getMacAddres().substring(2, 4) + ":" +
                    bulbBean.getMacAddres().substring(4, 6) + ":" + bulbBean.getMacAddres().substring(6, 8) + ":" +
                    bulbBean.getMacAddres().substring(8, 10) + ":" + bulbBean.getMacAddres().substring(10, 12);

            lightSort.setMacAddress(mac);
        }

        return lightSort;
    }

    public static GroupBean groupSortToBean(GroupSort groupSort) {
        GroupBean groupBean = new GroupBean();

        groupBean.setGroupAddr(groupSort.getMeshAddress());
        groupBean.setDisplayName(groupSort.getName());
        groupBean.setShowOnHomeScreen(groupSort.getIsShowOnHomeScreen());
        List<Integer> integerList = new ArrayList<>();
        for (String mesh : new Group(groupSort).getMembers()) {
            integerList.add(Integer.parseInt(mesh));
        }
        groupBean.setBulbMeshArray(integerList);

        return groupBean;
    }

    public static GroupSort groupBeanToSort(GroupBean groupBean) {
        GroupSort groupSort = new GroupSort();

        groupSort.setMeshAddress((int) groupBean.getGroupAddr());
        groupSort.setName(groupBean.getDisplayName());
        groupSort.setIsShowOnHomeScreen(groupBean.isShowOnHomeScreen());
        groupSort.setBrightness(100);
        groupSort.setTemperature(100);
        groupSort.setColor(Color.WHITE);

        List<String> meshs = new ArrayList<>();
        for (Integer integer : groupBean.getBulbMeshArray()) {
            meshs.add(integer.toString());
        }
        groupSort.setMembers(XlinkUtils.listToString(meshs));


        return groupSort;
    }

    public static SceneBean sceneSortToBean(SceneSort sceneSort, String uId, String placeMesh) {
        SceneBean sceneBean = new SceneBean();

        sceneBean.setName(sceneSort.getName());
        sceneBean.setSceneId(sceneSort.getSceneId());
        sceneBean.setShowOnHomeScreen(sceneSort.getIsShowOnHomeScreen());
        sceneBean.setSceneType(sceneSort.getSceneType());

        List<ActionScene> actionScenes = new ArrayList<>();

        for (SceneActionSort actionSort : SceneActionsDbUtils.getInstance().getSceneActionSortById(
                SceneActionsDbUtils.getType(uId, placeMesh), sceneSort.getSceneId())) {
            ActionScene actionScene = new ActionScene();
            actionScene.setSceneId(actionSort.getSceneId());
            actionScene.setDeviceMesh(actionSort.getDeviceMesh());
            actionScene.setBrightness(actionSort.getBrightness());
            actionScene.setColor(actionSort.getColor());
            actionScene.setTemperature(actionSort.getTemperature());
            actionScenes.add(actionScene);
        }

        List<AlarmScene> alarmScenes = new ArrayList<>();
        for (SceneTimerSort sceneTimerSort : new Scene(sceneSort).getSceneTimerSort(SceneTimersDbUtils.getType(uId, placeMesh))) {
            AlarmScene alarmScene = new AlarmScene();
            alarmScene.setSceneId(sceneTimerSort.getSceneId());
            alarmScene.setSceneTimerId(sceneTimerSort.getSceneTimerId());
            alarmScene.setTimerType(sceneTimerSort.getTimerType());
            alarmScene.setWorkDay(sceneTimerSort.getWorkDay());
            alarmScene.setHour(sceneTimerSort.getHour());
            alarmScene.setMinute(sceneTimerSort.getMinute());
            alarmScene.setEnable(sceneTimerSort.getIsEnable());

            List<AlarmDevice> alarmDevices = new ArrayList<>();
            for (SceneTimerSort timerSort : SceneTimersDbUtils.getInstance().getSceneTimerSortAction(
                    SceneTimersDbUtils.getType(uId, placeMesh), sceneTimerSort.getSceneId(), sceneTimerSort.getTimerId())) {
                AlarmDevice alarmDevice = new AlarmDevice();
                alarmDevice.setAlarmId(timerSort.getTimerId());
                alarmDevice.setDeviceMesh(timerSort.getDeviceMesh());
                alarmDevices.add(alarmDevice);
            }
            alarmScene.setAlarmDevices(alarmDevices);
            alarmScenes.add(alarmScene);
        }
        sceneBean.setActionArray(actionScenes);
        sceneBean.setAlarmScenes(alarmScenes);
        return sceneBean;
    }

    public static SceneSort sceneBeanToSort(SceneBean sceneBean) {
        SceneSort sceneSort = new SceneSort();

        sceneSort.setSceneId((int) sceneBean.getSceneId());
        sceneSort.setIsShowOnHomeScreen(sceneBean.getShowOnHomeScreen());
        sceneSort.setName(sceneBean.getName());
        sceneSort.setSceneType((int) sceneBean.getSceneType());

        return sceneSort;
    }

    public static SceneActionSort sceneActionToSort(ActionScene actionScene) {
        SceneActionSort sceneActionSort = new SceneActionSort();

        sceneActionSort.setSceneId(actionScene.getSceneId());
        sceneActionSort.setDeviceMesh(actionScene.getDeviceMesh());
        sceneActionSort.setColor(actionScene.getColor());
        sceneActionSort.setBrightness(actionScene.getBrightness());
        sceneActionSort.setTemperature(actionScene.getTemperature());
        return sceneActionSort;
    }

    public static List<SceneTimerSort> alarmSceneToSort(AlarmScene alarmScene) {
        List<SceneTimerSort> sceneTimerSorts = new ArrayList<>();
        if(alarmScene.getAlarmDevices() == null){
            return sceneTimerSorts;
        }
        for (AlarmDevice alarmDevice : alarmScene.getAlarmDevices()) {
            SceneTimerSort sceneTimerSort = new SceneTimerSort();

            sceneTimerSort.setDeviceMesh(alarmDevice.getDeviceMesh());
            sceneTimerSort.setSceneTimerId(alarmScene.getSceneTimerId());
            sceneTimerSort.setTimerId(alarmDevice.getAlarmId());

            sceneTimerSort.setSceneId(alarmScene.getSceneId());
            sceneTimerSort.setTimerId(alarmScene.getSceneTimerId());
            sceneTimerSort.setTimerType(alarmScene.getTimerType());
            sceneTimerSort.setWorkDay(alarmScene.getWorkDay());
            sceneTimerSort.setIsEnable(alarmScene.getEnable());
            sceneTimerSort.setHour(alarmScene.getHour());
            sceneTimerSort.setMinute(alarmScene.getMinute());

            sceneTimerSorts.add(sceneTimerSort);
        }
        return sceneTimerSorts;
    }

}
