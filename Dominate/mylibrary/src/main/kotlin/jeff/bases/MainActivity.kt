package jeff.bases

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.widget.RadioButton
import com.jeff.mylibrary.R
import jeff.device.DeviceFragment
import jeff.main.MainFragment
import jeff.me.MeFragment
import jeff.scene.SceneFragment

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：bases.MainActivity
 */
open class MainActivity : BaseActivity<MainActivityDB>() {

    /**************************页面使用，没有功能***********************************************/
    override fun getContentViewId(): Int=R.layout.activity_mains
    override fun initViews() { //
    mTabRadioGroup()// TabRadioGroup  的点击或着滑动切换

    }

    var mFragments: MutableList<Fragment> = ArrayList()
    private var mAdapter: FragmentPagerAdapter? = null
    // TabRadioGroup  的点击或着滑动切换
    fun mTabRadioGroup() {
        // // init fragment
        mFragments.add(MainFragment())//主页
        mFragments.add(SceneFragment())//情景
        mFragments.add(DeviceFragment())//设备管理
        mFragments.add(MeFragment())//我的
        //页面管理  // init view pager
        mAdapter = MyFragmentPagerAdapter(supportFragmentManager, mFragments)
        binding.mainVP.adapter = mAdapter
        // register listener
        binding.mainVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
                (binding.tabsRG.getChildAt(position) as RadioButton).isChecked = true
            }

        })
        binding.tabsRG.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount) {
                if (group.getChildAt(i).id == checkedId) {
                    binding.mainVP.setCurrentItem(i)
                    break
                }
            }
        }
    }

}


