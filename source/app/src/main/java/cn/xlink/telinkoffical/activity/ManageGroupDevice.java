package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telink.bluetooth.light.ConnectionStatus;

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
public class ManageGroupDevice extends BaseActivity {

    @Bind(R.id.manage_titlebar)
    TitleBar titleBar;

    @Bind(R.id.manage_tips)
    TextView textTips;

    @Bind(R.id.manage_list)
    RecyclerView recyclerView;

    private Group group;

    private List<Light> lightList;

    private List<Light> selectlightList;

    private CommonAdapter<Light> commonAdapter;

    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_manage);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        handler = null;
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        int mesh = bundle.getInt(TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH, -1);
        group = Groups.getInstance().getByMeshAddress(mesh);

        lightList = new ArrayList<>();
        if (Groups.getInstance().get().size() < 8) {
            for (Light light : Lights.getInstance().get()) {
                lightList.add(light);
            }
        } else {
            for (Light light : Lights.getInstance().get()) {
                if (light.getGroups().size() >= 8) {
                    if (light.getGroups().contains(group.getGroupSort().getMeshAddress())) {
                        lightList.add(light);
                    }
                } else {
                    lightList.add(light);
                }
            }
        }

        selectlightList = new ArrayList<>();
        for (String meshaddress : group.getMembers()) {
            Light light = Lights.getInstance().getByMeshAddress(Integer.parseInt(meshaddress));
            if (light != null) {
                selectlightList.add(light);
            }
        }
    }

    @Override
    protected void initView() {

        titleBar.getCenterText().setText(R.string.manage_group_member);
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
                showWaitingDialog(null);
                final List<String> meshList = group.getMembers();

                for (int i = 0; i < lightList.size(); i++) {
                    final Light light = lightList.get(i);
                    if (selectlightList.contains(light)) {
                        if (!meshList.contains(light.getLightSort().getMeshAddress().toString())) {

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CmdManage.allocDeviceGroup(group, light.getLightSort().getMeshAddress());
                                }
                            }, i * CmdManage.sendDelay);

                            if (light.status != ConnectionStatus.OFFLINE) {
                                meshList.add(light.getLightSort().getMeshAddress().toString());
                            }
                        }
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CmdManage.delGroupLight(group, light.getLightSort().getMeshAddress());
                            }
                        }, i * CmdManage.sendDelay);

                        meshList.remove(light.getLightSort().getMeshAddress().toString());
                    }
                }


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        group.setMembers(meshList);
                        GroupsDbUtils.getInstance().updataOrInsert(group.getGroupSort());
                        DataToHostManage.updataCurToHost();
                        finish();
                    }
                }, (lightList.size() + 2) * CmdManage.sendDelay);

            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        commonAdapter = new CommonAdapter<Light>(R.layout.view_common_item, lightList) {
            @Override
            public void convert(RecyclerViewHolder holder, final Light light, final int position) {
                ImageView selectImage = holder.getView(R.id.commom_right_image);
                View line = holder.getView(R.id.view_common_line);
                holder.setText(R.id.commom_center_text, light.getLightSort().getName());
                holder.setImageResource(R.id.commom_left_image, light.getSelectIcon());
                if (selectlightList.contains(light)) {
                    selectImage.setVisibility(View.VISIBLE);
                } else {
                    selectImage.setVisibility(View.INVISIBLE);
                }

                if (position == lightList.size() - 1) {
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
                if (lightList.get(position).status == ConnectionStatus.OFFLINE) {
                    return;
                }
                if (selectlightList.contains(lightList.get(position))) {
                    selectlightList.remove(lightList.get(position));
                } else if (lightList.get(position).status != ConnectionStatus.OFFLINE) {
                    selectlightList.add(lightList.get(position));
                }
                notifyDataSetChanged();
            }
        };

        recyclerView.setAdapter(commonAdapter);
    }
}
