package jeff.login

import android.text.InputType
import com.jeff.mylibrary.R
import jeff.bases.BaseActivity
import jeff.utils.SPUtils
import jeff.utils.SPUtils.Companion.getLocalName
import jeff.utils.SPUtils.Companion.getLocalPassword
import jeff.utils.ToastUtil


/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-27 3:08
 * description ：LoginActivity 登录
 */
open class LoginActivity : BaseActivity<LoginActivityDB>() {

    /**************************页面使用，没有功能***********************************************/
    override fun getContentViewId(): Int = R.layout.activity_login

    //自动登录
    override fun initViews() {
        //判断用户第一次登陆
        if (getLocalName(mActivity) == null) {
            binding.checkBoxPassword.isChecked = false//取消记住密码的复选框
            binding.checkBoxLogin.isChecked = false//取消自动登录的复选框
        } else {
            //判断是否记住密码
            if (SPUtils.getRemenberPassword(mActivity)) {
                binding.checkBoxPassword.isChecked = true//勾选记住密码
                //)//把密码和账号输入到输入框中
                binding.etAccount.setText(getLocalName(mActivity))
                binding.etPassword.setText(getLocalPassword(mActivity))
            } else {
                //把用户账号放到输入账号的输入框中
                binding.etAccount.setText(getLocalName(mActivity))
            }
        }
        /***********添加点击和选择事件****/
        //显示密码，和隐藏密码
        binding.ivSeePassword.setOnClickListener {
            if (binding.ivSeePassword.isSelected) {
                //密码不可见
                binding.ivSeePassword.isSelected = false
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD;
            } else {
                //密码可见
                binding.ivSeePassword.isSelected = true
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
        }
        //选择事件记住密码
        binding.checkBoxPassword.setOnCheckedChangeListener { _, isChecked ->
            isSavePassword = isChecked
        }
        //选择事件，记录登录状态
        binding.checkBoxLogin.setOnCheckedChangeListener { _, isChecked ->
            isAutomaticLogin = isChecked;
        }
        //登录
        binding.btnLogin.setOnClickListener {
            login()//登录，登录成功了之后保存用户名和密码
        }
    }

    //保存帐号
    var name: String? = null
    //保存密码
    var password: String? = null
    //记住密码
    var isSavePassword: Boolean = false
    //自动登录
    var isAutomaticLogin: Boolean = false

    /**
     * 模拟登录情况
     * 用户名csdn，密码123456，就能登录成功，否则登录失败
     */
    private fun login() {
        //先做一些基本的判断，比如输入的用户命为空，密码为空，网络不可用多大情况，都不需要去链接服务器了，而是直接返回提示错误
        if (name.isNullOrEmpty()) {
            ToastUtil("你输入的账号为空！")
            return
        }

        if (password.isNullOrEmpty()) {
            ToastUtil("你输入的密码为空！")
            return
        }
        //设置登录不可以点击   //正在登录是不可以点击登录按钮的
        binding.btnLogin.isClickable=false
        //保存帐号  登录成功了之后保存用户名和密码
        SPUtils.setLoadLogin(mActivity, name!!, password!!, isSavePassword, isAutomaticLogin)
        //登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程

    }
}