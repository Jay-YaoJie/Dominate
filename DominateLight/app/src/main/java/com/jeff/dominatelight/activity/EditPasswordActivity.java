package com.jeff.dominatelight.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.http.HttpHelper;
import com.jeff.dominatelight.http.HttpManage;
import com.jeff.dominatelight.http.constant.HttpConstant;
import com.jeff.dominatelight.utils.UserUtil;
import com.jeff.dominatelight.view.TitleBar;
import com.squareup.okhttp.Request;



/**
 * Created by liucr on 2016/4/11.
 */
public class EditPasswordActivity extends BaseActivity {

    @BindView(R.id.act_edit_password_title)
    TitleBar titleBar;

    @BindView(R.id.edit_password_cur)
    EditText curEdit;

    @BindView(R.id.edit_password_new)
    EditText newEdit;

    @BindView(R.id.edit_password_enter)
    EditText enterEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_password);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(getString(R.string.edit_password));
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
                clickFinish();
            }
        });

        newEdit.addTextChangedListener(getWatcher(newEdit));
    }

    public void clickFinish() {

        String oldPassword = curEdit.getText().toString();
        String newPassword = newEdit.getText().toString();
        String enterPassword = enterEdit.getText().toString();


        if (TextUtils.isEmpty(oldPassword)) {
            showTipsDialog(getString(R.string.old_password_empty), getString(R.string.enter_1));
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            showTipsDialog(getString(R.string.new_password_empty), getString(R.string.enter_1));
            return;
        }

        if (oldPassword.length() > 16 || oldPassword.length() < 6) {
            showTipsDialog(getString(R.string.password_lenght_tips), getString(R.string.enter_1));
            return;
        }

        if (newPassword.length() > 16 || newPassword.length() < 6) {
            showTipsDialog(getString(R.string.password_lenght_tips), getString(R.string.enter_1));
            return;
        }

        if (!newPassword.equals(enterPassword)) {
            showTipsDialog(getString(R.string.change_password_difference), getString(R.string.enter_1));
            return;
        }

        resetPassword(oldPassword, newPassword);
    }

    private void resetPassword(String oldPassword, final String newPassword) {

        HttpManage.getInstance().resetPassword(oldPassword, newPassword, new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                showTipsDialog(getString(R.string.network_error), getString(R.string.enter));
            }

            @Override
            public void onResponse(final int code, final String response) {
                if (code == HttpConstant.PARAM_SUCCESS) {
                    UserUtil.getUser().setPassword(newPassword);
                    UserUtil.updataUser();
                    finish();
                } else {
                    HttpManage.ErrorEntity errorEntity = new Gson().fromJson(response, HttpManage.ErrorEntity.class);
                    String tips = getString(R.string.network_error);
                    switch (errorEntity.getCode()) {
                        case HttpConstant.ACCOUNT_VAILD_ERROR:
                        case HttpConstant.ACCOUNT_PASSWORD_ERROR:
                            tips = getString(R.string.change_password_cur_error);
                            break;
                        default:
                            break;
                    }
                    showTipsDialog(tips, getString(R.string.enter));
                }
            }
        });
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
