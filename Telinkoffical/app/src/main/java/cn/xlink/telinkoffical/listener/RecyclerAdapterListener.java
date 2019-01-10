package cn.xlink.telinkoffical.listener;

import android.view.View;

/**
 * 为 RecyclerView Item 添加监听接口
 */
public interface RecyclerAdapterListener {
    void onItemClick(View v, int position);

    boolean onItemLongClick(View v, int position);
}

