package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.telink.bluetooth.Command;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.bean.greenDao.SceneTimerSort;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.model.Scene;
import cn.xlink.telinkoffical.model.Scenes;
import cn.xlink.telinkoffical.utils.SceneActionsDbUtils;
import cn.xlink.telinkoffical.utils.SceneTimersDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.utils.TelinkTimerUtils;
import cn.xlink.telinkoffical.view.MyScrollview;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/3/30.
 */
public class NewTimerActivity extends BaseActivity implements EventListener<String> {

    public static final int delay = 1000;

    public static final String TYPE_NEW = "type_new";

    public static final String TYPE_EDIT = "type_edit";

    @Bind(R.id.fgt_new_timer_titlebar)
    TitleBar titleBar;

    @Bind(R.id.time_select_time)
    WheelView selectTime;

    @Bind(R.id.time_select_hour)
    WheelView selectHour;

    @Bind(R.id.time_select_min)
    WheelView selectMin;

    @Bind(R.id.time_select_list)
    RecyclerView recyclerView;

    private CommonAdapter<String> timerAdapter;

    private List<String> days = new ArrayList<>();

    private List<Integer> selectDays = new ArrayList<>();

    private String curType;

    private String time;

    private Scene scene;

    private List<SceneTimerSort> timerSorts;

    private int curSelect = 0;

    private boolean isSecondTime = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeCallbacks(runnableDelay);
            if (msg.what == 0) {          //成功

                SceneTimersDbUtils.getInstance().updataOrInsert(timerSorts.get(curSelect));

                if (curSelect == timerSorts.size() - 1) {
                    successFinish();
                } else {
                    curSelect++;
                    sendData(curSelect);
                    isSecondTime = false;
                    handler.postDelayed(runnableDelay, delay);
                }
            } else if (msg.what == 1) {       //第一次失败
                sendData(curSelect);
                isSecondTime = true;
                handler.postDelayed(runnableDelay, delay);
            } else if (msg.what == 2) {                 //失败了
                if (curSelect >= timerSorts.size() - 1) {
                    SceneTimersDbUtils.getInstance().updataOrInsert(timerSorts.get(curSelect));
                    successFinish();
                } else {
                    //失败了也添加进去。。。
                    SceneTimersDbUtils.getInstance().updataOrInsert(timerSorts.get(curSelect));

                    curSelect++;
                    sendData(curSelect);
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
        setContentView(R.layout.fgt_new_timer);
        MyApp.getApp().addEventListener(NotificationEvent.GET_ALARM, this);
        ButterKnife.bind(this);
        initData();
        initView();
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
    protected void initView() {

        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.getRightText().setText(getString(R.string.finish));
        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFinish();
            }
        });

        ArrayList<String> times = new ArrayList<>();
        times.add(getString(R.string.timer_am));
        times.add(getString(R.string.timer_pm));
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<>(times, times.size());
        selectTime.setGravity(Gravity.RIGHT);
        selectTime.setAdapter(arrayWheelAdapter);
        selectTime.setCyclic(false);
        selectTime.setTextSize(22);

        NumericWheelAdapter numericWheelAdapter3 = new NumericWheelAdapter(1, 12);
        selectHour.setAdapter(numericWheelAdapter3);
        selectHour.setCyclic(true);
        selectHour.setTextSize(22);
        selectHour.setGravity(Gravity.CENTER);

        ArrayList<String> minutes = new ArrayList<>();
        for(int i = 0; i<60;i++){
            if(i<10){
                minutes.add("0"+i);
            }else {
                minutes.add(i+"");
            }
        }
        ArrayWheelAdapter<String> arrayWheelAdapter4 = new ArrayWheelAdapter<>(minutes, minutes.size());
        selectMin.setAdapter(arrayWheelAdapter4);
        selectMin.setCyclic(true);
        selectMin.setTextSize(22);
        selectMin.setGravity(Gravity.LEFT);

        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        timerAdapter = new CommonAdapter<String>(R.layout.view_common_item, days) {
            @Override
            public void convert(RecyclerViewHolder holder, String s, int position) {
                holder.getView(R.id.commom_left_image).setVisibility(View.GONE);

                holder.setText(R.id.commom_center_text, s);
                ImageView select = holder.getView(R.id.commom_right_image);
                if (selectDays.get(position) == 1) {
                    select.setVisibility(View.VISIBLE);
                } else {
                    select.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onItemClick(View v, int position) {
                super.onItemClick(v, position);
                if (selectDays.get(position) == 0) {
                    selectDays.set(position, 1);
                } else {
                    selectDays.set(position, 0);
                }
                timerAdapter.notifyDataSetChanged();
            }
        };
        recyclerView.setAdapter(timerAdapter);

        if (curType.equals(TYPE_EDIT)) {
            initEditView();
        } else {
            initNewView();
        }

    }

    @Override
    protected void initData() {

        int sceneId = getIntent().getIntExtra(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, -1);
        int timerId = getIntent().getIntExtra(TelinkCommon.ACTIVITY_TYPE_TIME_ID, -1);
        scene = Scenes.getInstance().getById(sceneId);
        timerSorts = SceneTimersDbUtils.getInstance().getSceneTimerSort(sceneId, timerId);

        if (timerSorts.size() == 0) {
            curType = TYPE_NEW;
            initNewTimer();
        } else {
            curType = TYPE_EDIT;
            initEditTimer();
        }

        days.clear();
        days.add(getString(R.string.monday));
        days.add(getString(R.string.tuesday));
        days.add(getString(R.string.wednesday));
        days.add(getString(R.string.thursday));
        days.add(getString(R.string.friday));
        days.add(getString(R.string.saturday));
        days.add(getString(R.string.sunday));
    }

    /**
     * 初始化新建数据
     */
    private void initNewTimer() {
        timerSorts = new ArrayList<>();
        timerSorts = TelinkTimerUtils.newTimerSortList(scene);
        selectDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            selectDays.add(0);
        }
    }

    /**
     * 初始化新建UI
     */
    private void initNewView() {

        titleBar.getCenterText().setText(getString(R.string.add_timer));

        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        selectTime.setCurrentItem(t.hour >= 12 ? 1 : 0);
        selectHour.setCurrentItem((t.hour > 12 ? (t.hour - 12) : t.hour) - 1);
        selectMin.setCurrentItem(t.minute - 1);
    }

    /**
     * 初始化修改数据
     */
    private void initEditTimer() {
        if (timerSorts.get(0).getTimerType() == 0) {
            selectDays = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                selectDays.add(0);
            }
        } else {
            selectDays = TelinkTimerUtils.getRepeatTimer(timerSorts.get(0).getWorkDay());
        }
    }

    /**
     * 初始化修改UI
     */
    private void initEditView() {

        titleBar.getCenterText().setText(getString(R.string.edit_timer));

        int hour = timerSorts.get(0).getHour();
        int min = timerSorts.get(0).getMinute();

        selectTime.setCurrentItem(hour >= 12 ? 1 : 0);
        selectHour.setCurrentItem((hour > 12 ? (hour - 12) : hour) - 1);
        selectMin.setCurrentItem(min - 1);
    }

    /**
     * 创建/编辑完成
     */

    private int hour;
    private int min;
    private int timerType;
    private int workData;

    private void editFinish() {
        int month;
        int day;
        timerType = 0;
        workData = 0;
        hour = selectHour.getCurrentItem() + 1;
        min = selectMin.getCurrentItem();

        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow();
        day = t.monthDay;
        month = t.month + 1;

        if (selectTime.getCurrentItem() == 1) {
            if (hour != 12) {
                hour = hour + 12;
            }
        }

        //判定类型
        for (int a : selectDays) {
            if (a == 1) {
                timerType = 1;
                break;
            }
        }

        //通过类型设置执行日期
        if (timerType == 0) {
            int curMin = t.hour * 60 + t.minute;
            int workMin = hour * 60 + min;
            workData = month * 100 + ((curMin > workMin) ? (day + 1) : day);
        } else {
            workData = TelinkTimerUtils.getRepeatTimer(selectDays);
        }

        showWaitingDialog(null);
        //等待回调确认已添加
        isSecondTime = false;
        sendData(curSelect);
        handler.postDelayed(runnableDelay, delay);
    }

    private void sendData(final int select){
        timerSorts.get(select).setHour(hour);
        timerSorts.get(select).setMinute(min);
        timerSorts.get(select).setTimerType(timerType);
        timerSorts.get(select).setWorkDay(workData);
        timerSorts.get(select).setIsEnable(true);

        if (curType.equals(TYPE_EDIT)) {
            CmdManage.addEditAlarm(timerSorts.get(select), scene.getSceneSort().getSceneId(), false);
        } else {
            CmdManage.addEditAlarm(timerSorts.get(select), scene.getSceneSort().getSceneId(), true);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CmdManage.getAlarm(timerSorts.get(select));
            }
        }, delay/2);

    }

    private void successFinish(){
        DataToHostManage.updataCurToHost();
        finish();
    }

    @Override
    public void performed(Event<String> event) {
        if(event.getType().equals(NotificationEvent.GET_ALARM)){
            int src = ((NotificationEvent) event).getArgs().src;
            int lightMesh = src < 0 ? 256 + src : src;
            byte[] params = ((NotificationEvent) event).getArgs().params;

            if(lightMesh == timerSorts.get(curSelect).getDeviceMesh()){
                handler.sendEmptyMessage(0);
            }
        }
    }
}
