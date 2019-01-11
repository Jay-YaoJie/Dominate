package com.jeff.dominatelight.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by liucr on 2016/5/26.
 */
public class ProgressView extends MaterialProgressDrawable {

    private ValueAnimator valueAnimator;

    public ProgressView(Context context, View parent) {
        super(context, parent);
        init();
    }

    private void init(){
        valueAnimator = valueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(600);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float n = (float) animation.getAnimatedValue();
                //圈圈的旋转角度
                setProgressRotation(n * 0.5f);
                //圈圈周长，0f-1F
                setStartEndTrim(0f, n * 1.0f);
                //透明度，0-255
                setAlpha((int) (255 * n));
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
    }

    @Override
    public void start() {
        valueAnimator.start();
        super.start();
    }

    @Override
    public void stop() {
        valueAnimator.end();
        super.stop();
    }
}
