package com.example.sos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CardsFragment cardsFragment = new CardsFragment();
                return cardsFragment;
            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        switch (position) {
            case 0:
                String Cardstitle = "Find Users";
                return Cardstitle;
            case 1:
                String Friendstitle = "Friends";
                return Friendstitle;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
