package com.jeff.dominate.util;


import com.jeff.dominate.TelinkLightService;

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-11-17.
 * description ：
 */


public class MeshCommandUtil {

    /**
     * 停止mesh ota
     */
    public static void sendStopMeshOTACommand(){
        byte opcode = (byte) 0xC6;
        int address = 0xFFFF;
        byte[] params = new byte[]{(byte) 0xFE, (byte) 0xFF};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                params);
    }

    /**
     * 获取设备OTA状态
     */
    public static void getDeviceOTAState(){
        byte opcode = (byte) 0xC7;
        int address = 0x0000;
        byte[] params = new byte[]{0x20, 0x05};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                params);
    }

    /**
     * 获取设备版本
     */
    public static void getVersion(){
        byte opcode = (byte) 0xC7;
        int address = 0xFFFF;
        byte[] params = new byte[]{0x20, 0x00};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
    }


}
