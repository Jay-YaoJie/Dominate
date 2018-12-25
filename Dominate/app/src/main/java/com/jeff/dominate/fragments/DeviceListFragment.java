package com.jeff.dominate.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jeff.dominate.R;
import com.jeff.dominate.activity.DeviceSettingActivity;
import com.jeff.dominate.model.Light;

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：设备控制
 */
public final class DeviceListFragment extends Fragment {

    private Activity mContext;

    // interval on off test
    private Handler mIntervalHandler;

    //组设备，单个设备
    ListView listView1, listView2;
    private BaseAdapters.DeviceListAdapter adapter2;
    private BaseAdapters.GroupListAdapter adapter1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device2, null);
        listView1 = view.findViewById(R.id.devicelist);
        listView2 = view.findViewById(R.id.devicelist2);
        listView2.setAdapter(this.adapter2);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),
                        DeviceSettingActivity.class);
                Light light = adapter2.getItem(position);
                intent.putExtra("meshAddress", light.meshAddress);
                startActivity(intent);
            }
        });


        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this.getActivity();
        this.adapter2 = new BaseAdapters.DeviceListAdapter(mContext, true);
        mIntervalHandler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIntervalHandler.removeCallbacksAndMessages(null);
    }


    public void addDevice(Light light) {
        this.adapter2.add(light);
    }

    public Light getDevice(int meshAddress) {
        return this.adapter2.get(meshAddress);
    }

    public void notifyDataSetChanged() {
        if (this.adapter2 != null)
            this.adapter2.notifyDataSetChanged();
    }


}
