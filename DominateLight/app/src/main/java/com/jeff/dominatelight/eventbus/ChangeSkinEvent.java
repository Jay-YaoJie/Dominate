package com.jeff.dominatelight.eventbus;

import com.telink.bluetooth.event.DataEvent;

/**
 * Created by liucr on 2015/12/23.
 */
public class ChangeSkinEvent extends DataEvent<String> {

    public static final String ChangeSkinEvent = "ChangeSkinEvent";

    public ChangeSkinEvent(Object sender, String type, String args) {
        super(sender, type, args);
    }

    public static ChangeSkinEvent newInstance(Object sender, String type, String args) {
        return new ChangeSkinEvent(sender, type, args);
    }

}
