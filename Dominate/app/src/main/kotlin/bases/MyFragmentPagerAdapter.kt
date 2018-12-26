package bases

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：MyFragmentPagerAdapter  Fragment的适配器
 */
open class MyFragmentPagerAdapter(fm: FragmentManager,  val mList: List<Fragment>?) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return if (this.mList == null) null else this.mList[position]
    }

    override fun getCount(): Int {
        return if (this.mList == null) 0 else this.mList.size
    }
}