package com.kksmartcontrol.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kksmartcontrol.fragment.ControlSettingFragment;
import com.kksmartcontrol.fragment.MediaPlayListFragment;

/**
 * Created by BigBigBoy on 2015/3/18.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private ControlSettingFragment controlFragment;

    private MediaPlayListFragment mediaplayListFragment;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private final String[] titles = {"设    置", "播    放"};

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (controlFragment == null) {
                    controlFragment = new ControlSettingFragment();
                }
                return controlFragment;
            case 1:
                if (mediaplayListFragment == null) {
                    mediaplayListFragment = new MediaPlayListFragment();
                }
                return mediaplayListFragment;

            default:
                return null;
        }
    }
}


