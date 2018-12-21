package scene

import bases.BaseFragment
import com.jeff.dominate.R
import main.SceneFragmentDB

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：SceneFragment 场景模式
 */
class SceneFragment:BaseFragment<SceneFragmentDB>() {
    override fun getContentViewId(): Int= R.layout.fragment_scene

    override fun initViews() {
         }

}