package com.jeff.dominatelight.utils;

import android.content.Context;
import android.util.Log;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.bean.greenDao.SceneActionSort;
import com.jeff.dominatelight.bean.greenDao.SceneTimerSort;
import com.jeff.dominatelight.model.Scene;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liucr on 2016/3/30.
 */
public class TelinkTimerUtils {

    public static List<SceneTimerSort> newTimerSortList(Scene scene) {
        List<SceneTimerSort> sceneTimerSortList = new ArrayList<>();
        for (SceneActionSort sceneActionSort : scene.getSceneActionSort()) {
            SceneTimerSort sceneTimerSort = new SceneTimerSort();
            sceneTimerSort.setSceneId(scene.getSceneSort().getSceneId());
            sceneTimerSort.setDeviceMesh(sceneActionSort.getDeviceMesh());
            sceneTimerSort.setSceneTimerId(getNoUseSceneTimerId(scene));
            sceneTimerSort.setTimerId(getNoUseDeviceTimerId(sceneActionSort.getDeviceMesh()));
            sceneTimerSortList.add(sceneTimerSort);
        }
        return sceneTimerSortList;
    }

    /**
     * 获取设备未被使用的定时器Id
     *
     * @param deviceMesh
     * @return
     */
    public static int getNoUseDeviceTimerId(int deviceMesh) {
        List<SceneTimerSort> sceneTimerSortList = SceneTimersDbUtils.getInstance().getDeviceTimerSort(deviceMesh);
        for (int i = 1; i <= 16; i++) {
            boolean ishad = false;
            for (SceneTimerSort sceneTimerSort : sceneTimerSortList) {
                if (sceneTimerSort.getTimerId() == i) {
                    ishad = true;
                    break;
                }
            }
            if (!ishad) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取情景下未被使用的定时器Id
     *
     * @param scene
     * @return
     */
    private static int getNoUseSceneTimerId(Scene scene) {
        List<SceneTimerSort> sceneTimerSortList = scene.getSceneTimerSort();
        for (int i = 0; ; i++) {
            boolean isHad = false;
            for (SceneTimerSort timerSort : sceneTimerSortList) {
                if (timerSort.getSceneTimerId() == i) {
                    isHad = true;
                    break;
                }
            }
            if (!isHad) {
                return i;
            }
        }
    }

    /**
     * 获取单次定时器的月、日
     *
     * @param workday
     * @return
     */
    public static int[] getOnceTimerData(int workday) {
        int[] monthDay = {0, 0};
        int data = workday;
        monthDay[0] = data / 100;
        monthDay[1] = data - monthDay[0] * 100;
        return monthDay;
    }

    /**
     * 星期队列转换成整形数
     *
     * @param integers
     * @return
     */
    public static int getRepeatTimer(List<Integer> integers) {
        int data = 0;
        data = integers.get(6);
        integers.remove(6);
        for (int i = 0; i < integers.size(); i++) {
            data = data + (int) (Math.pow(2, i + 1) * integers.get(i));
        }
        List<Integer> aa = getRepeatTimer(data);
        return data;
    }

    /**
     * 整形数转化为
     *
     * @param data
     * @return
     */
    public static List<Integer> getRepeatTimer(int data) {
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            integers.add(0);
        }
        String sss = Integer.toBinaryString(data);
        int length = sss.length();
        for (int i = length; i < 7; i++) {
            sss = "0" + sss;
        }

        integers.set(0, Integer.parseInt(sss.substring(5, 6)));
        integers.set(1, Integer.parseInt(sss.substring(4, 5)));
        integers.set(2, Integer.parseInt(sss.substring(3, 4)));
        integers.set(3, Integer.parseInt(sss.substring(2, 3)));
        integers.set(4, Integer.parseInt(sss.substring(1, 2)));
        integers.set(5, Integer.parseInt(sss.substring(0, 1)));
        integers.set(6, Integer.parseInt(sss.substring(6, 7)));

        return integers;
    }

    /**
     * 获取时间Text
     *
     * @param timerSort
     * @return
     */
    public static String getHourMin(SceneTimerSort timerSort) {
        String h = timerSort.getHour().toString();
        String min = timerSort.getMinute().toString();
        if (h.length() == 1) {
            h = "0" + h;
        }
        if (min.length() == 1) {
            min = "0" + min;
        }
        return h + " : " + min;
    }

    public static String getWorkDayText(SceneTimerSort timerSort, Context context) {

        if (timerSort.getTimerType() == 0) {
            return context.getString(R.string.timer_once);
        }

        int data = timerSort.getWorkDay();

        List<Integer> integerList = getRepeatTimer(data);
        boolean isAllDay = true;
        boolean isWorkDay = true;
        boolean isWeekend = true;
        for (int i = 0; i < integerList.size(); i++) {

            //每天
            if (integerList.get(i) == 0) {
                isAllDay = false;
            }

            //工作日
            if (integerList.get(5) == 1 || integerList.get(6) == 1) {
                isWorkDay = false;
            } else if (i < 5 && integerList.get(i) == 0) {
                isWorkDay = false;
            }

            //周末
            if (integerList.get(5) == 0 || integerList.get(6) == 0) {
                isWeekend = false;
            } else if (i < 5 && integerList.get(i) == 1) {
                isWeekend = false;
            }

        }

        if (isAllDay) {
            return context.getResources().getString(R.string.timer_all_day);
        } else if (isWorkDay) {
            return context.getResources().getString(R.string.timer_work_day);
        } else if (isWeekend) {
            return context.getResources().getString(R.string.timer_weekend_day);
        } else {
            String ss = "";
            if (integerList.get(0) == 1) ss = ss + "一 ";
            if (integerList.get(1) == 1) ss = ss + "二 ";
            if (integerList.get(2) == 1) ss = ss + "三 ";
            if (integerList.get(3) == 1) ss = ss + "四 ";
            if (integerList.get(4) == 1) ss = ss + "五 ";
            if (integerList.get(5) == 1) ss = ss + "六 ";
            if (integerList.get(6) == 1) ss = ss + "日 ";
            return ss;
        }

    }

}
