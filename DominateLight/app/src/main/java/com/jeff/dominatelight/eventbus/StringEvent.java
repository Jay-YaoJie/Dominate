package com.jeff.dominatelight.eventbus;

import com.telink.bluetooth.event.DataEvent;

/**
 * Created by liucr on 2015/12/23.
 */
public class StringEvent extends DataEvent<String> {

    public static final String StringEevent = "StringEvent";

    public static final String USER_EXTRUSION = "USER_EXTRUSION";

    public static final String CONNECTING = "CONNECTING";

    public StringEvent(Object sender, String type, String args) {
        super(sender, type, args);
    }

    public static StringEvent newInstance(Object sender, String type, String args) {
        return new StringEvent(sender, type, args);
    }

}
