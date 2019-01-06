package device

import android.content.Intent
import bases.MainActivity
import jeff.device.DeviceFragment
import jeff.utils.LogUtils

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-28.
 * description ：DeviceFragment
 */
class DeviceFragment : DeviceFragment() {
    override fun initViews() {
        super.initViews()

        //点击设备，添加设备
        binding.deviceFragmentUngroupedIv.setOnClickListener {
            mActivity.startActivity(Intent(mActivity, DeviceScaningActivity::class.java))
        }

    }

    override fun lazyLoad() {
        super.lazyLoad()
        //可见时
    }
}