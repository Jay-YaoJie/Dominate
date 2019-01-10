package cn.xlink.telinkoffical.manage;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.Parameters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.xlink.telinkoffical.bean.greenDao.SceneActionSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneTimerSort;
import cn.xlink.telinkoffical.model.Group;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Scene;
import cn.xlink.telinkoffical.service.TelinkLightService;
import cn.xlink.telinkoffical.utils.TelinkTimerUtils;
import cn.xlink.telinkoffical.utils.XlinkUtils;

/**
 * Created by MYFLY on 2015/12/24.
 */
public class CmdManage {

    public static int sendDelay = 100;

    public static int sendDelay500 = 500;

    private static int DELAY_TIME = 1000;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                notifyLight(2);
            }
        }
    };

    /**
     * 添加组
     *
     * @param group     组
     * @param deviceAdd 设备id
     */
    public static void allocDeviceGroup(Group group, int deviceAdd) {

        int groupAddress = group.getGroupSort().getMeshAddress();
        int dstAddress = deviceAdd;
        byte opcode = (byte) 0xD7;
        byte[] params = new byte[]{0x01, (byte) (groupAddress & 0xFF),
                (byte) (groupAddress >> 8 & 0xFF)};

        TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddress, params);
    }

    /**
     * 删除一个组
     *
     * @param group
     */
    public static void deleteGroup(Group group) {
        int groupAddress = group.getGroupSort().getMeshAddress();
        byte opcode = (byte) 0xD7;
        byte[] params = new byte[]{0x00, (byte) (groupAddress & 0xFF),
                (byte) (groupAddress >> 8 & 0xFF)};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, groupAddress, params);
    }

    /**
     * 删除组内的灯
     *
     * @param group
     * @param lightAdd
     */
    public static void delGroupLight(Group group, int lightAdd) {
        int groupAddress = group.getGroupSort().getMeshAddress();
        byte opcode = (byte) 0xd7;
        byte[] params = new byte[]{0x00, (byte) (groupAddress & 0xFF),
                (byte) (groupAddress >> 8 & 0xFF)};

        TelinkLightService.Instance().sendCommandNoResponse(opcode, lightAdd, params);
    }

    /**
     * 获取灯所在的所有组
     *
     * @param deviceAdd
     */
//    public static void getDeviceGroup(int deviceAdd) {
//        byte opcode = (byte) 0xDD;
//        int dstAddress = deviceAdd;
//        byte[] params = new byte[]{0x08, 0x01};
//
//        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);
//    }

    /**
     * 获取组内所有成员
     *
     * @param groupAdd
     */
//    public static void getGroupDevice(int groupAdd) {
//        byte opcode = (byte) 0xE0;
//        int dstAddress = groupAdd;
//        byte[] params = new byte[]{(byte) 0xff, (byte) 0xff};
//
//        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);
//    }

    /**
     * 改变组的开关状态
     *
     * @param group
     */
    public static void changeGroupStatus(Group group) {

        if (group.status == ConnectionStatus.OFFLINE) {
            return;
        }

        byte opcode = (byte) 0xD0;
        int dstAddr = group.getGroupSort().getMeshAddress();

        if (group.status == ConnectionStatus.ON) {
            TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
                    new byte[]{0x00, 0x00, 0x00});
        } else {
            TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
                    new byte[]{0x01, 0x00, 0x00});
        }
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0, DELAY_TIME);
    }

    /**
     * 设置组的亮度
     *
     * @param group
     */
    public static void changeGroupLum(Group group) {
        int brightness = group.getGroupSort().getBrightness();
        //设置灯的亮度
        if (brightness < 5) {
            brightness = 5;
        } else if (brightness > 100) {
            brightness = 100;
        }
        byte opcode = (byte) 0xD2;
        byte[] params = new byte[]{(byte) brightness};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, group.getGroupSort().getMeshAddress(), params);
    }

    /**
     * 改变色温
     *
     * @param light
     */
    public static void changeLightCT(Light light) {
        byte opcode = (byte) 0xE2;
        byte[] params = new byte[]{0x05, (byte) light.temperature};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, light.getLightSort().getMeshAddress(), params);
    }

    public static void changeLightCT(Light light, int ct) {
        byte opcode = (byte) 0xE2;
        byte[] params = new byte[]{0x05, (byte) ct};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, light.getLightSort().getMeshAddress(), params);
    }

    /**
     * light color ctrl
     *
     * @param light
     * @param color
     */
    public static void changeLightColor(Light light, int color) {
        byte red = (byte) (color >> 16 & 0xFF);
        byte green = (byte) (color >> 8 & 0xFF);
        byte blue = (byte) (color & 0xFF);

        byte[] params = new byte[]{0x04, red, green, blue};
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE2, light.getLightSort().getMeshAddress(), params);
    }

    /**
     * 更改meshAddr
     *
     * @param meshAddr
     */
    public static void changeDeviceAddr(Light light, int meshAddr) {
        byte opcode = (byte) 0xE0;
        byte[] params = new byte[]{(byte) meshAddr};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, light.getLightSort().getMeshAddress(), params);
    }

    //切换灯状态, 由开->关. 关->开
    public static void changeLightStatus(Light light) {
        ConnectionStatus status = ConnectionStatus.OFF;

        if (light == null) {
            return;
        }
//        if (light.status == ConnectionStatus.OFFLINE)
//            return;

        if (light.status == ConnectionStatus.OFF)
            status = ConnectionStatus.ON;

        if (light.status == ConnectionStatus.ON)
            status = ConnectionStatus.OFF;

        setLightStatus(light, status);
    }

    //设置灯的状态
    public static void setLightStatus(Light light, ConnectionStatus status) {
        if (light == null)
            return;

        int dstAddr = light.getLightSort().getMeshAddress();

        Log.d("setLightStatus", dstAddr + ":" + status.toString());

        byte opcode = (byte) 0xD0;
        if (status == ConnectionStatus.OFF) {
            TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr, new byte[]{0x00, 0x00, 0x00});
        } else if (status == ConnectionStatus.ON) {
            TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr, new byte[]{0x01, 0x00, 0x00});
        }
//        light.status = status;
//        light.updateIcon();
    }

    //设置灯的亮度
    public static void setLightLum(Light light) {
        byte opcode = (byte) 0xD2;
        if (light.brightness < 5) {
            light.brightness = 5;
        }
        byte[] params = new byte[]{(byte) light.brightness};

        TelinkLightService.Instance().sendCommandNoResponse(opcode, light.getLightSort().getMeshAddress(), params);
    }

    //设置灯的亮度
    public static void setLightLum(int mesh, int brightness) {
        byte opcode = (byte) 0xD2;
        if (brightness < 5) {
            brightness = 5;
        }
        byte[] params = new byte[]{(byte) brightness};

        TelinkLightService.Instance().sendCommandNoResponse(opcode, mesh, params);
    }

    //设置灯的亮度
    public static void kickOut(Light light) {
        byte opcode = (byte) 0xE3;
        TelinkLightService.Instance().sendCommandNoResponse(opcode, light.getLightSort().getMeshAddress(), null);
    }

    /**
     * icon_scene_add
     *
     * @param sceneActionSort
     */
    private static int addDeviceScenceCount = 0;

    private static Handler scencehandler = new Handler();

    public static void addDeviceScenceDelay(final SceneActionSort sceneActionSort) {
        addDeviceScenceCount++;
        scencehandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addDeviceScenceCount--;
                addDeviceScence(sceneActionSort);
            }
        }, sendDelay * addDeviceScenceCount);
    }

    public static void addDeviceScence(SceneActionSort sceneActionSort) {
        int meshAddress = sceneActionSort.getDeviceMesh();
        int actionId = sceneActionSort.getSceneId();
        int brightness = sceneActionSort.getBrightness();
        int temp = sceneActionSort.getTemperature();

        byte red = (byte) (sceneActionSort.getColor() >> 16 & 0xFF);
        byte green = (byte) (sceneActionSort.getColor() >> 8 & 0xFF);
        byte blue = (byte) (sceneActionSort.getColor() & 0xFF);
        byte[] params = new byte[]{0x01, (byte) actionId, (byte) brightness, red, green, blue, (byte) temp};
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xEE, meshAddress, params);
    }

    /**
     * 执行情景
     *
     * @param scene
     */
    public static void executeScene(Scene scene) {
        int sceneId = scene.getSceneSort().getSceneId();
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xEF, 0xffff, new byte[]{(byte) sceneId});
    }

    /**
     * 删除情景动作
     *
     * @param scene
     */
    public static void deleteScene(Scene scene) {
        int sceneId = scene.getSceneSort().getSceneId();
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xEE, 0xffff, new byte[]{0x00, (byte) sceneId});

    }

    /**
     * 删除情景动作
     *
     * @param sceneActionSort
     */
    public static void deleteSceneAction(SceneActionSort sceneActionSort) {
        int meshAddress = sceneActionSort.getDeviceMesh();
        int actionId = sceneActionSort.getSceneId();
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xEE, meshAddress, new byte[]{0x00, (byte) actionId});

    }

    /**
     * 删除情景
     *
     * @param sceneActionSorts
     */
    public static void deleteSceneAction(List<SceneActionSort> sceneActionSorts) {
        for (SceneActionSort sceneActionSort : sceneActionSorts) {
            deleteSceneAction(sceneActionSort);
        }
    }

    public static void getDeviceScene(SceneActionSort sceneActionSort) {
        int meshAddress = sceneActionSort.getDeviceMesh();
        int actionId = sceneActionSort.getSceneId();
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xc0, meshAddress, new byte[]{0x10, (byte) actionId});
    }

    /**
     * 添加情景闹钟默认为开
     *
     * @param timerSort
     * @param sceneId
     */
    public static void addEditAlarm(final SceneTimerSort timerSort, int sceneId, boolean isAdd) {
        int deviceMesh = timerSort.getDeviceMesh();
        int timerId = timerSort.getTimerId();
        int type = 0;
        int month = 0;
        int day = 0;
        int hour = timerSort.getHour();
        int min = timerSort.getMinute();
        int sec = 0;
        if (timerSort.getTimerType() == 0) {      //单次定时
            type = 0x82;
            month = TelinkTimerUtils.getOnceTimerData(timerSort.getWorkDay())[0];
            day = TelinkTimerUtils.getOnceTimerData(timerSort.getWorkDay())[1];
        } else {                                 //循环定时
            type = 0x92;
            day = timerSort.getWorkDay();
        }

        byte[] params = new byte[]{(byte) (isAdd ? 0x00 : 0x02), (byte) timerId, (byte) type,
                (byte) month, (byte) day, (byte) hour, (byte) min, (byte) sec, (byte) sceneId};
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE5, deviceMesh, params);
    }

    public static void getAlarm(SceneTimerSort timerSort) {
        int timerId = timerSort.getTimerId();
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE6, timerSort.getDeviceMesh(), new byte[]{0x10, (byte) timerId});
    }

    /**
     * 删除闹钟
     *
     * @param timerSort
     */
    public static void deleteAlarm(SceneTimerSort timerSort) {
        int deviceMesh = timerSort.getDeviceMesh();
        int alarmId = timerSort.getTimerId();
        byte[] params = new byte[]{0x01, (byte) alarmId, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE5, (byte) deviceMesh, params);
    }

    /**
     * 打开闹钟
     *
     * @param timerSort
     */
    public static void openAlarm(SceneTimerSort timerSort) {
        int deviceMesh = timerSort.getDeviceMesh();
        int alarmId = timerSort.getTimerId();
        byte[] params = new byte[]{0x03, (byte) alarmId, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE5, (byte) deviceMesh, params);
    }

    /**
     * 打开闹钟
     *
     * @param timerSort
     */
    public static void closeAlarm(SceneTimerSort timerSort) {
        int deviceMesh = timerSort.getDeviceMesh();
        int alarmId = timerSort.getTimerId();
        byte[] params = new byte[]{0x04, (byte) alarmId, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE5, (byte) deviceMesh, params);
    }

    /**
     * 校准蓝牙设备灯时间
     *
     * @param meshId
     */
    public static void timeSet(int meshId) {
        Calendar calendar = Calendar.getInstance();
        byte[] bytes = XlinkUtils.shortToByteArray((short) calendar.get(Calendar.YEAR));
        byte[] params = new byte[]{bytes[1], bytes[0], (byte) (calendar.get(Calendar.MONTH) + 1), (byte) calendar.get(Calendar.DAY_OF_MONTH),
                (byte) calendar.get(Calendar.HOUR_OF_DAY), (byte) calendar.get(Calendar.MINUTE), (byte) calendar.get(Calendar.SECOND)};
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE4, meshId, params);
    }

    public static void getTimeSet() {
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE8, 0x0000, new byte[]{0x10});
    }

    /**
     * 使设备上报状态
     */
    public static void notifyLight(final int repeatCount) {
        TelinkLightService.Instance().autoRefreshNotify(false, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TelinkLightService.Instance().enableNotification();
                LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();

                refreshNotifyParams.setRefreshRepeatCount(repeatCount);
                refreshNotifyParams.setRefreshInterval(2000);
                TelinkLightService.Instance().autoRefreshNotify(true, refreshNotifyParams);
            }
        }, 500);

    }

}
