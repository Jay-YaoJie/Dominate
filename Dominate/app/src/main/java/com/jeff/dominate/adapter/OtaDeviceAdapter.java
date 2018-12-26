package com.jeff.dominate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.jeff.dominate.R;
import com.jeff.dominate.model.Light;

import java.util.List;

/**
 * Created by kee on 2017/12/19.
 */

public class OtaDeviceAdapter extends BaseRecyclerViewAdapter<OtaDeviceAdapter.ViewHolder> {
    private List<Light> models;
    private Context context;

    public OtaDeviceAdapter(Context context, List<Light> models) {
        this.context = context;
        this.models = models;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.ota_device_item, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        holder.tv_name = (TextView) itemView.findViewById(R.id.txt_name);
        holder.cb_select = (CheckBox) itemView.findViewById(R.id.cb_select);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Light light = models.get(position);
        holder.cb_select.setChecked(light.selected);
        holder.tv_name.setText("Type: 0x" + Integer.toHexString(light.productUUID) +
                "\nmeshAddress: " + light.meshAddress +
                "\nmac: " + light.macAddress);
    }

    @Override
    public int getItemCount() {
        return models == null ? 0 : models.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        CheckBox cb_select;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
