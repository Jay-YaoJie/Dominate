package jeff.utils

import android.Manifest
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil
import jeff.bases.DominateApplication.Companion.instance

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-24.
 * description ：PermissionUtils 添加权限检查工具
 */
object PermissionUtils {
    private var isChecked: Boolean = false;
    //添加权限检查 检查蓝牙权限，是否打开
    fun checkPermission() {
        if (!isChecked) {
            PermissionsUtil.requestPermission(instance, object : PermissionListener {
                override fun permissionGranted(permissions: Array<String>) {
                    isChecked = true;
                    //Toast.makeText(MainActivity.this, "访问蓝牙", Toast.LENGTH_LONG).show();
                }

                override fun permissionDenied(permissions: Array<String>) {
                    ToastUtil.show("用户拒绝了访问蓝牙")
                    isChecked = false;
                }
            }, Manifest.permission.BLUETOOTH)

            PermissionsUtil.requestPermission(instance, object : PermissionListener {
                override fun permissionGranted(permissions: Array<String>) {
                    isChecked = true;
                    // Toast.makeText(MainActivity.this, "访问位置", Toast.LENGTH_LONG).show();
                }

                override fun permissionDenied(permissions: Array<String>) {
                    ToastUtil.show("用户拒绝了访问位置")
                    isChecked = false;
                }
            }, Manifest.permission.ACCESS_FINE_LOCATION)

            PermissionsUtil.requestPermission(instance, object : PermissionListener {
                override fun permissionGranted(permissions: Array<String>) {
                    isChecked = true;
                    //Toast.makeText(MainActivity.this, "访问位置", Toast.LENGTH_LONG).show();
                }

                override fun permissionDenied(permissions: Array<String>) {
                    // Toast.makeText(MainActivity.this, "用户拒绝了访问位置", Toast.LENGTH_LONG).show();
                    isChecked = false;
                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION)
            PermissionsUtil.requestPermission(instance, object : PermissionListener {
                override fun permissionGranted(permissions: Array<String>) {
                    isChecked = true;
                    //Toast.makeText(MainActivity.this, "访问网络", Toast.LENGTH_LONG).show();
                }

                override fun permissionDenied(permissions: Array<String>) {
                    // Toast.makeText(MainActivity.this, "用户拒绝了访问网络", Toast.LENGTH_LONG).show();
                    isChecked = false;
                }
            }, Manifest.permission.INTERNET)
        }
    }

}