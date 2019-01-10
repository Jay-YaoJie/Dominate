package cn.xlink.telinkoffical.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;

import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.adapter.PopWindowAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.model.Group;
import cn.xlink.telinkoffical.model.Groups;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.model.Places;
import cn.xlink.telinkoffical.utils.EventBusUtils;
import cn.xlink.telinkoffical.utils.GroupsDbUtils;
import cn.xlink.telinkoffical.utils.LightsDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.view.MyPopupWindow;
import cn.xlink.telinkoffical.view.MyScrollview;
import cn.xlink.telinkoffical.view.TitleBar;
import cn.xlink.telinkoffical.view.togglebutton.zcw.togglebutton.ToggleButton;

/**
 * Created by liucr on 2016/3/25.
 */
public class GroupActivity extends BaseActivity implements EventListener<String> {

    @Bind(R.id.act_group_title)
    TitleBar titleBar;

    @Bind(R.id.act_group_scrollview)
    MyScrollview scrollView;

    @Bind(R.id.act_group_topview)
    View topView;

    @Bind(R.id.group_select_button)
    ToggleButton toggleButton;

    @Bind(R.id.act_group_image)
    ImageView groupImage;

    @Bind(R.id.light_seekbar)
    SeekBar lightBar;

    @Bind(R.id.act_group_devicelist)
    RecyclerView recyclerView;

    private CommonAdapter<Light> deviceListAdapter;

    private List<Light> lightList = new ArrayList<>();

    private Group group;

    private int mesh = -1;

    private MyPopupWindow popupWindow;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                GroupsDbUtils.getInstance().updataOrInsert(group.getGroupSort());
                DataToHostManage.updataCurToHost();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_group);
        ButterKnife.bind(this);
        EventBusUtils.getInstance().addEventListener(NotificationEvent.ONLINE_STATUS, this);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
        titleBar.getCenterText().setText(group.getGroupSort().getName());
        updataUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBusUtils.getInstance().removeEventListener(this);
        handler.removeMessages(0);
        handler = null;
    }

    @Override
    protected void back() {
        if (popupWindow.isShowing()) {
            popupWindow.myDismiss();
        } else {
            finish();
        }
    }

    @Override
    protected void initData() {
        mesh = getIntent().getIntExtra(TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH, -1);
        group = Groups.getInstance().getByMeshAddress(mesh);
        if (group == null) {
            finish();
        } else {
            lightList.clear();
            for (String mesh : group.getMembers()) {
                Light light = Lights.getInstance().getByMeshAddress(Integer.parseInt(mesh));
                if (light != null) {
                    lightList.add(light);
                }
            }
        }
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(group.getGroupSort().getName());
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.getRightImage().setImageResource(R.mipmap.icon_more);
        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.myDismiss();
                } else {
                    popupWindow.showAtLocation(titleBar, Gravity.BOTTOM, 0, 0);
                }
            }
        });

        if(group.getGroupSort().getMeshAddress() == 0xffff){
            titleBar.getRightItem().setVisibility(View.INVISIBLE);
        }

        if (group.getGroupSort().getIsShowOnHomeScreen()) {
            toggleButton.setToggleOn();
        } else {
            toggleButton.setToggleOff();
        }

        toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                group.getGroupSort().setIsShowOnHomeScreen(on);
                handler.removeMessages(0);
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        });

        lightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        //置顶
        scrollView.smoothScrollTo(0,0);
        initDevicesView();
        initPopUpWindow();
        updataUI();
        if (Places.getInstance().curPlaceIsShare()) {
            titleBar.getRightItem().setVisibility(View.INVISIBLE);
            topView.setVisibility(View.GONE);
        }
    }

    private void initDevicesView() {
        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        deviceListAdapter = new CommonAdapter<Light>(R.layout.view_device_item, lightList) {
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
                        Intent intent = new Intent(GroupActivity.this, BlubbActivity.class);
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

        recyclerView.setAdapter(deviceListAdapter);
        deviceListAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化更多
     */
    private void initPopUpWindow() {
        List<String> strings = new ArrayList<>();

        strings.add(getString(R.string.manage_group_member));
        strings.add(getString(R.string.rename));
        strings.add(getString(R.string.delete_group));

        popupWindow = new MyPopupWindow(this, strings, 0, new PopWindowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                switch (position) {
                    case 0:
                        bundle.putInt(TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH, group.getGroupSort().getMeshAddress());
                        openActivity(ManageGroupDevice.class, bundle);
                        break;
                    case 1:
                        bundle.putString(TelinkCommon.ACTIVITY_TYPE, TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH);
                        bundle.putInt(TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH, group.getGroupSort().getMeshAddress());
                        openActivity(RenameActivity.class, bundle);
                        break;
                    case 2:
                        showDeleteTips();
                        break;
                }
                popupWindow.dismiss();
            }
        });
    }

    @OnClick(R.id.act_group_image)
    void ClickImage() {
        CmdManage.changeGroupStatus(group);
    }

    private void updataUI() {
        groupImage.setImageResource(group.getStatusBigIcon());
        group.updataBrightness();
        deviceListAdapter.notifyDataSetChanged();

        if(lightList.size() == 0){
            recyclerView.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (group.status == ConnectionStatus.ON) {
            lightBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_on));
            lightBar.setThumb(getResources().getDrawable(R.mipmap.thumb_light));
            lightBar.setEnabled(true);
            lightBar.setProgress(group.getGroupSort().getBrightness() - 5);
        } else {
            lightBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_off));
            lightBar.setThumb(getResources().getDrawable(R.mipmap.thumb_unlight));
            lightBar.setEnabled(false);
            lightBar.setProgress(0);
        }

    }

    private void showDeleteTips() {
        showTipsDialog(getString(R.string.group_delete_tips, group.getGroupSort().getName()), getString(R.string.enter),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CmdManage.deleteGroup(group);
                        Groups.getInstance().remove(group);
                        DataToHostManage.updataCurToHost();
                        finish();
                    }
                });
    }

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(NotificationEvent.ONLINE_STATUS)) {
            updataUI();
        }
    }
}
