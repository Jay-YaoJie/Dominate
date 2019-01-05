package jeff.adjust

import android.view.View
import com.jeff.mylibrary.R
import jeff.bases.BaseFragment
import jeff.utils.LogUtils

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-05 0:33
 * description ：BrightnessFragment 亮度
 */
open class BrightnessFragment : BaseFragment<BrightnessFragmentDB>() {
    override fun getContentViewId(): Int = R.layout.fragment_brightness

    override fun initViews() {
        binding.brightnessProgress.progressChangedCallback = {
            val textStr = String.format("%.2f", it)
            val sourceI = java.lang.Float.valueOf(it).toInt()
            changeBrightness(sourceI)
            binding.brightnessProgressText.text = textStr
        }
    }

    open fun changeBrightness(progress: Int) {
        LogUtils.d(tag, "当前翻卷拖动了多少textStr=" + progress)
    }


}