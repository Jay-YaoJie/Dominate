package cn.xlink.telinkoffical.eventbus;

import com.telink.bluetooth.event.DataEvent;

import java.util.List;

/**
 * Created by liucr on 2015/12/26.
 */
public class GetDeviceGoupEvent extends DataEvent<List<Integer>> {

    public static final String GetDeviceGoupAction = "GetDeviceGoupAction";

    public GetDeviceGoupEvent(int lightAdd, String type, List<Integer> args) {
        super(lightAdd, type, args);
    }

    public static GetDeviceGoupEvent newInstance(int lightAdd, String type, List<Integer> args) {
        return new GetDeviceGoupEvent(lightAdd, type, args);
    }

}
