package kotlin_adapter.adapter_exension.stickyHeader

import android.view.View
import kotlin_adapter.adapter_core.interfaces.Listener

/**
 * 监听Sticky header item view的创建
 */
interface HeaderViewHolderCreateListener<in VH> : Listener<VH> {
    /**
     * @param holder
     */
    fun onCreateHeaderViewHolder(holder: VH)
}

/**
 * 监听Sticky header item view的绑定
 */
interface HeaderViewHolderBindListener<in VH> : Listener<VH> {
    /**
     * @param holder
     * @param position
     */
    fun onBindHeaderViewHolder(holder: VH, position: Int)
}

/**
 * 监听Sticky header item view的点击事件
 */
interface HeaderClickListener<in VH> : Listener<VH> {
    /**
     * @param holder
     * @param clickView
     * @param position
     */
    fun onHeaderClick(holder: VH, clickView: View, position: Int)
}