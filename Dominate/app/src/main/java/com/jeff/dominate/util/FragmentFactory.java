package com.jeff.dominate.util;

import android.app.Fragment;

import com.jeff.dominate.R;
import com.jeff.dominate.fragments.DeviceListFragment;
import com.jeff.dominate.fragments.GroupListFragment;
import com.jeff.dominate.fragments.MainTestFragment;



public abstract class FragmentFactory {

    public static Fragment createFragment(int id) {

        Fragment fragment = null;

        if (id == R.id.tab_devices) {
            fragment = new DeviceListFragment();
        } else if (id == R.id.tab_groups) {
            fragment = new GroupListFragment();
        } else if (id == R.id.tab_account) {
            // todo me fragment
        } else if (id == R.id.tab_test) {
            fragment = new MainTestFragment();
        }

        return fragment;
    }
}
