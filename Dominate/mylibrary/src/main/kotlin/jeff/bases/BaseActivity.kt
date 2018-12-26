package jeff.bases

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
    lateinit var eventBus: EventBus;
    protected lateinit var mActivity: AppCompatActivity
    protected abstract fun getContentViewId(): Int
     protected  var tag: String = "BaseActivity"
    protected abstract fun initViews()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getContentViewId())
        initViews()
        mActivity = this;
        tag = this.localClassName
    }

    public override fun onStart() {
        super.onStart()
        eventBus=EventBus.getDefault()
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

}