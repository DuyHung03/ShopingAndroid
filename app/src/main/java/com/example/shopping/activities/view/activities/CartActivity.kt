package com.example.shopping.activities.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.activities.adapter.ProductInCartAdapter
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.utils.Resources
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
    private var currentUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeView()
        initializeListener()

    }

    private fun initializeView() {}

    private fun initializeListener() {
        currentUser = authViewModel.currentUser
        if (currentUser != null)
            dataViewModel.getProductsInCart(currentUser!!.uid)

        dataViewModel.productsCartFlow.observe(this) { state ->
            when (state) {
                is Resources.Failure -> {
                    val error = state.e
                    Log.d("TAG", "onCreateView: $error")
                    Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                    binding.emptyLayout.root.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }

                Resources.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resources.Success -> {
                    val result = state.result
                    if (result!!.isEmpty()) {
                        binding.emptyLayout.root.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    } else {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyLayout.root.visibility = View.GONE
                        productList.addAll(result)
                        setupProductsAdapter()
                    }
                }
            }
        }

        binding.backButton.setOnClickListener {
            this.finish()
        }
    }

    private fun setupProductsAdapter() {
        productInCartAdapter = ProductInCartAdapter(productList, this)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.productRecyclerView.layoutManager = layoutManager
        binding.productRecyclerView.adapter = productInCartAdapter
    }

    override fun onItemClick(item: CartItem) {
        Log.d("TAG", "onItemClick: ${item.product.id}")
    }

    override fun onCheckBoxClick(isChecked: Boolean, item: CartItem) {
        Log.d("TAG", "onCheckBoxClick: ${item.product.title} $isChecked")
    }
}