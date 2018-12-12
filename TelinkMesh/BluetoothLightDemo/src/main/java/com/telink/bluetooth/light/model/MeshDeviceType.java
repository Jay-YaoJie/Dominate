package com.telink.bluetooth.light.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kee on 2018/4/20.
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
