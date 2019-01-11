package com.jeff.dominatelight.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.jeff.dominatelight.http.HttpHelper;
import com.jeff.dominatelight.http.HttpManage;
import com.jeff.dominatelight.utils.UserUtil;
import com.squareup.okhttp.Request;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by MYFLY on 2016/1/22.
 */
public class RefreshTokenService extends Service {
    static Timer timer = null;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (UserUtil.getUser() == null) {
                return;
            }
            HttpManage.getInstance().refreshToken(UserUtil.getUser().getRefreshToken(), new HttpHelper.ResultCallback<Map<String, Object>>() {
                @Override
                public void onError(Request request, Exception e) {
                }

                @Override
                public void onResponse(int code, Map<String, Object> response) {
                    if (code == 200) {
                        String refreshToken = (String) response.get("refresh_token");
                        String accessToken = (String) response.get("access_token");
                        UserUtil.getUser().setAccessToken(accessToken);
                        UserUtil.getUser().setRefreshToken(refreshToken);
                        UserUtil.updataUser();
                    }
                }

            });
            super.handleMessage(msg);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long period = (long) (1 * 60 * 60 * 1000);
        if (null == timer) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, period, period);
        return super.onStartCommand(intent, flags, startId);
    }
}
