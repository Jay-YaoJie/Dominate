package jeff.main


import android.annotation.SuppressLint
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import ch.ielse.view.SwitchView
import com.bumptech.glide.Glide
import com.jeff.mylibrary.R
import com.wuhenzhizao.titlebar.utils.ScreenUtils
import jeff.bases.BaseFragment
import jeff.beans.FragmentAdapterBeans.deviceBean
import jeff.utils.LogUtils
import jeff.widgets.LinearOffsetsItemDecoration
import kotlin_adapter.adapter_core.*
import kotlin_adapter.adapter_core.extension.getItems
import kotlin_adapter.adapter_core.extension.putItems
import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerView
import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerViewAdapter
import kotlin_adapter.adapter_exension.dragSwipeDismiss.dragListener
import kotlin_adapter.adapter_exension.dragSwipeDismiss.swipeListener


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-20.
 * description ：MainFragment 主页显示
 */
open class MainFragment : BaseFragment<MainFragmentDB>() {
    override fun lazyLoad() {
        //TO DO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentViewId(): Int = R.layout.fragment_main

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
        bindTopAdapter()//最顶层的列表
        bindGroupAdapter()  //组 数据列表
        bindSingleAdapter()//  //单个数据列表
    }

    //top 最顶的一个横向列表
    lateinit var mainFragment_DSRV_top: DragAndSwipeRecyclerView;
    private lateinit var topAdapter: DragAndSwipeRecyclerViewAdapter<deviceBean>
    open var topicList: ArrayList<deviceBean> = ArrayList()
    //最顶层的列表
    private fun bindTopAdapter() {
        mainFragment_DSRV_top = binding.mainFragmentTopDSRV
        mainFragment_DSRV_top.layoutManager = LinearLayoutManager(mActivity)
        mainFragment_DSRV_top.isLongPressDragEnable = true  //// 开启长按拖拽
        // 关闭开启Swipe Dismiss
        mainFragment_DSRV_top.isItemViewSwipeEnable = false
        //可以拖的位置
        mainFragment_DSRV_top.dragDirection = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN
        // binding.rv.layoutManager = GridLayoutManager(mActivity, 3, GridLayoutManager.VERTICAL, false)

        //可以设置为 横向，纵向，，spanCount设置的是当前行或列    orientation是横或纵
        mainFragment_DSRV_top.layoutManager = GridLayoutManager(mActivity, 1, GridLayoutManager.HORIZONTAL, false)
        //没有移动之前的items
        LogUtils.d(tag, "没有移动之前的items  topicList.toString()=" + topicList.toString())
        topAdapter = DragAndSwipeRecyclerViewAdapter<deviceBean>(mActivity)
                //加载item布局
                .match(deviceBean::class, R.layout.fragment_main_top_item)
                .holderCreateListener {}
                .holderBindListener { holder, position ->
                    val topic = topAdapter.getItem(position)
                    holder.withView<ImageView>(R.id.main_top_iv, {
                        //是否已经打开了场景的控制
                        if (topic.checke) {
                            //已经打开场景
                            Glide.with(mActivity).load(topic.imgAny).into(this)
                        } else {
                            //未打开场景
                            Glide.with(mActivity).load(topic.imgAny).into(this)
                        }

                    }).withView<TextView>(R.id.main_top_tv, { text = topic.textStr })
                }
                .clickListener { holder, position ->
                    val topic: deviceBean = topAdapter.getItem(position)
                    //点击事件
                    topClickListener(topic)

                }
                .dragListener { from, target ->
                    //当前移动的数据
                    Toast.makeText(mActivity, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
                    //移动后的items
                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())

                }
                .attach(mainFragment_DSRV_top)
        //添加数据
        topAdapter.putItems(topicList)
    }

    //点击列表事件
    open fun topClickListener(deviceBean: deviceBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    //group  //组 数据列表
    lateinit var mainFragment_DSRV_group: DragAndSwipeRecyclerView;
    private lateinit var groupAdapter: DragAndSwipeRecyclerViewAdapter<deviceBean>
    open var groupList: ArrayList<deviceBean> = ArrayList()
    //组 数据列表
    @SuppressLint("NewApi")
    private fun bindGroupAdapter() {
        mainFragment_DSRV_group = binding.mainFragmentGroupDSRV
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
        groupAdapter = DragAndSwipeRecyclerViewAdapter<deviceBean>(mActivity)
                .match(deviceBean::class, R.layout.all_single_item)
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
                    Toast.makeText(mActivity, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + BluetoothInfoList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + BluetoothInfoList)

                }
                .swipeListener { position, direction ->
                    //当前移动取消数据
                    Toast.makeText(mActivity, "position $position dismissed", Toast.LENGTH_LONG)
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
                }
                .attach(mainFragment_DSRV_group)
        groupAdapter.putItems(groupList)

    }

    //点击按钮 开 返回的事件
    open fun groupToggleToOn(deviceBean: deviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 开 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    //点击按钮 关 返回的事件
    open fun groupToggleToOff(deviceBean: deviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 关 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    //点击列表事件
    open fun groupClickListener(deviceBean: deviceBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    // single  ////单个数据列表
    lateinit var mainFragment_DSRV_single: DragAndSwipeRecyclerView;
    private lateinit var singleAdapter: DragAndSwipeRecyclerViewAdapter<deviceBean>
    open var singleList: ArrayList<deviceBean> = ArrayList()
    //单个数据列表
    @SuppressLint("NewApi")
    private fun bindSingleAdapter() {
        mainFragment_DSRV_single = binding.mainFragmentSingleDSRV
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
        singleAdapter = DragAndSwipeRecyclerViewAdapter<deviceBean>(mActivity)
                .match(deviceBean::class, R.layout.all_single_item)
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
                    Toast.makeText(mActivity, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())

                }
                .swipeListener { position, direction ->
                    //当前移动取消数据
                    Toast.makeText(mActivity, "position $position dismissed", Toast.LENGTH_LONG)
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
                }
                .attach(mainFragment_DSRV_single)
        singleAdapter.putItems(singleList)
    }

    //点击按钮 开 返回的事件
    open fun singleToggleToOn(deviceBean: deviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 开 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    //点击按钮 关 返回的事件
    open fun singleToggleToOff(deviceBean: deviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 关 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }

    //点击列表事件
    open fun singleClickListener(deviceBean: deviceBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceBean.toString()} ")
        return false
    }
}