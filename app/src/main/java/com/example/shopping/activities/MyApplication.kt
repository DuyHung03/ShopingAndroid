package com.example.shopping.activities

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.shopping.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Base_Theme_Shopping)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

}