package jeff.device

import android.annotation.SuppressLint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.TextView
import ch.ielse.view.SwitchView
import com.jeff.mylibrary.R
import com.wuhenzhizao.titlebar.utils.ScreenUtils
import jeff.bases.BaseFragment
import jeff.beans.FragmentAdapterBeans
import jeff.utils.LogUtils
import jeff.utils.ToastUtil
import jeff.widgets.LinearOffsetsItemDecoration
import kotlin_adapter.adapter_core.*
import kotlin_adapter.adapter_core.extension.putItems
import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerView
import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerViewAdapter
import kotlin_adapter.adapter_exension.dragSwipeDismiss.dragListener
import kotlin_adapter.adapter_exension.dragSwipeDismiss.swipeListener


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：DeviceFragment 设备管理
 */
@SuppressLint("NewApi")
open class DeviceFragment : BaseFragment<DeviceFragmentDB>() {
    override fun lazyLoad() {
        //T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentViewId(): Int = R.layout.fragment_device

    override fun initViews() {
//        async {
//            await<Unit> {
//                //加载测试数据
//
//            }
//
//            info()//加载数据列表适配器
//        }
    }

    open fun info() {
        bindGroupAdapter()  //组 数据列表
        bindSingleAdapter()//  //单个数据列表
    }


    //group  //组 数据列表
    lateinit var mainFragment_DSRV_group: DragAndSwipeRecyclerView;
    private lateinit var groupAdapter: DragAndSwipeRecyclerViewAdapter<FragmentAdapterBeans.deviceBean>
    open var groupList: ArrayList<FragmentAdapterBeans.deviceBean> = ArrayList()
    //组 数据列表
    private fun bindGroupAdapter() {
        mainFragment_DSRV_group = binding.deviceFragmentGroupDSRV
        mainFragment_DSRV_group.isLongPressDragEnable = true
        mainFragment_DSRV_group.isItemViewSwipeEnable = false
        mainFragment_DSRV_group.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        // mainFragment_DSRV_group.swipeDirection = ItemTouchHelper.LEFT
        mainFragment_DSRV_group.layoutManager = LinearLayoutManager(mActivity)
        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
        //decoration.setItemOffsets(ScreenUtils.dp2PxInt(mActivity, 10f))
        decoration.setOffsetEdge(true)
        decoration.setOffsetLast(true)
        mainFragment_DSRV_group.addItemDecoration(decoration)
        LogUtils.d(tag, "没有移动之前的items  groupList.toString()=" + groupList.toString())
        groupAdapter = DragAndSwipeRecyclerViewAdapter<FragmentAdapterBeans.deviceBean>(mActivity)
                .match(FragmentAdapterBeans.deviceBean::class, R.layout.all_single_item)
                .holderCreateListener {
                }
                .holderBindListener { holder, position ->
                    val topic = groupAdapter.getItem(position)
                    holder.withView<TextView>(R.id.all_single_item_tv, {
                        text = topic.textStr

                    }).withView<SwitchView>(R.id.all_single_item_sv, {
                        // //是否已经打开了当前组的控制
                        if (topic.checke) {
                            //当前组的灯已经打开
                            this.toggleSwitch(true);

                        } else {
                            //当前组未打开
                            this.toggleSwitch(false);
                        }
                        this.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
                            override fun toggleToOn(view: SwitchView) {
                                if (groupToggleToOn(topic)) {
                                    //点击按钮 开
                                    view.toggleSwitch(true);
                                }
                            }

                            override fun toggleToOff(view: SwitchView) {
                                //点击按钮 关
                                if (groupToggleToOff(topic)) {
                                    view.toggleSwitch(false);
                                }
                            }

                        })
                    })
                }
                .clickListener { holder, position ->
                    val topic = groupAdapter.getItem(position)
                    groupClickListener(topic)
                }
                .dragListener { from, target ->
                    //当前移动的数据

                    ToastUtil("item is dragged, from $from to $target")
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + BluetoothInfoList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + BluetoothInfoList)

                }
                .swipeListener { position, direction ->
                    //当前移动取消数据

                    ToastUtil("position $position dismissed")
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
                }
                .attach(mainFragment_DSRV_group)
        groupAdapter.putItems(groupList)

    }

    //点击按钮 开 返回的事件
    open fun groupToggleToOn(deviceBean: FragmentAdapterBeans.deviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 开 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    //点击按钮 关 返回的事件
    open fun groupToggleToOff(deviceBean: FragmentAdapterBeans.deviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 关 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    //点击列表事件
    open fun groupClickListener(deviceBean: FragmentAdapterBeans.deviceBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    // single  ////单个数据列表
    lateinit var mainFragment_DSRV_single: DragAndSwipeRecyclerView;
    private lateinit var singleAdapter: DragAndSwipeRecyclerViewAdapter<FragmentAdapterBeans.deviceBean>
    open var singleList: ArrayList<FragmentAdapterBeans.deviceBean> = ArrayList()
    //单个数据列表

    private fun bindSingleAdapter() {
        mainFragment_DSRV_single = binding.deviceFragmentUngroupedDSRV
        mainFragment_DSRV_single.isLongPressDragEnable = true
        mainFragment_DSRV_single.isItemViewSwipeEnable = false
        mainFragment_DSRV_single.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        //  mainFragment_DSRV_single.swipeDirection = ItemTouchHelper.LEFT
        mainFragment_DSRV_single.layoutManager = LinearLayoutManager(mActivity)
        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
        decoration.setItemOffsets(ScreenUtils.dp2PxInt(mActivity, 10f))
        decoration.setOffsetEdge(true)
        decoration.setOffsetLast(true)
        mainFragment_DSRV_single.addItemDecoration(decoration)
        LogUtils.d(tag, "没有移动之前的items  singleList.toString()=" + singleList.toString())
        singleAdapter = DragAndSwipeRecyclerViewAdapter<FragmentAdapterBeans.deviceBean>(mActivity)
                .match(FragmentAdapterBeans.deviceBean::class, R.layout.all_single_item)
                .holderCreateListener {

                }
                .holderBindListener { holder, position ->
                    val topic = singleAdapter.getItem(position)
                    holder.withView<TextView>(R.id.all_single_item_tv, {
                        text = topic.textStr

                    }).withView<SwitchView>(R.id.all_single_item_sv, {
                        // //是否已经打开了当前组的控制
                        if (topic.checke) {
                            //当前组的灯已经打开
                            this.toggleSwitch(true);

                        } else {
                            //当前组未打开
                            this.toggleSwitch(false);
                        }
                        this.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
                            override fun toggleToOn(view: SwitchView) {
                                if (singleToggleToOn(topic)) {
                                    //点击按钮 开
                                    view.toggleSwitch(true);
                                }
                            }

                            override fun toggleToOff(view: SwitchView) {
                                //点击按钮 关
                                if (singleToggleToOff(topic)) {
                                    view.toggleSwitch(false);
                                }
                            }

                        })
                    })
                }
                .clickListener { holder, position ->
                    val topic = singleAdapter.getItem(position)
                    singleClickListener(topic)
                }
                .dragListener { from, target ->
                    //当前移动的数据

                    ToastUtil("item is dragged, from $from to $target")
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())

                }
                .swipeListener { position, direction ->
                    //当前移动取消数据

                    ToastUtil("position $position dismissed")
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
                }
                .attach(mainFragment_DSRV_single)
        singleAdapter.putItems(singleList)
    }

    //点击按钮 开 返回的事件
    open fun singleToggleToOn(deviceBean: FragmentAdapterBeans.deviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 开 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    //点击按钮 关 返回的事件
    open fun singleToggleToOff(deviceBean: FragmentAdapterBeans.deviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 关 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    //点击列表事件
    open fun singleClickListener(deviceBean: FragmentAdapterBeans.deviceBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

}