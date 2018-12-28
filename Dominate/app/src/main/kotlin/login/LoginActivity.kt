package login

import android.content.Intent
import com.jeff.dominate.R
import  device.DeviceScaningActivity
import jeff.login.LoginActivity
import jeff.utils.ToastUtil

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-27.
 * description ：LoginActivity 登录页面
 */
class LoginActivity : LoginActivity() {
    override fun login(): Boolean {
        if (super.login()) {
            //登录成功
            ToastUtil(R.string.login_success)
            startActivity(Intent(this, DeviceScaningActivity::class.java))
            this.finish()

            return true
        } else {
            //登录失败
            ToastUtil(R.string.logon_back)
            logonBack()//清除数据
        }

        return false
    }


}