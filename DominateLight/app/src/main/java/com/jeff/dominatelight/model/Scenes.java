package com.jeff.dominatelight.model;

import android.os.Handler;
import com.jeff.dominatelight.bean.greenDao.SceneActionSort;
import com.jeff.dominatelight.bean.greenDao.SceneTimerSort;
import com.jeff.dominatelight.manage.CmdManage;
import com.jeff.dominatelight.manage.DataToHostManage;
import com.jeff.dominatelight.utils.SceneActionsDbUtils;
import com.jeff.dominatelight.utils.SceneTimersDbUtils;
import com.jeff.dominatelight.utils.TelinkTimerUtils;
import com.telink.bluetooth.light.ConnectionStatus;


public class Scenes extends DataStorageImpl<Scene> {

    private static Scenes mThis;

    private Scenes() {
        super();
    }

    public static Scenes getInstance() {

        if (mThis == null)
            mThis = new Scenes();

        return mThis;
    }

    public Scene getById(int sceneId) {
        for (Scene scene : get()) {
            if (scene.getSceneSort().getSceneId() == sceneId) {
                return scene;
            }
        }
        return null;
    }

    public void setByMeshAddress(int meshAddress, Scene scene) {
        boolean isHad = false;
        for (int i = 0; i < get().size(); i++) {
            if (meshAddress == get().get(i).getSceneSort().getSceneId()) {
                get().set(i, scene);
                isHad = true;
            }
        }
        if (!isHad) {
            add(scene);
        }
    }

    /**
     * 将一个灯从情景中移除
     *
     * @param light
     */
    public void removeLight(Light light) {
        for (SceneActionSort actionSort : SceneActionsDbUtils.getInstance().getSceneActionSort(
                light.getLightSort().getMeshAddress())) {
            SceneActionsDbUtils.getInstance().deleteSceneActionSort(actionSort);
        }

        for (SceneTimerSort timerSort : SceneTimersDbUtils.getInstance().getDeviceTimerSort(
                light.getLightSort().getMeshAddress())) {
            SceneTimersDbUtils.getInstance().deleteSceneTimerSort(timerSort);
        }
    }

    public void addLightToDefaultScene(final Light light) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Light newLight = new Light(light.getLightSort());
                newLight.temperature = light.temperature;
                newLight.color = light.color;
                newLight.brightness = 0;
                newLight.status = ConnectionStatus.OFF;
                addLightToScene(getById(1), newLight);
            }
        }, 200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Light newLight = new Light(light.getLightSort());
                newLight.temperature = light.temperature;
                newLight.color = light.color;
                newLight.brightness = 100;
                newLight.status = ConnectionStatus.ON;
                addLightToScene(getById(2), newLight);
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Light newLight = new Light(light.getLightSort());
                newLight.temperature = light.temperature;
                newLight.color = light.color;
                newLight.brightness = 50;
                newLight.status = ConnectionStatus.ON;
                addLightToScene(getById(3), newLight);
            }
        }, 800);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Light newLight = new Light(light.getLightSort());
                newLight.temperature = light.temperature;
                newLight.color = light.color;
                newLight.brightness = 5;
                newLight.status = ConnectionStatus.ON;
                addLightToScene(getById(4), newLight);
            }
        }, 1000);
    }

    /**
     * 将一个灯添加到某个情景
     *
     * @param scene
     * @param light
     */
    private void addLightToScene(Scene scene, Light light) {
        SceneActionSort sceneActionSort = SceneActionsDbUtils.getInstance().getSceneActionSort(scene.getSceneSort().getSceneId(),
                light.getLightSort().getMeshAddress());
        if (sceneActionSort != null) {
            return;
        }

        sceneActionSort = new SceneActionSort();
        saveAction(scene, light, sceneActionSort);
    }

    /**
     * 新建并保存一个关于该灯的动作
     *
     * @param scene
     * @param light
     * @param sceneActionSort
     */
    private void saveAction(Scene scene, Light light, SceneActionSort sceneActionSort) {
        sceneActionSort.setSceneId(scene.getSceneSort().getSceneId());
        sceneActionSort.setDeviceMesh(light.getLightSort().getMeshAddress());
        sceneActionSort.setBrightness(light.brightness);
        sceneActionSort.setColor(light.color);
        sceneActionSort.setTemperature(light.temperature);

        //发送添加/修改情景命令
        CmdManage.addDeviceScenceDelay(sceneActionSort);
        SceneActionsDbUtils.getInstance().updataOrInsert(sceneActionSort);

        addAlarm(scene, light);
    }

    /**
     * 为该灯添加该情景所有定时器
     *
     * @param scene
     * @param light
     */
    private void addAlarm(Scene scene, Light light) {
        for (SceneTimerSort timerSort : scene.getSceneTimerSort()) {
            SceneTimerSort curSceneTimerSort = new SceneTimerSort();
            curSceneTimerSort.setSceneId(scene.getSceneSort().getSceneId());
            curSceneTimerSort.setDeviceMesh(light.getLightSort().getMeshAddress());
            curSceneTimerSort.setSceneTimerId(timerSort.getSceneTimerId());
            curSceneTimerSort.setTimerId(TelinkTimerUtils.getNoUseDeviceTimerId(light.getLightSort().getMeshAddress()));
            curSceneTimerSort.setIsEnable(timerSort.getIsEnable());

            curSceneTimerSort.setTimerType(timerSort.getTimerType());
            curSceneTimerSort.setWorkDay(timerSort.getWorkDay());
            curSceneTimerSort.setHour(timerSort.getHour());
            curSceneTimerSort.setMinute(timerSort.getMinute());

            CmdManage.addEditAlarm(curSceneTimerSort, scene.getSceneSort().getSceneId(), true);
            SceneTimersDbUtils.getInstance().updataOrInsert(curSceneTimerSort);
        }
        DataToHostManage.updataCurToHost();
    }

    public static boolean checkNameHad(String name, String oldName) {
        for (Scene scene : getInstance().get()) {
            if (scene.getSceneSort().getName().equals(name)
                    && !scene.getSceneSort().getName().equals(oldName)) {
                return true;
            }
        }
        return false;
    }
}
