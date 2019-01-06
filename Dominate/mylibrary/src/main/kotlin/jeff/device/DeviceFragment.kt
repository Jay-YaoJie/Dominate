package jeff.device

import android.annotation.SuppressLint
import android.app.Dialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import ch.ielse.view.SwitchView
import co.metalab.asyncawait.async
import com.jeff.mylibrary.R
import com.wuhenzhizao.titlebar.utils.ScreenUtils
import jeff.bases.BaseFragment
import jeff.constants.DeviceBean
import jeff.utils.LogUtils
import jeff.utils.SPUtils
import jeff.utils.SPUtils.Companion.tag
import jeff.utils.ToastUtil
import jeff.widgets.LinearOffsetsItemDecoration
import kotlin_adapter.adapter_core.*
import kotlin_adapter.adapter_core.extension.putItems
import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerView
import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerViewAdapter
import kotlin_adapter.adapter_exension.dragSwipeDismiss.dragListener
import kotlin_adapter.adapter_exension.dragSwipeDismiss.swipeListener
import android.text.TextUtils
import android.graphics.Typeface
import android.support.v4.app.DialogFragment
import android.text.InputType
import android.widget.*
import com.kongzue.dialog.listener.InputDialogOkButtonClickListener
import com.kongzue.dialog.util.InputInfo
import com.kongzue.dialog.v2.DialogSettings
import com.kongzue.dialog.v2.InputDialog
import com.mylhyl.circledialog.CircleDialog
import kotlin_adapter.adapter_core.extension.getItems
import kotlin_adapter.adapter_exension.swipeMenu.SwipeMenuStickyRecyclerViewAdapter


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：DeviceFragment 设备管理
 */
@SuppressLint("NewApi")
open class DeviceFragment : BaseFragment<DeviceFragmentDB>() {
    override fun getContentViewId(): Int = R.layout.fragment_device
    override fun initViews() {
        async {
            await<Unit> {
                //加载测试数据
                //group  //组 数据列表
                groupList = SPUtils.getDeviceBeans(mActivity, "deviceGroupList")
                // single  ////单个数据列表
                singleList = SPUtils.getDeviceBeans(mActivity, "deviceSingleList")

            }
            //加载数据列表适配器
            bindGroupAdapter()  //组 数据列表
            bindSingleAdapter()//  //单个数据列表
        }
        //点击所有设备事件
        binding.deviceFragmentAllTV.setOnClickListener {
            LogUtils.d(tag, "点击所有设备事件")
        }
        //点击组，添加组
        binding.deviceFragmentGroupIv.setOnClickListener {
            //iOS 风格对应 DialogSettings.STYLE_IOS
            DialogSettings.style = DialogSettings.STYLE_IOS
            //设置提示框主题为亮色主题
            DialogSettings.tip_theme = DialogSettings.THEME_LIGHT;
            InputDialog.show(mActivity, "分组昵称", "请输入分组昵称：", object : InputDialogOkButtonClickListener {
                override fun onClick(dialog: Dialog?, inputText: String?) {
                    if (inputText!!.isNullOrEmpty()) {
                        ToastUtil.show(mActivity, "请输入分组昵称")
                    } else {
                        if (inputGroupName(inputText)) {
                            dialog!!.dismiss()
                        }

                    }

                }
            }).setInputInfo(InputInfo().setMAX_LENGTH(10).setInputType(InputType.TYPE_CLASS_TEXT))

        }
    }

    //分组昵称
    open fun inputGroupName(nputText: String?): Boolean {
        return true
    }


    //group  //组 数据列表
    lateinit var mainFragment_DSRV_group: DragAndSwipeRecyclerView;
    private lateinit var groupAdapter: DragAndSwipeRecyclerViewAdapter<DeviceBean>
    open var groupList: ArrayList<DeviceBean> = ArrayList()
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
        groupAdapter = DragAndSwipeRecyclerViewAdapter<DeviceBean>(mActivity)
                .match(DeviceBean::class, R.layout.all_single_iv_a_item)
                .holderCreateListener {
                }
                .holderBindListener { holder, position ->
                    val topic = groupAdapter.getItem(position)
                    holder.withView<TextView>(R.id.all_single_iv_item_tv, {
                        text = topic.meshAddress.toString()

                    }).withView<ImageView>(R.id.all_single_iv_item_iv, {
                        //点击添加按钮
                        groupAdd(topic)
                    })
                }
                .clickListener { holder, position ->
                    val topic = groupAdapter.getItem(position)
                    groupClickListener(topic)
                }
                .dragListener { from, target ->
                    //当前移动的数据
                    ToastUtil.show("item is dragged, from $from to $target")
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + BluetoothInfoList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + BluetoothInfoList)

                }
                .swipeListener { position, direction ->
                    //当前移动取消数据

                    ToastUtil.show("position $position dismissed $direction")
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
                }
                .attach(mainFragment_DSRV_group)
        groupAdapter.putItems(groupList)

    }

    private lateinit var groupAddAdapter: ListViewAdapter<DeviceBean>
    private lateinit var roupAddList: ArrayList<DeviceBean>
    private lateinit var listview:ListView
    //点击添加按钮，组里添加设备
    open fun groupAdd(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tag, "点击添加按钮，组里添加设备 deviceBean= ${deviceBean.toString()} ")
        groupAddAdapter = ListViewAdapter(context!!, roupAddList)
                .match(DeviceBean::class, R.layout.all_single_iv_c_item)
                .holderCreateListener {

                }
                .holderBindListener { holder, position ->
                    val province:DeviceBean = roupAddList[position]
                    holder.withView<TextView>(R.id.all_single_iv_c_tv, { text = province.meshAddress.toString() })
                            .withView<CheckBox>(R.id.all_single_iv_c_cb, { isChecked = province.checkd })
                }
                .clickListener { holder, position ->
                    val province = roupAddList[position]
                   // showToast("position $position, ${province.name} clicked")
                    groupAddAdapter.getItems().forEachIndexed { index, item ->
                        //当前选择的；item
                       // item.checked = (index == position)
                    }
                    groupAddAdapter.notifyDataSetChanged()
                }
                .longClickListener { holder, position ->
                    val province = groupAddAdapter.getItem(position)
                    //showToast("position $position, ${province.name} long clicked")
                }
                .attach(listview)

        return true
    }

    //点击列表事件
    open fun groupClickListener(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceBean.toString()} ")
        return true
    }

    // single  ////单个数据列表
    lateinit var mainFragment_DSRV_single: DragAndSwipeRecyclerView;
    private lateinit var singleAdapter: DragAndSwipeRecyclerViewAdapter<DeviceBean>
    open var singleList: ArrayList<DeviceBean> = ArrayList()
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
        singleAdapter = DragAndSwipeRecyclerViewAdapter<DeviceBean>(mActivity)
                .match(DeviceBean::class, R.layout.all_single_iv_r_item)
                .holderCreateListener {

                }
                .holderBindListener { holder, position ->
                    val topic = singleAdapter.getItem(position)
                    holder.withView<TextView>(R.id.all_single_iv_item_tv, {
                        text = topic.meshAddress!!.toString()

                    })
                }
                .clickListener { holder, position ->
                    val topic = singleAdapter.getItem(position)
                    singleClickListener(topic)
                }
                .dragListener { from, target ->
                    //当前移动的数据

                    ToastUtil.show("item is dragged, from $from to $target")
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())

                }
                .swipeListener { position, direction ->
                    //当前移动取消数据

                    ToastUtil.show("position $position dismissed $direction")
                    //移动后的items
//                    Log.d("", "移动后的items topicList.toString()=" + topicList.toString())
//                    Log.d("", "移动后的items  adapter.getItems()=" + topAdapter.getItems())
                }
                .attach(mainFragment_DSRV_single)
        singleAdapter.putItems(singleList)
    }

    //点击列表事件
    open fun singleClickListener(deviceBean: DeviceBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${deviceBean.toString()} ")
        return true
    }

}