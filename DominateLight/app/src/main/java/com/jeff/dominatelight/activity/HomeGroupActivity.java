package com.jeff.dominatelight.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.adapter.recycler_adapter.CommonAdapter;
import com.jeff.dominatelight.adapter.recycler_adapter.RecyclerViewHolder;
import com.jeff.dominatelight.manage.DataToHostManage;
import com.jeff.dominatelight.model.Group;
import com.jeff.dominatelight.model.Groups;
import com.jeff.dominatelight.utils.GroupsDbUtils;
import com.jeff.dominatelight.view.TitleBar;
import com.jeff.dominatelight.view.togglebutton.zcw.togglebutton.ToggleButton;


/**
 * Created by liucr on 2016/4/5.
 */
public class HomeGroupActivity extends BaseActivity {

    @BindView(R.id.act_recylerview_title)
    TitleBar titleBar;

    @BindView(R.id.act_recylerview)
    RecyclerView recyclerView;

    private CommonAdapter<Group> commonAdapter;

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
        titleBar.getCenterText().setText(getString(R.string.manage_home_group));
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
        commonAdapter = new CommonAdapter<Group>(R.layout.item_manage_home, Groups.getInstance().get()) {
            @Override
            public void convert(RecyclerViewHolder holder, final Group group, int position) {
                holder.setImageResource(R.id.item_manage_left, R.mipmap.icon_bottom_group_select);
                holder.setText(R.id.item_manage_center, group.getGroupSort().getName());

                ToggleButton toggleButton = holder.getView(R.id.item_manage_right);
                if(group.getGroupSort().getIsShowOnHomeScreen()){
                    toggleButton.setToggleOn();
                }else {
                    toggleButton.setToggleOff();
                }

                toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                    @Override
                    public void onToggle(boolean on) {
                        group.getGroupSort().setIsShowOnHomeScreen(on);
                        GroupsDbUtils.getInstance().updataOrInsert(group.getGroupSort());
                    }
                });
            }
        };
        recyclerView.setAdapter(commonAdapter);
    }
}
