package jeff.bases

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jeff.utils.ActivitiesManager.popActivity
import jeff.utils.ActivitiesManager.pushActivity
import jeff.utils.LogUtils
import org.greenrobot.eventbus.EventBus


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：BaseActivity  Activity 的base
 */
abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: DB
    // 创建默认的EventBus对象，相当于EventBus.getDefault()。
    protected var eventBus: EventBus = EventBus.getDefault()

    protected lateinit var mActivity: AppCompatActivity
     protected abstract fun getContentViewId():Int
    protected var tag: String = "BaseActivity"
    protected abstract fun initViews()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d(tag,"getContentViewId"+getContentViewId())
        binding= DataBindingUtil.setContentView(this,getContentViewId())
        mActivity = this;
        tag = this.localClassName
        pushActivity(this);//添加当前activity
        initViews()
    }
    //如果要使用evnt就在这里注册，直接调用此方法
    open fun eventReg(){
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this)

        }
    }

    public override fun onStop() {
        super.onStop()
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this)
        }

    }

    override fun onDestroy() {
        popActivity(this);//关闭当前activity
        super.onDestroy()

    }

}