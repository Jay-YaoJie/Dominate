package com.jeff.dominate.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jeff.dominate.R;


/**
 * 悬浮框
 * Created by kee on 2018/4/23.
 */

public class ProgressWindow extends FrameLayout {
    private WindowManager mWindowManager;
    private TextView tv_state, tv_progress;
    private float mTouchStartX;
    private float mTouchStartY;
    private WindowManager.LayoutParams mWmParams;

    private long startTime;
    private static final int MSG_PROGRESS = 1;
    private static final int MSG_STATE = 2;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_PROGRESS) {
                if (tv_progress != null)
                    tv_progress.setText((CharSequence) msg.obj);
            } else if (msg.what == MSG_STATE) {
                if (tv_state != null)
                    tv_state.setText((CharSequence) msg.obj);
            }
        }
    };

    public void setParams(WindowManager.LayoutParams params) {
        mWmParams = params;
    }

    public ProgressWindow(@NonNull Context context) {
        this(context, null);
    }

    public ProgressWindow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.view_mesh_ota_state, this);
        //浮动窗口按钮
        tv_state = (TextView) findViewById(R.id.tv_ota_state);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
    }


    public ProgressWindow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int action = event.getAction();
        boolean performClick = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mWmParams);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if ((System.currentTimeMillis() - startTime) > 0.1 * 1000L) {
                    performClick = false;
                } else {
                    performClick = true;
                }
                break;
        }
        //响应点击事件
        if (performClick) {
            performClick();
        }
        return true;
    }


    public void updateProgress(int progress) {
        if (tv_progress != null) {
            handler.obtainMessage(MSG_PROGRESS, getResources().getString(R.string.progress_mesh_ota, progress + "%")).sendToTarget();
//            tv_progress.setText();
        }
    }

    public void updateState(String state) {
        handler.obtainMessage(MSG_STATE, state).sendToTarget();
    }


}
