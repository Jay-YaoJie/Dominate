package com.jeff.dominatelight.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.OnClick;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.activity.SceneActivity;
import com.jeff.dominatelight.manage.CmdManage;
import com.jeff.dominatelight.model.Light;
import com.jeff.dominatelight.view.RGBPickView;
import com.telink.bluetooth.light.ConnectionStatus;

import java.util.List;


/**
 * Created by liucr on 2016/3/29.
 */
public class ActionDetailFragment extends BaseFragment {

    private View view;

    @BindView(R.id.act_action_image)
    ImageView blubImage;

    @BindView(R.id.light_seekbar)
    SeekBar lightBar;

    @BindView(R.id.act_action_temp_seekbar)
    SeekBar tempBar;

    @BindView(R.id.act_action_rgbpickview)
    RGBPickView rgbPickView;

    private Light light;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fgt_action_detail, container, false);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {

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
                light.color = pixel;
                CmdManage.changeLightColor(light, pixel);
            }

            @Override
            public void onMoveColor(int pixel) {

            }
        });

        if(light != null){
            notifData();
        }
    }

    @OnClick(R.id.act_action_image)
    void ClickImage(){
        if(light.status == ConnectionStatus.ON){
            CmdManage.setLightStatus(light, ConnectionStatus.OFF);
            light.status = ConnectionStatus.OFF;
        }else {
            CmdManage.setLightStatus(light, ConnectionStatus.ON);
            light.status = ConnectionStatus.ON;
        }
        notifData();
    }

    public void notifData(){
        blubImage.setImageResource(light.getStatusBigIcon());
        lightBar.setProgress(light.brightness - 5);
        tempBar.setProgress(light.temperature);

        if(light.status == ConnectionStatus.ON){
            lightBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_on));
            lightBar.setThumb(getResources().getDrawable(R.mipmap.thumb_light));
            lightBar.setEnabled(true);
        }else {
            lightBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_off));
            lightBar.setThumb(getResources().getDrawable(R.mipmap.thumb_unlight));
            lightBar.setEnabled(false);
        }

//        if(light.getLightSort().getLightType() == 0){
//
//        }else if(light.getLightSort().getLightType() == 4){
//            rgbPickView.setVisibility(View.VISIBLE);
//            tempBar.setVisibility(View.GONE);
//        }else if(light.getLightSort().getLightType() == 5){
//            tempBar.setVisibility(View.VISIBLE);
//            rgbPickView.setVisibility(View.GONE);
//        }
    }

    public void setCurPosition(int position){
        light = getSelectList().get(position);
        rgbPickView.setCenter();
        notifData();
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
