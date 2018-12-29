/*
 * Copyright (C) 2015 The Telink Bluetooth Light Project
 *
 */
package com.telink.util;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：
 */

public class Event<T> {

    protected Object sender;
    protected T type;
    protected ThreadMode threadMode = ThreadMode.Default;

    public Event(Object sender, T type) {
        this(sender, type, ThreadMode.Default);
    }

    public Event(Object sender, T type, ThreadMode threadMode) {
        this.sender = sender;
        this.type = type;
        this.threadMode = threadMode;
    }

    public Object getSender() {
        return sender;
    }

    public T getType() {
        return type;
    }

    public ThreadMode getThreadMode() {
        return this.threadMode;
    }

    public Event<T> setThreadMode(ThreadMode mode) {
        this.threadMode = mode;
        return this;
    }

    public enum ThreadMode {
        Background, Main, Default,;
    }

    @Override
    public String toString() {
        return "Event{" +
                "sender=" + sender +
                ", type=" + type +
                ", threadMode=" + threadMode +
                '}';
    }
}