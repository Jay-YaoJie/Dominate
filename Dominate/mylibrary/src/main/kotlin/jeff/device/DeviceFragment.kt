package jeff.device

import jeff.bases.BaseFragment
import com.jeff.mylibrary.R



/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：DeviceFragment 设备管理
 */
open  class DeviceFragment : BaseFragment<DeviceFragmentDB>() {
    override fun getContentViewId(): Int = R.layout.fragment_device

    override fun initViews() {
    }

//
//    //group  //组 数据列表
//    lateinit var mainFragment_DSRV_group: DragAndSwipeRecyclerView;
//    private lateinit var groupAdapter: DragAndSwipeRecyclerViewAdapter<BluetoothInfo>
//    //组 数据列表
//    @SuppressLint("NewApi")
//    private fun bindGroupAdapter() {
//        mainFragment_DSRV_group = binding.deviceGroupDSRV
//        mainFragment_DSRV_group.isLongPressDragEnable = true
//        mainFragment_DSRV_group.isItemViewSwipeEnable = false
//        mainFragment_DSRV_group.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//       // mainFragment_DSRV_group.swipeDirection = ItemTouchHelper.LEFT
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
//      //  mainFragment_DSRV_single.swipeDirection = ItemTouchHelper.LEFT
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