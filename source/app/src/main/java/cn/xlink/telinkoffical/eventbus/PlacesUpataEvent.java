package cn.xlink.telinkoffical.eventbus;

import com.telink.bluetooth.event.DataEvent;

import cn.xlink.telinkoffical.bean.greenDao.PlaceSort;

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
