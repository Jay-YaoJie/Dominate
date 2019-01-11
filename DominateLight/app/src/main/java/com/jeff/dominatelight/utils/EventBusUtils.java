package com.jeff.dominatelight.utils;


import com.telink.util.Event;
import com.telink.util.EventBus;
import com.telink.util.EventListener;

/**
 * Created by liucr on 2015/12/23.
 */
public class EventBusUtils {

    private EventBus eventBus;

    private static EventBusUtils busUtils;

    public EventBusUtils(){
        eventBus = new EventBus();
    }

    public static void init(){
        busUtils = new EventBusUtils();
    }

    public static EventBusUtils getInstance(){
        return busUtils;
    }

    public void addEventListener(String eventType, EventListener listener) {
        this.eventBus.addEventListener(eventType, listener);
    }

    public void removeEventListener(EventListener listener) {
        this.eventBus.removeEventListener(listener);
    }

    public void removeEventListener(String eventType, EventListener listener) {
        this.eventBus.removeEventListener(eventType, listener);
    }

    public void removeEventListeners() {
        this.eventBus.removeEventListeners();
    }

    public void dispatchEvent(Event event) {
        this.eventBus.dispatchEvent(event);
    }

}
