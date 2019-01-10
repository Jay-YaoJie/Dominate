package cn.xlink.telinkoffical.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.telink.bluetooth.light.ConnectionStatus;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.activity.SceneActivity;
import cn.xlink.telinkoffical.manage.CmdManage;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.view.RGBPickView;

/**
 * Created by liucr on 2016/3/29.
 */
public class ActionDetailFragment extends BaseFragment {

    private View view;

    @Bind(R.id.act_action_image)
    ImageView blubImage;

    @Bind(R.id.light_seekbar)
    SeekBar lightBar;

    @Bind(R.id.act_action_temp_seekbar)
    SeekBar tempBar;

    @Bind(R.id.act_action_rgbpickview)
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
