package com.example.ilias.finalapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ilias.finalapplication.Fragment.MapsFragment;
import com.example.ilias.finalapplication.Fragment.MyCarsFragment;
import com.example.ilias.finalapplication.Fragment.UserFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {



    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                UserFragment requestsFragment = new UserFragment();
                return requestsFragment;
            case 1:
                MyCarsFragment myCarsFragment = new MyCarsFragment();
                return  myCarsFragment;
            case 2:
                MapsFragment mapsFragment = new MapsFragment();
                return mapsFragment;
            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "My profile";
            case 1:
                return "My CARS";
            case 2:
                return "Map";



            default:
                return null;
        }

    }

}