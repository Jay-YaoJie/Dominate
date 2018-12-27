package jeff.device

import com.jeff.mylibrary.R
import jeff.bases.BaseFragment
import jeff.utils.LogUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

//import android.annotation.SuppressLint
//import android.graphics.drawable.Drawable
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.helper.ItemTouchHelper
//import android.widget.TextView
//import android.widget.Toast
//import com.jeff.mylibrary.R
//import com.wuhenzhizao.titlebar.utils.ScreenUtils
//import jeff.bases.BaseFragment
//import jeff.utils.LogUtils
//import jeff.widgets.LinearOffsetsItemDecoration
//import kotlin_adapter.adapter_core.*
//import kotlin_adapter.adapter_core.extension.putItems
//import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerView
//import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerViewAdapter
//import kotlin_adapter.adapter_exension.dragSwipeDismiss.dragListener
//import kotlin_adapter.adapter_exension.dragSwipeDismiss.swipeListener
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：DeviceFragment 设备管理
 */
open class DeviceFragment : BaseFragment<DeviceFragmentDB>() {
    override fun getContentViewId(): Int = R.layout.fragment_device

    override fun initViews() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventMain(event: String) {
        LogUtils.i(tag, "onMessageEventMain(), current thread is " + event + Thread.currentThread().getName());
    }


    //group  //组 数据列表
//    lateinit var mainFragment_DSRV_group: DragAndSwipeRecyclerView;
//    private lateinit var groupAdapter: DragAndSwipeRecyclerViewAdapter<BluetoothInfo>
//    //组 数据列表
//    @SuppressLint("NewApi")
//    private fun bindGroupAdapter() {
//        mainFragment_DSRV_group = binding.deviceGroupDSRV
//        mainFragment_DSRV_group.isLongPressDragEnable = true
//        mainFragment_DSRV_group.isItemViewSwipeEnable = false
//        mainFragment_DSRV_group.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//        // mainFragment_DSRV_group.swipeDirection = ItemTouchHelper.LEFT
//        mainFragment_DSRV_group.layoutManager = LinearLayoutManager(context)
//        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
//        //decoration.setItemOffsets(ScreenUtils.dp2PxInt(context, 10f))
//        decoration.setOffsetEdge(true)
//        decoration.setOffsetLast(true)
//        mainFragment_DSRV_group.addItemDecoration(decoration)
//        groupAdapter = DragAndSwipeRecyclerViewAdapter<BluetoothInfo>(context!!)
//                .match(BluetoothInfo::class, R.layout.all_single_item)
//                .holderCreateListener {
//
//                }
//                .holderBindListener { holder, position ->
//                    val topic = groupAdapter.getItem(position)
//                    holder.withView<TextView>(R.id.all_single_item_tv, {
//                        text = topic.groupName
//                        // //是否已经打开了当前组的控制
//                        if (topic.checked) {
//                            //当前组的灯已经打开
//                            val drawable: Drawable = context!!.resources.getDrawable(R.mipmap.arrow_r, null)//getResources().getDrawable(R.drawable.drawable);
//                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                            this.setCompoundDrawables(drawable, null, null, null);
//                        } else {
//                            //当前组未打开
//                            val drawable: Drawable = context!!.resources.getDrawable(R.mipmap.arrow_l, null)//getResources().getDrawable(R.drawable.drawable);
//                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                            this.setCompoundDrawables(drawable, null, null, null);
//                        }
//
//                    })
//                }
//                .clickListener { holder, position ->
//                    val topic = groupAdapter.getItem(position)
//
//                    Toast.makeText(context!!, "position $position, ${topic.groupName} clicked", Toast.LENGTH_LONG)
//                }
//                .dragListener { from, target ->
//                    //当前移动的数据
//                    Toast.makeText(context!!, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
//                    //移动后的items
////                    Log.d("", "移动后的items topicList.toString()=" + BluetoothInfoList.toString())
////                    Log.d("", "移动后的items  adapter.getItems()=" + BluetoothInfoList)
//
//                }
//                .swipeListener { position, direction ->
//                    //当前移动取消数据
//                    Toast.makeText(context!!, "position $position dismissed", Toast.LENGTH_LONG)
//                    //移动后的items
////                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
////                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
//                }
//                .attach(mainFragment_DSRV_group)
//        groupAdapter.putItems(blueList)
//
//
//    }
//
//
//    // single  ////单个数据列表
//    lateinit var mainFragment_DSRV_single: DragAndSwipeRecyclerView;
//    private lateinit var singleAdapter: DragAndSwipeRecyclerViewAdapter<BluetoothInfo>
//    //单个数据列表
//    @SuppressLint("NewApi")
//    private fun bindSingleAdapter() {
//        mainFragment_DSRV_single = binding.deviceUngroupedDSRV
//        mainFragment_DSRV_single.isLongPressDragEnable = true
//        mainFragment_DSRV_single.isItemViewSwipeEnable = false
//        mainFragment_DSRV_single.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//        //  mainFragment_DSRV_single.swipeDirection = ItemTouchHelper.LEFT
//        mainFragment_DSRV_single.layoutManager = LinearLayoutManager(context)
//        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
//        decoration.setItemOffsets(ScreenUtils.dp2PxInt(context, 10f))
//        decoration.setOffsetEdge(true)
//        decoration.setOffsetLast(true)
//        mainFragment_DSRV_single.addItemDecoration(decoration)
//        singleAdapter = DragAndSwipeRecyclerViewAdapter<BluetoothInfo>(context!!)
//                .match(BluetoothInfo::class, R.layout.all_single_item)
//                .holderCreateListener {
//
//                }
//                .holderBindListener { holder, position ->
//                    val topic = singleAdapter.getItem(position)
//                    holder.withView<TextView>(R.id.all_single_item_tv, {
//                        text = topic.getLabel()
//                        // //是否已经打开了当前单个设备的控制
//                        if (topic.selected) {
//                            //当前单个设备的灯已经打开
//                            val drawable: Drawable = context!!.resources.getDrawable(R.mipmap.arrow_r, null)//getResources().getDrawable(R.drawable.drawable);
//                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                            this.setCompoundDrawables(drawable, null, null, null);
//                        } else {
//                            //当前单个设备未打开
//                            val drawable: Drawable = context!!.resources.getDrawable(R.mipmap.arrow_l, null)//getResources().getDrawable(R.drawable.drawable);
//                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                            this.setCompoundDrawables(drawable, null, null, null);
//                        }
//
//                    })
//                }
//                .clickListener { holder, position ->
//                    val topic = singleAdapter.getItem(position)
//
//                    Toast.makeText(context!!, "position $position, ${topic.getLabel()} clicked", Toast.LENGTH_LONG)
//                }
//                .dragListener { from, target ->
//                    //当前移动的数据
//                    Toast.makeText(context!!, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
//                    //移动后的items
////                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
////                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
//
//                }
//                .swipeListener { position, direction ->
//                    //当前移动取消数据
//                    Toast.makeText(context!!, "position $position dismissed", Toast.LENGTH_LONG)
//                    //移动后的items
////                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
////                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
//                }
//                .attach(mainFragment_DSRV_single)
//        singleAdapter.putItems(blueList)
//    }
}