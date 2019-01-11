package com.jeff.dominatelight.view.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.view.MaterialProgressDrawable;
import com.jeff.dominatelight.view.ProgressView;


public class WaitingDialog extends Dialog {

    private TextView tipsView;

    private ProgressView mProgress;

    private ImageView imageView;

    public WaitingDialog(Context context) {
        super(context, R.style.WaitingDialogStyle);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_waiting, null);
        imageView = (ImageView) mView.findViewById(R.id.progress_view);

        mProgress = new ProgressView(getContext(),imageView);

        mProgress.setBackgroundColor(Color.WHITE);
        //圈圈颜色,可以是多种颜色
        mProgress.setColorSchemeColors(getContext().getResources().getColor(R.color.act_fgt_bg));
        //设置圈圈的各种大小
        mProgress.updateSizes(MaterialProgressDrawable.LARGE);
        imageView.setImageDrawable(mProgress);


        tipsView = (TextView) mView.findViewById(R.id.waiting_tips);
        super.setContentView(mView);
    }

    public void setWaitingText(String waitingText) {
        if (!TextUtils.isEmpty(waitingText)) {
            tipsView.setText(waitingText);
        }
    }

    @Override
    public void show() {
        super.show();
        mProgress.start();
    }

    @Override
    public void hide() {
        super.hide();
        mProgress.stop();
    }
}
