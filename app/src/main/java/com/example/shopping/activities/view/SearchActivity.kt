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
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.adapter.ProductAdapter
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.viewmodel.ProductViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var searchView: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private var productList = mutableListOf<Product>()
    private lateinit var progressBar: ProgressBar
    val viewModel by viewModels<ProductViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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

        backButton.setOnClickListener {
            this.finish()
        }

        searchView.addTextChangedListener(object : TextWatcher {

            private val timer = Timer()
            private val DELAY: Long = 500 // 0.5s

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        if (s != null) {
                            //cal api
                            Log.d("TAG", "run: ${s.toString().trim()}")
                            viewModel.getProductsByTitle(s.toString().trim())
                        } else {
                            return
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
                    for (product in products) {
                        productList.add(product)
                    }
                    progressBar.visibility = View.GONE
                    showProducts()
                }
            }
        }

    }

    private fun showProducts() {
        productAdapter = ProductAdapter(productList) { product ->
            toProductScreen(product)
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