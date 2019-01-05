package adjust

import adjust.AdjustActivity.Companion.meshAddress
import android.view.View
import bases.DominateApplication.Companion.mLightService
import com.jeff.dominate.TelinkLightService
import jeff.adjust.BrightnessFragment


/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-05 0:33
 * description ：BrightnessFragment 亮度
 */
class BrightnessFragment : BrightnessFragment() {
    override fun changeBrightness(progress: Int) {
        super.changeBrightness(progress)
        val addr = meshAddress
        val opcode: Byte
        val params: ByteArray
        var progress2: Int = progress
        progress2 += 5 * 100
        opcode = 0xD2.toByte()
        params = byteArrayOf(progress2.toByte())

        mLightService.sendCommandNoResponse(opcode, addr, params)
    }


}