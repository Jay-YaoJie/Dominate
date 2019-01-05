package jeff.adjust


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.widget.RadioButton
import com.jeff.mylibrary.R
import jeff.bases.BaseActivity
import jeff.bases.MyFragmentPagerAdapter
import java.util.*

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2019-01-05 0:26
 * description ：AdjustActivity
 */
open class AdjustActivity : BaseActivity<AdjustActivityDB>() {
    /**************************页面使用，没有功能***********************************************/
    override fun getContentViewId(): Int = R.layout.activity_adjust

    override fun initViews() { //
//        mFragments.add(BrightnessFragment())//亮度
//        mFragments.add(ColourTemperatureFragment())//色温
//        mFragments.add(ColourFragment())//颜色
        mTabRadioGroup()// TabRadioGroup  的点击或着滑动切换
    }

    open var mFragments: MutableList<Fragment> = ArrayList()

    private var mAdapter: FragmentPagerAdapter? = null
    // TabRadioGroup  的点击或着滑动切换
    fun mTabRadioGroup() {
        // // init fragment

        //页面管理  // init view pager
        mAdapter = MyFragmentPagerAdapter(mActivity.supportFragmentManager, mFragments)
        //ViewPager 加载Fragment oncreatview() 方法重复调用最简单解决方案
        binding.adjustVP.setOffscreenPageLimit(3)
        binding.adjustVP.adapter = mAdapter
        // register listener
        binding.adjustVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(position: Int) {
                //("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //("not implemented") //To change body of created functions use File | Settings | File Templates.
                // val radioButton = mTabRadioGroup.getChildAt(position) as RadioButton
                // radioButton.isChecked = true
            }

            override fun onPageSelected(position: Int) {
                //("not implemented") //To change body of created functions use File | Settings | File Templates.
                (binding.adjustTabsRG.getChildAt(position) as RadioButton).isChecked = true
            }

        })
        binding.adjustTabsRG.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount) {
                if (group.getChildAt(i).id == checkedId) {
                    binding.adjustVP.setCurrentItem(i)
                    break
                }
            }
        }
    }

}