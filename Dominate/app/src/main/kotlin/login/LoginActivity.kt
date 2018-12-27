package login

import android.content.Intent
import bases.DominateApplication.Companion.dominate
import bases.MainActivity
import com.jeff.dominate.R
import com.jeff.dominate.model.Mesh
import com.jeff.dominate.model.SharedPreferencesHelper
import com.jeff.dominate.util.FileSystem
import jeff.login.LoginActivity
import jeff.utils.ToastUtil

/**
 * author : Jeff  5899859876@qq.com
 * CSDN ： https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-12-27.
 * description ：LoginActivity 登录页面
 */
class LoginActivity : LoginActivity() {
    override fun login() {
        super.login()

        //设置用户名和密码
        var mesh = FileSystem.readAsObject(this, "$name.$password")

        if (mesh == null) {
            mesh = Mesh()
            mesh.name = name
            mesh.password = password
        }

        (mesh as Mesh).factoryName = "telink_mesh1"
        mesh.factoryPassword = "123"

        if (mesh.saveOrUpdate(this)) {
            dominate.setupMesh(mesh)
            SharedPreferencesHelper.saveMeshName(this, mesh.name)
            SharedPreferencesHelper.saveMeshPassword(this, mesh.password)
            //  this.showToast("Save Mesh Success");
            //登录成功
            ToastUtil(R.string.login_success)
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        } else {
            //登录失败
            ToastUtil(R.string.logon_back)
            logonBack()//清除数据
        }
    }

    /*
    *删除登录数据  Mesh
    *   if (mApplication.getMesh().devices != null) {
                    mApplication.getMesh().devices.clear();
                }
    * */


}