package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.bean.greenDao.PlaceSort;
import cn.xlink.telinkoffical.http.HttpHelper;
import cn.xlink.telinkoffical.http.HttpManage;
import cn.xlink.telinkoffical.http.constant.HttpConstant;
import cn.xlink.telinkoffical.utils.PlacesDbUtils;
import cn.xlink.telinkoffical.utils.UserUtil;
import cn.xlink.telinkoffical.utils.XlinkUtils;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/4/8.
 */
public class ShareToActivity extends BaseActivity {

    @Bind(R.id.act_share_titlebar)
    TitleBar titleBar;

    @Bind(R.id.act_shareto_edit)
    EditText editText;

    private boolean isClick = false;

    PlaceSort placeSort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_shareto);
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
        placeSort = PlacesDbUtils.getInstance().getPlaceByCreatorId(UserUtil.getUser().getUid()).get(0);
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(getString(R.string.share));
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.getRightText().setText(getString(R.string.send));
        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = editText.getText().toString();
                if (TextUtils.isEmpty(account)) {
                    showTipsDialog(getString(R.string.share_tips), getString(R.string.enter));
                    return;
                }

                if (!XlinkUtils.checkEmail(account)) {
                    showTipsDialog(getString(R.string.share_to_error_tips_1), getString(R.string.enter));
                    return;
                }

                if (account.equals(UserUtil.getUser().getAccount())) {
                    showTipsDialog(getString(R.string.share_account_error_tips), getString(R.string.enter));
                    return;
                }

                shareWith(placeSort.getPlaceId(), account);
            }
        });
    }

    public void shareWith(String placeId, String account) {
        if (isClick) {
            return;
        }
        isClick = true;

        HttpManage.getInstance().shareWith(placeId, account, new HttpHelper.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                isClick = false;
                showTipsDialog(getString(R.string.network_error), getString(R.string.enter));
            }

            @Override
            public void onResponse(int code, String response) {

                if (code == 200) {
                    showSuccess();
                } else {
                    HttpManage.ErrorEntity errorEntity = new Gson().fromJson(response, HttpManage.ErrorEntity.class);
                    if (errorEntity != null) {
                        String tips = errorEntity.getMsg();
                        if (errorEntity.getCode() == HttpConstant.SERVICE_EXCEPTION) {
                            tips = getString(R.string.network_error);
                        } else if (errorEntity.getCode() == HttpConstant.SERVICE_EXCEPTION) {
                            tips = getString(R.string.network_error);
                        } else if (errorEntity.getCode() == HttpConstant.USER_NOT_EXISTS) {
                            tips = getString(R.string.share_to_error_tips_2);
                        } else if (errorEntity.getCode() == HttpConstant.ACCESS_TOKEN_INVALID) {
                            tips = getString(R.string.share_to_error_tips_3);
                        }
                        showTipsDialog(tips, getString(R.string.enter));
                    }
                }
                isClick = false;
            }
        });
    }

    private void showSuccess() {
        showOneButtonDialog(getString(R.string.share_to_success_tips_1), false, getString(R.string.enter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
