package kotlin_adapter.adapter_core.interfaces

import android.view.View


interface ViewHolderSupport {

    fun <T : View> getView(viewId: Int): T
}
