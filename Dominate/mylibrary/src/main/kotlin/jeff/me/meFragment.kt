package jeff.me

import com.jeff.mylibrary.R
import jeff.bases.BaseFragment

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：meFragment 我的页面
 */
open class MeFragment : BaseFragment<MeFragmentDB>() {
    override fun lazyLoad() {
        //T ODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentViewId(): Int= R.layout.fragment_me

    override fun initViews() {
           }

}