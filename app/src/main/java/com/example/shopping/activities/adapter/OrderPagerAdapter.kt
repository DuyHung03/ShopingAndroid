package com.example.shopping.activities.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.shopping.activities.view.fragment.CancelledFragment
import com.example.shopping.activities.view.fragment.ConfirmedFragment
import com.example.shopping.activities.view.fragment.WaitingOrderFragment

class OrderPagerAdapter(
    var context: Context,
    fragmentManager: FragmentManager,
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> WaitingOrderFragment()
        1 -> ConfirmedFragment()
        2 -> CancelledFragment()
        else -> WaitingOrderFragment()
    }

    override fun getCount(): Int = 3

}