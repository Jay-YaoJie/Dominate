package com.jeff.dominatelight.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 根据support-v4包的{@link SlidingPaneLayout}控件，增加左右两个布局的变化
 */
public class SlideMenu extends SlidingPaneLayout {
    protected View mMenuPanel;
    protected float mSlideOffset;
    protected boolean isCustom;
    private boolean isMenuOpen;

    public SlideMenu(Context context) {
        this(context, null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        isCustom = true;
        // 调用监听，根据滑动比例，产生滑动动画。
        setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                mSlideOffset = slideOffset;
//                if (mMenuPanel == null) {
//                    final int childCount = getChildCount();
//                    for (int i = 0; i < childCount; i++) {
//                        View child = getChildAt(i);
//                        if (child != panel) {
//                            mMenuPanel = child; // 得到menu的根布局
//                            break;
//                        }
//                    }
//                }
//
//                if (mMenuPanel != null) {
//                    float leftScale = 0.7f + 0.3f * slideOffset;
//                    float rightScale = 1.0f - 0.1f * slideOffset;
//                    // menu布局的动画
//                    ViewCompat.setScaleX(mMenuPanel, leftScale);
//                    ViewCompat.setScaleY(mMenuPanel, leftScale);
//                    ViewCompat.setAlpha(mMenuPanel, 0.6f + 0.4f * slideOffset);
////                    ViewCompat.setTranslationX(mMenuPanel, mMenuPanel.getMeasuredWidth() * (0.3f + slideOffset * 0.7f));
//                    // 主布局的动画
//                    ViewCompat.setPivotX(panel, 0);
//                    ViewCompat.setPivotY(panel, panel.getMeasuredHeight());
//                    ViewCompat.setScaleX(panel, rightScale);
//                    ViewCompat.setScaleY(panel, rightScale);
//                }
            }

            @Override
            public void onPanelOpened(View panel) {
                isMenuOpen = true;
            }

            @Override
            public void onPanelClosed(View panel) {
                isMenuOpen = false;
            }
        });

        // 设置主布局被滑至右边时，布局表面的覆盖色。
        setSliderFadeColor(Color.parseColor("#66666666"));
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (isCustomAble) {
//            dimOnForeground(canvas);
//        }
//    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (isCustom && child == mMenuPanel) {
            dimOnForeground(canvas);
        }
        return result;
    }

    /**
     * 设置menu布局的暗淡程度
     */
    private void dimOnForeground(Canvas canvas) {
        canvas.drawColor(Color.argb((int) (0xff * (0.5f - 0.5f * mSlideOffset)), 0, 0, 0));
    }

    public boolean isMenuOpen() {
        return isMenuOpen;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isMenuOpen){
            return super.onInterceptTouchEvent(ev);
        }else {
            return false;
        }
    }
}
