package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.model.Group;
import cn.xlink.telinkoffical.model.Groups;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.utils.GroupsDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/3/28.
 */
public class JoinGroupActivity extends BaseActivity {

    @Bind(R.id.act_joingroup_titlebar)
    TitleBar titleBar;

    @Bind(R.id.act_joingroup)
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
        ButterKnife.unbind(this);
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
