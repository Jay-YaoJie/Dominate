package cn.xlink.telinkoffical.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.view.togglebutton.zcw.togglebutton.ToggleButton;

/**
 * Created by liucr on 2016/3/31.
 */
public class SlideView extends FrameLayout {

    private final int Duration = 200;

    public static String STYLE_GENERAL = "STYLE_GENERAL";

    public static String STYLE_EDIT = "STYLE_EDIT";

    public static String STYLE_DELETE = "STYLE_DELETE";

    @Bind(R.id.view_slide)
    View view;

    @Bind(R.id.view_center_item)
    View centerItem;

    @Bind(R.id.view_slide_center_text1)
    TextView centetTopText;

    @Bind(R.id.view_slide_center_text2)
    TextView centerBottomText;

    @Bind(R.id.view_slide_bottomtext)
    TextView bottomRightText;

    @Bind(R.id.view_slide_topleft)
    ImageView topLeftImage;

    @Bind(R.id.view_slide_top)
    LinearLayout slideTop;

    @Bind(R.id.view_slide_right)
    ImageView topRightImage;

    @Bind(R.id.view_slide_togglebutton)
    ToggleButton toggleButton;

    int bottomTextWidth;
    int topImageWidth;

    private String curStyle = STYLE_GENERAL;

    private boolean isAnimation = true;

    public SlideView(Context context) {
        super(context);
        init(context);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_slide, this, true);
        ButterKnife.bind(this);

        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        bottomRightText.measure(w, h);
        bottomTextWidth = bottomRightText.getMeasuredWidth();

        topLeftImage.measure(w, h);
        topImageWidth = topLeftImage.getMeasuredWidth();
    }

    @OnClick(R.id.view_slide_bottomtext)
    void ClickBottomText() {

    }

    @OnClick(R.id.view_slide_top)
    void ClickSlideTop() {
        if (curStyle.equals(STYLE_DELETE)) {
            changeEdit(isAnimation);
        }
    }

    @OnClick(R.id.view_slide_topleft)
    void ClickTopLeftImage() {
        if (!curStyle.equals(STYLE_DELETE)) {
            changeDelete(isAnimation);
        }
    }

    public void setStyle(String style) {
        curStyle = style;
        if(style.equals(STYLE_GENERAL)){
            editToGeneral();
        }else if(style.equals(STYLE_EDIT)){
            generalToEdit();
        }
    }

    public void generalToEdit() {
        topLeftImage.setVisibility(VISIBLE);
        topRightImage.setVisibility(VISIBLE);
        toggleButton.setVisibility(GONE);
        ViewCompat.setAlpha(topRightImage,1);
        curStyle = STYLE_EDIT;
    }

    public void editToGeneral() {
        topLeftImage.setVisibility(GONE);
        topRightImage.setVisibility(GONE);
        toggleButton.setVisibility(VISIBLE);
        ViewCompat.setX(slideTop,0);
        curStyle = STYLE_GENERAL;
    }

    /**
     * 改变到一般状态
     *
     * @param isAnimation
     */
    private void changeEdit(boolean isAnimation) {
        topRightImage.setVisibility(VISIBLE);
        topLeftImage.setVisibility(VISIBLE);
        if (!isAnimation) {
            ViewCompat.setX(slideTop, 0);
        } else {
            topRightImage.setVisibility(VISIBLE);
            ViewCompat.animate(topRightImage).alpha(1).setDuration(Duration).start();
            ViewCompat.animate(slideTop).translationX(0).setDuration(Duration).setListener(changeGenerallistener).start();
        }

    }

    /**
     * 显示底部
     *
     * @param isAnimation
     */
    private void changeDelete(boolean isAnimation) {
        if (!isAnimation) {
            ViewCompat.setX(slideTop, -bottomTextWidth);
            topRightImage.setVisibility(INVISIBLE);
            topLeftImage.setVisibility(INVISIBLE);
        } else {
            ViewCompat.animate(topRightImage).alpha(0).setDuration(Duration).start();
            ViewCompat.animate(slideTop).translationX(-bottomTextWidth).setDuration(Duration).setListener(changeEditlistener).start();
        }
    }

    private ViewPropertyAnimatorListener changeEditlistener = new ViewPropertyAnimatorListener() {
        @Override
        public void onAnimationStart(View view) {

        }

        @Override
        public void onAnimationEnd(View view) {
            topRightImage.setVisibility(INVISIBLE);
            topLeftImage.setVisibility(INVISIBLE);
            curStyle = STYLE_DELETE;
        }

        @Override
        public void onAnimationCancel(View view) {

        }
    };

    private ViewPropertyAnimatorListener changeGenerallistener = new ViewPropertyAnimatorListener() {
        @Override
        public void onAnimationStart(View view) {
        }

        @Override
        public void onAnimationEnd(View view) {
            curStyle = STYLE_EDIT;
        }

        @Override
        public void onAnimationCancel(View view) {

        }
    };

    /**
     * 设置中间Text
     * @param top
     * @param bottom
     */
    public void setCenterText(String top, String bottom){
        centetTopText.setText(top);
        centerBottomText.setText(bottom);
    }

    /**
     * 删除按钮监听
     * @param onDeleteLister
     */
    public void setOnDeleteLister(View.OnClickListener onDeleteLister){
        bottomRightText.setOnClickListener(onDeleteLister);
    }

    public void setOnClickRightLister(View.OnClickListener onClickRight){
        topRightImage.setOnClickListener(onClickRight);
    }

    public void setToggle(boolean toggle){
        if(toggle){
            toggleButton.setToggleOn();
        }else {
            toggleButton.setToggleOff();
        }
    }

    public void setonToggleChanged(ToggleButton.OnToggleChanged onToggleChanged){
        toggleButton.setOnToggleChanged(onToggleChanged);
    }

}
