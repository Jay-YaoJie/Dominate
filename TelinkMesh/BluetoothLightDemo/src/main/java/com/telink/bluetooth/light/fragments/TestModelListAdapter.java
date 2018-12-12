package com.telink.bluetooth.light.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telink.bluetooth.light.R;
import com.telink.bluetooth.light.adapter.BaseRecyclerViewAdapter;
import com.telink.bluetooth.light.model.TestModel;

import java.util.List;

/**
 * Created by kee on 2017/12/19.
 */

public class TestModelListAdapter extends BaseRecyclerViewAdapter<TestModelListAdapter.ViewHolder> {
    private List<TestModel> models;
    private int selectPosition = 0;
    private Context context;
    private boolean isSettingMode = false;

    public TestModelListAdapter(Context context, List<TestModel> models) {
        this.context = context;
        this.models = models;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_test_model, null);
        ViewHolder holder = new ViewHolder(itemView);
        holder.tv_name = (TextView) itemView.findViewById(R.id.tv_model_name);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
//        holder.tv_name.setText(models.get(position).getOpCode() + "");
        if (isSettingMode && position == selectPosition) {
            holder.itemView.setBackgroundResource(R.color.theme_positive_color);
        } else {
            holder.itemView.setBackgroundResource(0);
        }


        if (!isSettingMode) {
            if (models.get(position).isHolder()) {
                holder.itemView.setVisibility(View.INVISIBLE);
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
            }
        }

        holder.tv_name.setText(models.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return models == null ? 0 : models.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;

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

    public void setSettingMode(boolean settingMode) {
        isSettingMode = settingMode;
    }
}
