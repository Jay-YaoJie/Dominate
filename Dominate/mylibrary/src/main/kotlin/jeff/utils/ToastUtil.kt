package jeff.utils

import android.content.Context
import android.widget.Toast
import jeff.bases.DominateApplication.Companion.instance

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：ToastUtil toast弹出工具
 */
object ToastUtil {
    /**
     * 直接传入string字符串
     */
    fun show(context: Context, text: CharSequence) {
            if (text.length < 10) {
                Toast.makeText(context, text, Toast.LENGTH_SHORT)
            } else {
                Toast.makeText(context, text, Toast.LENGTH_LONG)
            }
    }

    /**
     * 可以传入@string
     */
    fun show(context: Context, resId: Int) {
        show(context, context.getString(resId))
    }

    fun show(resId: Int) {
        show(instance.getString(resId))
    }

    fun show(context: Context) {
        show(context, "")//这里可以是指定的值
    }

    fun show(text: CharSequence) {
        show(instance, text)
    }
    operator fun invoke(resId: Int) {
        show(instance, instance.getString(resId))
    }


    operator fun invoke(text: CharSequence) {
        show(instance, text)
    }


}