package cn.xlink.telinkoffical.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.bean.greenDao.GroupSort;
import cn.xlink.telinkoffical.bean.greenDao.LightSort;
import cn.xlink.telinkoffical.eventbus.GetDeviceGoupEvent;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.model.Group;
import cn.xlink.telinkoffical.model.Groups;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.utils.EventBusUtils;
import cn.xlink.telinkoffical.utils.GroupsDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/3/26.
 */
public class NewGroupActivity extends BaseActivity implements EventListener<String> {

    public static final int delay = 1000;

    @Bind(R.id.act_forget_titlebar)
    TitleBar titleBar;

    @Bind(R.id.new_event_tips)
    TextView tips;

    @Bind(R.id.new_event_edit)
    EditText groupName;

    @Bind(R.id.new_event_list)
    RecyclerView recyclerView;

    private CommonAdapter<Light> commonAdapter;

    private List<Light> lightList;

    private List<Light> selectLightList;

    private List<String> successMeshs;

    private Group group;

    //
    private boolean isSecondTime = false;

    private int curSelect = 0;

    private long times = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            delayHand.removeCallbacks(runnableDelay);
            if (msg.what == 0) {          //成功
                if (!successMeshs.contains(selectLightList.get(curSelect).getLightSort().getMeshAddress() + "")) {
                    successMeshs.add(selectLightList.get(curSelect).getLightSort().getMeshAddress() + "");
                }
                if (curSelect == selectLightList.size() - 1) {
                    saveAndFinish();
                } else {
                    curSelect++;
                    CmdManage.allocDeviceGroup(group, selectLightList.get(curSelect).getLightSort().getMeshAddress());
                    isSecondTime = false;
                    times = System.currentTimeMillis();
                    delayHand.postDelayed(runnableDelay, delay);
                }
            } else if (msg.what == 1) {       //第一次失败
                CmdManage.allocDeviceGroup(group, selectLightList.get(curSelect).getLightSort().getMeshAddress());
                isSecondTime = true;
                times = System.currentTimeMillis();
                delayHand.postDelayed(runnableDelay, delay);
            } else if (msg.what == 2) {                 //失败了
                if (curSelect >= selectLightList.size() - 1) {
                    saveAndFinish();
                } else {
                    //失败了也添加进去。。。
                    successMeshs.add(selectLightList.get(curSelect).getLightSort().getMeshAddress() + "");

                    curSelect++;
                    CmdManage.allocDeviceGroup(group, selectLightList.get(curSelect).getLightSort().getMeshAddress());
                    isSecondTime = false;
                    times = System.currentTimeMillis();
                    delayHand.postDelayed(runnableDelay, delay);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_new_event);
        MyApp.getApp().addEventListener(NotificationEvent.GET_GROUP, this);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        MyApp.getApp().removeEventListener(this);
        super.onDestroy();
    }

    @Override
    protected void initData() {
        lightList = new ArrayList<>();
        selectLightList = new ArrayList<>();
        successMeshs = new ArrayList<>();
        if(Groups.getInstance().get().size()<8){
            for (Light light : Lights.getInstance().get()) {
                lightList.add(light);
            }
        }else {
            for (Light light : Lights.getInstance().get()) {
                if(light.getGroups().size()<8){
                    lightList.add(light);
                }
            }
        }

        //初始化组
        for (int i = 0x8002; i < 0x80FF; i++) {
            boolean unExist = true;
            for (int t = 0; t < Groups.getInstance().get().size(); t++) {
                if (Groups.getInstance().get().get(t).getGroupSort().getMeshAddress() == i) {
                    unExist = false;
                    break;
                } else {
                    unExist = true;
                }
            }
            if (unExist) {
                group = new Group(new GroupSort());
                group.getGroupSort().setIsShowOnHomeScreen(true);
                group.getGroupSort().setBrightness(0);
                group.getGroupSort().setColor(0);
                group.getGroupSort().setMeshAddress(i);
                group.getGroupSort().setTemperature(0);
                break;
            }
        }
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(R.string.new_group_title);
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

                if (TextUtils.isEmpty(groupName.getText().toString())) {
                    showTipsDialog(getString(R.string.group_name_empty_tips), getString(R.string.enter));
                    return;
                }

                if(Groups.checkNameHad(groupName.getText().toString(), "")){
                    showTipsDialog(getString(R.string.group_name_had), getString(R.string.enter));
                    return;
                }

                if (selectLightList.size() == 0) {
                    showTipsDialog(getString(R.string.devices_empty_tips), getString(R.string.enter));
                    return;
                }

                showWaitingDialog(getString(R.string.operating));

//                for(int i = 0; i< selectLightList.size(); i++){
//                    final Light light = selectLightList.get(i);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            successMeshs.add(light.getLightSort().getMeshAddress()+"");
//                            CmdManage.allocDeviceGroup(group, light.getLightSort().getMeshAddress());
//                            if(light.getLightSort().getMeshAddress() == selectLightList.get(selectLightList.size()-1).getLightSort().getMeshAddress()){
//                                saveAndFinish();
//                            }
//                        }
//                    }, CmdManage.sendDelay*i);
//                }

                //等待回调确认已添加
                isSecondTime = false;
                CmdManage.allocDeviceGroup(group, selectLightList.get(curSelect).getLightSort().getMeshAddress());
                delayHand.postDelayed(runnableDelay, delay);
                times = System.currentTimeMillis();
            }
        });

        tips.setText(getString(R.string.new_group_tips));
        groupName.setHint(getString(R.string.new_group_name));

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
                if (selectLightList.contains(light)) {
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
                if (selectLightList.contains(lightList.get(position))) {
                    selectLightList.remove(lightList.get(position));
                } else {
                    selectLightList.add(lightList.get(position));
                }
                notifyDataSetChanged();
            }
        };

        recyclerView.setAdapter(commonAdapter);
    }

    Handler delayHand = new Handler();

    private Runnable runnableDelay = new Runnable() {
        @Override
        public void run() {

            if (!isSecondTime) {
                handler.sendEmptyMessage(1);
            } else {
                handler.sendEmptyMessage(2);
            }
        }
    };

    private void saveAndFinish() {

        group.getGroupSort().setName(groupName.getText().toString());
        group.setMembers(successMeshs);
        Groups.getInstance().add(group);
        GroupsDbUtils.getInstance().updataOrInsert(group.getGroupSort());
        DataToHostManage.updataCurToHost();

        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra(TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH, group.getGroupSort().getMeshAddress());
        startActivity(intent);
        finish();
    }

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(NotificationEvent.GET_GROUP)) {
            int src = ((NotificationEvent) event).getArgs().src;
            int lightMesh = src < 0 ? 256 + src : src;
            List<Integer> lightGroups = Lights.getLightGroups(((NotificationEvent) event).getArgs().params);
            Log.i("liucr","lightGroups: " + Lights.getInstance().getByMeshAddress(lightMesh).getLightSort().getName());
            if (lightMesh == selectLightList.get(curSelect).getLightSort().getMeshAddress()
                    && lightGroups.contains(group.getGroupSort().getMeshAddress())) {
                handler.sendEmptyMessage(0);
            }
        }
    }

}
