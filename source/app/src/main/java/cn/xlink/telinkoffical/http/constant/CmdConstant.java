package cn.xlink.telinkoffical.http.constant;

/**
 * Created by MYFLY on 2015/11/6.
 */
public final class CmdConstant {

    public static final byte D_DATA_HEADER = (byte) 0xAA;
    public static final byte D_DATA_END = (byte) 0x55;

    public final static class CmdCode {
        public static final byte D_GET_DEVICE_INFO = 0x10;// 查询设备类型
        public static final byte D_GET_DEVICE_PROP = 0x11;// 获取设备属性
        public static final byte D_GET_DEVICE_STA = 0x12;// 获取设备状态
        public static final byte D_SET_DEVICE_PROP = 0x13;// 设置设备属性
        public static final byte D_SET_DEVICE_STA = 0x14;// 设置设备状态
        public static final byte D_REPORT_DEVICE_STA = 0x15;// 设备上报状态;

        /**
         * 获取产品基本信息
         */
        public static final byte D_GET_DEVICE_CHECK_CODE2 = 0x3A;//获取设备校验值 2 --0x3A
        /**
         * 用户权限验证--0x20
         */
        public static final byte D_GET_USER_PERMISSION = 0x20;

        /**
         * 修改用户属性(增加用户,删除用户,修改用户密码) --0x21
         */
        public static final byte D_GET_USER_MODIFY_INFO = 0x21;

        /**
         * 查询用户(获取用户)--0x22
         */
        public static final byte D_GET_USER_QUERY = 0x22;
        /**
         * 获取设备校验码--0x30
         */
        public static final byte D_GET_DEVICE_CHECK_CODE = 0x30;
        /**
         * 获取设备列表--0x31
         */
        public static final byte D_GET_DEVICE_LIST = 0x31;

        /**
         * 更改设备属性--0x32
         */
        public static final byte D_SET_DEVICE_INFO = 0x32;

        /**
         * 获取设备当前状态--0x33
         */
        public static final byte D_GET_DEVICE_STATUS = 0x33;
        /**
         * 允许设备加入--0x34
         */
        public static final byte D_GET_ALLOW_DEVICE_JOIN = 0x34;
        /**
         * 设备控制--0x35
         */
        public static final byte D_DEVICE_CONTROL = 0x35;

        /**
         * 设备上报状态--0x36
         */
        public static final byte D_GET_DEVICE_REPORT_STATUS = 0x36;
        /**
         * 设备学习--0x37
         */
        public static final byte D_DEVICE_STUDY = 0x37;
        /**
         * 增加设备--0x38
         */
        public static final byte D_DEVICE_ADD = 0x38;
        /**
         * 删除设备--0x39
         */
        public static final byte D_DEVICE_DELETE = 0x39;

        /**
         * 取消学习--0x3B
         */
        public static final byte D_CANEL_DEVICE_STUDY = 0x3B;

        //   #pragma mark - 分组协议

        /**
         * 删除分组--0x40
         */
        public static final byte D_GROUP_DELETE = 0x40;
        /**
         * 修改分组(增加,修改)--0x41
         */
        public static final byte D_GROUP_MODIFY = 0x41;
        /**
         * 查询分组--0x42
         */
        public static final byte D_GROUP_QUERY = 0x42;

        //#pragma mark - 情景

        /**
         * 获取情景校验值--0x50
         */
        public static final byte D_SCENE_CHECK_CODE = 0x50;
        /**
         * 修改情景图片和名称(增加,删除,修改)--0x51
         */
        public static final byte D_SCENE_MODIFY_PICANDNAME = 0x51;
        /**
         * 修改情景动作(增加,删除,修改)--0x52
         */
        public static final byte D_SCENE_MODIFY_ACTION = 0x52;
        /**
         * 查询情景--0x53
         */
        public static final byte D_SCENE_QUERY = 0x53;

        // #pragma mark - 定时器
        /**
         * 修改定时器(增加,删除,修改)--0x60
         */
        public static final byte D_TIMER_MODIFY = 0x60;
        /**
         * 查询定时器--0x61
         */
        public static final byte D_TIMER_QUERY = 0x61;
        /**
         * 获取定时器校验值--0x62
         */
        public static final byte D_GET_TIMER_CHECK_CODE = 0x62;

        /**
         * 0x70 重置主机
         */
        public static final byte RESET_HOST_DEVICE = 0x70;
    }

    public final static class DeviceType {

        public static final int TYPE_DOOR_DETECTOR = 0x1001;//门磁探测器
        public static final int TYPE_PI_DETECTOR = 0x1002; //红外探测器
        public static final int TYPE_BEAN_DETECTOR = 0x1003;//红外对射探测器
        public static final int TYPE_PANIC_DETECTOR = 0x1006;//紧急探测器
        public static final int TYPE_GLASSBREAK_DETECTOR = 0x1004;//玻璃破碎探测器
        public static final int TYPE_GAS_DETECTOR = 0x1005;//漏水探测器
        public static final int TYPE_LEAK_DETECTOR = 0x1007;//气体探测器
        public static final int TYPE_SMOKE_DETECTOR = 0x1008;//烟雾探测器
        public static final int TYPE_REMOTE_CONTROL = 0x1009;//遥控器
        public static final int TYPE_RFID_KEYPAD = 0x100A;//RFID键盘
        public static final int TYPE_CO_DETECTOR = 0x100b;//CO探测器
        public static final int TYPE_TEMP_DETECTOR = 0X100C;//温度探测器
        public static final int TYPE_SHAKE_DETECTOR = 0X100D;//震动探测器
        public static final int TYPE_HOST = 0x100E;//主机

        //    自动化设备：
        public static final int TYPE_AUTO_RELAY = 0x2002;//继电器
        public static final int TYPE_AUTO_SOCKET = 0X2001;//插座
        public static final int TYPE_AUTO_ALARMA = 0X2003;//警号A
        public static final int TYPE_AUTO_ALARMB = 0X2004;//警号B
    }

//    public enum DeviceTypeEnum implements Serializable{
//        TYPE_DOOR_DETECTOR(0x1001,R.mipmap.doorsensor, R.string.door_sensor),
//        TYPE_PI_DETECTOR(0x1002,R.mipmap.pisensor,R.string.pi_sensor),
//        TYPE_BEAN_DETECTOR(0x1003, R.mipmap.bean,R.string.pi_relatve_sensor),
//        TYPE_PANIC_DETECTOR(0x1006,R.mipmap.panicbutton,R.string.panic_button),
//        TYPE_GLASSBREAK_DETECTOR(0x1004,R.mipmap.glassbreak,R.string.glass_sensor),
//        TYPE_GAS_DETECTOR(0x1005,R.mipmap.leak,R.string.leak_sensor),
//        TYPE_LEAK_DETECTOR(0x1007,R.mipmap.gasdetector,R.string.gas_detector),
//        TYPE_SMOKE_DETECTOR(0x1008,R.mipmap.smokedetector,R.string.smoke_detector),
//        TYPE_REMOTE_CONTROL(0x1009,R.mipmap.remotecontrol,R.string.remote_control),
//        TYPE_RFID_KEYPAD(0x100a,R.mipmap.rfid_keypad,R.string.rfid_keypad),
//        TYPE_CO_DETECTOR(0x100b,R.mipmap.codetector,R.string.code_tector),
//        TYPE_TEMP_DETECTOR(0x100c,R.mipmap.temperaturedetector,R.string.temperature_detector),
//        TYPE_SHAKE_DETECTOR(0x100d,R.mipmap.shakedetector,R.string.shakede_tector),
//
//        //    自动化设备：
//        TYPE_AUTO_RELAY(0x2002,R.mipmap.relayoutput,R.string.wireless_relay),
//        TYPE_AUTO_SOCKET(0x2001,R.mipmap.socket,R.string.wireless_socket),//插座 操作码0x2000是我自定义的-无用的
//        TYPE_AUTO_ALARM(0x2003,R.mipmap.siren,R.string.wireless_alarm),//警号
//        TYPE_AUTO_ALARMA(0x2003,R.mipmap.siren,R.string.wireless_alarm),//警号A
//        TYPE_AUTO_ALARMB(0x2004,R.mipmap.siren,R.string.wireless_alarm);//警号B
//
//        public int cd = 0x2000;
//
//        public int Type;
//        public int name;
//        public int iconRes;
//        private DeviceTypeEnum(int Type ,int iconRes,int nameRes){
//            this.name = nameRes;
//            this.Type = Type;
//            this.iconRes = iconRes;
//        };
//    }
}
