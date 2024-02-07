package com.example.shopping.activities.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.shopping.R
import com.example.shopping.activities.view.fragment.HomeFragment
import com.example.shopping.activities.view.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView


    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val homeFragment = HomeFragment()
        val userFragment = UserFragment()

        val fm: FragmentManager = supportFragmentManager
        var active: Fragment = homeFragment

        fm.beginTransaction().add(R.id.fragment_container, homeFragment).commit()
        fm.beginTransaction().add(R.id.fragment_container, userFragment).hide(userFragment).commit()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    fm.beginTransaction().hide(active).show(homeFragment).commit()
                    active = homeFragment
                }

                R.id.userFragment -> {
                    fm.beginTransaction().hide(active).show(userFragment).commit()
                    active = userFragment
                }
            }
            true
        }
    }
}