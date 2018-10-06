package ru.rewindforce.concerts.MyConcerts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import ru.rewindforce.concerts.HomeScreen.HomeScreenFragment;
import ru.rewindforce.concerts.HomepageActivity;
import ru.rewindforce.concerts.R;

public class MyConcertsFragment extends Fragment {

    private final static String BUNDLE_FRAGMENT_TAGS = "fragments_tags",
                                BUNDLE_FRAGMENT_TITLES = "fragments_titles";

    private ViewPager viewPager;
    private MyConcertsPageAdapter myConcertsAdapter;
    private OnHideBottomNav hideBottomNavListener;

    public MyConcertsFragment() { }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() != null) hideBottomNavListener = (HomepageActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_concerts, container, false);
        viewPager = view.findViewById(R.id.view_pager);
        if (savedInstanceState == null) {
            setupViewPager();
        } else {
            ArrayList<String> tags = savedInstanceState.getStringArrayList(BUNDLE_FRAGMENT_TAGS);
            ArrayList<String> titles = savedInstanceState.getStringArrayList(BUNDLE_FRAGMENT_TITLES);
            restoreViewPager(tags, titles);
        }
        TabLayout tabLayout = view.findViewById(R.id.my_concerts_tabs);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (hideBottomNavListener != null) hideBottomNavListener.onHide();
            }

            @Override
            public void onPageSelected(int position) { }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    private void setupViewPager() {
        myConcertsAdapter = new MyConcertsPageAdapter(getChildFragmentManager());
        myConcertsAdapter.addFragment(HomeScreenFragment.newInstance(HomeScreenFragment.ARG_UPCOMING), "Будущие");
        myConcertsAdapter.addFragment(HomeScreenFragment.newInstance(HomeScreenFragment.ARG_PAST), "Прошедшие");
        viewPager.setAdapter(myConcertsAdapter);
    }

    private void restoreViewPager(ArrayList<String> tags, ArrayList<String> titleList) {
        myConcertsAdapter = new MyConcertsPageAdapter(getChildFragmentManager(),
                                                        tags, titleList);
        viewPager.setAdapter(myConcertsAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> tags = new ArrayList<>();
        for(Fragment fragment : myConcertsAdapter.fragmentList)
            tags.add(fragment.getTag());
        outState.putStringArrayList(BUNDLE_FRAGMENT_TAGS, tags);
        outState.putStringArrayList(BUNDLE_FRAGMENT_TITLES, myConcertsAdapter.titleList);
    }

    public interface OnHideBottomNav {
        void onHide();
    }
}
