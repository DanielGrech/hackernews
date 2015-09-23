package com.dgsd.android.hackernews.activity

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.fragment.StoryListFragment
import com.dgsd.android.hackernews.model.PageType
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.MainPresenter
import com.dgsd.android.hackernews.mvp.view.MainMvpView
import com.dgsd.android.hackernews.util.getTitleRes
import kotlinx.android.synthetic.act_main.*
import org.jetbrains.anko.toast

public class MainActivity : PresentableActivity<MainMvpView, MainPresenter>(), MainMvpView {

    override fun getLayoutResource(): Int {
        return R.layout.act_main
    }

    override fun createPresenter(component: AppServicesComponent): MainPresenter {
        return MainPresenter(this, component)
    }

    override fun getContext(): Context {
        return this
    }

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        viewPager.adapter = SectionAdapter(supportFragmentManager)

        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
                // TODO: Scroll list to top..
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                // No-op
            }
        })
    }

    private inner class SectionAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

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
