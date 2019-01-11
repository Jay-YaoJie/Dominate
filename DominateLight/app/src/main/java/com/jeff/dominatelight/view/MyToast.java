package com.jeff.dominatelight.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.jeff.dominatelight.R;


public class MyToast {

    private LayoutParams mParams;
    private boolean mIsShow;
    private WindowManager mWdm;
    private View mToastView;
    private Activity activity;

    public MyToast(Activity activity, String text) {
        this.activity = activity;
        Toast toast = initMyToast(activity, text);
        mIsShow = false;
        mWdm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        //通过Toast实例获取当前android系统的默认Toast的View布局
        mToastView = toast.getView();
        //设置布局参数
        setParams();
    }

    public static void showTopToast(Activity activity, String text) {
        new MyToast(activity, text).show();
    }

    private Toast initMyToast(Context context, String text) {
        Toast toast = new Toast(context);

        int padding = (int) context.getResources().getDimension(R.dimen.toast_padding);
        int textSize = (int) context.getResources().getDimension(R.dimen.toast_padding);


        TextView textView = new TextView(context);
        textView.setPadding(padding, padding, padding, padding);
        textView.setBackgroundColor(ContextCompat.getColor(context, R.color.toast_bg));
        textView.setTextColor(Color.WHITE);
        textView.setText(text);
//        textView.setTextSize(textSize);
        textView.setGravity(Gravity.CENTER);

        toast.setView(textView);

        return toast;
    }

    private void setParams() {
        mParams = new LayoutParams();
        mParams.height = LayoutParams.WRAP_CONTENT;
        mParams.width = LayoutParams.MATCH_PARENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.windowAnimations = R.style.anim_view;//设置进入退出动画效果
        mParams.type = LayoutParams.TYPE_TOAST;
        mParams.flags = LayoutParams.FLAG_KEEP_SCREEN_ON
                | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_NOT_TOUCHABLE;
        mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
    }

    public void show() {
        if (!mIsShow) {//如果Toast没有显示，则开始加载显示
            if (activity.isFinishing()) {
                return;
            }
            mIsShow = true;
            mWdm.addView(mToastView, mParams);//将其加载到windowManager上

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (activity.isFinishing()) {
                        mIsShow = false;
                        return;
                    }
                    mWdm.removeView(mToastView);
                    mIsShow = false;
                }
            }, 1500);
        }
    }
}
