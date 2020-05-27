package com.proyecto.tucca.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.proyecto.tucca.R;

import java.util.ArrayList;
import java.util.List;

public class GapAndFareFragment extends Fragment {
    private View view;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gap_and_fare, container, false);
        viewPager = view.findViewById(R.id.viewpager);
        sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), 2);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);
        return view;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<Fragment>();
        private int numTabs;
        public SectionsPagerAdapter(FragmentManager fm, int numTabs) {
            super(fm);
            this.numTabs = numTabs;
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new GapFragment();
                    fragments.add(fragment);
                    break;
                case 1:
                    fragment = new FareFragment();
                    fragments.add(fragment);
                    break;
            }
            return fragment;
        }
        @Override
        public int getCount() {
            return numTabs;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.gap);
                case 1:
                    return getString(R.string.fare);
            }
            return null;
        }
    }
}
