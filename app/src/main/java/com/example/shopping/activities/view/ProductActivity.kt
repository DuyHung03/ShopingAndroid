package com.example.shopping.activities.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.R
import com.example.shopping.activities.adapter.SliderPagerAdapter
import com.example.shopping.activities.entities.Product
import dagger.hilt.android.AndroidEntryPoint
import me.relex.circleindicator.CircleIndicator3

@AndroidEntryPoint
class ProductActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var title: TextView
    private lateinit var price: TextView
    private lateinit var likeButton: ImageView
    private lateinit var description: TextView
    private lateinit var sliderPagerAdapter: SliderPagerAdapter
    private var imageList = mutableListOf<String>()
    private var product: Product? = null
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        product = intent.getParcelableExtra("product")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        initialView()
        initialListener()
    }


    private fun initialView() {
        viewPager = findViewById(R.id.viewPager)
        indicator = findViewById(R.id.indicator)
        title = findViewById(R.id.title)
        price = findViewById(R.id.price)
        likeButton = findViewById(R.id.likeButton)
        description = findViewById(R.id.description)
        backButton = findViewById(R.id.backButton)
    }

    private fun initialListener() {
        backButton.setOnClickListener {
            this.finish()
        }

        setContent(product)
    }

    private fun setContent(product: Product?) {
        if (product != null) {
            title.text = product.title
            price.text = getString(R.string.price, product.price.toString())
            description.text = product.description

            for (image in product.images) {
                imageList.add(image)
            }

            setImagesViewPager(imageList)
        }
    }

    private fun setImagesViewPager(imageList: List<String>) {
        sliderPagerAdapter = SliderPagerAdapter(imageList)
        viewPager.adapter = sliderPagerAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val currentPageIndex = 0
        viewPager.currentItem = currentPageIndex

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {})

        indicator.setViewPager(viewPager)

        sliderPagerAdapter.registerAdapterDataObserver(indicator.adapterDataObserver)
    }

}