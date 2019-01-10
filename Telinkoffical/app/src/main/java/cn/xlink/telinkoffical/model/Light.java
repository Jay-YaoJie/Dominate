package cn.xlink.telinkoffical.model;

import android.graphics.Color;
import android.util.Log;

import com.telink.bluetooth.light.ConnectionStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.bean.greenDao.LightSort;

/**
 * lightType:1:亮度，4：颜色，5：色温
 * <p/>
 * Created by liucr on 2016/3/24.
 */
public class Light implements Serializable {

    public static int Type_Lum = 1;

    public static int Type_Color = 4;

    public static int Type_Temp = 5;

    private LightSort lightSort;

    public int brightness;

    public int temperature;

    public int color = Color.WHITE;

    public ConnectionStatus status = ConnectionStatus.OFFLINE;

    public Light(LightSort lightSort) {
        this.lightSort = lightSort;
    }

    public Light(String name, String macAddress, String firmwareRevision, int meshAddress, int lightType) {
        lightSort = new LightSort();
        lightSort.setName(name);
        lightSort.setMacAddress(macAddress);
        lightSort.setFirmwareRevision(firmwareRevision);
        lightSort.setMeshAddress(meshAddress);
        lightSort.setLightType(lightType);
        lightSort.setIsShowOnHomeScreen(true);
        lightSort.setIsAlone(true);
        lightSort.setIsAddToDefault(false);
    }

    public LightSort getLightSort() {
        return lightSort;
    }

    public void setLightSort(LightSort lightSort) {
        this.lightSort = lightSort;
    }

    public int getStatusIcon() {
        if (status == ConnectionStatus.ON) {
            return R.mipmap.icon_item_device_on;
        } else if (status == ConnectionStatus.OFF) {
            return R.mipmap.icon_item_device_off;
        } else {
            return R.mipmap.icon_item_device_offline;
        }
    }

    public int getStatusBigIcon() {
        if (status == ConnectionStatus.ON) {
            return R.mipmap.icon_device_on;
        } else if (status == ConnectionStatus.OFF) {
            return R.mipmap.icon_device_off;
        } else {
            return R.mipmap.icon_device_offline;
        }
    }

    public int getSelectIcon() {
        if (status == ConnectionStatus.OFFLINE) {
            return R.mipmap.icon_offline_device;
        } else {
            return R.mipmap.icon_device;
        }
    }

    /**
     * 获取灯所在的所有组
     *
     * @return
     */
    public List<Integer> getGroups() {
        List<Integer> groups = new ArrayList<>();
        for (Group group : Groups.getInstance().get()) {
            if (group.getMembers().contains(lightSort.getMeshAddress() + "")) {
                groups.add(group.getGroupSort().getMeshAddress());
            }
        }
        return groups;
    }

    /**
     * 是否可升级
     * 0:最新， 1：可升级， -1：离线可升级
     *
     * @return
     */
    public int isCanUpdata() {
        long localVer = 20170101;
        long lightVer = 0;

        try {
            localVer = Long.parseLong(MyApp.getApp().getVersion());
            lightVer = Long.parseLong(lightSort.getFirmwareRevision());
        } catch (Exception e) {

        }

        if (lightVer >= localVer) {
            return 0;
        } else if (status != ConnectionStatus.OFFLINE) {
            return 1;
        }
        return -1;
    }

}
