package jeff.bases

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jeff.mylibrary.R
import jeff.utils.SPUtils

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-27.
 * description ：WelcomeActivity
 */
open class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welocme)
        Glide.with(this).load(R.drawable.welcome).into((findViewById(R.id.welcome) as ImageView))
        Thread(Runnable {
            try {
                Thread.sleep(3000) //睡眠3秒
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            if (SPUtils.getAutoLogin(this)) {
                //如果当前已经登录,并记录的是自动登录，就直接到主页
                main();
            } else {
                //如果没有自动登录，或着没有登录 就去登录页面
                login()
            }
        }).start()


    }

    fun login() {}//登录
    fun main() {}//已经登录过了，去主页
}