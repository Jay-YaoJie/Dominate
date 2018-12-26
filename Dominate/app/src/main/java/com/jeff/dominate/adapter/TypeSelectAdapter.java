package com.jeff.dominate.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.jeff.dominate.R;
import com.jeff.dominate.activity.FileSelectActivity;
import com.jeff.dominate.activity.MeshOTAActivity;
import com.jeff.dominate.model.MeshDeviceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kee on 2017/12/19.
 */

public class TypeSelectAdapter extends BaseRecyclerViewAdapter<TypeSelectAdapter.ViewHolder> {
    private List<MeshDeviceType> models;
    //    private List<String> filePathList;
    private int selectPosition = -1;
    private boolean enable = true;
    private Context context;

    public TypeSelectAdapter(Context context, List<MeshDeviceType> models) {
        this.context = context;
        this.models = models;
        /*filePathList = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            filePathList.add(null);
        }*/
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_mesh_ota_file_select, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        holder.tv_name = (TextView) itemView.findViewById(R.id.tv_type_name);
        holder.iv_select = (ImageView) itemView.findViewById(R.id.iv_select);
        holder.ll_select = itemView.findViewById(R.id.ll_select);
        holder.tv_file_path = (TextView) itemView.findViewById(R.id.tv_file_path);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        if (position == selectPosition) {
            holder.iv_select.setImageResource(R.drawable.light_group_select2);
        } else {
            holder.iv_select.setImageResource(R.drawable.light_group_select);
        }
        holder.tv_name.setText("Type: 0x" + Integer.toHexString(models.get(position).type) +
                "（online: " + models.get(position).deviceList.size() + "）");
        holder.ll_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enable)
                    ((MeshOTAActivity) context).startActivityForResult(new Intent(context, FileSelectActivity.class), position);
            }
        });

        holder.tv_file_path.setText(models.get(position).filePath == null ? "Select file(NULL)" : models.get(position).filePath);
    }

    @Override
    public int getItemCount() {
        return models == null ? 0 : models.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_file_path;
        ImageView iv_select;
        View ll_select;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    public void insertFileInfo(int position, String filePath) {
//        filePathList.set(position, filePath);
        models.get(position).filePath = filePath;
        notifyDataSetChanged();
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
