package com.example.taler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.taler.Profile.ProfileFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int num;

    public PagerAdapter(FragmentManager fm, int num) {
        super(fm, num);
        this.num = num;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ProfileFragment tab1 = new ProfileFragment();
                return tab1;
            case 1:
                MenuFragment tab2 = new MenuFragment();
                return tab2;
            case 2:
                SettingFragment tab3 = new SettingFragment();
                return tab3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return num;
    }

}
