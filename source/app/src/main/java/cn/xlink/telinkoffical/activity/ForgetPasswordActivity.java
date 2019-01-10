package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.Request;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.http.HttpHelper;
import cn.xlink.telinkoffical.http.HttpManage;
import cn.xlink.telinkoffical.http.constant.HttpConstant;
import cn.xlink.telinkoffical.utils.XlinkUtils;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/3/24.
 */
public class ForgetPasswordActivity extends BaseActivity {

    @Bind(R.id.act_forget_titlebar)
    TitleBar titleBar;

    @Bind(R.id.act_forget_edit)
    EditText editText;

    @Bind(R.id.view_email_send)
    View sendView;

    @Bind(R.id.view_email_title)
    TextView emailTitle;

    @Bind(R.id.view_email_msg)
    TextView emailMsg;

    @Bind(R.id.view_email_resend)
    TextView resend;

    @Bind(R.id.view_email_button)
    TextView button;

    private boolean isClick = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_forget_password);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

        titleBar.getCenterText().setText(getString(R.string.forget_password_title));
        titleBar.getRightText().setText(getString(R.string.finish));
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editText.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    showTipsDialog(getString(R.string.email_empty_tips), getString(R.string.enter_1));
                    return;
                }

                if (!XlinkUtils.checkEmail(email)) {
                    showTipsDialog(getString(R.string.email_error_tips), getString(R.string.enter_1));
                    return;
                }

                forgetPasswd(email);
            }
        });

        emailTitle.setText(getString(R.string.forget_password_title));
        resend.setText(Html.fromHtml("2." + "<u>" + "<font color=\"#009cff\">" + getString(R.string.resend_email) + "</font>" + "</u>"));
        button.setText(getString(R.string.finish));
    }

    @OnClick(R.id.view_email_button)
    void ClickSendBack() {
        finish();
    }

    @OnClick(R.id.view_email_resend)
    void ClickResend() {
        showTipsDialog(getString(R.string.resend_create_email_tips), getString(R.string.enter));

        forgetPasswd(editText.getText().toString());
    }

    public void forgetPasswd(final String email) {
        if(isClick){
            return;
        }
        isClick = true;
        showWaitingDialog(null);
        HttpManage.getInstance().forgetPasswd(email, new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                isClick = false;
                hideWaitingDialog();
                showTipsDialog(getString(R.string.network_error), getString(R.string.enter_1));
            }

            @Override
            public void onResponse(final int code, String response) {
                isClick = false;
                hideWaitingDialog();
                if (code == HttpConstant.PARAM_SUCCESS) {
                    showSuccessView(email);
                } else {
                    HttpManage.ErrorEntity errorEntity = null;
                    String tips = getString(R.string.network_error);
                    try {
                        errorEntity = new Gson().fromJson(response, HttpManage.ErrorEntity.class);
                    } catch (JsonSyntaxException e) {
                        Log.e("liucr", "JsonSyntaxException: " + e.getMessage());
                    }
                    switch (errorEntity.getCode()){
                        case HttpConstant.USER_EMAIL_NOT_VAILD:
                            tips = getString(R.string.forget_account_not_vaild);
                            break;
                        case HttpConstant.USER_NOT_EXISTS:
                            tips = getString(R.string.user_not_exist);
                            break;
                    }
                    showTipsDialog(tips, getString(R.string.enter_1));
                }
            }
        });
    }

    private void showSuccessView(String email) {
        emailMsg.setText(Html.fromHtml(getString(R.string.forget_password_email_tips) + "<font color=\"#009cff\">" + email + "</font>"));
        sendView.setVisibility(View.VISIBLE);
    }
}
