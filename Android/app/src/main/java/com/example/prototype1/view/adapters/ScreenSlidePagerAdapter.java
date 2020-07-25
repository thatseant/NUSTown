package com.example.prototype1.view.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    public ScreenSlidePagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        if (position==0) {
            return new JiosFragmentForPager();
        } else {
            return new GroupsFragmentForPager();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}