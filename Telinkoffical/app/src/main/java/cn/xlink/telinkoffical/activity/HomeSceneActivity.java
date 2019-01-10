package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.model.Group;
import cn.xlink.telinkoffical.model.Groups;
import cn.xlink.telinkoffical.model.Scene;
import cn.xlink.telinkoffical.model.Scenes;
import cn.xlink.telinkoffical.utils.GroupsDbUtils;
import cn.xlink.telinkoffical.utils.ScenesDbUtils;
import cn.xlink.telinkoffical.view.TitleBar;
import cn.xlink.telinkoffical.view.togglebutton.zcw.togglebutton.ToggleButton;

/**
 * Created by liucr on 2016/4/5.
 */
public class HomeSceneActivity extends BaseActivity {

    @Bind(R.id.act_recylerview_title)
    TitleBar titleBar;

    @Bind(R.id.act_recylerview)
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
