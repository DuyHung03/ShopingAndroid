package com.example.shopping.activities.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shopping.R
import com.example.shopping.activities.adapter.OrderPagerAdapter
import com.example.shopping.databinding.ActivityOrderTrackingBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderTrackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderTrackingBinding

    //    private val dataViewModel by viewModels<DataViewModel>()
//    private val authViewModel by viewModels<AuthViewModel>()
//    private var waitingList: MutableList<Order> = mutableListOf()
//    private var confirmedList: MutableList<Order> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_tracking)

        binding = ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.wait_for_confirmation))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.confirmed))

        val adapter = OrderPagerAdapter(this, supportFragmentManager)

        binding.viewPager.adapter = adapter

        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.backButton.setOnClickListener {
            this.finish()
        }

    }

}