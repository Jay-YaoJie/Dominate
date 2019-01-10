package cn.xlink.telinkoffical.adapter.recycler_adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.xlink.telinkoffical.listener.RecyclerAdapterListener;


public abstract class CommonAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder>
        implements RecyclerAdapterListener {
    @LayoutRes
    private int layoutId;
    private List<T> data;

    public CommonAdapter(@LayoutRes int layoutId, List<T> data) {
        this.layoutId = layoutId;
        this.data = data;
        setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    // 设置ID，保证item操作不错乱
    @Override
    public long getItemId(int position) {
        T t = getItem(position);
        if (t != null)
            return t.hashCode();
        else
            return super.getItemId(position);
    }

    // 获得item数据封装
    public T getItem(int position) {
        if (data != null && data.size() > position)
            return data.get(position);
        return null;
    }

    // 创建hold
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return RecyclerViewHolder.get(parent, layoutId, this);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        convert(holder, getItem(position), position);
    }

    public abstract void convert(RecyclerViewHolder holder, T t, int position);

    @Override
    public void onItemClick(View v, int position) {
    }

    @Override
    public boolean onItemLongClick(View v, int position) {
        return false;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
