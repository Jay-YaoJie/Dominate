package cn.xlink.telinkoffical.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telink.util.Event;
import com.telink.util.EventListener;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment implements EventListener<String> {
    private boolean isRun = false;
    protected boolean isInit = false;

    public void Log(String msg) {
//        MyLog.e(this.getClass().getSimpleName(), msg);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        initData();
        if (!isInit) {
            initView(view);
        }
        isInit = true;
    }

    protected abstract void initView(View view);

    protected abstract void initData();

    public boolean isInit() {
        return isInit;
    }

    public boolean back() {
        return false;
    }


    /**
     * 跳转界面
     *
     * @param paramClass
     */
    protected void openActivity(Class<?> paramClass) {
        Log.e(getClass().getSimpleName(),
                "openActivity：：" + paramClass.getSimpleName());
        openActivity(paramClass, null);
    }


    protected void openActivity(Class<?> paramClass, Bundle paramBundle) {
        Intent localIntent = new Intent(getActivity(), paramClass);
        if (paramBundle != null)
            localIntent.putExtras(paramBundle);
        startActivity(localIntent);
    }

    protected void openActivityForResult(Class<?> paramClass, Bundle paramBundle, int requestCode) {
        Intent localIntent = new Intent(getActivity(), paramClass);
        if (paramBundle != null)
            localIntent.putExtras(paramBundle);
        startActivityForResult(localIntent, requestCode);
    }

    protected void openActivityForResult(Class<?> paramClass, int requestCode) {
        Log.e(getClass().getSimpleName(),
                "openActivity：：" + paramClass.getSimpleName());
        openActivityForResult(paramClass, null, requestCode);
    }


    protected void openActivity(String paramString) {
        openActivity(paramString, null);
    }

    protected void openActivity(String paramString, Bundle paramBundle) {
        Intent localIntent = new Intent(paramString);
        if (paramBundle != null)
            localIntent.putExtras(paramBundle);
        startActivity(localIntent);
    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log("onPause");
        isRun = false;
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        Field childFragmentManager;
        try {
            childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void doneClick(View v) {

    }

    public void deviceUpdateView() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log("onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    /**
     * 得到根Fragment
     *
     * @return
     */
    public Fragment getRootFragment() {
        Fragment fragment = getParentFragment();
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;

    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        Log("onDestroyView");

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log("onDestroy");
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        isRun = true;
        changeSkin();
        Log("onResume");
    }

    public final boolean isRun() {
        return isRun;
    }

    protected  boolean isChangeSkin = true;
    public void changeSkin(){
        if(!isChangeSkin){
            return;
        }
        isChangeSkin = false;
    }


    @Override
    public void performed(Event<String> event) {

    }
}
