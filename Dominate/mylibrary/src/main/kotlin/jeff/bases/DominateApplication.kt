package jeff.bases

import android.app.Application
import com.kongzue.dialog.v2.DialogSettings


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：DominateApplication
 */
class DominateApplication {
    companion object {
        lateinit var instance:Application;
        //初始化全局的风格样式 dialog
        fun dialogSetting(){
            //iOS 风格对应 DialogSettings.STYLE_IOS
            DialogSettings.style = DialogSettings.STYLE_IOS
            //设置提示框主题为亮色主题
            DialogSettings.tip_theme = DialogSettings.THEME_LIGHT;
        }
    }

}