package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.model.Scene;
import cn.xlink.telinkoffical.model.Scenes;
import cn.xlink.telinkoffical.utils.LightsDbUtils;
import cn.xlink.telinkoffical.utils.ScenesDbUtils;
import cn.xlink.telinkoffical.view.TitleBar;
import cn.xlink.telinkoffical.view.togglebutton.zcw.togglebutton.ToggleButton;

/**
 * Created by liucr on 2016/4/5.
 */
public class HomeDeviceActivity extends BaseActivity {

    @Bind(R.id.act_recylerview_title)
    TitleBar titleBar;

    @Bind(R.id.act_recylerview)
    RecyclerView recyclerView;

    @Bind(R.id.act_recyclerview_empty)
    View noDeviceView;

    @Bind(R.id.act_recyclerview_empty_image)
    ImageView emptyImage;

    @Bind(R.id.act_recyclerview_empty_text)
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
