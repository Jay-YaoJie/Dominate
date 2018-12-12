package com.telink.bluetooth.light.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.telink.bluetooth.light.R;
import com.telink.bluetooth.light.TelinkBaseActivity;
import com.telink.bluetooth.light.TelinkLightApplication;
import com.telink.bluetooth.light.TelinkLightService;
import com.telink.bluetooth.light.model.Light;

import java.util.List;

public class OtaDeviceListActivity extends TelinkBaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private TelinkLightApplication mApp;
    private List<Light> mDevices;
    private GridView mDeviceListView;
    private Button mNext;
    private DeviceListAdapter mDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelinkLightService.Instance().idleMode(true);
        setContentView(R.layout.activity_ota_device_list);
        mApp = (TelinkLightApplication) this.getApplication();
        mDevices = mApp.getMesh().devices;
        normal();
        mDeviceAdapter = new DeviceListAdapter();
        mDeviceListView = (GridView) findViewById(R.id.devices);
        mDeviceListView.setAdapter(mDeviceAdapter);
        mDeviceListView.setOnItemClickListener(this);

        mNext = (Button) findViewById(R.id.next);
        mNext.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void normal() {
        for (Light deviceInfo : mDevices) {
            deviceInfo.selected = false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Light deviceInfo = this.mDeviceAdapter.getItem(position);
        deviceInfo.selected = !deviceInfo.selected;
        DeviceItemHolder holder = (DeviceItemHolder) view.getTag();
        holder.selected.setChecked(deviceInfo.selected);
    }

    @Override
    public void onClick(View v) {
        if (v == mNext) {
            Intent intent = new Intent(this, BatchOtaActivity.class);
            startActivity(intent);
        }
    }

    private static class DeviceItemHolder {
        public ImageView icon;
        public TextView txtName;
        public TextView txtMac;
        public CheckBox selected;
    }

    private class DeviceListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDevices == null ? 0 : mDevices.size();
        }

        @Override
        public Light getItem(int position) {
            return mDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DeviceItemHolder holder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.device_item, null);
                ImageView icon = (ImageView) convertView
                        .findViewById(R.id.img_icon);
                TextView txtName = (TextView) convertView
                        .findViewById(R.id.txt_name);
                TextView txtMac = (TextView) convertView.findViewById(R.id.txt_mac);
                CheckBox selected = (CheckBox) convertView.findViewById(R.id.selected);

                selected.setVisibility(View.VISIBLE);
                holder = new DeviceItemHolder();

                holder.icon = icon;
                holder.txtName = txtName;
                holder.txtMac = txtMac;
                holder.selected = selected;
                holder.txtMac.setVisibility(View.VISIBLE);
                convertView.setTag(holder);
            } else {
                holder = (DeviceItemHolder) convertView.getTag();
            }

            Light deviceInfo = this.getItem(position);

            holder.txtName.setText(deviceInfo.deviceName);
            holder.txtMac.setText(deviceInfo.macAddress);
            holder.icon.setImageResource(R.drawable.icon_light_on);
            holder.selected.setChecked(deviceInfo.selected);

            return convertView;
        }
    }
}
