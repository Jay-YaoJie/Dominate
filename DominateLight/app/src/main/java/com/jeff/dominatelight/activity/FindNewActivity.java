package com.jeff.dominatelight.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.jeff.dominatelight.MyApp;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.adapter.IntroductionPagerAdapter;
import com.jeff.dominatelight.adapter.recycler_adapter.CommonAdapter;
import com.jeff.dominatelight.adapter.recycler_adapter.RecyclerViewHolder;
import com.jeff.dominatelight.bean.greenDao.PlaceSort;
import com.jeff.dominatelight.manage.DataToHostManage;
import com.jeff.dominatelight.model.Groups;
import com.jeff.dominatelight.model.Light;
import com.jeff.dominatelight.model.Lights;
import com.jeff.dominatelight.model.Places;
import com.jeff.dominatelight.service.TelinkLightService;
import com.jeff.dominatelight.utils.LogUtil;
import com.jeff.dominatelight.view.IndexViewPager;
import com.jeff.dominatelight.view.MaterialProgressDrawable;
import com.jeff.dominatelight.view.ProgressView;
import com.jeff.dominatelight.view.TitleBar;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeScanParameters;
import com.telink.bluetooth.light.LeUpdateParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liucr on 2016/3/24.
 */
public class FindNewActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.act_find_new_title)
    TitleBar titleBar;

    @BindView(R.id.act_findnew_viewpager)
    IndexViewPager viewPager;

    private TextView firstButton;

    private TextView secondButton;

    private TextView threeButton;

    private RecyclerView recyclerView;

    View vPageFirst;

    View vPageSecond;

    View vPageThree;

    private ImageView threeImage;

    private TextView threeText;

    private ImageView imageView;

    private ProgressView progressView;

    private CommonAdapter<Light> deviceListAdapter;

    private List<Light> scaneLights = new ArrayList<>();

    private static final int TIME = 17000;
    private static final int GOTO_MAINACTIVITY = 0;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_find_new);
        ButterKnife.bind(this);
        MyApp.getApp().setIsFindOrOta(true);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
        MyApp.getApp().removeEventListener(telinkEventListener);
        mHandlerTimeOut.removeMessages(GOTO_MAINACTIVITY);
        MyApp.getApp().setIsFindOrOta(false);
        MyApp.getApp().setCanShowConnectLoading(true);
    }

    @Override
    protected void initData() {

        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        if (blueadapter != null && !blueadapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

    }

    @Override
    protected void initView() {

        LayoutInflater inflater = getLayoutInflater();
        vPageFirst = inflater.inflate(R.layout.view_find_first, null);
        vPageSecond = inflater.inflate(R.layout.view_find_second, null);
        vPageThree = inflater.inflate(R.layout.view_find_three, null);
        viewPager.setAdapter(new IntroductionPagerAdapter(vPageFirst, vPageSecond, vPageThree));

        firstButton = (TextView) vPageFirst.findViewById(R.id.find_first_button);
        secondButton = (TextView) vPageSecond.findViewById(R.id.find_second_button);
        threeButton = (TextView) vPageThree.findViewById(R.id.find_three_button);
        threeImage = (ImageView) vPageThree.findViewById(R.id.find_three_image);
        threeText = (TextView) vPageThree.findViewById(R.id.find_three_tips);

        recyclerView = (RecyclerView) vPageSecond.findViewById(R.id.find_second_list);
        imageView = (ImageView) vPageSecond.findViewById(R.id.find_second_list_loading);
        progressView = new ProgressView(this, imageView);

        progressView.setBackgroundColor(Color.WHITE);
        //圈圈颜色,可以是多种颜色
        progressView.setColorSchemeColors(getResources().getColor(R.color.act_fgt_bg));
        //设置圈圈的各种大小
        progressView.updateSizes(MaterialProgressDrawable.LARGE);
        progressView.start();
        imageView.setImageDrawable(progressView);

        firstButton.setOnClickListener(this);
        secondButton.setOnClickListener(this);
        threeButton.setOnClickListener(this);

        secondButton.setVisibility(View.GONE);

        titleBar.getCenterText().setText(getString(R.string.find_new_title));

        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    titleBar.getLeftItem().setVisibility(View.INVISIBLE);
                }
                if (position == 1) {
                    initScan();
                    startScan(0);
                    mHandlerTimeOut.sendEmptyMessageDelayed(GOTO_MAINACTIVITY, TIME);
                    titleBar.getLeftItem().setVisibility(View.VISIBLE);
                } else if (position == 0) {
                    mHandlerTimeOut.removeMessages(GOTO_MAINACTIVITY);
                    MyApp.getApp().removeEventListener(telinkEventListener);
                    TelinkLightService.Instance().idleMode(true);
                    titleBar.getLeftItem().setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        deviceListAdapter = new CommonAdapter<Light>(R.layout.view_common_item, scaneLights) {
            @Override
            public void convert(RecyclerViewHolder holder, Light light, int position) {
                holder.setText(R.id.commom_center_text, light.getLightSort().getName());
                holder.setImageResource(R.id.commom_left_image, R.mipmap.icon_device);
            }

            @Override
            public void onItemClick(View v, int position) {
                super.onItemClick(v, position);
            }
        };

        recyclerView.setAdapter(deviceListAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_first_button:
                viewPager.setCurrentItem(1);
                break;
            case R.id.find_second_button:
                if (scaneLights.size() > 0) {
                    sceneFinish();
                }
                break;
            case R.id.find_three_button:
                if (threeButton.getText().equals(getString(R.string.retry))) {
                    viewPager.setCurrentItem(1);
                } else {
                    startActivity(new Intent(FindNewActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    finish();
                }
                break;
        }
    }

    @Override
    protected void back() {
        if (viewPager.getCurrentItem() == 0) {
            startActivity(new Intent(FindNewActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private void initScan() {
        MyApp.getApp().addEventListener(LeScanEvent.LE_SCAN, telinkEventListener);
        MyApp.getApp().addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, telinkEventListener);
        MyApp.getApp().addEventListener(DeviceEvent.STATUS_CHANGED, telinkEventListener);
        MyApp.getApp().addEventListener(MeshEvent.UPDATE_COMPLETED, telinkEventListener);
    }

    private void onLeScan(LeScanEvent event) {

        PlaceSort placeSort = Places.getInstance().getCurPlaceSort();
        LeUpdateParameters params = new LeUpdateParameters();
        params.setOldMeshName(placeSort.getFactoryName());
        params.setOldPassword(placeSort.getFactoryMeshKey());
        params.setNewMeshName(placeSort.getMeshAddress());
        params.setNewPassword(placeSort.getMeshKey());
        //修改灯的Mesh地址
        DeviceInfo deviceInfo = event.getArgs();
        deviceInfo.meshAddress = Lights.getInstance().getNextMeshAddress();
        params.setUpdateDeviceList(deviceInfo);
        TelinkLightService.Instance().idleMode(true);
        TelinkLightService.Instance().updateMesh(params);
    }

    private void onLeScanTimeout(LeScanEvent event) {
        LogUtil.e("onLeScanTimeout--> " + scaneLights.size());
        sceneFinish();
    }

    private void onDeviceStatusChanged(DeviceEvent event) {

        DeviceInfo deviceInfo = event.getArgs();

        switch (deviceInfo.status) {
            case LightAdapter.STATUS_UPDATE_MESH_COMPLETED:
                LogUtil.e("productUUID:  " + deviceInfo.macAddress + "-->" + deviceInfo.productUUID);
                int meshAddress = deviceInfo.meshAddress & 0xFF;
                Light scaneLight = getScanLight(meshAddress);
                if (scaneLight == null) {
                    String name = "Bulb-" + Integer.toString(meshAddress, 16).toUpperCase();
                    String macAddress = deviceInfo.macAddress;
                    String firmwareRevision = "";
                    int lightType = deviceInfo.productUUID;
                    try {
                        firmwareRevision = deviceInfo.firmwareRevision.replaceAll("[.]", "");
                        if (firmwareRevision.length() > 8) {
                            firmwareRevision = firmwareRevision.substring(0, 8);
                        }
                    } catch (Exception e) {
                        firmwareRevision = 20160526 + "";
                    }
                    scaneLight = new Light(name, macAddress, firmwareRevision, meshAddress, lightType);
                    if (!Lights.getInstance().contains(meshAddress)) {
                        LogUtil.e("productUUID:  " + deviceInfo.macAddress + " --> " + meshAddress);
                        scaneLights.add(scaneLight);
                        MyApp.getApp().setCheckTime(false);
                        Lights.getInstance().add(scaneLight);
                        Lights.getInstance().setCurMesh(meshAddress);
                        Groups.getInstance().addLightToGroup(scaneLight, 0xFFFF);
                        DataToHostManage.updataCurToHost();
//                        secondButton.setVisibility(View.VISIBLE);
                        notifyData();
                    }
                    mHandlerTimeOut.removeMessages(GOTO_MAINACTIVITY);
                    mHandlerTimeOut.sendEmptyMessageDelayed(GOTO_MAINACTIVITY, TIME);
                }
                this.startScan(1000);
                break;
            case LightAdapter.STATUS_UPDATE_MESH_FAILURE:
                this.startScan(1000);
                break;
        }
    }

    private Light getScanLight(int meshAddress) {
        for (Light light : scaneLights) {
            if (light.getLightSort().getMeshAddress() == meshAddress) {
                return light;
            }
        }
        return null;
    }

    private EventListener telinkEventListener = new EventListener<String>() {

        @Override
        public void performed(Event<String> event) {
            switch (event.getType()) {
                case LeScanEvent.LE_SCAN:
                    onLeScan((LeScanEvent) event);
                    break;
                case LeScanEvent.LE_SCAN_TIMEOUT:
                    onLeScanTimeout((LeScanEvent) event);
                    break;
                case DeviceEvent.STATUS_CHANGED:
                    onDeviceStatusChanged((DeviceEvent) event);
                    break;
            }
        }
    };

    private void startScan(int delay) {
        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PlaceSort placeSort = Places.getInstance().getCurPlaceSort();

                LeScanParameters params = Parameters.createScanParameters();
                params.setMeshName(placeSort.getFactoryName());
                //test
                params.setOutOfMeshName("out_of_mesh");
                params.setTimeoutSeconds(15);
                params.setScanMode(true);

                TelinkLightService.Instance().startScan(params);
            }
        }, delay);

    }

    private void notifyData() {
        deviceListAdapter.setData(scaneLights);
        deviceListAdapter.notifyDataSetChanged();
        LogUtil.e("scaneLights--> " + scaneLights.size());
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandlerTimeOut = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == GOTO_MAINACTIVITY) {
                LogUtil.e("mHandlerTimeOut--> " + scaneLights.size());
                sceneFinish();
            }
            super.handleMessage(msg);
        }

    };

    private void sceneFinish() {

        mHandlerTimeOut.removeMessages(GOTO_MAINACTIVITY);
        MyApp.getApp().removeEventListener(telinkEventListener);

        if (scaneLights.size() == 0) {
            threeImage.setImageResource(R.mipmap.icon_not_found);
            threeText.setText(getString(R.string.not_found_tips));
            threeButton.setText(getString(R.string.retry));
        } else {
            threeImage.setImageResource(R.mipmap.icon_success);
            threeText.setText(getString(R.string.add_success_tips));
            threeButton.setText(getString(R.string.finish));
        }
        viewPager.setCurrentItem(2);
    }

}
