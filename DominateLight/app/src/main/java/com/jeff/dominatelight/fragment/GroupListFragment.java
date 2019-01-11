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

import butterknife.BindView;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.activity.GroupActivity;
import com.jeff.dominatelight.activity.MainActivity;
import com.jeff.dominatelight.adapter.recycler_adapter.CommonAdapter;
import com.jeff.dominatelight.adapter.recycler_adapter.RecyclerViewHolder;
import com.jeff.dominatelight.eventbus.ConnectStateEvent;
import com.jeff.dominatelight.manage.CmdManage;
import com.jeff.dominatelight.model.Group;
import com.jeff.dominatelight.model.Groups;
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
public class GroupListFragment extends BaseFragment {


    private View view;

    @BindView(R.id.fgt_recyclerview)
    RecyclerView recyclerView;

    private CommonAdapter<Group> groupListAdapter;

    private List<Group> groupList = new ArrayList<>();

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView(View view) {

        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);
        groupListAdapter = new CommonAdapter<Group>(R.layout.view_device_item, Groups.getInstance().get()) {
            @Override
            public void convert(RecyclerViewHolder holder, final Group group, int position) {
                SeekBar seekBar = holder.getView(R.id.device_item_light);
                ImageView rightView = holder.getView(R.id.device_item_right);
                ImageView leftView = holder.getView(R.id.device_item_left);
                holder.setImageResource(R.id.device_item_left, group.getStatusIcon());
                holder.setText(R.id.device_item_name, group.getGroupSort().getName());

                group.updataBrightness();
                if (group.status() == ConnectionStatus.ON) {
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_on));
                    seekBar.setThumb(getResources().getDrawable(R.mipmap.thumb_light));
                    seekBar.setEnabled(true);
                    seekBar.setProgress(group.getGroupSort().getBrightness() - 5);
                } else {
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.light_bar_off));
                    seekBar.setThumb(getResources().getDrawable(R.mipmap.thumb_unlight));
                    seekBar.setEnabled(false);
                    seekBar.setProgress(0);
                }

                leftView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(group.getMembers().size() == 0){
                            getMainActivity().showTipsDialog(getString(R.string.group_no_device), getString(R.string.enter));
                        }else {
                            group.status();
                            CmdManage.changeGroupStatus(group);
                        }
                    }
                });

                rightView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), GroupActivity.class);
                        intent.putExtra(TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH, group.getGroupSort().getMeshAddress());
                        startActivity(intent);
                    }
                });


                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        group.getGroupSort().setBrightness(seekBar.getProgress() + 5);
                        CmdManage.changeGroupLum(group);
                    }
                });

            }
        };

        recyclerView.setAdapter(groupListAdapter);
        groupListAdapter.notifyDataSetChanged();
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    protected void initData() {

    }

    private void notifyData() {
        groupListAdapter.notifyDataSetChanged();
    }

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(NotificationEvent.ONLINE_STATUS)
                || event.getType().equals(ConnectStateEvent.ConnectStateEvent)) {
            notifyData();
        }
    }
}
