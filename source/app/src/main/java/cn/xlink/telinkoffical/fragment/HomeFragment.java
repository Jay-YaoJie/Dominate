package cn.xlink.telinkoffical.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.util.Event;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.activity.BlubbActivity;
import cn.xlink.telinkoffical.activity.FindNewActivity;
import cn.xlink.telinkoffical.activity.GroupActivity;
import cn.xlink.telinkoffical.activity.MainActivity;
import cn.xlink.telinkoffical.activity.NewGroupActivity;
import cn.xlink.telinkoffical.activity.SceneActivity;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.bean.greenDao.SceneSort;
import cn.xlink.telinkoffical.eventbus.ChangePlaceEvent;
import cn.xlink.telinkoffical.eventbus.ConnectStateEvent;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.model.Group;
import cn.xlink.telinkoffical.model.Groups;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.model.Places;
import cn.xlink.telinkoffical.model.Scene;
import cn.xlink.telinkoffical.model.Scenes;
import cn.xlink.telinkoffical.utils.EventBusUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.view.MyToast;

/**
 * Created by liucr on 2016/3/24.
 */
public class HomeFragment extends BaseFragment {

    private View view;

    @Bind(R.id.fgt_home_scenelist)
    RecyclerView sceneListView;

    @Bind(R.id.fgt_home_grouplist)
    RecyclerView groupListView;

    @Bind(R.id.fgt_home_group_add)
    View addGroupView;

    @Bind(R.id.fgt_home_devicelist)
    RecyclerView deviceListView;

    @Bind(R.id.fgt_home_device_add)
    View addDeviceView;

    private CommonAdapter<Scene> sceneCommonAdapter;

    private CommonAdapter<Group> groupCommonAdapter;

    private CommonAdapter<Light> lightCommonAdapter;

    private List<Scene> scenes = new ArrayList<>();

    private List<Group> groups = new ArrayList<>();

    private List<Light> lights = new ArrayList<>();

    int width;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusUtils.getInstance().addEventListener(NotificationEvent.ONLINE_STATUS, this);
        EventBusUtils.getInstance().addEventListener(ConnectStateEvent.ConnectStateEvent, this);
        EventBusUtils.getInstance().addEventListener(ChangePlaceEvent.ChangePlaceEvent, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fgt_home, container, false);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.getInstance().removeEventListener(this);
    }

    @Override
    protected void initView(View view) {

        initSceneView();
        initGroupView();
        initDeviceView();
    }

    @Override
    protected void initData() {
        notifyData();
        WindowManager wm = getActivity().getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
    }

    public void notifyData() {

        scenes.clear();
        for (Scene scene : Scenes.getInstance().get()) {
            if (scene.getSceneSort().getIsShowOnHomeScreen()) {
                scenes.add(scene);
            }
        }

        groups.clear();
        for (Group group : Groups.getInstance().get()) {
            if (group.getGroupSort().getIsShowOnHomeScreen()) {
                groups.add(group);
            }
        }

        lights.clear();
        for (Light light : Lights.getInstance().get()) {
            if (light.getLightSort().getIsAlone() || light.getLightSort().getIsShowOnHomeScreen()) {
                lights.add(light);
            }
        }

        //是否需要显示添加按钮
        if (!Places.getInstance().curPlaceIsShare()) {

            if (Lights.getInstance().size() != 0) {
                SceneSort sceneSort = new SceneSort();
                sceneSort.setSceneType(-1);
                sceneSort.setName(getString(R.string.add_new_scene));
                Scene scene = new Scene(sceneSort);
                scenes.add(scene);
            }

            if (groups.size() == 0 && Lights.getInstance().size() != 0) {
                addGroupView.setVisibility(View.VISIBLE);
            } else {
                addGroupView.setVisibility(View.GONE);
            }

            if (lights.size() == 0) {
                addDeviceView.setVisibility(View.VISIBLE);
            } else {
                addDeviceView.setVisibility(View.GONE);
            }
        } else {
            addGroupView.setVisibility(View.GONE);
            addDeviceView.setVisibility(View.GONE);
        }

        if (sceneCommonAdapter != null) {
            sceneCommonAdapter.notifyDataSetChanged();
            groupCommonAdapter.notifyDataSetChanged();
            lightCommonAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 初始化情景列表
     */
    private void initSceneView() {

        //设置布局管理器
        sceneListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        sceneListView.setItemAnimator(null);

        sceneCommonAdapter = new CommonAdapter<Scene>(R.layout.view_top_view, scenes) {
            @Override
            public void convert(RecyclerViewHolder holder, Scene scene, int position) {
                View item = holder.getView(R.id.view_top_item);
                item.setLayoutParams(new LinearLayout.LayoutParams(width / 4, ViewGroup.LayoutParams.WRAP_CONTENT));

                holder.setText(R.id.view_top_text, scene.getSceneSort().getName());
                holder.setImageResource(R.id.view_top_image, scene.getHomeIcon());
            }

            @Override
            public void onItemClick(View v, int position) {
                super.onItemClick(v, position);
                if (!Places.getInstance().curPlaceIsShare() && position == scenes.size() - 1) {
                    startActivity(new Intent(getActivity(), SceneActivity.class));
                } else if (scenes.get(position).getSceneActionSort().size() == 0) {
                    MyToast.showTopToast(getActivity(), getString(R.string.scene_no_device));
                } else {
                    CmdManage.executeScene(scenes.get(position));
                    MyToast.showTopToast(getActivity(), getString(R.string.scene_execute));
                }
            }

            @Override
            public boolean onItemLongClick(View v, int position) {

                if (Places.getInstance().curPlaceIsShare() || position == scenes.size() - 1) {
                    return super.onItemLongClick(v, position);
                }

                Intent intent = new Intent(getActivity(), SceneActivity.class);
                intent.putExtra(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, scenes.get(position).getSceneSort().getSceneId());
                startActivity(intent);
                return super.onItemLongClick(v, position);
            }
        };

        sceneListView.setAdapter(sceneCommonAdapter);
    }

    /**
     * 初始化分组列表
     */
    private void initGroupView() {
        //设置布局管理器
        groupListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupListView.setItemAnimator(null);
        groupCommonAdapter = new CommonAdapter<Group>(R.layout.view_device_item, groups) {
            @Override
            public void convert(RecyclerViewHolder holder, final Group group, int position) {
                SeekBar seekBar = holder.getView(R.id.device_item_light);
                ImageView rightView = holder.getView(R.id.device_item_right);
                ImageView leftView = holder.getView(R.id.device_item_left);
                holder.setImageResource(R.id.device_item_left, group.getStatusIcon());
                holder.setText(R.id.device_item_name, group.getGroupSort().getName());

                group.updataBrightness();
                if (group.status() == ConnectionStatus.ON) {
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_on));
                    seekBar.setThumb(getResources().getDrawable(R.mipmap.thumb_light));
                    seekBar.setEnabled(true);
                    seekBar.setProgress(group.getGroupSort().getBrightness() - 5);
                } else {
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_off));
                    seekBar.setThumb(getResources().getDrawable(R.mipmap.thumb_unlight));
                    seekBar.setEnabled(false);
                    seekBar.setProgress(0);
                }

                leftView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (group.getMembers().size() == 0) {
                            getMainActivity().showTipsDialog(getString(R.string.group_no_device), getString(R.string.enter));
                        } else {
                            group.status();
                            CmdManage.changeGroupStatus(group);
                        }
                    }
                });

                rightView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), GroupActivity.class);
                        intent.putExtra(TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH, group.getGroupSort().getMeshAddress());
                        startActivity(intent);
                    }
                });


                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        group.getGroupSort().setBrightness(seekBar.getProgress() + 5);
                        CmdManage.changeGroupLum(group);
                    }
                });

            }
        };

        groupListView.setAdapter(groupCommonAdapter);
    }

    private void initDeviceView() {
        //设置布局管理器
        deviceListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        deviceListView.setItemAnimator(null);
        lightCommonAdapter = new CommonAdapter<Light>(R.layout.view_device_item, lights) {
            @Override
            public void convert(RecyclerViewHolder holder, final Light light, int position) {
                SeekBar seekBar = holder.getView(R.id.device_item_light);
                ImageView rightView = holder.getView(R.id.device_item_right);
                ImageView leftView = holder.getView(R.id.device_item_left);
                holder.setImageResource(R.id.device_item_left, light.getStatusIcon());
                holder.setText(R.id.device_item_name, light.getLightSort().getName());

                if (light.status == ConnectionStatus.ON) {
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_on));
                    seekBar.setThumb(getResources().getDrawable(R.mipmap.thumb_light));
                    seekBar.setEnabled(true);
                } else {
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_off));
                    seekBar.setThumb(getResources().getDrawable(R.mipmap.thumb_unlight));
                    seekBar.setEnabled(false);
                }

                leftView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CmdManage.changeLightStatus(light);
                    }
                });

                rightView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), BlubbActivity.class);
                        intent.putExtra(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH, light.getLightSort().getMeshAddress());
                        startActivity(intent);
                    }
                });

                seekBar.setProgress(light.brightness - 5);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        light.brightness = seekBar.getProgress() + 5;
                        CmdManage.setLightLum(light);
                    }
                });

            }
        };

        deviceListView.setAdapter(lightCommonAdapter);
    }

    @OnClick(R.id.fgt_home_group_add)
    void ClickAddGroup() {
        startActivity(new Intent(getActivity(), NewGroupActivity.class));
    }

    @OnClick(R.id.fgt_home_device_add)
    void ClickAddDevice() {
        startActivity(new Intent(getActivity(), FindNewActivity.class));
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    public void performed(Event<String> event) {
        super.performed(event);

        if (event.getType().equals(NotificationEvent.ONLINE_STATUS)
                || event.getType().equals(ConnectStateEvent.ConnectStateEvent)) {
            groupCommonAdapter.notifyDataSetChanged();
            lightCommonAdapter.notifyDataSetChanged();
        } else if (event.getType().equals(ChangePlaceEvent.ChangePlaceEvent)) {
            notifyData();
        }
    }
}
