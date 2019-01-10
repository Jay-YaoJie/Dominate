package cn.xlink.telinkoffical.utils;


import android.text.TextUtils;

import cn.xlink.telinkoffical.bean.greenDao.User;
import cn.xlink.telinkoffical.model.Places;

public class TelinkCommon {

    /***************************** Activity 切换传值**********************************/
    public static final String ACTIVITY_TYPE = "activity_type";
    public static final String ACTIVITY_TYPE_LIGHT_MESH = "activity_type_light_mesh";
    public static final String ACTIVITY_TYPE_GTOUP_MESH = "activity_type_group_mesh";
    public static final String ACTIVITY_TYPE_SCENE_MESH = "activity_type_scene_id";
    public static final String ACTIVITY_TYPE_TIME_ID = "activity_type_time_id";
    public static final String ACTIVITY_TYPE_ACCOUNT = "activity_type_account";
    public static final String ACTIVITY_TYPE_PASSWORD = "activity_type_password";
    public static final String ACTIVITY_TYPE_SHARE = "activity_type_share";
    public static final String ACTIVITY_TYPE_OTA = "activity_type_ota";

    /*****************************EventBus******************************/
    public static final String STRING_ADDPLACE_SUCCESS = "String_AddPlace_Success";
    public static final String STRING_ADDPLACE_ERROR = "String_AddPlace_Error";
    public static final String STRING_GETPLACE_ERROR = "String_GetPlace_Error";


    /***************************** 数据区 **********************************/
    public static final String CURUSER = "CURUSER";


    public static final String PLACESLIST = "Place列表";

    public static final String GROUPLIST = "分组列表";

    public static final String SCENELIST = "情景列表列表";

    public static final String LIGHTLIST = "灯列表";

    public static final String SCENEACTIONLIST = "情景动作列表列表";

    public static final String SCENETIMERLIST = "情景定时器列表列表";

    /**************************SharedPreferences******************************/
    public static final String CURPLACEMESH = "curplacemesh";


    //GeneralSort用的标识
    public final static int Light_Type = 0;

    public final static int Group_Type = 1;

    public final static int Text_Type = 2;

    public final static int Add_Group_Type = 3;

    public final static int Add_Light_Type = 4;

    public static String getCurPlaceType() {
//        if(Places.getInstance().getCurPlaceSort() == null){
//            return UserUtil.NoMaster;
//        }
        return Places.getInstance().getCurPlaceSort().getMeshAddress();
    }

    public static String getCurDbUidType() {
        if(UserUtil.getUser()!=null && !TextUtils.isEmpty(UserUtil.getUser().getUid())){
            return UserUtil.getUser().getUid();
        }
        return UserUtil.NoMaster;
    }

}
