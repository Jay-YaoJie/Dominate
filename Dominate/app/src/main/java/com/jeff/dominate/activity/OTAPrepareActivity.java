package com.jeff.dominate.activity;/*
package com.telink.bluetooth.light.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.R;
import com.telink.bluetooth.light.TelinkBaseActivity;
import com.telink.bluetooth.light.TelinkLightApplication;
import com.telink.bluetooth.light.TelinkLightService;
import com.telink.bluetooth.light.model.Light;
import com.telink.bluetooth.light.model.Lights;
import com.telink.util.Event;
import com.telink.util.EventListener;
import com.telink.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

*/
 /**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：OTA准备页，主要 选择对应bin档并获取mesh内各设备的version信息;
 */
/*


public class OTAPrepareActivity extends TelinkBaseActivity implements View.OnClickListener, EventListener<String> {
    private static final int REQUEST_CODE_CHOOSE_FILE = 0x01;
    private TextView tv_file;
    private TextView tv_version;
    private Button btn_next;
    private String mPath;
    private List<Light> devices = new ArrayList<>();
    private String mFileVersion;
    private Handler mHandler = new Handler();
    private OTADeviceAdapter mListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota_prepare);
        tv_file = (TextView) findViewById(R.id.file);
        btn_next = (Button) findViewById(R.id.btn_next);
        tv_version = (TextView) findViewById(R.id.tv_version);
        ListView lv_device = (ListView) findViewById(R.id.lv_device);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.select).setOnClickListener(this);
        btn_next.setOnClickListener(this);
        mListAdapter = new OTADeviceAdapter();
        lv_device.setAdapter(mListAdapter);
        TelinkLightApplication.getApp().addEventListener(NotificationEvent.GET_DEVICE_STATE, this);
        TelinkLightApplication.getApp().addEventListener(DeviceEvent.STATUS_CHANGED, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_CHOOSE_FILE) return;
        if (resultCode == RESULT_OK) {
            mPath = data.getStringExtra("path");
            tv_file.setText(getString(R.string.select_file, mPath));
//            parseVersion();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelinkLightApplication.getApp().removeEventListener(this);
    }






*/
/*    private void getVersion() {
        byte opcode = (byte) 0xC7;
        int address = 0xFFFF;
        byte[] params = new byte[]{0x10, 0x00};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);

        btn_next.setEnabled(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btn_next.setEnabled(true);
            }
        }, 2 * 1000);
    }*//*


    private void next() {
        Intent intent = new Intent(this, MeshOTAActivity.class);
        intent.putExtra("path", mPath);
//        intent.putExtra("target_version", mFileVersion);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_next:
                if (this.mPath == null || this.mPath.equals("")) {
                    Toast.makeText(this, "Select File First", Toast.LENGTH_SHORT).show();
                    return;
                }
                // check devices version
                for (Light light : devices) {
                    if (!light.version.equals(mFileVersion)) {
                        next();
                        return;
                    }
                }
                Toast.makeText(this, "No Device Need OTA", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case NotificationEvent.GET_DEVICE_STATE:
                byte[] data = ((NotificationEvent) event).getArgs().params;
                if (data[0] == NotificationEvent.DATA_GET_VERSION) {
                    String version = Strings.bytesToString(Arrays.copyOfRange(data, 1, 5));
                    int meshAddress = ((NotificationEvent) event).getArgs().src;

                    Light light = Lights.getInstance().getByMeshAddress(meshAddress);
                    light.version = version;
                    devices.add(light);
                    mHandler.post(uiTask);
                   */
/* if (!version.equals(mFileVersion) && !devices.contains(light)) {
                        devices.add(light);
                        mHandler.post(uiTask);
                    }*//*

                    TelinkLog.i("OTAPrepareActivity#GET_DEVICE_STATE#src:" + meshAddress + " get version success: " + version);
                }*/
/* else if (data[0] == NotificationEvent.DATA_GET_MESH_OTA_PROGRESS) {
                    TelinkLog.i("ota progress: " + data[1]);
                }*//*

                break;

            case DeviceEvent.STATUS_CHANGED:
                if (((DeviceEvent) event).getArgs().status == LightAdapter.STATUS_LOGOUT) {
                    // 断连时将模式置为空闲，避免反复扫描重连
                    TelinkLightService.Instance().idleMode(true);
                    showToast("Mesh Logout!");
                    finish();
                }
                break;
        }
    }

    private Runnable uiTask = new Runnable() {
        @Override
        public void run() {
            mListAdapter.notifyDataSetChanged();
        }
    };

    private class OTADeviceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return devices == null ? 0 : devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return devices.get(position).meshAddress;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_ota_device, null);
                holder = new ViewHolder();
                holder.deviceName = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.deviceName.setText(devices.get(position).getLabel1() + "\t/\t" + devices.get(position).version);
            return convertView;
        }

        class ViewHolder {
            TextView deviceName;
        }
    }
}
*/
