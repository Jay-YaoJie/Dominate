package com.jeff.dominatelight.eventbus;


import com.jeff.dominatelight.bean.greenDao.PlaceSort;
import com.telink.bluetooth.event.DataEvent;

/**
 * Created by liucr on 2016/1/22.
 */
public class ChangePlaceEvent extends DataEvent<PlaceSort> {

    public static final String ChangePlaceEvent = "ChangePlaceEvent";

    public ChangePlaceEvent(Object sender, String type, PlaceSort args) {
        super(sender, type, args);
    }

}
