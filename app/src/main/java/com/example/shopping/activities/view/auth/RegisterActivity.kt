package com.example.shopping.activities.view.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shopping.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }
}