package cn.xlink.telinkoffical.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
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
import cn.xlink.telinkoffical.eventbus.ConnectStateEvent;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.model.Groups;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.model.Places;
import cn.xlink.telinkoffical.model.Scenes;
import cn.xlink.telinkoffical.utils.EventBusUtils;
import cn.xlink.telinkoffical.utils.GroupsDbUtils;
import cn.xlink.telinkoffical.utils.LightsDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.view.MyPopupWindow;
import cn.xlink.telinkoffical.view.RGBPickView;
import cn.xlink.telinkoffical.view.TitleBar;
import cn.xlink.telinkoffical.view.togglebutton.zcw.togglebutton.ToggleButton;

/**
 * Created by liucr on 2016/3/25.
 */
public class BlubbActivity extends BaseActivity implements EventListener<String> {

    private final int DELAY_TIME = 4000;

    private final int DELETE_SUCCESS = 0;

    private final int DELETE_NO_RESPOND = 1;

    @Bind(R.id.act_blub_title)
    TitleBar titleBar;

    @Bind(R.id.act_blub_topview)
    View topView;

    @Bind(R.id.timer_select_button)
    ToggleButton toggleButton;

    @Bind(R.id.act_blub_image)
    ImageView blubImage;

    @Bind(R.id.light_seekbar)
    SeekBar lightBar;

    @Bind(R.id.act_blub_temp_seekbar)
    SeekBar tempBar;

    @Bind(R.id.act_blub_rgbpickview)
    RGBPickView rgbPickView;

    private Light light;

    private int mesh = -1;

    private MyPopupWindow popupWindow;

    private boolean isClickDelete = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                LightsDbUtils.getInstance().updataOrInsert(light.getLightSort());
                DataToHostManage.updataCurToHost();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_blub);
        EventBusUtils.getInstance().addEventListener(NotificationEvent.ONLINE_STATUS, this);
        EventBusUtils.getInstance().addEventListener(ConnectStateEvent.ConnectStateEvent, this);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        titleBar.getCenterText().setText(light.getLightSort().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBusUtils.getInstance().removeEventListener(this);
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
        mesh = getIntent().getIntExtra(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH, -1);
        light = Lights.getInstance().getByMeshAddress(mesh);
        if (light == null) {
            finish();
        }
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(light.getLightSort().getName());
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

        if (light.getLightSort().getIsShowOnHomeScreen()) {
            toggleButton.setToggleOn();
        } else {
            toggleButton.setToggleOff();
        }

        toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                light.getLightSort().setIsAlone(false);
                light.getLightSort().setIsShowOnHomeScreen(on);
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
                light.brightness = seekBar.getProgress() + 5;
                CmdManage.setLightLum(light);
            }
        });

        tempBar.setProgress(50);
        tempBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                light.temperature = seekBar.getProgress() + 5;
                CmdManage.changeLightCT(light);
            }
        });

        rgbPickView.setOnColorChangedListener(new RGBPickView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int pixel) {
                if (pixel == 0) {
                    pixel = Color.WHITE;
                }
                light.color = pixel;
                CmdManage.changeLightColor(light, pixel);
            }

            @Override
            public void onMoveColor(int pixel) {

            }
        });

        initPopUpWindow();
        updataUI();

//        if(light.getLightSort().getLightType() == 0){

//        }else if(light.getLightSort().getLightType() == 4){
        rgbPickView.setVisibility(View.VISIBLE);
//        }else if(light.getLightSort().getLightType() == 5){
        tempBar.setVisibility(View.VISIBLE);
//        }

        if (Places.getInstance().curPlaceIsShare()) {
            titleBar.getRightItem().setVisibility(View.INVISIBLE);
            topView.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化更多
     */
    private void initPopUpWindow() {
        final List<String> strings = new ArrayList<>();

        strings.add(getString(R.string.rename));
        if (light.status != ConnectionStatus.OFFLINE) {
            strings.add(getString(R.string.join_group));
        }
        strings.add(getString(R.string.delete_blub));

        popupWindow = new MyPopupWindow(this, strings, 0, new PopWindowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                if (strings.get(position).equals(getString(R.string.rename))) {
                    bundle.putString(TelinkCommon.ACTIVITY_TYPE, TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH);
                    bundle.putInt(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH, light.getLightSort().getMeshAddress());
                    openActivity(RenameActivity.class, bundle);
                } else if (strings.get(position).equals(getString(R.string.join_group))) {
                    bundle.putInt(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH, light.getLightSort().getMeshAddress());
                    openActivity(JoinGroupActivity.class, bundle);
                } else if (strings.get(position).equals(getString(R.string.delete_blub))) {
                    showDeleteTips();
                }
                popupWindow.dismiss();
            }
        });
    }

    @OnClick(R.id.act_blub_image)
    void ClickImage() {
        CmdManage.changeLightStatus(light);
    }

    private void updataUI() {
        blubImage.setImageResource(light.getStatusBigIcon());
        lightBar.setProgress(light.brightness - 5);

        if (light.status == ConnectionStatus.ON) {
            lightBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_on));
            lightBar.setThumb(getResources().getDrawable(R.mipmap.thumb_light));
            lightBar.setEnabled(true);
            tempBar.setEnabled(true);
            rgbPickView.setIsOpen(true);
        } else {
            lightBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_off));
            lightBar.setThumb(getResources().getDrawable(R.mipmap.thumb_unlight));
            lightBar.setEnabled(false);
            tempBar.setEnabled(false);
            rgbPickView.setIsOpen(false);
        }
    }

    private void showDeleteTips() {
        showTipsDialog(getString(R.string.delete_blub) + "\n" + light.getLightSort().getName(), getString(R.string.enter),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isClickDelete = true;
                        if (light.status == ConnectionStatus.OFFLINE) {
                            CmdManage.kickOut(light);
                            deleteLocalData();
                        } else {
                            hideDialog();
                            waitingDialog.setCancelable(false);
                            showWaitingDialog(getString(R.string.blub_deleting_tips));
                            waitingDialog.setOnDismissListener(onDismissListener);
                            CmdManage.kickOut(light);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CmdManage.kickOut(light);
                                }
                            }, 2000);
//                            handler.sendEmptyMessageDelayed(DELETE_NO_RESPOND, DELAY_TIME);
                        }
                    }
                });

    }

    private DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            isClickDelete = false;
        }
    };

    private void deleteLocalData() {
        Groups.getInstance().removeLight(light);
        Scenes.getInstance().removeLight(light);

        Lights.getInstance().remove(light);
        LightsDbUtils.getInstance().deleteLight(light.getLightSort());
        DataToHostManage.updataCurToHost();
        finish();
    }

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(NotificationEvent.ONLINE_STATUS)) {
            updataUI();
            if (isClickDelete && light.status == ConnectionStatus.OFFLINE) {
                handler.removeMessages(DELETE_NO_RESPOND);
                deleteLocalData();
            }
        } else if (event.getType().equals(ConnectStateEvent.ConnectStateEvent)) {
            if (isClickDelete && light.status == ConnectionStatus.OFFLINE) {
                handler.removeMessages(DELETE_NO_RESPOND);
                deleteLocalData();
            }
        }
    }
}
