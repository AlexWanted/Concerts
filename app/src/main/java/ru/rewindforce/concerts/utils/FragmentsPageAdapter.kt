package ru.rewindforce.concerts.utils

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentsPageAdapter(var fragmentManager: FragmentManager,
                           val fragments: ArrayList<Fragment> = ArrayList(),
                           val titles: ArrayList<String> = ArrayList()): FragmentPagerAdapter(fragmentManager) {

    fun addFragment(fragment: Fragment, tag: String) {
        fragments.add(fragment)
        titles.add(tag)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        super.destroyItem(container, position, item)
    }
}