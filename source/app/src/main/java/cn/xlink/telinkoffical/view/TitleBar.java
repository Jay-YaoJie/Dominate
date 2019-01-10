package cn.xlink.telinkoffical.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xlink.telinkoffical.R;

/**
 * Created by liucr on 2016/3/9.
 */
public class TitleBar extends FrameLayout {

    @Bind(R.id.title_bar)
    LinearLayout titleBar;

    @Bind(R.id.title_left)
    View leftItem;

    @Bind(R.id.title_center)
    View centerView;

    @Bind(R.id.title_center_text)
    TextView centerText;

    @Bind(R.id.title_center_image)
    ImageView centerImage;

    @Bind(R.id.title_left_text)
    TextView leftText;

    @Bind(R.id.title_left_image)
    ImageView leftImage;

    @Bind(R.id.title_right)
    FrameLayout rightItem;

    @Bind(R.id.title_right_text)
    TextView rightText;

    @Bind(R.id.title_right_image)
    ImageView rightImage;

    @Bind(R.id.view_title_bottom)
    View bottomView;

    @Bind(R.id.view_title_bottom_frame)
    View frameView;

    @Bind(R.id.view_title_bottom_iamge)
    ImageView bottomImage;

    @Bind(R.id.view_title_bottom_progress)
    ProgressBar progressBar;

    private Context context;

    public TitleBar(Context context) {
        super(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_title, this, true);
        ButterKnife.bind(view);
        titleBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setBottomVisibility(int visibility) {
        bottomView.setVisibility(GONE);
        setProgressAni(false);
    }

    public void setProgressAni(boolean b) {
        if(b){
            progressBar.setVisibility(VISIBLE);
            bottomImage.setVisibility(GONE);
        }else {
            progressBar.setVisibility(GONE);
            bottomImage.setVisibility(VISIBLE);
        }

    }

    /**
     * 设置标题栏颜色
     *
     * @param color
     */
    public void setTitleBgColor(int color) {
        titleBar.setBackgroundColor(color);
    }

    /**
     * 设置背景图
     *
     * @param id
     */
    public void setTitleResource(int id) {
        titleBar.setBackgroundResource(id);
    }

    /**
     * 获取标题栏
     *
     * @return
     */
    public LinearLayout getTitleBar() {
        return titleBar;
    }

    public ImageView getLeftImage() {
        return leftImage;
    }

    public ImageView getRightImage() {
        return rightImage;
    }

    public TextView getCenterText() {
        return centerText;
    }

    public TextView getLeftText() {
        return leftText;
    }

    public TextView getRightText() {
        return rightText;
    }

    public View getLeftItem() {
        return leftItem;
    }

    public View getRightItem() {
        return rightItem;
    }

    public void setCenterRightDrawable(int visibility) {
        centerImage.setVisibility(visibility);
    }

    /**
     * 左边点击事件
     *
     * @param onClickListener
     */
    public void setOnClickLeftListener(OnClickListener onClickListener) {
        leftItem.setOnClickListener(onClickListener);
    }

    public void setOnClickRightListener(OnClickListener onClickListener) {
        rightItem.setOnClickListener(onClickListener);
    }

    public void setOnClickCenterLister(OnClickListener onClickListener) {
        centerView.setOnClickListener(onClickListener);
    }

    public void setOnClickLeftBottomImageListener(OnClickListener onClickListener) {
        frameView.setOnClickListener(onClickListener);
    }

}
