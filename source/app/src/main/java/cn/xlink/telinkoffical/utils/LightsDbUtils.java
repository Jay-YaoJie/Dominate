package cn.xlink.telinkoffical.utils;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.bean.greenDao.LightSort;
import cn.xlink.telinkoffical.database.LightSortDao;
import cn.xlink.telinkoffical.model.Light;

/**
 * Created by liucr on 2016/3/24.
 */
public class LightsDbUtils {

    private static LightsDbUtils lightsDbUtils;

    private static LightSortDao lightSortDao;

    public LightsDbUtils() {
        lightSortDao = MyApp.getApp().getDaoSession().getLightSortDao();
    }

    public static void init() {
        lightsDbUtils = new LightsDbUtils();
    }

    public static LightsDbUtils getInstance() {
        return lightsDbUtils;
    }

    /**
     * 当前设备插入或更新设备
     *
     * @param lightSort
     */
    public void updataOrInsert(LightSort lightSort) {
        updataOrInsert(getCurType(), lightSort);
    }

    /**
     * 插入或更新设备
     *
     * @param type
     * @param lightSort
     */
    public void updataOrInsert(String type, LightSort lightSort) {
        lightSort.setType(type);
        LightSort dblightSort = getLightByMesh(type, lightSort.getMeshAddress());
        if (dblightSort == null) {
            LogUtil.e("insert lightSort : " + lightSortDao.insert(lightSort));
        } else {
            lightSort.setId(dblightSort.getId());
            lightSortDao.update(lightSort);
            LogUtil.e("update lightSort : " + lightSort.getMacAddress());
        }
    }

    /**
     * 删除本地记录
     *
     * @param lightSort
     */
    public void deleteLight(LightSort lightSort) {
        lightSortDao.delete(lightSort);
    }

    /**
     * 删除本地记录
     * @param account
     * @param placeMesh
     */
    public void deleteLight(String account, String placeMesh) {
        for (Light light : getAllLightsByType(getType(account, placeMesh))) {
            deleteLight(light.getLightSort());
        }
    }

    /**
     * 通过前缀及mac查询设备
     *
     * @param type
     * @param mac
     * @return
     */
    public LightSort getLightByMac(String type, String mac) {
        List<LightSort> lightSorts = lightSortDao.queryBuilder().where(
                LightSortDao.Properties.Type.eq(type), LightSortDao.Properties.MacAddress.eq(mac)).list();
        if (lightSorts.size() == 0) {
            return null;
        } else {
            return lightSorts.get(0);
        }
    }

    /**
     * 通过前缀及mac查询设备
     *
     * @param type
     * @param mesh
     * @return
     */
    public LightSort getLightByMesh(String type, int mesh) {
        List<LightSort> lightSorts = lightSortDao.queryBuilder().where(
                LightSortDao.Properties.Type.eq(type), LightSortDao.Properties.MeshAddress.eq(mesh)).list();
        if (lightSorts.size() == 0) {
            return null;
        } else {
            return lightSorts.get(0);
        }
    }

    /**
     * 获取当前账号的所有灯
     *
     * @return
     */
    public List<Light> getCurAccountLights() {
        //从数据库读出所有组
        return getAllLightsByType(getCurType());
    }

    /**
     * 根据前缀获取所有灯
     *
     * @param type
     * @return
     */
    public List<Light> getAllLightsByType(String type) {
        //从数据库读出所有组
        List<LightSort> lightSorts = lightSortDao.queryBuilder().where(LightSortDao.Properties.Type.eq
                (type)).list();
        List<Light> lightList = new ArrayList<>();
        for (LightSort lightSort : lightSorts) {
            Light light = new Light(lightSort);
            lightList.add(light);
        }
        return lightList;
    }

    /**
     * 获取当前前缀
     *
     * @return
     */
    private String getCurType() {
        String type = getType(TelinkCommon.getCurDbUidType(), TelinkCommon.getCurPlaceType());
        return type;
    }

    public static String getType(String account, String place) {
        return account + place + TelinkCommon.LIGHTLIST;
    }
}
