package ru.rewindforce.concerts

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import kotlinx.android.synthetic.main.activity_homepage.*
import ru.rewindforce.concerts.home.ConcertsListFragment
import ru.rewindforce.concerts.home.ConcertsListFragment.Companion.ARG_OVERVIEW
import ru.rewindforce.concerts.home.ConcertsListFragment.Companion.ARG_PAST
import ru.rewindforce.concerts.home.ConcertsListFragment.Companion.ARG_UPCOMING
import ru.rewindforce.concerts.authorization.AuthorizationActivity
import ru.rewindforce.concerts.utils.TabsFragment
import ru.rewindforce.concerts.utils.*
import java.util.ArrayList

private const val BUNDLE_CURRENT_FRAGMENT = "bundle_current_fragment"

class HomepageActivity: AppCompatActivity(), TabsFragment.OnHideBottomNav {

    private val TAG: String = HomepageActivity::class.java.simpleName

    companion object {
        const val OVERVIEW_FRAGMENT = "fragment_overview"
        const val MY_CONCERTS_FRAGMENT = "fragment_tabs"
        const val MENU_FRAGMENT = "menu_fragment"

        const val PROFILE_FRAGMENT = "profile_fragment"
        const val EDIT_PROFILE_FRAGMENT = "fragment_edit_profile"
        const val FRIENDS_LIST_FRAGMENT = "friends_list_fragment"
        const val REQUESTS_LIST_FRAGMENT = "requests_list_fragment"
        const val CONCERT_DETAILS = "concert_details"

        @JvmStatic
        var currentFragmentTag: String = ""
    }

    private lateinit var previousItem: MenuItem
    private var whiteStatusEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        setStatusBarTranslucent(true)
        marginStatusBar(fragment_container)

        if (savedInstanceState?.containsKey(BUNDLE_CURRENT_FRAGMENT) == true)
            currentFragmentTag = savedInstanceState.getString(BUNDLE_CURRENT_FRAGMENT) ?: ""

        Log.d(TAG, "onCreate()")
        Log.d(TAG, currentFragmentTag)

        previousItem = bottomNavigation.menu.getItem(1)
        bottomNavigation.menu.getItem(0).isChecked = true

        if (!hasPref(AuthorizationActivity.PREF_TOKEN) && !hasPref(AuthorizationActivity.PREF_UID)) bottomNavigation.visibility = View.GONE
        if (currentFragmentTag == "") openFragment(OVERVIEW_FRAGMENT,
                ConcertsListFragment.newInstance(ARG_OVERVIEW, getStringPref(PREF_UID) ?: ""), false)
        else hideEverythingExceptCurrent()

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    openFragment(OVERVIEW_FRAGMENT,
                            ConcertsListFragment.newInstance(ARG_OVERVIEW, getStringPref(PREF_UID) ?: ""), true)
                    changeBottomNavSelectedTab(0)
                }
                R.id.my_concerts -> {
                    val fragmentsList: ArrayList<ParcelFragment> = ArrayList()
                    val tabsList: ArrayList<String> = ArrayList()
                    fragmentsList.add(ConcertsListFragment.newInstance(ARG_UPCOMING, getStringPref(PREF_UID) ?: ""))
                    tabsList.add("Будущие")
                    fragmentsList.add(ConcertsListFragment.newInstance(ARG_PAST, getStringPref(PREF_UID) ?: ""))
                    tabsList.add("Прошедшие")
                    openFragment(MY_CONCERTS_FRAGMENT, TabsFragment.newInstance(fragmentsList, tabsList), true)
                    changeBottomNavSelectedTab(1)
                }
                R.id.menu -> {
                    openFragment(MENU_FRAGMENT, MenuFragment.newInstance(), true)
                    changeBottomNavSelectedTab(2)
                }
                else -> { }
            }

            false
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (hasPref(AuthorizationActivity.PREF_TOKEN) && hasPref(AuthorizationActivity.PREF_UID)) {
                val fragmentDetails = findFragment(CONCERT_DETAILS)
                val fragmentOverview = findFragment(OVERVIEW_FRAGMENT)
                val fragmentMyConcerts = findFragment(MY_CONCERTS_FRAGMENT)
                val fragmentMenu = findFragment(MENU_FRAGMENT)

                if (fragmentDetails != null)
                    if (fragmentDetails.isVisible) bottomNavigation.visibility = View.GONE
                    else bottomNavigation.visibility = View.VISIBLE
                else bottomNavigation.visibility = View.VISIBLE

                if (fragmentOverview?.isVisible == true) {
                    changeBottomNavSelectedTab(0)
                    currentFragmentTag = OVERVIEW_FRAGMENT
                }
                if (fragmentMyConcerts?.isVisible == true) {
                    changeBottomNavSelectedTab(1)
                    currentFragmentTag = MY_CONCERTS_FRAGMENT
                }
                if (fragmentMenu?.isVisible == true) {
                    changeBottomNavSelectedTab(2)
                    currentFragmentTag = MENU_FRAGMENT
                }
            }
        }
    }

    override fun onBackPressed() {
        val currentFragment = findFragment(currentFragmentTag)
        if (currentFragment != null && currentFragment.childFragmentManager.backStackEntryCount > 0) {
            currentFragment.childFragmentManager.popBackStack()
        } else super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_CURRENT_FRAGMENT, currentFragmentTag)
    }

    private fun hideEverythingExceptCurrent() {
        val ft = supportFragmentManager.beginTransaction()

        findFragment(OVERVIEW_FRAGMENT).let {
            if (currentFragmentTag != OVERVIEW_FRAGMENT && it != null) ft.hide(it)
            if (currentFragmentTag == OVERVIEW_FRAGMENT && it == null)
                openFragment(OVERVIEW_FRAGMENT, ConcertsListFragment.newInstance(ARG_OVERVIEW, getStringPref(PREF_UID) ?: ""), false)
        }
        findFragment(MY_CONCERTS_FRAGMENT).let {
            if (currentFragmentTag != MY_CONCERTS_FRAGMENT && it != null) ft.hide(it)
            if (currentFragmentTag == MY_CONCERTS_FRAGMENT && it == null) {
                val fragmentsList: ArrayList<ParcelFragment> = ArrayList()
                val tabsList: ArrayList<String> = ArrayList()
                fragmentsList.add(ConcertsListFragment.newInstance(ARG_UPCOMING, getStringPref(PREF_UID) ?: ""))
                tabsList.add("Будущие")
                fragmentsList.add(ConcertsListFragment.newInstance(ARG_PAST, getStringPref(PREF_UID) ?: ""))
                tabsList.add("Прошедшие")
                openFragment(MY_CONCERTS_FRAGMENT, TabsFragment.newInstance(fragmentsList, tabsList), false)
            }
        }
        findFragment(MENU_FRAGMENT).let {
            if (currentFragmentTag != MENU_FRAGMENT && it != null) ft.hide(it)
            if (currentFragmentTag == MENU_FRAGMENT && it == null)
                openFragment(MENU_FRAGMENT, MenuFragment.newInstance(), false)

        }
        ft.commit()
    }

    private fun setStatusBarTranslucent(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (enable) window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            else window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    private fun changeBottomNavSelectedTab(position: Int) {
        previousItem.isChecked = false
        bottomNavigation.menu.getItem(position).isChecked = true
        previousItem = bottomNavigation.menu.getItem(position)
    }

    fun hideBottomNav(hide: Boolean) {
        if (hide) bottomNavigation.visibility = View.GONE else bottomNavigation.visibility = View.VISIBLE
    }

    fun setStatusBarWhite(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                !whiteStatusEnabled && enable -> {
                    var flags = window.decorView.systemUiVisibility
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    window.decorView.systemUiVisibility = flags
                    window.statusBarColor = Color.WHITE
                    setStatusBarTranslucent(false)
                    whiteStatusEnabled = true
                } whiteStatusEnabled && !enable -> {
                    var flags = window.decorView.systemUiVisibility
                    flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    window.decorView.systemUiVisibility = flags
                    setStatusBarTranslucent(true)
                    whiteStatusEnabled = false
                }
            }
        }
    }

    override fun onHide() {
        val params = bottomNavigation.layoutParams as CoordinatorLayout.LayoutParams
        (params.behavior as HideBottomViewOnScrollBehavior)
                .onNestedScroll(coordinator, bottomNavigation, bottomNavigation, 0, -1, 0, 0)
    }

    private fun openFragment(tag: String, fragment: Fragment, shouldAddToBS: Boolean) {
        if (findFragment(tag) == null) {
            val ft = supportFragmentManager.beginTransaction()

            findFragment(currentFragmentTag)?.let { ft.detach(it) }

            ft.add(container.id, fragment, tag)
            if (shouldAddToBS) ft.addToBackStack(tag)
            currentFragmentTag = tag
            ft.commit()
        } else if (findFragment(tag)?.isDetached == true) {
            val ft = supportFragmentManager.beginTransaction()

            findFragment(currentFragmentTag)?.let { ft.detach(it) }

            findFragment(tag)?.let { ft.attach(it) }
            if (shouldAddToBS) ft.addToBackStack(tag)
            currentFragmentTag = tag
            ft.commit()
        }
    }

    interface OnTabReselected {
        fun onReselected()
    }
}