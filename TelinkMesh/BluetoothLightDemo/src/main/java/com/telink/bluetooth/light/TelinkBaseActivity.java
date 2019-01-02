package com.telink.bluetooth.light;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class TelinkBaseActivity extends Activity {

    protected Toast toast;
    protected boolean foreground = false;

    @Override
    @SuppressLint("ShowToast")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        foreground = true;
        Log.d("TelinkBaseActivity","");
    }

    @Override
    protected void onPause() {
        super.onPause();
        foreground = false;
        Log.d("TelinkBaseActivity","");
    }


    @Override
    protected void onResume() {
        super.onResume();
        foreground = true;
        Log.d("TelinkBaseActivity","");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TelinkBaseActivity","");
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
