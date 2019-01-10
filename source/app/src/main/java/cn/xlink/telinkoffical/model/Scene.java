package cn.xlink.telinkoffical.model;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.bean.greenDao.SceneActionSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneTimerSort;
import cn.xlink.telinkoffical.utils.SceneActionsDbUtils;
import cn.xlink.telinkoffical.utils.SceneTimersDbUtils;

/**
 * Created by liucr on 2016/3/29.
 */
public class Scene {

    private SceneSort sceneSort;

    public Scene(SceneSort sceneSort) {
        this.sceneSort = sceneSort;
    }

    public SceneSort getSceneSort() {
        return sceneSort;
    }

    public List<SceneActionSort> getSceneActionSort() {
        //从数据库读出该情景的所有动作
        return SceneActionsDbUtils.getInstance().getSceneActionSortById(sceneSort.getSceneId());
    }

    /**
     * 获取该情景下的定时器列表
     *
     * @return
     */
    public List<SceneTimerSort> getSceneTimerSort() {
        List<SceneTimerSort> sceneTimerSortList = new ArrayList<>();
        List<SceneTimerSort> timerSorts = SceneTimersDbUtils.getInstance().getSceneTimerSortById(sceneSort.getSceneId());
        int timerSortId = -1;
        for (SceneTimerSort sceneTimerSort : timerSorts) {
            if (sceneTimerSort.getSceneTimerId() != timerSortId) {
                sceneTimerSortList.add(sceneTimerSort);
                timerSortId = sceneTimerSort.getSceneTimerId();
            }
        }
        return sceneTimerSortList;
    }

    /**
     * 获取该情景下的定时器列表
     *
     * @return
     */
    public List<SceneTimerSort> getSceneTimerSort(String type) {
        List<SceneTimerSort> sceneTimerSortList = new ArrayList<>();
        List<SceneTimerSort> timerSorts = SceneTimersDbUtils.getInstance().getSceneTimerSortById(type, sceneSort.getSceneId());
        int timerSortId = -1;
        for (SceneTimerSort sceneTimerSort : timerSorts) {
            if (sceneTimerSort.getSceneTimerId() != timerSortId) {
                sceneTimerSortList.add(sceneTimerSort);
                timerSortId = sceneTimerSort.getSceneTimerId();
            }
        }
        return sceneTimerSortList;
    }

    public int getHomeIcon() {
        switch (sceneSort.getSceneType()) {
            case SceneType.SCENETYPE_LEAVE:
                return R.mipmap.icon_scene_leave;
            case SceneType.SCENETYPE_GOHOME:
                return R.mipmap.icon_scene_gohome;
            case SceneType.SCENETYPE_REST:
                return R.mipmap.icon_scene_rest;
            case SceneType.SCENETYPE_TV:
                return R.mipmap.icon_scene_tv;
            case SceneType.SCENETYPE_ADD:
                return R.mipmap.icon_scene_add;
            default:
                return R.mipmap.icon_scene_custom;
        }
    }

    public int getBigIcon() {
        switch (sceneSort.getSceneType()) {
            case SceneType.SCENETYPE_LEAVE:
                return R.mipmap.icon_scene_leave_big;
            case SceneType.SCENETYPE_GOHOME:
                return R.mipmap.icon_scene_gohome_big;
            case SceneType.SCENETYPE_REST:
                return R.mipmap.icon_scene_rest_big;
            case SceneType.SCENETYPE_TV:
                return R.mipmap.icon_scene_tv_big;
            default:
                return R.mipmap.icon_scene_custom_big;
        }
    }

    public int getManageIcon() {
        switch (sceneSort.getSceneType()) {
            case SceneType.SCENETYPE_LEAVE:
                return R.mipmap.icon_scene_leave_manage;
            case SceneType.SCENETYPE_GOHOME:
                return R.mipmap.icon_scene_gohome_manage;
            case SceneType.SCENETYPE_REST:
                return R.mipmap.icon_scene_rest_manage;
            case SceneType.SCENETYPE_TV:
                return R.mipmap.icon_scene_tv_manage;
            default:
                return R.mipmap.icon_scene_custom_manage;
        }
    }

    public final static class SceneType {
        public static final int SCENETYPE_LEAVE = 1;
        public static final int SCENETYPE_GOHOME = 2;
        public static final int SCENETYPE_REST = 3;
        public static final int SCENETYPE_TV = 4;
        public static final int SCENETYPE_CUSTOM = 5;
        public static final int SCENETYPE_ADD = -1;
    }
}
