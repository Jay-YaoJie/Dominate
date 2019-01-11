/*
 * Copyright (C) 2015 The Telink Bluetooth Light Project
 *
 */
package com.telink.bluetooth;

import java.util.UUID;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：
 */

public class Command {

    public UUID serviceUUID;
    public UUID characteristicUUID;
    public CommandType type;
    public byte[] data;
    public Object tag;
    public int delay;

    public Command() {
        this(null, null, CommandType.WRITE);
    }

    public Command(UUID serviceUUID, UUID characteristicUUID, CommandType type) {
        this(serviceUUID, characteristicUUID, type, null);
    }

    public Command(UUID serviceUUID, UUID characteristicUUID, CommandType type,
                   byte[] data) {
        this(serviceUUID, characteristicUUID, type, data, null);
    }

    public Command(UUID serviceUUID, UUID characteristicUUID, CommandType type,
                   byte[] data, Object tag) {

        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
        this.type = type;
        this.data = data;
        this.tag = tag;
    }

    public static Command newInstance() {
        return new Command();
    }

    public void clear() {
        this.serviceUUID = null;
        this.characteristicUUID = null;
        this.data = null;
    }

    public enum CommandType {
        READ, WRITE, WRITE_NO_RESPONSE, ENABLE_NOTIFY, DISABLE_NOTIFY
    }

    public interface Callback {

        void success(Peripheral peripheral, Command command, Object obj);

        void error(Peripheral peripheral, Command command, String errorMsg);

        boolean timeout(Peripheral peripheral, Command command);
    }

    @Override
    public String toString() {
        return "Command{" +
                "serviceUUID=" + serviceUUID +
                ", characteristicUUID=" + characteristicUUID +
                ", type=" + type +
                ", data=" + java.util.Arrays.toString(data) +
                ", tag=" + tag +
                ", delay=" + delay +
                '}';
    }
}
