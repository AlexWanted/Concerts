package ru.rewindforce.concerts;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.rewindforce.concerts.AddConcert.AddConcertFragment;
import ru.rewindforce.concerts.Authorization.AuthorizationActivity;
import ru.rewindforce.concerts.HomeScreen.Concert;
import ru.rewindforce.concerts.ConcertDetails.ConcertDetailsFragment;
import ru.rewindforce.concerts.HomeScreen.HomeScreenFragment;
import ru.rewindforce.concerts.MyConcerts.MyConcertsFragment;

public class HomepageActivity extends AppCompatActivity implements MyConcertsFragment.OnHideBottomNav {

    private static final String TAG = HomepageActivity.class.getSimpleName();

    public static final String OVERVIEW_FRAGMENT = "fragment_overview",
                               MY_CONCERTS_FRAGMENT = "fragment_my_concerts";
    public static final int OVERVIEW_INDEX = 0,
                            MY_CONCERTS_INDEX = 1;

    private BottomNavigationView bottomNavigation;
    private MenuItem previousItem;
    private OnTabReselected tabReselectedListener;
    private String currentFragmentTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        setStatusBarTranslucent(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if(savedInstanceState != null && !savedInstanceState.isEmpty())
            currentFragmentTag = savedInstanceState.getString("current_fragment");

        bottomNavigation = findViewById(R.id.bottom_navigation);
        previousItem = bottomNavigation.getMenu().getItem(1);
        bottomNavigation.getMenu().getItem(0).setChecked(true);

        if (!hasToken() && !hasUid()) bottomNavigation.setVisibility(View.GONE);

        if (currentFragmentTag.equals("")) openFragment(OVERVIEW_FRAGMENT, OVERVIEW_INDEX,
                HomeScreenFragment.newInstance(HomeScreenFragment.ARG_OVERVIEW));
        else hideEverythingExceptCurrent();

        //tabReselectedListener = (OnTabReselected) getSupportFragmentManager().findFragmentByTag(currentFragmentTag);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.my_concerts:
                        openFragment(MY_CONCERTS_FRAGMENT, MY_CONCERTS_INDEX, new MyConcertsFragment());
                        break;
                    case R.id.home:
                        openFragment(OVERVIEW_FRAGMENT, OVERVIEW_INDEX, HomeScreenFragment.newInstance(HomeScreenFragment.ARG_OVERVIEW));
                        break;
                    case R.id.menu:
                        if (getSupportFragmentManager().findFragmentByTag("add_concert") == null) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            AddConcertFragment addConcertFragment =  AddConcertFragment.newInstance();
                            transaction.addToBackStack(null);
                            transaction.add(R.id.fragment_container, addConcertFragment, "add_concert");
                            transaction.hide(findFragment(currentFragmentTag));
                            transaction.commit();
                        }
                        break;
                }
                return false;
            }
        });

        bottomNavigation.setOnNavigationItemReselectedListener((MenuItem menuItem) ->  {
            if (tabReselectedListener != null) tabReselectedListener.onReselected();
        });
        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) bottomNavigation.getLayoutParams();
                if (params.getBehavior() != null)
                    ((HideBottomViewOnScrollBehavior)params.getBehavior()).onNestedScroll(
                            (CoordinatorLayout)findViewById(R.id.coordinator),
                            bottomNavigation, bottomNavigation, 0, -1, 0, 0);
            }

            @Override
            public void onPageSelected(int position) {
                if (previousItem != null) {
                    previousItem.setChecked(false);
                }
                bottomNavigation.getMenu().getItem(position).setChecked(true);
                previousItem = bottomNavigation.getMenu().getItem(position);
                tabReselectedListener = (OnTabReselected) adapter.getItem(viewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });*/
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (hasToken() && hasUid()) {
                if (getSupportFragmentManager().findFragmentByTag("concert_details") != null)
                    bottomNavigation.setVisibility(View.GONE);
                else bottomNavigation.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean hasToken() {
        return getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE).contains(AuthorizationActivity.PREF_TOKEN);
    }

    private boolean hasUid() {
        return getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE).contains(AuthorizationActivity.PREF_UID);
    }

    private void hideEverythingExceptCurrent() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (findFragment(OVERVIEW_FRAGMENT) != null && !currentFragmentTag.equals(OVERVIEW_FRAGMENT))
            ft.hide(findFragment(OVERVIEW_FRAGMENT));
        if (findFragment(MY_CONCERTS_FRAGMENT) != null && !currentFragmentTag.equals(MY_CONCERTS_FRAGMENT))
            ft.hide(findFragment(MY_CONCERTS_FRAGMENT));
        ft.commit();
    }

    private void openFragment(String tag, int index, Fragment fragment) {
        if(findFragment(tag) == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (findFragment(currentFragmentTag) != null)
                ft.hide(findFragment(currentFragmentTag));
            ft.add(R.id.container, fragment, tag);
            currentFragmentTag = tag;
            ft.commit();
            changeBottomNavSelectedTab(index);
        } else if (findFragment(tag).isHidden()){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (findFragment(currentFragmentTag) != null)
                ft.hide(findFragment(currentFragmentTag));
            ft.show(findFragment(tag));
            currentFragmentTag = tag;
            ft.commit();
            changeBottomNavSelectedTab(index);
        }
    }

    private Fragment findFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    private void changeBottomNavSelectedTab(int position) {
        if (previousItem != null) {
            previousItem.setChecked(false);
        }
        bottomNavigation.getMenu().getItem(position).setChecked(true);
        previousItem = bottomNavigation.getMenu().getItem(position);
        //tabReselectedListener = (OnTabReselected) getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
    }

    public void openConcertDetailsFragment(Concert concert) {
        if (getSupportFragmentManager().findFragmentByTag("concert_details") == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ConcertDetailsFragment concertDetailsFragment =
                    ConcertDetailsFragment.newInstance(concert);
            transaction.addToBackStack(null);
            transaction.add(R.id.fragment_container, concertDetailsFragment, "concert_details");
            transaction.hide(findFragment(currentFragmentTag));
            transaction.commit();
        }
    }

    /**
     * Метод для изменения прозрачности и цвета статус бара.
     * Не работает на версии SDK меньше 19.
     *
     * @param enable - включена ли прозрачность.
     */
    protected void setStatusBarTranslucent(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (enable) getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onHide() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) bottomNavigation.getLayoutParams();
        if (params.getBehavior() != null)
            ((HideBottomViewOnScrollBehavior)params.getBehavior()).onNestedScroll(findViewById(R.id.coordinator),
                    bottomNavigation, bottomNavigation, 0, -1, 0, 0);
    }

    public interface OnTabReselected {
        void onReselected();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState()");
        outState.putString("current_fragment", currentFragmentTag);
    }
}
