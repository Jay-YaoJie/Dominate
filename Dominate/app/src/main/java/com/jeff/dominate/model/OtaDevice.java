package com.jeff.dominate.model;

import java.io.Serializable;


/**
 * author : Jeff  5899859876@qq.com
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-11-17.
 * description ： MeshOTA升级过程中会将正在升级的设备保存在本地
 *  在意外退出或者连接状态不稳定时，优先连接保存的设备；
 */

public class OtaDevice implements Serializable {
    private static final long serialVersionUID = 2L;

    // saved mesh info
    public String meshName;
    public String meshPwd;
    public String mac;
}
