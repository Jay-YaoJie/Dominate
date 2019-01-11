package com.jeff.dominatelight.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.activity.BlubbActivity;
import com.jeff.dominatelight.activity.MainActivity;
import com.jeff.dominatelight.adapter.recycler_adapter.CommonAdapter;
import com.jeff.dominatelight.adapter.recycler_adapter.RecyclerViewHolder;
import com.jeff.dominatelight.eventbus.ConnectStateEvent;
import com.jeff.dominatelight.manage.CmdManage;
import com.jeff.dominatelight.model.Light;
import com.jeff.dominatelight.model.Lights;
import com.jeff.dominatelight.utils.EventBusUtils;
import com.jeff.dominatelight.utils.TelinkCommon;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.util.Event;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liucr on 2016/3/24.
 */
public class BulbsListFragment extends BaseFragment {

    private View view;

    @BindView(R.id.fgt_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.fgt_recyclerview_empty)
    View emptyView;

    @BindView(R.id.fgt_recyclerview_empty_image)
    ImageView emptyImage;

    @BindView(R.id.fgt_recyclerview_empty_text)
    TextView emptyText;

    private CommonAdapter<Light> deviceListAdapter;

    private List<Light> lightList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusUtils.getInstance().addEventListener(NotificationEvent.ONLINE_STATUS, this);
        EventBusUtils.getInstance().addEventListener(ConnectStateEvent.ConnectStateEvent, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fgt_recyclerview, container, false);
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
        notifyData();
        if (Lights.getInstance().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.getInstance().removeEventListener(this);
    }

    @OnClick(R.id.fgt_recyclerview_empty)
    void clickEmpty() {

    }

    @Override
    protected void initView(View view) {

        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);
        deviceListAdapter = new CommonAdapter<Light>(R.layout.view_device_item, Lights.getInstance().get()) {
            @Override
            public void convert(RecyclerViewHolder holder, final Light light, int position) {
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
                        CmdManage.changeLightStatus(light);
                    }
                });

                rightView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), BlubbActivity.class);
                        intent.putExtra(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH, light.getLightSort().getMeshAddress());
                        startActivity(intent);
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

        emptyImage.setImageResource(R.mipmap.icon_empty_devicelist);
        emptyText.setText(getString(R.string.empty_devicelist));
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    protected void initData() {

    }

    private void notifyData() {
        deviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(NotificationEvent.ONLINE_STATUS)
                || event.getType().equals(ConnectStateEvent.ConnectStateEvent)) {
            notifyData();
        }
    }
}
