package com.dboy.meusbagulhos.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dboy.meusbagulhos.fragments.DoneFragment
import com.dboy.meusbagulhos.fragments.UndoneFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val numeroTabs: Int):
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return numeroTabs //número de tabs
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return UndoneFragment()
            1 -> return DoneFragment()
            else -> return UndoneFragment()
        }
    }
}