package kotlin_adapter.adapter_core

/**
 * Created by liufei on 2017/12/3.
 */
class ItemType(val itemLayoutId: Int, val variableId: Int) {

    constructor(itemLayoutId: Int) : this(itemLayoutId, 0)
}