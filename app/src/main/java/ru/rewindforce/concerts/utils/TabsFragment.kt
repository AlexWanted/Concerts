package ru.rewindforce.concerts.utils

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import ru.rewindforce.concerts.R
import kotlinx.android.synthetic.main.fragment_tabs.*
import org.jetbrains.anko.support.v4.ctx
import ru.rewindforce.concerts.HomepageActivity
import java.util.ArrayList

private const val BUNDLE_FRAGMENT_TAGS = "fragments_tags"
private const val BUNDLE_FRAGMENTS = "bundle_fragments"
private const val BUNDLE_TABS = "bundle_tabs"
private const val BUNDLE_FRAGMENT_TITLES = "fragments_titles"

class TabsFragment: BaseFragment(R.layout.fragment_tabs, true) {

    private lateinit var fragmentsAdapter: FragmentsPageAdapter
    private var hideBottomNavListener: OnHideBottomNav? = null

    private lateinit var fragmentsList: ArrayList<ParcelFragment>
    private lateinit var tabsList: ArrayList<String>

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.let { hideBottomNavListener = it as HomepageActivity }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { fragmentsList = (it.getParcelableArrayList(BUNDLE_FRAGMENTS) ?: ArrayList())
                         tabsList = (it.getStringArrayList(BUNDLE_TABS) ?: ArrayList())}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        returnButton.setOnClickListener { activity?.onBackPressed() }

        if (savedInstanceState == null) setupViewPager()
        else {
            val tags = savedInstanceState.getStringArrayList(BUNDLE_FRAGMENT_TAGS) ?: ArrayList()
            val titles = savedInstanceState.getStringArrayList(BUNDLE_FRAGMENT_TITLES) ?: ArrayList()
            restoreViewPager(tags, titles)
        }

        fragmentTabs.setupWithViewPager(fragmentsViewPager)
        fragmentTabs.setSelectedTabIndicator(ContextCompat.getDrawable(ctx, R.drawable.drawable_indicator))
        fragmentsViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                hideBottomNavListener?.onHide()
            }
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val tags = ArrayList<String>()
        for (fragment in fragmentsAdapter.fragments) tags.add(fragment.tag ?: "")
        outState.putStringArrayList(BUNDLE_FRAGMENT_TAGS, tags)
        outState.putStringArrayList(BUNDLE_FRAGMENT_TITLES, fragmentsAdapter.titles)
    }

    private fun setupViewPager() {
        fragmentsAdapter = FragmentsPageAdapter(fragmentManager = childFragmentManager)
        fragmentsList.forEachIndexed { index, parcelFragment ->
            fragmentsAdapter.addFragment(parcelFragment, tabsList[index])
        }
        fragmentsViewPager.adapter = fragmentsAdapter
    }

    private fun restoreViewPager(tags: ArrayList<String>, titleList: ArrayList<String>) {
        val fragments: ArrayList<Fragment> = ArrayList()
        tags.forEach { fragments.add(childFragmentManager.findFragmentByTag(it) ?: Fragment()) }
        fragmentsAdapter = FragmentsPageAdapter(fragmentManager = childFragmentManager,
                                                 fragments = fragments,
                                                 titles = titleList)
        fragmentsViewPager.adapter = fragmentsAdapter
    }

    interface OnHideBottomNav {
        fun onHide()
    }

    companion object {
        @JvmStatic
        fun newInstance(fragments: ArrayList<ParcelFragment>, tabs: ArrayList<String>) = TabsFragment().apply {
            arguments = Bundle().apply { putParcelableArrayList(BUNDLE_FRAGMENTS, fragments)
                                         putStringArrayList(BUNDLE_TABS, tabs)}
        }
    }
}