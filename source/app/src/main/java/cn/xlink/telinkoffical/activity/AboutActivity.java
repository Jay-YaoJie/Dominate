package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/4/21.
 */
public class AboutActivity extends BaseActivity {

    @Bind(R.id.act_about_title)
    TitleBar titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_about);
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
        titleBar.getCenterText().setText(getString(R.string.edit_about));
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
