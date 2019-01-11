package com.jeff.dominatelight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.bean.greenDao.User;
import com.jeff.dominatelight.eventbus.PlacesUpataEvent;
import com.jeff.dominatelight.eventbus.StringEvent;
import com.jeff.dominatelight.http.HttpHelper;
import com.jeff.dominatelight.http.HttpManage;
import com.jeff.dominatelight.http.constant.HttpConstant;
import com.jeff.dominatelight.manage.DataToLocalManage;
import com.jeff.dominatelight.manage.PlaceManage;
import com.jeff.dominatelight.model.Lights;
import com.jeff.dominatelight.model.Places;
import com.jeff.dominatelight.utils.*;
import com.squareup.okhttp.Request;
import com.telink.util.Event;
import com.telink.util.EventListener;
import io.xlink.wifi.sdk.XlinkAgent;

import java.util.Map;


/**
 * Created by liucr on 2016/3/24.
 */
public class LoginActivity extends BaseActivity implements EventListener {

    @BindView(R.id.act_login_email)
    EditText emailEdit;

    @BindView(R.id.act_login_password)
    EditText passwordEdit;

    @BindView(R.id.act_login_password_visible)
    CheckBox checkBox;

    private boolean isClickLogin = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.bind(this);
        EventBusUtils.getInstance().addEventListener(StringEvent.StringEevent, this);
        EventBusUtils.getInstance().addEventListener(PlacesUpataEvent.PlacesUpataEvent, this);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
        EventBusUtils.getInstance().removeEventListener(this);
    }

    @Override
    protected void back() {
        finish();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        if (UserUtil.getUser() != null && !TextUtils.isEmpty(UserUtil.getUser().getAccount())) {
            emailEdit.setText(UserUtil.getUser().getAccount());
        }

        checkBox.setChecked(false);
        emailEdit.setSelection(emailEdit.getText().length());
        passwordEdit.setSelection(passwordEdit.getText().length());
    }

    @OnClick(R.id.act_login_create)
    void ClickCreate() {
        openActivity(CreateActivity.class);
    }

    @OnClick(R.id.act_login_forget)
    void ClickForget() {
        openActivity(ForgetPasswordActivity.class);
    }

    @OnCheckedChanged(R.id.act_login_password_visible)
    void CheckedVisible(boolean isChecked) {
        if (isChecked) {
            passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            checkBox.setBackgroundResource(R.mipmap.icon_password_visibility);
            passwordEdit.setSelection(passwordEdit.getText().length());
        } else {
            passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            checkBox.setBackgroundResource(R.mipmap.icon_password_invisibility);
            passwordEdit.setSelection(passwordEdit.getText().length());
        }
    }

    @OnClick(R.id.act_login_skip)
    void ClickSkip() {
        UserUtil.clearUser(true);
        if (UserUtil.getUser() != null && !TextUtils.isEmpty(UserUtil.getUser().getUid())) {
            String lastPlace = SharedPreferencesUtil.queryValue(TelinkCommon.CURPLACEMESH);
            PlaceManage.changePlaceByMesh(UserUtil.getUser().getUid(), lastPlace);
            PlaceManage.changeToCurUser();
            toMainOrFindActivity();
        } else {
            if (PlaceManage.changeToNoUser(this)) {
                openActivity(FindNewActivity.class);
            } else {
                toMainOrFindActivity();
            }
        }
        finish();
    }

    @OnClick(R.id.act_login_button)
    void ClickLogin() {
        String email = emailEdit.getText().toString();
        email = email.trim();
        String password = passwordEdit.getText().toString();

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            showTipsDialog(getString(R.string.email_password_empty_tips), getString(R.string.enter));
            return;
        }

        if (TextUtils.isEmpty(email)) {
            showTipsDialog(getString(R.string.email_empty_tips), getString(R.string.enter));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showTipsDialog(getString(R.string.password_empty_tips), getString(R.string.enter));
            return;
        }

        if (!XlinkUtils.checkEmail(email)) {
            showTipsDialog(getString(R.string.email_error_tips), getString(R.string.enter));
            return;
        }

        if (password.length() > 16 || password.length() < 6) {
            showTipsDialog(getString(R.string.password_error_tips), getString(R.string.enter));
            return;
        }

        login(email, password);
    }

    public void login(final String email, final String pwd) {
        if (isClickLogin) {
            return;
        }
        isClickLogin = true;
        showWaitingDialog(null);
        HttpManage.getInstance().login(email, pwd, new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                hideWaitingDialog();
                showTipsDialog(getString(R.string.network_error), getString(R.string.enter));
                isClickLogin = false;
            }

            @Override
            public void onResponse(int code, String response) {
                isClickLogin = false;
                if (code == HttpConstant.PARAM_SUCCESS) {
                    Map<String, Object> map = null;
                    try {
                        map = new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
                        }.getType());
                    } catch (JsonSyntaxException e) {
                        hideWaitingDialog();
                        showTipsDialog(getString(R.string.network_error), getString(R.string.enter));
                        return;
                    }

                    String uid = Double.valueOf(String.valueOf(map.get("user_id"))).intValue()+"";
                    String authKey = String.valueOf(map.get("authorize"));
                    String accessToken = String.valueOf(map.get("access_token"));
                    String refreshToken = String.valueOf(map.get("refresh_token"));
                    User user = new User();
                    user.setUid(uid);
                    user.setAuthKey(authKey);
                    user.setPassword(pwd);
                    user.setAccount(email);
                    user.setAccessToken(accessToken);
                    user.setRefreshToken(refreshToken);
                    UserUtil.setUser(user);

                    getUserMsg(uid);     //只是为了获取用户名/是否激活
                } else {
                    hideWaitingDialog();
                    HttpManage.ErrorEntity errorEntity = null;
                    String tips = getString(R.string.network_error);
                    try {
                        errorEntity = new Gson().fromJson(response, HttpManage.ErrorEntity.class);
                    } catch (JsonSyntaxException e) {
                        Log.e("liucr", "JsonSyntaxException: " + e.getMessage());
                    }
                    if (errorEntity != null) {
                        tips = errorEntity.getMsg();
                        switch (errorEntity.getCode()) {
                            case HttpConstant.ACCOUNT_VAILD_ERROR:
                            case HttpConstant.ACCOUNT_PASSWORD_ERROR:
                                tips = getString(R.string.email_or_password_error);
                                break;
                            case HttpConstant.USER_NOT_EXISTS:
                                tips = getString(R.string.user_not_exist);
                                break;
                            case HttpConstant.USER_LOCKED:
                                tips = getString(R.string.password_more_error);
                                break;
                            default:
                                tips = getString(R.string.network_error);
                                break;
                        }
                    }
                    showTipsDialog(tips, getString(R.string.enter));
                }
            }
        });

    }

    /**
     * 获取昵称
     */
    public void getUserMsg(String uid) {
        HttpManage.getInstance().getUserMsg(uid, new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                hideWaitingDialog();
                showTipsDialog(getString(R.string.network_error), getString(R.string.enter));
                isClickLogin = false;
            }

            @Override
            public void onResponse(int code, String response) {
                isClickLogin = false;
                hideWaitingDialog();
                Log.i("liucr", response);
                if (code == HttpConstant.PARAM_SUCCESS) {
                    Map<String, Object> map = new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
                    }.getType());

                    String nickname = (String) map.get("nickname");
                    boolean isVaild = (boolean) map.get("is_vaild");

                    if (!isVaild) {

                        showRegisterDialog(emailEdit.getText().toString().trim(), nickname, passwordEdit.getText().toString());

                        UserUtil.clearUser(false);
                    } else {
                        UserUtil.getUser().setName(nickname);
                        UserUtil.getUser().setIsVaild(isVaild);
                        UserUtil.updataUser();
                        getUserData();          //获取用户设备列表，分组等等
                    }
                } else {
                    showTipsDialog(getString(R.string.network_error), getString(R.string.enter));
                }
            }
        });
    }

    /**
     * 重新注册
     *
     * @param email
     * @param name
     * @param pwd
     */
    public void registerUser(final String email, final String name, final String pwd) {

        HttpManage.getInstance().registerUserByMail(email, name, pwd, new HttpHelper.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        hideWaitingDialog();
                        showTipsDialog(getString(R.string.network_error), getString(R.string.enter));
                    }

                    @Override
                    public void onResponse(final int code, final String response) {

                        if (code == HttpConstant.PARAM_SUCCESS) {
                            showSendSuccess(email);
                        } else {
                            HttpManage.ErrorEntity errorEntity = new Gson().fromJson(response, HttpManage.ErrorEntity.class);
                            String tips = errorEntity.getMsg();
                            switch (errorEntity.getCode()) {
                                case HttpConstant.ACCOUNT_VAILD_ERROR:
                                case HttpConstant.ACCOUNT_PASSWORD_ERROR:
                                    break;
                                case HttpConstant.REGISTER_EMAIL_EXISTS:
                                    tips = getString(R.string.user_had);
                                    break;
                                case HttpConstant.SERVICE_EXCEPTION:
                                    tips = getString(R.string.network_error);
                                    break;
                                default:
                                    break;
                            }
                            showTipsDialog(tips, getString(R.string.enter));
                        }
                    }

                }
        );

    }

    private void getUserData() {

        DataToLocalManage.getUserDeviceList();

    }

    /**
     * 弹出重新发送注册邮件提示
     *
     * @param email
     * @param name
     * @param pwd
     */
    private void showRegisterDialog(final String email, final String name, final String pwd) {
        tipsDialog.showDialogWithTips(getString(R.string.account_not_vaild),
                getString(R.string.create_not_received_tips2), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerUser(email, name, pwd);
                    }
                }, getString(R.string.enter_1), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideDialog();
                    }
                });
    }

    private void showSendSuccess(String account) {
        tipsDialog.showDialogWithTips(getString(R.string.send_success), getString(R.string.account_vaild_send, account), getString(R.string.enter_1),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tipsDialog.dismiss();
                    }
                });
    }

    @Override
    public void performed(Event event) {
        if (event.getType().equals(StringEvent.StringEevent)) {
            if (((StringEvent) event).getArgs().equals(TelinkCommon.STRING_ADDPLACE_SUCCESS)) {
                openActivity(FindNewActivity.class);
                finish();
            } else if (((StringEvent) event).getArgs().equals(TelinkCommon.STRING_ADDPLACE_ERROR)
                    || ((StringEvent) event).getArgs().equals(TelinkCommon.STRING_GETPLACE_ERROR)) {
                showTipsDialog(getString(R.string.network_error), getString(R.string.enter));
                UserUtil.clearUser(false);
            }
        } else if (event.getType().equals(PlacesUpataEvent.PlacesUpataEvent)) {
            if (Places.getInstance().getCurPlaceSort() == null) {
                PlaceManage.changePlaceByMesh(UserUtil.getUser().getUid(), ((PlacesUpataEvent) event).getArgs().getMeshAddress());
            }

            if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
                XlinkAgent.getInstance().start();
            }
            LogUtil.e("XlinkAgent Login : " + XlinkAgent.getInstance().login(Integer.parseInt(UserUtil.getUser().getUid()),
                    UserUtil.getUser().getAuthKey()));

            toMainOrFindActivity();
            finish();
        }
    }

    private void toMainOrFindActivity() {
        if (Lights.getInstance().get().size() == 0 && !Places.getInstance().curPlaceIsShare()) {
            openActivity(FindNewActivity.class);
        } else {
            openActivity(MainActivity.class);
        }
    }
}
