package cn.xlink.telinkoffical.bean;

import java.util.List;

/**
 * Created by liucr on 2016/1/17.
 */
public class DeviceList {

    private static final long serialVersionUID = 1L;

    private List<DeviceBean> list;

    private int version;

    public void setList(List<DeviceBean> list) {
        this.list = list;
    }

    public List<DeviceBean> getList() {
        return this.list;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }
}
