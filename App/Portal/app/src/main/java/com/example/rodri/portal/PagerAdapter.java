package com.example.rodri.portal;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by rodri on 15-Apr-17.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a UserContainerFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return UserContainerFragment.newInstance();
            case 1:
                return AdminContainerFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "USER";
            case 1:
                return "ADMIN";
        }
        return null;
    }
}
