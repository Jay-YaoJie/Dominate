package jeff.device

import android.annotation.SuppressLint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import ch.ielse.view.SwitchView
import com.bumptech.glide.Glide
import com.jeff.mylibrary.R
import com.wuhenzhizao.titlebar.utils.ScreenUtils
import jeff.bases.BaseActivity
import jeff.beans.FragmentAdapterBeans.DeviceI
import jeff.utils.LogUtils
import jeff.widgets.LinearOffsetsItemDecoration
import kotlin_adapter.adapter_core.attach
import kotlin_adapter.adapter_core.clickListener
import kotlin_adapter.adapter_core.extension.putItems
import kotlin_adapter.adapter_core.holderBindListener
import kotlin_adapter.adapter_core.match
import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerView
import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerViewAdapter
import kotlin_adapter.adapter_exension.dragSwipeDismiss.dragListener
import kotlin_adapter.adapter_exension.dragSwipeDismiss.swipeListener

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-28.
 * description ：DeviceScanningActivity 设备扫描，添加
 */
@SuppressLint("NewApi")
open class DeviceScaningActivity : BaseActivity<DeviceScaningActivityDB>() {
    override fun getContentViewId(): Int = R.layout.activity_device_scaning
    override fun initViews() {
        //topLeftView(null,R.mipmap.arrow_l,true)
        topMiddleView(resources.getString(R.string.add_device), 0)
    }

    // single  ////单个数据列表
    private lateinit var mainFragment_DSRV_single: DragAndSwipeRecyclerView;
    open var singleAdapter: DragAndSwipeRecyclerViewAdapter<DeviceI>? = null
    open var deviceList: ArrayList<DeviceI> = ArrayList()
    //单个数据列表
    open fun bindAdapter() {
        mainFragment_DSRV_single = binding.deviceScaningActivityDSRV
        mainFragment_DSRV_single.isLongPressDragEnable = true
        mainFragment_DSRV_single.isItemViewSwipeEnable = false
        mainFragment_DSRV_single.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        //  mainFragment_DSRV_single.swipeDirection = ItemTouchHelper.LEFT
        mainFragment_DSRV_single.layoutManager = LinearLayoutManager(mActivity) as RecyclerView.LayoutManager?
        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
        decoration.setItemOffsets(ScreenUtils.dp2PxInt(mActivity, 10f))
        decoration.setOffsetEdge(true)
        decoration.setOffsetLast(true)
        mainFragment_DSRV_single.addItemDecoration(decoration)
        LogUtils.d(tag, "没有移动之前的items  singleList.toString()=" + deviceList.toString())
        singleAdapter = DragAndSwipeRecyclerViewAdapter<DeviceI>(mActivity)
                .match(DeviceI::class, R.layout.all_single_item)
               // .holderCreateListener {}
                .holderBindListener { holder, position ->
                    val topic = singleAdapter!!.getItem(position)
                    holder.withView<TextView>(R.id.all_single_item_tv, {
                        this.text = topic.deviceName

                    }).withView<SwitchView>(R.id.all_single_item_sv, {
                        this.visibility = View.GONE
                    }).withView<ImageView>(R.id.all_single_item_iv, {
                        Glide.with(mActivity).load(R.mipmap.icon_add).into(this)
                        this.setOnClickListener {
                            deviceScanningChlidClick(topic)
                        }
                    })
                }
                .clickListener { holder, position ->
                    val topic = singleAdapter!!.getItem(position)
                    deviceScanningClickListener(topic)
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
       singleAdapter!!.putItems(deviceList)
    }

    //单个控件点击事件
    open fun deviceScanningChlidClick(deviceInfo: DeviceI): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceInfo.toString()} ")
        return false
    }

    //点击列表事件
    open fun deviceScanningClickListener(deviceInfo: DeviceI): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceInfo.toString()} ")
        return false
    }
}