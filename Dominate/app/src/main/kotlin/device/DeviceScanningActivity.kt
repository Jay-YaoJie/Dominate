package device

import co.metalab.asyncawait.async
import jeff.device.DeviceScaningActivity


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-28.
 * description ：DeviceScanningActivity 扫描并添加设备
 */
 class DeviceScanningActivity : DeviceScaningActivity() {
    override fun initViews() {
        super.initViews()
        async {
            await<Unit> {
                //加载测试数据

            }
            bindAdapter()//加载数据列表适配器
        }
    }
}