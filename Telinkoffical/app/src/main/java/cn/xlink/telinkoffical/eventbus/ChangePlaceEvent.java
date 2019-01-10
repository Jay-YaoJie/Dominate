package cn.xlink.telinkoffical.eventbus;

import com.telink.bluetooth.event.DataEvent;

import cn.xlink.telinkoffical.bean.greenDao.PlaceSort;

/**
 * Created by liucr on 2016/1/22.
 */
public class ChangePlaceEvent extends DataEvent<PlaceSort> {

    public static final String ChangePlaceEvent = "ChangePlaceEvent";

    public ChangePlaceEvent(Object sender, String type, PlaceSort args) {
        super(sender, type, args);
    }

}
