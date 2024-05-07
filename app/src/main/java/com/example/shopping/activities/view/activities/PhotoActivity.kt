package com.example.shopping.activities.view.activities

import android.os.Bundle
import android.view.GestureDetector
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shopping.databinding.ActivityPhotoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoBinding
    private lateinit var gestureDetector: GestureDetector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve photoUrl and transitionName from intent
        val photoUrl = intent.getStringExtra("photoUrl")

        // Load image using Glide
        Glide.with(this)
            .load(photoUrl)
            .into(binding.image)

        binding.closeButton.setOnClickListener {
            this.finish()
        }
    }

}
