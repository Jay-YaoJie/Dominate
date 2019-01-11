package com.jeff.dominatelight.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.adapter.recycler_adapter.CommonAdapter;
import com.jeff.dominatelight.adapter.recycler_adapter.RecyclerViewHolder;
import com.jeff.dominatelight.manage.DataToHostManage;
import com.jeff.dominatelight.model.Scene;
import com.jeff.dominatelight.model.Scenes;
import com.jeff.dominatelight.utils.ScenesDbUtils;
import com.jeff.dominatelight.view.TitleBar;
import com.jeff.dominatelight.view.togglebutton.zcw.togglebutton.ToggleButton;


/**
 * Created by liucr on 2016/4/5.
 */
public class HomeSceneActivity extends BaseActivity {

    @BindView(R.id.act_recylerview_title)
    TitleBar titleBar;

    @BindView(R.id.act_recylerview)
    RecyclerView recyclerView;

    private CommonAdapter<Scene> commonAdapter;

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
        titleBar.getCenterText().setText(getString(R.string.manage_home_scene));
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
        commonAdapter = new CommonAdapter<Scene>(R.layout.item_manage_home, Scenes.getInstance().get()) {
            @Override
            public void convert(RecyclerViewHolder holder, final Scene scene, int position) {
                holder.setImageResource(R.id.item_manage_left, scene.getManageIcon());
                holder.setText(R.id.item_manage_center, scene.getSceneSort().getName());

                ToggleButton toggleButton = holder.getView(R.id.item_manage_right);
                if(scene.getSceneSort().getIsShowOnHomeScreen()){
                    toggleButton.setToggleOn();
                }else {
                    toggleButton.setToggleOff();
                }

                toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                    @Override
                    public void onToggle(boolean on) {
                        scene.getSceneSort().setIsShowOnHomeScreen(on);
                        ScenesDbUtils.getInstance().updataOrInsert(scene.getSceneSort());
                    }
                });
            }
        };
        recyclerView.setAdapter(commonAdapter);
    }
}
