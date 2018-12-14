package com.jeff.dominate.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemLongClickListener;
import com.jeff.dominate.R;
import com.jeff.dominate.TelinkLightService;
import com.jeff.dominate.activity.GroupSettingActivity;
import com.jeff.dominate.model.Group;
import com.jeff.dominate.model.Groups;

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：
 */
public final class GroupListFragment extends Fragment {

    private LayoutInflater inflater;
    private GroupListAdapter adapter;

    private Activity mContext;
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {

            Group group = adapter.getItem(position);

            Intent intent = new Intent(mContext, GroupSettingActivity.class);
            intent.putExtra("groupAddress", group.meshAddress);

            startActivity(intent);

            return true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.mContext = this.getActivity();
        this.adapter = new GroupListAdapter();
        this.testData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Groups.getInstance().clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;

        View view = inflater.inflate(R.layout.fragment_group_list, null);

        GridView listView = (GridView) view.findViewById(R.id.list_groups);
        listView.setOnItemLongClickListener(this.itemLongClickListener);
        listView.setAdapter(this.adapter);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            this.testData();
        }
    }

    private void testData() {

        Groups.getInstance().clear();

        Group all = new Group();
        all.name = "All Device";
        all.meshAddress = 0xFFFF;
        all.brightness = 100;
        all.temperature = 100;
        all.color = 0xFFFFFF;

        Group living = new Group();
        living.name = "Living Room";
        living.meshAddress = 0x8001;
        living.brightness = 100;
        living.temperature = 100;
        living.color = 0xFFFFFF;

        Group family = new Group();
        family.name = "Family Room";
        family.meshAddress = 0x8002;
        family.brightness = 100;
        family.temperature = 100;
        family.color = 0xFFFFFF;

        Group kitchen = new Group();
        kitchen.name = "Kitchen";
        kitchen.meshAddress = 0x8003;
        kitchen.brightness = 100;
        kitchen.temperature = 100;
        kitchen.color = 0xFFFFFF;

        Group bedroom = new Group();
        bedroom.name = "Bedroom";
        bedroom.meshAddress = 0x8004;
        bedroom.brightness = 100;
        bedroom.temperature = 100;
        bedroom.color = 0xFFFFFF;

        Groups.getInstance().add(all);
        Groups.getInstance().add(living);
        Groups.getInstance().add(family);
        Groups.getInstance().add(kitchen);
        Groups.getInstance().add(bedroom);

        this.notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        this.adapter.notifyDataSetChanged();
    }

    private static class GroupItemHolder {
        public TextView txtName;
        public Button btnOn;
        public Button btnOff;
    }

    final class GroupListAdapter extends BaseAdapter implements
            OnClickListener, OnLongClickListener {

        public GroupListAdapter() {

        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            return Groups.getInstance().size();
        }

        @Override
        public Group getItem(int position) {
            return Groups.getInstance().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            GroupItemHolder holder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.group_item, null);

                TextView txtName = (TextView) convertView
                        .findViewById(R.id.txt_name);
                txtName.setOnLongClickListener(this);

                Button btnOn = (Button) convertView.findViewById(R.id.btn_on);
                btnOn.setOnClickListener(this);

                Button btnOff = (Button) convertView.findViewById(R.id.btn_off);
                btnOff.setOnClickListener(this);

                holder = new GroupItemHolder();

                holder.txtName = txtName;
                holder.btnOn = btnOn;
                holder.btnOff = btnOff;

                convertView.setTag(holder);

            } else {
                holder = (GroupItemHolder) convertView.getTag();
            }

            Group group = this.getItem(position);

            if (group != null) {
                if (group.textColor == null)
                    group.textColor = mContext.getResources()
                            .getColorStateList(R.color.black);

                holder.txtName.setText(group.name);
                holder.txtName.setTextColor(group.textColor);
                holder.txtName.setTag(group.meshAddress);
                holder.btnOn.setTag(group.meshAddress);
                holder.btnOff.setTag(group.meshAddress);
            }

            return convertView;
        }

        @Override
        public void onClick(View view) {

            int clickId = view.getId();
            int meshAddress = (int) view.getTag();

            byte opcode = (byte) 0xD0;
            int dstAddr = meshAddress;

            if (clickId == R.id.btn_on) {
                TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
                        new byte[]{0x01, 0x00, 0x00});

            } else if (clickId == R.id.btn_off) {
                TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
                        new byte[]{0x00, 0x00, 0x00});
            }
        }

        @Override
        public boolean onLongClick(View v) {

            return false;
        }
    }
}
