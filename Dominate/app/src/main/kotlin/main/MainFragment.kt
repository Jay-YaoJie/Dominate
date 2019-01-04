package main

import android.content.Intent
import device.DeviceScaningActivity
import jeff.main.MainFragment


/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-27 23:16
 * description ：MainFragment 主页显示
 */
class MainFragment : MainFragment() {
    override fun initViews() {
        super.initViews()
        binding.mainFragmentDeviceUngroupedTV.setOnClickListener {
            mActivity.startActivity(Intent(mActivity, DeviceScaningActivity::class.java))
        }
    }

    override fun lazyLoad() {
        super.lazyLoad()
        //可见时
    }
}