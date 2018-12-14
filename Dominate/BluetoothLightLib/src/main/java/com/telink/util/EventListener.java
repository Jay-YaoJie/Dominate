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

public interface EventListener<T> {
    void performed(Event<T> event);
}