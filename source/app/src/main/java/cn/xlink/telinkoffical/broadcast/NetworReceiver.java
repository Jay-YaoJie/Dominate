package cn.xlink.telinkoffical.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by liucr on 2016/4/1.
 */
public class NetworReceiver extends BroadcastReceiver {

    private static long lastTime = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        //Toast.makeText(context, intent.getAction(), 1).show();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();  //无网络下为空

        Log.i("liucr", "mobile:" + mobileInfo.isConnected() + "\n" + "wifi:" + wifiInfo.isConnected()
                + "\n" + "active:" + activeInfo);
        if (activeInfo != null) {
            if(System.currentTimeMillis() - lastTime >30000){
                lastTime = System.currentTimeMillis();
            }
        }
    }
}
