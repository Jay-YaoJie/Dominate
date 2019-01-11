package com.jeff.dominatelight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
 * Created by liucr on 2016/4/14.
 */
public class EditNickActivity extends BaseActivity {

    @BindView(R.id.rename_titlebar)
    TitleBar titleBar;

    @BindView(R.id.rename_tips)
    TextView tipsText;

    @BindView(R.id.rename_edit)
    EditText nameEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rename);
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
        titleBar.getCenterText().setText(getString(R.string.edit_nick));
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.getRightText().setText(getString(R.string.finish));
        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEdit.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    showTipsDialog(getString(R.string.create_tips2), getString(R.string.enter));
                    return;
                }


                if (name.length() > 16) {
                    showTipsDialog(getString(R.string.name_length_error), getString(R.string.enter));
                    return;
                }

                editName(UserUtil.getUser().getUid(), name);
            }
        });

        tipsText.setText(getString(R.string.create_tips2));
        nameEdit.setHint(UserUtil.getUser().getName());
    }

    private void editName(String uid, final String name) {
        HttpManage.getInstance().reName(uid, name, new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                showTipsDialog(getString(R.string.network_error), getString(R.string.enter));
            }

            @Override
            public void onResponse(int code, String response) {

                if (code == HttpConstant.PARAM_SUCCESS) {
                    UserUtil.getUser().setName(name);
                    UserUtil.updataUser();
                    finish();
                } else {
                    HttpManage.ErrorEntity errorEntity = new Gson().fromJson(response, HttpManage.ErrorEntity.class);
                    String tips = errorEntity.getMsg();
                    switch (errorEntity.getCode()) {
                        case HttpConstant.ACCOUNT_VAILD_ERROR:
                        case HttpConstant.ACCOUNT_PASSWORD_ERROR:
                            break;
                        case HttpConstant.SERVICE_EXCEPTION:
                            tips = getString(R.string.network_error);
                            break;
                        case HttpConstant.ACCESS_TOKEN_INVALID:
                            tips = getString(R.string.login_state_error);
                            break;
                        default:
                            tips = getString(R.string.network_error);
                            break;
                    }
                    showTipsDialog(tips, getString(R.string.enter));
                }
            }
        });
    }
}
