package com.example.shopping.activities.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.adapter.ProductAdapter
import com.example.shopping.activities.entities.Category
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.viewmodel.ProductViewModel
import com.example.shopping.databinding.ActivitySearchBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var backButton: ImageView
    private lateinit var searchView: AppCompatEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private var productList = mutableListOf<Product>()
    private lateinit var progressBar: ProgressBar
    val viewModel by viewModels<ProductViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialView()
        initialListener()
    }

    private fun initialView() {
        backButton = findViewById(R.id.backButton)
        searchView = findViewById(R.id.edtSearch)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun initialListener() {

        if (intent != null) {
            val category = intent.getParcelableExtra<Category>("category")
            if (category != null)
                viewModel.getProductByCategory(category.id)
        }

        backButton.setOnClickListener {
            this.finish()
        }

        searchView.addTextChangedListener(object : TextWatcher {

            private var timer: Timer? = null
            private val DELAY: Long = 500 // 0.5s

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                timer?.cancel()
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (s!!.isNotEmpty()) {
                            // Call API
                            Log.d("TAG", "run: ${s.toString().trim()}")
                            viewModel.getProductsByTitle(s.toString().trim())
                        }
                    }
                }, DELAY)
            }
        })


        viewModel.products.observe(this) {
            when (it) {
                is Resources.Failure -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.e.toString(), Toast.LENGTH_SHORT).show()
                }

                Resources.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is Resources.Success -> {
                    val products = it.result
                    productList.clear()
                    productList.addAll(products)
                    showProducts()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showProducts() {
        if (productList.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
        } else {
            binding.emptyLayout.visibility = View.GONE
            productAdapter = ProductAdapter(productList) { product ->
                toProductScreen(product)
            }
            val layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = productAdapter
        }
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
}