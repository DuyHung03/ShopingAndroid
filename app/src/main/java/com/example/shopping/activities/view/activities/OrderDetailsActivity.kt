package com.example.shopping.activities.view.activities

import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.R
import com.example.shopping.activities.adapter.ProductInPayAdapter
import com.example.shopping.activities.entities.Order
import com.example.shopping.databinding.ActivityOrderDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var order: Order
    private lateinit var addressDetails: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent != null) {
            order = intent.getParcelableExtra("order")!!
        }

        addressDetails = findViewById(R.id.addressDetail)
        initializeListener()

    }

    private fun initializeListener() {

        binding.backButton.setOnClickListener {
            this.finish()
        }

        //set address details
        val formattedAddress = SpannableStringBuilder()
        formattedAddress.append(Html.fromHtml("<b>${order.address.name}</b>"))
        formattedAddress.append(",  ${order.address.phoneNumber}\n")
        formattedAddress.append("${order.address.city},\n")
        formattedAddress.append("${order.address.ward},\n")
        formattedAddress.append("${order.address.district},\n")
        formattedAddress.append(order.address.houseAddress)

        addressDetails.text = formattedAddress

        //orderId
        binding.orderId.text = order.orderId

        //product list
        setupProductsRecyclerView()

        //cost
        binding.totalOfList.text = getString(R.string.price, order.cost.productsCost.toString())
        binding.shippingCost.text = getString(R.string.price, order.cost.shippingCost.toString())
        binding.promotion.text = getString(R.string.price, "10")
        binding.total.text = getString(R.string.price, order.cost.total.toString())
    }

    private fun setupProductsRecyclerView() {
        val productInPayAdapter = ProductInPayAdapter(order.productList)
        binding.productRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.productRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.productRecyclerView.adapter = productInPayAdapter
    }
}