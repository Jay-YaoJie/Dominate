package me

import bases.BaseFragment
import com.jeff.dominate.R
import main.MeFragmentDB

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-21.
 * description ：meFragment
 */
class MeFragment : BaseFragment<MeFragmentDB>() {
    override fun getContentViewId(): Int= R.layout.fragment_me

    override fun initViews() {
           }

}