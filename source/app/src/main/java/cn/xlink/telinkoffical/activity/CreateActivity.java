package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.telink.util.Event;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.http.HttpHelper;
import cn.xlink.telinkoffical.http.HttpManage;
import cn.xlink.telinkoffical.http.constant.HttpConstant;
import cn.xlink.telinkoffical.utils.XlinkUtils;

/**
 * Created by liucr on 2016/3/24.
 */
public class CreateActivity extends BaseActivity {

    @Bind(R.id.create_email)
    EditText emailEdit;

    @Bind(R.id.create_nickname)
    EditText nicknameEdit;

    @Bind(R.id.create_password)
    EditText passwordEdit;

    @Bind(R.id.create_password_visible)
    CheckBox checkBox;

    @Bind(R.id.view_email_send)
    View sendView;

    @Bind(R.id.view_email_msg)
    TextView emailMsg;

    @Bind(R.id.view_email_resend)
    TextView resendTips;

    private boolean isSend = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

        resendTips.setText(Html.fromHtml("2." + "<u>" + "<font color=\"#009cff\">" + getString(R.string.create_not_received_tips2) + "</font>" + "</u>"));
        checkBox.setChecked(false);
        passwordEdit.addTextChangedListener(getWatcher(passwordEdit));
    }

    @OnClick(R.id.create_button)
    void ClickButton() {
        String email = emailEdit.getText().toString();
        String name = nicknameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if(TextUtils.isEmpty(email)){
            showTipsDialog(getString(R.string.create_tips1), getString(R.string.enter_1));
            return;
        }

        if(TextUtils.isEmpty(name)){
            showTipsDialog(getString(R.string.create_tips2), getString(R.string.enter_1));
            return;
        }

        if(TextUtils.isEmpty(password)){
            showTipsDialog(getString(R.string.create_tips3), getString(R.string.enter_1));
            return;
        }

        if (!XlinkUtils.checkEmail(email)) {
            showTipsDialog(getString(R.string.email_error_tips), getString(R.string.enter_1));
            return;
        }

        if(name.length()>16){
            showTipsDialog(getString(R.string.name_length_error), getString(R.string.enter_1));
            return;
        }

        if(password.length()>16 || password.length()<6){
            showTipsDialog(getString(R.string.password_lenght_tips), getString(R.string.enter_1));
            return;
        }

        registerUser(email,name, password);
    }

    @OnClick(R.id.create_login_back)
    void Clickback() {
        finish();
    }

    @OnClick(R.id.view_email_button)
    void ClickSendBack() {
        finish();
    }

    @OnCheckedChanged(R.id.create_password_visible)
    void CheckedVisible(boolean isChecked) {
        if (isChecked) {
            passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            checkBox.setBackgroundResource(R.mipmap.icon_password_visibility);
        } else {
            passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            checkBox.setBackgroundResource(R.mipmap.icon_password_invisibility);
        }
    }

    @OnClick(R.id.view_email_resend)
    void ClickResend(){
        showTipsDialog(getString(R.string.resend_create_email_tips), getString(R.string.enter));

        registerUser(emailEdit.getText().toString(),
                nicknameEdit.getText().toString(), passwordEdit.getText().toString());
    }

    @OnClick(R.id.view_email_send)
    void ClickSuccess(){

    }

    public void registerUser(final String email, final String name, final String pwd) {
        if (isSend) {
            return;
        }
        isSend = true;
        showWaitingDialog(null);
        HttpManage.getInstance().registerUserByMail(email, name, pwd, new HttpHelper.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        isSend = false;
                        hideWaitingDialog();
                        showTipsDialog(getString(R.string.network_error), getString(R.string.enter_1));
                    }

                    @Override
                    public void onResponse(final int code, final String response) {
                        isSend = false;
                        hideWaitingDialog();
                        if (code == HttpConstant.PARAM_SUCCESS) {
                            showSuccessView(email);
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
                            showTipsDialog(tips,getString(R.string.enter_1));
                        }
                    }

                }
        );

    }

    private void showSuccessView(String email){
        emailMsg.setText(Html.fromHtml(getString(R.string.create_send_tips1) + "<font color=\"#009cff\">" + email + "</font>" + getString(R.string.create_send_tips2)));
        sendView.setVisibility(View.VISIBLE);
    }

    public TextWatcher getWatcher(final EditText editText) {
        TextWatcher watcher = new TextWatcher() {
            String tmp = "";
            String digits = getResources().getString(R.string.register_name_digits);

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tmp = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.equals(tmp)) {
                    return;
                }

                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < str.length(); i++) {
                    if (digits.indexOf(str.charAt(i)) >= 0) {
                        sb.append(str.charAt(i));
                    }
                }
                tmp = sb.toString();
                editText.setText(tmp);
                editText.setSelection(tmp.length());
            }
        };
        return watcher;
    }
}