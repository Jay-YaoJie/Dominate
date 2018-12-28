package jeff.bases

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
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
    protected lateinit var mActivity: BaseActivity<DB>;
    internal var tag: String = "BaseFragment";
    protected abstract fun getContentViewId(): Int


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, getContentViewId(), container, false)
        val rootView = binding.root
        val parent = rootView.parent
        if (parent != null && parent is ViewGroup) {
            parent.removeView(rootView)
        }
        mActivity = activity!! as BaseActivity<DB>
        tag=mActivity.localClassName
        initViews()
        LogUtils.d(tag,"initViews()")
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            //加载
            lazyLoad();
        }else{
            stopLoad()
        }
    }
    /**初始化的时候去加载数据**/
    protected abstract fun initViews()
    ////在这里假设一个Fragment需要加载很多数据很复杂很耗时的时间后才能获得足够数据渲染View
    //当视图初始化并且对用户可见的时候去真正的加载数据
    abstract fun lazyLoad()

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
     */
    protected fun stopLoad() {}
}