package com.jeff.dominatelight.bean;

/**
 * Created by liucr on 2016/6/7.
 */
public class DeviceUser {

    private int role;

    private int from_id;

    private int user_id;

    private String nickname;

    public void setRole(int role) {
        this.role = role;
    }

    public int getRole() {
        return this.role;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getFrom_id() {
        return this.from_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return this.user_id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }
}
