package jeff.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jeff.beans.FragmentAdapterBeans.deviceBean


/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-27.
 * description ：SPUtils
 *  这是一个SharePreference的根据类，使用它可以更方便的数据进行简单存储
 * 这里只要知道基本调用方法就可以了
 * 1.通过构造方法来传入上下文和文件名
 * 2.通过putValue方法传入一个或多个自定义的ContentValue对象，进行数据存储
 * 3.通过get方法来获取数据
 * 4.通过clear方法来清除这个文件的数据
 * 这里没有提供清除单个key的数据，是因为存入相同的数据会自动覆盖，没有必要去理会
 *
 *  https://blog.csdn.net/wenzhi20102321/article/details/77772002
 */
class SPUtils {

    //定义一个SharePreference对象
    var sharedPreferences: SharedPreferences
    //定义一个上下文对象

    //创建SharePreference对象时要上下文和存储的模式
    //通过构造方法传入一个上下文
    constructor(context: Context, fileName: String) {
        //实例化SharePreference对象，使用的是get方法，而不是new创建
        //第一个参数是文件的名字
        //第二个参数是存储的模式，一般都是使用私有方式：Context.MODE_PRIVATE
        sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    }


    /**
     * 存储数据
     * 这里要对存储的数据进行判断在存储
     * 只能存储简单的几种数据
     * 这里使用的是自定义的ContentValue类，来进行对多个数据的处理
     */
    //创建一个内部类使用，里面有key和value这两个值
    class ContentValue//通过构造方法来传入key和value
    (internal var key: String, internal var value: Any)

    //一次可以传入多个ContentValue对象的值
    fun putValues(vararg contentValues: ContentValue) {
        //获取SharePreference对象的编辑对象，才能进行数据的存储
        val editor = sharedPreferences.edit()
        //数据分类和存储
        for (contentValue in contentValues) {
            //如果是字符型类型
            if (contentValue.value is String) {
                editor.putString(contentValue.key, contentValue.value.toString()).commit()
            }
            //如果是int类型
            if (contentValue.value is Int) {
                editor.putInt(contentValue.key, Integer.parseInt(contentValue.value.toString())).commit()
            }
            //如果是Long类型
            if (contentValue.value is Long) {
                editor.putLong(contentValue.key, java.lang.Long.parseLong(contentValue.value.toString())).commit()
            }
            //如果是布尔类型
            if (contentValue.value is Boolean) {
                editor.putBoolean(contentValue.key, java.lang.Boolean.parseBoolean(contentValue.value.toString())).commit()
            }

        }
    }

    //获取数据的方法
    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getBoolean(key: String, b: Boolean?): Boolean {
        return sharedPreferences.getBoolean(key, b!!)
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, -1)
    }

    fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, -1)
    }

    //清除当前文件的所有的数据
    fun clear() {
        sharedPreferences.edit().clear().commit()
    }

    //使用静态方法去调用这里的对象
    companion object {
        val tag: String = "SPUtils"
        /****保存/获取 用户数据对象，登录 时的对象********************************************/
        /******保存****************************/
        /**
         * 保存按钮的状态值
         */
        fun setLoadLogin(context: Context, name: String, password: String, isSavePassword: Boolean, isAutomaticLogin: Boolean) {

            //获取SharedPreferences对象，使用自定义类的方法来获取对象
            val helper = SPUtils(context, "Login")

            //如果设置自动登录
            if (isAutomaticLogin) {
                //创建记住密码和自动登录是都选择,保存密码数据
                helper.putValues(
                        SPUtils.ContentValue("remenberPassword", true),
                        SPUtils.ContentValue("autoLogin", true),
                        SPUtils.ContentValue("name", name),
                        SPUtils.ContentValue("password", password))

            } else if (!isSavePassword) { //如果没有保存密码，那么自动登录也是不选的
                //创建记住密码和自动登录是默认不选,密码为空
                helper.putValues(
                        SPUtils.ContentValue("remenberPassword", false),
                        SPUtils.ContentValue("autoLogin", false),
                        SPUtils.ContentValue("name", name),
                        SPUtils.ContentValue("password", ""))
            } else if (isSavePassword) {   //如果保存密码，没有自动登录
                //创建记住密码为选中和自动登录是默认不选,保存密码数据
                helper.putValues(
                        SPUtils.ContentValue("remenberPassword", true),
                        SPUtils.ContentValue("autoLogin", false),
                        SPUtils.ContentValue("name", name),
                        SPUtils.ContentValue("password", password))
            }
        }
        /******获取****************************/
        /**
         * 获得保存在本地的用户名
         */
        fun getLocalName(context: Context): String {
            //获取SharedPreferences对象，使用自定义类的方法来获取对象
            val helper = SPUtils(context, "Login")
            val name = helper.getString("name");
            if (name == null) {
                return "";
            } else {
                return name
            }

        }

        /**
         * 获得保存在本地的密码
         */
        fun getLocalPassword(context: Context): String {
            //获取SharedPreferences对象，使用自定义类的方法来获取对象
            val helper = SPUtils(context, "Login")
            val password = helper.getString("password")
            // return Base64Utils.decryptBASE64(password)   //解码一下
            if (password == null) {
                return "";
            } else {
                return password
            }

        }

        /**
         * 判断是否记住密码
         */
        fun getRemenberPassword(context: Context): Boolean {
            //获取SharedPreferences对象，使用自定义类的方法来获取对象
            val helper = SPUtils(context, "Login")
            return helper.getBoolean("remenberPassword", false)
        }

        /**
         * 判断是否自动登录
         */
        fun getAutoLogin(context: Context): Boolean {
            //获取SharedPreferences对象，使用自定义类的方法来获取对象
            val helper = SPUtils(context, "Login")
            return helper.getBoolean("autoLogin", false)
        }

        //清除当前登录用户文件的所有的数据
        fun loginClear(context: Context) {
            SPUtils(context, "Login").clear()
        }


        /*********FragmentAdapterBeans  页面列表对象*************************************************/
        /**
         * 获得列表对象
         */
        fun setFragmentAdapterBeans(context: Context, listBeanName: String, list: ArrayList<deviceBean>) {
            //获取SharedPreferences对象，使用自定义类的方法来获取对象
            val helper = SPUtils(context, "FragmentAdapterBeans")
            val strJson = (Gson().toJson(list))
            helper.putValues(SPUtils.ContentValue(listBeanName, strJson))
        }

        /**
         * 获得列表对象
         */
        fun getFragmentAdapterBeans(context: Context, listBeanName: String): ArrayList<deviceBean>? {
            //获取SharedPreferences对象，使用自定义类的方法来获取对象
            val helper = SPUtils(context, "FragmentAdapterBeans")
            val strJson = helper.getString(listBeanName)
            if (!listBeanName.isNullOrEmpty()) {
                //使用TypeToken进行转化
                val type = object : TypeToken<List<deviceBean>>() {}.type
                return (Gson().fromJson(strJson, type))
            } else {
                return null;
            }

        }

        //清除当前登录用户文件的所有的数据
        fun FragmentAdapterBeansClear(context: Context) {
            SPUtils(context, "Login").clear()
        }

    }


}