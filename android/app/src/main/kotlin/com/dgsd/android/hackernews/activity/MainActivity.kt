package com.dgsd.android.hackernews.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup
import com.dgsd.android.hackernews.HNApp
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.analytics.Tracker
import com.dgsd.android.hackernews.fragment.StoryListFragment
import com.dgsd.android.hackernews.model.PageType
import com.dgsd.android.hackernews.util.findFirstChild
import com.dgsd.android.hackernews.util.getTitleRes
import kotlinx.android.synthetic.act_main.tabLayout
import kotlinx.android.synthetic.act_main.toolbar
import kotlinx.android.synthetic.act_main.viewPager

public class MainActivity : BaseActivity() {

    private var analytics: Tracker? = null

    override fun getLayoutResource(): Int {
        return R.layout.act_main
    }

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        analytics = (application as HNApp).getAppServicesComponent().analytics()

        viewPager.adapter = SectionAdapter(supportFragmentManager)

        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setOnTabSelectedListener(object: TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                super.onTabSelected(tab)
                analytics?.trackClick("tab_${tab?.text?.toString()?.toLowerCase() ?: "unknown"}")
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                analytics?.trackClick("selected_tab")

                val fragment = (viewPager.adapter as SectionAdapter).fragArray.get(tab.position)
                // TODO: Baaaddd .. shouldnt rely on traversing view hierarchy for this
                fragment.view?.findFirstChild<RecyclerView>()?.smoothScrollToPosition(0)
            }
        })
    }

    private inner class SectionAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

        val fragArray: SparseArray<Fragment> = SparseArray()

        override fun instantiateItem(container: ViewGroup?, position: Int): Any? {
            val fragment = super.instantiateItem(container, position) as Fragment
            fragArray.put(position, fragment)
            return fragment
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            super.destroyItem(container, position, `object`)
            fragArray.delete(position)
        }

        override fun getItem(position: Int): Fragment? {
            return StoryListFragment.newInstance(getPageType(position))
        }

        override fun getCount(): Int {
            return PageType.values().size()
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return getString(getPageType(position).getTitleRes())
        }

        private fun getPageType(pos: Int): PageType {
            return PageType.values().elementAt(pos)
        }
    }
}
