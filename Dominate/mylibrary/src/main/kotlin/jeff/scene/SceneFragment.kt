package jeff.scene

import com.jeff.mylibrary.R
import jeff.bases.BaseFragment



/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：SceneFragment 场景模式
 */
open class SceneFragment : BaseFragment<SceneFragmentDB>() {
    override fun lazyLoad() {
        //T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentViewId(): Int = R.layout.fragment_scene

    override fun initViews() {
    }


//
//    private lateinit var sceneAdapter: DragAndSwipeRecyclerViewAdapter<BluetoothInfo.SceneInfo>
//    //单个数据列表
//    @SuppressLint("NewApi")
//    private fun bindSingleAdapter() {
//        binding.SceneFragmentSSRVScene.isLongPressDragEnable = true
//        binding.SceneFragmentSSRVScene.isItemViewSwipeEnable = false
//        binding.SceneFragmentSSRVScene.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//        //  mainFragment_DSRV_single.swipeDirection = ItemTouchHelper.LEFT
//        binding.SceneFragmentSSRVScene.layoutManager = LinearLayoutManager(context)
//        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
//        decoration.setItemOffsets(ScreenUtils.dp2PxInt(context, 10f))
//        decoration.setOffsetEdge(true)
//        decoration.setOffsetLast(true)
//        binding.SceneFragmentSSRVScene.addItemDecoration(decoration)
//        sceneAdapter = DragAndSwipeRecyclerViewAdapter<BluetoothInfo.SceneInfo>(context!!)
//                .match(BluetoothInfo.SceneInfo::class, R.layout.all_single_item)
//                .holderCreateListener {
//
//                }
//                .holderBindListener { holder, position ->
//                    val topic = sceneAdapter.getItem(position)
//                    holder.withView<TextView>(R.id.all_single_item_tv, {
//                        //   text = topic.getLabel()
//                        // //是否已经打开了当前单个设备的控制
////                        if (topic.selected) {
////                            //当前单个设备的灯已经打开
////                            val drawable: Drawable = context!!.resources.getDrawable(R.mipmap.arrow_r, null)//getResources().getDrawable(R.drawable.drawable);
////                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
////                            this.setCompoundDrawables(drawable, null, null, null);
////                        } else {
////                            //当前单个设备未打开
////                            val drawable: Drawable = context!!.resources.getDrawable(R.mipmap.arrow_l, null)//getResources().getDrawable(R.drawable.drawable);
////                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
////                            this.setCompoundDrawables(drawable, null, null, null);
////                        }
//
//                    })
//                }
//                .clickListener { holder, position ->
//                    val topic = sceneAdapter.getItem(position)
//
//                    //Toast.makeText(context!!, "position $position, ${topic.getLabel()} clicked", Toast.LENGTH_LONG)
//                }
//                .dragListener { from, target ->
//                    //当前移动的数据
//                    // Toast.makeText(context!!, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
//                    //移动后的items
////                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
////                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
//
//                }
//                .swipeListener { position, direction ->
//                    //当前移动取消数据
//                    //  Toast.makeText(context!!, "position $position dismissed", Toast.LENGTH_LONG)
//                    //移动后的items
////                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
////                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
//                }
//                .attach(binding.SceneFragmentSSRVScene)
//        sceneAdapter.putItems(seceneList)
//    }
}