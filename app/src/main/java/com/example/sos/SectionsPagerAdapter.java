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
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                CardsFragment cardsFragment = new CardsFragment();
                return cardsFragment;
            case 2:
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
                String Userstitle = "FIX";
                return Userstitle;
            case 1:
                String Cardstitle = "Find Users";
                return Cardstitle;
            case 2:
                String Friendstitle = "Friends";
                return Friendstitle;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
