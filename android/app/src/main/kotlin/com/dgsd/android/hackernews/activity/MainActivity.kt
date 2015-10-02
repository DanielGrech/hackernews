package com.dgsd.android.hackernews.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.dgsd.android.hackernews.HNApp
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.adapter.MainSectionAdapter
import com.dgsd.android.hackernews.analytics.Tracker
import com.dgsd.android.hackernews.util.findFirstChild
import com.dgsd.android.hackernews.util.onPreDraw
import com.dgsd.android.hackernews.view.BaseRecyclerView
import com.dgsd.android.hackernews.view.StoryRecyclerView
import kotlinx.android.synthetic.act_main.appbar
import kotlinx.android.synthetic.act_main.tabLayout
import kotlinx.android.synthetic.act_main.toolbar
import kotlinx.android.synthetic.act_main.viewPager
import org.jetbrains.anko.onHierarchyChangeListener

public class MainActivity : BaseActivity() {

    companion object {
        public fun getStartIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private var analytics: Tracker? = null

    override fun getLayoutResource(): Int {
        return R.layout.act_main
    }

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        analytics = (application as HNApp).getAppServicesComponent().analytics()

        viewPager.adapter = MainSectionAdapter(this, supportFragmentManager)

        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                super.onTabSelected(tab)
                analytics?.trackClick("tab_${tab?.text?.toString()?.toLowerCase() ?: "unknown"}")

                appbar.animate().translationY(0f).start()
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                analytics?.trackClick("selected_tab")

                val fragment = (viewPager.adapter as MainSectionAdapter).fragArray.get(tab.position)
                // TODO: Baaaddd .. shouldnt rely on traversing view hierarchy for this
                fragment.view?.findFirstChild<RecyclerView>()?.smoothScrollToPosition(0)
            }
        })

        setupToolbarCollapse()
    }

    override fun onStart() {
        super.onStart()
        appbar.translationY = 0f
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.act_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            analytics?.trackClick("settings")
            startActivity(SettingsActivity.getStartIntent(this))
        }

        return super.onOptionsItemSelected(item)
    }

    val PARALLAX_OFFSET_SCROLL_DOWN = 0.5f
    val PARALLAX_OFFSET_SCROLL_UP = 0.1f

    private fun setupToolbarCollapse() {
        viewPager.onHierarchyChangeListener {
            onChildViewAdded { parent, child ->
                val recyclerView = child?.findFirstChild<StoryRecyclerView>()
                if (recyclerView != null) {
                    with (recyclerView) {
                        setOnScrollListener { dY ->
                            val change = -dY * if (dY > 0) PARALLAX_OFFSET_SCROLL_UP else PARALLAX_OFFSET_SCROLL_DOWN
                            val minTrans = -appbar.height / 2f
                            val proposedTrans = appbar.translationY + change

                            appbar.translationY = Math.max(minTrans, Math.min(0f, proposedTrans))
                        }

                        onPreDraw {
                            recyclerView.setTopItemAllowance(appbar.height)
                            true
                        }
                    }
                }

                var swipeRefreshLayout = child?.findFirstChild<SwipeRefreshLayout>()
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.onPreDraw {
                        swipeRefreshLayout?.setProgressViewOffset(false,
                                (appbar.height * .5f).toInt(), (appbar.height * 1.2f).toInt())
                        false
                    }
                }
            }

            onChildViewRemoved { parent, child ->
                if (child is BaseRecyclerView<*>) {
                    child.setOnScrollListener {
                        // Clear out our listener..
                    }
                }
            }
        }
    }
}
