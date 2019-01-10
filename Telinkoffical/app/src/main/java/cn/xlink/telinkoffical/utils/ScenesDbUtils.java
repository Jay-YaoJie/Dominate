package cn.xlink.telinkoffical.utils;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.bean.greenDao.GroupSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneSort;
import cn.xlink.telinkoffical.database.GroupSortDao;
import cn.xlink.telinkoffical.database.SceneSortDao;
import cn.xlink.telinkoffical.model.Group;
import cn.xlink.telinkoffical.model.Scene;

/**
 * Created by liucr on 2016/3/24.
 */
public class ScenesDbUtils {

    private static ScenesDbUtils scenesDbUtils;

    private static SceneSortDao sceneSortDao;

    public ScenesDbUtils() {
        sceneSortDao = MyApp.getApp().getDaoSession().getSceneSortDao();
    }

    public static void init() {
        scenesDbUtils = new ScenesDbUtils();
    }

    public static ScenesDbUtils getInstance() {
        return scenesDbUtils;
    }

    /**
     * 当前设备插入或更新情景
     *
     * @param sceneSort
     */
    public void updataOrInsert(SceneSort sceneSort) {
        updataOrInsert(getCurType(), sceneSort);
    }

    /**
     * 插入或更新情景
     *
     * @param type
     * @param sceneSort
     */
    public void updataOrInsert(String type, SceneSort sceneSort) {
        sceneSort.setType(type);
        SceneSort localsort = getSceneByMesh(type, sceneSort.getSceneId());
        if (localsort == null) {
            LogUtil.e("insert scene : " + sceneSortDao.insert(sceneSort));
        } else {
            sceneSort.setId(localsort.getId());
            sceneSortDao.update(sceneSort);
            LogUtil.e("update sceneSortDao : " + sceneSort.getName());
        }
    }

    /**
     * 删除本地记录
     *
     * @param sceneSort
     */
    public void deleteSceneSort(SceneSort sceneSort) {
        sceneSortDao.delete(sceneSort);
    }

    /**
     * 通过前缀及mac查询情景
     *
     * @param type
     * @param mesh
     * @return
     */
    public SceneSort getSceneByMesh(String type, int mesh) {
        List<SceneSort> sceneSorts = sceneSortDao.queryBuilder().where(
                SceneSortDao.Properties.Type.eq(type), SceneSortDao.Properties.SceneId.eq(mesh)).list();
        if (sceneSorts.size() == 0) {
            return null;
        } else {
            return sceneSorts.get(0);
        }
    }

    /**
     * 通过前缀及id查询情景
     *
     * @param id
     * @param type
     * @param id
     * @return
     */
    public SceneSort getSceneById(String type, Long id) {
        List<SceneSort> sceneSorts = sceneSortDao.queryBuilder().where(
                SceneSortDao.Properties.Type.eq(type), SceneSortDao.Properties.Id.eq(id)).list();
        if (sceneSorts.size() == 0) {
            return null;
        } else {
            return sceneSorts.get(0);
        }
    }

    /**
     * 获取当前账号的所有情景
     *
     * @return
     */
    public List<Scene> getCurAccountScenes() {
        //从数据库读出所有组
        return getAllScenesByType(getCurType());
    }

    /**
     * 根据前缀获取所有情景
     *
     * @param type
     * @return
     */
    public List<Scene> getAllScenesByType(String type) {
        //从数据库读出所有组
        List<SceneSort> sceneSorts = sceneSortDao.queryBuilder().where(SceneSortDao.Properties.Type.eq
                (type)).list();
        List<Scene> scenes = new ArrayList<>();
        for (SceneSort sceneSort : sceneSorts) {
            Scene scene = new Scene(sceneSort);
            scenes.add(scene);
        }
        return scenes;
    }

    /**
     * 获取当前前缀
     *
     * @return
     */
    private String getCurType() {
        String type = getType(TelinkCommon.getCurDbUidType(),  TelinkCommon.getCurPlaceType());
        return type;
    }

    public static String getType(String uId, String place) {
        return uId + place + TelinkCommon.SCENELIST;
    }
}
