package jeff.scene

import android.annotation.SuppressLint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.ImageView
import android.widget.TextView
import co.metalab.asyncawait.async
import com.jeff.mylibrary.R
import com.wuhenzhizao.titlebar.utils.ScreenUtils
import jeff.bases.BaseFragment
import jeff.constants.DeviceBean
import jeff.utils.LogUtils
import jeff.utils.SPUtils
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
 * description ：SceneFragment 场景模式
 */
open class SceneFragment : BaseFragment<SceneFragmentDB>() {

    override fun getContentViewId(): Int = R.layout.fragment_scene
    override fun initViews() {
        async {
            await<Unit> {
                //加载测试数据
                // single  ///情景数据列表
                sceneList = SPUtils.getDeviceBeans(mActivity, "deviceSceneList")
            }
            //加载数据列表适配器
            bindSeceneAdapter()//情景列表
        }
    }


    open lateinit var sceneAdapter: DragAndSwipeRecyclerViewAdapter<DeviceBean>
    lateinit var scene_fragment_SSRV: DragAndSwipeRecyclerView;
    open var sceneList: ArrayList<DeviceBean> = ArrayList()
    //单个数据列表
    @SuppressLint("NewApi")
    private fun bindSeceneAdapter() {
        scene_fragment_SSRV = binding.sceneFragmentSSRV
        scene_fragment_SSRV.isLongPressDragEnable = true
        scene_fragment_SSRV.isItemViewSwipeEnable = false
        scene_fragment_SSRV.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        //  mainFragment_DSRV_single.swipeDirection = ItemTouchHelper.LEFT
        scene_fragment_SSRV.layoutManager = LinearLayoutManager(context)
        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
        decoration.setItemOffsets(ScreenUtils.dp2PxInt(context, 10f))
        decoration.setOffsetEdge(true)
        decoration.setOffsetLast(true)
        scene_fragment_SSRV.addItemDecoration(decoration)
        sceneAdapter = DragAndSwipeRecyclerViewAdapter<DeviceBean>(context!!)
                .match(DeviceBean::class, R.layout.all_single_iv_r_item)
                .holderCreateListener {}
                .holderBindListener { holder, position ->
                    val topic = sceneAdapter.getItem(position)
                    holder.withView<TextView>(R.id.all_single_iv_item_iv, {
                        this.text = topic.sceneName
                    })
                }
                .clickListener { holder, position ->
                    val topic = sceneAdapter.getItem(position)
                    seceneClickListener(topic)
                    //Toast.makeText(context!!, "position $position, ${topic.getLabel()} clicked", Toast.LENGTH_LONG)
                }
                .dragListener { from, target ->
                    //当前移动的数据
                    // Toast.makeText(context!!, "item is dragged, from $from to $target", Toast.LENGTH_LONG)
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())

                }
                .swipeListener { position, direction ->
                    //当前移动取消数据
                    //  Toast.makeText(context!!, "position $position dismissed", Toast.LENGTH_LONG)
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
                }
                .attach(scene_fragment_SSRV)
        sceneAdapter.putItems(sceneList)
    }

    //点击列表事件
    open fun seceneClickListener(deviceInfo: DeviceBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceInfo.toString()} ")
        return false
    }
}