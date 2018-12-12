package com.telink.bluetooth.light.model;

import java.io.Serializable;

/**
 * Created by kee on 2018/1/8.
 */

public class TestModel implements Serializable{
    private int id;
    private String name;
    private byte opCode;
    private int vendorId;
    private int address;
    private byte[] params;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // 用于占位
    private boolean isHolder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getOpCode() {
        return opCode;
    }

    public void setOpCode(byte opCode) {
        this.opCode = opCode;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public byte[] getParams() {
        return params;
    }

    public void setParams(byte[] params) {
        this.params = params;
    }

    public boolean isHolder() {
        return isHolder;
    }

    public void setHolder(boolean holder) {
        isHolder = holder;
    }
}
