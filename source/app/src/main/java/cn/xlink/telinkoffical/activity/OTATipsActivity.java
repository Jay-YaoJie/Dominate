package cn.xlink.telinkoffical.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/4/6.
 */
public class OTATipsActivity extends BaseActivity {

    @Bind(R.id.act_ota_tips_title)
    TitleBar titleBar;

    @Bind(R.id.act_ota_tips_button)
    TextView textView;

    private int mesh = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ota_tips);
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
        mesh = getIntent().getIntExtra(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH, -1);
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(getString(R.string.updata_tips));
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.act_ota_tips_button)
    void OnClick(){
        Intent intent = new Intent(OTATipsActivity.this, OtaUpdataActivity.class);
        intent.putExtra(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH, mesh);
        startActivity(intent);
        finish();
    }
}
