package com.jeff.dominatelight.bean;

import java.io.Serializable;

/**
 * Created by liucr on 2016/1/17.
 */
public class DeviceBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private boolean is_online;

    private String product_id;

    private boolean is_active;

    private String active_code;

    private int mcu_version;

    private String mac;

    private String active_date;

    private String last_login;

    private int id;

    private String authorize_code;

    private int firmware_version;

    private int role;

    private int access_key;

    public void setIs_online(boolean is_online) {
        this.is_online = is_online;
    }

    public boolean getIs_online() {
        return this.is_online;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_id() {
        return this.product_id;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public boolean getIs_active() {
        return this.is_active;
    }

    public void setActive_code(String active_code) {
        this.active_code = active_code;
    }

    public String getActive_code() {
        return this.active_code;
    }

    public void setMcu_version(int mcu_version) {
        this.mcu_version = mcu_version;
    }

    public int getMcu_version() {
        return this.mcu_version;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return this.mac;
    }

    public void setActive_date(String active_date) {
        this.active_date = active_date;
    }

    public String getActive_date() {
        return this.active_date;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getLast_login() {
        return this.last_login;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setAuthorize_code(String authorize_code) {
        this.authorize_code = authorize_code;
    }

    public String getAuthorize_code() {
        return this.authorize_code;
    }

    public void setFirmware_version(int firmware_version) {
        this.firmware_version = firmware_version;
    }

    public int getFirmware_version() {
        return this.firmware_version;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getRole() {
        return this.role;
    }

    public void setAccess_key(int access_key) {
        this.access_key = access_key;
    }

    public int getAccess_key() {
        return this.access_key;
    }
}
