package bases

import android.content.Intent
import jeff.bases.WelcomeActivity
import login.LoginActivity

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-27.
 * description ：WelcomeActivity 欢迎页面
 */
class WelcomeActivity : WelcomeActivity() {
    override fun login(){
       //登录页面
      startActivity(Intent(this ,LoginActivity::class.java))
        this.finish()
    }

    override fun main() {
      //进入主页
        startActivity(Intent(this ,MainActivity::class.java))
        this.finish()
    }
}