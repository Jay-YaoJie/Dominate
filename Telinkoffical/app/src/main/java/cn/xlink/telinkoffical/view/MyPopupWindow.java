package cn.xlink.telinkoffical.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.adapter.PopWindowAdapter;

/**
 * Created by liucr on 2016/1/2.
 */
public class MyPopupWindow extends PopupWindow {

    private Context mContext;
    private WindowManager mWindowManager;

    private View view;

    private LinearLayout viewLine;

    private ListView listView;

    private View item;

    private View bg;

    private View itemsView;

    private TextView cancelButton;

    private View topView;

    private View bottomView;

    private PopWindowAdapter adapter;

    private PopupWindow popupWindow;

    private boolean isShowIcon = false;

    private int style = 0;

    public MyPopupWindow(Activity activity, List<String> strings, int style, PopWindowAdapter.OnItemClickListener listener) {
        this.style = style;
        init(activity, strings, listener);
    }

    public MyPopupWindow(Activity activity, List<String> strings, int style, boolean isShowIcon, PopWindowAdapter.OnItemClickListener listener) {
        this.style = style;
        this.isShowIcon = isShowIcon;
        init(activity, strings, listener);
    }

    public MyPopupWindow(Activity activity, List<String> strings, PopWindowAdapter.OnItemClickListener listener) {
        init(activity, strings, listener);
    }

    private void init(Activity activity, List<String> strings, PopWindowAdapter.OnItemClickListener listener) {
        initView(activity, strings, listener);

        if (view != null) {
            mContext = view.getContext();
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }

        setContentView(view);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setFocusable(false);

        popupWindow = this;
    }

    private void initView(Activity activity, List<String> strings, PopWindowAdapter.OnItemClickListener listener) {

        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_popwindow, null);
        itemsView = view.findViewById(R.id.popwindow_items);
        viewLine = (LinearLayout) view.findViewById(R.id.view_popwindow_list_bg);
        listView = (ListView) view.findViewById(R.id.view_popwindow_list);
        topView = view.findViewById(R.id.view_popwindow_topview);
        bottomView = view.findViewById(R.id.view_popwindow_bottomview);
        cancelButton = (TextView) view.findViewById(R.id.view_popwindow_item_text);
        cancelButton.setText(view.getContext().getString(R.string.cancel));
        item = view.findViewById(R.id.view_popwindow_item_bg);

        bg = view.findViewById(R.id.view_popwindow_bg);
        adapter = new PopWindowAdapter(activity);
        adapter.setIsShowIcon(isShowIcon);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPopupWindow.this.myDismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPopupWindow.this.myDismiss();
            }
        });

        adapter.setData(strings);
        listView.setAdapter(adapter);
        adapter.setOnItemClickListener(listener);

        if (style == 1) {
//            viewLine.setPadding(100, 0, 100, 0);
            listView.setPadding(0, 0, 0, 0);
            adapter.setStyle(1);
            topView.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            item.setVisibility(View.GONE);
        } else {
            bottomView.setVisibility(View.GONE);
            int padding = (int) activity.getResources().getDimension(R.dimen.popupwind_bg_padding);
            viewLine.setPadding(padding, padding, padding, padding);
            listView.setBackgroundResource(R.drawable.white_rect_bg_big);
            item.setBackgroundResource(R.drawable.white_rect_bg_big);
        }
    }

    public void showAtLocation(final View parent, final int gravity, final int x, final int y) {
        Animation listShow;
        if (style == 1) {
            listShow = getTranslateAnimation(-1.0f, 0.0f);
        } else {
            listShow = getTranslateAnimation(1.0f, 0.0f);
        }

        viewLine.startAnimation(listShow);
        bg.startAnimation(getAlphaAnimation(0.0f, 1.0f));
        super.showAtLocation(parent, gravity, x, y);
    }

    public void showAsDropDown(final View parent, final int gravity, final int x, final int y) {
        Animation listShow;
        if (style == 1) {
            listShow = getTranslateAnimation(-1.0f, 0.0f);
        } else {
            listShow = getTranslateAnimation(1.0f, 0.0f);
        }

        viewLine.startAnimation(listShow);
        bg.startAnimation(getAlphaAnimation(0.0f, 1.0f));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            super.showAsDropDown(parent, x, y, gravity);
        } else {
            super.showAsDropDown(parent, x, y);
        }
    }

    public void myDismiss() {
        final Animation listHide;

        if (style == 1) {
            listHide = getTranslateAnimation(0.0f, -1.0f);
        } else {
            listHide = getTranslateAnimation(0.0f, 1.0f);
        }

        listHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MyPopupWindow.this.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        viewLine.startAnimation(listHide);
        bg.startAnimation(getAlphaAnimation(1.0f, 0.0f));
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public Animation getTranslateAnimation(float startY, float endY) {
        TranslateAnimation animation = new
                TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, startY,
                Animation.RELATIVE_TO_SELF, endY);
        animation.setDuration(300);
        return animation;
    }

    /**
     * 透明效果
     *
     * @return
     */
    public Animation getAlphaAnimation(float start, float end) {
        Animation animation = new AlphaAnimation(start, end);
        animation.setDuration(300);
        return animation;
    }

}
