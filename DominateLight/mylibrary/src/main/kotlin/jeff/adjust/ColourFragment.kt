package jeff.adjust

import com.jeff.mylibrary.R
import jeff.bases.BaseFragment
import jeff.utils.LogUtils
import jeff.utils.SPUtils.Companion.tag
import jeff.widgets.ColorPicker

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-05 0:35
 * description ：ColourFragment  颜色
 */
open class ColourFragment : BaseFragment<ColourFragmentDB>() {
    override fun getContentViewId(): Int = R.layout.fragment_colour
    private var preTime: Long = 0
    private val delayTime = 100
    override fun initViews() {

        binding.colourPicker.setOnColorChangeListener(object : ColorPicker.OnColorChangeListener {
            override fun onStartTrackingTouch(view: ColorPicker?) {
                // T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                preTime = System.currentTimeMillis()
                changeColor(view!!.getColor())
            }

            override fun onStopTrackingTouch(view: ColorPicker?) {
                changeColor(view!!.getColor())
                // T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onColorChanged(view: ColorPicker?, color: Int) {
                // T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                val currentTime = System.currentTimeMillis()

                if (currentTime - preTime >= delayTime) {
                    preTime = currentTime
                    changeColor(color)
                }
            }
        })
    }

    open fun changeColor(color: Int) {
        LogUtils.d(tag, "当前选择的颜色值 color=" + color)
    }
}