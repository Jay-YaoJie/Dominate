package com.jeff.dominatelight.adapter.recycler_adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jeff.dominatelight.listener.RecyclerAdapterListener;


/**
 * 自定义 RecyclerView 的 ViewHolder
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private final SparseArray<View> views;
    @LayoutRes
    private int layoutId;
    private RecyclerAdapterListener listener;

    private RecyclerViewHolder(ViewGroup parent, @LayoutRes int layoutId, RecyclerAdapterListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        this.views = new SparseArray<>();
        this.layoutId = layoutId;
        this.listener = listener;

        //添加监听事件
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

    }

    public static RecyclerViewHolder get(ViewGroup parent, @LayoutRes int layoutId, RecyclerAdapterListener listener) {
        return new RecyclerViewHolder(parent, layoutId, listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            // 为item设置字体
//            if (view instanceof TextView) {
//                ((TextView) view).setTypeface(TTFUtil.tf_2nd);
//            }
            views.put(viewId, view);
        }
        return (T) view;
    }

    @Override
    public void onClick(View v) {
        listener.onItemClick(v, getLayoutPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        return listener.onItemLongClick(v, getAdapterPosition());
    }

    public Context getContext() {
        return itemView.getContext();
    }

    @LayoutRes
    public int getLayoutId() {
        return layoutId;
    }

    public void setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
    }

    public void setTextTopImage(int viewId, int resId) {
        TextView tv = getView(viewId);
        Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(null, drawable, null, null);
    }

    public void setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
    }


}
