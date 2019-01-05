package jeff.adjust

import com.jeff.mylibrary.R
import jeff.bases.BaseFragment
import jeff.utils.LogUtils
import kotlinx.android.synthetic.main.fragment_colour_temperature.view.*


/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-05 0:34
 * description ：ColourTemperatureFragment  色温
 */
open class ColourTemperatureFragment : BaseFragment<ColourTemperatureFragmentDB>() {
    override fun getContentViewId(): Int = R.layout.fragment_colour_temperature

    override fun initViews() {
        binding.colourTemperatureSeekbar.setOnSeekBarChangeListener { _, curValue ->
            binding.colourTemperatureTextview.text = curValue.toString()
            changeColourTemperature(curValue)
        }
        binding.colourTemperatureSeekbar.curProcess = 5
    }

   open fun changeColourTemperature(curValue: Int) {
        LogUtils.d(tag, "当前选择的色温是curValue=" + curValue)
    }
}