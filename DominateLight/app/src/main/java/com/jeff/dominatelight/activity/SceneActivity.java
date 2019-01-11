package com.jeff.dominatelight.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.jeff.dominatelight.MyApp;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.adapter.MyFragmentPagerAdapter;
import com.jeff.dominatelight.adapter.PopWindowAdapter;
import com.jeff.dominatelight.bean.greenDao.SceneActionSort;
import com.jeff.dominatelight.bean.greenDao.SceneSort;
import com.jeff.dominatelight.bean.greenDao.SceneTimerSort;
import com.jeff.dominatelight.fragment.ActionDetailFragment;
import com.jeff.dominatelight.fragment.BaseFragment;
import com.jeff.dominatelight.fragment.SceneActionFragment;
import com.jeff.dominatelight.fragment.SceneFirstFragment;
import com.jeff.dominatelight.manage.CmdManage;
import com.jeff.dominatelight.manage.DataToHostManage;
import com.jeff.dominatelight.model.Light;
import com.jeff.dominatelight.model.Lights;
import com.jeff.dominatelight.model.Scene;
import com.jeff.dominatelight.model.Scenes;
import com.jeff.dominatelight.utils.SceneActionsDbUtils;
import com.jeff.dominatelight.utils.SceneTimersDbUtils;
import com.jeff.dominatelight.utils.ScenesDbUtils;
import com.jeff.dominatelight.utils.TelinkCommon;
import com.jeff.dominatelight.view.IndexViewPager;
import com.jeff.dominatelight.view.MyPopupWindow;
import com.jeff.dominatelight.view.TitleBar;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liucr on 2016/3/29.
 */
public class SceneActivity extends BaseActivity implements EventListener<String> {

    public static final int delay = 1000;

    public static final String EDIT_TYPE = "edit_type";

    public static final String NEW_TYPE = "new_type";

    private String curType;

    @BindView(R.id.act_scene_title)
    TitleBar titleBar;

    @BindView(R.id.act_scene_viewpager)
    IndexViewPager viewPager;

    private MyFragmentPagerAdapter pagersAdapter;

    private SceneFirstFragment sceneFirstFragment;

    private SceneActionFragment sceneActionFragment;

    private ActionDetailFragment actionDetailFragment;

    private BaseFragment baseFragment[];

    private Scene scene;

    private int curSelect = 0;

    private SceneActionSort curActionSort;

    private List<Light> lightList;

    private List<Light> selectLightList;

    private boolean isSecondTime = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeCallbacks(runnableDelay);
            if (msg.what == 0) {          //成功

                SceneActionsDbUtils.getInstance().updataOrInsert(curActionSort);

                if (curSelect == selectLightList.size() - 1) {
                    successFinish();
                } else {
                    curSelect++;
                    sendAction(selectLightList.get(curSelect));
                    isSecondTime = false;
                    handler.postDelayed(runnableDelay, delay);
                }
            } else if (msg.what == 1) {       //第一次失败
                sendAction(selectLightList.get(curSelect));
                isSecondTime = true;
                handler.postDelayed(runnableDelay, delay);
            } else if (msg.what == 2) {                 //失败了
                if (curSelect >= selectLightList.size() - 1) {
                    SceneActionsDbUtils.getInstance().updataOrInsert(curActionSort);
                    successFinish();
                } else {
                    //失败了也添加进去。。。
                    SceneActionsDbUtils.getInstance().updataOrInsert(curActionSort);
                    curSelect++;
                    sendAction(selectLightList.get(curSelect));
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_new_scene);
        MyApp.getApp().addEventListener(NotificationEvent.GET_SCENE, this);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        scene = Scenes.getInstance().getById(scene.getSceneSort().getSceneId());
        titleBar.getCenterText().setText(scene.getSceneSort().getName());
        initEditData();
        sceneActionFragment.notifData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.getApp().removeEventListener(this);
        handler.removeMessages(0);
        handler.removeMessages(1);
        handler.removeMessages(2);
        handler.removeCallbacks(runnableDelay);
        handler = null;
    }

    @Override
    protected void back() {
        if (viewPager.getCurrentItem() == 0) {
            finish();
        } else if (viewPager.getCurrentItem() == 1) {
            if (curType.equals(NEW_TYPE)) {
                viewPager.setCurrentItem(0);
            } else {
                finish();
            }
        } else {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    protected void initData() {

        lightList = new ArrayList<>();
        selectLightList = new ArrayList<>();
        int sceneId = getIntent().getIntExtra(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, -1);
        scene = Scenes.getInstance().getById(sceneId);

        if (scene == null) {
            curType = NEW_TYPE;
            initNewData();
        } else {
            curType = EDIT_TYPE;
            initEditData();
        }

        sceneFirstFragment = new SceneFirstFragment();
        sceneActionFragment = new SceneActionFragment();
        actionDetailFragment = new ActionDetailFragment();

        baseFragment = new BaseFragment[]{sceneFirstFragment, sceneActionFragment, actionDetailFragment};
        pagersAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), baseFragment);
    }

    /**
     * 初始化新建情景数据
     */
    private void initNewData() {

        for (Light light : Lights.getInstance().get()) {
            lightList.add(light);
        }

        SceneSort sceneSort = new SceneSort();

        boolean ishad = true;
        int mesh = 5;
        while (ishad) {
            ishad = false;
            for (Scene scene : Scenes.getInstance().get()) {
                if (scene.getSceneSort().getSceneId() == mesh) {
                    mesh++;
                    ishad = true;
                }
            }
        }
        sceneSort.setSceneId(mesh);
        sceneSort.setIsShowOnHomeScreen(true);
        sceneSort.setSceneType(Scene.SceneType.SCENETYPE_CUSTOM);
        scene = new Scene(sceneSort);
    }

    /**
     * 初始化修改数据
     */
    private void initEditData() {
        lightList.clear();
        selectLightList.clear();
        for (Light light : Lights.getInstance().get()) {
            for (SceneActionSort sceneAction : scene.getSceneActionSort()) {
                if (sceneAction.getDeviceMesh().intValue() == light.getLightSort().getMeshAddress().intValue()) {
                    //new一个新的light不要影响原来的
                    Light lightEdit = new Light(light.getLightSort());
                    lightEdit.brightness = sceneAction.getBrightness();
                    lightEdit.color = sceneAction.getColor();
                    lightEdit.temperature = sceneAction.getTemperature();
                    lightEdit.status = sceneAction.getBrightness() == 0 ? ConnectionStatus.OFF : ConnectionStatus.ON;
                    lightList.add(lightEdit);
                    selectLightList.add(lightEdit);
                }
            }
        }
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(getString(R.string.new_scene_title));

        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        if (curType.equals(EDIT_TYPE)) {
            titleBar.getRightImage().setImageResource(R.mipmap.icon_more);
            titleBar.getRightText().setText(R.string.finish);
        } else {
            titleBar.getRightText().setText(R.string.next);
        }
        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curType.equals(EDIT_TYPE)) {
                    if (viewPager.getCurrentItem() != 1) {
                        viewPager.setCurrentItem(1);
                    } else {
                        popupWindow.showAtLocation(titleBar, Gravity.BOTTOM, 0, 0);
                    }
                    return;
                }
                if (viewPager.getCurrentItem() == 0) {
                    if (TextUtils.isEmpty(sceneFirstFragment.getName())) {
                        showTipsDialog(getString(R.string.name_your_scene_tips), getString(R.string.enter));
                        return;
                    }
                    hideKey();
                    if (Scenes.checkNameHad(sceneFirstFragment.getName(), "")) {
                        showTipsDialog(getString(R.string.scene_name_had), getString(R.string.enter));
                        return;
                    }

                    if (selectLightList.size() == 0) {
                        showTipsDialog(getString(R.string.devices_empty_tips), getString(R.string.enter));
                        return;
                    }

                    viewPager.setCurrentItem(1);
                } else if (viewPager.getCurrentItem() == 1) {
                    successEdit();
                } else {
                    viewPager.setCurrentItem(1);
                }
            }
        });

        initViewPager();
    }

    private void initViewPager() {
        viewPager.setAdapter(pagersAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (curType.equals(EDIT_TYPE)) {
                    titleBar.getRightImage().setVisibility(View.INVISIBLE);
                    titleBar.getRightText().setVisibility(View.INVISIBLE);
                    if (position == 2) {
                        titleBar.getRightText().setVisibility(View.VISIBLE);
                    } else {
                        titleBar.getRightImage().setVisibility(View.VISIBLE);
                        sceneActionFragment.notifData();
                    }
                    return;
                }

                if (position == 0) {
                    titleBar.getRightText().setText(getString(R.string.next));
                } else if (position == 2) {
                    titleBar.getRightText().setText(getString(R.string.finish));
                } else {
                    sceneActionFragment.notifData();
                    titleBar.getRightText().setText(getString(R.string.finish));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (curType.equals(EDIT_TYPE)) {
            titleBar.getCenterText().setText(scene.getSceneSort().getName());
            viewPager.setCurrentItem(1);
            initPopUpWindow();
        }
    }

    /**
     * 点击完成
     */
    public void successEdit() {
        boolean isHadOffline = false;
        for (Light light : getSelectLightList()) {
            if (Lights.getInstance().getByMeshAddress(light.getLightSort().getMeshAddress()).status == ConnectionStatus.OFFLINE) {
                isHadOffline = true;
                break;
            }
        }

        if (isHadOffline) {
            showOfflineOpeTips();
        } else {
            doSuccessEdit();
        }
    }

    public void doSuccessEdit() {
        showWaitingDialog(null);
        if (curType.equals(NEW_TYPE)) {
            scene.getSceneSort().setName(sceneFirstFragment.getName());
        }

        //等待回调确认已添加
        isSecondTime = false;
        sendAction(selectLightList.get(curSelect));
        handler.postDelayed(runnableDelay, delay);
    }

    public void successFinish() {
        if (curType.equals(NEW_TYPE)) {
            Scenes.getInstance().add(scene);
        }
        ScenesDbUtils.getInstance().updataOrInsert(scene.getSceneSort());
        DataToHostManage.updataCurToHost();
        finish();
    }

    /**
     * 保存情景动作到本地
     *
     * @param light
     */
    private void sendAction(Light light) {

        SceneActionSort sceneActionSort = SceneActionsDbUtils.getInstance().getSceneActionSort(scene.getSceneSort().getSceneId(),
                light.getLightSort().getMeshAddress());
        if (sceneActionSort == null) {
            sceneActionSort = new SceneActionSort();
        }

        sceneActionSort.setSceneId(scene.getSceneSort().getSceneId());
        sceneActionSort.setDeviceMesh(light.getLightSort().getMeshAddress());
        if (light.status == ConnectionStatus.OFF) {
            sceneActionSort.setBrightness(0);
        } else {
            sceneActionSort.setBrightness(light.brightness);
        }
        sceneActionSort.setColor(light.color);
        sceneActionSort.setTemperature(light.temperature);

        curActionSort = sceneActionSort;
        //发送添加/修改情景命令
        CmdManage.addDeviceScence(sceneActionSort);

        final SceneActionSort finalSceneActionSort = sceneActionSort;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CmdManage.getDeviceScene(finalSceneActionSort);
            }
        }, delay / 2);
    }

    public List<Light> getLightList() {
        return lightList;
    }

    public List<Light> getSelectLightList() {
        return selectLightList;
    }

    public void goToActionDetail(int position) {
        viewPager.setCurrentItem(2);
        actionDetailFragment.setCurPosition(position);
    }

    public Scene getScene() {
        return scene;
    }

    public String getCurType() {
        return curType;
    }

    private MyPopupWindow popupWindow;

    /**
     * 初始化更多
     */
    private void initPopUpWindow() {
        List<String> strings = new ArrayList<>();

        strings.add(getString(R.string.manage_group_member));
        strings.add(getString(R.string.timer));
        strings.add(getString(R.string.rename));
        strings.add(getString(R.string.delete_scene));

        popupWindow = new MyPopupWindow(this, strings, 0, new PopWindowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                switch (position) {
                    case 0:
                        bundle.putInt(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, scene.getSceneSort().getSceneId());
                        openActivity(ManageSceneDevice.class, bundle);
                        break;
                    case 1:
                        bundle.putInt(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, scene.getSceneSort().getSceneId());
                        openActivity(TimerListActivity.class, bundle);
                        break;
                    case 2:
                        bundle.putString(TelinkCommon.ACTIVITY_TYPE, TelinkCommon.ACTIVITY_TYPE_SCENE_MESH);
                        bundle.putInt(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, scene.getSceneSort().getSceneId());
                        openActivity(RenameActivity.class, bundle);
                        break;
                    case 3:
                        showDeleteTips();
                        break;
                }
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 删除提示dialog
     */
    private void showDeleteTips() {
        showTipsDialog(getString(R.string.scene_delete_tips, scene.getSceneSort().getName()),
                getString(R.string.enter), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideDialog();
                        showWaitingDialog(null);
                        //删除情景中的所有定时器
                        int i = 0;
                        for (final SceneTimerSort timerSort : SceneTimersDbUtils.getInstance().getSceneTimerSortById(scene.getSceneSort().getSceneId())) {
                            CmdManage.deleteAlarm(timerSort);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SceneTimersDbUtils.getInstance().deleteSceneTimerSort(timerSort);
                                }
                            }, delay / 2);
                            i++;
                        }

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //发送删除动作命令
                                CmdManage.deleteSceneAction(scene.getSceneActionSort());
                            }
                        }, delay / 2 * (i + 1));

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SceneActionsDbUtils.getInstance().deleteBySceneId(scene.getSceneSort().getSceneId());
                                ScenesDbUtils.getInstance().deleteSceneSort(scene.getSceneSort());
                                Scenes.getInstance().remove(scene);
                                DataToHostManage.updataCurToHost();
                                finish();
                            }
                        }, delay / 2 * (i + 2));

                    }
                });
    }

    private void showOfflineOpeTips() {
        showTipsDialog(getString(R.string.offline_operation_tips),
                getString(R.string.continue_), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideDialog();
                        doSuccessEdit();
                    }
                });
    }

    public int getTitleHeight() {
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        titleBar.measure(0, h);
        return titleBar.getMeasuredHeight();
    }

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(NotificationEvent.GET_SCENE)) {
            int src = ((NotificationEvent) event).getArgs().src;
            int lightMesh = src < 0 ? 256 + src : src;
            byte[] params = ((NotificationEvent) event).getArgs().params;

            if (curActionSort != null && lightMesh == curActionSort.getDeviceMesh()) {
                handler.sendEmptyMessage(0);
            }
        }
    }
}
