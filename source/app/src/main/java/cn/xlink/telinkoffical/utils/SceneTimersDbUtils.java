package cn.xlink.telinkoffical.utils;

import java.util.List;

import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.bean.greenDao.SceneActionSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneTimerSort;
import cn.xlink.telinkoffical.database.SceneActionSortDao;
import cn.xlink.telinkoffical.database.SceneTimerSortDao;

/**
 * Created by liucr on 2016/3/24.
 */
public class SceneTimersDbUtils {

    private static SceneTimersDbUtils sceneTimersDbUtils;

    private static SceneTimerSortDao sceneTimerSortDao;

    public SceneTimersDbUtils() {
        sceneTimerSortDao = MyApp.getApp().getDaoSession().getSceneTimerSortDao();
    }

    public static void init() {
        sceneTimersDbUtils = new SceneTimersDbUtils();
    }

    public static SceneTimersDbUtils getInstance() {
        return sceneTimersDbUtils;
    }

    /**
     * 当前设备插入或更新情景
     *
     * @param sceneTimerSort
     */
    public void updataOrInsert(SceneTimerSort sceneTimerSort) {
        updataOrInsert(getCurType(), sceneTimerSort);
    }

    /**
     * 插入或更新情景
     *
     * @param type
     * @param sceneTimerSort
     */
    public void updataOrInsert(String type, SceneTimerSort sceneTimerSort) {
        sceneTimerSort.setType(type);
        if (sceneTimerSort.getId() == null) {
            LogUtil.e("insert SceneTimers : " + sceneTimerSortDao.insert(sceneTimerSort));
        } else {
            sceneTimerSortDao.update(sceneTimerSort);
            LogUtil.e("update SceneTimers : " + sceneTimerSort.getSceneId());
        }
    }

    /**
     * 删除情景所有定时器
     *
     * @param sceneId
     */
    public void deleteBySceneId(int sceneId) {
        for (SceneTimerSort sceneTimerSort : getSceneTimerSortById(sceneId)) {
            sceneTimerSortDao.delete(sceneTimerSort);
        }
    }

    /**
     * 删除情景所有定时器
     *
     * @param sceneId
     */
    public void deleteBySceneId(String type, int sceneId) {
        for (SceneTimerSort sceneTimerSort : getSceneTimerSortById(type, sceneId)) {
            sceneTimerSortDao.delete(sceneTimerSort);
        }
    }

    public void deleteTimer(int sceneId, int sceneTimerId) {
        for (SceneTimerSort sceneTimerSort : getSceneTimerSort(sceneId, sceneTimerId)) {
            sceneTimerSortDao.delete(sceneTimerSort);
        }
    }

    /**
     * 删除本地记录
     *
     * @param sceneTimerSort
     */
    public void deleteSceneTimerSort(SceneTimerSort sceneTimerSort) {
        if (sceneTimerSort == null) {
            return;
        }
        sceneTimerSortDao.delete(sceneTimerSort);
    }

    /**
     * 获取某个情景某个定时组的所有设备动作
     *
     * @param sceneId
     * @param sceneTimerId
     * @return
     */
    public List<SceneTimerSort> getSceneTimerSort(int sceneId, int sceneTimerId) {
        List<SceneTimerSort> sceneTimerSorts = sceneTimerSortDao.queryBuilder().where(
                SceneTimerSortDao.Properties.Type.eq(getCurType()),
                SceneTimerSortDao.Properties.SceneTimerId.eq(sceneTimerId),
                SceneTimerSortDao.Properties.SceneId.eq(sceneId)).list();
        return sceneTimerSorts;
    }

    /**
     * 获取一个设备的所有定时器
     *
     * @param deviceMesh
     * @return
     */
    public List<SceneTimerSort> getDeviceTimerSort(int deviceMesh) {
        List<SceneTimerSort> sceneTimerSorts = sceneTimerSortDao.queryBuilder().where(
                SceneTimerSortDao.Properties.Type.eq(getCurType()),
                SceneTimerSortDao.Properties.DeviceMesh.eq(deviceMesh)).list();
        return sceneTimerSorts;
    }

    /**
     * 获取一个设备在情景里的所有定时器
     *
     * @param sceneId
     * @param deviceMesh
     * @return
     */
    public List<SceneTimerSort> getSceneDeviceTimerSort(int sceneId, int deviceMesh) {
        List<SceneTimerSort> sceneTimerSorts = sceneTimerSortDao.queryBuilder().where(
                SceneTimerSortDao.Properties.Type.eq(getCurType()),
                SceneTimerSortDao.Properties.SceneId.eq(sceneId),
                SceneTimerSortDao.Properties.DeviceMesh.eq(deviceMesh)).list();
        return sceneTimerSorts;
    }

    /**
     * 获取一个设备在情景里的所有定时器
     *
     * @param sceneId
     * @param deviceMesh
     * @return
     */
    public List<SceneTimerSort> getSceneDeviceTimerSort(String type, int sceneId, int deviceMesh) {
        List<SceneTimerSort> sceneTimerSorts = sceneTimerSortDao.queryBuilder().where(
                SceneTimerSortDao.Properties.Type.eq(type),
                SceneTimerSortDao.Properties.SceneId.eq(sceneId),
                SceneTimerSortDao.Properties.DeviceMesh.eq(deviceMesh)).list();
        return sceneTimerSorts;
    }

    /**
     * 获取某个情景某个定时组的所有定时动作
     *
     * @param sceneId
     * @param sceneTimerId
     * @return
     */
    public List<SceneTimerSort> getSceneTimerSortAction(String type, int sceneId, int sceneTimerId) {
        List<SceneTimerSort> sceneTimerSorts = sceneTimerSortDao.queryBuilder().where(
                SceneTimerSortDao.Properties.Type.eq(type),
                SceneTimerSortDao.Properties.SceneTimerId.eq(sceneTimerId),
                SceneTimerSortDao.Properties.SceneId.eq(sceneId)).list();

        return sceneTimerSorts;
    }

//    /**
//     * 获取设备的所有定时器
//     * @param deviceMesh
//     * @return
//     */
//    public List<SceneTimerSort> getSceneTimerSort(int deviceMesh) {
//        List<SceneTimerSort> sceneTimerSorts = sceneTimerSortDao.queryBuilder().where(
//                SceneTimerSortDao.Properties.Type.eq(getCurType()),
//                SceneTimerSortDao.Properties.DeviceMesh.eq(deviceMesh)).list();
//        return sceneTimerSorts;
//    }

    /**
     * 获取情景所有定时器
     *
     * @param type
     * @return
     */
    public List<SceneTimerSort> getSceneTimerSortById(String type, int sceneId) {
        List<SceneTimerSort> sceneTimerSorts = sceneTimerSortDao.queryBuilder().where(
                SceneTimerSortDao.Properties.Type.eq(type),
                SceneTimerSortDao.Properties.SceneId.eq(sceneId)).list();
        return sceneTimerSorts;
    }

    /**
     * 获取当前账号的所有情景
     *
     * @return
     */
    public List<SceneTimerSort> getSceneTimerSortById(int sceneId) {
        //从数据库读出所有组
        return getSceneTimerSortById(getCurType(), sceneId);
    }

    /**
     * 获取当前前缀
     *
     * @return
     */
    private String getCurType() {
        return getType(TelinkCommon.getCurDbUidType(), TelinkCommon.getCurPlaceType());
    }

    public static String getType(String uId, String placeMesh) {
        return uId + placeMesh + TelinkCommon.SCENETIMERLIST;
    }
}
