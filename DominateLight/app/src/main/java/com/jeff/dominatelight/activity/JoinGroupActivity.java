package com.jeff.dominatelight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.adapter.recycler_adapter.CommonAdapter;
import com.jeff.dominatelight.adapter.recycler_adapter.RecyclerViewHolder;
import com.jeff.dominatelight.manage.CmdManage;
import com.jeff.dominatelight.manage.DataToHostManage;
import com.jeff.dominatelight.model.Group;
import com.jeff.dominatelight.model.Groups;
import com.jeff.dominatelight.model.Light;
import com.jeff.dominatelight.model.Lights;
import com.jeff.dominatelight.utils.GroupsDbUtils;
import com.jeff.dominatelight.utils.TelinkCommon;
import com.jeff.dominatelight.view.TitleBar;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liucr on 2016/3/28.
 */
public class JoinGroupActivity extends BaseActivity {

    @BindView(R.id.act_joingroup_titlebar)
    TitleBar titleBar;

    @BindView(R.id.act_joingroup)
    RecyclerView recyclerView;

    private CommonAdapter<Group> commonAdapter;

    private List<Group> selectGroups = new ArrayList<>();

    private Light light;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_joingroup);
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
        Bundle bundle = getIntent().getExtras();
        int mesh = bundle.getInt(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH, -1);
        light = Lights.getInstance().getByMeshAddress(mesh);

        for(Group group : Groups.getInstance().get()){
            if(group.getMembers().contains(light.getLightSort().getMeshAddress().toString())){
                selectGroups.add(group);
            }
        }
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(R.string.join_group);
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
                for(Group group : Groups.getInstance().get()){
                    List<String> meshList = group.getMembers();
                    if(selectGroups.contains(group)){
                        CmdManage.allocDeviceGroup(group, light.getLightSort().getMeshAddress());
                        meshList.add(light.getLightSort().getMeshAddress().toString());
                        group.setMembers(meshList);
                    }else {
                        CmdManage.delGroupLight(group, light.getLightSort().getMeshAddress());
                        meshList.remove(light.getLightSort().getMeshAddress().toString());
                        group.setMembers(meshList);
                    }
                    GroupsDbUtils.getInstance().updataOrInsert(group.getGroupSort());
                }
                DataToHostManage.updataCurToHost();
                finish();
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        commonAdapter = new CommonAdapter<Group>(R.layout.view_common_item, Groups.getInstance().get()) {
            @Override
            public void convert(RecyclerViewHolder holder, final Group group, final int position) {
                ImageView selectImage = holder.getView(R.id.commom_right_image);
                View line = holder.getView(R.id.view_common_line);
                holder.setText(R.id.commom_center_text, group.getGroupSort().getName());
                holder.setImageResource(R.id.commom_left_image, R.mipmap.icon_bottom_group_select);
                if (selectGroups.contains(group)) {
                    selectImage.setVisibility(View.VISIBLE);
                } else {
                    selectImage.setVisibility(View.INVISIBLE);
                }

                if (position == Groups.getInstance().get().size() - 1) {
                    line.setVisibility(View.INVISIBLE);
                } else {
                    line.setVisibility(View.VISIBLE);
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                layoutParams.setMargins(0, 0, 0, 0);
                line.setLayoutParams(layoutParams);
            }

            @Override
            public void onItemClick(View v, int position) {
                super.onItemClick(v, position);

                if(position == 0){
                    return;
                }

                if(selectGroups.contains(Groups.getInstance().get().get(position))){
                    selectGroups.remove(Groups.getInstance().get().get(position));
                }else {
                    selectGroups.add(Groups.getInstance().get().get(position));
                }
                notifyDataSetChanged();
            }
        };

        recyclerView.setAdapter(commonAdapter);
    }
}
