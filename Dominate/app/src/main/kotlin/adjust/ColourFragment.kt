package adjust

import adjust.AdjustActivity.Companion.meshAddress
import bases.DominateApplication.Companion.mLightService
import jeff.adjust.ColourFragment

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-05 0:35
 * description ：ColourFragment  颜色
 */
class ColourFragment : ColourFragment() {
    override fun changeColor(color: Int) {
        super.changeColor(color)
        val red = (color shr 16 and 0xFF).toByte()
        val green = (color shr 8 and 0xFF).toByte()
        val blue = (color and 0xFF).toByte()

        val addr = meshAddress
        val opcode = 0xE2.toByte()
        val params = byteArrayOf(0x04, red, green, blue)
        mLightService.sendCommandNoResponse(opcode, addr, params)
    }
}