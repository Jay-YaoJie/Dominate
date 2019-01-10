package cn.xlink.telinkoffical.adapter.recycler_adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.xlink.telinkoffical.listener.RecyclerAdapterListener;


public abstract class CommonMultiItemAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder>
        implements RecyclerAdapterListener {
    private List<Integer> layoutIds;
    private List<T> data;

    public CommonMultiItemAdapter(List<Integer> layoutIds, List<T> data) {
        this.layoutIds = layoutIds;
        this.data = data;
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

    // 把layoutId当作viewType传递
    @Override
    public int getItemViewType(int position) {
        // 每个item对应一个布局
        if (layoutIds != null && layoutIds.size() > position)
            return layoutIds.get(position);
        return super.getItemViewType(position);
        /* 此处最好继承并重写该方法，在item里加上区别条件，并以此得到layout id */
//        T t = getItem(position);
//        if (t.getType()) {
//            return layout id ...
//        } else {
//            return layout id ...
//        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return RecyclerViewHolder.get(parent, viewType, this); // 此处的viewType就是layoutId
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        convert(holder.getLayoutId(), holder, getItem(position));
    }

    public abstract void convert(int layoutId, RecyclerViewHolder holder, T t);

    @Override
    public void onItemClick(View v, int position) {
    }

    @Override
    public boolean onItemLongClick(View v, int position) {
        return false;
    }
}
