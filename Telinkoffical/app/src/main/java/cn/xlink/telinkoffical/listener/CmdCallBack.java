package cn.xlink.telinkoffical.listener;

/**
 * Created by liucr on 2016/5/25.
 */
public interface CmdCallBack {

    void onResponse(boolean isSuccess, Object o);

    void onTimeOut();

}
