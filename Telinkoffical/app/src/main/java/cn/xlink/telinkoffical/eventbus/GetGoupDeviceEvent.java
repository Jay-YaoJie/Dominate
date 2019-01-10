package cn.xlink.telinkoffical.eventbus;


import com.telink.bluetooth.event.DataEvent;

/**
 * Created by liucr on 2015/12/26.
 */
public class GetGoupDeviceEvent extends DataEvent<byte[]> {

    public static final String GetGoupDeviceAction = "GetGoupDeviceAction";

    public GetGoupDeviceEvent(Object sender, String type, byte[] args) {
        super(sender, type, args);
    }

    public static StringEvent newInstance(Object sender, String type, String args) {
        return new StringEvent(sender, type, args);
    }

}
