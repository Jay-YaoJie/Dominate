package jeff.bases

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jeff.utils.LogUtils

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：BaseFragment Fragment的base
 *Android ViewPager Fragment使用懒加载提升性能
 * https://www.cnblogs.com/wangfeng520/p/6108189.html
 */
abstract class BaseFragment<DB : ViewDataBinding> : Fragment() {
    protected lateinit var binding: DB
    protected lateinit var mActivity: FragmentActivity;
    open var tagFragment: String = "BaseFragment"
    protected abstract fun getContentViewId(): Int


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, getContentViewId(), container, false)
        val rootView = binding.root
        val parent = rootView.parent
        if (parent != null && parent is ViewGroup) {
            parent.removeView(rootView)
        }
        tagFragment = this.javaClass.name
        mActivity = activity!!
        initViews()
        isLazyLoad = false
        LogUtils.d(tag, "onCreateView")
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isLazyLoad) {
                //加载
                lazyLoad();
            }
        } else {
            stopLoad()
        }
    }

    override fun onStart() {
        super.onStart()
        isLazyLoad = true
        LogUtils.d(tag, "onStart")
    }

    override fun onStop() {
        super.onStop()
        isLazyLoad = false
        LogUtils.d(tag, "onStop")
    }

    /**初始化的时候去加载数据**/
    protected abstract fun initViews()

    var isLazyLoad: Boolean = false
    ////在这里假设一个Fragment需要加载很多数据很复杂很耗时的时间后才能获得足够数据渲染View
    //当视图初始化并且对用户可见的时候去真正的加载数据
    protected open fun lazyLoad() {
        LogUtils.d(tag, "lazyLoad")
    }


    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
     */
    protected open fun stopLoad() {
        LogUtils.d(tag, "stopLoad")
    }
}