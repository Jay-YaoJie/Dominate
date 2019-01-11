package com.jeff.dominatelight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.adapter.recycler_adapter.CommonAdapter;
import com.jeff.dominatelight.adapter.recycler_adapter.RecyclerViewHolder;
import com.jeff.dominatelight.bean.greenDao.SceneTimerSort;
import com.jeff.dominatelight.manage.CmdManage;
import com.jeff.dominatelight.model.Scene;
import com.jeff.dominatelight.model.Scenes;
import com.jeff.dominatelight.utils.SceneTimersDbUtils;
import com.jeff.dominatelight.utils.TelinkCommon;
import com.jeff.dominatelight.utils.TelinkTimerUtils;
import com.jeff.dominatelight.view.SlideView;
import com.jeff.dominatelight.view.TitleBar;
import com.jeff.dominatelight.view.togglebutton.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by liucr on 2016/3/30.
 */
public class TimerListActivity extends BaseActivity {

    @BindView(R.id.act_timer_list_title)
    TitleBar titleBar;

    @BindView(R.id.act_timer_list)
    RecyclerView recyclerView;

    private String curType = SlideView.STYLE_GENERAL;

    private CommonAdapter<SceneTimerSort> commonAdapter;

    private List<SceneTimerSort> timerSorts = new ArrayList<>();

    private Scene scene;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_timer_list);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        timerSorts = scene.getSceneTimerSort();
        //底部位置
        SceneTimerSort sceneTimerSort = new SceneTimerSort();
        timerSorts.add(sceneTimerSort);
        commonAdapter.setData(timerSorts);
        commonAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        int mesh = bundle.getInt(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, -1);
        scene = Scenes.getInstance().getById(mesh);

        timerSorts = scene.getSceneTimerSort();
        for (SceneTimerSort timerSort : timerSorts) {
            if (timerSort.getTimerType() == 0) {
                updataOnceIsEnable(timerSort);
            }
        }
        //底部位置
        SceneTimerSort sceneTimerSort = new SceneTimerSort();
        timerSorts.add(sceneTimerSort);
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(getString(R.string.timer));
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        titleBar.getRightText().setText(getString(R.string.edit));
        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curType.equals(SlideView.STYLE_GENERAL)) {
                    titleBar.getRightText().setText(getString(R.string.finish));
                    curType = SlideView.STYLE_EDIT;
                } else {
                    titleBar.getRightText().setText(getString(R.string.edit));
                    curType = SlideView.STYLE_GENERAL;
                }
                commonAdapter.notifyDataSetChanged();
            }
        });

        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        commonAdapter = new CommonAdapter<SceneTimerSort>(R.layout.view_timer_item, timerSorts) {
            @Override
            public void convert(RecyclerViewHolder holder, final SceneTimerSort sceneTimerSort, final int position) {
                SlideView slideView = holder.getView(R.id.view_tiemer_item);
                View bottomView = holder.getView(R.id.view_timer_bottom);

                if (position == timerSorts.size() - 1) {
                    slideView.setVisibility(View.GONE);
                    bottomView.setVisibility(View.VISIBLE);
                    bottomView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(TimerListActivity.this, NewTimerActivity.class);
                            intent.putExtra(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, scene.getSceneSort().getSceneId());
                            startActivity(intent);
                        }
                    });

                } else {
                    slideView.setVisibility(View.VISIBLE);
                    bottomView.setVisibility(View.GONE);

                    slideView.setStyle(curType);
                    String time = TelinkTimerUtils.getHourMin(timerSorts.get(position));
                    slideView.setCenterText(time, TelinkTimerUtils.getWorkDayText(timerSorts.get(position), TimerListActivity.this));

                    slideView.setOnDeleteLister(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteTimer(timerSorts.get(position));
                            timerSorts.remove(position);
                            notifyDataSetChanged();
                        }
                    });

                    slideView.setOnClickRightLister(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(TimerListActivity.this, NewTimerActivity.class);
                            intent.putExtra(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, scene.getSceneSort().getSceneId());
                            intent.putExtra(TelinkCommon.ACTIVITY_TYPE_TIME_ID, sceneTimerSort.getSceneTimerId());
                            startActivity(intent);
                        }
                    });

                    slideView.setToggle(sceneTimerSort.getIsEnable());
                    slideView.setonToggleChanged(new ToggleButton.OnToggleChanged() {
                        @Override
                        public void onToggle(boolean on) {
                            sendONorOff(timerSorts.get(position), on);
                        }
                    });
                }

            }
        };

        recyclerView.setAdapter(commonAdapter);
    }

    /**
     * 更新单次定时器开关状态，若当前时间超过定时器执行时间视为关
     *
     * @param timerSort
     */
    public void updataOnceIsEnable(SceneTimerSort timerSort) {
        Calendar calendar = Calendar.getInstance();
        int curDayTime = (calendar.get(Calendar.MONTH) + 1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
        int curMin = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        int workDay = timerSort.getWorkDay();
        int workMin = timerSort.getHour() * 60 + timerSort.getMinute();
        if ((curDayTime > workDay) || (curDayTime == workDay && curMin > workMin)) {
            timerSort.setIsEnable(false);
            for (SceneTimerSort curTimerSort : SceneTimersDbUtils.getInstance().getSceneTimerSort(scene.getSceneSort().getSceneId(), timerSort.getSceneTimerId())) {
                curTimerSort.setIsEnable(false);
                SceneTimersDbUtils.getInstance().updataOrInsert(curTimerSort);
            }
        }
    }

    /**
     * 删除定时器
     *
     * @param timerSort
     */
    private void deleteTimer(SceneTimerSort timerSort) {

        for (final SceneTimerSort sceneTimerSort :
                SceneTimersDbUtils.getInstance().getSceneTimerSort(scene.getSceneSort().getSceneId(), timerSort.getSceneTimerId())) {
            CmdManage.deleteAlarm(sceneTimerSort);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CmdManage.deleteAlarm(sceneTimerSort);
                }
            },200);
        }

        SceneTimersDbUtils.getInstance().deleteTimer(scene.getSceneSort().getSceneId(),
                timerSort.getSceneTimerId());

    }

    /**
     * 发送开关指令
     *
     * @param timerSort
     * @param onOrOff
     */
    private void sendONorOff(SceneTimerSort timerSort, boolean onOrOff) {

        //将已过期的单次定时器再启用
        if (timerSort.getTimerType() == 0 && onOrOff) {
            Calendar calendar = Calendar.getInstance();

            int month = (calendar.get(Calendar.MONTH) + 1);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int curMin = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
            int workMin = timerSort.getHour() * 60 + timerSort.getMinute();
            int workData = month * 100 + ((curMin > workMin) ? (day + 1) : day);

            for (SceneTimerSort curTimerSort : SceneTimersDbUtils.getInstance().getSceneTimerSort(scene.getSceneSort().getSceneId(),
                    timerSort.getSceneTimerId())) {
                curTimerSort.setIsEnable(true);
                curTimerSort.setWorkDay(workData);
                CmdManage.addEditAlarm(curTimerSort, scene.getSceneSort().getSceneId(), false);
                SceneTimersDbUtils.getInstance().updataOrInsert(curTimerSort);
            }
            return;
        }

        for (SceneTimerSort timerSort1 : SceneTimersDbUtils.getInstance().getSceneTimerSort(scene.getSceneSort().getSceneId(),
                timerSort.getSceneTimerId())) {
            if (onOrOff) {
                CmdManage.openAlarm(timerSort1);
            } else {
                CmdManage.closeAlarm(timerSort1);
            }
            timerSort.setIsEnable(onOrOff);
            SceneTimersDbUtils.getInstance().updataOrInsert(timerSort);
        }
    }

}
