package com.jeff.dominate.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * creation date: 2018-12-27 3:08
 * description ：SharedPreferencesHelper
 *  * 这是一个SharePreference的根据类，使用它可以更方便的数据进行简单存储
 *  * 这里只要知道基本调用方法就可以了
 *  * 1.通过构造方法来传入上下文和文件名
 *  * 2.通过putValue方法传入一个或多个自定义的ContentValue对象，进行数据存储
 *  * 3.通过get方法来获取数据
 *  * 4.通过clear方法来清除这个文件的数据
 *  * 这里没有提供清除单个key的数据，是因为存入相同的数据会自动覆盖，没有必要去理会
 */
public class SharedPreferencesHelper {
    private static final String FILE_NAME = "com.telink.bluetooth.light.SharedPreferences";

    private static final String KEY_MESH_NAME = "com.telink.bluetooth.light.mesh_name";
    private static final String KEY_MESH_PASSWORD = "com.telink.bluetooth.light.mesh_password";

    public static String getMeshName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MESH_NAME, null);
    }

    public static String getMeshPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MESH_PASSWORD, null);
    }

    public static void saveMeshName(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MESH_NAME, name)
                .apply();
    }

    public static void saveMeshPassword(Context context, String pwd) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MESH_PASSWORD, pwd)
                .apply();
    }
}
