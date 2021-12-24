package com.android.documentsharing.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.android.documentsharing.Fragments.Downloaded;
import com.android.documentsharing.Fragments.Received;
import com.android.documentsharing.Fragments.Shared;

@SuppressWarnings ("ALL")
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Shared();
            case 1:
                return new Received();
            case 2:
                return new Downloaded();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
