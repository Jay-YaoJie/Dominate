package com.jeff.dominatelight.eventbus;


import com.jeff.dominatelight.bean.greenDao.PlaceSort;
import com.telink.bluetooth.event.DataEvent;

/**
 * Created by liucr on 2016/1/18.
 */
public class PlacesUpataEvent extends DataEvent<PlaceSort> {

    public static final String PlacesUpataEvent = "PlacesUpataEvent";

    public PlacesUpataEvent(Object sender, String type, PlaceSort args) {
        super(sender, type, args);
    }

    public static PlacesUpataEvent newInstance(Object sender, String type, PlaceSort args) {
        return new PlacesUpataEvent(sender, type, args);
    }

}
