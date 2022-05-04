package com.android.wikipedia.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.android.wikipedia.Fragments.CategoryList;
import com.android.wikipedia.Fragments.FeaturedImage;
import com.android.wikipedia.Fragments.Articles;

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
                return new Articles();
            case 1:
                return new FeaturedImage();
            case 2:
                return new CategoryList();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
