package com.telink.bluetooth.light.util;

import android.app.Fragment;

import com.telink.bluetooth.light.R;
import com.telink.bluetooth.light.fragments.DeviceListFragment;
import com.telink.bluetooth.light.fragments.GroupListFragment;
import com.telink.bluetooth.light.fragments.MainTestFragment;

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
