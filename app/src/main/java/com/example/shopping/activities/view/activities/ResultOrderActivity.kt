package com.example.shopping.activities.view.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shopping.R
import com.example.shopping.activities.entities.Order
import com.example.shopping.databinding.ActivityResultOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_order)
        binding = ActivityResultOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent != null) {
            val order = intent.getParcelableExtra<Order>("order") as Order
            binding.orderId.text = order.orderId
        }

        initialListener()

    }

    private fun initialListener() {
        binding.toHomeBtn.setOnClickListener {
            toHomeScreen()
        }

        binding.toOrderBtn.setOnClickListener {
            toOrderTrackingScreen()
        }
    }

    private fun toOrderTrackingScreen() {
        val intent = Intent(this, OrderTrackingActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        this.startActivity(intent, options.toBundle())
        this.finish()
    }

    private fun toHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        this.startActivity(intent, options.toBundle())
        this.finish()
    }
}