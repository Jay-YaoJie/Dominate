package utils

import android.util.Log
import constants.Settings.LOG_DEBUG
import constants.Settings.LOG_FILE_NAME
import constants.Settings.LOG_FILE_PATH
import constants.Settings.LOG_SAVESD
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-11-17.
 * description ：日志工具类
 */

object  LogUtils {
    private val execu: ExecutorService = Executors.newFixedThreadPool(1)
    private val tag:String="com.jeff.kotlindialog.utils.LogUtils"

    fun v(tag: String, msg: String) = LOG_DEBUG.debugLog(tag, "${tag}==---==${msg}", Log.VERBOSE)
    fun d(tag: String, msg: String) =LOG_DEBUG.debugLog(tag, "${tag}==---==${msg}", Log.DEBUG)
    fun i(tag: String, msg: String) = LOG_DEBUG.debugLog(tag, "${tag}==---==${msg}", Log.INFO)
    fun w(tag: String, msg: String) = LOG_DEBUG.debugLog(tag, "${tag}==---==${msg}", Log.WARN)
    fun e(tag: String, msg: String) = LOG_DEBUG.debugLog(tag, "${tag}==---==${msg}", Log.ERROR)
    //打印或保存到日志
    private fun Boolean.debugLog(tag: String, msg: String, type: Int) {
        LOG_SAVESD.saveToSd("${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}\n${tag}", msg)
        if (!this) {
            return
        }
        when (type) {
            Log.VERBOSE -> Log.v(tag, tag + msg)
            Log.DEBUG -> Log.d(tag, msg)
            Log.INFO -> Log.i(tag, msg)
            Log.WARN -> Log.w(tag, msg)
            Log.ERROR -> Log.e(tag, msg)
        }

    }

    //保存日志到本地文件
    private fun Boolean.saveToSd(tag: String, msg: String) {
        if (!this) {
            return
        }
        execu.submit({
            //获得当前时间
            val current = System.currentTimeMillis();
            val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(current));

            //保存日志信息
            FileUtils.appendText(File(LOG_FILE_PATH + time + LOG_FILE_NAME), "\r\n$tag\n$msg")
        })

    }

    //上传到服务器上
    private fun uploadLogToServer() {

    }


}
