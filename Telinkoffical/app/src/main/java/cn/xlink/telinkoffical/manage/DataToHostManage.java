package cn.xlink.telinkoffical.manage;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;

import java.util.Map;

import cn.xlink.telinkoffical.bean.PlaceBean;
import cn.xlink.telinkoffical.bean.greenDao.PlaceSort;
import cn.xlink.telinkoffical.eventbus.StringEvent;
import cn.xlink.telinkoffical.http.HttpHelper;
import cn.xlink.telinkoffical.http.HttpManage;
import cn.xlink.telinkoffical.http.constant.HttpConstant;
import cn.xlink.telinkoffical.model.Places;
import cn.xlink.telinkoffical.utils.ConvertUtil;
import cn.xlink.telinkoffical.utils.EventBusUtils;
import cn.xlink.telinkoffical.utils.LogUtil;
import cn.xlink.telinkoffical.utils.PlacesDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.utils.UserUtil;

/**
 * Created by liucr on 2016/4/8.
 */
public class DataToHostManage {

    /**
     * 将Place添加到用户里
     * @param placeSort
     */
    public static void addPlaceToUser(final PlaceSort placeSort){
        if(!UserUtil.isLogin()){
            return;
        }
        HttpManage.getInstance().addDevice(UserUtil.getUser().getUid(), placeSort.getMeshAddress(),
                placeSort.getMeshKey(), placeSort.getName(), new HttpHelper.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(int code, String response) {
                        if (code == HttpConstant.PARAM_SUCCESS) {
                            Map<String, String> map = new Gson().fromJson(response, new TypeToken<Map<String, String>>() {}.getType());
                            String placeid = map.get("device_id");

                            if(placeSort.getCreatorId().equals(UserUtil.NoMaster)){
                                PlaceManage.clearPlaceDb(UserUtil.NoMaster,placeSort);
                            }

                            placeSort.setPlaceId(placeid);
                            placeSort.setPlaceVersion((long) 0);
                            placeSort.setCreatorAccount(UserUtil.getUser().getAccount());
                            placeSort.setCreatorName(UserUtil.getUser().getName());
                            placeSort.setCreatorId(UserUtil.getUser().getUid());
                            PlacesDbUtils.getInstance().updataOrInsert(placeSort);
                            updataToHost(placeSort);
                            PlaceManage.changePlace(placeSort);
                            EventBusUtils.getInstance().dispatchEvent(StringEvent
                                    .newInstance("",StringEvent.StringEevent, TelinkCommon.STRING_ADDPLACE_SUCCESS));
                        }else {
                            EventBusUtils.getInstance().dispatchEvent(StringEvent
                                    .newInstance("",StringEvent.StringEevent, TelinkCommon.STRING_ADDPLACE_ERROR));

                            PlaceManage.clearPlaceDb(UserUtil.getUser().getUid(), placeSort);
                        }
                    }
                });
    }

    public static void updataCurToHost(){
        updataToHost(Places.getInstance().getCurPlaceSort());
    }

    /**
     * 更新Place到服务器
     * @param placeSort
     */
    public static void updataToHost(PlaceSort placeSort){

        if(placeSort.getCreatorId().equals("no_master")
                || Places.getInstance().curPlaceIsShare()){
            return;
        }

        //版本号增加
        long version = 0;

        placeSort.setPlaceVersion(placeSort.getPlaceVersion() + 1);
        PlacesDbUtils.getInstance().updataOrInsert(placeSort);

        if(!UserUtil.isLogin()){
            return;
        }

        PlaceBean placeBean = ConvertUtil.placeSortToBean(placeSort);
        Log.i("liucr","placeSort: " + new Gson().toJson(placeBean));
        HttpManage.getInstance().setDeviceProperty(placeSort.getPlaceId(), placeBean, new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(int code, String response) {
            }
        });
    }

}
