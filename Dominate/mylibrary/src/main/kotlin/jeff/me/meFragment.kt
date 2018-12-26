package jeff.me

import jeff.bases.BaseFragment
import com.jeff.mylibrary.R

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：meFragment 我的页面
 */
open class MeFragment : BaseFragment<MeFragmentDB>() {
    override fun getContentViewId(): Int= R.layout.fragment_me

    override fun initViews() {
           }

}