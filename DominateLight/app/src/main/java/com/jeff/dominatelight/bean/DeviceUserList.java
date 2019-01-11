package com.jeff.dominatelight.bean;

import java.util.List;

/**
 * Created by liucr on 2016/6/7.
 */
public class DeviceUserList {

    private int count;

    private List<DeviceUser> list ;

    public void setCount(int count){
        this.count = count;
    }
    public int getCount(){
        return this.count;
    }
    public void setList(List<DeviceUser> list){
        this.list = list;
    }
    public List<DeviceUser> getList(){
        return this.list;
    }

}
