package com.example.shopping.activities.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shopping.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)
    }
}