package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
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
import cn.xlink.telinkoffical.bean.greenDao.SceneActionSort;
import cn.xlink.telinkoffical.bean.greenDao.SceneTimerSort;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.model.Group;
import cn.xlink.telinkoffical.model.Groups;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.model.Scene;
import cn.xlink.telinkoffical.model.Scenes;
import cn.xlink.telinkoffical.utils.GroupsDbUtils;
import cn.xlink.telinkoffical.utils.SceneActionsDbUtils;
import cn.xlink.telinkoffical.utils.SceneTimersDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.utils.TelinkTimerUtils;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/3/28.
 */
public class ManageSceneDevice extends BaseActivity implements EventListener<String> {

    public static final int delay = 1000;

    @Bind(R.id.manage_titlebar)
    TitleBar titleBar;

    @Bind(R.id.manage_tips)
    TextView textTips;

    @Bind(R.id.manage_list)
    RecyclerView recyclerView;

    private Scene scene;

    private List<Light> lightList;

    private List<Light> oldSelectList;

    private List<Light> selectlightList;

    private CommonAdapter<Light> commonAdapter;

    private int curSelect = 0;

    private boolean isSecondTime = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeCallbacks(runnableDelay);
            if (msg.what == 0) {          //成功

                if (curSelect == lightList.size() - 1) {
                    successFinish();
                } else {
                    curSelect++;
                    doDeviceChange(curSelect);
                    isSecondTime = false;
                    handler.postDelayed(runnableDelay, delay);
                }
            } else if (msg.what == 1) {       //第一次失败
                doDeviceChange(curSelect);
                isSecondTime = true;
                handler.postDelayed(runnableDelay, delay);
            } else if (msg.what == 2) {                 //失败了
                if (curSelect >= lightList.size() - 1) {

                    successFinish();
                } else {
                    //失败了也添加进去。。。

                    curSelect++;
                    doDeviceChange(curSelect);
                    isSecondTime = false;
                    handler.postDelayed(runnableDelay, delay);
                }
            }
        }
    };

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_manage);
        MyApp.getApp().addEventListener(NotificationEvent.GET_SCENE, this);
        MyApp.getApp().addEventListener(NotificationEvent.GET_ALARM, this);
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
        int mesh = bundle.getInt(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, -1);
        scene = Scenes.getInstance().getById(mesh);
        lightList = new ArrayList<>();
        for (Light light : Lights.getInstance().get()) {
            lightList.add(light);
        }

        selectlightList = new ArrayList<>();
        oldSelectList = new ArrayList<>();
        for (SceneActionSort sceneActionSort : scene.getSceneActionSort()) {
            Light light = Lights.getInstance().getByMeshAddress(sceneActionSort.getDeviceMesh());
            if (light != null) {
                selectlightList.add(light);
                oldSelectList.add(light);
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

                //移除不需要作处理的设备
                for(int i = 0; i<lightList.size()-1;i++){
                    Light light = lightList.get(i);
                    if ((oldSelectList.contains(light) && selectlightList.contains(light))
                            || (!oldSelectList.contains(light) && !selectlightList.contains(light))) {
                        lightList.remove(light);
                    }
                }

                if (lightList.size() == 0) {
                    finish();
                } else {
                    showWaitingDialog(null);
                    //等待回调确认已添加
                    isSecondTime = false;
                    doDeviceChange(curSelect);
                    handler.postDelayed(runnableDelay, delay);
                }

            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        commonAdapter = new CommonAdapter<Light>(R.layout.view_common_item, Lights.getInstance().get()) {
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
                if(lightList.get(position).status == ConnectionStatus.OFFLINE){
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

    private void deleteSceneLight(Light light) {

        //删除设备在该情景中所有的定时器
        for (SceneTimerSort timerSort : SceneTimersDbUtils.getInstance().getSceneDeviceTimerSort(
                scene.getSceneSort().getSceneId(), light.getLightSort().getMeshAddress())) {
            CmdManage.deleteAlarm(timerSort);
            SceneTimersDbUtils.getInstance().deleteSceneTimerSort(timerSort);
        }

        SceneActionSort sceneActionSort = SceneActionsDbUtils.getInstance().getSceneActionSort(scene.getSceneSort().getSceneId(),
                light.getLightSort().getMeshAddress());
        //发送删除动作命令
        if (sceneActionSort != null) {
            CmdManage.deleteSceneAction(sceneActionSort);
            SceneActionsDbUtils.getInstance().deleteSceneActionSort(sceneActionSort);
        }

    }

    private void addSceneLight(Light light) {
        SceneActionSort sceneActionSort = SceneActionsDbUtils.getInstance().getSceneActionSort(scene.getSceneSort().getSceneId(),
                light.getLightSort().getMeshAddress());
        if (sceneActionSort != null) {
            return;
        }

        sceneActionSort = new SceneActionSort();
        saveAction(light, sceneActionSort);
    }

    /**
     * 保存情景动作到本地
     *
     * @param light
     * @param sceneActionSort
     */
    private void saveAction(Light light, final SceneActionSort sceneActionSort) {
        sceneActionSort.setSceneId(scene.getSceneSort().getSceneId());
        sceneActionSort.setDeviceMesh(light.getLightSort().getMeshAddress());
        sceneActionSort.setBrightness(light.brightness);
        sceneActionSort.setColor(light.color);
        sceneActionSort.setTemperature(light.temperature);

        //发送添加/修改情景命令
        CmdManage.addDeviceScence(sceneActionSort);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CmdManage.getDeviceScene(sceneActionSort);
            }
        },delay/2);

        SceneActionsDbUtils.getInstance().updataOrInsert(sceneActionSort);

        addAlarm(light);
    }

    private void addAlarm(Light light) {
        for (SceneTimerSort timerSort : scene.getSceneTimerSort()) {
            SceneTimerSort curSceneTimerSort = new SceneTimerSort();
            curSceneTimerSort.setSceneId(scene.getSceneSort().getSceneId());
            curSceneTimerSort.setDeviceMesh(light.getLightSort().getMeshAddress());
            curSceneTimerSort.setSceneTimerId(timerSort.getSceneTimerId());
            curSceneTimerSort.setTimerId(TelinkTimerUtils.getNoUseDeviceTimerId(light.getLightSort().getMeshAddress()));
            curSceneTimerSort.setIsEnable(timerSort.getIsEnable());

            curSceneTimerSort.setTimerType(timerSort.getTimerType());
            curSceneTimerSort.setWorkDay(timerSort.getWorkDay());
            curSceneTimerSort.setHour(timerSort.getHour());
            curSceneTimerSort.setMinute(timerSort.getMinute());

            CmdManage.addEditAlarm(curSceneTimerSort, scene.getSceneSort().getSceneId(), true);
            SceneTimersDbUtils.getInstance().updataOrInsert(curSceneTimerSort);
        }
    }

    private void doDeviceChange(int select) {
        Light light = lightList.get(select);
        if (selectlightList.contains(light)) {
            addSceneLight(light);
        } else {
            deleteSceneLight(light);
        }
    }

    private void successFinish() {
        DataToHostManage.updataCurToHost();
        finish();
    }

    @Override
    public void performed(Event<String> event) {
        if(event.getType().equals(NotificationEvent.GET_SCENE)){
            int src = ((NotificationEvent) event).getArgs().src;
            int lightMesh = src < 0 ? 256 + src : src;
            byte[] params = ((NotificationEvent) event).getArgs().params;

            if(lightList.get(curSelect).getLightSort().getMeshAddress() == lightMesh){
                handler.sendEmptyMessage(0);
            }
        }else if(event.getType().equals(NotificationEvent.GET_ALARM)){

        }
    }
}
