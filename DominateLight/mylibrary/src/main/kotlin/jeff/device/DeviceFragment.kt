package jeff.device

import android.annotation.SuppressLint
import android.app.Dialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.InputType
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import co.metalab.asyncawait.async
import com.jeff.mylibrary.R
import com.kongzue.dialog.listener.InputDialogOkButtonClickListener
import com.kongzue.dialog.util.InputInfo
import com.kongzue.dialog.v2.CustomDialog
import com.kongzue.dialog.v2.DialogSettings
import com.kongzue.dialog.v2.InputDialog
import com.kongzue.dialog.v2.WaitDialog
import com.wuhenzhizao.titlebar.utils.ScreenUtils
import jeff.bases.BaseFragment
import jeff.constants.DeviceBean
import jeff.constants.GroupBean
import jeff.utils.LogUtils
import jeff.utils.SPUtils
import jeff.utils.ToastUtil
import jeff.widgets.LinearOffsetsItemDecoration
import kotlin_adapter.adapter_core.*
import kotlin_adapter.adapter_core.extension.clear
import kotlin_adapter.adapter_core.extension.putItems
import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerView
import kotlin_adapter.adapter_exension.dragSwipeDismiss.DragAndSwipeRecyclerViewAdapter
import kotlin_adapter.adapter_exension.dragSwipeDismiss.dragListener
import kotlin_adapter.adapter_exension.dragSwipeDismiss.swipeListener
import java.util.logging.Handler


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
                groupList = SPUtils.getGroupBeans(mActivity, "grouplist")
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

            InputDialog.show(mActivity, "分组昵称", "请输入分组昵称：", object : InputDialogOkButtonClickListener {
                override fun onClick(dialog: Dialog?, inputText: String?) {
                    if (inputText!!.isNullOrEmpty()) {
                        ToastUtil.show(mActivity, "请输入分组昵称")
                    } else {
                        if (inputGroupName(inputText)) {
                            dialog!!.dismiss()
                            inputGroupNameOk()
                        }
                    }
                }
            }).setInputInfo(InputInfo().setMAX_LENGTH(10).setInputType(InputType.TYPE_CLASS_TEXT))
        }
    }

    //分组昵称
    private fun inputGroupName(nputText: String?): Boolean {
        var groupNamelist: ArrayList<GroupBean> = SPUtils.getGroupBeans(mActivity, "grouplist")
        for (groupNme: GroupBean in groupNamelist) {
            if (groupNme.equals(nputText)) {
                ToastUtil.show("当前输入的昵称已存在，请重新输入！")
                return false
            }
        }
        var groupNme = GroupBean()
        groupNme.brightness = 0
        //分组的组名
        groupNme.groupName = nputText //当前组的名称
        groupNme.groupId = groupNamelist.size + 1//当前id
        //以下是 组里的控制器
        if (groupNamelist.size > 0) {
            groupNme.meshAddress = groupNamelist.last().meshAddress + 1//灯的名称 数据列表12345
        } else {
            groupNme.meshAddress = 0x8001
        }
        LogUtils.d(tag, "分组昵称groupNme=" + groupNme)
        groupNamelist.add(groupNme)

        SPUtils.deviceBeansClear(mActivity, "grouplist")
        SPUtils.setGroupBeans(mActivity, "grouplist", groupNamelist)
        return true
    }

    //group  //组 数据列表
    lateinit var mainFragment_DSRV_group: DragAndSwipeRecyclerView;
    open lateinit var groupAdapter: DragAndSwipeRecyclerViewAdapter<GroupBean>
    open var groupList: ArrayList<GroupBean> = ArrayList()
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
        groupAdapter = DragAndSwipeRecyclerViewAdapter<GroupBean>(mActivity)
                .match(GroupBean::class, R.layout.all_single_iv_a_item)
                .holderCreateListener {
                }
                .holderBindListener { holder, position ->
                    val topic = groupAdapter.getItem(position)
                    holder.withView<TextView>(R.id.all_single_iv_item_tv, {
                        text = topic.meshAddress.toString()

                    }).withView<ImageView>(R.id.all_single_iv_item_iv, {
                        this.setOnClickListener {
                            //点击添加按钮 弹出选择列表
                            val customView = LayoutInflater.from(mActivity).inflate(R.layout.add_list, null)
                            customDialog = CustomDialog.show(mActivity, customView, {
                                groupAdd = customView.findViewById<DragAndSwipeRecyclerView>(R.id.add_list_groupadd_DSRV)
                                customView.findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
                                    customDialog!!.doDismiss()
                                }
                                customView.findViewById<TextView>(R.id.btn_ok).setOnClickListener {
                                    //向组里添加设备
                                    customDialog!!.doDismiss()
                                    wait = WaitDialog.show(mActivity, "${topic.groupName}组正在添加设备...").setCanCancel(false);
                                    groupAdd(groupBean, groupAddList)

                                }

                            })
                            groupAddList = ArrayList()
                            //，如果没有单个设备则不能选择
                            async {
                                await<Unit> {
                                    groupBean = topic
                                    for (deviceBean: DeviceBean in singleList) {
                                        if (!deviceBean.groupMeshAddressList.contains(groupBean.meshAddress)) {
                                            //如果添加的当前组没有10个
                                            groupAddList.add(deviceBean)
                                        }
                                    }
                                }
                                groupAddDialog()
                                groupAddAdapter!!.putItems(groupAddList)
                            }
                        }

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

    open fun inputGroupNameOk() {
    }

    private lateinit var wait: WaitDialog
    open fun waitDismiss(){
        wait.doDismiss()
    }
    //点击添加按钮，组里添加设备
    open fun groupAdd(groupName: GroupBean, singleList: ArrayList<DeviceBean>) {
        LogUtils.d(tag, "向组里添加设备完成")
    }

    private var customDialog: CustomDialog? = null
    private var groupAddAdapter: DragAndSwipeRecyclerViewAdapter<DeviceBean>? = null
    private lateinit var groupAddList: ArrayList<DeviceBean>
    private lateinit var groupAdd: DragAndSwipeRecyclerView;
    private lateinit var groupBean: GroupBean
    //点击组添加设备显示列表
    private fun groupAddDialog() {
        groupAdd.isLongPressDragEnable = true
        groupAdd.isItemViewSwipeEnable = false
        groupAdd.dragDirection = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        // mainFragment_DSRV_group.swipeDirection = ItemTouchHelper.LEFT
        groupAdd.layoutManager = LinearLayoutManager(mActivity)
        val decoration = LinearOffsetsItemDecoration(LinearOffsetsItemDecoration.LINEAR_OFFSETS_VERTICAL)  //LINEAR_OFFSETS_HORIZONTAL  LINEAR_OFFSETS_VERTICAL
        //decoration.setItemOffsets(ScreenUtils.dp2PxInt(mActivity, 10f))
        decoration.setOffsetEdge(true)
        decoration.setOffsetLast(true)
        groupAdd.addItemDecoration(decoration)
        LogUtils.d(tag, "点击添加按钮，组里添加设备 groupName= ${groupBean.toString()} ")
        groupAddAdapter = DragAndSwipeRecyclerViewAdapter(context!!, groupAddList)
                .match(DeviceBean::class, R.layout.all_single_iv_c_item)
                .holderCreateListener {
                }
                .holderBindListener { holder, position ->
                    val province: DeviceBean = groupAddList[position]
                    holder.withView<TextView>(R.id.all_single_iv_c_tv, {
                        text = province.meshAddress.toString()
                    }).withView<CheckBox>(R.id.all_single_iv_c_cb, {
                        isChecked = province.checkd
                    })
                }
                .clickListener { holder, position ->
                    //val province = groupAddList[position]
                    // showToast("position $position, ${province.name} clicked")
                    //当前选择的；item
                    // item.checked = (index == position)
                    if (groupAddList[position].checkd) {
                        groupAddList[position].checkd = false
                    } else {
                        groupAddList[position].checkd = true
                    }

                    groupAddAdapter!!.notifyDataSetChanged()
                }
                .longClickListener { holder, position ->
                    val province = groupAddAdapter!!.getItem(position)
                    //showToast("position $position, ${province.name} long clicked")
                }
                .attach(groupAdd)
    }

    //点击列表事件
    open fun groupClickListener(groupName: GroupBean): Boolean {
        LogUtils.d(tag, "点击列表事件 deviceBean= ${groupName.toString()} ")
        return true
    }

    // single  ////单个数据列表
    lateinit var mainFragment_DSRV_single: DragAndSwipeRecyclerView;
    open lateinit var singleAdapter: DragAndSwipeRecyclerViewAdapter<DeviceBean>
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