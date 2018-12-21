package main

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import bases.BaseFragment
import beans.LightBean
import beans.MainGroupBean
import beans.MainTopBean
import co.metalab.asyncawait.async
import com.bumptech.glide.Glide
import com.jeff.dominate.R
import com.wuhenzhizao.titlebar.utils.ScreenUtils
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
 * description ：MainFragment
 */
class MainFragment : BaseFragment<MainFragmentDB>() {

    override fun getContentViewId(): Int = R.layout.fragment_main2

    override fun initViews() {

        async {
            await<Unit> {
                //加载测试数据
//                val json = getString(R.string.topicList)
//                //顶部数据
//                topicList = Gson().fromJson<TopicList>(json, TopicList::class.java).topics
//                //组数据
//                groupicList = Gson().fromJson<TopicList>(json, TopicList::class.java).topics
//                //单个数据列表
//                singleicList = Gson().fromJson<TopicList>(json, TopicList::class.java).topics
            }
            bindTopAdapter()//最顶层的列表
            bindGroupAdapter()  //组 数据列表
            bindSingleAdapter()//  //单个数据列表
        }

    }

    //top 最顶的一个横向列表
    lateinit var mainFragment_DSRV_top: DragAndSwipeRecyclerView;
    private lateinit var topAdapter: DragAndSwipeRecyclerViewAdapter<MainTopBean>
    private lateinit var topicList: List<MainTopBean>
    //最顶层的列表
    private fun bindTopAdapter() {
        mainFragment_DSRV_top =binding.mainFragmentDSRVTop
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
        topAdapter = DragAndSwipeRecyclerViewAdapter<MainTopBean>(context!!)
                //加载item布局
                .match(MainTopBean::class, R.layout.fragment_main_top_item)
                .holderCreateListener {

                }
                .holderBindListener { holder, position ->
                    val topic = topAdapter.getItem(position)
                    holder.withView<ImageView>(R.id.fragment_main_top_item_iv, {
                        //是否已经打开了场景的控制
                        if (topic.isOf) {
                            //已经打开场景
                            Glide.with(context!!).load(topic.imgUrl).into(this)
                        } else {
                            //未打开场景
                            Glide.with(context!!).load(topic.imgUrl).into(this)
                        }

                    })
                            .withView<TextView>(R.id.fragment_main_top_item_tv, { text = topic.title })
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
    private lateinit var groupAdapter: DragAndSwipeRecyclerViewAdapter<MainGroupBean>
    private lateinit var groupicList: List<MainGroupBean>
    //组 数据列表
    @SuppressLint("NewApi")
    private fun bindGroupAdapter() {
        mainFragment_DSRV_group = binding.mainFragmentDSRVGroup
        mainFragment_DSRV_group.isLongPressDragEnable = true
        mainFragment_DSRV_group.isItemViewSwipeEnable = false
        mainFragment_DSRV_group.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        mainFragment_DSRV_group.swipeDirection = ItemTouchHelper.LEFT
        mainFragment_DSRV_group.layoutManager = LinearLayoutManager(context)
        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
        //decoration.setItemOffsets(ScreenUtils.dp2PxInt(context, 10f))
        decoration.setOffsetEdge(true)
        decoration.setOffsetLast(true)
        mainFragment_DSRV_group.addItemDecoration(decoration)
        groupAdapter = DragAndSwipeRecyclerViewAdapter<MainGroupBean>(context!!)
                .match(MainGroupBean::class, R.layout.all_single_item)
                .holderCreateListener {

                }
                .holderBindListener { holder, position ->
                    val topic = groupAdapter.getItem(position)
                    holder.withView<TextView>(R.id.all_single_item_tv, {
                        text = topic.groupName
                        // //是否已经打开了当前组的控制
                        if (topic.isOf) {
                            //当前组的灯已经打开
                            val drawable: Drawable = context!!.resources.getDrawable(R.mipmap.arrow_r, null)//getResources().getDrawable(R.drawable.drawable);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            this.setCompoundDrawables(drawable, null, null, null);
                        } else {
                            //当前组未打开
                            val drawable: Drawable = context!!.resources.getDrawable(R.mipmap.arrow_l, null)//getResources().getDrawable(R.drawable.drawable);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            this.setCompoundDrawables(drawable, null, null, null);
                        }

                    })
                }
                .clickListener { holder, position ->
                    val topic = groupAdapter.getItem(position)

                    Toast.makeText(context!!, "position $position, ${topic.groupName} clicked", Toast.LENGTH_LONG)
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
    private lateinit var singleAdapter: DragAndSwipeRecyclerViewAdapter<LightBean>
    private lateinit var singleicList: List<LightBean>
    //单个数据列表
    @SuppressLint("NewApi")
    private fun bindSingleAdapter() {
        mainFragment_DSRV_single = binding.mainFragmentSSRVSingle
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
        singleAdapter = DragAndSwipeRecyclerViewAdapter<LightBean>(context!!)
                .match(LightBean::class, R.layout.all_single_item)
                .holderCreateListener {

                }
                .holderBindListener { holder, position ->
                    val topic = singleAdapter.getItem(position)
                    holder.withView<TextView>(R.id.all_single_item_tv, {
                        text = topic.getLabel()
                        // //是否已经打开了当前单个设备的控制
                        if (topic.selected) {
                            //当前单个设备的灯已经打开
                            val drawable: Drawable = context!!.resources.getDrawable(R.mipmap.arrow_r, null)//getResources().getDrawable(R.drawable.drawable);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            this.setCompoundDrawables(drawable, null, null, null);
                        } else {
                            //当前单个设备未打开
                            val drawable: Drawable = context!!.resources.getDrawable(R.mipmap.arrow_l, null)//getResources().getDrawable(R.drawable.drawable);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            this.setCompoundDrawables(drawable, null, null, null);
                        }

                    })
                }
                .clickListener { holder, position ->
                    val topic = singleAdapter.getItem(position)

                    Toast.makeText(context!!, "position $position, ${topic.getLabel()} clicked", Toast.LENGTH_LONG)
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