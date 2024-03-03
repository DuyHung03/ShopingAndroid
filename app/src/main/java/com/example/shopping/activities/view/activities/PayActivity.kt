package com.example.shopping.activities.view.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.R
import com.example.shopping.activities.adapter.ProductInPayAdapter
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.databinding.ActivityPayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayBinding
    private var checkedList: ArrayList<CartItem>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        binding = ActivityPayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkedList?.clear()
        checkedList = intent.getParcelableArrayListExtra("checkedList")!!

        initializeListener()

    }

    private fun initializeListener() {

        setupRecyclerView()

        binding.backButton.setOnClickListener {
            this.finish()
        }

        binding.address.setOnClickListener {
            toAddressScreen()
        }

        setupPriceDetail()

    }

    private fun toAddressScreen() {
        val intent = Intent(this, AddressListActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        this.startActivity(intent, options.toBundle())
    }

    private fun setupPriceDetail() {
        var productsCost = 0.0
        val shippingCost = 10
        for (item in checkedList!!) {
            productsCost += (item.quantity * item.product.price)
        }

        val total = productsCost + shippingCost

        binding.totalOfList.text = getString(R.string.price, productsCost.toString())
        binding.shippingCost.text = getString(R.string.price, shippingCost.toString())
        binding.totalTitle.text = getString(R.string.total_title, checkedList!!.size.toString())
        binding.total.text = getString(R.string.price, total.toString())
        binding.totalPrice.text = getString(R.string.price, total.toString())
    }

    private fun setupRecyclerView() {
        if (checkedList != null) {
            val productInPayAdapter = ProductInPayAdapter(checkedList!!)
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.productRecyclerView.layoutManager = layoutManager
            binding.productRecyclerView.adapter = productInPayAdapter
        }
    }


}