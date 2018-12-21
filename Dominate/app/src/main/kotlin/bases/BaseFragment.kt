package bases

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * Created by liufei on 2017/10/12.
 */
abstract class BaseFragment<DB : ViewDataBinding> : Fragment() {
    protected lateinit var binding: DB
    protected lateinit var mActivity:AppCompatActivity;

    protected abstract fun getContentViewId(): Int

    protected abstract fun initViews()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, getContentViewId(), container, false)
        val rootView = binding.root
        val parent = rootView.parent
        if (parent != null && parent is ViewGroup) {
            parent.removeView(rootView)
        }
        initViews()
        mActivity= this.activity as AppCompatActivity
        return binding.root
    }

}