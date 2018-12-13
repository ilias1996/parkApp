package com.example.ilias.finalapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ilias.finalapplication.Fragment.MapsFragment;
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
                MapsFragment chatsFragment = new MapsFragment();
                return  chatsFragment;

            case 2:
                UserFragment friendsFragment = new UserFragment();
                return friendsFragment;
            case 3:
                MapsFragment mapsFragment = new MapsFragment();
                return mapsFragment;

            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "REQUESTS";

            case 1:
                return "CHATS";

            case 2:
                return "FRIENDS";
            case 3:
                return "Map";


            default:
                return null;
        }

    }

}