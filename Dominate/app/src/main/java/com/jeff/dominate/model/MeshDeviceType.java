package com.jeff.dominate.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * author : Jeff  5899859876@qq.com
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-11-17.
 * description ：
 */

public class MeshDeviceType implements Serializable {
    public int type;
    public List<Light> deviceList = new ArrayList<>();
    public String filePath;

    @Override
    public boolean equals(Object o) {
        return o instanceof MeshDeviceType && ((MeshDeviceType) o).type == type;
    }
}
