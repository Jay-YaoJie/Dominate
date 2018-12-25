package com.jeff.dominate.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeff.dominate.R;
import com.jeff.dominate.TelinkLightService;
import com.jeff.dominate.model.Group;
import com.jeff.dominate.model.Groups;
import com.jeff.dominate.model.Light;
import com.jeff.dominate.model.Lights;
import com.telink.bluetooth.light.ConnectionStatus;

import ch.ielse.view.SwitchView;

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-25 22:14
 * description ：Dominate
 */
public class BaseAdapters {
    private static class ViewHolder {
        TextView txtName;
        SwitchView switchView;
        ImageView imageView;
    }


    //单个设备列表
    static class DeviceListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context mContext;
        boolean isOpenSV;

        public DeviceListAdapter(Context context, boolean isOpen) {
            mContext = context;
            inflater = LayoutInflater.from(mContext);
            isOpenSV = isOpen;
        }

        @Override
        public int getCount() {
            return Lights.getInstance().size();
        }

        @Override
        public Light getItem(int position) {
            return Lights.getInstance().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.all_device_item, null);

                TextView txtName = (TextView) convertView
                        .findViewById(R.id.all_device_item_tv);

                holder = new ViewHolder();
                holder.txtName = txtName;

                if (isOpenSV) {
                    holder.switchView = convertView.findViewById(R.id.all_device_item_sv);
                    holder.switchView.setVisibility(View.VISIBLE);
                } else {
                    holder.imageView = convertView.findViewById(R.id.all_device_item_iv);
                    holder.imageView.setVisibility(View.VISIBLE);
                }

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Light light = this.getItem(position);

            holder.txtName.setText(light.getLabel());
            holder.txtName.setTextColor(mContext.getResources().getColor(light.textColor));

            if (light.connectionStatus == ConnectionStatus.OFFLINE) {
                // holder.statusIcon.setImageResource(R.mipmap.icon_light_offline);
                holder.switchView.toggleSwitch(false);
            } else if (light.connectionStatus == ConnectionStatus.OFF) {
                // holder.statusIcon.setImageResource(R.mipmap.icon_light_off);
                holder.switchView.toggleSwitch(false);
            } else if (light.connectionStatus == ConnectionStatus.ON) {
                //holder.statusIcon.setImageResource(R.mipmap.icon_light_on);
                holder.switchView.toggleSwitch(true);
            }
            holder.switchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (light.connectionStatus == ConnectionStatus.OFFLINE)
                        return;
                    int dstAddr = light.meshAddress;
                    byte opcode = (byte) 0xD0;
                    if (light.connectionStatus == ConnectionStatus.OFF) {
                        holder.switchView.toggleSwitch(false);
                        TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr, new byte[]{0x01, 0x00, 0x00});
                    } else if (light.connectionStatus == ConnectionStatus.ON) {
                        holder.switchView.toggleSwitch(true);
                        TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr, new byte[]{0x00, 0x00, 0x00});
                    }
                }
            });
            return convertView;
        }

        public void add(Light light) {
            Lights.getInstance().add(light);
        }

        public Light get(int meshAddress) {
            return Lights.getInstance().getByMeshAddress(meshAddress);
        }
    }

    //组设备列表
    static class GroupListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context mContext;
        boolean isOpenSV;

        public GroupListAdapter(Context context, boolean isOpen) {
            mContext = context;
            inflater = LayoutInflater.from(mContext);
            isOpenSV = isOpen;
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

            final ViewHolder holder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.all_device_item, null);

                TextView txtName = (TextView) convertView
                        .findViewById(R.id.all_device_item_tv);

                holder = new ViewHolder();
                holder.txtName = txtName;

                if (isOpenSV) {
                    holder.switchView = convertView.findViewById(R.id.all_device_item_sv);
                    holder.switchView.setVisibility(View.VISIBLE);
                } else {
                    holder.imageView = convertView.findViewById(R.id.all_device_item_iv);
                    holder.imageView.setVisibility(View.VISIBLE);
                }

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Group group = this.getItem(position);

            if (group != null) {
                if (group.textColor == null)
                    group.textColor = mContext.getResources()
                            .getColorStateList(R.color.black);
                holder.txtName.setText(group.name);

                holder.switchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        byte opcode = (byte) 0xD0;

                        if (holder.switchView.isOpened()) {
                            TelinkLightService.Instance().sendCommandNoResponse(opcode, group.meshAddress,
                                    new byte[]{0x00, 0x00, 0x00});
                        } else {
                            TelinkLightService.Instance().sendCommandNoResponse(opcode, group.meshAddress,
                                    new byte[]{0x01, 0x00, 0x00});
                        }

                    }
                });

            }

            return convertView;
        }


    }
}
