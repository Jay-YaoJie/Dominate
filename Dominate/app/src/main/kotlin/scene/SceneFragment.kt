package scene

import android.content.Intent
import co.metalab.asyncawait.async
import jeff.scene.SceneFragment
import jeff.utils.SPUtils
import kotlin_adapter.adapter_core.extension.putItems

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-06 1:21
 * description ：SceneFragment 情景主页显示
 */
class SceneFragment : SceneFragment() {
    override fun lazyLoad() {
        super.lazyLoad()
        async {
            await<Unit> {
                //加载测试数据
                // single  ///情景数据列表
                sceneList = SPUtils.getDeviceBeans(mActivity, "deviceSceneList")
            }
            //加载数据列表适配器
            //情景列表
            sceneAdapter!!.putItems(sceneList)
            sceneAdapter!!.notifyDataSetChanged()
        }
    }

    override fun initViews() {
        super.initViews()
        //添加
        binding.topRight.setOnClickListener {
            //添加情景页面
            mActivity.startActivity(Intent(mActivity, AddSceneActivity::class.java))
        }
    }
}