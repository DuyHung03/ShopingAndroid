package com.example.shopping.activities.view.activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.R
import com.example.shopping.activities.adapter.SliderPagerAdapter
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.helper.GlideImageLoader
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import me.relex.circleindicator.CircleIndicator3

@AndroidEntryPoint
@SuppressLint("LogNotTimber")
class ProductActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var title: TextView
    private lateinit var price: TextView
    private lateinit var likeButton: ImageView
    private lateinit var cart: ImageView
    private lateinit var description: TextView
    private lateinit var badge: TextView
    private lateinit var addToCartButton: MaterialButton
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var sliderPagerAdapter: SliderPagerAdapter
    private lateinit var glideImageLoader: GlideImageLoader
    private var imageList = mutableListOf<String>()
    private var product: Product? = null
    private lateinit var backButton: ImageView
    private val dataViewModel by viewModels<DataViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        product = intent.getParcelableExtra("product")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        initialView()
        initialListener()
    }


    override fun onResume() {
        super.onResume()
        dataViewModel.getProductsInCart(authViewModel.currentUser!!.uid)
        dataViewModel.cartQuantity.observe(this) { state ->
            if (state > 0) {
                badge.text = state.toString()
                badge.visibility = android.view.View.VISIBLE
            } else
                badge.visibility = android.view.View.GONE
        }
    }


    private fun initialView() {
        viewPager = findViewById(R.id.viewPager)
        indicator = findViewById(R.id.indicator)
        title = findViewById(R.id.title)
        price = findViewById(R.id.price)
        glideImageLoader = GlideImageLoader(this)
        likeButton = findViewById(R.id.likeButton)
        description = findViewById(R.id.description)
        backButton = findViewById(R.id.backButton)
        progressBar = findViewById(R.id.progressBar)
        addToCartButton = findViewById(R.id.addToCartButton)
        badge = findViewById(R.id.badge)
        cart = findViewById(R.id.cart)
    }

    private fun initialListener() {
        backButton.setOnClickListener {
            this.finish()
        }

        setContent(product)

        addToCartButton.setOnClickListener {
            showAddToCartBottomSheet()
        }

        cart.setOnClickListener {
            toCartScreen()
        }



        dataViewModel.addToCartFlow.asLiveData().observe(this) { state ->
            when (state) {
                is Resources.Failure -> {
                    val error = state.e
                    Log.d("TAG", "onCreateView: $error")
                    progressBar.visibility = View.GONE
                    bottomSheetDialog.hide()
                    Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                }

                Resources.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is Resources.Success -> {
                    val successMessage = state.result
                    Log.d("TAG", "onCreateView: $successMessage")
                    progressBar.visibility = View.GONE
                    bottomSheetDialog.hide()
                    Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showAddToCartBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.setCancelable(true)

        setContentToSheet(dialogView)

        bottomSheetDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setContentToSheet(dialogView: View?) {
        if (dialogView != null) {

            val image = dialogView.findViewById<ImageView>(R.id.image)
            val price = dialogView.findViewById<TextView>(R.id.price)
            val quantity = dialogView.findViewById<TextView>(R.id.quantity)
            val increaseButton = dialogView.findViewById<ImageView>(R.id.increaseButton)
            val decreaseButton = dialogView.findViewById<ImageView>(R.id.decreaseButton)
            val addToCartButton = dialogView.findViewById<MaterialButton>(R.id.addToCartButton)
            val close: ImageView = dialogView.findViewById(R.id.closeButton)

            close.setOnClickListener {
                bottomSheetDialog.hide()
            }

            //set image
            glideImageLoader.load(
                product?.images?.get(0),
                image!!,
                R.drawable.image_placeholder,
                R.drawable.image_placeholder
            )
            //set price
            price.text = getString(R.string.price, product!!.price.toString())

            //handle increase button
            increaseButton.setOnClickListener {
                quantity.text.toString().toIntOrNull()?.let { currentQuantity ->
                    quantity.text = (currentQuantity + 1).toString()
                }
            }

            //handle decrease button
            decreaseButton.setOnClickListener {
                quantity.text.toString().toIntOrNull()?.let { currentQuantity ->
                    if (currentQuantity > 1) quantity.text = (currentQuantity - 1).toString()
                }
            }

            //handle add to cart
            addToCartButton.setOnClickListener {
                val quantityValue = quantity.text.toString().toInt()

                val userId = authViewModel.currentUser!!.uid
                val item = CartItem(
                    product!!.id.toString(), product!!, quantityValue
                )
                dataViewModel.addToCart(userId, item)
            }

        }
    }

    private fun toCartScreen() {
        val intent = Intent(this, CartActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        this.startActivity(intent, options.toBundle())
    }

    private fun setContent(product: Product?) {
        if (product != null) {
            title.text = product.title
            price.text = getString(R.string.price, product.price.toString())
            description.text = product.description

            imageList.addAll(product.images)


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