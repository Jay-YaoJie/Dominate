/*
 * Copyright (C) 2015 The Telink Bluetooth Light Project
 *
 */
package com.telink.bluetooth.event;

import com.telink.util.Event;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：
 */

public class DataEvent<A> extends Event<String> {

    protected A args;

    public DataEvent(Object sender, String type, A args) {
        super(sender, type);
        this.args = args;
    }

    public A getArgs() {
        return args;
    }
}
