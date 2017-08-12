package com.example.stephen.soundrecorder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by stephen on 17-8-11.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    private String[] titles;

    public FragmentAdapter(FragmentManager fm,String[] list,List<Fragment> fragments) {
        super(fm);
        this.titles=list;
        this.fragmentList=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }
}
