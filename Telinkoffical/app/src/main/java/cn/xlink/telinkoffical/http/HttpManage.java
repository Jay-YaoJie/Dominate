package cn.xlink.telinkoffical.http;

import java.util.HashMap;
import java.util.Map;

import cn.xlink.telinkoffical.bean.PlaceBean;
import cn.xlink.telinkoffical.utils.UserUtil;

/**
 * Created by MYFLY on 2016/1/6.
 */
public class HttpManage {

    private static HttpManage instance;

    private static final String COMPANY_ID = "100fa2ade6d5e600";

    public static String PRODUCT_ID = "160fa2ade723c000160fa2ade723c001";

    private final String host = "http://api2.xlink.cn";

    // 用户类型url
    public final String registerUrl = host + "/v2/user_register";
    public final String loginUrl = host + "/v2/user_auth";
    public final String forgetUrl = host + "/v2/user/password/forgot";
    public final String reNameUrl = host + "/v2/user/";
    public final String resetPasswordUrl = host + "/v2/user/password/reset";
    public final String userUrl = host + "/v2/user/";
    //刷新凭证
    public final String refreshTokenUrl = host + "/v2/user/token/refresh";
    //设备类型URL/v2/product/{product_id}/device/{device_id}
    public final String getDeviceUrl = host + "/v2/product/" + PRODUCT_ID + "/device/";
    //获取设备订阅用户列表
    public final String getDeviceSubUrl = host + "/v2/user/{user_id}/subscribe_users?device_id={device_id}";
    //分享类型URL
    public final String shareWithUrl = host + "/v2/share/device";
    public final String shareAcceptUrl = host + "/v2/share/device/accept";
    public final String shareDenyUrl = host + "/v2/share/device/deny";
    public final String getAllShareUrl = host + "/v2/share/device/list";
    public final String cancelShareUrl = host + "/v2/share/device/cancel";
    public final String deleteShareUrl = host + "/v2/share/device/delete/";

    /**
     * code : 5031001
     * msg : service unavailable
     */


    public static HttpManage getInstance() {
        if (instance == null) {
            instance = new HttpManage();
        }
        return instance;
    }


    /**
     * http 邮箱注册接口
     *
     * @param mail 用户 邮箱
     * @param name 昵称（别名，仅供后台管理平台观看，对用户来说记住uid和pwd就行）
     * @param pwd  密码
     */
    public void registerUserByMail(String mail, String name, String pwd, final HttpHelper.ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mail);
        params.put("nickname", name);
        params.put("corp_id", COMPANY_ID);
        params.put("password", pwd);
        params.put("source", "2");
        HttpHelper.postAsyn(registerUrl, params, callback);
    }

    /**
     * http 邮箱登录接口
     *
     * @param mail 用户 邮箱
     * @param pwd  密码
     */
    public void login(String mail, String pwd, final HttpHelper.ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mail);
        params.put("corp_id", COMPANY_ID);
        params.put("password", pwd);
        HttpHelper.postAsyn(loginUrl, params, callback);
    }

    /**
     * 获取用户信息
     *
     * @param uid
     * @param callback
     */
    public void getUserMsg(String uid, final HttpHelper.ResultCallback callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());
        String url = userUrl + uid;
        HttpHelper.getAsyn(url, head, callback);
    }

    /**
     * http 忘记密码
     *
     * @param mail 用户 邮箱
     */
    public void forgetPasswd(String mail, final HttpHelper.ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mail);
        params.put("corp_id", COMPANY_ID);
        HttpHelper.postAsyn(forgetUrl, params, callback);
    }

    /**
     * 修改用户名
     *
     * @param uid
     * @param name
     * @param callback
     */
    public void reName(String uid, String name, final HttpHelper.ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());
        params.put("nickname", name);
        String url = reNameUrl + uid;
        HttpHelper.putAsyn(url, head, params, callback);
    }

    /**
     * http 刷新凭证
     *
     * @param refreshToken
     */
    public void refreshToken(String refreshToken, final HttpHelper.ResultCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("refresh_token", refreshToken);
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Token", getAccessToken());
        HttpHelper.postAsyn(refreshTokenUrl, headers, params, callback);
    }


    /**
     * 重置密码
     *
     * @param oldPassword
     * @param newPassword
     * @param callback
     */
    public void resetPassword(String oldPassword, String newPassword, final HttpHelper.ResultCallback callback) {
        Map<String, String> params = new HashMap<>();
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());
        params.put("old_password", oldPassword);
        params.put("new_password", newPassword);
        HttpHelper.putAsyn(resetPasswordUrl, head, params, callback);
    }

    /**
     * 停用用户
     *
     * @param uid
     * @param callback
     */
    public void cancelUser(String uid, final HttpHelper.ResultCallback callback) {
        Map<String, String> params = new HashMap<>();
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());
        String url = userUrl + uid + "/status";
        HttpHelper.putAsyn(url, head, params, callback);
    }

    /**
     * 获取用户设备列表
     *
     * @param uid
     * @param callback
     */
    public void getUserDeviceList(String uid, final HttpHelper.ResultCallback callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());
        String url = userUrl + uid + "/subscribe/devices?version=0";
        HttpHelper.getAsyn(url, head, callback);
    }

    /**
     * 取消订阅设备
     *
     * @param uid
     * @param deviceID
     * @param callback
     */
    public void unSubscribeDevice(String uid, String deviceID, final HttpHelper.ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        Map<String, Object> parms = new HashMap<>();
        head.put("Access-Token", getAccessToken());

        parms.put("device_id", deviceID);

        String unSubscribeDeviceUrl = userUrl + uid + "/unsubscribe";
        HttpHelper.postAsyn(unSubscribeDeviceUrl, head, parms, callback);
    }

    /**
     * 获取设备被那些用户订阅了
     *
     * @param uid
     * @param deviceID
     * @param callback
     */
    public void getDeviceShare(String uid, String deviceID, final HttpHelper.ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());
        String getDeviceShareUrl = userUrl + uid + "/subscribe_users?device=" + deviceID;
        HttpHelper.getAsyn(getDeviceShareUrl, head, callback);
    }

    /***********************************************
     * 设备类接口
     ***********************************************/

    /**
     * 添加place
     *
     * @param mac
     * @param callback
     */
    public void addDevice(String uid, String mac, String access_key, String name, final HttpHelper.ResultCallback callback) {
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String> head = new HashMap<>();

        head.put("Access-Token", getAccessToken());

        params.put("product_id", PRODUCT_ID);
        params.put("mac", mac);
        params.put("name", name);
        params.put("access_key", access_key);

        String addDeviceUrl = userUrl + uid + "/register_device";
        HttpHelper.postAsyn(addDeviceUrl, head, params, callback);
    }

    /**
     * 删除设备
     *
     * @param deviceId
     * @param callback
     */
    public void deleteDevice(String deviceId, final HttpHelper.ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());

        String getDeviceMsgUrl = getDeviceUrl + deviceId;
        HttpHelper.deleteAsyn(getDeviceMsgUrl, head, null, callback);
    }

    /**
     * 获取设备详细信息
     *
     * @param deviceId
     * @param callback
     */
    public void getDeviceMsg(String deviceId, final HttpHelper.ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());

        String getDeviceMsgUrl = getDeviceUrl + deviceId;
        HttpHelper.getAsyn(getDeviceMsgUrl, head, callback);
    }

    /**
     * @param deviceId
     * @param callback
     */
    public void setDeviceMsg(String deviceId, String name, final HttpHelper.ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());

        Map<String, String> parms = new HashMap<>();
        parms.put("name", name);

        String getDeviceMsgUrl = getDeviceUrl + deviceId;
        HttpHelper.putAsyn(getDeviceMsgUrl, head, parms, callback);
    }

    /**
     * 设置设备扩展属性
     *
     * @param deviceId
     * @param property
     * @param callback
     */
    public void setDeviceProperty(String deviceId, PlaceBean property, final HttpHelper.ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("bulbsArray", property.getBulbsArray());
        params.put("sceneArray", property.getSceneArray());
        params.put("groupsArray", property.getGroupsArray());
        params.put("adminBean", property.getAdminBean());
        params.put("placeName", property.getPlaceName());
        params.put("createDate", property.getCreateDate());
        params.put("lastUseDate", property.getLastUseDate());
        params.put("placeVersion", property.getPlaceVersion());
        params.put("deviceIdRecord", property.getDeviceIdRecord());
        String url = getDeviceUrl + deviceId + "/property";
        HttpHelper.postAsyn(url, head, params, callback);
    }

    /**
     * 获取设备扩展属性
     *
     * @param deviceId
     * @param callback
     */
    public void getDeviceProperty(String deviceId, final HttpHelper.ResultCallback callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());

        String url = getDeviceUrl + deviceId + "/property";
        HttpHelper.getAsyn(url, head, callback);
    }

    /**
     * 删除设备扩展属性
     *
     * @param deviceId
     * @param key
     * @param callback
     */
    public void deleteDeviceProperty(String deviceId, String key, final HttpHelper.ResultCallback callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());

        String url = getDeviceUrl + deviceId + "/property/" + key;

        HttpHelper.deleteAsyn(url, head, null, callback);
    }

    /**
     * 获取设备订阅用户列表
     */
    public void getDeviceUserList(String uid, String deviceId, final HttpHelper.ResultCallback callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());

        String url = getDeviceSubUrl.replace("{user_id}", uid);
        url = url.replace("{device_id}", deviceId);
        HttpHelper.getAsyn(url, head, callback);
    }

    /**
     * 分享给其他用户
     *
     * @param deviceId        设备id
     * @param withUserAccount 对方用户账号
     * @param callback
     */
    public void shareWith(String deviceId, String withUserAccount, final HttpHelper.ResultCallback callback) {
        Map<String, String> head = new HashMap<>();
        Map<String, Object> parms = new HashMap<>();

        head.put("Access-Token", getAccessToken());

        parms.put("device_id", deviceId);
        parms.put("user", withUserAccount);
        parms.put("expire", "7200");
        parms.put("mode", "email");

        HttpHelper.postAsyn(shareWithUrl, head, parms, callback);
    }

    /**
     * 分享应答
     *
     * @param inviteCode
     * @param callback
     */
    public void setShareAccept(String inviteCode, final HttpHelper.ResultCallback callback) {
        Map<String, String> head = new HashMap<>();
        Map<String, Object> parms = new HashMap<>();

        head.put("Access-Token", getAccessToken());
        parms.put("invite_code", inviteCode);

        HttpHelper.postAsyn(shareAcceptUrl, head, parms, callback);
    }

    /**
     * 分享应答
     *
     * @param inviteCode
     * @param callback
     */
    public void setShareDeny(String inviteCode, final HttpHelper.ResultCallback callback) {
        Map<String, String> head = new HashMap<>();
        Map<String, Object> parms = new HashMap<>();

        head.put("Access-Token", getAccessToken());
        parms.put("invite_code", inviteCode);

        HttpHelper.postAsyn(shareDenyUrl, head, parms, callback);
    }

    /**
     * 获取所有分享列表
     *
     * @param callback
     */
    public void getAllShare(final HttpHelper.ResultCallback callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());
        HttpHelper.getAsyn(getAllShareUrl, head, callback);
    }

    /**
     * 回收莫一条分享
     *
     * @param inviteCode
     * @param callback
     */
    public void cancelShare(String inviteCode, final HttpHelper.ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        Map<String, Object> parms = new HashMap<>();
        head.put("Access-Token", getAccessToken());
        parms.put("invite_code", inviteCode);
        HttpHelper.postAsyn(cancelShareUrl, head, parms, callback);
    }

    /**
     * 删除分享记录
     *
     * @param inviteCode
     * @param callback
     */
    public void deleteShare(String inviteCode, final HttpHelper.ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Access-Token", getAccessToken());
        String url = deleteShareUrl + inviteCode;
        HttpHelper.deleteAsyn(url, head, null, callback);
    }

    private String getAccessToken() {
        if (UserUtil.getUser() != null) {
            return UserUtil.getUser().getAccessToken();
        }
        return "";
    }

    //{"error":{"code":4001007,"msg":"password error"}}
    public static class ErrorEntity {
        public Error getError() {
            return error;
        }

        public void setError(Error error) {
            this.error = error;
        }

        public int getCode() {
            return error.getCode();
        }

        public String getMsg() {
            return error.getMsg();
        }

        private Error error;

        public static class Error {
            private int code;
            private String msg;

            public void setCode(int code) {
                this.code = code;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public int getCode() {
                return code;
            }

            public String getMsg() {
                return msg;
            }
        }

    }
}
