package com.jeff.dominate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-13.
 * description ：
 */
public abstract class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements View.OnClickListener, View.OnLongClickListener {

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick((Integer) v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onItemLongClickListener != null){
            onItemLongClickListener.onLongClick((Integer) v.getTag());
        }
        return false;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener{
        void onLongClick(int position);
    }
}
