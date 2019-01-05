package adjust

import adjust.AdjustActivity.Companion.meshAddress
import bases.DominateApplication.Companion.mLightService
import com.jeff.dominate.TelinkLightService
import jeff.adjust.ColourTemperatureFragment

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-05 0:34
 * description ：ColourTemperatureFragment  色温
 */
class ColourTemperatureFragment : ColourTemperatureFragment() {
    override fun changeColourTemperature(curValue: Int) {
        super.changeColourTemperature(curValue)
        val addr = meshAddress
        val opcode: Byte
        val params: ByteArray
        opcode = 0xE2.toByte()
        params = byteArrayOf(0x05, curValue.toByte())
        mLightService.sendCommandNoResponse(opcode, addr, params)
    }
}