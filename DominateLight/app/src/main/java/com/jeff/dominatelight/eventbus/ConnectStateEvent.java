package com.jeff.dominatelight.eventbus;

import com.telink.bluetooth.event.DataEvent;

/**
 * Created by liucr on 2016/1/22.
 */
public class ConnectStateEvent extends DataEvent<Integer> {

    public static final String ConnectStateEvent = "ConnectStateEvent";

    public ConnectStateEvent(Object sender, String type, Integer args) {
        super(sender, type, args);
    }

    public static ConnectStateEvent newInstance(Object sender, String type, Integer args) {
        return new ConnectStateEvent(sender, type, args);
    }
}
