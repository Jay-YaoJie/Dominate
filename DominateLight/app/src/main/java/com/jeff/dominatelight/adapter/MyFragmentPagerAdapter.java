package com.jeff.dominatelight.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.jeff.dominatelight.fragment.BaseFragment;


/**
 * @Title: MyFragmentPagerAdapter
 * @Description: 类的描述 - ViewPager用到的FraPagerAdapter
 * @date
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private BaseFragment[] mFragments;

    public MyFragmentPagerAdapter(FragmentManager fm, BaseFragment[] mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    public void setmFragments(BaseFragment[] mFragments) {
        this.mFragments = mFragments;
    }
}
