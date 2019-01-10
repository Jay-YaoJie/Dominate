package cn.xlink.telinkoffical.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.bean.greenDao.PlaceSort;
import cn.xlink.telinkoffical.database.PlaceSortDao;

/**
 * Created by liucr on 2016/3/24.
 */
public class PlacesDbUtils {

    private static PlacesDbUtils placesDbUtils;

    private static PlaceSortDao placeSortDao;

    public PlacesDbUtils() {
        placeSortDao = MyApp.getApp().getDaoSession().getPlaceSortDao();
    }

    public static void init() {
        placesDbUtils = new PlacesDbUtils();
    }

    public static PlacesDbUtils getInstance() {
        return placesDbUtils;
    }

    /**
     * 当前设备插入或更新设备
     * @param placeSort
     */
    public void updataOrInsert(PlaceSort placeSort) {
        updataOrInsert(getCurType(), placeSort);
    }

    /**
     * 插入或更新设备
     * @param type
     * @param placeSort
     */
    public void updataOrInsert(String type, PlaceSort placeSort) {
        placeSort.setType(type);
        PlaceSort localSort = getPlaceByMac(type, placeSort.getMeshAddress());
        if(localSort == null){
            long id = placeSortDao.insert(placeSort);
        }else {
            placeSort.setId(localSort.getId());
            placeSortDao.update(placeSort);
        }
    }

    /**
     * 删除本地记录
     * @param placeSort
     */
    public void deletePlace(PlaceSort placeSort){
        placeSortDao.delete(placeSort);
    }

    /**
     * 通过前缀及mac查询设备
     * @param type
     * @param meshAddress
     * @return
     */
    public PlaceSort getPlaceByMac(String type, String meshAddress){
        List<PlaceSort> placeSorts = placeSortDao.queryBuilder().where(
                PlaceSortDao.Properties.Type.eq(type), PlaceSortDao.Properties.MeshAddress.eq(meshAddress)).list();
        if(placeSorts.size() == 0){
            return null;
        }else {
            return placeSorts.get(0);
        }
    }

    /**
     * 获取当前账号的所有灯
     * @return
     */
    public List<PlaceSort> getAllPlace() {
        //从数据库读出所有组
        return getAllPlacesByType(getCurType());
    }

    /**
     * 根据前缀获取所有灯
     * @param type
     * @return
     */
    public List<PlaceSort> getAllPlacesByType(String type) {
        //从数据库读出所有组
        List<PlaceSort> placeSorts = placeSortDao.queryBuilder().where(PlaceSortDao.Properties.Type.eq
                (type)).list();
        Log.i("liucr","placeSorts.size(): "+ placeSorts.size());
        return placeSorts;
    }

    /**
     * 根据前缀、创建者获取Place
     * @param creatorId
     * @return
     */
    public List<PlaceSort> getPlaceByCreatorId(String creatorId) {
        //从数据库读出所有组
        List<PlaceSort> placeSorts = placeSortDao.queryBuilder().where(
                PlaceSortDao.Properties.Type.eq(getCurType()),
                PlaceSortDao.Properties.CreatorId.eq(creatorId)
                ).list();
        Log.i("liucr","placeSorts.size(): "+ placeSorts.size());
        return placeSorts;
    }

    /**
     * 获取当前前缀
     * @return
     */
    private String getCurType(){
        String type = getType(TelinkCommon.getCurDbUidType());
        return type;
    }

    public static String getType(String uId){
        return uId + TelinkCommon.PLACESLIST;
    }
}
