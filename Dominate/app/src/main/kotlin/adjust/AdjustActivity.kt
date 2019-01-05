package adjust

import jeff.adjust.AdjustActivity

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-05 1:07
 * description ：AdjustActivity
 */
class AdjustActivity : AdjustActivity() {
    companion object {
        var meshAddress: Int = 0
    }

    override fun initViews() {
        super.initViews()
        mFragments.add(BrightnessFragment())//亮度
        mFragments.add(ColourTemperatureFragment())//色温
        mFragments.add(ColourFragment())//颜色

        super.initViews()
    }
}