package jeff.main


import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import ch.ielse.view.SwitchView
import co.metalab.asyncawait.async
import com.bumptech.glide.Glide
import com.jeff.mylibrary.R
import com.wuhenzhizao.titlebar.utils.ScreenUtils
import jeff.bases.BaseFragment
import jeff.constants.DeviceBean
import jeff.constants.GroupBean
import jeff.constants.SceneBean
import jeff.utils.LogUtils
import jeff.utils.SPUtils
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

    override fun getContentViewId(): Int = R.layout.fragment_main
    override fun initViews() {
        async {
            await<Unit> {
                //加载测试数据
                //  //获得最顶上的数据
                topicList = SPUtils.getSceneBeans(mActivity, "sceneList")
                //   //获得组列表数据
                groupList = SPUtils.getGroupBeans(mActivity, "grouplist")
                var groupName: GroupBean = GroupBean()
                groupName.groupName = "All Device";
                groupName.groupId = 1
                groupName.meshAddress = 0xFFFF;
                groupName.brightness = 100;
                groupName.connectionStatus = 1;
                groupList.add(0, groupName)
                // //获得单个设备列表数据
                singleList = SPUtils.getDeviceBeans(mActivity, "deviceSingleList")
            }
            //加载数据列表适配器
            bindTopAdapter()//最顶层的列表
            bindGroupAdapter()  //组 数据列表
            bindSingleAdapter()//  //单个数据列表
        }


    }


    //top 最顶的一个横向列表
    lateinit var mainFragment_DSRV_top: DragAndSwipeRecyclerView;
    open var topAdapter: DragAndSwipeRecyclerViewAdapter<SceneBean>? = null
    open var topicList: ArrayList<SceneBean> = ArrayList()
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
        topAdapter = DragAndSwipeRecyclerViewAdapter<SceneBean>(mActivity)
                //加载item布局
                .match(SceneBean::class, R.layout.fragment_main_top_item)
                .holderCreateListener {}
                .holderBindListener { holder, position ->
                    val topic = topAdapter!!.getItem(position)
                    holder.withView<ImageView>(R.id.main_top_iv, {
                        //是否已经打开了场景的控制
                        //  connectionStatus OFF(0), ON(1), OFFLINE(2);  关，开，离线
                        if (topic.connectionStatus == 1) {
                            //已经打开场景
                            Glide.with(mActivity).load(topic.imgAny).into(this)
                            val matrix = ColorMatrix()
                            matrix.setSaturation(Float.fromBits(50)) //饱和度 0灰色 100过度彩色，50正常
                            val filter = ColorMatrixColorFilter(matrix)
                            this.setColorFilter(filter)
                        } else {
                            //未打开场景
                            Glide.with(mActivity).load(topic.imgAny).into(this)
                            // 将ImageView变成灰色
//                            ColorMatrix matrix = new ColorMatrix();
//                            matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
//                            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
//                            viewHolder.image.setColorFilter(filter);
                            val matrix: ColorMatrix = ColorMatrix()
                            matrix.setSaturation(Float.fromBits(0))
                            val filter = ColorMatrixColorFilter(matrix)
                            this.setColorFilter(filter)

                        }

                    }).withView<TextView>(R.id.main_top_tv, { text = topic.sceneName })
                }
                .clickListener { holder, position ->
                    val topic: SceneBean = topAdapter!!.getItem(position)
                    //点击事件
                    topClickListener(topic)

                }
                .dragListener { from, target ->
                    //当前移动的数据
                    Toast.makeText(mActivity, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
                    //移动后的items
                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter!!.getItems())

                }
                .attach(mainFragment_DSRV_top)
        //添加数据
        topAdapter!!.putItems(topicList)
    }

    //点击列表事件
    open fun topClickListener(topicBean: SceneBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${topicBean.toString()} ")
        return false
    }

    //group  //组 数据列表
    lateinit var mainFragment_DSRV_group: DragAndSwipeRecyclerView;
    open var groupAdapter: DragAndSwipeRecyclerViewAdapter<GroupBean>? = null
    open var groupList: ArrayList<GroupBean> = ArrayList()
    //组 数据列表
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
        groupAdapter = DragAndSwipeRecyclerViewAdapter<GroupBean>(mActivity)
                .match(GroupBean::class, R.layout.all_single_sv_item)
                .holderCreateListener {}
                .holderBindListener { holder, position ->
                    val topic = groupAdapter!!.getItem(position)
                    holder.withView<TextView>(R.id.all_single_sv_item_tv, {
                        text = topic.groupName

                    }).withView<SwitchView>(R.id.all_single_sv_item_sv, {
                        // //是否已经打开了当前组的控制
                        //  connectionStatus OFF(0), ON(1), OFFLINE(2);  关，开，离线
                        if (topic.connectionStatus == 1) {
                            //当前组的灯已经打开
                            this.toggleSwitch(true);

                        } else if (topic.connectionStatus == 0) {
                            //当前组未打开
                            this.toggleSwitch(false);
                        } else {
                            this.toggleSwitch(false);
                            return@withView
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
                    val topic = groupAdapter!!.getItem(position)
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
        groupAdapter!!.putItems(groupList)

    }

    //点击按钮 开 返回的事件
    open fun groupToggleToOn(groupName: GroupBean): Boolean {
        LogUtils.d(tag, "点击按钮 开 返回的事件 deviceBean= ${groupName.toString()} ")
        return false
    }

    //点击按钮 关 返回的事件
    open fun groupToggleToOff(groupName: GroupBean): Boolean {
        LogUtils.d(tag, "点击按钮 关 返回的事件 deviceBean= ${groupName.toString()} ")
        return false
    }

    //点击列表事件
    open fun groupClickListener(groupName: GroupBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${groupName.toString()} ")
        return false
    }

    // single  ////单个数据列表
    lateinit var mainFragment_DSRV_single: DragAndSwipeRecyclerView;
    open var singleAdapter: DragAndSwipeRecyclerViewAdapter<DeviceBean>? = null
    open var singleList: ArrayList<DeviceBean> = ArrayList()
    //单个数据列表
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
        singleAdapter = DragAndSwipeRecyclerViewAdapter<DeviceBean>(mActivity)
                .match(DeviceBean::class, R.layout.all_single_sv_item)
                .holderCreateListener {}
                .holderBindListener { holder, position ->
                    val topic = singleAdapter!!.getItem(position)
                    LogUtils.d(tag, "bindSingleAdapter---topic.toString()= " + topic.toString())
                    holder.withView<TextView>(R.id.all_single_sv_item_tv, {
                        text = topic.meshAddress.toString()

                    }).withView<SwitchView>(R.id.all_single_sv_item_sv, {
                        // //是否已经打开了当前组的控制
                        //  connectionStatus OFF(0), ON(1), OFFLINE(2);  关，开，离线
                        if (topic.connectionStatus == 1) {
                            //当前组的灯已经打开
                            this.toggleSwitch(true);

                        } else if (topic.connectionStatus == 0) {
                            //当前组未打开
                            this.toggleSwitch(false);
                        } else {
                            this.toggleSwitch(false);
                            return@withView
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
                    val topic = singleAdapter!!.getItem(position)
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
        singleAdapter!!.putItems(singleList)
    }

    //点击按钮 开 返回的事件
    open fun singleToggleToOn(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 开 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return true
    }

    //点击按钮 关 返回的事件
    open fun singleToggleToOff(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tag, "点击按钮 关 返回的事件 deviceBean= ${deviceBean.toString()} ")
        return true
    }

    //点击列表事件
    open fun singleClickListener(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceBean.toString()} ")
        return true
    }
}