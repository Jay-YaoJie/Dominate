package com.jeff.dominate.util;

import android.app.Fragment;
import com.jeff.dominate.R;
import com.jeff.dominate.fragments.DeviceListFragment;
import com.jeff.dominate.fragments.GroupListFragment;
import com.jeff.dominate.fragments.MainFragment;
import com.jeff.dominate.fragments.MeFragment;

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-11-17.
 * description ：
 */
public abstract class FragmentFactory {

    public static Fragment createFragment(int id) {

        Fragment fragment = null;

        if (id == R.id.tab_devices) {
            fragment = new DeviceListFragment();
        } else if (id == R.id.tab_groups) {
            fragment = new GroupListFragment();
        } else if (id == R.id.tab_me) {
            fragment = new MeFragment();
        } else if (id == R.id.tab_main) {
            fragment = new MainFragment();
        }

        return fragment;
    }
}
