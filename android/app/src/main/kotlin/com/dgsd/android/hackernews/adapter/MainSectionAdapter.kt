package com.dgsd.android.hackernews.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray
import android.view.ViewGroup
import com.dgsd.android.hackernews.fragment.StoryListFragment
import com.dgsd.android.hackernews.model.PageType
import com.dgsd.android.hackernews.util.getTitleRes


public class MainSectionAdapter(val context: Context, fm : FragmentManager) : FragmentPagerAdapter(fm) {

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
        return context.getString(getPageType(position).getTitleRes())
    }

    private fun getPageType(pos: Int): PageType {
        return PageType.values().elementAt(pos)
    }
}