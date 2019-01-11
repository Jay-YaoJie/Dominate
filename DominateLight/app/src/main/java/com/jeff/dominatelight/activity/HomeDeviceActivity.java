package com.jeff.dominatelight.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.adapter.recycler_adapter.CommonAdapter;
import com.jeff.dominatelight.adapter.recycler_adapter.RecyclerViewHolder;
import com.jeff.dominatelight.manage.DataToHostManage;
import com.jeff.dominatelight.model.Light;
import com.jeff.dominatelight.model.Lights;
import com.jeff.dominatelight.utils.LightsDbUtils;
import com.jeff.dominatelight.view.TitleBar;
import com.jeff.dominatelight.view.togglebutton.zcw.togglebutton.ToggleButton;


/**
 * Created by liucr on 2016/4/5.
 */
public class HomeDeviceActivity extends BaseActivity {

    @BindView(R.id.act_recylerview_title)
    TitleBar titleBar;

    @BindView(R.id.act_recylerview)
    RecyclerView recyclerView;

    @BindView(R.id.act_recyclerview_empty)
    View noDeviceView;

    @BindView(R.id.act_recyclerview_empty_image)
    ImageView emptyImage;

    @BindView(R.id.act_recyclerview_empty_text)
    TextView emptyText;

    private CommonAdapter<Light> commonAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recyclerview);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(getString(R.string.manage_home_device));
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataToHostManage.updataCurToHost();
                finish();
            }
        });

        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataToHostManage.updataCurToHost();
                finish();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        commonAdapter = new CommonAdapter<Light>(R.layout.item_manage_home, Lights.getInstance().get()) {
            @Override
            public void convert(RecyclerViewHolder holder, final Light light, int position) {
                holder.setImageResource(R.id.item_manage_left, R.mipmap.icon_device);
                holder.setText(R.id.item_manage_center, light.getLightSort().getName());

                ToggleButton toggleButton = holder.getView(R.id.item_manage_right);
                if (light.getLightSort().getIsShowOnHomeScreen()) {
                    toggleButton.setToggleOn();
                } else {
                    toggleButton.setToggleOff();
                }

                toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                    @Override
                    public void onToggle(boolean on) {
                        light.getLightSort().setIsAlone(false);
                        light.getLightSort().setIsShowOnHomeScreen(on);
                        LightsDbUtils.getInstance().updataOrInsert(light.getLightSort());
                    }
                });
            }
        };
        recyclerView.setAdapter(commonAdapter);

        if(Lights.getInstance().get().size() == 0){
            emptyImage.setImageResource(R.mipmap.icon_empty_devicelist);
            emptyText.setText(getString(R.string.empty_devicelist));
            noDeviceView.setVisibility(View.VISIBLE);
        }
    }
}
