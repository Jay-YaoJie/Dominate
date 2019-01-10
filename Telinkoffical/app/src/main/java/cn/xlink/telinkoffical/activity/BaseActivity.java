package cn.xlink.telinkoffical.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.telink.util.EventListener;

import java.util.List;

import cn.xlink.telinkoffical.MyApp;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.view.dialog.TipsDialog;
import cn.xlink.telinkoffical.view.dialog.WaitingDialog;

/**
 * Created by liucr on 2016/3/24.
 */
public abstract class BaseActivity extends AppCompatActivity {

    final String TAG = getClass().getSimpleName();

    public Fragment currentViewFr;
    private FragmentManager fargmentManager;

    protected TipsDialog tipsDialog;

    protected WaitingDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tipsDialog = new TipsDialog(this);
        waitingDialog = new WaitingDialog(this);
        fargmentManager = getSupportFragmentManager();
        MyApp.getApp().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.getApp().setCurrentActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            if (fm.getFragments() == null || index < 0 || index >= fm.getFragments().size()) {
                Log.w(TAG, "Activity result fragment index out of range: 0x" + Integer.toHexString(requestCode));
                return;
            }
            Fragment frag = fm.getFragments().get(index);
            if (frag == null) {
                Log.w(TAG, "Activity result no fragment exists for index: 0x" + Integer.toHexString(requestCode));
            } else {
                handleResult(frag, requestCode, resultCode, data);
            }
            return;
        }
    }

    protected abstract void initData();

    protected abstract void initView();

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleResult(Fragment frag, int requestCode, int resultCode, Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.getApp().removeActivity(this);
    }

    @Override
    public void finish() {
        tipsDialog = null;
        waitingDialog = null;
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                back();
            } else if (event.getAction() == KeyEvent.KEYCODE_MENU) {

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void back() {
        finish();
    }

    public void replaceViewFragment(Fragment viewFr, String tag, int id) {
        if (viewFr == currentViewFr) {
            return;
        }
        fargmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fargmentManager.beginTransaction().replace(id, viewFr).commitAllowingStateLoss();
        currentViewFr = viewFr;
    }

    public void removeFragment(Fragment fragment) {
        FragmentTransaction mFragmentTransaction;
        mFragmentTransaction = fargmentManager.beginTransaction();
        mFragmentTransaction.remove(fragment);
        mFragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 跳转界面
     *
     * @param paramClass
     */
    public void openActivity(Class<?> paramClass) {
        Log.e(getClass().getSimpleName(),
                "openActivity：：" + paramClass.getSimpleName());
        openActivity(paramClass, null);
    }

    public void openActivity(Class<?> paramClass, Bundle paramBundle) {
        Intent localIntent = new Intent(this, paramClass);
        if (paramBundle != null)
            localIntent.putExtras(paramBundle);
        startActivity(localIntent);
    }

    /**
     * 文字提示加确定按钮
     */
    public void showOneButtonDialog(String tips, boolean isCancelable, String buttonText, View.OnClickListener onClickListener) {
        if (isDestroyed()) {
            return;
        }
        if (tipsDialog == null) {
            tipsDialog = new TipsDialog(this);
        }
        tipsDialog.setCancelable(isCancelable);
        tipsDialog.setCanceledOnTouchOutside(isCancelable);
        if (tipsDialog == null) {
            tipsDialog = new TipsDialog(this);
        }
        tipsDialog.showDialogWithTips(tips, buttonText, onClickListener);
    }

    /**
     * 文字提示加确定按钮
     */
    public void showTipsDialog(String tips, String buttonText) {
        if (isDestroyed()) {
            return;
        }
        if (tipsDialog == null) {
            tipsDialog = new TipsDialog(this);
        }

        tipsDialog.showDialogWithTips(tips, buttonText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.hide();
            }
        });
    }

    /**
     * 两个按钮(一个为取消)
     */
    public void showTipsDialog(String tips, String buttonText, View.OnClickListener onClickListener) {
        if (isDestroyed()) {
            return;
        }
        if (tipsDialog == null) {
            tipsDialog = new TipsDialog(this);
        }
        tipsDialog.showDialogWithTips(tips, getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.hide();
            }
        }, buttonText, onClickListener);
    }

    /**
     * 隐藏
     */
    public void hideDialog() {
        if (tipsDialog != null && tipsDialog.isShowing()) {
            tipsDialog.dismiss();
        }
    }

    /**
     * 显示Loading
     */
    public void showWaitingDialog(String text) {
        if (waitingDialog == null) {
            waitingDialog = new WaitingDialog(this);
        }
        if (waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
        waitingDialog.setCancelable(false);
        waitingDialog.setWaitingText(text);
        waitingDialog.show();
    }

    /**
     * 隐藏Loading
     */
    public void hideWaitingDialog() {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    public void hideKey(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
        }
    }

}
