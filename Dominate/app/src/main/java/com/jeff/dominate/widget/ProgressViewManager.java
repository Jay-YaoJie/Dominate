package com.jeff.dominate.widget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import com.telink.bluetooth.TelinkLog;
/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-11-17.
 * description ：
 */

public class ProgressViewManager implements View.OnClickListener {

    private static ProgressViewManager instance;

    private ProgressWindow container;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private boolean mHasShown;

    private ProgressViewManager() {
    }


    public static ProgressViewManager getInstance() {
        if (instance == null)
            instance = new ProgressViewManager();
        return instance;
    }

    public void createFloatWindow(Context context) {
        wmParams = new WindowManager.LayoutParams();
        WindowManager windowManager = getWindowManager(context);
        container = new ProgressWindow(context);

        if (Build.VERSION.SDK_INT >= 24) { /*android7.0不能用TYPE_TOAST*/
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else { /*以下代码块使得android6.0之后的用户不必再去手动开启悬浮窗权限*/
            String packName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packName));
            if (permission) {
                wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.START | Gravity.TOP;

        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        //窗口高度
        int screenHeight = dm.heightPixels / 2;
        //以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = screenWidth;
        wmParams.y = screenHeight;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        container.setParams(wmParams);
        windowManager.addView(container, wmParams);
        mHasShown = true;
        //是否展示小红点展示
    }


    /**
     * 返回当前已创建的WindowManager。
     */
    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }


    /**
     * 移除悬浮窗
     */
    public void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = container.isAttachedToWindow();
        }
        if (mHasShown && isAttach && mWindowManager != null)
            mWindowManager.removeView(container);
    }


    public void updateState(String state) {
        if (container != null) {
            container.updateState(state);
        }
    }


    public void updateProgress(int progress) {
        if (container != null) {
            container.updateProgress(progress);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == container) {
            TelinkLog.e("click");
        }
    }
}
