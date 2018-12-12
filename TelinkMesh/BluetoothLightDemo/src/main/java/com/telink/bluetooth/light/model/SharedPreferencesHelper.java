package com.telink.bluetooth.light.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.telink.util.MeshUtils;

/**
 * Created by kee on 2017/12/22.
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
