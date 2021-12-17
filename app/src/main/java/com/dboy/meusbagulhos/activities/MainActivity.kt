package com.dboy.meusbagulhos.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dboy.meusbagulhos.R
import com.dboy.meusbagulhos.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val tabLayout by lazy { findViewById<TabLayout>(R.id.mainTabLayout) }
    private val viewPager2 by lazy { findViewById<ViewPager2>(R.id.mainViewPager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViewPagerAdapter()
        setTabLayout()
    }

    private fun setViewPagerAdapter() {
        val viewPagerAdapter = ViewPagerAdapter(this, 2)
        viewPager2.adapter = viewPagerAdapter
    }

    private fun setTabLayout() {
        TabLayoutMediator(tabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.text = getText(R.string.tabUndone)
                1 -> tab.text = getText(R.string.tabDone)
            }
        }.attach()
    }
}