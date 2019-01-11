package com.jeff.dominatelight.fragment;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.activity.SceneActivity;
import com.jeff.dominatelight.adapter.recycler_adapter.CommonAdapter;
import com.jeff.dominatelight.adapter.recycler_adapter.RecyclerViewHolder;
import com.jeff.dominatelight.manage.CmdManage;
import com.jeff.dominatelight.manage.DataToHostManage;
import com.jeff.dominatelight.model.Light;
import com.jeff.dominatelight.model.Lights;
import com.jeff.dominatelight.utils.ScenesDbUtils;
import com.jeff.dominatelight.view.MyScrollview;
import com.jeff.dominatelight.view.togglebutton.zcw.togglebutton.ToggleButton;
import com.telink.bluetooth.light.ConnectionStatus;

import java.util.List;

/**
 * Created by liucr on 2016/3/29.
 */
public class SceneActionFragment extends BaseFragment {

    private View view;

    @BindView(R.id.view_scene_action_scrollview)
    MyScrollview scrollview;

    @BindView(R.id.act_scene_devicelist)
    RecyclerView recyclerView;

    @BindView(R.id.act_scene_listtips)
    TextView tipsTextView;

    @BindView(R.id.scene_select_button)
    ToggleButton toggleButton;

    @BindView(R.id.view_scene_bottom)
    View viewButton;

    private CommonAdapter<Light> deviceListAdapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                ScenesDbUtils.getInstance().updataOrInsert(getNewSceneActivity().getScene().getSceneSort());
                DataToHostManage.updataCurToHost();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.view_scene_action, container, false);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
        handler = null;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {

        if (getNewSceneActivity().getScene().getSceneSort().getIsShowOnHomeScreen()) {
            toggleButton.setToggleOn();
        } else {
            toggleButton.setToggleOff();
        }

        toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                getNewSceneActivity().getScene().getSceneSort().setIsShowOnHomeScreen(on);
                if (getNewSceneActivity().getCurType().equals(SceneActivity.EDIT_TYPE)) {
                    handler.removeMessages(0);
                    handler.sendEmptyMessageDelayed(0, 1000);
                }
            }
        });

        initDevicesView();
        if (getNewSceneActivity().getCurType().equals(SceneActivity.EDIT_TYPE)) {
            viewButton.setVisibility(View.VISIBLE);
            tipsTextView.setText(getString(R.string.edit_scene_devices_tips));
        } else {
            tipsTextView.setText(getString(R.string.new_scene_devices_tips));
        }

        WindowManager wm = getActivity().getWindowManager();
        int height = wm.getDefaultDisplay().getHeight() - getStatusBarHeight() - getNewSceneActivity().getTitleHeight();

        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        viewButton.measure(0, h);
        scrollview.measure(0, h);
        int buttonHeight = viewButton.getMeasuredHeight();
        int scrollviewHeight = scrollview.getMeasuredHeight();

        if (buttonHeight + scrollviewHeight > height) {
            scrollview.getLayoutParams().height = height - buttonHeight;
        }

        //置顶
        scrollview.smoothScrollTo(0,0);

    }

    //获取状态栏高度
    public int getStatusBarHeight() {

        if(Build.VERSION.SDK_INT > 18){
            return 0;
        }

        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initDevicesView() {
        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);
        deviceListAdapter = new CommonAdapter<Light>(R.layout.view_device_item, getSelectList()) {
            @Override
            public void convert(RecyclerViewHolder holder, final Light light, final int position) {
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

                        if (light.status == ConnectionStatus.ON) {
                            CmdManage.setLightStatus(light, ConnectionStatus.OFF);
                            light.status = ConnectionStatus.OFF;
                        } else if (light.status == ConnectionStatus.OFF) {
                            CmdManage.setLightStatus(light, ConnectionStatus.ON);
                            light.status = ConnectionStatus.ON;
                            if(light.brightness == 0){
                                light.brightness = Lights.getInstance().getByMeshAddress(light.getLightSort().getMeshAddress()).brightness;
                            }
                            if(light.brightness == 0){
                                light.brightness = 5;
                            }
                        }

                        notifyDataSetChanged();
                    }
                });

                rightView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getNewSceneActivity().goToActionDetail(position);
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

    @OnClick(R.id.view_scene_finish)
    void ClickFinish() {
        getNewSceneActivity().successEdit();
    }

    public void notifData() {
        if (deviceListAdapter != null) {
            deviceListAdapter.notifyDataSetChanged();
        }
    }

    private List<Light> getLightList() {
        return getNewSceneActivity().getLightList();
    }

    private List<Light> getSelectList() {
        return getNewSceneActivity().getSelectLightList();
    }

    private SceneActivity getNewSceneActivity() {
        return (SceneActivity) getActivity();
    }
}
