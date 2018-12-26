package com.jeff.dominate.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.jeff.dominate.R;
import com.jeff.dominate.TelinkBaseActivity;
import com.jeff.dominate.TelinkLightApplication;
import com.jeff.dominate.TelinkLightService;
import com.jeff.dominate.activity.OtaActivity;
import com.jeff.dominate.model.Light;
import com.jeff.dominate.model.Lights;
import com.jeff.dominate.widget.ColorPicker;


public final class DeviceSettingFragment extends Fragment implements View.OnClickListener {

    private   String tag = "DeviceSettingFragment";

    public int meshAddress;

    private SeekBar brightnessBar;
    private SeekBar temperatureBar;
    private ColorPicker colorPicker;
    private Button remove, ota;

    private OnSeekBarChangeListener barChangeListener = new OnSeekBarChangeListener() {

        private long preTime;
        private int delayTime = 100;

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            this.onValueChange(seekBar, seekBar.getProgress(), true);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            this.preTime = System.currentTimeMillis();
            this.onValueChange(seekBar, seekBar.getProgress(), true);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

           /* if (progress % 5 != 0)
                return;

            long currentTime = System.currentTimeMillis();

            if ((currentTime - this.preTime) >= this.delayTime) {
                this.preTime = currentTime;*/
            this.onValueChange(seekBar, progress, false);
            //}
        }

        private void onValueChange(View view, int progress, boolean immediate) {

            int addr = meshAddress;
            byte opcode;
            byte[] params;
            if (view == brightnessBar) {
                progress += 5;
                opcode = (byte) 0xD2;
                params = new byte[]{(byte) progress};

                TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);

            } else if (view == temperatureBar) {

                opcode = (byte) 0xE2;
                params = new byte[]{0x05, (byte) progress};

                TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);
            }
        }
    };

    private ColorPicker.OnColorChangeListener colorChangedListener = new ColorPicker.OnColorChangeListener() {

        private long preTime;
        private int delayTime = 100;

        @Override
        public void onStartTrackingTouch(ColorPicker view) {
            this.preTime = System.currentTimeMillis();
            this.changeColor(view.getColor());
        }

        @Override
        public void onStopTrackingTouch(ColorPicker view) {
            this.changeColor(view.getColor());
        }

        @Override
        public void onColorChanged(ColorPicker view, int color) {

            long currentTime = System.currentTimeMillis();

            if ((currentTime - this.preTime) >= this.delayTime) {
                this.preTime = currentTime;
                this.changeColor(color);
            }
        }

        private void changeColor(int color) {

            byte red = (byte) (color >> 16 & 0xFF);
            byte green = (byte) (color >> 8 & 0xFF);
            byte blue = (byte) (color & 0xFF);

            int addr = meshAddress;
            byte opcode = (byte) 0xE2;
            byte[] params = new byte[]{0x04, red, green, blue};

            TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);
        }
    };
    private TelinkLightApplication mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mApp = (TelinkLightApplication) this.getActivity().getApplication();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_device_setting, null);

        this.brightnessBar = (SeekBar) view.findViewById(R.id.sb_brightness);
        this.temperatureBar = (SeekBar) view.findViewById(R.id.sb_temperature);

        this.brightnessBar.setOnSeekBarChangeListener(this.barChangeListener);
        this.temperatureBar.setOnSeekBarChangeListener(this.barChangeListener);

        this.colorPicker = (ColorPicker) view.findViewById(R.id.color_picker);
        this.colorPicker.setOnColorChangeListener(this.colorChangedListener);

        this.remove = (Button) view.findViewById(R.id.btn_remove);
        this.remove.setOnClickListener(this);

        this.ota = (Button) view.findViewById(R.id.btn_ota);
        this.ota.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == this.remove) {
            byte opcode = (byte) 0xE3;
            byte[] params = new byte[]{0x01};
            TelinkLightService.Instance().sendCommandNoResponse(opcode, meshAddress, params);
            Lights.getInstance().remove(Lights.getInstance().getByMeshAddress(meshAddress));
            if (TelinkLightApplication.getApp().getMesh().removeDeviceByMeshAddress(meshAddress)) {
                TelinkLightApplication.getApp().getMesh().saveOrUpdate(getActivity());
            }

//            getActivity().finish();
        } else if (v == this.ota) {
            Light light = Lights.getInstance().getByMeshAddress(meshAddress);
            if (light == null || TextUtils.isEmpty(light.macAddress)) {
                ((TelinkBaseActivity) getActivity()).showToast("error! Lack of mac");
                return;
            }

            startActivity(new Intent(getActivity(), OtaActivity.class).putExtra("meshAddress", meshAddress));
        }
    }
}
