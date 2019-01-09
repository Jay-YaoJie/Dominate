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
        lateinit var instance: Application;
        //初始化全局的风格样式 dialog
        fun dialogSetting() {
            
            //是否打印日志
            // public static boolean DEBUGMODE = true;
            DialogSettings.DEBUGMODE = true

            //决定等待框、提示框以及iOS风格的对话框是否启用模糊背景
            //public static boolean use_blur = true;

            //决定等待框、提示框以及iOS风格的对话框的模糊背景透明度（50-255）
            // public static int blur_alpha = 200;

            //决定对话框的默认样式，请使用 TYPE_MATERIAL、TYPE_KONGZUE、TYPE_IOS 赋值
            //  public static int type = 0;
            DialogSettings.style = DialogSettings.STYLE_IOS

            //决定对话框的模式（亮色和暗色两种），请使用 THEME_LIGHT、THEME_DARK 赋值
            //public static int dialog_theme = 0;
            DialogSettings.dialog_theme = DialogSettings.THEME_LIGHT

            //决定对话框的默认背景色
            // public static int dialog_background_color = -1;

            //决定提示框的模式（亮色和暗色两种），请使用 THEME_LIGHT、THEME_DARK 赋值
            //public static int tip_theme = 1;
            DialogSettings.tip_theme = DialogSettings.THEME_LIGHT
        }
    }

}