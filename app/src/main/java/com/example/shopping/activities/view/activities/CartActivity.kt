package com.example.shopping.activities.view.activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.R
import com.example.shopping.activities.adapter.ProductInCartAdapter
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.utils.Toast.toast
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import com.example.shopping.databinding.ActivityCartBinding
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("LogNotTimber")
@AndroidEntryPoint
class CartActivity : AppCompatActivity(), ProductInCartAdapter.OnItemClickListener {
    private lateinit var binding: ActivityCartBinding
    private lateinit var payButton: Button
    private val dataViewModel by viewModels<DataViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var productInCartAdapter: ProductInCartAdapter
    private var productList = mutableListOf<CartItem>()
    private var checkedList = mutableListOf<CartItem>()
    private lateinit var productProgressBar: ProgressBar
    private var currentUser: FirebaseUser? = null
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeListener()

    }

    private fun initializeListener() {
        currentUser = authViewModel.currentUser
        if (currentUser != null)
            dataViewModel.getProductsInCart(currentUser!!.uid)

        binding.backButton.setOnClickListener {
            this.finish()
        }

//        binding.swipeDownLayout.setOnRefreshListener {
//            dataViewModel.getProductsInCart(currentUser!!.uid)
//        }
    }

    override fun onResume() {
        super.onResume()
        dataViewModel.getProductsInCart(currentUser!!.uid)
        checkedList.clear()
        dataViewModel.productsCartFlow.observe(this) { list ->
            if (list!!.isEmpty()) {
                binding.emptyLayout.root.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.emptyLayout.root.visibility = View.GONE
                productList.clear()
                productList.addAll(list)
                setupProductsAdapter()
                binding.totalPrice.text = 0.0.toString()
            }
        }

        dataViewModel.errorMessage.observe(this) { message ->
            message?.let {
                toast(it)
            }
        }

        binding.checkboxAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkAllProducts()
            } else {
                checkedList.clear()
            }
            val updatedTotalPrice =
                calculateTotalPrice(checkedList) // Calculate total price efficiently
            binding.totalPrice.text =
                getString(R.string.price, updatedTotalPrice.toString()) // Update UI
        }

        binding.payButton.setOnClickListener {
            if (checkedList.isEmpty()) {
                toast("Please choose your product")
            } else {
                toPayScreen(checkedList)
            }
        }

    }

    private fun toPayScreen(checkedList: MutableList<CartItem>) {
        val intent = Intent(this, PayActivity::class.java)
        intent.putParcelableArrayListExtra("checkedList", ArrayList(checkedList))
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        this.startActivity(intent, options.toBundle())
    }

    private fun checkAllProducts() {
        checkedList.clear()
        checkedList.addAll(productList)
    }


    private fun setupProductsAdapter() {
        productInCartAdapter = ProductInCartAdapter(this)
        productInCartAdapter.setData(this.productList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.productRecyclerView.layoutManager = layoutManager
        binding.productRecyclerView.adapter = productInCartAdapter
    }


    private fun toProductScreen(product: Product) {
        val intent = Intent(this, ProductActivity::class.java)
        intent.putExtra("product", product)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        this.startActivity(intent, options.toBundle())
    }

    override fun onItemClick(item: CartItem) {
        Log.d("TAG", "onItemClick: ${item.product.id}")
        toProductScreen(item.product)
    }

    override fun onCheckBoxClick(isChecked: Boolean, item: CartItem) {
        if (isChecked) {
            checkedList.add(item)
        } else {
            checkedList.remove(item)
        }

        val updatedTotalPrice =
            calculateTotalPrice(checkedList) // Calculate total price efficiently
        binding.totalPrice.text =
            getString(R.string.price, updatedTotalPrice.toString()) // Update UI
    }

    private fun calculateTotalPrice(cartItems: List<CartItem>): Double {
        totalPrice = 0.0

        for (cartItem in cartItems) {
            totalPrice += cartItem.quantity * cartItem.product.price
        }

        return totalPrice
    }

    override fun onIncreaseQuantity(cartItem: CartItem) {
        dataViewModel.increaseQuantity(cartItem)
    }

    override fun onDeleteItem(cartItem: CartItem) {
        dataViewModel.deleteProductInCart(cartItem)
    }

}