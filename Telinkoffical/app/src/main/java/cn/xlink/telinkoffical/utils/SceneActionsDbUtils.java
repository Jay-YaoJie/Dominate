package cn.xlink.telinkoffical.utils;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.bean.greenDao.SceneActionSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneSort;
import cn.xlink.telinkoffical.database.SceneActionSortDao;
import cn.xlink.telinkoffical.database.SceneSortDao;
import cn.xlink.telinkoffical.model.Scene;

/**
 * Created by liucr on 2016/3/24.
 */
public class SceneActionsDbUtils {

    private static SceneActionsDbUtils sceneActionsDbUtils;

    private static SceneActionSortDao sceneActionSortDao;

    public SceneActionsDbUtils() {
        sceneActionSortDao = MyApp.getApp().getDaoSession().getSceneActionSortDao();
    }

    public static void init() {
        sceneActionsDbUtils = new SceneActionsDbUtils();
    }

    public static SceneActionsDbUtils getInstance() {
        return sceneActionsDbUtils;
    }

    /**
     * 当前设备插入或更新情景
     *
     * @param sceneActionSort
     */
    public void updataOrInsert(SceneActionSort sceneActionSort) {
        updataOrInsert(getCurType(), sceneActionSort);
    }

    /**
     * 插入或更新情景
     *
     * @param type
     * @param sceneActionSort
     */
    public void updataOrInsert(String type, SceneActionSort sceneActionSort) {
        sceneActionSort.setType(type);
        if (sceneActionSort.getId() == null) {
            LogUtil.e("insert SceneActions : " + sceneActionSortDao.insert(sceneActionSort));
        } else {
            sceneActionSortDao.update(sceneActionSort);
            LogUtil.e("update SceneActions : " + sceneActionSort.getSceneId());
        }
    }

    public void deleteBySceneId(int sceneId){
        for(SceneActionSort sceneActionSort :getSceneActionSortById(sceneId)){
            sceneActionSortDao.delete(sceneActionSort);
        }
    }

    public void deleteBySceneId(String type,int sceneId){
        for(SceneActionSort sceneActionSort :getSceneActionSortById(type,sceneId)){
            sceneActionSortDao.delete(sceneActionSort);
        }
    }
    /**
     * 删除本地记录
     *
     * @param sceneActionSort
     */
    public void deleteSceneActionSort(SceneActionSort sceneActionSort) {
        if(sceneActionSort == null){
            return;
        }
        sceneActionSortDao.delete(sceneActionSort);
    }

    public SceneActionSort getSceneActionSort(int sceneId, int deviceMesh) {
        List<SceneActionSort> sceneActionSorts = sceneActionSortDao.queryBuilder().where(
                SceneActionSortDao.Properties.Type.eq(getCurType()),
                SceneActionSortDao.Properties.DeviceMesh.eq(deviceMesh),
                SceneActionSortDao.Properties.SceneId.eq(sceneId)).list();
        if (sceneActionSorts.size() == 0) {
            return null;
        } else {
            return sceneActionSorts.get(0);
        }
    }

    public List<SceneActionSort> getSceneActionSort(int deviceMesh) {
        List<SceneActionSort> sceneActionSorts = sceneActionSortDao.queryBuilder().where(
                SceneActionSortDao.Properties.Type.eq(getCurType()),
                SceneActionSortDao.Properties.DeviceMesh.eq(deviceMesh)).list();
        return sceneActionSorts;
    }

    /**
     * 通过前缀及id查询情景
     *
     * @param type
     * @return
     */
    public List<SceneActionSort> getSceneActionSortById(String type, int sceneId) {
        List<SceneActionSort> sceneActionSorts = sceneActionSortDao.queryBuilder().where(
                SceneActionSortDao.Properties.Type.eq(type), SceneActionSortDao.Properties.SceneId.eq(sceneId)).list();
        return sceneActionSorts;
    }

    /**
     * 获取当前账号的所有情景
     *
     * @return
     */
    public List<SceneActionSort> getSceneActionSortById(int sceneId) {
        //从数据库读出所有组
        return getSceneActionSortById(getCurType(), sceneId);
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
        return uId + placeMesh + TelinkCommon.SCENEACTIONLIST;
    }
}
