package jeff.constants

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-20.
 * description ：Settings
 */
object Settings {

    //默认出厂名称
    const val factoryName="telink_mesh1"
    const val factoryPassword="123"
    //这里保存连接，使用的是那一个名字连接
    var masLogin:Boolean=false //true，使用原有的

    //全局属性   var是一个可变变量，val是一个只读变量相当于java中的final变量。
    //不可变的变量
    const val APP_NAME: String = "jeff"

    //是否打印日志
    //var DEBUGMODE = true
//保存根目录文件地址
    var ROOT_DIR: String = "";//在Application初始化中赋值，其他地方直接调用即可

    //CRASH保存的异常文件目录和文件名称
    var CRASH_FILE_PATH: String? = "${ROOT_DIR}/${APP_NAME}/crash/";//保存文件地址，有可能为空
    var CRASH_FILE_NAME: String = ".crash";//保存的异常文件名为.crash
    var CRASH_SAVESD: Boolean = true;//true打打印到机器则为调试，false则保存到本地

    //LOG保存平常打印的信息文件地址和文件名称
    var LOG_FILE_PATH: String? = "${ROOT_DIR}/${APP_NAME}/log/";//保存文件地址，有可能为空
    var LOG_FILE_NAME: String = ".log";//保存的异常文件名为.log
    var LOG_SAVESD: Boolean = false;//是否保存到文件//是否存log到sd卡
    var LOG_DEBUG: Boolean = true;//保存是否开启打印模式//是否打印log

}