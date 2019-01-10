package cn.xlink.telinkoffical.listener;

import android.support.annotation.LayoutRes;

public interface RecyclerMultiItemType<T> {
    @LayoutRes
    int getLayoutId(int position, T t);

    int getViewTypeCount();

    int getItemViewType(int position, T t);
}