package ru.rewindforce.concerts.MyConcerts;


import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyConcertsPageAdapter extends FragmentPagerAdapter {

    public ArrayList<Fragment> fragmentList = new ArrayList<>();
    public ArrayList<String> titleList = new ArrayList<>();

    public MyConcertsPageAdapter(FragmentManager fragmentManager, ArrayList<String> tags, ArrayList<String> titleList) {
        super(fragmentManager);
        this.titleList.addAll(titleList);
        for (String tag : tags)
            fragmentList.add(fragmentManager.findFragmentByTag(tag));
    }

    public MyConcertsPageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragment(Fragment fragment, String tag) {
        fragmentList.add(fragment);
        titleList.add(tag);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
