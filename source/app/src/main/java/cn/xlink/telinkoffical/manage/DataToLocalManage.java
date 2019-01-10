package cn.xlink.telinkoffical.manage;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.Request;
import com.telink.bluetooth.light.Manufacture;

import java.util.List;

import cn.xlink.telinkoffical.bean.ActionScene;
import cn.xlink.telinkoffical.bean.AlarmScene;
import cn.xlink.telinkoffical.bean.BulbBean;
import cn.xlink.telinkoffical.bean.DeviceBean;
import cn.xlink.telinkoffical.bean.DeviceList;
import cn.xlink.telinkoffical.bean.GroupBean;
import cn.xlink.telinkoffical.bean.PlaceBean;
import cn.xlink.telinkoffical.bean.SceneBean;
import cn.xlink.telinkoffical.bean.greenDao.GroupSort;
import cn.xlink.telinkoffical.bean.greenDao.LightSort;
import cn.xlink.telinkoffical.bean.greenDao.PlaceSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneActionSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneTimerSort;
import cn.xlink.telinkoffical.eventbus.PlacesUpataEvent;
import cn.xlink.telinkoffical.eventbus.StringEvent;
import cn.xlink.telinkoffical.http.HttpHelper;
import cn.xlink.telinkoffical.http.HttpManage;
import cn.xlink.telinkoffical.http.constant.HttpConstant;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Places;
import cn.xlink.telinkoffical.utils.ConvertUtil;
import cn.xlink.telinkoffical.utils.EventBusUtils;
import cn.xlink.telinkoffical.utils.GroupsDbUtils;
import cn.xlink.telinkoffical.utils.LightsDbUtils;
import cn.xlink.telinkoffical.utils.PlacesDbUtils;
import cn.xlink.telinkoffical.utils.SceneActionsDbUtils;
import cn.xlink.telinkoffical.utils.SceneTimersDbUtils;
import cn.xlink.telinkoffical.utils.ScenesDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.utils.UserUtil;

/**
 * Created by liucr on 2016/4/9.
 */
public class DataToLocalManage {

    static int deviceCount = 0;

    public static void getUserDeviceList() {
        deviceCount = 0;
        if (!UserUtil.isLogin()) {
            return;
        }
        HttpManage.getInstance().getUserDeviceList(UserUtil.getUser().getUid(), new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                sendGetError();
            }

            @Override
            public void onResponse(int code, String response) {
                Log.i("liucr", "getUserDeviceList: " + response);
                if (code == HttpConstant.PARAM_SUCCESS) {
                    DeviceList deviceList = new Gson().fromJson(response, DeviceList.class);
                    if (deviceList != null && deviceList.getList().size() > 0) {
                        deviceCount = deviceList.getList().size();
                        for (DeviceBean deviceBean : deviceList.getList()) {
                            PlaceSort placeSort = new PlaceSort();
                            placeSort.setFactoryName(Manufacture.getDefault().getFactoryName());
                            placeSort.setFactoryMeshKey(Manufacture.getDefault().getFactoryPassword());
                            placeSort.setMeshAddress(deviceBean.getMac());
                            placeSort.setMeshKey(deviceBean.getAccess_key() + "");
                            placeSort.setPlaceId(deviceBean.getId() + "");
                            placeSort.setRole(deviceBean.getRole());
                            getPlaceProperty(placeSort);
                        }

                        //订阅列表中没有，本地有，删掉（分享的家被取消了）
                        for (PlaceSort placeSort : PlacesDbUtils.getInstance().getAllPlacesByType(
                                PlacesDbUtils.getType(UserUtil.getUser().getUid()))) {
                            boolean hostHad = false;
                            for (DeviceBean deviceBean : deviceList.getList()) {
                                if (deviceBean.getMac().equals(placeSort.getMeshAddress())) {
                                    hostHad = true;
                                    break;
                                }
                            }
                            if (!hostHad) {
                                PlacesDbUtils.getInstance().deletePlace(placeSort);
                            }
                        }

                    } else {
                        //用户第一次登陆，没有设备首先新建一个设备并添加到用户设备列表中
                        List<PlaceSort> placeSorts = PlacesDbUtils.getInstance().getAllPlacesByType(PlacesDbUtils.getType(UserUtil.NoMaster));
                        PlaceSort placeSort;
                        if (placeSorts.size() == 0) {
                            placeSort = PlaceManage.initNewPlace(UserUtil.getUser().getUid());
                        } else {
                            placeSort = PlaceManage.copyPlace(placeSorts.get(0), UserUtil.getUser().getUid());
                        }
                        DataToHostManage.addPlaceToUser(placeSort);
                    }
                } else {
                    sendGetError();
                }
            }
        });
    }

    /**
     * 获取place的扩展属性
     *
     * @param placeSort
     */
    public static void getPlaceProperty(final PlaceSort placeSort) {
        HttpManage.getInstance().getDeviceProperty(placeSort.getPlaceId(), new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                deviceCount--;
            }

            @Override
            public void onResponse(int code, String response) {
                deviceCount--;
                if (UserUtil.getUser() == null || TextUtils.isEmpty(UserUtil.getUser().getAccount())) {
                    return;
                }
                Log.i("liucr", "getPlaceProperty: " + response);
                if (code == HttpConstant.PARAM_SUCCESS) {
                    PlaceBean placeBean = null;
                    try {
                        placeBean = new Gson().fromJson(response, PlaceBean.class);
                    } catch (JsonSyntaxException jsonSyntaxException) {
                        Log.e("jsonSyntaxException: ", jsonSyntaxException.getMessage());
                    }

                    if (placeBean != null) {

                        placeSort.setCreateDate(placeBean.getCreateDate());
                        placeSort.setLastUseDate(placeBean.getLastUseDate());
                        placeSort.setCreatorAccount(placeBean.getAdminBean().getEmailAddress());
                        placeSort.setCreatorId(placeBean.getAdminBean().getUserID() + "");
                        placeSort.setCreatorName(placeBean.getAdminBean().getUsername());
                        placeSort.setName(placeBean.getPlaceName());
                        placeSort.setPlaceVersion(placeBean.getPlaceVersion());
                        placeSort.setDeviceIdRecord(placeBean.getDeviceIdRecord());

                        if (isLocalNew(placeSort)) {

                            PlaceSort localPlace = PlacesDbUtils.getInstance().getPlaceByMac(
                                    PlacesDbUtils.getType(placeSort.getCreatorId()), placeSort.getMeshAddress());

                            DataToHostManage.updataToHost(localPlace);
                            EventBusUtils.getInstance().dispatchEvent(PlacesUpataEvent.newInstance("", PlacesUpataEvent.PlacesUpataEvent, placeSort));
                            return;
                        }
                        dataToDb(placeBean, placeSort);
                    }

                } else {

                    HttpManage.ErrorEntity errorEntity = new Gson().fromJson(response, HttpManage.ErrorEntity.class);
                    //设备扩展属性设置失败
                    if (errorEntity.getCode() == HttpConstant.DEVICE_PROPERTY_NOT_EXISTS) {
                        unSubscribeDevice(placeSort.getPlaceId());

                        //设备扩展属性设置失败，没有设备首先新建一个设备并添加到用户设备列表中
                        List<PlaceSort> placeSorts = PlacesDbUtils.getInstance().getAllPlacesByType(PlacesDbUtils.getType(UserUtil.NoMaster));
                        PlaceSort placeSort;
                        if (placeSorts.size() == 0) {
                            placeSort = PlaceManage.initNewPlace(UserUtil.getUser().getUid());
                        } else {
                            placeSort = PlaceManage.copyPlace(placeSorts.get(0), UserUtil.getUser().getUid());
                        }
                        DataToHostManage.addPlaceToUser(placeSort);
                    }
                }
            }
        });
    }

    private static void unSubscribeDevice(String placeid) {
        if (!UserUtil.isLogin()) {

            return;
        }
        HttpManage.getInstance().unSubscribeDevice(UserUtil.getUser().getUid() + "", placeid, new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(int code, String response) {
            }
        });
    }

    /**
     * 对比服务器与本地数据的新旧
     *
     * @param placeSort
     * @return
     */
    private static boolean isLocalNew(PlaceSort placeSort) {

        PlaceSort localPlace = PlacesDbUtils.getInstance().getPlaceByMac(
                PlacesDbUtils.getType(placeSort.getCreatorId()), placeSort.getMeshAddress());

        if (localPlace == null) {
            return false;
        }

        if (placeSort.getPlaceVersion() < localPlace.getPlaceVersion()) {
            return true;
        }

        return false;
    }

    /**
     * 将数据写入数据库
     *
     * @param placeBean
     * @param placeSort
     */
    private static void dataToDb(PlaceBean placeBean, PlaceSort placeSort) {

        String typeuId = UserUtil.getUser().getUid();
        String typePlace = placeSort.getMeshAddress();

        PlaceManage.clearPlaceDb(typeuId, placeSort.getMeshAddress());

        if (placeBean.getBulbsArray() != null) {
            String typeLight = LightsDbUtils.getType(typeuId, typePlace);
            for (BulbBean bulbBean : placeBean.getBulbsArray()) {
                LightSort lightSort = ConvertUtil.bulbBeanToSort(bulbBean);
                LightsDbUtils.getInstance().updataOrInsert(typeLight, lightSort);
            }
        }

        if (placeBean.getGroupsArray() != null) {
            String typeGroup = GroupsDbUtils.getType(typeuId, typePlace);
            for (GroupBean groupBean : placeBean.getGroupsArray()) {
                if (groupBean.getGroupAddr() == 0x8001) {
                    groupBean.setGroupAddr(0xFFFF);
                }
                GroupSort groupSort = ConvertUtil.groupBeanToSort(groupBean);
                GroupsDbUtils.getInstance().updataOrInsert(typeGroup, groupSort);
            }
        }

        if (placeBean.getSceneArray() != null) {
            String typeScene = ScenesDbUtils.getType(typeuId, typePlace);
            for (SceneBean sceneBean : placeBean.getSceneArray()) {

                SceneSort sceneSort = ConvertUtil.sceneBeanToSort(sceneBean);
                ScenesDbUtils.getInstance().updataOrInsert(typeScene, sceneSort);

                String typeAction = SceneActionsDbUtils.getType(typeuId, typePlace);
                SceneActionsDbUtils.getInstance().deleteBySceneId(typeAction, (int) sceneBean.getSceneId());
                for (ActionScene actionScene : sceneBean.getActionArray()) {
                    SceneActionSort sceneActionSort = ConvertUtil.sceneActionToSort(actionScene);
                    SceneActionsDbUtils.getInstance().updataOrInsert(typeAction, sceneActionSort);
                }

                String typeTimer = SceneTimersDbUtils.getType(typeuId, typePlace);
                SceneTimersDbUtils.getInstance().deleteBySceneId(typeTimer, (int) sceneBean.getSceneId());
                for (AlarmScene alarmScene : sceneBean.getAlarmScenes()) {
                    List<SceneTimerSort> sceneTimerSorts = ConvertUtil.alarmSceneToSort(alarmScene);
                    for (SceneTimerSort sceneTimerSort : sceneTimerSorts) {
                        SceneTimersDbUtils.getInstance().updataOrInsert(typeTimer, sceneTimerSort);
                    }
                }

            }
        }

        /**
         * 清除没用户的place
         */
        for (PlaceSort noPlaceSort : PlacesDbUtils.getInstance().getAllPlacesByType(PlacesDbUtils.getType(UserUtil.NoMaster))) {
            PlacesDbUtils.getInstance().deletePlace(noPlaceSort);
        }

        PlacesDbUtils.getInstance().updataOrInsert(placeSort);

        PlaceManage.changeToCurUser();

        EventBusUtils.getInstance().dispatchEvent(PlacesUpataEvent.newInstance("", PlacesUpataEvent.PlacesUpataEvent, placeSort));

    }

    public static void sendGetError() {
        EventBusUtils.getInstance().dispatchEvent(StringEvent
                .newInstance("", StringEvent.StringEevent, TelinkCommon.STRING_GETPLACE_ERROR));
    }

}
