package main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.jeff.dominate.R
import co.metalab.asyncawait.async
import com.wuhenzhizao.adapter.*
import com.wuhenzhizao.adapter.extension.dragSwipeDismiss.DragAndSwipeRecyclerView
import com.wuhenzhizao.adapter.extension.dragSwipeDismiss.DragAndSwipeRecyclerViewAdapter
import com.wuhenzhizao.adapter.extension.dragSwipeDismiss.dragListener
import com.wuhenzhizao.adapter.extension.dragSwipeDismiss.swipeListener
import com.wuhenzhizao.adapter.extension.getItems
import com.wuhenzhizao.adapter.extension.putItems


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-20.
 * description ：MainFragment
 */
class MainFragment : Fragment() {

    internal lateinit var view: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        view = inflater.inflate(R.layout.fragment_main2, null);

        async {
            await<Unit> {
                //加载测试数据
                val json = getString(R.string.topicList)
                //顶部数据
                topicList = Gson().fromJson<TopicList>(json, TopicList::class.java).topics
                //组数据
                groupicList = Gson().fromJson<TopicList>(json, TopicList::class.java).topics
                //单个数据列表
                singleicList = Gson().fromJson<TopicList>(json, TopicList::class.java).topics
            }
            bindTopAdapter()//最顶层的列表
            bindGroupAdapter()  //组 数据列表
            bindSingleAdapter()//  //单个数据列表
        }


        return view
    }

    //top 最顶的一个横向列表
    lateinit var mainFragment_DSRV_top: DragAndSwipeRecyclerView;
    private lateinit var topAdapter: DragAndSwipeRecyclerViewAdapter<Topic>
    private lateinit var topicList: List<Topic>
    //最顶层的列表
    private fun bindTopAdapter() {
        mainFragment_DSRV_top = view.findViewById(R.id.mainFragment_DSRV_top)
        mainFragment_DSRV_top.layoutManager = LinearLayoutManager(context)
        mainFragment_DSRV_top.isLongPressDragEnable = true  //// 开启长按拖拽
        // 关闭开启Swipe Dismiss
        mainFragment_DSRV_top.isItemViewSwipeEnable = false
        //可以拖的位置
        mainFragment_DSRV_top.dragDirection = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN
        // binding.rv.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)

        //可以设置为 横向，纵向，，spanCount设置的是当前行或列    orientation是横或纵
        mainFragment_DSRV_top.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        //没有移动之前的items
        Log.d("", "没有移动之前的items  topicList.toString()=" + topicList.toString())
        topAdapter = DragAndSwipeRecyclerViewAdapter<Topic>(context!!)
                //加载item布局
                .match(Topic::class, R.layout.item_drag_recycler_view)
                .holderCreateListener {

                }
                .holderBindListener { holder, position ->
                    val topic = topAdapter.getItem(position)
                    holder.withView<RatioImageView>(R.id.iv, { GImageLoader.displayUrl(context, this, topic.smallImg) })
                            .withView<TextView>(R.id.name, { text = topic.title })
                }
                .clickListener { holder, position ->
                    val topic = topAdapter.getItem(position)
                    //点击事件
                    Toast.makeText(context!!, "position $position, ${topic.title} clicked", Toast.LENGTH_LONG)

                }
                .dragListener { from, target ->
                    //当前移动的数据
                    Toast.makeText(context!!, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
                    //移动后的items
                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())

                }
                .attach(mainFragment_DSRV_top)
        //添加数据
        topAdapter.putItems(topicList)
        Log.d("", "未移动的items  adapter.getItems()=" + topAdapter.getItems())
    }

    //group  //组 数据列表
    lateinit var mainFragment_DSRV_group: DragAndSwipeRecyclerView;
    private lateinit var groupAdapter: DragAndSwipeRecyclerViewAdapter<Topic>
    private lateinit var groupicList: List<Topic>
    //组 数据列表
    private fun bindGroupAdapter() {
        mainFragment_DSRV_group = view.findViewById(R.id.mainFragment_DSRV_group);
        mainFragment_DSRV_group.isLongPressDragEnable = true
        mainFragment_DSRV_group.isItemViewSwipeEnable = false
        mainFragment_DSRV_group.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        mainFragment_DSRV_group.swipeDirection = ItemTouchHelper.LEFT
        mainFragment_DSRV_group.layoutManager = LinearLayoutManager(context)
        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
        decoration.setItemOffsets(ScreenUtils.dp2PxInt(context, 10f))
        decoration.setOffsetEdge(true)
        decoration.setOffsetLast(true)
        mainFragment_DSRV_group.addItemDecoration(decoration)
        groupAdapter = DragAndSwipeRecyclerViewAdapter<Topic>(context!!)
                .match(Topic::class, R.layout.item_swipe_dismiss_recycler_view)
                .holderCreateListener {

                }
                .holderBindListener { holder, position ->
                    val topic = groupAdapter.getItem(position)
                    holder.withView<DraweeImageView>(R.id.iv, { GImageLoader.displayUrl(context, this, topic.bigImg) })
                            .withView<TextView>(R.id.name, { text = topic.title })
                }
                .clickListener { holder, position ->
                    val topic = groupAdapter.getItem(position)

                    Toast.makeText(context!!, "position $position, ${topic.title} clicked", Toast.LENGTH_LONG)
                }
                .dragListener { from, target ->
                    //当前移动的数据
                    Toast.makeText(context!!, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
                    //移动后的items
                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())

                }
                .swipeListener { position, direction ->
                    //当前移动取消数据
                    Toast.makeText(context!!, "position $position dismissed", Toast.LENGTH_LONG)
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
                }
                .attach(mainFragment_DSRV_group)
        groupAdapter.putItems(groupicList)


    }


    // single  ////单个数据列表
    lateinit var mainFragment_DSRV_single: DragAndSwipeRecyclerView;
    private lateinit var singleAdapter: DragAndSwipeRecyclerViewAdapter<Topic>
    private lateinit var singleicList: List<Topic>
    //单个数据列表
    private fun bindSingleAdapter() {
        mainFragment_DSRV_single = view.findViewById(R.id.mainFragment_DSRV_single);
        mainFragment_DSRV_single.isLongPressDragEnable = true
        mainFragment_DSRV_single.isItemViewSwipeEnable = false
        mainFragment_DSRV_single.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        mainFragment_DSRV_single.swipeDirection = ItemTouchHelper.LEFT
        mainFragment_DSRV_single.layoutManager = LinearLayoutManager(context)
        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
        decoration.setItemOffsets(ScreenUtils.dp2PxInt(context, 10f))
        decoration.setOffsetEdge(true)
        decoration.setOffsetLast(true)
        mainFragment_DSRV_single.addItemDecoration(decoration)
        singleAdapter = DragAndSwipeRecyclerViewAdapter<Topic>(context!!)
                .match(Topic::class, R.layout.item_swipe_dismiss_recycler_view)
                .holderCreateListener {

                }
                .holderBindListener { holder, position ->
                    val topic = singleAdapter.getItem(position)
                    holder.withView<DraweeImageView>(R.id.iv, { GImageLoader.displayUrl(context, this, topic.bigImg) })
                            .withView<TextView>(R.id.name, { text = topic.title })
                }
                .clickListener { holder, position ->
                    val topic = singleAdapter.getItem(position)

                    Toast.makeText(context!!, "position $position, ${topic.title} clicked", Toast.LENGTH_LONG)
                }
                .dragListener { from, target ->
                    //当前移动的数据
                    Toast.makeText(context!!, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
                    //移动后的items
                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())

                }
                .swipeListener { position, direction ->
                    //当前移动取消数据
                    Toast.makeText(context!!, "position $position dismissed", Toast.LENGTH_LONG)
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
                }
                .attach(mainFragment_DSRV_single)
        singleAdapter.putItems(singleicList)
    }

}