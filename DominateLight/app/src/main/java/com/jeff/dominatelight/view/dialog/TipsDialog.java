package com.jeff.dominatelight.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.jeff.dominatelight.R;


/**
 * Created by liucr on 2015/12/14.
 */
public class TipsDialog extends Dialog {

    private TextView mTipsText;

    private ImageView mTipsImage;

    private View centerView;

    private TextView centerText1;

    private View line;

    private View buttonLine;

    private Button mLeftButton;

    private Button mRightButton;

    public TipsDialog(Context context) {
        super(context, R.style.noTitleDialogStyle);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_tips, null);
        mTipsText = (TextView) mView.findViewById(R.id.view_dialog_tips_text);
        mTipsImage = (ImageView) mView.findViewById(R.id.view_dialog_tips_image);
        centerView = mView.findViewById(R.id.view_dialog_tips_center);
        centerText1 = (TextView) mView.findViewById(R.id.view_dialog_tips_center_text1);
        line = mView.findViewById(R.id.view_dialog_tips_line);
        buttonLine = mView.findViewById(R.id.view_dialog_tips_button_line);
        mLeftButton = (Button) mView.findViewById(R.id.view_dialog_tips_button_left);
        mRightButton = (Button) mView.findViewById(R.id.view_dialog_tips_button_right);
        super.setContentView(mView);
    }

    public void showDialogWithTips(String tips, String buttonName, View.OnClickListener onClickListener) {
        mLeftButton.setVisibility(View.GONE);
        buttonLine.setVisibility(View.GONE);
        mTipsText.setText(getContext().getResources().getString(R.string.hint));
        centerText1.setText(tips);
        mRightButton.setText(buttonName);
        mRightButton.setOnClickListener(onClickListener);
        this.show();
    }

    public void showDialogWithTips(String title, String tips, String buttonName, View.OnClickListener onClickListener) {
        mLeftButton.setVisibility(View.GONE);
        buttonLine.setVisibility(View.GONE);
        mTipsText.setText(title);
        centerText1.setText(tips);
        mRightButton.setText(buttonName);
        mRightButton.setOnClickListener(onClickListener);
        this.show();
    }

    public void showDialogWithImage(int id, String text1, View.OnClickListener onClickListener) {
        centerView.setVisibility(View.VISIBLE);
        mLeftButton.setVisibility(View.GONE);
        buttonLine.setVisibility(View.GONE);
        mTipsImage.setImageResource(id);
        centerText1.setText(text1);
        mRightButton.setText(R.string.enter);
        mRightButton.setOnClickListener(onClickListener);
        this.show();
    }

    public void showDialogWithTips(String title, String tips, String leftName, View.OnClickListener leftOnClickListener,
                                   String rightName, View.OnClickListener rightOnClickListener) {
        mTipsText.setText(title);
        centerText1.setText(tips);
        mLeftButton.setVisibility(View.VISIBLE);
        buttonLine.setVisibility(View.VISIBLE);
        mLeftButton.setText(leftName);
        mLeftButton.setOnClickListener(leftOnClickListener);
        mRightButton.setText(rightName);
        mRightButton.setOnClickListener(rightOnClickListener);
        this.show();
    }

    public void showDialogWithTips(String tips, String leftName, View.OnClickListener leftOnClickListener,
                                   String rightName, View.OnClickListener rightOnClickListener) {
        showDialogWithTips(getContext().getResources().getString(R.string.hint), tips,
                leftName, leftOnClickListener, rightName, rightOnClickListener);
    }


}
