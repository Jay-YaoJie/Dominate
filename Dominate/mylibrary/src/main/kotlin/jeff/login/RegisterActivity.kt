package jeff.login

import android.text.InputType
import com.jeff.mylibrary.R
import jeff.bases.BaseActivity
import jeff.utils.SPUtils
import jeff.utils.ToastUtil

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-27 3:08
 * description ：RegisterActivity 注册
 */
open class RegisterActivity : BaseActivity<RegisterActivityDB>() {

    /**************************页面使用，没有功能***********************************************/
    override fun getContentViewId(): Int = R.layout.activity_register

    //自动注册
    override fun initViews() {
        /***********添加点击和选择事件****/
        //显示密码，和隐藏密码
        binding.ivSeeRegisterPassword.setOnClickListener {
            if (binding.ivSeeRegisterPassword.isSelected) {
                //密码不可见
                binding.ivSeeRegisterPassword.isSelected = false
                binding.etRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD;
            } else {
                //密码可见
                binding.ivSeeRegisterPassword.isSelected = true
                binding.etRegisterPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
        }

        //登录
        binding.btnRegister.setOnClickListener {
            register()//登录，登录成功了之后保存用户名和密码
        }
    }

    //保存帐号
    open var name: String? = null
    //保存密码
    open var password: String? = null
    //记住密码
    var isSavePassword: Boolean = true
    //自动登录
    var isAutomaticLogin: Boolean = false

    /**
     * 模拟注册情况
     * 用户名csdn，密码123456，就能登录成功，否则注册失败
     */
    open fun register(): Boolean {
        name = binding.etRegisterAccount.text.toString().trim()//保存用户名
        password = binding.etRegisterPassword.text.toString().trim()//保存密码
        //先做一些基本的判断，比如输入的用户命为空，密码为空，网络不可用多大情况，都不需要去链接服务器了，而是直接返回提示错误
        if (name.isNullOrEmpty()) {
            ToastUtil.show(mActivity.resources.getString(R.string.register_account))
            return false
        }

        if (password.isNullOrEmpty()) {
            ToastUtil.show(mActivity.resources.getString(R.string.register_password))
            return false
        }
        //设置注册不可以点击   //正在注册是不可以点击注册按钮的
        binding.btnRegister.isClickable = false
        //保存帐号  登录成功了之后保存用户名和密码
        SPUtils.setLoadLogin(mActivity, name!!, password!!, isSavePassword, isAutomaticLogin)
        //登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
        return true
    }

    //重新 注册
    open fun registerBack() {
        binding.btnRegister.isClickable = true//提交按钮可以点击
        SPUtils.loginClear(mActivity)
    }
}