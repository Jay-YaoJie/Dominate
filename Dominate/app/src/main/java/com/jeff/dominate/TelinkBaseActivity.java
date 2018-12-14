package com.jeff.dominate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-11-17.
 * description ：Activity
 */
public class TelinkBaseActivity extends Activity {

    protected Toast toast;
    protected boolean foreground = false;

    @Override
    @SuppressLint("ShowToast")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        foreground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        foreground = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        foreground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.toast.cancel();
        this.toast = null;
    }

    public void showToast(CharSequence s) {

        if (this.toast != null) {
            this.toast.setView(this.toast.getView());
            this.toast.setDuration(Toast.LENGTH_SHORT);
            this.toast.setText(s);
            this.toast.show();
        }
    }

    protected void saveLog(String log) {
        ((TelinkLightApplication) getApplication()).saveLog(log);
    }
}
