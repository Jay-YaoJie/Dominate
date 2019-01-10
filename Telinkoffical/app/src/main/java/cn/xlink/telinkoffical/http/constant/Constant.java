package cn.xlink.telinkoffical.http.constant;

import cn.xlink.telinkoffical.MyApp;

public class Constant {
    // 默认密码
    public static final String passwrod = "8888";
    // 产品id
    public static String PRODUCTID = "";

    public static final String APP_ID = "appid";
    public static final String APP_KEY = "appkey";
    public static final String APP_PSWD = "app_passwrod";


    public static String INTENT_TYPE = "intent_type";
    public static String INTENT_TITLE = "intent_title";
    public static String INTENT_DEVICE_LIST = "intent_device_list";
    public static String INTENT_ROOM_LIST = "intent_room_list";
    public static String LOGIN_USERNAME = "login_username";
    public static String LOGIN_PASSWORD = "login_password";
    public static String LOGIN_USER_NICK_NAME = "login_user_nick_name";
    public static String EXTRA_DATA = "extra_data";
    public static String EXTRA_STATUS = "extra_status";


    // ------------启动 监听
    public static final String PACKAGE_NAME = MyApp.getApp().getPackageName();
    public static final String BROADCAST_ON_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"; //
    public static final String BROADCAST_ON_START = PACKAGE_NAME + ".onStart"; //
    public static final String BROADCAST_ON_LOGIN = PACKAGE_NAME
            + ".xlinkonLogin";

    public static final String BROADCAST_CLOUD_DISCONNECT = PACKAGE_NAME
            + ".clouddisconnect";
    public static final String BROADCAST_LOCAL_DISCONNECT = PACKAGE_NAME
            + ".localdisconnect";
    public static final String BROADCAST_RECVPIPE = PACKAGE_NAME + ".recv-pipe";
    public static final String BROADCAST_DEVICE_CHANGED = PACKAGE_NAME + ".device-changed";
    public static final String BROADCAST_DEVICE_STUDY = PACKAGE_NAME + ".device-study";
    public static final String BROADCAST_DEVICE_GETONLINEDEVICE= PACKAGE_NAME + ".device-onlineDevice-success";
    public static final String BROADCAST_DEVICE_GETDLIST= PACKAGE_NAME + ".device-list-success";
    public static final String BROADCAST_CANEL_DEVICE_STUDY = PACKAGE_NAME + ".canel-device-study";
    public static final String BROADCAST_DELETE_DEVICE = PACKAGE_NAME + ".broadcast_delete_device";
    public static final String BROADCAST_DEVICE_STATUS = PACKAGE_NAME + ".broadcast_device_status";
    public static final String BROADCAST_SECURITY_STATUS = PACKAGE_NAME + ".broadcast_security_status";

    public static final String BROADCAST_DEVICE_SYNC = PACKAGE_NAME
            + ".device-sync";
    public static final String BROADCAST_EXIT = PACKAGE_NAME + ".exit";
    public static final String BROADCAST_TIMER_UPDATE = PACKAGE_NAME
            + "timer-update";
    public static final String BROADCAST_SOCKET_STATUS = PACKAGE_NAME
            + "socket-status";

    public static final String BROADCAST_ZIGBEE_UPDATA = PACKAGE_NAME
            + ".zigbee.updata";
    public static final String BROADCAST_ROOM_UPDATA = PACKAGE_NAME
            + ".room.updata";
    public static final String BROADCAST_SCENE_UPDATA = PACKAGE_NAME
            + ".scene.updata";

    public static final String BROADCAST_ROOM_LIST = PACKAGE_NAME
            + ".room.list";
    public static final String BROADCAST_SHORTCUT_UPDATE = PACKAGE_NAME
            + ".shortcut.update";

    public static final String BROADCAST_NOTIFY_UPDATE = PACKAGE_NAME
            + ".notify.update";


    // http 注册，获取appid回调
    public static final int HTTP_NETWORK_ERR = 1;

    // 数据包超时时间
    public static final int TIMEOUT = 10;// 设置请求超时时间

    public static final String DATA = "data";
    // public static final String DEVICE = "device";
    public static final String DEVICE_MAC = "device-mac";
    public static final String DEVICE_IP = "device-ip";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String KEY = "key";
    public static final String SLAVEID = "slaveid";
    public static final int TIMER_OFF = 0;
    public static final int TIMER_ON = 1;
    public static final int TIMER_BUFF_SIZE = 6;
    public static final int TIMER_MAX = 19;
    public static final String DEVICE = "device";
    public static final String ROOM = "ROOM";
    public static final String NAME = "NAME";
    public static final String SCENE = "SCENE";
    public static final String BROADCAST_DEVICE_ADD = PACKAGE_NAME
            + "broadcast-device-add";
    public static final String BROADCAST_DEVICE_CONNECT_STATUS = PACKAGE_NAME
            + "broadcast-device-connect-status";

}
