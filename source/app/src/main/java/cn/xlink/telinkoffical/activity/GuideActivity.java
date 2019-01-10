package cn.xlink.telinkoffical.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;

import java.util.Map;

import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.http.HttpHelper;
import cn.xlink.telinkoffical.http.HttpManage;
import cn.xlink.telinkoffical.http.constant.HttpConstant;
import cn.xlink.telinkoffical.manage.PlaceManage;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.utils.LogUtil;
import cn.xlink.telinkoffical.utils.SharedPreferencesUtil;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.utils.UserUtil;
import cn.xlink.telinkoffical.utils.XlinkUtils;
import io.xlink.wifi.sdk.XlinkAgent;

/**
 * Created by liucr on 2016/3/24.
 */
public class GuideActivity extends BaseActivity {

    private String lastMesh;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.act_guide);
        XlinkAgent.getInstance().start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lastMesh = SharedPreferencesUtil.queryValue(TelinkCommon.CURPLACEMESH);
        if (UserUtil.getUser() == null && TextUtils.isEmpty(lastMesh)) {        //用户第一登录
            openActivity(LoginActivity.class);
            finish();
        } else if (UserUtil.getUser() == null) {       //没登陆过使用过跳过模式
//            if(PlaceManage.changePlaceByMesh(UserUtil.NoMaster, lastMesh) == 1) {
//                openActivity(LoginActivity.class);
//            }else {
//                openActivity(MainActivity.class);
//            }
            openActivity(LoginActivity.class);
            finish();
        } else if (!UserUtil.isLogin() && !TextUtils.isEmpty(UserUtil.getUser().getUid())) {     //登出了
//            if(PlaceManage.changePlaceByMesh(UserUtil.getUser().getUid(), lastMesh) == 1) {
//                openActivity(LoginActivity.class);
//            }else {
//                PlaceManage.changeToCurUser();
//                openActivity(MainActivity.class);
//            }
            openActivity(LoginActivity.class);
            finish();
        }else {         //登录状态
            initUser();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    //只是为了拿accessToken
    public void initUser() {

        if (UserUtil.getUser() != null && UserUtil.isGetAccessToken()) {
            HttpManage.getInstance().login(UserUtil.getUser().getAccount(), UserUtil.getUser().getPassword(),
                    new HttpHelper.ResultCallback<String>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            Log.i("liucr", "request:　" + request + " : " + e.getMessage());
                            openActivity(LoginActivity.class);
                            finish();
                        }

                        @Override
                        public void onResponse(int code, String response) {
                            if (code == HttpConstant.PARAM_SUCCESS) {
                                Map<String, Object> map = new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                String accessToken = map.get("access_token").toString();
                                String refreshToken = map.get("refresh_token").toString();
                                UserUtil.getUser().setAccessToken(accessToken);
                                UserUtil.getUser().setRefreshToken(refreshToken);
                                UserUtil.updataUser();

                                if(PlaceManage.changePlaceByMesh(UserUtil.getUser().getUid(), lastMesh) == 1){
                                    openActivity(LoginActivity.class);
                                }else {
                                    Log.i("liucr","UserUtil: "+ UserUtil.getUser().getUid());
                                    LogUtil.e("XlinkAgent Login : "+XlinkAgent.getInstance().login(Integer.parseInt(UserUtil.getUser().getUid()),
                                            UserUtil.getUser().getAuthKey()));

                                    PlaceManage.changeToCurUser();
                                    openActivity(MainActivity.class);
                                }
                            } else {
                                openActivity(LoginActivity.class);
                            }
                            finish();
                        }
                    });
        }
    }
}
