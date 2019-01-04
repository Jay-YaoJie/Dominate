package jeff.bases

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import jeff.utils.ActivitiesManager.popActivity
import jeff.utils.ActivitiesManager.pushActivity
import jeff.utils.LogUtils
import kotlinx.android.synthetic.main.top.view.*


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：BaseActivity  Activity 的base
 */
@SuppressLint("NewApi")
abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: DB

    protected lateinit var mActivity: AppCompatActivity
    protected abstract fun getContentViewId(): Int
    open protected var tag: String = "BaseActivity"
    protected abstract fun initViews()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d(tag, "getContentViewId" + getContentViewId())
        binding = DataBindingUtil.setContentView(this, getContentViewId())
        mActivity = this;
        tag = this.javaClass.name
        pushActivity(this);//添加当前activity
        initViews()

    }

    /*顶部的 左 控件*/
    open fun topLeftView(text: String?, img: Int, isblak: Boolean): TextView {
        val textView: TextView = binding.root.top_left
        textView.visibility = View.VISIBLE
        if (text.isNullOrEmpty()) {
            textView.text = text
        }
        if (img > 0) {
            val nav_up = mActivity.resources.getDrawable(img, null)//.resources.getDrawable(img)
            nav_up.setBounds(0, 0, nav_up.minimumWidth, nav_up.minimumHeight)
            textView.setCompoundDrawables(nav_up, null, null, null)
        }
        if (isblak) {
            textView.setOnClickListener({
                //关闭页面
                mActivity.finish()
            })

        }
        return textView;
    }

    /*顶部的 中间  控件*/
    open fun topMiddleView(text: String?, img: Int): TextView {
        val textView: TextView = binding.root.top_middle
        textView.visibility = View.VISIBLE
        if (text.isNullOrEmpty()) {
            textView.text = text
        }
        if (img > 0) {
            val nav_up = mActivity.resources.getDrawable(img, null)//.resources.getDrawable(img)
            nav_up.setBounds(0, 0, nav_up.minimumWidth, nav_up.minimumHeight)
            textView.setCompoundDrawables(nav_up, null, null, null)
        }
        return textView;
    }

    /*顶部的 右 控件*/
    open fun topRightView(text: String?, img: Int, ischke: Boolean): TextView {
        val textView: TextView = binding.root.top_right
        textView.visibility = View.VISIBLE
        if (text.isNullOrEmpty()) {
            textView.text = text
        }
        if (img > 0) {
            val nav_up = mActivity.resources.getDrawable(img, null)//.resources.getDrawable(img)
            nav_up.setBounds(0, 0, nav_up.minimumWidth, nav_up.minimumHeight)
            textView.setCompoundDrawables(nav_up, null, null, null)
        }
        return textView;
    }

    override fun onDestroy() {
        popActivity(this);//关闭当前activity
        super.onDestroy()

    }

}